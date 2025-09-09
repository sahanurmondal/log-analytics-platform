package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 188: Best Time to Buy and Sell Stock IV
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/
 *
 * Description:
 * You are given an integer array prices where prices[i] is the price of a given
 * stock on the ith day,
 * and an integer k. Find the maximum profit you can achieve. You may complete
 * at most k transactions.
 * Note: You may not engage in multiple transactions simultaneously (i.e., you
 * must sell the stock before you buy again).
 *
 * Constraints:
 * - 0 <= k <= 100
 * - 0 <= prices.length <= 1000
 * - 0 <= prices[i] <= 1000
 *
 * Follow-up:
 * - What if k is very large?
 * - Can you optimize space complexity?
 * 
 * Company Tags: Bloomberg, Amazon, Google, Microsoft, Facebook
 * Difficulty: Hard (but in medium folder per prompt)
 */
public class BestTimeToBuyAndSellStockIV {

    // Approach 1: 3D DP - O(n*k) time, O(n*k) space
    public int maxProfitDP3D(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k == 0)
            return 0;

        // If k >= n/2, we can make as many transactions as we want
        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // dp[i][j][0] = max profit on day i with at most j transactions, not holding
        // stock
        // dp[i][j][1] = max profit on day i with at most j transactions, holding stock
        int[][][] dp = new int[n][k + 1][2];

