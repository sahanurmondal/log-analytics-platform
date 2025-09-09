package greedy.hard;

import java.util.*;

/**
 * LeetCode 1353: Maximum Number of Events That Can Be Attended
 * URL: https://leetcode.com/problems/maximum-number-of-events-that-can-be-attended/
 * Difficulty: Hard
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 80+ interviews)
 *
 * Description:
 * Given an array of events where events[i] = [startDayi, endDayi], return the 
 * maximum number of events you can attend. You can only attend one event per day.
 * If you choose to attend an event on day d, you cannot attend any other event on that day.
 *
 * Example:
 * Input: events = [[1,2],[2,3],[3,4]]
 * Output: 3
 * Explanation: You can attend all three events.
 * One way is: day 1 -> event 0, day 2 -> event 1, day 3 -> event 2.
 *
 * Constraints:
 * - 1 <= events.length <= 10^5
 * - events[i].length == 2
 * - 1 <= events[i][0] <= events[i][1] <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you optimize for very large time ranges?
 * 2. What if events have different priorities/weights?
 * 3. How would you handle concurrent access?
 * 4. Can you solve with different data structures?
 */
public class MaximumNumberOfEventsThatCanBeAttended {
    
    // Approach 1: Greedy with Priority Queue - O(n log n + d log n) time, O(n) space
    public int maxEvents(int[][] events) {
        if (events == null || events.length == 0) {
            return 0;
        }
        
        // Sort events by start day
        Arrays.sort(events, (a, b) -> a[0] - b[0]);
        
        // Priority queue to store end days (min heap)
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        
        int eventIndex = 0;
        int attendedEvents = 0;
        int currentDay = 1;
        int maxDay = getMaxDay(events);
        
        while (currentDay <= maxDay) {
            // Add all events that start on current day
            while (eventIndex < events.length && events[eventIndex][0] == currentDay) {
                pq.offer(events[eventIndex][1]);
                eventIndex++;
            }
            
            // Remove expired events
            while (!pq.isEmpty() && pq.peek() < currentDay) {
                pq.poll();
            }
            
            // Attend one event if possible
            if (!pq.isEmpty()) {
                pq.poll();
                attendedEvents++;
            }
            
            currentDay++;
        }
        
        return attendedEvents;
    }
    
    private int getMaxDay(int[][] events) {
        int max = 0;
        for (int[] event : events) {
            max = Math.max(max, event[1]);
        }
        return max;
    }
    
    // Approach 2: Optimized Greedy - O(n log n) time, O(n) space
    public int maxEventsOptimized(int[][] events) {
        if (events == null || events.length == 0) {
            return 0;
        }
        
        Arrays.sort(events, (a, b) -> a[0] - b[0]);
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        
        int i = 0, attendedEvents = 0, day = 0;
        
        while (i < events.length || !pq.isEmpty()) {
            if (pq.isEmpty()) {
                day = events[i][0];
            }
            
            // Add all events starting on current day
            while (i < events.length && events[i][0] <= day) {
                pq.offer(events[i][1]);
                i++;
            }
            
            // Remove expired events
            while (!pq.isEmpty() && pq.peek() < day) {
                pq.poll();
            }
            
            // Attend one event
            if (!pq.isEmpty()) {
                pq.poll();
                attendedEvents++;
            }
            
            day++;
        }
        
        return attendedEvents;
    }
    
    // Approach 3: Union Find approach - O(n log n) time, O(d) space
    public int maxEventsUnionFind(int[][] events) {
        if (events == null || events.length == 0) {
            return 0;
        }
        
        Arrays.sort(events, (a, b) -> a[1] - b[1]); // Sort by end day
        
        int maxDay = getMaxDay(events);
        UnionFind uf = new UnionFind(maxDay + 2);
        int attendedEvents = 0;
        
        for (int[] event : events) {
            int availableDay = uf.find(event[0]);
            if (availableDay <= event[1]) {
                attendedEvents++;
                uf.union(availableDay, availableDay + 1);
            }
        }
        
        return attendedEvents;
    }
    
