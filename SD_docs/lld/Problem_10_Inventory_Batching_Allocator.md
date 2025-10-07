# Problem 10: Inventory Batching Allocator (Bin Packing + Greedy Heuristics + Load Balancing)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design an inventory allocation system that efficiently distributes bulk orders across fragmented stock locations, minimizing shipments while maximizing fill rate and load balance.

**Assumptions / Scope:**
- Multiple warehouses with fragmented inventory
- Orders with multiple line items (SKU + quantity)
- Minimize number of shipments per order
- Balance load across warehouses
- Support partial allocation (backorder remaining)
- Consider shipping costs and warehouse capacity
- Scale: 1000 warehouses, 10K orders/hour, 100 items/order
- Out of scope: Real-time routing, dynamic pricing, returns processing

**Non-Functional Goals:**
- Allocate order in < 100ms
- Achieve 95%+ fill rate on first attempt
- Minimize split shipments (prefer 1-2 shipments/order)
- Balance warehouse utilization (prevent hot spots)
- Handle concurrent allocations safely

### 2. Core Requirements

**Functional:**
- Allocate order across available warehouse inventory
- Minimize number of shipments (bin packing optimization)
- Consider shipping costs (proximity, zone-based pricing)
- Support allocation constraints (hazmat, temperature, weight limits)
- Handle partial allocations with backorder creation
- Reserve allocated inventory atomically
- Support allocation priority (VIP customers, express orders)
- Rebalance allocations on cancellation
- Provide allocation preview (dry run)

