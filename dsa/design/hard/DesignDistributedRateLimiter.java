package design.hard;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Variation: Design Distributed Rate Limiter
 *
 * Description:
 * Design a distributed rate limiter that allows at most N requests per user per
 * time window.
 *
 * Constraints:
 * - User IDs are strings.
 * - Window is in seconds.
 * - At most 10^6 operations.
 *
 * Follow-up:
 * - Can you optimize for distributed systems?
 * - Can you support burst limits and replication?
 * 
 * Time Complexity: O(1) for allowRequest with sliding window
 * Space Complexity: O(users * windows) distributed across nodes
 * 
 * Company Tags: Google, Amazon, Netflix, Stripe, CloudFlare
 */
public class DesignDistributedRateLimiter {

    enum LimitStrategy {
        FIXED_WINDOW, SLIDING_WINDOW, TOKEN_BUCKET
    }

    class RateLimitNode {
        int nodeId;
        Map<String, UserRateLimit> userLimits;
        AtomicInteger requestCount;

        RateLimitNode(int nodeId) {
            this.nodeId = nodeId;
            this.userLimits = new ConcurrentHashMap<>();
            this.requestCount = new AtomicInteger(0);
        }
    }

    class UserRateLimit {
        String userId;
        Queue<Integer> requestTimestamps; // For sliding window
        int fixedWindowStart;
        int fixedWindowCount;
        int tokens; // For token bucket
        int lastTokenRefill;

        UserRateLimit(String userId) {
            this.userId = userId;
            this.requestTimestamps = new LinkedList<>();
            this.fixedWindowStart = -1;
            this.fixedWindowCount = 0;
            this.tokens = maxRequests;
            this.lastTokenRefill = 0;
        }
    }

    private final List<RateLimitNode> nodes;
    private final int maxRequests;
    private final int windowSeconds;
    private final LimitStrategy strategy;
    private final Map<String, Integer> userToNode; // Consistent hashing

    public DesignDistributedRateLimiter(int nodeCount, int maxRequests, int windowSeconds) {
        this(nodeCount, maxRequests, windowSeconds, LimitStrategy.SLIDING_WINDOW);
    }

    public DesignDistributedRateLimiter(int nodeCount, int maxRequests, int windowSeconds, LimitStrategy strategy) {
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
        this.strategy = strategy;
        this.nodes = new ArrayList<>();
        this.userToNode = new ConcurrentHashMap<>();

        // Initialize nodes
        for (int i = 0; i < nodeCount; i++) {
            nodes.add(new RateLimitNode(i));
        }
    }

    public boolean allowRequest(String userId, int timestamp) {
        if (userId == null || userId.trim().isEmpty()) {
            return false;
        }

        // Get the node responsible for this user (consistent hashing)
        int nodeIndex = getNodeForUser(userId);
        RateLimitNode node = nodes.get(nodeIndex);

        // Get or create user rate limit
        UserRateLimit userLimit = node.userLimits.computeIfAbsent(userId, UserRateLimit::new);

        node.requestCount.incrementAndGet();

        switch (strategy) {
            case SLIDING_WINDOW:
                return allowRequestSlidingWindow(userLimit, timestamp);
            case FIXED_WINDOW:
                return allowRequestFixedWindow(userLimit, timestamp);
            case TOKEN_BUCKET:
                return allowRequestTokenBucket(userLimit, timestamp);
            default:
                return allowRequestSlidingWindow(userLimit, timestamp);
        }
    }

    private boolean allowRequestSlidingWindow(UserRateLimit userLimit, int timestamp) {
        // Remove old requests outside the window
        while (!userLimit.requestTimestamps.isEmpty() &&
                timestamp - userLimit.requestTimestamps.peek() >= windowSeconds) {
            userLimit.requestTimestamps.poll();
        }

        // Check if we can allow the request
        if (userLimit.requestTimestamps.size() < maxRequests) {
            userLimit.requestTimestamps.offer(timestamp);
            return true;
        }

        return false;
    }

    private boolean allowRequestFixedWindow(UserRateLimit userLimit, int timestamp) {
        int currentWindow = timestamp / windowSeconds;

        if (userLimit.fixedWindowStart != currentWindow) {
            // New window
            userLimit.fixedWindowStart = currentWindow;
            userLimit.fixedWindowCount = 1;
            return true;
        }

        if (userLimit.fixedWindowCount < maxRequests) {
            userLimit.fixedWindowCount++;
            return true;
        }

        return false;
    }

    private boolean allowRequestTokenBucket(UserRateLimit userLimit, int timestamp) {
        // Refill tokens based on time passed
        if (userLimit.lastTokenRefill == 0) {
            userLimit.lastTokenRefill = timestamp;
        }

        int timePassed = timestamp - userLimit.lastTokenRefill;
        int tokensToAdd = (timePassed * maxRequests) / windowSeconds;

        if (tokensToAdd > 0) {
            userLimit.tokens = Math.min(maxRequests, userLimit.tokens + tokensToAdd);
            userLimit.lastTokenRefill = timestamp;
        }

        if (userLimit.tokens > 0) {
            userLimit.tokens--;
            return true;
        }

        return false;
    }

    private int getNodeForUser(String userId) {
        return userToNode.computeIfAbsent(userId,
                user -> Math.abs(user.hashCode()) % nodes.size());
    }

