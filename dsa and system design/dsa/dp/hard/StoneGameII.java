package dp.hard;

/**
 * LeetCode 1140: Stone Game II
 * https://leetcode.com/problems/stone-game-ii/
 *
 * Description:
 * Given piles of stones, two players take turns. Return the maximum stones the
 * first player can get.
 *
 * Constraints:
 * - 1 <= piles.length <= 100
 * - 1 <= piles[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it in O(n^2) time?
 * 
 * Company Tags: Google, Microsoft, Amazon
 * Difficulty: Hard
 */
public class StoneGameII {

    // Approach 1: Dynamic Programming with Memoization - O(n^3) time, O(n^2) space
    public int stoneGameII(int[] piles) {
        int n = piles.length;

        // Precompute suffix sums for efficient range sum queries
        int[] suffixSum = new int[n];
        suffixSum[n - 1] = piles[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            suffixSum[i] = suffixSum[i + 1] + piles[i];
        }

        // Memoization table: memo[i][m] = max stones Alice can get from index i with M
        // = m
        Integer[][] memo = new Integer[n][n + 1];

        return dfs(piles, suffixSum, 0, 1, memo);
    }

    private int dfs(int[] piles, int[] suffixSum, int i, int M, Integer[][] memo) {
        // Base case: if we can take all remaining piles
        if (i + 2 * M >= piles.length) {
            return suffixSum[i];
        }

        // Check memo
        if (memo[i][M] != null) {
            return memo[i][M];
        }

        int maxStones = 0;

        // Try taking X piles where 1 <= X <= 2*M
        for (int X = 1; X <= 2 * M; X++) {
            if (i + X > piles.length)
                break;

            // Current player takes X piles, opponent plays optimally from the rest
            int currentTake = suffixSum[i] - suffixSum[i + X];
            int opponentOptimal = dfs(piles, suffixSum, i + X, Math.max(M, X), memo);

            // Current player gets remaining stones after opponent's optimal play
            int totalStones = currentTake + (suffixSum[i + X] - opponentOptimal);
            maxStones = Math.max(maxStones, totalStones);
        }

        memo[i][M] = maxStones;
        return maxStones;
    }

    // Approach 2: Bottom-up DP - O(n^3) time, O(n^2) space
    public int stoneGameIIBottomUp(int[] piles) {
        int n = piles.length;

        // Precompute suffix sums
        int[] suffixSum = new int[n + 1];
        for (int i = n - 1; i >= 0; i--) {
            suffixSum[i] = suffixSum[i + 1] + piles[i];
        }

        // dp[i][m] = maximum stones first player can get from position i with M = m
        int[][] dp = new int[n + 1][n + 1];

        // Fill DP table bottom-up
        for (int i = n - 1; i >= 0; i--) {
            for (int m = 1; m <= n; m++) {
                // If can take all remaining piles
                if (i + 2 * m >= n) {
                    dp[i][m] = suffixSum[i];
                } else {
                    // Try all possible moves
                    for (int x = 1; x <= 2 * m && i + x <= n; x++) {
                        // Total stones from i to end minus what opponent gets optimally
                        dp[i][m] = Math.max(dp[i][m],
                                suffixSum[i] - dp[i + x][Math.max(m, x)]);
                    }
                }
            }
        }

        return dp[0][1];
    }

    // Approach 3: Optimized with better understanding - O(n^3) time, O(n^2) space
    public int stoneGameIIOptimized(int[] piles) {
        int n = piles.length;
        int[][] dp = new int[n][n + 1];
        int[] suffixSum = new int[n];

        // Calculate suffix sums
        suffixSum[n - 1] = piles[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            suffixSum[i] = suffixSum[i + 1] + piles[i];
        }

        // DP: work backwards
        for (int i = n - 1; i >= 0; i--) {
            for (int m = 1; m <= n; m++) {
                if (i + 2 * m >= n) {
                    // Can take all remaining stones
                    dp[i][m] = suffixSum[i];
                } else {
                    // Try all possible number of piles to take
                    for (int x = 1; x <= 2 * m; x++) {
                        // Maximum stones = total remaining - what opponent gets
                        dp[i][m] = Math.max(dp[i][m],
                                suffixSum[i] - dp[i + x][Math.max(m, x)]);
                    }
                }
            }
        }

        return dp[0][1];
    }

    public static void main(String[] args) {
        StoneGameII solution = new StoneGameII();

        // Test case 1: [2,7,9,4,4]
        int[] piles1 = { 2, 7, 9, 4, 4 };
        System.out.println("=== Test Case 1: [2,7,9,4,4] ===");
        System.out.println("Memoization: " + solution.stoneGameII(piles1)); // Expected: 10
        System.out.println("Bottom-up: " + solution.stoneGameIIBottomUp(piles1)); // Expected: 10
        System.out.println("Optimized: " + solution.stoneGameIIOptimized(piles1)); // Expected: 10

        // Test case 2: [1,2,3,4,5,6,7,8,9,10]
        int[] piles2 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 };
        System.out.println("\n=== Test Case 2: [1,2,3,4,5,6,7,8,9,10] ===");
        System.out.println("Memoization: " + solution.stoneGameII(piles2)); // Expected: 37
        System.out.println("Bottom-up: " + solution.stoneGameIIBottomUp(piles2)); // Expected: 37
        System.out.println("Optimized: " + solution.stoneGameIIOptimized(piles2)); // Expected: 37

        // Test case 3: Small array
        int[] piles3 = { 3, 7, 2, 3 };
        System.out.println("\n=== Test Case 3: [3,7,2,3] ===");
        System.out.println("Memoization: " + solution.stoneGameII(piles3));
        System.out.println("Bottom-up: " + solution.stoneGameIIBottomUp(piles3));

        // Test case 4: Edge case - single pile
        int[] piles4 = { 100 };
        System.out.println("\n=== Test Case 4: [100] ===");
        System.out.println("Result: " + solution.stoneGameII(piles4)); // Expected: 100

        // Test case 5: Two piles
        int[] piles5 = { 1, 2 };
        System.out.println("\n=== Test Case 5: [1,2] ===");
        System.out.println("Result: " + solution.stoneGameII(piles5)); // Expected: 3

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Test ===");
        StoneGameII solution = new StoneGameII();

        // Create a larger test array
        int[] largePiles = new int[50];
        for (int i = 0; i < 50; i++) {
            largePiles[i] = (i + 1) * 2;
        }

        long startTime, endTime;

        // Test memoization approach
        startTime = System.nanoTime();
        int result1 = solution.stoneGameII(largePiles.clone());
        endTime = System.nanoTime();
        System.out.println("Memoization: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test bottom-up approach
        startTime = System.nanoTime();
        int result2 = solution.stoneGameIIBottomUp(largePiles.clone());
        endTime = System.nanoTime();
        System.out.println("Bottom-up: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimized approach
        startTime = System.nanoTime();
        int result3 = solution.stoneGameIIOptimized(largePiles.clone());
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }
}
