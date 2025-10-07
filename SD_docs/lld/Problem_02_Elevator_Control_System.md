# Problem 2: Elevator Control System (SCAN/LOOK Scheduling + State Machine)

## LLD Deep Dive: Comprehensive Design

### 1. Problem Clarification
Design an elevator control system that efficiently schedules multiple elevators to serve floor requests, minimizing wait time while preventing starvation.

**Assumptions / Scope:**
- Building with N floors (1 to N) and M elevators
- Two types of requests: Hall calls (outside) and Car calls (inside)
- Hall calls specify direction (UP/DOWN), car calls specify destination
- Each elevator has weight capacity and max passenger count
- Support emergency mode (direct to ground floor)
- Handle door open/close timing
- Scale: 50 floors, 8 elevators, 1000 requests/hour
- Out of scope: Maintenance mode, express elevators, group control algorithms

**Non-Functional Goals:**
- Average wait time < 60 seconds
- No request starvation (max wait < 5 minutes)
- Energy efficient (minimize empty movements)
- Fair allocation across all floors
- Real-time responsiveness (< 100ms request processing)

### 2. Core Requirements

**Functional:**
- Accept hall calls (floor + direction) and car calls (destination)
- Assign optimal elevator to new hall calls
- Schedule stops using efficient algorithm (SCAN/LOOK)
- Handle door open/close with safety timer
- Detect and prevent overload conditions
- Support emergency override
- Display current floor and direction
- Queue multiple destinations per elevator

**Non-Functional:**
- **Performance**: Process request assignment in O(M) time (M = elevators)
- **Availability**: System continues with degraded service if one elevator fails
- **Safety**: Door sensors prevent closure on obstruction
- **Fairness**: Prevent indefinite waiting (starvation)
- **Observability**: Track wait times, trip durations, elevator utilization

### 3. Main Engineering Challenges & Solutions

**Challenge 1: Elevator Scheduling Algorithm (SCAN)**
- **Problem**: Naive FCFS causes excessive direction changes and wait times
- **Solution**: SCAN algorithm (similar to disk scheduling)
- **Algorithm**:
```java
/**
 * SCAN Algorithm (Elevator Algorithm)
 * - Continue in current direction until no more requests
 * - Reverse direction and repeat
 * - Pick up requests along the way
 */
class SCANScheduler {
    
    /**
     * Determine if elevator should stop at floor
     */
    boolean shouldStopAt(Elevator elevator, int floor) {
        Direction dir = elevator.getDirection();
        int currentFloor = elevator.getCurrentFloor();
        
        // Check car calls (always stop if destination)
        if (elevator.hasCarCall(floor)) {
            return true;
        }
        
        // Check hall calls in same direction
        if (dir == Direction.UP) {
            // Stop if someone wants to go up and we're moving up
            if (floor > currentFloor && hasHallCallUp(floor)) {
                return true;
            }
        } else if (dir == Direction.DOWN) {
            // Stop if someone wants to go down and we're moving down
            if (floor < currentFloor && hasHallCallDown(floor)) {
                return true;
            }
        }
        
        // Check if this is the last request in current direction
        if (!hasRequestsBeyond(elevator, floor, dir)) {
            // Reverse direction and pick up opposite requests
            if (hasHallCall(floor)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Find next destination floor using SCAN
     */
    int getNextFloor(Elevator elevator) {
        int currentFloor = elevator.getCurrentFloor();
        Direction dir = elevator.getDirection();
        
        // Get all pending stops
        TreeSet<Integer> upStops = getPendingStops(elevator, Direction.UP);
        TreeSet<Integer> downStops = getPendingStops(elevator, Direction.DOWN);
        
        if (dir == Direction.UP) {
            // Find next stop above current floor
            Integer nextUp = upStops.higher(currentFloor);
            if (nextUp != null) {
                return nextUp;
            }
            // No more up requests, reverse and go down
            elevator.setDirection(Direction.DOWN);
            Integer nextDown = downStops.lower(currentFloor);
            return nextDown != null ? nextDown : currentFloor;
        } else {
            // Find next stop below current floor
            Integer nextDown = downStops.lower(currentFloor);
            if (nextDown != null) {
                return nextDown;
            }
            // No more down requests, reverse and go up
            elevator.setDirection(Direction.UP);
            Integer nextUp = upStops.higher(currentFloor);
            return nextUp != null ? nextUp : currentFloor;
        }
    }
    
    TreeSet<Integer> getPendingStops(Elevator elevator, Direction direction) {
        TreeSet<Integer> stops = new TreeSet<>();
        
        // Add car calls
        stops.addAll(elevator.getCarCalls());
        
        // Add hall calls in specified direction
        for (HallCall call : hallCallQueue) {
            if (call.getDirection() == direction && !call.isAssigned()) {
                stops.add(call.getFloor());
            }
        }
        
        return stops;
    }
}
```

