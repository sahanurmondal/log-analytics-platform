package dp.grid.path_counting;

import java.util.Arrays;

/**
 * LeetCode 63: Unique Paths II
 * https://leetcode.com/problems/unique-paths-ii/
 *
 * Description:
 * You are given an m x n integer array grid. There is a robot initially located
 * at the top-left corner (i.e., grid[0][0]).
 * The robot tries to move to the bottom-right corner (i.e., grid[m - 1][n -
 * 1]). The robot can only move either down or right at any point in time.
 * An obstacle and space are marked as 1 and 0 respectively in grid. A path that
 * the robot takes cannot include any square that is an obstacle.
 * Return the number of possible unique paths that the robot can take to reach
 * the bottom-right corner.
 *
 * Constraints:
 * - m == obstacleGrid.length
 * - n == obstacleGrid[i].length
 * - 1 <= m, n <= 100
 * - obstacleGrid[i][j] is 0 or 1.
 *
 * Follow-up:
 * - Can you solve it in O(min(m,n)) space?
 * - What if we need to find all unique paths?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg
 * Difficulty: Medium
 */
public class UniquePathsWithObstacles {

    // Approach 1: 2D DP - O(m*n) time, O(m*n) space
    public int uniquePathsWithObstacles(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;

        // If start or end is blocked
        if (obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return 0;
        }

        int[][] dp = new int[m][n];
        dp[0][0] = 1;

        // Fill first row
        for (int j = 1; j < n; j++) {
            dp[0][j] = (obstacleGrid[0][j] == 1) ? 0 : dp[0][j - 1];
        }

        // Fill first column
        for (int i = 1; i < m; i++) {
            dp[i][0] = (obstacleGrid[i][0] == 1) ? 0 : dp[i - 1][0];
        }

        // Fill the rest
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    dp[i][j] = 0;
                } else {
                    dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
                }
            }
        }

        return dp[m - 1][n - 1];
    }

    // Approach 2: Space Optimized DP - O(m*n) time, O(min(m,n)) space
    public int uniquePathsWithObstaclesOptimized(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;

        if (obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return 0;
        }

        // Use the smaller dimension for space optimization
        if (m > n) {
            return uniquePathsWithObstaclesHelper(transpose(obstacleGrid));
        }

        int[] dp = new int[n];
        dp[0] = 1;

        for (int i = 0; i < m; i++) {
            // Handle first column
            if (obstacleGrid[i][0] == 1) {
                dp[0] = 0;
            }

            // Handle remaining columns
            for (int j = 1; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    dp[j] = 0;
                } else {
                    dp[j] = dp[j] + dp[j - 1];
                }
            }
        }

        return dp[n - 1];
    }

    private int uniquePathsWithObstaclesHelper(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;
        int[] dp = new int[n];
        dp[0] = 1;

        for (int i = 0; i < m; i++) {
            if (obstacleGrid[i][0] == 1) {
                dp[0] = 0;
            }

            for (int j = 1; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    dp[j] = 0;
                } else {
                    dp[j] = dp[j] + dp[j - 1];
                }
            }
        }

        return dp[n - 1];
    }

    private int[][] transpose(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] transposed = new int[n][m];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }

        return transposed;
    }

    // Approach 3: In-place DP - O(m*n) time, O(1) space
    public int uniquePathsWithObstaclesInPlace(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;

        if (obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return 0;
        }

        // Convert obstacles to -1 for distinction
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (obstacleGrid[i][j] == 1) {
                    obstacleGrid[i][j] = -1;
                }
            }
        }

        obstacleGrid[0][0] = 1;

        // Fill first row
        for (int j = 1; j < n; j++) {
            obstacleGrid[0][j] = (obstacleGrid[0][j] == -1) ? 0 : obstacleGrid[0][j - 1];
        }

        // Fill first column
        for (int i = 1; i < m; i++) {
            obstacleGrid[i][0] = (obstacleGrid[i][0] == -1) ? 0 : obstacleGrid[i - 1][0];
        }

        // Fill the rest
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (obstacleGrid[i][j] == -1) {
                    obstacleGrid[i][j] = 0;
                } else {
                    obstacleGrid[i][j] = obstacleGrid[i - 1][j] + obstacleGrid[i][j - 1];
                }
            }
        }

        return obstacleGrid[m - 1][n - 1];
    }

    // Approach 4: Memoization - O(m*n) time, O(m*n) space
    public int uniquePathsWithObstaclesMemo(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;

        if (obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return 0;
        }

        Integer[][] memo = new Integer[m][n];
        return uniquePathsMemoHelper(obstacleGrid, 0, 0, memo);
    }

    private int uniquePathsMemoHelper(int[][] obstacleGrid, int i, int j, Integer[][] memo) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;

        // Out of bounds or obstacle
        if (i >= m || j >= n || obstacleGrid[i][j] == 1) {
            return 0;
        }

        // Reached destination
        if (i == m - 1 && j == n - 1) {
            return 1;
        }

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int paths = uniquePathsMemoHelper(obstacleGrid, i + 1, j, memo) +
                uniquePathsMemoHelper(obstacleGrid, i, j + 1, memo);

        memo[i][j] = paths;
        return paths;
    }

    // Approach 5: Get All Unique Paths - O(2^(m+n)) time, O(2^(m+n)) space
    public java.util.List<java.util.List<String>> getAllUniquePaths(int[][] obstacleGrid) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;

        if (obstacleGrid[0][0] == 1 || obstacleGrid[m - 1][n - 1] == 1) {
            return new java.util.ArrayList<>();
        }

        java.util.List<java.util.List<String>> result = new java.util.ArrayList<>();
        java.util.List<String> currentPath = new java.util.ArrayList<>();

        getAllUniquePathsHelper(obstacleGrid, 0, 0, currentPath, result);
        return result;
    }

    private void getAllUniquePathsHelper(int[][] obstacleGrid, int i, int j,
            java.util.List<String> currentPath,
            java.util.List<java.util.List<String>> result) {
        int m = obstacleGrid.length, n = obstacleGrid[0].length;

        // Out of bounds or obstacle
        if (i >= m || j >= n || obstacleGrid[i][j] == 1) {
            return;
        }

        currentPath.add("(" + i + "," + j + ")");

        // Reached destination
        if (i == m - 1 && j == n - 1) {
            result.add(new java.util.ArrayList<>(currentPath));
        } else {
            // Go right
            getAllUniquePathsHelper(obstacleGrid, i, j + 1, currentPath, result);

            // Go down
            getAllUniquePathsHelper(obstacleGrid, i + 1, j, currentPath, result);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    public static void main(String[] args) {
        UniquePathsWithObstacles solution = new UniquePathsWithObstacles();

        System.out.println("=== Unique Paths with Obstacles Test Cases ===");

        // Test Case 1: Example from problem
        int[][] obstacleGrid1 = {
                { 0, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 0 }
        };
        System.out.println("Test 1 - Grid:");
        printGrid(obstacleGrid1);
        System.out.println("2D DP: " + solution.uniquePathsWithObstacles(obstacleGrid1));
        System.out.println("Optimized: " + solution.uniquePathsWithObstaclesOptimized(obstacleGrid1));
        System.out.println("Memoization: " + solution.uniquePathsWithObstaclesMemo(obstacleGrid1));

        java.util.List<java.util.List<String>> paths1 = solution.getAllUniquePaths(obstacleGrid1);
        System.out.println("All paths (" + paths1.size() + " total):");
        for (java.util.List<String> path : paths1) {
            System.out.println("  " + path);
        }
        System.out.println("Expected: 2\n");

        // Test Case 2: Start blocked
        int[][] obstacleGrid2 = {
                { 1, 0 },
                { 0, 0 }
        };
        System.out.println("Test 2 - Grid:");
        printGrid(obstacleGrid2);
        System.out.println("2D DP: " + solution.uniquePathsWithObstacles(obstacleGrid2));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        UniquePathsWithObstacles solution = new UniquePathsWithObstacles();

        int m = 50, n = 50;
        int[][] largeGrid = new int[m][n];

        // Add some random obstacles
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                largeGrid[i][j] = (Math.random() < 0.2) ? 1 : 0;
            }
        }
        largeGrid[0][0] = 0; // Ensure start is not blocked
        largeGrid[m - 1][n - 1] = 0; // Ensure end is not blocked

        System.out.println("=== Performance Test (Grid size: " + m + "x" + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.uniquePathsWithObstacles(largeGrid);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.uniquePathsWithObstaclesOptimized(largeGrid);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.uniquePathsWithObstaclesMemo(largeGrid);
        end = System.nanoTime();
        System.out.println("Memoization: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
