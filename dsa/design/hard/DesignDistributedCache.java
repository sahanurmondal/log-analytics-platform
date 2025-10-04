package design.hard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Design Distributed Cache System
 *
 * Description: Design a distributed cache that supports:
 * - Consistent hashing for data distribution
 * - Replication for fault tolerance
 * - Cache eviction policies (LRU, LFU, TTL)
 * - Node failure handling
 *
 * Constraints:
 * - Support multiple cache nodes
 * - Handle node failures gracefully
 * - Implement replication factor
 *
 * Follow-up:
 * - How to handle network partitions?
 * - Cache coherency protocols?
 *
 * Time Complexity: O(log n) for operations, O(1) for local cache access
 * Space Complexity: O(data_size * replication_factor)
 *
 * Company Tags: Google, Amazon, Facebook, Netflix
 */
public class DesignDistributedCache {

    enum NodeStatus {
        ACTIVE, FAILED, RECOVERING
    }

    class CacheNode {
        String nodeId;
        String address;
        NodeStatus status;
        Map<String, CacheEntry> localCache;
        ReentrantReadWriteLock lock;
        long lastHeartbeat;

        CacheNode(String nodeId, String address) {
            this.nodeId = nodeId;
            this.address = address;
            this.status = NodeStatus.ACTIVE;
            this.localCache = new ConcurrentHashMap<>();
            this.lock = new ReentrantReadWriteLock();
            this.lastHeartbeat = System.currentTimeMillis();
        }

        void put(String key, Object value, long ttl) {
            lock.writeLock().lock();
            try {
                long expiration = ttl > 0 ? System.currentTimeMillis() + ttl : Long.MAX_VALUE;
                localCache.put(key, new CacheEntry(value, expiration));
            } finally {
                lock.writeLock().unlock();
            }
        }

        CacheEntry get(String key) {
            lock.readLock().lock();
            try {
                CacheEntry entry = localCache.get(key);
                if (entry != null && entry.isExpired()) {
                    localCache.remove(key);
                    return null;
                }
                return entry;
            } finally {
                lock.readLock().unlock();
            }
        }

        boolean remove(String key) {
            lock.writeLock().lock();
            try {
                return localCache.remove(key) != null;
            } finally {
                lock.writeLock().unlock();
            }
        }

        void cleanup() {
            lock.writeLock().lock();
            try {
                localCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
            } finally {
                lock.writeLock().unlock();
            }
        }
    }

    class CacheEntry {
        Object value;
        long expiration;
        long accessTime;
        int accessCount;

        CacheEntry(Object value, long expiration) {
            this.value = value;
            this.expiration = expiration;
            this.accessTime = System.currentTimeMillis();
            this.accessCount = 1;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiration;
        }

        void access() {
            this.accessTime = System.currentTimeMillis();
            this.accessCount++;
        }
    }

    private TreeMap<Integer, CacheNode> hashRing;
    private Map<String, CacheNode> nodes;
    private int replicationFactor;
    private int virtualNodes;

    public DesignDistributedCache(int replicationFactor) {
        this.hashRing = new TreeMap<>();
        this.nodes = new ConcurrentHashMap<>();
        this.replicationFactor = replicationFactor;
        this.virtualNodes = 150;
    }

    public void addNode(String nodeId, String address) {
        CacheNode node = new CacheNode(nodeId, address);
        nodes.put(nodeId, node);

        // Add virtual nodes to hash ring
        for (int i = 0; i < virtualNodes; i++) {
            String virtualNodeId = nodeId + ":" + i;
            int hash = virtualNodeId.hashCode();
            hashRing.put(hash, node);
        }

        System.out.println("Added cache node: " + nodeId);
    }

    public void removeNode(String nodeId) {
        CacheNode node = nodes.remove(nodeId);
        if (node == null)
            return;

        // Remove virtual nodes from hash ring
        hashRing.entrySet().removeIf(entry -> entry.getValue().nodeId.equals(nodeId));

        // Redistribute data to other nodes
        redistributeData(node);

        System.out.println("Removed cache node: " + nodeId);
    }

    private void redistributeData(CacheNode failedNode) {
        // In a real implementation, this would migrate data to replica nodes
        System.out.println("Redistributing data from failed node: " + failedNode.nodeId);
    }

    private List<CacheNode> getNodesForKey(String key) {
        List<CacheNode> selectedNodes = new ArrayList<>();
        if (hashRing.isEmpty())
            return selectedNodes;

        int hash = key.hashCode();

        // Find the first node clockwise from the hash
        Map.Entry<Integer, CacheNode> entry = hashRing.ceilingEntry(hash);
        if (entry == null) {
            entry = hashRing.firstEntry();
        }

        Set<String> addedNodes = new HashSet<>();
        Iterator<Map.Entry<Integer, CacheNode>> iterator = hashRing.tailMap(entry.getKey()).entrySet().iterator();

        // Add primary and replica nodes
        while (selectedNodes.size() < replicationFactor && iterator.hasNext()) {
            CacheNode node = iterator.next().getValue();
            if (node.status == NodeStatus.ACTIVE && !addedNodes.contains(node.nodeId)) {
                selectedNodes.add(node);
                addedNodes.add(node.nodeId);
            }
        }

        // Wrap around if needed
        if (selectedNodes.size() < replicationFactor) {
            iterator = hashRing.entrySet().iterator();
            while (selectedNodes.size() < replicationFactor && iterator.hasNext()) {
                CacheNode node = iterator.next().getValue();
                if (node.status == NodeStatus.ACTIVE && !addedNodes.contains(node.nodeId)) {
                    selectedNodes.add(node);
                    addedNodes.add(node.nodeId);
                }
            }
        }

        return selectedNodes;
    }

