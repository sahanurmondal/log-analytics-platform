package company.ecommerce.ebay;

import java.util.*;

/**
 * Order Fulfillment Pipeline
 *
 * Problem: Process orders through multiple stages while respecting dependencies
 * Used by: Fulfillment centers, order management, warehouse operations
 *
 * Stages:
 * 1. Order Creation
 * 2. Payment Processing
 * 3. Inventory Allocation
 * 4. Picking
 * 5. Packing
 * 6. QC Check
 * 7. Shipping
 *
 * Algorithm: Topological Sort (Course Schedule pattern)
 * Time Complexity: O(V + E) where V = orders, E = dependencies
 * Space Complexity: O(V + E)
 */
public class OrderFulfillmentPipeline {

    static class Order {
        int orderId;
        String customerId;
        int status; // 0=created, 1=paid, 2=allocated, 3=picked, 4=packed, 5=qc, 6=shipped
        List<Integer> items;
        long createdAt;

        public Order(int id, String customer, List<Integer> items) {
            this.orderId = id;
            this.customerId = customer;
            this.items = items;
            this.status = 0;
            this.createdAt = System.currentTimeMillis();
        }
    }

    static class Stage {
        int stageId;
        String stageName;
        int requiredStatus; // Must be at this status before processing
        long processingTime;

        public Stage(int id, String name, int required, long time) {
            this.stageId = id;
            this.stageName = name;
            this.requiredStatus = required;
            this.processingTime = time;
        }
    }

    static class FulfillmentEvent {
        int orderId;
        String stage;
        long timestamp;
        String status;

        public FulfillmentEvent(int orderId, String stage, String status) {
            this.orderId = orderId;
            this.stage = stage;
            this.timestamp = System.currentTimeMillis();
            this.status = status;
        }

        @Override
        public String toString() {
            return String.format("Order %d: %s - %s (at %d)", orderId, stage, status, timestamp);
        }
    }

    private Map<Integer, Order> orders;
    private List<Stage> stages;
    private List<FulfillmentEvent> eventLog;
    private Queue<Order> processingQueue;

    // Dependencies between stages (adjacency list)
    private Map<Integer, List<Integer>> stageDependencies;

    public OrderFulfillmentPipeline() {
        this.orders = new HashMap<>();
        this.stages = new ArrayList<>();
        this.eventLog = new ArrayList<>();
        this.processingQueue = new LinkedList<>();
        this.stageDependencies = new HashMap<>();

        initializeStages();
    }

    /**
     * Initialize fulfillment stages
     */
    private void initializeStages() {
        stages.add(new Stage(0, "Order Creation", -1, 100));
        stages.add(new Stage(1, "Payment Processing", 0, 500));
        stages.add(new Stage(2, "Inventory Allocation", 1, 300));
        stages.add(new Stage(3, "Picking", 2, 1000));
        stages.add(new Stage(4, "Packing", 3, 800));
        stages.add(new Stage(5, "QC Check", 4, 600));
        stages.add(new Stage(6, "Shipping", 5, 200));

        // Build dependency graph
        stageDependencies.put(0, Arrays.asList()); // No dependencies
        stageDependencies.put(1, Arrays.asList(0)); // Depends on Order Creation
        stageDependencies.put(2, Arrays.asList(1)); // Depends on Payment
        stageDependencies.put(3, Arrays.asList(2)); // Depends on Allocation
        stageDependencies.put(4, Arrays.asList(3)); // Depends on Picking
        stageDependencies.put(5, Arrays.asList(4)); // Depends on Packing
        stageDependencies.put(6, Arrays.asList(5)); // Depends on QC
    }

    /**
     * Create new order
     * Time: O(1)
     */
    public void createOrder(int orderId, String customerId, List<Integer> items) {
        Order order = new Order(orderId, customerId, items);
        orders.put(orderId, order);
        processingQueue.offer(order);

        logEvent(orderId, "Order Creation", "CREATED");
    }

