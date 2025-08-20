package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Design Distributed Transaction Log
 * 
 * Related LeetCode Problems:
 * - Similar to: Design Log System, Append-only Log
 * - No direct LeetCode equivalent (System Design)
 * 
 * Company Tags: Apache Kafka, Apache Pulsar, Database Systems, Blockchain
 * Difficulty: Hard
 * 
 * Description:
 * Design a distributed transaction log that supports:
 * 1. append(entry) - Append log entry to the distributed log
 * 2. read(index) - Read log entry at specific index
 * 3. truncate(index) - Remove log entries from index onwards
 * 
 * The system should handle:
 * - Ordering guarantees across distributed nodes
 * - Durability and consistency
 * - Log compaction for space efficiency
 * - Leader election for coordination
 * 
 * Constraints:
 * - At most 10^6 operations
 * - Support multiple nodes/replicas
 * - Handle network partitions
 * 
 * Follow-ups:
 * 1. Durability and ordering optimization
 * 2. Log compaction support
 * 3. Leader election mechanism
 * 4. WAL (Write-Ahead Logging) implementation
 */
public class DesignDistributedTransactionLog {
    private final int nodeCount;
    private final int replicationFactor;
    private final List<List<LogEntry>> nodeLog;
    private final AtomicLong globalIndex;
    private final Map<Long, LogEntry> indexToEntry;
    private final Queue<LogEntry> pendingEntries;
    private final ExecutorService executorService;
    private volatile int leaderId;
    private final Map<Integer, Long> nodeHeartbeats;
    private final Map<Long, Set<Integer>> replicationAcks;

    // Log entry with metadata
    private static class LogEntry {
        long index;
        String data;
        long timestamp;
        String transactionId;
        int leaderId;
        String checksum;
        Set<Integer> replicatedTo;
        boolean committed;

        LogEntry(long index, String data, String transactionId, int leaderId) {
            this.index = index;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
            this.transactionId = transactionId;
            this.leaderId = leaderId;
            this.checksum = calculateChecksum(data);
            this.replicatedTo = new HashSet<>();
            this.committed = false;
        }

        private String calculateChecksum(String data) {
            return String.valueOf((data + timestamp).hashCode());
        }

        @Override
        public String toString() {
            return String.format("[%d] %s (tx:%s, leader:%d, time:%d, committed:%s)",
                    index, data, transactionId, leaderId, timestamp, committed);
        }
    }

    /**
     * Constructor - Initialize distributed transaction log
     * Time: O(n), Space: O(n)
     */
    public DesignDistributedTransactionLog(int nodeCount) {
        this(nodeCount, Math.min(3, nodeCount)); // Default replication factor
    }

    public DesignDistributedTransactionLog(int nodeCount, int replicationFactor) {
        if (nodeCount <= 0 || replicationFactor <= 0 || replicationFactor > nodeCount) {
            throw new IllegalArgumentException("Invalid node count or replication factor");
        }

        this.nodeCount = nodeCount;
        this.replicationFactor = replicationFactor;
        this.nodeLog = new ArrayList<>();
        this.globalIndex = new AtomicLong(0);
        this.indexToEntry = new ConcurrentHashMap<>();
        this.pendingEntries = new ConcurrentLinkedQueue<>();
        this.executorService = Executors.newFixedThreadPool(nodeCount + 2);
        this.leaderId = 0; // Start with node 0 as leader
        this.nodeHeartbeats = new ConcurrentHashMap<>();
        this.replicationAcks = new ConcurrentHashMap<>();

        // Initialize node logs
        for (int i = 0; i < nodeCount; i++) {
            nodeLog.add(new ArrayList<>());
            nodeHeartbeats.put(i, System.currentTimeMillis());
        }

        // Start background processes
        startLeaderElection();
        startLogReplication();
        startHeartbeatMonitor();
    }

