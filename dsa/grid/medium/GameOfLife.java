package grid.medium;

/**
 * LeetCode 289: Game of Life
 * https://leetcode.com/problems/game-of-life/
 *
 * Description:
 * Given a board with m by n cells, each cell has an initial state: live (1) or
 * dead (0).
 * Each cell interacts with its eight neighbors using the following four rules:
 * 1. Any live cell with fewer than two live neighbors dies (under-population).
 * 2. Any live cell with two or three live neighbors lives on to the next
 * generation.
 * 3. Any live cell with more than three live neighbors dies (over-population).
 * 4. Any dead cell with exactly three live neighbors becomes a live cell
 * (reproduction).
 *
 * Constraints:
 * - m == board.length
 * - n == board[i].length
 * - 1 <= m, n <= 25
 * - board[i][j] is 0 or 1
 */
public class GameOfLife {

    public void gameOfLife(int[][] board) {
        int m = board.length, n = board[0].length;

        // Use state encoding: 0->1: 2, 1->0: 3
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int liveNeighbors = countLiveNeighbors(board, i, j);

                if (board[i][j] == 1) {
                    if (liveNeighbors < 2 || liveNeighbors > 3) {
                        board[i][j] = 3; // 1->0
                    }
                } else {
                    if (liveNeighbors == 3) {
                        board[i][j] = 2; // 0->1
                    }
                }
            }
        }

        // Decode states
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] %= 2;
            }
        }
    }

    private int countLiveNeighbors(int[][] board, int row, int col) {
        int count = 0;
        int m = board.length, n = board[0].length;

        for (int i = Math.max(0, row - 1); i <= Math.min(m - 1, row + 1); i++) {
            for (int j = Math.max(0, col - 1); j <= Math.min(n - 1, col + 1); j++) {
                if (i == row && j == col)
                    continue;
                count += board[i][j] == 1 || board[i][j] == 3 ? 1 : 0;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        GameOfLife solution = new GameOfLife();

        int[][] board = { { 0, 1, 0 }, { 0, 0, 1 }, { 1, 1, 1 }, { 0, 0, 0 } };
        solution.gameOfLife(board);

        for (int[] row : board) {
            System.out.println(java.util.Arrays.toString(row));
        }
        // Output: [[0,0,0],[1,0,1],[0,1,1],[0,1,0]]
    }
}
