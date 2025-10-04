package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * Variation: Design Distributed LRU Set
 *
 * Description:
 * Design a distributed LRU set supporting add, remove, and eviction operations.
 *
 * Constraints:
 * - At most 10^6 operations.
 *
 * Follow-up:
 * - Can you optimize for eviction policy?
 * - Can you support TTL and replication?
 * 
 * Time Complexity: O(1) for all operations (amortized across nodes)
 * Space Complexity: O(n/k) per node where n is elements, k is nodes
 * 
 * Company Tags: System Design, Distributed Systems, Cache
 */
public class DesignDistributedLRUSet {

    // Node class for doubly linked list (LRU tracking)
    private static class LRUNode {
        String value;
        LRUNode prev;
        LRUNode next;
        long accessTime;
        long ttl; // Time to live (0 means no expiration)

        LRUNode(String value) {
            this.value = value;
            this.accessTime = System.currentTimeMillis();
            this.ttl = 0;
        }

        LRUNode(String value, long ttlMs) {
            this.value = value;
            this.accessTime = System.currentTimeMillis();
            this.ttl = ttlMs > 0 ? System.currentTimeMillis() + ttlMs : 0;
        }

        boolean isExpired() {
            return ttl > 0 && System.currentTimeMillis() > ttl;
        }
    }

    // Distributed node in the LRU set
    private static class DistributedNode {
        String nodeId;
        int capacity;
        int currentSize;
        Map<String, LRUNode> cache;
        LRUNode head; // Most recently used
        LRUNode tail; // Least recently used
        final ReentrantReadWriteLock lock;
        boolean isHealthy;

        DistributedNode(String nodeId, int capacity) {
            this.nodeId = nodeId;
            this.capacity = capacity;
            this.currentSize = 0;
            this.cache = new ConcurrentHashMap<>();
            this.lock = new ReentrantReadWriteLock();
            this.isHealthy = true;

            // Initialize dummy head and tail
            this.head = new LRUNode("");
            this.tail = new LRUNode("");
            head.next = tail;
            tail.prev = head;
        }

        void add(String value, long ttlMs) {
            lock.writeLock().lock();
            try {
                LRUNode existingNode = cache.get(value);
                if (existingNode != null) {
                    // Update existing node
                    existingNode.accessTime = System.currentTimeMillis();
                    if (ttlMs > 0) {
                        existingNode.ttl = System.currentTimeMillis() + ttlMs;
                    }
                    moveToHead(existingNode);
                    return;
                }

                // Add new node
                LRUNode newNode = new LRUNode(value, ttlMs);

                if (currentSize >= capacity) {
                    // Evict LRU item
                    evictLRU();
                }

                addToHead(newNode);
                cache.put(value, newNode);
                currentSize++;

            } finally {
                lock.writeLock().unlock();
            }
        }

        boolean remove(String value) {
            lock.writeLock().lock();
            try {
                LRUNode node = cache.get(value);
                if (node != null) {
                    removeNode(node);
                    cache.remove(value);
                    currentSize--;
                    return true;
                }
                return false;
            } finally {
                lock.writeLock().unlock();
            }
        }

        boolean contains(String value) {
            lock.readLock().lock();
            try {
                LRUNode node = cache.get(value);
                if (node != null) {
                    if (node.isExpired()) {
                        // Remove expired node asynchronously
                        CompletableFuture.runAsync(() -> remove(value));
                        return false;
                    }

                    // Update access time and move to head
                    lock.readLock().unlock();
                    lock.writeLock().lock();
                    try {
                        node.accessTime = System.currentTimeMillis();
                        moveToHead(node);
                        return true;
                    } finally {
                        lock.readLock().lock();
                        lock.writeLock().unlock();
                    }
                }
                return false;
            } finally {
                lock.readLock().unlock();
            }
        }

