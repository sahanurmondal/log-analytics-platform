package design.medium;

/**
 * LeetCode 348: Design Tic-Tac-Toe
 * https://leetcode.com/problems/design-tic-tac-toe/
 *
 * Description: Assume the following rules are for the tic-tac-toe game on an n
 * × n board between two players:
 * 1. A move is guaranteed to be valid and is placed on an empty block.
 * 2. Once a winning condition is reached, no more moves are allowed.
 * 3. A player who succeeds in placing n of their marks in a horizontal,
 * vertical, or diagonal row wins the game.
 * 
 * Constraints:
 * - 2 <= n <= 100
 * - player is 1 or 2
 * - 0 <= row, col < n
 * - (row, col) are unique for each different call to move
 *
 * Follow-up:
 * - Can you do better than O(n^2) per move?
 * 
 * Time Complexity: O(1) per move
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class TicTacToe {

    private int[] rows;
    private int[] cols;
    private int diagonal;
    private int antiDiagonal;
    private int n;

    public TicTacToe(int n) {
        this.n = n;
        rows = new int[n];
        cols = new int[n];
        diagonal = 0;
        antiDiagonal = 0;
    }

    public int move(int row, int col, int player) {
        int value = (player == 1) ? 1 : -1;

        rows[row] += value;
        cols[col] += value;

        if (row == col) {
            diagonal += value;
        }

        if (row + col == n - 1) {
            antiDiagonal += value;
        }

        // Check for win condition
        if (Math.abs(rows[row]) == n ||
                Math.abs(cols[col]) == n ||
                Math.abs(diagonal) == n ||
                Math.abs(antiDiagonal) == n) {
            return player;
        }

        return 0;
    }

    // Alternative implementation - 2D board (O(n^2) space, O(n) time per move)
    static class TicTacToeBoard {
        private int[][] board;
        private int n;

        public TicTacToeBoard(int n) {
            this.n = n;
            board = new int[n][n];
        }

        public int move(int row, int col, int player) {
            board[row][col] = player;

            // Check row
            boolean rowWin = true;
            for (int c = 0; c < n; c++) {
                if (board[row][c] != player) {
                    rowWin = false;
                    break;
                }
            }
            if (rowWin)
                return player;

            // Check column
            boolean colWin = true;
            for (int r = 0; r < n; r++) {
                if (board[r][col] != player) {
                    colWin = false;
                    break;
                }
            }
            if (colWin)
                return player;

            // Check diagonal
            if (row == col) {
                boolean diagWin = true;
                for (int i = 0; i < n; i++) {
                    if (board[i][i] != player) {
                        diagWin = false;
                        break;
                    }
                }
                if (diagWin)
                    return player;
            }

            // Check anti-diagonal
            if (row + col == n - 1) {
                boolean antiDiagWin = true;
                for (int i = 0; i < n; i++) {
                    if (board[i][n - 1 - i] != player) {
                        antiDiagWin = false;
                        break;
                    }
                }
                if (antiDiagWin)
                    return player;
            }

            return 0;
        }
    }

    public static void main(String[] args) {
        TicTacToe ticTacToe = new TicTacToe(3);
        System.out.println(ticTacToe.move(0, 0, 1)); // Expected: 0
        System.out.println(ticTacToe.move(0, 2, 2)); // Expected: 0
        System.out.println(ticTacToe.move(2, 2, 1)); // Expected: 0
        System.out.println(ticTacToe.move(1, 1, 2)); // Expected: 0
        System.out.println(ticTacToe.move(2, 0, 1)); // Expected: 0
        System.out.println(ticTacToe.move(1, 0, 2)); // Expected: 0
        System.out.println(ticTacToe.move(2, 1, 1)); // Expected: 1
    }
}
