# Problem 5: Ride Sharing Matching Core (Strategy + Observer + State)

## System Design Perspective (High-Level)

### Architecture Overview
```
┌─────────────┐         ┌──────────────┐         ┌─────────────┐
│   Mobile    │────────>│  API Gateway │────────>│ Matching    │
│   Apps      │<────────│   Service    │<────────│  Service    │
└─────────────┘         └──────────────┘         └──────┬──────┘
                                                         │
                        ┌────────────────────────────────┼─────────────┐
                        │                                │             │
                        ▼                                ▼             ▼
                ┌───────────────┐              ┌─────────────┐  ┌─────────┐
                │  Geospatial   │              │   Driver    │  │  Rider  │
                │     Index     │              │  Tracking   │  │ Service │
                │  (Redis Geo)  │              │  Service    │  │         │
                └───────────────┘              └─────────────┘  └─────────┘
                        │
                        ▼
                ┌───────────────┐              ┌─────────────┐
                │  Notification │              │   Event     │
                │    Service    │<─────────────│    Bus      │
                └───────────────┘              └─────────────┘
```

### Key System Design Decisions
1. **Geospatial Indexing**: Redis Geo / PostGIS for O(log n) proximity queries
2. **Real-time Updates**: WebSocket connections for driver location streaming
3. **Event-Driven**: Publish-subscribe for ride state changes
4. **Horizontal Scaling**: Shard by geographic region (city/zone)
5. **CAP Trade-off**: Availability over strict consistency (eventual consistency for driver locations)

### Scalability Considerations
- **Read-heavy**: 10K req/s for driver location updates
- **Write-heavy**: 5K req/s for matching requests
- **Data Volume**: 100K active drivers, 500K ride requests/hour
- **Latency Target**: <500ms for match response

---

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design a ride-sharing matching engine that pairs riders with nearby available drivers, calculates ETAs, handles ride lifecycle, and optimizes assignment based on multiple factors.

**Assumptions / Scope:**
- Support ride types: Economy, Premium, Shared, XL
- Matching criteria: Distance, ETA, driver rating, acceptance rate
- Real-time driver location updates (every 5-10 seconds)
- Match expiration: 30 seconds timeout, retry with next best driver
- Concurrent matching: Handle simultaneous rider requests
- Scale: 10K concurrent rides, 100K active drivers per city
- Out of scope: Payment processing, navigation routing (use external API)

**Non-Functional Goals:**
- Sub-second matching for 95th percentile
- No double-assignment of drivers
- Fair driver distribution (prevent starving low-rated drivers)
- Graceful degradation under high load
- Audit trail for matching decisions

### 2. Core Requirements

**Functional:**
- Request ride with pickup location, destination, ride type
- Find available drivers within radius (dynamic expansion)
- Calculate ETA using distance + traffic conditions
- Assign driver using configurable strategy (nearest, highest rated, balanced)
- Handle driver accept/decline with automatic retry
- Support ride cancellation and driver reassignment
- Track ride states: Requested → Matched → DriverEnroute → PickedUp → InProgress → Completed

**Non-Functional:**
- **Performance**: Match within 500ms for 95% of requests
- **Consistency**: Strong consistency for driver assignment (no double-booking)
- **Availability**: 99.9% uptime during peak hours
- **Extensibility**: New matching strategies without core changes
- **Observability**: Metrics for match success rate, average match time, driver utilization

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Efficient Proximity Search**
- **Problem**: Finding drivers within radius from millions of locations
- **Solution**: Geospatial indexing with quadtree or geohash
- **Algorithm**:
```
GeospatialIndex:
  QuadTree<DriverId, Location> driverIndex
  
  findNearbyDrivers(location, radius):
    // O(log n + k) where k = results
    boundingBox = location.expandToRadius(radius)
    candidates = quadTree.query(boundingBox)
    
    // Filter by actual distance using Haversine
    nearby = []
    for driver in candidates:
      distance = haversineDistance(location, driver.location)
      if distance <= radius:
        nearby.add((driver, distance))
    
    return nearby.sortByDistance()
```

