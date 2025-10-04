package company.banking.kotakbank.missing;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Banking Branch Locator System
 * 
 * Problem: Design a system to help customers find the nearest bank branches,
 * ATMs, and banking services based on their current location, with additional
 * filters for services available, operating hours, and accessibility features.
 * 
 * Features:
 * 1. Find nearest branch/ATM using GPS coordinates
 * 2. Filter by available services (loan, forex, wealth management)
 * 3. Filter by operating hours and current availability
 * 4. Calculate route distance and estimated travel time
 * 5. Batch search for multiple locations
 * 6. Accessibility features for disabled customers
 * 
 * Input:
 * - latitude: double - Customer's current latitude
 * - longitude: double - Customer's current longitude
 * - filters: BranchFilter - Optional filters for services, hours, etc.
 * 
 * Output:
 * - List<BranchResult> - Sorted list of branches with distance and details
 * 
 * Example:
 * Input: lat=19.0760, lon=72.8777 (Mumbai), maxDistance=5km, services=[ATM,
 * FOREX]
 * Output: [BranchResult{id="MUM001", distance=1.2km,
 * services=[ATM,FOREX,LOAN]...}]
 * 
 * Banking Context:
 * - Customer service and satisfaction
 * - Branch network optimization
 * - ATM placement strategy
 * - Emergency cash access
 * - Accessibility compliance
 */
public class BankBranchLocator {

    // Service types available at branches
    public enum ServiceType {
        ATM, CASH_DEPOSIT, FOREX, LOAN_SERVICES, WEALTH_MANAGEMENT,
        SAFE_DEPOSIT, CUSTOMER_SERVICE, MOBILE_BANKING_SUPPORT,
        CHEQUE_DEPOSIT, DRAFT_SERVICES, GOLD_LOAN, INSURANCE
    }

    // Accessibility features
    public enum AccessibilityFeature {
        WHEELCHAIR_ACCESSIBLE, BRAILLE_ATM, AUDIO_ASSISTANCE,
        RAMP_ACCESS, DISABLED_PARKING, SIGN_LANGUAGE_SUPPORT
    }

    // Operating status
    public enum OperatingStatus {
        OPEN, CLOSED, TEMPORARILY_CLOSED, MAINTENANCE, COMING_SOON
    }

    /**
     * Enhanced Branch class with comprehensive information
     */
    public static class Branch {
        public final String id;
        public final String name;
        public final double latitude;
        public final double longitude;
        public final String address;
        public final String phone;
        public final Set<ServiceType> services;
        public final Set<AccessibilityFeature> accessibilityFeatures;
        public final OperatingHours operatingHours;
        public final BranchType type;
        public final int capacity; // customer capacity
        public final boolean is24x7;

        public Branch(String id, String name, double latitude, double longitude, String address,
                String phone, Set<ServiceType> services, Set<AccessibilityFeature> accessibilityFeatures,
                OperatingHours operatingHours, BranchType type, int capacity, boolean is24x7) {
            this.id = id;
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.phone = phone;
            this.services = new HashSet<>(services);
            this.accessibilityFeatures = new HashSet<>(accessibilityFeatures);
            this.operatingHours = operatingHours;
            this.type = type;
            this.capacity = capacity;
            this.is24x7 = is24x7;
        }

        // Simple constructor for backward compatibility
        public Branch(String id, double lat, double lon) {
            this(id, id + " Branch", lat, lon, "Address not available", "Phone not available",
                    new HashSet<>(Arrays.asList(ServiceType.ATM, ServiceType.CUSTOMER_SERVICE)),
                    new HashSet<>(), new OperatingHours(9, 17), BranchType.FULL_SERVICE, 50, false);
        }

        @Override
        public String toString() {
            return String.format("Branch{id='%s', name='%s', location=(%.4f,%.4f), services=%d}",
                    id, name, latitude, longitude, services.size());
        }
    }

    public enum BranchType {
        FULL_SERVICE, ATM_ONLY, MINI_BRANCH, PREMIUM_BANKING, DIGITAL_BRANCH
    }

    public static class OperatingHours {
        public final int openHour;
        public final int closeHour;
        public final boolean[] openDays; // 0=Sunday, 1=Monday, ..., 6=Saturday

