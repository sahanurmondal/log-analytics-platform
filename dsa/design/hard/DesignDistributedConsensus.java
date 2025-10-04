package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Design Distributed Consensus System (Raft Algorithm)
 *
 * Description: Design a distributed consensus system that supports:
 * - Leader election with term-based voting
 * - Log replication across nodes
 * - Fault tolerance and split-brain prevention
 * - State machine consistency
 * 
 * Constraints:
 * - Support Byzantine fault tolerance
 * - Handle network partitions gracefully
 * - Ensure linearizability
 *
 * Follow-up:
 * - How to optimize log compaction?
 * - Multi-Raft implementation?
 * 
 * Time Complexity: O(n) for consensus, O(log n) for log operations
 * Space Complexity: O(log_entries * nodes)
 * 
 * Company Tags: etcd, Consul, MongoDB
 */
public class DesignDistributedConsensus {

    enum NodeState {
        FOLLOWER, CANDIDATE, LEADER
    }

    enum MessageType {
        REQUEST_VOTE, REQUEST_VOTE_RESPONSE, APPEND_ENTRIES, APPEND_ENTRIES_RESPONSE
    }

    class LogEntry {
        long term;
        long index;
        String command;
        Object data;
        long timestamp;
        boolean committed;

        LogEntry(long term, long index, String command, Object data) {
            this.term = term;
            this.index = index;
            this.command = command;
            this.data = data;
            this.timestamp = System.currentTimeMillis();
            this.committed = false;
        }
    }

    class RaftMessage {
        MessageType type;
        String fromNodeId;
        String toNodeId;
        long term;
        Map<String, Object> payload;

        RaftMessage(MessageType type, String fromNodeId, String toNodeId, long term) {
            this.type = type;
            this.fromNodeId = fromNodeId;
            this.toNodeId = toNodeId;
            this.term = term;
            this.payload = new HashMap<>();
        }
    }

    class RaftNode {
        String nodeId;
        NodeState state;
        AtomicLong currentTerm;
        String votedFor;
        List<LogEntry> log;
        AtomicLong commitIndex;
        AtomicLong lastApplied;

        // Leader state
        Map<String, Long> nextIndex;
        Map<String, Long> matchIndex;

        // Election state
        Set<String> votesReceived;
        long lastHeartbeat;
        long electionTimeout;
        ScheduledExecutorService scheduler;

        // Network simulation
        Map<String, RaftNode> peers;
        BlockingQueue<RaftMessage> messageQueue;

        RaftNode(String nodeId, Set<String> peerIds) {
            this.nodeId = nodeId;
            this.state = NodeState.FOLLOWER;
            this.currentTerm = new AtomicLong(0);
            this.votedFor = null;
            this.log = new ArrayList<>();
            this.commitIndex = new AtomicLong(0);
            this.lastApplied = new AtomicLong(0);

            this.nextIndex = new ConcurrentHashMap<>();
            this.matchIndex = new ConcurrentHashMap<>();
            this.votesReceived = ConcurrentHashMap.newKeySet();

            this.lastHeartbeat = System.currentTimeMillis();
            this.electionTimeout = 5000 + new Random().nextInt(5000); // 5-10s
            this.scheduler = Executors.newScheduledThreadPool(3);
            this.messageQueue = new LinkedBlockingQueue<>();

            // Initialize peer indices
            for (String peerId : peerIds) {
                if (!peerId.equals(nodeId)) {
                    nextIndex.put(peerId, 1L);
                    matchIndex.put(peerId, 0L);
                }
            }

            startElectionTimer();
            startMessageProcessor();
        }

        void startElectionTimer() {
            scheduler.scheduleWithFixedDelay(() -> {
                if (state != NodeState.LEADER &&
                        System.currentTimeMillis() - lastHeartbeat > electionTimeout) {
                    startElection();
                }
            }, electionTimeout, 1000, TimeUnit.MILLISECONDS);
        }

        void startMessageProcessor() {
            scheduler.submit(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        RaftMessage message = messageQueue.take();
                        processMessage(message);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            });
        }

