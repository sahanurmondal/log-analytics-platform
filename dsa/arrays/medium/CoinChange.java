package arrays.medium;

import java.util.*;

/**
 * LeetCode 322: Coin Change
 * https://leetcode.com/problems/coin-change/
 *
 * Description:
 * You are given an integer array coins representing coins of different
 * denominations and an integer amount
 * representing a total amount of money. Return the fewest number of coins that
 * you need to make up that amount.
 * If that amount of money cannot be made up by any combination of the coins,
 * return -1.
 *
 * Constraints:
 * - 1 <= coins.length <= 12
 * - 1 <= coins[i] <= 2^31 - 1
 * - 0 <= amount <= 10^4
 *
 * Follow-up:
 * - Can you solve it using BFS?
 * 
 * Time Complexity: O(amount * coins.length)
 * Space Complexity: O(amount)
 * 
 * Algorithm:
 * 1. Use dynamic programming with bottom-up approach
 * 2. For each amount, try all coins and take minimum
 * 3. Initialize dp array with amount+1 (impossible value)
 */
public class CoinChange {
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        CoinChange solution = new CoinChange();

        // Test Case 1: Normal case
        System.out.println(solution.coinChange(new int[] { 1, 3, 4 }, 6)); // Expected: 2

        // Test Case 2: Edge case - impossible
        System.out.println(solution.coinChange(new int[] { 2 }, 3)); // Expected: -1

        // Test Case 3: Corner case - amount is 0
        System.out.println(solution.coinChange(new int[] { 1 }, 0)); // Expected: 0

        // Test Case 4: Large input
        System.out.println(solution.coinChange(new int[] { 1, 2, 5 }, 11)); // Expected: 3

        // Test Case 5: Minimum input - single coin
        System.out.println(solution.coinChange(new int[] { 1 }, 1)); // Expected: 1

        // Test Case 6: Special case - coin equals amount
        System.out.println(solution.coinChange(new int[] { 5 }, 5)); // Expected: 1

        // Test Case 7: Boundary case - multiple same coins needed
        System.out.println(solution.coinChange(new int[] { 2 }, 4)); // Expected: 2

        // Test Case 8: Large denominations
        System.out.println(solution.coinChange(new int[] { 186, 419, 83, 408 }, 6249)); // Expected: 20

        // Test Case 9: Greedy fails case
        System.out.println(solution.coinChange(new int[] { 1, 4, 5 }, 8)); // Expected: 2

        // Test Case 10: Single large coin
        System.out.println(solution.coinChange(new int[] { 2147483647 }, 2)); // Expected: -1
    }
}
