# Problem 8: Inventory Reservation Service (Pessimistic/Optimistic Locking + Two-Phase Commit)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design an inventory reservation system that temporarily holds stock during checkout, preventing overselling while supporting concurrent reservations with automatic timeout and cleanup.

**Assumptions / Scope:**
- Support temporary reservations with TTL (time-to-live)
- Handle concurrent reservations for same product
- Prevent overselling (available stock never goes negative)
- Auto-release expired reservations
- Support both pessimistic and optimistic locking strategies
- Track reservation lifecycle: PENDING → CONFIRMED/CANCELLED/EXPIRED
- Scale: 100K reservations/min during peak, 10M products
- Out of scope: Warehouse management, cross-warehouse allocation

**Non-Functional Goals:**
- Strong consistency for stock levels
- Sub-50ms reservation latency
- Handle 10K concurrent reservations
- Zero overselling tolerance
- Automatic cleanup of expired reservations

### 2. Core Requirements

**Functional:**
- Reserve inventory with quantity and expiration time
- Confirm reservation (convert to actual sale)
- Cancel reservation (return stock to available pool)
- Auto-expire reservations after timeout
- Query available stock in real-time
- Prevent double-reservation
- Support batch reservations (multiple products)

**Non-Functional:**
- **Consistency**: Strong consistency for stock updates (no phantom reads)
- **Concurrency**: Handle 10K concurrent reserve requests
- **Performance**: Reserve operation < 50ms, cleanup < 100ms
- **Reliability**: Guaranteed cleanup of expired reservations
- **Observability**: Track reservation success rate, expiration rate

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Pessimistic Locking (SELECT FOR UPDATE)**
- **Problem**: Multiple concurrent reservations for same product can cause race conditions
- **Solution**: Database-level row lock during reservation
- **Algorithm**:
```sql
-- Pessimistic Lock Approach
BEGIN TRANSACTION;

-- Lock the product row (other transactions wait)
SELECT available_stock, reserved_stock 
FROM products 
WHERE product_id = ? 
FOR UPDATE;  -- Exclusive lock

-- Check availability
IF available_stock >= requested_quantity THEN
    -- Update stock atomically
    UPDATE products 
    SET available_stock = available_stock - requested_quantity,
        reserved_stock = reserved_stock + requested_quantity
    WHERE product_id = ?;
    
    -- Insert reservation record
    INSERT INTO reservations (id, product_id, quantity, expires_at, status)
    VALUES (?, ?, ?, ?, 'PENDING');
    
    COMMIT;
    RETURN SUCCESS;
ELSE
    ROLLBACK;
    RETURN INSUFFICIENT_STOCK;
END IF;
```

**Challenge 2: Optimistic Locking (Version-Based)**
- **Problem**: Pessimistic locks cause contention under high load
- **Solution**: CAS (Compare-And-Swap) with version number
- **Algorithm**:
```java
// Optimistic Lock Approach
reserveOptimistic(productId, quantity, expiresAt) {
    maxRetries = 3;
    attempt = 0;
    
    while (attempt < maxRetries) {
        // Read current state
        product = productRepo.findById(productId);
        
        if (product.availableStock < quantity) {
            throw InsufficientStockException();
        }
        
        // Prepare updates
        newAvailableStock = product.availableStock - quantity;
        newReservedStock = product.reservedStock + quantity;
        newVersion = product.version + 1;
        
        // Atomic CAS update with version check
        rowsUpdated = executeUpdate(
            "UPDATE products 
             SET available_stock = ?, 
                 reserved_stock = ?, 
                 version = ? 
             WHERE product_id = ? AND version = ?",
            newAvailableStock, newReservedStock, newVersion,
            productId, product.version  // WHERE version = old
        );
        
        if (rowsUpdated == 1) {
            // Success! Insert reservation
            insertReservation(productId, quantity, expiresAt);
            return SUCCESS;
        } else {
            // Version mismatch, retry
            attempt++;
            sleep(exponentialBackoff(attempt));
        }
    }
    
    throw OptimisticLockException("Max retries exceeded");
}
```