        void startElection() {
            state = NodeState.CANDIDATE;
            currentTerm.incrementAndGet();
            votedFor = nodeId;
            votesReceived.clear();
            votesReceived.add(nodeId);
            lastHeartbeat = System.currentTimeMillis();

            System.out.println("Node " + nodeId + " starting election for term " + currentTerm.get());

            // Send RequestVote to all peers
            for (String peerId : nextIndex.keySet()) {
                sendRequestVote(peerId);
            }

            // Check if won election
            if (votesReceived.size() > (nextIndex.size() + 1) / 2) {
                becomeLeader();
            }
        }

        void sendRequestVote(String peerId) {
            RaftMessage message = new RaftMessage(MessageType.REQUEST_VOTE, nodeId, peerId, currentTerm.get());

            long lastLogIndex = log.isEmpty() ? 0 : log.get(log.size() - 1).index;
            long lastLogTerm = log.isEmpty() ? 0 : log.get(log.size() - 1).term;

            message.payload.put("candidateId", nodeId);
            message.payload.put("lastLogIndex", lastLogIndex);
            message.payload.put("lastLogTerm", lastLogTerm);

            sendMessage(message);
        }

        void becomeLeader() {
            state = NodeState.LEADER;
            System.out.println("Node " + nodeId + " became leader for term " + currentTerm.get());

            // Initialize leader state
            long nextLogIndex = log.isEmpty() ? 1 : log.get(log.size() - 1).index + 1;
            for (String peerId : nextIndex.keySet()) {
                nextIndex.put(peerId, nextLogIndex);
                matchIndex.put(peerId, 0L);
            }

            // Start sending heartbeats
            startHeartbeats();
        }

        void startHeartbeats() {
            scheduler.scheduleWithFixedDelay(() -> {
                if (state == NodeState.LEADER) {
                    sendHeartbeats();
                }
            }, 0, 2000, TimeUnit.MILLISECONDS); // Every 2 seconds
        }

        void sendHeartbeats() {
            for (String peerId : nextIndex.keySet()) {
                sendAppendEntries(peerId, true);
            }
        }

        void sendAppendEntries(String peerId, boolean isHeartbeat) {
            RaftMessage message = new RaftMessage(MessageType.APPEND_ENTRIES, nodeId, peerId, currentTerm.get());

            long prevLogIndex = nextIndex.get(peerId) - 1;
            long prevLogTerm = 0;

            if (prevLogIndex > 0 && prevLogIndex <= log.size()) {
                prevLogTerm = log.get((int) prevLogIndex - 1).term;
            }

            message.payload.put("leaderId", nodeId);
            message.payload.put("prevLogIndex", prevLogIndex);
            message.payload.put("prevLogTerm", prevLogTerm);
            message.payload.put("leaderCommit", commitIndex.get());

            if (!isHeartbeat) {
                // Include log entries
                List<LogEntry> entries = new ArrayList<>();
                long startIndex = nextIndex.get(peerId);

                for (int i = (int) startIndex - 1; i < log.size(); i++) {
                    entries.add(log.get(i));
                }

                message.payload.put("entries", entries);
            } else {
                message.payload.put("entries", new ArrayList<LogEntry>());
            }

            sendMessage(message);
        }

        void processMessage(RaftMessage message) {
            // Update term if message has higher term
            if (message.term > currentTerm.get()) {
                currentTerm.set(message.term);
                votedFor = null;
                state = NodeState.FOLLOWER;
            }

            switch (message.type) {
                case REQUEST_VOTE:
                    handleRequestVote(message);
                    break;
                case REQUEST_VOTE_RESPONSE:
                    handleRequestVoteResponse(message);
                    break;
                case APPEND_ENTRIES:
                    handleAppendEntries(message);
                    break;
                case APPEND_ENTRIES_RESPONSE:
                    handleAppendEntriesResponse(message);
                    break;
            }
        }

