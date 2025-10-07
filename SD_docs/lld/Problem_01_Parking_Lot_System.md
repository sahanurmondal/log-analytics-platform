# Problem 1: Parking Lot System (Strategy + Factory + State Pattern)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a parking lot system managing multiple floors, spot types (compact/large/handicapped), vehicle assignment, and fee calculation with hourly rates.

**Assumptions / Scope:**
- Multiple floors with different spot types per floor
- Vehicle types: Motorcycle, Car, Truck (each requires different spot sizes)
- Dynamic pricing based on vehicle type and duration
- Real-time spot availability tracking
- Entry/exit gate management with ticket generation
- Payment processing at exit
- Handle concurrent vehicle entry/exit
- Scale: 1000 spots, 100 concurrent operations
- Out of scope: Reservations, valet service, EV charging

**Non-Functional Goals:**
- Find available spot in < 100ms
- Thread-safe spot allocation (no double-booking)
- Accurate fee calculation (no rounding errors)
- Handle peak load (100 vehicles/min)
- Audit trail for all parking events

### 2. Core Requirements

**Functional:**
- Park vehicle (assign appropriate spot)
- Unpark vehicle (calculate fee, release spot)
- Query available spots by type
- Support multiple entry/exit gates
- Generate parking ticket with timestamp
- Calculate parking fee based on duration and vehicle type
- Track full/available status per floor
- Handle spot reservation (temporary hold)

**Non-Functional:**
- **Concurrency**: Multiple vehicles entering/exiting simultaneously
- **Consistency**: No double-booking of spots
- **Performance**: Spot search < 100ms, checkout < 200ms
- **Accuracy**: Precise time and money calculations
- **Scalability**: Support 10K spots across 100 floors

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Spot Allocation Algorithm**
- **Problem**: Find nearest available spot matching vehicle size quickly
- **Solution**: Floor-level indexing with spot type buckets
- **Algorithm**:
```java
// Hierarchical Search Strategy
findAvailableSpot(vehicleType) {
    requiredSpotTypes = getCompatibleSpotTypes(vehicleType);
    
    // Search from ground floor upward (preference)
    for (floor in floors) {
        for (spotType in requiredSpotTypes) {
            availableSpots = floor.getAvailableSpots(spotType);
            
            if (!availableSpots.isEmpty()) {
                // Return nearest to entrance
                spot = availableSpots.first();
                
                // Atomic reservation
                if (spot.tryReserve()) {
                    return spot;
                }
            }
        }
    }
    
    return null; // Parking lot full
}

// Vehicle to SpotType compatibility
getCompatibleSpotTypes(vehicleType) {
    switch (vehicleType) {
        case MOTORCYCLE:
            return [MOTORCYCLE, COMPACT, LARGE]; // Can fit in any
        case CAR:
            return [COMPACT, LARGE]; // Cannot use motorcycle spots
        case TRUCK:
            return [LARGE]; // Only large spots
    }
}
```

**Challenge 2: Concurrent Spot Allocation (Race Condition)**
- **Problem**: Two vehicles assigned same spot simultaneously
- **Solution**: Optimistic locking with CAS (Compare-And-Swap)
- **Algorithm**:
```java
class ParkingSpot {
    private volatile SpotStatus status;
    private AtomicReference<Vehicle> occupiedBy;
    
    // CRITICAL: Thread-safe reservation
    public boolean tryReserve(Vehicle vehicle) {
        // Atomic CAS operation
        if (status == AVAILABLE) {
            // Try to atomically set occupant
            boolean reserved = occupiedBy.compareAndSet(null, vehicle);
            
            if (reserved) {
                status = OCCUPIED;
                occupiedAt = Instant.now();
                return true;
            }
        }
        
        return false;
    }
    
    public void release() {
        occupiedBy.set(null);
        status = AVAILABLE;
        occupiedAt = null;
    }
}

// Alternative: Pessimistic locking with synchronized
public synchronized boolean reserve(Vehicle vehicle) {
    if (status == AVAILABLE) {
        this.occupiedBy = vehicle;
        this.status = OCCUPIED;
        this.occupiedAt = Instant.now();
        return true;
    }
    return false;
}
```

