package graphs.easy;

/**
 * LeetCode 695: Max Area of Island
 * https://leetcode.com/problems/max-area-of-island/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given a 2D grid, return the area of the largest island.
 *
 * Constraints:
 * - 1 <= m, n <= 50
 * 
 * Follow-up Questions:
 * 1. Can you solve with BFS?
 */
public class MaxAreaOfIsland {
    // Approach 1: DFS - O(mn) time, O(mn) space
    public int maxAreaOfIsland(int[][] grid) {
        int m = grid.length, n = grid[0].length, max = 0;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (grid[i][j] == 1)
                    max = Math.max(max, dfs(grid, i, j, m, n));
        return max;
    }

    private int dfs(int[][] g, int i, int j, int m, int n) {
        if (i < 0 || j < 0 || i >= m || j >= n || g[i][j] != 1)
            return 0;
        g[i][j] = 0;
        return 1 + dfs(g, i + 1, j, m, n) + dfs(g, i - 1, j, m, n) + dfs(g, i, j + 1, m, n) + dfs(g, i, j - 1, m, n);
    }

    // Approach 2: BFS
    // ...implement if needed...
    public static void main(String[] args) {
        MaxAreaOfIsland mai = new MaxAreaOfIsland();
        int[][] grid = { { 0, 0, 1, 0, 0 }, { 1, 1, 1, 0, 0 }, { 0, 0, 0, 0, 1 }, { 1, 1, 0, 1, 1 } };
        System.out.println(mai.maxAreaOfIsland(grid) == 5);

        // All water
        int[][] grid2 = { { 0, 0 }, { 0, 0 } };
        System.out.println(mai.maxAreaOfIsland(grid2) == 0);

        // All land
        int[][] grid3 = { { 1, 1 }, { 1, 1 } };
        System.out.println(mai.maxAreaOfIsland(grid3) == 4);

        // Single cell island
        int[][] grid4 = { { 1 } };
        System.out.println(mai.maxAreaOfIsland(grid4) == 1);

        // Multiple islands
        int[][] grid5 = { { 1, 0, 1 }, { 0, 1, 0 }, { 1, 0, 1 } };
        System.out.println(mai.maxAreaOfIsland(grid5) == 1);
    }
}
