# Problem 4: Hotel Booking Engine (Overbooking + Time Modeling + CQRS)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a hotel booking system that manages room inventory, reservations, pricing, cancellations, and controlled overbooking to maximize occupancy while minimizing customer impact.

**Assumptions / Scope:**
- Multiple hotels, each with multiple room types
- Dynamic pricing based on demand, seasonality, events
- Cancellation with refund policies
- Controlled overbooking (oversell by 10% based on cancellation rates)
- Check-in/check-out time modeling
- Guest preferences tracking
- Scale: 1000 hotels, 100K rooms, 1M reservations/year
- Out of scope: Payment processing, loyalty programs, housekeeping

**Non-Functional Goals:**
- No double-booking of same room (strong consistency)
- Sub-500ms booking response time
- Support 10K concurrent booking requests
- Real-time inventory updates
- Audit trail for all price changes

### 2. Core Requirements

**Functional:**
- Search available rooms by date range, location, room type
- Book rooms with guest details
- Modify/cancel reservations with refund calculation
- Check-in and check-out workflow
- Dynamic pricing engine
- Overbooking management with waitlist
- Room assignment (specific room or room type)
- Block rooms for maintenance
- Generate booking confirmation

**Non-Functional:**
- **Consistency**: No double-booking, accurate inventory
- **Performance**: Search < 300ms, booking < 500ms
- **Scalability**: Handle 10K concurrent bookings
- **Availability**: 99.95% uptime
- **Observability**: Track occupancy rate, overbooking frequency

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Prevent Double Booking with Date Overlaps**
- **Problem**: Multiple concurrent bookings for same room on overlapping dates
- **Solution**: Database constraints + optimistic locking on inventory
- **Algorithm**:
```java
/**
 * Atomic booking with date overlap prevention
 */
class BookingService {
    
    @Transactional
    public BookingResult bookRoom(BookingRequest request) {
        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();
        UUID roomTypeId = request.getRoomTypeId();
        UUID hotelId = request.getHotelId();
        
        // Validate dates
        if (!checkIn.isBefore(checkOut)) {
            return BookingResult.failure("Check-out must be after check-in");
        }
        
        if (checkIn.isBefore(LocalDate.now())) {
            return BookingResult.failure("Cannot book in the past");
        }
        
        // Find available room using FOR UPDATE lock
        Optional<Room> availableRoom = roomRepo.findAvailableRoom(
            hotelId, roomTypeId, checkIn, checkOut
        );
        
        if (availableRoom.isEmpty()) {
            // Check if overbooking allowed
            if (canOverbook(hotelId, roomTypeId, checkIn, checkOut)) {
                return createOverbookedReservation(request);
            }
            
            return BookingResult.failure("No rooms available");
        }
        
        Room room = availableRoom.get();
        
        // Calculate price
        BigDecimal totalPrice = pricingEngine.calculatePrice(
            room, checkIn, checkOut, request.getGuestCount()
        );
        
        // Create reservation
        Reservation reservation = Reservation.builder()
            .id(UUID.randomUUID())
            .hotelId(hotelId)
            .roomId(room.getId())
            .guestId(request.getGuestId())
            .checkInDate(checkIn)
            .checkOutDate(checkOut)
            .status(ReservationStatus.CONFIRMED)
            .totalPrice(totalPrice)
            .createdAt(Instant.now())
            .build();
        
        reservationRepo.save(reservation);
        
        // Update room inventory
        RoomInventory inventory = inventoryRepo.findByRoomAndDate(
            room.getId(), checkIn, checkOut
        );
        
        inventory.decrementAvailable();
        inventoryRepo.save(inventory);
        
        // Publish event
        eventPublisher.publish(new RoomBookedEvent(
            reservation.getId(), room.getId(), checkIn, checkOut
        ));
        
        return BookingResult.success(reservation);
    }
    
    /**
     * INTERVIEW CRITICAL: Check for date overlap conflicts
     */
    private boolean hasDateOverlap(LocalDate start1, LocalDate end1,
                                    LocalDate start2, LocalDate end2) {
        // Overlap exists if:
        // - start1 < end2 AND end1 > start2
        // Note: Check-out day is not included (guest leaves in morning)
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}

/**
 * Repository query with date overlap check
 */
@Query("""
    SELECT r FROM Room r
    WHERE r.hotelId = :hotelId
      AND r.roomTypeId = :roomTypeId
      AND r.status = 'AVAILABLE'
      AND NOT EXISTS (
          SELECT res FROM Reservation res
          WHERE res.roomId = r.id
            AND res.status IN ('CONFIRMED', 'CHECKED_IN')
            AND res.checkInDate < :checkOut
            AND res.checkOutDate > :checkIn
      )
    ORDER BY r.roomNumber
    LIMIT 1
    FOR UPDATE
""")
Optional<Room> findAvailableRoom(
    @Param("hotelId") UUID hotelId,
    @Param("roomTypeId") UUID roomTypeId,
    @Param("checkIn") LocalDate checkIn,
    @Param("checkOut") LocalDate checkOut
);
```

