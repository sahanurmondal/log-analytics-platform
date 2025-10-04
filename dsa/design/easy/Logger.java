package design.easy;

import java.util.*;

/**
 * LeetCode 359: Logger Rate Limiter
 * https://leetcode.com/problems/logger-rate-limiter/
 *
 * Description: Design a logger system that receives a stream of messages along
 * with their timestamps.
 * Each unique message should only be printed at most every 10 seconds.
 * 
 * Constraints:
 * - 0 <= timestamp <= 10^9
 * - Every timestamp will be passed in non-decreasing order (chronological
 * order)
 * - 1 <= message.length <= 30
 * - At most 10^4 calls will be made to shouldPrintMessage
 *
 * Follow-up:
 * - What if messages can arrive out of order?
 * 
 * Time Complexity: O(1) for shouldPrintMessage
 * Space Complexity: O(n) where n is number of unique messages
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class Logger {

    private Map<String, Integer> messageTimestamps;

    public Logger() {
        messageTimestamps = new HashMap<>();
    }

    public boolean shouldPrintMessage(int timestamp, String message) {
        if (!messageTimestamps.containsKey(message) ||
                timestamp - messageTimestamps.get(message) >= 10) {
            messageTimestamps.put(message, timestamp);
            return true;
        }
        return false;
    }

    // Alternative implementation with cleanup for memory optimization
    static class LoggerWithCleanup {
        private Map<String, Integer> messageTimestamps;
        private Queue<MessageEntry> messageQueue;

        class MessageEntry {
            String message;
            int timestamp;

            MessageEntry(String message, int timestamp) {
                this.message = message;
                this.timestamp = timestamp;
            }
        }

        public LoggerWithCleanup() {
            messageTimestamps = new HashMap<>();
            messageQueue = new LinkedList<>();
        }

        public boolean shouldPrintMessage(int timestamp, String message) {
            // Clean up old messages
            while (!messageQueue.isEmpty() &&
                    timestamp - messageQueue.peek().timestamp >= 10) {
                MessageEntry entry = messageQueue.poll();
                messageTimestamps.remove(entry.message);
            }

            if (!messageTimestamps.containsKey(message)) {
                messageTimestamps.put(message, timestamp);
                messageQueue.offer(new MessageEntry(message, timestamp));
                return true;
            }

            return false;
        }
    }

    /**
     * Logger that supports out-of-order timestamps.
     *
     * Approach:
     * - Maintain a map message -> lastPrintedTimestamp.
     * - Maintain a min-heap (priority queue) of (timestamp, message) for lazy
     * cleanup.
     * - On shouldPrintMessage(timestamp, message):
     * * First, cleanup heap entries with timestamp <= timestamp - 10. For each
     * popped
     * entry, if the map currently maps that message to the same timestamp, remove
     * it from the map.
     * * Then check last = map.get(message). If last == null || timestamp - last >=
     * 10, print
     * (i.e., update map and push to heap).
     *
     * Complexity:
     * - shouldPrintMessage: O(log n) amortized because of heap operations (cleanup
     * + push)
     * - Space: O(n)
     */
    static class LoggerOutOfOrder {
        private static class MessageEntry {
            final String message;
            final int timestamp;

            MessageEntry(String message, int timestamp) {
                this.message = message;
                this.timestamp = timestamp;
            }
        }

        private final Map<String, Integer> messageTimestamps;
        private final PriorityQueue<MessageEntry> minHeap;

        public LoggerOutOfOrder() {
            this.messageTimestamps = new HashMap<>();
            this.minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a.timestamp, b.timestamp));
        }

        public boolean shouldPrintMessage(int timestamp, String message) {
            // Cleanup entries that are older than timestamp - 10
            int threshold = timestamp - 10;
            while (!minHeap.isEmpty() && minHeap.peek().timestamp <= threshold) {
                MessageEntry entry = minHeap.poll();
                Integer last = messageTimestamps.get(entry.message);
                // Only remove from map if this heap entry reflects the current mapping
                if (last != null && last == entry.timestamp) {
                    messageTimestamps.remove(entry.message);
                }
            }

            Integer lastPrinted = messageTimestamps.get(message);
            if (lastPrinted == null || timestamp - lastPrinted >= 10) {
                messageTimestamps.put(message, timestamp);
                minHeap.offer(new MessageEntry(message, timestamp));
                return true;
            }

            return false;
        }
    }

    public static void main(String[] args) {
        // Logger logger = new Logger();
        // System.out.println(logger.shouldPrintMessage(1, "foo")); // Expected: true
        // System.out.println(logger.shouldPrintMessage(2, "bar")); // Expected: true
        // System.out.println(logger.shouldPrintMessage(3, "foo")); // Expected: false
        // System.out.println(logger.shouldPrintMessage(8, "bar")); // Expected: false
        // System.out.println(logger.shouldPrintMessage(10, "foo")); // Expected: false
        // System.out.println(logger.shouldPrintMessage(11, "foo")); // Expected: true

        // // Demonstrate LoggerWithCleanup (in-order timestamps)
        // System.out.println("\n-- LoggerWithCleanup (in-order) --");
        // LoggerWithCleanup lcu = new LoggerWithCleanup();
        // System.out.println(lcu.shouldPrintMessage(1, "ping")); // true
        // System.out.println(lcu.shouldPrintMessage(2, "pong")); // true
        // System.out.println(lcu.shouldPrintMessage(11, "ping")); // true (cleaned up)

        // Demonstrate LoggerOutOfOrder (out-of-order timestamps)
        System.out.println("\n-- LoggerOutOfOrder (out-of-order) --");
        LoggerOutOfOrder ldoo = new LoggerOutOfOrder();
        System.out.println(ldoo.shouldPrintMessage(10, "x")); // true
        // out-of-order older timestamp arrives after the newer one
        System.out.println(ldoo.shouldPrintMessage(1, "x")); // false (1 is within 10s of last=10)
        System.out.println(ldoo.shouldPrintMessage(21, "x")); // true (21 - 10 >= 10)
    }
}
