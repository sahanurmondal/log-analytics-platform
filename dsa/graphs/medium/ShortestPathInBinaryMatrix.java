package graphs.medium;

import java.util.*;

/**
 * LeetCode 1091: Shortest Path in Binary Matrix
 * https://leetcode.com/problems/shortest-path-in-binary-matrix/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Find shortest clear path in a binary matrix from top-left to
 * bottom-right.
 *
 * Constraints:
 * - 1 <= n <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve with BFS?
 * 2. Can you optimize for space?
 */
public class ShortestPathInBinaryMatrix {
    // Approach 1: BFS - O(n^2) time, O(n^2) space
    public int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] != 0 || grid[n - 1][n - 1] != 0)
            return -1;
        int[][] dirs = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } };
        Queue<int[]> q = new LinkedList<>();
        q.offer(new int[] { 0, 0, 1 });
        grid[0][0] = 1;
        while (!q.isEmpty()) {
            int[] cur = q.poll();
            int x = cur[0], y = cur[1], d = cur[2];
            if (x == n - 1 && y == n - 1)
                return d;
            for (int[] dir : dirs) {
                int nx = x + dir[0], ny = y + dir[1];
                if (nx >= 0 && ny >= 0 && nx < n && ny < n && grid[nx][ny] == 0) {
                    q.offer(new int[] { nx, ny, d + 1 });
                    grid[nx][ny] = 1;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        ShortestPathInBinaryMatrix spbm = new ShortestPathInBinaryMatrix();
        int[][] grid = { { 0, 1 }, { 1, 0 } };
        System.out.println(spbm.shortestPathBinaryMatrix(grid) == 2);

        // No path
        int[][] grid2 = { { 0, 1, 1 }, { 1, 1, 0 }, { 1, 0, 0 } };
        System.out.println(spbm.shortestPathBinaryMatrix(grid2) == -1);

        // Single cell
        int[][] grid3 = { { 0 } };
        System.out.println(spbm.shortestPathBinaryMatrix(grid3) == 1);

        // All open
        int[][] grid4 = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
        System.out.println(spbm.shortestPathBinaryMatrix(grid4) == 5);

        // Blocked start
        int[][] grid5 = { { 1, 0 }, { 0, 0 } };
        System.out.println(spbm.shortestPathBinaryMatrix(grid5) == -1);

        // Large open grid
        int n = 10;
        int[][] grid6 = new int[n][n];
        System.out.println(spbm.shortestPathBinaryMatrix(grid6) == 2 * n - 1);
    }
}
