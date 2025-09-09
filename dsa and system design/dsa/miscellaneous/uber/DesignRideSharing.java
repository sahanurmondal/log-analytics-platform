package miscellaneous.uber;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Question: Design a Ride Sharing Service
 * 
 * Description:
 * Design a ride-sharing platform that supports:
 * - Driver and rider matching
 * - Real-time location tracking
 * - Dynamic pricing
 * - Trip management
 * - Rating system
 * 
 * Company: Uber
 * Difficulty: Hard
 * Asked: System design interviews 2023-2024
 */
public class DesignRideSharing {

    enum RideStatus {
        REQUESTED, ACCEPTED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    enum DriverStatus {
        AVAILABLE, BUSY, OFFLINE
    }

    enum VehicleType {
        ECONOMY, PREMIUM, LUXURY
    }

    class Location {
        double latitude;
        double longitude;

        Location(double lat, double lng) {
            this.latitude = lat;
            this.longitude = lng;
        }

        public double distanceTo(Location other) {
            // Simplified distance calculation
            double lat1 = Math.toRadians(this.latitude);
            double lat2 = Math.toRadians(other.latitude);
            double deltaLat = Math.toRadians(other.latitude - this.latitude);
            double deltaLng = Math.toRadians(other.longitude - this.longitude);

            double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                    Math.cos(lat1) * Math.cos(lat2) *
                            Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return 6371 * c; // Earth's radius in km
        }
    }

    class Driver {
        String id;
        String name;
        String phoneNumber;
        Location currentLocation;
        DriverStatus status;
        VehicleType vehicleType;
        double rating;
        int totalRides;

        Driver(String id, String name, VehicleType vehicleType) {
            this.id = id;
            this.name = name;
            this.vehicleType = vehicleType;
            this.status = DriverStatus.OFFLINE;
            this.rating = 5.0;
            this.totalRides = 0;
        }
    }

    class Rider {
        String id;
        String name;
        String phoneNumber;
        double rating;
        int totalRides;

        Rider(String id, String name) {
            this.id = id;
            this.name = name;
            this.rating = 5.0;
            this.totalRides = 0;
        }
    }

    class Ride {
        String id;
        String riderId;
        String driverId;
        Location pickupLocation;
        Location dropoffLocation;
        RideStatus status;
        VehicleType vehicleType;
        double estimatedPrice;
        double actualPrice;
        long requestTime;
        long startTime;
        long endTime;

        Ride(String riderId, Location pickup, Location dropoff, VehicleType vehicleType) {
            this.id = UUID.randomUUID().toString();
            this.riderId = riderId;
            this.pickupLocation = pickup;
            this.dropoffLocation = dropoff;
            this.vehicleType = vehicleType;
            this.status = RideStatus.REQUESTED;
            this.requestTime = System.currentTimeMillis();
        }
    }

    class PricingEngine {
        private Map<VehicleType, Double> basePrices = Map.of(
                VehicleType.ECONOMY, 1.0,
                VehicleType.PREMIUM, 1.5,
                VehicleType.LUXURY, 2.0);

        public double calculatePrice(Location pickup, Location dropoff, VehicleType vehicleType) {
            double distance = pickup.distanceTo(dropoff);
            double basePrice = basePrices.get(vehicleType);
            double surge = getCurrentSurgeMultiplier(pickup);

            return basePrice * distance * surge;
        }

        private double getCurrentSurgeMultiplier(Location location) {
            // Simplified surge calculation based on demand
            int activeRidesInArea = getActiveRidesInArea(location);
            int availableDriversInArea = getAvailableDriversInArea(location);

            if (availableDriversInArea == 0)
                return 3.0;

            double demandRatio = (double) activeRidesInArea / availableDriversInArea;
            return Math.min(1.0 + demandRatio, 3.0);
        }

        private int getActiveRidesInArea(Location location) {
            // Simplified - count rides within 5km radius
            return (int) rides.values().stream()
                    .filter(r -> r.status == RideStatus.IN_PROGRESS)
                    .filter(r -> r.pickupLocation.distanceTo(location) < 5.0)
                    .count();
        }

        private int getAvailableDriversInArea(Location location) {
            // Simplified - count available drivers within 5km radius
            return (int) drivers.values().stream()
                    .filter(d -> d.status == DriverStatus.AVAILABLE)
                    .filter(d -> d.currentLocation.distanceTo(location) < 5.0)
                    .count();
        }
    }