**Challenge 2: Real-Time ETA Calculation**
- **Problem**: Accurate ETA considering traffic, road network, driver behavior
- **Solution**: Hybrid approach - precomputed zones + real-time adjustments
- **Algorithm**:
```
ETACalculator:
  Map<(ZoneId, ZoneId), HistoricalETA> etaCache
  TrafficDataProvider trafficProvider
  
  calculateETA(driverLoc, pickupLoc):
    // Step 1: Straight-line distance baseline
    straightDist = haversineDistance(driverLoc, pickupLoc)
    baselineETA = straightDist / AVG_SPEED
    
    // Step 2: Historical zone adjustment
    driverZone = getZone(driverLoc)
    pickupZone = getZone(pickupLoc)
    historicalFactor = etaCache.get((driverZone, pickupZone))
    
    // Step 3: Real-time traffic multiplier
    trafficMultiplier = trafficProvider.getCurrentMultiplier(driverZone)
    
    finalETA = baselineETA * historicalFactor * trafficMultiplier
    return finalETA.clampBetween(MIN_ETA, MAX_ETA)
```

**Challenge 3: Fair Driver Matching Algorithm**
- **Problem**: Balance between rider experience (nearest driver) and driver fairness (prevent starving)
- **Solution**: Weighted scoring with multiple factors
- **Algorithm**:
```
MatchingStrategy:
  calculateScore(driver, rideRequest):
    // Multi-factor scoring
    distanceScore = 1.0 / (1.0 + driver.distance)  // Closer = better
    ratingScore = driver.rating / 5.0              // Higher rating = better
    acceptanceScore = driver.acceptanceRate        // Higher acceptance = better
    idleTimeScore = min(1.0, driver.idleMinutes / 30.0)  // Prevent starving
    
    // Weighted combination
    finalScore = (
      0.5 * distanceScore +
      0.2 * ratingScore +
      0.2 * acceptanceScore +
      0.1 * idleTimeScore
    )
    
    return finalScore
```

**Challenge 4: Handling Match Timeouts and Retries**
- **Problem**: Driver doesn't respond, need automatic retry without duplicate assignments
- **Solution**: State machine with timeout handlers + pessimistic locking
- **Algorithm**:
```
MatchCoordinator:
  matchRide(rideRequest):
    attempt = 0
    maxAttempts = 3
    radiusMultiplier = 1.0
    
    while attempt < maxAttempts:
      // Expand search radius on retry
      searchRadius = BASE_RADIUS * radiusMultiplier
      candidates = findCandidates(rideRequest, searchRadius)
      
      for driver in candidates:
        // Pessimistic lock to prevent double assignment
        if !driverLockService.tryLock(driver.id, TIMEOUT):
          continue
        
        try:
          match = createMatch(rideRequest, driver)
          notifyDriver(driver, match)
          
          // Wait for driver response
          response = awaitResponse(match, RESPONSE_TIMEOUT)
          
          if response == ACCEPTED:
            rideRequest.state = MATCHED
            return match
          else:
            // Driver declined, try next
            continue
        finally:
          driverLockService.unlock(driver.id)
      
      // No driver accepted, expand radius and retry
      attempt++
      radiusMultiplier *= 1.5
    
    throw NoDriverAvailableException()
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **Strategy** | Matching algorithms | Swap between nearest-first, balanced, surge-aware strategies |
| **Observer** | Ride state changes | Decouple state transitions from notifications, analytics, billing |
| **State** | Ride lifecycle | Clear state transitions with validation (can't pick up before enroute) |
| **Factory** | Create different ride types | Encapsulate instantiation logic for Economy, Premium, Shared rides |
| **Command** | Match operations | Encapsulate match request as object for queuing, retry, audit |
| **Repository** | Data access | Abstract persistence for rides, drivers, locations |
| **Adapter** | External routing APIs | Wrap Google Maps / Mapbox APIs with uniform interface |
| **Circuit Breaker** | External service calls | Fail fast when routing API is down, fallback to straight-line distance |

### 5. Domain Model & Class Structure

```
┌────────────────────┐
│  MatchingService   │ (Facade)
│  - geospatialIndex │
│  - matchStrategy   │
│  - etaCalculator   │
│  - driverLockSvc   │
│  - eventPublisher  │
└─────────┬──────────┘
          │ coordinates
          │
    ┌─────┴────────────────┬──────────────────┐
    ▼                      ▼                  ▼
