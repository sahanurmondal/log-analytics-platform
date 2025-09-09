package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Distributed Set
 * 
 * Related LeetCode Problems:
 * - Similar to: Design HashSet (705), Union-Find
 * - No direct LeetCode equivalent (System Design)
 * 
 * Company Tags: Amazon, Google, Cassandra, DynamoDB, Redis
 * Difficulty: Hard
 * 
 * Description:
 * Design a distributed set that supports:
 * 1. add(value) - Add value to the set
 * 2. remove(value) - Remove value from the set
 * 3. contains(value) - Check if value exists in the set
 * 
 * The system should handle:
 * - Data partitioning across multiple nodes
 * - Consistency and partition tolerance
 * - Set operations (union, intersection, difference)
 * - Replication for fault tolerance
 * 
 * Constraints:
 * - At most 10^6 operations
 * - Support multiple nodes/partitions
 * - Handle node failures gracefully
 * 
 * Follow-ups:
 * 1. Consistency and partition tolerance optimization
 * 2. Set operations (union, intersection, difference)
 * 3. Bloom filter integration for membership tests
 * 4. Anti-entropy and repair mechanisms
 */
public class DesignDistributedSet {
    private final int nodeCount;
    private final int replicationFactor;
    private final List<Set<String>> partitions;
    private final Map<String, Set<Integer>> replicationMap;
    private final Set<Integer> availableNodes;
    private final Random random;

    // Bloom filter for fast membership testing
    private final BloomFilter bloomFilter;

    // Simple Bloom Filter implementation
    private static class BloomFilter {
        private final BitSet bitSet;
        private final int size;
        private final int hashFunctions;

        BloomFilter(int expectedElements, double falsePositiveRate) {
            this.size = (int) (-expectedElements * Math.log(falsePositiveRate) / (Math.log(2) * Math.log(2)));
            this.hashFunctions = (int) (size * Math.log(2) / expectedElements);
            this.bitSet = new BitSet(size);
        }

        void add(String value) {
            for (int i = 0; i < hashFunctions; i++) {
                int hash = hash(value, i);
                bitSet.set(Math.abs(hash) % size);
            }
        }

        boolean mightContain(String value) {
            for (int i = 0; i < hashFunctions; i++) {
                int hash = hash(value, i);
                if (!bitSet.get(Math.abs(hash) % size)) {
                    return false;
                }
            }
            return true;
        }

        private int hash(String value, int seed) {
            return (value.hashCode() + seed * 31) * 31;
        }
    }

    /**
     * Constructor - Initialize distributed set
     * Time: O(n), Space: O(n)
     */
    public DesignDistributedSet(int nodeCount) {
        this(nodeCount, Math.min(3, nodeCount)); // Default replication factor
    }

    public DesignDistributedSet(int nodeCount, int replicationFactor) {
        if (nodeCount <= 0 || replicationFactor <= 0 || replicationFactor > nodeCount) {
            throw new IllegalArgumentException("Invalid node count or replication factor");
        }

        this.nodeCount = nodeCount;
        this.replicationFactor = replicationFactor;
        this.partitions = new ArrayList<>();
        this.replicationMap = new ConcurrentHashMap<>();
        this.availableNodes = ConcurrentHashMap.newKeySet();
        this.random = new Random();
        this.bloomFilter = new BloomFilter(100000, 0.01); // 1% false positive rate

        // Initialize partitions and mark all nodes as available
        for (int i = 0; i < nodeCount; i++) {
            partitions.add(ConcurrentHashMap.newKeySet());
            availableNodes.add(i);
        }
    }

    /**
     * Add value to the distributed set
     * Time: O(r) where r is replication factor, Space: O(1)
     */
    public void add(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        // Add to bloom filter for fast membership testing
        bloomFilter.add(value);

        // Get replica nodes for this value
        Set<Integer> replicaNodes = getReplicaNodes(value);
        replicationMap.put(value, replicaNodes);

        // Add to all replica nodes
        for (int nodeId : replicaNodes) {
            if (availableNodes.contains(nodeId)) {
                partitions.get(nodeId).add(value);
            }
        }
    }

    /**
     * Remove value from the distributed set
     * Time: O(r) where r is replication factor, Space: O(1)
     */
    public void remove(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }

        // Get replica nodes for this value
        Set<Integer> replicaNodes = replicationMap.get(value);
        if (replicaNodes == null) {
            replicaNodes = getReplicaNodes(value);
        }

        // Remove from all replica nodes
        boolean removed = false;
        for (int nodeId : replicaNodes) {
            if (availableNodes.contains(nodeId)) {
                boolean nodeRemoved = partitions.get(nodeId).remove(value);
                removed = removed || nodeRemoved;
            }
        }