**Non-Functional:**
- **Performance**: Allocate in < 100ms, 10K orders/hour
- **Consistency**: No double-allocation, atomic reservation
- **Optimality**: 90%+ of optimal solution (NP-hard problem)
- **Fairness**: Balance load across warehouses
- **Observability**: Track fill rate, split shipment rate, warehouse utilization

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Multi-Bin Packing (NP-Hard Optimization)**
- **Problem**: Assign order items to minimum warehouses while respecting capacity
- **Solution**: Greedy heuristics with best-fit decreasing (BFD)
- **Algorithm**:
```java
/**
 * Inventory allocation using Best-Fit Decreasing + Proximity
 */
class InventoryAllocationService {
    
    /**
     * INTERVIEW CRITICAL: Allocate order across warehouses
     */
    @Transactional
    public AllocationResult allocateOrder(Order order, AllocationStrategy strategy) {
        // 1. Fetch available inventory for all items
        Map<String, List<InventoryLocation>> availableInventory = 
            fetchAvailableInventory(order.getLineItems());
        
        // 2. Check if order can be fulfilled
        if (!isFullfillable(order, availableInventory)) {
            return handlePartialAllocation(order, availableInventory);
        }
        
        // 3. Run allocation algorithm
        List<Allocation> allocations = switch (strategy) {
            case MINIMIZE_SHIPMENTS -> minimizeShipments(order, availableInventory);
            case MINIMIZE_COST -> minimizeCost(order, availableInventory);
            case BALANCE_LOAD -> balanceLoad(order, availableInventory);
            case CLOSEST_WAREHOUSE -> closestWarehouse(order, availableInventory);
        };
        
        // 4. Validate constraints
        if (!validateConstraints(allocations, order)) {
            return AllocationResult.failure("Constraints violated");
        }
        
        // 5. Reserve inventory atomically
        boolean reserved = reserveInventory(allocations);
        if (!reserved) {
            return AllocationResult.failure("Reservation failed");
        }
        
        // 6. Create shipments
        List<Shipment> shipments = createShipments(allocations);
        
        return AllocationResult.success(allocations, shipments);
    }
    
    /**
     * INTERVIEW CRITICAL: Minimize shipments using bin packing heuristic
     */
    private List<Allocation> minimizeShipments(Order order, 
                                               Map<String, List<InventoryLocation>> inventory) {
        // Best-Fit Decreasing (BFD) heuristic
        // 1. Sort items by quantity (largest first)
        List<LineItem> sortedItems = order.getLineItems().stream()
            .sorted(Comparator.comparingInt(LineItem::getQuantity).reversed())
            .collect(Collectors.toList());
        
        // 2. Initialize bins (warehouses)
        List<Bin> bins = new ArrayList<>();
        
        // 3. For each item, find best-fit warehouse
        for (LineItem item : sortedItems) {
            String sku = item.getSku();
            int needed = item.getQuantity();
            
            List<InventoryLocation> locations = inventory.get(sku);
            if (locations == null || locations.isEmpty()) {
                continue; // Handle backorder separately
            }
            
            // Sort warehouses by current utilization (least full first for BFD)
            locations.sort(Comparator.comparingDouble(loc -> 
                getBinUtilization(bins, loc.getWarehouseId())
            ));
            
            int remaining = needed;
            
            for (InventoryLocation location : locations) {
                if (remaining <= 0) break;
                
                // Find or create bin for this warehouse
                Bin bin = findOrCreateBin(bins, location.getWarehouseId());
                
                // Check if bin can accommodate (capacity constraints)
                int canAllocate = Math.min(remaining, location.getAvailableQuantity());
                canAllocate = Math.min(canAllocate, bin.getRemainingCapacity());
                
                if (canAllocate > 0) {
                    bin.addItem(new AllocationItem(
                        sku,
                        canAllocate,
                        location.getWarehouseId()
                    ));
                    
                    remaining -= canAllocate;
                }
            }
        }
        
        // 4. Convert bins to allocations
        return bins.stream()
            .flatMap(bin -> bin.getItems().stream()
                .map(item -> new Allocation(
                    item.getSku(),
                    item.getQuantity(),
                    bin.getWarehouseId(),
                    order.getId()
                )))
            .collect(Collectors.toList());
    }
    
    /**
     * Alternative: Minimize shipping cost
     */
    private List<Allocation> minimizeCost(Order order,
                                          Map<String, List<InventoryLocation>> inventory) {
        List<Allocation> allocations = new ArrayList<>();
        
        for (LineItem item : order.getLineItems()) {
            String sku = item.getSku();
            int needed = item.getQuantity();
            
            List<InventoryLocation> locations = inventory.get(sku);
            if (locations == null) continue;
            
            // Sort by shipping cost (closest first)
            locations.sort(Comparator.comparingDouble(loc -> 
                calculateShippingCost(loc, order.getShippingAddress())
            ));
            
            int remaining = needed;
            
            for (InventoryLocation location : locations) {
                if (remaining <= 0) break;
                
                int allocated = Math.min(remaining, location.getAvailableQuantity());
                
                allocations.add(new Allocation(
                    sku,
                    allocated,
                    location.getWarehouseId(),
                    order.getId()
                ));
                
                remaining -= allocated;
            }
        }
        
        return allocations;
    }
    
    /**
     * INTERVIEW CRITICAL: Balance load across warehouses
     */
    private List<Allocation> balanceLoad(Order order,
                                         Map<String, List<InventoryLocation>> inventory) {
        // Get current warehouse utilization
        Map<UUID, Double> utilization = getWarehouseUtilization();
        
        List<Allocation> allocations = new ArrayList<>();
        
        for (LineItem item : order.getLineItems()) {
            String sku = item.getSku();
            int needed = item.getQuantity();
            
            List<InventoryLocation> locations = inventory.get(sku);
            if (locations == null) continue;
            
            // Sort by utilization (least utilized first)
            locations.sort(Comparator.comparingDouble(loc -> 
                utilization.getOrDefault(loc.getWarehouseId(), 0.0)
            ));
            
            int remaining = needed;
            
            for (InventoryLocation location : locations) {
                if (remaining <= 0) break;
                
                // Allocate proportionally to available capacity
                double availableCapacity = 1.0 - utilization.getOrDefault(
                    location.getWarehouseId(), 0.0
                );
                
                int allocated = (int) Math.min(
                    remaining,
                    Math.min(
                        location.getAvailableQuantity(),
                        availableCapacity * 1000 // Scale factor
                    )
                );
                
                if (allocated > 0) {
                    allocations.add(new Allocation(
                        sku,
                        allocated,
                        location.getWarehouseId(),
                        order.getId()
                    ));
                    
                    remaining -= allocated;
                    
                    // Update utilization
                    utilization.merge(location.getWarehouseId(), 0.01, Double::sum);
                }
            }
        }
        
        return allocations;
    }
    
    /**
     * Calculate bin utilization for BFD
     */
    private double getBinUtilization(List<Bin> bins, UUID warehouseId) {
        return bins.stream()
            .filter(bin -> bin.getWarehouseId().equals(warehouseId))
            .findFirst()
            .map(Bin::getUtilization)
            .orElse(0.0);
    }
    
    /**
     * Find existing bin or create new one
     */
    private Bin findOrCreateBin(List<Bin> bins, UUID warehouseId) {
        return bins.stream()
            .filter(bin -> bin.getWarehouseId().equals(warehouseId))
            .findFirst()
            .orElseGet(() -> {
                Warehouse warehouse = warehouseRepo.findById(warehouseId).orElseThrow();
                Bin newBin = new Bin(warehouseId, warehouse.getShipmentCapacity());
                bins.add(newBin);
                return newBin;
            });
    }
}

/**
 * Bin abstraction for bin packing
 */
class Bin {
    private final UUID warehouseId;
    private final int maxCapacity; // Max items per shipment
    private final List<AllocationItem> items = new ArrayList<>();
    private int currentWeight = 0;
    
    public Bin(UUID warehouseId, int maxCapacity) {
        this.warehouseId = warehouseId;
        this.maxCapacity = maxCapacity;
    }
    
    public void addItem(AllocationItem item) {
        items.add(item);
        currentWeight += item.getQuantity();
    }
    
    public int getRemainingCapacity() {
        return maxCapacity - currentWeight;
    }
    
    public double getUtilization() {
        return (double) currentWeight / maxCapacity;
    }
    
    public UUID getWarehouseId() {
        return warehouseId;
    }
    
    public List<AllocationItem> getItems() {
        return items;
    }
}
```