**Challenge 2: Optimal Elevator Assignment**
- **Problem**: Which elevator should serve a new hall call?
- **Solution**: Cost-based assignment with multiple factors
- **Algorithm**:
```java
/**
 * Assign hall call to best elevator using cost function
 */
Elevator assignHallCall(HallCall hallCall) {
    Elevator bestElevator = null;
    double minCost = Double.MAX_VALUE;
    
    for (Elevator elevator : elevators) {
        if (!elevator.isOperational() || elevator.isOverloaded()) {
            continue;
        }
        
        double cost = calculateCost(elevator, hallCall);
        
        if (cost < minCost) {
            minCost = cost;
            bestElevator = elevator;
        }
    }
    
    if (bestElevator != null) {
        bestElevator.addHallCall(hallCall);
        hallCall.setAssigned(true);
    }
    
    return bestElevator;
}

/**
 * Cost function considers:
 * 1. Distance to pickup floor
 * 2. Direction compatibility
 * 3. Current load
 * 4. Number of stops before pickup
 */
double calculateCost(Elevator elevator, HallCall hallCall) {
    int pickupFloor = hallCall.getFloor();
    Direction callDirection = hallCall.getDirection();
    
    // Factor 1: Distance penalty
    int distance = Math.abs(elevator.getCurrentFloor() - pickupFloor);
    double distanceCost = distance * 1.0;
    
    // Factor 2: Direction compatibility
    double directionCost = 0.0;
    Direction elevatorDir = elevator.getDirection();
    
    if (elevatorDir == Direction.IDLE) {
        // Idle elevator is best choice
        directionCost = 0.0;
    } else if (isOnTheWay(elevator, pickupFloor, callDirection)) {
        // Elevator going in same direction and will pass pickup floor
        directionCost = 10.0; // Small penalty
    } else {
        // Elevator needs to reverse or going wrong way
        directionCost = 50.0; // Large penalty
    }
    
    // Factor 3: Load penalty (prefer less loaded elevators)
    double loadFactor = elevator.getCurrentLoad() / (double) elevator.getMaxCapacity();
    double loadCost = loadFactor * 20.0;
    
    // Factor 4: Stops before pickup
    int stopsBeforePickup = countStopsBefore(elevator, pickupFloor);
    double stopsCost = stopsBeforePickup * 5.0;
    
    return distanceCost + directionCost + loadCost + stopsCost;
}

boolean isOnTheWay(Elevator elevator, int pickupFloor, Direction callDirection) {
    int currentFloor = elevator.getCurrentFloor();
    Direction elevatorDir = elevator.getDirection();
    
    if (elevatorDir == Direction.UP && callDirection == Direction.UP) {
        return pickupFloor > currentFloor;
    } else if (elevatorDir == Direction.DOWN && callDirection == Direction.DOWN) {
        return pickupFloor < currentFloor;
    }
    
    return false;
}
```

**Challenge 3: State Machine for Elevator Lifecycle**
- **Problem**: Complex state transitions (moving, stopped, door opening, etc.)
- **Solution**: Explicit state machine with validated transitions
- **Algorithm**:
```java
/**
 * Elevator State Machine
 * States: IDLE, MOVING_UP, MOVING_DOWN, DOOR_OPENING, DOOR_OPEN, DOOR_CLOSING
 */
class ElevatorStateMachine {
    private ElevatorState currentState;
    private final Elevator elevator;
    
    // Valid state transitions
    private static final Map<ElevatorState, Set<ElevatorState>> TRANSITIONS = Map.of(
        IDLE, Set.of(MOVING_UP, MOVING_DOWN, DOOR_OPENING),
        MOVING_UP, Set.of(DOOR_OPENING, MOVING_UP),
        MOVING_DOWN, Set.of(DOOR_OPENING, MOVING_DOWN),
        DOOR_OPENING, Set.of(DOOR_OPEN),
        DOOR_OPEN, Set.of(DOOR_CLOSING),
        DOOR_CLOSING, Set.of(DOOR_OPEN, IDLE, MOVING_UP, MOVING_DOWN)
    );
    
    void transitionTo(ElevatorState newState) {
        if (!TRANSITIONS.get(currentState).contains(newState)) {
            throw new IllegalStateTransitionException(
                String.format("Cannot transition from %s to %s", currentState, newState)
            );
        }
        
        ElevatorState previousState = currentState;
        currentState = newState;
        
        // State entry actions
        onStateEntry(newState, previousState);
    }
    
    void onStateEntry(ElevatorState newState, ElevatorState previousState) {
        switch (newState) {
            case MOVING_UP:
                elevator.setDirection(Direction.UP);
                startMovementTimer();
                break;
                
            case MOVING_DOWN:
                elevator.setDirection(Direction.DOWN);
                startMovementTimer();
                break;
                
            case DOOR_OPENING:
                openDoors();
                scheduleDoorOpenComplete(3000); // 3 seconds
                break;
                
            case DOOR_OPEN:
                startDoorTimer(10000); // Keep open 10 seconds
                break;
                
            case DOOR_CLOSING:
                closeDoors();
                scheduleDoorCloseComplete(3000);
                break;
                
            case IDLE:
                elevator.setDirection(Direction.IDLE);
                clearAllTimers();
                break;
        }
        
        publishStateChange(previousState, newState);
    }
    
    /**
     * Main control loop
     */
    void processStep() {
        switch (currentState) {
            case MOVING_UP:
            case MOVING_DOWN:
                handleMovement();
                break;
                
            case DOOR_OPEN:
                // Check if timeout or all passengers boarded
                if (doorTimerExpired() || shouldCloseDoor()) {
                    transitionTo(DOOR_CLOSING);
                }
                break;
                
            case DOOR_CLOSING:
                // Check for obstruction
                if (doorObstructed()) {
                    transitionTo(DOOR_OPENING);
                }
                break;
                
            case IDLE:
                // Check for new requests
                if (hasPendingRequests()) {
                    Direction nextDir = determineNextDirection();
                    transitionTo(nextDir == Direction.UP ? MOVING_UP : MOVING_DOWN);
                }
                break;
        }
    }
    
    void handleMovement() {
        if (hasReachedNextFloor()) {
            elevator.setCurrentFloor(elevator.getNextFloor());
            
            if (shouldStopAtCurrentFloor()) {
                transitionTo(DOOR_OPENING);
                removeStopRequest(elevator.getCurrentFloor());
            } else {
                // Continue moving
                continueInCurrentDirection();
            }
        }
    }
}
```

