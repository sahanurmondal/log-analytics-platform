package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Design Distributed Database System
 *
 * Description: Design a distributed database that supports:
 * - Data sharding and replication
 * - ACID transactions across nodes
 * - Consensus protocols (Raft/PBFT)
 * - Query routing and optimization
 * 
 * Constraints:
 * - Support horizontal scaling
 * - Handle node failures gracefully
 * - Maintain consistency guarantees
 *
 * Follow-up:
 * - How to handle network partitions?
 * - CAP theorem trade-offs?
 * 
 * Time Complexity: O(log n) for queries, O(n) for distributed transactions
 * Space Complexity: O(data_size * replication_factor)
 * 
 * Company Tags: Google Spanner, Amazon DynamoDB, MongoDB
 */
public class DesignDistributedDatabase {

    enum NodeStatus {
        ACTIVE, FAILED, RECOVERING
    }

    enum TransactionStatus {
        PENDING, COMMITTED, ABORTED, PREPARING
    }

    class DatabaseNode {
        String nodeId;
        NodeStatus status;
        Map<String, Object> localData;
        Set<String> replicationGroups;
        Map<String, Transaction> activeTransactions;
        long lastHeartbeat;
        boolean isLeader;

        DatabaseNode(String nodeId) {
            this.nodeId = nodeId;
            this.status = NodeStatus.ACTIVE;
            this.localData = new ConcurrentHashMap<>();
            this.replicationGroups = ConcurrentHashMap.newKeySet();
            this.activeTransactions = new ConcurrentHashMap<>();
            this.lastHeartbeat = System.currentTimeMillis();
            this.isLeader = false;
        }

        void put(String key, Object value, String transactionId) {
            if (transactionId != null) {
                Transaction tx = activeTransactions.get(transactionId);
                if (tx != null) {
                    tx.operations.add(new Operation("PUT", key, value));
                    return;
                }
            }
            localData.put(key, value);
        }

        Object get(String key) {
            return localData.get(key);
        }

        boolean delete(String key, String transactionId) {
            if (transactionId != null) {
                Transaction tx = activeTransactions.get(transactionId);
                if (tx != null) {
                    tx.operations.add(new Operation("DELETE", key, null));
                    return true;
                }
            }
            return localData.remove(key) != null;
        }

        String beginTransaction() {
            String txId = UUID.randomUUID().toString();
            Transaction tx = new Transaction(txId, nodeId);
            activeTransactions.put(txId, tx);
            return txId;
        }

        boolean commitTransaction(String transactionId) {
            Transaction tx = activeTransactions.get(transactionId);
            if (tx == null || tx.status != TransactionStatus.PENDING) {
                return false;
            }

            // Apply all operations
            for (Operation op : tx.operations) {
                switch (op.type) {
                    case "PUT":
                        localData.put(op.key, op.value);
                        break;
                    case "DELETE":
                        localData.remove(op.key);
                        break;
                }
            }

            tx.status = TransactionStatus.COMMITTED;
            activeTransactions.remove(transactionId);
            return true;
        }

        void abortTransaction(String transactionId) {
            Transaction tx = activeTransactions.get(transactionId);
            if (tx != null) {
                tx.status = TransactionStatus.ABORTED;
                activeTransactions.remove(transactionId);
            }
        }
    }

    class Transaction {
        String transactionId;
        String coordinatorNodeId;
        TransactionStatus status;
        List<Operation> operations;
        long startTime;
        Set<String> participatingNodes;

        Transaction(String transactionId, String coordinatorNodeId) {
            this.transactionId = transactionId;
            this.coordinatorNodeId = coordinatorNodeId;
            this.status = TransactionStatus.PENDING;
            this.operations = new ArrayList<>();
            this.startTime = System.currentTimeMillis();
            this.participatingNodes = new HashSet<>();
        }
    }

    class Operation {
        String type;
        String key;
        Object value;

        Operation(String type, String key, Object value) {
            this.type = type;
            this.key = key;
            this.value = value;
        }
    }

    class ShardManager {
        private int numShards;
        private Map<Integer, Set<String>> shardToNodes;
        private ConsistentHashRing hashRing;

        ShardManager(int numShards) {
            this.numShards = numShards;
            this.shardToNodes = new HashMap<>();
            this.hashRing = new ConsistentHashRing();

            for (int i = 0; i < numShards; i++) {
                shardToNodes.put(i, new HashSet<>());
            }
        }