        void evictLRU() {
            lock.writeLock().lock();
            try {
                LRUNode lruNode = tail.prev;
                if (lruNode != head) { // Not empty
                    removeNode(lruNode);
                    cache.remove(lruNode.value);
                    currentSize--;
                    System.out.println("Evicted LRU item: " + lruNode.value + " from " + nodeId);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        Set<String> getAllValues() {
            lock.readLock().lock();
            try {
                return new HashSet<>(cache.keySet());
            } finally {
                lock.readLock().unlock();
            }
        }

        void cleanupExpired() {
            lock.writeLock().lock();
            try {
                List<String> expiredKeys = cache.values().stream()
                        .filter(LRUNode::isExpired)
                        .map(node -> node.value)
                        .collect(Collectors.toList());

                for (String key : expiredKeys) {
                    LRUNode node = cache.get(key);
                    if (node != null) {
                        removeNode(node);
                        cache.remove(key);
                        currentSize--;
                    }
                }

                if (!expiredKeys.isEmpty()) {
                    System.out.println("Cleaned up " + expiredKeys.size() +
                            " expired items from " + nodeId);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        // Helper methods for doubly linked list operations

        private void addToHead(LRUNode node) {
            node.prev = head;
            node.next = head.next;
            head.next.prev = node;
            head.next = node;
        }

        private void removeNode(LRUNode node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        private void moveToHead(LRUNode node) {
            removeNode(node);
            addToHead(node);
        }
    }

    private final List<DistributedNode> nodes;
    private final ExecutorService executorService;
    private final ScheduledExecutorService maintenanceService;
    private final ReentrantReadWriteLock globalLock;
    private final ConsistentHashing hashRing;
    private final boolean enableTTL;

    public DesignDistributedLRUSet(int nodeCount, int capacity) {
        this(nodeCount, capacity, false);
    }

    public DesignDistributedLRUSet(int nodeCount, int capacity, boolean enableTTL) {
        if (nodeCount <= 0 || capacity <= 0) {
            throw new IllegalArgumentException("Node count and capacity must be positive");
        }

        this.nodes = new ArrayList<>();
        this.executorService = Executors.newCachedThreadPool();
        this.maintenanceService = Executors.newScheduledThreadPool(2);
        this.globalLock = new ReentrantReadWriteLock();
        this.hashRing = new ConsistentHashing();
        this.enableTTL = enableTTL;

        // Initialize nodes
        int capacityPerNode = Math.max(1, capacity / nodeCount);
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "node-" + i;
            DistributedNode node = new DistributedNode(nodeId, capacityPerNode);
            nodes.add(node);
            hashRing.addNode(nodeId);
        }

        startMaintenance();

        System.out.println("Initialized Distributed LRU Set with " + nodeCount +
                " nodes, capacity " + capacityPerNode + " per node, TTL: " + enableTTL);
    }

    public void add(String value) {
        add(value, 0); // No TTL by default
    }

    public void add(String value, long ttlMs) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }

        globalLock.readLock().lock();
        try {
            String targetNodeId = hashRing.getNode(value);
            DistributedNode targetNode = findNodeById(targetNodeId);

            if (targetNode != null && targetNode.isHealthy) {
                targetNode.add(value, enableTTL ? ttlMs : 0);
                // System.out.println("Added '" + value + "' to " + targetNodeId);
            } else {
                throw new RuntimeException("Target node " + targetNodeId + " is not available");
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public void remove(String value) {
        if (value == null) {
            return;
        }

        globalLock.readLock().lock();
        try {
            String targetNodeId = hashRing.getNode(value);
            DistributedNode targetNode = findNodeById(targetNodeId);

            if (targetNode != null && targetNode.isHealthy) {
                targetNode.remove(value);
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public void evict() {
        // Global eviction - evict from the node with most LRU item
        globalLock.readLock().lock();
        try {
            DistributedNode nodeToEvictFrom = null;
            long oldestAccessTime = Long.MAX_VALUE;

            for (DistributedNode node : nodes) {
                if (node.isHealthy && node.currentSize > 0) {
                    node.lock.readLock().lock();
                    try {
                        if (node.tail.prev != node.head) { // Has items
                            long tailAccessTime = node.tail.prev.accessTime;
                            if (tailAccessTime < oldestAccessTime) {
                                oldestAccessTime = tailAccessTime;
                                nodeToEvictFrom = node;
                            }
                        }
                    } finally {
                        node.lock.readLock().unlock();
                    }
                }
            }

            if (nodeToEvictFrom != null) {
                nodeToEvictFrom.evictLRU();
            }
        } finally {
            globalLock.readLock().unlock();
        }
    }

    public boolean contains(String value) {
        if (value == null) {
            return false;
        }

        globalLock.readLock().lock();
        try {
            String targetNodeId = hashRing.getNode(value);
            DistributedNode targetNode = findNodeById(targetNodeId);

            if (targetNode != null && targetNode.isHealthy) {
                return targetNode.contains(value);
            }
            return false;
        } finally {
            globalLock.readLock().unlock();
        }
    }

    // Additional utility methods

    public int size() {
        return nodes.stream()
                .filter(node -> node.isHealthy)
                .mapToInt(node -> node.currentSize)
                .sum();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set<String> getAllValues() {
        Set<String> allValues = new HashSet<>();
        for (DistributedNode node : nodes) {
            if (node.isHealthy) {
                allValues.addAll(node.getAllValues());
            }
        }
        return allValues;
    }

    public Map<String, Integer> getNodeSizes() {
        Map<String, Integer> sizes = new HashMap<>();
        for (DistributedNode node : nodes) {
            sizes.put(node.nodeId, node.currentSize);
        }
        return sizes;
    }

    // Private helper methods

    private DistributedNode findNodeById(String nodeId) {
        return nodes.stream()
                .filter(node -> node.nodeId.equals(nodeId))
                .findFirst()
                .orElse(null);
    }

    private void startMaintenance() {
        // Cleanup expired items
        if (enableTTL) {
            maintenanceService.scheduleWithFixedDelay(() -> {
                for (DistributedNode node : nodes) {
                    if (node.isHealthy) {
                        node.cleanupExpired();
                    }
                }
            }, 10, 10, TimeUnit.SECONDS);
        }

        // Health monitoring
        maintenanceService.scheduleWithFixedDelay(() -> {
            for (DistributedNode node : nodes) {
                // Simulate occasional failures (very low chance)
                if (Math.random() < 0.001) {
                    node.isHealthy = false;
                } else if (!node.isHealthy && Math.random() < 0.1) {
                    node.isHealthy = true;
                }
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    // Simple consistent hashing implementation
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

        private long hash(String key) {
            return key.hashCode() & 0x7FFFFFFFL; // Ensure positive
        }
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

    public static void main(String[] args) {
        System.out.println("=== Distributed LRU Set Tests ===");

        DesignDistributedLRUSet set = new DesignDistributedLRUSet(3, 6);

        System.out.println("\n--- Basic Operations Test ---");
        set.add("a");
        set.add("b");
        System.out.println("Contains a: " + set.contains("a")); // true
        set.add("c");
        set.evict();
        System.out.println("Contains b after evict: " + set.contains("b")); // may be false due to eviction

        // Edge Case: Remove non-existent value
        set.remove("d");
        System.out.println("Size after removing non-existent: " + set.size());

        System.out.println("\n--- Load Distribution Test ---");
        DesignDistributedLRUSet distributedSet = new DesignDistributedLRUSet(3, 12);

        for (int i = 1; i <= 15; i++) {
            distributedSet.add("item" + i);
        }

        System.out.println("Node sizes: " + distributedSet.getNodeSizes());
        System.out.println("Total size: " + distributedSet.size());

        System.out.println("\n--- LRU Eviction Test ---");
        // Test automatic eviction when capacity is exceeded
        for (int i = 16; i <= 20; i++) {
            distributedSet.add("item" + i);
        }

        System.out.println("After adding more items:");
        System.out.println("Node sizes: " + distributedSet.getNodeSizes());
        System.out.println("Contains item1: " + distributedSet.contains("item1")); // May be evicted
        System.out.println("Contains item20: " + distributedSet.contains("item20")); // Should exist

        System.out.println("\n--- Manual Eviction Test ---");
        int sizeBefore = distributedSet.size();
        distributedSet.evict();
        int sizeAfter = distributedSet.size();
        System.out.println("Size before eviction: " + sizeBefore);
        System.out.println("Size after eviction: " + sizeAfter);

        System.out.println("\n--- TTL Test ---");
        DesignDistributedLRUSet ttlSet = new DesignDistributedLRUSet(2, 8, true);
        ttlSet.add("temporary", 100); // Very short TTL for testing
        ttlSet.add("permanent", 0); // No TTL

        System.out.println("Contains temporary (immediate): " + ttlSet.contains("temporary"));
        System.out.println("Contains permanent (immediate): " + ttlSet.contains("permanent"));

        try {
            Thread.sleep(150); // Wait for expiration
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Contains temporary (after TTL): " + ttlSet.contains("temporary"));
        System.out.println("Contains permanent (after wait): " + ttlSet.contains("permanent"));

        System.out.println("\n--- Set Behavior Test (No Duplicates) ---");
        DesignDistributedLRUSet uniqueSet = new DesignDistributedLRUSet(2, 10);
        uniqueSet.add("apple");
        uniqueSet.add("banana");
        uniqueSet.add("apple"); // Duplicate - should update position, not increase size

        System.out.println("Size after adding duplicate: " + uniqueSet.size()); // Should be 2
        System.out.println("Contains apple: " + uniqueSet.contains("apple")); // Should be true and move to front

        set.shutdown();
        distributedSet.shutdown();
        ttlSet.shutdown();
        uniqueSet.shutdown();

        System.out.println("Distributed LRU Set tests completed.");
    }
}