**Challenge 4: Preventing Starvation**
- **Problem**: Far floors or opposite direction calls may wait indefinitely
- **Solution**: Priority aging and periodic idle elevator dispatch
- **Algorithm**:
```java
/**
 * Anti-starvation mechanism using priority aging
 */
class StarvationPrevention {
    private static final long MAX_WAIT_TIME_MS = 5 * 60 * 1000; // 5 minutes
    
    /**
     * Increase priority of old requests
     */
    void ageRequests() {
        Instant now = Instant.now();
        
        for (HallCall call : hallCallQueue) {
            long waitTime = Duration.between(call.getTimestamp(), now).toMillis();
            
            // Exponential priority increase
            if (waitTime > MAX_WAIT_TIME_MS) {
                // Force assign to nearest elevator
                forceAssignment(call);
            } else if (waitTime > MAX_WAIT_TIME_MS / 2) {
                // Increase priority significantly
                call.setPriority(call.getPriority() + 100);
            } else if (waitTime > MAX_WAIT_TIME_MS / 4) {
                // Moderate priority boost
                call.setPriority(call.getPriority() + 50);
            }
        }
    }
    
    void forceAssignment(HallCall call) {
        // Find idle or least busy elevator
        Elevator leastBusy = elevators.stream()
            .filter(Elevator::isOperational)
            .min(Comparator.comparingInt(e -> e.getPendingStops().size()))
            .orElseThrow();
        
        leastBusy.addHallCall(call);
        call.setAssigned(true);
        
        logger.warn("Force assigned starving request: floor={}, waitTime={}s",
                   call.getFloor(), 
                   Duration.between(call.getTimestamp(), Instant.now()).getSeconds());
    }
}
```

### 4. Design Patterns & Justification

| Pattern | Usage | Why It Fits |
|---------|-------|-------------|
| **State** | Elevator lifecycle (IDLE, MOVING, DOOR_OPEN, etc.) | Clear state transitions with validation |
| **Strategy** | Scheduling algorithms (SCAN, LOOK, SSTF) | Swap algorithms based on building profile |
| **Observer** | Floor displays, event notifications | Decouple elevator events from UI updates |
| **Command** | Floor button presses, car calls | Encapsulate requests for queuing/logging |
| **Singleton** | ElevatorController (central dispatcher) | Single point of control for all elevators |
| **Template Method** | Request processing flow | Common steps with algorithm variants |
| **Factory** | Create different elevator types | Instantiate based on capacity/speed |

### 5. Domain Model & Class Structure

```
┌────────────────────────┐
│  ElevatorController    │ (Central Coordinator)
│   - elevators[]        │
│   - hallCallQueue      │
│   - scheduler          │
│   - assignmentStrategy │
└────────┬───────────────┘
         │ manages
         │
    ┌────┴──────────────┬─────────────────┐
    ▼                   ▼                 ▼
┌──────────┐     ┌─────────────┐   ┌──────────────┐
│ Elevator │     │  Scheduler  │   │  HallCall    │
│ (Entity) │     │  (Strategy) │   │  (Value Obj) │
└────┬─────┘     └─────────────┘   └──────────────┘
     │ has                ▲
     │                    │
     ▼              ┌─────┴──────┬──────────┐
┌──────────────┐   │            │          │
│ StateMachine │   │ SCAN    LOOK   SSTF  │
│              │   └────────────────────────┘
└──────────────┘
     │ uses
     ▼
┌──────────────┐
│ ElevatorState│
│    (Enum)    │
└──────────────┘
```

### 6. Detailed Sequence Diagrams

**Sequence: Handle Hall Call Request**
```
User    Button   Controller   Scheduler   Elevator1  Elevator2
 │        │          │            │           │          │
 ├─press─>│          │            │           │          │
 │        ├─request─>│            │           │          │
 │        │          ├─assign────>│           │          │
 │        │          │            ├─calcCost─>│          │
 │        │          │            │<─cost=50──┤          │
 │        │          │            ├─calcCost──────────>│
 │        │          │            │<─cost=30────────────┤
 │        │          │<─Elevator2─┤           │          │
 │        │          ├─addHallCall──────────────────>│
 │        │          │            │           │        │
 │        │          │            │           │    [recalculate route]
 │        │<─confirm─┤            │           │          │
 │<─light─┤          │            │           │          │
```

**Sequence: Elevator Movement with Stop**
```
Elevator  StateMachine  Scheduler  Door  FloorDisplay
   │           │            │        │         │
   │ [MOVING_UP]           │        │         │
   ├─reachFloor─>│          │        │         │
   │           ├─shouldStop?>│        │         │
   │           │<─YES───────┤        │         │
   │           ├─transition─>│        │         │
   │           │  (DOOR_OPENING)     │         │
   │           ├─openDoors──────────>│         │
   │           ├─notify──────────────────────>│
   │           │            │      [open]   [display]
   │           ├─transition─>│        │         │
   │           │  (DOOR_OPEN) │        │         │
   │           │<─timeout(10s)│        │         │
   │           ├─transition─>│        │         │
   │           │  (DOOR_CLOSING)     │         │
   │           ├─closeDoors─────────>│         │
   │           │            │      [close]      │
   │           ├─transition─>│        │         │
   │           │  (MOVING_UP) │        │         │
```

### 7. Core Implementation (Interview-Critical Methods)

