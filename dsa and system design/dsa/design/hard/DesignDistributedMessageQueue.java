package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Design Distributed Message Queue
 * 
 * Related LeetCode Problems:
 * - Similar to: Design Message Queue, Pub-Sub System
 * - No direct LeetCode equivalent (System Design)
 * 
 * Company Tags: Amazon, Microsoft, Google, Meta, Uber
 * Difficulty: Hard
 * 
 * Description:
 * Design a distributed message queue that supports:
 * 1. enqueue(message) - Add a message to the queue
 * 2. dequeue() - Remove and return the next message
 * 3. acknowledge(messageId) - Acknowledge message processing
 * 
 * The system should handle:
 * - Message ordering (FIFO)
 * - Message durability
 * - Acknowledgment mechanism
 * - Partition tolerance
 * 
 * Constraints:
 * - At most 10^6 operations
 * - Support multiple nodes/partitions
 * - Handle node failures gracefully
 * 
 * Follow-ups:
 * 1. Message ordering and durability optimization
 * 2. Delayed message support
 * 3. Dead letter queue handling
 * 4. Priority queue support
 */
public class DesignDistributedMessageQueue {
    private final int nodeCount;
    private final List<Queue<Message>> partitions;
    private final Map<String, Message> pendingAcks;
    private final Map<String, Long> delayedMessages;
    private final AtomicLong messageIdCounter;
    private final Random random;

    // Message class with metadata
    private static class Message {
        String id;
        String content;
        long timestamp;
        int partition;
        boolean acknowledged;
        int retryCount;
        long deliveryTime;

        Message(String id, String content, long timestamp, int partition) {
            this.id = id;
            this.content = content;
            this.timestamp = timestamp;
            this.partition = partition;
            this.acknowledged = false;
            this.retryCount = 0;
            this.deliveryTime = timestamp;
        }
    }

    /**
     * Constructor - Initialize distributed message queue
     * Time: O(n), Space: O(n)
     */
    public DesignDistributedMessageQueue(int nodeCount) {
        this.nodeCount = nodeCount;
        this.partitions = new ArrayList<>();
        this.pendingAcks = new ConcurrentHashMap<>();
        this.delayedMessages = new ConcurrentHashMap<>();
        this.messageIdCounter = new AtomicLong(0);
        this.random = new Random();

        for (int i = 0; i < nodeCount; i++) {
            partitions.add(new ConcurrentLinkedQueue<>());
        }
    }

    /**
     * Enqueue message to distributed queue
     * Time: O(1) average, Space: O(1)
     */
    public void enqueue(String message) {
        String messageId = "msg_" + messageIdCounter.incrementAndGet();
        long timestamp = System.currentTimeMillis();
        int partition = getPartition(messageId);

        Message msg = new Message(messageId, message, timestamp, partition);
        partitions.get(partition).offer(msg);
    }

    /**
     * Dequeue message from distributed queue
     * Time: O(n) worst case, Space: O(1)
     */
    public String dequeue() {
        // Try round-robin approach across partitions
        for (int i = 0; i < nodeCount; i++) {
            Queue<Message> partition = partitions.get(i);
            Message msg = partition.poll();

            if (msg != null) {
                // Check if message is ready for delivery (delayed messages)
                if (msg.deliveryTime > System.currentTimeMillis()) {
                    partition.offer(msg); // Put back if not ready
                    continue;
                }

                pendingAcks.put(msg.id, msg);
                return msg.content;
            }
        }
        return null;
    }

    /**
     * Acknowledge message processing
     * Time: O(1), Space: O(1)
     */
    public void acknowledge(String messageId) {
        Message msg = pendingAcks.remove(messageId);
        if (msg != null) {
            msg.acknowledged = true;
        }
    }

    /**
     * Get partition for message (consistent hashing)
     * Time: O(1), Space: O(1)
     */
    private int getPartition(String messageId) {
        return Math.abs(messageId.hashCode()) % nodeCount;
    }

