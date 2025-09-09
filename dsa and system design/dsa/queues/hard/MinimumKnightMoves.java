package queues.hard;

/**
 * LeetCode 1197: Minimum Knight Moves
 * https://leetcode.com/problems/minimum-knight-moves/
 *
 * Description:
 * In an infinite chessboard with coordinates from -infinity to +infinity, you
 * have a knight at square [0, 0].
 *
 * Constraints:
 * - |x| + |y| <= 300
 *
 * ASCII Art:
 * Knight moves (L-shaped):
 * . 1 . 1 .
 * 1 . . . 1
 * . . K . .
 * 1 . . . 1
 * . 1 . 1 .
 *
 * Follow-up:
 * - Can you solve it using bidirectional BFS?
 * - Can you optimize for large coordinates?
 */
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * LeetCode 1197: Minimum Knight Moves
 * https://leetcode.com/problems/minimum-knight-moves/
 *
 * Description:
 * In an infinite chessboard with coordinates from -infinity to +infinity, you
 * have a knight at square [0, 0].
 *
 * Constraints:
 * - |x| + |y| <= 300
 *
 * ASCII Art:
 * Knight moves (L-shaped):
 * . 1 . 1 .
 * 1 . . . 1
 * . . K . .
 * 1 . . . 1
 * . 1 . 1 .
 *
 * Follow-up:
 * - Can you solve it using bidirectional BFS?
 * - Can you optimize for large coordinates?
 */
public class MinimumKnightMoves {
    public int minKnightMoves(int x, int y) {
        // Use symmetry to reduce search space
        x = Math.abs(x);
        y = Math.abs(y);

        if (x == 0 && y == 0)
            return 0;
        if (x + y == 1)
            return 3;

        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        queue.offer(new int[] { 0, 0, 0 });
        visited.add("0,0");

        int[][] directions = { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { 1, -2 }, { -1, 2 }, { -1, -2 } };

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int currX = curr[0], currY = curr[1], steps = curr[2];

            for (int[] dir : directions) {
                int newX = currX + dir[0];
                int newY = currY + dir[1];
                String key = newX + "," + newY;

                if (newX == x && newY == y) {
                    return steps + 1;
                }

                // Pruning: don't go too far from target
                if (!visited.contains(key) && newX >= -1 && newY >= -1 &&
                        newX <= x + 2 && newY <= y + 2) {
                    visited.add(key);
                    queue.offer(new int[] { newX, newY, steps + 1 });
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        MinimumKnightMoves solution = new MinimumKnightMoves();
        System.out.println(solution.minKnightMoves(2, 1)); // 1
        System.out.println(solution.minKnightMoves(5, 5)); // 4
        // Edge Case: Origin
        System.out.println(solution.minKnightMoves(0, 0)); // 0
        // Edge Case: Negative coordinates
        System.out.println(solution.minKnightMoves(-5, -5)); // 4
        // Edge Case: Large coordinates
        System.out.println(solution.minKnightMoves(300, 300)); // Should be computed efficiently
    }
}