**Challenge 3: Two-Phase Commit (Distributed Reservations)**
- **Problem**: Checkout may need multiple products, partial failure leaves inconsistent state
- **Solution**: Coordinator-based 2PC with compensation
- **Algorithm**:
```
// Two-Phase Commit for Multi-Product Reservation
reserveMultipleProducts(reservationId, itemsToReserve) {
    List<ReservationRecord> successfulReservations = [];
    
    try {
        // PHASE 1: PREPARE (Reserve each product)
        for (item in itemsToReserve) {
            reservation = reserveSingleProduct(
                item.productId,
                item.quantity,
                reservationId,
                expiresAt = now() + 10_MINUTES
            );
            
            if (!reservation.success) {
                // Rollback all previous reservations
                compensate(successfulReservations);
                throw InsufficientStockException(item.productId);
            }
            
            successfulReservations.add(reservation);
        }
        
        // PHASE 2: COMMIT (Mark all as confirmed)
        for (res in successfulReservations) {
            res.status = CONFIRMED;
            reservationRepo.update(res);
        }
        
        return ReservationResult.SUCCESS(reservationId);
        
    } catch (Exception e) {
        // Compensate: Release all reservations
        compensate(successfulReservations);
        throw ReservationFailedException(e);
    }
}

compensate(reservations) {
    for (res in reservations) {
        // Return stock to available pool
        product = productRepo.lockById(res.productId);
        product.availableStock += res.quantity;
        product.reservedStock -= res.quantity;
        productRepo.update(product);
        
        // Mark reservation as cancelled
        res.status = CANCELLED;
        reservationRepo.update(res);
    }
}
```

**Challenge 4: Automatic Expiration Cleanup**
- **Problem**: Expired reservations hold stock indefinitely
- **Solution**: Background job with batch processing
- **Algorithm**:
```java
@Scheduled(fixedDelay = 30_000) // Every 30 seconds
cleanupExpiredReservations() {
    batchSize = 1000;
    Instant now = Instant.now();
    
    // Find expired pending reservations
    List<Reservation> expired = reservationRepo.findExpired(
        status = PENDING,
        expiresAtBefore = now,
        limit = batchSize
    );
    
    if (expired.isEmpty()) return;
    
    // Group by product for batch updates
    Map<UUID, List<Reservation>> byProduct = expired.stream()
        .collect(Collectors.groupingBy(Reservation::getProductId));
    
    for (Map.Entry<UUID, List<Reservation>> entry : byProduct.entrySet()) {
        UUID productId = entry.getKey();
        List<Reservation> reservations = entry.getValue();
        
        // Aggregate total quantity to release
        int totalQuantity = reservations.stream()
            .mapToInt(Reservation::getQuantity)
            .sum();
        
        // Single update per product
        int updated = executeUpdate(
            "UPDATE products 
             SET available_stock = available_stock + ?,
                 reserved_stock = reserved_stock - ?
             WHERE product_id = ?",
            totalQuantity, totalQuantity, productId
        );
        
        if (updated == 1) {
            // Mark all as expired
            reservationRepo.batchUpdateStatus(
                reservations.stream().map(Reservation::getId).collect(toList()),
                EXPIRED
            );
        }
    }
    
    metrics.recordExpiredReservations(expired.size());
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **State** | Reservation lifecycle (PENDING → CONFIRMED/CANCELLED/EXPIRED) | Clear state transitions with validation |
| **Strategy** | Locking strategies (Pessimistic vs Optimistic) | Swap locking mechanism based on contention |
| **Template Method** | Reserve flow (validate → lock → update → commit) | Common steps with variant locking |
| **Saga** | Multi-product reservation with compensation | Distributed transaction handling |
| **Repository** | Product and Reservation data access | Abstract persistence layer |
| **Command** | Reservation operations (Reserve, Confirm, Cancel) | Encapsulate operations for undo/audit |
| **Factory** | Create appropriate lock strategy | Instantiate based on configuration |

### 5. Domain Model & Class Structure

```
┌──────────────────────┐
│ ReservationService   │ (Application Service)
│  - lockingStrategy   │
│  - reservationRepo   │
│  - productRepo       │
│  - cleanupScheduler  │
└───────┬──────────────┘
        │ manages
        │
    ┌───┴────────────────────┬─────────────────┐
    ▼                        ▼                 ▼