    // Monitoring and management methods

    public Map<String, Object> getRateLimiterStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("maxRequests", maxRequests);
        stats.put("windowSeconds", windowSeconds);
        stats.put("strategy", strategy.toString());
        stats.put("nodeCount", nodes.size());

        int totalUsers = 0;
        int totalRequests = 0;

        for (RateLimitNode node : nodes) {
            totalUsers += node.userLimits.size();
            totalRequests += node.requestCount.get();
        }

        stats.put("totalUsers", totalUsers);
        stats.put("totalRequests", totalRequests);

        return stats;
    }

    public List<Map<String, Object>> getNodeStats() {
        List<Map<String, Object>> nodeStats = new ArrayList<>();

        for (RateLimitNode node : nodes) {
            Map<String, Object> stats = new HashMap<>();
            stats.put("nodeId", node.nodeId);
            stats.put("userCount", node.userLimits.size());
            stats.put("requestCount", node.requestCount.get());

            nodeStats.add(stats);
        }

        return nodeStats;
    }

    public Map<String, Object> getUserStats(String userId) {
        int nodeIndex = getNodeForUser(userId);
        RateLimitNode node = nodes.get(nodeIndex);
        UserRateLimit userLimit = node.userLimits.get(userId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("userId", userId);
        stats.put("nodeId", nodeIndex);

        if (userLimit != null) {
            stats.put("currentRequests", userLimit.requestTimestamps.size());
            stats.put("remainingRequests", Math.max(0, maxRequests - userLimit.requestTimestamps.size()));
            stats.put("tokens", userLimit.tokens);
        } else {
            stats.put("currentRequests", 0);
            stats.put("remainingRequests", maxRequests);
            stats.put("tokens", maxRequests);
        }

        return stats;
    }

    // Cleanup method for memory management
    public void cleanup(int currentTimestamp) {
        for (RateLimitNode node : nodes) {
            Iterator<Map.Entry<String, UserRateLimit>> iterator = node.userLimits.entrySet().iterator();

            while (iterator.hasNext()) {
                Map.Entry<String, UserRateLimit> entry = iterator.next();
                UserRateLimit userLimit = entry.getValue();

                // Remove users with no recent activity
                if (strategy == LimitStrategy.SLIDING_WINDOW) {
                    while (!userLimit.requestTimestamps.isEmpty() &&
                            currentTimestamp - userLimit.requestTimestamps.peek() >= windowSeconds * 2) {
                        userLimit.requestTimestamps.poll();
                    }

                    if (userLimit.requestTimestamps.isEmpty()) {
                        iterator.remove();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Basic Rate Limiting Tests ===");
        DesignDistributedRateLimiter limiter = new DesignDistributedRateLimiter(3, 2, 10);

        System.out.println(limiter.allowRequest("user1", 1)); // true
        System.out.println(limiter.allowRequest("user1", 2)); // true
        System.out.println(limiter.allowRequest("user1", 3)); // false (exceeded limit)

        // Edge Case: New user
        System.out.println(limiter.allowRequest("user2", 4)); // true

        // Edge Case: Window reset
        System.out.println(limiter.allowRequest("user1", 12)); // true (window reset)

        System.out.println("\n=== Different Strategy Tests ===");

        // Fixed Window Strategy
        DesignDistributedRateLimiter fixedLimiter = new DesignDistributedRateLimiter(
                2, 3, 5, LimitStrategy.FIXED_WINDOW);

        System.out.println("Fixed Window Results:");
        for (int i = 1; i <= 8; i++) {
            boolean allowed = fixedLimiter.allowRequest("user1", i);
            System.out.println("Timestamp " + i + ": " + allowed);
        }

        // Token Bucket Strategy
        DesignDistributedRateLimiter tokenLimiter = new DesignDistributedRateLimiter(
                2, 2, 4, LimitStrategy.TOKEN_BUCKET);

        System.out.println("\nToken Bucket Results:");
        System.out.println("Initial: " + tokenLimiter.allowRequest("user1", 1)); // true
        System.out.println("Second: " + tokenLimiter.allowRequest("user1", 1)); // true
        System.out.println("Third: " + tokenLimiter.allowRequest("user1", 1)); // false
        System.out.println("After refill: " + tokenLimiter.allowRequest("user1", 5)); // true

        System.out.println("\n=== Load Test ===");
        long startTime = System.currentTimeMillis();
        int allowedCount = 0;

        for (int i = 0; i < 1000; i++) {
            String userId = "user" + (i % 50); // 50 different users
            if (limiter.allowRequest(userId, i)) {
                allowedCount++;
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("Processed 1000 requests in " + (endTime - startTime) + "ms");
        System.out.println("Allowed: " + allowedCount + ", Blocked: " + (1000 - allowedCount));

        System.out.println("\n=== Statistics ===");
        System.out.println("Rate Limiter Stats: " + limiter.getRateLimiterStats());
        System.out.println("Node Stats: " + limiter.getNodeStats());
        System.out.println("User1 Stats: " + limiter.getUserStats("user1"));

        System.out.println("\n=== Cleanup Test ===");
        limiter.cleanup(1000);
        System.out.println("Stats after cleanup: " + limiter.getRateLimiterStats());
    }
}
