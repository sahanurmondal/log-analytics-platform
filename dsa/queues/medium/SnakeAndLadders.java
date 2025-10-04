package queues.medium;

/**
 * LeetCode 909: Snakes and Ladders
 * https://leetcode.com/problems/snakes-and-ladders/
 *
 * Description:
 * You are given an n x n integer matrix board where the cells are labeled from
 * 1 to n² in a Boustrophedon style starting from the bottom left of the board.
 *
 * Constraints:
 * - n == board.length == board[i].length
 * - 2 <= n <= 20
 * - board[i][j] is either -1 or in the range [1, n²]
 * - The squares labeled 1 and n² do not have any ladders or snakes
 *
 * Follow-up:
 * - Can you solve it using BFS with a queue?
 * - Can you optimize for large boards?
 */
import java.util.LinkedList;
import java.util.Queue;

/**
 * LeetCode 909: Snakes and Ladders
 * https://leetcode.com/problems/snakes-and-ladders/
 *
 * Description:
 * You are given an n x n integer matrix board where the cells are labeled from
 * 1 to n² in a Boustrophedon style starting from the bottom left of the board.
 *
 * Constraints:
 * - n == board.length == board[i].length
 * - 2 <= n <= 20
 * - board[i][j] is either -1 or in the range [1, n²]
 * - The squares labeled 1 and n² do not have any ladders or snakes
 *
 * Follow-up:
 * - Can you solve it using BFS with a queue?
 * - Can you optimize for large boards?
 */
public class SnakeAndLadders {
    public int snakesAndLadders(int[][] board) {
        int n = board.length;
        boolean[] visited = new boolean[n * n + 1];
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { 1, 0 }); // {position, moves}
        visited[1] = true;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int pos = curr[0], moves = curr[1];

            if (pos == n * n)
                return moves;

            // Try all dice rolls from 1 to 6
            for (int i = 1; i <= 6; i++) {
                int next = pos + i;
                if (next > n * n)
                    break;

                int[] coords = getCoordinates(next, n);
                int row = coords[0], col = coords[1];

                // Check for snake or ladder
                if (board[row][col] != -1) {
                    next = board[row][col];
                }

                if (!visited[next]) {
                    visited[next] = true;
                    queue.offer(new int[] { next, moves + 1 });
                }
            }
        }

        return -1;
    }

    private int[] getCoordinates(int num, int n) {
        int row = (num - 1) / n;
        int col = (num - 1) % n;

        // Handle boustrophedon pattern
        if (row % 2 == 1) {
            col = n - 1 - col;
        }

        // Convert to board coordinates (0-indexed from top)
        row = n - 1 - row;

        return new int[] { row, col };
    }

    public static void main(String[] args) {
        SnakeAndLadders solution = new SnakeAndLadders();
        int[][] board1 = {
                { -1, -1, -1, -1, -1, -1 },
                { -1, -1, -1, -1, -1, -1 },
                { -1, -1, -1, -1, -1, -1 },
                { -1, 35, -1, -1, 13, -1 },
                { -1, -1, -1, -1, -1, -1 },
                { -1, 15, -1, -1, -1, -1 }
        };
        System.out.println(solution.snakesAndLadders(board1)); // 4

        // Edge Case: No snakes or ladders
        int[][] board2 = { { -1, -1 }, { -1, -1 } };
        System.out.println(solution.snakesAndLadders(board2)); // 1

        // Edge Case: Direct path blocked
        int[][] board3 = { { 1, 1, -1 }, { 1, 1, 1 }, { -1, 1, 1 } };
        System.out.println(solution.snakesAndLadders(board3)); // -1
    }
}
