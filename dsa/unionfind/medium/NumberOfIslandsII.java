package unionfind.medium;

import unionfind.UnionFind;
import unionfind.UnionFind2D;

import java.util.*;

/**
 * LeetCode 305: Number of Islands II
 * https://leetcode.com/problems/number-of-islands-ii/
 *
 * Description:
 * You are given an empty 2D binary grid grid of size m x n. The grid represents
 * a map
 * where 0's represent water and 1's represent land. Initially, all the cells of
 * grid are water cells.
 * We can perform an addLand operation which turns the water at position into a
 * land.
 * Given an array positions where positions[i] = [ri, ci] is the position to
 * perform the ith addLand operation,
 * return an array that contains the number of islands after each addLand
 * operation.
 *
 * Constraints:
 * - 1 <= m, n, positions.length <= 10^4
 * - 1 <= m * n <= 10^4
 * - positions[i].length == 2
 * - 0 <= ri < m
 * - 0 <= ci < n
 *
 * Visual Example:
 * m = 3, n = 3, positions = [[0,0],[0,1],[1,2],[2,1]]
 * 
 * Step 1: [0,0] Step 2: [0,1] Step 3: [1,2] Step 4: [2,1]
 * 1 0 0 1 1 0 1 1 0 1 1 0
 * 0 0 0 → 1 0 0 0 → 1 0 0 1 → 2 0 1 1 → 3
 * 0 0 0 0 0 0 0 0 0 0 0 0
 * 
 * Output: [1,1,2,3]
 *
 * Follow-up:
 * - Can you solve it with better time complexity?
 * - How would you handle remove operations?
 */
public class NumberOfIslandsII {

    public List<Integer> numIslandsAfterEachAddLand(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        UnionFind uf = new UnionFind(m * n, true); // Use dynamic constructor
        boolean[][] grid = new boolean[m][n];
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

        for (int[] pos : positions) {
            int row = pos[0];
            int col = pos[1];
            int id = row * n + col;

            if (grid[row][col]) {
                result.add(uf.getComponents());
                continue;
            }

            grid[row][col] = true;
            uf.addComponent(id);

            // Check all 4 directions
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n && grid[newRow][newCol]) {
                    int neighborId = newRow * n + newCol;
                    uf.union(id, neighborId);
                }
            }

            result.add(uf.getComponents());
        }

        return result;
    }

    // True DFS solution for Number of Islands II
    public List<Integer> numIslandsAfterEachAddLandDFS(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        boolean[][] grid = new boolean[m][n];
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

        for (int[] pos : positions) {
            int row = pos[0], col = pos[1];

            if (grid[row][col]) {
                // Already land, count existing islands
                result.add(countIslandsDFS(grid, m, n));
                continue;
            }

            grid[row][col] = true;
            // Count islands after adding this land
            result.add(countIslandsDFS(grid, m, n));
        }
        return result;
    }

    private int countIslandsDFS(boolean[][] grid, int m, int n) {
        boolean[][] visited = new boolean[m][n];
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] && !visited[i][j]) {
                    dfs(grid, visited, i, j, m, n);
                    count++;
                }
            }
        }
        return count;
    }

    private void dfs(boolean[][] grid, boolean[][] visited, int i, int j, int m, int n) {
        if (i < 0 || i >= m || j < 0 || j >= n || !grid[i][j] || visited[i][j]) {
            return;
        }

        visited[i][j] = true;

        // Visit all 4 neighbors
        dfs(grid, visited, i + 1, j, m, n);
        dfs(grid, visited, i - 1, j, m, n);
        dfs(grid, visited, i, j + 1, m, n);
        dfs(grid, visited, i, j - 1, m, n);
    }

    // 2D Union-Find solution for Number of Islands II
    public List<Integer> numIslandsAfterEachAddLand2DUF(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        UnionFind2D uf = new UnionFind2D(m, n);
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        for (int[] pos : positions) {
            int r = pos[0], c = pos[1];
            if (uf.isActive(r, c)) {
                result.add(uf.getComponentCount());
                continue;
            }
            uf.activate(r, c);
            for (int[] d : directions) {
                int nr = r + d[0], nc = c + d[1];
                if (nr >= 0 && nr < m && nc >= 0 && nc < n && uf.isActive(nr, nc)) {
                    uf.union(r, c, nr, nc);
                }
            }
            result.add(uf.getComponentCount());
        }
        return result;
    }

    public static void main(String[] args) {
        NumberOfIslandsII solution = new NumberOfIslandsII();

        // Test case 1: Basic example
        System.out.println(solution.numIslandsAfterEachAddLand(3, 3,
                new int[][] { { 0, 0 }, { 0, 1 }, { 1, 2 }, { 2, 1 } })); // [1,1,2,3]
        System.out.println(solution.numIslandsAfterEachAddLandDFS(3, 3,
                new int[][] { { 0, 0 }, { 0, 1 }, { 1, 2 }, { 2, 1 } })); // [1,1,2,3]
        System.out.println(solution.numIslandsAfterEachAddLand2DUF(3, 3,
                new int[][] { { 0, 0 }, { 0, 1 }, { 1, 2 }, { 2, 1 } })); // 2D UF: [1,1,2,3]

        // Test case 2: All positions form one island
        System.out.println(solution.numIslandsAfterEachAddLand(1, 1,
                new int[][] { { 0, 0 } })); // [1]
        System.out.println(solution.numIslandsAfterEachAddLandDFS(1, 1,
                new int[][] { { 0, 0 } })); // [1]
        System.out.println(solution.numIslandsAfterEachAddLand2DUF(1, 1,
                new int[][] { { 0, 0 } })); // 2D UF: [1]

        // Test case 3: Merging islands
        System.out.println(solution.numIslandsAfterEachAddLand(3, 3,
                new int[][] { { 0, 0 }, { 0, 2 }, { 1, 1 }, { 0, 1 } })); // [1,2,3,2]
        System.out.println(solution.numIslandsAfterEachAddLandDFS(3, 3,
                new int[][] { { 0, 0 }, { 0, 2 }, { 1, 1 }, { 0, 1 } })); // [1,2,3,2]
        System.out.println(solution.numIslandsAfterEachAddLand2DUF(3, 3,
                new int[][] { { 0, 0 }, { 0, 2 }, { 1, 1 }, { 0, 1 } })); // 2D UF: [1,2,3,2]

        // Test case 4: Duplicate positions
        System.out.println(solution.numIslandsAfterEachAddLand(2, 2,
                new int[][] { { 0, 0 }, { 0, 0 }, { 1, 1 } })); // [1,1,2]
        System.out.println(solution.numIslandsAfterEachAddLandDFS(2, 2,
                new int[][] { { 0, 0 }, { 0, 0 }, { 1, 1 } })); // [1,1,2]
        System.out.println(solution.numIslandsAfterEachAddLand2DUF(2, 2,
                new int[][] { { 0, 0 }, { 0, 0 }, { 1, 1 } })); // 2D UF: [1,1,2]
    }
}
