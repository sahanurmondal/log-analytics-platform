package company.ecommerce.ebay;

import java.util.*;

/**
 * Delivery Time Prediction
 *
 * Problem: Predict delivery time based on historical data and features
 * Used by: Shipping estimates, SLA tracking, customer communication
 *
 * Features:
 * - Order weight and dimensions
 * - Distance from warehouse to delivery address
 * - Historical average for route
 * - Carrier performance
 * - Time of order (rush hours, weekends)
 * - Weather conditions impact
 *
 * Algorithm: Linear Regression + Feature Analysis
 * Time Complexity: O(n) for prediction, O(n log n) for model training
 * Space Complexity: O(n) for storing historical data
 */
public class DeliveryTimePrediction {

    static class Shipment {
        String shipmentId;
        int originWarehouse;
        String destinationZip;
        double weight;
        int distance; // km
        String carrierType; // "STANDARD", "FAST", "EXPRESS"
        long orderTime;
        long actualDeliveryTime;
        int estimatedDays;

        public Shipment(String id, int warehouse, String zip, double weight, int distance, String carrier) {
            this.shipmentId = id;
            this.originWarehouse = warehouse;
            this.destinationZip = zip;
            this.weight = weight;
            this.distance = distance;
            this.carrierType = carrier;
            this.orderTime = System.currentTimeMillis();
            this.actualDeliveryTime = 0;
            this.estimatedDays = 0;
        }
    }

    static class DeliveryEstimate {
        String shipmentId;
        int estimatedDays;
        String estimationRange; // "2-4 business days"
        double confidence; // 0-100
        List<String> factors;

        public DeliveryEstimate(String id, int days, String range, double conf) {
            this.shipmentId = id;
            this.estimatedDays = days;
            this.estimationRange = range;
            this.confidence = Math.round(conf * 100.0) / 100.0;
            this.factors = new ArrayList<>();
        }

        @Override
        public String toString() {
            return String.format("Shipment %s | Est: %d days (%s) | Confidence: %.0f%% | Factors: %s",
                shipmentId, estimatedDays, estimationRange, confidence, factors);
        }
    }

    private List<Shipment> historicalData;
    private Map<String, Integer> zipCodeAverageDelivery; // zip -> average days
    private Map<String, Double> carrierPerformance; // carrier -> average days
    private Map<Integer, Double> warehouseDistance; // warehouse -> avg distance to delivery

    // Model parameters (learned from historical data)
    private double baseDeliveryDays = 3.0;
    private double distanceWeightPerKm = 0.005; // 0.005 days per km
    private double weightFactor = 0.1; // 0.1 days per kg

    public DeliveryTimePrediction() {
        this.historicalData = new ArrayList<>();
        this.zipCodeAverageDelivery = new HashMap<>();
        this.carrierPerformance = new HashMap<>();
        this.warehouseDistance = new HashMap<>();
    }

    /**
     * Record historical shipment data
     * Time: O(1)
     */
    public void recordHistoricalData(String shipmentId, int warehouse, String zip,
                                    double weight, int distance, String carrier, int deliveryDays) {
        Shipment shipment = new Shipment(shipmentId, warehouse, zip, weight, distance, carrier);
        shipment.actualDeliveryTime = shipment.orderTime + (deliveryDays * 24L * 60L * 60L * 1000L);
        shipment.estimatedDays = deliveryDays;

        historicalData.add(shipment);

        // Update aggregates
        updateStatistics(shipment, deliveryDays);
    }

    /**
     * Update statistics from new shipment
     * Time: O(1)
     */
    private void updateStatistics(Shipment shipment, int deliveryDays) {
        // Update zip code average
        String zip = shipment.destinationZip;
        int currentAvg = zipCodeAverageDelivery.getOrDefault(zip, 0);
        int count = (int) historicalData.stream().filter(s -> s.destinationZip.equals(zip)).count();
        zipCodeAverageDelivery.put(zip, (currentAvg * (count - 1) + deliveryDays) / count);

        // Update carrier performance
        String carrier = shipment.carrierType;
        double carrierAvg = carrierPerformance.getOrDefault(carrier, 0.0);
        count = (int) historicalData.stream().filter(s -> s.carrierType.equals(carrier)).count();
        carrierPerformance.put(carrier, (carrierAvg * (count - 1) + deliveryDays) / count);
    }

    /**
     * Predict delivery time for new shipment
     * Time: O(n) for analysis
     */
    public DeliveryEstimate predictDeliveryTime(int warehouse, String zip, double weight, int distance, String carrier) {
        // Base calculation
        double predictedDays = baseDeliveryDays;

        // Factor 1: Distance
        predictedDays += distance * distanceWeightPerKm;

        // Factor 2: Weight
        predictedDays += weight * weightFactor;

        // Factor 3: Carrier type adjustments
        double carrierMultiplier = getCarrierMultiplier(carrier);
        predictedDays *= carrierMultiplier;

        // Factor 4: Historical zip code performance
        if (zipCodeAverageDelivery.containsKey(zip)) {
            double zipAvg = zipCodeAverageDelivery.get(zip);
            predictedDays = predictedDays * 0.7 + zipAvg * 0.3; // Weight historical data
        }

        // Calculate confidence
        double confidence = calculateConfidence(warehouse, zip, carrier);

        // Round to business days (0.5 day = next business day)
        int estimatedDays = (int) Math.ceil(predictedDays);
        String estimationRange = generateEstimationRange(estimatedDays);

        DeliveryEstimate estimate = new DeliveryEstimate("", estimatedDays, estimationRange, confidence);

        // Add factors for explanation
        estimate.factors.add("Distance: " + distance + " km");
        estimate.factors.add("Weight: " + weight + " kg");
        estimate.factors.add("Carrier: " + carrier);
        estimate.factors.add("Zip avg: " + zipCodeAverageDelivery.getOrDefault(zip, estimatedDays) + " days");

        return estimate;
    }

