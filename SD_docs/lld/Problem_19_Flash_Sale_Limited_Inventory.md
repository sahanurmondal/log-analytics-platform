# Deep Dive #19: Flash Sale / Limited Inventory System

## 1. Problem Clarification

Design a flash sale system handling time-limited promotions with scarce inventory, optimistic concurrency control, queue management for high traffic spikes, fair allocation, and bot prevention.

**Assumptions / Scope:**
- **Scale:** 10M concurrent users, 100K QPS during flash sale, 10K units inventory typical
- **Duration:** Sales last 1-60 minutes, multiple concurrent sales possible
- **Features:** Countdown timers, inventory reservation with expiry, fair queuing (FIFO vs lottery), rate limiting, bot detection, purchase limits per user
- **Inventory:** Pre-allocated limited stock, no replenishment during sale
- **Fairness:** First-come-first-served with queue, or lottery for extremely hot items
- **Performance:** Add-to-cart < 100ms under load, checkout within 5-minute window
- **Consistency:** Prevent overselling (strong consistency on inventory decrement)
- **Out of scope:** Dynamic pricing (surge), multi-region coordination (single region initially), fraud detection beyond basic bot mitigation

**Non-Functional Goals:**
- No overselling (critical)
- Fair access (queue vs flood)
- High availability (99.9%)
- Graceful degradation (queue overflow → waiting page)
- Observable (queue depth, sale velocity, bot detection rate)

---

## 2. Core Requirements

| Category | Requirement |
|----------|-------------|
| **Functional** | • Create flash sale with start/end time, inventory limit<br>• Reserve inventory on add-to-cart (temporary hold)<br>• Release expired reservations<br>• Fair queue management (FIFO or lottery)<br>• Purchase limit per user (e.g., max 2 units)<br>• Real-time countdown timer<br>• Sold-out detection & notification<br>• Rate limiting per user/IP<br>• Bot detection (CAPTCHA, fingerprinting)<br>• Graceful queue overflow handling |
| **Non-Functional** | • Scalability: 10M concurrent, 100K QPS<br>• Performance: Add-to-cart < 100ms p99<br>• Consistency: No overselling (strong consistency)<br>• Fairness: FIFO queue or random lottery<br>• Availability: 99.9% during sale<br>• Observability: Queue depth, inventory velocity, error rates |

---

## 3. Engineering Challenges

1. **Inventory Atomicity:** Prevent overselling with high concurrency (10K competing for 100 units)
2. **Thundering Herd:** 10M users hitting sale at same instant (queue/rate limit)
3. **Reservation Expiry:** Auto-release expired carts without blocking operations
4. **Fair Access:** Balance FIFO fairness vs scalability (queue vs lottery)
5. **Bot Mitigation:** Detect automated scripts, CAPTCHA without hurting UX
6. **Queue Overflow:** Handle 10M in virtual queue for 10K inventory
7. **Real-time Updates:** Push countdown, stock levels to clients without overload
8. **Purchase Limits:** Enforce per-user caps across multiple attempts
9. **Distributed Coordination:** Lock-free inventory decrement at scale
10. **Graceful Degradation:** Fall back to waiting page if queue full

---

## 4. Design Patterns Applied

| Concern | Pattern | Justification |
|---------|---------|---------------|
| Inventory reservation | **Pessimistic Lock** (or Optimistic) | Atomic decrement with SELECT FOR UPDATE |
| Queue management | **Producer-Consumer** | Enqueue requests, drain at controlled rate |
| Reservation expiry | **Scheduled Task / TTL** | Background job reclaims expired holds |
| Countdown sync | **Observer** | Push updates to subscribed clients via WebSocket |
| Sale lifecycle | **State** | NOT_STARTED → ACTIVE → SOLD_OUT → ENDED transitions |
| Rate limiting | **Token Bucket** | Per-user/IP request throttling |
| Bot detection | **Strategy** | Pluggable detectors (CAPTCHA, fingerprint, behavior) |
| Graceful degradation | **Circuit Breaker** | Switch to waiting page on overload |
| Inventory check | **Facade** | Simplify complex reservation + expiry logic |
| Fair allocation | **Strategy** | FIFO queue vs lottery picker |

