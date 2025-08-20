package dp.easy;

/**
 * LeetCode 746: Min Cost Climbing Stairs
 * https://leetcode.com/problems/min-cost-climbing-stairs/
 *
 * Description:
 * You are given an integer array cost where cost[i] is the cost of ith step on
 * a staircase.
 * Once you pay the cost, you can either climb one or two steps.
 * You can either start from the step with index 0, or the step with index 1.
 * Return the minimum cost to reach the top of the floor.
 *
 * Constraints:
 * - 2 <= cost.length <= 1000
 * - 0 <= cost[i] <= 999
 *
 * Company Tags: Amazon, Google, Microsoft
 * Difficulty: Easy
 */
public class MinCostClimbingStairs {

    // Approach 1: DP Array - O(n) time, O(n) space
    public int minCostClimbingStairs(int[] cost) {
        int n = cost.length;
        int[] dp = new int[n + 1];

        dp[0] = 0; // Start at step 0 (free)
        dp[1] = 0; // Start at step 1 (free)

        for (int i = 2; i <= n; i++) {
            dp[i] = Math.min(dp[i - 1] + cost[i - 1], dp[i - 2] + cost[i - 2]);
        }

        return dp[n];
    }

    // Approach 2: Space Optimized - O(n) time, O(1) space
    public int minCostClimbingStairsOptimized(int[] cost) {
        int prev2 = 0; // dp[i-2]
        int prev1 = 0; // dp[i-1]

        for (int i = 2; i <= cost.length; i++) {
            int current = Math.min(prev1 + cost[i - 1], prev2 + cost[i - 2]);
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    // Approach 3: Recursive with Memoization - O(n) time, O(n) space
    public int minCostClimbingStairsMemo(int[] cost) {
        Integer[] memo = new Integer[cost.length];
        return Math.min(helper(cost, 0, memo), helper(cost, 1, memo));
    }

    private int helper(int[] cost, int i, Integer[] memo) {
        if (i >= cost.length)
            return 0;

        if (memo[i] != null)
            return memo[i];

        memo[i] = cost[i] + Math.min(helper(cost, i + 1, memo), helper(cost, i + 2, memo));
        return memo[i];
    }

    public static void main(String[] args) {
        MinCostClimbingStairs solution = new MinCostClimbingStairs();

        System.out.println("=== Min Cost Climbing Stairs Test Cases ===");

        int[] cost1 = { 10, 15, 20 };
        System.out.println("Cost: " + java.util.Arrays.toString(cost1));
        System.out.println("DP: " + solution.minCostClimbingStairs(cost1));
        System.out.println("Optimized: " + solution.minCostClimbingStairsOptimized(cost1));
        System.out.println("Expected: 15\n");

        int[] cost2 = { 1, 100, 1, 1, 1, 100, 1, 1, 100, 1 };
        System.out.println("Cost: " + java.util.Arrays.toString(cost2));
        System.out.println("DP: " + solution.minCostClimbingStairs(cost2));
        System.out.println("Expected: 6\n");
    }
}
