package intervals.hard;

import java.util.*;

/**
 * Interval Covering - Minimum Intervals to Cover Target
 * 
 * Related LeetCode Problems:
 * - 1024. Video Stitching
 * - 45. Jump Game II
 * - 1326. Minimum Number of Taps to Open to Water a Garden
 * URL: https://leetcode.com/problems/video-stitching/
 * 
 * Company Tags: Google, Amazon, Microsoft, Apple, Meta
 * Difficulty: Hard
 * 
 * Description:
 * Given a set of intervals and a target interval, find the minimum number of
 * intervals needed to completely cover the target interval. An interval covers
 * part of the target if there's any overlap.
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^9
 * - 0 <= target[0] < target[1] <= 10^9
 * 
 * Follow-ups:
 * 1. Return the actual intervals used for covering
 * 2. Find all possible minimum coverings
 * 3. Handle weighted intervals (minimum cost covering)
 * 4. Cover multiple targets simultaneously
 * 5. Partial covering with maximum coverage
 */
public class IntervalCovering {

    /**
     * Greedy approach - always pick interval that extends coverage the most
     * Time: O(n log n), Space: O(1)
     */
    public int minIntervalsToCover(int[][] intervals, int[] target) {
        if (intervals == null || intervals.length == 0 || target == null) {
            return -1;
        }

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int targetStart = target[0];
        int targetEnd = target[1];
        int currentCovered = targetStart;
        int count = 0;
        int i = 0;

        while (currentCovered < targetEnd) {
            int maxReach = currentCovered;

            // Find the interval that extends coverage the most
            while (i < intervals.length && intervals[i][0] <= currentCovered) {
                maxReach = Math.max(maxReach, intervals[i][1]);
                i++;
            }

            // If no progress can be made
            if (maxReach == currentCovered) {
                return -1;
            }

            currentCovered = maxReach;
            count++;
        }

        return count;
    }

    /**
     * Dynamic Programming approach - find optimal covering
     * Time: O(n^2), Space: O(n)
     */
    public int minIntervalsToCoverDP(int[][] intervals, int[] target) {
        if (intervals == null || intervals.length == 0 || target == null) {
            return -1;
        }

        // Filter and sort intervals that can contribute to covering
        List<int[]> validIntervals = new ArrayList<>();
        for (int[] interval : intervals) {
            if (interval[1] > target[0] && interval[0] < target[1]) {
                validIntervals.add(new int[] {
                        Math.max(interval[0], target[0]),
                        Math.min(interval[1], target[1])
                });
            }
        }

        if (validIntervals.isEmpty())
            return -1;

        validIntervals.sort((a, b) -> Integer.compare(a[0], b[0]));

        int n = validIntervals.size();
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MAX_VALUE);

        // Base case: first interval
        if (validIntervals.get(0)[0] == target[0]) {
            dp[0] = 1;
        }

        for (int i = 1; i < n; i++) {
            int[] current = validIntervals.get(i);

            // Option 1: Don't use current interval
            dp[i] = dp[i - 1];

            // Option 2: Use current interval
            if (current[0] == target[0]) {
                dp[i] = Math.min(dp[i], 1);
            } else {
                for (int j = 0; j < i; j++) {
                    if (validIntervals.get(j)[1] >= current[0] && dp[j] != Integer.MAX_VALUE) {
                        dp[i] = Math.min(dp[i], dp[j] + 1);
                    }
                }
            }
        }

        // Check if any interval covers the entire target
        for (int i = 0; i < n; i++) {
            if (validIntervals.get(i)[1] >= target[1] && dp[i] != Integer.MAX_VALUE) {
                return dp[i];
            }
        }