┌─────────────┐      ┌──────────────┐   ┌──────────────┐
│ Reservation │      │   Product    │   │  LockingStrategy │
│  (Entity)   │      │   (Aggregate)│   │  (Strategy)      │
└─────────────┘      └──────────────┘   └──────────────┘
    │                                            ▲
    │ has                                        │
    ▼                                    ┌───────┴────────┐
┌──────────────┐                   ┌────▼──────────┐ ┌──▼──────────┐
│ReservationState│                  │ Pessimistic │ │ Optimistic  │
│    (Enum)      │                  │   Locking   │ │   Locking   │
└────────────────┘                  └─────────────┘ └─────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Reserve with Pessimistic Lock**
```
Client    ReservationSvc   ProductRepo   ReservationRepo   Database
  │            │                │              │              │
  ├─reserve────>│                │              │              │
  │            ├─BEGIN TX───────────────────────────────────>│
  │            ├─lockProduct───>│              │              │
  │            │                ├─SELECT FOR UPDATE──────────>│
  │            │                │<─locked product─────────────┤
  │            │<─product───────┤              │              │
  │            ├─checkStock────>│              │              │
  │            ├─updateStock────>│              │              │
  │            │                ├─UPDATE products────────────>│
  │            ├─createReservation──────────────>│              │
  │            │                │              ├─INSERT───────>│
  │            ├─COMMIT──────────────────────────────────────>│
  │<─success───┤                │              │              │
```