┌─────────┐        ┌──────────────┐    ┌──────────┐
│  Ride   │        │    Driver    │    │  Rider   │
│ (Entity)│        │   (Entity)   │    │ (Entity) │
└─────────┘        └──────────────┘    └──────────┘
    │                      │
    │ has                  │ has
    ▼                      ▼
┌─────────────┐    ┌──────────────┐
│  RideState  │    │DriverStatus  │
│  (Enum)     │    │  (Enum)      │
│ REQUESTED   │    │ AVAILABLE    │
│ MATCHED     │    │ ON_RIDE      │
│ ENROUTE     │    │ OFFLINE      │
│ IN_PROGRESS │    └──────────────┘
│ COMPLETED   │
└─────────────┘

Strategies:
┌───────────────────────┐
│  MatchingStrategy     │
│  + findBestDriver()   │
└───────────────────────┘
        ▲
        │implements
┌───────┴────────────────────────────┐
│  NearestDriverStrategy             │
│  BalancedMatchingStrategy          │
│  SurgeAwareMatchingStrategy        │
└────────────────────────────────────┘

┌───────────────────────┐
│   ETACalculator       │
│  + calculateETA()     │
└───────────────────────┘
        ▲
┌───────┴────────────────────────────┐
│  SimpleETACalculator               │
│  TrafficAwareETACalculator         │
│  MLBasedETACalculator              │
└────────────────────────────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Request Ride & Match Driver**
```
Rider    MatchingSvc   GeoIndex   MatchStrategy   Driver   LockSvc   EventBus
  │           │            │            │            │         │          │
  ├─request──>│            │            │            │         │          │
  │  (pickup, │            │            │            │         │          │
  │   dest)   │            │            │            │         │          │
  │           ├─findNearby>│            │            │         │          │
  │           │  (radius)  │            │            │         │          │
  │           │<─drivers───┤            │            │         │          │
  │           ├─rank()─────────────────>│            │         │          │
  │           │<──ranked───────────────┤            │         │          │
  │           │                         │            │         │          │
  │           ├─tryLock(driverId)──────────────────────────>│          │
  │           │<──lockAcquired──────────────────────────────┤          │
  │           ├─notify(match)──────────────────────>│         │          │
  │           │                         │            │         │          │
  │           │<────ACCEPTED────────────────────────┤         │          │
  │           ├─publish(RideMatched)────────────────────────────────>│
  │           ├─unlock(driverId)────────────────────────────>│          │
  │<──match───┤                         │            │         │          │
```

**Sequence: Driver Location Update**
```
Driver   LocationSvc   GeoIndex   AnalyticsSvc   NotificationSvc
  │           │            │            │                │
  ├─update──>│            │            │                │
  │ (lat,lng) │            │            │                │
  │           ├─updateIdx─>│            │                │
  │           ├─analyze────────────────>│                │
  │           │            │            │                │
  │           │            │  (detect surge zone)        │
  │           │<──surgeAlert───────────┤                │
  │           ├─notify──────────────────────────────────>│
  │<──ack─────┤            │            │                │
```

### 7. Core Implementation (Java-esque Pseudocode)

