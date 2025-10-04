package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.io.*;

/**
 * Design Distributed Snapshot
 * 
 * Related LeetCode Problems:
 * - Similar to: Snapshot Array (1146), Version Control System
 * - No direct LeetCode equivalent (System Design)
 * 
 * Company Tags: Google, Microsoft, Amazon, Git, Database Systems
 * Difficulty: Hard
 * 
 * Description:
 * Design a distributed snapshot system that supports:
 * 1. takeSnapshot(snapshotId) - Create a snapshot of current state
 * 2. getSnapshot(snapshotId) - Retrieve a specific snapshot
 * 3. Support incremental snapshots and compression
 * 
 * The system should handle:
 * - Multiple nodes contributing to snapshots
 * - Memory optimization through compression
 * - Incremental snapshots (only changes since last snapshot)
 * - Concurrent snapshot operations
 * 
 * Constraints:
 * - At most 10^5 operations
 * - Support multiple nodes/partitions
 * - Handle large datasets efficiently
 * 
 * Follow-ups:
 * 1. Memory usage optimization
 * 2. Incremental snapshot support
 * 3. Compression and deduplication
 * 4. Distributed coordination for consistent snapshots
 */
public class DesignDistributedSnapshot {
    private final int nodeCount;
    private final Map<Integer, Snapshot> snapshots;
    private final List<Map<String, String>> nodeData;
    private final Map<String, String> changeLog;
    private final ExecutorService executorService;
    private volatile int lastSnapshotId;

    // Snapshot metadata
    private static class Snapshot {
        int snapshotId;
        long timestamp;
        Map<Integer, NodeSnapshot> nodeSnapshots;
        int totalSize;
        boolean compressed;
        String checksum;

        Snapshot(int snapshotId) {
            this.snapshotId = snapshotId;
            this.timestamp = System.currentTimeMillis();
            this.nodeSnapshots = new HashMap<>();
            this.totalSize = 0;
            this.compressed = false;
        }
    }

    // Per-node snapshot data
    private static class NodeSnapshot {
        int nodeId;
        Map<String, String> data;
        byte[] compressedData;
        boolean isIncremental;
        Set<String> changedKeys;
        int baseSnapshotId;

        NodeSnapshot(int nodeId, Map<String, String> data) {
            this.nodeId = nodeId;
            this.data = new HashMap<>(data);
            this.isIncremental = false;
            this.changedKeys = new HashSet<>();
            this.baseSnapshotId = -1;
        }

        NodeSnapshot(int nodeId, Set<String> changedKeys, Map<String, String> changes, int baseSnapshotId) {
            this.nodeId = nodeId;
            this.data = new HashMap<>(changes);
            this.isIncremental = true;
            this.changedKeys = new HashSet<>(changedKeys);
            this.baseSnapshotId = baseSnapshotId;
        }
    }

    /**
     * Constructor - Initialize distributed snapshot system
     * Time: O(n), Space: O(n)
     */
    public DesignDistributedSnapshot(int nodeCount) {
        if (nodeCount <= 0) {
            throw new IllegalArgumentException("Node count must be positive");
        }

        this.nodeCount = nodeCount;
        this.snapshots = new ConcurrentHashMap<>();
        this.nodeData = new ArrayList<>();
        this.changeLog = new ConcurrentHashMap<>();
        this.executorService = Executors.newFixedThreadPool(Math.min(nodeCount, 10));
        this.lastSnapshotId = -1;

        // Initialize node data
        for (int i = 0; i < nodeCount; i++) {
            nodeData.add(new ConcurrentHashMap<>());
        }
    }

    /**
     * Take snapshot of current distributed state
     * Time: O(n * m) where n is nodes, m is data per node, Space: O(n * m)
     */
    public void takeSnapshot(int snapshotId) {
        if (snapshots.containsKey(snapshotId)) {
            return; // Snapshot already exists
        }

        Snapshot snapshot = new Snapshot(snapshotId);
        List<CompletableFuture<NodeSnapshot>> futures = new ArrayList<>();

        // Create snapshot for each node concurrently
        for (int nodeId = 0; nodeId < nodeCount; nodeId++) {
            final int currentNodeId = nodeId;
            CompletableFuture<NodeSnapshot> future = CompletableFuture.supplyAsync(() -> {
                return createNodeSnapshot(currentNodeId, snapshotId);
            }, executorService);
            futures.add(future);
        }

        // Collect all node snapshots
        try {
            for (int i = 0; i < futures.size(); i++) {
                NodeSnapshot nodeSnapshot = futures.get(i).get();
                snapshot.nodeSnapshots.put(i, nodeSnapshot);
                snapshot.totalSize += nodeSnapshot.data.size();
            }
        } catch (Exception e) {
            System.err.println("Error creating snapshot: " + e.getMessage());
            return;
        }

        // Calculate checksum for integrity
        snapshot.checksum = calculateChecksum(snapshot);

        snapshots.put(snapshotId, snapshot);
        lastSnapshotId = snapshotId;

        // Clear change log after successful snapshot
        changeLog.clear();
    }