    private static class UnionFind {
        private int[] parent;
        
        public UnionFind(int n) {
            parent = new int[n];
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }
        
        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }
        
        public void union(int x, int y) {
            parent[find(x)] = find(y);
        }
    }
    
    // Approach 4: Dynamic Programming - O(n * d) time, O(d) space
    public int maxEventsDP(int[][] events) {
        if (events == null || events.length == 0) {
            return 0;
        }
        
        int maxDay = getMaxDay(events);
        boolean[] occupied = new boolean[maxDay + 1];
        
        // Sort by end day (greedy choice: attend events ending earlier first)
        Arrays.sort(events, (a, b) -> a[1] - b[1]);
        
        int attendedEvents = 0;
        
        for (int[] event : events) {
            // Try to attend on the earliest available day
            for (int day = event[0]; day <= event[1]; day++) {
                if (!occupied[day]) {
                    occupied[day] = true;
                    attendedEvents++;
                    break;
                }
            }
        }
        
        return attendedEvents;
    }

    public static void main(String[] args) {
        MaximumNumberOfEventsThatCanBeAttended solution = new MaximumNumberOfEventsThatCanBeAttended();
        
        // Test Case 1: Basic case
        System.out.println("=== Test Case 1: Basic Case ===");
        int[][] events1 = {{1, 2}, {2, 3}, {3, 4}};
        System.out.println("Expected: 3, Got: " + solution.maxEvents(events1)); // 3
        
        // Test Case 2: Overlapping events
        System.out.println("\n=== Test Case 2: Overlapping Events ===");
        int[][] events2 = {{1, 2}, {2, 3}, {3, 4}, {1, 2}};
        System.out.println("Expected: 4, Got: " + solution.maxEvents(events2)); // 4
        
        // Test Case 3: All events overlap completely
        System.out.println("\n=== Test Case 3: Complete Overlap ===");
        int[][] events3 = {{1, 10}, {2, 9}, {3, 8}};
        System.out.println("Expected: 3, Got: " + solution.maxEvents(events3)); // 3
        
        // Test Case 4: No overlap
        System.out.println("\n=== Test Case 4: No Overlap ===");
        int[][] events4 = {{1, 2}, {3, 4}, {5, 6}};
        System.out.println("Expected: 3, Got: " + solution.maxEvents(events4)); // 3
        
        // Test Case 5: Single event
        System.out.println("\n=== Test Case 5: Single Event ===");
        int[][] events5 = {{1, 1}};
        System.out.println("Expected: 1, Got: " + solution.maxEvents(events5)); // 1
        
        // Test Case 6: Complex overlapping
        System.out.println("\n=== Test Case 6: Complex Overlapping ===");
        int[][] events6 = {{1, 4}, {4, 4}, {2, 2}, {3, 4}, {1, 1}};
        System.out.println("Expected: 4, Got: " + solution.maxEvents(events6)); // 4
        
        // Test Case 7: Large range events
        System.out.println("\n=== Test Case 7: Large Range Events ===");
        int[][] events7 = {{1, 100}, {2, 3}, {4, 5}};
        System.out.println("Expected: 3, Got: " + solution.maxEvents(events7)); // 3
        
        // Test Case 8: Compare all approaches
        System.out.println("\n=== Test Case 8: Approach Comparison ===");
        compareApproaches(solution, events2);
        
        // Test Case 9: Performance test
        System.out.println("\n=== Test Case 9: Performance Test ===");
        performanceTest(solution);
        
        // Test Case 10: Edge case - empty array
        System.out.println("\n=== Test Case 10: Empty Array ===");
        int[][] emptyEvents = {};
        System.out.println("Expected: 0, Got: " + solution.maxEvents(emptyEvents)); // 0
        
        // Test Case 11: Single day events
        System.out.println("\n=== Test Case 11: Single Day Events ===");
        int[][] singleDayEvents = {{1, 1}, {1, 1}, {1, 1}};
        System.out.println("Expected: 1, Got: " + solution.maxEvents(singleDayEvents)); // 1
        
        // Test Case 12: Sequential events
        System.out.println("\n=== Test Case 12: Sequential Events ===");
        int[][] sequentialEvents = {{1, 1}, {2, 2}, {3, 3}, {4, 4}};
        System.out.println("Expected: 4, Got: " + solution.maxEvents(sequentialEvents)); // 4
        
        // Test Case 13: Validation test
        System.out.println("\n=== Test Case 13: Validation Test ===");
        validateAllApproaches(solution);
        
        // Test Case 14: Stress test
        System.out.println("\n=== Test Case 14: Stress Test ===");
        stressTest(solution);
        
        // Test Case 15: Maximum constraints
        System.out.println("\n=== Test Case 15: Large Input Test ===");
        largeInputTest(solution);
    }
    
    private static void compareApproaches(MaximumNumberOfEventsThatCanBeAttended solution, int[][] events) {
        int result1 = solution.maxEvents(events);
        int result2 = solution.maxEventsOptimized(events);
        int result3 = solution.maxEventsUnionFind(events);
        int result4 = solution.maxEventsDP(events);
        
        System.out.println("Greedy PQ: " + result1);
        System.out.println("Optimized: " + result2);
        System.out.println("Union Find: " + result3);
        System.out.println("DP: " + result4);
        System.out.println("All consistent: " + 
            (result1 == result2 && result2 == result3 && result3 == result4));
    }
    
    private static void performanceTest(MaximumNumberOfEventsThatCanBeAttended solution) {
        int[][] largeEvents = new int[1000][2];
        Random rand = new Random(42);
        
        for (int i = 0; i < 1000; i++) {
            int start = rand.nextInt(100) + 1;
            int end = start + rand.nextInt(10) + 1;
            largeEvents[i] = new int[]{start, end};
        }
        
        long start = System.currentTimeMillis();
        int result = solution.maxEvents(largeEvents);
        long end = System.currentTimeMillis();
        
        System.out.println("Performance test result: " + result + 
                          " events in " + (end - start) + "ms");
    }
    
    private static void validateAllApproaches(MaximumNumberOfEventsThatCanBeAttended solution) {
        int[][] testEvents = {{1, 3}, {1, 3}, {1, 3}, {3, 4}};
        
        int result1 = solution.maxEvents(testEvents);
        int result2 = solution.maxEventsOptimized(testEvents);
        int result3 = solution.maxEventsUnionFind(testEvents);
        int result4 = solution.maxEventsDP(testEvents);
        
        boolean allConsistent = result1 == result2 && result2 == result3 && result3 == result4;
        System.out.println("Validation result: " + result1 + ", All consistent: " + allConsistent);
    }
    
    private static void stressTest(MaximumNumberOfEventsThatCanBeAttended solution) {
        int[][] stressEvents = new int[100][2];
        for (int i = 0; i < 100; i++) {
            stressEvents[i] = new int[]{i % 10 + 1, (i % 10) + 5};
        }
        
        int result = solution.maxEvents(stressEvents);
        System.out.println("Stress test completed with result: " + result);
    }
    
    private static void largeInputTest(MaximumNumberOfEventsThatCanBeAttended solution) {
        int[][] largeInput = new int[5000][2];
        for (int i = 0; i < 5000; i++) {
            largeInput[i] = new int[]{i / 10 + 1, i / 10 + 10};
        }
        
        long start = System.currentTimeMillis();
        int result = solution.maxEvents(largeInput);
        long end = System.currentTimeMillis();
        
        System.out.println("Large input test: " + result + 
                          " events processed in " + (end - start) + "ms");
    }
}