---

## 5. Domain Model

| Entity / Component | Responsibility |
|--------------------|----------------|
| **FlashSale (Entity)** | Sale metadata (product, start/end time, inventory limit, state) |
| **SaleState (enum)** | NOT_STARTED, ACTIVE, SOLD_OUT, ENDED |
| **Inventory (Entity)** | Available units, reserved units, sold units (atomic counters) |
| **Reservation (Entity)** | User, quantity, expiry time, status |
| **ReservationStatus (enum)** | ACTIVE, EXPIRED, PURCHASED, CANCELLED |
| **VirtualQueue (Service)** | Admit users to sale at controlled rate |
| **QueueEntry (VO)** | User ID, position, admitted_at |
| **RateLimiter (Service)** | Token bucket per user/IP |
| **BotDetector (interface)** | Evaluate if request is from bot |
| **CaptchaValidator** | Verify CAPTCHA challenges |
| **FingerprintDetector** | Track device fingerprints, flag duplicates |
| **CountdownService** | Broadcast real-time countdown updates |
| **ReservationCleaner (Job)** | Scheduled task to reclaim expired reservations |
| **PurchaseLimitTracker (Service)** | Enforce per-user purchase caps |
| **EventPublisher** | Emit domain events (SaleStarted, SoldOut, ReservationExpired) |

---

## 6. UML Class Diagram (ASCII)

```
┌──────────────────┐         ┌──────────────────┐
│ FlashSaleService │────────>│ VirtualQueue     │
│ -inventory       │         │ +enqueue(user)   │
│ -queue           │         │ +admit()         │
│ -rateLimiter     │         └──────────────────┘
│ -botDetector     │
└────────┬─────────┘         ┌──────────────────┐
         │                   │ FlashSale (Entity)│
         │                   │ -productId       │
         v                   │ -startTime       │
┌──────────────────┐         │ -endTime         │
│ FlashSale        │<────────│ -inventoryLimit  │
│ +start()         │         │ -state           │
│ +end()           │         └────────┬─────────┘
│ +checkSoldOut()  │                  │
└────────┬─────────┘         ┌────────v─────────┐
         │                   │ SaleState        │<<enum>>
         │                   │ NOT_STARTED      │
         v                   │ ACTIVE           │
┌──────────────────┐         │ SOLD_OUT         │
│ Inventory        │         │ ENDED            │
│ -available       │         └──────────────────┘
│ -reserved        │
│ -sold            │         ┌──────────────────┐
│ +reserve(qty)    │         │ Reservation      │
│ +release(qty)    │────────>│ -userId          │
│ +confirm(qty)    │         │ -quantity        │
└──────────────────┘         │ -expiresAt       │
                             │ -status          │
                             └──────────────────┘

┌──────────────────┐         ┌──────────────────┐
│ BotDetector      │<<interface>>│ RateLimiter  │
│ +isBot(request)  │         │ +allow(userId)   │
└────────┬─────────┘         └──────────────────┘
         │
    ┌────┴─────┐
    │          │
┌───v────┐ ┌──v──────┐
│Captcha │ │Fingerprint│
│Validator│ │Detector │
└────────┘ └─────────┘
```

---

## 7. Sequence Diagram (Reserve Inventory During Flash Sale)

