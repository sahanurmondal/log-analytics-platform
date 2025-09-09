package miscellaneous.amazon;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Custom Amazon Interview Questions (2021-2024)
 * Based on real interview experiences from LeetCode Discuss and Glassdoor
 */
public class CustomQuestions {

    /**
     * Custom Question: Design a Package Delivery Optimization System
     * 
     * Description:
     * Design a system that optimizes package delivery routes for delivery trucks.
     * Consider factors like:
     * - Package priority (Prime, regular)
     * - Delivery time windows
     * - Truck capacity
     * - Traffic conditions
     * - Fuel efficiency
     * 
     * Company: Amazon
     * Difficulty: Hard
     * Asked: System design interviews 2023-2024
     */
    class DeliveryOptimization {
        class Package {
            String id;
            String address;
            int priority; // 1 = Prime, 2 = regular
            int weight;
            int timeWindow; // preferred delivery hour

            Package(String id, String address, int priority, int weight, int timeWindow) {
                this.id = id;
                this.address = address;
                this.priority = priority;
                this.weight = weight;
                this.timeWindow = timeWindow;
            }
        }

        class DeliveryRoute {
            List<Package> packages;
            double totalDistance;
            double estimatedTime;

            DeliveryRoute() {
                this.packages = new ArrayList<>();
            }
        }

        public DeliveryRoute optimizeRoute(List<Package> packages, int truckCapacity) {
            // Sort packages by priority and time window
            packages.sort((p1, p2) -> {
                if (p1.priority != p2.priority) {
                    return Integer.compare(p1.priority, p2.priority);
                }
                return Integer.compare(p1.timeWindow, p2.timeWindow);
            });

            DeliveryRoute route = new DeliveryRoute();
            int currentCapacity = 0;

            for (Package pkg : packages) {
                if (currentCapacity + pkg.weight <= truckCapacity) {
                    route.packages.add(pkg);
                    currentCapacity += pkg.weight;
                    route.totalDistance += calculateDistance(route.packages.size() - 1, pkg.address);
                }
            }

            route.estimatedTime = calculateEstimatedTime(route);
            return route;
        }

        private double calculateDistance(int currentStop, String address) {
            // Simplified distance calculation
            return Math.random() * 10 + 1;
        }

        private double calculateEstimatedTime(DeliveryRoute route) {
            return route.totalDistance * 0.5 + route.packages.size() * 0.1;
        }
    }

    /**
     * Custom Question: Design a Inventory Management System
     * 
     * Description:
     * Build a system that tracks inventory across multiple warehouses:
     * - Real-time inventory updates
     * - Automatic reordering when stock is low
     * - Demand forecasting
     * - Optimal warehouse selection for orders
     * 
     * Company: Amazon
     * Difficulty: Medium
     * Asked: Backend interviews 2023-2024
     */
    class InventoryManagement {
        class Product {
            String id;
            String name;
            int reorderPoint;
            int maxStock;

            Product(String id, String name, int reorderPoint, int maxStock) {
                this.id = id;
                this.name = name;
                this.reorderPoint = reorderPoint;
                this.maxStock = maxStock;
            }
        }

        class Warehouse {
            String id;
            String location;
            Map<String, Integer> inventory;

            Warehouse(String id, String location) {
                this.id = id;
                this.location = location;
                this.inventory = new HashMap<>();
            }
        }

        private Map<String, Product> products = new HashMap<>();
        private Map<String, Warehouse> warehouses = new HashMap<>();
        private Map<String, Queue<Integer>> demandHistory = new HashMap<>();

        public boolean reserveInventory(String productId, int quantity, String preferredWarehouse) {
            // Try preferred warehouse first
            Warehouse warehouse = warehouses.get(preferredWarehouse);
            if (warehouse != null && warehouse.inventory.getOrDefault(productId, 0) >= quantity) {
                warehouse.inventory.put(productId, warehouse.inventory.get(productId) - quantity);
                checkReorderPoint(productId, warehouse);
                return true;
            }

            // Try other warehouses
            for (Warehouse w : warehouses.values()) {
                if (w.inventory.getOrDefault(productId, 0) >= quantity) {
                    w.inventory.put(productId, w.inventory.get(productId) - quantity);
                    checkReorderPoint(productId, w);
                    return true;
                }
            }

            return false;
        }

        private void checkReorderPoint(String productId, Warehouse warehouse) {
            Product product = products.get(productId);
            int currentStock = warehouse.inventory.getOrDefault(productId, 0);

            if (currentStock <= product.reorderPoint) {
                int reorderQuantity = product.maxStock - currentStock;
                scheduleReorder(productId, warehouse.id, reorderQuantity);
            }
        }

