package grid.hard;

import java.util.*;

/**
 * LeetCode 1091: Shortest Path in Binary Matrix
 * https://leetcode.com/problems/shortest-path-in-binary-matrix/
 *
 * Description:
 * Given an n x n binary matrix grid, return the length of the shortest clear
 * path in the matrix.
 * If there is no clear path, return -1.
 * A clear path in a binary matrix is a path from the top-left cell (0, 0) to
 * the bottom-right cell (n - 1, n - 1) such that:
 * - All the visited cells of the path are 0.
 * - All the adjacent cells of the path are 8-directionally connected (i.e.,
 * they are different and they share an edge or a corner).
 * The length of a clear path is the number of visited cells of this path.
 *
 * Constraints:
 * - n == grid.length
 * - n == grid[i].length
 * - 1 <= n <= 100
 * - grid[i][j] is 0 or 1
 */
public class ShortestPathInBinaryMatrix {

    private int[][] directions = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 },
            { 1, 1 } };

    public int shortestPathBinaryMatrix(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1)
            return -1;
        if (n == 1)
            return 1;

        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[n][n];

        queue.offer(new int[] { 0, 0, 1 });
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1], path = curr[2];

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < n && ny >= 0 && ny < n &&
                        grid[nx][ny] == 0 && !visited[nx][ny]) {

                    if (nx == n - 1 && ny == n - 1) {
                        return path + 1;
                    }

                    visited[nx][ny] = true;
                    queue.offer(new int[] { nx, ny, path + 1 });
                }
            }
        }

        return -1;
    }

    // A* implementation
    public int shortestPathBinaryMatrixAStar(int[][] grid) {
        int n = grid.length;
        if (grid[0][0] == 1 || grid[n - 1][n - 1] == 1)
            return -1;
        if (n == 1)
            return 1;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[3] - b[3]);
        boolean[][] visited = new boolean[n][n];

        pq.offer(new int[] { 0, 0, 1, 1 + heuristic(0, 0, n - 1, n - 1) });
        visited[0][0] = true;

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int x = curr[0], y = curr[1], path = curr[2];

            if (x == n - 1 && y == n - 1) {
                return path;
            }

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < n && ny >= 0 && ny < n &&
                        grid[nx][ny] == 0 && !visited[nx][ny]) {

                    visited[nx][ny] = true;
                    int newPath = path + 1;
                    int f = newPath + heuristic(nx, ny, n - 1, n - 1);
                    pq.offer(new int[] { nx, ny, newPath, f });
                }
            }
        }

        return -1;
    }

    private int heuristic(int x1, int y1, int x2, int y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    public static void main(String[] args) {
        ShortestPathInBinaryMatrix solution = new ShortestPathInBinaryMatrix();

        int[][] grid1 = { { 0, 0, 0 }, { 1, 1, 0 }, { 1, 1, 0 } };
        System.out.println(solution.shortestPathBinaryMatrix(grid1)); // 4

        int[][] grid2 = { { 0, 1 }, { 1, 0 } };
        System.out.println(solution.shortestPathBinaryMatrix(grid2)); // 2
    }
}