**Challenge 3: Fee Calculation (Hourly Rates)**
- **Problem**: Accurate time-based fee calculation with different rates
- **Solution**: Strategy pattern for pricing + Money pattern
- **Algorithm**:
```java
// Fee Calculation Strategy
interface PricingStrategy {
    Money calculateFee(Duration parkingDuration, VehicleType vehicleType);
}

class HourlyPricingStrategy implements PricingStrategy {
    private Map<VehicleType, Money> hourlyRates;
    
    public HourlyPricingStrategy() {
        hourlyRates = Map.of(
            MOTORCYCLE, Money.dollars(2),
            CAR, Money.dollars(5),
            TRUCK, Money.dollars(10)
        );
    }
    
    @Override
    public Money calculateFee(Duration parkingDuration, VehicleType vehicleType) {
        Money hourlyRate = hourlyRates.get(vehicleType);
        
        // Calculate total hours (round up)
        long totalMinutes = parkingDuration.toMinutes();
        long totalHours = (totalMinutes + 59) / 60; // Ceiling division
        
        // Minimum 1 hour charge
        if (totalHours < 1) {
            totalHours = 1;
        }
        
        return hourlyRate.multiply((int) totalHours);
    }
}

// Tiered pricing (first 2 hours flat, then hourly)
class TieredPricingStrategy implements PricingStrategy {
    @Override
    public Money calculateFee(Duration parkingDuration, VehicleType vehicleType) {
        long hours = parkingDuration.toHours();
        
        Money flatRate = Money.dollars(10); // First 2 hours
        Money hourlyRate = Money.dollars(5); // After 2 hours
        
        if (hours <= 2) {
            return flatRate;
        } else {
            long additionalHours = hours - 2;
            Money additionalFee = hourlyRate.multiply((int) additionalHours);
            return flatRate.add(additionalFee);
        }
    }
}
```

**Challenge 4: Floor Display Board Updates**
- **Problem**: Keep display boards in sync with real-time availability
- **Solution**: Observer pattern for reactive updates
- **Algorithm**:
```java
// Observer pattern for display boards
interface ParkingObserver {
    void onSpotOccupied(ParkingSpot spot);
    void onSpotReleased(ParkingSpot spot);
}

class DisplayBoard implements ParkingObserver {
    private Map<SpotType, Integer> availableCountByType;
    
    @Override
    public void onSpotOccupied(ParkingSpot spot) {
        SpotType type = spot.getType();
        availableCountByType.compute(type, (k, v) -> v - 1);
        updateDisplay();
    }
    
    @Override
    public void onSpotReleased(ParkingSpot spot) {
        SpotType type = spot.getType();
        availableCountByType.compute(type, (k, v) -> v + 1);
        updateDisplay();
    }
    
    private void updateDisplay() {
        // Refresh LED display with current counts
        for (Map.Entry<SpotType, Integer> entry : availableCountByType.entrySet()) {
            displayPanel.update(entry.getKey(), entry.getValue());
        }
    }
}

class ParkingFloor {
    private List<ParkingObserver> observers = new CopyOnWriteArrayList<>();
    
    public void addObserver(ParkingObserver observer) {
        observers.add(observer);
    }
    
    private void notifySpotOccupied(ParkingSpot spot) {
        observers.forEach(o -> o.onSpotOccupied(spot));
    }
    
    private void notifySpotReleased(ParkingSpot spot) {
        observers.forEach(o -> o.onSpotReleased(spot));
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Strategy** | Pricing algorithms (hourly, flat, tiered) | Swap pricing logic without changing core |
| **Factory** | Create spots, vehicles, tickets | Centralize object creation logic |
| **Singleton** | ParkingLot instance | Single parking lot per application |
| **Observer** | Display board updates | Decouple spot changes from UI updates |
| **State** | Spot status (Available/Occupied/Reserved) | Clear state transitions |
| **Command** | Park/Unpark operations | Encapsulate operations for audit trail |
| **Repository** | Persist tickets and transactions | Abstract data access |

### 5. Domain Model & Class Structure

```
┌─────────────────┐
│  ParkingLot     │ (Singleton Aggregate Root)
│  - floors       │
│  - gates        │
│  - pricingStrategy │
└────────┬────────┘
         │ contains
    ┌────┴────────────────┐
    ▼                     ▼
┌─────────────┐    ┌──────────────┐
│ParkingFloor │    │ EntryGate    │
│  - spots    │    │ ExitGate     │
│  - floorNum │    └──────────────┘
└──────┬──────┘
       │ contains
       ▼
┌──────────────┐       ┌─────────────┐
│ ParkingSpot  │──────>│  Vehicle    │
│  - spotNum   │ parks │  - license  │
│  - type      │       │  - type     │
│  - status    │       └─────────────┘
└──────────────┘
       │ generates
       ▼
