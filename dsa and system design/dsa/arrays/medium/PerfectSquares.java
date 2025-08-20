package arrays.medium;

import java.util.*;

/**
 * LeetCode 279: Perfect Squares
 * https://leetcode.com/problems/perfect-squares/
 *
 * Description:
 * Given an integer n, return the least number of perfect square numbers that
 * sum to n.
 * A perfect square is an integer that is the square of an integer.
 *
 * Constraints:
 * - 1 <= n <= 10^4
 *
 * Follow-up:
 * - Can you solve it using BFS?
 * 
 * Time Complexity: O(n * sqrt(n))
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Use dynamic programming approach
 * 2. For each number, try all perfect squares less than it
 * 3. Take minimum count among all possibilities
 */
public class PerfectSquares {
    public int numSquares(int n) {
        int[] dp = new int[n + 1];
        Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j * j <= i; j++) {
                dp[i] = Math.min(dp[i], dp[i - j * j] + 1);
            }
        }

        return dp[n];
    }

    public static void main(String[] args) {
        PerfectSquares solution = new PerfectSquares();

        // Test Case 1: Normal case
        System.out.println(solution.numSquares(12)); // Expected: 3

        // Test Case 2: Edge case - perfect square
        System.out.println(solution.numSquares(13)); // Expected: 2

        // Test Case 3: Corner case - small number
        System.out.println(solution.numSquares(1)); // Expected: 1

        // Test Case 4: Large input
        System.out.println(solution.numSquares(100)); // Expected: 1

        // Test Case 5: Prime number
        System.out.println(solution.numSquares(7)); // Expected: 4

        // Test Case 6: Special case - sum of two squares
        System.out.println(solution.numSquares(5)); // Expected: 2

        // Test Case 7: Boundary case
        System.out.println(solution.numSquares(3)); // Expected: 3

        // Test Case 8: Large perfect square
        System.out.println(solution.numSquares(64)); // Expected: 1

        // Test Case 9: Sum of three squares
        System.out.println(solution.numSquares(11)); // Expected: 3

        // Test Case 10: Four squares (worst case)
        System.out.println(solution.numSquares(15)); // Expected: 4
    }
}
