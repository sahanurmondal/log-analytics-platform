package dp.easy;

/**
 * LeetCode 121: Best Time to Buy and Sell Stock
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 *
 * Description:
 * You are given an array prices where prices[i] is the price of a given stock
 * on the ith day.
 * You want to maximize your profit by choosing a single day to buy one stock
 * and choosing a different day in the future to sell that stock.
 * Return the maximum profit you can achieve from this transaction. If you
 * cannot achieve any profit, return 0.
 *
 * Constraints:
 * - 1 <= prices.length <= 10^5
 * - 0 <= prices[i] <= 10^4
 *
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Easy
 */
public class BestTimeToBuyAndSellStock {

    // Approach 1: One Pass - O(n) time, O(1) space
    public int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price;
            } else if (price - minPrice > maxProfit) {
                maxProfit = price - minPrice;
            }
        }

        return maxProfit;
    }

    // Approach 2: DP - O(n) time, O(n) space
    public int maxProfitDP(int[] prices) {
        int n = prices.length;
        int[] buy = new int[n];
        int[] sell = new int[n];

        buy[0] = -prices[0];
        sell[0] = 0;

        for (int i = 1; i < n; i++) {
            buy[i] = Math.max(buy[i - 1], -prices[i]);
            sell[i] = Math.max(sell[i - 1], buy[i - 1] + prices[i]);
        }

        return sell[n - 1];
    }

    public static void main(String[] args) {
        BestTimeToBuyAndSellStock solution = new BestTimeToBuyAndSellStock();

        System.out.println("=== Best Time to Buy and Sell Stock Test Cases ===");

        int[] prices1 = { 7, 1, 5, 3, 6, 4 };
        System.out.println("Prices: " + java.util.Arrays.toString(prices1));
        System.out.println("Max Profit: " + solution.maxProfit(prices1));
        System.out.println("Expected: 5\n");
    }
}
