package dp.grid.optimization;

import java.util.Arrays;

/**
 * LeetCode 1463: Cherry Pickup II
 * https://leetcode.com/problems/cherry-pickup-ii/
 *
 * Description:
 * You are given a rows x cols matrix grid representing a field of cherries
 * where grid[i][j] represents the number of cherries that you can collect from
 * the (i, j) cell.
 * You have two robots that can collect cherries for you:
 * - Robot #1 is located at the top-left corner (0, 0), and
 * - Robot #2 is located at the top-right corner (0, cols - 1).
 * Return the maximum number of cherries collection using both robots by
 * following the rules below:
 * - From a cell (i, j), robots can move to cell (i + 1, j - 1), (i + 1, j), or
 * (i + 1, j + 1).
 * - When any robot passes through a cell, it picks up all cherries in that
 * cell.
 * - If both robots are in the same cell, only one of them takes the cherries.
 * - Both robots cannot move to the same cell.
 * - Both robots should reach the bottom row in grid.
 *
 * Constraints:
 * - rows == grid.length
 * - cols == grid[i].length
 * - 2 <= rows, cols <= 70
 * - 0 <= grid[i][j] <= 100
 *
 * Follow-up:
 * - What if there are k robots?
 * - Can you solve it in O(rows * cols^2) space?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class CherryPickupII {

    // Approach 1: 3D DP with Memoization - O(rows * cols^2) time, O(rows * cols^2)
    // space
    public int cherryPickupMemo(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Integer[][][] memo = new Integer[rows][cols][cols];
        return cherryPickupHelper(grid, 0, 0, cols - 1, memo);
    }

    private int cherryPickupHelper(int[][] grid, int row, int col1, int col2, Integer[][][] memo) {
        int rows = grid.length, cols = grid[0].length;

        // Base case: out of bounds
        if (col1 < 0 || col1 >= cols || col2 < 0 || col2 >= cols) {
            return Integer.MIN_VALUE;
        }

        // Base case: reached last row
        if (row == rows - 1) {
            if (col1 == col2) {
                return grid[row][col1];
            } else {
                return grid[row][col1] + grid[row][col2];
            }
        }

        if (memo[row][col1][col2] != null) {
            return memo[row][col1][col2];
        }

        int maxCherries = Integer.MIN_VALUE;

        // Try all 9 combinations of moves for both robots
        for (int nextCol1 = col1 - 1; nextCol1 <= col1 + 1; nextCol1++) {
            for (int nextCol2 = col2 - 1; nextCol2 <= col2 + 1; nextCol2++) {
                int cherries = cherryPickupHelper(grid, row + 1, nextCol1, nextCol2, memo);
                if (cherries != Integer.MIN_VALUE) {
                    maxCherries = Math.max(maxCherries, cherries);
                }
            }
        }

        // Add current row cherries
        if (maxCherries != Integer.MIN_VALUE) {
            if (col1 == col2) {
                maxCherries += grid[row][col1];
            } else {
                maxCherries += grid[row][col1] + grid[row][col2];
            }
        }

        memo[row][col1][col2] = maxCherries;
        return maxCherries;
    }

    // Approach 2: Bottom-up DP - O(rows * cols^2) time, O(rows * cols^2) space
    public int cherryPickupDP(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][][] dp = new int[rows][cols][cols];

        // Initialize with negative values
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Arrays.fill(dp[i][j], Integer.MIN_VALUE);
            }
        }

        // Base case: last row
        for (int col1 = 0; col1 < cols; col1++) {
            for (int col2 = 0; col2 < cols; col2++) {
                if (col1 == col2) {
                    dp[rows - 1][col1][col2] = grid[rows - 1][col1];
                } else {
                    dp[rows - 1][col1][col2] = grid[rows - 1][col1] + grid[rows - 1][col2];
                }
            }
        }

        // Fill DP table from bottom to top
        for (int row = rows - 2; row >= 0; row--) {
            for (int col1 = 0; col1 < cols; col1++) {
                for (int col2 = 0; col2 < cols; col2++) {
                    int maxCherries = Integer.MIN_VALUE;

                    // Try all 9 combinations
                    for (int nextCol1 = Math.max(0, col1 - 1); nextCol1 <= Math.min(cols - 1, col1 + 1); nextCol1++) {
                        for (int nextCol2 = Math.max(0, col2 - 1); nextCol2 <= Math.min(cols - 1,
                                col2 + 1); nextCol2++) {
                            if (dp[row + 1][nextCol1][nextCol2] != Integer.MIN_VALUE) {
                                maxCherries = Math.max(maxCherries, dp[row + 1][nextCol1][nextCol2]);
                            }
                        }
                    }

                    if (maxCherries != Integer.MIN_VALUE) {
                        if (col1 == col2) {
                            dp[row][col1][col2] = maxCherries + grid[row][col1];
                        } else {
                            dp[row][col1][col2] = maxCherries + grid[row][col1] + grid[row][col2];
                        }
                    }
                }
            }
        }

        return dp[0][0][cols - 1];
    }

    // Approach 3: Space Optimized DP - O(rows * cols^2) time, O(cols^2) space
    public int cherryPickupOptimized(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][] prev = new int[cols][cols];
        int[][] curr = new int[cols][cols];

        // Initialize with negative values
        for (int i = 0; i < cols; i++) {
            Arrays.fill(prev[i], Integer.MIN_VALUE);
            Arrays.fill(curr[i], Integer.MIN_VALUE);
        }

        // Base case: last row
        for (int col1 = 0; col1 < cols; col1++) {
            for (int col2 = 0; col2 < cols; col2++) {
                if (col1 == col2) {
                    prev[col1][col2] = grid[rows - 1][col1];
                } else {
                    prev[col1][col2] = grid[rows - 1][col1] + grid[rows - 1][col2];
                }
            }
        }

        // Fill DP table from bottom to top
        for (int row = rows - 2; row >= 0; row--) {
            for (int col1 = 0; col1 < cols; col1++) {
                for (int col2 = 0; col2 < cols; col2++) {
                    int maxCherries = Integer.MIN_VALUE;

                    // Try all 9 combinations
                    for (int nextCol1 = Math.max(0, col1 - 1); nextCol1 <= Math.min(cols - 1, col1 + 1); nextCol1++) {
                        for (int nextCol2 = Math.max(0, col2 - 1); nextCol2 <= Math.min(cols - 1,
                                col2 + 1); nextCol2++) {
                            if (prev[nextCol1][nextCol2] != Integer.MIN_VALUE) {
                                maxCherries = Math.max(maxCherries, prev[nextCol1][nextCol2]);
                            }
                        }
                    }

                    if (maxCherries != Integer.MIN_VALUE) {
                        if (col1 == col2) {
                            curr[col1][col2] = maxCherries + grid[row][col1];
                        } else {
                            curr[col1][col2] = maxCherries + grid[row][col1] + grid[row][col2];
                        }
                    }
                }
            }

            // Swap arrays
            int[][] temp = prev;
            prev = curr;
            curr = temp;

            // Reset curr array
            for (int i = 0; i < cols; i++) {
                Arrays.fill(curr[i], Integer.MIN_VALUE);
            }
        }

        return prev[0][cols - 1];
    }

    // Approach 4: Iterative with Path Tracking - O(rows * cols^2) time, O(cols^2)
    // space
    public int cherryPickupWithPath(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        int[][] dp = new int[cols][cols];
        int[][] newDp = new int[cols][cols];

        // Initialize
        dp[0][cols - 1] = grid[0][0] + grid[0][cols - 1];

        for (int row = 1; row < rows; row++) {
            // Reset new DP array
            for (int i = 0; i < cols; i++) {
                Arrays.fill(newDp[i], Integer.MIN_VALUE);
            }

            for (int col1 = 0; col1 < cols; col1++) {
                for (int col2 = 0; col2 < cols; col2++) {
                    if (dp[col1][col2] == Integer.MIN_VALUE)
                        continue;

                    // Try all 9 combinations of moves
                    for (int nextCol1 = Math.max(0, col1 - 1); nextCol1 <= Math.min(cols - 1, col1 + 1); nextCol1++) {
                        for (int nextCol2 = Math.max(0, col2 - 1); nextCol2 <= Math.min(cols - 1,
                                col2 + 1); nextCol2++) {
                            int cherries = dp[col1][col2];

                            if (nextCol1 == nextCol2) {
                                cherries += grid[row][nextCol1];
                            } else {
                                cherries += grid[row][nextCol1] + grid[row][nextCol2];
                            }

                            newDp[nextCol1][nextCol2] = Math.max(newDp[nextCol1][nextCol2], cherries);
                        }
                    }
                }
            }

            // Swap arrays
            int[][] temp = dp;
            dp = newDp;
            newDp = temp;
        }

        // Find maximum value in last row
        int maxCherries = 0;
        for (int col1 = 0; col1 < cols; col1++) {
            for (int col2 = 0; col2 < cols; col2++) {
                maxCherries = Math.max(maxCherries, dp[col1][col2]);
            }
        }

        return maxCherries;
    }

    // Approach 5: DFS with Pruning - O(rows * cols^2) time, O(rows) space
    public int cherryPickupDFS(int[][] grid) {
        int rows = grid.length, cols = grid[0].length;
        Integer[][][] memo = new Integer[rows][cols][cols];
        return Math.max(0, dfs(grid, 0, 0, cols - 1, memo));
    }

    private int dfs(int[][] grid, int row, int col1, int col2, Integer[][][] memo) {
        int rows = grid.length, cols = grid[0].length;

        if (row >= rows || col1 < 0 || col1 >= cols || col2 < 0 || col2 >= cols) {
            return Integer.MIN_VALUE;
        }

        if (memo[row][col1][col2] != null) {
            return memo[row][col1][col2];
        }

        int result = 0;

        // Collect cherries from current position
        if (col1 == col2) {
            result += grid[row][col1];
        } else {
            result += grid[row][col1] + grid[row][col2];
        }

        if (row < rows - 1) {
            int maxNext = Integer.MIN_VALUE;

            // Try all 9 combinations for next row
            for (int nextCol1 = col1 - 1; nextCol1 <= col1 + 1; nextCol1++) {
                for (int nextCol2 = col2 - 1; nextCol2 <= col2 + 1; nextCol2++) {
                    maxNext = Math.max(maxNext, dfs(grid, row + 1, nextCol1, nextCol2, memo));
                }
            }

            if (maxNext != Integer.MIN_VALUE) {
                result += maxNext;
            } else {
                result = Integer.MIN_VALUE;
            }
        }

        memo[row][col1][col2] = result;
        return result;
    }

    public static void main(String[] args) {
        CherryPickupII solution = new CherryPickupII();

        System.out.println("=== Cherry Pickup II Test Cases ===");

        // Test Case 1: Example from problem
        int[][] grid1 = {
                { 3, 1, 1 },
                { 2, 5, 1 },
                { 1, 5, 5 },
                { 2, 1, 1 }
        };
        System.out.println("Test 1 - Grid:");
        printGrid(grid1);
        System.out.println("Memoization: " + solution.cherryPickupMemo(grid1));
        System.out.println("DP: " + solution.cherryPickupDP(grid1));
        System.out.println("Optimized: " + solution.cherryPickupOptimized(grid1));
        System.out.println("With Path: " + solution.cherryPickupWithPath(grid1));
        System.out.println("DFS: " + solution.cherryPickupDFS(grid1));
        System.out.println("Expected: 24\n");

        // Test Case 2: Another example
        int[][] grid2 = {
                { 1, 0, 0, 0, 0, 0, 1 },
                { 2, 0, 0, 0, 0, 3, 0 },
                { 2, 0, 9, 0, 0, 0, 0 },
                { 0, 3, 0, 5, 4, 0, 0 },
                { 1, 0, 2, 3, 0, 0, 6 }
        };
        System.out.println("Test 2 - Grid:");
        printGrid(grid2);
        System.out.println("Optimized: " + solution.cherryPickupOptimized(grid2));
        System.out.println("Expected: 28\n");

        performanceTest();
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        CherryPickupII solution = new CherryPickupII();

        // Generate large test grid
        int rows = 70, cols = 70;
        int[][] largeGrid = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                largeGrid[i][j] = (int) (Math.random() * 100);
            }
        }

        System.out.println("=== Performance Test (Grid size: " + rows + "x" + cols + ") ===");

        long start = System.nanoTime();
        int result1 = solution.cherryPickupDP(largeGrid);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.cherryPickupOptimized(largeGrid);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.cherryPickupWithPath(largeGrid);
        end = System.nanoTime();
        System.out.println("With Path: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