        private void scheduleReorder(String productId, String warehouseId, int quantity) {
            // Simulate reorder process
            System.out.println("Reordering " + quantity + " units of " + productId + " for warehouse " + warehouseId);
        }

        public int forecastDemand(String productId, int days) {
            Queue<Integer> history = demandHistory.getOrDefault(productId, new LinkedList<>());
            if (history.isEmpty())
                return 0;

            double averageDemand = history.stream().mapToInt(Integer::intValue).average().orElse(0);
            return (int) (averageDemand * days * 1.1); // 10% buffer
        }

        public void updateDemandHistory(String productId, int demand) {
            Queue<Integer> history = demandHistory.computeIfAbsent(productId, k -> new LinkedList<>());
            history.offer(demand);

            // Keep only last 30 days
            while (history.size() > 30) {
                history.poll();
            }
        }
    }

    /**
     * Custom Question: Design a Price Comparison and Dynamic Pricing System
     * 
     * Description:
     * Build a system that:
     * - Monitors competitor prices
     * - Adjusts prices dynamically based on demand, inventory, and competition
     * - Implements different pricing strategies (penetration, skimming, etc.)
     * - Handles flash sales and promotions
     * 
     * Company: Amazon
     * Difficulty: Hard
     * Asked: Senior engineer interviews 2023-2024
     */
    class DynamicPricing {
        class PriceHistory {
            String productId;
            double price;
            long timestamp;
            String source; // "competitor", "internal"

            PriceHistory(String productId, double price, String source) {
                this.productId = productId;
                this.price = price;
                this.timestamp = System.currentTimeMillis();
                this.source = source;
            }
        }

        class PricingStrategy {
            double minMargin;
            double maxMargin;
            double demandSensitivity;

            PricingStrategy(double minMargin, double maxMargin, double demandSensitivity) {
                this.minMargin = minMargin;
                this.maxMargin = maxMargin;
                this.demandSensitivity = demandSensitivity;
            }
        }

        private Map<String, List<PriceHistory>> priceHistory = new HashMap<>();
        private Map<String, PricingStrategy> pricingStrategies = new HashMap<>();
        private Map<String, Double> currentPrices = new HashMap<>();

        public double calculateOptimalPrice(String productId, double cost, int inventoryLevel, int demandLevel) {
            PricingStrategy strategy = pricingStrategies.getOrDefault(productId,
                    new PricingStrategy(0.1, 0.5, 1.0));

            double basePrice = cost * (1 + strategy.minMargin);
            double competitorPrice = getAverageCompetitorPrice(productId);

            // Adjust for demand
            double demandMultiplier = 1.0 + (demandLevel - 50) * strategy.demandSensitivity / 100.0;

            // Adjust for inventory
            double inventoryMultiplier = inventoryLevel < 10 ? 1.1 : inventoryLevel > 100 ? 0.9 : 1.0;

            double calculatedPrice = basePrice * demandMultiplier * inventoryMultiplier;

            // Don't price too far from competition
            if (competitorPrice > 0) {
                calculatedPrice = Math.max(calculatedPrice, competitorPrice * 0.8);
                calculatedPrice = Math.min(calculatedPrice, competitorPrice * 1.2);
            }

            // Respect margin constraints
            double maxPrice = cost * (1 + strategy.maxMargin);
            calculatedPrice = Math.min(calculatedPrice, maxPrice);

            return calculatedPrice;
        }

        private double getAverageCompetitorPrice(String productId) {
            List<PriceHistory> history = priceHistory.getOrDefault(productId, new ArrayList<>());
            long twentyFourHoursAgo = System.currentTimeMillis() - 24 * 60 * 60 * 1000;

            return history.stream()
                    .filter(h -> h.source.equals("competitor") && h.timestamp > twentyFourHoursAgo)
                    .mapToDouble(h -> h.price)
                    .average()
                    .orElse(0.0);
        }

        public void updateCompetitorPrice(String productId, double price, String competitor) {
            List<PriceHistory> history = priceHistory.computeIfAbsent(productId, k -> new ArrayList<>());
            history.add(new PriceHistory(productId, price, "competitor"));

            // Trigger price recalculation if significant change
            double currentPrice = currentPrices.getOrDefault(productId, 0.0);
            if (Math.abs(price - currentPrice) / currentPrice > 0.05) { // 5% change
                // Trigger price update
                notifyPriceUpdate(productId);
            }
        }

        private void notifyPriceUpdate(String productId) {
            System.out.println("Price update triggered for product: " + productId);
        }
    }

    public static void main(String[] args) {
        // Test implementations would go here
        System.out.println("Custom Amazon interview questions implemented");
    }
}