        void handleRequestVote(RaftMessage message) {
            boolean voteGranted = false;

            if (message.term >= currentTerm.get() &&
                    (votedFor == null || votedFor.equals(message.payload.get("candidateId")))) {

                // Check log up-to-date condition
                long lastLogIndex = log.isEmpty() ? 0 : log.get(log.size() - 1).index;
                long lastLogTerm = log.isEmpty() ? 0 : log.get(log.size() - 1).term;

                long candidateLastLogIndex = (Long) message.payload.get("lastLogIndex");
                long candidateLastLogTerm = (Long) message.payload.get("lastLogTerm");

                if (candidateLastLogTerm > lastLogTerm ||
                        (candidateLastLogTerm == lastLogTerm && candidateLastLogIndex >= lastLogIndex)) {

                    voteGranted = true;
                    votedFor = (String) message.payload.get("candidateId");
                    lastHeartbeat = System.currentTimeMillis();
                }
            }

            // Send response
            RaftMessage response = new RaftMessage(MessageType.REQUEST_VOTE_RESPONSE,
                    nodeId, message.fromNodeId, currentTerm.get());
            response.payload.put("voteGranted", voteGranted);

            sendMessage(response);
        }

        void handleRequestVoteResponse(RaftMessage message) {
            if (state != NodeState.CANDIDATE || message.term < currentTerm.get()) {
                return;
            }

            if ((Boolean) message.payload.get("voteGranted")) {
                votesReceived.add(message.fromNodeId);

                // Check if won election
                if (votesReceived.size() > (nextIndex.size() + 1) / 2) {
                    becomeLeader();
                }
            }
        }

        @SuppressWarnings("unchecked")
        void handleAppendEntries(RaftMessage message) {
            boolean success = false;
            lastHeartbeat = System.currentTimeMillis();

            if (message.term >= currentTerm.get()) {
                state = NodeState.FOLLOWER;

                long prevLogIndex = (Long) message.payload.get("prevLogIndex");
                long prevLogTerm = (Long) message.payload.get("prevLogTerm");

                // Check log consistency
                if (prevLogIndex == 0 ||
                        (prevLogIndex <= log.size() &&
                                log.get((int) prevLogIndex - 1).term == prevLogTerm)) {

                    success = true;

                    // Append entries
                    List<LogEntry> entries = (List<LogEntry>) message.payload.get("entries");
                    if (!entries.isEmpty()) {
                        // Remove conflicting entries
                        while (log.size() > prevLogIndex) {
                            log.remove(log.size() - 1);
                        }

                        // Append new entries
                        log.addAll(entries);
                    }

                    // Update commit index
                    long leaderCommit = (Long) message.payload.get("leaderCommit");
                    if (leaderCommit > commitIndex.get()) {
                        commitIndex.set(Math.min(leaderCommit, log.size()));
                    }
                }
            }

            // Send response
            RaftMessage response = new RaftMessage(MessageType.APPEND_ENTRIES_RESPONSE,
                    nodeId, message.fromNodeId, currentTerm.get());
            response.payload.put("success", success);
            response.payload.put("matchIndex", log.size());

            sendMessage(response);
        }

        void handleAppendEntriesResponse(RaftMessage message) {
            if (state != NodeState.LEADER || message.term < currentTerm.get()) {
                return;
            }

            String peerId = message.fromNodeId;
            boolean success = (Boolean) message.payload.get("success");

            if (success) {
                long matchIndexValue = (Long) message.payload.get("matchIndex");
                matchIndex.put(peerId, matchIndexValue);
                nextIndex.put(peerId, matchIndexValue + 1);

                // Update commit index
                updateCommitIndex();
            } else {
                // Decrement nextIndex and retry
                nextIndex.put(peerId, Math.max(1, nextIndex.get(peerId) - 1));
                sendAppendEntries(peerId, false);
            }
        }

        void updateCommitIndex() {
            for (long n = commitIndex.get() + 1; n <= log.size(); n++) {
                if (log.get((int) n - 1).term == currentTerm.get()) {
                    int replicationCount = 1; // Count self

                    for (long matchIndexValue : matchIndex.values()) {
                        if (matchIndexValue >= n) {
                            replicationCount++;
                        }
                    }

                    if (replicationCount > (matchIndex.size() + 1) / 2) {
                        commitIndex.set(n);
                        log.get((int) n - 1).committed = true;
                    }
                }
            }
        }