```
User      FlashSaleService  VirtualQueue  RateLimiter  BotDetector  Inventory  Reservation
 │              │               │              │            │            │           │
 │ addToCart    │               │              │            │            │           │
 ├─────────────>│               │              │            │            │           │
 │              │ checkRateLimit│              │            │            │           │
 │              ├───────────────┼─────────────>│            │            │           │
 │              │<──────────────┼──────────────┤            │            │           │
 │              │   allowed     │              │            │            │           │
 │              │               │              │            │            │           │
 │              │ checkBot      │              │            │            │           │
 │              ├───────────────┼──────────────┼───────────>│            │           │
 │              │<──────────────┼──────────────┼────────────┤            │           │
 │              │   not bot     │              │            │            │           │
 │              │               │              │            │            │           │
 │              │ checkQueue    │              │            │            │           │
 │              ├───────────────>              │            │            │           │
 │              │<──────────────┤              │            │            │           │
 │              │  admitted     │              │            │            │           │
 │              │               │              │            │            │           │
 │              │ reserve(qty, userId)         │            │            │           │
 │              ├───────────────┼──────────────┼────────────┼───────────>│           │
 │              │               │              │            │  SELECT FOR UPDATE     │
 │              │               │              │            │  available -= qty      │
 │              │               │              │            │  reserved += qty       │
 │              │               │              │            │<──────────┤           │
 │              │               │              │            │  success   │           │
 │              │               │              │            │            │           │
 │              │ createReservation(expiresIn=5min)         │            │           │
 │              ├───────────────┼──────────────┼────────────┼────────────┼──────────>│
 │              │<──────────────┼──────────────┼────────────┼────────────┼───────────┤
 │<─────────────┤               │              │            │            │           │
 │  Reservation │               │              │            │            │           │
```

---

## 8. Implementation (Java-like Pseudocode)

### Core Interfaces

```java
// ========== INTERVIEW-CRITICAL: Strategy pattern for fair allocation ==========
interface AllocationStrategy {
    List<QueueEntry> selectWinners(List<QueueEntry> queue, int availableUnits);
}

class FIFOAllocationStrategy implements AllocationStrategy {
    @Override
    public List<QueueEntry> selectWinners(List<QueueEntry> queue, int availableUnits) {
        return queue.stream()
            .sorted(Comparator.comparing(QueueEntry::getEnqueuedAt))
            .limit(availableUnits)
            .collect(Collectors.toList());
    }
}

class LotteryAllocationStrategy implements AllocationStrategy {
    @Override
    public List<QueueEntry> selectWinners(List<QueueEntry> queue, int availableUnits) {
        Collections.shuffle(queue); // Random selection
        return queue.stream()
            .limit(availableUnits)
            .collect(Collectors.toList());
    }
}

// ========== INTERVIEW-CRITICAL: Strategy pattern for bot detection ==========
interface BotDetector {
    boolean isBot(HttpServletRequest request, String userId);
}

class CaptchaValidator implements BotDetector {
    @Override
    public boolean isBot(HttpServletRequest request, String userId) {
        String captchaToken = request.getHeader("X-Captcha-Token");
        if (captchaToken == null) return true;
        
        // Verify with reCAPTCHA service
        return !verifyCaptcha(captchaToken);
    }
}

class FingerprintDetector implements BotDetector {
    private final FingerprintRepository fingerprintRepo;
    
    @Override
    public boolean isBot(HttpServletRequest request, String userId) {
        String fingerprint = extractFingerprint(request); // browser, IP, headers
        
        // Check if fingerprint used by multiple users (suspicious)
        long userCount = fingerprintRepo.countDistinctUsers(fingerprint);
        return userCount > 5; // Flag if > 5 users share same fingerprint
    }
}

class CompositeBotDetector implements BotDetector {
    private final List<BotDetector> detectors;
    
    @Override
    public boolean isBot(HttpServletRequest request, String userId) {
        // Any detector flags as bot → reject
        return detectors.stream().anyMatch(d -> d.isBot(request, userId));
    }
}
```

### Flash Sale Service

