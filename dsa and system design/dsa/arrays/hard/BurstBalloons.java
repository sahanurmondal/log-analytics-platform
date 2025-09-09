package arrays.hard;

/**
 * LeetCode 312: Burst Balloons
 * https://leetcode.com/problems/burst-balloons/
 *
 * Description:
 * You are given n balloons, indexed from 0 to n - 1. Each balloon is painted
 * with a number on it represented by an array nums.
 * You are asked to burst all the balloons. If you burst the ith balloon, you
 * will get nums[i - 1] * nums[i] * nums[i + 1] coins.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 300
 * - 0 <= nums[i] <= 100
 *
 * Follow-up:
 * - Can you solve it using divide and conquer with memoization?
 * 
 * Time Complexity: O(n^3)
 * Space Complexity: O(n^2)
 * 
 * Algorithm:
 * 1. Add boundary balloons with value 1
 * 2. Use dynamic programming on intervals
 * 3. For each interval, try bursting each balloon last
 */
public class BurstBalloons {
    public int maxCoins(int[] nums) {
        int n = nums.length;
        int[] balloons = new int[n + 2];
        balloons[0] = balloons[n + 1] = 1;

        for (int i = 0; i < n; i++) {
            balloons[i + 1] = nums[i];
        }

        int[][] dp = new int[n + 2][n + 2];

        for (int len = 2; len <= n + 1; len++) {
            for (int left = 0; left <= n + 1 - len; left++) {
                int right = left + len;
                for (int k = left + 1; k < right; k++) {
                    int coins = balloons[left] * balloons[k] * balloons[right];
                    dp[left][right] = Math.max(dp[left][right],
                            dp[left][k] + dp[k][right] + coins);
                }
            }
        }

        return dp[0][n + 1];
    }

    public static void main(String[] args) {
        BurstBalloons solution = new BurstBalloons();

        // Test Case 1: Normal case
        System.out.println(solution.maxCoins(new int[] { 3, 1, 5, 8 })); // Expected: 167

        // Test Case 2: Edge case - single balloon
        System.out.println(solution.maxCoins(new int[] { 1, 5 })); // Expected: 10

        // Test Case 3: Corner case - all zeros
        System.out.println(solution.maxCoins(new int[] { 0, 0, 0 })); // Expected: 0

        // Test Case 4: Large input - increasing values
        System.out.println(solution.maxCoins(new int[] { 1, 2, 3, 4, 5 })); // Expected: 110

        // Test Case 5: Minimum input - single element
        System.out.println(solution.maxCoins(new int[] { 5 })); // Expected: 5

        // Test Case 6: Special case - decreasing values
        System.out.println(solution.maxCoins(new int[] { 5, 4, 3, 2, 1 })); // Expected: 110

        // Test Case 7: Boundary case - all same values
        System.out.println(solution.maxCoins(new int[] { 3, 3, 3 })); // Expected: 45

        // Test Case 8: Large values
        System.out.println(solution.maxCoins(new int[] { 100, 100 })); // Expected: 20000

        // Test Case 9: Mixed with zeros
        System.out.println(solution.maxCoins(new int[] { 3, 0, 5 })); // Expected: 15

        // Test Case 10: Optimal ordering
        System.out.println(solution.maxCoins(new int[] { 2, 4, 6 })); // Expected: 64
    }
}
