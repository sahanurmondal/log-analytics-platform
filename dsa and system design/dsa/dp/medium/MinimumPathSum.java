package dp.medium;

/**
 * LeetCode 64: Minimum Path Sum
 * https://leetcode.com/problems/minimum-path-sum/
 *
 * Description:
 * Given a m x n grid filled with non-negative numbers, find a path from top
 * left to bottom right which minimizes the sum of all numbers along its path.
 * Note: You can only move either down or right at any point in time.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[0].length
 * - 1 <= m, n <= 200
 * - 0 <= grid[i][j] <= 100
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Amazon, Microsoft, Google, Apple, Facebook, Bloomberg
 * Difficulty: Medium
 */
public class MinimumPathSum {

    // Approach 1: 2D Dynamic Programming - O(m*n) time, O(m*n) space
    public int minPathSum(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Create DP table
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];

        // Fill first row
        for (int j = 1; j < n; j++) {
            dp[0][j] = dp[0][j - 1] + grid[0][j];
        }

        // Fill first column
        for (int i = 1; i < m; i++) {
            dp[i][0] = dp[i - 1][0] + grid[i][0];
        }

        // Fill remaining cells
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + grid[i][j];
            }
        }

        return dp[m - 1][n - 1];
    }

    // Approach 2: Space Optimized (1D Array) - O(m*n) time, O(n) space
    public int minPathSumOptimized(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;

        int[] dp = new int[n];
        dp[0] = grid[0][0];

        // Fill first row
        for (int j = 1; j < n; j++) {
            dp[j] = dp[j - 1] + grid[0][j];
        }

        // Process remaining rows
        for (int i = 1; i < m; i++) {
            dp[0] += grid[i][0]; // Update first column

            for (int j = 1; j < n; j++) {
                dp[j] = Math.min(dp[j], dp[j - 1]) + grid[i][j];
            }
        }

        return dp[n - 1];
    }

    // Approach 3: In-place modification - O(m*n) time, O(1) space
    public int minPathSumInPlace(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;

        // Fill first row
        for (int j = 1; j < n; j++) {
            grid[0][j] += grid[0][j - 1];
        }

        // Fill first column
        for (int i = 1; i < m; i++) {
            grid[i][0] += grid[i - 1][0];
        }

        // Fill remaining cells
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                grid[i][j] += Math.min(grid[i - 1][j], grid[i][j - 1]);
            }
        }

        return grid[m - 1][n - 1];
    }

    // Approach 4: Recursive with Memoization - O(m*n) time, O(m*n) space
    public int minPathSumMemo(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return 0;
        }

        int m = grid.length;
        int n = grid[0].length;
        Integer[][] memo = new Integer[m][n];

        return minPathHelper(grid, 0, 0, memo);
    }

    private int minPathHelper(int[][] grid, int i, int j, Integer[][] memo) {
        int m = grid.length;
        int n = grid[0].length;

        // Base case: reached bottom-right
        if (i == m - 1 && j == n - 1) {
            return grid[i][j];
        }

        // Out of bounds
        if (i >= m || j >= n) {
            return Integer.MAX_VALUE;
        }

        if (memo[i][j] != null) {
            return memo[i][j];
        }

        int right = minPathHelper(grid, i, j + 1, memo);
        int down = minPathHelper(grid, i + 1, j, memo);

        memo[i][j] = grid[i][j] + Math.min(right, down);
        return memo[i][j];
    }

    // Approach 5: Get the actual path - O(m*n) time, O(m*n) space
    public java.util.List<int[]> getMinPath(int[][] grid) {
        if (grid == null || grid.length == 0 || grid[0].length == 0) {
            return new java.util.ArrayList<>();
        }

        int m = grid.length;
        int n = grid[0].length;

        // Create DP table
        int[][] dp = new int[m][n];
        dp[0][0] = grid[0][0];

        // Fill first row
        for (int j = 1; j < n; j++) {
            dp[0][j] = dp[0][j - 1] + grid[0][j];
        }

        // Fill first column
        for (int i = 1; i < m; i++) {
            dp[i][0] = dp[i - 1][0] + grid[i][0];
        }

        // Fill remaining cells
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = Math.min(dp[i - 1][j], dp[i][j - 1]) + grid[i][j];
            }
        }

        // Reconstruct path
        java.util.List<int[]> path = new java.util.ArrayList<>();
        int i = m - 1, j = n - 1;

        while (i > 0 || j > 0) {
            path.add(0, new int[] { i, j, grid[i][j] });

            if (i == 0) {
                j--;
            } else if (j == 0) {
                i--;
            } else {
                // Choose the direction that led to minimum path
                if (dp[i - 1][j] < dp[i][j - 1]) {
                    i--;
                } else {
                    j--;
                }
            }
        }

        path.add(0, new int[] { 0, 0, grid[0][0] });
        return path;
    }

    public static void main(String[] args) {
        MinimumPathSum solution = new MinimumPathSum();

        System.out.println("=== Minimum Path Sum Test Cases ===");

        // Test case 1: Normal case
        int[][] grid1 = { { 1, 3, 1 }, { 1, 5, 1 }, { 4, 2, 1 } };
        System.out.println("Grid 1:");
        printGrid(grid1);
        System.out.println("2D DP: " + solution.minPathSum(deepCopy(grid1))); // Expected: 7
        System.out.println("1D DP: " + solution.minPathSumOptimized(deepCopy(grid1))); // Expected: 7
        System.out.println("In-place: " + solution.minPathSumInPlace(deepCopy(grid1))); // Expected: 7
        System.out.println("Memoization: " + solution.minPathSumMemo(deepCopy(grid1))); // Expected: 7
        System.out.println("Path: " + pathToString(solution.getMinPath(deepCopy(grid1))));

        // Test case 2: 2x2 grid
        int[][] grid2 = { { 1, 2 }, { 3, 4 } };
        System.out.println("\nGrid 2:");
        printGrid(grid2);
        System.out.println("2D DP: " + solution.minPathSum(deepCopy(grid2))); // Expected: 7
        System.out.println("1D DP: " + solution.minPathSumOptimized(deepCopy(grid2))); // Expected: 7

        // Test case 3: Single row
        int[][] grid3 = { { 1, 3, 1, 5 } };
        System.out.println("\nGrid 3 (Single row):");
        printGrid(grid3);
        System.out.println("2D DP: " + solution.minPathSum(deepCopy(grid3))); // Expected: 10
        System.out.println("1D DP: " + solution.minPathSumOptimized(deepCopy(grid3))); // Expected: 10

        // Test case 4: Single column
        int[][] grid4 = { { 1 }, { 3 }, { 1 }, { 5 } };
        System.out.println("\nGrid 4 (Single column):");
        printGrid(grid4);
        System.out.println("2D DP: " + solution.minPathSum(deepCopy(grid4))); // Expected: 10
        System.out.println("Memoization: " + solution.minPathSumMemo(deepCopy(grid4))); // Expected: 10

        // Test case 5: Single cell
        int[][] grid5 = { { 5 } };
        System.out.println("\nGrid 5 (Single cell):");
        printGrid(grid5);
        System.out.println("2D DP: " + solution.minPathSum(deepCopy(grid5))); // Expected: 5

        // Test case 6: Larger grid
        int[][] grid6 = {
                { 1, 2, 3, 4 },
                { 5, 6, 7, 8 },
                { 9, 10, 11, 12 }
        };
        System.out.println("\nGrid 6 (Larger):");
        printGrid(grid6);
        System.out.println("2D DP: " + solution.minPathSum(deepCopy(grid6))); // Expected: 30
        System.out.println("1D DP: " + solution.minPathSumOptimized(deepCopy(grid6))); // Expected: 30
        System.out.println("Path: " + pathToString(solution.getMinPath(deepCopy(grid6))));

        // Performance comparison
        performanceTest();
    }

    private static int[][] deepCopy(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            System.out.println(java.util.Arrays.toString(row));
        }
    }

    private static String pathToString(java.util.List<int[]> path) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < path.size(); i++) {
            int[] cell = path.get(i);
            sb.append("(").append(cell[0]).append(",").append(cell[1]).append(":").append(cell[2]).append(")");
            if (i < path.size() - 1)
                sb.append(" -> ");
        }
        sb.append("]");
        return sb.toString();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison ===");
        MinimumPathSum solution = new MinimumPathSum();

        // Create large test grid
        int[][] largeGrid = new int[200][200];
        java.util.Random random = new java.util.Random(42);
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                largeGrid[i][j] = random.nextInt(101); // 0-100
            }
        }

        long start, end;

        // Test 2D DP approach
        start = System.nanoTime();
        int result1 = solution.minPathSum(deepCopy(largeGrid));
        end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        // Test 1D DP approach
        start = System.nanoTime();
        int result2 = solution.minPathSumOptimized(largeGrid);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
        
        start = System.nanoTime();
        int result3 = solution.minPathSumMemo(largeGrid);
        end = System.nanoTime();
        System.out.println("Memoization: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