        public OperatingHours(int openHour, int closeHour) {
            this.openHour = openHour;
            this.closeHour = closeHour;
            this.openDays = new boolean[] { false, true, true, true, true, true, true }; // Closed on Sunday
        }

        public OperatingHours(int openHour, int closeHour, boolean[] openDays) {
            this.openHour = openHour;
            this.closeHour = closeHour;
            this.openDays = openDays.clone();
        }

        public boolean isOpenNow() {
            Calendar cal = Calendar.getInstance();
            int currentHour = cal.get(Calendar.HOUR_OF_DAY);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1; // Convert to 0-based

            return openDays[dayOfWeek] && currentHour >= openHour && currentHour < closeHour;
        }
    }

    public static class BranchFilter {
        public double maxDistanceKm = Double.MAX_VALUE;
        public Set<ServiceType> requiredServices = new HashSet<>();
        public Set<AccessibilityFeature> requiredAccessibility = new HashSet<>();
        public boolean onlyOpenNow = false;
        public BranchType branchType = null;
        public boolean only24x7 = false;
        public int maxResults = 10;

        public BranchFilter() {
        }

        public BranchFilter setMaxDistance(double km) {
            this.maxDistanceKm = km;
            return this;
        }

        public BranchFilter requireServices(ServiceType... services) {
            this.requiredServices.addAll(Arrays.asList(services));
            return this;
        }

        public BranchFilter requireAccessibility(AccessibilityFeature... features) {
            this.requiredAccessibility.addAll(Arrays.asList(features));
            return this;
        }

        public BranchFilter onlyOpenNow() {
            this.onlyOpenNow = true;
            return this;
        }
    }

    public static class BranchResult {
        public final Branch branch;
        public final double distanceKm;
        public final double estimatedTravelTimeMinutes;
        public final OperatingStatus currentStatus;
        public final String[] availableServicesNow;

        public BranchResult(Branch branch, double distanceKm, double estimatedTravelTimeMinutes,
                OperatingStatus currentStatus, String[] availableServicesNow) {
            this.branch = branch;
            this.distanceKm = distanceKm;
            this.estimatedTravelTimeMinutes = estimatedTravelTimeMinutes;
            this.currentStatus = currentStatus;
            this.availableServicesNow = availableServicesNow.clone();
        }

        @Override
        public String toString() {
            return String.format("BranchResult{id='%s', distance=%.2fkm, travelTime=%.0fmin, status=%s}",
                    branch.id, distanceKm, estimatedTravelTimeMinutes, currentStatus);
        }
    }

    /**
     * Calculate distance between two points using Haversine formula
     */
    private static double calculateDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        if (lat1 == lat2 && lon1 == lon2)
            return 0.0;

