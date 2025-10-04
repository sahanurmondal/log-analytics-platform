package greedy.hard;

import java.util.*;

/**
 * Minimum Number of Intervals to Cover a Range
 * 
 * LeetCode Problem: 1024. Video Stitching (similar concept)
 * URL: https://leetcode.com/problems/video-stitching/
 * 
 * Related: 45. Jump Game II, 55. Jump Game
 * URL: https://leetcode.com/problems/jump-game-ii/
 * 
 * Company Tags: Google, Microsoft, Amazon, Meta, Apple
 * Difficulty: Hard
 * 
 * Description:
 * Given a set of intervals and a target range [0, target], find the minimum
 * number of intervals needed to cover the entire target range.
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^5
 * - 0 <= target <= 10^5
 * 
 * Follow-ups:
 * 1. Can you optimize for very large inputs?
 * 2. Can you handle negative coordinates?
 * 3. Can you find all possible minimum coverings?
 * 4. Can you solve for 2D interval covering?
 * 5. Can you handle weighted intervals?
 */
public class MinimumNumberOfIntervalsToCoverRange {

    /**
     * Greedy approach - always pick interval that extends furthest
     * Time: O(n log n), Space: O(1)
     */
    public int minIntervalsToCover(int[][] intervals, int target) {
        if (target <= 0)
            return 0;
        if (intervals == null || intervals.length == 0)
            return -1;

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int count = 0;
        int currentEnd = 0;
        int i = 0;

        while (currentEnd < target) {
            int farthest = currentEnd;

            // Find the interval that starts at or before currentEnd
            // and extends the farthest
            while (i < intervals.length && intervals[i][0] <= currentEnd) {
                farthest = Math.max(farthest, intervals[i][1]);
                i++;
            }

            // If we can't extend further, impossible to cover
            if (farthest == currentEnd) {
                return -1;
            }

            count++;
            currentEnd = farthest;
        }

        return count;
    }

    /**
     * Alternative greedy approach with different sorting
     * Time: O(n log n), Space: O(1)
     */
    public int minIntervalsToCoverAlternative(int[][] intervals, int target) {
        if (target <= 0)
            return 0;
        if (intervals == null || intervals.length == 0)
            return -1;

        // Sort by start time, then by end time descending
        Arrays.sort(intervals, (a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(b[1], a[1]);
        });

        int count = 0;
        int currentPos = 0;
        int i = 0;

        while (currentPos < target && i < intervals.length) {
            // If current interval can't help us progress
            if (intervals[i][0] > currentPos) {
                return -1;
            }

            // Find the interval that can extend us the furthest
            int maxEnd = intervals[i][1];
            while (i < intervals.length && intervals[i][0] <= currentPos) {
                maxEnd = Math.max(maxEnd, intervals[i][1]);
                i++;
            }

            count++;
            currentPos = maxEnd;
        }

        return currentPos >= target ? count : -1;
    }