**Challenge 2: Controlled Overbooking Strategy**
- **Problem**: Balance revenue (high occupancy) vs risk (bumping guests)
- **Solution**: Statistical model based on historical cancellation rates
- **Algorithm**:
```java
/**
 * Overbooking decision engine
 */
class OverbookingStrategy {
    
    private static final double MAX_OVERBOOKING_RATE = 0.10; // 10%
    private static final double BUMP_COST_MULTIPLIER = 3.0; // 3x room rate
    
    /**
     * INTERVIEW CRITICAL: Decide if overbooking is allowed
     */
    public boolean canOverbook(UUID hotelId, UUID roomTypeId, 
                               LocalDate checkIn, LocalDate checkOut) {
        // Get room type capacity
        RoomType roomType = roomTypeRepo.findById(roomTypeId).orElseThrow();
        int totalRooms = roomType.getTotalCount();
        
        // Count existing bookings (including overbooked)
        int confirmedBookings = reservationRepo.countConfirmedBookings(
            hotelId, roomTypeId, checkIn, checkOut
        );
        
        // Calculate max allowed bookings
        int maxBookings = (int) (totalRooms * (1 + MAX_OVERBOOKING_RATE));
        
        if (confirmedBookings >= maxBookings) {
            return false; // Already at overbooking limit
        }
        
        // Calculate expected cancellation rate
        double cancellationRate = calculateCancellationRate(
            hotelId, roomTypeId, checkIn
        );
        
        // Expected cancellations = current_bookings * cancellation_rate
        double expectedCancellations = confirmedBookings * cancellationRate;
        
        // Risk: Probability of bumping guest
        double bumpProbability = calculateBumpProbability(
            confirmedBookings, totalRooms, cancellationRate
        );
        
        // Expected cost of bumping
        BigDecimal roomRate = pricingEngine.getBaseRate(roomTypeId, checkIn);
        BigDecimal expectedBumpCost = roomRate
            .multiply(BigDecimal.valueOf(BUMP_COST_MULTIPLIER))
            .multiply(BigDecimal.valueOf(bumpProbability));
        
        // Revenue from additional booking
        BigDecimal expectedRevenue = roomRate;
        
        // Allow if expected revenue > expected bump cost
        return expectedRevenue.compareTo(expectedBumpCost) > 0;
    }
    
    /**
     * Calculate historical cancellation rate
     */
    private double calculateCancellationRate(UUID hotelId, UUID roomTypeId, 
                                              LocalDate checkInDate) {
        // Look at same day-of-week in past 6 months
        LocalDate startDate = checkInDate.minusMonths(6);
        DayOfWeek dayOfWeek = checkInDate.getDayOfWeek();
        
        List<Reservation> historicalBookings = reservationRepo
            .findHistoricalBookings(hotelId, roomTypeId, startDate, checkInDate, dayOfWeek);
        
        if (historicalBookings.isEmpty()) {
            return 0.05; // Default 5% cancellation rate
        }
        
        long totalBookings = historicalBookings.size();
        long cancelledBookings = historicalBookings.stream()
            .filter(r -> r.getStatus() == ReservationStatus.CANCELLED)
            .count();
        
        return (double) cancelledBookings / totalBookings;
    }
    
    /**
     * Calculate probability of bumping guest (binomial distribution)
     */
    private double calculateBumpProbability(int bookings, int rooms, 
                                             double cancellationRate) {
        // Probability that (bookings - cancellations) > rooms
        // Using normal approximation for simplicity
        
        double expectedCancellations = bookings * cancellationRate;
        double variance = bookings * cancellationRate * (1 - cancellationRate);
        double stdDev = Math.sqrt(variance);
        
        // P(actual_guests > rooms) = P(cancellations < bookings - rooms)
        double threshold = bookings - rooms;
        
        if (threshold <= 0) {
            return 0.0; // No risk if under capacity
        }
        
        // Z-score
        double z = (threshold - expectedCancellations) / stdDev;
        
        // Use standard normal CDF
        return standardNormalCDF(z);
    }
    
    private double standardNormalCDF(double z) {
        // Simplified approximation
        return 0.5 * (1 + erf(z / Math.sqrt(2)));
    }
    
    private double erf(double x) {
        // Error function approximation
        double a1 =  0.254829592;
        double a2 = -0.284496736;
        double a3 =  1.421413741;
        double a4 = -1.453152027;
        double a5 =  1.061405429;
        double p  =  0.3275911;
        
        int sign = x < 0 ? -1 : 1;
        x = Math.abs(x);
        
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);
        
        return sign * y;
    }
}

/**
 * Handle overbooking resolution
 */
class OverbookingResolutionService {
    
    /**
     * INTERVIEW CRITICAL: Resolve overbooking on check-in day
     */
    @Scheduled(cron = "0 0 10 * * *") // Run at 10 AM daily
    @Transactional
    public void resolveOverbooking() {
        LocalDate today = LocalDate.now();
        
        // Find hotels with overbooking today
        List<OverbookingSituation> situations = findOverbookingSituations(today);
        
        for (OverbookingSituation situation : situations) {
            int excessBookings = situation.getConfirmedCount() - situation.getAvailableRooms();
            
            if (excessBookings <= 0) {
                continue; // No issue
            }
            
            // Strategy: Relocate lowest priority guests
            List<Reservation> candidates = findRelocationCandidates(situation);
            
            for (int i = 0; i < excessBookings && i < candidates.size(); i++) {
                Reservation reservation = candidates.get(i);
                
                // Try to relocate to nearby hotel
                Optional<RelocateResult> relocation = relocateGuest(reservation);
                
                if (relocation.isPresent()) {
                    // Update original reservation
                    reservation.setStatus(ReservationStatus.RELOCATED);
                    reservation.setRelocatedTo(relocation.get().getNewHotelId());
                    reservationRepo.save(reservation);
                    
                    // Notify guest
                    notificationService.notifyRelocation(reservation, relocation.get());
                    
                    // Compensate guest (free upgrade, voucher, etc.)
                    compensationService.compensateGuest(reservation);
                    
                } else {
                    // Worst case: Cannot relocate
                    logger.error("Failed to relocate reservation: {}", reservation.getId());
                    // Escalate to hotel manager
                }
            }
        }
    }
    
    /**
     * Find candidates for relocation (lowest priority first)
     */
    private List<Reservation> findRelocationCandidates(OverbookingSituation situation) {
        List<Reservation> reservations = reservationRepo.findConfirmedForDate(
            situation.getHotelId(), situation.getRoomTypeId(), situation.getDate()
        );
        
        // Priority: Late bookers, non-loyalty members, short stays
        return reservations.stream()
            .sorted(Comparator
                .comparing((Reservation r) -> r.getGuest().getLoyaltyTier())
                .reversed()
                .thenComparing(Reservation::getCreatedAt)
                .reversed()
            )
            .collect(Collectors.toList());
    }
}
```