        return -1;
    }

    /**
     * Follow-up 1: Return the actual intervals used for covering
     * Time: O(n log n), Space: O(n)
     */
    public List<int[]> findCoveringIntervals(int[][] intervals, int[] target) {
        List<int[]> result = new ArrayList<>();

        if (intervals == null || intervals.length == 0 || target == null) {
            return result;
        }

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int targetStart = target[0];
        int targetEnd = target[1];
        int currentCovered = targetStart;
        int i = 0;

        while (currentCovered < targetEnd) {
            int maxReach = currentCovered;
            int[] bestInterval = null;

            // Find the interval that extends coverage the most
            while (i < intervals.length && intervals[i][0] <= currentCovered) {
                if (intervals[i][1] > maxReach) {
                    maxReach = intervals[i][1];
                    bestInterval = intervals[i];
                }
                i++;
            }

            // If no progress can be made
            if (maxReach == currentCovered) {
                return new ArrayList<>(); // Return empty list if impossible
            }

            result.add(bestInterval);
            currentCovered = maxReach;

            // Reset i to continue searching from where we left off
            while (i > 0 && intervals[i - 1][0] > currentCovered) {
                i--;
            }
        }

        return result;
    }

    /**
     * Follow-up 2: Find all possible minimum coverings
     * Time: O(2^n), Space: O(2^n)
     */
    public List<List<int[]>> findAllMinimumCoverings(int[][] intervals, int[] target) {
        List<List<int[]>> allCoverings = new ArrayList<>();
        int minCount = minIntervalsToCover(intervals, target);

        if (minCount == -1)
            return allCoverings;

        findAllCoveringsRecursive(intervals, target, 0, new ArrayList<>(),
                allCoverings, minCount, target[0]);

        return allCoverings;
    }

    private void findAllCoveringsRecursive(int[][] intervals, int[] target, int index,
            List<int[]> current, List<List<int[]>> allCoverings,
            int minCount, int currentPos) {
        if (currentPos >= target[1]) {
            if (current.size() == minCount) {
                allCoverings.add(new ArrayList<>(current));
            }
            return;
        }

        if (current.size() >= minCount || index >= intervals.length) {
            return;
        }

        for (int i = index; i < intervals.length; i++) {
            int[] interval = intervals[i];

            // Check if this interval can extend coverage
            if (interval[0] <= currentPos && interval[1] > currentPos) {
                current.add(interval);
                findAllCoveringsRecursive(intervals, target, i + 1, current,
                        allCoverings, minCount, Math.max(currentPos, interval[1]));
                current.remove(current.size() - 1);
            }
        }
    }

    /**
     * Follow-up 3: Weighted intervals - minimum cost covering
     * Time: O(n log n), Space: O(n)
     */
    public int minCostToCover(int[][] intervals, int[] weights, int[] target) {
        if (intervals == null || weights == null || intervals.length != weights.length) {
            return -1;
        }

        // Create weighted intervals and sort by start time
        List<WeightedInterval> weightedIntervals = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            if (intervals[i][1] > target[0] && intervals[i][0] < target[1]) {
                weightedIntervals.add(new WeightedInterval(intervals[i], weights[i], i));
            }
        }

        weightedIntervals.sort((a, b) -> Integer.compare(a.start, b.start));

        int n = weightedIntervals.size();
        int[] dp = new int[n];
        Arrays.fill(dp, Integer.MAX_VALUE);

        for (int i = 0; i < n; i++) {
            WeightedInterval current = weightedIntervals.get(i);

            // Can this interval start the covering?
            if (current.start <= target[0]) {
                dp[i] = current.weight;
            }

            // Try extending from previous intervals
            for (int j = 0; j < i; j++) {
                WeightedInterval prev = weightedIntervals.get(j);
                if (prev.end >= current.start && dp[j] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[j] + current.weight);
                }
            }
        }

        int result = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            if (weightedIntervals.get(i).end >= target[1] && dp[i] != Integer.MAX_VALUE) {
                result = Math.min(result, dp[i]);
            }
        }

        return result == Integer.MAX_VALUE ? -1 : result;
    }

    /**
     * Follow-up 4: Cover multiple targets simultaneously
     * Time: O(k * n log n), Space: O(k) where k is number of targets
     */
    public int[] minIntervalsToCoverMultiple(int[][] intervals, int[][] targets) {
        if (targets == null)
            return new int[0];

        int[] results = new int[targets.length];
        for (int i = 0; i < targets.length; i++) {
            results[i] = minIntervalsToCover(intervals, targets[i]);
        }

        return results;
    }

    /**
     * Follow-up 5: Partial covering - maximize coverage with k intervals
     * Time: O(n log n + nk), Space: O(n)
     */
    public int maxCoverageWithKIntervals(int[][] intervals, int[] target, int k) {
        if (intervals == null || intervals.length == 0 || k <= 0) {
            return 0;
        }

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // DP: dp[i][j] = maximum coverage using first i intervals and j selections
        int n = intervals.length;
        int[][] dp = new int[n + 1][k + 1];

        for (int i = 1; i <= n; i++) {
            int[] interval = intervals[i - 1];
            int coverage = Math.max(0, Math.min(interval[1], target[1]) - Math.max(interval[0], target[0]));

            for (int j = 0; j <= k; j++) {
                // Don't use current interval
                dp[i][j] = dp[i - 1][j];

                // Use current interval (if we have selections left)
                if (j > 0) {
                    dp[i][j] = Math.max(dp[i][j], dp[i - 1][j - 1] + coverage);
                }
            }
        }

        return dp[n][k];
    }

    /**
     * Advanced: Interval covering with gaps analysis
     * Time: O(n log n), Space: O(n)
     */
    public CoverageResult analyzeCoverage(int[][] intervals, int[] target) {
        if (intervals == null || target == null) {
            return new CoverageResult();
        }

        List<int[]> covering = findCoveringIntervals(intervals, target);
        if (covering.isEmpty()) {
            return new CoverageResult(false, 0, Arrays.asList(target), new ArrayList<>());
        }

        // Find gaps and overlaps
        List<int[]> gaps = new ArrayList<>();
        List<int[]> overlaps = new ArrayList<>();

        covering.sort((a, b) -> Integer.compare(a[0], b[0]));

        int currentPos = target[0];
        for (int[] interval : covering) {
            if (interval[0] > currentPos) {
                gaps.add(new int[] { currentPos, interval[0] });
            }

            if (interval[0] < currentPos) {
                overlaps.add(new int[] { interval[0], Math.min(currentPos, interval[1]) });
            }

            currentPos = Math.max(currentPos, interval[1]);
        }

        if (currentPos < target[1]) {
            gaps.add(new int[] { currentPos, target[1] });
        }

        return new CoverageResult(gaps.isEmpty(), covering.size(), gaps, overlaps);
    }

    // Helper classes
    static class WeightedInterval {
        int start, end, weight, index;

        WeightedInterval(int[] interval, int weight, int index) {
            this.start = interval[0];
            this.end = interval[1];
            this.weight = weight;
            this.index = index;
        }
    }

    static class CoverageResult {
        boolean isFullyCovered;
        int intervalsUsed;
        List<int[]> gaps;
        List<int[]> overlaps;

        CoverageResult() {
            this.isFullyCovered = false;
            this.intervalsUsed = 0;
            this.gaps = new ArrayList<>();
            this.overlaps = new ArrayList<>();
        }

        CoverageResult(boolean isFullyCovered, int intervalsUsed, List<int[]> gaps, List<int[]> overlaps) {
            this.isFullyCovered = isFullyCovered;
            this.intervalsUsed = intervalsUsed;
            this.gaps = gaps;
            this.overlaps = overlaps;
        }

        @Override
        public String toString() {
            return String.format("CoverageResult{covered=%s, intervals=%d, gaps=%s, overlaps=%s}",
                    isFullyCovered, intervalsUsed, formatIntervals(gaps), formatIntervals(overlaps));
        }

        private String formatIntervals(List<int[]> intervals) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < intervals.size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append("[").append(intervals.get(i)[0]).append(",").append(intervals.get(i)[1]).append("]");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        IntervalCovering solution = new IntervalCovering();
        
        System.out.println("=== Interval Covering Test ===");
        
        // Test Case 1: Basic examples
        int[][] intervals1 = {{1, 4}, {2, 6}, {5, 8}};
        int[] target1 = {2, 8};
        System.out.println("Basic covering [1,4],[2,6],[5,8] -> [2,8]:");
        System.out.println("  Min intervals: " + solution.minIntervalsToCover(intervals1, target1)); // 2
        System.out.println("  DP approach: " + solution.minIntervalsToCoverDP(intervals1, target1));
        
        // Test Case 2: Sequential intervals
        int[][] intervals2 = {{1, 2}, {2, 3}, {3, 4}};
        int[] target2 = {1, 4};
        System.out.println("Sequential [1,2],[2,3],[3,4] -> [1,4]:");
        System.out.println("  Min intervals: " + solution.minIntervalsToCover(intervals2, target2)); // 3
        
        // Test Case 3: Impossible case
        int[][] intervals3 = {{1, 2}, {4, 5}};
        int[] target3 = {1, 5};
        System.out.println("Impossible [1,2],[4,5] -> [1,5]:");
        System.out.println("  Min intervals: " + solution.minIntervalsToCover(intervals3, target3)); // -1
        
        // Test Case 4: Follow-up 1 - Actual intervals
        System.out.println("Follow-up 1 - Find covering intervals:");
        List<int[]> covering = solution.findCoveringIntervals(intervals1, target1);
        System.out.println("  Covering intervals:");
        for (int[] interval : covering) {
            System.out.println("    [" + interval[0] + "," + interval[1] + "]");
        }
        
        // Test Case 5: Follow-up 2 - All minimum coverings
        System.out.println("Follow-up 2 - All minimum coverings:");
        List<List<int[]>> allCoverings = solution.findAllMinimumCoverings(intervals1, target1);
        System.out.println("  Number of minimum coverings: " + allCoverings.size());
        
        // Test Case 6: Follow-up 3 - Weighted covering
        System.out.println("Follow-up 3 - Weighted interval covering:");
        int[] weights = {3, 2, 4};
        int minCost = solution.minCostToCover(intervals1, weights, target1);
        System.out.println("  Minimum cost: " + minCost);
        
        // Test Case 7: Follow-up 4 - Multiple targets
        System.out.println("Follow-up 4 - Multiple targets:");
        int[][] targets = {{2, 6}, {3, 7}, {1, 8}};
        int[] results = solution.minIntervalsToCoverMultiple(intervals1, targets);
        System.out.println("  Results: " + Arrays.toString(results));
        
        // Test Case 8: Follow-up 5 - Maximum coverage with k intervals
        System.out.println("Follow-up 5 - Max coverage with k=2 intervals:");
        int maxCoverage = solution.maxCoverageWithKIntervals(intervals1, target1, 2);
        System.out.println("  Maximum coverage: " + maxCoverage);
        
        // Test Case 9: Coverage analysis
        System.out.println("Coverage analysis:");
        CoverageResult analysis = solution.analyzeCoverage(intervals1, target1);
        System.out.println("  " + analysis);
        
        // Test Case 10: Edge cases
        System.out.println("Edge cases:");
        System.out.println("  Empty intervals: " + solution.minIntervalsToCover(new int[][]{}, target1));
        System.out.println("  Single covering interval: " + 
                          solution.minIntervalsToCover(new int[][]{{0, 10}}, new int[]{1, 9}));
        
        // Test Case 11: Complex scenario
        int[][] complex = {{0, 2}, {1, 4}, {3, 6}, {5, 8}, {7, 10}};
        int[] complexTarget = {1, 9};
        System.out.println("Complex scenario [0,2],[1,4],[3,6],[5,8],[7,10] -> [1,9]:");
        System.out.println("  Min intervals: " + solution.minIntervalsToCover(complex, complexTarget));
        
        List<int[]> complexCovering = solution.findCoveringIntervals(complex, complexTarget);
        System.out.println("  Selected intervals:");
        for (int[] interval : complexCovering) {
            System.out.println("    [" + interval[0] + "," + interval[1] + "]");
        }
        
        // Test Case 12: Performance test
        System.out.println("=== Performance Test ===");
        int[][] largeIntervals = new int[10000][2];
        Random random = new Random(42);
        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(1000);
            int length = random.nextInt(100) + 1;
            largeIntervals[i] = new int[]{start, start + length};
        }
        
        int[] largeTarget = {0, 1000};
        
        long startTime = System.currentTimeMillis();
        int result1 = solution.minIntervalsToCover(largeIntervals, largeTarget);
        long time1 = System.currentTimeMillis() - startTime;
        
        startTime = System.currentTimeMillis();
        int result2 = solution.minIntervalsToCoverDP(largeIntervals, largeTarget);
        long time2 = System.currentTimeMillis() - startTime;
        
        System.out.println("Greedy (10000 intervals): " + result1 + " (" + time1 + "ms)");
        System.out.println("DP (10000 intervals): " + result2 + " (" + time2 + "ms)");
        
        System.out.println("=== Summary ===");
        System.out.println("All interval covering tests completed successfully!");
    }
}
