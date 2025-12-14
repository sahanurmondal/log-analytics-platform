package arrays.easy;

/**
 * LeetCode 122: Best Time to Buy and Sell Stock II
 *
 * You are given an integer array prices where prices[i] is the price of a given stock on the ith day.
 * On each day, you may decide to buy and/or sell the stock. You can hold at most one share of the
 * stock at any time. However, you can buy it then immediately sell it on the same day.
 *
 * Find and return the maximum profit you can achieve.
 *
 * Example 1:
 * Input: prices = [7,1,5,3,6,4]
 * Output: 7
 * Explanation: Buy on day 2 (price = 1) and sell on day 3 (price = 5), profit = 5
 *              Then buy on day 4 (price = 3) and sell on day 5 (price = 6), profit = 3
 *              Total profit = 5 + 3 = 8
 *
 * Example 2:
 * Input: prices = [1,2,3,4,5]
 * Output: 4
 * Explanation: Buy on day 1 (price = 1) and sell on day 5 (price = 5), profit = 4
 *              Total profit = 4
 */
public class BestTimeToBuyAndSellStockII {

    /**
     * Solution: Greedy - Buy every valley and sell every peak
     * Time: O(n), Space: O(1)
     *
     * Key insight: Since we can buy and sell multiple times, we capture every upward movement
     * If price[i] < price[i+1], we buy at i and sell at i+1
     * This is equivalent to summing all positive differences
     */
    public int maxProfit(int[] prices) {
        int profit = 0;

        for (int i = 1; i < prices.length; i++) {
            // If price goes up, we capture the gain
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }

        return profit;
    }

    /**
     * Alternative: Explicit buy-sell tracking
     * Time: O(n), Space: O(1)
     *
     * Track whether we're holding stock or not
     */
    public int maxProfitV2(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }

        int profit = 0;
        boolean holding = false;
        int buyPrice = 0;

        for (int i = 0; i < prices.length; i++) {
            if (!holding && i + 1 < prices.length && prices[i] < prices[i + 1]) {
                // Buy: next day price is higher
                holding = true;
                buyPrice = prices[i];
            } else if (holding && (i + 1 >= prices.length || prices[i] >= prices[i + 1])) {
                // Sell: no next day or next day price is not higher
                profit += prices[i] - buyPrice;
                holding = false;
            }
        }

        return profit;
    }

    /**
     * Dynamic Programming approach
     * Time: O(n), Space: O(1)
     *
     * States:
     * - hold: profit if holding stock
     * - sold: profit if not holding stock
     */
    public int maxProfitDP(int[] prices) {
        if (prices == null || prices.length < 2) {
            return 0;
        }

        int hold = -prices[0];  // Start by buying first stock
        int sold = 0;           // Start by not buying

        for (int i = 1; i < prices.length; i++) {
            // Either keep holding or sell
            hold = Math.max(hold, sold - prices[i]);

            // Either keep sold state or sell current holding
            sold = Math.max(sold, hold + prices[i]);
        }

        return sold;  // We must be in "sold" state at the end for max profit
    }

    public static void main(String[] args) {
        BestTimeToBuyAndSellStockII solution = new BestTimeToBuyAndSellStockII();

        // Test case 1
        int[] prices1 = {7, 1, 5, 3, 6, 4};
        System.out.println("Input: [7,1,5,3,6,4]");
        System.out.println("Output: " + solution.maxProfit(prices1)); // 7

        // Test case 2
        int[] prices2 = {1, 2, 3, 4, 5};
        System.out.println("\nInput: [1,2,3,4,5]");
        System.out.println("Output: " + solution.maxProfit(prices2)); // 4

        // Test case 3
        int[] prices3 = {7, 6, 4, 3, 1};
        System.out.println("\nInput: [7,6,4,3,1]");
        System.out.println("Output: " + solution.maxProfit(prices3)); // 0

        // Test case 4
        int[] prices4 = {2, 1, 2, 0, 1};
        System.out.println("\nInput: [2,1,2,0,1]");
        System.out.println("Output: " + solution.maxProfit(prices4)); // 1
    }
}

