# LLD Problems Batch 1: Problems 1-10
## Creational and Structural Design Patterns

---

## Problem 1: Parking Lot System (Singleton + Strategy + Factory)

### Deep Dive: Comprehensive Design

#### 1. Problem Clarification
Design a parking lot management system handling multiple levels, various spot sizes (motorcycle, compact, large, EV), entry/exit flow, ticketing, pricing, and capacity queries.

**Assumptions / Scope:**
- Supports vehicles: Motorcycle, Car, Bus, EV Car
- Spot types: Motorcycle, Compact, Large, EV (EV adds charger resource)
- Allocation: First-fit within allowed spot type hierarchy (e.g., Car can use Compact or Large; Motorcycle can use Motorcycle or higher; Bus needs contiguous Large spots)
- Pricing: Base rate + hourly tier (e.g., first hour fixed, subsequent per-hour); EV surcharge
- Time quantization: Round up to nearest 15 minutes
- Concurrency: Multiple entry/exit kiosks
- Scale: Single facility (≤ 10k spots) in-memory model with repository abstraction
- Out of scope: Online reservations, dynamic surge pricing, payment integration details

**Non-Functional Goals:**
- O(1) or O(log n) spot allocation
- Thread-safe concurrent entry/exit
- Extensible pricing & spot selection strategies
- Auditable transactions (tickets immutable once closed)

#### 2. Core Requirements

**Functional:**
- Issue ticket at entry with assigned spot
- Release spot & compute fee at exit
- Query availability (global & per spot type / floor)
- Support EV charging spot allocation constraints
- Support pricing strategy variants

**Non-Functional:**
- **Performance**: Entry allocation < 10ms typical
- **Consistency**: No double-allocation under concurrency
- **Reliability**: Recoverable state via repository (pluggable)
- **Extensibility**: New vehicle & pricing strategies with minimal change
- **Observability**: Events for ticket issued/closed

#### 3. Main Engineering Challenges & Solutions

**Challenge 1: Thread-Safe Spot Allocation**
- **Problem**: Multiple entry kiosks attempting to allocate same spot simultaneously
- **Solution**: 
  - Per-level synchronized blocks (reduces contention vs global lock)
  - Lock striping for high-concurrency scenarios
  - Atomic CAS operations on spot status
- **Algorithm**:
```
synchronized on Level:
  spot = availabilityIndex.popSpot(vehicleType)
  if spot != null:
    spot.markOccupied(vehicleId)
    return spot
  return null
```

**Challenge 2: Efficient Spot Search**
- **Problem**: O(n) linear search through all spots is too slow
- **Solution**: Multi-level availability index
  - Maintain TreeMap<SpotType, Queue<SpotId>> for O(1) pop
  - Separate indexes per floor for spatial locality
  - Bitmap or bitset for fast empty slot scanning
- **Algorithm**:
```
AvailabilityIndex:
  Map<SpotType, PriorityQueue<Spot>> freeSpots
  
  popSpot(type):
    // Try exact match first
    spot = freeSpots.get(type).poll()
    if spot != null: return spot
    
    // Try larger spots (upgrade)
    for largerType in getCompatibleTypes(type):
      spot = freeSpots.get(largerType).poll()
      if spot != null: return spot
    
    return null
```

**Challenge 3: Extensible Pricing Logic**
- **Problem**: Different pricing rules (hourly, daily, weekend, EV surcharge)
- **Solution**: Strategy Pattern + Chain of Responsibility
- **Algorithm**:
```
interface PricingStrategy:
  Money calculate(Ticket ticket, Duration duration)

class TieredPricingStrategy implements PricingStrategy:
  calculate(ticket, duration):
    hours = ceiling(duration.toMinutes() / 60.0)
    baseFee = FIRST_HOUR_RATE
    
    if hours > 1:
      baseFee += (hours - 1) * HOURLY_RATE
    
    // Apply multipliers
    if ticket.spotType == EV:
      baseFee *= EV_MULTIPLIER
    
    if isWeekend(ticket.startTime):
      baseFee *= WEEKEND_MULTIPLIER
    
    return Money.of(baseFee)
```

**Challenge 4: Preventing Memory Leaks from Unclosed Tickets**
- **Problem**: Vehicles exit without proper checkout, spots remain occupied
- **Solution**:
  - Background cleanup thread scans for stale tickets
  - Timeout mechanism (e.g., 24 hours auto-close)
  - Sensor integration for physical vacancy detection
- **Algorithm**:
```
class TicketCleanupService:
  scheduledExecutor.scheduleAtFixedRate():
    currentTime = clock.now()
    staleTickets = ticketRepo.findByOpenAndStartTimeBefore(
      currentTime.minus(24, HOURS)
    )
    
    for ticket in staleTickets:
      logger.warn("Auto-closing stale ticket", ticket.id)
      parkingLotService.closeTicket(ticket.id, ADMIN_OVERRIDE)
```

#### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Singleton** | ParkingLotService instance | Single point of coordination for the facility; prevents multiple conflicting lot managers |
| **Strategy** | Pricing & Allocation | Easily swap algorithms (weekend pricing, nearest-exit allocation) without changing core logic |
| **Factory Method** | Vehicle creation | Encapsulate instantiation logic; easy to add new vehicle types |
| **Repository** | Data persistence boundary | Abstract storage; swap in-memory ↔ database without changing business logic |
| **Observer** | Event publishing | Decouple ticket operations from audit/notification systems |
| **Aggregate Root (DDD)** | ParkingLot | Ensure consistency boundary; all spot changes through lot |
| **Value Object** | Ticket, Money | Immutability prevents accidental state corruption |
| **Facade** | ParkingLotService | Simplify complex subsystem (spots, levels, pricing) into clean API |

#### 5. Domain Model & Class Structure

```
┌─────────────────────┐
│  ParkingLotService  │ (Singleton Facade)
│  - levels           │
│  - allocStrategy    │
│  - pricingStrategy  │
│  - ticketRepo       │
│  - eventPublisher   │
└──────────┬──────────┘
           │ manages
           ▼
    ┌─────────────┐
    │   Level     │
    │  - spots[]  │
    │  - index    │
    └──────┬──────┘
           │ contains
           ▼
    ┌─────────────┐         ┌─────────────┐
    │ ParkingSpot │◄────────┤   Vehicle   │
    │  - type     │ occupies│  (abstract) │
    │  - state    │         └─────────────┘
    │  - vehicleId│              ▲
    └─────────────┘              │
                          ┌──────┴──────┐
                     Motorcycle  Car  Bus  EVCar
                     
    ┌──────────────────┐
    │     Ticket       │ (Value Object)
    │  - id            │
    │  - vehicle       │
    │  - spotId        │
    │  - startTime     │
    │  - endTime       │
    │  - fee           │
    └──────────────────┘
    
Strategies (interfaces):
┌────────────────────────┐
│ SpotAllocationStrategy │
│  + allocate(vehicle)   │
└────────────────────────┘
        ▲
        │implements
        │
┌───────┴────────────────┐
│  NearestFirstStrategy  │
│  RandomStrategy        │
│  LoadBalancingStrategy │
└────────────────────────┘

┌────────────────────────┐
│   PricingStrategy      │
│  + calculate(ticket)   │
└────────────────────────┘
        ▲
        │
┌───────┴────────────────┐
│  HourlyPricingStrategy │
│  FlatRateStrategy      │
│  DynamicPricingStrategy│
└────────────────────────┘
```

#### 6. Detailed Sequence Diagrams

**Sequence: Issue Ticket (Entry)**
```
Client          Service       AllocStrategy    Level      AvailIndex    TicketRepo    EventPub
  │                │                │            │             │             │            │
  ├─issueTicket──>│                │            │             │             │            │
  │   (vehicle)    │                │            │             │             │            │
  │                ├─allocate()────>│            │             │             │            │
  │                │                ├─findSpot─>│             │             │            │
  │                │                │            ├─popSpot()─>│             │            │
  │                │                │            │◄───spotId──┤             │            │
  │                │                │◄──spot────┤             │             │            │
  │                │◄──spot─────────┤            │             │             │            │
  │                ├─spot.occupy()─────────────>│             │             │            │
  │                ├─new Ticket()────────────────────────────────────────>│            │
  │                ├─save(ticket)───────────────────────────>│             │            │
  │                ├─publish(TicketIssued)──────────────────────────────────────────>│
  │◄───ticket──────┤                │            │             │             │            │
```

**Sequence: Close Ticket (Exit)**
```
Client          Service       PricingStrategy  Level      TicketRepo    EventPub
  │                │                │            │             │            │
  ├─closeTicket──>│                │            │             │            │
  │   (ticketId)   │                │            │             │            │
  │                ├─get(id)───────────────────────────────>│            │
  │                │◄──ticket────────────────────────────────┤            │
  │                ├─calculate()──>│            │             │            │
  │                │  (ticket,dur)  │            │             │            │
  │                │◄──fee──────────┤            │             │            │
  │                ├─ticket.close(fee)─────────────────────>│            │
  │                ├─releaseSpot()──────────>│               │            │
  │                ├─publish(TicketClosed)─────────────────────────────>│
  │◄───closedTicket┤                │            │             │            │
```

#### 7. Core Implementation (Java-esque Pseudocode)