**Challenge 3: Dynamic Pricing Engine**
- **Problem**: Optimize revenue with demand-based pricing
- **Solution**: Multi-factor pricing model with A/B testing support
- **Algorithm**:
```java
/**
 * Dynamic pricing engine
 */
class PricingEngine {
    
    /**
     * INTERVIEW CRITICAL: Calculate price with multiple factors
     */
    public BigDecimal calculatePrice(Room room, LocalDate checkIn, LocalDate checkOut,
                                     int guestCount) {
        // Get base rate
        BigDecimal baseRate = room.getRoomType().getBaseRate();
        
        // Calculate number of nights
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        // Apply multipliers
        BigDecimal occupancyMultiplier = getOccupancyMultiplier(
            room.getHotelId(), room.getRoomTypeId(), checkIn, checkOut
        );
        
        BigDecimal seasonalityMultiplier = getSeasonalityMultiplier(checkIn);
        
        BigDecimal eventMultiplier = getEventMultiplier(
            room.getHotel().getLocation(), checkIn, checkOut
        );
        
        BigDecimal dayOfWeekMultiplier = getDayOfWeekMultiplier(checkIn);
        
        // Calculate total
        BigDecimal pricePerNight = baseRate
            .multiply(occupancyMultiplier)
            .multiply(seasonalityMultiplier)
            .multiply(eventMultiplier)
            .multiply(dayOfWeekMultiplier);
        
        BigDecimal totalPrice = pricePerNight.multiply(BigDecimal.valueOf(nights));
        
        // Apply guest count surcharge (if > 2 guests)
        if (guestCount > 2) {
            BigDecimal surcharge = BigDecimal.valueOf((guestCount - 2) * 20.0);
            totalPrice = totalPrice.add(surcharge.multiply(BigDecimal.valueOf(nights)));
        }
        
        // Round to 2 decimal places
        return totalPrice.setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Occupancy-based multiplier (supply-demand)
     */
    private BigDecimal getOccupancyMultiplier(UUID hotelId, UUID roomTypeId,
                                               LocalDate checkIn, LocalDate checkOut) {
        // Get current occupancy rate
        double occupancyRate = inventoryService.getOccupancyRate(
            hotelId, roomTypeId, checkIn, checkOut
        );
        
        // Pricing curve
        if (occupancyRate >= 0.90) {
            return BigDecimal.valueOf(1.50); // 50% increase
        } else if (occupancyRate >= 0.80) {
            return BigDecimal.valueOf(1.30); // 30% increase
        } else if (occupancyRate >= 0.70) {
            return BigDecimal.valueOf(1.15); // 15% increase
        } else if (occupancyRate >= 0.50) {
            return BigDecimal.valueOf(1.00); // Base price
        } else {
            return BigDecimal.valueOf(0.85); // 15% discount (low demand)
        }
    }
    
    /**
     * Seasonality multiplier
     */
    private BigDecimal getSeasonalityMultiplier(LocalDate date) {
        Month month = date.getMonth();
        
        // Summer peak season (June-August)
        if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST) {
            return BigDecimal.valueOf(1.25);
        }
        
        // Winter holidays (December)
        if (month == Month.DECEMBER) {
            return BigDecimal.valueOf(1.20);
        }
        
        // Off-season (Jan-Feb)
        if (month == Month.JANUARY || month == Month.FEBRUARY) {
            return BigDecimal.valueOf(0.90);
        }
        
        return BigDecimal.valueOf(1.00);
    }
    
    /**
     * Event-based multiplier (conferences, concerts, etc.)
     */
    private BigDecimal getEventMultiplier(Location location, LocalDate checkIn, 
                                           LocalDate checkOut) {
        // Check for major events in date range
        List<Event> events = eventRepo.findEventsByLocationAndDate(
            location, checkIn, checkOut
        );
        
        if (events.isEmpty()) {
            return BigDecimal.valueOf(1.00);
        }
        
        // Find highest impact event
        BigDecimal maxMultiplier = events.stream()
            .map(Event::getPriceMultiplier)
            .max(Comparator.naturalOrder())
            .orElse(BigDecimal.ONE);
        
        return maxMultiplier;
    }
    
    /**
     * Day-of-week multiplier
     */
    private BigDecimal getDayOfWeekMultiplier(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        
        // Weekend premium
        if (day == DayOfWeek.FRIDAY || day == DayOfWeek.SATURDAY) {
            return BigDecimal.valueOf(1.10);
        }
        
        // Weekday discount
        if (day == DayOfWeek.MONDAY || day == DayOfWeek.TUESDAY) {
            return BigDecimal.valueOf(0.95);
        }
        
        return BigDecimal.valueOf(1.00);
    }
}
```