```java
// Main Matching Service
public class RideMatchingService {
    private final GeospatialIndex<Driver> driverIndex;
    private final MatchingStrategy matchingStrategy;
    private final ETACalculator etaCalculator;
    private final DriverLockService lockService;
    private final EventPublisher eventPublisher;
    private final RideRepository rideRepository;
    
    private static final Duration MATCH_TIMEOUT = Duration.ofSeconds(30);
    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final double BASE_SEARCH_RADIUS_KM = 5.0;
    
    public Match matchRide(RideRequest request) throws NoDriverAvailableException {
        Ride ride = createRide(request);
        rideRepository.save(ride);
        
        eventPublisher.publish(new RideRequestedEvent(ride));
        
        int attempt = 0;
        double searchRadius = BASE_SEARCH_RADIUS_KM;
        
        while (attempt < MAX_RETRY_ATTEMPTS) {
            List<DriverCandidate> candidates = findCandidates(
                request.getPickupLocation(),
                searchRadius,
                request.getRideType()
            );
            
            if (candidates.isEmpty()) {
                attempt++;
                searchRadius *= 1.5; // Expand search radius
                continue;
            }
            
            // Try each candidate until one accepts
            for (DriverCandidate candidate : candidates) {
                Optional<Match> match = attemptMatch(ride, candidate);
                if (match.isPresent()) {
                    return match.get();
                }
            }
            
            attempt++;
            searchRadius *= 1.5;
        }
        
        ride.setState(RideState.NO_DRIVER_AVAILABLE);
        eventPublisher.publish(new NoDriverFoundEvent(ride));
        throw new NoDriverAvailableException(request);
    }
    
    private List<DriverCandidate> findCandidates(
        Location pickup,
        double radiusKm,
        RideType rideType
    ) {
        // Step 1: Geospatial query
        List<Driver> nearbyDrivers = driverIndex.findWithinRadius(
            pickup,
            radiusKm,
            DriverStatus.AVAILABLE
        );
        
        // Step 2: Filter by ride type compatibility
        List<Driver> compatibleDrivers = nearbyDrivers.stream()
            .filter(d -> d.supportsRideType(rideType))
            .collect(Collectors.toList());
        
        // Step 3: Calculate ETA and create candidates
        List<DriverCandidate> candidates = compatibleDrivers.stream()
            .map(driver -> {
                Duration eta = etaCalculator.calculateETA(
                    driver.getCurrentLocation(),
                    pickup
                );
                double score = matchingStrategy.calculateScore(
                    driver,
                    pickup,
                    eta
                );
                return new DriverCandidate(driver, eta, score);
            })
            .sorted(Comparator.comparingDouble(DriverCandidate::getScore).reversed())
            .collect(Collectors.toList());
        
        return candidates;
    }
    
    private Optional<Match> attemptMatch(Ride ride, DriverCandidate candidate) {
        Driver driver = candidate.getDriver();
        
        // Pessimistic lock to prevent double assignment
        if (!lockService.tryLock(driver.getId(), MATCH_TIMEOUT)) {
            return Optional.empty();
        }
        
        try {
            // Create pending match
            Match match = Match.builder()
                .id(UUID.randomUUID())
                .ride(ride)
                .driver(driver)
                .eta(candidate.getEta())
                .status(MatchStatus.PENDING)
                .expiresAt(Instant.now().plus(MATCH_TIMEOUT))
                .build();
            
            // Notify driver
            notificationService.notifyDriver(driver, match);
            
            // Wait for response (with timeout)
            MatchResponse response = awaitDriverResponse(match, MATCH_TIMEOUT);
            
            if (response.isAccepted()) {
                match = match.accept();
                ride.setState(RideState.MATCHED);
                driver.setStatus(DriverStatus.ON_RIDE);
                
                rideRepository.update(ride);
                eventPublisher.publish(new RideMatchedEvent(ride, driver));
                
                return Optional.of(match);
            } else {
                match = match.decline(response.getReason());
                eventPublisher.publish(new MatchDeclinedEvent(match));
                return Optional.empty();
            }
        } catch (TimeoutException e) {
            // Driver didn't respond in time
            eventPublisher.publish(new MatchTimeoutEvent(ride, driver));
            return Optional.empty();
        } finally {
            lockService.unlock(driver.getId());
        }
    }
}

// Geospatial Index using QuadTree
public class QuadTreeDriverIndex implements GeospatialIndex<Driver> {
    private final QuadTree<String, Driver> quadTree;
    private final Map<String, Location> driverLocations;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public QuadTreeDriverIndex(BoundingBox worldBounds) {
        this.quadTree = new QuadTree<>(worldBounds, MAX_CAPACITY);
        this.driverLocations = new ConcurrentHashMap<>();
    }
    
    @Override
    public void updateLocation(Driver driver, Location location) {
        lock.writeLock().lock();
        try {
            Location oldLocation = driverLocations.get(driver.getId());
            if (oldLocation != null) {
                quadTree.remove(oldLocation, driver.getId());
            }
            
            quadTree.insert(location, driver.getId(), driver);
            driverLocations.put(driver.getId(), location);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public List<Driver> findWithinRadius(
        Location center,
        double radiusKm,
        DriverStatus status
    ) {
        lock.readLock().lock();
        try {
            BoundingBox searchBox = center.toBoundingBox(radiusKm);
            List<Driver> candidates = quadTree.query(searchBox);
            
            // Filter by actual distance and status
            return candidates.stream()
                .filter(d -> d.getStatus() == status)
                .filter(d -> {
                    Location driverLoc = driverLocations.get(d.getId());
                    double distance = HaversineDistance.calculate(center, driverLoc);
                    return distance <= radiusKm;
                })
                .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
}

// Matching Strategy
public interface MatchingStrategy {
    double calculateScore(Driver driver, Location pickup, Duration eta);
}

public class BalancedMatchingStrategy implements MatchingStrategy {
    private static final double DISTANCE_WEIGHT = 0.5;
    private static final double RATING_WEIGHT = 0.2;
    private static final double ACCEPTANCE_WEIGHT = 0.2;
    private static final double IDLE_TIME_WEIGHT = 0.1;
    
    @Override
    public double calculateScore(Driver driver, Location pickup, Duration eta) {
        // Distance score (closer is better)
        double distanceScore = 1.0 / (1.0 + eta.toMinutes());
        
        // Rating score (normalized 0-1)
        double ratingScore = driver.getRating() / 5.0;
        
        // Acceptance rate score
        double acceptanceScore = driver.getAcceptanceRate();
        
        // Idle time score (prevent driver starvation)
        double idleMinutes = driver.getIdleTime().toMinutes();
        double idleScore = Math.min(1.0, idleMinutes / 30.0);
        
        // Weighted combination
        return DISTANCE_WEIGHT * distanceScore +
               RATING_WEIGHT * ratingScore +
               ACCEPTANCE_WEIGHT * acceptanceScore +
               IDLE_TIME_WEIGHT * idleScore;
    }
}

// ETA Calculator
public interface ETACalculator {
    Duration calculateETA(Location from, Location to);
}

public class TrafficAwareETACalculator implements ETACalculator {
    private final Map<ZonePair, Double> historicalFactors;
    private final TrafficDataProvider trafficProvider;
    private static final double AVG_SPEED_KMH = 30.0;
    
    @Override
    public Duration calculateETA(Location from, Location to) {
        // Baseline: straight-line distance
        double distanceKm = HaversineDistance.calculate(from, to);
        double baseMinutes = (distanceKm / AVG_SPEED_KMH) * 60.0;
        
        // Historical zone adjustment
        ZoneId fromZone = getZone(from);
        ZoneId toZone = getZone(to);
        double historicalFactor = historicalFactors.getOrDefault(
            new ZonePair(fromZone, toZone),
            1.3 // Default: 30% longer than straight line
        );
        
        // Real-time traffic multiplier
        double trafficMultiplier = trafficProvider.getCurrentMultiplier(fromZone);
        
        double finalMinutes = baseMinutes * historicalFactor * trafficMultiplier;
        
        // Clamp to reasonable bounds
        finalMinutes = Math.max(1.0, Math.min(finalMinutes, 120.0));
        
        return Duration.ofMinutes((long) finalMinutes);
    }
}

// Ride State Machine
public class Ride {
    private final UUID id;
    private final Rider rider;
    private final Location pickupLocation;
    private final Location destinationLocation;
    private final RideType rideType;
    private RideState state;
    private Driver assignedDriver;
    private Instant createdAt;
    private Instant matchedAt;
    private Instant startedAt;
    private Instant completedAt;
    
    public void setState(RideState newState) {
        if (!state.canTransitionTo(newState)) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", state, newState)
            );
        }
        this.state = newState;
    }
}

public enum RideState {
    REQUESTED,
    MATCHED,
    DRIVER_ENROUTE,
    PICKED_UP,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    NO_DRIVER_AVAILABLE;
    
    public boolean canTransitionTo(RideState target) {
        return VALID_TRANSITIONS.getOrDefault(this, Set.of()).contains(target);
    }
    
    private static final Map<RideState, Set<RideState>> VALID_TRANSITIONS = Map.of(
        REQUESTED, Set.of(MATCHED, CANCELLED, NO_DRIVER_AVAILABLE),
        MATCHED, Set.of(DRIVER_ENROUTE, CANCELLED),
        DRIVER_ENROUTE, Set.of(PICKED_UP, CANCELLED),
        PICKED_UP, Set.of(IN_PROGRESS, CANCELLED),
        IN_PROGRESS, Set.of(COMPLETED, CANCELLED)
    );
}
```