```java
// Singleton ParkingLotService
public class ParkingLotService {
    private static volatile ParkingLotService instance;
    private static final Object lock = new Object();
    
    private final List<Level> levels;
    private final SpotAllocationStrategy allocationStrategy;
    private final PricingStrategy pricingStrategy;
    private final TicketRepository ticketRepository;
    private final EventPublisher eventPublisher;
    private final Clock clock;
    
    private ParkingLotService(/* dependencies */) {
        this.levels = initializeLevels();
        // ... initialize other fields
    }
    
    // Double-checked locking with volatile
    public static ParkingLotService getInstance() {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ParkingLotService(/* deps */);
                }
            }
        }
        return instance;
    }
    
    public Ticket issueTicket(Vehicle vehicle) throws NoSpotAvailableException {
        // Step 1: Allocate spot using strategy
        ParkingSpot spot = allocationStrategy.allocate(vehicle, levels)
            .orElseThrow(() -> new NoSpotAvailableException(vehicle.getType()));
        
        // Step 2: Mark spot as occupied (atomic operation)
        spot.occupy(vehicle.getId());
        
        // Step 3: Create immutable ticket
        Ticket ticket = Ticket.builder()
            .id(UUID.randomUUID())
            .vehicle(vehicle)
            .spotId(spot.getId())
            .startTime(clock.now())
            .build();
        
        // Step 4: Persist ticket
        ticketRepository.save(ticket);
        
        // Step 5: Publish event (async)
        eventPublisher.publish(new TicketIssuedEvent(ticket));
        
        return ticket;
    }
    
    public Ticket closeTicket(UUID ticketId) throws TicketNotFoundException {
        // Step 1: Retrieve ticket (idempotency check)
        Ticket ticket = ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        if (ticket.isClosed()) {
            return ticket; // Already closed, idempotent
        }
        
        // Step 2: Calculate duration and fee
        Instant endTime = clock.now();
        Duration parkingDuration = Duration.between(ticket.getStartTime(), endTime);
        Money fee = pricingStrategy.calculate(ticket, parkingDuration);
        
        // Step 3: Close ticket (returns new immutable instance)
        Ticket closedTicket = ticket.close(endTime, fee);
        ticketRepository.update(closedTicket);
        
        // Step 4: Release spot
        Level level = findLevelBySpotId(ticket.getSpotId());
        level.releaseSpot(ticket.getSpotId());
        
        // Step 5: Publish event
        eventPublisher.publish(new TicketClosedEvent(closedTicket));
        
        return closedTicket;
    }
    
    public AvailabilitySnapshot getAvailability() {
        return levels.stream()
            .map(Level::getAvailability)
            .reduce(AvailabilitySnapshot::merge)
            .orElse(AvailabilitySnapshot.empty());
    }
}

// Level with fine-grained locking
public class Level {
    private final String id;
    private final Map<String, ParkingSpot> spots; // spotId -> spot
    private final AvailabilityIndex availabilityIndex;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    public synchronized Optional<ParkingSpot> acquireSpot(SpotType type) {
        lock.writeLock().lock();
        try {
            String spotId = availabilityIndex.popSpot(type);
            if (spotId == null) {
                return Optional.empty();
            }
            
            ParkingSpot spot = spots.get(spotId);
            spot.markReserved(); // Prevents double allocation
            return Optional.of(spot);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public synchronized void releaseSpot(String spotId) {
        lock.writeLock().lock();
        try {
            ParkingSpot spot = spots.get(spotId);
            spot.free();
            availabilityIndex.pushSpot(spotId, spot.getType());
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public AvailabilitySnapshot getAvailability() {
        lock.readLock().lock();
        try {
            return availabilityIndex.snapshot();
        } finally {
            lock.readLock().unlock();
        }
    }
}

// Immutable Ticket (Value Object)
public final class Ticket {
    private final UUID id;
    private final Vehicle vehicle;
    private final String spotId;
    private final Instant startTime;
    private final Instant endTime; // null if open
    private final Money fee; // null if open
    
    // Private constructor, use builder
    private Ticket(Builder builder) { /* ... */ }
    
    public Ticket close(Instant endTime, Money fee) {
        if (isClosed()) {
            throw new IllegalStateException("Ticket already closed");
        }
        return new Builder(this)
            .endTime(endTime)
            .fee(fee)
            .build();
    }
    
    public boolean isClosed() {
        return endTime != null;
    }
    
    // Getters only, no setters (immutable)
    public UUID getId() { return id; }
    // ... other getters
    
    public static Builder builder() {
        return new Builder();
    }
}

// Strategy: Allocation
public interface SpotAllocationStrategy {
    Optional<ParkingSpot> allocate(Vehicle vehicle, List<Level> levels);
}

public class NearestFirstAllocationStrategy implements SpotAllocationStrategy {
    @Override
    public Optional<ParkingSpot> allocate(Vehicle vehicle, List<Level> levels) {
        SpotType requiredType = vehicle.getRequiredSpotType();
        
        // Try each level in order (nearest first)
        for (Level level : levels) {
            Optional<ParkingSpot> spot = level.acquireSpot(requiredType);
            if (spot.isPresent()) {
                return spot;
            }
        }
        
        return Optional.empty();
    }
}

// Strategy: Pricing
public interface PricingStrategy {
    Money calculate(Ticket ticket, Duration duration);
}

public class HourlyPricingStrategy implements PricingStrategy {
    private static final Money FIRST_HOUR_RATE = Money.of(5.00);
    private static final Money HOURLY_RATE = Money.of(3.00);
    private static final double EV_MULTIPLIER = 1.5;
    
    @Override
    public Money calculate(Ticket ticket, Duration duration) {
        // Round up to nearest 15 minutes
        long minutes = duration.toMinutes();
        long roundedMinutes = ((minutes + 14) / 15) * 15;
        double hours = roundedMinutes / 60.0;
        
        Money total = FIRST_HOUR_RATE;
        if (hours > 1) {
            total = total.add(HOURLY_RATE.multiply(hours - 1));
        }
        
        // Apply EV surcharge
        if (ticket.getVehicle() instanceof EVCar) {
            total = total.multiply(EV_MULTIPLIER);
        }
        
        return total;
    }
}
```

#### 8. Thread Safety & Concurrency

**Locking Strategy:**
- **Per-Level Locks**: Each level has its own ReadWriteLock
  - Write lock: spot allocation/release
  - Read lock: availability queries
  - **Benefit**: Parallel operations across levels

**Atomic Operations:**
```java
// CAS-based spot status update
public class ParkingSpot {
    private final AtomicReference<SpotState> state = 
        new AtomicReference<>(SpotState.FREE);
    
    public boolean occupy(String vehicleId) {
        return state.compareAndSet(SpotState.FREE, SpotState.OCCUPIED);
    }
}
```

**Avoiding Deadlocks:**
- Always acquire locks in same order (Level 1 → Level N)
- No nested level locks
- Use tryLock() with timeout for cross-level operations

**Idempotency:**
- Close ticket operation checks if already closed
- Spot release checks current state before modifying
- Event publishing failures logged but don't block

#### 9. Top Interview Questions & Answers

**Q1: Why is Singleton appropriate for ParkingLotService?**
**A:** 
- Single physical facility requires single coordinator
- Prevents conflicting state from multiple manager instances
- Centralized control over spot allocation
- Global access point for entry/exit kiosks
- **Alternative**: Use dependency injection for testing (inject singleton instance)

**Q2: Why use Strategy pattern for allocation and pricing?**
**A:**
- **Open-Closed Principle**: Add new strategies without modifying service
- **Runtime flexibility**: Switch strategies based on time/load
- **Testing**: Mock strategies independently
- **Examples**:
  - Allocation: NearestFirst → LoadBalancing during peak
  - Pricing: Hourly → Dynamic surge pricing on holidays

**Q3: How do you prevent double allocation of the same spot?**
**A:**
- Synchronized block at Level scope (not global)
- Atomic CAS operation on spot state
- Spot marked RESERVED before returning from allocate()
- AvailabilityIndex.pop() removes spot atomically
- **Race condition eliminated**: Two threads can't pop same spotId

**Q4: What's the complexity of spot allocation and why?**
**A:**
- **Best Case**: O(1) - first level has free spot, index.pop() is O(1)
- **Average Case**: O(k) where k = number of levels checked (typically 1-2)
- **Worst Case**: O(L) where L = total levels (when lot nearly full)
- **Space**: O(N) for N spots + O(T) active tickets
- **Optimization**: Maintain global free spot counter for O(1) "is full" check

**Q5: How would you handle spot reservations (future bookings)?**
**A:**
```java
class Reservation {
    UUID id;
    Vehicle vehicle;
    Instant startTime;
    Instant endTime;
    SpotType requestedType;
    ReservationStatus status; // PENDING, CONFIRMED, EXPIRED
}

// Algorithm:
1. Accept reservation request with time window
2. Create PENDING reservation
3. Background job runs periodically:
   - Check if current time within reservation window
   - Allocate spot if available
   - Mark CONFIRMED or EXPIRED
4. User arrives → validate reservation → skip allocation, direct to spot
```

**Q6: How do you test thread safety?**
**A:**
```java
@Test
public void testConcurrentAllocation() throws Exception {
    ParkingLotService service = ParkingLotService.getInstance();
    int numThreads = 100;
    int spotsAvailable = 50;
    
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch latch = new CountDownLatch(numThreads);
    AtomicInteger successCount = new AtomicInteger(0);
    
    for (int i = 0; i < numThreads; i++) {
        executor.submit(() -> {
            try {
                Ticket ticket = service.issueTicket(new Car("car-" + Thread.currentThread().getId()));
                successCount.incrementAndGet();
            } catch (NoSpotAvailableException e) {
                // Expected when spots run out
            } finally {
                latch.countDown();
            }
        });
    }
    
    latch.await(10, TimeUnit.SECONDS);
    
    // Verify: exactly spotsAvailable tickets issued, no more
    assertEquals(spotsAvailable, successCount.get());
    assertEquals(0, service.getAvailability().getTotalFree());
}
```

**Q7: What are the trade-offs between global lock vs per-level locks?**
**A:**
| Aspect | Global Lock | Per-Level Locks |
|--------|-------------|-----------------|
| **Simplicity** | Easier to reason about | More complex |
| **Throughput** | Low (serializes all ops) | High (parallel ops) |
| **Contention** | High | Low (distributed) |
| **Deadlock Risk** | None | Possible if not careful |
| **Use Case** | Small lots (<100 spots) | Large lots (>1000 spots) |

**Q8: How would you implement dynamic pricing (surge pricing)?**
**A:**
```java
public class DynamicPricingStrategy implements PricingStrategy {
    private final OccupancyMonitor occupancyMonitor;
    private final PricingStrategy baseStrategy;
    
    @Override
    public Money calculate(Ticket ticket, Duration duration) {
        Money baseFee = baseStrategy.calculate(ticket, duration);
        
        // Calculate surge multiplier based on occupancy
        double occupancyRate = occupancyMonitor.getCurrentOccupancy();
        double surgeMultiplier = calculateSurgeMultiplier(occupancyRate);
        
        return baseFee.multiply(surgeMultiplier);
    }
    
    private double calculateSurgeMultiplier(double occupancyRate) {
        if (occupancyRate < 0.50) return 1.0;  // No surge
        if (occupancyRate < 0.75) return 1.2;  // 20% surge
        if (occupancyRate < 0.90) return 1.5;  // 50% surge
        return 2.0;  // 100% surge (almost full)
    }
}
```

**Q9: How do you handle spot type hierarchy (Car can use Compact or Large)?**
**A:**
```java
enum SpotType {
    MOTORCYCLE(1),
    COMPACT(2),
    LARGE(3),
    EV(2); // Same as compact but with charger
    
    private final int size;
    
    public List<SpotType> getCompatibleTypes() {
        List<SpotType> compatible = new ArrayList<>();
        for (SpotType type : values()) {
            if (type.size >= this.size && type != EV) {
                compatible.add(type);
            }
        }
        return compatible;
    }
}

// In AllocationStrategy:
SpotType required = vehicle.getRequiredSpotType();
for (SpotType compatible : required.getCompatibleTypes()) {
    Optional<ParkingSpot> spot = level.acquireSpot(compatible);
    if (spot.isPresent()) return spot;
}
```

**Q10: What events should you publish and why?**
**A:**
```java
// Events for observability and audit
class TicketIssuedEvent {
    UUID ticketId;
    String vehicleId;
    String spotId;
    Instant timestamp;
    // Use case: Real-time dashboard, analytics, billing
}

class TicketClosedEvent {
    UUID ticketId;
    Duration duration;
    Money fee;
    Instant timestamp;
    // Use case: Revenue tracking, pattern analysis
}

class SpotOccupancyChangedEvent {
    String levelId;
    int totalFree;
    int totalOccupied;
    // Use case: Dynamic pricing, capacity planning
}

class NoSpotAvailableEvent {
    VehicleType requestedFor;
    Instant timestamp;
    // Use case: Demand analysis, expansion planning
}
```

#### 10. Extensions & Variations

1. **Multi-lot federation**: Service discovery, cross-lot availability
2. **Sensor integration**: Physical sensors validate occupancy
3. **Mobile app**: QR code ticket, spot navigation
4. **Handicapped spots**: Policy decorator for priority allocation
5. **Event sourcing**: Rebuild state from event log
6. **CQRS**: Separate read model for fast queries
7. **Payment integration**: Strategy for multiple payment gateways

#### 11. Testing Strategy

**Unit Tests:**
- AllocationStrategy compatibility logic
- PricingStrategy calculations with edge cases
- Ticket immutability and close() behavior
- AvailabilityIndex pop/push correctness

**Integration Tests:**
- Full issue → close ticket flow
- Concurrent allocations (thread safety)
- Spot release and re-allocation
- Event publishing verification

**Property-Based Tests:**
```java
@Property
public void totalSpotsRemainConstant(@ForAll List<Vehicle> vehicles) {
    int totalSpots = service.getTotalSpots();
    
    for (Vehicle v : vehicles) {
        try {
            service.issueTicket(v);
        } catch (NoSpotAvailableException e) {
            break;
        }
    }
    
    int free = service.getAvailability().getTotalFree();
    int occupied = service.getAvailability().getTotalOccupied();
    
    assertEquals(totalSpots, free + occupied);
}
```

#### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: God Object (one class doing everything)
✅ **Do**: Separate concerns (Level, Spot, Strategy, Service)