**Challenge 4: Cancellation & Refund Policy**
- **Problem**: Fair refund calculation based on cancellation timing
- **Solution**: Tiered refund policy with grace periods
- **Algorithm**:
```java
/**
 * Cancellation and refund service
 */
class CancellationService {
    
    /**
     * INTERVIEW CRITICAL: Calculate refund based on policy
     */
    @Transactional
    public CancellationResult cancelReservation(UUID reservationId, String reason) {
        Reservation reservation = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        
        // Validate cancellable
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            return CancellationResult.failure("Reservation cannot be cancelled");
        }
        
        // Calculate refund
        BigDecimal refundAmount = calculateRefund(reservation);
        BigDecimal cancellationFee = reservation.getTotalPrice().subtract(refundAmount);
        
        // Update reservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(Instant.now());
        reservation.setCancellationReason(reason);
        reservation.setRefundAmount(refundAmount);
        reservationRepo.save(reservation);
        
        // Release inventory
        inventoryService.releaseInventory(
            reservation.getRoomId(),
            reservation.getCheckInDate(),
            reservation.getCheckOutDate()
        );
        
        // Process refund (async)
        refundProcessor.processRefund(reservation, refundAmount);
        
        // Publish event
        eventPublisher.publish(new ReservationCancelledEvent(
            reservationId, refundAmount, cancellationFee
        ));
        
        return CancellationResult.success(reservation, refundAmount);
    }
    
    /**
     * Refund calculation based on cancellation timing
     */
    private BigDecimal calculateRefund(Reservation reservation) {
        LocalDate today = LocalDate.now();
        LocalDate checkInDate = reservation.getCheckInDate();
        
        long daysUntilCheckIn = ChronoUnit.DAYS.between(today, checkInDate);
        
        BigDecimal totalPrice = reservation.getTotalPrice();
        
        // Refund policy:
        // - More than 30 days: Full refund
        // - 15-30 days: 75% refund
        // - 7-14 days: 50% refund
        // - 3-6 days: 25% refund
        // - Less than 3 days: No refund
        
        if (daysUntilCheckIn > 30) {
            return totalPrice; // 100%
        } else if (daysUntilCheckIn >= 15) {
            return totalPrice.multiply(BigDecimal.valueOf(0.75)); // 75%
        } else if (daysUntilCheckIn >= 7) {
            return totalPrice.multiply(BigDecimal.valueOf(0.50)); // 50%
        } else if (daysUntilCheckIn >= 3) {
            return totalPrice.multiply(BigDecimal.valueOf(0.25)); // 25%
        } else {
            return BigDecimal.ZERO; // 0%
        }
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **CQRS** | Separate read (search) and write (booking) models | Optimize queries separately from transactional writes |
| **Strategy** | Pricing strategies, overbooking policies, refund rules | Swap algorithms based on hotel policy |
| **State** | Reservation lifecycle (CONFIRMED → CHECKED_IN → CHECKED_OUT) | Clear state transitions |
| **Specification** | Search filters, eligibility rules | Composable business rules |
| **Repository** | Data access abstraction | Isolate persistence logic |
| **Domain Events** | RoomBooked, ReservationCancelled | Decouple side effects |
| **Factory** | Create pricing strategy based on hotel tier | Encapsulate creation |

### 5. Domain Model & Class Structure

```
┌──────────────────────┐
│  BookingService      │ (Application Service)
│   - pricingEngine    │
│   - overbookingMgr   │
│   - inventoryService │
└────────┬─────────────┘
         │ manages
         │
    ┌────┴──────────┬────────────┬──────────┐
    ▼               ▼            ▼          ▼
┌────────┐    ┌──────────┐  ┌──────────┐ ┌──────┐
│  Hotel │    │   Room   │  │Reservat. │ │ Guest│
│(Aggr.) │    │ (Entity) │  │ (Entity) │ │(Ent.)│
└───┬────┘    └──────────┘  └──────────┘ └──────┘
    │ has
    ▼