        if (removed) {
            replicationMap.remove(value);
        }
    }

    /**
     * Check if value exists in the distributed set
     * Time: O(r) where r is replication factor, Space: O(1)
     */
    public boolean contains(String value) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        // Quick check with bloom filter first
        if (!bloomFilter.mightContain(value)) {
            return false; // Definitely not in the set
        }

        // Get replica nodes for this value
        Set<Integer> replicaNodes = replicationMap.get(value);
        if (replicaNodes == null) {
            replicaNodes = getReplicaNodes(value);
        }

        // Check if value exists in any available replica node
        for (int nodeId : replicaNodes) {
            if (availableNodes.contains(nodeId)) {
                if (partitions.get(nodeId).contains(value)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get replica nodes for a value using consistent hashing
     * Time: O(r), Space: O(r)
     */
    private Set<Integer> getReplicaNodes(String value) {
        Set<Integer> nodes = new HashSet<>();
        int primaryNode = Math.abs(value.hashCode()) % nodeCount;

        // Add primary node and next (replicationFactor - 1) nodes
        for (int i = 0; i < replicationFactor; i++) {
            int nodeId = (primaryNode + i) % nodeCount;
            nodes.add(nodeId);
        }

        return nodes;
    }

    // Follow-up 1: Set union operation
    public DesignDistributedSet union(DesignDistributedSet other) {
        DesignDistributedSet result = new DesignDistributedSet(this.nodeCount, this.replicationFactor);

        // Add all elements from this set
        for (Set<String> partition : this.partitions) {
            for (String value : partition) {
                result.add(value);
            }
        }

        // Add all elements from other set
        for (Set<String> partition : other.partitions) {
            for (String value : partition) {
                result.add(value);
            }
        }

        return result;
    }

    // Follow-up 2: Set intersection operation
    public DesignDistributedSet intersection(DesignDistributedSet other) {
        DesignDistributedSet result = new DesignDistributedSet(this.nodeCount, this.replicationFactor);

        // Check each element in this set
        for (Set<String> partition : this.partitions) {
            for (String value : partition) {
                if (other.contains(value)) {
                    result.add(value);
                }
            }
        }

        return result;
    }

    // Follow-up 3: Set difference operation
    public DesignDistributedSet difference(DesignDistributedSet other) {
        DesignDistributedSet result = new DesignDistributedSet(this.nodeCount, this.replicationFactor);

        // Add elements that are in this set but not in other
        for (Set<String> partition : this.partitions) {
            for (String value : partition) {
                if (!other.contains(value)) {
                    result.add(value);
                }
            }
        }

        return result;
    }

    // Follow-up 4: Simulate node failure
    public void simulateNodeFailure(int nodeId) {
        if (!availableNodes.contains(nodeId)) {
            return; // Node already failed
        }

        availableNodes.remove(nodeId);
        System.out.println("Node " + nodeId + " failed");

        // Trigger repair process
        repairFailedNode(nodeId);
    }

    // Follow-up 5: Repair failed node using remaining replicas
    private void repairFailedNode(int failedNodeId) {
        Set<String> dataToRestore = new HashSet<>(partitions.get(failedNodeId));
        partitions.get(failedNodeId).clear();

        // Find data that was stored on the failed node and re-replicate
        for (String value : dataToRestore) {
            Set<Integer> replicaNodes = replicationMap.get(value);
            if (replicaNodes != null && replicaNodes.contains(failedNodeId)) {
                // Find a new node to replicate this data
                int newReplicaNode = findAlternativeNode(replicaNodes);
                if (newReplicaNode != -1) {
                    partitions.get(newReplicaNode).add(value);
                    replicaNodes.remove(failedNodeId);
                    replicaNodes.add(newReplicaNode);
                }
            }
        }
    }

    // Follow-up 6: Find alternative node for replication
    private int findAlternativeNode(Set<Integer> excludeNodes) {
        for (int nodeId : availableNodes) {
            if (!excludeNodes.contains(nodeId)) {
                return nodeId;
            }
        }
        return -1; // No alternative node available
    }

    // Follow-up 7: Anti-entropy repair between nodes
    public void antiEntropyRepair(int node1, int node2) {
        if (!availableNodes.contains(node1) || !availableNodes.contains(node2)) {
            return;
        }

        Set<String> partition1 = partitions.get(node1);
        Set<String> partition2 = partitions.get(node2);

        // Sync data that should be replicated to both nodes
        Set<String> allData = new HashSet<>(partition1);
        allData.addAll(partition2);

        for (String value : allData) {
            Set<Integer> replicaNodes = getReplicaNodes(value);

            if (replicaNodes.contains(node1) && !partition1.contains(value)) {
                partition1.add(value);
            }

            if (replicaNodes.contains(node2) && !partition2.contains(value)) {
                partition2.add(value);
            }
        }
    }

    // Follow-up 8: Get set size (approximate due to replication)
    public int size() {
        Set<String> uniqueValues = new HashSet<>();

        for (Set<String> partition : partitions) {
            uniqueValues.addAll(partition);
        }

        return uniqueValues.size();
    }

    // Follow-up 9: Get all values in the set
    public Set<String> getAllValues() {
        Set<String> allValues = new HashSet<>();

        for (Set<String> partition : partitions) {
            allValues.addAll(partition);
        }

        return allValues;
    }

    // Follow-up 10: Get distribution statistics
    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        int totalElements = 0;
        List<Integer> partitionSizes = new ArrayList<>();

        for (int i = 0; i < nodeCount; i++) {
            int partitionSize = partitions.get(i).size();
            partitionSizes.add(partitionSize);
            totalElements += partitionSize;
        }

        stats.put("totalStoredElements", totalElements);
        stats.put("uniqueElements", size());
        stats.put("partitionSizes", partitionSizes);
        stats.put("availableNodes", availableNodes.size());
        stats.put("replicationFactor", replicationFactor);
        stats.put("averagePartitionSize", totalElements / (double) nodeCount);

        return stats;
    }

    public static void main(String[] args) {
        System.out.println("=== Design Distributed Set Test ===");

        // Test Case 1: Basic add/remove/contains operations
        DesignDistributedSet set = new DesignDistributedSet(3);

        set.add("apple");
        set.add("banana");
        set.add("cherry");

        System.out.println("Contains apple: " + set.contains("apple")); // true
        System.out.println("Contains banana: " + set.contains("banana")); // true
        System.out.println("Contains grape: " + set.contains("grape")); // false

        set.remove("apple");
        System.out.println("After removal, contains apple: " + set.contains("apple")); // false

        // Test Case 2: Edge cases
        set.add(null); // Should handle gracefully
        set.add(""); // Should handle gracefully
        System.out.println("Contains null: " + set.contains(null)); // false
        System.out.println("Contains empty: " + set.contains("")); // false

        // Test Case 3: Set operations (Follow-up)
        System.out.println("\n=== Set Operations ===");
        DesignDistributedSet set1 = new DesignDistributedSet(3);
        DesignDistributedSet set2 = new DesignDistributedSet(3);

        set1.add("a");
        set1.add("b");
        set1.add("c");

        set2.add("b");
        set2.add("c");
        set2.add("d");

        DesignDistributedSet unionSet = set1.union(set2);
        DesignDistributedSet intersectionSet = set1.intersection(set2);
        DesignDistributedSet differenceSet = set1.difference(set2);

        System.out.println("Set1: " + set1.getAllValues());
        System.out.println("Set2: " + set2.getAllValues());
        System.out.println("Union: " + unionSet.getAllValues());
        System.out.println("Intersection: " + intersectionSet.getAllValues());
        System.out.println("Difference (set1 - set2): " + differenceSet.getAllValues());

        // Test Case 4: Node failure and repair (Follow-up)
        System.out.println("\n=== Node Failure Simulation ===");
        System.out.println("Before failure: " + set.getStats());

        set.simulateNodeFailure(0);
        System.out.println("After node 0 failure: " + set.getStats());

        // Test Case 5: Anti-entropy repair (Follow-up)
        System.out.println("\n=== Anti-Entropy Repair ===");
        set.antiEntropyRepair(1, 2);
        System.out.println("After anti-entropy repair: " + set.getStats());

        // Test Case 6: Large-scale testing
        System.out.println("\n=== Performance Test ===");
        DesignDistributedSet perfSet = new DesignDistributedSet(5);
        long startTime = System.currentTimeMillis();

        // Add 10,000 elements
        for (int i = 0; i < 10000; i++) {
            perfSet.add("element_" + i);
        }

        // Test contains for all elements
        int containsCount = 0;
        for (int i = 0; i < 10000; i++) {
            if (perfSet.contains("element_" + i)) {
                containsCount++;
            }
        }

        // Remove half the elements
        for (int i = 0; i < 5000; i++) {
            perfSet.remove("element_" + i);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Operations completed in: " + (endTime - startTime) + "ms");
        System.out.println("Contains count: " + containsCount);
        System.out.println("Final set size: " + perfSet.size());
        System.out.println("Final stats: " + perfSet.getStats());

        // Test Case 7: Bloom filter effectiveness
        System.out.println("\n=== Bloom Filter Test ===");
        int bloomFilterSaves = 0;
        for (int i = 10000; i < 11000; i++) {
            String testValue = "nonexistent_" + i;
            if (!perfSet.bloomFilter.mightContain(testValue)) {
                bloomFilterSaves++;
            }
        }
        System.out.println("Bloom filter saved " + bloomFilterSaves + "/1000 unnecessary lookups");
    }
}