        int getShardForKey(String key) {
            return Math.abs(key.hashCode()) % numShards;
        }

        Set<String> getNodesForShard(int shardId) {
            return shardToNodes.getOrDefault(shardId, new HashSet<>());
        }

        void addNodeToShard(String nodeId, int shardId) {
            shardToNodes.get(shardId).add(nodeId);
            hashRing.addNode(nodeId);
        }

        void removeNodeFromShard(String nodeId, int shardId) {
            shardToNodes.get(shardId).remove(nodeId);
            hashRing.removeNode(nodeId);
        }
    }

    class ConsistentHashRing {
        private TreeMap<Integer, String> ring;
        private int virtualNodes;

        ConsistentHashRing() {
            this.ring = new TreeMap<>();
            this.virtualNodes = 150;
        }

        void addNode(String nodeId) {
            for (int i = 0; i < virtualNodes; i++) {
                String virtualNodeId = nodeId + ":" + i;
                int hash = virtualNodeId.hashCode();
                ring.put(hash, nodeId);
            }
        }

        void removeNode(String nodeId) {
            for (int i = 0; i < virtualNodes; i++) {
                String virtualNodeId = nodeId + ":" + i;
                int hash = virtualNodeId.hashCode();
                ring.remove(hash);
            }
        }

        String getNodeForKey(String key) {
            if (ring.isEmpty())
                return null;

            int hash = key.hashCode();
            Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
            return entry != null ? entry.getValue() : ring.firstEntry().getValue();
        }
    }

    private Map<String, DatabaseNode> nodes;
    private ShardManager shardManager;
    private int replicationFactor;
    private ScheduledExecutorService scheduler;

    public DesignDistributedDatabase(int numNodes, int numShards, int replicationFactor) {
        this.nodes = new ConcurrentHashMap<>();
        this.shardManager = new ShardManager(numShards);
        this.replicationFactor = replicationFactor;
        this.scheduler = Executors.newScheduledThreadPool(2);

        // Initialize nodes
        for (int i = 0; i < numNodes; i++) {
            String nodeId = "db-node-" + i;
            DatabaseNode node = new DatabaseNode(nodeId);
            nodes.put(nodeId, node);

            // Assign nodes to shards in round-robin fashion
            int shardId = i % numShards;
            shardManager.addNodeToShard(nodeId, shardId);
        }

        // Start background tasks
        startHealthChecking();
    }

    public void put(String key, Object value) {
        put(key, value, null);
    }

    public void put(String key, Object value, String transactionId) {
        int shardId = shardManager.getShardForKey(key);
        Set<String> nodeIds = shardManager.getNodesForShard(shardId);

        // Write to primary and replica nodes
        int successCount = 0;
        for (String nodeId : nodeIds) {
            DatabaseNode node = nodes.get(nodeId);
            if (node != null && node.status == NodeStatus.ACTIVE) {
                node.put(key, value, transactionId);
                successCount++;

                if (successCount >= replicationFactor) {
                    break;
                }
            }
        }
    }

    public Object get(String key) {
        int shardId = shardManager.getShardForKey(key);
        Set<String> nodeIds = shardManager.getNodesForShard(shardId);

        // Read from any available node in the shard
        for (String nodeId : nodeIds) {
            DatabaseNode node = nodes.get(nodeId);
            if (node != null && node.status == NodeStatus.ACTIVE) {
                Object value = node.get(key);
                if (value != null) {
                    return value;
                }
            }
        }

        return null;
    }

    public boolean delete(String key) {
        return delete(key, null);
    }

    public boolean delete(String key, String transactionId) {
        int shardId = shardManager.getShardForKey(key);
        Set<String> nodeIds = shardManager.getNodesForShard(shardId);

        boolean deleted = false;
        for (String nodeId : nodeIds) {
            DatabaseNode node = nodes.get(nodeId);
            if (node != null && node.status == NodeStatus.ACTIVE) {
                if (node.delete(key, transactionId)) {
                    deleted = true;
                }
            }
        }

        return deleted;
    }

    public String beginTransaction(String coordinatorNodeId) {
        DatabaseNode coordinator = nodes.get(coordinatorNodeId);
        if (coordinator != null) {
            return coordinator.beginTransaction();
        }
        return null;
    }

