package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Variation: Design Distributed Max Stack
 *
 * Description:
 * Design a distributed stack that supports push, pop, top, and retrieving the
 * maximum element in constant time.
 *
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 10^6 calls will be made to push, pop, top, and getMax.
 *
 * Follow-up:
 * - Can you optimize for thread safety?
 * - Can you generalize for min stack and replication?
 * 
 * Time Complexity: O(1) for all operations (amortized across nodes)
 * Space Complexity: O(n/k) per node where n is elements, k is nodes
 * 
 * Company Tags: System Design, Distributed Systems
 */
public class DesignDistributedStackWithMax {

    // Node in the distributed system
    private static class StackNode {
        String nodeId;
        Stack<Integer> stack;
        Stack<Integer> maxStack;
        final ReentrantReadWriteLock lock;
        boolean isHealthy;
        long lastHeartbeat;

        StackNode(String nodeId) {
            this.nodeId = nodeId;
            this.stack = new Stack<>();
            this.maxStack = new Stack<>();
            this.lock = new ReentrantReadWriteLock();
            this.isHealthy = true;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        void push(int val) {
            lock.writeLock().lock();
            try {
                stack.push(val);
                if (maxStack.isEmpty() || val >= maxStack.peek()) {
                    maxStack.push(val);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        int pop() {
            lock.writeLock().lock();
            try {
                if (stack.isEmpty()) {
                    return Integer.MIN_VALUE; // Indicates empty
                }

                int val = stack.pop();
                if (!maxStack.isEmpty() && maxStack.peek() == val) {
                    maxStack.pop();
                }
                return val;
            } finally {
                lock.writeLock().unlock();
            }
        }

        int top() {
            lock.readLock().lock();
            try {
                return stack.isEmpty() ? Integer.MIN_VALUE : stack.peek();
            } finally {
                lock.readLock().unlock();
            }
        }

        int getMax() {
            lock.readLock().lock();
            try {
                return maxStack.isEmpty() ? Integer.MIN_VALUE : maxStack.peek();
            } finally {
                lock.readLock().unlock();
            }
        }

        int size() {
            lock.readLock().lock();
            try {
                return stack.size();
            } finally {
                lock.readLock().unlock();
            }
        }

        boolean isEmpty() {
            return size() == 0;
        }
    }

    private final List<StackNode> nodes;
    private final ExecutorService executorService;
    private final ScheduledExecutorService healthChecker;
    private int currentNodeIndex; // Round-robin for push operations
    private final ReentrantReadWriteLock globalLock;
    private final Map<String, Object> metadata;

    public DesignDistributedStackWithMax(int nodeCount) {
        if (nodeCount <= 0) {
            throw new IllegalArgumentException("Node count must be positive");
        }

        this.nodes = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
        this.healthChecker = Executors.newScheduledThreadPool(1);
        this.currentNodeIndex = 0;
        this.globalLock = new ReentrantReadWriteLock();
        this.metadata = new ConcurrentHashMap<>();

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            nodes.add(new StackNode("node-" + i));
        }

        startHealthMonitoring();

        System.out.println("Initialized Distributed Stack with " + nodeCount + " nodes");
    }

    public void push(int val) {
        globalLock.readLock().lock();
        try {
            StackNode targetNode = selectNodeForPush();
            if (targetNode != null) {
                targetNode.push(val);
                updateMetadata("totalElements", getTotalSize());
                System.out.println("Pushed " + val + " to " + targetNode.nodeId);
            } else {
                throw new RuntimeException("No healthy nodes available for push operation");
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public int pop() {
        globalLock.readLock().lock();
        try {
            // Find the node with the most recent element (last push)
            StackNode targetNode = selectNodeForPop();
            if (targetNode != null) {
                int result = targetNode.pop();
                if (result != Integer.MIN_VALUE) {
                    updateMetadata("totalElements", getTotalSize());
                    System.out.println("Popped " + result + " from " + targetNode.nodeId);
                    return result;
                }
            }

            // No elements found in any node
            throw new RuntimeException("Stack is empty");
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public int top() {
        globalLock.readLock().lock();
        try {
            StackNode targetNode = selectNodeForPop();
            if (targetNode != null) {
                int result = targetNode.top();
                if (result != Integer.MIN_VALUE) {
                    return result;
                }
            }

            throw new RuntimeException("Stack is empty");
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public int getMax() {
        globalLock.readLock().lock();
        try {
            OptionalInt globalMax = nodes.stream()
                    .filter(node -> node.isHealthy && !node.isEmpty())
                    .mapToInt(StackNode::getMax)
                    .filter(max -> max != Integer.MIN_VALUE)
                    .max();

            if (globalMax.isPresent()) {
                return globalMax.getAsInt();
            } else {
                throw new RuntimeException("Stack is empty");
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    // Additional utility methods

    public int getTotalSize() {
        return nodes.stream()
                .filter(node -> node.isHealthy)
                .mapToInt(StackNode::size)
                .sum();
    }

    public boolean isEmpty() {
        return getTotalSize() == 0;
    }

    public List<String> getHealthyNodes() {
        return nodes.stream()
                .filter(node -> node.isHealthy)
                .map(node -> node.nodeId)
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getNodeSizes() {
        Map<String, Integer> sizes = new HashMap<>();
        for (StackNode node : nodes) {
            sizes.put(node.nodeId, node.size());
        }
        return sizes;
    }

    public void addNode(String nodeId) {
        globalLock.writeLock().lock();
        try {
            nodes.add(new StackNode(nodeId));
            System.out.println("Added new node: " + nodeId);
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    public boolean removeNode(String nodeId) {
        globalLock.writeLock().lock();
        try {
            // Find and remove the node, but first migrate its data
            StackNode nodeToRemove = nodes.stream()
                    .filter(node -> node.nodeId.equals(nodeId))
                    .findFirst()
                    .orElse(null);

            if (nodeToRemove != null) {
                // Migrate data to other nodes before removal
                migrateNodeData(nodeToRemove);
                nodes.remove(nodeToRemove);
                System.out.println("Removed node: " + nodeId);
                return true;
            }
            return false;
        } finally {
            globalLock.writeLock().unlock();
        }
    }

    // Private helper methods

    private StackNode selectNodeForPush() {
        // Round-robin selection among healthy nodes
        List<StackNode> healthyNodes = nodes.stream()
                .filter(node -> node.isHealthy)
                .collect(Collectors.toList());

        if (healthyNodes.isEmpty()) {
            return null;
        }

        // Update current node index in a round-robin fashion
        currentNodeIndex = (currentNodeIndex + 1) % healthyNodes.size();
        return healthyNodes.get(currentNodeIndex);
    }

    private StackNode selectNodeForPop() {
        // Find the node with elements, preferring the one with most recent push
        return nodes.stream()
                .filter(node -> node.isHealthy && !node.isEmpty())
                .max(Comparator.comparingLong(node -> node.lastHeartbeat))
                .orElse(null);
    }

    private void migrateNodeData(StackNode sourceNode) {
        List<Integer> elements = new ArrayList<>();

        // Extract all elements from source node
        while (!sourceNode.isEmpty()) {
            int val = sourceNode.pop();
            if (val != Integer.MIN_VALUE) {
                elements.add(val);
            }
        }

        // Redistribute elements to remaining healthy nodes
        Collections.reverse(elements); // Restore original order
        for (int val : elements) {
            StackNode targetNode = selectNodeForPush();
            if (targetNode != null && !targetNode.nodeId.equals(sourceNode.nodeId)) {
                targetNode.push(val);
            }
        }

        System.out.println("Migrated " + elements.size() + " elements from " + sourceNode.nodeId);
    }

    private void startHealthMonitoring() {
        healthChecker.scheduleWithFixedDelay(() -> {
            long currentTime = System.currentTimeMillis();
            for (StackNode node : nodes) {
                // Simulate heartbeat updates and health checks
                node.lastHeartbeat = currentTime;

                // Simulate occasional node failures (1% chance)
                if (Math.random() < 0.01) {
                    node.isHealthy = false;
                    System.out.println("Node " + node.nodeId + " marked as unhealthy");
                } else if (!node.isHealthy && Math.random() < 0.1) {
                    // Recovery (10% chance if unhealthy)
                    node.isHealthy = true;
                    System.out.println("Node " + node.nodeId + " recovered");
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    private void updateMetadata(String key, Object value) {
        metadata.put(key, value);
        metadata.put("lastUpdated", System.currentTimeMillis());
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNodes", nodes.size());
        stats.put("healthyNodes", getHealthyNodes().size());
        stats.put("totalElements", getTotalSize());
        stats.put("nodeSizes", getNodeSizes());
        stats.put("globalMax", isEmpty() ? null : getMax());
        stats.putAll(metadata);
        return stats;
    }

    public void shutdown() {
        healthChecker.shutdown();
        executorService.shutdown();
        try {
            if (!healthChecker.awaitTermination(5, TimeUnit.SECONDS)) {
                healthChecker.shutdownNow();
            }
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            healthChecker.shutdownNow();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Distributed Max Stack Tests ===");

        DesignDistributedStackWithMax stack = new DesignDistributedStackWithMax(3);

        System.out.println("\n--- Basic Operations Test ---");
        stack.push(-2);
        stack.push(0);
        stack.push(-3);
        System.out.println("Max after pushes: " + stack.getMax()); // 0

        System.out.println("Top: " + stack.top()); // -3
        System.out.println("Pop: " + stack.pop()); // -3
        System.out.println("Max after pop: " + stack.getMax()); // 0

        System.out.println("\n--- Load Distribution Test ---");
        for (int i = 1; i <= 10; i++) {
            stack.push(i);
        }

        System.out.println("Node sizes: " + stack.getNodeSizes());
        System.out.println("Total size: " + stack.getTotalSize());
        System.out.println("Global max: " + stack.getMax());

        System.out.println("\n--- Pop Operations Test ---");
        for (int i = 0; i < 5; i++) {
            System.out.println("Popped: " + stack.pop());
            System.out.println("Current max: " + stack.getMax());
        }

        System.out.println("\n--- Node Management Test ---");
        stack.addNode("node-extra");
        System.out.println("Healthy nodes: " + stack.getHealthyNodes());

        // Add more elements to distribute across new node
        for (int i = 100; i <= 105; i++) {
            stack.push(i);
        }

        System.out.println("Node sizes after adding node: " + stack.getNodeSizes());

        // Remove a node (with data migration)
        stack.removeNode("node-0");
        System.out.println("Node sizes after removing node-0: " + stack.getNodeSizes());

        System.out.println("\n--- Edge Cases Test ---");
        // Pop until empty
        try {
            while (!stack.isEmpty()) {
                stack.pop();
            }
            System.out.println("Stack is now empty");

            // Try operations on empty stack
            stack.getMax(); // Should throw exception
        } catch (RuntimeException e) {
            System.out.println("Expected exception on empty stack: " + e.getMessage());
        }

        System.out.println("\n--- System Statistics ---");
        Map<String, Object> stats = stack.getSystemStats();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        System.out.println("\n--- Concurrent Access Test ---");
        DesignDistributedStackWithMax concurrentStack = new DesignDistributedStackWithMax(2);

        // Concurrent pushes
        Thread pusher1 = new Thread(() -> {
            for (int i = 1; i <= 50; i++) {
                concurrentStack.push(i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread pusher2 = new Thread(() -> {
            for (int i = 51; i <= 100; i++) {
                concurrentStack.push(i);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread popper = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                try {
                    Thread.sleep(100);
                    if (!concurrentStack.isEmpty()) {
                        int val = concurrentStack.pop();
                        System.out.println("Concurrent pop: " + val);
                    }
                } catch (Exception e) {
                    // Handle empty stack gracefully
                    break;
                }
            }
        });

        pusher1.start();
        pusher2.start();
        popper.start();

        pusher1.join();
        pusher2.join();
        popper.join();

        System.out.println("Final concurrent stack size: " + concurrentStack.getTotalSize());
        System.out.println("Final max: " + (concurrentStack.isEmpty() ? "N/A" : concurrentStack.getMax()));

        // Health monitoring test
        System.out.println("\n--- Health Monitoring (5 second test) ---");
        Thread.sleep(6000); // Let health monitoring run

        stack.shutdown();
        concurrentStack.shutdown();
        System.out.println("Distributed Stack tests completed.");
    }
}