┌──────────┐
│ RoomType │ (Value Object)
└──────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Booking Flow**
```
Guest   UI   BookingSvc   PricingEng   RoomRepo   InvSvc   EventBus
  │      │        │            │           │         │         │
  ├─book─>│        │            │           │         │         │
  │      ├─create─>│            │           │         │         │
  │      │        ├─calcPrice──>│           │         │         │
  │      │        │<─price──────┤           │         │         │
  │      │        ├─findRoom────────────────>│         │         │
  │      │        │<─room(locked)────────────┤         │         │
  │      │        ├─decrementInv────────────────────>│         │
  │      │        ├─saveReserv──────────────>│         │         │
  │      │        ├─publish────────────────────────────────────>│
  │      │<─conf──┤            │           │         │         │
  │<─rcpt┤        │            │           │         │         │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// ============================================
// DOMAIN ENTITIES
// ============================================

public class Hotel {
    private UUID id;
    private String name;
    private Location location;
    private List<RoomType> roomTypes;
    private HotelTier tier; // BUDGET, STANDARD, LUXURY
}

public class RoomType {
    private UUID id;
    private String name; // "Deluxe King", "Standard Queen"
    private int maxOccupancy;
    private BigDecimal baseRate;
    private int totalCount; // Total rooms of this type
    private List<Amenity> amenities;
}

public class Room {
    private UUID id;
    private UUID hotelId;
    private UUID roomTypeId;
    private String roomNumber;
    private RoomStatus status; // AVAILABLE, OCCUPIED, MAINTENANCE, BLOCKED
    private int floor;
}

public class Reservation {
    private UUID id;
    private UUID hotelId;
    private UUID roomId; // Specific room (assigned at check-in) or null
    private UUID roomTypeId; // Type requested
    private UUID guestId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Instant checkInTime; // Actual check-in timestamp
    private Instant checkOutTime;
    private ReservationStatus status;
    private BigDecimal totalPrice;
    private BigDecimal refundAmount;
    private Instant createdAt;
    private Instant cancelledAt;
    private String cancellationReason;
    private UUID relocatedTo; // If bumped to another hotel
    
    @Version
    private long version;
}

public enum ReservationStatus {
    CONFIRMED,
    CHECKED_IN,
    CHECKED_OUT,
    CANCELLED,
    NO_SHOW,
    RELOCATED // Bumped due to overbooking
}

public class RoomInventory {
    private UUID id;
    private UUID hotelId;
    private UUID roomTypeId;
    private LocalDate date;
    private int totalRooms;
    private int availableRooms;
    private int bookedRooms;
    
    @Version
    private long version;
    
    public void decrementAvailable() {
        if (availableRooms <= 0) {
            throw new NoRoomsAvailableException();
        }
        this.availableRooms--;
        this.bookedRooms++;
    }
    
    public double getOccupancyRate() {
        return (double) bookedRooms / totalRooms;
    }
}

// ============================================
// BOOKING SERVICE
// ============================================

public class BookingService {
    private final ReservationRepository reservationRepo;
    private final RoomRepository roomRepo;
    private final RoomInventoryRepository inventoryRepo;
    private final PricingEngine pricingEngine;
    private final OverbookingStrategy overbookingStrategy;
    private final EventPublisher eventPublisher;
    
    /**
     * INTERVIEW CRITICAL: Atomic booking with inventory management
     */
    @Transactional
    public BookingResult bookRoom(BookingRequest request) {
        // Validate dates
        validateDates(request.getCheckInDate(), request.getCheckOutDate());
        
        // Find available room with pessimistic lock
        Optional<Room> availableRoom = roomRepo.findAvailableRoomWithLock(
            request.getHotelId(),
            request.getRoomTypeId(),
            request.getCheckInDate(),
            request.getCheckOutDate()
        );
        
        if (availableRoom.isEmpty()) {
            // Check overbooking
            if (overbookingStrategy.canOverbook(
                request.getHotelId(),
                request.getRoomTypeId(),
                request.getCheckInDate(),
                request.getCheckOutDate()
            )) {
                return createOverbookedReservation(request);
            }
            
            return BookingResult.failure(BookingFailureReason.NO_ROOMS_AVAILABLE);
        }
        
        Room room = availableRoom.get();
        
        // Calculate price
        BigDecimal totalPrice = pricingEngine.calculatePrice(
            room,
            request.getCheckInDate(),
            request.getCheckOutDate(),
            request.getGuestCount()
        );
        
        // Create reservation
        Reservation reservation = Reservation.builder()
            .id(UUID.randomUUID())
            .hotelId(request.getHotelId())
            .roomId(room.getId())
            .roomTypeId(request.getRoomTypeId())
            .guestId(request.getGuestId())
            .checkInDate(request.getCheckInDate())
            .checkOutDate(request.getCheckOutDate())
            .status(ReservationStatus.CONFIRMED)
            .totalPrice(totalPrice)
            .createdAt(Instant.now())
            .build();
        
        reservationRepo.save(reservation);
        
        // Update inventory for each night
        LocalDate currentDate = request.getCheckInDate();
        while (currentDate.isBefore(request.getCheckOutDate())) {
            RoomInventory inventory = inventoryRepo.findByHotelRoomTypeAndDate(
                request.getHotelId(),
                request.getRoomTypeId(),
                currentDate
            );
            
            inventory.decrementAvailable();
            inventoryRepo.save(inventory);
            
            currentDate = currentDate.plusDays(1);
        }
        
        // Publish event
        eventPublisher.publish(new RoomBookedEvent(
            reservation.getId(),
            room.getId(),
            request.getCheckInDate(),
            request.getCheckOutDate(),
            totalPrice
        ));
        
        return BookingResult.success(reservation);
    }
    
    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        LocalDate today = LocalDate.now();
        
        if (checkIn.isBefore(today)) {
            throw new InvalidBookingException("Cannot book in the past");
        }
        
        if (!checkIn.isBefore(checkOut)) {
            throw new InvalidBookingException("Check-out must be after check-in");
        }
        
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights > 30) {
            throw new InvalidBookingException("Maximum stay is 30 nights");
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Check-in process
     */
    @Transactional
    public CheckInResult checkIn(UUID reservationId, String roomNumber) {
        Reservation reservation = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        
        // Validate status
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            return CheckInResult.failure("Reservation not confirmed");
        }
        
        // Validate date (allow early check-in on same day)
        LocalDate today = LocalDate.now();
        if (!today.equals(reservation.getCheckInDate()) && 
            !today.isAfter(reservation.getCheckInDate())) {
            return CheckInResult.failure("Check-in date not reached");
        }
        
        // Assign specific room if not already assigned
        if (reservation.getRoomId() == null) {
            Room room = roomRepo.findByHotelAndRoomNumber(
                reservation.getHotelId(), roomNumber
            ).orElseThrow(() -> new RoomNotFoundException(roomNumber));
            
            // Verify room type matches
            if (!room.getRoomTypeId().equals(reservation.getRoomTypeId())) {
                return CheckInResult.failure("Room type mismatch");
            }
            
            reservation.setRoomId(room.getId());
        }
        
        // Update reservation
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservation.setCheckInTime(Instant.now());
        reservationRepo.save(reservation);
        
        // Update room status
        Room room = roomRepo.findById(reservation.getRoomId()).orElseThrow();
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepo.save(room);
        
        // Publish event
        eventPublisher.publish(new GuestCheckedInEvent(
            reservationId, reservation.getGuestId(), room.getId()
        ));
        
        return CheckInResult.success(reservation);
    }
    
    /**
     * INTERVIEW CRITICAL: Check-out process
     */
    @Transactional
    public CheckOutResult checkOut(UUID reservationId) {
        Reservation reservation = reservationRepo.findById(reservationId)
            .orElseThrow(() -> new ReservationNotFoundException(reservationId));
        
        // Validate status
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            return CheckOutResult.failure("Guest not checked in");
        }
        
        // Update reservation
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservation.setCheckOutTime(Instant.now());
        reservationRepo.save(reservation);
        
        // Update room status
        Room room = roomRepo.findById(reservation.getRoomId()).orElseThrow();
        room.setStatus(RoomStatus.AVAILABLE); // Ready for next guest after cleaning
        roomRepo.save(room);
        
        // Publish event
        eventPublisher.publish(new GuestCheckedOutEvent(
            reservationId, reservation.getGuestId(), room.getId()
        ));
        
        return CheckOutResult.success(reservation);
    }
}

// ============================================
// SEARCH SERVICE (CQRS Read Model)
// ============================================

public class RoomSearchService {
    private final RoomSearchRepository searchRepo; // Optimized read model
    
    /**
     * INTERVIEW CRITICAL: Search with date availability
     */
    public SearchResult searchAvailableRooms(SearchQuery query) {
        LocalDate checkIn = query.getCheckInDate();
        LocalDate checkOut = query.getCheckOutDate();
        Location location = query.getLocation();
        
        // Query read model (denormalized for performance)
        List<RoomAvailability> results = searchRepo.findAvailableRooms(
            location,
            checkIn,
            checkOut,
            query.getMinPrice(),
            query.getMaxPrice(),
            query.getGuestCount(),
            query.getAmenities()
        );
        
        // Enrich with real-time pricing
        List<RoomOffer> offers = results.stream()
            .map(availability -> {
                BigDecimal price = pricingEngine.calculatePrice(
                    availability.getRoom(),
                    checkIn,
                    checkOut,
                    query.getGuestCount()
                );
                
                return RoomOffer.builder()
                    .hotel(availability.getHotel())
                    .roomType(availability.getRoomType())
                    .price(price)
                    .availableCount(availability.getAvailableCount())
                    .build();
            })
            .sorted(Comparator.comparing(RoomOffer::getPrice))
            .collect(Collectors.toList());
        
        return SearchResult.of(offers);
    }
}

// Read model (denormalized)
@Entity
@Table(name = "room_availability_view")
public class RoomAvailability {
    private UUID hotelId;
    private String hotelName;
    private Location location;
    private UUID roomTypeId;
    private String roomTypeName;
    private LocalDate date;
    private int availableCount;
    // ... other fields for filtering
}

// ============================================
// REPOSITORY QUERIES
// ============================================

public interface ReservationRepository {
    
    @Query("""
        SELECT r FROM Room r
        WHERE r.hotelId = :hotelId
          AND r.roomTypeId = :roomTypeId
          AND r.status = 'AVAILABLE'
          AND NOT EXISTS (
              SELECT res FROM Reservation res
              WHERE res.roomId = r.id
                AND res.status IN ('CONFIRMED', 'CHECKED_IN')
                AND res.checkInDate < :checkOut
                AND res.checkOutDate > :checkIn
          )
        ORDER BY r.roomNumber
        LIMIT 1
        FOR UPDATE
    """)
    Optional<Room> findAvailableRoomWithLock(
        @Param("hotelId") UUID hotelId,
        @Param("roomTypeId") UUID roomTypeId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut
    );
    
    @Query("""
        SELECT COUNT(r) FROM Reservation r
        WHERE r.hotelId = :hotelId
          AND r.roomTypeId = :roomTypeId
          AND r.status IN ('CONFIRMED', 'CHECKED_IN')
          AND r.checkInDate < :checkOut
          AND r.checkOutDate > :checkIn
    """)
    int countConfirmedBookings(
        @Param("hotelId") UUID hotelId,
        @Param("roomTypeId") UUID roomTypeId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut
    );
}
```