### 8. Thread Safety & Concurrency

**Locking Strategy:**
- **Pessimistic Locking**: Driver locked during match attempt (prevents double assignment)
- **Distributed Lock**: Redis-based lock for multi-instance deployment
- **Lock Timeout**: 30 seconds max, auto-release on timeout

**Race Condition Prevention:**
```java
// Atomic driver status update
public class Driver {
    private final AtomicReference<DriverStatus> status = 
        new AtomicReference<>(DriverStatus.AVAILABLE);
    
    public boolean tryReserveForRide() {
        return status.compareAndSet(
            DriverStatus.AVAILABLE,
            DriverStatus.ON_RIDE
        );
    }
}
```

**Concurrent Index Updates:**
- QuadTree uses ReadWriteLock
- Write lock for location updates
- Read lock for queries (allows parallel searches)
- ConcurrentHashMap for driver location cache

### 9. Top Interview Questions & Answers

**Q1: Why use Strategy pattern for matching instead of hardcoding?**
**A:**
- **Flexibility**: Switch algorithms at runtime (peak vs normal hours)
- **A/B Testing**: Compare different strategies easily
- **Business Rules**: Different cities may need different strategies
- **Examples**:
  - Normal hours: Nearest driver (optimize rider experience)
  - Peak hours: Balanced (prevent driver starvation)
  - Surge zones: Incentivize drivers to move to high-demand areas

