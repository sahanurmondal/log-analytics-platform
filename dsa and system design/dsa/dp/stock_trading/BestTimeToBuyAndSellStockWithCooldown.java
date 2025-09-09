package dp.stock_trading;

import java.util.Arrays;

/**
 * LeetCode 309: Best Time to Buy and Sell Stock with Cooldown
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-cooldown/
 *
 * Description:
 * You are given an array prices where prices[i] is the price of a given stock
 * on the ith day.
 * Find the maximum profit you can achieve. You may complete as many
 * transactions as you like
 * (i.e., buy one and sell one share of the stock multiple times) with the
 * following restrictions:
 * - After you sell your stock, you cannot buy stock on next day (i.e., cooldown
 * 1 day).
 * Note: You may not engage in multiple transactions simultaneously.
 *
 * Constraints:
 * - 1 <= prices.length <= 5000
 * - 0 <= prices[i] <= 1000
 *
 * Follow-up:
 * - What if cooldown is k days instead of 1?
 * - Can you solve it in O(1) space?
 *
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg
 * Difficulty: Medium
 */
public class BestTimeToBuyAndSellStockWithCooldown {

    // Approach 1: State Machine DP (3 states) - O(n) time, O(n) space
    public int maxProfitStateMachine(int[] prices) {
        int n = prices.length;
        if (n <= 1)
            return 0;

        // State 0: Hold stock
        // State 1: Sold stock (cooldown)
        // State 2: No stock, can buy
        int[][] dp = new int[n][3];

        dp[0][0] = -prices[0]; // Buy on day 0
        dp[0][1] = 0; // Cannot sell on day 0
        dp[0][2] = 0; // Start with no stock

        for (int i = 1; i < n; i++) {
            dp[i][0] = Math.max(dp[i - 1][0], dp[i - 1][2] - prices[i]); // Hold or buy
            dp[i][1] = dp[i - 1][0] + prices[i]; // Sell
            dp[i][2] = Math.max(dp[i - 1][2], dp[i - 1][1]); // Rest or cooldown complete
        }

        return Math.max(dp[n - 1][1], dp[n - 1][2]);
    }

    // Approach 2: Space Optimized State Machine - O(n) time, O(1) space
    public int maxProfitOptimized(int[] prices) {
        int n = prices.length;
        if (n <= 1)
            return 0;

        int hold = -prices[0]; // Hold stock
        int sold = 0; // Just sold (cooldown)
        int rest = 0; // No stock, can buy

        for (int i = 1; i < n; i++) {
            int prevHold = hold;
            int prevSold = sold;
            int prevRest = rest;

            hold = Math.max(prevHold, prevRest - prices[i]);
            sold = prevHold + prices[i];
            rest = Math.max(prevRest, prevSold);
        }

        return Math.max(sold, rest);
    }

    // Approach 3: Buy/Sell DP - O(n) time, O(n) space
    public int maxProfitBuySell(int[] prices) {
        int n = prices.length;
        if (n <= 1)
            return 0;

        int[] buy = new int[n];
        int[] sell = new int[n];

        buy[0] = -prices[0];
        sell[0] = 0;

        for (int i = 1; i < n; i++) {
            buy[i] = Math.max(buy[i - 1], (i >= 2 ? sell[i - 2] : 0) - prices[i]);
            sell[i] = Math.max(sell[i - 1], buy[i - 1] + prices[i]);
        }

        return sell[n - 1];
    }

    // Approach 4: Memoization - O(n) time, O(n) space
    public int maxProfitMemo(int[] prices) {
        Integer[][] memo = new Integer[prices.length][2];
        return maxProfitMemoHelper(prices, 0, 0, memo);
    }

    private int maxProfitMemoHelper(int[] prices, int day, int holding, Integer[][] memo) {
        if (day >= prices.length)
            return 0;

        if (memo[day][holding] != null)
            return memo[day][holding];

        int result = maxProfitMemoHelper(prices, day + 1, holding, memo); // Do nothing

        if (holding == 1) {
            // Sell stock
            result = Math.max(result, prices[day] + maxProfitMemoHelper(prices, day + 2, 0, memo));
        } else {
            // Buy stock
            result = Math.max(result, -prices[day] + maxProfitMemoHelper(prices, day + 1, 1, memo));
        }

        memo[day][holding] = result;
        return result;
    }

    // Approach 5: Mathematical Approach - O(n) time, O(1) space
    public int maxProfitMath(int[] prices) {
        int n = prices.length;
        if (n <= 1)
            return 0;

        // buy[i] = max profit when we buy on day i
        // sell[i] = max profit when we sell on day i
        int buy1 = -prices[0], buy2 = -prices[0];
        int sell1 = 0, sell2 = 0;

        for (int i = 1; i < n; i++) {
            buy1 = Math.max(buy2, sell2 - prices[i]);
            sell1 = Math.max(sell2, buy2 + prices[i]);

            buy2 = buy1;
            sell2 = sell1;
        }

        return sell1;
    }

    public static void main(String[] args) {
        BestTimeToBuyAndSellStockWithCooldown solution = new BestTimeToBuyAndSellStockWithCooldown();

        System.out.println("=== Best Time to Buy and Sell Stock with Cooldown Test Cases ===");

        // Test Case 1: Example from problem
        int[] prices1 = { 1, 2, 3, 0, 2 };
        System.out.println("Test 1 - Prices: " + Arrays.toString(prices1));
        System.out.println("State Machine: " + solution.maxProfitStateMachine(prices1));
        System.out.println("Optimized: " + solution.maxProfitOptimized(prices1));
        System.out.println("Buy/Sell DP: " + solution.maxProfitBuySell(prices1));
        System.out.println("Memoization: " + solution.maxProfitMemo(prices1));
        System.out.println("Mathematical: " + solution.maxProfitMath(prices1));
        System.out.println("Expected: 3\n");

        // Test Case 2: No profit
        int[] prices2 = { 1 };
        System.out.println("Test 2 - Prices: " + Arrays.toString(prices2));
        System.out.println("Optimized: " + solution.maxProfitOptimized(prices2));
        System.out.println("Expected: 0\n");

        // Test Case 3: Decreasing prices
        int[] prices3 = { 5, 4, 3, 2, 1 };
        System.out.println("Test 3 - Prices: " + Arrays.toString(prices3));
        System.out.println("Optimized: " + solution.maxProfitOptimized(prices3));
        System.out.println("Expected: 0\n");

        // Test Case 4: Two transactions with cooldown
        int[] prices4 = { 2, 1, 4, 9, 1, 3 };
        System.out.println("Test 4 - Prices: " + Arrays.toString(prices4));
        System.out.println("Optimized: " + solution.maxProfitOptimized(prices4));
        System.out.println("Expected: 10\n");

        performanceTest();
    }

    private static void performanceTest() {
        BestTimeToBuyAndSellStockWithCooldown solution = new BestTimeToBuyAndSellStockWithCooldown();

        int[] largePrices = new int[5000];
        for (int i = 0; i < largePrices.length; i++) {
            largePrices[i] = (int) (Math.random() * 1000);
        }

        System.out.println("=== Performance Test (Array size: " + largePrices.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxProfitStateMachine(largePrices);
        long end = System.nanoTime();
        System.out.println("State Machine: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxProfitOptimized(largePrices);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxProfitBuySell(largePrices);
        end = System.nanoTime();
        System.out.println("Buy/Sell DP: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
