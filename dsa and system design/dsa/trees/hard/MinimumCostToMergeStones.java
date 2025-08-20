package trees.hard;

import java.util.*;

/**
 * LeetCode 1000: Minimum Cost to Merge Stones
 * https://leetcode.com/problems/minimum-cost-to-merge-stones/
 * 
 * Companies: Google, Amazon
 * Frequency: Hard
 *
 * Description: There are n piles of stones arranged in a row. Merge exactly k
 * consecutive piles into one pile, and the cost is the sum of stones. Find
 * minimum cost to merge all piles into one pile.
 *
 * Constraints:
 * - n == stones.length
 * - 1 <= n <= 30
 * - 1 <= stones[i] <= 100
 * - 2 <= k <= 30
 * 
 * Follow-up Questions:
 * 1. Can you track the merge sequence?
 * 2. Can you handle different merge costs?
 * 3. Can you optimize for large k?
 */
public class MinimumCostToMergeStones {

    // Approach 1: Dynamic Programming with 3D memoization
    public int mergeStones(int[] stones, int k) {
        int n = stones.length;
        if ((n - 1) % (k - 1) != 0)
            return -1;

        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + stones[i];
        }

        int[][][] memo = new int[n][n][k + 1];
        for (int[][] arr : memo) {
            for (int[] row : arr) {
                Arrays.fill(row, -1);
            }
        }

