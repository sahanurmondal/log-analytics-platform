package queues.hard;

/**
 * LeetCode 778: Swim in Rising Water
 * https://leetcode.com/problems/swim-in-rising-water/
 *
 * Description:
 * On an n x n grid, each square grid[i][j] represents the elevation at that
 * point (i, j).
 *
 * Constraints:
 * - n == grid.length == grid[i].length
 * - 1 <= n <= 50
 * - 0 <= grid[i][j] < n²
 * - Each value grid[i][j] is unique
 *
 * Follow-up:
 * - Can you solve it using binary search + BFS?
 * - Can you use Dijkstra's algorithm?
 */
import java.util.PriorityQueue;

/**
 * LeetCode 778: Swim in Rising Water
 * https://leetcode.com/problems/swim-in-rising-water/
 *
 * Description:
 * On an n x n grid, each square grid[i][j] represents the elevation at that
 * point (i, j).
 *
 * Constraints:
 * - n == grid.length == grid[i].length
 * - 1 <= n <= 50
 * - 0 <= grid[i][j] < n²
 * - Each value grid[i][j] is unique
 *
 * Follow-up:
 * - Can you solve it using binary search + BFS?
 * - Can you use Dijkstra's algorithm?
 */
public class SwimInRisingWater {
    public int swimInWater(int[][] grid) {
        int n = grid.length;
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[2] - b[2]);
        boolean[][] visited = new boolean[n][n];
        pq.offer(new int[] { 0, 0, grid[0][0] });

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int row = curr[0], col = curr[1], time = curr[2];

            if (row == n - 1 && col == n - 1) {
                return time;
            }

            if (visited[row][col])
                continue;
            visited[row][col] = true;

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < n && newCol >= 0 && newCol < n &&
                        !visited[newRow][newCol]) {
                    pq.offer(new int[] { newRow, newCol, Math.max(time, grid[newRow][newCol]) });
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        SwimInRisingWater solution = new SwimInRisingWater();
        System.out.println(solution.swimInWater(new int[][] { { 0, 2 }, { 1, 3 } })); // 3
        System.out.println(solution.swimInWater(new int[][] { { 0, 1, 2, 3, 4 }, { 24, 23, 22, 21, 5 },
                { 12, 13, 14, 15, 16 }, { 11, 17, 18, 19, 20 }, { 10, 9, 8, 7, 6 } })); // 16
        // Edge Case: Single cell
        System.out.println(solution.swimInWater(new int[][] { { 0 } })); // 0
        // Edge Case: Straight path
        System.out.println(solution.swimInWater(new int[][] { { 0, 1 }, { 2, 3 } })); // 1
    }
}