**Q2: How do you prevent the same driver from being assigned to multiple rides simultaneously?**
**A:**
```java
// Pessimistic locking approach
1. Before matching, acquire distributed lock on driver ID
2. Check driver status is AVAILABLE
3. Create match and update driver status to ON_RIDE
4. Release lock
5. If lock acquisition fails, skip to next driver

// CAS approach for single-instance
if (!driver.tryReserveForRide()) {
    continue; // Driver already assigned
}
```

**Q3: What's the time complexity of finding nearby drivers?**
**A:**
- **QuadTree Query**: O(log n + k) where k = results within radius
- **Haversine Distance**: O(k) to filter exact distances
- **Scoring & Sorting**: O(k log k)
- **Overall**: O(log n + k log k)
- **Space**: O(n) for n driver locations
- **Alternative**: Geohash with O(1) bucket lookup but less precise

**Q4: How would you handle surge pricing zones?**
**A:**
```java
class SurgeAwareMatchingStrategy implements MatchingStrategy {
    private final SurgePricingService surgeService;
    
    double calculateScore(Driver driver, Location pickup, Duration eta) {
        // Check if pickup is in surge zone
        Optional<SurgeZone> zone = surgeService.getSurgeZone(pickup);
        
        if (zone.isPresent() && zone.get().getSurgeMultiplier() > 1.5) {
            // Incentivize drivers to move to high-demand areas
            double distanceToZone = calculateDistanceToZone(
                driver.getLocation(),
                zone.get()
            );
            
            // Boost score for drivers near surge zone
            double surgeBonus = 0.3 / (1.0 + distanceToZone);
            return baseScore + surgeBonus;
        }
        
        return baseScore;
    }
}
```

**Q5: How do you test the matching algorithm?**
**A:**
```java
@Test
public void testMatchingPreventsSameDriverDoubleAssignment() {
    // Setup
    Driver driver = createAvailableDriver();
    RideRequest request1 = createRideRequest(location1);
    RideRequest request2 = createRideRequest(location2);
    
    // Execute concurrently
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<Match> match1 = executor.submit(() -> service.matchRide(request1));
    Future<Match> match2 = executor.submit(() -> service.matchRide(request2));
    
    // Verify
    Match result1 = match1.get();
    Match result2 = match2.get();
    
    // Only one should get this driver (or both get different drivers)
    assertNotEquals(result1.getDriver(), result2.getDriver());
}

@Test
public void testETACalculationAccuracy() {
    Location pickup = new Location(37.7749, -122.4194); // SF
    Location driver = new Location(37.7849, -122.4094); // ~1km away
    
    Duration eta = etaCalculator.calculateETA(driver, pickup);
    
    // Should be 2-5 minutes for 1km in city traffic
    assertTrue(eta.toMinutes() >= 2 && eta.toMinutes() <= 5);
}
```

