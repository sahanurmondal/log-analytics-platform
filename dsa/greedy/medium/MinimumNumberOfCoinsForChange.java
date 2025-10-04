package greedy.medium;

/**
 * Variation: Minimum Number of Coins for Change
 *
 * Description:
 * Given coins of different denominations and a total, find the minimum number
 * of coins needed to make the total.
 *
 * Constraints:
 * - 1 <= coins.length <= 12
 * - 1 <= coins[i] <= 10^5
 * - 1 <= amount <= 10^5
 */
import java.util.Arrays;

/**
 * LeetCode 322: Coin Change
 * https://leetcode.com/problems/coin-change/
 *
 * Description:
 * Given coins of different denominations and a total, find the minimum number
 * of coins needed to make the total.
 *
 * Constraints:
 * - 1 <= coins.length <= 12
 * - 1 <= coins[i] <= 10^5
 * - 1 <= amount <= 10^5
 *
 * Follow-up:
 * - Can you solve it using dynamic programming?
 * - Can you optimize space complexity?
 */
public class MinimumNumberOfCoinsForChange {
    public int coinChange(int[] coins, int amount) {
        if (amount == 0)
            return 0;

        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    public static void main(String[] args) {
        MinimumNumberOfCoinsForChange solution = new MinimumNumberOfCoinsForChange();
        System.out.println(solution.coinChange(new int[] { 1, 2, 5 }, 11)); // 3
        System.out.println(solution.coinChange(new int[] { 2 }, 3)); // -1
        // Edge Case: Amount is zero
        System.out.println(solution.coinChange(new int[] { 1, 2, 5 }, 0)); // 0
        // Edge Case: Single coin
        System.out.println(solution.coinChange(new int[] { 7 }, 14)); // 2
        // Edge Case: Large input
        int[] coins = new int[12];
        for (int i = 0; i < 12; i++)
            coins[i] = i + 1;
        System.out.println(solution.coinChange(coins, 100000)); // Should be large
    }
}
