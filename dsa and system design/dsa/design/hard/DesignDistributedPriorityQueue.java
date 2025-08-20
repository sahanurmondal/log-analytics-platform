package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Variation: Design Distributed Priority Queue
 *
 * Description:
 * Design a distributed priority queue supporting enqueue and dequeue
 * operations.
 *
 * Constraints:
 * - At most 10^6 operations.
 *
 * Follow-up:
 * - Can you optimize for consistency and partition tolerance?
 * - Can you support multi-level priorities?
 * 
 * Time Complexity: O(log n) for enqueue/dequeue operations
 * Space Complexity: O(n) distributed across nodes
 * 
 * Company Tags: System Design, Distributed Systems, Priority Queue
 */
public class DesignDistributedPriorityQueue {

    private static class PriorityItem implements Comparable<PriorityItem> {
        String value;
        int priority;
        long timestamp; // For tie-breaking (FIFO within same priority)
        String nodeId; // For tracking which node it came from

        PriorityItem(String value, int priority, String nodeId) {
            this.value = value;
            this.priority = priority;
            this.timestamp = System.currentTimeMillis();
            this.nodeId = nodeId;
        }

        @Override
        public int compareTo(PriorityItem other) {
            // Lower priority number = higher priority (like Linux nice values)
            int priorityCompare = Integer.compare(this.priority, other.priority);
            if (priorityCompare != 0) {
                return priorityCompare;
            }
            // If same priority, FIFO (earlier timestamp first)
            return Long.compare(this.timestamp, other.timestamp);
        }

        @Override
        public String toString() {
            return String.format("Item{value='%s', priority=%d, node='%s'}",
                    value, priority, nodeId);
        }
    }

    private static class DistributedQueueNode {
        String nodeId;
        PriorityQueue<PriorityItem> localQueue;
        final ReentrantReadWriteLock lock;
        boolean isHealthy;
        int capacity;

        DistributedQueueNode(String nodeId, int capacity) {
            this.nodeId = nodeId;
            this.localQueue = new PriorityQueue<>();
            this.lock = new ReentrantReadWriteLock();
            this.isHealthy = true;
            this.capacity = capacity;
        }

        boolean enqueue(String value, int priority) {
            lock.writeLock().lock();
            try {
                if (localQueue.size() >= capacity) {
                    return false; // Queue full
                }

                PriorityItem item = new PriorityItem(value, priority, nodeId);
                localQueue.offer(item);

                System.out.println("Enqueued " + item + " to " + nodeId);
                return true;
            } finally {
                lock.writeLock().unlock();
            }
        }

        PriorityItem dequeue() {
            lock.writeLock().lock();
            try {
                PriorityItem item = localQueue.poll();
                if (item != null) {
                    System.out.println("Dequeued " + item + " from " + nodeId);
                }
                return item;
            } finally {
                lock.writeLock().unlock();
            }
        }

        PriorityItem peek() {
            lock.readLock().lock();
            try {
                return localQueue.peek();
            } finally {
                lock.readLock().unlock();
            }
        }

        int size() {
            lock.readLock().lock();
            try {
                return localQueue.size();
            } finally {
                lock.readLock().unlock();
            }
        }

        boolean isEmpty() {
            return size() == 0;
        }

        List<PriorityItem> getAllItems() {
            lock.readLock().lock();
            try {
                return new ArrayList<>(localQueue);
            } finally {
                lock.readLock().unlock();
            }
        }

        void updateHeartbeat() {
            // Heartbeat tracking removed for simplicity
        }
    }

    private final List<DistributedQueueNode> nodes;
    private final ConsistentHashing hashRing;
    private final ExecutorService executorService;
    private final ScheduledExecutorService maintenanceService;
    private final ReentrantReadWriteLock globalLock;
    private final LoadBalancer loadBalancer;
    private final boolean enableReplication;
    private final int replicationFactor;

    public DesignDistributedPriorityQueue(int nodeCount) {
        this(nodeCount, 1000, false, 1); // Default capacity 1000 per node, no replication
    }