    /**
     * Get carrier performance multiplier
     * Time: O(1)
     */
    private double getCarrierMultiplier(String carrier) {
        return switch(carrier) {
            case "EXPRESS" -> 0.7;  // 30% faster
            case "FAST" -> 0.85;    // 15% faster
            default -> 1.0;         // STANDARD
        };
    }

    /**
     * Calculate confidence in prediction
     * Time: O(n)
     */
    private double calculateConfidence(int warehouse, String zip, String carrier) {
        // Base confidence
        double confidence = 70.0;

        // More historical data = higher confidence
        long zipcodeCount = historicalData.stream()
            .filter(s -> s.destinationZip.equals(zip))
            .count();

        confidence += Math.min(20.0, zipcodeCount * 0.5);

        // Carrier reliability bonus
        long carrierCount = historicalData.stream()
            .filter(s -> s.carrierType.equals(carrier))
            .count();

        confidence += Math.min(10.0, carrierCount * 0.2);

        return Math.min(99.0, confidence);
    }

    /**
     * Generate human-readable estimation range
     */
    private String generateEstimationRange(int estimatedDays) {
        int minDays = Math.max(1, estimatedDays - 1);
        int maxDays = estimatedDays + 1;

        return minDays + "-" + maxDays + " business days";
    }

    /**
     * Get delivery statistics for a route
     * Time: O(n)
     */
    public Map<String, Object> getRouteStatistics(String zip) {
        List<Shipment> routeShipments = historicalData.stream()
            .filter(s -> s.destinationZip.equals(zip))
            .toList();

        if (routeShipments.isEmpty()) {
            return new HashMap<>();
        }

        double avgDays = routeShipments.stream()
            .mapToInt(s -> s.estimatedDays)
            .average()
            .orElse(0);

        int minDays = routeShipments.stream()
            .mapToInt(s -> s.estimatedDays)
            .min()
            .orElse(0);

        int maxDays = routeShipments.stream()
            .mapToInt(s -> s.estimatedDays)
            .max()
            .orElse(0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("zip", zip);
        stats.put("avgDeliveryDays", String.format("%.1f", avgDays));
        stats.put("minDays", minDays);
        stats.put("maxDays", maxDays);
        stats.put("totalShipments", routeShipments.size());

        return stats;
    }

    /**
     * Train model on historical data
     * Time: O(n)
     */
    public void trainModel() {
        if (historicalData.isEmpty()) {
            return;
        }

        // Analyze historical data to refine parameters
        double totalDays = 0;
        double totalDistance = 0;

        for (Shipment s : historicalData) {
            totalDays += s.estimatedDays;
            totalDistance += s.distance;
        }

        double avgDays = totalDays / historicalData.size();
        double avgDistance = totalDistance / historicalData.size();

        // Recalibrate model parameters
        baseDeliveryDays = avgDays * 0.5;
        distanceWeightPerKm = (avgDays * 0.5) / avgDistance;
    }

    public static void main(String[] args) {
        DeliveryTimePrediction predictor = new DeliveryTimePrediction();

        System.out.println("=== Recording Historical Data ===");
        // Record some historical shipments
        predictor.recordHistoricalData("SHP001", 1, "10001", 1.5, 50, "STANDARD", 3);
        predictor.recordHistoricalData("SHP002", 1, "10001", 2.0, 50, "STANDARD", 3);
        predictor.recordHistoricalData("SHP003", 1, "10001", 1.0, 50, "FAST", 2);
        predictor.recordHistoricalData("SHP004", 2, "90210", 0.5, 500, "STANDARD", 5);
        predictor.recordHistoricalData("SHP005", 2, "90210", 3.0, 500, "EXPRESS", 3);

        System.out.println("Historical data recorded\n");

        System.out.println("=== Training Model ===");
        predictor.trainModel();
        System.out.println("Model trained\n");

        System.out.println("=== Predictions ===");
        DeliveryEstimate est1 = predictor.predictDeliveryTime(1, "10001", 1.5, 50, "STANDARD");
        System.out.println(est1);

        DeliveryEstimate est2 = predictor.predictDeliveryTime(2, "90210", 2.0, 500, "EXPRESS");
        System.out.println(est2);

        System.out.println("\n=== Route Statistics ===");
        System.out.println(predictor.getRouteStatistics("10001"));
        System.out.println(predictor.getRouteStatistics("90210"));
    }
}