❌ **Avoid**: Global Singleton for strategies (hard to test)
✅ **Do**: Inject strategies as dependencies

❌ **Avoid**: Mutable Ticket (state corruption risk)
✅ **Do**: Immutable Ticket with builder pattern

❌ **Avoid**: No locking (race conditions)
✅ **Do**: Per-level locks with clear acquisition order

❌ **Avoid**: Exposing internal collections
✅ **Do**: Return immutable snapshots/views

#### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Issue Ticket | O(L) worst, O(1) avg | O(1) | L = levels |
| Close Ticket | O(1) | O(1) | Direct spot lookup |
| Get Availability | O(L) or O(1) cached | O(1) | Aggregate counters |
| Spot Allocation | O(1) | - | Index-based pop |
| Event Publishing | O(1) async | O(E) | E = event queue size |

**Space Complexity:**
- O(N) for N spots
- O(T) for T active tickets
- O(L) for L levels
- O(E) for event queue

#### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Domain Modeling** | 25% | Clear boundaries (Lot, Level, Spot, Ticket), proper Entity vs Value Object |
| **Thread Safety** | 20% | Correct synchronization, lock granularity reasoning, deadlock awareness |
| **Design Patterns** | 20% | Appropriate pattern selection (Strategy, Singleton, Factory), justification |
| **Extensibility** | 15% | Easy to add spot types/pricing without core changes |
| **Error Handling** | 10% | Validation, exception hierarchy, idempotency |
| **Performance** | 10% | O(1) allocation, availability indexing, bottleneck identification |

**Red Flags:**
- No concurrency consideration
- Tight coupling (no interfaces)
- Mutable shared state without protection
- Over-engineering (premature optimization)
- No testing strategy

---

---

## Problem 2: Factory Pattern - Vehicle Manufacturing System

### Main Algorithm Pseudocode

```
// Abstract Product
interface Vehicle:
    void manufacture()
    void test()
    void deliver()

// Concrete Products
class Car implements Vehicle:
    manufacture(): print("Manufacturing car")
    test(): print("Testing car safety")
    deliver(): print("Delivering car")

class Bike implements Vehicle:
    manufacture(): print("Manufacturing bike")
    test(): print("Testing bike")
    deliver(): print("Delivering bike")

class Truck implements Vehicle:
    manufacture(): print("Manufacturing truck")
    test(): print("Testing truck capacity")
    deliver(): print("Delivering truck")

// Abstract Factory
abstract class VehicleFactory:
    abstract Vehicle createVehicle()
    
    public Vehicle orderVehicle():
        vehicle = createVehicle()
        vehicle.manufacture()
        vehicle.test()
        vehicle.deliver()
        return vehicle

// Concrete Factories
class CarFactory extends VehicleFactory:
    Vehicle createVehicle():
        return new Car()

class BikeFactory extends VehicleFactory:
    Vehicle createVehicle():
        return new Bike()

class TruckFactory extends VehicleFactory:
    Vehicle createVehicle():
        return new Truck()

// Client Code
function main():
    factory = getFactory(vehicleType)
    vehicle = factory.orderVehicle()
```

### Frequently Asked Interview Questions

**Q1: What's the difference between Factory Method and Abstract Factory?**
**A:**
- **Factory Method**: Creates one product through inheritance. Single method in base class, overridden in subclasses.
- **Abstract Factory**: Creates families of related products through composition. Multiple factory methods for related products.

**Q2: When should you use Factory Pattern over direct instantiation?**
**A:** Use Factory when:
- Object creation is complex
- Need to decouple client from concrete classes
- Creation logic may change
- Need centralized object creation logic
- Runtime type determination required

**Q3: How does Factory Pattern follow Open-Closed Principle?**
**A:** New vehicle types can be added by creating new concrete classes and factories without modifying existing code. System is open for extension but closed for modification.

**Q4: What are the disadvantages of Factory Pattern?**
**A:**
- Increased code complexity with more classes
- Overhead for simple object creation
- Harder to understand for newcomers
- May lead to class explosion

**Q5: How would you handle vehicle configuration in Factory?**
**A:** Use Builder pattern alongside Factory. Factory creates the vehicle, Builder configures it with options like color, engine type, features. This separates object creation from configuration.

---

## Problem 3: Builder Pattern - SQL Query Builder

### Main Algorithm Pseudocode

```
class SQLQuery:
    private String select
    private String from
    private String where
    private String orderBy
    private String limit
    
    // Private constructor
    private SQLQuery(Builder builder):
        this.select = builder.select
        this.from = builder.from
        this.where = builder.where
        this.orderBy = builder.orderBy
        this.limit = builder.limit
    
    public String build():
        query = "SELECT " + select + " FROM " + from
        if where != null:
            query += " WHERE " + where
        if orderBy != null:
            query += " ORDER BY " + orderBy
        if limit != null:
            query += " LIMIT " + limit
        return query
    
    // Static nested Builder class
    static class Builder:
        private String select = "*"
        private String from
        private String where
        private String orderBy
        private String limit
        
        public Builder(String from):
            this.from = from
        
        public Builder select(String columns):
            this.select = columns
            return this
        
        public Builder where(String condition):
            this.where = condition
            return this
        
        public Builder orderBy(String column):
            this.orderBy = column
            return this
        
        public Builder limit(int count):
            this.limit = String.valueOf(count)
            return this
        
        public SQLQuery build():
            return new SQLQuery(this)

// Usage
query = new SQLQuery.Builder("users")
    .select("name, email")
    .where("age > 18")
    .orderBy("name ASC")
    .limit(10)
    .build()
```

### Frequently Asked Interview Questions

**Q1: What's the difference between Builder and Factory patterns?**
**A:**
- **Builder**: Constructs complex objects step-by-step. Focuses on HOW to build. Same construction process creates different representations.
- **Factory**: Creates objects in one step. Focuses on WHAT to build. Hides creation logic but doesn't control step-by-step construction.

**Q2: Why make the Builder an inner static class?**
**A:**
- Logical grouping: Builder is closely related to the class it builds
- Encapsulation: Access to private constructor
- Namespace management: Builder doesn't pollute outer namespace
- Convenience: Clear relationship between Builder and built class

**Q3: How does Builder pattern ensure immutability?**
**A:**
- All fields in main class are final
- No setters in main class
- Builder sets all values before construction
- Once built, object cannot be modified
- Each builder method returns new builder or same builder (fluent API)

**Q4: What are telescoping constructors and how does Builder solve them?**
**A:** 
- **Telescoping constructors**: Multiple constructors with increasing parameters. Hard to read, error-prone, doesn't scale.
- **Builder solution**: Named methods for each parameter, optional parameters, clear and readable, extensible.

**Q5: How would you handle validation in Builder?**
**A:**
- Validate in build() method before creating object
- Throw exception if invalid state
- Validate each parameter in setter methods
- Use defensive copying for mutable parameters
- Consider creating separate Validator class

---

## Problem 4: Hotel Booking Engine (State + Strategy + Factory)

### Deep Dive: Comprehensive Design

#### 1. Problem Clarification
Design a hotel booking system managing room inventory, reservations, pricing, check-in/check-out, cancellations, and overbooking strategies.

**Assumptions / Scope:**
- Support multiple room types: Standard, Deluxe, Suite, Presidential
- Handle bookings: Create, modify, cancel, check-in, check-out
- Dynamic pricing based on: season, occupancy, advance booking, room type
- Overbooking: Allow configurable overbooking percentage
- Booking states: Pending → Confirmed → CheckedIn → CheckedOut / Cancelled
- Capacity: Single property with 100-500 rooms
- Concurrent bookings from multiple channels (web, mobile, front desk)
- Payment integration abstracted (separate concern)
- Out of scope: Multi-property chains, loyalty programs (initially)

**Non-Functional Goals:**
- No double bookings under concurrency
- Sub-second booking confirmation
- Inventory consistency across channels
- Extensible pricing strategies
- Audit trail for booking changes
- Handle peak load (holiday season rushes)

#### 2. Core Requirements

**Functional:**
- Search available rooms by date range and type
- Create reservation with customer details
- Modify booking (dates, room type, guests)
- Cancel booking with refund policy
- Check-in guest and assign physical room
- Check-out guest and finalize charges
- Overbooking management with auto-upgrade
- Pricing calculation with discounts
- Block rooms for maintenance

**Non-Functional:**
- **Consistency**: No conflicting reservations
- **Performance**: Search < 100ms, booking < 1s
- **Availability**: 99.9% uptime
- **Scalability**: Handle 1000 concurrent requests
- **Auditability**: Track all booking changes
- **Extensibility**: New room types, pricing rules without core changes

#### 3. Main Engineering Challenges & Solutions

**Challenge 1: Preventing Double Bookings**
- **Problem**: Two users booking same room for overlapping dates
- **Solution**: Optimistic locking with version control + pessimistic lock during allocation
- **Algorithm**:
```
// Two-phase booking
Phase 1: Inventory Check (Optimistic)
  rooms = searchAvailable(startDate, endDate, roomType)
  if rooms.isEmpty(): throw NoAvailabilityException
  
Phase 2: Reservation Creation (Pessimistic)
  synchronized on RoomInventory:
    recheck availability // Prevent race condition
    if still available:
      room.addReservation(booking)
      booking.status = CONFIRMED
      inventoryCache.invalidate()
    else:
      throw ConflictException
```

**Challenge 2: Efficient Date Range Queries**
- **Problem**: Finding available rooms across date ranges is O(n*m) naive scan
- **Solution**: Interval tree + occupancy calendar
- **Data Structure**:
```
class OccupancyCalendar {
    // TreeMap: date -> Set<RoomId> (occupied rooms)
    TreeMap<LocalDate, Set<String>> dailyOccupancy;
    
    // Interval tree for fast range queries
    IntervalTree<Booking> bookingIntervals;
    
    List<Room> findAvailable(LocalDate start, LocalDate end, RoomType type) {
        // Step 1: Get all rooms of type
        List<Room> candidateRooms = roomRepository.findByType(type);
        
        // Step 2: Query interval tree for conflicts
        Set<String> conflictingRoomIds = bookingIntervals
            .query(start, end)
            .stream()
            .map(Booking::getRoomId)
            .collect(Collectors.toSet());
        
        // Step 3: Filter out conflicting rooms
        return candidateRooms.stream()
            .filter(room -> !conflictingRoomIds.contains(room.getId()))
            .collect(Collectors.toList());
    }
}

// Complexity: O(log n + k) where k = overlapping bookings
```

**Challenge 3: Dynamic Pricing Engine**
- **Problem**: Price varies by season, demand, booking lead time, duration
- **Solution**: Strategy pattern + Rule chain
- **Algorithm**:
```
interface PricingStrategy {
    Money calculatePrice(Booking booking, SearchCriteria criteria);
}

class DynamicPricingStrategy implements PricingStrategy {
    private List<PricingRule> rules;
    private SeasonalRateCalendar seasonalRates;
    
    Money calculatePrice(Booking booking, SearchCriteria criteria) {
        // Base price from room type
        Money basePrice = booking.getRoom().getBaseRate();
        
        // Apply rules in order
        Money finalPrice = basePrice;
        for (PricingRule rule : rules) {
            finalPrice = rule.apply(finalPrice, booking, criteria);
        }
        
        return finalPrice;
    }
}

// Example rules:
class SeasonalPricingRule implements PricingRule {
    Money apply(Money current, Booking booking, SearchCriteria criteria) {
        Season season = seasonCalendar.getSeason(booking.getCheckInDate());
        double multiplier = season.getPriceMultiplier(); // 0.8 - 2.0
        return current.multiply(multiplier);
    }
}

class AdvanceBookingDiscountRule implements PricingRule {
    Money apply(Money current, Booking booking, SearchCriteria criteria) {
        long daysInAdvance = ChronoUnit.DAYS.between(
            LocalDate.now(), 
            booking.getCheckInDate()
        );
        
        if (daysInAdvance > 60) return current.multiply(0.85); // 15% off
        if (daysInAdvance > 30) return current.multiply(0.90); // 10% off
        if (daysInAdvance > 14) return current.multiply(0.95); // 5% off
        return current;
    }
}

class OccupancyBasedPricingRule implements PricingRule {
    Money apply(Money current, Booking booking, SearchCriteria criteria) {
        double occupancy = inventoryService.getOccupancyRate(
            booking.getCheckInDate()
        );
        
        // Demand-based surge pricing
        if (occupancy > 0.90) return current.multiply(1.30);
        if (occupancy > 0.75) return current.multiply(1.15);
        if (occupancy < 0.40) return current.multiply(0.85);
        return current;
    }
}
```

