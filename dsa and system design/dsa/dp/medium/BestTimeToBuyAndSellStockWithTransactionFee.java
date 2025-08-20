package dp.medium;

import java.util.Arrays;

/**
 * LeetCode 714: Best Time to Buy and Sell Stock with Transaction Fee
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-with-transaction-fee/
 *
 * Description:
 * You are given an array prices where prices[i] is the price of a given stock
 * on the ith day,
 * and an integer fee representing a transaction fee.
 * Find the maximum profit you can achieve. You may complete as many
 * transactions as you like,
 * but you need to pay the transaction fee for each transaction.
 * Note: You may not engage in multiple transactions simultaneously (i.e., you
 * must sell the stock before you buy again).
 *
 * Constraints:
 * - 1 <= prices.length <= 5 * 10^4
 * - 1 <= prices[i] < 5 * 10^4
 * - 0 <= fee < 5 * 10^4
 *
 * Follow-up:
 * - What if fee is applied only on buy or only on sell?
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg
 * Difficulty: Medium
 */
public class BestTimeToBuyAndSellStockWithTransactionFee {

    // Approach 1: State Machine DP - O(n) time, O(n) space
    public int maxProfitStateMachine(int[] prices, int fee) {
        int n = prices.length;
        if (n <= 1)
            return 0;

        // buy[i] = max profit on day i when holding stock
        // sell[i] = max profit on day i when not holding stock
        int[] buy = new int[n];
        int[] sell = new int[n];

        buy[0] = -prices[0];
        sell[0] = 0;

        for (int i = 1; i < n; i++) {
            buy[i] = Math.max(buy[i - 1], sell[i - 1] - prices[i]);
            sell[i] = Math.max(sell[i - 1], buy[i - 1] + prices[i] - fee);
        }

        return sell[n - 1];
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int maxProfitOptimized(int[] prices, int fee) {
        int n = prices.length;
        if (n <= 1)
            return 0;

        int buy = -prices[0];
        int sell = 0;

        for (int i = 1; i < n; i++) {
            int newBuy = Math.max(buy, sell - prices[i]);
            int newSell = Math.max(sell, buy + prices[i] - fee);

            buy = newBuy;
            sell = newSell;
        }

        return sell;
    }

    // Approach 3: Greedy Approach - O(n) time, O(1) space
    public int maxProfitGreedy(int[] prices, int fee) {
        int profit = 0;
        int minPrice = prices[0];

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < minPrice) {
                minPrice = prices[i];
            } else if (prices[i] > minPrice + fee) {
                profit += prices[i] - minPrice - fee;
                minPrice = prices[i] - fee; // Keep some profit in case of consecutive gains
            }
        }

        return profit;
    }

    // Approach 4: Memoization - O(n) time, O(n) space
    public int maxProfitMemo(int[] prices, int fee) {
        Integer[][] memo = new Integer[prices.length][2];
        return maxProfitMemoHelper(prices, 0, 0, fee, memo);
    }

    private int maxProfitMemoHelper(int[] prices, int day, int holding, int fee, Integer[][] memo) {
        if (day >= prices.length)
            return 0;

        if (memo[day][holding] != null)
            return memo[day][holding];

        int result = maxProfitMemoHelper(prices, day + 1, holding, fee, memo); // Do nothing

        if (holding == 1) {
            // Sell stock
            result = Math.max(result, prices[day] - fee + maxProfitMemoHelper(prices, day + 1, 0, fee, memo));
        } else {
            // Buy stock
            result = Math.max(result, -prices[day] + maxProfitMemoHelper(prices, day + 1, 1, fee, memo));
        }

        memo[day][holding] = result;
        return result;
    }

    // Approach 5: Peak Valley Approach - O(n) time, O(1) space
    public int maxProfitPeakValley(int[] prices, int fee) {
        int profit = 0;
        int i = 0;
        int n = prices.length;

        while (i < n - 1) {
            // Find valley
            while (i < n - 1 && prices[i + 1] <= prices[i]) {
                i++;
            }

            if (i == n - 1)
                break;

            int valley = prices[i];

            // Find peak
            while (i < n - 1 && prices[i + 1] > prices[i]) {
                i++;
            }

            int peak = prices[i];

            // Add profit if transaction is profitable
            if (peak - valley > fee) {
                profit += peak - valley - fee;
            }
        }

        return profit;
    }

    public static void main(String[] args) {
        BestTimeToBuyAndSellStockWithTransactionFee solution = new BestTimeToBuyAndSellStockWithTransactionFee();

        System.out.println("=== Best Time to Buy and Sell Stock with Transaction Fee Test Cases ===");

        // Test Case 1: Example from problem
        int[] prices1 = { 1, 3, 2, 8, 4, 9 };
        int fee1 = 2;
        System.out.println("Test 1 - Prices: " + Arrays.toString(prices1) + ", Fee: " + fee1);
        System.out.println("State Machine: " + solution.maxProfitStateMachine(prices1, fee1));
        System.out.println("Optimized: " + solution.maxProfitOptimized(prices1, fee1));
        System.out.println("Greedy: " + solution.maxProfitGreedy(prices1, fee1));
        System.out.println("Memoization: " + solution.maxProfitMemo(prices1, fee1));
        System.out.println("Peak Valley: " + solution.maxProfitPeakValley(prices1, fee1));
        System.out.println("Expected: 8\n");

        // Test Case 2: High fee
        int[] prices2 = { 1, 3, 7, 5, 10, 3 };
        int fee2 = 3;
        System.out.println("Test 2 - Prices: " + Arrays.toString(prices2) + ", Fee: " + fee2);
        System.out.println("Optimized: " + solution.maxProfitOptimized(prices2, fee2));
        System.out.println("Expected: 6\n");

        // Test Case 3: No profit possible
        int[] prices3 = { 5, 4, 3, 2, 1 };
        int fee3 = 1;
        System.out.println("Test 3 - Prices: " + Arrays.toString(prices3) + ", Fee: " + fee3);
        System.out.println("Optimized: " + solution.maxProfitOptimized(prices3, fee3));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void performanceTest() {
        BestTimeToBuyAndSellStockWithTransactionFee solution = new BestTimeToBuyAndSellStockWithTransactionFee();

        int[] largePrices = new int[50000];
        for (int i = 0; i < largePrices.length; i++) {
            largePrices[i] = (int) (Math.random() * 50000) + 1;
        }
        int fee = 100;

        System.out.println("=== Performance Test (Array size: " + largePrices.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxProfitStateMachine(largePrices, fee);
        long end = System.nanoTime();
        System.out.println("State Machine: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxProfitOptimized(largePrices, fee);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxProfitGreedy(largePrices, fee);
        end = System.nanoTime();
        System.out.println("Greedy: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
