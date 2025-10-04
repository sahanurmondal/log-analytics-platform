package dp.knapsack.unbounded;

/**
 * LeetCode 322: Coin Change
 * https://leetcode.com/problems/coin-change/
 *
 * Description:
 * Given coins of different denominations and a total amount, return the fewest
 * number of coins needed to make up that amount.
 * If that amount cannot be made up by any combination of the coins, return -1.
 *
 * Constraints:
 * - 1 <= coins.length <= 12
 * - 1 <= coins[i] <= 2^31 - 1
 * - 0 <= amount <= 10^4
 *
 * Follow-up:
 * - Can you solve it in O(amount * coins.length) time?
 * 
 * Company Tags: Amazon, Google, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class CoinChange {

    // Approach 1: Dynamic Programming (Bottom-up) - O(amount * coins.length) time,
    // O(amount) space
    public int coinChange(int[] coins, int amount) {
        if (amount == 0)
            return 0;

        // dp[i] = minimum coins needed to make amount i
        int[] dp = new int[amount + 1];
        java.util.Arrays.fill(dp, amount + 1); // Initialize with impossible value
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i) {
                    dp[i] = Math.min(dp[i], dp[i - coin] + 1);
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    // Approach 2: BFS (Level-order traversal) - O(amount * coins.length) time,
    // O(amount) space
    public int coinChangeBFS(int[] coins, int amount) {
        if (amount == 0)
            return 0;

        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        boolean[] visited = new boolean[amount + 1];

        queue.offer(amount);
        visited[amount] = true;
        int level = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            level++;

            for (int i = 0; i < size; i++) {
                int current = queue.poll();

                for (int coin : coins) {
                    int next = current - coin;

                    if (next == 0)
                        return level;

                    if (next > 0 && !visited[next]) {
                        visited[next] = true;
                        queue.offer(next);
                    }
                }
            }
        }

        return -1;
    }

    // Approach 3: DFS with Memoization - O(amount * coins.length) time, O(amount)
    // space
    public int coinChangeDFS(int[] coins, int amount) {
        return dfs(coins, amount, new java.util.HashMap<>());
    }

    private int dfs(int[] coins, int amount, java.util.Map<Integer, Integer> memo) {
        if (amount == 0)
            return 0;
        if (amount < 0)
            return -1;
        if (memo.containsKey(amount))
            return memo.get(amount);

        int minCoins = Integer.MAX_VALUE;

        for (int coin : coins) {
            int subResult = dfs(coins, amount - coin, memo);
            if (subResult != -1) {
                minCoins = Math.min(minCoins, subResult + 1);
            }
        }

        int result = minCoins == Integer.MAX_VALUE ? -1 : minCoins;
        memo.put(amount, result);
        return result;
    }

    public static void main(String[] args) {
        CoinChange solution = new CoinChange();

        System.out.println("=== Coin Change Test Cases ===");

        // Test case 1: coins = [1,2,5], amount = 11
        int[] coins1 = { 1, 2, 5 };
        int amount1 = 11;
        System.out.println("Coins: [1,2,5], Amount: 11");
        System.out.println("DP: " + solution.coinChange(coins1, amount1)); // Expected: 3
        System.out.println("BFS: " + solution.coinChangeBFS(coins1, amount1)); // Expected: 3
        System.out.println("DFS: " + solution.coinChangeDFS(coins1, amount1)); // Expected: 3

        // Test case 2: coins = [2], amount = 3
        int[] coins2 = { 2 };
        int amount2 = 3;
        System.out.println("\nCoins: [2], Amount: 3");
        System.out.println("DP: " + solution.coinChange(coins2, amount2)); // Expected: -1
        System.out.println("BFS: " + solution.coinChangeBFS(coins2, amount2)); // Expected: -1

        // Test case 3: coins = [1], amount = 0
        int[] coins3 = { 1 };
        int amount3 = 0;
        System.out.println("\nCoins: [1], Amount: 0");
        System.out.println("DP: " + solution.coinChange(coins3, amount3)); // Expected: 0

        // Test case 4: coins = [1,3,4], amount = 6
        int[] coins4 = { 1, 3, 4 };
        int amount4 = 6;
        System.out.println("\nCoins: [1,3,4], Amount: 6");
        System.out.println("DP: " + solution.coinChange(coins4, amount4)); // Expected: 2
        System.out.println("DFS: " + solution.coinChangeDFS(coins4, amount4)); // Expected: 2

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison ===");
        CoinChange solution = new CoinChange();

        int[] coins = { 1, 3, 4, 5 };
        int amount = 1000;

        long startTime, endTime;

        // Test DP approach
        startTime = System.nanoTime();
        int result1 = solution.coinChange(coins.clone(), amount);
        endTime = System.nanoTime();
        System.out.println("DP: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test BFS approach (smaller amount for performance)
        int smallAmount = 100;
        startTime = System.nanoTime();
        int result2 = solution.coinChangeBFS(coins.clone(), smallAmount);
        endTime = System.nanoTime();
        System.out.println("BFS (amount=100): " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test DFS approach
        startTime = System.nanoTime();
        int result3 = solution.coinChangeDFS(coins.clone(), amount);
        endTime = System.nanoTime();
        System.out.println("DFS: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }
}