**Challenge 4: Booking State Machine Management**
- **Problem**: Complex state transitions with validation and side effects
- **Solution**: State pattern with transition guards
- **Algorithm**:
```
enum BookingStatus {
    PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED, NO_SHOW
}

interface BookingState {
    BookingState confirm(Booking booking);
    BookingState checkIn(Booking booking);
    BookingState checkOut(Booking booking);
    BookingState cancel(Booking booking);
    boolean canModify();
}

class ConfirmedState implements BookingState {
    BookingState checkIn(Booking booking) {
        // Guard: Can only check-in on or after check-in date
        if (LocalDate.now().isBefore(booking.getCheckInDate())) {
            throw new IllegalStateException("Check-in date not reached");
        }
        
        // Side effect: Assign physical room
        Room physicalRoom = roomAssignmentService.assign(booking);
        booking.setAssignedRoom(physicalRoom);
        
        // Emit event
        eventPublisher.publish(new BookingCheckedInEvent(booking));
        
        return new CheckedInState();
    }
    
    BookingState cancel(Booking booking) {
        // Apply cancellation policy
        Money refund = cancellationPolicy.calculateRefund(booking);
        booking.setRefundAmount(refund);
        
        // Release inventory
        inventoryService.releaseRoom(booking.getRoomId(), 
            booking.getCheckInDate(), booking.getCheckOutDate());
        
        eventPublisher.publish(new BookingCancelledEvent(booking, refund));
        
        return new CancelledState();
    }
    
    boolean canModify() {
        return true;
    }
}

class CheckedInState implements BookingState {
    BookingState checkOut(Booking booking) {
        // Calculate final charges
        Money totalCharges = billingService.calculateFinalBill(booking);
        booking.setTotalCharges(totalCharges);
        
        // Clean room scheduling
        housekeepingService.scheduleRoomCleaning(booking.getAssignedRoom());
        
        eventPublisher.publish(new BookingCheckedOutEvent(booking));
        
        return new CheckedOutState();
    }
    
    boolean canModify() {
        return false; // Cannot modify after check-in
    }
}
```

**Challenge 5: Overbooking Management**
- **Problem**: Maximize revenue by intentional overbooking, handle no-shows
- **Solution**: Statistical model + auto-upgrade strategy
- **Algorithm**:
```
class OverbookingManager {
    private final double OVERBOOKING_PERCENTAGE = 0.10; // 10%
    private final HistoricalNoShowRate noShowPredictor;
    
    int calculateOverbookingCapacity(RoomType type, LocalDate date) {
        int physicalCapacity = inventory.getPhysicalRoomCount(type);
        double noShowRate = noShowPredictor.predict(type, date);
        
        // Allow overbooking based on historical no-show rate
        int allowedOverbooking = (int) Math.ceil(
            physicalCapacity * Math.min(noShowRate, OVERBOOKING_PERCENTAGE)
        );
        
        return physicalCapacity + allowedOverbooking;
    }
    
    void resolveOverbooking(LocalDate date) {
        List<Booking> confirmedBookings = bookingRepo
            .findByStatusAndCheckInDate(CONFIRMED, date);
        
        Map<RoomType, List<Booking>> bookingsByType = 
            confirmedBookings.stream()
                .collect(Collectors.groupingBy(Booking::getRoomType));
        
        for (Map.Entry<RoomType, List<Booking>> entry : bookingsByType.entrySet()) {
            RoomType type = entry.getKey();
            List<Booking> bookings = entry.getValue();
            int available = inventory.getAvailableCount(type, date);
            
            if (bookings.size() > available) {
                int overflow = bookings.size() - available;
                
                // Auto-upgrade strategy: upgrade to next tier
                RoomType upgradedType = type.getNextTier();
                List<Booking> toUpgrade = selectBookingsForUpgrade(bookings, overflow);
                
                for (Booking booking : toUpgrade) {
                    upgradeBooking(booking, upgradedType);
                    notifyCustomer(booking, "Complimentary upgrade");
                }
            }
        }
    }
    
    private List<Booking> selectBookingsForUpgrade(List<Booking> bookings, int count) {
        // Priority: loyal customers, longer stays, early bookers
        return bookings.stream()
            .sorted(Comparator
                .comparing(Booking::getCustomerLoyaltyTier).reversed()
                .thenComparing(Booking::getStayDuration).reversed()
                .thenComparing(Booking::getBookingDate))
            .limit(count)
            .collect(Collectors.toList());
    }
}
```

#### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **State** | Booking lifecycle | Clear state transitions (Pending→Confirmed→CheckedIn), encapsulates state-specific behavior |
| **Strategy** | Pricing, overbooking, cancellation policy | Swap algorithms dynamically (seasonal pricing, flexible cancellation rules) |
| **Factory** | Booking creation | Complex initialization logic with validation, different booking types (corporate, leisure, package) |
| **Repository** | Data access | Abstract persistence, enable caching layer, swap DB implementations |
| **Observer** | Event notifications | Decouple booking operations from notifications, analytics, audit logs |
| **Command** | Booking modifications | Undo capability, audit trail, queue operations |
| **Facade** | BookingService API | Simplify complex subsystem (inventory, pricing, state machine, events) |
| **Template Method** | Booking validation flow | Fixed skeleton (validate→allocate→persist→notify), customizable steps |
| **Chain of Responsibility** | Pricing rules | Flexible rule composition, easy to add/remove rules |

#### 5. Domain Model & Class Structure

```
┌──────────────────────┐
│   BookingService     │ (Facade)
│  - inventoryMgr      │
│  - pricingStrategy   │
│  - bookingRepo       │
│  - eventPublisher    │
└───────┬──────────────┘
        │ manages
        ▼
┌──────────────────────┐       ┌─────────────────┐
│      Booking         │       │  BookingState   │
│  - id                │──────>│   (interface)   │
│  - customer          │ state └─────────────────┘
│  - room              │              ▲
│  - checkInDate       │              │
│  - checkOutDate      │     ┌────────┴─────────┐
│  - status            │  Pending Confirmed CheckedIn
│  - totalPrice        │  CheckedOut Cancelled NoShow
└──────────────────────┘
        │ books
        ▼
┌──────────────────────┐       ┌──────────────────┐
│       Room           │       │    RoomType      │
│  - id                │──────>│   (enum)         │
│  - number            │  type │  STANDARD        │
│  - type              │       │  DELUXE          │
│  - floor             │       │  SUITE           │
│  - baseRate          │       │  PRESIDENTIAL    │
│  - amenities[]       │       └──────────────────┘
└──────────────────────┘

┌──────────────────────┐
│  InventoryManager    │
│  - occupancyCalendar │
│  - overbookingMgr    │
│  + searchAvailable() │
│  + reserve()         │
│  + release()         │
└──────────────────────┘
        │ uses
        ▼
┌──────────────────────┐
│  OccupancyCalendar   │
│  - dailyOccupancy    │
│  - intervalTree      │
│  + query(range)      │
└──────────────────────┘

Strategies:
┌──────────────────────┐
│  PricingStrategy     │
│  + calculate()       │
└──────────────────────┘
        ▲
        │
┌───────┴──────────────────────┐
│  FixedPricingStrategy        │
│  DynamicPricingStrategy      │
│  PackagePricingStrategy      │
└──────────────────────────────┘

┌──────────────────────┐
│ CancellationPolicy   │
│  + calculateRefund() │
└──────────────────────┘
        ▲
        │
┌───────┴──────────────────────┐
│  FlexiblePolicy              │
│  ModeratePolicy              │
│  StrictPolicy                │
└──────────────────────────────┘
```

#### 6. Detailed Sequence Diagrams

**Sequence: Create Booking**
```
Client      Service      Inventory    Pricing     StateMachine   Repo      Events
  │            │             │           │              │          │          │
  ├─create()──>│             │           │              │          │          │
  │            ├─search()───>│           │              │          │          │
  │            │◄──rooms─────┤           │              │          │          │
  │            ├─calculate()─────────────>│              │          │          │
  │            │◄────price───────────────┤              │          │          │
  │            ├─new Booking()────────────────────────>│          │          │
  │            ├─confirm()────────────────────────────>│          │          │
  │            │◄──confirmedState────────────────────┤          │          │
  │            ├─reserve()──>│           │              │          │          │
  │            ├─save()──────────────────────────────────────────>│          │
  │            ├─publish()──────────────────────────────────────────────────>│
  │◄──booking──┤             │           │              │          │          │
```

**Sequence: Check-In**
```
Client      Service      State        Assignment   Housekeeping   Repo    Events
  │            │            │              │              │          │       │
  ├─checkIn()─>│            │              │              │          │       │
  │            ├─get()──────────────────────────────────────────────>│       │
  │            │◄──booking──────────────────────────────────────────┤       │
  │            ├─checkIn()─>│              │              │          │       │
  │            │            ├─assign()────>│              │          │       │
  │            │            │◄─room────────┤              │          │       │
  │            │            ├─block()──────────────────────>│          │       │
  │            │◄──newState─┤              │              │          │       │
  │            ├─update()────────────────────────────────────────────>│       │
  │            ├─publish()──────────────────────────────────────────────────>│
  │◄──success──┤            │              │              │          │       │
```

#### 7. Core Implementation (Java-esque Pseudocode)

