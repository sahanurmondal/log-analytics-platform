package graphs.easy;

/**
 * LeetCode 200: Number of Islands
 * https://leetcode.com/problems/number-of-islands/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 20+ interviews)
 *
 * Description: Given a 2D grid map of '1's (land) and '0's (water), count the
 * number of islands.
 *
 * Constraints:
 * - 1 <= m, n <= 300
 * 
 * Follow-up Questions:
 * 1. Can you solve with Union-Find?
 * 2. Can you solve with BFS?
 */
public class NumberOfIslands {
    // Approach 1: DFS - O(mn) time, O(mn) space
    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length, count = 0;
        for (int i = 0; i < m; i++)
            for (int j = 0; j < n; j++)
                if (grid[i][j] == '1') {
                    dfs(grid, i, j, m, n);
                    count++;
                }
        return count;
    }

    private void dfs(char[][] g, int i, int j, int m, int n) {
        if (i < 0 || j < 0 || i >= m || j >= n || g[i][j] != '1')
            return;
        g[i][j] = '0';
        dfs(g, i + 1, j, m, n);
        dfs(g, i - 1, j, m, n);
        dfs(g, i, j + 1, m, n);
        dfs(g, i, j - 1, m, n);
    }

    // Approach 2: Union-Find
    // ...implement if needed...
    public static void main(String[] args) {
        NumberOfIslands ni = new NumberOfIslands();
        char[][] grid = {
                { '1', '1', '0', '0', '0' },
                { '1', '1', '0', '0', '0' },
                { '0', '0', '1', '0', '0' },
                { '0', '0', '0', '1', '1' }
        };
        System.out.println(ni.numIslands(grid)); // Output: 3
    }
}
