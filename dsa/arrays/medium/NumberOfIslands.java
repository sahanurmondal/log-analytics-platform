package arrays.medium;

/**
 * LeetCode 200: Number of Islands
 * https://leetcode.com/problems/number-of-islands/
 *
 * Description:
 * Given an m x n 2D binary grid which represents a map of '1's (land) and '0's
 * (water),
 * return the number of islands. An island is surrounded by water and is formed
 * by connecting
 * adjacent lands horizontally or vertically.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 300
 * - grid[i][j] is '0' or '1'
 *
 * Follow-up:
 * - Can you solve it using Union-Find?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n) - worst case for recursion stack
 * 
 * Algorithm:
 * 1. Iterate through each cell in the grid
 * 2. When find '1', increment island count and start DFS
 * 3. DFS marks all connected '1's as visited by changing to '0'
 */
public class NumberOfIslands {
    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0)
            return 0;

        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == '1') {
                    count++;
                    dfs(grid, i, j);
                }
            }
        }

        return count;
    }

    private void dfs(char[][] grid, int i, int j) {
        if (i < 0 || i >= grid.length || j < 0 || j >= grid[0].length || grid[i][j] == '0') {
            return;
        }

        grid[i][j] = '0'; // Mark as visited

        // Visit all 4 directions
        dfs(grid, i + 1, j);
        dfs(grid, i - 1, j);
        dfs(grid, i, j + 1);
        dfs(grid, i, j - 1);
    }

    public static void main(String[] args) {
        NumberOfIslands solution = new NumberOfIslands();

        // Test Case 1: Normal case
        char[][] grid1 = {
                { '1', '1', '1', '1', '0' },
                { '1', '1', '0', '1', '0' },
                { '1', '1', '0', '0', '0' },
                { '0', '0', '0', '0', '0' }
        };
        System.out.println(solution.numIslands(grid1)); // Expected: 1

        // Test Case 2: Edge case - multiple islands
        char[][] grid2 = {
                { '1', '1', '0', '0', '0' },
                { '1', '1', '0', '0', '0' },
                { '0', '0', '1', '0', '0' },
                { '0', '0', '0', '1', '1' }
        };
        System.out.println(solution.numIslands(grid2)); // Expected: 3

        // Test Case 3: Corner case - all water
        char[][] grid3 = {
                { '0', '0', '0' },
                { '0', '0', '0' }
        };
        System.out.println(solution.numIslands(grid3)); // Expected: 0

        // Test Case 4: Large input - all land
        char[][] grid4 = {
                { '1', '1', '1' },
                { '1', '1', '1' },
                { '1', '1', '1' }
        };
        System.out.println(solution.numIslands(grid4)); // Expected: 1

        // Test Case 5: Minimum input - single cell land
        char[][] grid5 = { { '1' } };
        System.out.println(solution.numIslands(grid5)); // Expected: 1

        // Test Case 6: Special case - single cell water
        char[][] grid6 = { { '0' } };
        System.out.println(solution.numIslands(grid6)); // Expected: 0

        // Test Case 7: Boundary case - diagonal pattern
        char[][] grid7 = {
                { '1', '0', '0' },
                { '0', '1', '0' },
                { '0', '0', '1' }
        };
        System.out.println(solution.numIslands(grid7)); // Expected: 3

        // Test Case 8: Connected vertically
        char[][] grid8 = {
                { '1' },
                { '1' },
                { '1' }
        };
        System.out.println(solution.numIslands(grid8)); // Expected: 1

        // Test Case 9: Connected horizontally
        char[][] grid9 = { { '1', '1', '1' } };
        System.out.println(solution.numIslands(grid9)); // Expected: 1

        // Test Case 10: Checkerboard pattern
        char[][] grid10 = {
                { '1', '0', '1' },
                { '0', '1', '0' },
                { '1', '0', '1' }
        };
        System.out.println(solution.numIslands(grid10)); // Expected: 5
    }
}
