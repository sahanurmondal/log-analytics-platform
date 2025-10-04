package greedy.hard;

/**
 * LeetCode 188: Best Time to Buy and Sell Stock IV
 * URL: https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/
 * Difficulty: Hard
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 150+ interviews)
 *
 * Description:
 * You are given an integer array prices where prices[i] is the price of a given
 * stock on the ith day,
 * and an integer k. Find the maximum profit you can achieve. You may complete
 * at most k transactions.
 * Note: You may not engage in multiple transactions simultaneously (buy before
 * sell).
 *
 * Example:
 * Input: k = 2, prices = [2,4,1]
 * Output: 2
 * Explanation: Buy on day 1 (price = 2) and sell on day 2 (price = 4), profit =
 * 2.
 *
 * Constraints:
 * - 1 <= k <= 100
 * - 1 <= prices.length <= 1000
 * - 0 <= prices[i] <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you optimize space complexity?
 * 2. What if k is very large (unlimited transactions)?
 * 3. How would you handle transaction fees?
 * 4. Can you solve with different approaches?
 */
public class MaximumProfitWithKTransactions {

    // Approach 1: Dynamic Programming - O(n*k) time, O(n*k) space
    public int maxProfit(int k, int[] prices) {
        if (prices == null || prices.length <= 1 || k == 0) {
            return 0;
        }

        int n = prices.length;

        // If k >= n/2, we can do unlimited transactions
        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // dp[i][j] = maximum profit after at most i transactions by day j
        int[][] dp = new int[k + 1][n];

        for (int i = 1; i <= k; i++) {
            int maxDiff = -prices[0]; // max(dp[i-1][m] - prices[m]) for m < j
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.max(dp[i][j - 1], prices[j] + maxDiff);
                maxDiff = Math.max(maxDiff, dp[i - 1][j] - prices[j]);
            }
        }

