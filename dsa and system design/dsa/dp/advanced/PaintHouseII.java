package dp.advanced;

import java.util.*;

/**
 * LeetCode 265: Paint House II
 * https://leetcode.com/problems/paint-house-ii/
 *
 * Description:
 * There are a row of n houses, and each house can be painted with one of the k
 * colors.
 * The cost of painting each house with a certain color is different.
 * You have to paint all the houses such that no two adjacent houses have the
 * same color.
 * The cost of painting each house with a certain color is represented by an n x
 * k cost matrix costs.
 * Return the minimum cost to paint all houses.
 *
 * Constraints:
 * - costs.length == n
 * - costs[i].length == k
 * - 1 <= n <= 100
 * - 2 <= k <= 20
 * - 1 <= costs[i][j] <= 20
 *
 * Follow-up:
 * - Could you solve it in O(nk) time?
 * - What if we need to track the actual painting sequence?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class PaintHouseII {

    // Approach 1: 2D DP - O(n*k^2) time, O(n*k) space
    public int minCostII(int[][] costs) {
        if (costs.length == 0 || costs[0].length == 0)
            return 0;

        int n = costs.length, k = costs[0].length;
        int[][] dp = new int[n][k];

        // Base case: first house
        System.arraycopy(costs[0], 0, dp[0], 0, k);

        for (int i = 1; i < n; i++) {
            for (int j = 0; j < k; j++) {
                dp[i][j] = Integer.MAX_VALUE;

                // Try all colors for previous house except current color
                for (int prevColor = 0; prevColor < k; prevColor++) {
                    if (prevColor != j) {
                        dp[i][j] = Math.min(dp[i][j], dp[i - 1][prevColor] + costs[i][j]);
                    }
                }
            }
        }

        return Arrays.stream(dp[n - 1]).min().orElse(0);
    }

    // Approach 2: Optimized O(nk) - O(n*k) time, O(n*k) space
    public int minCostIIOptimized(int[][] costs) {
        if (costs.length == 0 || costs[0].length == 0)
            return 0;

        int n = costs.length, k = costs[0].length;
        int[][] dp = new int[n][k];

        System.arraycopy(costs[0], 0, dp[0], 0, k);

        for (int i = 1; i < n; i++) {
            // Find two minimum values from previous row
            int min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
            int min1Index = -1;

            for (int j = 0; j < k; j++) {
                if (dp[i - 1][j] < min1) {
                    min2 = min1;
                    min1 = dp[i - 1][j];
                    min1Index = j;
                } else if (dp[i - 1][j] < min2) {
                    min2 = dp[i - 1][j];
                }
            }

            // Fill current row
            for (int j = 0; j < k; j++) {
                if (j == min1Index) {
                    dp[i][j] = min2 + costs[i][j];
                } else {
                    dp[i][j] = min1 + costs[i][j];
                }
            }
        }

        return Arrays.stream(dp[n - 1]).min().orElse(0);
    }

    // Approach 3: Space Optimized - O(n*k) time, O(k) space
    public int minCostIISpaceOptimized(int[][] costs) {
        if (costs.length == 0 || costs[0].length == 0)
            return 0;

        int n = costs.length, k = costs[0].length;
        int[] prev = new int[k];
        int[] curr = new int[k];

        System.arraycopy(costs[0], 0, prev, 0, k);

        for (int i = 1; i < n; i++) {
            // Find two minimums from previous row
            int min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
            int min1Index = -1;

            for (int j = 0; j < k; j++) {
                if (prev[j] < min1) {
                    min2 = min1;
                    min1 = prev[j];
                    min1Index = j;
                } else if (prev[j] < min2) {
                    min2 = prev[j];
                }
            }

            // Fill current row
            for (int j = 0; j < k; j++) {
                curr[j] = costs[i][j] + (j == min1Index ? min2 : min1);
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return Arrays.stream(prev).min().orElse(0);
    }

    // Approach 4: Get Optimal Painting Sequence - O(n*k) time, O(n*k) space
    public int[] getOptimalPaintingSequence(int[][] costs) {
        if (costs.length == 0 || costs[0].length == 0)
            return new int[0];

        int n = costs.length, k = costs[0].length;
        int[][] dp = new int[n][k];
        int[][] parent = new int[n][k];

        System.arraycopy(costs[0], 0, dp[0], 0, k);
        Arrays.fill(parent[0], -1);

        for (int i = 1; i < n; i++) {
            int min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
            int min1Index = -1, min2Index = -1;

            for (int j = 0; j < k; j++) {
                if (dp[i - 1][j] < min1) {
                    min2 = min1;
                    min2Index = min1Index;
                    min1 = dp[i - 1][j];
                    min1Index = j;
                } else if (dp[i - 1][j] < min2) {
                    min2 = dp[i - 1][j];
                    min2Index = j;
                }
            }

            for (int j = 0; j < k; j++) {
                if (j == min1Index) {
                    dp[i][j] = min2 + costs[i][j];
                    parent[i][j] = min2Index;
                } else {
                    dp[i][j] = min1 + costs[i][j];
                    parent[i][j] = min1Index;
                }
            }
        }

        // Find optimal last color
        int minCost = Integer.MAX_VALUE;
        int lastColor = 0;
        for (int j = 0; j < k; j++) {
            if (dp[n - 1][j] < minCost) {
                minCost = dp[n - 1][j];
                lastColor = j;
            }
        }

        // Reconstruct path
        int[] result = new int[n];
        result[n - 1] = lastColor;

        for (int i = n - 1; i > 0; i--) {
            result[i - 1] = parent[i][result[i]];
        }

        return result;
    }

    // Approach 5: Memoization - O(n*k^2) time, O(n*k) space
    public int minCostIIMemo(int[][] costs) {
        if (costs.length == 0 || costs[0].length == 0)
            return 0;

        int n = costs.length, k = costs[0].length;
        Integer[][] memo = new Integer[n][k];

        int minCost = Integer.MAX_VALUE;
        for (int color = 0; color < k; color++) {
            minCost = Math.min(minCost, minCostMemoHelper(costs, 0, color, memo));
        }

        return minCost;
    }

    private int minCostMemoHelper(int[][] costs, int house, int color, Integer[][] memo) {
        if (house >= costs.length)
            return 0;

        if (memo[house][color] != null)
            return memo[house][color];

        int currentCost = costs[house][color];
        int minFutureCost = Integer.MAX_VALUE;

        // Try all colors for next house except current color
        for (int nextColor = 0; nextColor < costs[0].length; nextColor++) {
            if (nextColor != color) {
                minFutureCost = Math.min(minFutureCost,
                        minCostMemoHelper(costs, house + 1, nextColor, memo));
            }
        }

        if (minFutureCost == Integer.MAX_VALUE)
            minFutureCost = 0;

        memo[house][color] = currentCost + minFutureCost;
        return memo[house][color];
    }

    public static void main(String[] args) {
        PaintHouseII solution = new PaintHouseII();

        System.out.println("=== Paint House II Test Cases ===");

        // Test Case 1: Example from problem
        int[][] costs1 = {
                { 1, 5, 3 },
                { 2, 9, 4 }
        };
        System.out.println("Test 1 - Costs:");
        printMatrix(costs1);
        System.out.println("2D DP: " + solution.minCostII(costs1));
        System.out.println("Optimized: " + solution.minCostIIOptimized(costs1));
        System.out.println("Space Optimized: " + solution.minCostIISpaceOptimized(costs1));
        System.out.println("Memoization: " + solution.minCostIIMemo(costs1));
        System.out.println("Painting Sequence: " + Arrays.toString(solution.getOptimalPaintingSequence(costs1)));
        System.out.println("Expected: 5\n");

        // Test Case 2: More colors
        int[][] costs2 = {
                { 1, 3, 2, 4 },
                { 5, 1, 4, 2 },
                { 3, 2, 1, 5 }
        };
        System.out.println("Test 2 - Costs:");
        printMatrix(costs2);
        System.out.println("Optimized: " + solution.minCostIIOptimized(costs2));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        PaintHouseII solution = new PaintHouseII();

        int n = 100, k = 20;
        int[][] largeCosts = new int[n][k];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < k; j++) {
                largeCosts[i][j] = (int) (Math.random() * 20) + 1;
            }
        }

        System.out.println("=== Performance Test (Houses: " + n + ", Colors: " + k + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minCostII(largeCosts);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minCostIIOptimized(largeCosts);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minCostIISpaceOptimized(largeCosts);
        end = System.nanoTime();
        System.out.println("Space Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