**Challenge 2: Atomic Multi-Warehouse Reservation**
- **Problem**: Prevent double-allocation when reserving from multiple warehouses
- **Solution**: Two-phase reservation with compensation
- **Algorithm**:
```java
/**
 * Atomic reservation across multiple warehouses
 */
class ReservationService {
    
    /**
     * INTERVIEW CRITICAL: Reserve inventory atomically with 2PC
     */
    @Transactional
    public boolean reserveInventory(List<Allocation> allocations) {
        List<Reservation> successfulReservations = new ArrayList<>();
        
        try {
            // PHASE 1: PREPARE - Reserve each location
            for (Allocation allocation : allocations) {
                Reservation reservation = reserveLocation(allocation);
                
                if (reservation == null) {
                    // Reservation failed, rollback all
                    throw new ReservationFailedException(
                        "Failed to reserve: " + allocation
                    );
                }
                
                successfulReservations.add(reservation);
            }
            
            // PHASE 2: COMMIT - Mark all as confirmed
            for (Reservation reservation : successfulReservations) {
                reservation.setStatus(ReservationStatus.CONFIRMED);
                reservationRepo.save(reservation);
            }
            
            return true;
            
        } catch (Exception e) {
            // COMPENSATE: Release all reservations
            compensateReservations(successfulReservations);
            return false;
        }
    }
    
    /**
     * Reserve inventory at single location with pessimistic lock
     */
    private Reservation reserveLocation(Allocation allocation) {
        // Lock inventory row
        InventoryLocation location = inventoryRepo.findByWarehouseAndSKUWithLock(
            allocation.getWarehouseId(),
            allocation.getSku()
        ).orElseThrow();
        
        // Check availability
        if (location.getAvailableQuantity() < allocation.getQuantity()) {
            return null; // Insufficient inventory
        }
        
        // Update inventory
        location.setAvailableQuantity(
            location.getAvailableQuantity() - allocation.getQuantity()
        );
        location.setReservedQuantity(
            location.getReservedQuantity() + allocation.getQuantity()
        );
        
        inventoryRepo.save(location);
        
        // Create reservation record
        Reservation reservation = Reservation.builder()
            .id(UUID.randomUUID())
            .orderId(allocation.getOrderId())
            .warehouseId(allocation.getWarehouseId())
            .sku(allocation.getSku())
            .quantity(allocation.getQuantity())
            .status(ReservationStatus.PENDING)
            .expiresAt(Instant.now().plus(Duration.ofMinutes(30)))
            .build();
        
        return reservationRepo.save(reservation);
    }
    
    /**
     * Compensation: Release all reservations
     */
    private void compensateReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            try {
                InventoryLocation location = inventoryRepo.findByWarehouseAndSKUWithLock(
                    reservation.getWarehouseId(),
                    reservation.getSku()
                ).orElseThrow();
                
                // Return inventory
                location.setAvailableQuantity(
                    location.getAvailableQuantity() + reservation.getQuantity()
                );
                location.setReservedQuantity(
                    location.setReservedQuantity() - reservation.getQuantity()
                );
                
                inventoryRepo.save(location);
                
                // Mark reservation as cancelled
                reservation.setStatus(ReservationStatus.CANCELLED);
                reservationRepo.save(reservation);
                
            } catch (Exception e) {
                logger.error("Failed to compensate reservation: {}", 
                           reservation.getId(), e);
            }
        }
    }
}
```