        double R = 6371.0; // Earth's radius in kilometers
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaPhi / 2) * Math.sin(deltaPhi / 2) +
                Math.cos(phi1) * Math.cos(phi2) *
                        Math.sin(deltaLambda / 2) * Math.sin(deltaLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    /**
     * Legacy method for backward compatibility
     */
    public static Branch findNearest(double lat, double lon, List<Branch> branches) {
        if (branches == null || branches.isEmpty()) {
            return null;
        }

        Branch best = null;
        double bestDist = Double.MAX_VALUE;

        for (Branch branch : branches) {
            double distance = calculateDistanceKm(lat, lon, branch.latitude, branch.longitude);
            if (distance < bestDist) {
                bestDist = distance;
                best = branch;
            }
        }

        return best;
    }

    /**
     * Find branches with comprehensive filtering and sorting
     */
    public static List<BranchResult> findBranches(double latitude, double longitude,
            List<Branch> branches, BranchFilter filter) {
        if (branches == null || branches.isEmpty()) {
            return new ArrayList<>();
        }

        return branches.stream()
                .filter(branch -> matchesFilter(branch, filter))
                .map(branch -> createBranchResult(branch, latitude, longitude))
                .filter(result -> result.distanceKm <= filter.maxDistanceKm)
                .sorted(Comparator.comparingDouble(result -> result.distanceKm))
                .limit(filter.maxResults)
                .collect(Collectors.toList());
    }

    private static boolean matchesFilter(Branch branch, BranchFilter filter) {
        // Check required services
        if (!branch.services.containsAll(filter.requiredServices)) {
            return false;
        }

        // Check accessibility features
        if (!branch.accessibilityFeatures.containsAll(filter.requiredAccessibility)) {
            return false;
        }

        // Check if open now
        if (filter.onlyOpenNow && !branch.operatingHours.isOpenNow()) {
            return false;
        }

        // Check branch type
        if (filter.branchType != null && branch.type != filter.branchType) {
            return false;
        }

        // Check 24x7 requirement
        if (filter.only24x7 && !branch.is24x7) {
            return false;
        }

        return true;
    }

    private static BranchResult createBranchResult(Branch branch, double userLat, double userLon) {
        double distance = calculateDistanceKm(userLat, userLon, branch.latitude, branch.longitude);
        double travelTime = estimateTravelTime(distance, branch.type);
        OperatingStatus status = getCurrentStatus(branch);
        String[] availableServices = getAvailableServicesNow(branch);

        return new BranchResult(branch, distance, travelTime, status, availableServices);
    }

    private static double estimateTravelTime(double distanceKm, BranchType type) {
        // Assume different travel speeds based on location type
        double avgSpeedKmh = 30.0; // Urban average
        return (distanceKm / avgSpeedKmh) * 60; // Convert to minutes
    }

    private static OperatingStatus getCurrentStatus(Branch branch) {
        if (branch.is24x7) {
            return OperatingStatus.OPEN;
        }
        return branch.operatingHours.isOpenNow() ? OperatingStatus.OPEN : OperatingStatus.CLOSED;
    }

    private static String[] getAvailableServicesNow(Branch branch) {
        OperatingStatus status = getCurrentStatus(branch);
        if (status == OperatingStatus.OPEN) {
            return branch.services.stream()
                    .map(Enum::name)
                    .toArray(String[]::new);
        } else {
            // Only ATM services available when branch is closed
            return branch.services.contains(ServiceType.ATM) ? new String[] { "ATM" } : new String[] {};
        }
    }

    /**
     * Follow-up 1: Find branches along a route
     */
    public static List<BranchResult> findBranchesAlongRoute(double startLat, double startLon,
            double endLat, double endLon,
            List<Branch> branches, double maxDeviationKm) {
        List<BranchResult> routeBranches = new ArrayList<>();

        for (Branch branch : branches) {
            double deviationDistance = calculateRouteDeviation(startLat, startLon, endLat, endLon,
                    branch.latitude, branch.longitude);
            if (deviationDistance <= maxDeviationKm) {
                double distanceFromStart = calculateDistanceKm(startLat, startLon,
                        branch.latitude, branch.longitude);
                BranchResult result = createBranchResult(branch, startLat, startLon);
                routeBranches.add(result);
            }
        }

        return routeBranches.stream()
                .sorted(Comparator.comparingDouble(r -> r.distanceKm))
                .collect(Collectors.toList());
    }

    private static double calculateRouteDeviation(double startLat, double startLon,
            double endLat, double endLon,
            double pointLat, double pointLon) {
        // Simplified calculation - distance from point to line
        double A = endLat - startLat;
        double B = startLon - endLon;
        double C = endLon * startLat - startLon * endLat;

        return Math.abs(A * pointLon + B * pointLat + C) / Math.sqrt(A * A + B * B);
    }

    /**
     * Follow-up 2: Branch load balancing and recommendations
     */
    public static List<BranchResult> getLoadBalancedRecommendations(double latitude, double longitude,
            List<Branch> branches,
            Map<String, Integer> currentLoads) {
        return branches.stream()
                .map(branch -> {
                    BranchResult result = createBranchResult(branch, latitude, longitude);
                    int load = currentLoads.getOrDefault(branch.id, 0);
                    double loadFactor = (double) load / branch.capacity;

                    // Adjust travel time based on expected wait time
                    double adjustedTravelTime = result.estimatedTravelTimeMinutes + (loadFactor * 15);

                    return new BranchResult(result.branch, result.distanceKm, adjustedTravelTime,
                            result.currentStatus, result.availableServicesNow);
                })
                .sorted(Comparator.comparingDouble(r -> r.estimatedTravelTimeMinutes))
                .collect(Collectors.toList());
    }

    /**
     * Follow-up 3: Emergency cash access finder
     */
    public static List<BranchResult> findEmergencyCashAccess(double latitude, double longitude,
            List<Branch> branches) {
        BranchFilter emergencyFilter = new BranchFilter()
                .setMaxDistance(10.0) // Within 10km
                .requireServices(ServiceType.ATM)
                .onlyOpenNow();

        List<BranchResult> results = findBranches(latitude, longitude, branches, emergencyFilter);

        // Prioritize 24x7 ATMs
        return results.stream()
                .sorted((r1, r2) -> {
                    if (r1.branch.is24x7 && !r2.branch.is24x7)
                        return -1;
                    if (!r1.branch.is24x7 && r2.branch.is24x7)
                        return 1;
                    return Double.compare(r1.distanceKm, r2.distanceKm);
                })
                .collect(Collectors.toList());
    }

    public static void main(String[] args) {
        System.out.println("=== Banking Branch Locator System ===\n");

        // Setup sample branches
        List<Branch> branches = createSampleBranches();

        // Test Case 1: Basic nearest branch finding (legacy)
        System.out.println("1. Basic Nearest Branch Finding:");
        double userLat = 19.0760;
        double userLon = 72.8777;

        Branch nearest = findNearest(userLat, userLon, branches);
        System.out.printf("Nearest branch: %s%n", nearest.id);

        // Test Case 2: Enhanced branch search with filters
        System.out.println("\n2. Enhanced Branch Search with Filters:");
        BranchFilter filter = new BranchFilter()
                .setMaxDistance(5.0)
                .requireServices(ServiceType.ATM, ServiceType.FOREX)
                .onlyOpenNow();

        List<BranchResult> results = findBranches(userLat, userLon, branches, filter);
        System.out.printf("Found %d branches matching criteria:%n", results.size());
        results.forEach(result -> System.out.printf("  %s - %.2fkm, %s%n",
                result.branch.name, result.distanceKm, result.currentStatus));

        // Test Case 3: Accessibility-focused search
        System.out.println("\n3. Accessibility-Focused Search:");
        BranchFilter accessibilityFilter = new BranchFilter()
                .setMaxDistance(3.0)
                .requireAccessibility(AccessibilityFeature.WHEELCHAIR_ACCESSIBLE);

        List<BranchResult> accessibleBranches = findBranches(userLat, userLon, branches, accessibilityFilter);
        System.out.printf("Found %d wheelchair accessible branches:%n", accessibleBranches.size());
        accessibleBranches
                .forEach(result -> System.out.printf("  %s - %.2fkm away%n", result.branch.name, result.distanceKm));

        // Test Case 4: 24x7 ATM search for emergencies
        System.out.println("\n4. Emergency Cash Access (24x7 ATMs):");
        List<BranchResult> emergencyATMs = findEmergencyCashAccess(userLat, userLon, branches);
        System.out.printf("Emergency ATMs available:%n");
        emergencyATMs.stream().limit(3).forEach(result -> System.out.printf("  %s - %.2fkm, Travel: %.0f min%n",
                result.branch.name, result.distanceKm, result.estimatedTravelTimeMinutes));

        // Test Case 5: Route-based branch finding
        System.out.println("\n5. Branches Along Route:");
        double destLat = 19.1000;
        double destLon = 72.9000;

        List<BranchResult> routeBranches = findBranchesAlongRoute(userLat, userLon, destLat, destLon,
                branches, 1.0);
        System.out.printf("Branches along route (within 1km deviation):%n");
        routeBranches.stream().limit(3).forEach(
                result -> System.out.printf("  %s - %.2fkm from start%n", result.branch.name, result.distanceKm));

        // Test Case 6: Load-balanced recommendations
        System.out.println("\n6. Load-Balanced Branch Recommendations:");
        Map<String, Integer> currentLoads = Map.of(
                "MUM001", 45, // High load
                "MUM002", 10, // Low load
                "MUM003", 25 // Medium load
        );

        List<BranchResult> loadBalanced = getLoadBalancedRecommendations(userLat, userLon,
                branches, currentLoads);
        System.out.println("Recommended branches (considering wait times):");
        loadBalanced.stream().limit(3)
                .forEach(result -> System.out.printf("  %s - Total time: %.0f min (%.2fkm + wait)%n",
                        result.branch.name, result.estimatedTravelTimeMinutes, result.distanceKm));

        // Test Case 7: Service-specific searches
        System.out.println("\n7. Service-Specific Searches:");

        // Wealth management services
        BranchFilter wealthFilter = new BranchFilter()
                .requireServices(ServiceType.WEALTH_MANAGEMENT)
                .setMaxDistance(10.0);

        List<BranchResult> wealthBranches = findBranches(userLat, userLon, branches, wealthFilter);
        System.out.printf("Wealth management centers: %d found%n", wealthBranches.size());

        // Forex services
        BranchFilter forexFilter = new BranchFilter()
                .requireServices(ServiceType.FOREX)
                .onlyOpenNow();

        List<BranchResult> forexBranches = findBranches(userLat, userLon, branches, forexFilter);
        System.out.printf("Forex services (open now): %d found%n", forexBranches.size());

        // Test Case 8: Performance test with large dataset
        System.out.println("\n8. Performance Test:");
        List<Branch> largeBranchList = generateLargeBranchDataset(1000);

        long startTime = System.currentTimeMillis();
        List<BranchResult> performanceResults = findBranches(userLat, userLon, largeBranchList,
                new BranchFilter().setMaxDistance(5.0));
        long endTime = System.currentTimeMillis();

        System.out.printf("Searched %d branches in %d ms, found %d results%n",
                largeBranchList.size(), endTime - startTime, performanceResults.size());

        System.out.println("\n=== Test Completed Successfully ===");
    }

    private static List<Branch> createSampleBranches() {
        List<Branch> branches = new ArrayList<>();

        // Mumbai branches
        branches.add(new Branch("MUM001", "Kotak Mahindra Bank - Bandra West",
                19.0596, 72.8295, "Linking Road, Bandra West, Mumbai", "+91-22-26430000",
                new HashSet<>(Arrays.asList(ServiceType.ATM, ServiceType.FOREX, ServiceType.LOAN_SERVICES,
                        ServiceType.WEALTH_MANAGEMENT)),
                new HashSet<>(
                        Arrays.asList(AccessibilityFeature.WHEELCHAIR_ACCESSIBLE, AccessibilityFeature.RAMP_ACCESS)),
                new OperatingHours(9, 17), BranchType.FULL_SERVICE, 50, false));

        branches.add(new Branch("MUM002", "Kotak ATM - 24x7 Powai",
                19.1197, 72.9078, "Hiranandani Gardens, Powai, Mumbai", "+91-22-25701234",
                new HashSet<>(Arrays.asList(ServiceType.ATM, ServiceType.CASH_DEPOSIT)),
                new HashSet<>(Arrays.asList(AccessibilityFeature.BRAILLE_ATM, AccessibilityFeature.AUDIO_ASSISTANCE)),
                new OperatingHours(0, 24), BranchType.ATM_ONLY, 0, true));

        branches.add(new Branch("MUM003", "Kotak Mahindra Bank - Andheri East",
                19.1075, 72.8263, "Chakala, Andheri East, Mumbai", "+91-22-28204567",
                new HashSet<>(Arrays.asList(ServiceType.ATM, ServiceType.CUSTOMER_SERVICE, ServiceType.CHEQUE_DEPOSIT)),
                new HashSet<>(Arrays.asList(AccessibilityFeature.WHEELCHAIR_ACCESSIBLE)),
                new OperatingHours(10, 16), BranchType.MINI_BRANCH, 30, false));

        return branches;
    }

    private static List<Branch> generateLargeBranchDataset(int count) {
        List<Branch> branches = new ArrayList<>();
        Random random = new Random(42); // Fixed seed for reproducible results

        for (int i = 0; i < count; i++) {
            String id = String.format("BR%04d", i);
            String name = "Branch " + id;
            double lat = 19.0 + random.nextDouble() * 0.5; // Mumbai area
            double lon = 72.8 + random.nextDouble() * 0.5;

            Set<ServiceType> services = new HashSet<>();
            services.add(ServiceType.ATM);
            if (random.nextDouble() > 0.5)
                services.add(ServiceType.CUSTOMER_SERVICE);
            if (random.nextDouble() > 0.7)
                services.add(ServiceType.FOREX);

            branches.add(new Branch(id, name, lat, lon, "Generated Address", "Phone",
                    services, new HashSet<>(), new OperatingHours(9, 17),
                    BranchType.FULL_SERVICE, 40, random.nextDouble() > 0.9));
        }

        return branches;
    }
}