    /**
     * Get snapshot data
     * Time: O(n * m) for full snapshot, O(k) for incremental where k is changes,
     * Space: O(n * m)
     */
    public String getSnapshot(int snapshotId) {
        Snapshot snapshot = snapshots.get(snapshotId);
        if (snapshot == null) {
            return null;
        }

        // Reconstruct full state if incremental snapshots are involved
        Map<String, String> fullState = reconstructFullState(snapshotId);

        StringBuilder result = new StringBuilder();
        result.append("Snapshot ").append(snapshotId)
                .append(" (").append(new Date(snapshot.timestamp)).append(")\n");
        result.append("Total size: ").append(snapshot.totalSize).append(" entries\n");
        result.append("Compressed: ").append(snapshot.compressed).append("\n");
        result.append("Checksum: ").append(snapshot.checksum).append("\n");
        result.append("Data:\n");

        // Sort keys for consistent output
        List<String> sortedKeys = new ArrayList<>(fullState.keySet());
        sortedKeys.sort(String::compareTo);

        for (String key : sortedKeys) {
            result.append("  ").append(key).append(" = ").append(fullState.get(key)).append("\n");
        }

        return result.toString();
    }

    /**
     * Create snapshot for a specific node
     * Time: O(m), Space: O(m)
     */
    private NodeSnapshot createNodeSnapshot(int nodeId, int snapshotId) {
        Map<String, String> currentData = nodeData.get(nodeId);

        // Check if we can create incremental snapshot
        if (lastSnapshotId >= 0 && canCreateIncremental(nodeId)) {
            return createIncrementalSnapshot(nodeId, currentData, snapshotId);
        } else {
            return new NodeSnapshot(nodeId, currentData);
        }
    }

    /**
     * Create incremental snapshot (only changes since last snapshot)
     * Time: O(m), Space: O(k) where k is number of changes
     */
    private NodeSnapshot createIncrementalSnapshot(int nodeId, Map<String, String> currentData, int snapshotId) {
        Set<String> changedKeys = new HashSet<>();
        Map<String, String> changes = new HashMap<>();

        // Find changes since last snapshot
        Snapshot lastSnapshot = snapshots.get(lastSnapshotId);
        NodeSnapshot lastNodeSnapshot = lastSnapshot.nodeSnapshots.get(nodeId);

        // Check for new/modified keys
        for (Map.Entry<String, String> entry : currentData.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            if (!lastNodeSnapshot.data.containsKey(key) ||
                    !lastNodeSnapshot.data.get(key).equals(value)) {
                changedKeys.add(key);
                changes.put(key, value);
            }
        }

        // Check for deleted keys
        for (String key : lastNodeSnapshot.data.keySet()) {
            if (!currentData.containsKey(key)) {
                changedKeys.add(key);
                changes.put(key, null); // null indicates deletion
            }
        }

        return new NodeSnapshot(nodeId, changedKeys, changes, lastSnapshotId);
    }

    /**
     * Check if incremental snapshot is beneficial
     * Time: O(1), Space: O(1)
     */
    private boolean canCreateIncremental(int nodeId) {
        if (lastSnapshotId < 0)
            return false;

        // Create incremental if changes are less than 30% of total data
        int totalKeys = nodeData.get(nodeId).size();
        int changedKeys = 0;

        // Count approximate changes (simplified heuristic)
        for (String key : changeLog.keySet()) {
            if (belongsToNode(key, nodeId)) {
                changedKeys++;
            }
        }

        return changedKeys < (totalKeys * 0.3);
    }

    /**
     * Reconstruct full state from incremental snapshots
     * Time: O(s * k) where s is snapshot chain length, k is changes, Space: O(n *
     * m)
     */
    private Map<String, String> reconstructFullState(int snapshotId) {
        Map<String, String> fullState = new HashMap<>();
        List<Integer> snapshotChain = buildSnapshotChain(snapshotId);

        // Apply snapshots in chronological order
        for (int sid : snapshotChain) {
            Snapshot snapshot = snapshots.get(sid);
            for (NodeSnapshot nodeSnapshot : snapshot.nodeSnapshots.values()) {
                if (nodeSnapshot.isIncremental) {
                    // Apply incremental changes
                    for (Map.Entry<String, String> entry : nodeSnapshot.data.entrySet()) {
                        if (entry.getValue() == null) {
                            fullState.remove(entry.getKey()); // Deletion
                        } else {
                            fullState.put(entry.getKey(), entry.getValue()); // Add/Update
                        }
                    }
                } else {
                    // Apply full snapshot
                    fullState.putAll(nodeSnapshot.data);
                }
            }
        }

        return fullState;
    }