### 8. Thread Safety & Concurrency

**Booking Concurrency:**
- Row-level lock on Room (FOR UPDATE)
- Optimistic lock on RoomInventory (version field)
- Transaction isolation prevents phantom reads

**Overbooking Safety:**
- Count query within transaction
- Pessimistic lock on inventory
- Atomic increment/decrement

**Search Performance:**
- CQRS: Separate read model (denormalized)
- No locks on queries
- Eventually consistent (acceptable for search)

### 9. Top Interview Questions & Answers

**Q1: How do you prevent double-booking?**
**A:**
```sql
-- Use FOR UPDATE to lock room row
SELECT * FROM rooms 
WHERE id = ? 
  AND NOT EXISTS (
    SELECT 1 FROM reservations
    WHERE room_id = rooms.id
      AND status IN ('CONFIRMED', 'CHECKED_IN')
      AND check_in_date < ?
      AND check_out_date > ?
  )
FOR UPDATE;

-- Within transaction:
1. Lock room row
2. Check no overlapping reservation
3. Create reservation
4. Commit (releases lock)
```

**Q2: When should you allow overbooking?**
**A:**
Decision criteria:
```
Allow if:
1. Current bookings < total_rooms * 1.10 (max 10% overbook)
2. Expected_revenue > Expected_bump_cost
3. Historical cancellation rate > 5%
4. Days until check-in > 7 (time to resolve)

Expected_bump_cost = room_rate * 3 * P(bump)
P(bump) = P(actual_guests > rooms)
```