    public boolean commitTransaction(String transactionId, String coordinatorNodeId) {
        DatabaseNode coordinator = nodes.get(coordinatorNodeId);
        if (coordinator != null) {
            Transaction tx = coordinator.activeTransactions.get(transactionId);
            if (tx != null) {
                // Two-phase commit protocol
                return performTwoPhaseCommit(tx);
            }
        }
        return false;
    }

    private boolean performTwoPhaseCommit(Transaction tx) {
        // Phase 1: Prepare
        boolean allPrepared = true;
        for (String nodeId : tx.participatingNodes) {
            DatabaseNode node = nodes.get(nodeId);
            if (node == null || node.status != NodeStatus.ACTIVE) {
                allPrepared = false;
                break;
            }
            // In a real implementation, we'd send PREPARE messages
        }

        // Phase 2: Commit or Abort
        if (allPrepared) {
            for (String nodeId : tx.participatingNodes) {
                DatabaseNode node = nodes.get(nodeId);
                if (node != null) {
                    node.commitTransaction(tx.transactionId);
                }
            }
            tx.status = TransactionStatus.COMMITTED;
            return true;
        } else {
            for (String nodeId : tx.participatingNodes) {
                DatabaseNode node = nodes.get(nodeId);
                if (node != null) {
                    node.abortTransaction(tx.transactionId);
                }
            }
            tx.status = TransactionStatus.ABORTED;
            return false;
        }
    }

    public void abortTransaction(String transactionId, String coordinatorNodeId) {
        DatabaseNode coordinator = nodes.get(coordinatorNodeId);
        if (coordinator != null) {
            coordinator.abortTransaction(transactionId);
        }
    }

    private void startHealthChecking() {
        scheduler.scheduleWithFixedDelay(() -> {
            for (DatabaseNode node : nodes.values()) {
                // Simulate health check
                boolean healthy = Math.random() > 0.02; // 98% uptime

                if (!healthy && node.status == NodeStatus.ACTIVE) {
                    node.status = NodeStatus.FAILED;
                    System.out.println("Node failed: " + node.nodeId);
                } else if (healthy && node.status == NodeStatus.FAILED) {
                    node.status = NodeStatus.ACTIVE;
                    System.out.println("Node recovered: " + node.nodeId);
                }

                node.lastHeartbeat = System.currentTimeMillis();
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public Map<String, Object> getSystemStats() {
        Map<String, Object> stats = new HashMap<>();

        int activeNodes = (int) nodes.values().stream()
                .filter(node -> node.status == NodeStatus.ACTIVE)
                .count();

        int totalData = nodes.values().stream()
                .mapToInt(node -> node.localData.size())
                .sum();

        int activeTransactions = nodes.values().stream()
                .mapToInt(node -> node.activeTransactions.size())
                .sum();

        stats.put("totalNodes", nodes.size());
        stats.put("activeNodes", activeNodes);
        stats.put("totalDataItems", totalData);
        stats.put("activeTransactions", activeTransactions);
        stats.put("replicationFactor", replicationFactor);

        return stats;
    }

    public void shutdown() {
        scheduler.shutdown();
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedDatabase db = new DesignDistributedDatabase(5, 3, 2);

        System.out.println("Initial stats: " + db.getSystemStats());

        // Test basic operations
        db.put("user:1", "John Doe");
        db.put("user:2", "Jane Smith");
        db.put("user:3", "Bob Johnson");

        System.out.println("Get user:1: " + db.get("user:1"));
        System.out.println("Get user:2: " + db.get("user:2"));
        System.out.println("Get user:3: " + db.get("user:3"));

        // Test transaction
        String coordinatorNode = "db-node-0";
        String txId = db.beginTransaction(coordinatorNode);

        if (txId != null) {
            db.put("user:4", "Alice Brown", txId);
            db.put("user:5", "Charlie Wilson", txId);

            boolean committed = db.commitTransaction(txId, coordinatorNode);
            System.out.println("Transaction committed: " + committed);
        }

        System.out.println("Get user:4: " + db.get("user:4"));
        System.out.println("Get user:5: " + db.get("user:5"));

        // Delete operation
        boolean deleted = db.delete("user:1");
        System.out.println("Deleted user:1: " + deleted);
        System.out.println("Get user:1 after delete: " + db.get("user:1"));

        System.out.println("\nFinal stats: " + db.getSystemStats());

        // Wait to see health check in action
        Thread.sleep(6000);
        System.out.println("Stats after health check: " + db.getSystemStats());

        db.shutdown();
    }
}