        boolean appendEntry(String command, Object data) {
            if (state != NodeState.LEADER) {
                return false;
            }

            long newIndex = log.isEmpty() ? 1 : log.get(log.size() - 1).index + 1;
            LogEntry entry = new LogEntry(currentTerm.get(), newIndex, command, data);
            log.add(entry);

            // Replicate to followers
            for (String peerId : nextIndex.keySet()) {
                sendAppendEntries(peerId, false);
            }

            return true;
        }

        void sendMessage(RaftMessage message) {
            // Simulate network by delivering to peer's message queue
            RaftNode peer = peers.get(message.toNodeId);
            if (peer != null) {
                try {
                    peer.messageQueue.put(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        void setPeers(Map<String, RaftNode> peers) {
            this.peers = peers;
        }

        void shutdown() {
            scheduler.shutdown();
        }

        Map<String, Object> getNodeStats() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nodeId", nodeId);
            stats.put("state", state);
            stats.put("currentTerm", currentTerm.get());
            stats.put("votedFor", votedFor);
            stats.put("logSize", log.size());
            stats.put("commitIndex", commitIndex.get());
            stats.put("lastApplied", lastApplied.get());

            if (state == NodeState.LEADER) {
                stats.put("nextIndex", new HashMap<>(nextIndex));
                stats.put("matchIndex", new HashMap<>(matchIndex));
            }

            return stats;
        }
    }

    private Map<String, RaftNode> nodes;
    private AtomicInteger commandCounter;

    public DesignDistributedConsensus(List<String> nodeIds) {
        nodes = new ConcurrentHashMap<>();
        commandCounter = new AtomicInteger(0);

        // Create nodes
        Set<String> peerIds = new HashSet<>(nodeIds);
        for (String nodeId : nodeIds) {
            RaftNode node = new RaftNode(nodeId, peerIds);
            nodes.put(nodeId, node);
        }

        // Set peer references
        for (RaftNode node : nodes.values()) {
            node.setPeers(nodes);
        }
    }

    public boolean submitCommand(String command, Object data) {
        // Find leader and submit command
        RaftNode leader = findLeader();
        if (leader != null) {
            return leader.appendEntry(command, data);
        }
        return false;
    }

    private RaftNode findLeader() {
        return nodes.values().stream()
                .filter(node -> node.state == NodeState.LEADER)
                .findFirst()
                .orElse(null);
    }

    public Map<String, Object> getClusterStats() {
        Map<String, Object> stats = new HashMap<>();

        long maxTerm = nodes.values().stream()
                .mapToLong(node -> node.currentTerm.get())
                .max()
                .orElse(0);

        List<String> leaders = nodes.values().stream()
                .filter(node -> node.state == NodeState.LEADER)
                .map(node -> node.nodeId)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        int totalLogEntries = nodes.values().stream()
                .mapToInt(node -> node.log.size())
                .sum();

        stats.put("totalNodes", nodes.size());
        stats.put("currentTerm", maxTerm);
        stats.put("leaders", leaders);
        stats.put("totalLogEntries", totalLogEntries);

        return stats;
    }

    public List<Map<String, Object>> getNodeStats() {
        return nodes.values().stream()
                .map(RaftNode::getNodeStats)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    public void shutdown() {
        for (RaftNode node : nodes.values()) {
            node.shutdown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<String> nodeIds = Arrays.asList("node1", "node2", "node3", "node4", "node5");
        DesignDistributedConsensus consensus = new DesignDistributedConsensus(nodeIds);

        System.out.println("Initial cluster stats: " + consensus.getClusterStats());

        // Wait for leader election
        Thread.sleep(10000);

        System.out.println("After leader election: " + consensus.getClusterStats());

        // Submit some commands
        for (int i = 0; i < 10; i++) {
            boolean success = consensus.submitCommand("SET", "key" + i + "=value" + i);
            System.out.println("Command " + i + " submitted: " + success);
            Thread.sleep(500);
        }

        // Wait for replication
        Thread.sleep(5000);

        System.out.println("Final cluster stats: " + consensus.getClusterStats());

        // Show individual node stats
        System.out.println("\nNode statistics:");
        for (Map<String, Object> nodeStats : consensus.getNodeStats()) {
            System.out.println(nodeStats);
        }

        consensus.shutdown();
    }
}
