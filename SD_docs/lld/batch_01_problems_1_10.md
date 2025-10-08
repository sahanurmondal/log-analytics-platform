
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