**Challenge 3: Constraint Validation**
- **Problem**: Ensure allocations respect shipping constraints (hazmat, weight, temperature)
- **Solution**: Rule-based constraint checker
- **Algorithm**:
```java
/**
 * Constraint validation engine
 */
class ConstraintValidator {
    
    /**
     * INTERVIEW CRITICAL: Validate all constraints
     */
    public boolean validateConstraints(List<Allocation> allocations, Order order) {
        // Group allocations by warehouse (shipment)
        Map<UUID, List<Allocation>> byWarehouse = allocations.stream()
            .collect(Collectors.groupingBy(Allocation::getWarehouseId));
        
        for (Map.Entry<UUID, List<Allocation>> entry : byWarehouse.entrySet()) {
            UUID warehouseId = entry.getKey();
            List<Allocation> shipmentAllocations = entry.getValue();
            
            // Load warehouse capabilities
            Warehouse warehouse = warehouseRepo.findById(warehouseId).orElseThrow();
            
            // Check constraints
            if (!validateWeightLimit(shipmentAllocations, warehouse)) {
                return false;
            }
            
            if (!validateHazmatRules(shipmentAllocations, warehouse)) {
                return false;
            }
            
            if (!validateTemperatureZones(shipmentAllocations, warehouse)) {
                return false;
            }
            
            if (!validateVolumeLimit(shipmentAllocations, warehouse)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validate weight limit per shipment
     */
    private boolean validateWeightLimit(List<Allocation> allocations, 
                                        Warehouse warehouse) {
        double totalWeight = 0.0;
        
        for (Allocation allocation : allocations) {
            Product product = productRepo.findBySku(allocation.getSku()).orElseThrow();
            totalWeight += product.getWeight() * allocation.getQuantity();
        }
        
        return totalWeight <= warehouse.getMaxShipmentWeight();
    }
    
    /**
     * Validate hazmat shipping rules
     */
    private boolean validateHazmatRules(List<Allocation> allocations,
                                        Warehouse warehouse) {
        // Check if warehouse is certified for hazmat
        boolean hasHazmat = allocations.stream()
            .map(Allocation::getSku)
            .map(sku -> productRepo.findBySku(sku).orElseThrow())
            .anyMatch(Product::isHazardousMaterial);
        
        if (hasHazmat && !warehouse.isHazmatCertified()) {
            return false;
        }
        
        // Hazmat items cannot be mixed with certain products
        if (hasHazmat) {
            boolean hasIncompatible = allocations.stream()
                .map(Allocation::getSku)
                .map(sku -> productRepo.findBySku(sku).orElseThrow())
                .anyMatch(p -> p.getCategory() == ProductCategory.FOOD || 
                              p.getCategory() == ProductCategory.COSMETICS);
            
            if (hasIncompatible) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validate temperature zone compatibility
     */
    private boolean validateTemperatureZones(List<Allocation> allocations,
                                             Warehouse warehouse) {
        Set<TemperatureZone> requiredZones = allocations.stream()
            .map(Allocation::getSku)
            .map(sku -> productRepo.findBySku(sku).orElseThrow())
            .map(Product::getRequiredTemperatureZone)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
        
        // Can only ship from one temperature zone per shipment
        if (requiredZones.size() > 1) {
            return false;
        }
        
        // Check if warehouse supports required zone
        if (!requiredZones.isEmpty()) {
            TemperatureZone required = requiredZones.iterator().next();
            if (!warehouse.getSupportedTemperatureZones().contains(required)) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validate volume/cubic capacity
     */
    private boolean validateVolumeLimit(List<Allocation> allocations,
                                        Warehouse warehouse) {
        double totalVolume = 0.0;
        
        for (Allocation allocation : allocations) {
            Product product = productRepo.findBySku(allocation.getSku()).orElseThrow();
            totalVolume += product.getVolume() * allocation.getQuantity();
        }
        
        return totalVolume <= warehouse.getMaxShipmentVolume();
    }
}
```

