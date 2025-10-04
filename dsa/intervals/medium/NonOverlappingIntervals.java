package intervals.medium;

import java.util.*;

/**
 * LeetCode 435: Non-overlapping Intervals
 * https://leetcode.com/problems/non-overlapping-intervals/
 * 
 * Companies: Amazon, Meta, Google, Microsoft, Apple, Adobe, Bloomberg
 * Frequency: Very High (Asked in 800+ interviews)
 *
 * Description:
 * Given an array of intervals where intervals[i] = [starti, endi],
 * return the minimum number of intervals you need to remove to make
 * the rest of the intervals non-overlapping.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - -5 * 10^4 <= starti < endi <= 5 * 10^4
 * 
 * Follow-up Questions:
 * 1. Can you return the actual intervals to remove?
 * 2. What if we want to maximize the number of intervals kept?
 * 3. How to handle weighted intervals?
 * 4. Can you solve in different time complexities?
 * 5. What about merging intervals instead of removing?
 */
public class NonOverlappingIntervals {

    // Approach 1: Greedy (sort by end time) - O(n log n) time, O(1) space
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        // Sort by end time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int count = 0;
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < lastEnd) {
                // Overlapping - remove current interval
                count++;
            } else {
                // Non-overlapping - update last end time
                lastEnd = intervals[i][1];
            }
        }

        return count;
    }

    // Approach 2: Greedy (sort by start time) - O(n log n) time, O(1) space
    public int eraseOverlapIntervalsStartTime(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int count = 0;
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < lastEnd) {
                // Overlapping - keep the one with earlier end time
                count++;
                lastEnd = Math.min(lastEnd, intervals[i][1]);
            } else {
                // Non-overlapping
                lastEnd = intervals[i][1];
            }
        }

        return count;
    }

    // Approach 3: Dynamic Programming - O(n^2) time, O(n) space
    public int eraseOverlapIntervalsDP(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int n = intervals.length;
        int[] dp = new int[n]; // dp[i] = max non-overlapping intervals ending at i
        Arrays.fill(dp, 1);

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < i; j++) {
                // If intervals[j] and intervals[i] don't overlap
                if (intervals[j][1] <= intervals[i][0]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }

        int maxNonOverlapping = Arrays.stream(dp).max().orElse(0);
        return n - maxNonOverlapping;
    }

    // Approach 4: Using TreeMap for efficient range queries - O(n log n) time, O(n)
    // space
    public int eraseOverlapIntervalsTreeMap(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        TreeMap<Integer, Integer> map = new TreeMap<>(); // start -> end

        for (int[] interval : intervals) {
            map.put(interval[0], interval[1]);
        }

        int count = 0;
        Integer lastEnd = null;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int start = entry.getKey();
            int end = entry.getValue();

            if (lastEnd != null && start < lastEnd) {
                // Overlapping
                count++;
                lastEnd = Math.min(lastEnd, end); // Keep the one with earlier end
            } else {
                lastEnd = end;
            }
        }

        return count;
    }

    // Follow-up 1: Return actual intervals to remove
    public List<int[]> getIntervalsToRemove(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return new ArrayList<>();
        }

        // Create array with original indices
        Integer[] indices = new Integer[intervals.length];
        for (int i = 0; i < intervals.length; i++) {
            indices[i] = i;
        }

        // Sort indices by end time of intervals
        Arrays.sort(indices, (a, b) -> Integer.compare(intervals[a][1], intervals[b][1]));

        List<int[]> toRemove = new ArrayList<>();
        int lastEnd = intervals[indices[0]][1];

        for (int i = 1; i < indices.length; i++) {
            int idx = indices[i];
            if (intervals[idx][0] < lastEnd) {
                // Overlapping - remove this interval
                toRemove.add(intervals[idx]);
            } else {
                // Non-overlapping
                lastEnd = intervals[idx][1];
            }
        }

        return toRemove;
    }

    // Follow-up 2: Maximize number of intervals kept
    public int maxNonOverlappingIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort by end time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int count = 1; // First interval is always selected
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= lastEnd) {
                // Non-overlapping
                count++;
                lastEnd = intervals[i][1];
            }
        }

        return count;
    }

    // Follow-up 3: Weighted intervals (Activity Selection with weights)
    public int eraseOverlapIntervalsWeighted(int[][] intervals, int[] weights) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        int n = intervals.length;

        // Create combined array with weights
        int[][] combined = new int[n][3];
        for (int i = 0; i < n; i++) {
            combined[i] = new int[] { intervals[i][0], intervals[i][1], weights[i] };
        }

        // Sort by start time
        Arrays.sort(combined, (a, b) -> Integer.compare(a[0], b[0]));

        // DP: dp[i] = maximum weight achievable up to interval i
        int[] dp = new int[n];
        dp[0] = combined[0][2];

        for (int i = 1; i < n; i++) {
            // Option 1: Don't include current interval
            dp[i] = dp[i - 1];

            // Option 2: Include current interval
            int includeWeight = combined[i][2];

            // Find the latest non-overlapping interval
            int j = i - 1;
            while (j >= 0 && combined[j][1] > combined[i][0]) {
                j--;
            }

            if (j >= 0) {
                includeWeight += dp[j];
            }

            dp[i] = Math.max(dp[i], includeWeight);
        }

        // Calculate total weight and subtract maximum achievable weight
        int totalWeight = Arrays.stream(weights).sum();
        return totalWeight - dp[n - 1];
    }

    // Follow-up 4: Different time complexity approach (using sweep line)
    public int eraseOverlapIntervalsSweepLine(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        List<int[]> events = new ArrayList<>();

        for (int[] interval : intervals) {
            events.add(new int[] { interval[0], 1 }); // Start event
            events.add(new int[] { interval[1], -1 }); // End event
        }

        // Sort events by time, with end events before start events at same time
        events.sort((a, b) -> {
            if (a[0] != b[0]) {
                return Integer.compare(a[0], b[0]);
            }
            return Integer.compare(a[1], b[1]); // End events (-1) come before start events (1)
        });

        int activeIntervals = 0;
        int maxConcurrent = 0;

        for (int[] event : events) {
            activeIntervals += event[1];
            maxConcurrent = Math.max(maxConcurrent, activeIntervals);
        }

        return intervals.length - (intervals.length - maxConcurrent + 1);
    }

    // Follow-up 5: Merge intervals instead of removing
    public int[][] mergeOverlappingIntervals(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        merged.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] last = merged.get(merged.size() - 1);
            int[] current = intervals[i];

            if (current[0] <= last[1]) {
                // Overlapping - merge
                last[1] = Math.max(last[1], current[1]);
            } else {
                // Non-overlapping - add new interval
                merged.add(current);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }

    // Advanced: Minimum intervals to cover a range
    public int minIntervalsToCover(int[][] intervals, int start, int end) {
        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int count = 0;
        int currentEnd = start;
        int i = 0;

        while (currentEnd < end && i < intervals.length) {
            if (intervals[i][0] > currentEnd) {
                return -1; // Gap that cannot be covered
            }

            // Find the interval that starts before or at currentEnd
            // and extends the furthest
            int maxReach = currentEnd;
            while (i < intervals.length && intervals[i][0] <= currentEnd) {
                maxReach = Math.max(maxReach, intervals[i][1]);
                i++;
            }

            if (maxReach <= currentEnd) {
                return -1; // Cannot make progress
            }

            currentEnd = maxReach;
            count++;
        }

        return currentEnd >= end ? count : -1;
    }

    // Advanced: Interval scheduling with deadlines
    public int maxIntervalsWithDeadlines(int[][] intervals, int[] deadlines) {
        int n = intervals.length;

        // Create combined array with deadlines
        int[][] combined = new int[n][3];
        for (int i = 0; i < n; i++) {
            combined[i] = new int[] { intervals[i][0], intervals[i][1], deadlines[i] };
        }

        // Sort by deadline
        Arrays.sort(combined, (a, b) -> Integer.compare(a[2], b[2]));

        List<int[]> selected = new ArrayList<>();

        for (int[] interval : combined) {
            // Check if this interval can be scheduled
            boolean canSchedule = true;

            for (int[] selectedInterval : selected) {
                if (hasOverlap(interval[0], interval[1], selectedInterval[0], selectedInterval[1])) {
                    canSchedule = false;
                    break;
                }
            }

            // Check if interval finishes before deadline
            if (canSchedule && interval[1] <= interval[2]) {
                selected.add(interval);
            }
        }

        return selected.size();
    }

    // Helper methods
    private boolean hasOverlap(int start1, int end1, int start2, int end2) {
        return Math.max(start1, start2) < Math.min(end1, end2);
    }

    // Helper: Validate intervals
    public boolean isValidInterval(int[] interval) {
        return interval != null && interval.length == 2 && interval[0] < interval[1];
    }

    // Helper: Calculate total coverage
    public int getTotalCoverage(int[][] intervals) {
        int[][] merged = mergeOverlappingIntervals(intervals);
        int total = 0;

        for (int[] interval : merged) {
            total += interval[1] - interval[0];
        }

        return total;
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(int[][] intervals) {
        Map<String, Long> results = new HashMap<>();

        // Test greedy (end time)
        long start = System.nanoTime();
        eraseOverlapIntervals(intervals.clone());
        results.put("GreedyEnd", System.nanoTime() - start);

        // Test greedy (start time)
        start = System.nanoTime();
        eraseOverlapIntervalsStartTime(intervals.clone());
        results.put("GreedyStart", System.nanoTime() - start);

        // Test DP (only for small inputs)
        if (intervals.length <= 100) {
            start = System.nanoTime();
            eraseOverlapIntervalsDP(intervals.clone());
            results.put("DP", System.nanoTime() - start);
        }

        // Test TreeMap
        start = System.nanoTime();
        eraseOverlapIntervalsTreeMap(intervals.clone());
        results.put("TreeMap", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        NonOverlappingIntervals solution = new NonOverlappingIntervals();

        // Test Case 1: Standard examples
        System.out.println("=== Test Case 1: Standard Examples ===");

        int[][][] testCases = {
                { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 1, 3 } }, // Expected: 1
                { { 1, 2 }, { 1, 2 }, { 1, 2 } }, // Expected: 2
                { { 1, 2 }, { 2, 3 } }, // Expected: 0
                { { 1, 100 }, { 11, 22 }, { 1, 11 }, { 2, 12 } } // Expected: 2
        };

        for (int i = 0; i < testCases.length; i++) {
            int result = solution.eraseOverlapIntervals(testCases[i]);
            System.out.println("Test case " + (i + 1) + ": " + result + " intervals to remove");
        }

        // Test Case 2: Compare all approaches
        System.out.println("\n=== Test Case 2: Compare All Approaches ===");
        int[][] compareIntervals = { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 1, 3 } };

        int greedyEnd = solution.eraseOverlapIntervals(compareIntervals.clone());
        int greedyStart = solution.eraseOverlapIntervalsStartTime(compareIntervals.clone());
        int dp = solution.eraseOverlapIntervalsDP(compareIntervals.clone());
        int treeMap = solution.eraseOverlapIntervalsTreeMap(compareIntervals.clone());

        System.out.println("Input intervals: " + Arrays.deepToString(compareIntervals));
        System.out.println("Greedy (end): " + greedyEnd);
        System.out.println("Greedy (start): " + greedyStart);
        System.out.println("DP: " + dp);
        System.out.println("TreeMap: " + treeMap);

        boolean allSame = greedyEnd == greedyStart && greedyStart == dp && dp == treeMap;
        System.out.println("All approaches consistent: " + allSame);

        // Follow-up 1: Return intervals to remove
        System.out.println("\n=== Follow-up 1: Intervals to Remove ===");
        int[][] removeExample = { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 1, 3 } };
        List<int[]> toRemove = solution.getIntervalsToRemove(removeExample);

        System.out.println("Original intervals: " + Arrays.deepToString(removeExample));
        System.out.print("Intervals to remove: ");
        for (int[] interval : toRemove) {
            System.out.print(Arrays.toString(interval) + " ");
        }
        System.out.println();

        // Follow-up 2: Maximize intervals kept
        System.out.println("\n=== Follow-up 2: Maximize Intervals Kept ===");
        int maxKept = solution.maxNonOverlappingIntervals(removeExample);
        System.out.println("Maximum non-overlapping intervals: " + maxKept);

        // Follow-up 3: Weighted intervals
        System.out.println("\n=== Follow-up 3: Weighted Intervals ===");
        int[][] weightedIntervals = { { 1, 3 }, { 2, 4 }, { 3, 5 } };
        int[] weights = { 10, 5, 8 };

        int weightToRemove = solution.eraseOverlapIntervalsWeighted(weightedIntervals, weights);
        System.out.println("Weight to remove: " + weightToRemove);

        // Follow-up 5: Merge intervals
        System.out.println("\n=== Follow-up 5: Merge Intervals ===");
        int[][] mergeExample = { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } };
        int[][] merged = solution.mergeOverlappingIntervals(mergeExample);

        System.out.println("Original: " + Arrays.deepToString(mergeExample));
        System.out.println("Merged: " + Arrays.deepToString(merged));

        // Advanced: Minimum intervals to cover
        System.out.println("\n=== Advanced: Minimum Intervals to Cover ===");
        int[][] coverIntervals = { { 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 } };
        int minToCover = solution.minIntervalsToCover(coverIntervals, 1, 6);
        System.out.println("Minimum intervals to cover [1, 6]: " + minToCover);

        // Advanced: Scheduling with deadlines
        System.out.println("\n=== Advanced: Scheduling with Deadlines ===");
        int[][] scheduleIntervals = { { 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 } };
        int[] deadlines = { 5, 6, 7, 8 };

        int maxScheduled = solution.maxIntervalsWithDeadlines(scheduleIntervals, deadlines);
        System.out.println("Maximum intervals with deadlines: " + maxScheduled);

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");

        // Generate larger test case
        int[][] performanceIntervals = new int[1000][2];
        Random random = new Random(42);

        for (int i = 0; i < 1000; i++) {
            int start = random.nextInt(1000);
            int end = start + random.nextInt(20) + 1;
            performanceIntervals[i] = new int[] { start, end };
        }

        Map<String, Long> performance = solution.comparePerformance(performanceIntervals);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // No intervals
        System.out.println("Empty array: " + solution.eraseOverlapIntervals(new int[][] {}));

        // Single interval
        System.out.println("Single interval: " +
                solution.eraseOverlapIntervals(new int[][] { { 1, 2 } }));

        // All identical intervals
        int[][] identical = { { 1, 2 }, { 1, 2 }, { 1, 2 } };
        System.out.println("All identical: " + solution.eraseOverlapIntervals(identical));

        // No overlaps
        int[][] noOverlap = { { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println("No overlaps: " + solution.eraseOverlapIntervals(noOverlap));

        // All overlapping
        int[][] allOverlap = { { 1, 5 }, { 2, 4 }, { 3, 6 } };
        System.out.println("All overlapping: " + solution.eraseOverlapIntervals(allOverlap));

        // Helper methods testing
        System.out.println("\n=== Helper Methods ===");

        int[][] testIntervals = { { 1, 3 }, { 2, 5 }, { 6, 8 } };
        System.out.println("Is [1, 3] valid: " + solution.isValidInterval(new int[] { 1, 3 }));
        System.out.println("Is [3, 1] valid: " + solution.isValidInterval(new int[] { 3, 1 }));
        System.out.println("Total coverage: " + solution.getTotalCoverage(testIntervals));

        System.out.println("\nNon-overlapping Intervals testing completed successfully!");
    }
}
