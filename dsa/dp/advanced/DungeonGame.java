package dp.advanced;

/**
 * LeetCode 174 - Dungeon Game
 * Hard Level - Dynamic Programming
 *
 * Problem:
 * A princess is saved in a dungeon. The dungeon is represented by a 2D array.
 * Each room has a number representing the net points a player gains or loses.
 * The player starts with some initial health and must reach the bottom-right cell.
 * The player must have at least 1 health at any point to survive.
 *
 * Find the minimum initial health the player needs to rescue the princess.
 *
 * Example:
 * [-3, 5]
 * [ 1, 4]
 *
 * The knight starts at top-left, must have health >= 7 to complete the dungeon.
 *
 * Time Complexity: O(m * n) where m x n is the dungeon size
 * Space Complexity: O(m * n) for the DP table
 */
public class DungeonGame {

    /**
     * Approach 1: Bottom-Up DP (Recommended)
     * Work backwards from the princess to determine minimum health needed at each cell
     *
     * Key Insight:
     * - Work from bottom-right to top-left (reverse direction)
     * - At each cell, calculate minimum health needed to survive from that cell onwards
     * - Formula: dp[i][j] = max(1, dp[i+1][j] - dungeon[i][j], dp[i][j+1] - dungeon[i][j])
     *   - dp[i+1][j] and dp[i][j+1] are minimum health needed at next cells
     *   - If we have that much health and gain dungeon[i][j] points, we need less before
     */
    public int calculateMinimumHP(int[][] dungeon) {
        if (dungeon == null || dungeon.length == 0) {
            return 1;
        }

        int m = dungeon.length;
        int n = dungeon[0].length;

        // dp[i][j] = minimum health required to leave cell (i, j) alive
        int[][] dp = new int[m][n];

        // Base case: bottom-right cell
        // Need at least 1 health + max(0, -dungeon[m-1][n-1]) to survive
        dp[m - 1][n - 1] = Math.max(1, 1 - dungeon[m - 1][n - 1]);

        // Fill last row (can only move right)
        for (int j = n - 2; j >= 0; j--) {
            dp[m - 1][j] = Math.max(1, dp[m - 1][j + 1] - dungeon[m - 1][j]);
        }

        // Fill last column (can only move down)
        for (int i = m - 2; i >= 0; i--) {
            dp[i][n - 1] = Math.max(1, dp[i + 1][n - 1] - dungeon[i][n - 1]);
        }

        // Fill remaining cells
        for (int i = m - 2; i >= 0; i--) {
            for (int j = n - 2; j >= 0; j--) {
                // Minimum health needed from next two cells
                int nextHealth = Math.min(dp[i + 1][j], dp[i][j + 1]);
                // Health needed at current cell
                dp[i][j] = Math.max(1, nextHealth - dungeon[i][j]);
            }
        }

        return dp[0][0];
    }

    /**
     * Approach 2: Space-Optimized DP
     * Use 1D array instead of 2D to save space
     *
     * Since we only need the current row and next row, we can use a 1D array
     */
    public int calculateMinimumHPOptimized(int[][] dungeon) {
        if (dungeon == null || dungeon.length == 0) {
            return 1;
        }

        int m = dungeon.length;
        int n = dungeon[0].length;

        // dp[j] = minimum health needed starting from cell (current_row, j)
        int[] dp = new int[n];

        // Initialize last cell
        dp[n - 1] = Math.max(1, 1 - dungeon[m - 1][n - 1]);

        // Fill last row
        for (int j = n - 2; j >= 0; j--) {
            dp[j] = Math.max(1, dp[j + 1] - dungeon[m - 1][j]);
        }

        // Fill other rows from bottom to top
        for (int i = m - 2; i >= 0; i--) {
            // Update last column
            dp[n - 1] = Math.max(1, dp[n - 1] - dungeon[i][n - 1]);

            // Update other columns
            for (int j = n - 2; j >= 0; j--) {
                dp[j] = Math.max(1, Math.min(dp[j], dp[j + 1]) - dungeon[i][j]);
            }
        }

        return dp[0];
    }

    /**
     * Test cases
     */
    public static void main(String[] args) {
        DungeonGame solution = new DungeonGame();

        // Test case 1
        int[][] dungeon1 = {{-3, 5}, {1, 4}};
        System.out.println("Test 1: " + solution.calculateMinimumHP(dungeon1)); // Expected: 7

        // Test case 2
        int[][] dungeon2 = {{-2, -3, 3}, {-5, -10, 1}, {10, 30, -5}};
        System.out.println("Test 2: " + solution.calculateMinimumHP(dungeon2)); // Expected: 7

        // Test case 3: Single cell with positive value
        int[][] dungeon3 = {{5}};
        System.out.println("Test 3: " + solution.calculateMinimumHP(dungeon3)); // Expected: 1

        // Test case 4: Single cell with negative value
        int[][] dungeon4 = {{-5}};
        System.out.println("Test 4: " + solution.calculateMinimumHP(dungeon4)); // Expected: 6

        // Test case 5: All negative values
        int[][] dungeon5 = {{-1, -2}, {-3, -4}};
        System.out.println("Test 5: " + solution.calculateMinimumHP(dungeon5)); // Expected: 11

        // Test case 6: Mixed values
        int[][] dungeon6 = {{100}};
        System.out.println("Test 6: " + solution.calculateMinimumHP(dungeon6)); // Expected: 1
    }
}

