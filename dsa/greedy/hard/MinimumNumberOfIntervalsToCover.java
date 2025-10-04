package greedy.hard;

/**
 * LeetCode 1024: Video Stitching (Similar Pattern)
 * Related Problem: Minimum Number of Intervals to Cover a Range
 * https://leetcode.com/problems/video-stitching/
 * 
 * Companies: Amazon, Google, Microsoft, Apple
 * Frequency: Medium (Asked in 35+ interviews)
 *
 * Description:
 * Given a set of intervals and a target range [0, target], find the minimum
 * number
 * of intervals needed to cover the entire range. Return -1 if it's impossible.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^5
 * - 0 <= target <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you optimize for large input?
 * 2. What if intervals can have negative coordinates?
 * 3. How to find actual intervals used?
 */
public class MinimumNumberOfIntervalsToCover {

    // Approach 1: Greedy with Sorting - O(n log n) time, O(1) space
    public int minIntervalsToCover(int[][] intervals, int target) {
        if (target == 0)
            return 0;

        // Sort intervals by start time
        java.util.Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        int count = 0;
        int currentEnd = 0;
        int i = 0;

        while (currentEnd < target) {
            int farthest = currentEnd;

            // Find the interval that starts before or at currentEnd and extends farthest
            while (i < intervals.length && intervals[i][0] <= currentEnd) {
                farthest = Math.max(farthest, intervals[i][1]);
                i++;
            }

            // No progress made - impossible to cover
            if (farthest == currentEnd) {
                return -1;
            }

            count++;
            currentEnd = farthest;
        }

        return count;
    }

    // Approach 2: Jump Game Style - O(n log n) time, O(1) space
    public int minIntervalsToCoverJump(int[][] intervals, int target) {
        if (target == 0)
            return 0;

        java.util.Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        int jumps = 0;
        int currentEnd = 0;
        int farthest = 0;
        int i = 0;

        while (currentEnd < target) {
            // Collect all intervals that can start from current position
            while (i < intervals.length && intervals[i][0] <= currentEnd) {
                farthest = Math.max(farthest, intervals[i][1]);
                i++;
            }

            if (farthest <= currentEnd) {
                return -1; // Cannot make progress
            }

            jumps++;
            currentEnd = farthest;
        }

        return jumps;
    }

    // Approach 3: Dynamic Programming - O(target * n) time, O(target) space
    public int minIntervalsToCoverDP(int[][] intervals, int target) {
        int[] dp = new int[target + 1];
        java.util.Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i <= target; i++) {
            for (int[] interval : intervals) {
                int start = interval[0];
                int end = interval[1];

                if (start <= i && i <= end && start >= 0 && dp[start] != Integer.MAX_VALUE) {
                    dp[i] = Math.min(dp[i], dp[start] + 1);
                }
            }
        }

        return dp[target] == Integer.MAX_VALUE ? -1 : dp[target];
    }

    // Follow-up: Return actual intervals used
    public java.util.List<int[]> getIntervalsUsed(int[][] intervals, int target) {
        if (target == 0)
            return new java.util.ArrayList<>();

        java.util.Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        java.util.List<int[]> result = new java.util.ArrayList<>();

        int currentEnd = 0;
        int i = 0;

        while (currentEnd < target) {
            int farthest = currentEnd;
            int bestInterval = -1;

            while (i < intervals.length && intervals[i][0] <= currentEnd) {
                if (intervals[i][1] > farthest) {
                    farthest = intervals[i][1];
                    bestInterval = i;
                }
                i++;
            }

            if (farthest == currentEnd) {
                return new java.util.ArrayList<>(); // Impossible
            }

            result.add(intervals[bestInterval]);
            currentEnd = farthest;

            // Reset i to find next best interval
            i = 0;
            while (i < intervals.length && intervals[i][1] <= currentEnd) {
                i++;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        MinimumNumberOfIntervalsToCover solution = new MinimumNumberOfIntervalsToCover();

        // Test Case 1: Basic example
        System.out.println("Basic: " + solution.minIntervalsToCover(new int[][] { { 1, 4 }, { 2, 6 }, { 5, 8 } }, 8)); // 2

        // Test Case 2: Adjacent intervals
        System.out
                .println("Adjacent: " + solution.minIntervalsToCover(new int[][] { { 1, 2 }, { 2, 3 }, { 3, 4 } }, 4)); // 3

        // Test Case 3: No intervals
        System.out.println("No intervals: " + solution.minIntervalsToCover(new int[][] {}, 5)); // -1

        // Test Case 4: Single interval covers all
        System.out.println("Single covers all: " + solution.minIntervalsToCover(new int[][] { { 0, 10 } }, 10)); // 1

        // Test Case 5: Gap in coverage
        System.out.println("Gap: " + solution.minIntervalsToCover(new int[][] { { 0, 2 }, { 4, 6 } }, 6)); // -1

        // Test Case 6: Overlapping intervals
        System.out.println(
                "Overlapping: " + solution.minIntervalsToCover(new int[][] { { 0, 3 }, { 1, 4 }, { 2, 5 } }, 5)); // 2

        // Test Case 7: Target is 0
        System.out.println("Target 0: " + solution.minIntervalsToCover(new int[][] { { 0, 5 } }, 0)); // 0

        // Test Case 8: No interval starts at 0
        System.out.println("No start at 0: " + solution.minIntervalsToCover(new int[][] { { 1, 3 }, { 2, 4 } }, 4)); // -1

        // Test approaches comparison
        System.out.println(
                "Jump style: " + solution.minIntervalsToCoverJump(new int[][] { { 1, 4 }, { 2, 6 }, { 5, 8 } }, 8)); // 2
        System.out.println("DP approach: " + solution.minIntervalsToCoverDP(new int[][] { { 0, 2 }, { 1, 3 } }, 3)); // 2

        // Test Case 9: Get actual intervals
        java.util.List<int[]> used = solution.getIntervalsUsed(new int[][] { { 0, 3 }, { 2, 6 }, { 5, 8 } }, 8);
        System.out.print("Intervals used: ");
        for (int[] interval : used) {
            System.out.print("[" + interval[0] + "," + interval[1] + "] ");
        }
        System.out.println();

        // Test Case 10: Large intervals
        System.out.println("Large: " + solution.minIntervalsToCover(new int[][] { { 0, 100 }, { 50, 200 } }, 200)); // 2
    }
}
