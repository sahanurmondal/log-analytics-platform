package greedy.medium;

import java.util.*;

/**
 * LeetCode 322: Coin Change
 * URL: https://leetcode.com/problems/coin-change/
 * Difficulty: Medium
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 */
public class MinimumNumberOfCoinsToMakeChange {
    // DP approach - O(amount * coins.length)
    public int coinChange(int[] coins, int amount) {
        int[] dp = new int[amount + 1];
        Arrays.fill(dp, amount + 1);
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

    // BFS approach
    public int coinChangeBFS(int[] coins, int amount) {
        if (amount == 0)
            return 0;

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
                        queue.offer(next);
                        visited.add(next);
                    }
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        MinimumNumberOfCoinsToMakeChange solution = new MinimumNumberOfCoinsToMakeChange();

        // Test Case 1: Basic optimal case
        System.out.println("Test 1: " + solution.coinChange(new int[] { 1, 3, 4 }, 6)); // Expected: 2 (3+3)

        // Test Case 2: Impossible case
        System.out.println("Test 2: " + solution.coinChange(new int[] { 2 }, 3)); // Expected: -1 (impossible)

        // Test Case 3: Zero amount
        System.out.println("Test 3: " + solution.coinChange(new int[] { 1 }, 0)); // Expected: 0 (no amount needed)

        // Test Case 4: Mixed denomination
        System.out.println("Test 4: " + solution.coinChange(new int[] { 1, 2, 5 }, 11)); // Expected: 3 (5+5+1)

        // Test Case 5: Large denomination mix
        System.out.println("Test 5: " + solution.coinChange(new int[] { 2, 5, 10, 1 }, 27)); // Expected: 4 (10+10+5+2)

        // Test Case 6: Single coin exact match
        System.out.println("Test 6: " + solution.coinChange(new int[] { 1 }, 1)); // Expected: 1 (single coin)

        // Test Case 7: Large coin, small amount
        System.out.println("Test 7: " + solution.coinChange(new int[] { 5 }, 3)); // Expected: -1 (impossible)

        // Test Case 8: All even coins, odd amount
        System.out.println("Test 8: " + solution.coinChange(new int[] { 2, 4, 6 }, 1)); // Expected: -1 (impossible)

        // Test Case 9: Greedy vs optimal
        System.out.println("Test 9: " + solution.coinChange(new int[] { 1, 3, 4 }, 6)); // Expected: 2 (3+3, not 4+1+1)

        // Test Case 10: US coin system
        System.out.println("Test 10: " + solution.coinChange(new int[] { 1, 5, 10, 25 }, 67)); // Expected: 6
                                                                                               // (25+25+10+5+1+1)

        // Test Case 11: Non-greedy optimal
        System.out.println("Test 11: " + solution.coinChange(new int[] { 1, 4, 5 }, 8)); // Expected: 2 (4+4, not
                                                                                         // 5+1+1+1)

        // Test Case 12: Multiple equal solutions
        System.out.println("Test 12: " + solution.coinChange(new int[] { 2, 3, 5 }, 9)); // Expected: 3 (3+3+3)

        // Test Case 13: Prime numbers
        System.out.println("Test 13: " + solution.coinChange(new int[] { 1, 7, 10 }, 14)); // Expected: 2 (7+7)

        // Test Case 14: Large amount decomposition
        System.out.println("Test 14: " + solution.coinChange(new int[] { 1, 3, 4 }, 15)); // Expected: 4 (4+4+4+3)

        // Test Case 15: BFS approach validation
        System.out.println("Test 15 (BFS): " + solution.coinChangeBFS(new int[] { 1, 3, 4 }, 6)); // Expected: 2
    }
}