┌──────────────┐       ┌─────────────┐
│ParkingTicket │──────>│   Money     │
│  - entryTime │ fee   │  (Value Obj)│
│  - exitTime  │       └─────────────┘
└──────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Park Vehicle**
```
Driver   Gate   ParkingLot   Floor   Spot   Ticket   Display
  │        │         │         │       │       │        │
  ├─enter──>│         │         │       │       │        │
  │        ├─findSpot────────>│         │       │        │
  │        │         ├─search──────>│         │        │
  │        │         │         ├─tryReserve──>│        │
  │        │         │         │     (CAS)    │        │
  │        │         │         │<─success─────┤        │
  │        │         │<─spot───┤       │       │        │
  │        │<─spot───┤         │       │       │        │
  │        ├─generate────────────────────────>│        │
  │        │         │         │       │       │        │
  │        ├─notify──────────────────────────────────>│
  │<─ticket┤         │         │       │       │        │
```

**Sequence: Unpark Vehicle (Calculate Fee)**
```
Driver   Gate   ParkingLot   Ticket   Pricing   Payment   Spot
  │        │         │          │        │         │        │
  ├─exit───>│         │          │        │         │        │
  │ (ticket)│         │          │        │         │        │
  │        ├─checkout─────────>│          │         │        │
  │        │         ├─getDuration────>│            │        │
  │        │         ├─calculateFee────>│            │        │
  │        │         │         │        │            │        │
  │        │         │<────────┴────────┤            │        │
  │        │<─fee────┤          │        │            │        │
  │        │         │          │        │            │        │
  │<─show fee────────┤          │        │            │        │
  ├─pay────>│         │          │        │            │        │
  │        ├─process─────────────────────────────────>│        │
  │        │         ├─releaseSpot─────────────────────────>│
  │        │<─success┤          │        │            │        │
  │<─receipt┤         │          │        │            │        │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// ============================================
// DOMAIN ENTITIES (Skeleton)
// ============================================

public enum VehicleType {
    MOTORCYCLE, CAR, TRUCK
}

public enum SpotType {
    MOTORCYCLE, COMPACT, LARGE, HANDICAPPED
}

public enum SpotStatus {
    AVAILABLE, OCCUPIED, RESERVED, OUT_OF_SERVICE
}

public class Vehicle {
    private String licensePlate;
    private VehicleType type;
    private String color;
    
    // Constructor and getters omitted
}

public class ParkingSpot {
    private String spotId;
    private SpotType type;
    private int floorNumber;
    private volatile SpotStatus status;
    private AtomicReference<Vehicle> occupiedBy;
    private Instant occupiedAt;
    
    // Getters omitted
}

public class ParkingTicket {
    private UUID ticketId;
    private Vehicle vehicle;
    private ParkingSpot assignedSpot;
    private Instant entryTime;
    private Instant exitTime;
    private Money fee;
    
    // Getters/setters omitted
}

// ============================================
// PARKING FLOOR
// ============================================

public class ParkingFloor {
    private int floorNumber;
    private Map<SpotType, List<ParkingSpot>> spotsByType;
    private List<ParkingObserver> observers;
    
    public ParkingFloor(int floorNumber, int compactCount, int largeCount, 
                        int motorcycleCount, int handicappedCount) {
        this.floorNumber = floorNumber;
        this.spotsByType = new ConcurrentHashMap<>();
        this.observers = new CopyOnWriteArrayList<>();
        
        // Initialize spots
        initializeSpots(SpotType.COMPACT, compactCount);
        initializeSpots(SpotType.LARGE, largeCount);
        initializeSpots(SpotType.MOTORCYCLE, motorcycleCount);
        initializeSpots(SpotType.HANDICAPPED, handicappedCount);
    }
    
    private void initializeSpots(SpotType type, int count) {
        List<ParkingSpot> spots = new CopyOnWriteArrayList<>();
        for (int i = 0; i < count; i++) {
            String spotId = String.format("F%d-%s-%03d", floorNumber, type, i + 1);
            spots.add(new ParkingSpot(spotId, type, floorNumber));
        }
        spotsByType.put(type, spots);
    }
    
    public Optional<ParkingSpot> findAvailableSpot(List<SpotType> compatibleTypes) {
        for (SpotType type : compatibleTypes) {
            List<ParkingSpot> spots = spotsByType.get(type);
            if (spots != null) {
                for (ParkingSpot spot : spots) {
                    if (spot.getStatus() == SpotStatus.AVAILABLE) {
                        return Optional.of(spot);
                    }
                }
            }
        }
        return Optional.empty();
    }
    
    public int getAvailableCount(SpotType type) {
        return (int) spotsByType.getOrDefault(type, Collections.emptyList())
            .stream()
            .filter(spot -> spot.getStatus() == SpotStatus.AVAILABLE)
            .count();
    }
    
    public void addObserver(ParkingObserver observer) {
        observers.add(observer);
    }
    
    void notifySpotOccupied(ParkingSpot spot) {
        observers.forEach(o -> o.onSpotOccupied(spot));
    }
    
    void notifySpotReleased(ParkingSpot spot) {
        observers.forEach(o -> o.onSpotReleased(spot));
    }
}

// ============================================
// PARKING SPOT (Thread-Safe)
// ============================================

public class ParkingSpot {
    private final String spotId;
    private final SpotType type;
    private final int floorNumber;
    private volatile SpotStatus status;
    private AtomicReference<Vehicle> occupiedBy;
    private Instant occupiedAt;
    
    public ParkingSpot(String spotId, SpotType type, int floorNumber) {
        this.spotId = spotId;
        this.type = type;
        this.floorNumber = floorNumber;
        this.status = SpotStatus.AVAILABLE;
        this.occupiedBy = new AtomicReference<>(null);
    }
    
    /**
     * INTERVIEW CRITICAL: Thread-safe spot reservation with CAS
     */
    public boolean tryReserve(Vehicle vehicle) {
        // Atomic compare-and-set
        if (status == SpotStatus.AVAILABLE) {
            boolean reserved = occupiedBy.compareAndSet(null, vehicle);
            
            if (reserved) {
                status = SpotStatus.OCCUPIED;
                occupiedAt = Instant.now();
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * INTERVIEW CRITICAL: Release spot (make available)
     */
    public void release() {
        occupiedBy.set(null);
        status = SpotStatus.AVAILABLE;
        occupiedAt = null;
    }
    
    public SpotStatus getStatus() {
        return status;
    }
    
    public SpotType getType() {
        return type;
    }
    
    public Optional<Vehicle> getOccupiedBy() {
        return Optional.ofNullable(occupiedBy.get());
    }
    
    public Instant getOccupiedAt() {
        return occupiedAt;
    }
}

// ============================================
// PRICING STRATEGY
// ============================================

public interface PricingStrategy {
    Money calculateFee(Duration parkingDuration, VehicleType vehicleType);
}

public class HourlyPricingStrategy implements PricingStrategy {
    private final Map<VehicleType, Money> hourlyRates;
    
    public HourlyPricingStrategy() {
        this.hourlyRates = Map.of(
            VehicleType.MOTORCYCLE, Money.dollars(2),
            VehicleType.CAR, Money.dollars(5),
            VehicleType.TRUCK, Money.dollars(10)
        );
    }
    
    /**
     * INTERVIEW CRITICAL: Fee calculation with ceiling hours
     */
    @Override
    public Money calculateFee(Duration parkingDuration, VehicleType vehicleType) {
        Money hourlyRate = hourlyRates.get(vehicleType);
        
        // Calculate total hours (round up to nearest hour)
        long totalMinutes = parkingDuration.toMinutes();
        long totalHours = (totalMinutes + 59) / 60; // Ceiling division
        
        // Minimum 1 hour charge
        if (totalHours < 1) {
            totalHours = 1;
        }
        
        return hourlyRate.multiply((int) totalHours);
    }
}

// ============================================
// PARKING LOT (Singleton)
// ============================================

public class ParkingLot {
    private static volatile ParkingLot instance;
    
    private final String name;
    private final List<ParkingFloor> floors;
    private final PricingStrategy pricingStrategy;
    private final Map<UUID, ParkingTicket> activeTickets;
    
    private ParkingLot(String name, int numFloors, PricingStrategy pricingStrategy) {
        this.name = name;
        this.pricingStrategy = pricingStrategy;
        this.floors = new ArrayList<>();
        this.activeTickets = new ConcurrentHashMap<>();
        
        // Initialize floors (example: 100 spots per floor)
        for (int i = 0; i < numFloors; i++) {
            floors.add(new ParkingFloor(
                i + 1,  // floor number
                40,     // compact spots
                30,     // large spots
                20,     // motorcycle spots
                10      // handicapped spots
            ));
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Thread-safe singleton with double-checked locking
     */
    public static ParkingLot getInstance(String name, int numFloors, 
                                         PricingStrategy pricingStrategy) {
        if (instance == null) {
            synchronized (ParkingLot.class) {
                if (instance == null) {
                    instance = new ParkingLot(name, numFloors, pricingStrategy);
                }
            }
        }
        return instance;
    }
    
    /**
     * INTERVIEW CRITICAL: Park vehicle with spot allocation
     */
    public ParkingTicket parkVehicle(Vehicle vehicle) {
        // Get compatible spot types for vehicle
        List<SpotType> compatibleTypes = getCompatibleSpotTypes(vehicle.getType());
        
        // Search for available spot (ground floor first)
        for (ParkingFloor floor : floors) {
            Optional<ParkingSpot> spotOpt = floor.findAvailableSpot(compatibleTypes);
            
            if (spotOpt.isPresent()) {
                ParkingSpot spot = spotOpt.get();
                
                // Try to atomically reserve the spot
                if (spot.tryReserve(vehicle)) {
                    // Generate ticket
                    ParkingTicket ticket = new ParkingTicket(
                        UUID.randomUUID(),
                        vehicle,
                        spot,
                        Instant.now()
                    );
                    
                    activeTickets.put(ticket.getTicketId(), ticket);
                    
                    // Notify observers (display boards)
                    floor.notifySpotOccupied(spot);
                    
                    return ticket;
                }
                // If reservation failed, continue to next spot
            }
        }
        
        throw new ParkingLotFullException("No available spots for vehicle type: " + vehicle.getType());
    }
    
    /**
     * INTERVIEW CRITICAL: Unpark vehicle with fee calculation
     */
    public Receipt unparkVehicle(UUID ticketId, Instant exitTime) {
        ParkingTicket ticket = activeTickets.remove(ticketId);
        
        if (ticket == null) {
            throw new InvalidTicketException("Ticket not found: " + ticketId);
        }
        
        // Calculate parking duration
        Duration parkingDuration = Duration.between(ticket.getEntryTime(), exitTime);
        
        // Calculate fee using pricing strategy
        Money fee = pricingStrategy.calculateFee(
            parkingDuration,
            ticket.getVehicle().getType()
        );
        
        // Update ticket
        ticket.setExitTime(exitTime);
        ticket.setFee(fee);
        
        // Release spot
        ParkingSpot spot = ticket.getAssignedSpot();
        spot.release();
        
        // Notify observers
        ParkingFloor floor = floors.get(spot.getFloorNumber() - 1);
        floor.notifySpotReleased(spot);
        
        // Generate receipt
        return new Receipt(
            ticket.getTicketId(),
            ticket.getVehicle().getLicensePlate(),
            spot.getSpotId(),
            ticket.getEntryTime(),
            exitTime,
            parkingDuration,
            fee
        );
    }
    
    /**
     * INTERVIEW CRITICAL: Get compatible spot types for vehicle
     */
    private List<SpotType> getCompatibleSpotTypes(VehicleType vehicleType) {
        switch (vehicleType) {
            case MOTORCYCLE:
                // Motorcycles can fit in any spot
                return Arrays.asList(
                    SpotType.MOTORCYCLE,
                    SpotType.COMPACT,
                    SpotType.LARGE
                );
            case CAR:
                // Cars need compact or large spots
                return Arrays.asList(
                    SpotType.COMPACT,
                    SpotType.LARGE,
                    SpotType.HANDICAPPED
                );
            case TRUCK:
                // Trucks only fit in large spots
                return Collections.singletonList(SpotType.LARGE);
            default:
                return Collections.emptyList();
        }
    }
    
    public Map<SpotType, Integer> getAvailability() {
        Map<SpotType, Integer> totalAvailability = new HashMap<>();
        
        for (ParkingFloor floor : floors) {
            for (SpotType type : SpotType.values()) {
                int count = floor.getAvailableCount(type);
                totalAvailability.merge(type, count, Integer::sum);
            }
        }
        
        return totalAvailability;
    }
}

// ============================================
// OBSERVER PATTERN (Display Board)
// ============================================

public interface ParkingObserver {
    void onSpotOccupied(ParkingSpot spot);
    void onSpotReleased(ParkingSpot spot);
}

public class DisplayBoard implements ParkingObserver {
    private final int floorNumber;
    private final Map<SpotType, AtomicInteger> availableCountByType;
    
    public DisplayBoard(int floorNumber, Map<SpotType, Integer> initialCounts) {
        this.floorNumber = floorNumber;
        this.availableCountByType = new ConcurrentHashMap<>();
        
        initialCounts.forEach((type, count) -> 
            availableCountByType.put(type, new AtomicInteger(count))
        );
    }
    
    @Override
    public void onSpotOccupied(ParkingSpot spot) {
        SpotType type = spot.getType();
        int newCount = availableCountByType.get(type).decrementAndGet();
        updateDisplay(type, newCount);
    }
    
    @Override
    public void onSpotReleased(ParkingSpot spot) {
        SpotType type = spot.getType();
        int newCount = availableCountByType.get(type).incrementAndGet();
        updateDisplay(type, newCount);
    }
    
    private void updateDisplay(SpotType type, int count) {
        System.out.printf("Floor %d - %s spots available: %d%n",
            floorNumber, type, count);
    }
}

// ============================================
// RECEIPT (DTO)
// ============================================

public class Receipt {
    private final UUID ticketId;
    private final String licensePlate;
    private final String spotId;
    private final Instant entryTime;
    private final Instant exitTime;
    private final Duration parkingDuration;
    private final Money fee;
    
    // Constructor and getters omitted
}

// ============================================
// MONEY VALUE OBJECT (From Problem 7)
// ============================================

public final class Money {
    private final long amountInCents;
    private final Currency currency;
    
    public static Money dollars(double amount) {
        return new Money(
            (long) (amount * 100),
            Currency.getInstance("USD")
        );
    }
    
    public Money multiply(int multiplier) {
        return new Money(amountInCents * multiplier, currency);
    }
    
    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amountInCents + other.amountInCents, currency);
    }
    
    @Override
    public String toString() {
        return String.format("%s %.2f", 
            currency.getSymbol(), 
            amountInCents / 100.0);
    }
    
    // Constructor, equals, hashCode omitted
}
```

