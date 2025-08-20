package greedy.hard;

import java.util.*;

/**
 * Minimum Number of Coins to Make Change (Greedy)
 * 
 * LeetCode Problem: 322. Coin Change (but solved with Greedy approach)
 * URL: https://leetcode.com/problems/coin-change/
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Difficulty: Hard (when using Greedy - doesn't always work)
 * 
 * Description:
 * Given coins of different denominations and a total amount, find the minimum
 * number
 * of coins needed to make the total using a greedy approach.
 * 
 * Note: Greedy approach only works for certain coin systems (like US coins).
 * For arbitrary denominations, dynamic programming is required.
 * 
 * Constraints:
 * - 1 <= coins.length <= 12
 * - 1 <= coins[i] <= 10^5
 * - 0 <= amount <= 10^5
 * 
 * Follow-ups:
 * 1. Can you solve it for arbitrary denominations using DP?
 * 2. Can you detect when greedy approach fails?
 * 3. Can you solve the change-making problem for different coin systems?
 * 4. Can you find all possible ways to make change?
 */
public class MinimumNumberOfCoinsToMakeChange {

    /**
     * Greedy approach - works only for certain coin systems
     * Time: O(n log n + k) where n is coins length, k is number of coins used
     * Space: O(1)
     */
    public int coinChangeGreedy(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        if (coins == null || coins.length == 0)
            return -1;

        // Sort coins in descending order
        Arrays.sort(coins);
        for (int i = 0; i < coins.length / 2; i++) {
            int temp = coins[i];
            coins[i] = coins[coins.length - 1 - i];
            coins[coins.length - 1 - i] = temp;
        }

        int count = 0;
        int remaining = amount;

        for (int coin : coins) {
            if (remaining >= coin) {
                int numCoins = remaining / coin;
                count += numCoins;
                remaining -= numCoins * coin;

                if (remaining == 0)
                    break;
            }
        }

        return remaining == 0 ? count : -1;
    }

    /**
     * Dynamic Programming approach - works for all coin systems
     * Time: O(amount * coins.length), Space: O(amount)
     */
    public int coinChangeDP(int[] coins, int amount) {
        if (amount == 0)
            return 0;
        if (coins == null || coins.length == 0)
            return -1;

        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1); // Initialize with impossible value
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
     * Follow-up 1: BFS approach for minimum coins
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
                    if (next == amount) {
                        return level;
                    }
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
     * Follow-up 2: Check if greedy approach works for given coin system
     * Time: O(n^2 * max_coin), Space: O(max_coin)
     */
    public boolean isGreedyOptimal(int[] coins) {
        Arrays.sort(coins);

        // Test with various amounts up to 2 * largest coin
        int maxTest = coins[coins.length - 1] * 2;

        for (int amount = 1; amount <= maxTest; amount++) {
            int greedyResult = coinChangeGreedy(coins.clone(), amount);
            int dpResult = coinChangeDP(coins, amount);

            if (greedyResult != dpResult) {
                return false;
            }
        }

        return true;
    }

