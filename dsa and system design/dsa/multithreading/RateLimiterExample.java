package multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;

/**
 * Q25: Implement a rate limiter for controlling concurrent access
 * Shows how to limit the rate of operations in concurrent systems
 */
public class RateLimiterExample {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Rate Limiter Examples ===");

        // Test token bucket rate limiter
        testTokenBucketRateLimiter();

        // Test sliding window rate limiter
        testSlidingWindowRateLimiter();
    }

    private static void testTokenBucketRateLimiter() throws InterruptedException {
        System.out.println("\n1. Token Bucket Rate Limiter:");

        // Create a rate limiter with 5 tokens per second
        TokenBucketRateLimiter rateLimiter = new TokenBucketRateLimiter(5, 1);

        // Create multiple threads that try to acquire permits
        Thread[] threads = new Thread[10];
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads.length; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 5; j++) {
                    boolean acquired = rateLimiter.tryAcquire();
                    System.out.println("Thread " + threadId + " attempt " + j +
                            ": " + (acquired ? "succeeded" : "rate limited"));

                    if (acquired) {
                        successCount.incrementAndGet();
                    }

                    try {
                        // Random delay between attempts
                        Thread.sleep((long) (Math.random() * 200));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            threads[i].start();
        }

        // Wait for all threads to finish
        for (Thread thread : threads) {
            thread.join();
        }

        System.out.println("Total successful acquisitions: " + successCount.get());
    }

    private static void testSlidingWindowRateLimiter() throws InterruptedException {
        System.out.println("\n2. Sliding Window Rate Limiter:");

        // Create a rate limiter that allows 10 operations per second
        SlidingWindowRateLimiter rateLimiter = new SlidingWindowRateLimiter(10, 1000);

        // Burst of requests
        System.out.println("Testing burst of requests:");
        for (int i = 0; i < 15; i++) {
            boolean allowed = rateLimiter.tryAcquire();
            System.out.println("Request " + i + ": " + (allowed ? "allowed" : "rate limited"));
        }

        // Wait half a second and try again
        Thread.sleep(500);
        System.out.println("\nAfter 500ms delay:");

        for (int i = 0; i < 5; i++) {
            boolean allowed = rateLimiter.tryAcquire();
            System.out.println("Request " + i + ": " + (allowed ? "allowed" : "rate limited"));
        }

        // Wait another second and try again
        Thread.sleep(1000);
        System.out.println("\nAfter another 1s delay (window should reset):");

        for (int i = 0; i < 12; i++) {
            boolean allowed = rateLimiter.tryAcquire();
            System.out.println("Request " + i + ": " + (allowed ? "allowed" : "rate limited"));
        }
    }

    /**
     * Token Bucket implementation of a rate limiter.
     * Generates tokens at a fixed rate and allows operations when tokens are
     * available.
     */
    static class TokenBucketRateLimiter {
        private final long ratePerSecond;
        private final long maxBucketSize;
        private final AtomicLong lastRefillTime;
        private final AtomicLong availableTokens;

        public TokenBucketRateLimiter(long ratePerSecond, long maxBucketSize) {
            this.ratePerSecond = ratePerSecond;
            this.maxBucketSize = maxBucketSize;
            this.lastRefillTime = new AtomicLong(System.nanoTime());
            this.availableTokens = new AtomicLong(maxBucketSize);
        }

        public synchronized boolean tryAcquire() {
            refill();

            if (availableTokens.get() > 0) {
                availableTokens.decrementAndGet();
                return true;
            }

            return false;
        }

        public synchronized boolean tryAcquire(int tokens) {
            refill();

            if (availableTokens.get() >= tokens) {
                availableTokens.addAndGet(-tokens);
                return true;
            }

            return false;
        }

        private void refill() {
            long now = System.nanoTime();
            long timeSinceLastRefill = now - lastRefillTime.get();

            // Convert to seconds
            double secondsSinceLastRefill = timeSinceLastRefill / 1_000_000_000.0;

            // Calculate tokens to add
            long tokensToAdd = (long) (secondsSinceLastRefill * ratePerSecond);

            if (tokensToAdd > 0) {
                long newTokens = Math.min(maxBucketSize,
                        availableTokens.get() + tokensToAdd);
                availableTokens.set(newTokens);
                lastRefillTime.set(now);
            }
        }
    }

    /**
     * Sliding Window implementation of a rate limiter.
     * Tracks requests within a rolling time window.
     */
    static class SlidingWindowRateLimiter {
        private final long maxRequestsPerWindow;
        private final long windowSizeInMillis;
        private final long[] requestTimestamps;
        private int currentIndex = 0;
        private int count = 0;

        public SlidingWindowRateLimiter(long maxRequestsPerWindow, long windowSizeInMillis) {
            this.maxRequestsPerWindow = maxRequestsPerWindow;
            this.windowSizeInMillis = windowSizeInMillis;
            this.requestTimestamps = new long[(int) maxRequestsPerWindow];
        }

        public synchronized boolean tryAcquire() {
            long currentTime = System.currentTimeMillis();

            // Remove expired timestamps from our count
            removeExpiredTimestamps(currentTime);

            // Check if we've reached our limit
            if (count >= maxRequestsPerWindow) {
                return false;
            }

            // Record this request
            requestTimestamps[currentIndex] = currentTime;
            currentIndex = (currentIndex + 1) % requestTimestamps.length;
            count++;

            return true;
        }

        private void removeExpiredTimestamps(long currentTime) {
            long cutoffTime = currentTime - windowSizeInMillis;

            // Count valid entries and zero out expired ones
            int validCount = 0;
            for (int i = 0; i < requestTimestamps.length; i++) {
                if (requestTimestamps[i] > 0) {
                    if (requestTimestamps[i] <= cutoffTime) {
                        // Expired timestamp
                        requestTimestamps[i] = 0;
                    } else {
                        // Valid timestamp
                        validCount++;
                    }
                }
            }

            count = validCount;
        }
    }
}
