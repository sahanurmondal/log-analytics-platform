package dp.medium;

import java.util.Arrays;

/**
 * LeetCode 256: Paint House
 * https://leetcode.com/problems/paint-house/
 *
 * Description:
 * There is a row of n houses, and each house can be painted one of three
 * colors: red, blue, or green.
 * The cost of painting each house with a certain color is different.
 * You have to paint all the houses such that no two adjacent houses have the
 * same color.
 * The cost of painting each house with a certain color is represented by an n x
 * 3 cost matrix costs.
 * Return the minimum cost to paint all houses.
 *
 * Constraints:
 * - costs.length == n
 * - costs[i].length == 3
 * - 1 <= n <= 100
 * - 1 <= costs[i][j] <= 20
 *
 * Follow-up:
 * - Could you solve it in O(1) space?
 * - What if there are k colors instead of 3?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class PaintHouse {

    // Approach 1: DP with 2D Array - O(n) time, O(n) space
    public int minCost(int[][] costs) {
        int n = costs.length;
        if (n == 0)
            return 0;

        int[][] dp = new int[n][3];

        // Base case: first house
        dp[0][0] = costs[0][0]; // Red
        dp[0][1] = costs[0][1]; // Blue
        dp[0][2] = costs[0][2]; // Green

        // Fill DP table
        for (int i = 1; i < n; i++) {
            dp[i][0] = costs[i][0] + Math.min(dp[i - 1][1], dp[i - 1][2]); // Red
            dp[i][1] = costs[i][1] + Math.min(dp[i - 1][0], dp[i - 1][2]); // Blue
            dp[i][2] = costs[i][2] + Math.min(dp[i - 1][0], dp[i - 1][1]); // Green
        }

        return Math.min(Math.min(dp[n - 1][0], dp[n - 1][1]), dp[n - 1][2]);
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int minCostOptimized(int[][] costs) {
        int n = costs.length;
        if (n == 0)
            return 0;

        int red = costs[0][0];
        int blue = costs[0][1];
        int green = costs[0][2];

        for (int i = 1; i < n; i++) {
            int newRed = costs[i][0] + Math.min(blue, green);
            int newBlue = costs[i][1] + Math.min(red, green);
            int newGreen = costs[i][2] + Math.min(red, blue);

            red = newRed;
            blue = newBlue;
            green = newGreen;
        }

        return Math.min(Math.min(red, blue), green);
    }

    // Approach 3: Recursive with Memoization - O(n) time, O(n) space
    public int minCostMemo(int[][] costs) {
        int n = costs.length;
        if (n == 0)
            return 0;

        Integer[][] memo = new Integer[n][3];

        int result = Integer.MAX_VALUE;
        for (int color = 0; color < 3; color++) {
            result = Math.min(result, minCostHelper(costs, 0, color, memo));
        }

        return result;
    }

    private int minCostHelper(int[][] costs, int house, int color, Integer[][] memo) {
        if (house >= costs.length)
            return 0;

        if (memo[house][color] != null)
            return memo[house][color];

        int cost = costs[house][color];
        int minFutureCost = Integer.MAX_VALUE;

        // Try all other colors for next house
        for (int nextColor = 0; nextColor < 3; nextColor++) {
            if (nextColor != color) {
                minFutureCost = Math.min(minFutureCost,
                        minCostHelper(costs, house + 1, nextColor, memo));
            }
        }

        if (minFutureCost == Integer.MAX_VALUE)
            minFutureCost = 0;

        memo[house][color] = cost + minFutureCost;
        return memo[house][color];
    }

    // Approach 4: Get Optimal Painting Sequence - O(n) time, O(n) space
    public int[] getOptimalPainting(int[][] costs) {
        int n = costs.length;
        if (n == 0)
            return new int[0];

        int[][] dp = new int[n][3];
        int[][] choice = new int[n][3];

        // Base case
        dp[0][0] = costs[0][0];
        dp[0][1] = costs[0][1];
        dp[0][2] = costs[0][2];

        // Fill DP table and track choices
        for (int i = 1; i < n; i++) {
            // Red
            if (dp[i - 1][1] < dp[i - 1][2]) {
                dp[i][0] = costs[i][0] + dp[i - 1][1];
                choice[i][0] = 1;
            } else {
                dp[i][0] = costs[i][0] + dp[i - 1][2];
                choice[i][0] = 2;
            }

            // Blue
            if (dp[i - 1][0] < dp[i - 1][2]) {
                dp[i][1] = costs[i][1] + dp[i - 1][0];
                choice[i][1] = 0;
            } else {
                dp[i][1] = costs[i][1] + dp[i - 1][2];
                choice[i][1] = 2;
            }

            // Green
            if (dp[i - 1][0] < dp[i - 1][1]) {
                dp[i][2] = costs[i][2] + dp[i - 1][0];
                choice[i][2] = 0;
            } else {
                dp[i][2] = costs[i][2] + dp[i - 1][1];
                choice[i][2] = 1;
            }
        }

        // Find optimal last color
        int lastColor = 0;
        if (dp[n - 1][1] < dp[n - 1][lastColor])
            lastColor = 1;
        if (dp[n - 1][2] < dp[n - 1][lastColor])
            lastColor = 2;

        // Reconstruct path
        int[] result = new int[n];
        result[n - 1] = lastColor;

        for (int i = n - 1; i > 0; i--) {
            result[i - 1] = choice[i][result[i]];
        }

        return result;
    }

    // Approach 5: K Colors Generalization - O(n*k^2) time, O(k) space
    public int minCostKColors(int[][] costs, int k) {
        int n = costs.length;
        if (n == 0)
            return 0;

        int[] prev = new int[k];
        System.arraycopy(costs[0], 0, prev, 0, k);

        for (int i = 1; i < n; i++) {
            int[] curr = new int[k];

            for (int j = 0; j < k; j++) {
                curr[j] = Integer.MAX_VALUE;

                for (int prevColor = 0; prevColor < k; prevColor++) {
                    if (prevColor != j) {
                        curr[j] = Math.min(curr[j], costs[i][j] + prev[prevColor]);
                    }
                }
            }

            prev = curr;
        }

        return Arrays.stream(prev).min().orElse(0);
    }

    public static void main(String[] args) {
        PaintHouse solution = new PaintHouse();

        System.out.println("=== Paint House Test Cases ===");

        // Test Case 1: Example from problem
        int[][] costs1 = { { 17, 2, 17 }, { 16, 16, 5 }, { 14, 3, 19 } };
        System.out.println("Test 1 - Costs:");
        printMatrix(costs1);
        System.out.println("2D DP: " + solution.minCost(costs1));
        System.out.println("Optimized: " + solution.minCostOptimized(costs1));
        System.out.println("Memoization: " + solution.minCostMemo(costs1));
        System.out.println("Optimal Painting: " + Arrays.toString(solution.getOptimalPainting(costs1)));
        System.out.println("Expected: 10\n");

        // Test Case 2: Single house
        int[][] costs2 = { { 7, 6, 2 } };
        System.out.println("Test 2 - Costs:");
        printMatrix(costs2);
        System.out.println("Optimized: " + solution.minCostOptimized(costs2));
        System.out.println("Expected: 2\n");

        // Test Case 3: Two houses
        int[][] costs3 = { { 1, 2, 3 }, { 4, 5, 6 } };
        System.out.println("Test 3 - Costs:");
        printMatrix(costs3);
        System.out.println("Optimized: " + solution.minCostOptimized(costs3));
        System.out.println("Expected: 5\n");

        performanceTest();
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        PaintHouse solution = new PaintHouse();

        int n = 100;
        int[][] largeCosts = new int[n][3];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 3; j++) {
                largeCosts[i][j] = (int) (Math.random() * 20) + 1;
            }
        }

        System.out.println("=== Performance Test (Houses: " + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minCost(largeCosts);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minCostOptimized(largeCosts);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minCostMemo(largeCosts);
        end = System.nanoTime();
        System.out.println("Memoization: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