**Sequence: Cleanup Expired Reservations**
```
Scheduler  CleanupService  ReservationRepo  ProductRepo
  │            │                │              │
  ├─trigger────>│                │              │
  │            ├─findExpired────>│              │
  │            │<─reservations───┤              │
  │            │                 │              │
  │            ├─groupByProduct──┤              │
  │            ├─batchRelease────────────────>│
  │            │                 │           (bulk update)
  │            ├─markExpired─────>│              │
  │            │<─updated─────────┤              │
  │            ├─recordMetric────>│              │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// ============================================
// DOMAIN ENTITIES (Skeleton)
// ============================================

public class Product {
    private UUID id;
    private String sku;
    private int totalStock;
    private int availableStock;
    private int reservedStock;
    private int soldStock;
    
    @Version
    private long version;
    
    private Instant updatedAt;
    
    // Getters only, setters omitted for brevity
}

public class Reservation {
    private UUID id;
    private UUID reservationGroupId;  // For multi-product reservations
    private UUID productId;
    private int quantity;
    private ReservationStatus status;
    private Instant createdAt;
    private Instant expiresAt;
    private Instant confirmedAt;
    private String reason;
    
    // Getters/setters omitted
}

public enum ReservationStatus {
    PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
}

// ============================================
// LOCKING STRATEGY INTERFACE
// ============================================

public interface LockingStrategy {
    ReservationResult reserve(UUID productId, int quantity, UUID reservationId, Instant expiresAt);
}

// ============================================
// PESSIMISTIC LOCKING IMPLEMENTATION
// ============================================

public class PessimisticLockingStrategy implements LockingStrategy {
    private final ProductRepository productRepo;
    private final ReservationRepository reservationRepo;
    private final TransactionTemplate transactionTemplate;
    
    @Override
    public ReservationResult reserve(UUID productId, int quantity, 
                                      UUID reservationId, Instant expiresAt) {
        return transactionTemplate.execute(status -> {
            // CRITICAL: Lock the product row
            Product product = productRepo.findByIdWithLock(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
            
            // Check availability
            if (product.getAvailableStock() < quantity) {
                return ReservationResult.failure(
                    ReservationFailureReason.INSUFFICIENT_STOCK,
                    String.format("Requested: %d, Available: %d", 
                        quantity, product.getAvailableStock())
                );
            }
            
            // Update stock atomically
            product.setAvailableStock(product.getAvailableStock() - quantity);
            product.setReservedStock(product.getReservedStock() + quantity);
            product.setUpdatedAt(Instant.now());
            
            productRepo.save(product);
            
            // Create reservation record
            Reservation reservation = Reservation.builder()
                .id(reservationId)
                .productId(productId)
                .quantity(quantity)
                .status(ReservationStatus.PENDING)
                .createdAt(Instant.now())
                .expiresAt(expiresAt)
                .build();
            
            reservationRepo.save(reservation);
            
            return ReservationResult.success(reservation);
        });
    }
}

// ============================================
// OPTIMISTIC LOCKING IMPLEMENTATION
// ============================================

public class OptimisticLockingStrategy implements LockingStrategy {
    private final ProductRepository productRepo;
    private final ReservationRepository reservationRepo;
    private static final int MAX_RETRIES = 3;
    private static final int BASE_BACKOFF_MS = 10;
    
    @Override
    @Transactional
    public ReservationResult reserve(UUID productId, int quantity, 
                                      UUID reservationId, Instant expiresAt) {
        int attempt = 0;
        
        while (attempt < MAX_RETRIES) {
            try {
                return attemptReservation(productId, quantity, reservationId, expiresAt);
            } catch (OptimisticLockException e) {
                attempt++;
                if (attempt >= MAX_RETRIES) {
                    return ReservationResult.failure(
                        ReservationFailureReason.OPTIMISTIC_LOCK_FAILURE,
                        "Max retries exceeded"
                    );
                }
                
                // Exponential backoff
                try {
                    Thread.sleep(BASE_BACKOFF_MS * (long) Math.pow(2, attempt));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(ie);
                }
            }
        }
        
        return ReservationResult.failure(
            ReservationFailureReason.OPTIMISTIC_LOCK_FAILURE,
            "Failed after retries"
        );
    }
    
    /**
     * INTERVIEW CRITICAL: Optimistic locking with version check
     */
    private ReservationResult attemptReservation(UUID productId, int quantity,
                                                  UUID reservationId, Instant expiresAt) {
        // Read current state (no lock)
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
        
        long currentVersion = product.getVersion();
        
        // Check availability
        if (product.getAvailableStock() < quantity) {
            return ReservationResult.failure(
                ReservationFailureReason.INSUFFICIENT_STOCK,
                String.format("Available: %d, Requested: %d",
                    product.getAvailableStock(), quantity)
            );
        }
        
        // Calculate new state
        int newAvailable = product.getAvailableStock() - quantity;
        int newReserved = product.getReservedStock() + quantity;
        
        // Atomic CAS update with version check
        int rowsUpdated = productRepo.updateStockWithVersion(
            productId,
            newAvailable,
            newReserved,
            currentVersion  // WHERE version = currentVersion
        );
        
        if (rowsUpdated == 0) {
            // Version mismatch - another transaction modified the row
            throw new OptimisticLockException("Product modified by another transaction");
        }
        
        // Success! Create reservation
        Reservation reservation = Reservation.builder()
            .id(reservationId)
            .productId(productId)
            .quantity(quantity)
            .status(ReservationStatus.PENDING)
            .createdAt(Instant.now())
            .expiresAt(expiresAt)
            .build();
        
        reservationRepo.save(reservation);
        
        return ReservationResult.success(reservation);
    }
}

// ============================================
// RESERVATION SERVICE (Main Coordinator)
// ============================================

public class ReservationService {
    private final LockingStrategy lockingStrategy;
    private final ReservationRepository reservationRepo;
    private final ProductRepository productRepo;
    private final EventPublisher eventPublisher;
    
    /**
     * INTERVIEW CRITICAL: Multi-product reservation with 2PC pattern
     */
    @Transactional
    public MultiReservationResult reserveMultiple(List<ReservationRequest> requests) {
        UUID reservationGroupId = UUID.randomUUID();
        List<Reservation> successfulReservations = new ArrayList<>();
        
        try {
            // PHASE 1: PREPARE - Reserve each product
            for (ReservationRequest request : requests) {
                ReservationResult result = lockingStrategy.reserve(
                    request.getProductId(),
                    request.getQuantity(),
                    UUID.randomUUID(),
                    Instant.now().plus(Duration.ofMinutes(10))
                );
                
                if (!result.isSuccess()) {
                    // Partial failure - compensate all previous reservations
                    compensateReservations(successfulReservations);
                    
                    return MultiReservationResult.failure(
                        request.getProductId(),
                        result.getFailureReason()
                    );
                }
                
                Reservation reservation = result.getReservation();
                reservation.setReservationGroupId(reservationGroupId);
                successfulReservations.add(reservation);
            }
            
            // PHASE 2: COMMIT - All reservations successful
            successfulReservations.forEach(res -> {
                res.setStatus(ReservationStatus.CONFIRMED);
                reservationRepo.save(res);
            });
            
            // Publish success event
            eventPublisher.publish(new ReservationsConfirmedEvent(
                reservationGroupId,
                successfulReservations.stream()
                    .map(Reservation::getId)
                    .collect(Collectors.toList())
            ));
            
            return MultiReservationResult.success(reservationGroupId, successfulReservations);
            
        } catch (Exception e) {
            // Unexpected error - compensate
            compensateReservations(successfulReservations);
            throw new ReservationException("Failed to complete multi-product reservation", e);
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Compensation logic for rollback
     */
    private void compensateReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            try {
                cancelReservation(reservation.getId());
            } catch (Exception e) {
                // Log but don't propagate - best effort compensation
                log.error("Failed to compensate reservation: {}", reservation.getId(), e);
            }
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Confirm reservation (convert to sale)
     */
    @Transactional
    public void confirmReservation(UUID reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        
        // Validate state transition
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationStateException(
                String.format("Cannot confirm reservation in state: %s", 
                    reservation.getStatus())
            );
        }
        
        // Check expiration
        if (Instant.now().isAfter(reservation.getExpiresAt())) {
            throw new ReservationExpiredException(reservationId);
        }
        
        // Update product: reserved → sold
        Product product = productRepo.findByIdWithLock(reservation.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(reservation.getProductId()));
        
        product.setReservedStock(product.getReservedStock() - reservation.getQuantity());
        product.setSoldStock(product.getSoldStock() + reservation.getQuantity());
        
        productRepo.save(product);
        
        // Update reservation
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setConfirmedAt(Instant.now());
        
        reservationRepo.save(reservation);
        
        // Publish event
        eventPublisher.publish(new ReservationConfirmedEvent(reservationId));
    }
    
    /**
     * INTERVIEW CRITICAL: Cancel reservation (return stock)
     */
    @Transactional
    public void cancelReservation(UUID reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        
        // Only cancel if pending
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidReservationStateException(
                String.format("Cannot cancel reservation in state: %s", 
                    reservation.getStatus())
            );
        }
        
        // Update product: reserved → available
        Product product = productRepo.findByIdWithLock(reservation.getProductId())
            .orElseThrow(() -> new ProductNotFoundException(reservation.getProductId()));
        
        product.setReservedStock(product.getReservedStock() - reservation.getQuantity());
        product.setAvailableStock(product.getAvailableStock() + reservation.getQuantity());
        
        productRepo.save(product);
        
        // Update reservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        
        reservationRepo.save(reservation);
        
        // Publish event
        eventPublisher.publish(new ReservationCancelledEvent(reservationId));
    }
    
    public int getAvailableStock(UUID productId) {
        Product product = productRepo.findById(productId)
            .orElseThrow(() -> new ProductNotFoundException(productId));
        return product.getAvailableStock();
    }
}

// ============================================
// CLEANUP SCHEDULER (Background Job)
// ============================================

@Component
public class ReservationCleanupScheduler {
    private final ReservationRepository reservationRepo;
    private final ProductRepository productRepo;
    private final MetricsService metricsService;
    private static final int BATCH_SIZE = 1000;
    
    /**
     * INTERVIEW CRITICAL: Automatic cleanup of expired reservations
     */
    @Scheduled(fixedDelay = 30_000) // Every 30 seconds
    @Transactional
    public void cleanupExpiredReservations() {
        Instant now = Instant.now();
        
        // Find expired pending reservations
        List<Reservation> expired = reservationRepo.findExpiredPending(now, BATCH_SIZE);
        
        if (expired.isEmpty()) {
            return;
        }
        
        // Group by product for efficient batch updates
        Map<UUID, List<Reservation>> byProduct = expired.stream()
            .collect(Collectors.groupingBy(Reservation::getProductId));
        
        int totalReleased = 0;
        
        for (Map.Entry<UUID, List<Reservation>> entry : byProduct.entrySet()) {
            UUID productId = entry.getKey();
            List<Reservation> reservations = entry.getValue();
            
            // Calculate total quantity to release
            int totalQuantity = reservations.stream()
                .mapToInt(Reservation::getQuantity)
                .sum();
            
            // Single atomic update per product
            int updated = productRepo.releaseReservedStock(productId, totalQuantity);
            
            if (updated > 0) {
                // Mark all reservations as expired
                List<UUID> reservationIds = reservations.stream()
                    .map(Reservation::getId)
                    .collect(Collectors.toList());
                
                reservationRepo.batchUpdateStatus(reservationIds, ReservationStatus.EXPIRED);
                
                totalReleased += totalQuantity;
            }
        }
        
        // Record metrics
        metricsService.recordExpiredReservations(expired.size(), totalReleased);
        
        log.info("Cleaned up {} expired reservations, released {} units",
            expired.size(), totalReleased);
    }
}

// ============================================
// REPOSITORY INTERFACES (Skeleton)
// ============================================

public interface ProductRepository {
    Optional<Product> findById(UUID id);
    Optional<Product> findByIdWithLock(UUID id);
    Product save(Product product);
    
    @Query("UPDATE products SET available_stock = available_stock + :quantity, " +
           "reserved_stock = reserved_stock - :quantity WHERE product_id = :productId")
    int releaseReservedStock(@Param("productId") UUID productId, 
                             @Param("quantity") int quantity);
    
    @Query("UPDATE products SET available_stock = :available, reserved_stock = :reserved, " +
           "version = version + 1 WHERE product_id = :productId AND version = :expectedVersion")
    int updateStockWithVersion(@Param("productId") UUID productId,
                               @Param("available") int available,
                               @Param("reserved") int reserved,
                               @Param("expectedVersion") long expectedVersion);
}

public interface ReservationRepository {
    Optional<Reservation> findById(UUID id);
    Reservation save(Reservation reservation);
    
    @Query("SELECT * FROM reservations WHERE status = 'PENDING' " +
           "AND expires_at < :expiresAt LIMIT :limit")
    List<Reservation> findExpiredPending(@Param("expiresAt") Instant expiresAt,
                                         @Param("limit") int limit);
    
    @Query("UPDATE reservations SET status = :status WHERE id IN (:ids)")
    void batchUpdateStatus(@Param("ids") List<UUID> ids, 
                          @Param("status") ReservationStatus status);
}

// ============================================
// RESULT OBJECTS (DTOs)
// ============================================

public class ReservationResult {
    private final boolean success;
    private final Reservation reservation;
    private final ReservationFailureReason failureReason;
    private final String message;
    
    public static ReservationResult success(Reservation reservation) {
        return new ReservationResult(true, reservation, null, null);
    }
    
    public static ReservationResult failure(ReservationFailureReason reason, String message) {
        return new ReservationResult(false, null, reason, message);
    }
    
    // Getters omitted
}

public enum ReservationFailureReason {
    INSUFFICIENT_STOCK,
    PRODUCT_NOT_FOUND,
    OPTIMISTIC_LOCK_FAILURE,
    RESERVATION_EXPIRED,
    INVALID_STATE_TRANSITION
}

public class MultiReservationResult {
    private final boolean success;
    private final UUID reservationGroupId;
    private final List<Reservation> reservations;
    private final UUID failedProductId;
    private final ReservationFailureReason failureReason;
    
    // Static factory methods and getters omitted
}

public class ReservationRequest {
    private final UUID productId;
    private final int quantity;
    
    // Constructor and getters omitted
}
```

