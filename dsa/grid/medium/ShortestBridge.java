package grid.medium;

import java.util.*;

/**
 * LeetCode 934: Shortest Bridge
 * https://leetcode.com/problems/shortest-bridge/
 *
 * Description:
 * You are given an n x n binary matrix grid where 1 represents land and 0
 * represents water.
 * An island is a 4-directionally connected group of 1's not connected to any
 * other 1's.
 * There are exactly two islands in grid.
 * You may change 0's to 1's to connect the two islands to form one island.
 * Return the smallest number of 0's you must flip to connect the two islands.
 *
 * Constraints:
 * - n == grid.length == grid[i].length
 * - 2 <= n <= 100
 * - grid[i][j] is either 0 or 1
 * - There are exactly two islands in grid
 */
public class ShortestBridge {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public int shortestBridge(int[][] grid) {
        int n = grid.length;
        Queue<int[]> queue = new LinkedList<>();
        boolean found = false;

        // Find first island and mark it as 2
        for (int i = 0; i < n && !found; i++) {
            for (int j = 0; j < n && !found; j++) {
                if (grid[i][j] == 1) {
                    dfs(grid, i, j, queue);
                    found = true;
                }
            }
        }

        // BFS to find shortest path to second island
        int steps = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();

                for (int[] dir : directions) {
                    int nr = curr[0] + dir[0];
                    int nc = curr[1] + dir[1];

                    if (nr >= 0 && nr < n && nc >= 0 && nc < n) {
                        if (grid[nr][nc] == 1) {
                            return steps;
                        }
                        if (grid[nr][nc] == 0) {
                            grid[nr][nc] = 2;
                            queue.offer(new int[] { nr, nc });
                        }
                    }
                }
            }
            steps++;
        }

        return -1;
    }

    private void dfs(int[][] grid, int r, int c, Queue<int[]> queue) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != 1) {
            return;
        }

        grid[r][c] = 2;
        queue.offer(new int[] { r, c });

        for (int[] dir : directions) {
            dfs(grid, r + dir[0], c + dir[1], queue);
        }
    }

    public static void main(String[] args) {
        ShortestBridge solution = new ShortestBridge();

        int[][] grid1 = { { 0, 1 }, { 1, 0 } };
        System.out.println(solution.shortestBridge(grid1)); // 1

        int[][] grid2 = { { 0, 1, 0 }, { 0, 0, 0 }, { 0, 0, 1 } };
        System.out.println(solution.shortestBridge(grid2)); // 2
    }
}