```java
// ============================================
// DOMAIN ENTITIES
// ============================================

public class Elevator {
    private final UUID id;
    private int currentFloor;
    private Direction direction;
    private ElevatorState state;
    private final int maxCapacity;
    private int currentLoad;
    
    // Pending stops
    private final TreeSet<Integer> carCalls = new TreeSet<>();
    private final Set<HallCall> assignedHallCalls = new HashSet<>();
    
    private final ElevatorStateMachine stateMachine;
    
    // Door management
    private boolean doorOpen;
    private Instant doorOpenedAt;
    
    private boolean operational = true;
    
    public Elevator(UUID id, int maxCapacity) {
        this.id = id;
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.IDLE;
        this.maxCapacity = maxCapacity;
        this.stateMachine = new ElevatorStateMachine(this);
    }
    
    public void addCarCall(int floor) {
        if (floor < 1 || floor > MAX_FLOORS) {
            throw new IllegalArgumentException("Invalid floor: " + floor);
        }
        carCalls.add(floor);
    }
    
    public void addHallCall(HallCall call) {
        assignedHallCalls.add(call);
    }
    
    public void removeStop(int floor) {
        carCalls.remove(floor);
        assignedHallCalls.removeIf(call -> call.getFloor() == floor);
    }
    
    public boolean hasCarCall(int floor) {
        return carCalls.contains(floor);
    }
    
    public boolean hasPendingRequests() {
        return !carCalls.isEmpty() || !assignedHallCalls.isEmpty();
    }
    
    public TreeSet<Integer> getPendingStops(Direction dir) {
        TreeSet<Integer> stops = new TreeSet<>();
        
        // Add all car calls
        stops.addAll(carCalls);
        
        // Add hall calls matching direction
        for (HallCall call : assignedHallCalls) {
            if (call.getDirection() == dir || dir == Direction.IDLE) {
                stops.add(call.getFloor());
            }
        }
        
        return stops;
    }
    
    public boolean isOverloaded() {
        return currentLoad >= maxCapacity;
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public int getCurrentFloor() { return currentFloor; }
    public void setCurrentFloor(int floor) { this.currentFloor = floor; }
    public Direction getDirection() { return direction; }
    public void setDirection(Direction direction) { this.direction = direction; }
    public ElevatorState getState() { return state; }
    public void setState(ElevatorState state) { this.state = state; }
    public int getMaxCapacity() { return maxCapacity; }
    public int getCurrentLoad() { return currentLoad; }
    public void setCurrentLoad(int load) { this.currentLoad = load; }
    public boolean isOperational() { return operational; }
    public boolean isDoorOpen() { return doorOpen; }
    public void setDoorOpen(boolean open) { 
        this.doorOpen = open;
        if (open) {
            this.doorOpenedAt = Instant.now();
        }
    }
}

public class HallCall {
    private final UUID id;
    private final int floor;
    private final Direction direction;
    private final Instant timestamp;
    private boolean assigned;
    private int priority;
    
    public HallCall(int floor, Direction direction) {
        this.id = UUID.randomUUID();
        this.floor = floor;
        this.direction = direction;
        this.timestamp = Instant.now();
        this.assigned = false;
        this.priority = 0;
    }
    
    public long getWaitTimeMillis() {
        return Duration.between(timestamp, Instant.now()).toMillis();
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public int getFloor() { return floor; }
    public Direction getDirection() { return direction; }
    public Instant getTimestamp() { return timestamp; }
    public boolean isAssigned() { return assigned; }
    public void setAssigned(boolean assigned) { this.assigned = assigned; }
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
}

public enum Direction {
    UP, DOWN, IDLE
}

public enum ElevatorState {
    IDLE,
    MOVING_UP,
    MOVING_DOWN,
    DOOR_OPENING,
    DOOR_OPEN,
    DOOR_CLOSING
}

// ============================================
// STATE MACHINE
// ============================================

public class ElevatorStateMachine {
    private final Elevator elevator;
    private final Map<ElevatorState, Set<ElevatorState>> validTransitions;
    
    public ElevatorStateMachine(Elevator elevator) {
        this.elevator = elevator;
        this.validTransitions = initializeTransitions();
    }
    
    private Map<ElevatorState, Set<ElevatorState>> initializeTransitions() {
        Map<ElevatorState, Set<ElevatorState>> transitions = new HashMap<>();
        
        transitions.put(ElevatorState.IDLE, 
            Set.of(ElevatorState.MOVING_UP, ElevatorState.MOVING_DOWN, ElevatorState.DOOR_OPENING));
        transitions.put(ElevatorState.MOVING_UP, 
            Set.of(ElevatorState.MOVING_UP, ElevatorState.DOOR_OPENING));
        transitions.put(ElevatorState.MOVING_DOWN, 
            Set.of(ElevatorState.MOVING_DOWN, ElevatorState.DOOR_OPENING));
        transitions.put(ElevatorState.DOOR_OPENING, 
            Set.of(ElevatorState.DOOR_OPEN));
        transitions.put(ElevatorState.DOOR_OPEN, 
            Set.of(ElevatorState.DOOR_CLOSING));
        transitions.put(ElevatorState.DOOR_CLOSING, 
            Set.of(ElevatorState.DOOR_OPEN, ElevatorState.IDLE, 
                   ElevatorState.MOVING_UP, ElevatorState.MOVING_DOWN));
        
        return transitions;
    }
    
    public void transitionTo(ElevatorState newState) {
        ElevatorState currentState = elevator.getState();
        
        if (!validTransitions.get(currentState).contains(newState)) {
            throw new IllegalStateTransitionException(
                String.format("Invalid transition from %s to %s for elevator %s",
                            currentState, newState, elevator.getId())
            );
        }
        
        elevator.setState(newState);
        onStateEntry(newState, currentState);
    }
    
    private void onStateEntry(ElevatorState newState, ElevatorState previousState) {
        switch (newState) {
            case MOVING_UP:
                elevator.setDirection(Direction.UP);
                break;
                
            case MOVING_DOWN:
                elevator.setDirection(Direction.DOWN);
                break;
                
            case DOOR_OPENING:
                elevator.setDoorOpen(true);
                // Schedule transition to DOOR_OPEN after 3 seconds
                scheduleTransition(ElevatorState.DOOR_OPEN, 3000);
                break;
                
            case DOOR_OPEN:
                // Keep door open for 10 seconds or until manual close
                break;
                
            case DOOR_CLOSING:
                elevator.setDoorOpen(false);
                // Schedule transition to next state after 3 seconds
                scheduleNextMove();
                break;
                
            case IDLE:
                elevator.setDirection(Direction.IDLE);
                break;
        }
    }
    
    private void scheduleTransition(ElevatorState targetState, long delayMs) {
        // Implementation would use ScheduledExecutorService
        // Simplified for clarity
    }
    
    private void scheduleNextMove() {
        if (elevator.hasPendingRequests()) {
            Direction nextDir = determineNextDirection();
            if (nextDir == Direction.UP) {
                transitionTo(ElevatorState.MOVING_UP);
            } else if (nextDir == Direction.DOWN) {
                transitionTo(ElevatorState.MOVING_DOWN);
            } else {
                transitionTo(ElevatorState.IDLE);
            }
        } else {
            transitionTo(ElevatorState.IDLE);
        }
    }
    
    private Direction determineNextDirection() {
        int currentFloor = elevator.getCurrentFloor();
        TreeSet<Integer> upStops = elevator.getPendingStops(Direction.UP);
        TreeSet<Integer> downStops = elevator.getPendingStops(Direction.DOWN);
        
        boolean hasUp = upStops.stream().anyMatch(f -> f > currentFloor);
        boolean hasDown = downStops.stream().anyMatch(f -> f < currentFloor);
        
        if (hasUp && !hasDown) return Direction.UP;
        if (hasDown && !hasUp) return Direction.DOWN;
        if (hasUp) return Direction.UP; // Prefer up if both exist
        
        return Direction.IDLE;
    }
}

// ============================================
// SCHEDULER (SCAN Algorithm)
// ============================================

public class SCANScheduler {
    private static final int MAX_FLOORS = 50;
    
    /**
     * INTERVIEW CRITICAL: Determine next floor using SCAN algorithm
     */
    public Integer getNextFloor(Elevator elevator) {
        int currentFloor = elevator.getCurrentFloor();
        Direction direction = elevator.getDirection();
        
        TreeSet<Integer> allStops = elevator.getPendingStops(direction);
        
        if (allStops.isEmpty()) {
            return null;
        }
        
        if (direction == Direction.UP || direction == Direction.IDLE) {
            // Find next stop above current floor
            Integer nextUp = allStops.higher(currentFloor);
            if (nextUp != null) {
                return nextUp;
            }
            // No more up requests, reverse direction
            Integer nextDown = allStops.lower(currentFloor);
            if (nextDown != null) {
                elevator.setDirection(Direction.DOWN);
                return nextDown;
            }
        } else { // Direction.DOWN
            // Find next stop below current floor
            Integer nextDown = allStops.lower(currentFloor);
            if (nextDown != null) {
                return nextDown;
            }
            // No more down requests, reverse direction
            Integer nextUp = allStops.higher(currentFloor);
            if (nextUp != null) {
                elevator.setDirection(Direction.UP);
                return nextUp;
            }
        }
        
        // Single stop at current floor
        return allStops.first();
    }
    
    /**
     * INTERVIEW CRITICAL: Determine if elevator should stop at floor
     */
    public boolean shouldStopAt(Elevator elevator, int floor) {
        int currentFloor = elevator.getCurrentFloor();
        Direction direction = elevator.getDirection();
        
        // Always stop if car call
        if (elevator.hasCarCall(floor)) {
            return true;
        }
        
        // Check hall calls in compatible direction
        for (HallCall call : elevator.getAssignedHallCalls()) {
            if (call.getFloor() != floor) continue;
            
            if (direction == Direction.UP && call.getDirection() == Direction.UP && floor > currentFloor) {
                return true;
            }
            if (direction == Direction.DOWN && call.getDirection() == Direction.DOWN && floor < currentFloor) {
                return true;
            }
        }
        
        // Check if this is the last request in current direction
        TreeSet<Integer> stops = elevator.getPendingStops(direction);
        
        if (direction == Direction.UP) {
            boolean noMoreUp = stops.stream().noneMatch(f -> f > floor);
            if (noMoreUp) {
                // This is the last stop going up, pick up any hall call here
                return stops.contains(floor);
            }
        } else if (direction == Direction.DOWN) {
            boolean noMoreDown = stops.stream().noneMatch(f -> f < floor);
            if (noMoreDown) {
                return stops.contains(floor);
            }
        }
        
        return false;
    }
}

// ============================================
// ASSIGNMENT STRATEGY
// ============================================

public class ElevatorAssignmentStrategy {
    
    /**
     * INTERVIEW CRITICAL: Assign hall call to optimal elevator
     */
    public Elevator assignHallCall(List<Elevator> elevators, HallCall hallCall) {
        Elevator bestElevator = null;
        double minCost = Double.MAX_VALUE;
        
        for (Elevator elevator : elevators) {
            if (!elevator.isOperational() || elevator.isOverloaded()) {
                continue;
            }
            
            double cost = calculateCost(elevator, hallCall);
            
            // Apply priority boost for starving requests
            cost -= hallCall.getPriority();
            
            if (cost < minCost) {
                minCost = cost;
                bestElevator = elevator;
            }
        }
        
        return bestElevator;
    }
    
    /**
     * INTERVIEW CRITICAL: Cost function for elevator assignment
     */
    private double calculateCost(Elevator elevator, HallCall hallCall) {
        int pickupFloor = hallCall.getFloor();
        Direction callDirection = hallCall.getDirection();
        int currentFloor = elevator.getCurrentFloor();
        Direction elevatorDir = elevator.getDirection();
        
        // Factor 1: Distance
        int distance = Math.abs(currentFloor - pickupFloor);
        double distanceCost = distance * 1.0;
        
        // Factor 2: Direction compatibility
        double directionCost = 0.0;
        
        if (elevatorDir == Direction.IDLE) {
            // Idle elevator is best
            directionCost = 0.0;
        } else if (isOnTheWay(elevator, pickupFloor, callDirection)) {
            // Going same direction and will pass floor
            directionCost = 10.0;
        } else {
            // Wrong direction or needs to reverse
            directionCost = 50.0;
        }
        
        // Factor 3: Current load
        double loadFactor = (double) elevator.getCurrentLoad() / elevator.getMaxCapacity();
        double loadCost = loadFactor * 20.0;
        
        // Factor 4: Number of stops before pickup
        int stopsBefore = countStopsBefore(elevator, pickupFloor);
        double stopsCost = stopsBefore * 5.0;
        
        return distanceCost + directionCost + loadCost + stopsCost;
    }
    
    private boolean isOnTheWay(Elevator elevator, int pickupFloor, Direction callDirection) {
        int currentFloor = elevator.getCurrentFloor();
        Direction elevatorDir = elevator.getDirection();
        
        if (elevatorDir == Direction.UP && callDirection == Direction.UP) {
            return pickupFloor > currentFloor;
        } else if (elevatorDir == Direction.DOWN && callDirection == Direction.DOWN) {
            return pickupFloor < currentFloor;
        }
        
        return false;
    }
    
    private int countStopsBefore(Elevator elevator, int targetFloor) {
        int currentFloor = elevator.getCurrentFloor();
        Direction direction = elevator.getDirection();
        
        TreeSet<Integer> stops = elevator.getPendingStops(direction);
        
        if (direction == Direction.UP) {
            return (int) stops.stream()
                .filter(f -> f > currentFloor && f < targetFloor)
                .count();
        } else if (direction == Direction.DOWN) {
            return (int) stops.stream()
                .filter(f -> f < currentFloor && f > targetFloor)
                .count();
        }
        
        return 0;
    }
}

// ============================================
// MAIN CONTROLLER
// ============================================

public class ElevatorController {
    private final List<Elevator> elevators;
    private final Queue<HallCall> hallCallQueue = new ConcurrentLinkedQueue<>();
    private final SCANScheduler scheduler = new SCANScheduler();
    private final ElevatorAssignmentStrategy assignmentStrategy = new ElevatorAssignmentStrategy();
    private final ScheduledExecutorService executorService;
    
    public ElevatorController(int numElevators, int elevatorCapacity) {
        this.elevators = new ArrayList<>();
        for (int i = 0; i < numElevators; i++) {
            elevators.add(new Elevator(UUID.randomUUID(), elevatorCapacity));
        }
        
        this.executorService = Executors.newScheduledThreadPool(numElevators + 1);
        
        // Start control loops
        startControlLoops();
        startStarvationPrevention();
    }
    
    /**
     * INTERVIEW CRITICAL: Handle hall call request
     */
    public void requestHallCall(int floor, Direction direction) {
        if (floor < 1 || floor > 50) {
            throw new IllegalArgumentException("Invalid floor: " + floor);
        }
        
        HallCall hallCall = new HallCall(floor, direction);
        hallCallQueue.offer(hallCall);
        
        // Try immediate assignment
        processHallCallQueue();
    }
    
    /**
     * INTERVIEW CRITICAL: Handle car call (button press inside elevator)
     */
    public void requestCarCall(UUID elevatorId, int floor) {
        Elevator elevator = findElevator(elevatorId);
        if (elevator == null) {
            throw new IllegalArgumentException("Elevator not found: " + elevatorId);
        }
        
        elevator.addCarCall(floor);
    }
    
    /**
     * Process pending hall calls
     */
    private void processHallCallQueue() {
        Iterator<HallCall> iterator = hallCallQueue.iterator();
        
        while (iterator.hasNext()) {
            HallCall call = iterator.next();
            
            if (call.isAssigned()) {
                iterator.remove();
                continue;
            }
            
            Elevator assigned = assignmentStrategy.assignHallCall(elevators, call);
            if (assigned != null) {
                assigned.addHallCall(call);
                call.setAssigned(true);
                iterator.remove();
            }
        }
    }
    
    /**
     * INTERVIEW CRITICAL: Main control loop for each elevator
     */
    private void startControlLoops() {
        for (Elevator elevator : elevators) {
            executorService.scheduleAtFixedRate(
                () -> processElevator(elevator),
                0, 100, TimeUnit.MILLISECONDS
            );
        }
    }
    
    private void processElevator(Elevator elevator) {
        try {
            ElevatorState state = elevator.getState();
            
            switch (state) {
                case IDLE:
                    handleIdleState(elevator);
                    break;
                    
                case MOVING_UP:
                case MOVING_DOWN:
                    handleMovingState(elevator);
                    break;
                    
                case DOOR_OPEN:
                    handleDoorOpenState(elevator);
                    break;
                    
                // DOOR_OPENING and DOOR_CLOSING handled by timers
            }
        } catch (Exception e) {
            // Log error, mark elevator as non-operational if critical
            handleElevatorError(elevator, e);
        }
    }
    
    private void handleIdleState(Elevator elevator) {
        if (elevator.hasPendingRequests()) {
            Integer nextFloor = scheduler.getNextFloor(elevator);
            if (nextFloor != null) {
                Direction direction = nextFloor > elevator.getCurrentFloor() 
                    ? Direction.UP : Direction.DOWN;
                
                if (direction == Direction.UP) {
                    elevator.getStateMachine().transitionTo(ElevatorState.MOVING_UP);
                } else {
                    elevator.getStateMachine().transitionTo(ElevatorState.MOVING_DOWN);
                }
            }
        }
    }
    
    private void handleMovingState(Elevator elevator) {
        Integer nextFloor = scheduler.getNextFloor(elevator);
        
        if (nextFloor == null) {
            // No more requests
            elevator.getStateMachine().transitionTo(ElevatorState.IDLE);
            return;
        }
        
        // Simulate movement (in real system, this would be motor control)
        int currentFloor = elevator.getCurrentFloor();
        Direction direction = elevator.getDirection();
        
        if (direction == Direction.UP && currentFloor < nextFloor) {
            elevator.setCurrentFloor(currentFloor + 1);
        } else if (direction == Direction.DOWN && currentFloor > nextFloor) {
            elevator.setCurrentFloor(currentFloor - 1);
        }
        
        // Check if should stop at current floor
        if (scheduler.shouldStopAt(elevator, elevator.getCurrentFloor())) {
            elevator.getStateMachine().transitionTo(ElevatorState.DOOR_OPENING);
            elevator.removeStop(elevator.getCurrentFloor());
        }
    }
    
    private void handleDoorOpenState(Elevator elevator) {
        // Check if door has been open long enough
        if (elevator.isDoorOpen()) {
            Instant openedAt = elevator.getDoorOpenedAt();
            long openDuration = Duration.between(openedAt, Instant.now()).toMillis();
            
            if (openDuration > 10000) { // 10 seconds
                elevator.getStateMachine().transitionTo(ElevatorState.DOOR_CLOSING);
            }
        }
    }
    
    /**
     * Anti-starvation mechanism
     */
    private void startStarvationPrevention() {
        executorService.scheduleAtFixedRate(
            this::preventStarvation,
            30, 30, TimeUnit.SECONDS
        );
    }
    
    private void preventStarvation() {
        Instant now = Instant.now();
        long maxWaitMs = 5 * 60 * 1000; // 5 minutes
        
        for (HallCall call : hallCallQueue) {
            long waitTime = Duration.between(call.getTimestamp(), now).toMillis();
            
            if (waitTime > maxWaitMs) {
                // Force assignment
                Elevator leastBusy = elevators.stream()
                    .filter(Elevator::isOperational)
                    .min(Comparator.comparingInt(e -> e.getPendingStops(Direction.IDLE).size()))
                    .orElse(null);
                
                if (leastBusy != null) {
                    leastBusy.addHallCall(call);
                    call.setAssigned(true);
                }
            } else if (waitTime > maxWaitMs / 2) {
                // Increase priority
                call.setPriority(call.getPriority() + 50);
            }
        }
    }
    
    private void handleElevatorError(Elevator elevator, Exception e) {
        // Log and potentially mark as non-operational
        System.err.println("Elevator error: " + elevator.getId() + " - " + e.getMessage());
    }
    
    private Elevator findElevator(UUID elevatorId) {
        return elevators.stream()
            .filter(e -> e.getId().equals(elevatorId))
            .findFirst()
            .orElse(null);
    }
    
    public void shutdown() {
        executorService.shutdown();
    }
}
```