    class DriverMatcher {
        public List<Driver> findNearbyDrivers(Location location, VehicleType vehicleType, int limit) {
            return drivers.values().stream()
                    .filter(d -> d.status == DriverStatus.AVAILABLE)
                    .filter(d -> d.vehicleType == vehicleType)
                    .filter(d -> d.currentLocation.distanceTo(location) < 10.0) // Within 10km
                    .sorted((d1, d2) -> Double.compare(
                            d1.currentLocation.distanceTo(location),
                            d2.currentLocation.distanceTo(location)))
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        public Driver matchDriver(Ride ride) {
            List<Driver> nearbyDrivers = findNearbyDrivers(
                    ride.pickupLocation,
                    ride.vehicleType,
                    5);

            // Simple matching - pick closest driver with good rating
            for (Driver driver : nearbyDrivers) {
                if (driver.rating >= 4.0) {
                    return driver;
                }
            }

            return nearbyDrivers.isEmpty() ? null : nearbyDrivers.get(0);
        }
    }

    private Map<String, Driver> drivers = new HashMap<>();
    private Map<String, Rider> riders = new HashMap<>();
    private Map<String, Ride> rides = new HashMap<>();
    private PricingEngine pricingEngine = new PricingEngine();
    private DriverMatcher driverMatcher = new DriverMatcher();

    public String requestRide(String riderId, Location pickup, Location dropoff, VehicleType vehicleType) {
        Rider rider = riders.get(riderId);
        if (rider == null) {
            return null;
        }

        Ride ride = new Ride(riderId, pickup, dropoff, vehicleType);
        ride.estimatedPrice = pricingEngine.calculatePrice(pickup, dropoff, vehicleType);

        rides.put(ride.id, ride);

        // Try to match with a driver
        Driver driver = driverMatcher.matchDriver(ride);
        if (driver != null) {
            ride.driverId = driver.id;
            ride.status = RideStatus.ACCEPTED;
            driver.status = DriverStatus.BUSY;

            // Notify driver
            notifyDriver(driver.id, ride);
        }

        return ride.id;
    }

    public void startRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.status == RideStatus.ACCEPTED) {
            ride.status = RideStatus.IN_PROGRESS;
            ride.startTime = System.currentTimeMillis();
        }
    }

    public void completeRide(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.status == RideStatus.IN_PROGRESS) {
            ride.status = RideStatus.COMPLETED;
            ride.endTime = System.currentTimeMillis();

            // Calculate actual price
            ride.actualPrice = ride.estimatedPrice;

            // Update driver status
            Driver driver = drivers.get(ride.driverId);
            if (driver != null) {
                driver.status = DriverStatus.AVAILABLE;
                driver.totalRides++;
            }

            // Update rider stats
            Rider rider = riders.get(ride.riderId);
            if (rider != null) {
                rider.totalRides++;
            }
        }
    }

    public void updateDriverLocation(String driverId, Location location) {
        Driver driver = drivers.get(driverId);
        if (driver != null) {
            driver.currentLocation = location;
        }
    }

    public void rateDriver(String rideId, double rating) {
        Ride ride = rides.get(rideId);
        if (ride != null && ride.status == RideStatus.COMPLETED) {
            Driver driver = drivers.get(ride.driverId);
            if (driver != null) {
                driver.rating = (driver.rating * (driver.totalRides - 1) + rating) / driver.totalRides;
            }
        }
    }

    private void notifyDriver(String driverId, Ride ride) {
        System.out.println("Notifying driver " + driverId + " about ride " + ride.id);
    }

    public static void main(String[] args) {
        DesignRideSharing rideService = new DesignRideSharing();

        // Create driver
        Driver driver = rideService.new Driver("driver1", "John Doe", VehicleType.ECONOMY);
        driver.currentLocation = rideService.new Location(37.7749, -122.4194);
        driver.status = DriverStatus.AVAILABLE;
        rideService.drivers.put(driver.id, driver);

        // Create rider
        Rider rider = rideService.new Rider("rider1", "Jane Smith");
        rideService.riders.put(rider.id, rider);

        // Request ride
        Location pickup = rideService.new Location(37.7849, -122.4094);
        Location dropoff = rideService.new Location(37.7949, -122.3994);
        String rideId = rideService.requestRide("rider1", pickup, dropoff, VehicleType.ECONOMY);

        System.out.println("Ride requested: " + rideId);

        // Start and complete ride
        rideService.startRide(rideId);
        rideService.completeRide(rideId);

        // Rate driver
        rideService.rateDriver(rideId, 4.5);

        System.out.println("Driver rating: " + driver.rating);
    }
}
