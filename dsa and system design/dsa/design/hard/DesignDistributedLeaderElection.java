package design.hard;

import java.util.*;
import java.util.concurrent.*;

/**
 * Variation: Design Distributed Leader Election
 *
 * Description:
 * Design a distributed leader election algorithm.
 *
 * Constraints:
 * - At most 10^5 operations.
 *
 * Follow-up:
 * - Can you optimize for failover?
 * - Can you support multiple leaders?
 * 
 * Time Complexity: O(n) for election, O(1) for failover
 * Space Complexity: O(n) for node management
 * 
 * Company Tags: Google, Amazon, Apache Zookeeper, Kubernetes
 */
public class DesignDistributedLeaderElection {

    enum NodeStatus {
        ACTIVE, FAILED, CANDIDATE, LEADER
    }

    class ElectionNode {
        int nodeId;
        NodeStatus status;
        int priority; // Higher priority wins in case of tie
        long lastHeartbeat;
        int term; // Election term number

        ElectionNode(int nodeId, int priority) {
            this.nodeId = nodeId;
            this.priority = priority;
            this.status = NodeStatus.ACTIVE;
            this.lastHeartbeat = System.currentTimeMillis();
            this.term = 0;
        }

        boolean isHealthy() {
            return status == NodeStatus.ACTIVE &&
                    (System.currentTimeMillis() - lastHeartbeat) < HEARTBEAT_TIMEOUT;
        }

        void updateHeartbeat() {
            this.lastHeartbeat = System.currentTimeMillis();
        }
    }

    private final Map<Integer, ElectionNode> nodes;
    private int currentLeader;
    private int currentTerm;
    private final Random random;
    private static final long HEARTBEAT_TIMEOUT = 5000; // 5 seconds
    private static final long ELECTION_TIMEOUT = 3000; // 3 seconds

    public DesignDistributedLeaderElection(int nodeCount) {
        this.nodes = new ConcurrentHashMap<>();
        this.currentLeader = -1;
        this.currentTerm = 0;
        this.random = new Random();

        // Initialize nodes with random priorities
        for (int i = 0; i < nodeCount; i++) {
            int priority = random.nextInt(1000) + nodeCount - i; // Higher ID gets slight boost
            nodes.put(i, new ElectionNode(i, priority));
        }
    }

    public int electLeader() {
        // Clean up failed nodes first
        performHealthCheck();

        List<ElectionNode> healthyNodes = getHealthyNodes();

        if (healthyNodes.isEmpty()) {
            currentLeader = -1;
            return -1;
        }

        // Check if current leader is still healthy
        if (currentLeader != -1 &&
                nodes.containsKey(currentLeader) &&
                nodes.get(currentLeader).isHealthy()) {
            return currentLeader;
        }

        // Conduct new election using Bully Algorithm approach
        return conductElection(healthyNodes);
    }

    private int conductElection(List<ElectionNode> candidates) {
        currentTerm++;

        // Reset all nodes to candidate status
        for (ElectionNode node : candidates) {
            node.status = NodeStatus.CANDIDATE;
            node.term = currentTerm;
        }

        // Sort by priority (higher priority wins)
        // In case of tie, higher nodeId wins (simulating deterministic tie-breaking)
        candidates.sort((a, b) -> {
            int priorityCompare = Integer.compare(b.priority, a.priority);
            return priorityCompare != 0 ? priorityCompare : Integer.compare(b.nodeId, a.nodeId);
        });

        // Simulate election process - highest priority healthy node wins
        ElectionNode winner = candidates.get(0);
        winner.status = NodeStatus.LEADER;

        // Set others back to active
        for (int i = 1; i < candidates.size(); i++) {
            candidates.get(i).status = NodeStatus.ACTIVE;
        }

        currentLeader = winner.nodeId;

        System.out.println("Election completed. Node " + currentLeader +
                " elected as leader in term " + currentTerm +
                " with priority " + winner.priority);

        return currentLeader;
    }

    private List<ElectionNode> getHealthyNodes() {
        List<ElectionNode> healthy = new ArrayList<>();
        for (ElectionNode node : nodes.values()) {
            if (node.isHealthy()) {
                healthy.add(node);
            }
        }
        return healthy;
    }

