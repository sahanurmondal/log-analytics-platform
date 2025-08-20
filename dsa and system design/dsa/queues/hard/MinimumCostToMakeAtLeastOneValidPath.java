package queues.hard;

/**
 * LeetCode 1368: Minimum Cost to Make at Least One Valid Path in a Grid
 * https://leetcode.com/problems/minimum-cost-to-make-at-least-one-valid-path-in-a-grid/
 *
 * Description:
 * Given a m x n grid. Each cell of the grid has a sign pointing to the next
 * cell you should visit.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 100
 * - grid[i][j] is 1, 2, 3, or 4
 *
 * Directions:
 * - 1: right, 2: left, 3: down, 4: up
 *
 * Follow-up:
 * - Can you solve it using 0-1 BFS?
 * - Can you optimize for very large grids?
 */
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * LeetCode 1368: Minimum Cost to Make at Least One Valid Path in a Grid
 * https://leetcode.com/problems/minimum-cost-to-make-at-least-one-valid-path-in-a-grid/
 *
 * Description:
 * Given a m x n grid. Each cell of the grid has a sign pointing to the next
 * cell you should visit.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 100
 * - grid[i][j] is 1, 2, 3, or 4
 *
 * Directions:
 * - 1: right, 2: left, 3: down, 4: up
 *
 * Follow-up:
 * - Can you solve it using 0-1 BFS?
 * - Can you optimize for very large grids?
 */
public class MinimumCostToMakeAtLeastOneValidPath {
    public int minCost(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };
        int[][] cost = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                cost[i][j] = Integer.MAX_VALUE;
            }
        }

        Deque<int[]> deque = new ArrayDeque<>();
        deque.offerFirst(new int[] { 0, 0 });
        cost[0][0] = 0;

        while (!deque.isEmpty()) {
            int[] curr = deque.pollFirst();
            int row = curr[0], col = curr[1];

            for (int dir = 0; dir < 4; dir++) {
                int newRow = row + directions[dir][0];
                int newCol = col + directions[dir][1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n) {
                    int newCost = cost[row][col] + (grid[row][col] == dir + 1 ? 0 : 1);
                    if (newCost < cost[newRow][newCol]) {
                        cost[newRow][newCol] = newCost;
                        if (grid[row][col] == dir + 1) {
                            deque.offerFirst(new int[] { newRow, newCol });
                        } else {
                            deque.offerLast(new int[] { newRow, newCol });
                        }
                    }
                }
            }
        }

        return cost[m - 1][n - 1];
    }

    public static void main(String[] args) {
        MinimumCostToMakeAtLeastOneValidPath solution = new MinimumCostToMakeAtLeastOneValidPath();
        System.out.println(
                solution.minCost(new int[][] { { 1, 1, 1, 1 }, { 2, 2, 2, 2 }, { 1, 1, 1, 1 }, { 2, 2, 2, 2 } })); // 3
        System.out.println(solution.minCost(new int[][] { { 1, 1, 3 }, { 3, 2, 2 }, { 1, 1, 4 } })); // 0
        System.out.println(solution.minCost(new int[][] { { 1, 2 }, { 4, 3 } })); // 1
        // Edge Case: Single cell
        System.out.println(solution.minCost(new int[][] { { 1 } })); // 0
        // Edge Case: Already valid path
        System.out.println(solution.minCost(new int[][] { { 1, 1, 1 }, { 3, 2, 2 }, { 1, 1, 4 } })); // 0
    }
}