```java
// Facade: BookingService
public class BookingService {
    private final InventoryManager inventoryManager;
    private final PricingStrategy pricingStrategy;
    private final BookingRepository bookingRepository;
    private final EventPublisher eventPublisher;
    private final BookingFactory bookingFactory;
    
    public Booking createBooking(BookingRequest request) throws NoAvailabilityException {
        // Step 1: Validate request
        validateBookingRequest(request);
        
        // Step 2: Search availability
        List<Room> availableRooms = inventoryManager.searchAvailable(
            request.getCheckInDate(),
            request.getCheckOutDate(),
            request.getRoomType()
        );
        
        if (availableRooms.isEmpty()) {
            throw new NoAvailabilityException(request);
        }
        
        // Step 3: Select room (strategy: nearest to elevator, specific floor, etc.)
        Room selectedRoom = roomSelectionStrategy.select(availableRooms, request.getPreferences());
        
        // Step 4: Calculate price
        Money totalPrice = pricingStrategy.calculatePrice(
            selectedRoom,
            request.getCheckInDate(),
            request.getCheckOutDate(),
            request
        );
        
        // Step 5: Create booking (Factory)
        Booking booking = bookingFactory.create(
            request.getCustomer(),
            selectedRoom,
            request.getCheckInDate(),
            request.getCheckOutDate(),
            totalPrice
        );
        
        // Step 6: Reserve inventory (atomic operation)
        synchronized (inventoryManager) {
            // Recheck availability to prevent race condition
            if (!inventoryManager.isAvailable(selectedRoom, request.getCheckInDate(), request.getCheckOutDate())) {
                throw new ConflictException("Room no longer available");
            }
            
            inventoryManager.reserve(selectedRoom, request.getCheckInDate(), request.getCheckOutDate());
            booking.confirm(); // State transition: PENDING → CONFIRMED
        }
        
        // Step 7: Persist
        booking = bookingRepository.save(booking);
        
        // Step 8: Publish event
        eventPublisher.publish(new BookingCreatedEvent(booking));
        
        // Step 9: Send confirmation (async)
        notificationService.sendBookingConfirmation(booking);
        
        return booking;
    }
    
    public Booking checkIn(UUID bookingId) throws BookingNotFoundException {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(bookingId));
        
        // Delegate to state machine
        booking.checkIn();
        
        // Assign physical room
        Room assignedRoom = roomAssignmentService.assignRoom(booking);
        booking.setAssignedRoom(assignedRoom);
        
        // Update
        booking = bookingRepository.update(booking);
        
        // Event
        eventPublisher.publish(new BookingCheckedInEvent(booking));
        
        return booking;
    }
    
    public Booking cancelBooking(UUID bookingId, String reason) throws BookingNotFoundException {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(bookingId));
        
        // Delegate to state machine
        booking.cancel(reason);
        
        // Calculate refund based on policy
        Money refund = cancellationPolicy.calculateRefund(booking);
        booking.setRefundAmount(refund);
        
        // Release inventory
        inventoryManager.release(
            booking.getRoom(),
            booking.getCheckInDate(),
            booking.getCheckOutDate()
        );
        
        // Update
        booking = bookingRepository.update(booking);
        
        // Event
        eventPublisher.publish(new BookingCancelledEvent(booking, refund));
        
        // Initiate refund (async)
        paymentService.processRefund(booking, refund);
        
        return booking;
    }
}

// State Pattern Implementation
public class Booking {
    private UUID id;
    private Customer customer;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Money totalPrice;
    private BookingState state; // Current state
    private BookingStatus status; // Enum for persistence
    
    public void confirm() {
        this.state = state.confirm(this);
        this.status = BookingStatus.CONFIRMED;
    }
    
    public void checkIn() {
        this.state = state.checkIn(this);
        this.status = BookingStatus.CHECKED_IN;
    }
    
    public void checkOut() {
        this.state = state.checkOut(this);
        this.status = BookingStatus.CHECKED_OUT;
    }
    
    public void cancel(String reason) {
        this.state = state.cancel(this);
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
    }
    
    public boolean canModify() {
        return state.canModify();
    }
}

// Interval Tree for efficient date range queries
public class OccupancyCalendar {
    private IntervalTree<Booking> bookingIntervals = new IntervalTree<>();
    private Map<String, TreeMap<LocalDate, Boolean>> roomOccupancy = new ConcurrentHashMap<>();
    
    public void addBooking(Booking booking) {
        Interval interval = new Interval(
            booking.getCheckInDate(),
            booking.getCheckOutDate()
        );
        bookingIntervals.insert(interval, booking);
        
        // Update daily occupancy map
        String roomId = booking.getRoom().getId();
        roomOccupancy.putIfAbsent(roomId, new TreeMap<>());
        
        LocalDate current = booking.getCheckInDate();
        while (!current.isAfter(booking.getCheckOutDate())) {
            roomOccupancy.get(roomId).put(current, true);
            current = current.plusDays(1);
        }
    }
    
    public List<Room> findAvailable(LocalDate checkIn, LocalDate checkOut, RoomType type) {
        // Get all rooms of type
        List<Room> candidateRooms = roomRepository.findByType(type);
        
        // Query interval tree for overlapping bookings
        List<Booking> overlappingBookings = bookingIntervals.query(checkIn, checkOut);
        Set<String> occupiedRoomIds = overlappingBookings.stream()
            .map(b -> b.getRoom().getId())
            .collect(Collectors.toSet());
        
        // Filter available rooms
        return candidateRooms.stream()
            .filter(room -> !occupiedRoomIds.contains(room.getId()))
            .collect(Collectors.toList());
    }
}

// Dynamic Pricing Strategy with Rule Chain
public class DynamicPricingStrategy implements PricingStrategy {
    private List<PricingRule> rules = new ArrayList<>();
    
    public DynamicPricingStrategy() {
        // Initialize rule chain
        rules.add(new SeasonalPricingRule());
        rules.add(new AdvanceBookingDiscountRule());
        rules.add(new OccupancyBasedPricingRule());
        rules.add(new LengthOfStayDiscountRule());
        rules.add(new WeekdayWeekendRule());
    }
    
    @Override
    public Money calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut, BookingRequest request) {
        Money basePrice = room.getBaseRate();
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        Money totalBase = basePrice.multiply(nights);
        
        // Apply each rule in sequence
        PricingContext context = new PricingContext(room, checkIn, checkOut, request);
        Money finalPrice = totalBase;
        
        for (PricingRule rule : rules) {
            finalPrice = rule.apply(finalPrice, context);
        }
        
        return finalPrice;
    }
}
```

#### 8. Thread Safety & Concurrency

**Critical Sections:**
1. **Inventory Reservation**: Synchronized block or pessimistic DB lock
2. **Booking State Transitions**: Use optimistic locking with version field
3. **Overbooking Calculations**: Read from cached occupancy stats

**Synchronization Strategy:**
```java
// Option 1: Optimistic Locking (JPA)
@Entity
public class Booking {
    @Version
    private Long version;
    // ... other fields
}

// Repository will throw OptimisticLockException on conflict
// Client retry logic:
int retries = 3;
while (retries-- > 0) {
    try {
        booking = bookingRepository.save(booking);
        break;
    } catch (OptimisticLockException e) {
        booking = bookingRepository.findById(booking.getId()).get();
        // Reapply changes
    }
}

// Option 2: Pessimistic Locking
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("SELECT b FROM Booking b WHERE b.id = :id")
Booking findByIdWithLock(@Param("id") UUID id);

// Option 3: Distributed Lock (Redis)
try (RedisLock lock = redisLockService.acquire("booking:" + bookingId, 10, TimeUnit.SECONDS)) {
    // Critical section
    booking.confirm();
    bookingRepository.save(booking);
}
```

**Cache Consistency:**
- Invalidate inventory cache on reservation/cancellation
- Use write-through cache for availability queries
- TTL-based expiration (30-60 seconds acceptable for availability data)

#### 9. Top Interview Questions & Answers

**Q1: Why use State pattern for booking lifecycle?**
**A:**
- **Eliminates conditionals**: No giant if-else for state checks
- **Encapsulation**: State-specific behavior in separate classes
- **Type safety**: Compiler enforces valid transitions
- **Extensibility**: Add new states (e.g., WaitListed) without modifying existing states
- **Single Responsibility**: Each state class handles its own transitions

**Q2: How do you prevent double bookings?**
**A:**
1. **Two-phase reservation**:
   - Phase 1: Optimistic search (no lock)
   - Phase 2: Pessimistic reserve (synchronized or DB lock)
2. **Recheck availability** after acquiring lock
3. **Optimistic locking** with version field for booking updates
4. **Idempotency keys** for retry scenarios
5. **Database unique constraint** on (roomId, date) if using daily occupancy table

**Q3: How would you handle peak load (Black Friday, holiday season)?**
**A:**
```
1. Read Replicas: Route search queries to read replicas
2. Caching Layer:
   - Cache room availability (60s TTL)
   - Cache pricing rules
   - Distributed cache (Redis)
3. Queue System:
   - Queue booking requests during peak
   - Rate limiting per user/IP
   - Return estimated wait time
4. Circuit Breaker:
   - Fail fast if inventory service degraded
   - Return cached "likely available" results
5. Database Optimization:
   - Partition by date range
   - Index on (roomType, checkInDate, checkOutDate)
   - Materialized view for availability counts
6. Async Processing:
   - Confirmation emails sent async
   - Analytics/audit logs async
```

**Q4: Explain your pricing strategy design. Why Chain of Responsibility?**
**A:**
- **Flexibility**: Add/remove/reorder pricing rules at runtime
- **Single Responsibility**: Each rule handles one concern (season, advance booking, occupancy)
- **Open-Closed**: New rules added without modifying existing ones
- **Composability**: Combine rules in different ways for different property types
- **Testing**: Test each rule independently

**Example**:
```java
// Standard property: all rules
StandardProperty: SeasonalRule → AdvanceBookingRule → OccupancyRule

// Budget property: simplified
BudgetProperty: FlatRateRule → WeekendSurchargeRule

// Luxury property: aggressive dynamic pricing
LuxuryProperty: SeasonalRule → OccupancyRule → EventBasedSurgeRule
```

**Q5: How do you handle booking modifications (date change, room type upgrade)?**
**A:**
```java
public Booking modifyBooking(UUID bookingId, ModificationRequest request) {
    Booking booking = bookingRepository.findById(bookingId).orElseThrow();
    
    // Validate: Only CONFIRMED bookings can be modified
    if (!booking.canModify()) {
        throw new IllegalStateException("Booking cannot be modified in current state");
    }
    
    // Store original for rollback
    BookingSnapshot originalSnapshot = booking.snapshot();
    
    try {
        // Release current inventory
        inventoryManager.release(booking.getRoom(), booking.getCheckInDate(), booking.getCheckOutDate());
        
        // Apply modifications
        booking.setCheckInDate(request.getNewCheckInDate());
        booking.setCheckOutDate(request.getNewCheckOutDate());
        booking.setRoomType(request.getNewRoomType());
        
        // Reserve new inventory
        Room newRoom = inventoryManager.searchAndReserve(
            request.getNewCheckInDate(),
            request.getNewCheckOutDate(),
            request.getNewRoomType()
        ).orElseThrow(() -> new NoAvailabilityException());
        
        booking.setRoom(newRoom);
        
        // Recalculate price
        Money newPrice = pricingStrategy.calculatePrice(/* ... */);
        Money priceDifference = newPrice.subtract(booking.getTotalPrice());
        booking.setTotalPrice(newPrice);
        
        // Save
        booking = bookingRepository.save(booking);
        
        // Event
        eventPublisher.publish(new BookingModifiedEvent(booking, originalSnapshot, priceDifference));
        
        return booking;
        
    } catch (Exception e) {
        // Rollback: restore original reservation
        originalSnapshot.restore(booking);
        throw e;
    }
}
```

**Q6: How do you implement cancellation policies (flexible, moderate, strict)?**
**A:**
```java
interface CancellationPolicy {
    Money calculateRefund(Booking booking, LocalDate cancellationDate);
}

class FlexiblePolicy implements CancellationPolicy {
    public Money calculateRefund(Booking booking, LocalDate cancellationDate) {
        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, booking.getCheckInDate());
        
        if (daysUntilCheckIn >= 1) {
            return booking.getTotalPrice(); // 100% refund
        }
        return Money.ZERO; // No refund if same-day cancellation
    }
}

class ModeratePolicy implements CancellationPolicy {
    public Money calculateRefund(Booking booking, LocalDate cancellationDate) {
        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, booking.getCheckInDate());
        
        if (daysUntilCheckIn >= 7) {
            return booking.getTotalPrice(); // 100%
        } else if (daysUntilCheckIn >= 3) {
            return booking.getTotalPrice().multiply(0.50); // 50%
        }
        return Money.ZERO; // No refund < 3 days
    }
}

class StrictPolicy implements CancellationPolicy {
    public Money calculateRefund(Booking booking, LocalDate cancellationDate) {
        long daysUntilCheckIn = ChronoUnit.DAYS.between(cancellationDate, booking.getCheckInDate());
        
        if (daysUntilCheckIn >= 30) {
            return booking.getTotalPrice().multiply(0.80); // 80%
        } else if (daysUntilCheckIn >= 14) {
            return booking.getTotalPrice().multiply(0.50); // 50%
        } else if (daysUntilCheckIn >= 7) {
            return booking.getTotalPrice().multiply(0.25); // 25%
        }
        return Money.ZERO; // No refund < 7 days
    }
}

// Factory to select policy based on booking type
class CancellationPolicyFactory {
    public CancellationPolicy getPolicy(Booking booking) {
        if (booking.getRoomType() == RoomType.PRESIDENTIAL) {
            return new StrictPolicy(); // High-value bookings
        } else if (booking.getCustomer().isLoyaltyMember()) {
            return new FlexiblePolicy(); // Reward loyalty
        } else {
            return new ModeratePolicy(); // Default
        }
    }
}
```