### 8. Thread Safety & Concurrency

**Control Loop:**
- Each elevator has dedicated thread (isolated state)
- ConcurrentLinkedQueue for hall calls (thread-safe)
- No shared mutable state between elevators

**State Transitions:**
- Synchronized within each elevator's state machine
- Atomic state updates
- No deadlocks (single elevator lock granularity)

**Assignment:**
- Read-only operations on elevator state
- No blocking locks during cost calculation
- Queue modifications synchronized

### 9. Top Interview Questions & Answers

**Q1: Why SCAN over FCFS?**
**A:**
| Metric | FCFS | SCAN |
|--------|------|------|
| Avg wait time | High (lots of direction changes) | Low (batches requests) |
| Starvation risk | Low | Medium (requires aging) |
| Energy efficiency | Poor | Good |
| Predictability | High | Medium |

**Q2: How do you prevent elevator deadlock?**
**A:**
- Single resource per elevator (no cross-elevator locks)
- Timeout on state transitions
- Watchdog thread detects stuck elevators
- Emergency mode overrides all requests

**Q3: What if multiple elevators reach same hall call floor?**
**A:**
```java
// First elevator to transition to DOOR_OPENING claims the call
synchronized (hallCall) {
    if (!hallCall.isServed()) {
        hallCall.setServed(true);
        elevator.addHallCall(hallCall);
    } else {
        // Another elevator already serving, skip
        return;
    }
}
```