**Challenge 4: Partial Allocation & Backorders**
- **Problem**: Handle cases where not all items can be fulfilled
- **Solution**: Prioritize fulfillment, create backorders for remainder
- **Algorithm**:
```java
/**
 * Handle partial allocations
 */
class PartialAllocationService {
    
    /**
     * INTERVIEW CRITICAL: Handle unfulfillable orders
     */
    public AllocationResult handlePartialAllocation(Order order,
                                                    Map<String, List<InventoryLocation>> inventory) {
        // Calculate what can be fulfilled
        Map<String, Integer> fulfillable = new HashMap<>();
        Map<String, Integer> backorder = new HashMap<>();
        
        for (LineItem item : order.getLineItems()) {
            String sku = item.getSku();
            int needed = item.getQuantity();
            
            int available = inventory.getOrDefault(sku, List.of()).stream()
                .mapToInt(InventoryLocation::getAvailableQuantity)
                .sum();
            
            if (available >= needed) {
                fulfillable.put(sku, needed);
            } else if (available > 0) {
                // Partial fulfillment
                fulfillable.put(sku, available);
                backorder.put(sku, needed - available);
            } else {
                // Full backorder
                backorder.put(sku, needed);
            }
        }
        
        // Check if partial shipment is acceptable
        if (!order.allowsPartialShipment() && !backorder.isEmpty()) {
            return AllocationResult.failure("Partial shipment not allowed");
        }
        
        // Allocate fulfillable items
        List<Allocation> allocations = new ArrayList<>();
        if (!fulfillable.isEmpty()) {
            Order partialOrder = order.copy();
            partialOrder.setLineItems(
                fulfillable.entrySet().stream()
                    .map(e -> new LineItem(e.getKey(), e.getValue()))
                    .collect(Collectors.toList())
            );
            
            allocations = minimizeShipments(partialOrder, inventory);
        }
        
        // Create backorder for remaining
        List<BackorderItem> backorderItems = backorder.entrySet().stream()
            .map(e -> new BackorderItem(
                e.getKey(),
                e.getValue(),
                BackorderReason.OUT_OF_STOCK
            ))
            .collect(Collectors.toList());
        
        if (!backorderItems.isEmpty()) {
            Backorder bo = Backorder.builder()
                .id(UUID.randomUUID())
                .orderId(order.getId())
                .items(backorderItems)
                .status(BackorderStatus.PENDING)
                .createdAt(Instant.now())
                .build();
            
            backorderRepo.save(bo);
        }
        
        return AllocationResult.partial(allocations, backorderItems);
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Strategy** | Allocation algorithms (minimize shipments, cost, load) | Swap optimization strategy |
| **Template Method** | Allocation flow (fetch → allocate → validate → reserve) | Common steps with variant algorithms |
| **Command** | Reservation actions (reserve, release, confirm) | Encapsulate for compensation |
| **Factory** | Create allocation strategy based on priority | Instantiate based on order type |
| **Specification** | Constraint validation rules | Composable constraint checks |
| **Repository** | Data access for inventory, orders | Abstract persistence |
| **Saga** | Two-phase reservation with compensation | Distributed transaction handling |

### 5. Domain Model & Class Structure

```
┌─────────────────────────┐
│ AllocationService       │ (Application Service)
│  - reservationService   │
│  - constraintValidator  │
│  - inventoryRepo        │
└────────┬────────────────┘
         │ coordinates
         │
    ┌────┴──────────┬──────────────┬──────────────┐
    ▼               ▼              ▼              ▼