    /**
     * Append entry to distributed log
     * Time: O(r) where r is replication factor, Space: O(1)
     */
    public void append(String entry) {
        if (entry == null || entry.isEmpty()) {
            return;
        }

        // Generate transaction ID
        String transactionId = "tx_" + System.currentTimeMillis() + "_" + Thread.currentThread().getId();

        // Create log entry with global index
        long index = globalIndex.getAndIncrement();
        LogEntry logEntry = new LogEntry(index, entry, transactionId, leaderId);

        // Add to pending entries for replication
        pendingEntries.offer(logEntry);
        indexToEntry.put(index, logEntry);

        // Replicate to leader first
        synchronized (nodeLog.get(leaderId)) {
            nodeLog.get(leaderId).add(logEntry);
            logEntry.replicatedTo.add(leaderId);
        }

        System.out.println("Appended: " + logEntry);
    }

    /**
     * Read log entry at specific index
     * Time: O(1), Space: O(1)
     */
    public String read(int index) {
        if (index < 0) {
            return null;
        }

        LogEntry entry = indexToEntry.get((long) index);
        if (entry == null || !entry.committed) {
            return null; // Entry doesn't exist or not committed
        }

        return entry.toString();
    }

    /**
     * Truncate log from specific index onwards
     * Time: O(n * m) where n is nodes, m is entries after index, Space: O(1)
     */
    public void truncate(int index) {
        if (index < 0) {
            return;
        }

        // Remove entries from all nodes
        for (int nodeId = 0; nodeId < nodeCount; nodeId++) {
            List<LogEntry> log = nodeLog.get(nodeId);
            synchronized (log) {
                log.removeIf(entry -> entry.index >= index);
            }
        }

        // Remove from index mapping
        indexToEntry.entrySet().removeIf(entry -> entry.getKey() >= index);

        // Update global index
        globalIndex.set(index);

        System.out.println("Truncated log from index " + index);
    }

