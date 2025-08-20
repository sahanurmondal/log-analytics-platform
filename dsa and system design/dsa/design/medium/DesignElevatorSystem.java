package design.medium;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design an Elevator System
 *
 * Description:
 * An elevator system for a building with multiple floors and elevators.
 * The system should handle elevator requests, simulate elevator movement,
 * and provide status updates.
 *
 * Constraints:
 * - Elevators can be in maintenance or emergency state.
 * - Requests can be for picking up or dropping off passengers.
 * - Elevators have a limited capacity.
 *
 * Follow-up:
 * - Can you optimize the elevator assignment algorithm?
 * - Can you add more features like priority requests, express elevators, etc.?
 */
public class DesignElevatorSystem {
    enum Direction {
        UP, DOWN, IDLE
    }

    enum ElevatorState {
        IDLE, MOVING, MAINTENANCE, EMERGENCY
    }

    class ElevatorRequest {
        int fromFloor;
        int toFloor;
        Direction direction;
        boolean isPickup;

        ElevatorRequest(int fromFloor, int toFloor) {
            this.fromFloor = fromFloor;
            this.toFloor = toFloor;
            this.direction = fromFloor < toFloor ? Direction.UP : Direction.DOWN;
            this.isPickup = true;
        }

        ElevatorRequest(int floor, Direction direction, boolean isPickup) {
            this.fromFloor = floor;
            this.toFloor = floor;
            this.direction = direction;
            this.isPickup = isPickup;
        }
    }

    class Elevator {
        int elevatorId;
        int capacity;
        int currentLoad;
        int currentFloor;
        Direction currentDirection;
        ElevatorState state;
        List<Integer> destinationFloors;

        Elevator(int elevatorId, int capacity) {
            this.elevatorId = elevatorId;
            this.capacity = capacity;
            this.currentLoad = 0;
            this.currentFloor = 0;
            this.currentDirection = Direction.IDLE;
            this.state = ElevatorState.IDLE;
            this.destinationFloors = new ArrayList<>();
        }

        void addDestination(int floor) {
            if (!destinationFloors.contains(floor)) {
                destinationFloors.add(floor);
                Collections.sort(destinationFloors);
            }
        }

        void moveToNextFloor() {
            if (destinationFloors.isEmpty()) {
                currentDirection = Direction.IDLE;
                state = ElevatorState.IDLE;
                return;
            }

            int nextFloor = destinationFloors.get(0);
            if (currentFloor < nextFloor) {
                currentFloor++;
                currentDirection = Direction.UP;
            } else if (currentFloor > nextFloor) {
                currentFloor--;
                currentDirection = Direction.DOWN;
            }

            // Simulate passenger loading/unloading
            if (currentFloor == nextFloor) {
                destinationFloors.remove(0);
                if (currentLoad < capacity) {
                    currentLoad++;
                }
            }
        }

        boolean canTakeMorePassengers() {
            return currentLoad < capacity;
        }

        double getDistance(int targetFloor) {
            return Math.abs(currentFloor - targetFloor);
        }

        boolean isMovingTowards(int floor) {
            if (currentDirection == Direction.UP) {
                return floor > currentFloor;
            } else if (currentDirection == Direction.DOWN) {
                return floor < currentFloor;
            }
            return true; // Idle elevator can go anywhere
        }
    }

    class ElevatorController {
        List<Elevator> elevators;
        int totalFloors;
        Queue<ElevatorRequest> pendingRequests;
        ScheduledExecutorService scheduler;

        ElevatorController(int numElevators, int totalFloors, int elevatorCapacity) {
            this.elevators = new ArrayList<>();
            this.totalFloors = totalFloors;
            this.pendingRequests = new LinkedList<>();
            this.scheduler = Executors.newScheduledThreadPool(numElevators);

            // Initialize elevators
            for (int i = 0; i < numElevators; i++) {
                elevators.add(new Elevator(i, elevatorCapacity));
            }

            // Start elevator movement simulation
            startElevatorSimulation();
        }

        void requestElevator(int fromFloor, int toFloor) {
            ElevatorRequest request = new ElevatorRequest(fromFloor, toFloor);
            assignRequestToElevator(request);
        }

        void callElevator(int floor, Direction direction) {
            ElevatorRequest request = new ElevatorRequest(floor, direction, true);
            assignRequestToElevator(request);
        }

        private void assignRequestToElevator(ElevatorRequest request) {
            Elevator bestElevator = findBestElevator(request);
            if (bestElevator != null) {
                if (request.isPickup) {
                    bestElevator.addDestination(request.fromFloor);
                } else {
                    bestElevator.addDestination(request.toFloor);
                }
            } else {
                pendingRequests.offer(request);
            }
        }

        private Elevator findBestElevator(ElevatorRequest request) {
            Elevator bestElevator = null;
            double bestScore = Double.MAX_VALUE;

            int targetFloor = request.isPickup ? request.fromFloor : request.toFloor;

            for (Elevator elevator : elevators) {
                if (elevator.state == ElevatorState.MAINTENANCE ||
                        elevator.state == ElevatorState.EMERGENCY) {
                    continue;
                }

                double score = calculateElevatorScore(elevator, request, targetFloor);
                if (score < bestScore) {
                    bestScore = score;
                    bestElevator = elevator;
                }
            }

            return bestElevator;
        }

