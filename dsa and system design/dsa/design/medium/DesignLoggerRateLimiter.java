package design.medium;

import java.util.*;

/**
 * LeetCode 359: Logger Rate Limiter
 * https://leetcode.com/problems/logger-rate-limiter/
 *
 * Description:
 * Design a logger system that receives a stream of messages and prints each
 * message only if it hasn't been printed in the last 10 seconds.
 *
 * Constraints:
 * - 1 <= message.length <= 30
 * - At most 10^4 calls will be made to shouldPrintMessage.
 *
 * Follow-up:
 * - Can you optimize for memory usage?
 * - Can you generalize for different time windows?
 * 
 * Time Complexity: O(1) for shouldPrintMessage
 * Space Complexity: O(M) where M is number of unique messages
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class DesignLoggerRateLimiter {

    private final Map<String, Integer> messageToLastTimestamp;
    private final int rateLimitWindow;

    public DesignLoggerRateLimiter() {
        this.messageToLastTimestamp = new HashMap<>();
        this.rateLimitWindow = 10; // 10 seconds rate limit window
    }

    // Alternative constructor for custom rate limit window
    public DesignLoggerRateLimiter(int rateLimitSeconds) {
        this.messageToLastTimestamp = new HashMap<>();
        this.rateLimitWindow = rateLimitSeconds;
    }

    public boolean shouldPrintMessage(int timestamp, String message) {
        if (message == null || message.trim().isEmpty()) {
            return false; // Invalid message
        }

        Integer lastTimestamp = messageToLastTimestamp.get(message);

        // If message hasn't been seen before, or enough time has passed
        if (lastTimestamp == null || timestamp - lastTimestamp >= rateLimitWindow) {
            messageToLastTimestamp.put(message, timestamp);
            return true;
        }

        return false; // Still within rate limit window
    }

    // Memory optimization: Clean up old entries
    public void cleanup(int currentTimestamp) {
        Iterator<Map.Entry<String, Integer>> iterator = messageToLastTimestamp.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<String, Integer> entry = iterator.next();
            if (currentTimestamp - entry.getValue() >= rateLimitWindow) {
                iterator.remove();
            }
        }
    }

    // Get current cache size for monitoring
    public int getCacheSize() {
        return messageToLastTimestamp.size();
    }

    // Get all cached messages (for debugging)
    public Set<String> getCachedMessages() {
        return new HashSet<>(messageToLastTimestamp.keySet());
    }

    public static void main(String[] args) {
        DesignLoggerRateLimiter logger = new DesignLoggerRateLimiter();

        System.out.println("=== Basic Rate Limiting Tests ===");
        System.out.println(logger.shouldPrintMessage(1, "foo")); // true
        System.out.println(logger.shouldPrintMessage(2, "bar")); // true
        System.out.println(logger.shouldPrintMessage(3, "foo")); // false (within 10s)
        System.out.println(logger.shouldPrintMessage(8, "bar")); // false (within 10s)
        System.out.println(logger.shouldPrintMessage(10, "foo")); // false (exactly 10s, still blocked)
        System.out.println(logger.shouldPrintMessage(11, "foo")); // true (>= 10s passed)

        System.out.println("\n=== Edge Case Tests ===");
        // Edge Case: New message
        System.out.println(logger.shouldPrintMessage(12, "baz")); // true (new message)
        // Edge Case: Same timestamp
        System.out.println(logger.shouldPrintMessage(12, "baz")); // false (same timestamp)
        // Edge Case: Empty/null message
        System.out.println(logger.shouldPrintMessage(13, "")); // false (empty message)
        System.out.println(logger.shouldPrintMessage(14, null)); // false (null message)

        System.out.println("\n=== Memory Management Tests ===");
        System.out.println("Cache size before cleanup: " + logger.getCacheSize());
        logger.cleanup(25); // Clean up messages older than 25-10=15
        System.out.println("Cache size after cleanup: " + logger.getCacheSize());
        System.out.println("Cached messages: " + logger.getCachedMessages());

        System.out.println("\n=== Custom Rate Limit Tests ===");
        DesignLoggerRateLimiter customLogger = new DesignLoggerRateLimiter(5); // 5 second window
        System.out.println(customLogger.shouldPrintMessage(1, "test")); // true
        System.out.println(customLogger.shouldPrintMessage(3, "test")); // false (within 5s)
        System.out.println(customLogger.shouldPrintMessage(6, "test")); // true (>= 5s passed)

        System.out.println("\n=== High Volume Test ===");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            logger.shouldPrintMessage(i, "message" + (i % 100)); // 100 unique messages
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Processed 1000 calls in " + (endTime - startTime) + "ms");
        System.out.println("Final cache size: " + logger.getCacheSize());
    }
}