        // Initialize
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= k; j++) {
                dp[i][j][0] = 0;
                dp[i][j][1] = -prices[0];
            }
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j <= k; j++) {
                dp[i][j][0] = Math.max(dp[i - 1][j][0], dp[i - 1][j][1] + prices[i]);
                dp[i][j][1] = Math.max(dp[i - 1][j][1], dp[i - 1][j - 1][0] - prices[i]);
            }
        }

        return dp[n - 1][k][0];
    }

    // Approach 2: 2D DP Space Optimized - O(n*k) time, O(k) space
    public int maxProfitDP2D(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k == 0)
            return 0;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // buy[j] = max profit with at most j transactions, currently holding stock
        // sell[j] = max profit with at most j transactions, not holding stock
        int[] buy = new int[k + 1];
        int[] sell = new int[k + 1];

        Arrays.fill(buy, -prices[0]);
        Arrays.fill(sell, 0);

        for (int i = 1; i < n; i++) {
            for (int j = k; j >= 1; j--) {
                sell[j] = Math.max(sell[j], buy[j] + prices[i]);
                buy[j] = Math.max(buy[j], sell[j - 1] - prices[i]);
            }
        }

        return sell[k];
    }

    // Approach 3: State Machine Approach - O(n*k) time, O(k) space
    public int maxProfitStateMachine(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k == 0)
            return 0;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // For each transaction, we have two states: bought and sold
        int[] bought = new int[k + 1];
        int[] sold = new int[k + 1];

        Arrays.fill(bought, Integer.MIN_VALUE);
        Arrays.fill(sold, 0);

        for (int price : prices) {
            for (int j = k; j >= 1; j--) {
                sold[j] = Math.max(sold[j], bought[j] + price);
                bought[j] = Math.max(bought[j], sold[j - 1] - price);
            }
        }

        return sold[k];
    }

    // Approach 4: Memoization - O(n*k) time, O(n*k) space
    public int maxProfitMemo(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k == 0)
            return 0;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        Integer[][][] memo = new Integer[n][k + 1][2];
        return maxProfitMemoHelper(prices, 0, k, 0, memo);
    }

    private int maxProfitMemoHelper(int[] prices, int day, int transactions, int holding, Integer[][][] memo) {
        if (day >= prices.length || transactions == 0)
            return 0;

        if (memo[day][transactions][holding] != null) {
            return memo[day][transactions][holding];
        }

        int doNothing = maxProfitMemoHelper(prices, day + 1, transactions, holding, memo);
        int doSomething = 0;

        if (holding == 1) {
            // Sell
            doSomething = prices[day] + maxProfitMemoHelper(prices, day + 1, transactions - 1, 0, memo);
        } else {
            // Buy
            doSomething = -prices[day] + maxProfitMemoHelper(prices, day + 1, transactions, 1, memo);
        }

        memo[day][transactions][holding] = Math.max(doNothing, doSomething);
        return memo[day][transactions][holding];
    }

    // Approach 5: Unlimited Transactions (when k is large) - O(n) time, O(1) space
    private int maxProfitUnlimited(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    // Bonus: Get transaction details
    public int[] maxProfitWithTransactions(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k == 0)
            return new int[] { 0 };

        if (k >= n / 2) {
            java.util.List<Integer> transactions = new java.util.ArrayList<>();
            int profit = 0;
            for (int i = 1; i < n; i++) {
                if (prices[i] > prices[i - 1]) {
                    profit += prices[i] - prices[i - 1];
                    transactions.add(i - 1); // buy day
                    transactions.add(i); // sell day
                }
            }
            int[] result = new int[transactions.size() + 1];
            result[0] = profit;
            for (int i = 0; i < transactions.size(); i++) {
                result[i + 1] = transactions.get(i);
            }
            return result;
        }

        int profit = maxProfitDP2D(k, prices);
        return new int[] { profit }; // Simplified for space
    }

    public static void main(String[] args) {
        BestTimeToBuyAndSellStockIV solution = new BestTimeToBuyAndSellStockIV();

        System.out.println("=== Best Time to Buy and Sell Stock IV Test Cases ===");

        // Test Case 1: Example from problem
        int k1 = 2;
        int[] prices1 = { 2, 4, 1 };
        System.out.println("Test 1 - k=" + k1 + ", prices: " + Arrays.toString(prices1));
        System.out.println("3D DP: " + solution.maxProfitDP3D(k1, prices1));
        System.out.println("2D DP: " + solution.maxProfitDP2D(k1, prices1));
        System.out.println("State Machine: " + solution.maxProfitStateMachine(k1, prices1));
        System.out.println("Memoization: " + solution.maxProfitMemo(k1, prices1));
        System.out.println("Expected: 2\n");

        // Test Case 2: Another example
        int k2 = 2;
        int[] prices2 = { 3, 2, 6, 5, 0, 3 };
        System.out.println("Test 2 - k=" + k2 + ", prices: " + Arrays.toString(prices2));
        System.out.println("State Machine: " + solution.maxProfitStateMachine(k2, prices2));
        int[] transactionResult = solution.maxProfitWithTransactions(k2, prices2);
        System.out.println("With Transactions: " + Arrays.toString(transactionResult));
        System.out.println("Expected: 7\n");

        // Test Case 3: k is large
        int k3 = 100;
        int[] prices3 = { 1, 2, 3, 4, 5 };
        System.out.println("Test 3 - k=" + k3 + ", prices: " + Arrays.toString(prices3));
        System.out.println("State Machine: " + solution.maxProfitStateMachine(k3, prices3));
        System.out.println("Expected: 4\n");

        // Test Case 4: No profit possible
        int k4 = 2;
        int[] prices4 = { 3, 2, 1 };
        System.out.println("Test 4 - k=" + k4 + ", prices: " + Arrays.toString(prices4));
        System.out.println("State Machine: " + solution.maxProfitStateMachine(k4, prices4));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void performanceTest() {
        BestTimeToBuyAndSellStockIV solution = new BestTimeToBuyAndSellStockIV();

        int[] largePrices = new int[1000];
        for (int i = 0; i < largePrices.length; i++) {
            largePrices[i] = (int) (Math.random() * 1000);
        }
        int k = 50;

        System.out.println("=== Performance Test (Array size: " + largePrices.length + ", k=" + k + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxProfitDP2D(k, largePrices);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxProfitStateMachine(k, largePrices);
        end = System.nanoTime();
        System.out.println("State Machine: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxProfitMemo(k, largePrices);
        end = System.nanoTime();
        System.out.println("Memoization: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