**Q4: How to handle peak hours (morning rush)?**
**A:**
```java
// Group Control: Assign sectors to elevators
if (peakHourDetected()) {
    // Elevators 1-3 serve floors 1-20
    // Elevators 4-6 serve floors 21-40
    // Elevator 7-8 express to top floors
}

// Or: Use destination dispatch (passengers enter floor before boarding)
```

**Q5: What metrics to monitor?**
**A:**
```java
Metrics:
1. Average wait time per floor
2. Average trip duration
3. Elevator utilization (% time moving vs idle)
4. Max wait time (detect starvation)
5. Energy consumption (direction changes)
6. Door cycle count (maintenance predictor)

Alerts:
- Wait time > 5 min → Starvation
- Utilization > 90% → Add elevator capacity
- Frequent direction changes → Review algorithm
```

**Q6: How to test the system?**
**A:**
```java
@Test
public void testConcurrentRequests() {
    ElevatorController controller = new ElevatorController(4, 10);
    
    // Simulate 100 random hall calls
    ExecutorService executor = Executors.newFixedThreadPool(10);
    CountDownLatch latch = new CountDownLatch(100);
    
    for (int i = 0; i < 100; i++) {
        executor.submit(() -> {
            int floor = random.nextInt(50) + 1;
            Direction dir = random.nextBoolean() ? Direction.UP : Direction.DOWN;
            controller.requestHallCall(floor, dir);
            latch.countDown();
        });
    }
    
    latch.await(60, TimeUnit.SECONDS);
    
    // Verify: All requests eventually served
    // No elevator stuck in invalid state
    // No starvation (max wait < 5 min)
}
```