    /**
     * Build chain of snapshots needed to reconstruct state
     * Time: O(s), Space: O(s)
     */
    private List<Integer> buildSnapshotChain(int targetSnapshotId) {
        List<Integer> chain = new ArrayList<>();
        int currentId = targetSnapshotId;

        while (currentId >= 0) {
            chain.add(0, currentId); // Add to front for chronological order

            Snapshot snapshot = snapshots.get(currentId);
            if (snapshot == null)
                break;

            // Find base snapshot for incremental snapshots
            boolean hasIncremental = false;
            int baseId = -1;

            for (NodeSnapshot nodeSnapshot : snapshot.nodeSnapshots.values()) {
                if (nodeSnapshot.isIncremental) {
                    hasIncremental = true;
                    baseId = Math.max(baseId, nodeSnapshot.baseSnapshotId);
                }
            }

            currentId = hasIncremental ? baseId : -1;
        }

        return chain;
    }

    /**
     * Calculate checksum for snapshot integrity
     * Time: O(n * m), Space: O(1)
     */
    private String calculateChecksum(Snapshot snapshot) {
        StringBuilder data = new StringBuilder();

        // Sort node snapshots by ID for consistent checksum
        List<Integer> nodeIds = new ArrayList<>(snapshot.nodeSnapshots.keySet());
        nodeIds.sort(Integer::compareTo);

        for (int nodeId : nodeIds) {
            NodeSnapshot nodeSnapshot = snapshot.nodeSnapshots.get(nodeId);

            // Sort keys for consistent checksum
            List<String> keys = new ArrayList<>(nodeSnapshot.data.keySet());
            keys.sort(String::compareTo);

            for (String key : keys) {
                data.append(key).append("=").append(nodeSnapshot.data.get(key)).append(";");
            }
        }

        return String.valueOf(data.toString().hashCode());
    }

    /**
     * Check if key belongs to specific node (simple partitioning)
     * Time: O(1), Space: O(1)
     */
    private boolean belongsToNode(String key, int nodeId) {
        return Math.abs(key.hashCode()) % nodeCount == nodeId;
    }

    // Follow-up 1: Compress snapshot data
    public void compressSnapshot(int snapshotId) {
        Snapshot snapshot = snapshots.get(snapshotId);
        if (snapshot == null || snapshot.compressed) {
            return;
        }

        for (NodeSnapshot nodeSnapshot : snapshot.nodeSnapshots.values()) {
            try {
                nodeSnapshot.compressedData = compress(nodeSnapshot.data);
                nodeSnapshot.data = null; // Free memory
            } catch (IOException e) {
                System.err.println("Compression failed: " + e.getMessage());
            }
        }

        snapshot.compressed = true;
    }

    // Follow-up 2: Decompress snapshot data
    public void decompressSnapshot(int snapshotId) {
        Snapshot snapshot = snapshots.get(snapshotId);
        if (snapshot == null || !snapshot.compressed) {
            return;
        }

        for (NodeSnapshot nodeSnapshot : snapshot.nodeSnapshots.values()) {
            try {
                if (nodeSnapshot.compressedData != null) {
                    nodeSnapshot.data = decompress(nodeSnapshot.compressedData);
                }
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Decompression failed: " + e.getMessage());
            }
        }

        snapshot.compressed = false;
    }