┌──────────┐  ┌──────────┐  ┌──────────┐   ┌──────────┐
│ Allocation│  │ Reservation│ │Constraint│   │Backorder │
│ (Entity)  │  │ (Entity)  │ │(Rules)   │   │(Entity)  │
└──────────┘  └──────────┘  └──────────┘   └──────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Allocation Flow**
```
OrderSvc  AllocSvc  InvRepo  ReservSvc  ConstraintVal  ShipmentSvc
   │         │         │         │            │             │
   ├─allocate─>│        │         │            │             │
   │         ├─fetchInv─>│        │            │             │
   │         │<─inv─────┤         │            │             │
   │         ├─runAlgo───────┐    │            │             │
   │         │<──────────────┘    │            │             │
   │         ├─validate─────────────────────>│             │
   │         │<─valid────────────────────────┤             │
   │         ├─reserve──────────>│            │             │
   │         │<─reserved──────────┤            │             │
   │         ├─createShipments────────────────────────────>│
   │         │<─shipments──────────────────────────────────┤
   │<─result─┤         │         │            │             │
```

### 7. Core Implementation (Interview-Critical Code)

```java
// Domain entities
public class Order {
    private UUID id;
    private List<LineItem> lineItems;
    private Address shippingAddress;
    private OrderPriority priority; // VIP, EXPRESS, STANDARD
    private boolean allowsPartialShipment;
}

public class LineItem {
    private String sku;
    private int quantity;
}

public class InventoryLocation {
    private UUID warehouseId;
    private String sku;
    private int availableQuantity;
    private int reservedQuantity;
    
    @Version
    private long version;
}

public class Allocation {
    private UUID id;
    private UUID orderId;
    private UUID warehouseId;
    private String sku;
    private int quantity;
    private AllocationStatus status; // PENDING, CONFIRMED, SHIPPED
}

public class Reservation {
    private UUID id;
    private UUID orderId;
    private UUID warehouseId;
    private String sku;
    private int quantity;
    private ReservationStatus status; // PENDING, CONFIRMED, CANCELLED
    private Instant expiresAt;
}

public class Warehouse {
    private UUID id;
    private String name;
    private Location location;
    private int shipmentCapacity; // Max items per shipment
    private double maxShipmentWeight; // kg
    private double maxShipmentVolume; // cubic meters
    private boolean hazmatCertified;
    private Set<TemperatureZone> supportedTemperatureZones;
}

public class Backorder {
    private UUID id;
    private UUID orderId;
    private List<BackorderItem> items;
    private BackorderStatus status; // PENDING, FULFILLED, CANCELLED
    private Instant createdAt;
    private Instant fulfilledAt;
}

public enum AllocationStrategy {
    MINIMIZE_SHIPMENTS,  // Bin packing
    MINIMIZE_COST,       // Closest warehouse
    BALANCE_LOAD,        // Even distribution
    CLOSEST_WAREHOUSE    // Proximity only
}

// Repository with locking
public interface InventoryLocationRepository {
    @Query("SELECT * FROM inventory_locations " +
           "WHERE warehouse_id = :warehouseId AND sku = :sku " +
           "FOR UPDATE")
    Optional<InventoryLocation> findByWarehouseAndSKUWithLock(
        @Param("warehouseId") UUID warehouseId,
        @Param("sku") String sku
    );
}
```

### 8. Thread Safety & Concurrency

**Reservation Locking:**
- Pessimistic lock on InventoryLocation (SELECT FOR UPDATE)
- Transaction isolation prevents phantom reads
- Compensation on partial failure

