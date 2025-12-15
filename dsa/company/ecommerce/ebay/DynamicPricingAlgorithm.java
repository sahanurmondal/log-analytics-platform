package company.ecommerce.ebay;

import java.util.*;

/**
 * Dynamic Pricing Algorithm
 *
 * Problem: Adjust product prices based on demand, competition, and market conditions
 * Used by: Pricing team, sellers, revenue optimization
 *
 * Factors Considered:
 * - Demand level (high demand = higher price)
 * - Competition (competitor price reference)
 * - Stock level (low stock = higher price)
 * - Time (peak hours = higher price)
 * - Category popularity
 *
 * Algorithm: DP + Greedy approach
 * Time Complexity: O(n * m) where n = products, m = price points
 * Space Complexity: O(n * m)
 */
public class DynamicPricingAlgorithm {

    static class Product {
        int productId;
        String name;
        double baseCost;
        double basePrice;
        int currentStock;
        int demandScore; // 0-100
        double competitorPrice;
        int category;

        public Product(int id, String name, double cost, int stock, int demand) {
            this.productId = id;
            this.name = name;
            this.baseCost = cost;
            this.basePrice = cost * 1.3; // 30% markup
            this.currentStock = stock;
            this.demandScore = demand;
            this.competitorPrice = basePrice * (0.95 + (demand / 100.0) * 0.1);
            this.category = id % 5; // Simple category assignment
        }
    }

    static class PricingStrategy {
        int productId;
        double originalPrice;
        double optimizedPrice;
        double margin;
        String reason;
        long timestamp;

        public PricingStrategy(int id, double orig, double opt, String reason) {
            this.productId = id;
            this.originalPrice = orig;
            this.optimizedPrice = opt;
            this.margin = ((opt - orig) / orig) * 100;
            this.reason = reason;
            this.timestamp = System.currentTimeMillis();
        }

        @Override
        public String toString() {
            return String.format("Product %d: $%.2f → $%.2f (%.2f%%) - %s",
                productId, originalPrice, optimizedPrice, margin, reason);
        }
    }

    private Map<Integer, Product> products;
    private List<PricingStrategy> priceHistory;

    // DP table for optimization
    private double[][] dpTable;

    // Parameters
    private static final double DEMAND_MULTIPLIER = 1.2;
    private static final double STOCK_MULTIPLIER = 0.8;
    private static final double COMPETITION_FACTOR = 0.95;

    public DynamicPricingAlgorithm() {
        this.products = new HashMap<>();
        this.priceHistory = new ArrayList<>();
    }

    /**
     * Add product to pricing system
     */
    public void addProduct(int productId, String name, double cost, int stock, int demand) {
        Product product = new Product(productId, name, cost, stock, demand);
        products.put(productId, product);
    }

    /**
     * Calculate optimal price for a product
     * Time: O(m) where m is number of price points
     */
    public double calculateOptimalPrice(int productId) {
        if (!products.containsKey(productId)) {
            return 0;
        }

        Product product = products.get(productId);
        double price = product.basePrice;

        // Factor 1: Demand multiplier
        double demandFactor = 1.0 + (product.demandScore / 100.0) * (DEMAND_MULTIPLIER - 1);
        price *= demandFactor;

        // Factor 2: Stock level (scarcity pricing)
        if (product.currentStock < 10) {
            price *= (1 + (10 - product.currentStock) / 10.0 * 0.2);
        } else if (product.currentStock > 100) {
            price *= STOCK_MULTIPLIER;
        }

        // Factor 3: Competition
        if (product.competitorPrice > 0) {
            double competitionDiff = product.competitorPrice - product.basePrice;
            if (competitionDiff > 0) {
                price = Math.min(price, product.competitorPrice + (product.demandScore / 100.0) * 10);
            }
        }

        // Ensure minimum profit margin
        double minPrice = product.baseCost * 1.1; // 10% minimum margin
        price = Math.max(price, minPrice);

        return Math.round(price * 100.0) / 100.0;
    }