    // Follow-up 3: Compression utilities
    private byte[] compress(Map<String, String> data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(new GZIPOutputStream(baos))) {
            oos.writeObject(data);
        }
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    private Map<String, String> decompress(byte[] compressedData) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (ObjectInputStream ois = new ObjectInputStream(new GZIPInputStream(bais))) {
            return (Map<String, String>) ois.readObject();
        }
    }

    // Follow-up 4: Update node data (triggers change tracking)
    public void setNodeData(int nodeId, String key, String value) {
        if (nodeId < 0 || nodeId >= nodeCount) {
            return;
        }

        nodeData.get(nodeId).put(key, value);
        changeLog.put(key, value);
    }

    // Follow-up 5: Get snapshot metadata
    public Map<String, Object> getSnapshotMetadata(int snapshotId) {
        Snapshot snapshot = snapshots.get(snapshotId);
        if (snapshot == null) {
            return null;
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("snapshotId", snapshot.snapshotId);
        metadata.put("timestamp", snapshot.timestamp);
        metadata.put("totalSize", snapshot.totalSize);
        metadata.put("compressed", snapshot.compressed);
        metadata.put("checksum", snapshot.checksum);
        metadata.put("nodeCount", snapshot.nodeSnapshots.size());

        // Count incremental vs full snapshots
        long incrementalCount = snapshot.nodeSnapshots.values().stream()
                .mapToLong(ns -> ns.isIncremental ? 1 : 0).sum();
        metadata.put("incrementalNodes", incrementalCount);
        metadata.put("fullNodes", snapshot.nodeSnapshots.size() - incrementalCount);

        return metadata;
    }

    // Follow-up 6: List all snapshots
    public List<Integer> listSnapshots() {
        List<Integer> snapshotIds = new ArrayList<>(snapshots.keySet());
        snapshotIds.sort(Integer::compareTo);
        return snapshotIds;
    }

    // Follow-up 7: Delete old snapshot
    public boolean deleteSnapshot(int snapshotId) {
        return snapshots.remove(snapshotId) != null;
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Design Distributed Snapshot Test ===");

        // Test Case 1: Basic snapshot operations
        DesignDistributedSnapshot snapshotSystem = new DesignDistributedSnapshot(3);

        // Add some data to nodes
        snapshotSystem.setNodeData(0, "key1", "value1");
        snapshotSystem.setNodeData(0, "key2", "value2");
        snapshotSystem.setNodeData(1, "key3", "value3");
        snapshotSystem.setNodeData(2, "key4", "value4");

        // Take first snapshot
        snapshotSystem.takeSnapshot(1);
        System.out.println("Snapshot 1 taken");
        System.out.println("Metadata: " + snapshotSystem.getSnapshotMetadata(1));

        // Test Case 2: Retrieve snapshot
        String snapshot1Data = snapshotSystem.getSnapshot(1);
        System.out.println("Snapshot 1 data:\n" + snapshot1Data);

        // Test Case 3: Incremental snapshot
        snapshotSystem.setNodeData(0, "key1", "updated_value1"); // Update
        snapshotSystem.setNodeData(1, "key5", "value5"); // Add new
        snapshotSystem.setNodeData(2, "key4", null); // Delete

        snapshotSystem.takeSnapshot(2);
        System.out.println("\nSnapshot 2 taken (incremental)");
        System.out.println("Metadata: " + snapshotSystem.getSnapshotMetadata(2));

        String snapshot2Data = snapshotSystem.getSnapshot(2);
        System.out.println("Snapshot 2 data:\n" + snapshot2Data);

        // Test Case 4: Non-existent snapshot
        System.out.println("Non-existent snapshot: " + snapshotSystem.getSnapshot(999));

        // Test Case 5: Compression (Follow-up)
        System.out.println("\n=== Compression Test ===");
        System.out.println("Before compression: " + snapshotSystem.getSnapshotMetadata(1));
        snapshotSystem.compressSnapshot(1);
        System.out.println("After compression: " + snapshotSystem.getSnapshotMetadata(1));

        snapshotSystem.decompressSnapshot(1);
        System.out.println("After decompression: " + snapshotSystem.getSnapshotMetadata(1));

        // Test Case 6: List snapshots (Follow-up)
        System.out.println("\n=== Snapshot Management ===");
        System.out.println("All snapshots: " + snapshotSystem.listSnapshots());

        // Test Case 7: Large dataset performance test
        System.out.println("\n=== Performance Test ===");
        DesignDistributedSnapshot perfSystem = new DesignDistributedSnapshot(5);

        long startTime = System.currentTimeMillis();

        // Add 10,000 entries across nodes
        for (int i = 0; i < 10000; i++) {
            int nodeId = i % 5;
            perfSystem.setNodeData(nodeId, "key_" + i, "value_" + i);
        }

        // Take snapshot
        perfSystem.takeSnapshot(100);

        // Modify some data
        for (int i = 0; i < 1000; i++) {
            int nodeId = i % 5;
            perfSystem.setNodeData(nodeId, "key_" + i, "updated_value_" + i);
        }

        // Take incremental snapshot
        perfSystem.takeSnapshot(101);

        long endTime = System.currentTimeMillis();

        System.out.println("Performance test completed in: " + (endTime - startTime) + "ms");
        System.out.println("Snapshot 100 metadata: " + perfSystem.getSnapshotMetadata(100));
        System.out.println("Snapshot 101 metadata: " + perfSystem.getSnapshotMetadata(101));

        // Test Case 8: Cleanup
        boolean deleted = snapshotSystem.deleteSnapshot(1);
        System.out.println("\nSnapshot 1 deleted: " + deleted);
        System.out.println("Remaining snapshots: " + snapshotSystem.listSnapshots());

        // Cleanup
        snapshotSystem.executorService.shutdown();
        perfSystem.executorService.shutdown();
    }
}