        return dp[k][n - 1];
    }

    // Approach 2: Space Optimized DP - O(n*k) time, O(k) space
    public int maxProfitSpaceOptimized(int k, int[] prices) {
        if (prices == null || prices.length <= 1 || k == 0) {
            return 0;
        }

        int n = prices.length;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // Only need current and previous transaction states
        int[] buy = new int[k + 1]; // Maximum profit after buying in transaction i
        int[] sell = new int[k + 1]; // Maximum profit after selling in transaction i

        // Initialize buy states (we start with spending money)
        for (int i = 0; i <= k; i++) {
            buy[i] = -prices[0];
        }

        for (int i = 1; i < n; i++) {
            for (int j = k; j >= 1; j--) {
                sell[j] = Math.max(sell[j], buy[j] + prices[i]);
                buy[j] = Math.max(buy[j], sell[j - 1] - prices[i]);
            }
        }

        return sell[k];
    }

    // Approach 3: State Machine DP - O(n*k) time, O(k) space
    public int maxProfitStateMachine(int k, int[] prices) {
        if (prices == null || prices.length <= 1 || k == 0) {
            return 0;
        }

        int n = prices.length;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // hold[i] = profit after buying in transaction i
        // sold[i] = profit after selling in transaction i
        int[] hold = new int[k + 1];
        int[] sold = new int[k + 1];

        // Initialize - buying costs money
        for (int i = 1; i <= k; i++) {
            hold[i] = -prices[0];
        }

        for (int price : prices) {
            for (int j = k; j >= 1; j--) {
                sold[j] = Math.max(sold[j], hold[j] + price);
                hold[j] = Math.max(hold[j], sold[j - 1] - price);
            }
        }

        return sold[k];
    }

    // Helper: Unlimited transactions
    private int maxProfitUnlimited(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    // Follow-up: With transaction fee
    public int maxProfitWithFee(int k, int[] prices, int fee) {
        if (prices == null || prices.length <= 1 || k == 0) {
            return 0;
        }

        int n = prices.length;

        if (k >= n / 2) {
            return maxProfitUnlimitedWithFee(prices, fee);
        }

        int[] buy = new int[k + 1];
        int[] sell = new int[k + 1];

        for (int i = 0; i <= k; i++) {
            buy[i] = -prices[0];
        }

        for (int i = 1; i < n; i++) {
            for (int j = k; j >= 1; j--) {
                sell[j] = Math.max(sell[j], buy[j] + prices[i] - fee);
                buy[j] = Math.max(buy[j], sell[j - 1] - prices[i]);
            }
        }

        return sell[k];
    }

    private int maxProfitUnlimitedWithFee(int[] prices, int fee) {
        int profit = 0;
        int minPrice = prices[0];

        for (int i = 1; i < prices.length; i++) {
            if (prices[i] < minPrice) {
                minPrice = prices[i];
            } else if (prices[i] - minPrice > fee) {
                profit += prices[i] - minPrice - fee;
                minPrice = prices[i] - fee; // Reset for next transaction
            }
        }

        return profit;
    }

    public static void main(String[] args) {
        MaximumProfitWithKTransactions solution = new MaximumProfitWithKTransactions();

        // Test Case 1: Basic case
        System.out.println("=== Test Case 1: Basic Case ===");
        int[] prices1 = { 2, 4, 1 };
        System.out.println("Expected: 2, Got: " + solution.maxProfit(2, prices1)); // 2

        // Test Case 2: Multiple transactions
        System.out.println("\n=== Test Case 2: Multiple Transactions ===");
        int[] prices2 = { 3, 2, 6, 5, 0, 3 };
        System.out.println("Expected: 7, Got: " + solution.maxProfit(2, prices2)); // 7

        // Test Case 3: k = 0
        System.out.println("\n=== Test Case 3: Zero transactions ===");
        int[] prices3 = { 1, 2, 3, 4, 5 };
        System.out.println("Expected: 0, Got: " + solution.maxProfit(0, prices3)); // 0

        // Test Case 4: Single price
        System.out.println("\n=== Test Case 4: Single Price ===");
        int[] prices4 = { 10 };
        System.out.println("Expected: 0, Got: " + solution.maxProfit(2, prices4)); // 0

        // Test Case 5: Decreasing prices
        System.out.println("\n=== Test Case 5: Decreasing Prices ===");
        int[] prices5 = { 5, 4, 3, 2, 1 };
        System.out.println("Expected: 0, Got: " + solution.maxProfit(2, prices5)); // 0

        // Test Case 6: Large k (unlimited transactions)
        System.out.println("\n=== Test Case 6: Large k ===");
        int[] prices6 = { 1, 5, 3, 6, 4 };
        System.out.println("Expected: 7, Got: " + solution.maxProfit(100, prices6)); // 7

        // Test Case 7: Compare approaches
        System.out.println("\n=== Test Case 7: Approach Comparison ===");
        compareApproaches(solution, 2, prices2);

        // Test Case 8: Single transaction
        System.out.println("\n=== Test Case 8: Single Transaction ===");
        int[] prices8 = { 7, 1, 5, 3, 6, 4 };
        System.out.println("Expected: 5, Got: " + solution.maxProfit(1, prices8)); // 5

        // Test Case 9: Performance test
        System.out.println("\n=== Test Case 9: Performance Test ===");
        performanceTest(solution);

        // Test Case 10: With transaction fee
        System.out.println("\n=== Test Case 10: With Transaction Fee ===");
        int[] prices10 = { 1, 3, 2, 8, 4, 9 };
        System.out.println("Expected: 5, Got: " + solution.maxProfitWithFee(2, prices10, 2)); // 5

        // Test Case 11: Edge case - same prices
        System.out.println("\n=== Test Case 11: Same Prices ===");
        int[] prices11 = { 5, 5, 5, 5, 5 };
        System.out.println("Expected: 0, Got: " + solution.maxProfit(2, prices11)); // 0

        // Test Case 12: Validation test
        System.out.println("\n=== Test Case 12: Validation Test ===");
        validateAllApproaches(solution);

        // Test Case 13: Stress test
        System.out.println("\n=== Test Case 13: Stress Test ===");
        stressTest(solution);

        // Test Case 14: Large input
        System.out.println("\n=== Test Case 14: Large Input ===");
        largeInputTest(solution);

        // Test Case 15: Optimal transaction timing
        System.out.println("\n=== Test Case 15: Optimal Timing ===");
        int[] prices15 = { 2, 1, 2, 0, 1 };
        System.out.println("Expected: 2, Got: " + solution.maxProfit(2, prices15)); // 2
    }

    private static void compareApproaches(MaximumProfitWithKTransactions solution, int k, int[] prices) {
        int result1 = solution.maxProfit(k, prices);
        int result2 = solution.maxProfitSpaceOptimized(k, prices);
        int result3 = solution.maxProfitStateMachine(k, prices);

        System.out.println("Standard DP: " + result1);
        System.out.println("Space Optimized: " + result2);
        System.out.println("State Machine: " + result3);
        System.out.println("All consistent: " + (result1 == result2 && result2 == result3));
    }

    private static void performanceTest(MaximumProfitWithKTransactions solution) {
        int[] largePrices = new int[1000];
        for (int i = 0; i < 1000; i++) {
            largePrices[i] = i % 100;
        }

        long start = System.currentTimeMillis();
        int result = solution.maxProfit(50, largePrices);
        long end = System.currentTimeMillis();

        System.out.println("Performance test result: " + result +
                " profit in " + (end - start) + "ms");
    }

    private static void validateAllApproaches(MaximumProfitWithKTransactions solution) {
        int[] testPrices = { 1, 2, 3, 0, 2 };
        int k = 3;

        int result1 = solution.maxProfit(k, testPrices);
        int result2 = solution.maxProfitSpaceOptimized(k, testPrices);
        int result3 = solution.maxProfitStateMachine(k, testPrices);

        boolean allConsistent = result1 == result2 && result2 == result3;
        System.out.println("Validation result: " + result1 + ", All consistent: " + allConsistent);
    }

    private static void stressTest(MaximumProfitWithKTransactions solution) {
        int[] stressPrices = new int[100];
        for (int i = 0; i < 100; i++) {
            stressPrices[i] = (i * 17) % 50;
        }

        int result = solution.maxProfit(25, stressPrices);
        System.out.println("Stress test completed with result: " + result);
    }

    private static void largeInputTest(MaximumProfitWithKTransactions solution) {
        int[] largeInput = new int[1000];
        for (int i = 0; i < 1000; i++) {
            largeInput[i] = i % 200;
        }

        long start = System.currentTimeMillis();
        int result = solution.maxProfit(100, largeInput);
        long end = System.currentTimeMillis();

        System.out.println("Large input test: " + result +
                " profit processed in " + (end - start) + "ms");
    }
}
