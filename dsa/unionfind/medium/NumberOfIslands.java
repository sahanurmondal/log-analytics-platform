package unionfind.medium;

import unionfind.UnionFind;
import unionfind.UnionFind2D;

/**
 * LeetCode 200: Number of Islands
 * https://leetcode.com/problems/number-of-islands/
 *
 * Description:
 * Given an m x n 2D binary grid which represents a map of '1's (land) and '0's
 * (water),
 * return the number of islands.
 * An island is surrounded by water and is formed by connecting adjacent lands
 * horizontally or vertically.
 * You may assume all four edges of the grid are all surrounded by water.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 300
 * - grid[i][j] is '0' or '1'
 */
public class NumberOfIslands {

    public int numIslands(char[][] grid) {
        if (grid == null || grid.length == 0)
            return 0;

        int m = grid.length, n = grid[0].length;
        UnionFind uf = new UnionFind(m * n);

        int landCount = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    landCount++;
                }
            }
        }

        uf.setComponents(landCount);

        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    for (int[] dir : directions) {
                        int ni = i + dir[0];
                        int nj = j + dir[1];
                        if (ni >= 0 && ni < m && nj >= 0 && nj < n && grid[ni][nj] == '1') {
                            uf.union(i * n + j, ni * n + nj);
                        }
                    }
                }
            }
        }

        return uf.getComponents();
    }

    // Optimized DFS solution for Number of Islands using visited grid
    public int numIslandsDFS(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int m = grid.length, n = grid[0].length;
        boolean[][] visited = new boolean[m][n];
        int islands = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1' && !visited[i][j]) {
                    dfs(grid, visited, i, j, m, n);
                    islands++;
                }
            }
        }
        return islands;
    }

    private void dfs(char[][] grid, boolean[][] visited, int i, int j, int m, int n) {
        if (i < 0 || i >= m || j < 0 || j >= n || grid[i][j] != '1' || visited[i][j]) return;
        visited[i][j] = true;
        dfs(grid, visited, i + 1, j, m, n);
        dfs(grid, visited, i - 1, j, m, n);
        dfs(grid, visited, i, j + 1, m, n);
        dfs(grid, visited, i, j - 1, m, n);
    }

    // 2D Union-Find solution for Number of Islands
    public int numIslands2DUF(char[][] grid) {
        if (grid == null || grid.length == 0) return 0;
        int m = grid.length, n = grid[0].length;
        UnionFind2D uf = new UnionFind2D(m, n);
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == '1') {
                    uf.activate(i, j);
                    for (int[] dir : directions) {
                        int ni = i + dir[0], nj = j + dir[1];
                        if (ni >= 0 && ni < m && nj >= 0 && nj < n && grid[ni][nj] == '1') {
                            uf.union(i, j, ni, nj);
                        }
                    }
                }
            }
        }
        return uf.getComponentCount();
    }

    public static void main(String[] args) {
        NumberOfIslands solution = new NumberOfIslands();

        // Test case 1
        char[][] grid1 = {
                { '1', '1', '1', '1', '0' },
                { '1', '1', '0', '1', '0' },
                { '1', '1', '0', '0', '0' },
                { '0', '0', '0', '0', '0' }
        };
        System.out.println(solution.numIslands(grid1)); // UF: 1
        // Deep copy for DFS
        char[][] grid1Copy = new char[grid1.length][grid1[0].length];
        for (int i = 0; i < grid1.length; i++) grid1Copy[i] = grid1[i].clone();
        System.out.println(solution.numIslandsDFS(grid1Copy)); // DFS: 1
        // Deep copy for 2D UF
        char[][] grid1Copy2 = new char[grid1.length][grid1[0].length];
        for (int i = 0; i < grid1.length; i++) grid1Copy2[i] = grid1[i].clone();
        System.out.println(solution.numIslands2DUF(grid1Copy2)); // 2D UF: 1

        // Test case 2
        char[][] grid2 = {
                { '1', '1', '0', '0', '0' },
                { '1', '1', '0', '0', '0' },
                { '0', '0', '1', '0', '0' },
                { '0', '0', '0', '1', '1' }
        };
        System.out.println(solution.numIslands(grid2)); // UF: 3
        char[][] grid2Copy = new char[grid2.length][grid2[0].length];
        for (int i = 0; i < grid2.length; i++) grid2Copy[i] = grid2[i].clone();
        System.out.println(solution.numIslandsDFS(grid2Copy)); // DFS: 3
        char[][] grid2Copy2 = new char[grid2.length][grid2[0].length];
        for (int i = 0; i < grid2.length; i++) grid2Copy2[i] = grid2[i].clone();
        System.out.println(solution.numIslands2DUF(grid2Copy2)); // 2D UF: 3
    }
}