    /**
     * Dynamic Programming approach
     * Time: O(n * target), Space: O(target)
     */
    public int minIntervalsToCoverDP(int[][] intervals, int target) {
        if (target <= 0)
            return 0;
        if (intervals == null || intervals.length == 0)
            return -1;

        int[] dp = new int[target + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        // Sort intervals by start position
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        for (int i = 1; i <= target; i++) {
            for (int[] interval : intervals) {
                int start = interval[0];
                int end = interval[1];

                // This interval can help cover position i
                if (start < i && end >= i) {
                    // Check all positions this interval can cover from
                    for (int j = Math.max(0, start); j < i; j++) {
                        if (dp[j] != Integer.MAX_VALUE) {
                            dp[i] = Math.min(dp[i], dp[j] + 1);
                        }
                    }
                }
            }
        }

        return dp[target] == Integer.MAX_VALUE ? -1 : dp[target];
    }

    /**
     * Follow-up 1: Optimized for large inputs using segment tree concept
     * Time: O(n log n), Space: O(n)
     */
    public int minIntervalsToCoverOptimized(int[][] intervals, int target) {
        if (target <= 0)
            return 0;
        if (intervals == null || intervals.length == 0)
            return -1;

        // Create events for interval starts and ends
        List<int[]> events = new ArrayList<>();
        for (int[] interval : intervals) {
            events.add(new int[] { interval[0], 0, interval[1] }); // start event
            events.add(new int[] { interval[1], 1, interval[1] }); // end event
        }

        // Sort events
        events.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]); // Process starts before ends
        });

        PriorityQueue<Integer> activeIntervals = new PriorityQueue<>(Collections.reverseOrder());
        int count = 0;
        int covered = 0;
        int eventIndex = 0;

        while (covered < target) {
            // Process all events at current position
            while (eventIndex < events.size() && events.get(eventIndex)[0] <= covered) {
                int[] event = events.get(eventIndex);
                if (event[1] == 0) { // start event
                    activeIntervals.offer(event[2]);
                } else { // end event
                    activeIntervals.remove(event[2]);
                }
                eventIndex++;
            }

            if (activeIntervals.isEmpty()) {
                return -1;
            }

            // Pick the interval that extends furthest
            int furthest = activeIntervals.peek();
            count++;
            covered = furthest;

            // Remove intervals that don't extend beyond current coverage
            while (!activeIntervals.isEmpty() && activeIntervals.peek() <= covered) {
                activeIntervals.poll();
            }
        }

        return count;
    }

    /**
     * Follow-up 2: Handle negative coordinates
     * Time: O(n log n), Space: O(1)
     */
    public int minIntervalsToCoverWithNegative(int[][] intervals, int start, int end) {
        if (start >= end)
            return 0;
        if (intervals == null || intervals.length == 0)
            return -1;

        // Filter intervals that can contribute to covering [start, end]
        List<int[]> validIntervals = new ArrayList<>();
        for (int[] interval : intervals) {
            if (interval[1] > start && interval[0] < end) {
                validIntervals.add(new int[] {
                        Math.max(interval[0], start),
                        Math.min(interval[1], end)
                });
            }
        }

        if (validIntervals.isEmpty())
            return -1;

        // Apply standard algorithm on adjusted intervals
        validIntervals.sort((a, b) -> Integer.compare(a[0], b[0]));

        int count = 0;
        int currentEnd = start;
        int i = 0;

        while (currentEnd < end) {
            int farthest = currentEnd;

            while (i < validIntervals.size() && validIntervals.get(i)[0] <= currentEnd) {
                farthest = Math.max(farthest, validIntervals.get(i)[1]);
                i++;
            }

            if (farthest == currentEnd) {
                return -1;
            }

            count++;
            currentEnd = farthest;
        }

        return count;
    }

    /**
     * Follow-up 3: Find all minimum coverings
     * Time: O(n^k) where k is minimum number of intervals, Space: O(n^k)
     */
    public List<List<int[]>> findAllMinimumCoverings(int[][] intervals, int target) {
        List<List<int[]>> allCoverings = new ArrayList<>();
        int minCount = minIntervalsToCover(intervals, target);

        if (minCount == -1)
            return allCoverings;

        findAllCoveringsHelper(intervals, target, 0, 0, new ArrayList<>(),
                allCoverings, minCount);
        return allCoverings;
    }

    private void findAllCoveringsHelper(int[][] intervals, int target, int currentEnd,
            int index, List<int[]> current,
            List<List<int[]>> result, int targetCount) {
        if (currentEnd >= target) {
            if (current.size() == targetCount) {
                result.add(new ArrayList<>(current));
            }
            return;
        }

        if (current.size() >= targetCount)
            return; // Pruning

        for (int i = index; i < intervals.length; i++) {
            if (intervals[i][0] <= currentEnd && intervals[i][1] > currentEnd) {
                current.add(intervals[i]);
                findAllCoveringsHelper(intervals, target, intervals[i][1],
                        i + 1, current, result, targetCount);
                current.remove(current.size() - 1);
            }
        }
    }

    /**
     * Follow-up 4: Weighted intervals (minimum cost to cover)
     * Time: O(n^2), Space: O(n)
     */
    public int minCostToCover(int[][] intervals, int[] costs, int target) {
        if (target <= 0)
            return 0;
        if (intervals == null || intervals.length == 0)
            return -1;

        int n = intervals.length;
        int[] dp = new int[target + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int pos = 1; pos <= target; pos++) {
            for (int i = 0; i < n; i++) {
                int start = intervals[i][0];
                int end = intervals[i][1];

                if (start < pos && end >= pos) {
                    for (int prevPos = Math.max(0, start); prevPos < pos; prevPos++) {
                        if (dp[prevPos] != Integer.MAX_VALUE) {
                            dp[pos] = Math.min(dp[pos], dp[prevPos] + costs[i]);
                        }
                    }
                }
            }
        }

        return dp[target] == Integer.MAX_VALUE ? -1 : dp[target];
    }

    /**
     * Follow-up 5: Get the actual intervals used in minimum covering
     * Time: O(n log n), Space: O(n)
     */
    public List<int[]> getMinimumCovering(int[][] intervals, int target) {
        if (target <= 0)
            return new ArrayList<>();
        if (intervals == null || intervals.length == 0)
            return null;

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> result = new ArrayList<>();
        int currentEnd = 0;
        int i = 0;

        while (currentEnd < target) {
            int farthest = currentEnd;
            int[] bestInterval = null;

            while (i < intervals.length && intervals[i][0] <= currentEnd) {
                if (intervals[i][1] > farthest) {
                    farthest = intervals[i][1];
                    bestInterval = intervals[i];
                }
                i++;
            }

            if (bestInterval == null) {
                return null; // Impossible to cover
            }

            result.add(bestInterval);
            currentEnd = farthest;

            // Reset i to continue from intervals that can extend from new position
            while (i > 0 && intervals[i - 1][0] <= currentEnd) {
                i--;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        MinimumNumberOfIntervalsToCoverRange solution = new MinimumNumberOfIntervalsToCoverRange();

        System.out.println("=== Minimum Intervals to Cover Range Test ===");

        // Test Case 1: Basic examples
        int[][] intervals1 = { { 1, 4 }, { 2, 6 }, { 5, 8 } };
        System.out.println("Basic [1,4],[2,6],[5,8] cover 8: " +
                solution.minIntervalsToCover(intervals1, 8)); // 2

        int[][] intervals2 = { { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println("Sequential [1,2],[2,3],[3,4] cover 4: " +
                solution.minIntervalsToCover(intervals2, 4)); // 3

        // Test Case 2: Alternative approach
        System.out.println("Alternative approach: " +
                solution.minIntervalsToCoverAlternative(intervals1, 8)); // 2

        // Test Case 3: DP approach
        System.out.println("DP approach: " +
                solution.minIntervalsToCoverDP(intervals1, 8)); // 2

        // Test Case 4: Edge cases
        System.out.println("Empty intervals: " +
                solution.minIntervalsToCover(new int[][] {}, 5)); // -1
        System.out.println("Zero target: " +
                solution.minIntervalsToCover(intervals1, 0)); // 0
        System.out.println("Single interval covers all: " +
                solution.minIntervalsToCover(new int[][] { { 0, 10 } }, 10)); // 1

        // Test Case 5: Impossible case
        int[][] impossible = { { 1, 2 }, { 4, 5 } };
        System.out.println("Impossible [1,2],[4,5] cover 5: " +
                solution.minIntervalsToCover(impossible, 5)); // -1

        // Test Case 6: Negative coordinates
        int[][] negativeIntervals = { { -2, 1 }, { 0, 3 }, { 2, 5 } };
        System.out.println("Negative coords [-2,1],[0,3],[2,5] cover [-1,4]: " +
                solution.minIntervalsToCoverWithNegative(negativeIntervals, -1, 4)); // 2

        // Test Case 7: Get actual covering
        List<int[]> covering = solution.getMinimumCovering(intervals1, 8);
        System.out.println("Actual covering intervals:");
        if (covering != null) {
            for (int[] interval : covering) {
                System.out.println("  [" + interval[0] + ", " + interval[1] + "]");
            }
        }

        // Test Case 8: Weighted intervals
        int[] costs = { 1, 2, 3 };
        System.out.println("Weighted covering cost: " +
                solution.minCostToCover(intervals1, costs, 8)); // 3 (intervals 0 and 2)

        // Test Case 9: All minimum coverings
        int[][] simple = { { 0, 2 }, { 1, 3 }, { 2, 4 } };
        List<List<int[]>> allCoverings = solution.findAllMinimumCoverings(simple, 4);
        System.out.println("All minimum coverings count: " + allCoverings.size());

        // Performance test
        System.out.println("\n=== Performance Test ===");
        int[][] largeIntervals = new int[10000][2];
        Random random = new Random(42);

        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(5000);
            int length = random.nextInt(100) + 1;
            largeIntervals[i] = new int[] { start, start + length };
        }

        long startTime = System.currentTimeMillis();
        int result = solution.minIntervalsToCover(largeIntervals, 5000);
        long endTime = System.currentTimeMillis();

        System.out.println("Large input (10K intervals) result: " + result +
                " in " + (endTime - startTime) + "ms");

        // Test optimized version
        startTime = System.currentTimeMillis();
        int optimizedResult = solution.minIntervalsToCoverOptimized(largeIntervals, 5000);
        endTime = System.currentTimeMillis();

        System.out.println("Optimized version result: " + optimizedResult +
                " in " + (endTime - startTime) + "ms");
    }
}
