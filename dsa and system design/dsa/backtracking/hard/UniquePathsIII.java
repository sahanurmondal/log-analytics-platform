package backtracking.hard;

/**
 * LeetCode 980: Unique Paths III
 * https://leetcode.com/problems/unique-paths-iii/
 *
 * Description: You are given an m x n integer array grid where grid[i][j] could
 * be:
 * 1 representing the starting square. There is exactly one starting square.
 * 2 representing the ending square. There is exactly one ending square.
 * 0 representing empty squares we can walk over.
 * -1 representing obstacles that we cannot walk over.
 * Return the number of 4-directional walks from the starting square to the
 * ending square,
 * that walk over every non-obstacle square exactly once.
 * 
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 20
 * - 1 <= m * n <= 20
 *
 * Follow-up:
 * - Can you optimize using bit manipulation?
 * 
 * Time Complexity: O(4^(m*n))
 * Space Complexity: O(m*n)
 * 
 * Company Tags: Google, Facebook
 */
public class UniquePathsIII {

    public int uniquePathsIII(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int startRow = 0, startCol = 0, emptyCount = 0;

        // Find start position and count empty squares
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    startRow = i;
                    startCol = j;
                    emptyCount++;
                } else if (grid[i][j] == 0) {
                    emptyCount++;
                }
            }
        }

        return dfs(grid, startRow, startCol, emptyCount);
    }

    private int dfs(int[][] grid, int row, int col, int remaining) {
        if (row < 0 || row >= grid.length || col < 0 || col >= grid[0].length || grid[row][col] == -1) {
            return 0;
        }

        if (grid[row][col] == 2) {
            return remaining == 0 ? 1 : 0;
        }

        int original = grid[row][col];
        grid[row][col] = -1; // Mark as visited

        int paths = dfs(grid, row + 1, col, remaining - 1) +
                dfs(grid, row - 1, col, remaining - 1) +
                dfs(grid, row, col + 1, remaining - 1) +
                dfs(grid, row, col - 1, remaining - 1);

        grid[row][col] = original; // Backtrack
        return paths;
    }

    public static void main(String[] args) {
        UniquePathsIII solution = new UniquePathsIII();

        // Test Case 1
        int[][] grid1 = { { 1, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 2, -1 } };
        System.out.println(solution.uniquePathsIII(grid1)); // Expected: 2

        // Test Case 2
        int[][] grid2 = { { 1, 0, 0, 0 }, { 0, 0, 0, 0 }, { 0, 0, 0, 2 } };
        System.out.println(solution.uniquePathsIII(grid2)); // Expected: 4

        // Test Case 3
        int[][] grid3 = { { 0, 1 }, { 2, 0 } };
        System.out.println(solution.uniquePathsIII(grid3)); // Expected: 0
    }
}