    private void performHealthCheck() {
        for (ElectionNode node : nodes.values()) {
            if (!node.isHealthy() && node.status != NodeStatus.FAILED) {
                System.out.println("Node " + node.nodeId + " detected as unhealthy");
                node.status = NodeStatus.FAILED;

                // If the failed node was the leader, trigger re-election
                if (node.nodeId == currentLeader) {
                    System.out.println("Leader node " + currentLeader + " failed, triggering re-election");
                    currentLeader = -1;
                }
            }
        }
    }

    public void failNode(int nodeId) {
        ElectionNode node = nodes.get(nodeId);
        if (node != null) {
            node.status = NodeStatus.FAILED;
            System.out.println("Node " + nodeId + " manually failed");

            if (nodeId == currentLeader) {
                System.out.println("Current leader failed, need new election");
                currentLeader = -1;
            }
        }
    }

    // Additional methods for monitoring and management

    public void recoverNode(int nodeId) {
        ElectionNode node = nodes.get(nodeId);
        if (node != null && node.status == NodeStatus.FAILED) {
            node.status = NodeStatus.ACTIVE;
            node.updateHeartbeat();
            System.out.println("Node " + nodeId + " recovered");
        }
    }

    public void sendHeartbeat(int nodeId) {
        ElectionNode node = nodes.get(nodeId);
        if (node != null) {
            node.updateHeartbeat();
        }
    }

    public int getCurrentLeader() {
        return currentLeader;
    }

    public int getCurrentTerm() {
        return currentTerm;
    }

    public Map<String, Object> getElectionStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("currentLeader", currentLeader);
        stats.put("currentTerm", currentTerm);
        stats.put("totalNodes", nodes.size());

        long healthyCount = nodes.values().stream()
                .filter(ElectionNode::isHealthy)
                .count();
        stats.put("healthyNodes", healthyCount);

        return stats;
    }

    public List<Map<String, Object>> getNodeStats() {
        List<Map<String, Object>> nodeStats = new ArrayList<>();

        for (ElectionNode node : nodes.values()) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nodeId", node.nodeId);
            stats.put("status", node.status.toString());
            stats.put("priority", node.priority);
            stats.put("term", node.term);
            stats.put("isHealthy", node.isHealthy());
            stats.put("timeSinceHeartbeat", System.currentTimeMillis() - node.lastHeartbeat);

            nodeStats.add(stats);
        }

        return nodeStats;
    }

    public static void main(String[] args) throws InterruptedException {
        DesignDistributedLeaderElection election = new DesignDistributedLeaderElection(5);

        System.out.println("=== Initial Election ===");
        int leader1 = election.electLeader();
        System.out.println("First leader: " + leader1);
        System.out.println("Election stats: " + election.getElectionStats());

        System.out.println("\n=== Node Failure Test ===");
        election.failNode(leader1);
        int leader2 = election.electLeader();
        System.out.println("New leader after failure: " + leader2);

        System.out.println("\n=== Multiple Failures ===");
        election.failNode(0);
        election.failNode(1);
        int leader3 = election.electLeader();
        System.out.println("Leader after multiple failures: " + leader3);

        System.out.println("\n=== Edge Case: Fail All Remaining Nodes ===");
        election.failNode(2);
        election.failNode(3);
        election.failNode(4);
        int leader4 = election.electLeader();
        System.out.println("Leader when all nodes failed: " + leader4); // Should be -1

        System.out.println("\n=== Recovery Test ===");
        election.recoverNode(0);
        election.recoverNode(2);
        int leader5 = election.electLeader();
        System.out.println("Leader after recovery: " + leader5);

        System.out.println("\n=== Heartbeat Simulation ===");
        // Simulate heartbeats
        for (int i = 0; i < 3; i++) {
            election.sendHeartbeat(leader5);
            Thread.sleep(1000);
        }

        // Verify leader is still stable
        int stableLeader = election.electLeader();
        System.out.println("Stable leader: " + stableLeader);

        System.out.println("\n=== Final Node Statistics ===");
        for (Map<String, Object> nodeStats : election.getNodeStats()) {
            System.out.println(nodeStats);
        }

        System.out.println("\nFinal election stats: " + election.getElectionStats());
    }
}
