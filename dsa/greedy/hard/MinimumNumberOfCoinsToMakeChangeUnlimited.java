package greedy.hard;

import java.util.*;

/**
 * Minimum Number of Coins to Make Change (Unlimited Supply)
 * 
 * LeetCode Problem: 322. Coin Change
 * URL: https://leetcode.com/problems/coin-change/
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple, Bloomberg
 * Difficulty: Hard (optimization variants)
 * 
 * Description:
 * Given coins of different denominations and a total amount, find the minimum
 * number of coins needed to make the total. You have unlimited supply of each
 * coin.
 * 
 * Constraints:
 * - 1 <= coins.length <= 12
 * - 1 <= coins[i] <= 2^31 - 1
 * - 0 <= amount <= 10^4
 * 
 * Follow-ups:
 * 1. Can you solve with different DP approaches?
 * 2. Can you optimize space complexity?
 * 3. Can you handle very large amounts efficiently?
 * 4. Can you find the actual coin combination?
 * 5. Can you handle negative coin values?
 */
public class MinimumNumberOfCoinsToMakeChangeUnlimited {

    /**
     * Dynamic Programming - Bottom Up (Tabulation)
     * Time: O(amount * coins.length), Space: O(amount)
     */
    public int coinChange(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        if (coins == null || coins.length == 0)
            return -1;

        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // Fill with impossible value
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

    /**
     * Dynamic Programming - Top Down (Memoization)
     * Time: O(amount * coins.length), Space: O(amount)
     */
    public int coinChangeTopDown(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        if (coins == null || coins.length == 0)
            return -1;

        Map<Integer, Integer> memo = new HashMap<>();
        int result = coinChangeHelper(coins, amount, memo);
        return result == Integer.MAX_VALUE ? -1 : result;
    }

    private int coinChangeHelper(int[] coins, int amount, Map<Integer, Integer> memo) {
        if (amount == 0)
            return 0;
        if (amount < 0)
            return Integer.MAX_VALUE;
        if (memo.containsKey(amount))
            return memo.get(amount);

        int minCoins = Integer.MAX_VALUE;
        for (int coin : coins) {
            int subResult = coinChangeHelper(coins, amount - coin, memo);
            if (subResult != Integer.MAX_VALUE) {
                minCoins = Math.min(minCoins, subResult + 1);
            }
        }

        memo.put(amount, minCoins);
        return minCoins;
    }

    /**
     * BFS Approach - Level by level exploration
     * Time: O(amount * coins.length), Space: O(amount)
     */
    public int coinChangeBFS(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        if (coins == null || coins.length == 0)
            return -1;

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.offer(0);
        visited.add(0);
        int level = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            level++;

            for (int i = 0; i < size; i++) {
                int current = queue.poll();

                for (int coin : coins) {
                    int next = current + coin;
                    if (next == amount)
                        return level;

                    if (next < amount && !visited.contains(next)) {
                        visited.add(next);
                        queue.offer(next);
                    }
                }
            }
        }

        return -1;
    }

    /**
     * Follow-up 1: Space Optimized DP (only works for specific cases)
     * Time: O(amount * coins.length), Space: O(min(amount, max_coin))
     */
    public int coinChangeSpaceOptimized(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        Arrays.sort(coins);

        int maxCoin = coins[coins.length - 1];
        int[] dp = new int[Math.min(amount + 1, maxCoin + 1)];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            int dpIndex = i % dp.length;
            if (dpIndex == 0)
                dpIndex = dp.length - 1;

            dp[dpIndex] = amount + 1; // Reset

            for (int coin : coins) {
                if (coin <= i) {
                    int prevIndex = (i - coin) % dp.length;
                    if (prevIndex == 0)
                        prevIndex = dp.length - 1;

                    if (dp[prevIndex] != amount + 1) {
                        dp[dpIndex] = Math.min(dp[dpIndex], dp[prevIndex] + 1);
                    }
                }
            }
        }