```java
// ========== INTERVIEW-CRITICAL: Facade with atomic inventory operations ==========
@Service
class FlashSaleService {
    private final FlashSaleRepository saleRepo;
    private final InventoryRepository inventoryRepo;
    private final ReservationRepository reservationRepo;
    private final VirtualQueue virtualQueue;
    private final RateLimiter rateLimiter;
    private final BotDetector botDetector;
    private final PurchaseLimitTracker purchaseLimitTracker;
    private final EventPublisher eventPublisher;
    
    public Reservation reserveInventory(String saleId, String userId, int quantity, 
                                        HttpServletRequest request) {
        // Step 1: Load flash sale
        FlashSale sale = saleRepo.findById(saleId)
            .orElseThrow(() -> new FlashSaleNotFoundException(saleId));
        
        // Step 2: Validate sale state
        if (sale.getState() != SaleState.ACTIVE) {
            throw new SaleNotActiveException("Sale not currently active");
        }
        
        // Step 3: Rate limiting
        if (!rateLimiter.allowRequest(userId, "flash_sale", 5, Duration.ofMinutes(1))) {
            throw new RateLimitExceededException("Too many requests");
        }
        
        // Step 4: Bot detection
        if (botDetector.isBot(request, userId)) {
            log.warn("Bot detected: userId={}, IP={}", userId, request.getRemoteAddr());
            throw new BotDetectedException("Automated access not allowed");
        }
        
        // Step 5: Check purchase limit
        int purchased = purchaseLimitTracker.getPurchased(saleId, userId);
        if (purchased + quantity > sale.getPurchaseLimit()) {
            throw new PurchaseLimitExceededException("Exceeds per-user limit");
        }
        
        // Step 6: Virtual queue admission
        if (!virtualQueue.isAdmitted(userId, saleId)) {
            QueuePosition position = virtualQueue.enqueue(userId, saleId);
            throw new QueuedException("You are in queue", position);
        }
        
        // Step 7: Reserve inventory (atomic)
        Inventory inventory = reserveInventoryAtomic(sale.getInventoryId(), quantity);
        
        // Step 8: Create reservation
        Reservation reservation = Reservation.builder()
            .id(UUID.randomUUID().toString())
            .saleId(saleId)
            .userId(userId)
            .quantity(quantity)
            .expiresAt(Instant.now().plus(Duration.ofMinutes(5)))
            .status(ReservationStatus.ACTIVE)
            .createdAt(Instant.now())
            .build();
        
        reservationRepo.save(reservation);
        
        // Step 9: Check if sold out
        if (inventory.getAvailable() == 0) {
            sale.setState(SaleState.SOLD_OUT);
            saleRepo.save(sale);
            eventPublisher.publish(new SaleSoldOutEvent(sale));
        }
        
        // Step 10: Publish event
        eventPublisher.publish(new InventoryReservedEvent(sale, userId, quantity));
        
        return reservation;
    }
    
    // ========== INTERVIEW-CRITICAL: Pessimistic locking for no overselling ==========
    @Transactional(isolation = Isolation.SERIALIZABLE)
    private Inventory reserveInventoryAtomic(String inventoryId, int quantity) {
        // SELECT FOR UPDATE ensures no concurrent modifications
        Inventory inventory = inventoryRepo.findByIdForUpdate(inventoryId)
            .orElseThrow(() -> new InventoryNotFoundException(inventoryId));
        
        if (inventory.getAvailable() < quantity) {
            throw new InsufficientInventoryException("Only " + inventory.getAvailable() + " units available");
        }
        
        inventory.setAvailable(inventory.getAvailable() - quantity);
        inventory.setReserved(inventory.getReserved() + quantity);
        
        return inventoryRepo.save(inventory);
    }
    
    public void confirmPurchase(String reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new IllegalStateException("Reservation not active");
        }
        
        if (reservation.getExpiresAt().isBefore(Instant.now())) {
            throw new ReservationExpiredException("Reservation expired");
        }
        
        // Update inventory: reserved → sold
        confirmInventoryAtomic(reservation);
        
        // Update reservation status
        reservation.setStatus(ReservationStatus.PURCHASED);
        reservation.setPurchasedAt(Instant.now());
        reservationRepo.save(reservation);
        
        // Track purchase limit
        purchaseLimitTracker.increment(reservation.getSaleId(), reservation.getUserId(), reservation.getQuantity());
        
        eventPublisher.publish(new PurchaseConfirmedEvent(reservation));
    }
    
    @Transactional(isolation = Isolation.SERIALIZABLE)
    private void confirmInventoryAtomic(Reservation reservation) {
        Inventory inventory = inventoryRepo.findByIdForUpdate(reservation.getInventoryId())
            .orElseThrow();
        
        inventory.setReserved(inventory.getReserved() - reservation.getQuantity());
        inventory.setSold(inventory.getSold() + reservation.getQuantity());
        
        inventoryRepo.save(inventory);
    }
}
```

### Reservation Cleaner (Background Job)

