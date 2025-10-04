package intervals.hard;

/**
 * LeetCode 1235: Maximum Profit in Job Scheduling (Weighted Interval
 * Scheduling)
 * https://leetcode.com/problems/maximum-profit-in-job-scheduling/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 20+ interviews)
 *
 * Description: Given a list of jobs where each job has a start time, end time,
 * and profit,
 * find the maximum profit subset of non-overlapping jobs.
 *
 * Constraints:
 * - 1 <= jobs.length <= 10^4
 * - 0 <= startTime[i] < endTime[i] <= 10^9
 * - 0 <= profit[i] <= 10^9
 *
 * Follow-up Questions:
 * 1. How to return the actual jobs selected for maximum profit?
 * 2. What if jobs can have negative profit?
 * 3. How to solve for exactly k jobs?
 */
public class IntervalSchedulingMaximumWeight {
    // Approach 1: DP with Binary Search - O(n log n) time, O(n) space
    public int maxProfit(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i][0] = startTime[i];
            jobs[i][1] = endTime[i];
            jobs[i][2] = profit[i];
        }
        java.util.Arrays.sort(jobs, java.util.Comparator.comparingInt(a -> a[1]));
        int[] dp = new int[n];
        dp[0] = jobs[0][2];
        for (int i = 1; i < n; i++) {
            int incl = jobs[i][2];
            int l = binarySearch(jobs, i);
            if (l != -1)
                incl += dp[l];
            dp[i] = Math.max(dp[i - 1], incl);
        }
        return dp[n - 1];
    }

    // Helper: Find last non-overlapping job
    private int binarySearch(int[][] jobs, int idx) {
        int lo = 0, hi = idx - 1, res = -1;
        while (lo <= hi) {
            int mid = lo + (hi - lo) / 2;
            if (jobs[mid][1] <= jobs[idx][0]) {
                res = mid;
                lo = mid + 1;
            } else
                hi = mid - 1;
        }
        return res;
    }

    // Approach 2: DP with TreeMap (for follow-up: negative profits) - O(n log n)
    public int maxProfitWithNegative(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i][0] = startTime[i];
            jobs[i][1] = endTime[i];
            jobs[i][2] = profit[i];
        }
        java.util.Arrays.sort(jobs, java.util.Comparator.comparingInt(a -> a[1]));
        java.util.TreeMap<Integer, Integer> dp = new java.util.TreeMap<>();
        dp.put(0, 0);
        for (int[] job : jobs) {
            int prev = dp.floorEntry(job[0]).getValue();
            int curr = Math.max(dp.lastEntry().getValue(), prev + job[2]);
            dp.put(job[1], curr);
        }
        return dp.lastEntry().getValue();
    }

    // Follow-up: Return actual jobs selected
    public java.util.List<int[]> getSelectedJobs(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i][0] = startTime[i];
            jobs[i][1] = endTime[i];
            jobs[i][2] = profit[i];
        }
        java.util.Arrays.sort(jobs, java.util.Comparator.comparingInt(a -> a[1]));
        int[] dp = new int[n];
        int[] prev = new int[n];
        dp[0] = jobs[0][2];
        prev[0] = -1;
        for (int i = 1; i < n; i++) {
            int incl = jobs[i][2];
            int l = binarySearch(jobs, i);
            if (l != -1)
                incl += dp[l];
            if (incl > dp[i - 1]) {
                dp[i] = incl;
                prev[i] = l;
            } else {
                dp[i] = dp[i - 1];
                prev[i] = i - 1;
            }
        }
        java.util.List<int[]> res = new java.util.ArrayList<>();
        int i = n - 1;
        while (i >= 0) {
            if (prev[i] == -1 || dp[i] != dp[prev[i]]) {
                res.add(0, jobs[i]);
                i = prev[i];
            } else
                i--;
        }
        return res;
    }

    // Follow-up: Exactly k jobs
    public int maxProfitKJobs(int[] startTime, int[] endTime, int[] profit, int k) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i][0] = startTime[i];
            jobs[i][1] = endTime[i];
            jobs[i][2] = profit[i];
        }
        java.util.Arrays.sort(jobs, java.util.Comparator.comparingInt(a -> a[1]));
        int[][] dp = new int[n + 1][k + 1];
        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= Math.min(i, k); j++) {
                int incl = jobs[i - 1][2];
                int l = binarySearch(jobs, i - 1);
                if (l != -1)
                    incl += dp[l + 1][j - 1];
                dp[i][j] = Math.max(dp[i - 1][j], incl);
            }
        }
        return dp[n][k];
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        IntervalSchedulingMaximumWeight sol = new IntervalSchedulingMaximumWeight();
        // Test 1: Basic
        int[] st1 = { 1, 2, 3, 3 }, et1 = { 3, 4, 5, 6 }, pf1 = { 50, 10, 40, 70 };
        System.out.println("Test 1: Expected 120 -> " + sol.maxProfit(st1, et1, pf1));
        // Test 2: Overlapping
        int[] st2 = { 1, 2, 3, 4 }, et2 = { 3, 5, 10, 6 }, pf2 = { 20, 20, 100, 70 };
        System.out.println("Test 2: Expected 150 -> " + sol.maxProfit(st2, et2, pf2));
        // Test 3: All overlap
        int[] st3 = { 1, 1, 1 }, et3 = { 2, 3, 4 }, pf3 = { 5, 6, 4 };
        System.out.println("Test 3: Expected 6 -> " + sol.maxProfit(st3, et3, pf3));
        // Test 4: Single job
        int[] st4 = { 1 }, et4 = { 2 }, pf4 = { 10 };
        System.out.println("Test 4: Expected 10 -> " + sol.maxProfit(st4, et4, pf4));
        // Test 5: Empty
        int[] st5 = {}, et5 = {}, pf5 = {};
        System.out.println("Test 5: Expected 0 -> " + sol.maxProfit(st5, et5, pf5));
        // Test 6: Negative profits
        int[] st6 = { 1, 2, 3 }, et6 = { 2, 3, 4 }, pf6 = { -5, 10, -1 };
        System.out.println("Test 6: Expected 10 -> " + sol.maxProfitWithNegative(st6, et6, pf6));
        // Test 7: Get selected jobs
        int[] st7 = { 1, 2, 3, 3 }, et7 = { 3, 4, 5, 6 }, pf7 = { 50, 10, 40, 70 };
        System.out.println("Test 7: Selected jobs -> " + sol.getSelectedJobs(st7, et7, pf7));
        // Test 8: Exactly k jobs
        int[] st8 = { 1, 2, 3, 3 }, et8 = { 3, 4, 5, 6 }, pf8 = { 50, 10, 40, 70 };
        System.out.println("Test 8: k=2 Expected 110 -> " + sol.maxProfitKJobs(st8, et8, pf8, 2));
        // Test 9: Large input
        int n = 1000;
        int[] st9 = new int[n], et9 = new int[n], pf9 = new int[n];
        for (int i = 0; i < n; i++) {
            st9[i] = i * 2;
            et9[i] = i * 2 + 1;
            pf9[i] = 1;
        }
        System.out.println("Test 9: Large input Expected 1000 -> " + sol.maxProfit(st9, et9, pf9));
        // Test 10: Edge case
        int[] st10 = { 1, 2, 3 }, et10 = { 2, 3, 4 }, pf10 = { 0, 0, 0 };
        System.out.println("Test 10: Expected 0 -> " + sol.maxProfit(st10, et10, pf10));
        // Test 11: All jobs non-overlapping
        int[] st11 = { 1, 3, 5 }, et11 = { 2, 4, 6 }, pf11 = { 10, 20, 30 };
        System.out.println("Test 11: Expected 60 -> " + sol.maxProfit(st11, et11, pf11));
        // Test 12: All jobs overlapping
        int[] st12 = { 1, 1, 1 }, et12 = { 2, 2, 2 }, pf12 = { 5, 6, 7 };
        System.out.println("Test 12: Expected 7 -> " + sol.maxProfit(st12, et12, pf12));
        // Test 13: k=1 job
        System.out.println("Test 13: k=1 Expected 70 -> " + sol.maxProfitKJobs(st8, et8, pf8, 1));
        // Test 14: k=4 jobs
        System.out.println("Test 14: k=4 Expected 170 -> " + sol.maxProfitKJobs(st8, et8, pf8, 4));
        // Test 15: Large profits
        int[] st15 = { 1, 2, 3 }, et15 = { 2, 3, 4 }, pf15 = { 1000000, 1000000, 1000000 };
        System.out.println("Test 15: Expected 2000000 -> " + sol.maxProfit(st15, et15, pf15));
    }
}
