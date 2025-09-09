package design.medium;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Design Advanced Rate Limiter
 *
 * Description: Design a rate limiter that supports:
 * - Multiple rate limiting algorithms
 * - Per-user and global rate limiting
 * - Different time windows
 * - Burst handling
 * 
 * Constraints:
 * - Support different algorithms: Token Bucket, Sliding Window, Fixed Window
 * - Handle high concurrency
 * - Configurable limits and windows
 *
 * Follow-up:
 * - Distributed rate limiting?
 * - Redis-based implementation?
 * 
 * Time Complexity: O(1) for most operations
 * Space Complexity: O(users * buckets)
 * 
 * Company Tags: Google, Amazon, Facebook
 */
public class DesignRateLimiter {

    enum Algorithm {
        TOKEN_BUCKET, SLIDING_WINDOW, FIXED_WINDOW
    }

    interface RateLimitStrategy {
        boolean allowRequest(String key);

        void reset(String key);

        Map<String, Object> getStats(String key);
    }

    // Token Bucket Implementation
    class TokenBucket implements RateLimitStrategy {
        private Map<String, BucketState> buckets;
        private int capacity;
        private int refillRate; // tokens per second

        class BucketState {
            int tokens;
            long lastRefill;

            BucketState(int capacity) {
                this.tokens = capacity;
                this.lastRefill = System.currentTimeMillis();
            }
        }

        public TokenBucket(int capacity, int refillRate) {
            this.capacity = capacity;
            this.refillRate = refillRate;
            this.buckets = new ConcurrentHashMap<>();
        }

        @Override
        public boolean allowRequest(String key) {
            BucketState bucket = buckets.computeIfAbsent(key, k -> new BucketState(capacity));

            synchronized (bucket) {
                refillBucket(bucket);

                if (bucket.tokens > 0) {
                    bucket.tokens--;
                    return true;
                }
                return false;
            }
        }

        private void refillBucket(BucketState bucket) {
            long now = System.currentTimeMillis();
            long timePassed = now - bucket.lastRefill;
            int tokensToAdd = (int) (timePassed * refillRate / 1000);

            if (tokensToAdd > 0) {
                bucket.tokens = Math.min(capacity, bucket.tokens + tokensToAdd);
                bucket.lastRefill = now;
            }
        }

        @Override
        public void reset(String key) {
            BucketState bucket = buckets.get(key);
            if (bucket != null) {
                synchronized (bucket) {
                    bucket.tokens = capacity;
                    bucket.lastRefill = System.currentTimeMillis();
                }
            }
        }

        @Override
        public Map<String, Object> getStats(String key) {
            BucketState bucket = buckets.get(key);
            Map<String, Object> stats = new HashMap<>();

            if (bucket != null) {
                synchronized (bucket) {
                    refillBucket(bucket);
                    stats.put("availableTokens", bucket.tokens);
                    stats.put("capacity", capacity);
                    stats.put("refillRate", refillRate);
                }
            }

            return stats;
        }
    }

    // Sliding Window Implementation
    class SlidingWindow implements RateLimitStrategy {
        private Map<String, WindowState> windows;
        private int limit;
        private long windowSizeMs;

        class WindowState {
            Queue<Long> requests;

            WindowState() {
                this.requests = new LinkedList<>();
            }
        }

        public SlidingWindow(int limit, long windowSizeMs) {
            this.limit = limit;
            this.windowSizeMs = windowSizeMs;
            this.windows = new ConcurrentHashMap<>();
        }

        @Override
        public boolean allowRequest(String key) {
            WindowState window = windows.computeIfAbsent(key, k -> new WindowState());

            synchronized (window) {
                long now = System.currentTimeMillis();

                // Remove old requests outside the window
                while (!window.requests.isEmpty() &&
                        now - window.requests.peek() >= windowSizeMs) {
                    window.requests.poll();
                }

                if (window.requests.size() < limit) {
                    window.requests.offer(now);
                    return true;
                }

                return false;
            }
        }

        @Override
        public void reset(String key) {
            WindowState window = windows.get(key);
            if (window != null) {
                synchronized (window) {
                    window.requests.clear();
                }
            }
        }

        @Override
        public Map<String, Object> getStats(String key) {
            WindowState window = windows.get(key);
            Map<String, Object> stats = new HashMap<>();

            if (window != null) {
                synchronized (window) {
                    long now = System.currentTimeMillis();

                    // Clean up old requests
                    while (!window.requests.isEmpty() &&
                            now - window.requests.peek() >= windowSizeMs) {
                        window.requests.poll();
                    }

                    stats.put("currentRequests", window.requests.size());
                    stats.put("limit", limit);
                    stats.put("windowSizeMs", windowSizeMs);
                    stats.put("remainingRequests", Math.max(0, limit - window.requests.size()));
                }
            }

            return stats;
        }
    }

