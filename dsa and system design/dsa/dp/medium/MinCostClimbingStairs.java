package dp.medium;

import java.util.Arrays;

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
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - What if you can climb 1, 2, or 3 steps at a time?
 * 
 * Company Tags: Amazon, Google, Microsoft, Facebook, Apple, Bloomberg
 * Difficulty: Easy (but categorized as Medium per prompt)
 */
public class MinCostClimbingStairs {

    // Approach 1: Recursive (Brute Force) - O(2^n) time, O(n) space
    public int minCostClimbingStairsRecursive(int[] cost) {
        return Math.min(minCostHelper(cost, 0), minCostHelper(cost, 1));
    }

    private int minCostHelper(int[] cost, int index) {
        if (index >= cost.length)
            return 0;

        int oneStep = minCostHelper(cost, index + 1);
        int twoSteps = minCostHelper(cost, index + 2);

        return cost[index] + Math.min(oneStep, twoSteps);
    }

    // Approach 2: Memoization (Top-down DP) - O(n) time, O(n) space
    public int minCostClimbingStairsMemo(int[] cost) {
        int[] memo = new int[cost.length];
        Arrays.fill(memo, -1);
        return Math.min(minCostMemoHelper(cost, 0, memo), minCostMemoHelper(cost, 1, memo));
    }

    private int minCostMemoHelper(int[] cost, int index, int[] memo) {
        if (index >= cost.length)
            return 0;

        if (memo[index] != -1)
            return memo[index];

        int oneStep = minCostMemoHelper(cost, index + 1, memo);
        int twoSteps = minCostMemoHelper(cost, index + 2, memo);

        memo[index] = cost[index] + Math.min(oneStep, twoSteps);
        return memo[index];
    }

    // Approach 3: Tabulation (Bottom-up DP) - O(n) time, O(n) space
    public int minCostClimbingStairsDP(int[] cost) {
        int n = cost.length;
        int[] dp = new int[n + 2];

        // Base cases
        dp[n] = 0;
        dp[n + 1] = 0;

        // Fill from bottom to top
        for (int i = n - 1; i >= 0; i--) {
            dp[i] = cost[i] + Math.min(dp[i + 1], dp[i + 2]);
        }

        return Math.min(dp[0], dp[1]);
    }

    // Approach 4: Space Optimized - O(n) time, O(1) space
    public int minCostClimbingStairsOptimized(int[] cost) {
        int n = cost.length;
        int first = 0; // dp[i+2]
        int second = 0; // dp[i+1]

        for (int i = n - 1; i >= 0; i--) {
            int current = cost[i] + Math.min(first, second);
            first = second;
            second = current;
        }

        return Math.min(first, second);
    }

    // Approach 5: Forward DP - O(n) time, O(1) space
    public int minCostClimbingStairsForward(int[] cost) {
        int n = cost.length;
        int prev2 = cost[0];
        int prev1 = cost[1];

        for (int i = 2; i < n; i++) {
            int current = cost[i] + Math.min(prev1, prev2);
            prev2 = prev1;
            prev1 = current;
        }

        return Math.min(prev1, prev2);
    }

    public static void main(String[] args) {
        MinCostClimbingStairs solution = new MinCostClimbingStairs();

        System.out.println("=== Min Cost Climbing Stairs Test Cases ===");

        // Test Case 1: Example from problem
        int[] cost1 = { 10, 15, 20 };
        System.out.println("Test 1 - Cost: " + Arrays.toString(cost1));
        System.out.println("Recursive: " + solution.minCostClimbingStairsRecursive(cost1));
        System.out.println("Memoization: " + solution.minCostClimbingStairsMemo(cost1));
        System.out.println("DP: " + solution.minCostClimbingStairsDP(cost1));
        System.out.println("Optimized: " + solution.minCostClimbingStairsOptimized(cost1));
        System.out.println("Forward: " + solution.minCostClimbingStairsForward(cost1));
        System.out.println("Expected: 15\n");

        // Test Case 2: Longer array
        int[] cost2 = { 1, 100, 1, 1, 1, 100, 1, 1, 100, 1 };
        System.out.println("Test 2 - Cost: " + Arrays.toString(cost2));
        System.out.println("Optimized: " + solution.minCostClimbingStairsOptimized(cost2));
        System.out.println("Expected: 6\n");

        // Test Case 3: Minimum length
        int[] cost3 = { 1, 2 };
        System.out.println("Test 3 - Cost: " + Arrays.toString(cost3));
        System.out.println("Optimized: " + solution.minCostClimbingStairsOptimized(cost3));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        MinCostClimbingStairs solution = new MinCostClimbingStairs();

        int[] largeCost = new int[1000];
        for (int i = 0; i < largeCost.length; i++) {
            largeCost[i] = (int) (Math.random() * 1000);
        }

        System.out.println("=== Performance Test (Array size: " + largeCost.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minCostClimbingStairsMemo(largeCost);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minCostClimbingStairsDP(largeCost);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minCostClimbingStairsOptimized(largeCost);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
