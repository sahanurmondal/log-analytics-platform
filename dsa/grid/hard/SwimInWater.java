package grid.hard;

import java.util.*;

/**
 * LeetCode 778: Swim in Rising Water
 * https://leetcode.com/problems/swim-in-rising-water/
 *
 * Description:
 * You are given an n x n integer matrix grid where each value grid[i][j]
 * represents the elevation at that point (i, j).
 * The rain starts to fall. At time t, the depth of the water everywhere is t.
 * You can swim from a square to another
 * 4-directionally adjacent square if and only if the elevation of both squares
 * individually are at most t.
 * You can swim infinite distance in zero time. Of course, you must stay within
 * the boundaries of the grid during your swim.
 * Return the least time until you can reach the bottom right square (n - 1, n -
 * 1) if you start at the top left square (0, 0).
 *
 * Constraints:
 * - n == grid.length
 * - n == grid[i].length
 * - 1 <= n <= 50
 * - 0 <= grid[i][j] < n^2
 * - Each value grid[i][j] is unique
 */
public class SwimInWater {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public int swimInWater(int[][] grid) {
        int n = grid.length;
        int left = grid[0][0];
        int right = n * n - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (canSwim(grid, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private boolean canSwim(int[][] grid, int time) {
        int n = grid.length;
        if (grid[0][0] > time)
            return false;

        boolean[][] visited = new boolean[n][n];
        Queue<int[]> queue = new LinkedList<>();

        queue.offer(new int[] { 0, 0 });
        visited[0][0] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int x = curr[0], y = curr[1];

            if (x == n - 1 && y == n - 1)
                return true;

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < n && ny >= 0 && ny < n &&
                        !visited[nx][ny] && grid[nx][ny] <= time) {
                    visited[nx][ny] = true;
                    queue.offer(new int[] { nx, ny });
                }
            }
        }

        return false;
    }

    // Alternative solution using Dijkstra's algorithm
    public int swimInWaterDijkstra(int[][] grid) {
        int n = grid.length;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]);
        boolean[][] visited = new boolean[n][n];

        pq.offer(new int[] { 0, 0, grid[0][0] });

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int x = curr[0], y = curr[1], time = curr[2];

            if (visited[x][y])
                continue;
            visited[x][y] = true;

            if (x == n - 1 && y == n - 1)
                return time;

            for (int[] dir : directions) {
                int nx = x + dir[0];
                int ny = y + dir[1];

                if (nx >= 0 && nx < n && ny >= 0 && ny < n && !visited[nx][ny]) {
                    pq.offer(new int[] { nx, ny, Math.max(time, grid[nx][ny]) });
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        SwimInWater solution = new SwimInWater();

        int[][] grid1 = { { 0, 2 }, { 1, 3 } };
        System.out.println(solution.swimInWater(grid1)); // 3

        int[][] grid2 = { { 0, 1, 2, 3, 4 }, { 24, 23, 22, 21, 5 }, { 12, 13, 14, 15, 16 }, { 11, 17, 18, 19, 20 },
                { 10, 9, 8, 7, 6 } };
        System.out.println(solution.swimInWater(grid2)); // 16
    }
}
