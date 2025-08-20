package design.medium;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * LeetCode 362: Design Hit Counter (Variation: Design Event Counter)
 * URL: https://leetcode.com/problems/design-hit-counter/
 * Difficulty: Medium
 * Companies: Google, Amazon, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 100+ interviews)
 *
 * Description:
 * Design a system to count the number of events in the last T seconds.
 * Support recording events and querying the count of events in the sliding
 * window.
 *
 * Example:
 * EventCounter counter = new EventCounter(300);
 * counter.recordEvent(1); // record event at timestamp 1
 * counter.recordEvent(2); // record event at timestamp 2
 * counter.getEventCount(3); // get count at timestamp 3, returns 2
 * counter.recordEvent(300); // record event at timestamp 300
 * counter.getEventCount(300); // returns 3
 * counter.getEventCount(301); // returns 2 (event at timestamp 1 is outside
 * window)
 *
 * Constraints:
 * - 1 <= T <= 10^6
 * - At most 10^5 events
 * - 1 <= timestamp <= 2 * 10^9
 * - All timestamps are in non-decreasing order
 * 
 * Follow-up Questions:
 * 1. Can you optimize for concurrent access?
 * 2. Can you support multiple event types?
 * 3. What if timestamps are not in order?
 * 4. How would you handle distributed counting?
 */
public class DesignEventCounter {

    // Approach 1: Using Queue - O(1) record, O(n) query time, O(n) space
    private Queue<Integer> events;
    private int windowSize;

    public DesignEventCounter(int windowSeconds) {
        this.events = new LinkedList<>();
        this.windowSize = windowSeconds;
    }

    public void recordEvent(int timestamp) {
        events.offer(timestamp);
    }

    public int getEventCount(int timestamp) {
        // Remove events outside the window
        while (!events.isEmpty() && events.peek() <= timestamp - windowSize) {
            events.poll();
        }
        return events.size();
    }

    // Approach 2: Using Bucketing - O(1) time, O(windowSize) space
    public static class EventCounterBucketing {
        private int[] buckets;
        private int[] times;
        private int windowSize;

        public EventCounterBucketing(int windowSeconds) {
            this.windowSize = windowSeconds;
            this.buckets = new int[windowSize];
            this.times = new int[windowSize];
        }

        public void recordEvent(int timestamp) {
            int idx = timestamp % windowSize;
            if (times[idx] != timestamp) {
                times[idx] = timestamp;
                buckets[idx] = 0;
            }
            buckets[idx]++;
        }

        public int getEventCount(int timestamp) {
            int total = 0;
            for (int i = 0; i < windowSize; i++) {
                if (timestamp - times[i] < windowSize) {
                    total += buckets[i];
                }
            }
            return total;
        }
    }

    // Approach 3: Thread-Safe Version - O(1) record, O(n) query, concurrent safe
    public static class ThreadSafeEventCounter {
        private final ConcurrentLinkedQueue<Integer> events;
        private final int windowSize;
        private final ReentrantReadWriteLock lock;

        public ThreadSafeEventCounter(int windowSeconds) {
            this.events = new ConcurrentLinkedQueue<>();
            this.windowSize = windowSeconds;
            this.lock = new ReentrantReadWriteLock();
        }

