package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Distributed Multi-Master Key-Value Store
 * 
 * Related LeetCode Problems:
 * - Similar to: Design HashMap, LRU Cache
 * - No direct LeetCode equivalent (System Design)
 * 
 * Company Tags: Amazon, Google, Meta, Netflix, Cassandra/DynamoDB
 * Difficulty: Hard
 * 
 * Description:
 * Design a distributed key-value store that supports:
 * 1. put(key, value, nodeId) - Store key-value pair on specific node
 * 2. get(key) - Retrieve value for key
 * 3. resolveConflict(key) - Handle conflicting writes
 * 
 * The system should handle:
 * - Multi-master writes (multiple nodes can write)
 * - Conflict resolution (last-write-wins, vector clocks)
 * - Eventual consistency
 * - Partition tolerance
 * 
 * Constraints:
 * - At most 10^6 operations
 * - Support multiple nodes/masters
 * - Handle network partitions
 * 
 * Follow-ups:
 * 1. Conflict resolution optimization
 * 2. Causal consistency support
 * 3. Read/write quorum implementation
 * 4. Anti-entropy mechanism
 */
public class DesignDistributedMultiMasterKVStore {
    private final int nodeCount;
    private final Map<Integer, Map<String, VersionedValue>> nodes;
    private final Map<String, List<VersionedValue>> conflictLog;
    private final Map<Integer, VectorClock> vectorClocks;

    // Versioned value with metadata
    private static class VersionedValue {
        String value;
        long timestamp;
        int nodeId;
        VectorClock vectorClock;

        VersionedValue(String value, long timestamp, int nodeId, VectorClock vectorClock) {
            this.value = value;
            this.timestamp = timestamp;
            this.nodeId = nodeId;
            this.vectorClock = vectorClock.copy();
        }
    }

    // Vector clock for causal consistency
    private static class VectorClock {
        private final Map<Integer, Long> clock;

        VectorClock(int nodeCount) {
            this.clock = new HashMap<>();
            for (int i = 0; i < nodeCount; i++) {
                clock.put(i, 0L);
            }
        }

        VectorClock(Map<Integer, Long> clock) {
            this.clock = new HashMap<>(clock);
        }

        void increment(int nodeId) {
            clock.put(nodeId, clock.get(nodeId) + 1);
        }

        void update(VectorClock other) {
            for (Map.Entry<Integer, Long> entry : other.clock.entrySet()) {
                int nodeId = entry.getKey();
                long otherTime = entry.getValue();
                clock.put(nodeId, Math.max(clock.get(nodeId), otherTime));
            }
        }

        // Compare vector clocks for causal ordering
        ConflictStatus compare(VectorClock other) {
            boolean thisGreater = false, otherGreater = false;

            for (int nodeId : clock.keySet()) {
                long thisTime = clock.get(nodeId);
                long otherTime = other.clock.get(nodeId);

                if (thisTime > otherTime)
                    thisGreater = true;
                if (thisTime < otherTime)
                    otherGreater = true;
            }

            if (thisGreater && !otherGreater)
                return ConflictStatus.AFTER;
            if (!thisGreater && otherGreater)
                return ConflictStatus.BEFORE;
            if (!thisGreater && !otherGreater)
                return ConflictStatus.EQUAL;
            return ConflictStatus.CONCURRENT;
        }

        VectorClock copy() {
            return new VectorClock(this.clock);
        }

        @Override
        public String toString() {
            return clock.toString();
        }
    }

    private enum ConflictStatus {
        BEFORE, AFTER, EQUAL, CONCURRENT
    }

    /**
     * Constructor - Initialize distributed multi-master KV store
     * Time: O(n²), Space: O(n²)
     */
    public DesignDistributedMultiMasterKVStore(int nodeCount) {
        this.nodeCount = nodeCount;
        this.nodes = new ConcurrentHashMap<>();
        this.conflictLog = new ConcurrentHashMap<>();
        this.vectorClocks = new ConcurrentHashMap<>();

        // Initialize nodes and vector clocks
        for (int i = 0; i < nodeCount; i++) {
            nodes.put(i, new ConcurrentHashMap<>());
            vectorClocks.put(i, new VectorClock(nodeCount));
        }
    }