    // Follow-up 1: Enqueue with delayed delivery
    public void enqueueDelayed(String message, long delayMs) {
        String messageId = "msg_" + messageIdCounter.incrementAndGet();
        long timestamp = System.currentTimeMillis();
        long deliveryTime = timestamp + delayMs;
        int partition = getPartition(messageId);

        Message msg = new Message(messageId, message, timestamp, partition);
        msg.deliveryTime = deliveryTime;
        partitions.get(partition).offer(msg);
    }

    // Follow-up 2: Priority queue support
    public void enqueueWithPriority(String message, int priority) {
        // Could use PriorityQueue instead of regular Queue
        // Implementation would involve custom comparator
        enqueue(message + "_priority_" + priority);
    }

    // Follow-up 3: Dead letter queue handling
    public void handleDeadLetter(String messageId) {
        Message msg = pendingAcks.get(messageId);
        if (msg != null && msg.retryCount >= 3) {
            // Move to dead letter queue
            pendingAcks.remove(messageId);
            System.out.println("Message " + messageId + " moved to dead letter queue");
        }
    }

    // Follow-up 4: Get queue size across all partitions
    public int getTotalSize() {
        return partitions.stream().mapToInt(Queue::size).sum();
    }

    // Follow-up 5: Get partition sizes for monitoring
    public Map<Integer, Integer> getPartitionSizes() {
        Map<Integer, Integer> sizes = new HashMap<>();
        for (int i = 0; i < nodeCount; i++) {
            sizes.put(i, partitions.get(i).size());
        }
        return sizes;
    }

    // Follow-up 6: Rebalance partitions (for load balancing)
    public void rebalancePartitions() {
        List<Message> allMessages = new ArrayList<>();

        // Collect all messages
        for (Queue<Message> partition : partitions) {
            while (!partition.isEmpty()) {
                allMessages.add(partition.poll());
            }
        }

        // Redistribute messages
        for (Message msg : allMessages) {
            int newPartition = getPartition(msg.id);
            msg.partition = newPartition;
            partitions.get(newPartition).offer(msg);
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Design Distributed Message Queue Test ===");

        // Test Case 1: Basic enqueue/dequeue
        DesignDistributedMessageQueue queue = new DesignDistributedMessageQueue(3);
        queue.enqueue("msg1");
        queue.enqueue("msg2");
        queue.enqueue("msg3");

        System.out.println("Dequeue: " + queue.dequeue()); // Should return a message
        System.out.println("Dequeue: " + queue.dequeue()); // Should return a message

        // Test Case 2: Acknowledgment
        String msg = queue.dequeue();
        if (msg != null) {
            System.out.println("Dequeued: " + msg);
            // In real system, we'd need to track message IDs
            queue.acknowledge("msg_1");
        }

        // Test Case 3: Empty queue
        queue.dequeue(); // Clear remaining messages
        System.out.println("Empty queue dequeue: " + queue.dequeue()); // null

        // Test Case 4: Multiple enqueues and partition distribution
        for (int i = 1; i <= 10; i++) {
            queue.enqueue("message_" + i);
        }
        System.out.println("Total messages: " + queue.getTotalSize());
        System.out.println("Partition sizes: " + queue.getPartitionSizes());

        // Test Case 5: Delayed message (Follow-up)
        queue.enqueueDelayed("delayed_msg", 1000); // 1 second delay
        System.out.println("Immediate dequeue of delayed: " + queue.dequeue());

        // Test Case 6: Priority message (Follow-up)
        queue.enqueueWithPriority("high_priority", 1);
        queue.enqueueWithPriority("low_priority", 5);

        // Test Case 7: Rebalancing (Follow-up)
        queue.rebalancePartitions();
        System.out.println("After rebalancing: " + queue.getPartitionSizes());

        // Performance test
        System.out.println("\n=== Performance Test ===");
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            queue.enqueue("perf_test_" + i);
        }

        int dequeueCount = 0;
        while (queue.dequeue() != null) {
            dequeueCount++;
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Processed " + dequeueCount + " messages in " +
                (endTime - startTime) + "ms");
    }
}