**Q3: How do you calculate refunds fairly?**
**A:**
Tiered policy:
```
> 30 days: 100% refund
15-30 days: 75% refund
7-14 days: 50% refund
3-6 days: 25% refund
< 3 days: 0% refund

Premium members: +1 tier upgrade
Non-refundable bookings: 0% always (but lower price)
```

**Q4: How to implement dynamic pricing?**
**A:**
```java
Final_price = Base_rate 
  × Occupancy_multiplier 
  × Seasonality_multiplier
  × Event_multiplier
  × DayOfWeek_multiplier

Occupancy_multiplier:
- 90%+: 1.50x
- 70-89%: 1.15x
- 50-69%: 1.00x
- <50%: 0.85x

Update hourly based on booking velocity
```

**Q5: What metrics to track?**
**A:**
```
KPIs:
1. Occupancy rate (booked / total rooms)
2. RevPAR (revenue per available room)
3. ADR (average daily rate)
4. Cancellation rate
5. Overbooking incidents (bumped guests)
6. Booking window (days in advance)
7. Length of stay distribution

Alerts:
- Occupancy < 60% → Increase promotions
- Cancellation rate > 15% → Review policy
- Bump rate > 2% → Reduce overbooking
```

**Q6: Database schema?**
**A:**
```sql
CREATE TABLE hotels (
    id UUID PRIMARY KEY,
    name VARCHAR(200),
    location_city VARCHAR(100),
    location_country VARCHAR(50),
    tier VARCHAR(20)
);

CREATE TABLE room_types (
    id UUID PRIMARY KEY,
    hotel_id UUID REFERENCES hotels(id),
    name VARCHAR(100),
    max_occupancy INT,
    base_rate DECIMAL(10,2),
    total_count INT
);

CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    hotel_id UUID REFERENCES hotels(id),
    room_type_id UUID REFERENCES room_types(id),
    room_number VARCHAR(20),
    status VARCHAR(20),
    floor INT,
    UNIQUE(hotel_id, room_number)
);

CREATE TABLE reservations (
    id UUID PRIMARY KEY,
    hotel_id UUID REFERENCES hotels(id),
    room_id UUID REFERENCES rooms(id),
    room_type_id UUID REFERENCES room_types(id),
    guest_id UUID NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    check_in_time TIMESTAMP,
    check_out_time TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    total_price DECIMAL(10,2),
    refund_amount DECIMAL(10,2),
    created_at TIMESTAMP NOT NULL,
    version BIGINT DEFAULT 0
);

CREATE INDEX idx_reservations_room_dates 
    ON reservations(room_id, status, check_in_date, check_out_date);

CREATE INDEX idx_reservations_check_in 
    ON reservations(hotel_id, check_in_date, status);

CREATE TABLE room_inventory (
    id UUID PRIMARY KEY,
    hotel_id UUID NOT NULL,
    room_type_id UUID NOT NULL,
    date DATE NOT NULL,
    total_rooms INT NOT NULL,
    available_rooms INT NOT NULL,
    booked_rooms INT NOT NULL,
    version BIGINT DEFAULT 0,
    UNIQUE(hotel_id, room_type_id, date)
);

CREATE INDEX idx_inventory_search 
    ON room_inventory(hotel_id, room_type_id, date, available_rooms);
```