```java
// ========== INTERVIEW-CRITICAL: Scheduled task for expiry cleanup ==========
@Component
class ReservationCleanerJob {
    private final ReservationRepository reservationRepo;
    private final InventoryRepository inventoryRepo;
    private final EventPublisher eventPublisher;
    
    @Scheduled(fixedRate = 10000) // Every 10 seconds
    public void cleanExpiredReservations() {
        List<Reservation> expired = reservationRepo.findExpired(Instant.now());
        
        if (expired.isEmpty()) return;
        
        log.info("Cleaning {} expired reservations", expired.size());
        
        for (Reservation reservation : expired) {
            try {
                releaseReservation(reservation);
            } catch (Exception e) {
                log.error("Failed to release reservation {}", reservation.getId(), e);
            }
        }
    }
    
    @Transactional(isolation = Isolation.SERIALIZABLE)
    private void releaseReservation(Reservation reservation) {
        // Return inventory to available pool
        Inventory inventory = inventoryRepo.findByIdForUpdate(reservation.getInventoryId())
            .orElseThrow();
        
        inventory.setReserved(inventory.getReserved() - reservation.getQuantity());
        inventory.setAvailable(inventory.getAvailable() + reservation.getQuantity());
        
        inventoryRepo.save(inventory);
        
        // Update reservation status
        reservation.setStatus(ReservationStatus.EXPIRED);
        reservationRepo.save(reservation);
        
        eventPublisher.publish(new ReservationExpiredEvent(reservation));
    }
}
```

### Virtual Queue

```java
// ========== INTERVIEW-CRITICAL: Producer-Consumer pattern for controlled admission ==========
@Service
class VirtualQueue {
    private final Queue<QueueEntry> queue = new ConcurrentLinkedQueue<>();
    private final Set<String> admitted = ConcurrentHashMap.newKeySet();
    private final int admissionRate = 100; // per second
    
    public QueuePosition enqueue(String userId, String saleId) {
        String key = saleId + ":" + userId;
        
        if (admitted.contains(key)) {
            return QueuePosition.ADMITTED;
        }
        
        // Check if already in queue
        if (queue.stream().anyMatch(e -> e.getKey().equals(key))) {
            int position = calculatePosition(key);
            return new QueuePosition(position, false);
        }
        
        QueueEntry entry = new QueueEntry(key, userId, saleId, Instant.now());
        queue.offer(entry);
        
        int position = calculatePosition(key);
        return new QueuePosition(position, false);
    }
    
    public boolean isAdmitted(String userId, String saleId) {
        return admitted.contains(saleId + ":" + userId);
    }
    
    @Scheduled(fixedRate = 1000) // Every second
    public void admitFromQueue() {
        int toAdmit = Math.min(admissionRate, queue.size());
        
        for (int i = 0; i < toAdmit; i++) {
            QueueEntry entry = queue.poll();
            if (entry != null) {
                admitted.add(entry.getKey());
                log.debug("Admitted user {} to sale {}", entry.getUserId(), entry.getSaleId());
            }
        }
    }
    
    private int calculatePosition(String key) {
        int position = 0;
        for (QueueEntry entry : queue) {
            position++;
            if (entry.getKey().equals(key)) {
                return position;
            }
        }
        return -1; // Not found
    }
    
    public int getQueueDepth(String saleId) {
        return (int) queue.stream()
            .filter(e -> e.getSaleId().equals(saleId))
            .count();
    }
}
```

### Purchase Limit Tracker

```java
// ========== INTERVIEW-CRITICAL: Redis for distributed counters ==========
@Service
class PurchaseLimitTracker {
    private final RedisTemplate<String, Integer> redis;
    
    public int getPurchased(String saleId, String userId) {
        String key = buildKey(saleId, userId);
        Integer count = redis.opsForValue().get(key);
        return count != null ? count : 0;
    }
    
    public void increment(String saleId, String userId, int quantity) {
        String key = buildKey(saleId, userId);
        redis.opsForValue().increment(key, quantity);
        
        // Set expiration (cleanup after sale ends)
        redis.expire(key, Duration.ofHours(24));
    }
    
    private String buildKey(String saleId, String userId) {
        return "purchase_limit:" + saleId + ":" + userId;
    }
}
```

### Database Schema

