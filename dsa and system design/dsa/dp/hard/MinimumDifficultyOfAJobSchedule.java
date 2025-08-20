package dp.hard;

import java.util.Arrays;

/**
 * LeetCode 1335: Minimum Difficulty of a Job Schedule
 * https://leetcode.com/problems/minimum-difficulty-of-a-job-schedule/
 *
 * Description:
 * You want to schedule a list of jobs in d days. Jobs are dependent (i.e To
 * work on the ith job, you have to finish all the jobs j where 0 <= j < i).
 * You have to finish at least one task every day. The difficulty of a job
 * schedule is the sum of difficulties of each day of the d days.
 * The difficulty of a day is the maximum difficulty of a job done on that day.
 * Given an array of integers jobDifficulty and an integer d. Return the minimum
 * difficulty of a job schedule. If you cannot find a schedule for the jobs
 * return -1.
 *
 * Constraints:
 * - 1 <= jobDifficulty.length <= 300
 * - 0 <= jobDifficulty[i] <= 1000
 * - 1 <= d <= 10
 *
 * Follow-up:
 * - What if d can be larger than array length?
 * - Can you optimize space complexity?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class MinimumDifficultyOfAJobSchedule {

    // Approach 1: Recursive with Memoization - O(n^2 * d) time, O(n * d) space
    public int minDifficultyMemo(int[] jobDifficulty, int d) {
        int n = jobDifficulty.length;
        if (d > n)
            return -1;

        Integer[][] memo = new Integer[n][d + 1];
        return minDifficultyHelper(jobDifficulty, 0, d, memo);
    }

    private int minDifficultyHelper(int[] jobDifficulty, int index, int daysLeft, Integer[][] memo) {
        int n = jobDifficulty.length;

        // Base case: no days left
        if (daysLeft == 0) {
            return index == n ? 0 : Integer.MAX_VALUE;
        }

        // Base case: not enough jobs for remaining days
        if (n - index < daysLeft) {
            return Integer.MAX_VALUE;
        }

        if (memo[index][daysLeft] != null) {
            return memo[index][daysLeft];
        }

        int minDifficulty = Integer.MAX_VALUE;
        int maxDifficultyToday = 0;

        // Try different number of jobs for today (at least 1, at most
        // n-index-daysLeft+1)
        for (int i = index; i <= n - daysLeft; i++) {
            maxDifficultyToday = Math.max(maxDifficultyToday, jobDifficulty[i]);

            int remainingDifficulty = minDifficultyHelper(jobDifficulty, i + 1, daysLeft - 1, memo);
            if (remainingDifficulty != Integer.MAX_VALUE) {
                minDifficulty = Math.min(minDifficulty, maxDifficultyToday + remainingDifficulty);
            }
        }

        memo[index][daysLeft] = minDifficulty;
        return minDifficulty;
    }

    // Approach 2: Bottom-up DP - O(n^2 * d) time, O(n * d) space
    public int minDifficultyDP(int[] jobDifficulty, int d) {
        int n = jobDifficulty.length;
        if (d > n)
            return -1;

        // dp[i][j] = minimum difficulty for first i jobs in j days
        int[][] dp = new int[n + 1][d + 1];

        // Initialize with large values
        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }

        dp[0][0] = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= Math.min(i, d); j++) {
                int maxDifficulty = 0;

                // Try different starting positions for day j
                for (int k = i - 1; k >= j - 1; k--) {
                    maxDifficulty = Math.max(maxDifficulty, jobDifficulty[k]);

                    if (dp[k][j - 1] != Integer.MAX_VALUE) {
                        dp[i][j] = Math.min(dp[i][j], dp[k][j - 1] + maxDifficulty);
                    }
                }
            }
        }

        return dp[n][d] == Integer.MAX_VALUE ? -1 : dp[n][d];
    }

    // Approach 3: Space Optimized DP - O(n^2 * d) time, O(n) space
    public int minDifficultyOptimized(int[] jobDifficulty, int d) {
        int n = jobDifficulty.length;
        if (d > n)
            return -1;

        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        Arrays.fill(prev, Integer.MAX_VALUE);
        Arrays.fill(curr, Integer.MAX_VALUE);

        prev[0] = 0;

        for (int day = 1; day <= d; day++) {
            Arrays.fill(curr, Integer.MAX_VALUE);

            for (int i = day; i <= n; i++) {
                int maxDifficulty = 0;

                for (int k = i - 1; k >= day - 1; k--) {
                    maxDifficulty = Math.max(maxDifficulty, jobDifficulty[k]);

                    if (prev[k] != Integer.MAX_VALUE) {
                        curr[i] = Math.min(curr[i], prev[k] + maxDifficulty);
                    }
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n] == Integer.MAX_VALUE ? -1 : prev[n];
    }

    // Approach 4: Monotonic Stack Optimization - O(n * d) time, O(n * d) space
    public int minDifficultyMonotonicStack(int[] jobDifficulty, int d) {
        int n = jobDifficulty.length;
        if (d > n)
            return -1;

        int[][] dp = new int[d][n];

        // Initialize first day
        dp[0][0] = jobDifficulty[0];
        for (int i = 1; i < n; i++) {
            dp[0][i] = Math.max(dp[0][i - 1], jobDifficulty[i]);
        }

        // Fill remaining days
        for (int day = 1; day < d; day++) {
            java.util.Stack<Integer> stack = new java.util.Stack<>();

            for (int i = day; i < n; i++) {
                dp[day][i] = Integer.MAX_VALUE;

                // Find minimum difficulty for ending at position i on this day
                for (int k = day - 1; k < i; k++) {
                    int maxDifficulty = 0;
                    for (int j = k + 1; j <= i; j++) {
                        maxDifficulty = Math.max(maxDifficulty, jobDifficulty[j]);
                    }
                    dp[day][i] = Math.min(dp[day][i], dp[day - 1][k] + maxDifficulty);
                }
            }
        }

        return dp[d - 1][n - 1];
    }

    // Approach 5: Precomputed Max Range - O(n^2 * d) time, O(n^2) space
    public int minDifficultyPrecomputed(int[] jobDifficulty, int d) {
        int n = jobDifficulty.length;
        if (d > n)
            return -1;

        // Precompute maximum in each range
        int[][] maxInRange = new int[n][n];
        for (int i = 0; i < n; i++) {
            maxInRange[i][i] = jobDifficulty[i];
            for (int j = i + 1; j < n; j++) {
                maxInRange[i][j] = Math.max(maxInRange[i][j - 1], jobDifficulty[j]);
            }
        }

        // DP with precomputed ranges
        int[][] dp = new int[n + 1][d + 1];

        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }

        dp[0][0] = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= Math.min(i, d); j++) {
                for (int k = j - 1; k < i; k++) {
                    if (dp[k][j - 1] != Integer.MAX_VALUE) {
                        dp[i][j] = Math.min(dp[i][j], dp[k][j - 1] + maxInRange[k][i - 1]);
                    }
                }
            }
        }

        return dp[n][d] == Integer.MAX_VALUE ? -1 : dp[n][d];
    }

    public static void main(String[] args) {
        MinimumDifficultyOfAJobSchedule solution = new MinimumDifficultyOfAJobSchedule();

        System.out.println("=== Minimum Difficulty of a Job Schedule Test Cases ===");

        // Test Case 1: Example from problem
        int[] jobDifficulty1 = { 6, 5, 4, 3, 2, 1 };
        int d1 = 2;
        System.out.println("Test 1 - Jobs: " + Arrays.toString(jobDifficulty1) + ", d: " + d1);
        System.out.println("Memoization: " + solution.minDifficultyMemo(jobDifficulty1, d1));
        System.out.println("DP: " + solution.minDifficultyDP(jobDifficulty1, d1));
        System.out.println("Optimized: " + solution.minDifficultyOptimized(jobDifficulty1, d1));
        System.out.println("Precomputed: " + solution.minDifficultyPrecomputed(jobDifficulty1, d1));
        System.out.println("Expected: 7\n");

        // Test Case 2: Impossible case
        int[] jobDifficulty2 = { 9, 9, 9 };
        int d2 = 4;
        System.out.println("Test 2 - Jobs: " + Arrays.toString(jobDifficulty2) + ", d: " + d2);
        System.out.println("DP: " + solution.minDifficultyDP(jobDifficulty2, d2));
        System.out.println("Expected: -1\n");

        // Test Case 3: Single day
        int[] jobDifficulty3 = { 1, 1, 1 };
        int d3 = 3;
        System.out.println("Test 3 - Jobs: " + Arrays.toString(jobDifficulty3) + ", d: " + d3);
        System.out.println("DP: " + solution.minDifficultyDP(jobDifficulty3, d3));
        System.out.println("Expected: 3\n");

        performanceTest();
    }

    private static void performanceTest() {
        MinimumDifficultyOfAJobSchedule solution = new MinimumDifficultyOfAJobSchedule();

        int[] largeArray = new int[300];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000);
        }
        int d = 10;

        System.out.println("=== Performance Test (Jobs: " + largeArray.length + ", d: " + d + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minDifficultyMemo(largeArray, d);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minDifficultyDP(largeArray, d);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minDifficultyOptimized(largeArray, d);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