        public void recordEvent(int timestamp) {
            lock.writeLock().lock();
            try {
                events.offer(timestamp);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public int getEventCount(int timestamp) {
            lock.readLock().lock();
            try {
                // Clean up old events
                while (!events.isEmpty() && events.peek() <= timestamp - windowSize) {
                    events.poll();
                }
                return events.size();
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    // Follow-up: Multi-Type Event Counter
    public static class MultiTypeEventCounter {
        private Map<String, Queue<Integer>> eventQueues;
        private int windowSize;

        public MultiTypeEventCounter(int windowSeconds) {
            this.eventQueues = new HashMap<>();
            this.windowSize = windowSeconds;
        }

        public void recordEvent(String eventType, int timestamp) {
            if (!eventQueues.containsKey(eventType)) {
                eventQueues.put(eventType, new LinkedList<>());
            }
            eventQueues.get(eventType).offer(timestamp);
        }

        public int getEventCount(String eventType, int timestamp) {
            Queue<Integer> events = eventQueues.get(eventType);
            if (events == null)
                return 0;

            while (!events.isEmpty() && events.peek() <= timestamp - windowSize) {
                events.poll();
            }
            return events.size();
        }

        public int getTotalEventCount(int timestamp) {
            int total = 0;
            for (Queue<Integer> events : eventQueues.values()) {
                while (!events.isEmpty() && events.peek() <= timestamp - windowSize) {
                    events.poll();
                }
                total += events.size();
            }
            return total;
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");
        DesignEventCounter counter = new DesignEventCounter(10);
        counter.recordEvent(1);
        counter.recordEvent(2);
        counter.recordEvent(11);
        System.out.println("Events at timestamp 12: " + counter.getEventCount(12)); // Expected: 2

        // Test Case 2: Empty counter
        System.out.println("\n=== Test Case 2: Empty Counter ===");
        DesignEventCounter emptyCounter = new DesignEventCounter(5);
        System.out.println("Empty counter at timestamp 10: " + emptyCounter.getEventCount(10)); // Expected: 0

        // Test Case 3: Window sliding
        System.out.println("\n=== Test Case 3: Window Sliding ===");
        DesignEventCounter slidingCounter = new DesignEventCounter(300);
        slidingCounter.recordEvent(1);
        slidingCounter.recordEvent(2);
        slidingCounter.recordEvent(300);
        System.out.println("Count at 300: " + slidingCounter.getEventCount(300)); // Expected: 3
        System.out.println("Count at 301: " + slidingCounter.getEventCount(301)); // Expected: 2

        // Test Case 4: Bucketing approach
        System.out.println("\n=== Test Case 4: Bucketing Approach ===");
        EventCounterBucketing bucketCounter = new EventCounterBucketing(10);
        bucketCounter.recordEvent(1);
        bucketCounter.recordEvent(2);
        bucketCounter.recordEvent(11);
        System.out.println("Bucket counter at 12: " + bucketCounter.getEventCount(12)); // Expected: 2

        // Test Case 5: Thread-safe version
        System.out.println("\n=== Test Case 5: Thread-Safe Counter ===");
        ThreadSafeEventCounter safeCounter = new ThreadSafeEventCounter(10);
        safeCounter.recordEvent(1);
        safeCounter.recordEvent(5);
        System.out.println("Safe counter at 10: " + safeCounter.getEventCount(10)); // Expected: 2

        // Test Case 6: Multi-type events
        System.out.println("\n=== Test Case 6: Multi-Type Events ===");
        MultiTypeEventCounter multiCounter = new MultiTypeEventCounter(10);
        multiCounter.recordEvent("click", 1);
        multiCounter.recordEvent("view", 2);
        multiCounter.recordEvent("click", 5);
        System.out.println("Click events at 10: " + multiCounter.getEventCount("click", 10)); // Expected: 2
        System.out.println("View events at 10: " + multiCounter.getEventCount("view", 10)); // Expected: 1
        System.out.println("Total events at 10: " + multiCounter.getTotalEventCount(10)); // Expected: 3

        // Test Case 7: Edge case - same timestamp
        System.out.println("\n=== Test Case 7: Same Timestamp ===");
        DesignEventCounter sameTimeCounter = new DesignEventCounter(5);
        sameTimeCounter.recordEvent(10);
        sameTimeCounter.recordEvent(10);
        sameTimeCounter.recordEvent(10);
        System.out.println("Same timestamp events: " + sameTimeCounter.getEventCount(14)); // Expected: 3

        // Test Case 8: Large window
        System.out.println("\n=== Test Case 8: Large Window ===");
        DesignEventCounter largeWindow = new DesignEventCounter(1000000);
        largeWindow.recordEvent(1);
        largeWindow.recordEvent(500000);
        largeWindow.recordEvent(999999);
        System.out.println("Large window count: " + largeWindow.getEventCount(1000000)); // Expected: 3

        // Test Case 9: Performance test
        System.out.println("\n=== Test Case 9: Performance Test ===");
        performanceTest();

        // Test Case 10: Boundary conditions
        System.out.println("\n=== Test Case 10: Boundary Conditions ===");
        DesignEventCounter boundaryCounter = new DesignEventCounter(1);
        boundaryCounter.recordEvent(100);
        System.out.println("Boundary test at 100: " + boundaryCounter.getEventCount(100)); // Expected: 1
        System.out.println("Boundary test at 101: " + boundaryCounter.getEventCount(101)); // Expected: 0

        // Test Case 11: Multiple queries
        System.out.println("\n=== Test Case 11: Multiple Queries ===");
        DesignEventCounter queryCounter = new DesignEventCounter(5);
        queryCounter.recordEvent(1);
        queryCounter.recordEvent(3);
        queryCounter.recordEvent(5);
        System.out.println("Query at 5: " + queryCounter.getEventCount(5)); // Expected: 3
        System.out.println("Query at 6: " + queryCounter.getEventCount(6)); // Expected: 2
        System.out.println("Query at 8: " + queryCounter.getEventCount(8)); // Expected: 2
        System.out.println("Query at 10: " + queryCounter.getEventCount(10)); // Expected: 1

        // Test Case 12: Comparison test
        System.out.println("\n=== Test Case 12: Approach Comparison ===");
        compareApproaches();

        // Test Case 13: Stress test
        System.out.println("\n=== Test Case 13: Stress Test ===");
        stressTest();

        // Test Case 14: Zero window size
        System.out.println("\n=== Test Case 14: Minimum Window ===");
        DesignEventCounter minWindow = new DesignEventCounter(1);
        minWindow.recordEvent(10);
        minWindow.recordEvent(11);
        System.out.println("Min window at 11: " + minWindow.getEventCount(11)); // Expected: 1

        // Test Case 15: Validation test
        System.out.println("\n=== Test Case 15: Validation Test ===");
        validateAllApproaches();
    }

    private static void performanceTest() {
        DesignEventCounter counter = new DesignEventCounter(1000);
        long start = System.currentTimeMillis();

        // Record 10000 events
        for (int i = 1; i <= 10000; i++) {
            counter.recordEvent(i);
        }

        // Query 1000 times
        for (int i = 5000; i <= 6000; i++) {
            counter.getEventCount(i);
        }

        long end = System.currentTimeMillis();
        System.out.println("Performance test completed in: " + (end - start) + "ms");
    }

    private static void compareApproaches() {
        DesignEventCounter queueCounter = new DesignEventCounter(10);
        EventCounterBucketing bucketCounter = new EventCounterBucketing(10);

        // Add same events to both
        int[] events = { 1, 3, 5, 7, 9, 11, 13, 15 };
        for (int event : events) {
            queueCounter.recordEvent(event);
            bucketCounter.recordEvent(event);
        }

        // Compare results
        int queueResult = queueCounter.getEventCount(15);
        int bucketResult = bucketCounter.getEventCount(15);

        System.out.println("Queue approach result: " + queueResult);
        System.out.println("Bucket approach result: " + bucketResult);
        System.out.println("Results match: " + (queueResult == bucketResult));
    }

    private static void stressTest() {
        DesignEventCounter counter = new DesignEventCounter(100);
        Random rand = new Random(42); // Fixed seed for reproducibility

        // Record random events
        for (int i = 0; i < 1000; i++) {
            counter.recordEvent(rand.nextInt(1000) + 1);
        }

        // Query at various timestamps
        int queryCount = 0;
        for (int i = 500; i < 600; i++) {
            queryCount += counter.getEventCount(i);
        }

        System.out.println("Stress test completed, total query results: " + queryCount);
    }

    private static void validateAllApproaches() {
        // Validation with same input across all approaches
        int[] testEvents = { 1, 2, 3, 10, 11, 12, 20, 21, 22 };
        int windowSize = 5;
        int queryTime = 25;

        DesignEventCounter queueCounter = new DesignEventCounter(windowSize);
        EventCounterBucketing bucketCounter = new EventCounterBucketing(windowSize);
        ThreadSafeEventCounter safeCounter = new ThreadSafeEventCounter(windowSize);

        for (int event : testEvents) {
            queueCounter.recordEvent(event);
            bucketCounter.recordEvent(event);
            safeCounter.recordEvent(event);
        }

        int queueResult = queueCounter.getEventCount(queryTime);
        int bucketResult = bucketCounter.getEventCount(queryTime);
        int safeResult = safeCounter.getEventCount(queryTime);

        System.out.println("Queue result: " + queueResult);
        System.out.println("Bucket result: " + bucketResult);
        System.out.println("Thread-safe result: " + safeResult);
        System.out.println("All approaches consistent: " +
                (queueResult == bucketResult && bucketResult == safeResult));
    }
}