**Q7: How would you implement overbooking without upsetting customers?**
**A:**
```
1. Predictive Model:
   - Analyze historical no-show rates by:
     * Room type
     * Day of week
     * Season
     * Customer type (business vs leisure)
   - Conservative: max 10% overbooking
   
2. Risk Mitigation:
   - Track confirmed bookings vs physical capacity in real-time
   - Alert system when approaching capacity (95%)
   - Automatic upgrade selection algorithm:
     Priority: Loyalty members > Longest stay > Early bookers
   
3. Customer Communication:
   - Proactive: Notify 24h before check-in if overbooked
   - Offer alternatives:
     * Free upgrade to next tier
     * Booking at partner property + transportation
     * Compensation (discount, future credit)
   
4. Graceful Degradation:
   - Stop accepting bookings when risk threshold reached
   - Display "Limited availability" instead of "Sold out" to encourage calls
   
5. Audit & Optimization:
   - Track customer satisfaction scores for upgraded guests
   - Adjust overbooking percentage based on realized no-shows
   - A/B test different overbooking strategies
```

**Q8: Explain your date range query optimization. Why interval tree?**
**A:**
- **Problem**: Naive scan is O(n*m) where n=rooms, m=days
- **Interval Tree Benefits**:
  - **Query**: O(log n + k) where k = overlapping bookings
  - **Insert**: O(log n)
  - **Space**: O(n) for n bookings
- **Alternative**: Segment tree (range queries), but interval tree simpler for this use case

**Implementation**:
```java
// Interval representation
class Interval {
    LocalDate start;
    LocalDate end;
    
    boolean overlaps(Interval other) {
        return !this.end.isBefore(other.start) && !this.start.isAfter(other.end);
    }
}

// Interval Tree Node
class IntervalTreeNode {
    Interval interval;
    Booking booking;
    LocalDate maxEnd; // Max end date in subtree (for efficient pruning)
    IntervalTreeNode left, right;
}

// Query algorithm
List<Booking> query(Interval queryInterval) {
    List<Booking> result = new ArrayList<>();
    queryHelper(root, queryInterval, result);
    return result;
}

private void queryHelper(IntervalTreeNode node, Interval query, List<Booking> result) {
    if (node == null) return;
    
    // If left subtree exists and its maxEnd >= query.start, search it
    if (node.left != null && !node.left.maxEnd.isBefore(query.start)) {
        queryHelper(node.left, query, result);
    }
    
    // Check current node
    if (node.interval.overlaps(query)) {
        result.add(node.booking);
    }
    
    // If node.interval.start > query.end, no point searching right subtree
    if (node.interval.start.isAfter(query.end)) {
        return;
    }
    
    // Search right subtree
    queryHelper(node.right, query, result);
}
```

**Q9: How do you handle check-in for group bookings (multiple rooms)?**
**A:**
```java
class GroupBooking {
    UUID groupId;
    List<Booking> individualBookings;
    Customer groupCoordinator;
    
    // Atomic check-in for all rooms
    public void checkInGroup() {
        List<Room> assignedRooms = new ArrayList<>();
        
        try {
            // Phase 1: Assign all rooms atomically
            for (Booking booking : individualBookings) {
                Room room = roomAssignmentService.assignRoom(booking);
                assignedRooms.add(room);
                booking.setAssignedRoom(room);
            }
            
            // Phase 2: Update all bookings
            for (Booking booking : individualBookings) {
                booking.checkIn();
                bookingRepository.save(booking);
            }
            
            // Phase 3: Emit event
            eventPublisher.publish(new GroupCheckedInEvent(this));
            
        } catch (Exception e) {
            // Rollback: release assigned rooms
            for (Room room : assignedRooms) {
                roomAssignmentService.releaseRoom(room);
            }
            throw new GroupCheckInFailedException("Failed to check in group", e);
        }
    }
}

// Room assignment strategy for groups
class GroupRoomAssignmentStrategy implements RoomAssignmentStrategy {
    public List<Room> assign(GroupBooking groupBooking) {
        // Constraint: Assign rooms on same floor if possible
        Map<Integer, List<Room>> roomsByFloor = availableRooms.stream()
            .collect(Collectors.groupingBy(Room::getFloor));
        
        // Find floor with enough rooms
        for (Map.Entry<Integer, List<Room>> entry : roomsByFloor.entrySet()) {
            if (entry.getValue().size() >= groupBooking.size()) {
                return entry.getValue().subList(0, groupBooking.size());
            }
        }
        
        // Fallback: adjacent floors
        // ... (implementation)
    }
}
```

**Q10: What events should you publish and why?**
**A:**
```java
// Event types for Hotel Booking

1. BookingCreatedEvent
   - Triggers: Confirmation email, payment processing, CRM update
   - Data: bookingId, customer, room, dates, price
   
2. BookingModifiedEvent
   - Triggers: Update confirmation, recalculate charges, inventory adjustment
   - Data: bookingId, oldSnapshot, newSnapshot, priceDifference
   
3. BookingCancelledEvent
   - Triggers: Refund processing, inventory release, notification
   - Data: bookingId, cancellationReason, refundAmount, policy
   
4. BookingCheckedInEvent
   - Triggers: Room key activation, welcome message, loyalty points
   - Data: bookingId, assignedRoom, checkInTime
   
5. BookingCheckedOutEvent
   - Triggers: Final billing, room cleaning, survey request
   - Data: bookingId, checkOutTime, totalCharges, loyaltyPointsEarned
   
6. RoomUpgradedEvent
   - Triggers: Customer notification, update booking details
   - Data: bookingId, fromRoomType, toRoomType, reason
   
7. OverbookingDetectedEvent
   - Triggers: Alert operations team, trigger auto-upgrade
   - Data: date, roomType, overBookedCount
   
8. PricingChangedEvent
   - Triggers: Update pricing cache, notify yield management
   - Data: roomType, oldBaseRate, newBaseRate, effectiveDate

// Event-driven architecture benefits:
- Decouples booking core from notifications, billing, analytics
- Enables async processing (emails, external APIs)
- Facilitates audit trail and event sourcing
- Supports real-time dashboard updates
```

#### 10. Extensions & Variations

1. **Multi-property chain**: Federated inventory, cross-property transfers
2. **Loyalty program**: Points accrual, tier-based benefits, redemptions
3. **Dynamic room blocking**: Reserve blocks for corporate clients
4. **Predictive waitlist**: Queue guests when soldout, auto-book on cancellations
5. **Revenue management**: ML-based price optimization
6. **Channel management**: Sync with OTAs (Booking.com, Expedia)
7. **Mobile check-in**: QR code room access, remote check-in
8. **Package deals**: Flight + hotel bundles
9. **Event-based pricing**: Concerts, conventions surge pricing

#### 11. Testing Strategy

**Unit Tests:**
- State transitions: Valid and invalid transitions
- Pricing rules: Each rule independently, edge cases
- Cancellation policies: Refund calculations
- Interval tree: Insert, query, overlaps

**Integration Tests:**
- End-to-end booking flow: Create → Modify → Check-in → Check-out
- Concurrent bookings: Race condition prevention
- Overbooking resolution: Auto-upgrade logic
- Event publishing: Verify all events emitted

**Property-Based Tests:**
```java
@Property
public void neverDoubleBook(@ForAll List<BookingRequest> requests) {
    for (BookingRequest req : requests) {
        try {
            bookingService.createBooking(req);
        } catch (NoAvailabilityException e) {
            // Expected
        }
    }
    
    // Verify: No overlapping bookings for same room
    List<Booking> allBookings = bookingRepository.findAll();
    Map<String, List<Booking>> bookingsByRoom = allBookings.stream()
        .collect(Collectors.groupingBy(b -> b.getRoom().getId()));
    
    for (List<Booking> roomBookings : bookingsByRoom.values()) {
        for (int i = 0; i < roomBookings.size(); i++) {
            for (int j = i + 1; j < roomBookings.size(); j++) {
                assertFalse(roomBookings.get(i).overlaps(roomBookings.get(j)));
            }
        }
    }
}
```

**Load Tests:**
- Simulate peak booking load (1000 req/s)
- Measure: P50, P95, P99 latencies
- Monitor: Database connection pool, CPU, memory
- Test: Circuit breaker activation under stress

#### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Anemic domain model (all logic in service layer)
✅ **Do**: Rich domain objects with behavior (Booking.confirm(), Booking.checkIn())

❌ **Avoid**: Mutable booking state without versioning
✅ **Do**: Optimistic locking with @Version field

❌ **Avoid**: Direct database queries in controllers
✅ **Do**: Repository pattern + service layer abstraction

❌ **Avoid**: Hardcoded pricing logic
✅ **Do**: Strategy pattern + configurable rules

❌ **Avoid**: Synchronous external API calls in booking flow
✅ **Do**: Async event-driven notifications

❌ **Avoid**: Global locks for entire inventory
✅ **Do**: Fine-grained locking per room or date range

#### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Search Availability | O(log n + k) | O(1) | n=bookings, k=overlaps (interval tree) |
| Create Booking | O(log n) | O(1) | Insert into interval tree |
| Modify Booking | O(log n) | O(1) | Delete + insert |
| Cancel Booking | O(log n) | O(1) | Remove from interval tree |
| Check-in | O(1) | O(1) | State transition + DB update |
| Price Calculation | O(r) | O(1) | r = number of pricing rules |
| Overbooking Resolution | O(b log b) | O(b) | b = bookings on date (sorting) |

**Space Complexity:**
- O(R) for R rooms
- O(B) for B bookings
- O(B) for interval tree
- O(R * D) for daily occupancy calendar (R rooms, D days cached)

#### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **State Management** | 25% | Correct use of State pattern, valid transitions, error handling |
| **Concurrency** | 20% | Double booking prevention, optimistic locking, race condition awareness |
| **Design Patterns** | 20% | Appropriate pattern selection (State, Strategy, Repository), justification |
| **Data Structures** | 15% | Efficient date range queries (interval tree), O(log n) operations |
| **Extensibility** | 10% | Easy to add room types, pricing rules, cancellation policies |
| **Real-world Awareness** | 10% | Overbooking handling, peak load strategies, business logic understanding |

**Red Flags:**
- No double booking prevention strategy
- Giant if-else for state transitions
- Linear search for availability
- No pricing flexibility (hardcoded rates)
- Ignoring concurrency concerns

---

## Problem 5: Prototype Pattern - Document Management System

### Main Algorithm Pseudocode

```
interface Cloneable:
    Document clone()

class Document implements Cloneable:
    private String title
    private String content
    private List<String> attachments
    private Metadata metadata
    
    public Document(title, content):
        this.title = title
        this.content = content
        this.attachments = new ArrayList()
        this.metadata = new Metadata()
    
    // Deep clone
    public Document clone():
        cloned = new Document(this.title, this.content)
        
        // Deep copy attachments
        for attachment in this.attachments:
            cloned.attachments.add(new String(attachment))
        
        // Deep copy metadata
        cloned.metadata = this.metadata.clone()
        
        return cloned
    
    // Shallow clone (if needed)
    public Document shallowClone():
        cloned = new Document(this.title, this.content)
        cloned.attachments = this.attachments // same reference
        cloned.metadata = this.metadata // same reference
        return cloned

class DocumentRegistry:
    private Map<String, Document> prototypes = new HashMap()
    
    public void registerPrototype(String key, Document doc):
        prototypes.put(key, doc)
    
    public Document getPrototype(String key):
        prototype = prototypes.get(key)
        if prototype != null:
            return prototype.clone()
        return null
    
    public void unregisterPrototype(String key):
        prototypes.remove(key)

// Usage
function main():
    registry = new DocumentRegistry()
    
    // Register templates
    template = new Document("Template", "Default content")
    registry.registerPrototype("invoice", template)
    
    // Create new documents from prototype
    doc1 = registry.getPrototype("invoice")
    doc2 = registry.getPrototype("invoice")
    
    // Modify independently
    doc1.setTitle("Invoice #1001")
    doc2.setTitle("Invoice #1002")
```

