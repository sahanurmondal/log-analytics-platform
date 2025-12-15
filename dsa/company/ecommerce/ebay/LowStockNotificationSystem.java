package company.ecommerce.ebay;

import java.util.*;

/**
 * Low Stock Notification System
 *
 * Problem: Monitor product inventory levels and notify when stock falls below threshold
 * Used by: Warehouse managers, sellers, procurement team
 *
 * Features:
 * - Real-time stock monitoring
 * - Threshold-based notifications
 * - Priority alerts for critical items
 * - Historical tracking
 *
 * Time Complexity: O(log n) per update, O(k) to retrieve alerts
 * Space Complexity: O(n) where n is number of products
 */
public class LowStockNotificationSystem {

    static class Product {
        int productId;
        String name;
        int currentStock;
        int threshold;
        long lastUpdated;

        public Product(int productId, String name, int stock, int threshold) {
            this.productId = productId;
            this.name = name;
            this.currentStock = stock;
            this.threshold = threshold;
            this.lastUpdated = System.currentTimeMillis();
        }
    }

    static class StockAlert {
        int productId;
        String productName;
        int currentStock;
        int threshold;
        long alertTime;
        String severity; // CRITICAL, WARNING, INFO

        public StockAlert(int productId, String name, int stock, int threshold) {
            this.productId = productId;
            this.productName = name;
            this.currentStock = stock;
            this.threshold = threshold;
            this.alertTime = System.currentTimeMillis();
            this.severity = calculateSeverity(stock, threshold);
        }

        private String calculateSeverity(int stock, int threshold) {
            if (stock == 0) return "CRITICAL";
            if (stock <= threshold / 2) return "WARNING";
            return "INFO";
        }

        @Override
        public String toString() {
            return String.format("[%s] %s (ID: %d) - Stock: %d, Threshold: %d, Time: %d",
                severity, productName, productId, currentStock, threshold, alertTime);
        }
    }

    // Min heap based on stock level (priority queue)
    private PriorityQueue<Product> lowStockHeap;

    // HashMap for O(1) access
    private Map<Integer, Product> productMap;

    // Alert history
    private List<StockAlert> alertHistory;

    public LowStockNotificationSystem() {
        this.lowStockHeap = new PriorityQueue<>((a, b) -> Integer.compare(a.currentStock, b.currentStock));
        this.productMap = new HashMap<>();
        this.alertHistory = new ArrayList<>();
    }

    /**
     * Add product to system
     * Time: O(log n)
     */
    public void addProduct(int productId, String name, int initialStock, int threshold) {
        Product product = new Product(productId, name, initialStock, threshold);
        productMap.put(productId, product);
        lowStockHeap.offer(product);
    }

    /**
     * Update stock level and check for alerts
     * Time: O(log n)
     */
    public List<StockAlert> updateStock(int productId, int newStock) {
        List<StockAlert> alerts = new ArrayList<>();

        if (!productMap.containsKey(productId)) {
            System.out.println("Product not found: " + productId);
            return alerts;
        }

        Product product = productMap.get(productId);
        int oldStock = product.currentStock;
        product.currentStock = newStock;
        product.lastUpdated = System.currentTimeMillis();

        // Check if stock fell below threshold
        if (newStock <= product.threshold && oldStock > product.threshold) {
            StockAlert alert = new StockAlert(productId, product.name, newStock, product.threshold);
            alertHistory.add(alert);
            alerts.add(alert);
        }

        return alerts;
    }

    /**
     * Get all products below threshold
     * Time: O(n) where n is number of products below threshold
     */
    public List<StockAlert> getCriticalProducts() {
        List<StockAlert> criticalAlerts = new ArrayList<>();

        for (Product product : productMap.values()) {
            if (product.currentStock <= product.threshold) {
                criticalAlerts.add(new StockAlert(
                    product.productId,
                    product.name,
                    product.currentStock,
                    product.threshold
                ));
            }
        }

        // Sort by stock level (ascending)
        criticalAlerts.sort((a, b) -> Integer.compare(a.currentStock, b.currentStock));
        return criticalAlerts;
    }

    /**
     * Restock product
     * Time: O(log n)
     */
    public void restock(int productId, int quantity) {
        if (productMap.containsKey(productId)) {
            Product product = productMap.get(productId);
            int oldStock = product.currentStock;
            product.currentStock += quantity;
            product.lastUpdated = System.currentTimeMillis();

            System.out.println(String.format("Restocked %s: %d -> %d units",
                product.name, oldStock, product.currentStock));
        }
    }

    /**
     * Get alert history
     */
    public List<StockAlert> getAlertHistory() {
        return new ArrayList<>(alertHistory);
    }

    /**
     * Set new threshold for product
     * Time: O(1)
     */
    public void updateThreshold(int productId, int newThreshold) {
        if (productMap.containsKey(productId)) {
            productMap.get(productId).threshold = newThreshold;
        }
    }

    public static void main(String[] args) {
        LowStockNotificationSystem system = new LowStockNotificationSystem();

        // Add products
        system.addProduct(1, "iPhone 14", 50, 20);
        system.addProduct(2, "Samsung Galaxy", 30, 15);
        system.addProduct(3, "Google Pixel", 10, 5);
        system.addProduct(4, "OnePlus", 5, 10);

        System.out.println("=== Initial Products ===");
        System.out.println("Products added successfully\n");

        // Simulate stock updates
        System.out.println("=== Stock Updates ===");
        List<StockAlert> alerts = system.updateStock(1, 15);
        if (!alerts.isEmpty()) {
            alerts.forEach(System.out::println);
        }

        system.updateStock(2, 5);
        system.updateStock(3, 2);
        system.updateStock(4, 0);

        System.out.println("\n=== Critical Products ===");
        system.getCriticalProducts().forEach(System.out::println);

        System.out.println("\n=== Restocking ===");
        system.restock(1, 100);
        system.restock(3, 50);

        System.out.println("\n=== Critical Products After Restock ===");
        system.getCriticalProducts().forEach(System.out::println);

        System.out.println("\n=== Alert History ===");
        system.getAlertHistory().forEach(System.out::println);
    }
}