**Q6: What happens if a driver's location update is delayed or lost?**
**A:**
- **Stale Data Detection**: Track last update timestamp
- **Fallback**: Use last known location with staleness indicator
- **Auto-Offline**: Mark driver offline if no update for > 60 seconds
- **Re-sync**: On next update, recalculate position and re-insert into index
```java
public void updateDriverLocation(Driver driver, Location location) {
    driver.setLastUpdateTime(Instant.now());
    driverIndex.updateLocation(driver, location);
    
    // Schedule staleness check
    scheduler.schedule(() -> {
        if (driver.getLastUpdateTime().isBefore(
            Instant.now().minus(60, SECONDS)
        )) {
            driver.setStatus(DriverStatus.OFFLINE);
            eventPublisher.publish(new DriverOfflineEvent(driver));
        }
    }, 60, SECONDS);
}
```

**Q7: How would you implement ride sharing (multiple riders in same car)?**
**A:**
```java
class SharedRideMatchingStrategy implements MatchingStrategy {
    
    Match findSharedRideMatch(RideRequest newRequest) {
        // Step 1: Find active rides heading in similar direction
        List<Ride> activeRides = rideRepository.findActive();
        
        for (Ride existingRide : activeRides) {
            if (existingRide.getRideType() != RideType.SHARED) continue;
            if (existingRide.getPassengerCount() >= MAX_SHARED_PASSENGERS) continue;
            
            // Step 2: Check route compatibility
            boolean compatible = isRouteCompatible(
                existingRide.getRoute(),
                newRequest.getPickupLocation(),
                newRequest.getDestinationLocation()
            );
            
            if (!compatible) continue;
            
            // Step 3: Check detour impact (< 10 minutes extra)
            Duration detour = calculateDetour(existingRide, newRequest);
            if (detour.toMinutes() > 10) continue;
            
            // Step 4: Check existing riders consent (notify & wait)
            if (existingRidersConsent(existingRide)) {
                return createSharedMatch(existingRide, newRequest);
            }
        }
        
        // No compatible shared ride, create new one
        return createNewSharedRide(newRequest);
    }
}
```

**Q8: How do you handle driver cancellations after match?**
**A:**
```java
public void handleDriverCancellation(Match match, CancellationReason reason) {
    // Step 1: Log cancellation and update metrics
    analyticsService.recordCancellation(match.getDriver(), reason);
    
    // Step 2: Penalize driver (reduce acceptance rate)
    match.getDriver().recordCancellation();
    
    // Step 3: Reset ride state
    Ride ride = match.getRide();
    ride.setState(RideState.REQUESTED);
    
    // Step 4: Retry matching (prioritize this request)
    match.getDriver().setStatus(DriverStatus.AVAILABLE);
    
    // Step 5: Add to priority queue for faster retry
    priorityMatchQueue.addFirst(ride);
    
    // Step 6: Notify rider
    notificationService.notifyRider(
        ride.getRider(),
        "Driver cancelled. Finding you another driver..."
    );
}
```

**Q9: What metrics would you track for this system?**
**A:**
```java
Metrics to track:
1. Match Success Rate: % of requests that get matched
2. Average Match Time: P50, P95, P99 time to match
3. Driver Utilization: % time drivers are on rides vs idle
4. Cancellation Rate: % matches cancelled by driver/rider
5. ETA Accuracy: Predicted vs actual pickup time
6. Search Radius Distribution: How often we expand radius
7. Geographic Hotspots: High demand zones
8. Driver Earnings: Revenue per hour per driver
9. Rider Wait Time: Request to pickup duration
10. System Throughput: Matches per second

// Implementation
@Timed(value = "ride.matching.duration", percentiles = {0.5, 0.95, 0.99})
public Match matchRide(RideRequest request) { /* ... */ }

@Counted("ride.matching.success")
@Counted("ride.matching.failure")
```