    /**
     * Put key-value pair on specific node
     * Time: O(1) average, Space: O(1)
     */
    public void put(String key, String value, int nodeId) {
        if (nodeId < 0 || nodeId >= nodeCount) {
            throw new IllegalArgumentException("Invalid node ID");
        }

        // Increment vector clock for this node
        VectorClock nodeClock = vectorClocks.get(nodeId);
        nodeClock.increment(nodeId);

        long timestamp = System.currentTimeMillis();
        VersionedValue versionedValue = new VersionedValue(value, timestamp, nodeId, nodeClock);

        // Store on the specific node
        nodes.get(nodeId).put(key, versionedValue);

        // Check for conflicts and log them
        checkAndLogConflicts(key, versionedValue);
    }

    /**
     * Get value for key (with conflict resolution)
     * Time: O(n) where n is number of nodes, Space: O(1)
     */
    public String get(String key) {
        List<VersionedValue> allVersions = new ArrayList<>();

        // Collect all versions of the key from all nodes
        for (Map<String, VersionedValue> nodeData : nodes.values()) {
            VersionedValue version = nodeData.get(key);
            if (version != null) {
                allVersions.add(version);
            }
        }

        if (allVersions.isEmpty()) {
            return null;
        }

        // Return the resolved value
        return resolveConflicts(allVersions).value;
    }

    /**
     * Explicit conflict resolution for a key
     * Time: O(n * m) where n is nodes, m is versions, Space: O(m)
     */
    public void resolveConflict(String key) {
        List<VersionedValue> conflicts = conflictLog.get(key);
        if (conflicts == null || conflicts.isEmpty()) {
            return; // No conflicts to resolve
        }

        VersionedValue resolved = resolveConflicts(conflicts);

        // Propagate resolved value to all nodes
        for (int nodeId = 0; nodeId < nodeCount; nodeId++) {
            nodes.get(nodeId).put(key, resolved);
        }

        // Clear conflicts for this key
        conflictLog.remove(key);
    }

    /**
     * Check and log conflicts for a key
     * Time: O(n), Space: O(1)
     */
    private void checkAndLogConflicts(String key, VersionedValue newValue) {
        List<VersionedValue> conflicts = new ArrayList<>();

        for (Map<String, VersionedValue> nodeData : nodes.values()) {
            VersionedValue existingValue = nodeData.get(key);
            if (existingValue != null && !existingValue.value.equals(newValue.value)) {
                ConflictStatus status = newValue.vectorClock.compare(existingValue.vectorClock);
                if (status == ConflictStatus.CONCURRENT) {
                    conflicts.add(existingValue);
                }
            }
        }

        if (!conflicts.isEmpty()) {
            conflicts.add(newValue);
            conflictLog.put(key, conflicts);
        }
    }

    /**
     * Resolve conflicts using multiple strategies
     * Time: O(n log n), Space: O(1)
     */
    private VersionedValue resolveConflicts(List<VersionedValue> versions) {
        if (versions.size() == 1) {
            return versions.get(0);
        }

        // Strategy 1: Last-write-wins (by timestamp)
        return versions.stream()
                .max(Comparator.comparingLong((VersionedValue v) -> v.timestamp)
                        .thenComparingInt(v -> v.nodeId))
                .orElse(versions.get(0));
    }

    // Follow-up 1: Causal consistency - check if read is causally consistent
    public boolean isCausallyConsistent(String key, int readerId) {
        VersionedValue value = nodes.get(readerId).get(key);
        if (value == null)
            return true;

        VectorClock readerClock = vectorClocks.get(readerId);
        return readerClock.compare(value.vectorClock) != ConflictStatus.BEFORE;
    }

    // Follow-up 2: Read quorum - read from majority of nodes
    public String readQuorum(String key, int quorumSize) {
        List<VersionedValue> quorumValues = new ArrayList<>();
        int readCount = 0;

        for (Map<String, VersionedValue> nodeData : nodes.values()) {
            if (readCount >= quorumSize)
                break;

            VersionedValue value = nodeData.get(key);
            if (value != null) {
                quorumValues.add(value);
                readCount++;
            }
        }

        if (readCount < quorumSize) {
            return null; // Quorum not achieved
        }

        return resolveConflicts(quorumValues).value;
    }