**Q7: How to handle emergency mode?**
**A:**
```java
public void activateEmergency(UUID elevatorId) {
    Elevator elevator = findElevator(elevatorId);
    
    // Clear all pending requests
    elevator.clearAllCalls();
    
    // Force transition to moving down
    elevator.setState(ElevatorState.MOVING_DOWN);
    elevator.setDirection(Direction.DOWN);
    
    // Add ground floor as only destination
    elevator.addCarCall(1);
    
    // Disable new hall call assignments
    elevator.setEmergencyMode(true);
}
```

**Q8: What's the database schema?**
**A:**
```sql
-- Real-time state (in-memory, optional persistence)
CREATE TABLE elevators (
    elevator_id UUID PRIMARY KEY,
    current_floor INT NOT NULL,
    direction VARCHAR(10) NOT NULL,
    state VARCHAR(20) NOT NULL,
    current_load INT NOT NULL,
    max_capacity INT NOT NULL,
    operational BOOLEAN NOT NULL,
    last_updated TIMESTAMP NOT NULL
);

-- Audit log
CREATE TABLE elevator_events (
    event_id UUID PRIMARY KEY,
    elevator_id UUID NOT NULL,
    event_type VARCHAR(50) NOT NULL, -- FLOOR_REACHED, DOOR_OPENED, etc.
    floor INT,
    timestamp TIMESTAMP NOT NULL,
    metadata JSONB
);

CREATE INDEX idx_events_elevator_time 
    ON elevator_events(elevator_id, timestamp DESC);

-- Request tracking (for analytics)
CREATE TABLE hall_calls_log (
    call_id UUID PRIMARY KEY,
    floor INT NOT NULL,
    direction VARCHAR(10) NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    assigned_at TIMESTAMP,
    served_at TIMESTAMP,
    elevator_id UUID,
    wait_time_ms INT
);

CREATE INDEX idx_hall_calls_time 
    ON hall_calls_log(requested_at DESC);
```