**Q10: How would you scale this system to handle 1M concurrent rides?**
**A:**
- **Geographic Sharding**: Partition by city/region (reduce search space)
- **Read Replicas**: Separate read (queries) from write (updates)
- **Caching**: Redis for hot driver locations, recent matches
- **Async Processing**: Queue match requests, process in batches
- **Load Balancing**: Round-robin across matching service instances
- **Database**: PostgreSQL with PostGIS for geospatial queries, shard by region
```
Architecture:
┌──────────────────────────────────────────────────┐
│            Load Balancer (Region Router)          │
└─────────┬────────────────┬───────────────┬────────┘
          │                │               │
    ┌─────▼─────┐    ┌────▼──────┐  ┌────▼──────┐
    │  SF Zone  │    │  NYC Zone │  │  LA Zone  │
    │  Matching │    │  Matching │  │  Matching │
    │  Service  │    │  Service  │  │  Service  │
    └────┬──────┘    └────┬──────┘  └────┬──────┘
         │                │               │
    ┌────▼──────┐    ┌───▼───────┐  ┌───▼───────┐
    │  SF Redis │    │  NYC Redis│  │  LA Redis │
    │  Geo Index│    │  Geo Index│  │  Geo Index│
    └───────────┘    └───────────┘  └───────────┘
```

### 10. Extensions & Variations

1. **ML-Based ETA**: Train model on historical data for better accuracy
2. **Route Optimization**: Batch multiple shared rides on same route
3. **Dynamic Pricing**: Adjust fare based on demand/supply ratio
4. **Driver Incentives**: Bonuses for accepting rides in low-supply zones
5. **Rider Preferences**: Filter drivers by language, music, temperature
6. **Safety Features**: Share ride details with emergency contacts
7. **Carbon Credits**: Reward shared rides with eco-friendly drivers

### 11. Testing Strategy

**Unit Tests:**
- MatchingStrategy score calculation with edge cases
- ETACalculator with various distances and traffic conditions
- State machine transitions validation
- Haversine distance accuracy

**Integration Tests:**
- End-to-end matching flow
- Concurrent match requests
- Driver location updates during matching
- Match timeout and retry logic

**Load Tests:**
```java
@LoadTest(users = 10000, duration = "5m")
public void testConcurrentMatching() {
    // Simulate 10K concurrent ride requests
    // Measure: match time, success rate, system throughput
}
```

**Property-Based Tests:**
```java
@Property
public void noDriverDoubleAssignment(@ForAll List<RideRequest> requests) {
    Set<String> assignedDrivers = new HashSet<>();
    
    for (RideRequest req : requests) {
        Match match = service.matchRide(req);
        String driverId = match.getDriver().getId();
        
        // No driver should be assigned twice
        assertFalse(assignedDrivers.contains(driverId));
        assignedDrivers.add(driverId);
    }
}
```

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Global lock for all matches (serializes everything)
✅ **Do**: Per-driver locks (parallel matching)

❌ **Avoid**: Linear search through all drivers
✅ **Do**: Geospatial index with O(log n) queries

❌ **Avoid**: Blocking wait for driver response
✅ **Do**: Async notification with timeout

❌ **Avoid**: Ignoring driver fairness (starvation)
✅ **Do**: Multi-factor scoring with idle time consideration

❌ **Avoid**: Tight coupling to external routing API
✅ **Do**: Adapter pattern with fallback to simple calculation

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Find Nearby Drivers | O(log n + k) | O(1) | QuadTree query |
| Calculate ETA | O(1) | O(1) | Hash map lookup + calculation |
| Match Ranking | O(k log k) | O(k) | Sort k candidates |
| Full Match | O(log n + k log k) | O(k) | Dominated by ranking |
| Location Update | O(log n) | O(1) | QuadTree reinsert |

**Space Complexity:**
- O(D) for D active drivers in index
- O(R) for R active rides
- O(Z) for Z historical zone pairs

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Geospatial Understanding** | 25% | Knows QuadTree/Geohash, Haversine formula, radius search |
| **Concurrency** | 20% | Prevents double assignment, uses appropriate locks |
| **Algorithm Design** | 20% | Multi-factor scoring, ETA calculation, retry logic |
| **Scalability** | 15% | Geographic sharding, caching, load considerations |
| **State Management** | 10% | Clear state transitions, handles edge cases |
| **Real-world Awareness** | 10% | Surge pricing, driver fairness, cancellations |

**Red Flags:**
- No consideration for double assignment
- Linear search through drivers
- No timeout/retry handling
- Ignoring driver fairness
- Over-engineered ML solution without baseline

---