    /**
     * Follow-up 3: Find all possible ways to make change
     * Time: O(amount^coins.length), Space: O(amount^coins.length)
     */
    public List<List<Integer>> findAllWays(int[] coins, int amount) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(coins, amount, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(int[] coins, int remaining, int start,
            List<Integer> current, List<List<Integer>> result) {
        if (remaining == 0) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < coins.length; i++) {
            if (coins[i] <= remaining) {
                current.add(coins[i]);
                backtrack(coins, remaining - coins[i], i, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    /**
     * Follow-up 4: Coin change with specific denominations (US coins)
     * Time: O(1) - fixed number of denominations, Space: O(1)
     */
    public int coinChangeUSCoins(int amount) {
        int[] usDenominations = { 25, 10, 5, 1 }; // quarters, dimes, nickels, pennies
        return coinChangeGreedy(usDenominations, amount);
    }

    /**
     * Follow-up 5: Minimum coins with coin count tracking
     * Time: O(amount * coins.length), Space: O(amount)
     */
    public Map<String, Object> coinChangeWithDetails(int[] coins, int amount) {
        Map<String, Object> result = new HashMap<>();

        if (amount == 0) {
            result.put("minCoins", 0);
            result.put("coinCounts", new HashMap<Integer, Integer>());
            return result;
        }

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

        if (dp[amount] > amount) {
            result.put("minCoins", -1);
            return result;
        }

        // Reconstruct solution
        Map<Integer, Integer> coinCounts = new HashMap<>();
        int current = amount;
        while (current > 0) {
            int coin = parent[current];
            coinCounts.put(coin, coinCounts.getOrDefault(coin, 0) + 1);
            current -= coin;
        }

        result.put("minCoins", dp[amount]);
        result.put("coinCounts", coinCounts);
        return result;
    }

    public static void main(String[] args) {
        MinimumNumberOfCoinsToMakeChange solution = new MinimumNumberOfCoinsToMakeChange();

        System.out.println("=== Minimum Number of Coins to Make Change Test ===");

        // Test Case 1: Standard cases
        System.out.println("Greedy [1,2,5], 11: " + solution.coinChangeGreedy(new int[] { 1, 2, 5 }, 11)); // 3
        System.out.println("DP [1,2,5], 11: " + solution.coinChangeDP(new int[] { 1, 2, 5 }, 11)); // 3

        // Test Case 2: Greedy fails
        System.out.println("Greedy [1,3,4], 6: " + solution.coinChangeGreedy(new int[] { 1, 3, 4 }, 6)); // 3 (wrong)
        System.out.println("DP [1,3,4], 6: " + solution.coinChangeDP(new int[] { 1, 3, 4 }, 6)); // 2 (correct)

        // Test Case 3: No solution
        System.out.println("Greedy [2], 3: " + solution.coinChangeGreedy(new int[] { 2 }, 3)); // -1
        System.out.println("DP [2], 3: " + solution.coinChangeDP(new int[] { 2 }, 3)); // -1

        // Test Case 4: Amount is zero
        System.out.println("Zero amount: " + solution.coinChangeGreedy(new int[] { 1, 2, 5 }, 0)); // 0

        // Test Case 5: BFS approach
        System.out.println("BFS [1,2,5], 11: " + solution.coinChangeBFS(new int[] { 1, 2, 5 }, 11)); // 3

        // Test Case 6: US coins
        System.out.println("US coins, 67¢: " + solution.coinChangeUSCoins(67)); // 2 quarters + 1 dime + 1 nickel + 2
                                                                                // pennies = 6

        // Test Case 7: Check if greedy is optimal
        System.out.println("Is [1,2,5] greedy optimal? " + solution.isGreedyOptimal(new int[] { 1, 2, 5 })); // true
        System.out.println("Is [1,3,4] greedy optimal? " + solution.isGreedyOptimal(new int[] { 1, 3, 4 })); // false

        // Test Case 8: Find all ways (small example)
        System.out.println("All ways [1,2], 4: " + solution.findAllWays(new int[] { 1, 2 }, 4));

        // Test Case 9: Detailed solution
        System.out.println("Detailed [1,2,5], 11: " + solution.coinChangeWithDetails(new int[] { 1, 2, 5 }, 11));

        // Performance test
        System.out.println("\n=== Performance Test ===");
        long startTime = System.currentTimeMillis();

        int[] largeCoinSet = { 1, 5, 10, 25, 50, 100 };
        for (int i = 1; i <= 1000; i++) {
            solution.coinChangeDP(largeCoinSet, i);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("DP for amounts 1-1000: " + (endTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        for (int i = 1; i <= 1000; i++) {
            solution.coinChangeGreedy(largeCoinSet.clone(), i);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Greedy for amounts 1-1000: " + (endTime - startTime) + "ms");
    }
}