**Q7: How to handle maintenance blocks?**
**A:**
```java
// Block rooms for maintenance
@Transactional
public void blockRoom(UUID roomId, LocalDate startDate, LocalDate endDate) {
    Room room = roomRepo.findById(roomId).orElseThrow();
    room.setStatus(RoomStatus.MAINTENANCE);
    
    // Create blocking reservation
    Reservation block = Reservation.builder()
        .id(UUID.randomUUID())
        .roomId(roomId)
        .checkInDate(startDate)
        .checkOutDate(endDate)
        .status(ReservationStatus.BLOCKED)
        .build();
    
    reservationRepo.save(block);
    
    // Reduce inventory
    LocalDate current = startDate;
    while (current.isBefore(endDate)) {
        RoomInventory inv = inventoryRepo.findByRoomAndDate(roomId, current);
        inv.decrementAvailable();
        inventoryRepo.save(inv);
        current = current.plusDays(1);
    }
}
```

**Q8: How to scale search?**
**A:**
```
CQRS Read Model:
1. Denormalized view: room_availability_view
2. Pre-aggregated inventory counts
3. Cache popular searches (Redis, 5 min TTL)
4. Elasticsearch for full-text hotel/location search

Write Model:
- Strong consistency on booking
- Event sourcing for audit trail
- Background job syncs read model

Partitioning:
- Shard by location/hotel_id
- Separate DB for read model
```

**Q9: How to test overbooking logic?**
**A:**
```java
@Test
public void testOverbookingDecision() {
    // Setup: 100 rooms, 95 booked, 10% cancellation rate
    when(roomTypeRepo.findById(any()))
        .thenReturn(roomType(totalCount = 100));
    when(reservationRepo.countConfirmed(...))
        .thenReturn(95);
    when(calculator.getCancellationRate(...))
        .thenReturn(0.10);
    
    // Expected cancellations: 95 * 0.10 = 9.5
    // Expected available: 100 - 95 + 9.5 = 14.5
    // Safe to overbook
    
    boolean allowed = strategy.canOverbook(...);
    assertTrue(allowed);
    
    // Test with 105 bookings (at limit)
    when(reservationRepo.countConfirmed(...))
        .thenReturn(105);
    
    allowed = strategy.canOverbook(...);
    assertFalse(allowed); // At 105% capacity
}
```

**Q10: How to handle time zones?**
**A:**
```java
// Store all dates in hotel's local timezone
public class Reservation {
    private LocalDate checkInDate; // Date in hotel timezone
    private ZoneId hotelTimezone; // e.g., "America/New_York"
    
    public ZonedDateTime getCheckInDateTime() {
        return checkInDate.atTime(15, 0) // 3 PM check-in
            .atZone(hotelTimezone);
    }
}

// Convert for guest display
public ZonedDateTime getGuestLocalCheckIn(ZoneId guestTimezone) {
    return getCheckInDateTime().withZoneSameInstant(guestTimezone);
}
```

### 10. Extensions & Variations

1. **Group Bookings**: Reserve multiple rooms atomically
2. **Packages**: Room + meals + spa (composite pricing)
3. **Loyalty Program**: Points, tier-based benefits
4. **Waitlist**: Auto-book if cancellation occurs
5. **Real-time Chat**: Guest support during booking

### 11. Testing Strategy

**Unit Tests:**
- Date overlap logic
- Pricing calculation
- Refund policy
- Overbooking decision

**Integration Tests:**
- Full booking flow
- Concurrent bookings (race condition)
- Check-in/check-out lifecycle
- Cancellation with refund

**Load Tests:**
- 10K concurrent searches
- 1K concurrent bookings
- Overbooking resolution at scale

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Check availability → Book (race condition)
✅ **Do**: Lock room within transaction

❌ **Avoid**: Unlimited overbooking
✅ **Do**: Cap at 10% with risk calculation

❌ **Avoid**: Same model for search & booking
✅ **Do**: CQRS with separate read model

❌ **Avoid**: Hardcoded pricing rules
✅ **Do**: Pluggable pricing strategy

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Search | O(log N) | O(R) | N = total rooms, R = results (indexed) |
| Book | O(D) | O(D) | D = days in stay (inventory updates) |
| Check-in | O(1) | O(1) | Single update |
| Cancel | O(D) | O(1) | D = days to release |
| Price calculation | O(1) | O(1) | Multiplier lookup |
| Overbooking decision | O(H) | O(1) | H = historical data points |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Concurrency** | 30% | Prevents double-booking, proper locking |
| **Overbooking** | 25% | Statistical model, risk calculation |
| **Pricing** | 20% | Multi-factor dynamic pricing |
| **Time Modeling** | 15% | Date overlap, timezone handling |
| **CQRS** | 10% | Separate read/write models |

**Red Flags:**
- No locking for bookings
- Unlimited overbooking
- Static pricing
- Ignoring date overlaps
- Same model for search & booking

---