    /**
     * Get pricing recommendations for all products
     * Time: O(n)
     */
    public List<PricingStrategy> getPricingRecommendations() {
        List<PricingStrategy> recommendations = new ArrayList<>();

        for (Product product : products.values()) {
            double currentPrice = product.basePrice;
            double optimalPrice = calculateOptimalPrice(product.productId);

            String reason = generateReason(product, currentPrice, optimalPrice);
            PricingStrategy strategy = new PricingStrategy(
                product.productId, currentPrice, optimalPrice, reason
            );

            recommendations.add(strategy);
        }

        return recommendations;
    }

    /**
     * Generate human-readable reason for price change
     */
    private String generateReason(Product product, double current, double optimal) {
        if (optimal > current * 1.05) {
            if (product.demandScore > 70) {
                return "High demand +" + (product.demandScore / 10) + "%";
            } else if (product.currentStock < 10) {
                return "Low stock (scarcity)";
            } else {
                return "Market opportunity";
            }
        } else if (optimal < current * 0.95) {
            if (product.demandScore < 30) {
                return "Low demand -" + ((100 - product.demandScore) / 10) + "%";
            } else if (product.currentStock > 100) {
                return "Overstock clearance";
            } else {
                return "Competitive pricing";
            }
        }
        return "Optimal pricing maintained";
    }

    /**
     * Apply pricing recommendation
     * Time: O(1)
     */
    public void applyPricing(int productId, double newPrice) {
        if (products.containsKey(productId)) {
            Product product = products.get(productId);
            double oldPrice = product.basePrice;
            product.basePrice = newPrice;

            PricingStrategy strategy = new PricingStrategy(
                productId, oldPrice, newPrice, "Manual adjustment"
            );
            priceHistory.add(strategy);
        }
    }

    /**
     * Update product demand score (from sales data)
     * Time: O(1)
     */
    public void updateDemand(int productId, int demandScore) {
        if (products.containsKey(productId)) {
            products.get(productId).demandScore = Math.min(100, Math.max(0, demandScore));
        }
    }

    /**
     * Update competitor price
     * Time: O(1)
     */
    public void updateCompetitorPrice(int productId, double price) {
        if (products.containsKey(productId)) {
            products.get(productId).competitorPrice = price;
        }
    }

    /**
     * Update stock level
     * Time: O(1)
     */
    public void updateStock(int productId, int quantity) {
        if (products.containsKey(productId)) {
            products.get(productId).currentStock = quantity;
        }
    }

    /**
     * Get pricing history
     */
    public List<PricingStrategy> getPricingHistory() {
        return new ArrayList<>(priceHistory);
    }

    public static void main(String[] args) {
        DynamicPricingAlgorithm pricing = new DynamicPricingAlgorithm();

        // Add products
        pricing.addProduct(1, "iPhone 14", 500, 50, 85);  // High demand
        pricing.addProduct(2, "Samsung Galaxy", 400, 5, 60);  // Low stock
        pricing.addProduct(3, "Google Pixel", 350, 150, 30);  // Overstock, low demand
        pricing.addProduct(4, "OnePlus", 300, 20, 45);  // Moderate

        System.out.println("=== Initial Pricing Recommendations ===");
        pricing.getPricingRecommendations().forEach(System.out::println);

        System.out.println("\n=== After Demand Update ===");
        pricing.updateDemand(3, 75); // Increase demand for Google Pixel
        pricing.updateStock(2, 2); // Critical stock level
        pricing.getPricingRecommendations().forEach(System.out::println);

        System.out.println("\n=== After Competitor Price Update ===");
        pricing.updateCompetitorPrice(1, 550);
        pricing.getPricingRecommendations().forEach(System.out::println);

        System.out.println("\n=== Stock Updates ===");
        pricing.updateStock(3, 50); // Reduce overstock
        System.out.println("Products updated");

        System.out.println("\n=== Final Recommendations ===");
        pricing.getPricingRecommendations().forEach(System.out::println);
    }
}

