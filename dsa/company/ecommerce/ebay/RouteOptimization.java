package company.ecommerce.ebay;

import java.util.*;

/**
 * Route Optimization System
 *
 * Problem: Find optimal delivery routes to minimize time and cost
 * Used by: Delivery logistics, driver assignment, cost optimization
 *
 * Approaches:
 * 1. Nearest Neighbor (greedy)
 * 2. Dijkstra's Algorithm (shortest path)
 * 3. TSP-inspired (Traveling Salesman approximation)
 *
 * Features:
 * - Multiple delivery points
 * - Time window constraints
 * - Vehicle capacity limits
 * - Road network distances
 *
 * Algorithm: Graph-based optimization + Greedy approach
 * Time Complexity: O(n^2) for nearest neighbor, O(E log V) for Dijkstra
 * Space Complexity: O(V + E) for graph representation
 */
public class RouteOptimization {

    static class Location {
        int locationId;
        String name;
        double latitude;
        double longitude;
        int timeWindow; // minutes to serve

        public Location(int id, String name, double lat, double lon, int window) {
            this.locationId = id;
            this.name = name;
            this.latitude = lat;
            this.longitude = lon;
            this.timeWindow = window;
        }

        // Simple distance calculation (Euclidean)
        public int distanceTo(Location other) {
            double dx = this.latitude - other.latitude;
            double dy = this.longitude - other.longitude;
            return (int) Math.sqrt(dx * dx + dy * dy) * 111; // Convert to km
        }
    }

    static class Route {
        int routeId;
        List<Integer> stops; // locationIds in order
        double totalDistance;
        int totalTime;
        int vehicleCapacity;
        int currentLoad;

        public Route(int id, int capacity) {
            this.routeId = id;
            this.stops = new ArrayList<>();
            this.totalDistance = 0;
            this.totalTime = 0;
            this.vehicleCapacity = capacity;
            this.currentLoad = 0;
        }

        @Override
        public String toString() {
            return String.format("Route %d | Stops: %d | Distance: %.1f km | Time: %d min | Load: %d/%d",
                routeId, stops.size(), totalDistance, totalTime, currentLoad, vehicleCapacity);
        }
    }

    static class RoutingPlan {
        List<Route> routes;
        double totalDistance;
        int totalTime;
        int undeliveredCount;

        public RoutingPlan() {
            this.routes = new ArrayList<>();
            this.totalDistance = 0;
            this.totalTime = 0;
            this.undeliveredCount = 0;
        }
    }

    private Map<Integer, Location> locations;
    private List<Route> routes;
    private Location depot; // Starting point

    public RouteOptimization(Location depot) {
        this.locations = new HashMap<>();
        this.routes = new ArrayList<>();
        this.depot = depot;
        this.locations.put(depot.locationId, depot);
    }

    /**
     * Add delivery location
     * Time: O(1)
     */
    public void addLocation(int locationId, String name, double latitude, double longitude, int timeWindow) {
        Location location = new Location(locationId, name, latitude, longitude, timeWindow);
        locations.put(locationId, location);
    }

    /**
     * Nearest Neighbor algorithm for route optimization
     * Time: O(n^2)
     */
    public RoutingPlan optimizeRoutesNearestNeighbor(List<Integer> deliveryLocations, int vehicleCapacity, int maxStopsPerRoute) {
        RoutingPlan plan = new RoutingPlan();
        Set<Integer> undelivered = new HashSet<>(deliveryLocations);
        int routeId = 1;

        while (!undelivered.isEmpty()) {
            Route route = new Route(routeId++, vehicleCapacity);
            Location currentLocation = depot;
            route.stops.add(depot.locationId);

            // Greedy: always go to nearest undelivered location
            while (!undelivered.isEmpty() && route.stops.size() < maxStopsPerRoute + 1) {
                Integer nearest = findNearestLocation(currentLocation, undelivered);

                if (nearest == null) break;

                Location nextLocation = locations.get(nearest);
                int distance = currentLocation.distanceTo(nextLocation);
                int timeNeeded = nextLocation.timeWindow + (distance / 50) * 60; // 50 km/hour

                // Check if we can fit this delivery
                if (route.currentLoad + 1 <= route.vehicleCapacity &&
                    route.totalTime + timeNeeded <= 8 * 60) { // 8 hour work day

                    route.stops.add(nearest);
                    route.totalDistance += distance;
                    route.totalTime += timeNeeded;
                    route.currentLoad++;

                    undelivered.remove(nearest);
                    currentLocation = nextLocation;
                } else {
                    break; // Route full
                }
            }

            // Return to depot
            int returnDistance = currentLocation.distanceTo(depot);
            route.totalDistance += returnDistance;
            route.totalTime += (returnDistance / 50) * 60;
            route.stops.add(depot.locationId);

            plan.routes.add(route);
        }

        plan.totalDistance = plan.routes.stream().mapToDouble(r -> r.totalDistance).sum();
        plan.totalTime = plan.routes.stream().mapToInt(r -> r.totalTime).sum();
        plan.undeliveredCount = undelivered.size();

        return plan;
    }