### 8. Thread Safety & Concurrency

**Atomic Operations:**
- `AtomicReference<Vehicle>` for CAS spot reservation
- `ConcurrentHashMap` for active tickets
- `CopyOnWriteArrayList` for observers (rarely modified)

**Race Condition Prevention:**
- `tryReserve()` uses CAS to prevent double-booking
- Each spot has independent lock (no global lock bottleneck)

**Thread-Safe Collections:**
- `ConcurrentHashMap` for tickets (high concurrency)
- `AtomicInteger` for display board counters

### 9. Top Interview Questions & Answers

**Q1: How do you prevent two vehicles from being assigned the same spot?**
**A:**
```java
// Use AtomicReference with compareAndSet (CAS)
public boolean tryReserve(Vehicle vehicle) {
    if (status == SpotStatus.AVAILABLE) {
        // Atomic operation - only ONE thread succeeds
        boolean reserved = occupiedBy.compareAndSet(null, vehicle);
        
        if (reserved) {
            status = SpotStatus.OCCUPIED;
            return true;
        }
    }
    return false; // Another thread won the race
}

// Caller retries with next available spot
for (ParkingSpot spot : availableSpots) {
    if (spot.tryReserve(vehicle)) {
        return spot; // Success
    }
    // Failed, try next spot
}
```