### 8. Thread Safety & Concurrency

**Pessimistic Locking:**
- Database-level row lock (SELECT FOR UPDATE)
- Blocks concurrent transactions
- Best for high-contention scenarios
- Prevents deadlocks with timeout

**Optimistic Locking:**
- Version-based CAS (Compare-And-Swap)
- No blocking, retry on conflict
- Better throughput under low contention
- Exponential backoff on retries

**Cleanup Job:**
- Non-blocking reads (status = PENDING)
- Batch updates grouped by product
- Idempotent (safe to run multiple times)

### 9. Top Interview Questions & Answers

**Q1: Pessimistic vs Optimistic locking - when to use each?**
**A:**
| Scenario | Use Pessimistic | Use Optimistic |
|----------|----------------|----------------|
| High contention (hot products) | ✅ | ❌ |
| Low contention | ❌ | ✅ |
| Long transaction time | ❌ | ✅ |
| Read-heavy workload | ❌ | ✅ |
| Flash sale (100K/sec) | ✅ | ❌ |

**Q2: How do you prevent deadlocks with pessimistic locks?**
**A:**
```java
// Always acquire locks in consistent order (by product ID)
List<UUID> sortedProductIds = requests.stream()
    .map(ReservationRequest::getProductId)
    .sorted()  // Critical: Always lock in same order
    .collect(toList());

for (UUID productId : sortedProductIds) {
    Product product = productRepo.findByIdWithLock(productId);
    // Process...
}
```