        return helper(stones, prefixSum, 0, n - 1, 1, k, memo);
    }

    private int helper(int[] stones, int[] prefixSum, int i, int j, int piles, int k, int[][][] memo) {
        if (i == j)
            return piles == 1 ? 0 : Integer.MAX_VALUE;

        if (memo[i][j][piles] != -1)
            return memo[i][j][piles];

        int result = Integer.MAX_VALUE;

        if (piles == 1) {
            int temp = helper(stones, prefixSum, i, j, k, k, memo);
            if (temp != Integer.MAX_VALUE) {
                result = temp + prefixSum[j + 1] - prefixSum[i];
            }
        } else {
            for (int m = i; m < j; m += k - 1) {
                int left = helper(stones, prefixSum, i, m, 1, k, memo);
                int right = helper(stones, prefixSum, m + 1, j, piles - 1, k, memo);
                if (left != Integer.MAX_VALUE && right != Integer.MAX_VALUE) {
                    result = Math.min(result, left + right);
                }
            }
        }

        return memo[i][j][piles] = result;
    }

    // Follow-up 1: Track merge sequence
    public List<String> getMergeSequence(int[] stones, int k) {
        List<String> sequence = new ArrayList<>();
        int n = stones.length;
        if ((n - 1) % (k - 1) != 0)
            return sequence;

        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + stones[i];
        }

        int[][][] memo = new int[n][n][k + 1];
        for (int[][] arr : memo) {
            for (int[] row : arr) {
                Arrays.fill(row, -1);
            }
        }

        traceMerge(stones, prefixSum, 0, n - 1, 1, k, memo, sequence);
        return sequence;
    }

    private void traceMerge(int[] stones, int[] prefixSum, int i, int j, int piles, int k,
            int[][][] memo, List<String> sequence) {
        if (i == j)
            return;

        if (piles == 1) {
            traceMerge(stones, prefixSum, i, j, k, k, memo, sequence);
            sequence.add("Merge piles " + i + " to " + j + " into 1 pile with cost " +
                    (prefixSum[j + 1] - prefixSum[i]));
        } else {
            for (int m = i; m < j; m += k - 1) {
                int left = helper(stones, prefixSum, i, m, 1, k, memo);
                int right = helper(stones, prefixSum, m + 1, j, piles - 1, k, memo);
                if (left != Integer.MAX_VALUE && right != Integer.MAX_VALUE) {
                    traceMerge(stones, prefixSum, i, m, 1, k, memo, sequence);
                    traceMerge(stones, prefixSum, m + 1, j, piles - 1, k, memo, sequence);
                    break;
                }
            }
        }
    }

    // Follow-up 2: Different merge costs based on pile sizes
    public int mergeStonesWithCosts(int[] stones, int k, int[][] mergeCosts) {
        int n = stones.length;
        if ((n - 1) % (k - 1) != 0)
            return -1;

        int[][][] memo = new int[n][n][k + 1];
        for (int[][] arr : memo) {
            for (int[] row : arr) {
                Arrays.fill(row, -1);
            }
        }

        return helperWithCosts(stones, 0, n - 1, 1, k, mergeCosts, memo);
    }

    private int helperWithCosts(int[] stones, int i, int j, int piles, int k, int[][] mergeCosts, int[][][] memo) {
        if (i == j)
            return piles == 1 ? 0 : Integer.MAX_VALUE;

        if (memo[i][j][piles] != -1)
            return memo[i][j][piles];

        int result = Integer.MAX_VALUE;

        if (piles == 1) {
            int temp = helperWithCosts(stones, i, j, k, k, mergeCosts, memo);
            if (temp != Integer.MAX_VALUE) {
                int totalStones = 0;
                for (int x = i; x <= j; x++)
                    totalStones += stones[x];
                result = temp + mergeCosts[k][totalStones];
            }
        } else {
            for (int m = i; m < j; m += k - 1) {
                int left = helperWithCosts(stones, i, m, 1, k, mergeCosts, memo);
                int right = helperWithCosts(stones, m + 1, j, piles - 1, k, mergeCosts, memo);
                if (left != Integer.MAX_VALUE && right != Integer.MAX_VALUE) {
                    result = Math.min(result, left + right);
                }
            }
        }

        return memo[i][j][piles] = result;
    }

    // Follow-up 3: Optimized for large k
    public int mergeStonesOptimized(int[] stones, int k) {
        int n = stones.length;
        if ((n - 1) % (k - 1) != 0)
            return -1;

        int[] prefixSum = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + stones[i];
        }

        int[][] dp = new int[n][n];

        for (int len = k; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                dp[i][j] = Integer.MAX_VALUE;

                for (int m = i; m < j; m += k - 1) {
                    if (dp[i][m] != Integer.MAX_VALUE && dp[m + 1][j] != Integer.MAX_VALUE) {
                        dp[i][j] = Math.min(dp[i][j], dp[i][m] + dp[m + 1][j]);
                    }
                }

                if ((j - i) % (k - 1) == 0) {
                    dp[i][j] += prefixSum[j + 1] - prefixSum[i];
                }
            }
        }

        return dp[0][n - 1];
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MinimumCostToMergeStones solution = new MinimumCostToMergeStones();

        // Test case 1: Basic case
        int[] stones1 = { 3, 2, 4, 1 };
        int k1 = 2;
        System.out.println("Test 1 - stones: " + Arrays.toString(stones1) + ", k: " + k1);
        System.out.println("Result: " + solution.mergeStones(stones1, k1));

        // Test case 2: Impossible case
        int[] stones2 = { 3, 5, 1, 2, 6 };
        int k2 = 3;
        System.out.println("\nTest 2 - Impossible case:");
        System.out.println("Result: " + solution.mergeStones(stones2, k2));

        // Test case 3: Merge sequence
        System.out.println("\nTest 3 - Merge sequence:");
        List<String> sequence = solution.getMergeSequence(stones1, k1);
        for (String step : sequence) {
            System.out.println(step);
        }

        // Test case 4: Optimized version
        System.out.println("\nTest 4 - Optimized version:");
        System.out.println("Result: " + solution.mergeStonesOptimized(stones1, k1));

        // Edge cases
        System.out.println("\nEdge cases:");
        int[] singleStone = { 5 };
        System.out.println("Single stone: " + solution.mergeStones(singleStone, 2));

        int[] twoStones = { 1, 2 };
        System.out.println("Two stones, k=2: " + solution.mergeStones(twoStones, 2));

        // Stress test
        System.out.println("\nStress test:");
        int[] largeStones = new int[25];
        for (int i = 0; i < 25; i++) {
            largeStones[i] = (i % 10) + 1;
        }

        long start = System.nanoTime();
        int result = solution.mergeStonesOptimized(largeStones, 3);
        long end = System.nanoTime();
        System.out.println("Large case result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