**Q2: How do you handle different vehicle sizes?**
**A:**
```java
// Compatibility matrix (vehicle → spot types)
MOTORCYCLE → [MOTORCYCLE, COMPACT, LARGE]  // Can fit anywhere
CAR        → [COMPACT, LARGE, HANDICAPPED] // Needs larger spots
TRUCK      → [LARGE]                       // Only large spots

// Search in order of preference
List<SpotType> compatible = getCompatibleSpotTypes(vehicleType);
for (SpotType type : compatible) {
    spot = findSpotOfType(type);
    if (spot != null) return spot;
}
```

**Q3: How do you calculate fees accurately?**
**A:**
```java
// Use Money value object (integer cents) to avoid floating-point errors
Money hourlyRate = Money.dollars(5.00); // Stored as 500 cents

// Calculate duration in minutes, round up to hours
long minutes = Duration.between(entryTime, exitTime).toMinutes();
long hours = (minutes + 59) / 60; // Ceiling division

// Multiply rate by hours (precise integer arithmetic)
Money fee = hourlyRate.multiply((int) hours);

// Example: 135 minutes
// hours = (135 + 59) / 60 = 194 / 60 = 3 hours
// fee = $5.00 * 3 = $15.00 (exact, no rounding errors)
```

**Q4: What if parking lot is full?**
**A:**
```java
try {
    ticket = parkingLot.parkVehicle(vehicle);
} catch (ParkingLotFullException e) {
    // Option 1: Show "LOT FULL" sign
    displayFullSign();
    
    // Option 2: Waitlist (queue vehicles)
    waitlist.add(vehicle);
    
    // Option 3: Redirect to nearby parking
    suggestAlternativeParkingLots();
    
    // Option 4: Valet service (double-park temporarily)
    valetService.handleOverflow(vehicle);
}
```