**Concurrent Allocations:**
- Each allocation in separate transaction
- No shared mutable state
- Atomic CAS on inventory version

**Batch Processing:**
- Partition orders by hash(orderId)
- Parallel allocation per partition
- No cross-partition dependencies

### 9. Top Interview Questions & Answers

**Q1: Why greedy heuristic instead of exact solution?**
**A:**
```
Bin packing is NP-hard:
- Exact: O(2^N) exponential time
- Greedy BFD: O(N log N) with 11/9 approximation ratio

For 100 items:
- Exact: 2^100 = 1.27×10^30 operations (infeasible)
- Greedy: ~700 operations (< 100ms)

BFD achieves 90-95% of optimal in practice
```

**Q2: How to handle allocation failures?**
**A:**
```java
try {
    allocations = allocate(order);
    reserve(allocations);
    createShipments(allocations);
} catch (ReservationFailedException e) {
    // Retry with different strategy
    allocations = allocate(order, CLOSEST_WAREHOUSE);
    reserve(allocations);
} catch (ConstraintViolationException e) {
    // Split into multiple shipments
    allocations = splitAndAllocate(order);
}
```

**Q3: How to minimize split shipments?**
**A:**
```java
// BFD naturally minimizes bins
// Additional optimization: Prefer warehouses with multiple SKUs

int score = 0;
for (LineItem item : order.getLineItems()) {
    if (warehouse.hasInventory(item.getSku())) {
        score++;
    }
}

// Sort warehouses by score (highest first)
// Allocate from warehouses that can fulfill more items
```

**Q4: What if allocation takes too long (> 100ms)?**
**A:**
```java
// Use timeout with fallback
CompletableFuture<AllocationResult> future = 
    CompletableFuture.supplyAsync(() -> 
        allocateWithOptimalStrategy(order)
    );

try {
    return future.get(100, TimeUnit.MILLISECONDS);
} catch (TimeoutException e) {
    // Fallback to simpler strategy
    return allocateWithClosestWarehouse(order);
}
```

**Q5: How to test bin packing logic?**
**A:**
```java
@Test
public void testMinimizeShipments() {
    Order order = Order.builder()
        .addItem("SKU001", 100)
        .addItem("SKU002", 50)
        .addItem("SKU003", 30)
        .build();
    
    // Setup inventory
    // WH1: SKU001=100, SKU002=25
    // WH2: SKU002=25, SKU003=30
    // WH3: SKU001=50
    
    List<Allocation> result = service.allocate(order, MINIMIZE_SHIPMENTS);
    
    // Expected: 2 shipments (WH1 + WH2)
    Set<UUID> warehouses = result.stream()
        .map(Allocation::getWarehouseId)
        .collect(Collectors.toSet());
    
    assertEquals(2, warehouses.size());
    assertTrue(warehouses.contains(WH1));
    assertTrue(warehouses.contains(WH2));
}
```

**Q6: How to handle warehouse capacity limits?**
**A:**
```java
// In bin packing, check capacity before adding
if (bin.getCurrentWeight() + item.getWeight() > bin.getMaxCapacity()) {
    // Create new bin (shipment)
    bin = new Bin(nextWarehouse);
}

// For weight/volume constraints
if (totalWeight > MAX_SHIPMENT_WEIGHT) {
    // Split into multiple shipments from same warehouse
    List<Shipment> splits = splitByWeight(allocations);
}
```

**Q7: Database schema?**
**A:**
```sql
CREATE TABLE inventory_locations (
    id UUID PRIMARY KEY,
    warehouse_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    available_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL,
    version BIGINT DEFAULT 0,
    UNIQUE(warehouse_id, sku)
);

CREATE INDEX idx_inventory_sku 
    ON inventory_locations(sku, available_quantity DESC);

CREATE TABLE allocations (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_allocations_order 
    ON allocations(order_id, status);

CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    warehouse_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

CREATE INDEX idx_reservations_expires 
    ON reservations(status, expires_at) 
    WHERE status = 'PENDING';

CREATE TABLE backorders (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL,
    sku VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    reason VARCHAR(50),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    fulfilled_at TIMESTAMP
);

CREATE INDEX idx_backorders_status 
    ON backorders(status, created_at);
```

