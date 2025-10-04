package design.hard;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Design Distributed Event Counter
 *
 * Description: Design a distributed system to count the number of events
 * in the last T seconds across multiple nodes with time-based sliding window.
 *
 * Constraints:
 * - 1 <= T <= 10^6 seconds
 * - At most 10^6 events per second
 * - Support multiple event types
 * - Handle node failures gracefully
 *
 * Follow-up:
 * - Can you optimize for concurrent access?
 * - Can you support multiple event types and replication?
 * - How to handle clock skew between nodes?
 * 
 * Time Complexity: O(1) for recordEvent, O(k) for getEventCount where k is
 * events in window
 * Space Complexity: O(events_in_window * replication_factor)
 * 
 * Company Tags: Google, Facebook, Amazon, Uber, Twitter
 */
public class DesignDistributedEventCounter {

    class EventNode {
        private final String nodeId;
        private final ConcurrentLinkedQueue<Long> events;
        private final AtomicLong totalEvents;
        private final Map<String, ConcurrentLinkedQueue<Long>> eventsByType;

        public EventNode(String nodeId) {
            this.nodeId = nodeId;
            this.events = new ConcurrentLinkedQueue<>();
            this.totalEvents = new AtomicLong(0);
            this.eventsByType = new ConcurrentHashMap<>();
        }

        public void recordEvent(long timestamp, String eventType) {
            events.offer(timestamp);
            totalEvents.incrementAndGet();

            if (!eventsByType.containsKey(eventType)) {
                eventsByType.put(eventType, new ConcurrentLinkedQueue<>());
            }
            eventsByType.get(eventType).offer(timestamp);
        }

        public int getEventCount(long currentTime, String eventType) {
            long windowStart = currentTime - windowSeconds;

            if (eventType == null) {
                return (int) events.stream()
                        .mapToLong(Long::longValue)
                        .filter(timestamp -> timestamp > windowStart && timestamp <= currentTime)
                        .count();
            } else {
                ConcurrentLinkedQueue<Long> typeEvents = eventsByType.get(eventType);
                if (typeEvents == null)
                    return 0;

                return (int) typeEvents.stream()
                        .mapToLong(Long::longValue)
                        .filter(timestamp -> timestamp > windowStart && timestamp <= currentTime)
                        .count();
            }
        }

        public void cleanup(long currentTime) {
            long cutoffTime = currentTime - windowSeconds - 60; // Keep extra buffer

            // Clean main events queue
            while (!events.isEmpty() && events.peek() < cutoffTime) {
                events.poll();
            }

            // Clean event type queues
            for (ConcurrentLinkedQueue<Long> typeQueue : eventsByType.values()) {
                while (!typeQueue.isEmpty() && typeQueue.peek() < cutoffTime) {
                    typeQueue.poll();
                }
            }
        }

        public String getNodeId() {
            return nodeId;
        }

        public long getTotalEvents() {
            return totalEvents.get();
        }
    }

    private final List<EventNode> nodes;
    private final Map<String, EventNode> nodeMap;
    private final int nodeCount;
    private final int windowSeconds;
    private final int replicationFactor;

