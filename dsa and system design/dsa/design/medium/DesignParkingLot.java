package design.medium;

import java.util.*;

/**
 * Design Parking Lot System
 *
 * Description: Design a parking lot system that supports:
 * - Different vehicle types (car, motorcycle, truck)
 * - Multiple levels and zones
 * - Real-time availability tracking
 * - Pricing and payment processing
 * 
 * Constraints:
 * - Support different spot sizes
 * - Handle peak hours pricing
 * - Provide efficient spot finding
 *
 * Follow-up:
 * - How to handle reservations?
 * - Dynamic pricing strategies?
 * 
 * Time Complexity: O(1) for most operations, O(n) for finding spots
 * Space Complexity: O(total_spots)
 * 
 * Company Tags: System Design Interview, Uber, Lyft
 */
public class DesignParkingLot {

    enum VehicleType {
        MOTORCYCLE(1), CAR(2), TRUCK(3);

        private final int size;

        VehicleType(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    enum SpotType {
        MOTORCYCLE, COMPACT, REGULAR, LARGE
    }

    enum SpotStatus {
        AVAILABLE, OCCUPIED, RESERVED, OUT_OF_ORDER
    }

    class Vehicle {
        String licensePlate;
        VehicleType type;
        long entryTime;

        Vehicle(String licensePlate, VehicleType type) {
            this.licensePlate = licensePlate;
            this.type = type;
            this.entryTime = System.currentTimeMillis();
        }
    }

    class ParkingSpot {
        String spotId;
        SpotType spotType;
        SpotStatus status;
        int level;
        int row;
        int number;
        Vehicle currentVehicle;

        ParkingSpot(String spotId, SpotType spotType, int level, int row, int number) {
            this.spotId = spotId;
            this.spotType = spotType;
            this.status = SpotStatus.AVAILABLE;
            this.level = level;
            this.row = row;
            this.number = number;
        }

        boolean canFitVehicle(VehicleType vehicleType) {
            switch (spotType) {
                case MOTORCYCLE:
                    return vehicleType == VehicleType.MOTORCYCLE;
                case COMPACT:
                    return vehicleType == VehicleType.MOTORCYCLE || vehicleType == VehicleType.CAR;
                case REGULAR:
                    return vehicleType != VehicleType.TRUCK;
                case LARGE:
                    return true;
                default:
                    return false;
            }
        }

        boolean parkVehicle(Vehicle vehicle) {
            if (status == SpotStatus.AVAILABLE && canFitVehicle(vehicle.type)) {
                this.currentVehicle = vehicle;
                this.status = SpotStatus.OCCUPIED;
                return true;
            }
            return false;
        }

        Vehicle removeVehicle() {
            Vehicle vehicle = currentVehicle;
            currentVehicle = null;
            status = SpotStatus.AVAILABLE;
            return vehicle;
        }

        boolean isAvailable() {
            return status == SpotStatus.AVAILABLE;
        }
    }

    class ParkingLevel {
        int levelNumber;
        Map<SpotType, List<ParkingSpot>> spotsByType;
        Map<String, ParkingSpot> spotMap; // spotId -> spot

        ParkingLevel(int levelNumber) {
            this.levelNumber = levelNumber;
            this.spotsByType = new HashMap<>();
            this.spotMap = new HashMap<>();

            for (SpotType type : SpotType.values()) {
                spotsByType.put(type, new ArrayList<>());
            }
        }

        void addSpot(ParkingSpot spot) {
            spotsByType.get(spot.spotType).add(spot);
            spotMap.put(spot.spotId, spot);
        }

        ParkingSpot findAvailableSpot(VehicleType vehicleType) {
            // Try to find the most appropriate spot type first
            for (SpotType spotType : getPreferredSpotTypes(vehicleType)) {
                for (ParkingSpot spot : spotsByType.get(spotType)) {
                    if (spot.isAvailable() && spot.canFitVehicle(vehicleType)) {
                        return spot;
                    }
                }
            }
            return null;
        }

        private List<SpotType> getPreferredSpotTypes(VehicleType vehicleType) {
            switch (vehicleType) {
                case MOTORCYCLE:
                    return Arrays.asList(SpotType.MOTORCYCLE, SpotType.COMPACT, SpotType.REGULAR, SpotType.LARGE);
                case CAR:
                    return Arrays.asList(SpotType.COMPACT, SpotType.REGULAR, SpotType.LARGE);
                case TRUCK:
                    return Arrays.asList(SpotType.LARGE);
                default:
                    return new ArrayList<>();
            }
        }

        int getAvailableSpots(SpotType spotType) {
            return (int) spotsByType.get(spotType).stream()
                    .filter(ParkingSpot::isAvailable)
                    .count();
        }

        int getTotalSpots(SpotType spotType) {
            return spotsByType.get(spotType).size();
        }
    }

    class ParkingTicket {
        String ticketId;
        String licensePlate;
        String spotId;
        long entryTime;
        long exitTime;
        double amount;
        boolean isPaid;

        ParkingTicket(String licensePlate, String spotId) {
            this.ticketId = UUID.randomUUID().toString();
            this.licensePlate = licensePlate;
            this.spotId = spotId;
            this.entryTime = System.currentTimeMillis();
            this.isPaid = false;
        }

        void completePayment(double amount) {
            this.amount = amount;
            this.exitTime = System.currentTimeMillis();
            this.isPaid = true;
        }

        long getParkingDurationMinutes() {
            long endTime = exitTime > 0 ? exitTime : System.currentTimeMillis();
            return (endTime - entryTime) / (1000 * 60);
        }
    }

    class PricingStrategy {
        private Map<SpotType, Double> baseRates; // per hour
        private double peakHourMultiplier;
        private Set<Integer> peakHours;

        PricingStrategy() {
            baseRates = new HashMap<>();
            baseRates.put(SpotType.MOTORCYCLE, 2.0);
            baseRates.put(SpotType.COMPACT, 3.0);
            baseRates.put(SpotType.REGULAR, 4.0);
            baseRates.put(SpotType.LARGE, 6.0);

            peakHourMultiplier = 1.5;
            peakHours = Set.of(7, 8, 9, 17, 18, 19); // 7-9 AM and 5-7 PM
        }

        double calculateFee(SpotType spotType, long durationMinutes) {
            double baseRate = baseRates.get(spotType);
            double hours = Math.max(1.0, Math.ceil(durationMinutes / 60.0)); // Minimum 1 hour

            Calendar cal = Calendar.getInstance();
            int currentHour = cal.get(Calendar.HOUR_OF_DAY);

            double rate = baseRate;
            if (peakHours.contains(currentHour)) {
                rate *= peakHourMultiplier;
            }

            return rate * hours;
        }
    }

    private List<ParkingLevel> levels;
    private Map<String, ParkingTicket> activeTickets; // licensePlate -> ticket
    private Map<String, ParkingSpot> occupiedSpots; // licensePlate -> spot
    private PricingStrategy pricingStrategy;
    private int maxLevels;

    public DesignParkingLot(int maxLevels) {
        this.maxLevels = maxLevels;
        this.levels = new ArrayList<>();
        this.activeTickets = new HashMap<>();
        this.occupiedSpots = new HashMap<>();
        this.pricingStrategy = new PricingStrategy();

        initializeParkingLot();
    }

    private void initializeParkingLot() {
        for (int i = 0; i < maxLevels; i++) {
            ParkingLevel level = new ParkingLevel(i);

            // Add spots to each level
            addSpotsToLevel(level, SpotType.MOTORCYCLE, 20);
            addSpotsToLevel(level, SpotType.COMPACT, 40);
            addSpotsToLevel(level, SpotType.REGULAR, 60);
            addSpotsToLevel(level, SpotType.LARGE, 10);

            levels.add(level);
        }
    }

    private void addSpotsToLevel(ParkingLevel level, SpotType spotType, int count) {
        for (int i = 0; i < count; i++) {
            String spotId = String.format("L%d-%s-%03d", level.levelNumber,
                    spotType.name().substring(0, 1), i + 1);
            ParkingSpot spot = new ParkingSpot(spotId, spotType, level.levelNumber, i / 10, i % 10);
            level.addSpot(spot);
        }
    }

    public ParkingTicket parkVehicle(String licensePlate, VehicleType vehicleType) {
        if (activeTickets.containsKey(licensePlate)) {
            return null; // Vehicle already parked
        }

        // Find available spot
        ParkingSpot availableSpot = findAvailableSpot(vehicleType);
        if (availableSpot == null) {
            return null; // No available spots
        }

        // Park the vehicle
        Vehicle vehicle = new Vehicle(licensePlate, vehicleType);
        if (availableSpot.parkVehicle(vehicle)) {
            ParkingTicket ticket = new ParkingTicket(licensePlate, availableSpot.spotId);
            activeTickets.put(licensePlate, ticket);
            occupiedSpots.put(licensePlate, availableSpot);
            return ticket;
        }

        return null;
    }

    private ParkingSpot findAvailableSpot(VehicleType vehicleType) {
        // Try each level starting from ground level
        for (ParkingLevel level : levels) {
            ParkingSpot spot = level.findAvailableSpot(vehicleType);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public double calculateParkingFee(String licensePlate) {
        ParkingTicket ticket = activeTickets.get(licensePlate);
        ParkingSpot spot = occupiedSpots.get(licensePlate);

        if (ticket == null || spot == null) {
            return 0.0;
        }

        long durationMinutes = ticket.getParkingDurationMinutes();
        return pricingStrategy.calculateFee(spot.spotType, durationMinutes);
    }

    public boolean exitVehicle(String licensePlate, double payment) {
        ParkingTicket ticket = activeTickets.get(licensePlate);
        ParkingSpot spot = occupiedSpots.get(licensePlate);

        if (ticket == null || spot == null) {
            return false;
        }

        double fee = calculateParkingFee(licensePlate);
        if (payment < fee) {
            return false; // Insufficient payment
        }

        // Complete the transaction
        ticket.completePayment(payment);
        spot.removeVehicle();

        // Clean up
        activeTickets.remove(licensePlate);
        occupiedSpots.remove(licensePlate);

        return true;
    }

    public Map<String, Object> getParkingStatus() {
        Map<String, Object> status = new HashMap<>();

        int totalSpots = 0;
        int occupiedSpots = 0;
        Map<SpotType, Integer> availableByType = new HashMap<>();
        Map<SpotType, Integer> totalByType = new HashMap<>();

        for (SpotType type : SpotType.values()) {
            availableByType.put(type, 0);
            totalByType.put(type, 0);
        }

        for (ParkingLevel level : levels) {
            for (SpotType type : SpotType.values()) {
                int available = level.getAvailableSpots(type);
                int total = level.getTotalSpots(type);

                availableByType.put(type, availableByType.get(type) + available);
                totalByType.put(type, totalByType.get(type) + total);

                totalSpots += total;
                occupiedSpots += (total - available);
            }
        }

        status.put("totalSpots", totalSpots);
        status.put("occupiedSpots", occupiedSpots);
        status.put("availableSpots", totalSpots - occupiedSpots);
        status.put("occupancyRate", (double) occupiedSpots / totalSpots * 100);
        status.put("availableByType", availableByType);
        status.put("totalByType", totalByType);

        return status;
    }

    public List<Map<String, Object>> getLevelStatus() {
        List<Map<String, Object>> levelStatuses = new ArrayList<>();

        for (ParkingLevel level : levels) {
            Map<String, Object> levelStatus = new HashMap<>();
            levelStatus.put("level", level.levelNumber);

            Map<SpotType, Integer> available = new HashMap<>();
            Map<SpotType, Integer> total = new HashMap<>();

            for (SpotType type : SpotType.values()) {
                available.put(type, level.getAvailableSpots(type));
                total.put(type, level.getTotalSpots(type));
            }

            levelStatus.put("availableByType", available);
            levelStatus.put("totalByType", total);

            levelStatuses.add(levelStatus);
        }

        return levelStatuses;
    }

    public ParkingSpot findSpotById(String spotId) {
        for (ParkingLevel level : levels) {
            ParkingSpot spot = level.spotMap.get(spotId);
            if (spot != null) {
                return spot;
            }
        }
        return null;
    }

    public List<String> findNearbyAvailableSpots(String referenceSpotId, VehicleType vehicleType) {
        ParkingSpot referenceSpot = findSpotById(referenceSpotId);
        if (referenceSpot == null) {
            return new ArrayList<>();
        }

        List<String> nearbySpots = new ArrayList<>();
        ParkingLevel level = levels.get(referenceSpot.level);

        // Find spots in the same row first, then adjacent rows
        for (int rowOffset = 0; rowOffset <= 2; rowOffset++) {
            for (int direction : new int[] { -1, 1 }) {
                if (rowOffset == 0 && direction == -1)
                    continue; // Skip duplicate of same row

                int targetRow = referenceSpot.row + (rowOffset * direction);

                for (ParkingSpot spot : level.spotMap.values()) {
                    if (spot.row == targetRow && spot.isAvailable() &&
                            spot.canFitVehicle(vehicleType) &&
                            !spot.spotId.equals(referenceSpotId)) {
                        nearbySpots.add(spot.spotId);
                    }
                }
            }
        }

        return nearbySpots;
    }

    public static void main(String[] args) {
        DesignParkingLot parkingLot = new DesignParkingLot(3);

        System.out.println("Initial parking status:");
        System.out.println(parkingLot.getParkingStatus());

        // Park some vehicles
        ParkingTicket ticket1 = parkingLot.parkVehicle("ABC123", VehicleType.CAR);
        ParkingTicket ticket2 = parkingLot.parkVehicle("XYZ789", VehicleType.MOTORCYCLE);
        ParkingTicket ticket3 = parkingLot.parkVehicle("TRUCK01", VehicleType.TRUCK);

        if (ticket1 != null) {
            System.out.println("\nParked car ABC123 at spot: " + ticket1.spotId);
        }
        if (ticket2 != null) {
            System.out.println("Parked motorcycle XYZ789 at spot: " + ticket2.spotId);
        }
        if (ticket3 != null) {
            System.out.println("Parked truck TRUCK01 at spot: " + ticket3.spotId);
        }

        // Wait a bit to simulate parking time
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Calculate fees
        if (ticket1 != null) {
            double fee = parkingLot.calculateParkingFee("ABC123");
            System.out.println("\nParking fee for ABC123: $" + String.format("%.2f", fee));

            // Exit vehicle
            boolean success = parkingLot.exitVehicle("ABC123", fee);
            System.out.println("Exit successful: " + success);
        }

        // Show updated status
        System.out.println("\nUpdated parking status:");
        System.out.println(parkingLot.getParkingStatus());

        // Show level-wise status
        System.out.println("\nLevel-wise status:");
        List<Map<String, Object>> levelStatuses = parkingLot.getLevelStatus();
        for (Map<String, Object> levelStatus : levelStatuses) {
            System.out.println("Level " + levelStatus.get("level") + ": " + levelStatus);
        }

        // Find nearby spots
        if (ticket2 != null) {
            List<String> nearbySpots = parkingLot.findNearbyAvailableSpots(ticket2.spotId, VehicleType.CAR);
            System.out.println("\nNearby available spots for cars near " + ticket2.spotId + ": "
                    + nearbySpots.subList(0, Math.min(5, nearbySpots.size())));
        }
    }
}