    // Follow-up 3: Write quorum - write to majority of nodes
    public boolean writeQuorum(String key, String value, int quorumSize) {
        int writeCount = 0;
        long timestamp = System.currentTimeMillis();

        for (int nodeId = 0; nodeId < nodeCount && writeCount < quorumSize; nodeId++) {
            VectorClock nodeClock = vectorClocks.get(nodeId);
            nodeClock.increment(nodeId);

            VersionedValue versionedValue = new VersionedValue(value, timestamp, nodeId, nodeClock);
            nodes.get(nodeId).put(key, versionedValue);
            writeCount++;
        }

        return writeCount >= quorumSize;
    }

    // Follow-up 4: Anti-entropy mechanism - sync between nodes
    public void antiEntropy(int node1, int node2) {
        Map<String, VersionedValue> data1 = nodes.get(node1);
        Map<String, VersionedValue> data2 = nodes.get(node2);

        // Sync from node1 to node2
        for (Map.Entry<String, VersionedValue> entry : data1.entrySet()) {
            String key = entry.getKey();
            VersionedValue value1 = entry.getValue();
            VersionedValue value2 = data2.get(key);

            if (value2 == null || value1.timestamp > value2.timestamp) {
                data2.put(key, value1);
            }
        }

        // Sync from node2 to node1
        for (Map.Entry<String, VersionedValue> entry : data2.entrySet()) {
            String key = entry.getKey();
            VersionedValue value2 = entry.getValue();
            VersionedValue value1 = data1.get(key);

            if (value1 == null || value2.timestamp > value1.timestamp) {
                data1.put(key, value2);
            }
        }
    }

    // Follow-up 5: Get all conflicts for monitoring
    public Map<String, List<String>> getAllConflicts() {
        Map<String, List<String>> conflicts = new HashMap<>();

        for (Map.Entry<String, List<VersionedValue>> entry : conflictLog.entrySet()) {
            String key = entry.getKey();
            List<String> values = new ArrayList<>();

            for (VersionedValue version : entry.getValue()) {
                values.add(version.value + "@node" + version.nodeId);
            }

            conflicts.put(key, values);
        }

        return conflicts;
    }

    public static void main(String[] args) {
        System.out.println("=== Design Distributed Multi-Master KV Store Test ===");

        // Test Case 1: Basic put/get operations
        DesignDistributedMultiMasterKVStore store = new DesignDistributedMultiMasterKVStore(3);

        store.put("key1", "value1", 0);
        store.put("key2", "value2", 1);

        System.out.println("Get key1: " + store.get("key1")); // value1
        System.out.println("Get key2: " + store.get("key2")); // value2

        // Test Case 2: Conflict scenario
        store.put("conflictKey", "valueFromNode0", 0);
        store.put("conflictKey", "valueFromNode1", 1);

        System.out.println("Before resolution: " + store.get("conflictKey"));
        System.out.println("Conflicts: " + store.getAllConflicts());

        store.resolveConflict("conflictKey");
        System.out.println("After resolution: " + store.get("conflictKey"));

        // Test Case 3: Non-existent key
        System.out.println("Non-existent key: " + store.get("nonExistent")); // null

        // Test Case 4: Quorum operations (Follow-up)
        System.out.println("\n=== Quorum Operations ===");
        boolean quorumWrite = store.writeQuorum("quorumKey", "quorumValue", 2);
        System.out.println("Quorum write success: " + quorumWrite);

        String quorumRead = store.readQuorum("quorumKey", 2);
        System.out.println("Quorum read: " + quorumRead);

        // Test Case 5: Anti-entropy sync (Follow-up)
        System.out.println("\n=== Anti-Entropy Sync ===");
        store.put("syncKey", "beforeSync", 0);
        System.out.println("Before sync - Node 1 has syncKey: " +
                (store.nodes.get(1).containsKey("syncKey")));

        store.antiEntropy(0, 1);
        System.out.println("After sync - Node 1 has syncKey: " +
                (store.nodes.get(1).containsKey("syncKey")));

        // Test Case 6: Causal consistency (Follow-up)
        System.out.println("\n=== Causal Consistency ===");
        store.put("causalKey", "causalValue", 0);
        boolean consistent = store.isCausallyConsistent("causalKey", 0);
        System.out.println("Causally consistent: " + consistent);

        // Performance test
        System.out.println("\n=== Performance Test ===");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            store.put("perf_key_" + i, "value_" + i, i % 3);
        }

        int getCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (store.get("perf_key_" + i) != null) {
                getCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Processed " + getCount + " operations in " +
                (endTime - startTime) + "ms");
    }
}
