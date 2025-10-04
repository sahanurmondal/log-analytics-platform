package grid.medium;

/**
 * LeetCode 348: Design Tic-Tac-Toe
 * https://leetcode.com/problems/design-tic-tac-toe/
 *
 * Description:
 * Assume the following rules for Tic-Tac-Toe:
 * - A move is guaranteed to be valid and is placed on an empty block.
 * - Once a winning condition is reached, no more moves are allowed.
 * - A player who succeeds in placing n of their marks in a horizontal,
 * vertical, or diagonal row wins the game.
 * Implement the TicTacToe class:
 * - TicTacToe(int n) Initializes the object the size of the board n.
 * - int move(int row, int col, int player) Indicates that the player with id
 * player plays at the cell (row, col) of the board.
 * The move is guaranteed to be a valid move, and the board will not be in a
 * winning state.
 * Return 0 if there is no winning after the move, the player number if the
 * player wins after the move.
 *
 * Constraints:
 * - 2 <= n <= 100
 * - player is 1 or 2
 * - 0 <= row, col < n
 * - (row, col) are unique for each different call to move
 * - At most n^2 calls will be made to move
 */
public class DesignTicTacToe {

    private int[] rows;
    private int[] cols;
    private int diagonal;
    private int antiDiagonal;
    private int n;

    public DesignTicTacToe(int n) {
        this.n = n;
        this.rows = new int[n];
        this.cols = new int[n];
        this.diagonal = 0;
        this.antiDiagonal = 0;
    }

    public int move(int row, int col, int player) {
        int toAdd = player == 1 ? 1 : -1;

        rows[row] += toAdd;
        cols[col] += toAdd;

        if (row == col) {
            diagonal += toAdd;
        }

        if (row + col == n - 1) {
            antiDiagonal += toAdd;
        }

        // Check for win
        if (Math.abs(rows[row]) == n || Math.abs(cols[col]) == n ||
                Math.abs(diagonal) == n || Math.abs(antiDiagonal) == n) {
            return player;
        }

        return 0;
    }

    public static void main(String[] args) {
        DesignTicTacToe ticTacToe = new DesignTicTacToe(3);

        System.out.println(ticTacToe.move(0, 0, 1)); // 0
        System.out.println(ticTacToe.move(0, 2, 2)); // 0
        System.out.println(ticTacToe.move(2, 2, 1)); // 0
        System.out.println(ticTacToe.move(1, 1, 2)); // 0
        System.out.println(ticTacToe.move(2, 0, 1)); // 0
        System.out.println(ticTacToe.move(1, 0, 2)); // 0
        System.out.println(ticTacToe.move(2, 1, 1)); // 1 (Player 1 wins)
    }
}
