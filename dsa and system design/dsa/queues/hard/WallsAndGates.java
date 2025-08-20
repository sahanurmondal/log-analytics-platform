package queues.hard;

import java.util.LinkedList;
import java.util.Queue;

/**
 * LeetCode 286: Walls and Gates
 * https://leetcode.com/problems/walls-and-gates/
 *
 * Description:
 * You are given an m x n grid rooms initialized with these three possible
 * values.
 *
 * Constraints:
 * - 1 <= m, n <= 250
 * - rooms[i][j] is -1, 0, or 2^31 - 1
 *
 * Values:
 * - -1: Wall or obstacle
 * - 0: Gate
 * - INF: Empty room (2^31 - 1)
 *
 * Follow-up:
 * - Can you solve it using multi-source BFS?
 * - Can you optimize for sparse gates?
 */
public class WallsAndGates {
    public void wallsAndGates(int[][] rooms) {
        if (rooms == null || rooms.length == 0)
            return;
        int m = rooms.length, n = rooms[0].length;
        Queue<int[]> queue = new LinkedList<>();

        // Add all gates to queue
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (rooms[i][j] == 0) {
                    queue.offer(new int[] { i, j });
                }
            }
        }

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int row = curr[0], col = curr[1];
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];
                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                        rooms[newRow][newCol] == Integer.MAX_VALUE) {
                    rooms[newRow][newCol] = rooms[row][col] + 1;
                    queue.offer(new int[] { newRow, newCol });
                }
            }
        }
    }

    public static void main(String[] args) {
        WallsAndGates solution = new WallsAndGates();
        int INF = Integer.MAX_VALUE;
        int[][] rooms1 = { { INF, -1, 0, INF }, { INF, INF, INF, -1 }, { INF, -1, INF, -1 }, { 0, -1, INF, INF } };
        solution.wallsAndGates(rooms1);
        System.out.println(java.util.Arrays.deepToString(rooms1)); // Should show distances

        // Edge Case: No gates
        int[][] rooms2 = { { INF, INF }, { INF, INF } };
        solution.wallsAndGates(rooms2);
        System.out.println(java.util.Arrays.deepToString(rooms2)); // Should remain unchanged

        // Edge Case: All gates
        int[][] rooms3 = { { 0, 0 }, { 0, 0 } };
        solution.wallsAndGates(rooms3);
        System.out.println(java.util.Arrays.deepToString(rooms3)); // Should remain 0
    }
}