**Q8: How to prioritize VIP orders?**
**A:**
```java
// Allocate VIP orders first
List<Order> sorted = orders.stream()
    .sorted(Comparator
        .comparing(Order::getPriority)
        .reversed() // VIP > EXPRESS > STANDARD
        .thenComparing(Order::getCreatedAt)
    )
    .collect(Collectors.toList());

for (Order order : sorted) {
    allocate(order);
}

// Or reserve capacity for VIP
if (order.getPriority() == VIP) {
    // Use warehouses with >50% availability
    // Ensures VIP gets best inventory
}
```

**Q9: How to rebalance on cancellation?**
**A:**
```java
@Transactional
public void cancelOrder(UUID orderId) {
    // Release reservations
    List<Reservation> reservations = reservationRepo.findByOrder(orderId);
    
    for (Reservation res : reservations) {
        InventoryLocation loc = inventoryRepo.findByWarehouseAndSKU(
            res.getWarehouseId(), res.getSku()
        );
        
        loc.setAvailableQuantity(
            loc.getAvailableQuantity() + res.getQuantity()
        );
        loc.setReservedQuantity(
            loc.getReservedQuantity() - res.getQuantity()
        );
        
        inventoryRepo.save(loc);
    }
    
    // Check if any backorders can now be fulfilled
    List<Backorder> backorders = backorderRepo.findPending();
    for (Backorder bo : backorders) {
        tryFulfillBackorder(bo);
    }
}
```

**Q10: What metrics to track?**
**A:**
```
KPIs:
1. Fill rate (% of items allocated on first attempt)
2. Split shipment rate (avg shipments per order)
3. Allocation latency (p50, p95, p99)
4. Warehouse utilization (% capacity used)
5. Backorder rate (% of orders with backorders)
6. Constraint violation rate

Alerts:
- Fill rate < 95% → Inventory shortage
- Allocation latency > 200ms → Optimize algorithm
- Utilization imbalance > 30% → Rebalance needed
```

### 10. Extensions & Variations

1. **Dynamic Reallocation**: Reassign on demand spikes
2. **Predictive Allocation**: Pre-position inventory based on forecasts
3. **Cross-Dock Optimization**: Route through consolidation centers
4. **Split-Merge Shipments**: Combine/split for efficiency
5. **Real-time Inventory Sync**: Stream updates from warehouses

### 11. Testing Strategy

**Unit Tests:**
- Bin packing algorithm correctness
- Constraint validation logic
- Reservation compensation
- Partial allocation handling

**Integration Tests:**
- Full allocation flow
- Concurrent order allocations
- Reservation atomicity
- Backorder creation

**Performance Tests:**
- 10K orders/hour throughput
- 100ms allocation latency
- 1000 concurrent reservations

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Exact bin packing (exponential time)
✅ **Do**: Greedy heuristic with good approximation

❌ **Avoid**: Allocate without reservation
✅ **Do**: Two-phase with compensation

❌ **Avoid**: Single-threaded allocation
✅ **Do**: Partition and parallelize

❌ **Avoid**: Ignore constraints until shipping
✅ **Do**: Validate during allocation

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| BFD allocation | O(N log N) | O(W) | N = items, W = warehouses |
| Minimize cost | O(N × W) | O(N) | Sort by cost per item |
| Balance load | O(N × W) | O(W) | Update utilization |
| Reserve | O(L) | O(L) | L = locations (2PC) |
| Validate constraints | O(A) | O(1) | A = allocations |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Algorithm** | 30% | Understands bin packing, BFD heuristic |
| **Atomicity** | 25% | Two-phase reservation with compensation |
| **Constraints** | 20% | Validates hazmat, weight, temperature |
| **Performance** | 15% | Greedy optimization, sub-100ms |
| **Real-world Awareness** | 10% | Partial allocation, backorders, load balance |

**Red Flags:**
- Attempts exact bin packing solution
- No reservation atomicity
- Ignores shipping constraints
- No handling for partial fulfillment
- Doesn't consider concurrent allocations

---
