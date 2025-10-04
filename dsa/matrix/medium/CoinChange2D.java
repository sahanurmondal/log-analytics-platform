package matrix.medium;

import java.util.*;

/**
 * Variation: Coin Change in 2D Grid
 *
 * Description:
 * Given a 2D grid with coin values, find the minimum number of coins to reach
 * from top-left to bottom-right.
 *
 * Constraints:
 * - 1 <= m, n <= 100
 * - 1 <= grid[i][j] <= 100
 */
public class CoinChange2D {
    public int minCoins(int[][] grid) {
        // Example: min coins to reach bottom-right from top-left
        int m = grid.length, n = grid[0].length;
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i > 0)
                    dp[i][j] = Math.min(dp[i][j], dp[i - 1][j] + grid[i][j]);
                if (j > 0)
                    dp[i][j] = Math.min(dp[i][j], dp[i][j - 1] + grid[i][j]);
            }
        }
        return dp[m - 1][n - 1];
    }

    public static void main(String[] args) {
        CoinChange2D solution = new CoinChange2D();
        System.out.println(solution.minCoins(new int[][] { { 1, 3, 1 }, { 1, 5, 1 }, { 4, 2, 1 } })); // 4 coins
        System.out.println(solution.minCoins(new int[][] { { 1, 2 }, { 3, 4 } })); // 3 coins
        // Edge Case: Single row
        System.out.println(solution.minCoins(new int[][] { { 1, 2, 3 } })); // 3 coins
        // Edge Case: Single column
        System.out.println(solution.minCoins(new int[][] { { 1 }, { 2 }, { 3 } })); // 3 coins
        // Edge Case: Single cell
        System.out.println(solution.minCoins(new int[][] { { 5 } })); // 1 coin
    }
}