**Q5: How do you test concurrent parking?**
**A:**
```java
@Test
public void testConcurrentParking() throws Exception {
    ParkingLot lot = createLotWithSpots(10);
    
    int numThreads = 20; // More threads than spots
    ExecutorService executor = Executors.newFixedThreadPool(numThreads);
    CountDownLatch latch = new CountDownLatch(numThreads);
    
    List<Future<ParkingTicket>> futures = new ArrayList<>();
    
    for (int i = 0; i < numThreads; i++) {
        final int threadId = i;
        futures.add(executor.submit(() -> {
            latch.countDown();
            latch.await(); // All start together
            
            Vehicle vehicle = new Vehicle("CAR-" + threadId, VehicleType.CAR);
            return parkingLot.parkVehicle(vehicle);
        }));
    }
    
    // Collect results
    List<ParkingTicket> tickets = new ArrayList<>();
    int failures = 0;
    
    for (Future<ParkingTicket> future : futures) {
        try {
            tickets.add(future.get());
        } catch (ExecutionException e) {
            if (e.getCause() instanceof ParkingLotFullException) {
                failures++;
            }
        }
    }
    
    // Verify: Exactly 10 succeed, 10 fail
    assertEquals(10, tickets.size());
    assertEquals(10, failures);
    
    // Verify: No duplicate spot assignments
    Set<String> assignedSpots = tickets.stream()
        .map(t -> t.getAssignedSpot().getSpotId())
        .collect(Collectors.toSet());
    
    assertEquals(10, assignedSpots.size()); // All unique
}
```