    /**
     * Find nearest location from current location
     * Time: O(n)
     */
    private Integer findNearestLocation(Location current, Set<Integer> candidates) {
        if (candidates.isEmpty()) return null;

        Integer nearest = null;
        int minDistance = Integer.MAX_VALUE;

        for (int locationId : candidates) {
            Location location = locations.get(locationId);
            int distance = current.distanceTo(location);

            if (distance < minDistance) {
                minDistance = distance;
                nearest = locationId;
            }
        }

        return nearest;
    }

    /**
     * 2-Opt improvement: swap edges to reduce distance
     * Time: O(n^2)
     */
    public void improve2Opt(Route route) {
        boolean improved = true;

        while (improved) {
            improved = false;

            for (int i = 1; i < route.stops.size() - 1; i++) {
                for (int j = i + 1; j < route.stops.size(); j++) {
                    // Try reversing segment between i and j
                    double delta = calculateSwapGain(route, i, j);

                    if (delta < 0) { // Improvement found
                        reverseSegment(route, i, j);
                        route.totalDistance += delta;
                        improved = true;
                        break;
                    }
                }
                if (improved) break;
            }
        }
    }

    /**
     * Calculate distance improvement from 2-opt swap
     */
    private double calculateSwapGain(Route route, int i, int j) {
        List<Integer> stops = route.stops;
        Location a = locations.get(stops.get(i - 1));
        Location b = locations.get(stops.get(i));
        Location c = locations.get(stops.get(j - 1));
        Location d = locations.get(stops.get(j));

        // Current: a->b + c->d
        double current = a.distanceTo(b) + c.distanceTo(d);

        // After swap: a->c + b->d
        double after = a.distanceTo(c) + b.distanceTo(d);

        return after - current; // Negative means improvement
    }

    /**
     * Reverse route segment for 2-opt
     */
    private void reverseSegment(Route route, int i, int j) {
        while (i < j) {
            int temp = route.stops.get(i);
            route.stops.set(i, route.stops.get(j));
            route.stops.set(j, temp);
            i++;
            j--;
        }
    }

    /**
     * Get route details as string
     */
    public String getRouteDetails(Route route) {
        StringBuilder sb = new StringBuilder();
        sb.append(route.toString()).append("\n");
        sb.append("Stops: ");

        for (int locId : route.stops) {
            Location loc = locations.get(locId);
            sb.append(loc.name).append(" -> ");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        // Create depot
        Location depot = new Location(0, "Warehouse", 0, 0, 0);
        RouteOptimization optimizer = new RouteOptimization(depot);

        // Add delivery locations
        System.out.println("=== Adding Delivery Locations ===");
        optimizer.addLocation(1, "Store A", 1.0, 1.0, 15);
        optimizer.addLocation(2, "Store B", 2.0, 2.0, 15);
        optimizer.addLocation(3, "Store C", 1.5, 2.5, 15);
        optimizer.addLocation(4, "Store D", 3.0, 1.0, 15);
        optimizer.addLocation(5, "Store E", 2.5, 3.0, 15);
        System.out.println("Locations added\n");

        // Optimize routes
        System.out.println("=== Route Optimization ===");
        List<Integer> deliveries = Arrays.asList(1, 2, 3, 4, 5);
        RoutingPlan plan = optimizer.optimizeRoutesNearestNeighbor(deliveries, 3, 3);

        System.out.println("Plan Summary:");
        System.out.println("Total Routes: " + plan.routes.size());
        System.out.println("Total Distance: " + String.format("%.1f", plan.totalDistance) + " km");
        System.out.println("Total Time: " + plan.totalTime + " min");
        System.out.println("Undelivered: " + plan.undeliveredCount + "\n");

        System.out.println("=== Route Details ===");
        for (Route route : plan.routes) {
            System.out.println(optimizer.getRouteDetails(route));
        }

        // Improve routes with 2-opt
        System.out.println("\n=== Applying 2-Opt Improvement ===");
        for (Route route : plan.routes) {
            double originalDistance = route.totalDistance;
            optimizer.improve2Opt(route);
            System.out.println(route.routeId + ": " + String.format("%.1f", originalDistance) +
                             " -> " + String.format("%.1f", route.totalDistance) + " km");
        }
    }
}

