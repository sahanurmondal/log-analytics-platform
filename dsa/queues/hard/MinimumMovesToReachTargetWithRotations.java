package queues.hard;

/**
 * LeetCode 1210: Minimum Moves to Reach Target with Rotations
 * https://leetcode.com/problems/minimum-moves-to-reach-target-with-rotations/
 *
 * Description:
 * In an n*n grid, there is a snake that spans 2 cells and starts moving from
 * the top left corner.
 *
 * Constraints:
 * - 2 <= n <= 100
 * - grid consists only of 0's and 1's
 * - It is guaranteed that the snake can move from the starting position to the
 * target position
 *
 * ASCII Art:
 * Snake positions:
 * Horizontal: [head][tail]
 * Vertical: [head]
 * [tail]
 *
 * Follow-up:
 * - Can you solve it using BFS with state representation?
 * - Can you optimize for larger grids?
 */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * LeetCode 1210: Minimum Moves to Reach Target with Rotations
 * https://leetcode.com/problems/minimum-moves-to-reach-target-with-rotations/
 *
 * Description:
 * In an n*n grid, there is a snake that spans 2 cells and starts moving from
 * the top left corner.
 *
 * Constraints:
 * - 2 <= n <= 100
 * - grid consists only of 0's and 1's
 * - It is guaranteed that the snake can move from the starting position to the
 * target position
 *
 * ASCII Art:
 * Snake positions:
 * Horizontal: [head][tail]
 * Vertical: [head]
 * [tail]
 *
 * Follow-up:
 * - Can you solve it using BFS with state representation?
 * - Can you optimize for larger grids?
 */
public class MinimumMovesToReachTargetWithRotations {
    public int minimumMoves(int[][] grid) {
        int n = grid.length;
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // State: [tailRow, tailCol, headRow, headCol]
        queue.offer(new int[] { 0, 0, 0, 1 }); // Starting horizontal position
        visited.add("0,0,0,1");

        int moves = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                int tr = curr[0], tc = curr[1], hr = curr[2], hc = curr[3];

                // Check if reached target
                if (tr == n - 1 && tc == n - 2 && hr == n - 1 && hc == n - 1) {
                    return moves;
                }

                // Try all possible moves
                if (hr == tr) { // Horizontal
                    // Move right
                    if (hc + 1 < n && grid[hr][hc + 1] == 0) {
                        addState(queue, visited, tr, tc + 1, hr, hc + 1);
                    }
                    // Move down
                    if (hr + 1 < n && grid[tr + 1][tc] == 0 && grid[hr + 1][hc] == 0) {
                        addState(queue, visited, tr + 1, tc, hr + 1, hc);
                    }
                    // Rotate clockwise (to vertical)
                    if (hr + 1 < n && grid[hr + 1][hc] == 0 && grid[tr + 1][tc] == 0) {
                        addState(queue, visited, tr, tc, tr + 1, tc);
                    }
                } else { // Vertical
                    // Move right
                    if (tc + 1 < n && grid[tr][tc + 1] == 0 && grid[hr][hc + 1] == 0) {
                        addState(queue, visited, tr, tc + 1, hr, hc + 1);
                    }
                    // Move down
                    if (hr + 1 < n && grid[hr + 1][hc] == 0) {
                        addState(queue, visited, tr + 1, tc, hr + 1, hc);
                    }
                    // Rotate counterclockwise (to horizontal)
                    if (tc + 1 < n && grid[tr][tc + 1] == 0 && grid[hr][hc + 1] == 0) {
                        addState(queue, visited, tr, tc, tr, tc + 1);
                    }
                }
            }
            moves++;
        }

        return -1;
    }

    private void addState(Queue<int[]> queue, Set<String> visited, int tr, int tc, int hr, int hc) {
        String state = tr + "," + tc + "," + hr + "," + hc;
        if (!visited.contains(state)) {
            visited.add(state);
            queue.offer(new int[] { tr, tc, hr, hc });
        }
    }

    public static void main(String[] args) {
        MinimumMovesToReachTargetWithRotations solution = new MinimumMovesToReachTargetWithRotations();
        int[][] grid1 = { { 0, 0, 0, 0, 0, 1 }, { 1, 1, 0, 0, 1, 0 }, { 0, 0, 0, 0, 1, 1 }, { 0, 0, 1, 0, 1, 0 },
                { 0, 1, 1, 0, 0, 0 }, { 0, 1, 1, 0, 0, 0 } };
        System.out.println(solution.minimumMoves(grid1)); // 11

        int[][] grid2 = { { 0, 0, 1, 1, 1, 1 }, { 0, 0, 0, 0, 1, 1 }, { 1, 1, 0, 0, 0, 1 }, { 1, 1, 1, 0, 0, 1 },
                { 1, 1, 1, 0, 0, 1 }, { 1, 1, 1, 0, 0, 0 } };
        System.out.println(solution.minimumMoves(grid2)); // 9

        // Edge Case: Minimum grid
        int[][] grid3 = { { 0, 0 }, { 0, 0 } };
        System.out.println(solution.minimumMoves(grid3)); // 1
    }
}
