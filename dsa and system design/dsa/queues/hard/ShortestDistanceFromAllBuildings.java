package queues.hard;

/**
 * LeetCode 317: Shortest Distance from All Buildings
 * https://leetcode.com/problems/shortest-distance-from-all-buildings/
 *
 * Description:
 * You are given an m x n grid grid of values 0, 1, or 2, where each value
 * represents land, building, or obstacle.
 *
 * Constraints:
 * - 1 <= m, n <= 50
 * - grid[i][j] is 0, 1, or 2
 *
 * Follow-up:
 * - Can you optimize by starting BFS from buildings?
 * - Can you handle very large grids efficiently?
 */
import java.util.LinkedList;
import java.util.Queue;

/**
 * LeetCode 317: Shortest Distance from All Buildings
 * https://leetcode.com/problems/shortest-distance-from-all-buildings/
 *
 * Description:
 * You are given an m x n grid grid of values 0, 1, or 2, where each value
 * represents land, building, or obstacle.
 *
 * Constraints:
 * - 1 <= m, n <= 50
 * - grid[i][j] is 0, 1, or 2
 *
 * Follow-up:
 * - Can you optimize by starting BFS from buildings?
 * - Can you handle very large grids efficiently?
 */
public class ShortestDistanceFromAllBuildings {
    public int shortestDistance(int[][] grid) {
        if (grid == null || grid.length == 0)
            return -1;

        int m = grid.length, n = grid[0].length;
        int[][] totalDistance = new int[m][n];
        int[][] reachCount = new int[m][n];
        int buildingCount = 0;

        // Count total buildings
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1)
                    buildingCount++;
            }
        }

        // BFS from each building
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 1) {
                    bfs(grid, i, j, totalDistance, reachCount, m, n);
                }
            }
        }

        int minDistance = Integer.MAX_VALUE;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 0 && reachCount[i][j] == buildingCount) {
                    minDistance = Math.min(minDistance, totalDistance[i][j]);
                }
            }
        }

        return minDistance == Integer.MAX_VALUE ? -1 : minDistance;
    }

    private void bfs(int[][] grid, int startR, int startC, int[][] totalDistance,
            int[][] reachCount, int m, int n) {
        Queue<int[]> queue = new LinkedList<>();
        boolean[][] visited = new boolean[m][n];
        queue.offer(new int[] { startR, startC, 0 });
        visited[startR][startC] = true;

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r = curr[0], c = curr[1], dist = curr[2];

            for (int[] dir : directions) {
                int nr = r + dir[0], nc = c + dir[1];

                if (nr >= 0 && nr < m && nc >= 0 && nc < n &&
                        !visited[nr][nc] && grid[nr][nc] == 0) {
                    visited[nr][nc] = true;
                    totalDistance[nr][nc] += dist + 1;
                    reachCount[nr][nc]++;
                    queue.offer(new int[] { nr, nc, dist + 1 });
                }
            }
        }
    }

    public static void main(String[] args) {
        ShortestDistanceFromAllBuildings solution = new ShortestDistanceFromAllBuildings();
        int[][] grid1 = { { 1, 0, 2, 0, 1 }, { 0, 0, 0, 0, 0 }, { 0, 0, 1, 0, 0 } };
        System.out.println(solution.shortestDistance(grid1)); // 7

        int[][] grid2 = { { 1, 0 }, { 0, 0 } };
        System.out.println(solution.shortestDistance(grid2)); // 1

        // Edge Case: No valid position
        int[][] grid3 = { { 1, 1 }, { 1, 1 } };
        System.out.println(solution.shortestDistance(grid3)); // -1

        // Edge Case: Single building
        int[][] grid4 = { { 0, 0, 0 }, { 0, 1, 0 }, { 0, 0, 0 } };
        System.out.println(solution.shortestDistance(grid4)); // 2
    }
}