    /**
     * Start leader election process
     */
    private void startLeaderElection() {
        executorService.submit(() -> {
            while (true) {
                try {
                    Thread.sleep(5000); // Check every 5 seconds

                    // Simple leader election - highest ID of available nodes
                    int newLeader = -1;
                    long currentTime = System.currentTimeMillis();

                    for (int nodeId = nodeCount - 1; nodeId >= 0; nodeId--) {
                        Long lastHeartbeat = nodeHeartbeats.get(nodeId);
                        if (lastHeartbeat != null &&
                                (currentTime - lastHeartbeat) < 15000) { // 15 second timeout
                            newLeader = nodeId;
                            break;
                        }
                    }

                    if (newLeader != -1 && newLeader != leaderId) {
                        System.out.println("Leader changed from " + leaderId + " to " + newLeader);
                        leaderId = newLeader;
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Start log replication process
     */
    private void startLogReplication() {
        executorService.submit(() -> {
            while (true) {
                try {
                    LogEntry entry = pendingEntries.poll();
                    if (entry != null) {
                        replicateEntry(entry);
                    } else {
                        Thread.sleep(100); // Wait for new entries
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
    }

    /**
     * Replicate log entry to required number of nodes
     * Time: O(r), Space: O(1)
     */
    private void replicateEntry(LogEntry entry) {
        List<CompletableFuture<Boolean>> replicationFutures = new ArrayList<>();

        // Replicate to other nodes (excluding leader if already added)
        for (int nodeId = 0; nodeId < nodeCount; nodeId++) {
            if (nodeId != leaderId && entry.replicatedTo.size() < replicationFactor) {
                final int currentNodeId = nodeId;
                CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                    return replicateToNode(entry, currentNodeId);
                }, executorService);
                replicationFutures.add(future);
            }
        }

        // Wait for replication to complete
        int successfulReplications = 1; // Already replicated to leader
        for (CompletableFuture<Boolean> future : replicationFutures) {
            try {
                if (future.get(1000, TimeUnit.MILLISECONDS)) {
                    successfulReplications++;
                }
            } catch (Exception e) {
                // Replication failed or timed out
            }
        }

        // Commit if majority replicated
        if (successfulReplications >= (replicationFactor + 1) / 2) {
            entry.committed = true;
            System.out.println("Committed: " + entry.index + " (replicated to " + successfulReplications + " nodes)");
        }
    }

    /**
     * Replicate entry to specific node
     * Time: O(1), Space: O(1)
     */
    private boolean replicateToNode(LogEntry entry, int nodeId) {
        try {
            // Simulate network delay
            Thread.sleep(50 + (int) (Math.random() * 100));

            // Check if node is alive
            Long lastHeartbeat = nodeHeartbeats.get(nodeId);
            if (lastHeartbeat == null ||
                    (System.currentTimeMillis() - lastHeartbeat) > 10000) {
                return false; // Node is down
            }

            // Add to node's log
            synchronized (nodeLog.get(nodeId)) {
                nodeLog.get(nodeId).add(entry);
                entry.replicatedTo.add(nodeId);
            }

            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * Start heartbeat monitoring
     */
    private void startHeartbeatMonitor() {
        // Simulate heartbeats from nodes
        for (int nodeId = 0; nodeId < nodeCount; nodeId++) {
            final int currentNodeId = nodeId;
            executorService.submit(() -> {
                while (true) {
                    try {
                        // Simulate occasional node failures
                        if (Math.random() < 0.95) { // 95% uptime
                            nodeHeartbeats.put(currentNodeId, System.currentTimeMillis());
                        }
                        Thread.sleep(2000); // Heartbeat every 2 seconds
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }
    }

    // Follow-up 1: Log compaction - remove duplicate/obsolete entries
    public void compactLog() {
        Map<String, LogEntry> latestEntries = new HashMap<>();

        // Find latest entry for each transaction ID
        for (LogEntry entry : indexToEntry.values()) {
            String txId = entry.transactionId;
            if (!latestEntries.containsKey(txId) ||
                    entry.timestamp > latestEntries.get(txId).timestamp) {
                latestEntries.put(txId, entry);
            }
        }

        // Remove obsolete entries
        Set<Long> toKeep = new HashSet<>();
        for (LogEntry entry : latestEntries.values()) {
            toKeep.add(entry.index);
        }

        indexToEntry.entrySet().removeIf(entry -> !toKeep.contains(entry.getKey()));

        // Compact node logs
        for (List<LogEntry> log : nodeLog) {
            synchronized (log) {
                log.removeIf(entry -> !toKeep.contains(entry.index));
            }
        }

        System.out.println("Log compacted. Kept " + toKeep.size() + " entries");
    }

    // Follow-up 2: Get log statistics
    public Map<String, Object> getLogStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalEntries", indexToEntry.size());
        stats.put("currentLeader", leaderId);
        stats.put("replicationFactor", replicationFactor);

        long committedEntries = indexToEntry.values().stream()
                .mapToLong(entry -> entry.committed ? 1 : 0).sum();
        stats.put("committedEntries", committedEntries);
        stats.put("pendingEntries", indexToEntry.size() - committedEntries);

        // Node-specific stats
        Map<Integer, Integer> nodeSizes = new HashMap<>();
        for (int i = 0; i < nodeCount; i++) {
            nodeSizes.put(i, nodeLog.get(i).size());
        }
        stats.put("nodeSizes", nodeSizes);

        // Alive nodes
        long currentTime = System.currentTimeMillis();
        List<Integer> aliveNodes = new ArrayList<>();
        for (int nodeId = 0; nodeId < nodeCount; nodeId++) {
            Long lastHeartbeat = nodeHeartbeats.get(nodeId);
            if (lastHeartbeat != null && (currentTime - lastHeartbeat) < 10000) {
                aliveNodes.add(nodeId);
            }
        }
        stats.put("aliveNodes", aliveNodes);

        return stats;
    }

    // Follow-up 3: Read log entries in range
    public List<String> readRange(int startIndex, int endIndex) {
        List<String> result = new ArrayList<>();

        for (int i = startIndex; i <= endIndex; i++) {
            String entry = read(i);
            if (entry != null) {
                result.add(entry);
            }
        }

        return result;
    }

    // Follow-up 4: Get uncommitted entries
    public List<String> getUncommittedEntries() {
        return indexToEntry.values().stream()
                .filter(entry -> !entry.committed)
                .sorted(Comparator.comparingLong(entry -> entry.index))
                .map(LogEntry::toString)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    // Follow-up 5: Force commit entry (for testing)
    public boolean forceCommit(long index) {
        LogEntry entry = indexToEntry.get(index);
        if (entry != null) {
            entry.committed = true;
            return true;
        }
        return false;
    }

    // Follow-up 6: Simulate node failure
    public void simulateNodeFailure(int nodeId) {
        nodeHeartbeats.remove(nodeId);
        System.out.println("Simulated failure of node " + nodeId);
    }

    // Follow-up 7: Simulate node recovery
    public void simulateNodeRecovery(int nodeId) {
        nodeHeartbeats.put(nodeId, System.currentTimeMillis());
        System.out.println("Simulated recovery of node " + nodeId);
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Design Distributed Transaction Log Test ===");

        // Test Case 1: Basic append/read operations
        DesignDistributedTransactionLog log = new DesignDistributedTransactionLog(3);

        log.append("Transaction 1: Transfer $100 from A to B");
        log.append("Transaction 2: Transfer $50 from B to C");
        log.append("Transaction 3: Transfer $25 from C to A");

        // Wait for replication
        Thread.sleep(2000);

        System.out.println("Read index 0: " + log.read(0));
        System.out.println("Read index 1: " + log.read(1));
        System.out.println("Read index 2: " + log.read(2));

        // Test Case 2: Force commit for testing
        log.forceCommit(0);
        log.forceCommit(1);
        log.forceCommit(2);

        System.out.println("After committing:");
        System.out.println("Read index 0: " + log.read(0));
        System.out.println("Read index 1: " + log.read(1));

        // Test Case 3: Non-existent and out-of-bounds reads
        System.out.println("Read index 100: " + log.read(100)); // null
        System.out.println("Read index -1: " + log.read(-1)); // null

        // Test Case 4: Truncation
        log.truncate(1);
        System.out.println("After truncation from index 1:");
        System.out.println("Read index 0: " + log.read(0));
        System.out.println("Read index 1: " + log.read(1)); // null

        // Test Case 5: Log statistics (Follow-up)
        System.out.println("\n=== Log Statistics ===");
        System.out.println("Stats: " + log.getLogStats());

        // Test Case 6: Range reading (Follow-up)
        log.append("New entry after truncation");
        Thread.sleep(1000);
        log.forceCommit(1); // New entry at index 1

        System.out.println("\n=== Range Reading ===");
        List<String> range = log.readRange(0, 2);
        System.out.println("Range 0-2: " + range);

        // Test Case 7: Node failure simulation (Follow-up)
        System.out.println("\n=== Node Failure Simulation ===");
        System.out.println("Before failure: " + log.getLogStats());

        log.simulateNodeFailure(2);
        Thread.sleep(6000); // Wait for leader election
        System.out.println("After node 2 failure: " + log.getLogStats());

        log.simulateNodeRecovery(2);
        Thread.sleep(1000);
        System.out.println("After node 2 recovery: " + log.getLogStats());

        // Test Case 8: Uncommitted entries (Follow-up)
        log.append("Uncommitted transaction");
        Thread.sleep(500);
        System.out.println("\n=== Uncommitted Entries ===");
        System.out.println("Uncommitted: " + log.getUncommittedEntries());

        // Test Case 9: Log compaction (Follow-up)
        System.out.println("\n=== Log Compaction ===");
        System.out.println("Before compaction: " + log.getLogStats());
        log.compactLog();
        System.out.println("After compaction: " + log.getLogStats());

        // Test Case 10: Performance test
        System.out.println("\n=== Performance Test ===");
        DesignDistributedTransactionLog perfLog = new DesignDistributedTransactionLog(5);

        long startTime = System.currentTimeMillis();

        // Append 1000 entries
        for (int i = 0; i < 1000; i++) {
            perfLog.append("Performance test entry " + i);
        }

        Thread.sleep(3000); // Wait for replication

        // Force commit all entries
        for (int i = 0; i < 1000; i++) {
            perfLog.forceCommit(i);
        }

        // Read all entries
        int readCount = 0;
        for (int i = 0; i < 1000; i++) {
            if (perfLog.read(i) != null) {
                readCount++;
            }
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Performance test completed in: " + (endTime - startTime) + "ms");
        System.out.println("Successfully read " + readCount + "/1000 entries");
        System.out.println("Final stats: " + perfLog.getLogStats());

        // Cleanup
        log.executorService.shutdown();
        perfLog.executorService.shutdown();
    }
}