### Frequently Asked Interview Questions

**Q1: What's the difference between deep copy and shallow copy?**
**A:**
- **Shallow Copy**: Copies object's primitive fields, but references to objects are shared. Changes to nested objects affect both copies.
- **Deep Copy**: Recursively copies all objects. New object is completely independent. Changes don't affect original.

**Q2: When should you use Prototype over Factory?**
**A:** Use Prototype when:
- Object creation is expensive (database/network operations)
- Need to avoid subclasses of creator
- Classes to instantiate are specified at runtime
- Need to hide complexity of creating new instances
- Want to reduce number of classes

**Q3: How do you handle circular references in cloning?**
**A:**
- Maintain a map of already cloned objects
- Check map before cloning each object
- If object already cloned, return reference from map
- Otherwise, clone and add to map
- This prevents infinite loops and maintains object graph structure

**Q4: What's the difference between Prototype and Singleton?**
**A:**
- **Singleton**: One instance per application. Prevents multiple instances.
- **Prototype**: Creates new instances by cloning. Allows multiple instances with same initial state.

**Q5: How would you implement versioning in Document system?**
**A:**
- Store version number in Document
- Maintain version history (list of cloned snapshots)
- Clone document before each modification
- Add timestamp and author to metadata
- Implement diff mechanism to show changes between versions

---

## Problem 5: Abstract Factory - UI Components Library

### Main Algorithm Pseudocode

```
// Abstract Products
interface Button:
    void render()
    void onClick()

interface Checkbox:
    void render()
    void toggle()

interface TextField:
    void render()
    String getValue()

// Concrete Products - Windows
class WindowsButton implements Button:
    render(): print("Rendering Windows button")
    onClick(): print("Windows button clicked")

class WindowsCheckbox implements Checkbox:
    render(): print("Rendering Windows checkbox")
    toggle(): print("Windows checkbox toggled")

class WindowsTextField implements TextField:
    render(): print("Rendering Windows textfield")
    getValue(): return windowsValue

// Concrete Products - Mac
class MacButton implements Button:
    render(): print("Rendering Mac button")
    onClick(): print("Mac button clicked")

class MacCheckbox implements Checkbox:
    render(): print("Rendering Mac checkbox")
    toggle(): print("Mac checkbox toggled")

class MacTextField implements TextField:
    render(): print("Rendering Mac textfield")
    getValue(): return macValue

// Abstract Factory
interface UIFactory:
    Button createButton()
    Checkbox createCheckbox()
    TextField createTextField()

// Concrete Factories
class WindowsFactory implements UIFactory:
    createButton(): return new WindowsButton()
    createCheckbox(): return new WindowsCheckbox()
    createTextField(): return new WindowsTextField()

class MacFactory implements UIFactory:
    createButton(): return new MacButton()
    createCheckbox(): return new MacCheckbox()
    createTextField(): return new MacTextField()

// Client
class Application:
    private UIFactory factory
    private Button button
    private Checkbox checkbox
    
    constructor(factory):
        this.factory = factory
    
    void createUI():
        button = factory.createButton()
        checkbox = factory.createCheckbox()
        button.render()
        checkbox.render()

// Usage
function main():
    os = detectOperatingSystem()
    factory = null
    
    if os == "Windows":
        factory = new WindowsFactory()
    else if os == "Mac":
        factory = new MacFactory()
    
    app = new Application(factory)
    app.createUI()
```

### Frequently Asked Interview Questions

**Q1: How does Abstract Factory differ from Factory Method?**
**A:**
- **Factory Method**: One factory method creates one product type. Uses inheritance.
- **Abstract Factory**: Multiple factory methods create family of related products. Uses composition.

**Q2: How do you add a new product family (e.g., Linux)?**
**A:**
1. Create concrete product classes (LinuxButton, LinuxCheckbox, etc.)
2. Create concrete factory (LinuxFactory)
3. No changes to existing code (Open-Closed Principle)
4. Client code uses same interface

**Q3: What are the advantages of Abstract Factory?**
**A:**
- Ensures product compatibility (all from same family)
- Isolates concrete classes from client
- Easy to swap product families
- Promotes consistency among products
- Follows Single Responsibility and Open-Closed principles

**Q4: What's the main disadvantage of Abstract Factory?**
**A:** Adding new product types requires changing the abstract factory interface and all concrete factories. This violates Open-Closed Principle at the factory level. Mitigation: use flexible factory with product registration.

**Q5: How would you implement theme support (Dark/Light)?**
**A:** 
- Create separate factory for each theme
- Use Strategy pattern to switch factories at runtime
- Store current factory in application context
- Reload UI components when theme changes
- Use Observer pattern to notify components of theme change

---

## Problem 6: Object Pool Pattern - Database Connection Pool

### Main Algorithm Pseudocode

```
class Connection:
    private String id
    private boolean inUse
    private long lastUsed
    
    constructor(id):
        this.id = id
        this.inUse = false
        this.lastUsed = System.currentTime()
    
    void reset():
        // Reset connection state
        this.lastUsed = System.currentTime()

class ConnectionPool:
    private List<Connection> available
    private List<Connection> inUse
    private int maxPoolSize = 10
    private int minPoolSize = 2
    private long maxIdleTime = 30000 // 30 seconds
    private Object lock = new Object()
    
    constructor():
        available = new ArrayList()
        inUse = new ArrayList()
        initializePool()
        startMaintenanceThread()
    
    private void initializePool():
        for i from 0 to minPoolSize:
            available.add(createNewConnection())
    
    public Connection acquire():
        synchronized(lock):
            // Try to get from available pool
            if not available.isEmpty():
                connection = available.remove(0)
                connection.inUse = true
                inUse.add(connection)
                return connection
            
            // Create new if under max limit
            if (available.size() + inUse.size()) < maxPoolSize:
                connection = createNewConnection()
                connection.inUse = true
                inUse.add(connection)
                return connection
            
            // Wait for available connection
            while available.isEmpty():
                lock.wait(1000)
                if timeout:
                    throw PoolExhaustedException()
            
            return acquire() // recursive call
    
    public void release(Connection connection):
        synchronized(lock):
            if inUse.remove(connection):
                connection.inUse = false
                connection.reset()
                available.add(connection)
                lock.notify()
    
    private void startMaintenanceThread():
        thread = new Thread():
            while true:
                sleep(10000) // 10 seconds
                cleanupIdleConnections()
                ensureMinimumPool()
    
    private void cleanupIdleConnections():
        synchronized(lock):
            currentTime = System.currentTime()
            iterator = available.iterator()
            while iterator.hasNext():
                connection = iterator.next()
                if (currentTime - connection.lastUsed) > maxIdleTime:
                    if available.size() > minPoolSize:
                        iterator.remove()
                        connection.close()
    
    private void ensureMinimumPool():
        synchronized(lock):
            while available.size() < minPoolSize:
                available.add(createNewConnection())
```

### Frequently Asked Interview Questions

**Q1: Why use Object Pool instead of creating objects on demand?**
**A:**
- **Performance**: Avoids expensive object creation/destruction
- **Resource management**: Limits total resources (connections, threads)
- **Predictability**: Known resource consumption
- **Reduced GC pressure**: Reuses objects instead of creating new ones

**Q2: How do you handle pool exhaustion?**
**A:** Options:
1. **Wait with timeout**: Block until connection available or timeout
2. **Throw exception**: Let caller handle immediately
3. **Grow pool**: Create temporary connections beyond max
4. **Queue requests**: FIFO queue for waiting requests

**Q3: What's the difference between maxPoolSize and minPoolSize?**
**A:**
- **minPoolSize**: Always maintained. Pre-created at startup. Ensures quick availability.
- **maxPoolSize**: Upper limit. Prevents resource exhaustion. Pool can grow between min and max based on demand.

**Q4: How would you detect and handle connection leaks?**
**A:**
- Track acquisition time for each connection
- Log warning if connection held too long
- Force release after timeout
- Add stack trace capture at acquisition
- Implement connection wrapper with try-with-resources
- Monitor pool metrics (usage, wait time)

**Q5: How does connection validation work?**
**A:**
- **On acquisition**: Test connection before giving to client
- **Periodic validation**: Background thread validates idle connections
- **On release**: Optionally test before returning to pool
- **Validation query**: Simple query like "SELECT 1"
- **Failed validation**: Close bad connection, create new one

---

## Problem 7: Multiton Pattern - Logger System

### Main Algorithm Pseudocode

```
class Logger:
    private String name
    private LogLevel level
    private List<Handler> handlers
    
    private Logger(name):
        this.name = name
        this.level = LogLevel.INFO
        this.handlers = new ArrayList()
    
    public void log(level, message):
        if level >= this.level:
            logEntry = formatLogEntry(level, message)
            for handler in handlers:
                handler.write(logEntry)
    
    public void info(message): log(LogLevel.INFO, message)
    public void warn(message): log(LogLevel.WARN, message)
    public void error(message): log(LogLevel.ERROR, message)

class LoggerFactory:
    private static Map<String, Logger> instances = new ConcurrentHashMap()
    private static Object lock = new Object()
    
    private LoggerFactory(): // private constructor
    
    public static Logger getLogger(String name):
        if not instances.containsKey(name):
            synchronized(lock):
                if not instances.containsKey(name):
                    logger = new Logger(name)
                    instances.put(name, logger)
        return instances.get(name)
    
    public static Logger getLogger(Class<?> clazz):
        return getLogger(clazz.getName())
    
    public static void removeLogger(String name):
        synchronized(lock):
            logger = instances.get(name)
            if logger != null:
                logger.close()
                instances.remove(name)
    
    public static Collection<Logger> getAllLoggers():
        return instances.values()

// Handler interface
interface Handler:
    void write(String message)

class ConsoleHandler implements Handler:
    void write(String message):
        System.out.println(message)

class FileHandler implements Handler:
    private String filename
    void write(String message):
        writeToFile(filename, message)

// Usage
function main():
    // Each class gets its own logger instance
    logger1 = LoggerFactory.getLogger("com.app.UserService")
    logger2 = LoggerFactory.getLogger("com.app.PaymentService")
    logger3 = LoggerFactory.getLogger("com.app.UserService") // same as logger1
    
    logger1.info("User logged in")
    logger2.info("Payment processed")
```

### Frequently Asked Interview Questions

**Q1: What's the difference between Singleton and Multiton?**
**A:**
- **Singleton**: One instance per application. Single key (the class itself).
- **Multiton**: Multiple instances, one per unique key. Map of key-to-instance. Controlled instantiation.

**Q2: Why use Multiton for logging instead of Singleton?**
**A:**
- Different log levels per component
- Different output destinations per logger
- Easier to filter and search logs
- Component-specific configuration
- Better debugging and troubleshooting

**Q3: How do you prevent memory leaks with Multiton?**
**A:**
- Implement logger removal/cleanup method
- Use WeakHashMap for automatic garbage collection
- Set maximum number of logger instances
- Implement LRU eviction policy
- Periodically clean up unused loggers

**Q4: How would you implement hierarchical logging (e.g., com.app.service inherits from com.app)?**
**A:**
- Parse logger name to determine hierarchy
- Store loggers in tree structure
- Propagate log entries up the hierarchy
- Child inherits parent's level and handlers by default
- Allow override at each level