**Q3: What if cleanup job crashes midway?**
**A:**
- Job is idempotent (WHERE status = PENDING)
- Next run will pick up remaining expired reservations
- No partial state - each product update is atomic
- Monitor for stuck PENDING reservations (alert if > 15 min)

**Q4: How do you handle reservation timeout during payment?**
**A:**
```java
// Option 1: Extend reservation
extendReservation(reservationId, additionalMinutes = 5);

// Option 2: Re-reserve if expired
if (reservation.isExpired()) {
    newReservation = reserve(productId, quantity, expiresAt = now() + 10min);
    // Continue with new reservation
}

// Option 3: Grace period (allow confirm within 2 min of expiry)
if (now().isBefore(reservation.expiresAt.plus(Duration.ofMinutes(2)))) {
    confirmReservation(reservationId);
}
```

**Q5: How do you test race conditions?**
**A:**
```java
@Test
public void testConcurrentReservations() throws Exception {
    UUID productId = createProductWithStock(100);
    
    int numThreads = 50;
    int quantityPerThread = 3; // Total: 150 (exceeds 100)
    
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch latch = new CountDownLatch(numThreads);
    
    List<Future<ReservationResult>> futures = new ArrayList<>();
    
    for (int i = 0; i < numThreads; i++) {
        futures.add(executor.submit(() -> {
            latch.countDown();
            latch.await(); // All threads start together
            
            return reservationService.reserve(
                productId, 
                quantityPerThread,
                UUID.randomUUID(),
                Instant.now().plus(Duration.ofMinutes(10))
            );
        }));
    }
    
    // Collect results
    List<ReservationResult> results = futures.stream()
        .map(f -> f.get())
        .collect(toList());
    
    long successCount = results.stream()
        .filter(ReservationResult::isSuccess)
        .count();
    
    // Only 33 should succeed (100 / 3 = 33.33)
    assertEquals(33, successCount);
    
    // Verify no overselling
    Product product = productRepo.findById(productId).get();
    assertEquals(100, product.getReservedStock() + product.getAvailableStock());
}
```

