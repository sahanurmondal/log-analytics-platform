package grid.hard;

import java.util.*;

/**
 * LeetCode 407: Trapping Rain Water II
 * https://leetcode.com/problems/trapping-rain-water-ii/
 *
 * Description:
 * Given an m x n integer matrix heightMap representing the height of each unit
 * cell in a 2D elevation map,
 * return the volume of water it can trap after raining.
 *
 * Constraints:
 * - m == heightMap.length
 * - n == heightMap[i].length
 * - 1 <= m, n <= 200
 * - 0 <= heightMap[i][j] <= 2 * 10^4
 */
public class TrappingRainWater2D {

    class Cell {
        int row, col, height;

        Cell(int row, int col, int height) {
            this.row = row;
            this.col = col;
            this.height = height;
        }
    }

    public int trapRainWater(int[][] heightMap) {
        if (heightMap == null || heightMap.length == 0)
            return 0;

        int m = heightMap.length, n = heightMap[0].length;
        PriorityQueue<Cell> pq = new PriorityQueue<>((a, b) -> a.height - b.height);
        boolean[][] visited = new boolean[m][n];

        // Add all border cells to priority queue
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || i == m - 1 || j == 0 || j == n - 1) {
                    pq.offer(new Cell(i, j, heightMap[i][j]));
                    visited[i][j] = true;
                }
            }
        }

        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        int water = 0;

        while (!pq.isEmpty()) {
            Cell curr = pq.poll();

            for (int[] dir : directions) {
                int nr = curr.row + dir[0];
                int nc = curr.col + dir[1];

                if (nr >= 0 && nr < m && nc >= 0 && nc < n && !visited[nr][nc]) {
                    visited[nr][nc] = true;
                    water += Math.max(0, curr.height - heightMap[nr][nc]);
                    pq.offer(new Cell(nr, nc, Math.max(heightMap[nr][nc], curr.height)));
                }
            }
        }

        return water;
    }

    public static void main(String[] args) {
        TrappingRainWater2D solution = new TrappingRainWater2D();

        int[][] heightMap = { { 1, 4, 3, 1, 3, 2 }, { 3, 2, 1, 3, 2, 4 }, { 2, 3, 3, 2, 3, 1 } };
        System.out.println(solution.trapRainWater(heightMap)); // 4
    }
}