```sql
-- ========== INTERVIEW-CRITICAL: Inventory with atomic counters ==========
CREATE TABLE flash_sales (
    id VARCHAR(36) PRIMARY KEY,
    product_id VARCHAR(36) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    inventory_limit INT NOT NULL,
    purchase_limit INT NOT NULL DEFAULT 1, -- per user
    state VARCHAR(20) NOT NULL, -- NOT_STARTED, ACTIVE, SOLD_OUT, ENDED
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    
    INDEX idx_state_start (state, start_time),
    INDEX idx_product (product_id)
);

CREATE TABLE inventory (
    id VARCHAR(36) PRIMARY KEY,
    flash_sale_id VARCHAR(36) NOT NULL,
    available INT NOT NULL DEFAULT 0,
    reserved INT NOT NULL DEFAULT 0,
    sold INT NOT NULL DEFAULT 0,
    version BIGINT NOT NULL DEFAULT 0, -- Optimistic locking alternative
    
    FOREIGN KEY (flash_sale_id) REFERENCES flash_sales(id),
    CONSTRAINT chk_inventory CHECK (available >= 0 AND reserved >= 0 AND sold >= 0)
);

CREATE TABLE reservations (
    id VARCHAR(36) PRIMARY KEY,
    sale_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    quantity INT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL, -- ACTIVE, EXPIRED, PURCHASED, CANCELLED
    created_at TIMESTAMP NOT NULL,
    purchased_at TIMESTAMP,
    
    FOREIGN KEY (sale_id) REFERENCES flash_sales(id),
    INDEX idx_user (user_id),
    INDEX idx_status_expires (status, expires_at), -- for cleanup job
    INDEX idx_sale_user (sale_id, user_id)
);
```

---

## 9. Thread Safety Analysis

**Concurrency Model:**
- **Inventory decrement:** SELECT FOR UPDATE (pessimistic lock) ensures atomicity
- **Reservation expiry:** Background job with row-level locks
- **Virtual queue:** ConcurrentLinkedQueue + ConcurrentHashMap for thread-safe operations
- **Rate limiting:** Atomic counters in Redis (INCR command)
- **Purchase limit tracking:** Redis atomic increments

**Critical Sections:**
1. **Reserve inventory:** Serializable transaction, SELECT FOR UPDATE on inventory row
2. **Confirm purchase:** Update inventory (reserved → sold) with row lock
3. **Expire reservation:** Return to available pool with row lock
4. **Queue admission:** Concurrent queue poll (single thread admitter avoids races)

**Race Conditions Prevented:**
- **Overselling:** Serializable isolation + check available >= quantity before decrement
- **Double reservation:** Unique constraint (sale_id, user_id) for single active reservation
- **Expired reservation purchase:** Check expiry timestamp before confirm
- **Concurrent expiry:** Pessimistic lock serializes inventory updates

---

## 10. Top 10 Interview Q&A

**Q1: How do you prevent overselling with high concurrency?**
**A:** Use pessimistic locking (SELECT FOR UPDATE) on inventory row. Transaction: check available >= quantity, then decrement atomically. Serializable isolation level. Alternative: Redis DECR with Lua script for atomic check-and-decrement. Trade-off: locking reduces throughput but guarantees correctness.

**Q2: How would you handle 10M users hitting sale start simultaneously?**
**A:** Virtual queue: accept all to queue (async), admit at controlled rate (100/sec). Return queue position to user. CDN serves waiting page. Load balancer distributes across nodes. Rate limit per user (5 req/min). CAPTCHA on suspicious IPs. Gradual admission prevents thundering herd.

**Q3: How do you implement fair allocation (FIFO vs lottery)?**
**A:** Strategy pattern. FIFO: enqueue with timestamp, admit oldest first. Lottery: random shuffle before admission. Hybrid: FIFO for first 50%, lottery for rest. Trade-off: FIFO may favor bots with low latency, lottery more fair but less predictable. Use lottery for extremely hot items (PS5, sneakers).

**Q4: How would you scale to multiple data centers?**
**A:** Challenge: inventory consistency across regions. Solution: Single region owns inventory, others forward requests. Use global load balancer with latency routing. Inventory sharded by sale_id (co-locate sale + inventory). Conflict-free: reserve from closest region, confirm centrally. Trade-off: higher latency for remote users.