    /**
     * Process next stage for an order
     * Time: O(1)
     */
    public boolean processNextStage(int orderId) {
        if (!orders.containsKey(orderId)) {
            return false;
        }

        Order order = orders.get(orderId);
        int nextStatus = order.status + 1;

        if (nextStatus >= stages.size()) {
            logEvent(orderId, stages.get(order.status).stageName, "COMPLETED");
            return false; // Order already completed
        }

        Stage stage = stages.get(nextStatus);

        // Check if dependencies are satisfied
        if (!canProcessStage(order, nextStatus)) {
            logEvent(orderId, stage.stageName, "WAITING_FOR_DEPENDENCIES");
            return false;
        }

        // Simulate processing
        try {
            Thread.sleep(Math.min(stage.processingTime / 10, 100)); // Simulate work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        order.status = nextStatus;
        logEvent(orderId, stage.stageName, "COMPLETED");
        return true;
    }

    /**
     * Check if order meets dependencies for a stage
     * Time: O(k) where k = dependencies
     */
    private boolean canProcessStage(Order order, int stageId) {
        List<Integer> dependencies = stageDependencies.get(stageId);
        if (dependencies == null || dependencies.isEmpty()) {
            return true;
        }

        for (int depStage : dependencies) {
            if (order.status < depStage) {
                return false;
            }
        }
        return true;
    }

    /**
     * Process all orders through pipeline
     * Time: O(V * E) where V = orders, E = stages
     */
    public void processPipeline() {
        while (!processingQueue.isEmpty()) {
            Order order = processingQueue.poll();

            // Process all stages for this order
            while (order.status < stages.size() - 1) {
                if (!processNextStage(order.orderId)) {
                    processingQueue.offer(order); // Re-queue if dependencies not met
                    break;
                }
            }
        }
    }

    /**
     * Get order status
     * Time: O(1)
     */
    public String getOrderStatus(int orderId) {
        if (!orders.containsKey(orderId)) {
            return "Order not found";
        }

        Order order = orders.get(orderId);
        if (order.status >= stages.size()) {
            return "DELIVERED";
        }
        return stages.get(order.status).stageName;
    }

    /**
     * Get fulfillment timeline
     */
    public List<FulfillmentEvent> getFulfillmentTimeline(int orderId) {
        return eventLog.stream()
            .filter(e -> e.orderId == orderId)
            .toList();
    }

    /**
     * Log fulfillment event
     */
    private void logEvent(int orderId, String stage, String status) {
        FulfillmentEvent event = new FulfillmentEvent(orderId, stage, status);
        eventLog.add(event);
    }

    /**
     * Get all events
     */
    public List<FulfillmentEvent> getEventLog() {
        return new ArrayList<>(eventLog);
    }

    /**
     * Topologically sort stages to verify no cycles
     * Time: O(V + E)
     */
    public boolean validatePipeline() {
        // Check for cycles using DFS
        int n = stages.size();
        int[] visited = new int[n]; // 0=unvisited, 1=visiting, 2=visited

        for (int i = 0; i < n; i++) {
            if (hasCycleDFS(i, visited)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasCycleDFS(int node, int[] visited) {
        if (visited[node] == 1) return true; // Back edge found (cycle)
        if (visited[node] == 2) return false; // Already processed

        visited[node] = 1;

        List<Integer> dependencies = stageDependencies.getOrDefault(node, new ArrayList<>());
        for (int dep : dependencies) {
            if (hasCycleDFS(dep, visited)) {
                return true;
            }
        }

        visited[node] = 2;
        return false;
    }

    public static void main(String[] args) {
        OrderFulfillmentPipeline pipeline = new OrderFulfillmentPipeline();

        System.out.println("=== Pipeline Validation ===");
        System.out.println("Pipeline valid: " + pipeline.validatePipeline());

        System.out.println("\n=== Creating Orders ===");
        pipeline.createOrder(1001, "user123", Arrays.asList(101, 102, 103));
        pipeline.createOrder(1002, "user456", Arrays.asList(104, 105));
        pipeline.createOrder(1003, "user789", Arrays.asList(106));

        System.out.println("\n=== Processing Pipeline ===");
        pipeline.processPipeline();

        System.out.println("\n=== Order Status ===");
        System.out.println("Order 1001: " + pipeline.getOrderStatus(1001));
        System.out.println("Order 1002: " + pipeline.getOrderStatus(1002));
        System.out.println("Order 1003: " + pipeline.getOrderStatus(1003));

        System.out.println("\n=== Fulfillment Timeline (Order 1001) ===");
        pipeline.getFulfillmentTimeline(1001).forEach(System.out::println);

        System.out.println("\n=== All Events ===");
        pipeline.getEventLog().forEach(System.out::println);
    }
}