**Q5: How do you handle concurrent logging from multiple threads?**
**A:**
- Use ConcurrentHashMap for instances map
- Synchronize handler writes
- Use thread-safe buffers
- Consider async logging with queue
- Use ThreadLocal for per-thread context

---

## Problem 8: Lazy Initialization - Configuration Manager

### Main Algorithm Pseudocode

```
class Configuration:
    private Map<String, String> properties
    private long lastModified
    private String configFile
    
    constructor(configFile):
        this.configFile = configFile
        this.properties = null
        this.lastModified = 0
    
    private void loadConfiguration():
        if properties == null:
            properties = new HashMap()
            // Load from file
            content = readFile(configFile)
            parseProperties(content, properties)
            lastModified = getFileModifiedTime(configFile)

class ConfigurationManager:
    private static volatile ConfigurationManager instance
    private volatile Configuration config
    private String configPath
    private Object lock = new Object()
    
    private ConfigurationManager(configPath):
        this.configPath = configPath
        // Don't load config yet (lazy)
    
    public static ConfigurationManager getInstance(configPath):
        if instance == null:
            synchronized(ConfigurationManager.class):
                if instance == null:
                    instance = new ConfigurationManager(configPath)
        return instance
    
    // Lazy loading with double-check
    private Configuration getConfig():
        if config == null:
            synchronized(lock):
                if config == null:
                    config = new Configuration(configPath)
                    config.loadConfiguration()
        return config
    
    public String getProperty(String key):
        return getConfig().getProperty(key)
    
    public String getProperty(String key, String defaultValue):
        config = getConfig()
        value = config.getProperty(key)
        return value != null ? value : defaultValue
    
    public void reloadConfiguration():
        synchronized(lock):
            config = new Configuration(configPath)
            config.loadConfiguration()
    
    public boolean isModified():
        if config == null:
            return false
        currentModified = getFileModifiedTime(configPath)
        return currentModified > config.lastModified
    
    public void reloadIfModified():
        if isModified():
            reloadConfiguration()

// Auto-refresh mechanism
class ConfigurationRefresher:
    private ConfigurationManager manager
    private long checkInterval = 10000 // 10 seconds
    
    constructor(manager):
        this.manager = manager
        startRefreshThread()
    
    private void startRefreshThread():
        thread = new Thread():
            while true:
                sleep(checkInterval)
                manager.reloadIfModified()
```

### Frequently Asked Interview Questions

**Q1: What are the benefits of lazy initialization?**
**A:**
- **Reduced startup time**: Don't load until needed
- **Memory efficiency**: Only allocate memory when required
- **Avoid unnecessary work**: Skip loading if never used
- **Conditional loading**: Load based on runtime conditions

**Q2: What are the thread safety concerns with lazy initialization?**
**A:**
- **Race condition**: Multiple threads may create multiple instances
- **Solution 1**: Synchronized method (performance overhead)
- **Solution 2**: Double-checked locking with volatile
- **Solution 3**: Initialization-on-demand holder idiom
- **Solution 4**: Use lazy initialization from concurrency library

**Q3: How does double-checked locking work and why is volatile needed?**
**A:**
- First check: Fast path without synchronization
- Synchronized block: Ensures only one thread enters
- Second check: Ensures instance still null after acquiring lock
- **Volatile**: Prevents instruction reordering, ensures visibility across threads. Without it, partially constructed object may be visible.

**Q4: What's the difference between lazy and eager initialization?**
**A:**
- **Lazy**: Create on first use. Saves resources. Requires thread safety handling. Slower first access.
- **Eager**: Create at class loading. Thread-safe by JVM. Wastes resources if unused. Fast access always.

**Q5: How would you implement hot-reloading of configuration?**
**A:**
- Watch file system for changes (WatchService)
- Periodic polling with modification time check
- When change detected, reload configuration
- Notify listeners of configuration change
- Use copy-on-write for thread-safe updates
- Validate new configuration before replacing old

---

## Problem 9: Adapter Pattern - Legacy System Integration

### Main Algorithm Pseudocode

```
// Target Interface (what client expects)
interface PaymentProcessor:
    PaymentResult processPayment(amount, currency, cardDetails)
    PaymentResult refund(transactionId, amount)
    TransactionStatus checkStatus(transactionId)

// Adaptee (legacy system with different interface)
class LegacyPaymentGateway:
    String makePayment(double dollars, String cardNumber):
        // Legacy logic
        return transactionCode
    
    boolean reverseTransaction(String code):
        // Legacy refund logic
        return success
    
    int getTransactionState(String code):
        // Returns: 0=pending, 1=success, 2=failed
        return state

// Adapter
class PaymentAdapter implements PaymentProcessor:
    private LegacyPaymentGateway legacyGateway
    
    constructor():
        legacyGateway = new LegacyPaymentGateway()
    
    PaymentResult processPayment(amount, currency, cardDetails):
        // Convert amount to dollars if needed
        dollars = convertToDollars(amount, currency)
        
        // Extract card number from cardDetails object
        cardNumber = cardDetails.getCardNumber()
        
        // Call legacy system
        transactionCode = legacyGateway.makePayment(dollars, cardNumber)
        
        // Convert response to new format
        return new PaymentResult(
            transactionId: transactionCode,
            status: "SUCCESS",
            timestamp: getCurrentTime()
        )
    
    PaymentResult refund(transactionId, amount):
        success = legacyGateway.reverseTransaction(transactionId)
        
        return new PaymentResult(
            transactionId: transactionId,
            status: success ? "REFUNDED" : "FAILED",
            timestamp: getCurrentTime()
        )
    
    TransactionStatus checkStatus(transactionId):
        state = legacyGateway.getTransactionState(transactionId)
        
        // Map legacy states to new enum
        switch state:
            case 0: return TransactionStatus.PENDING
            case 1: return TransactionStatus.SUCCESS
            case 2: return TransactionStatus.FAILED
            default: return TransactionStatus.UNKNOWN

// Two-way adapter (if needed to support both interfaces)
class TwoWayPaymentAdapter implements PaymentProcessor, LegacyPaymentInterface:
    private PaymentProcessor modernSystem
    private LegacyPaymentGateway legacySystem
    
    // Implement both interfaces with bidirectional conversion
```

### Frequently Asked Interview Questions

**Q1: When should you use Adapter pattern?**
**A:** Use Adapter when:
- Integrating with legacy systems
- Using third-party libraries with incompatible interfaces
- Need to support multiple similar systems
- Want to decouple client from external dependencies
- Migrating gradually from old to new system

**Q2: What's the difference between Adapter and Facade?**
**A:**
- **Adapter**: Makes one interface compatible with another. Wraps single class. Focuses on interface conversion.
- **Facade**: Simplifies complex subsystem. May wrap multiple classes. Focuses on simplification.

**Q3: What's the difference between Class Adapter and Object Adapter?**
**A:**
- **Class Adapter**: Uses inheritance (extends Adaptee, implements Target). Works with single class. Not possible if Adaptee is final.
- **Object Adapter**: Uses composition (contains Adaptee, implements Target). More flexible. Can adapt multiple adaptees.

**Q4: How would you handle error mapping between systems?**
**A:**
- Create error code mapping table
- Catch legacy exceptions and wrap in modern exceptions
- Log original error details for debugging
- Maintain error context through translation
- Document error mappings

**Q5: How do you test an Adapter?**
**A:**
- Mock the legacy system
- Test each method mapping independently
- Verify data conversion correctness
- Test error handling and edge cases
- Integration tests with real legacy system
- Verify backward compatibility

---

## Problem 10: Decorator Pattern - Text Editor Features

### Main Algorithm Pseudocode

```
// Component Interface
interface Text:
    String getContent()
    int getLength()
    void render()

// Concrete Component
class SimpleText implements Text:
    private String content
    
    constructor(content):
        this.content = content
    
    String getContent():
        return content
    
    int getLength():
        return content.length()
    
    void render():
        print(content)

// Base Decorator
abstract class TextDecorator implements Text:
    protected Text wrappedText
    
    constructor(text):
        this.wrappedText = text
    
    String getContent():
        return wrappedText.getContent()
    
    int getLength():
        return wrappedText.getLength()
    
    void render():
        wrappedText.render()

// Concrete Decorators
class BoldDecorator extends TextDecorator:
    constructor(text):
        super(text)
    
    String getContent():
        return "<b>" + wrappedText.getContent() + "</b>"
    
    void render():
        print("<b>")
        wrappedText.render()
        print("</b>")

class ItalicDecorator extends TextDecorator:
    constructor(text):
        super(text)
    
    String getContent():
        return "<i>" + wrappedText.getContent() + "</i>"
    
    void render():
        print("<i>")
        wrappedText.render()
        print("</i>")

class UnderlineDecorator extends TextDecorator:
    constructor(text):
        super(text)
    
    String getContent():
        return "<u>" + wrappedText.getContent() + "</u>"
    
    void render():
        print("<u>")
        wrappedText.render()
        print("</u>")

class ColorDecorator extends TextDecorator:
    private String color
    
    constructor(text, color):
        super(text)
        this.color = color
    
    String getContent():
        return "<span color='" + color + "'>" + 
               wrappedText.getContent() + "</span>"

class FontSizeDecorator extends TextDecorator:
    private int size
    
    constructor(text, size):
        super(text)
        this.size = size
    
    String getContent():
        return "<span size='" + size + "'>" + 
               wrappedText.getContent() + "</span>"

// Usage
function main():
    // Start with simple text
    text = new SimpleText("Hello World")
    
    // Add decorations dynamically
    text = new BoldDecorator(text)
    text = new ItalicDecorator(text)
    text = new ColorDecorator(text, "red")
    
    // Result: <span color='red'><i><b>Hello World</b></i></span>
    text.render()
    
    // Can remove decorations by keeping references
    // or create new chain
```

### Frequently Asked Interview Questions

**Q1: What's the difference between Decorator and Inheritance?**
**A:**
- **Inheritance**: Static, compile-time. Creates class explosion with many combinations. Rigid.
- **Decorator**: Dynamic, runtime. Flexible combinations. Follows Open-Closed Principle. Uses composition.

**Q2: What's the difference between Decorator and Proxy?**
**A:**
- **Decorator**: Adds functionality/responsibility. Focus on enhancement. Client knows it's decorated.
- **Proxy**: Controls access. Focus on access control, lazy loading, etc. Client may not know about proxy.

**Q3: What are the disadvantages of Decorator pattern?**
**A:**
- Many small objects in memory
- Complex object initialization with multiple decorators
- Order of decoration may matter
- Harder to debug (deep call stacks)
- Identity problems (decorated object != original)

**Q4: How do you handle decorator ordering dependencies?**
**A:**
- Document required order
- Create composite decorators for common combinations
- Validate order in decorator constructor
- Use Builder pattern to ensure correct order
- Consider Template Method for fixed skeleton

**Q5: How would you implement undo for decorators?**
**A:**
- Keep decoration history stack
- Store decorator type and parameters
- Undo removes last decorator from chain
- Redo reapplies from redo stack
- Use Memento pattern to save decorator states
- Clone decorated object for undo/redo

---

## Summary

This batch covered 10 fundamental LLD problems focusing on:
- **Creational Patterns**: Singleton, Factory, Builder, Prototype, Abstract Factory
- **Structural Patterns**: Adapter, Decorator
- **Resource Management**: Object Pool, Connection Pool
- **Advanced Patterns**: Multiton, Lazy Initialization

### Key Takeaways:
1. **Thread Safety**: Critical in Singleton, Pool, and Multiton patterns
2. **Flexibility**: Decorator and Factory provide runtime flexibility
3. **Resource Management**: Pool pattern essential for expensive resources
4. **Interface Adaptation**: Adapter crucial for legacy integration
5. **Immutability**: Builder pattern promotes immutable objects

### Common Interview Themes:
- Design pattern selection and trade-offs
- Thread safety and concurrency
- Performance and memory optimization
- Extensibility and maintainability
- Real-world application scenarios