    public DesignDistributedEventCounter(int nodeCount, int windowSeconds) {
        this.nodeCount = nodeCount;
        this.windowSeconds = windowSeconds;
        this.replicationFactor = Math.min(3, nodeCount); // Default replication
        this.nodes = new ArrayList<>();
        this.nodeMap = new ConcurrentHashMap<>();

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            String nodeId = "counter-node-" + i;
            EventNode node = new EventNode(nodeId);
            nodes.add(node);
            nodeMap.put(nodeId, node);
        }
    }

    public void recordEvent(long timestamp) {
        recordEvent(timestamp, "default");
    }

    public void recordEvent(long timestamp, String eventType) {
        if (eventType == null)
            eventType = "default";

        // Hash-based partitioning with replication
        int primaryNode = Math.abs((eventType + timestamp).hashCode()) % nodeCount;

        // Record on primary node
        nodes.get(primaryNode).recordEvent(timestamp, eventType);

        // Record on replica nodes
        for (int i = 1; i < replicationFactor; i++) {
            int replicaNode = (primaryNode + i) % nodeCount;
            nodes.get(replicaNode).recordEvent(timestamp, eventType);
        }
    }

    public int getEventCount(long timestamp) {
        return getEventCount(timestamp, null);
    }

    public int getEventCount(long timestamp, String eventType) {
        // Query all nodes and use the maximum count (accounting for replication)
        Map<String, Integer> nodeCounts = new HashMap<>();

        for (EventNode node : nodes) {
            int count = node.getEventCount(timestamp, eventType);
            nodeCounts.put(node.getNodeId(), count);
        }

        // For replicated data, we need to deduplicate
        // Simple approach: divide by replication factor and take max
        return nodeCounts.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0) / replicationFactor;
    }

    public void cleanup(long currentTime) {
        for (EventNode node : nodes) {
            node.cleanup(currentTime);
        }
    }

    public Map<String, Long> getNodeStats() {
        Map<String, Long> stats = new HashMap<>();
        for (EventNode node : nodes) {
            stats.put(node.getNodeId(), node.getTotalEvents());
        }
        return stats;
    }

    public void addNode(String nodeId) {
        if (!nodeMap.containsKey(nodeId)) {
            EventNode newNode = new EventNode(nodeId);
            nodes.add(newNode);
            nodeMap.put(nodeId, newNode);
        }
    }

    public boolean removeNode(String nodeId) {
        EventNode node = nodeMap.remove(nodeId);
        return node != null && nodes.remove(node);
    }

    public int getActiveNodes() {
        return nodes.size();
    }

    public static void main(String[] args) {
        System.out.println("=== Distributed Event Counter Test ===");

        DesignDistributedEventCounter counter = new DesignDistributedEventCounter(3, 10);

        // Test 1: Basic functionality
        long currentTime = System.currentTimeMillis() / 1000; // Convert to seconds

        counter.recordEvent(currentTime - 5);
        counter.recordEvent(currentTime - 3);
        counter.recordEvent(currentTime - 1);
        counter.recordEvent(currentTime);

        System.out.println("Events in last 10 seconds: " + counter.getEventCount(currentTime));

        // Test 2: Event types
        System.out.println("\n=== Event Types Test ===");
        counter.recordEvent(currentTime, "click");
        counter.recordEvent(currentTime, "view");
        counter.recordEvent(currentTime, "click");
        counter.recordEvent(currentTime, "purchase");

        System.out.println("Total events: " + counter.getEventCount(currentTime));
        System.out.println("Click events: " + counter.getEventCount(currentTime, "click"));
        System.out.println("View events: " + counter.getEventCount(currentTime, "view"));
        System.out.println("Purchase events: " + counter.getEventCount(currentTime, "purchase"));

        // Test 3: Time window
        System.out.println("\n=== Time Window Test ===");
        counter.recordEvent(currentTime - 15); // Outside window
        counter.recordEvent(currentTime - 8); // Inside window
        counter.recordEvent(currentTime - 2); // Inside window

        System.out.println("Events in window: " + counter.getEventCount(currentTime));

        // Test 4: Node statistics
        System.out.println("\n=== Node Statistics ===");
        Map<String, Long> stats = counter.getNodeStats();
        for (Map.Entry<String, Long> entry : stats.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " events");
        }

        // Test 5: Node management
        System.out.println("\n=== Node Management Test ===");
        System.out.println("Active nodes: " + counter.getActiveNodes());

        counter.addNode("backup-counter");
        System.out.println("After adding node: " + counter.getActiveNodes());

        counter.removeNode("counter-node-0");
        System.out.println("After removing node: " + counter.getActiveNodes());

        // Test 6: Cleanup
        System.out.println("\n=== Cleanup Test ===");
        counter.cleanup(currentTime);
        System.out.println("After cleanup - Events in window: " + counter.getEventCount(currentTime));

        // Edge Case: No events
        DesignDistributedEventCounter emptyCounter = new DesignDistributedEventCounter(2, 5);
        System.out.println("Empty counter events: " + emptyCounter.getEventCount(currentTime));
    }
}
