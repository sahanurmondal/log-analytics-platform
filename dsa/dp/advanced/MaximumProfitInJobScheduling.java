package dp.advanced;

import java.util.*;

/**
 * LeetCode 1235: Maximum Profit in Job Scheduling
 * https://leetcode.com/problems/maximum-profit-in-job-scheduling/
 *
 * Description:
 * Given startTime, endTime, and profit arrays, return the maximum profit you
 * can achieve by scheduling non-overlapping jobs.
 *
 * Constraints:
 * - 1 <= startTime.length == endTime.length == profit.length <= 5 * 10^4
 * - 1 <= startTime[i] < endTime[i] <= 10^9
 * - 1 <= profit[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it in O(n log n) time?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class MaximumProfitInJobScheduling {

    // Approach 1: DP + Binary Search - O(n log n) time, O(n) space
    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;

        // Create jobs array and sort by end time
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i] = new int[] { startTime[i], endTime[i], profit[i] };
        }

        Arrays.sort(jobs, (a, b) -> a[1] - b[1]); // Sort by end time

        // dp[i] = maximum profit using jobs 0..i
        int[] dp = new int[n];
        dp[0] = jobs[0][2];

        for (int i = 1; i < n; i++) {
            // Option 1: Skip current job
            int skipProfit = dp[i - 1];

            // Option 2: Include current job
            int includeProfit = jobs[i][2];

            // Find latest job that doesn't overlap
            int latestNonOverlap = findLatestNonOverlapping(jobs, i);
            if (latestNonOverlap != -1) {
                includeProfit += dp[latestNonOverlap];
            }

            dp[i] = Math.max(skipProfit, includeProfit);
        }

        return dp[n - 1];
    }

    private int findLatestNonOverlapping(int[][] jobs, int index) {
        int startTime = jobs[index][0];
        int left = 0, right = index - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (jobs[mid][1] <= startTime) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Approach 2: Memoization - O(n^2) time, O(n) space
    public int jobSchedulingMemo(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];

        for (int i = 0; i < n; i++) {
            jobs[i] = new int[] { startTime[i], endTime[i], profit[i] };
        }

        Arrays.sort(jobs, (a, b) -> a[0] - b[0]); // Sort by start time

        Integer[] memo = new Integer[n];
        return dfs(jobs, 0, memo);
    }

    private int dfs(int[][] jobs, int index, Integer[] memo) {
        if (index >= jobs.length)
            return 0;

        if (memo[index] != null)
            return memo[index];

        // Option 1: Skip current job
        int skip = dfs(jobs, index + 1, memo);

        // Option 2: Include current job
        int include = jobs[index][2];
        int nextIndex = findNextNonOverlapping(jobs, index);
        include += dfs(jobs, nextIndex, memo);

        memo[index] = Math.max(skip, include);
        return memo[index];
    }

    private int findNextNonOverlapping(int[][] jobs, int index) {
        int endTime = jobs[index][1];

        for (int i = index + 1; i < jobs.length; i++) {
            if (jobs[i][0] >= endTime) {
                return i;
            }
        }

        return jobs.length;
    }

    public static void main(String[] args) {
        MaximumProfitInJobScheduling solution = new MaximumProfitInJobScheduling();

        System.out.println("=== Maximum Profit in Job Scheduling Test Cases ===");

        // Test Case 1
        int[] start1 = { 1, 2, 3, 3 };
        int[] end1 = { 3, 4, 5, 6 };
        int[] profit1 = { 50, 10, 40, 70 };
        System.out.println("Test 1:");
        System.out.println("DP + Binary Search: " + solution.jobScheduling(start1, end1, profit1));
        System.out.println("Memoization: " + solution.jobSchedulingMemo(start1, end1, profit1));
        System.out.println("Expected: 120\n");

        // Test Case 2
        int[] start2 = { 1, 2, 3, 4, 6 };
        int[] end2 = { 3, 5, 10, 6, 9 };
        int[] profit2 = { 20, 20, 100, 70, 60 };
        System.out.println("Test 2:");
        System.out.println("DP + Binary Search: " + solution.jobScheduling(start2, end2, profit2));
        System.out.println("Expected: 150\n");

        performanceTest();
    }

    private static void performanceTest() {
        MaximumProfitInJobScheduling solution = new MaximumProfitInJobScheduling();

        int n = 1000;
        int[] start = new int[n];
        int[] end = new int[n];
        int[] profit = new int[n];

        for (int i = 0; i < n; i++) {
            start[i] = i * 2;
            end[i] = i * 2 + 1;
            profit[i] = (int) (Math.random() * 100) + 1;
        }

        System.out.println("=== Performance Test (Jobs: " + n + ") ===");

        long startTime = System.nanoTime();
        int result = solution.jobScheduling(start, end, profit);
        long endTime = System.nanoTime();
        System.out.println("DP + Binary Search: " + result + " - Time: " + (endTime - startTime) / 1_000_000.0 + " ms");
    }
}