**Q9: How to scale to 100+ floors?**
**A:**
```
Express Elevators:
- Local elevators: Floors 1-30
- Mid-range: Floors 30-60
- Express: Direct to 60+, limited stops

Destination Dispatch:
- Passengers enter destination before boarding
- System groups passengers with similar destinations
- Reduces stops per trip

Sky Lobbies:
- Transfer floors at intervals (e.g., 30, 60)
- Reduce load on ground floor elevators
```

**Q10: Alternative scheduling algorithms?**
**A:**
```java
// LOOK (simplified SCAN, doesn't go to extremes)
// Only travels to highest/lowest request, not building limits

// SSTF (Shortest Seek Time First)
// Always serve closest request
// Risk: Starvation of far floors

// Destination Dispatch
// Pre-assign elevator based on destination
// Optimal for high-rise buildings
```

### 10. Extensions & Variations

1. **Group Control**: Coordinate multiple elevators for zone coverage
2. **Destination Dispatch**: Passengers enter destination at hall call
3. **Energy Optimization**: Sleep idle elevators, predictive positioning
4. **VIP Mode**: Priority service for certain floors/users
5. **Adaptive Learning**: ML to predict traffic patterns

### 11. Testing Strategy

**Unit Tests:**
- State machine transitions
- SCAN algorithm correctness
- Cost calculation logic
- Starvation prevention

**Integration Tests:**
- Full request lifecycle
- Concurrent hall calls
- Multi-elevator coordination
- Emergency mode override

**Stress Tests:**
- 1000 requests/hour load
- Peak hour simulation (80% to ground floor)
- Single elevator failure scenario

### 12. Pitfalls & Anti-Patterns Avoided

❌ **Avoid**: Global queue with single dispatcher (bottleneck)
✅ **Do**: Distributed assignment with per-elevator queues

❌ **Avoid**: Infinite wait for direction reversal
✅ **Do**: Priority aging and forced assignment

❌ **Avoid**: Hard-coded floor numbers
✅ **Do**: Configurable building parameters

❌ **Avoid**: Blocking state transitions
✅ **Do**: Non-blocking event-driven state machine

### 13. Complexity Analysis

| Operation | Time | Space | Notes |
|-----------|------|-------|-------|
| Assign hall call | O(M) | O(1) | M = number of elevators |
| Get next floor | O(log N) | O(N) | N = pending stops (TreeSet) |
| Should stop | O(K) | O(1) | K = assigned hall calls |
| Process step | O(1) | O(1) | Single elevator iteration |
| Prevent starvation | O(Q) | O(1) | Q = queue size |

### 14. Interview Evaluation Rubric

| Criterion | Weight | What to Look For |
|-----------|--------|------------------|
| **Scheduling Algorithm** | 35% | SCAN/LOOK implementation, efficiency |
| **State Machine** | 25% | Clear states, valid transitions |
| **Assignment Strategy** | 20% | Cost function, multi-factor optimization |
| **Concurrency** | 15% | Thread-safe, no deadlocks |
| **Real-world Awareness** | 5% | Starvation, emergency, monitoring |

**Red Flags:**
- FCFS scheduling
- No starvation prevention
- Invalid state transitions allowed
- Ignoring direction in cost function
- No error handling for stuck elevators

---