**Q5: How do you handle reservation expiry without blocking sales?**
**A:** Background job runs every 10 seconds, finds expired reservations (status=ACTIVE AND expires_at < now). Process in batches, update inventory atomically. Use indexed query (idx_status_expires). Alternative: Redis TTL keys trigger cleanup lambda. Trade-off: slight delay (up to 10s) before inventory returns to pool.

**Q6: How would you detect and block bots?**
**A:** Multi-layered: 1) CAPTCHA on entry, 2) Rate limiting (5 req/min per user), 3) Fingerprinting (browser, IP, headers), 4) Behavioral analysis (too fast, no mouse movement), 5) Velocity checks (100 accounts from same IP). ML model scoring risk. Trade-off: false positives hurt legitimate users.

**Q7: How do you implement real-time countdown timers for millions of users?**
**A:** Server sends sale start time (epoch), client computes countdown locally (avoids server load). WebSocket for "sale started" event (or SSE). Push to all connected clients. Alternative: long-polling (lower scalability). CDN caches static countdown page. Trade-off: clock skew (client-side), mitigated by NTP sync check.

**Q8: How would you handle flash sale for physical stores (hybrid online/offline)?**
**A:** Unified inventory pool. In-store POS system reserves via same API. Eventual consistency acceptable (few seconds lag). Prefer online reservations (higher margin). Update inventory via event stream (Kafka). Resolve conflicts: timestamp-based last-write-wins. Trade-off: potential overselling if network partition.

**Q9: How do you enforce purchase limits per user across multiple attempts?**
**A:** Track in Redis: key=sale_id:user_id, value=purchased_count. Increment on confirm. TTL = sale duration + 1 day. Check before reservation. Handle account sharing: also track per device fingerprint. Strict limit: block at reservation. Soft limit: warn, allow override with CAPTCHA.

**Q10: How would you implement a priority queue for VIP users?**
**A:** Two queues: VIP (priority) and regular. Admit from VIP first (80% of capacity), regular (20%). VIP determined by user tier. Separate endpoints (/vip vs /regular). Monitor fairness metrics. Trade-off: regular users may feel unfair, mitigate by transparency (show VIP badge).

---

## 11. Extension Points

**Immediate Extensions:**
1. **Waitlist:** Queue overflow → waitlist, notify if inventory returns (cancellation)
2. **Pre-sale access:** Early access for VIP/subscribers (staggered start)
3. **Bundle sales:** Multiple products in flash sale, allocate atomically
4. **Dynamic pricing:** Increase price as inventory depletes (surge pricing)
5. **Lottery mode:** Random winner selection for ultra-hot items

**Advanced Features:**
1. **Fraud detection:** ML model for fake accounts, coordinated attacks
2. **Geofencing:** Limit access to specific regions (licensing, shipping)
3. **Referral incentives:** Earn early access by inviting friends
4. **Historical analytics:** Predict demand, right-size inventory
5. **Multi-wave sales:** Release inventory in batches (wave 1, 2, 3)

**Operational Improvements:**
1. **Metrics dashboard:** Real-time queue depth, sale velocity, bot detection rate
2. **A/B testing:** Test queue vs lottery, admission rate tuning
3. **Alerting:** Sold out faster than expected, bot spike detected
4. **Capacity planning:** Load testing, simulate 10M concurrent users
5. **Post-sale analysis:** Conversion rate, average reservation time, expiry rate

---

## 12. Testing Strategy

**Unit Tests:**
- **Inventory atomicity:** Concurrent reserve attempts, verify no overselling
- **Reservation expiry:** Verify expired carts return inventory correctly
- **Bot detection:** Various fingerprints, CAPTCHA validation
- **Purchase limits:** Exceed limit scenarios

**Integration Tests:**
- **End-to-end reservation:** Reserve → wait → confirm → inventory updated
- **Queue admission:** Enqueue → wait → admitted → reserve
- **Expiry job:** Expired reservation → inventory released
- **Rate limiting:** Burst requests, verify throttling

