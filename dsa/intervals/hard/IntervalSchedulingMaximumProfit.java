package intervals.hard;

import java.util.*;

/**
 * LeetCode 1235: Maximum Profit in Job Scheduling
 * https://leetcode.com/problems/maximum-profit-in-job-scheduling/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 100+ interviews)
 *
 * Description: We have n jobs, where every job is scheduled to be done from
 * startTime[i] to endTime[i],
 * obtaining a profit of profit[i]. You're given the startTime, endTime and
 * profit arrays, return the
 * maximum profit you can take such that there are no two jobs in the subset
 * with overlapping time range.
 *
 * Constraints:
 * - 1 <= startTime.length == endTime.length == profit.length <= 5 * 10^4
 * - 1 <= startTime[i] < endTime[i] <= 10^9
 * - 1 <= profit[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you optimize space complexity?
 * 2. What if we need to return the actual job sequence?
 * 3. Can you solve it with different DP state definitions?
 */
public class IntervalSchedulingMaximumProfit {

    // Approach 1: DP + Binary Search - O(n log n) time, O(n) space
    public int jobScheduling(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;

        // Create jobs array and sort by end time
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i] = new int[] { startTime[i], endTime[i], profit[i] };
        }
        Arrays.sort(jobs, (a, b) -> Integer.compare(a[1], b[1]));

        // dp[i] = maximum profit using jobs 0 to i
        int[] dp = new int[n];
        dp[0] = jobs[0][2];

        for (int i = 1; i < n; i++) {
            // Option 1: Don't take current job
            int profitWithoutCurrent = dp[i - 1];

            // Option 2: Take current job
            int profitWithCurrent = jobs[i][2];
            int lastCompatible = findLastCompatibleJob(jobs, i);
            if (lastCompatible != -1) {
                profitWithCurrent += dp[lastCompatible];
            }

            dp[i] = Math.max(profitWithoutCurrent, profitWithCurrent);
        }

        return dp[n - 1];
    }

    // Approach 2: TreeMap DP - O(n log n) time, O(n) space
    public int jobSchedulingTreeMap(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i] = new int[] { startTime[i], endTime[i], profit[i] };
        }
        Arrays.sort(jobs, (a, b) -> Integer.compare(a[1], b[1]));

        // TreeMap: endTime -> maxProfit up to that time
        TreeMap<Integer, Integer> dp = new TreeMap<>();
        dp.put(0, 0);

        for (int[] job : jobs) {
            int start = job[0], end = job[1], prof = job[2];

            // Find maximum profit before current job starts
            int prevMaxProfit = dp.floorEntry(start).getValue();
            int currentMaxProfit = dp.lastEntry().getValue();

            // Update if taking current job gives better profit
            if (prevMaxProfit + prof > currentMaxProfit) {
                dp.put(end, prevMaxProfit + prof);
            }
        }

        return dp.lastEntry().getValue();
    }

    // Approach 3: Recursive DP with Memoization - O(n log n) time, O(n) space
    public int jobSchedulingRecursive(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][3];
        for (int i = 0; i < n; i++) {
            jobs[i] = new int[] { startTime[i], endTime[i], profit[i] };
        }
        Arrays.sort(jobs, (a, b) -> Integer.compare(a[1], b[1]));

        Integer[] memo = new Integer[n];
        return dfs(jobs, n - 1, memo);
    }

    private int dfs(int[][] jobs, int i, Integer[] memo) {
        if (i < 0)
            return 0;
        if (memo[i] != null)
            return memo[i];

        // Option 1: Don't take current job
        int withoutCurrent = dfs(jobs, i - 1, memo);

        // Option 2: Take current job
        int withCurrent = jobs[i][2];
        int lastCompatible = findLastCompatibleJob(jobs, i);
        withCurrent += dfs(jobs, lastCompatible, memo);

        return memo[i] = Math.max(withoutCurrent, withCurrent);
    }

    // Helper method: Find last job that ends before current job starts
    private int findLastCompatibleJob(int[][] jobs, int currentIndex) {
        int start = jobs[currentIndex][0];
        int left = 0, right = currentIndex - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (jobs[mid][1] <= start) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Follow-up: Return actual job sequence
    public List<Integer> getOptimalJobSequence(int[] startTime, int[] endTime, int[] profit) {
        int n = startTime.length;
        int[][] jobs = new int[n][4]; // [start, end, profit, originalIndex]
        for (int i = 0; i < n; i++) {
            jobs[i] = new int[] { startTime[i], endTime[i], profit[i], i };
        }
        Arrays.sort(jobs, (a, b) -> Integer.compare(a[1], b[1]));

        int[] dp = new int[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        dp[0] = jobs[0][2];

        for (int i = 1; i < n; i++) {
            int profitWithoutCurrent = dp[i - 1];
            int profitWithCurrent = jobs[i][2];
            int lastCompatible = findLastCompatibleJob(jobs, i);

            if (lastCompatible != -1) {
                profitWithCurrent += dp[lastCompatible];
            }

            if (profitWithCurrent > profitWithoutCurrent) {
                dp[i] = profitWithCurrent;
                parent[i] = lastCompatible;
            } else {
                dp[i] = profitWithoutCurrent;
                parent[i] = i - 1;
            }
        }

        // Reconstruct path
        List<Integer> result = new ArrayList<>();
        int current = n - 1;
        while (current >= 0) {
            if (current == 0 || (parent[current] != current - 1)) {
                result.add(jobs[current][3]); // Add original index
                current = parent[current];
            } else {
                current = parent[current];
            }
        }

        Collections.reverse(result);
        return result;
    }

    // Helper method: Validate job sequence
    private boolean isValidSequence(int[] startTime, int[] endTime, List<Integer> sequence) {
        for (int i = 0; i < sequence.size() - 1; i++) {
            int job1 = sequence.get(i);
            int job2 = sequence.get(i + 1);
            if (endTime[job1] > startTime[job2]) {
                return false;
            }
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        IntervalSchedulingMaximumProfit solver = new IntervalSchedulingMaximumProfit();

        // Test case 1: Basic case
        int[] start1 = { 1, 2, 3, 3 };
        int[] end1 = { 3, 4, 5, 6 };
        int[] profit1 = { 50, 10, 40, 70 };
        System.out.println("Test 1 - Expected: 120");
        System.out.println("Approach 1 (DP + Binary Search): " + solver.jobScheduling(start1, end1, profit1));
        System.out.println("Approach 2 (TreeMap): " + solver.jobSchedulingTreeMap(start1, end1, profit1));
        System.out.println("Approach 3 (Recursive): " + solver.jobSchedulingRecursive(start1, end1, profit1));

        // Test case 2: No overlaps
        int[] start2 = { 1, 2, 3, 4, 6 };
        int[] end2 = { 2, 3, 4, 5, 7 };
        int[] profit2 = { 1, 2, 3, 4, 5 };
        System.out.println("\nTest 2 - Expected: 15");
        System.out.println("Approach 1: " + solver.jobScheduling(start2, end2, profit2));
        System.out.println("Approach 2: " + solver.jobSchedulingTreeMap(start2, end2, profit2));
        System.out.println("Approach 3: " + solver.jobSchedulingRecursive(start2, end2, profit2));

        // Test case 3: All overlap
        int[] start3 = { 1, 1, 1 };
        int[] end3 = { 2, 3, 4 };
        int[] profit3 = { 5, 6, 4 };
        System.out.println("\nTest 3 - Expected: 6");
        System.out.println("Approach 1: " + solver.jobScheduling(start3, end3, profit3));
        System.out.println("Approach 2: " + solver.jobSchedulingTreeMap(start3, end3, profit3));
        System.out.println("Approach 3: " + solver.jobSchedulingRecursive(start3, end3, profit3));

        // Test follow-up: Job sequence
        System.out.println("\nOptimal Job Sequence Test:");
        List<Integer> sequence = solver.getOptimalJobSequence(start1, end1, profit1);
        System.out.println("Optimal jobs (0-indexed): " + sequence);
        System.out.println("Valid sequence: " + solver.isValidSequence(start1, end1, sequence));

        int totalProfit = 0;
        for (int job : sequence) {
            totalProfit += profit1[job];
            System.out.println("Job " + job + ": [" + start1[job] + ", " + end1[job] + "] profit=" + profit1[job]);
        }
        System.out.println("Total profit: " + totalProfit);
    }
}
