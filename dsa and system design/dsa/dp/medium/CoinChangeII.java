package dp.medium;

import java.util.*;

/**
 * LeetCode 518: Coin Change II
 * https://leetcode.com/problems/coin-change-ii/
 *
 * Description:
 * You are given an integer array coins representing coins of different
 * denominations and an integer amount representing a total amount of money.
 * Return the number of combinations that make up that amount. If that amount of
 * money cannot be made up by any combination of the coins, return 0.
 * You may assume that you have an infinite number of each kind of coin.
 * The answer is guaranteed to fit into a signed 32-bit integer.
 *
 * Constraints:
 * - 1 <= coins.length <= 300
 * - 1 <= coins[i] <= 5000
 * - All the values of coins are unique.
 * - 0 <= amount <= 5000
 *
 * Follow-up:
 * - What if we need to find all possible combinations?
 * - Can you solve it in O(amount) space?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class CoinChangeII {

    // Approach 1: 2D DP - O(coins.length * amount) time, O(coins.length * amount)
    // space
    public int change(int amount, int[] coins) {
        int n = coins.length;
        int[][] dp = new int[n + 1][amount + 1];

        // Base case: one way to make amount 0 (use no coins)
        for (int i = 0; i <= n; i++) {
            dp[i][0] = 1;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= amount; j++) {
                // Don't use current coin
                dp[i][j] = dp[i - 1][j];

                // Use current coin (if possible)
                if (j >= coins[i - 1]) {
                    dp[i][j] += dp[i][j - coins[i - 1]];
                }
            }
        }

        return dp[n][amount];
    }

    // Approach 2: 1D DP (Space Optimized) - O(coins.length * amount) time,
    // O(amount) space
    public int changeOptimized(int amount, int[] coins) {
        int[] dp = new int[amount + 1];
        dp[0] = 1; // One way to make amount 0

        // Process each coin
        for (int coin : coins) {
            for (int j = coin; j <= amount; j++) {
                dp[j] += dp[j - coin];
            }
        }

        return dp[amount];
    }

    // Approach 3: Memoization - O(coins.length * amount) time, O(coins.length *
    // amount) space
    public int changeMemo(int amount, int[] coins) {
        Integer[][] memo = new Integer[coins.length][amount + 1];
        return changeMemoHelper(amount, coins, 0, memo);
    }

    private int changeMemoHelper(int amount, int[] coins, int index, Integer[][] memo) {
        if (amount == 0)
            return 1;
        if (index >= coins.length || amount < 0)
            return 0;

        if (memo[index][amount] != null)
            return memo[index][amount];

        // Don't use current coin
        int ways = changeMemoHelper(amount, coins, index + 1, memo);

        // Use current coin
        ways += changeMemoHelper(amount - coins[index], coins, index, memo);

        memo[index][amount] = ways;
        return ways;
    }

    // Approach 4: BFS Approach - O(amount * coins.length) time, O(amount) space
    public int changeBFS(int amount, int[] coins) {
        if (amount == 0)
            return 1;

        Map<Integer, Integer> ways = new HashMap<>();
        Queue<Integer> queue = new LinkedList<>();

        queue.offer(0);
        ways.put(0, 1);

        Arrays.sort(coins);

        while (!queue.isEmpty()) {
            int current = queue.poll();

            for (int coin : coins) {
                int next = current + coin;
                if (next > amount)
                    break;

                ways.put(next, ways.getOrDefault(next, 0) + ways.get(current));

                if (!ways.containsKey(next) || ways.get(next) == ways.get(current)) {
                    queue.offer(next);
                }
            }
        }

        return ways.getOrDefault(amount, 0);
    }

    // Approach 5: Get All Combinations - O(exponential) time, O(exponential) space
    public List<List<Integer>> getAllCombinations(int amount, int[] coins) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> current = new ArrayList<>();
        getAllCombinationsHelper(amount, coins, 0, current, result);
        return result;
    }

    private void getAllCombinationsHelper(int amount, int[] coins, int index,
            List<Integer> current, List<List<Integer>> result) {
        if (amount == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        if (index >= coins.length || amount < 0)
            return;

        // Don't use current coin
        getAllCombinationsHelper(amount, coins, index + 1, current, result);

        // Use current coin (can use multiple times)
        current.add(coins[index]);
        getAllCombinationsHelper(amount - coins[index], coins, index, current, result);
        current.remove(current.size() - 1);
    }

    public static void main(String[] args) {
        CoinChangeII solution = new CoinChangeII();

        System.out.println("=== Coin Change II Test Cases ===");

        // Test Case 1: Example from problem
        int amount1 = 5;
        int[] coins1 = { 1, 2, 5 };
        System.out.println("Test 1 - amount: " + amount1 + ", coins: " + Arrays.toString(coins1));
        System.out.println("2D DP: " + solution.change(amount1, coins1));
        System.out.println("1D DP: " + solution.changeOptimized(amount1, coins1));
        System.out.println("Memoization: " + solution.changeMemo(amount1, coins1));
        System.out.println("BFS: " + solution.changeBFS(amount1, coins1));

        List<List<Integer>> combinations1 = solution.getAllCombinations(amount1, coins1);
        System.out.println("All combinations (" + combinations1.size() + " total):");
        for (List<Integer> combo : combinations1) {
            System.out.println("  " + combo);
        }
        System.out.println("Expected: 4\n");

        // Test Case 2: No solution
        int amount2 = 3;
        int[] coins2 = { 2 };
        System.out.println("Test 2 - amount: " + amount2 + ", coins: " + Arrays.toString(coins2));
        System.out.println("1D DP: " + solution.changeOptimized(amount2, coins2));
        System.out.println("Expected: 0\n");

        // Test Case 3: Amount is 0
        int amount3 = 0;
        int[] coins3 = { 1, 2, 3 };
        System.out.println("Test 3 - amount: " + amount3 + ", coins: " + Arrays.toString(coins3));
        System.out.println("1D DP: " + solution.changeOptimized(amount3, coins3));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        CoinChangeII solution = new CoinChangeII();

        int amount = 1000;
        int[] coins = { 1, 2, 5, 10, 20, 50, 100, 200, 500 };

        System.out.println("=== Performance Test (amount: " + amount + ", coins: " + coins.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.change(amount, coins);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.changeOptimized(amount, coins);
        end = System.nanoTime();
        System.out.println("1D DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.changeMemo(amount, coins);
        end = System.nanoTime();
        System.out.println("Memoization: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