**Q6: How do you monitor reservation health?**
**A:**
```java
Metrics to track:
1. Reservation success rate: % successful reserves
2. Expiration rate: % reservations that expire without confirmation
3. Average reservation duration: Time from reserve → confirm
4. Optimistic lock retry count: Avg retries per reservation
5. Cleanup lag: Time between expiry and actual cleanup
6. Stock utilization: reserved / (available + reserved)

Alerts:
- Expiration rate > 30% → Timeout too short
- Optimistic retries > 5 → High contention, switch to pessimistic
- Cleanup lag > 5 min → Scale up cleanup job
- Success rate < 95% → Investigate stock availability
```

**Q7: What if two checkout flows reserve same items?**
**A:**
- Each checkout gets unique `reservationGroupId`
- Reservations are independent
- First to confirm wins
- Second gets insufficient stock error on confirm attempt
- Requires coordination at checkout level (cart lock)

**Q8: How do you handle partial reservations in multi-product checkout?**
**A:**
```java
// All-or-nothing approach
try {
    reserveMultiple(items); // Reserves all or compensates all
} catch (InsufficientStockException e) {
    // Show user which item is out of stock
    // Option 1: Offer to remove item and continue
    // Option 2: Cancel entire checkout
}

// Alternative: Best-effort approach
List<Reservation> successful = new ArrayList<>();
List<UUID> failed = new ArrayList<>();

for (item : items) {
    try {
        reservation = reserve(item.productId, item.quantity);
        successful.add(reservation);
    } catch (InsufficientStockException e) {
        failed.add(item.productId);
    }
}

// Let user decide: proceed with partial or cancel all
```