**Load Tests:**
- **Thundering herd:** 10K concurrent reserve requests at sale start
- **Sustained load:** 100K QPS for 10 minutes
- **Queue overflow:** 1M enqueue requests
- **Database locking:** Measure contention on inventory row

**Edge Cases:**
- **Reservation expires during checkout:** Confirm fails gracefully
- **Sale ends while in queue:** Reject new reservations
- **Concurrent expiry + confirm:** Pessimistic lock prevents conflict
- **Inventory goes negative:** Constraint violation, rollback
- **Bot circumvents CAPTCHA:** Fingerprint detector catches

**Property-Based Tests:**
- **Inventory invariant:** available + reserved + sold = inventory_limit (always)
- **No overselling:** sum(reservations.quantity) + inventory.sold ≤ inventory_limit
- **Expiry consistency:** Expired reservations eventually released
- **Fairness:** FIFO queue admits oldest first

---

## 13. Pitfalls & Anti-Patterns Avoided

| Anti-Pattern | How Avoided |
|--------------|-------------|
| **Optimistic locking only** | Pessimistic lock (SELECT FOR UPDATE) for critical inventory path |
| **No rate limiting** | Token bucket per user/IP prevents flood |
| **Unbounded queue** | Virtual queue with overflow → waiting page |
| **Synchronous expiry** | Background job avoids blocking sales |
| **No bot detection** | Multi-layered (CAPTCHA, fingerprint, behavior) |
| **Stale countdown timers** | Client-side computation from server-sent epoch |
| **Missing purchase limits** | Redis counters enforce per-user caps |
| **No queue visibility** | Return position, estimated wait time |
| **Hardcoded admission rate** | Configurable, tunable under load |
| **No audit trail** | Event log for reservations, purchases, expirations |

---

## 14. Complexity Analysis

| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| **Reserve inventory** | O(1) | O(1) | Row-level lock, single update |
| **Confirm purchase** | O(1) | O(1) | Update reservation + inventory |
| **Enqueue** | O(1) | O(1) | ConcurrentLinkedQueue offer |
| **Admit from queue** | O(R) | O(1) | R = admission rate (100) |
| **Check bot** | O(D) | O(1) | D = number of detectors (3-5) |
| **Expiry cleanup** | O(E) | O(E) | E = expired reservations (batch 100) |
| **Purchase limit check** | O(1) | O(1) | Redis GET |
| **Overall throughput** | 100K QPS | O(Q + R) | Q = queue size, R = reservations |

**Bottlenecks:**
- **Inventory row lock:** Single point of contention (Amdahl's law applies)
- **Mitigation:** Shard inventory by product_id, rate limit admission

---

## 15. Interview Evaluation Rubric

**Requirements Clarification (20%):**
- [ ] Identified no-overselling as critical constraint
- [ ] Clarified scale (10M concurrent, 100K QPS)
- [ ] Discussed fairness (queue vs lottery)
- [ ] Asked about bot mitigation, purchase limits

**System Design (30%):**
- [ ] Proposed virtual queue for admission control
- [ ] Atomic inventory operations (pessimistic lock)
- [ ] Reservation expiry with background cleanup
- [ ] Multi-layered bot detection
- [ ] Real-time countdown sync strategy

**Code Quality (25%):**
- [ ] Clean domain model (FlashSale, Inventory, Reservation)
- [ ] Proper use of patterns (Strategy, Facade, Producer-Consumer)
- [ ] Transactional consistency (SELECT FOR UPDATE)
- [ ] Thread-safe queue operations
- [ ] Idempotent operations

**Scalability & Performance (15%):**
- [ ] Analyzed time complexity (O(1) for core ops)
- [ ] Identified locking bottleneck, proposed rate limiting
- [ ] Queue overflow handling (waiting page)
- [ ] CDN for static content (countdown page)
- [ ] Redis for distributed counters

**Edge Cases & Testing (10%):**
- [ ] Overselling prevention (inventory invariant)
- [ ] Concurrent expiry + confirm conflicts
- [ ] Bot detection false positives
- [ ] Sale state transitions (NOT_STARTED → ACTIVE → SOLD_OUT)
- [ ] Comprehensive load testing strategy