    private Map<String, RateLimitStrategy> strategies;
    private RateLimitStrategy defaultStrategy;

    public DesignRateLimiter() {
        strategies = new HashMap<>();
        defaultStrategy = new TokenBucket(10, 1); // Default: 10 requests, 1 token/sec
    }

    public void addStrategy(String name, RateLimitStrategy strategy) {
        strategies.put(name, strategy);
    }

    public void setDefaultStrategy(RateLimitStrategy strategy) {
        this.defaultStrategy = strategy;
    }

    public boolean isAllowed(String key) {
        return defaultStrategy.allowRequest(key);
    }

    public boolean isAllowed(String key, String strategyName) {
        RateLimitStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            strategy = defaultStrategy;
        }
        return strategy.allowRequest(key);
    }

    public void resetLimit(String key) {
        defaultStrategy.reset(key);
    }

    public void resetLimit(String key, String strategyName) {
        RateLimitStrategy strategy = strategies.get(strategyName);
        if (strategy != null) {
            strategy.reset(key);
        }
    }

    public Map<String, Object> getStats(String key) {
        return defaultStrategy.getStats(key);
    }

    public Map<String, Object> getStats(String key, String strategyName) {
        RateLimitStrategy strategy = strategies.get(strategyName);
        if (strategy != null) {
            return strategy.getStats(key);
        }
        return new HashMap<>();
    }

    // Factory methods for common configurations
    public static DesignRateLimiter createTokenBucketLimiter(int capacity, int refillRate) {
        DesignRateLimiter limiter = new DesignRateLimiter();
        limiter.setDefaultStrategy(limiter.new TokenBucket(capacity, refillRate));
        return limiter;
    }

    public static DesignRateLimiter createSlidingWindowLimiter(int limit, long windowMs) {
        DesignRateLimiter limiter = new DesignRateLimiter();
        limiter.setDefaultStrategy(limiter.new SlidingWindow(limit, windowMs));
        return limiter;
    }

    public static void main(String[] args) throws InterruptedException {
        // Test Token Bucket
        System.out.println("Testing Token Bucket Rate Limiter:");
        DesignRateLimiter tokenBucketLimiter = createTokenBucketLimiter(3, 1);

        String user = "user123";
        for (int i = 0; i < 5; i++) {
            boolean allowed = tokenBucketLimiter.isAllowed(user);
            System.out.println("Request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "DENIED"));
            System.out.println("Stats: " + tokenBucketLimiter.getStats(user));
        }

        System.out.println("\nWaiting 2 seconds for token refill...");
        Thread.sleep(2000);

        boolean allowed = tokenBucketLimiter.isAllowed(user);
        System.out.println("After refill: " + (allowed ? "ALLOWED" : "DENIED"));
        System.out.println("Stats: " + tokenBucketLimiter.getStats(user));

        // Test Sliding Window
        System.out.println("\n\nTesting Sliding Window Rate Limiter:");
        DesignRateLimiter slidingWindowLimiter = createSlidingWindowLimiter(3, 5000); // 3 requests per 5 seconds

        for (int i = 0; i < 4; i++) {
            allowed = slidingWindowLimiter.isAllowed(user);
            System.out.println("Request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "DENIED"));
            System.out.println("Stats: " + slidingWindowLimiter.getStats(user));
            Thread.sleep(1000);
        }

        // Test multiple strategies
        System.out.println("\n\nTesting Multiple Strategies:");
        DesignRateLimiter multiLimiter = new DesignRateLimiter();
        multiLimiter.addStrategy("strict", multiLimiter.new TokenBucket(2, 1));
        multiLimiter.addStrategy("lenient", multiLimiter.new SlidingWindow(5, 10000));

        System.out.println("Strict strategy:");
        for (int i = 0; i < 3; i++) {
            allowed = multiLimiter.isAllowed(user, "strict");
            System.out.println("Request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "DENIED"));
        }

        System.out.println("\nLenient strategy:");
        for (int i = 0; i < 3; i++) {
            allowed = multiLimiter.isAllowed(user, "lenient");
            System.out.println("Request " + (i + 1) + ": " + (allowed ? "ALLOWED" : "DENIED"));
        }
    }
}
