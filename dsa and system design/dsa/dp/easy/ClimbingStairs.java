package dp.easy;

/**
 * LeetCode 70: Climbing Stairs
 * https://leetcode.com/problems/climbing-stairs/
 *
 * Description:
 * You are climbing a staircase. It takes n steps to reach the top.
 * Each time you can either climb 1 or 2 steps. In how many distinct ways can
 * you climb to the top?
 *
 * Constraints:
 * - 1 <= n <= 45
 *
 * Company Tags: Google, Amazon, Microsoft, Apple
 * Difficulty: Easy
 */
public class ClimbingStairs {

    // Approach 1: DP - O(n) time, O(n) space
    public int climbStairs(int n) {
        if (n <= 2)
            return n;

        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    // Approach 2: Space Optimized - O(n) time, O(1) space
    public int climbStairsOptimized(int n) {
        if (n <= 2)
            return n;

        int prev2 = 1, prev1 = 2;

        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    public static void main(String[] args) {
        ClimbingStairs solution = new ClimbingStairs();

        System.out.println("=== Climbing Stairs Test Cases ===");

        for (int n = 1; n <= 10; n++) {
            System.out.println("n = " + n + ": " + solution.climbStairs(n));
        }
    }
}
