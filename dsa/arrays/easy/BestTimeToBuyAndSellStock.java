package arrays.easy;

/**
 * LeetCode 121: Best Time to Buy and Sell Stock
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock/
 */
public class BestTimeToBuyAndSellStock {
    // Main solution - One pass
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

    // Alternative solution - Track min and max
    public int maxProfitTrackMinMax(int[] prices) {
        if (prices.length < 2)
            return 0;

        int minPrice = prices[0];
        int maxProfit = 0;

        for (int i = 1; i < prices.length; i++) {
            maxProfit = Math.max(maxProfit, prices[i] - minPrice);
            minPrice = Math.min(minPrice, prices[i]);
        }

        return maxProfit;
    }

    // Follow-up solution - Multiple transactions allowed
    public int maxProfitMultiple(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    public static void main(String[] args) {
        BestTimeToBuyAndSellStock solution = new BestTimeToBuyAndSellStock();

        // Test Case 1: Normal case
        System.out.println(solution.maxProfit(new int[] { 7, 1, 5, 3, 6, 4 })); // Expected: 5

        // Test Case 2: Edge case - decreasing prices
        System.out.println(solution.maxProfit(new int[] { 7, 6, 4, 3, 1 })); // Expected: 0

        // Test Case 3: Corner case - single price
        System.out.println(solution.maxProfit(new int[] { 1 })); // Expected: 0

        // Test Case 4: Large input - increasing prices
        System.out.println(solution.maxProfit(new int[] { 1, 2, 3, 4, 5 })); // Expected: 4

        // Test Case 5: Minimum input - two prices
        System.out.println(solution.maxProfit(new int[] { 1, 5 })); // Expected: 4

        // Test Case 6: Special case - all same prices
        System.out.println(solution.maxProfit(new int[] { 3, 3, 3, 3 })); // Expected: 0

        // Test Case 7: Peak in middle
        System.out.println(solution.maxProfit(new int[] { 1, 5, 3, 6, 4 })); // Expected: 5

        // Test Case 8: Multiple peaks
        System.out.println(solution.maxProfit(new int[] { 2, 4, 1, 5, 3, 7 })); // Expected: 6

        // Test Case 9: Large numbers
        System.out.println(solution.maxProfit(new int[] { 10000, 1, 9999 })); // Expected: 9998

        // Test Case 10: Valley pattern
        System.out.println(solution.maxProfit(new int[] { 5, 1, 2, 1, 3 })); // Expected: 2
    }
}