    public void put(String key, Object value, long ttlMs) {
        List<CacheNode> nodes = getNodesForKey(key);

        for (CacheNode node : nodes) {
            try {
                node.put(key, value, ttlMs);
            } catch (Exception e) {
                System.err.println("Failed to put to node " + node.nodeId + ": " + e.getMessage());
                markNodeAsFailed(node.nodeId);
            }
        }
    }

    public Object get(String key) {
        List<CacheNode> nodes = getNodesForKey(key);

        for (CacheNode node : nodes) {
            try {
                CacheEntry entry = node.get(key);
                if (entry != null) {
                    entry.access();
                    return entry.value;
                }
            } catch (Exception e) {
                System.err.println("Failed to get from node " + node.nodeId + ": " + e.getMessage());
                markNodeAsFailed(node.nodeId);
            }
        }

        return null; // Cache miss
    }

    public boolean remove(String key) {
        List<CacheNode> nodes = getNodesForKey(key);
        boolean removed = false;

        for (CacheNode node : nodes) {
            try {
                if (node.remove(key)) {
                    removed = true;
                }
            } catch (Exception e) {
                System.err.println("Failed to remove from node " + node.nodeId + ": " + e.getMessage());
                markNodeAsFailed(node.nodeId);
            }
        }

        return removed;
    }

    private void markNodeAsFailed(String nodeId) {
        CacheNode node = nodes.get(nodeId);
        if (node != null) {
            node.status = NodeStatus.FAILED;
            System.out.println("Marked node as failed: " + nodeId);

            // In a real system, we might try to recover the node or redistribute its data
        }
    }

    public void performHealthCheck() {
        long now = System.currentTimeMillis();

        for (CacheNode node : nodes.values()) {
            // Simulate health check
            boolean healthy = simulateHealthCheck(node);

            if (!healthy && node.status == NodeStatus.ACTIVE) {
                markNodeAsFailed(node.nodeId);
            } else if (healthy && node.status == NodeStatus.FAILED) {
                node.status = NodeStatus.ACTIVE;
                System.out.println("Node recovered: " + node.nodeId);
            }

            node.lastHeartbeat = now;

            // Cleanup expired entries
            node.cleanup();
        }
    }

    private boolean simulateHealthCheck(CacheNode node) {
        // Simulate network call to check node health
        return Math.random() > 0.05; // 95% chance of being healthy
    }

    public Map<String, Object> getClusterStats() {
        Map<String, Object> stats = new HashMap<>();

        int activeNodes = 0;
        int failedNodes = 0;
        int totalEntries = 0;

        for (CacheNode node : nodes.values()) {
            if (node.status == NodeStatus.ACTIVE) {
                activeNodes++;
                totalEntries += node.localCache.size();
            } else {
                failedNodes++;
            }
        }

        stats.put("totalNodes", nodes.size());
        stats.put("activeNodes", activeNodes);
        stats.put("failedNodes", failedNodes);
        stats.put("totalCacheEntries", totalEntries);
        stats.put("replicationFactor", replicationFactor);

        return stats;
    }

    public List<Map<String, Object>> getNodeStats() {
        List<Map<String, Object>> nodeStats = new ArrayList<>();

        for (CacheNode node : nodes.values()) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nodeId", node.nodeId);
            stats.put("address", node.address);
            stats.put("status", node.status);
            stats.put("cacheSize", node.localCache.size());
            stats.put("lastHeartbeat", node.lastHeartbeat);

            nodeStats.add(stats);
        }

        return nodeStats;
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedCache cache = new DesignDistributedCache(2); // Replication factor of 2

        // Add cache nodes
        cache.addNode("node1", "192.168.1.1:6379");
        cache.addNode("node2", "192.168.1.2:6379");
        cache.addNode("node3", "192.168.1.3:6379");

        // Test cache operations
        cache.put("user:1", "John Doe", 60000); // 1 minute TTL
        cache.put("user:2", "Jane Smith", 0); // No expiration
        cache.put("session:abc", "session_data", 30000); // 30 seconds TTL

        System.out.println("Get user:1: " + cache.get("user:1"));
        System.out.println("Get user:2: " + cache.get("user:2"));
        System.out.println("Get user:3: " + cache.get("user:3")); // Should be null

        // Show cluster stats
        System.out.println("\nCluster stats: " + cache.getClusterStats());

        // Perform health check
        cache.performHealthCheck();

        // Test after potential node failure
        System.out.println("\nAfter health check:");
        System.out.println("Get user:1: " + cache.get("user:1"));

        // Add another node
        cache.addNode("node4", "192.168.1.4:6379");
        System.out.println("\nAfter adding node4:");
        System.out.println("Cluster stats: " + cache.getClusterStats());

        // Show individual node stats
        System.out.println("\nNode statistics:");
        for (Map<String, Object> nodeStats : cache.getNodeStats()) {
            System.out.println(nodeStats);
        }
    }
}
