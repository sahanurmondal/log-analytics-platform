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

    public static void main(String[] args) {
        Logger logger = new Logger();
        System.out.println(logger.shouldPrintMessage(1, "foo")); // Expected: true
        System.out.println(logger.shouldPrintMessage(2, "bar")); // Expected: true
        System.out.println(logger.shouldPrintMessage(3, "foo")); // Expected: false
        System.out.println(logger.shouldPrintMessage(8, "bar")); // Expected: false
        System.out.println(logger.shouldPrintMessage(10, "foo")); // Expected: false
        System.out.println(logger.shouldPrintMessage(11, "foo")); // Expected: true
    }
}