**Q6: How do display boards stay updated?**
**A:**
```java
// Observer pattern decouples spot updates from display logic
floor.addObserver(displayBoard);

// When spot occupied:
spot.tryReserve(vehicle); // Updates spot state
floor.notifySpotOccupied(spot); // Notifies all observers

// DisplayBoard receives notification:
@Override
public void onSpotOccupied(ParkingSpot spot) {
    availableCount.get(spot.getType()).decrementAndGet();
    updateLEDDisplay(); // Refresh physical display
}

// Benefits:
// 1. No tight coupling between parking logic and UI
// 2. Can add multiple observers (mobile app, website, LED boards)
// 3. Observers update asynchronously (non-blocking)
```

**Q7: What metrics would you track?**
**A:**
```java
Metrics:
1. Occupancy Rate: % spots occupied per floor
2. Average Parking Duration: Mean time vehicles stay
3. Revenue Per Day: Total fees collected
4. Peak Hours: Busiest entry/exit times
5. Vehicle Type Distribution: % motorcycles/cars/trucks
6. Spot Utilization: Which spot types are most used
7. Average Wait Time: Time to find available spot
8. Turnover Rate: How quickly spots become available

Dashboard:
- Real-time occupancy heatmap
- Revenue trends (hourly/daily/monthly)
- Predictive analytics (when will lot fill up)
- Alerts (>95% occupancy, equipment failures)
```

**Q8: How do you handle handicapped spots?**
**A:**
```java
// Prioritize handicapped spots for eligible vehicles
public ParkingTicket parkVehicle(Vehicle vehicle) {
    List<SpotType> compatibleTypes;
    
    if (vehicle.hasHandicappedPermit()) {
        // Check handicapped spots first
        compatibleTypes = Arrays.asList(
            SpotType.HANDICAPPED,
            SpotType.COMPACT,
            SpotType.LARGE
        );
    } else {
        // Exclude handicapped spots for regular vehicles
        compatibleTypes = getCompatibleSpotTypes(vehicle.getType())
            .stream()
            .filter(type -> type != SpotType.HANDICAPPED)
            .collect(toList());
    }
    
    // Find available spot with priority order
    for (SpotType type : compatibleTypes) {
        spot = findSpotOfType(type);
        if (spot != null) return spot;
    }
}

// Fine for misuse
if (spot.getType() == HANDICAPPED && !vehicle.hasHandicappedPermit()) {
    throw new IllegalParkingException("Handicapped permit required");
}
```

