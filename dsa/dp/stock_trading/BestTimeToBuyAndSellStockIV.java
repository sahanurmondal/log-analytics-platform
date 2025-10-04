package dp.stock_trading;

import java.util.*;

/**
 * LeetCode 188: Best Time to Buy and Sell Stock IV
 * https://leetcode.com/problems/best-time-to-buy-and-sell-stock-iv/
 *
 * Description:
 * You are given an integer array prices where prices[i] is the price of a given
 * stock on the ith day, and an integer k.
 * Find the maximum profit you can achieve. You may complete at most k
 * transactions.
 * Note: You may not engage in multiple transactions simultaneously (i.e., you
 * must sell the stock before you buy again).
 *
 * Constraints:
 * - 1 <= k <= 100
 * - 1 <= prices.length <= 1000
 * - 0 <= prices[i] <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(n) space?
 * - What if we need to track the actual transactions?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class BestTimeToBuyAndSellStockIV {

    // Approach 1: 3D DP - O(n*k) time, O(n*k) space
    public int maxProfit(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k <= 0)
            return 0;

        // If k >= n/2, we can do as many transactions as we want
        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // dp[i][j][0] = max profit on day i with at most j transactions, not holding
        // stock
        // dp[i][j][1] = max profit on day i with at most j transactions, holding stock
        int[][][] dp = new int[n][k + 1][2];

        // Initialize
        for (int j = 0; j <= k; j++) {
            dp[0][j][0] = 0;
            dp[0][j][1] = -prices[0];
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j <= k; j++) {
                // Not holding stock
                dp[i][j][0] = Math.max(dp[i - 1][j][0], dp[i - 1][j][1] + prices[i]);
                // Holding stock
                dp[i][j][1] = Math.max(dp[i - 1][j][1], dp[i - 1][j - 1][0] - prices[i]);
            }
        }

        return dp[n - 1][k][0];
    }

    private int maxProfitUnlimited(int[] prices) {
        int profit = 0;
        for (int i = 1; i < prices.length; i++) {
            if (prices[i] > prices[i - 1]) {
                profit += prices[i] - prices[i - 1];
            }
        }
        return profit;
    }

    // Approach 2: Space Optimized DP - O(n*k) time, O(k) space
    public int maxProfitOptimized(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k <= 0)
            return 0;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        int[] buy = new int[k + 1];
        int[] sell = new int[k + 1];

        // Initialize
        for (int j = 0; j <= k; j++) {
            buy[j] = -prices[0];
            sell[j] = 0;
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
        int n = prices.length;
        if (n <= 1 || k <= 0)
            return 0;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        // hold[i] = max profit after buying ith stock
        // sold[i] = max profit after selling ith stock
        int[] hold = new int[k + 1];
        int[] sold = new int[k + 1];

        Arrays.fill(hold, -prices[0]);

        for (int i = 1; i < n; i++) {
            for (int j = k; j >= 1; j--) {
                sold[j] = Math.max(sold[j], hold[j] + prices[i]);
                hold[j] = Math.max(hold[j], sold[j - 1] - prices[i]);
            }
        }

        return sold[k];
    }

    // Approach 4: Get Transaction Details - O(n*k) time, O(n*k) space
    public List<int[]> getTransactionDetails(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k <= 0)
            return new ArrayList<>();

        if (k >= n / 2) {
            return getUnlimitedTransactions(prices);
        }

        int[][][] dp = new int[n][k + 1][2];
        boolean[][][] choice = new boolean[n][k + 1][2];

        // Initialize
        for (int j = 0; j <= k; j++) {
            dp[0][j][0] = 0;
            dp[0][j][1] = -prices[0];
        }

        for (int i = 1; i < n; i++) {
            for (int j = 1; j <= k; j++) {
                // Not holding stock
                if (dp[i - 1][j][0] >= dp[i - 1][j][1] + prices[i]) {
                    dp[i][j][0] = dp[i - 1][j][0];
                    choice[i][j][0] = false; // Don't sell
                } else {
                    dp[i][j][0] = dp[i - 1][j][1] + prices[i];
                    choice[i][j][0] = true; // Sell
                }

                // Holding stock
                if (dp[i - 1][j][1] >= dp[i - 1][j - 1][0] - prices[i]) {
                    dp[i][j][1] = dp[i - 1][j][1];
                    choice[i][j][1] = false; // Don't buy
                } else {
                    dp[i][j][1] = dp[i - 1][j - 1][0] - prices[i];
                    choice[i][j][1] = true; // Buy
                }
            }
        }

        // Reconstruct transactions
        List<int[]> transactions = new ArrayList<>();
        int i = n - 1, j = k, state = 0;

        while (i >= 0 && j >= 0) {
            if (state == 0) { // Not holding
                if (i == 0)
                    break;
                if (choice[i][j][0]) { // Sold at day i
                    transactions.add(0, new int[] { -1, i }); // Placeholder for buy day
                    state = 1; // Now looking for corresponding buy
                }
                i--;
            } else { // Holding
                if (choice[i][j][1]) { // Bought at day i
                    if (!transactions.isEmpty()) {
                        transactions.get(0)[0] = i; // Set buy day
                    }
                    state = 0;
                    j--;
                }
                i--;
            }
        }

        return transactions;
    }

    private List<int[]> getUnlimitedTransactions(int[] prices) {
        List<int[]> transactions = new ArrayList<>();
        int buy = -1;

        for (int i = 0; i < prices.length - 1; i++) {
            if (buy == -1 && prices[i] < prices[i + 1]) {
                buy = i;
            } else if (buy != -1 && prices[i] > prices[i + 1]) {
                transactions.add(new int[] { buy, i });
                buy = -1;
            }
        }

        if (buy != -1) {
            transactions.add(new int[] { buy, prices.length - 1 });
        }

        return transactions;
    }

    // Approach 5: Memoization - O(n*k) time, O(n*k) space
    public int maxProfitMemo(int k, int[] prices) {
        int n = prices.length;
        if (n <= 1 || k <= 0)
            return 0;

        if (k >= n / 2) {
            return maxProfitUnlimited(prices);
        }

        Integer[][][] memo = new Integer[n][k + 1][2];
        return maxProfitMemoHelper(prices, 0, k, 0, memo);
    }

    private int maxProfitMemoHelper(int[] prices, int day, int transactions, int holding, Integer[][][] memo) {
        if (day >= prices.length || transactions <= 0)
            return 0;

        if (memo[day][transactions][holding] != null) {
            return memo[day][transactions][holding];
        }

        int doNothing = maxProfitMemoHelper(prices, day + 1, transactions, holding, memo);
        int doSomething = 0;

        if (holding == 1) { // Currently holding stock
            doSomething = prices[day] + maxProfitMemoHelper(prices, day + 1, transactions - 1, 0, memo);
        } else { // Not holding stock
            doSomething = -prices[day] + maxProfitMemoHelper(prices, day + 1, transactions, 1, memo);
        }

        memo[day][transactions][holding] = Math.max(doNothing, doSomething);
        return memo[day][transactions][holding];
    }

    public static void main(String[] args) {
        BestTimeToBuyAndSellStockIV solution = new BestTimeToBuyAndSellStockIV();

        System.out.println("=== Best Time to Buy and Sell Stock IV Test Cases ===");

        // Test Case 1: Example from problem
        int k1 = 2;
        int[] prices1 = { 2, 4, 1 };
        System.out.println("Test 1 - k: " + k1 + ", prices: " + Arrays.toString(prices1));
        System.out.println("3D DP: " + solution.maxProfit(k1, prices1));
        System.out.println("Optimized: " + solution.maxProfitOptimized(k1, prices1));
        System.out.println("State Machine: " + solution.maxProfitStateMachine(k1, prices1));
        System.out.println("Memoization: " + solution.maxProfitMemo(k1, prices1));

        List<int[]> transactions1 = solution.getTransactionDetails(k1, prices1);
        System.out.println("Transactions:");
        for (int[] transaction : transactions1) {
            System.out.println("  Buy day " + transaction[0] + ", Sell day " + transaction[1]);
        }
        System.out.println("Expected: 2\n");

        // Test Case 2: More complex
        int k2 = 2;
        int[] prices2 = { 3, 2, 6, 5, 0, 3 };
        System.out.println("Test 2 - k: " + k2 + ", prices: " + Arrays.toString(prices2));
        System.out.println("3D DP: " + solution.maxProfit(k2, prices2));
        System.out.println("Expected: 7\n");

        performanceTest();
    }

    private static void performanceTest() {
        BestTimeToBuyAndSellStockIV solution = new BestTimeToBuyAndSellStockIV();

        int k = 50;
        int[] prices = new int[1000];
        for (int i = 0; i < prices.length; i++) {
            prices[i] = (int) (Math.random() * 1000);
        }

        System.out.println("=== Performance Test (k: " + k + ", prices length: " + prices.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxProfit(k, prices);
        long end = System.nanoTime();
        System.out.println("3D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxProfitOptimized(k, prices);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxProfitStateMachine(k, prices);
        end = System.nanoTime();
        System.out.println("State Machine: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