        int resultIndex = amount % dp.length;
        if (resultIndex == 0)
            resultIndex = dp.length - 1;

        return dp[resultIndex] > amount ? -1 : dp[resultIndex];
    }

    /**
     * Follow-up 2: Find actual coin combination
     * Time: O(amount * coins.length), Space: O(amount)
     */
    public List<Integer> coinChangeWithCombination(int[] coins, int amount) {
        if (amount == 0)
            return new ArrayList<>();
        if (coins == null || coins.length == 0)
            return null;

        int[] dp = new int[amount + 1];
        int[] parent = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        Arrays.fill(parent, -1);
        dp[0] = 0;

        for (int i = 1; i <= amount; i++) {
            for (int coin : coins) {
                if (coin <= i && dp[i - coin] + 1 < dp[i]) {
                    dp[i] = dp[i - coin] + 1;
                    parent[i] = coin;
                }
            }
        }

        if (dp[amount] > amount)
            return null;

        // Reconstruct path
        List<Integer> result = new ArrayList<>();
        int current = amount;
        while (current > 0) {
            int coin = parent[current];
            result.add(coin);
            current -= coin;
        }

        return result;
    }

    /**
     * Follow-up 3: Handle large amounts with mathematical optimization
     * Time: O(coins.length * sqrt(amount)), Space: O(sqrt(amount))
     */
    public int coinChangeLargeAmount(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        Arrays.sort(coins);

        // For very large amounts, use mathematical insights
        // This is a simplified version - real implementation would be more complex
        int largestCoin = coins[coins.length - 1];
        if (amount >= largestCoin * largestCoin) {
            // Use mostly the largest coin for efficiency
            int baseCoins = amount / largestCoin;
            int remainder = amount % largestCoin;

            // Use regular DP for remainder
            int remainderCoins = coinChange(coins, remainder);
            if (remainderCoins == -1)
                return -1;

            return baseCoins + remainderCoins;
        }

        return coinChange(coins, amount);
    }

    /**
     * Follow-up 4: Count number of ways to make change
     * Time: O(amount * coins.length), Space: O(amount)
     */
    public int countWaysToMakeChange(int[] coins, int amount) {
        if (amount == 0)
            return 1;
        if (coins == null || coins.length == 0)
            return 0;

        int[] dp = new int[amount + 1];
        dp[0] = 1;

        for (int coin : coins) {
            for (int i = coin; i <= amount; i++) {
                dp[i] += dp[i - coin];
            }
        }

        return dp[amount];
    }

    /**
     * Follow-up 5: Coin change with limited supply
     * Time: O(amount * sum(coinCounts)), Space: O(amount)
     */
    public int coinChangeWithLimitedSupply(int[] coins, int[] counts, int amount) {
        if (amount == 0)
            return 0;

        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
        dp[0] = 0;

        for (int i = 0; i < coins.length; i++) {
            int coin = coins[i];
            int count = counts[i];

            // Multiple knapsack approach
            for (int k = 1; k <= count; k++) {
                for (int j = amount; j >= coin; j--) {
                    if (dp[j - coin] != amount + 1) {
                        dp[j] = Math.min(dp[j], dp[j - coin] + 1);
                    }
                }
            }
        }

        return dp[amount] > amount ? -1 : dp[amount];
    }

    /**
     * Follow-up 6: Fractional coin change (for theoretical analysis)
     * Time: O(coins.length log coins.length), Space: O(1)
     */
    public double fractionalCoinChange(int[] coins, int amount) {
        // Sort coins in descending order of value
        Integer[] sortedCoins = new Integer[coins.length];
        for (int i = 0; i < coins.length; i++) {
            sortedCoins[i] = coins[i];
        }
        Arrays.sort(sortedCoins, Collections.reverseOrder());

        double totalCoins = 0;
        int remaining = amount;

        for (int coin : sortedCoins) {
            if (remaining >= coin) {
                int wholeCoins = remaining / coin;
                totalCoins += wholeCoins;
                remaining -= wholeCoins * coin;
            }
        }

        // If there's remaining amount, we'd need a fraction of the smallest coin
        if (remaining > 0 && coins.length > 0) {
            int smallest = Arrays.stream(coins).min().getAsInt();
            totalCoins += (double) remaining / smallest;
        }

        return totalCoins;
    }

    public static void main(String[] args) {
        MinimumNumberOfCoinsToMakeChangeUnlimited solution = new MinimumNumberOfCoinsToMakeChangeUnlimited();

        System.out.println("=== Minimum Coins to Make Change (Unlimited) Test ===");

        // Test Case 1: Basic cases
        int[] coins1 = { 1, 2, 5 };
        System.out.println("DP [1,2,5], 11: " + solution.coinChange(coins1, 11)); // 3
        System.out.println("Top-Down [1,2,5], 11: " + solution.coinChangeTopDown(coins1, 11)); // 3
        System.out.println("BFS [1,2,5], 11: " + solution.coinChangeBFS(coins1, 11)); // 3

        // Test Case 2: No solution
        int[] coins2 = { 2 };
        System.out.println("No solution [2], 3: " + solution.coinChange(coins2, 3)); // -1

        // Test Case 3: Zero amount
        System.out.println("Zero amount: " + solution.coinChange(coins1, 0)); // 0

        // Test Case 4: Single coin multiple times
        int[] coins3 = { 7 };
        System.out.println("Single coin [7], 14: " + solution.coinChange(coins3, 14)); // 2

        // Test Case 5: Find actual combination
        List<Integer> combination = solution.coinChangeWithCombination(coins1, 11);
        System.out.println("Combination for 11: " + combination); // [5, 5, 1] or similar

        // Test Case 6: Count ways
        System.out.println("Ways to make 4 with [1,2,3]: " +
                solution.countWaysToMakeChange(new int[] { 1, 2, 3 }, 4)); // 4 ways

        // Test Case 7: Limited supply
        int[] coinCounts = { 1, 2, 3 }; // 1 coin of value 1, 2 coins of value 2, 3 coins of value 5
        System.out.println("Limited supply [1,2,5] with counts [1,2,3], amount 11: " +
                solution.coinChangeWithLimitedSupply(coins1, coinCounts, 11));

        // Test Case 8: Large amount optimization
        int[] largeCoins = { 1, 5, 10, 25 };
        System.out.println("Large amount 10000: " +
                solution.coinChangeLargeAmount(largeCoins, 10000));

        // Test Case 9: Fractional change (theoretical)
        System.out.println("Fractional change [1,2,5], 11: " +
                solution.fractionalCoinChange(coins1, 11)); // 2.2

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        int[] perfCoins = { 1, 3, 5, 7, 9, 11 };
        int testAmount = 1000;

        long startTime = System.currentTimeMillis();
        int dpResult = solution.coinChange(perfCoins, testAmount);
        long dpTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int topDownResult = solution.coinChangeTopDown(perfCoins, testAmount);
        long topDownTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int bfsResult = solution.coinChangeBFS(perfCoins, testAmount);
        long bfsTime = System.currentTimeMillis() - startTime;

        System.out.println("DP: " + dpResult + " (" + dpTime + "ms)");
        System.out.println("Top-Down: " + topDownResult + " (" + topDownTime + "ms)");
        System.out.println("BFS: " + bfsResult + " (" + bfsTime + "ms)");

        // Edge cases
        System.out.println("\n=== Edge Cases ===");
        System.out.println("Empty coins: " + solution.coinChange(new int[] {}, 5)); // -1
        System.out.println("Large coin [1000000], small amount 5: " +
                solution.coinChange(new int[] { 1000000 }, 5)); // -1
        System.out.println("All same coins [3,3,3], 9: " +
                solution.coinChange(new int[] { 3, 3, 3 }, 9)); // 3
    }
}