    public DesignDistributedPriorityQueue(int nodeCount, int capacityPerNode,
            boolean enableReplication, int replicationFactor) {
        if (nodeCount <= 0 || capacityPerNode <= 0) {
            throw new IllegalArgumentException("Node count and capacity must be positive");
        }

        this.nodes = new ArrayList<>();
        this.hashRing = new ConsistentHashing();
        this.executorService = Executors.newCachedThreadPool();
        this.maintenanceService = Executors.newScheduledThreadPool(2);
        this.globalLock = new ReentrantReadWriteLock();
        this.loadBalancer = new LoadBalancer();
        this.enableReplication = enableReplication;
        this.replicationFactor = Math.min(replicationFactor, nodeCount);

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "pq-node-" + i;
            DistributedQueueNode node = new DistributedQueueNode(nodeId, capacityPerNode);
            nodes.add(node);
            hashRing.addNode(nodeId);
        }

        startMaintenance();

        System.out.println("Initialized Distributed Priority Queue with " + nodeCount +
                " nodes, capacity " + capacityPerNode + " per node, replication: " +
                enableReplication + " (factor: " + this.replicationFactor + ")");
    }

    public void enqueue(String value, int priority) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        globalLock.readLock().lock();
        try {
            if (enableReplication) {
                enqueueWithReplication(value, priority);
            } else {
                enqueueSingle(value, priority);
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    private void enqueueSingle(String value, int priority) {
        // Use consistent hashing to determine target node
        String targetNodeId = hashRing.getNode(value);
        DistributedQueueNode targetNode = findNodeById(targetNodeId);

        if (targetNode != null && targetNode.isHealthy) {
            if (!targetNode.enqueue(value, priority)) {
                // Primary node full, try load balancing to another node
                DistributedQueueNode alternativeNode = loadBalancer.findLeastLoadedNode(nodes);
                if (alternativeNode != null && alternativeNode != targetNode) {
                    alternativeNode.enqueue(value, priority);
                } else {
                    throw new RuntimeException("All nodes are full or unavailable");
                }
            }
        } else {
            throw new RuntimeException("Target node " + targetNodeId + " is not available");
        }
    }

    private void enqueueWithReplication(String value, int priority) {
        String primaryNodeId = hashRing.getNode(value);
        List<String> replicaNodeIds = hashRing.getReplicaNodes(primaryNodeId, replicationFactor);

        int successfulReplicas = 0;
        List<String> failedNodes = new ArrayList<>();

        for (String nodeId : replicaNodeIds) {
            DistributedQueueNode node = findNodeById(nodeId);
            if (node != null && node.isHealthy) {
                if (node.enqueue(value, priority)) {
                    successfulReplicas++;
                } else {
                    failedNodes.add(nodeId);
                }
            } else {
                failedNodes.add(nodeId);
            }
        }

        if (successfulReplicas == 0) {
            throw new RuntimeException("Failed to enqueue to any replica nodes");
        }

        if (!failedNodes.isEmpty()) {
            System.out.println("Warning: Failed to replicate to nodes: " + failedNodes);
        }
    }

    public String dequeue() {
        globalLock.readLock().lock();
        try {
            return enableReplication ? dequeueWithReplication() : dequeueSingle();
        } finally {
            globalLock.readLock().unlock();
        }
    }

    private String dequeueSingle() {
        // Find the node with the highest priority item across all nodes
        DistributedQueueNode bestNode = null;
        PriorityItem bestItem = null;

        for (DistributedQueueNode node : nodes) {
            if (node.isHealthy && !node.isEmpty()) {
                PriorityItem topItem = node.peek();
                if (topItem != null && (bestItem == null || topItem.compareTo(bestItem) < 0)) {
                    bestItem = topItem;
                    bestNode = node;
                }
            }
        }

        if (bestNode != null) {
            PriorityItem dequeuedItem = bestNode.dequeue();
            return dequeuedItem != null ? dequeuedItem.value : null;
        }

        return null; // All queues empty
    }

    private String dequeueWithReplication() {
        // In replicated mode, we need to ensure consistency
        // Find the globally highest priority item and remove from all replicas

        Map<String, List<DistributedQueueNode>> itemToReplicas = new HashMap<>();
        PriorityItem globalBestItem = null;

        // Collect all items and their replica locations
        for (DistributedQueueNode node : nodes) {
            if (node.isHealthy && !node.isEmpty()) {
                PriorityItem topItem = node.peek();
                if (topItem != null) {
                    String itemKey = topItem.value + ":" + topItem.priority + ":" + topItem.timestamp;
                    @SuppressWarnings("unused")
                    List<DistributedQueueNode> replicas = itemToReplicas.computeIfAbsent(itemKey,
                            k -> new ArrayList<>());
                    replicas.add(node);

                    if (globalBestItem == null || topItem.compareTo(globalBestItem) < 0) {
                        globalBestItem = topItem;
                    }
                }
            }
        }

        if (globalBestItem != null) {
            String bestItemKey = globalBestItem.value + ":" + globalBestItem.priority + ":" + globalBestItem.timestamp;
            List<DistributedQueueNode> replicaNodes = itemToReplicas.get(bestItemKey);

            // Remove from all replicas
            if (replicaNodes != null) {
                for (DistributedQueueNode node : replicaNodes) {
                    node.dequeue(); // Remove the top item
                }
            }

            return globalBestItem.value;
        }

        return null;
    }

    public boolean isEmpty() {
        return nodes.stream()
                .filter(node -> node.isHealthy)
                .allMatch(DistributedQueueNode::isEmpty);
    }

    public int size() {
        return nodes.stream()
                .filter(node -> node.isHealthy)
                .mapToInt(DistributedQueueNode::size)
                .sum();
    }

    public PriorityItem peek() {
        globalLock.readLock().lock();
        try {
            PriorityItem bestItem = null;

            for (DistributedQueueNode node : nodes) {
                if (node.isHealthy && !node.isEmpty()) {
                    PriorityItem topItem = node.peek();
                    if (topItem != null && (bestItem == null || topItem.compareTo(bestItem) < 0)) {
                        bestItem = topItem;
                    }
                }
            }

            return bestItem;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public Map<String, Integer> getNodeSizes() {
        Map<String, Integer> sizes = new HashMap<>();
        for (DistributedQueueNode node : nodes) {
            sizes.put(node.nodeId, node.size());
        }
        return sizes;
    }

    public List<PriorityItem> getAllItems() {
        List<PriorityItem> allItems = new ArrayList<>();
        for (DistributedQueueNode node : nodes) {
            if (node.isHealthy) {
                allItems.addAll(node.getAllItems());
            }
        }
        allItems.sort(PriorityItem::compareTo);
        return allItems;
    }

    // Helper classes and methods

    private DistributedQueueNode findNodeById(String nodeId) {
        return nodes.stream()
                .filter(node -> node.nodeId.equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    private void startMaintenance() {
        // Health monitoring
        maintenanceService.scheduleWithFixedDelay(() -> {
            for (DistributedQueueNode node : nodes) {
                node.updateHeartbeat();

                // Simulate occasional failures
                if (Math.random() < 0.001) {
                    node.isHealthy = false;
                    System.out.println("Node " + node.nodeId + " marked as unhealthy");
                } else if (!node.isHealthy && Math.random() < 0.1) {
                    node.isHealthy = true;
                    System.out.println("Node " + node.nodeId + " recovered");
                }
            }
        }, 5, 5, TimeUnit.SECONDS);

        // Load balancing and rebalancing
        maintenanceService.scheduleWithFixedDelay(() -> {
            loadBalancer.rebalanceIfNeeded(nodes);
        }, 30, 30, TimeUnit.SECONDS);
    }

    private static class LoadBalancer {
        DistributedQueueNode findLeastLoadedNode(List<DistributedQueueNode> nodes) {
            return nodes.stream()
                    .filter(node -> node.isHealthy)
                    .min(Comparator.comparingInt(DistributedQueueNode::size))
                    .orElse(null);
        }

        void rebalanceIfNeeded(List<DistributedQueueNode> nodes) {
            List<DistributedQueueNode> healthyNodes = nodes.stream()
                    .filter(node -> node.isHealthy)
                    .toList();

            if (healthyNodes.size() < 2)
                return;

            // Calculate average load
            double avgLoad = healthyNodes.stream()
                    .mapToInt(DistributedQueueNode::size)
                    .average()
                    .orElse(0.0);

            // Find overloaded and underloaded nodes
            List<DistributedQueueNode> overloaded = healthyNodes.stream()
                    .filter(node -> node.size() > avgLoad * 1.5)
                    .toList();

            List<DistributedQueueNode> underloaded = healthyNodes.stream()
                    .filter(node -> node.size() < avgLoad * 0.5)
                    .toList();

            // Simple rebalancing: move items from overloaded to underloaded
            for (DistributedQueueNode overloadedNode : overloaded) {
                for (DistributedQueueNode underloadedNode : underloaded) {
                    if (overloadedNode.size() > underloadedNode.size() + 10) {
                        PriorityItem item = overloadedNode.dequeue();
                        if (item != null) {
                            underloadedNode.enqueue(item.value, item.priority);
                            System.out.println("Rebalanced item from " + overloadedNode.nodeId +
                                    " to " + underloadedNode.nodeId);
                            break;
                        }
                    }
                }
            }
        }
    }

    private static class ConsistentHashing {
        private final SortedMap<Long, String> ring = new TreeMap<>();
        private final int virtualNodes = 150;

        void addNode(String nodeId) {
            for (int i = 0; i < virtualNodes; i++) {
                long hash = hash(nodeId + ":" + i);
                ring.put(hash, nodeId);
            }
        }

        String getNode(String key) {
            if (ring.isEmpty()) {
                return null;
            }

            long hash = hash(key);
            SortedMap<Long, String> tailMap = ring.tailMap(hash);
            Long nodeHash = tailMap.isEmpty() ? ring.firstKey() : tailMap.firstKey();
            return ring.get(nodeHash);
        }

        List<String> getReplicaNodes(String primaryNodeId, int replicationFactor) {
            List<String> replicas = new ArrayList<>();
            replicas.add(primaryNodeId);

            if (ring.size() <= 1)
                return replicas;

            // Find primary node in ring
            Long primaryHash = null;
            for (Map.Entry<Long, String> entry : ring.entrySet()) {
                if (entry.getValue().equals(primaryNodeId)) {
                    primaryHash = entry.getKey();
                    break;
                }
            }

            if (primaryHash != null) {
                SortedMap<Long, String> tailMap = ring.tailMap(primaryHash + 1);
                SortedMap<Long, String> headMap = ring.headMap(primaryHash);

                // Get next nodes in ring
                Set<String> uniqueNodes = new HashSet<>();
                uniqueNodes.add(primaryNodeId);

                for (String nodeId : tailMap.values()) {
                    if (uniqueNodes.add(nodeId) && replicas.size() < replicationFactor) {
                        replicas.add(nodeId);
                    }
                    if (replicas.size() >= replicationFactor)
                        break;
                }

                // Wrap around if needed
                if (replicas.size() < replicationFactor) {
                    for (String nodeId : headMap.values()) {
                        if (uniqueNodes.add(nodeId) && replicas.size() < replicationFactor) {
                            replicas.add(nodeId);
                        }
                        if (replicas.size() >= replicationFactor)
                            break;
                    }
                }
            }

            return replicas;
        }

        private long hash(String key) {
            return key.hashCode() & 0x7FFFFFFFL;
        }
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalNodes", nodes.size());
        stats.put("healthyNodes", nodes.stream().mapToInt(n -> n.isHealthy ? 1 : 0).sum());
        stats.put("totalSize", size());
        stats.put("nodeSizes", getNodeSizes());
        stats.put("replicationEnabled", enableReplication);
        stats.put("replicationFactor", replicationFactor);
        return stats;
    }

    public void shutdown() {
        maintenanceService.shutdown();
        executorService.shutdown();
        try {
            if (!maintenanceService.awaitTermination(5, TimeUnit.SECONDS)) {
                maintenanceService.shutdownNow();
            }
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            maintenanceService.shutdownNow();
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Distributed Priority Queue Tests ===");

        DesignDistributedPriorityQueue queue = new DesignDistributedPriorityQueue(3);

        System.out.println("\n--- Basic Operations Test ---");
        queue.enqueue("a", 2);
        queue.enqueue("b", 1);
        System.out.println("Dequeue: " + queue.dequeue()); // b (priority 1)
        System.out.println("Dequeue: " + queue.dequeue()); // a (priority 2)

        // Edge Case: Dequeue from empty queue
        System.out.println("Dequeue from empty: " + queue.dequeue()); // null

        System.out.println("\n--- Priority Ordering Test ---");
        String[] values = { "high", "medium", "low", "urgent", "normal" };
        int[] priorities = { 2, 5, 8, 1, 5 };

        for (int i = 0; i < values.length; i++) {
            queue.enqueue(values[i], priorities[i]);
        }

        System.out.println("Items in priority order:");
        while (!queue.isEmpty()) {
            System.out.println("  " + queue.dequeue());
        }

        System.out.println("\n--- Load Distribution Test ---");
        DesignDistributedPriorityQueue distributedQueue = new DesignDistributedPriorityQueue(3, 5, false, 1);

        for (int i = 1; i <= 12; i++) {
            distributedQueue.enqueue("item" + i, i % 3);
        }

        System.out.println("Node sizes: " + distributedQueue.getNodeSizes());
        System.out.println("Total size: " + distributedQueue.size());

        System.out.println("\n--- Peek Operation Test ---");
        PriorityItem topItem = distributedQueue.peek();
        System.out.println("Top priority item: " + (topItem != null ? topItem : "null"));

        System.out.println("All items in order:");
        List<PriorityItem> allItems = distributedQueue.getAllItems();
        allItems.forEach(System.out::println);

        System.out.println("\n--- FIFO Within Same Priority Test ---");
        DesignDistributedPriorityQueue fifoQueue = new DesignDistributedPriorityQueue(2);

        fifoQueue.enqueue("first", 5);
        Thread.sleep(10); // Ensure different timestamps
        fifoQueue.enqueue("second", 5);
        Thread.sleep(10);
        fifoQueue.enqueue("third", 5);

        System.out.println("Same priority items (should be FIFO):");
        System.out.println("  " + fifoQueue.dequeue()); // first
        System.out.println("  " + fifoQueue.dequeue()); // second
        System.out.println("  " + fifoQueue.dequeue()); // third

        System.out.println("\n--- Replication Test ---");
        DesignDistributedPriorityQueue replicatedQueue = new DesignDistributedPriorityQueue(3, 10, true, 2);

        replicatedQueue.enqueue("replicated1", 1);
        replicatedQueue.enqueue("replicated2", 2);
        replicatedQueue.enqueue("replicated3", 1);

        System.out.println("Replicated queue node sizes: " + replicatedQueue.getNodeSizes());
        System.out.println("Dequeue from replicated: " + replicatedQueue.dequeue());
        System.out.println("After dequeue node sizes: " + replicatedQueue.getNodeSizes());

        System.out.println("\n--- Concurrent Operations Test ---");
        DesignDistributedPriorityQueue concurrentQueue = new DesignDistributedPriorityQueue(2);

        Thread producer1 = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                concurrentQueue.enqueue("p1-" + i, i % 5);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread producer2 = new Thread(() -> {
            for (int i = 0; i < 20; i++) {
                concurrentQueue.enqueue("p2-" + i, i % 5);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread consumer = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                try {
                    Thread.sleep(50);
                    String item = concurrentQueue.dequeue();
                    if (item != null) {
                        System.out.println("Consumed: " + item);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        producer1.start();
        producer2.start();
        consumer.start();

        producer1.join();
        producer2.join();
        consumer.join();

        System.out.println("Final concurrent queue size: " + concurrentQueue.size());

        System.out.println("\n--- Capacity and Load Balancing Test ---");
        DesignDistributedPriorityQueue capacityQueue = new DesignDistributedPriorityQueue(2, 3, false, 1);

        // Fill beyond single node capacity
        for (int i = 1; i <= 8; i++) {
            capacityQueue.enqueue("load" + i, i);
        }

        System.out.println("Load balanced node sizes: " + capacityQueue.getNodeSizes());

        // Wait for rebalancing
        Thread.sleep(1000);

        System.out.println("\n--- System Statistics ---");
        Map<String, Object> stats = distributedQueue.getSystemStats();
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        queue.shutdown();
        distributedQueue.shutdown();
        fifoQueue.shutdown();
        replicatedQueue.shutdown();
        concurrentQueue.shutdown();
        capacityQueue.shutdown();

        System.out.println("Distributed Priority Queue tests completed.");
    }
}