**Q9: What's the database schema?**
**A:**
```sql
CREATE TABLE products (
    product_id UUID PRIMARY KEY,
    sku VARCHAR(50) NOT NULL UNIQUE,
    total_stock INT NOT NULL,
    available_stock INT NOT NULL,
    reserved_stock INT NOT NULL,
    sold_stock INT NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL,
    
    CONSTRAINT stock_consistency CHECK (
        total_stock = available_stock + reserved_stock + sold_stock
    ),
    CONSTRAINT non_negative_stock CHECK (
        available_stock >= 0 AND reserved_stock >= 0 AND sold_stock >= 0
    )
);

CREATE INDEX idx_products_sku ON products(sku);

CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    reservation_group_id UUID,
    product_id UUID NOT NULL REFERENCES products(product_id),
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    confirmed_at TIMESTAMP,
    reason VARCHAR(255),
    
    CONSTRAINT valid_quantity CHECK (quantity > 0)
);

CREATE INDEX idx_reservations_status_expires 
    ON reservations(status, expires_at) 
    WHERE status = 'PENDING';
    
CREATE INDEX idx_reservations_group 
    ON reservations(reservation_group_id);
```

**Q10: How would you scale this to millions of products?**
**A:**
```
Horizontal Scaling:
1. Partition by product_id (consistent hashing)
2. Each partition handles subset of products
3. No cross-partition transactions needed

Caching:
1. Cache product stock in Redis (read-through)
2. Invalidate on reservation/confirmation
3. TTL = 10 seconds for hot products

Database Optimizations:
1. Dedicated reservation DB (separate from catalog)
2. Read replicas for stock queries
3. Batch cleanup job scales horizontally (partition by product_id % num_workers)

Event Sourcing Alternative:
1. Store all stock changes as events
2. Rebuild current state from event log
3. Supports time-travel debugging and audit
```

### 10. Extensions & Variations

1. **Priority Reservations**: VIP customers get longer timeout
2. **Waitlist**: Queue users when stock exhausted
3. **Pre-orders**: Reserve future stock before arrival
4. **Cross-Warehouse**: Reserve from multiple locations
5. **Partial Fulfillment**: Allow splitting reservation across warehouses

### 11. Testing Strategy

**Unit Tests:**
- Pessimistic locking logic
- Optimistic locking retry mechanism
- State transitions (PENDING → CONFIRMED)
- Compensation logic

**Integration Tests:**
- Full reserve → confirm flow
- Concurrent reservations (race conditions)
- Expiration cleanup job
- Database constraint validation

**Load Tests:**
- 10K concurrent reserves (measure throughput)
- Hot product scenario (1 product, 1000 reserves/sec)
- Cleanup job under high load

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Check stock → Update stock (race condition)
✅ **Do**: Atomic check-and-update with lock

❌ **Avoid**: No reservation timeout (stock held forever)
✅ **Do**: Auto-expire with cleanup job

❌ **Avoid**: Synchronous cleanup in reserve flow
✅ **Do**: Background job handles cleanup

❌ **Avoid**: No compensation for partial failures
✅ **Do**: 2PC with compensating transactions

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Reserve (Pessimistic) | O(1) | O(1) | DB lock wait time varies |
| Reserve (Optimistic) | O(R) | O(1) | R = retry count (avg 1-2) |
| Confirm | O(1) | O(1) | Single row updates |
| Cancel | O(1) | O(1) | Single row updates |
| Cleanup (batch) | O(N) | O(N) | N = expired reservations |
| Multi-reserve | O(P) | O(P) | P = number of products |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Locking Strategy** | 30% | Understands pessimistic vs optimistic trade-offs |
| **Concurrency** | 25% | Handles race conditions, prevents overselling |
| **Compensation** | 20% | 2PC pattern, rollback on partial failure |
| **Cleanup** | 15% | Automatic expiration, batch processing |
| **Real-world Awareness** | 10% | Deadlocks, monitoring, scaling strategies |

**Red Flags:**
- No locking strategy
- Check-then-update pattern (race condition)
- No reservation timeout
- No compensation for partial failures
- Ignoring deadlock prevention

---