**Q9: What's the database schema?**
**A:**
```sql
CREATE TABLE parking_spots (
    spot_id VARCHAR(20) PRIMARY KEY,
    floor_number INT NOT NULL,
    spot_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    occupied_by_license VARCHAR(20),
    occupied_at TIMESTAMP,
    
    INDEX idx_floor_type_status (floor_number, spot_type, status)
);

CREATE TABLE parking_tickets (
    ticket_id UUID PRIMARY KEY,
    license_plate VARCHAR(20) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    spot_id VARCHAR(20) NOT NULL,
    entry_time TIMESTAMP NOT NULL,
    exit_time TIMESTAMP,
    fee_cents BIGINT,
    
    FOREIGN KEY (spot_id) REFERENCES parking_spots(spot_id),
    INDEX idx_active_tickets (exit_time) WHERE exit_time IS NULL
);

CREATE TABLE transactions (
    transaction_id UUID PRIMARY KEY,
    ticket_id UUID NOT NULL,
    amount_cents BIGINT NOT NULL,
    payment_method VARCHAR(20) NOT NULL,
    paid_at TIMESTAMP NOT NULL,
    
    FOREIGN KEY (ticket_id) REFERENCES parking_tickets(ticket_id)
);
```

**Q10: How would you scale to multiple parking lots?**
**A:**
```
Architecture:
1. Microservice per parking lot (independent deployment)
2. Central discovery service (find nearby lots)
3. Shared analytics database (cross-lot reporting)
4. Event bus for real-time availability updates

Scalability:
- Partition by geography (each lot is isolated)
- Read replicas for availability queries
- Cache spot counts in Redis (1-second TTL)
- Mobile app subscribes to WebSocket for real-time updates

Load Balancing:
- Route vehicles to nearest available lot
- Suggest alternatives when lot is 90% full
- Dynamic pricing (surge pricing during peak hours)
```

### 10. Extensions & Variations

1. **EV Charging**: Reserve charging spots with time limits
2. **Valet Service**: Allow double-parking with valet tracking
3. **Reservations**: Pre-book spots with guaranteed availability
4. **Dynamic Pricing**: Surge pricing during peak hours
5. **Monthly Passes**: Subscription-based unlimited parking
6. **Mobile Payment**: QR code scan for entry/exit

### 11. Testing Strategy

**Unit Tests:**
- Spot reservation with CAS
- Fee calculation for various durations
- Compatible spot type logic
- Observer notification

**Integration Tests:**
- Full park/unpark flow
- Concurrent parking (race conditions)
- Display board updates
- Payment processing

**Load Tests:**
- 100 concurrent park operations
- 1000 vehicles in 10 minutes
- Display board update latency

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Check status → Update spot (race condition)
✅ **Do**: Atomic CAS with tryReserve()

❌ **Avoid**: Global lock for entire parking lot
✅ **Do**: Per-spot locks (fine-grained)

❌ **Avoid**: Float/double for fees
✅ **Do**: Money value object with integer cents

❌ **Avoid**: Polling for display updates
✅ **Do**: Observer pattern (event-driven)

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Park Vehicle | O(F × S) | O(1) | F=floors, S=spot types (typically ~3) |
| Unpark Vehicle | O(1) | O(1) | Direct ticket lookup |
| Find Available Spot | O(F × S) | O(1) | Early exit when found |
| Calculate Fee | O(1) | O(1) | Simple arithmetic |
| Get Availability | O(F × S) | O(S) | Aggregate counts |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Concurrency** | 30% | CAS for spot reservation, thread-safe collections |
| **Design Patterns** | 25% | Strategy (pricing), Observer (display), Singleton (lot) |
| **Domain Modeling** | 20% | Clear entity relationships, spot types, vehicle compatibility |
| **Fee Calculation** | 15% | Money pattern, accurate time-based pricing |
| **Real-world Awareness** | 10% | Display boards, handicapped spots, peak load handling |

**Red Flags:**
- No concurrency control (double-booking)
- Floating-point for money
- Global lock (poor scalability)
- No strategy pattern for pricing
- Missing vehicle-to-spot compatibility logic

---