        private double calculateElevatorScore(Elevator elevator, ElevatorRequest request, int targetFloor) {
            double score = 0;

            // Distance factor
            double distance = elevator.getDistance(targetFloor);
            score += distance * 2;

            // Direction alignment factor
            if (request.direction != Direction.IDLE) {
                if (elevator.currentDirection == request.direction &&
                        elevator.isMovingTowards(targetFloor)) {
                    score -= 10; // Bonus for same direction
                } else if (elevator.currentDirection != Direction.IDLE &&
                        elevator.currentDirection != request.direction) {
                    score += 15; // Penalty for opposite direction
                }
            }

            // Load factor
            score += elevator.currentLoad * 3;

            // Idle elevator bonus
            if (elevator.state == ElevatorState.IDLE) {
                score -= 5;
            }

            // Destination count factor
            score += elevator.destinationFloors.size() * 2;

            return score;
        }

        private void startElevatorSimulation() {
            scheduler.scheduleWithFixedDelay(() -> {
                for (Elevator elevator : elevators) {
                    if (elevator.state == ElevatorState.MOVING) {
                        elevator.moveToNextFloor();
                    }
                }

                // Process pending requests
                processPendingRequests();

            }, 1, 1, TimeUnit.SECONDS);
        }

        private void processPendingRequests() {
            Iterator<ElevatorRequest> iterator = pendingRequests.iterator();
            while (iterator.hasNext()) {
                ElevatorRequest request = iterator.next();
                Elevator elevator = findBestElevator(request);
                if (elevator != null && elevator.canTakeMorePassengers()) {
                    if (request.isPickup) {
                        elevator.addDestination(request.fromFloor);
                    } else {
                        elevator.addDestination(request.toFloor);
                    }
                    iterator.remove();
                }
            }
        }

        void setElevatorMaintenance(int elevatorId, boolean maintenance) {
            if (elevatorId >= 0 && elevatorId < elevators.size()) {
                Elevator elevator = elevators.get(elevatorId);
                elevator.state = maintenance ? ElevatorState.MAINTENANCE : ElevatorState.IDLE;
            }
        }

        Map<String, Object> getSystemStatus() {
            Map<String, Object> status = new HashMap<>();

            List<Map<String, Object>> elevatorStatuses = new ArrayList<>();
            for (Elevator elevator : elevators) {
                Map<String, Object> elevatorStatus = new HashMap<>();
                elevatorStatus.put("id", elevator.elevatorId);
                elevatorStatus.put("currentFloor", elevator.currentFloor);
                elevatorStatus.put("direction", elevator.currentDirection);
                elevatorStatus.put("state", elevator.state);
                elevatorStatus.put("load", elevator.currentLoad + "/" + elevator.capacity);
                elevatorStatus.put("destinations", new ArrayList<>(elevator.destinationFloors));

                elevatorStatuses.add(elevatorStatus);
            }

            status.put("elevators", elevatorStatuses);
            status.put("pendingRequests", pendingRequests.size());
            status.put("totalFloors", totalFloors);

            return status;
        }

        void shutdown() {
            scheduler.shutdown();
        }
    }

    private ElevatorController controller;

    public DesignElevatorSystem(int numElevators, int totalFloors, int elevatorCapacity) {
        controller = new ElevatorController(numElevators, totalFloors, elevatorCapacity);
    }

    public void requestElevator(int fromFloor, int toFloor) {
        controller.requestElevator(fromFloor, toFloor);
    }

    public void callElevator(int floor, Direction direction) {
        controller.callElevator(floor, direction);
    }

    public Map<String, Object> getSystemStatus() {
        return controller.getSystemStatus();
    }

    public void setElevatorMaintenance(int elevatorId, boolean maintenance) {
        controller.setElevatorMaintenance(elevatorId, maintenance);
    }

    public void shutdown() {
        controller.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignElevatorSystem elevatorSystem = new DesignElevatorSystem(3, 10, 8);

        System.out.println("Initial system status:");
        System.out.println(elevatorSystem.getSystemStatus());

        // Simulate elevator requests
        elevatorSystem.callElevator(5, Direction.UP);
        elevatorSystem.requestElevator(1, 8);
        elevatorSystem.requestElevator(3, 7);
        elevatorSystem.callElevator(9, Direction.DOWN);

        System.out.println("\nAfter requests:");
        System.out.println(elevatorSystem.getSystemStatus());

        // Wait for some movement
        Thread.sleep(5000);

        System.out.println("\nAfter 5 seconds:");
        System.out.println(elevatorSystem.getSystemStatus());

        // Set one elevator to maintenance
        elevatorSystem.setElevatorMaintenance(1, true);

        // More requests
        elevatorSystem.requestElevator(2, 6);
        elevatorSystem.requestElevator(4, 1);

        Thread.sleep(3000);

        System.out.println("\nAfter maintenance and more requests:");
        System.out.println(elevatorSystem.getSystemStatus());

        elevatorSystem.shutdown();
    }
}