package grid.medium;

/**
 * LeetCode 723: Candy Crush
 * https://leetcode.com/problems/candy-crush/
 *
 * Description:
 * This question is about implementing a basic elimination algorithm for Candy
 * Crush.
 * Given an m x n integer array board representing the grid of candy where
 * board[i][j] represents the type of candy.
 * A value of board[i][j] = 0 represents that the cell is empty.
 * The given board represents the state of the game following a player's move.
 * Now, you need to restore the board to a stable state by crushing candies
 * according to the following rules:
 * - If three or more candies of the same type are adjacent vertically or
 * horizontally, crush them all at the same time.
 * - After crushing all candies simultaneously, if an empty space on the board
 * has candies on top of it, then these candies will drop until they hit a candy
 * or bottom.
 * - After the above steps, there may exist more candies that can be crushed. If
 * so, you need to repeat the above steps.
 * - If there does not exist more candies that can be crushed, then return the
 * current board.
 * You need to perform the above rules until the board becomes stable, then
 * return the stable board.
 *
 * Constraints:
 * - m == board.length
 * - n == board[i].length
 * - 3 <= m, n <= 50
 * - 1 <= board[i][j] <= 2000
 */
public class CandyCrush {

    public int[][] candyCrush(int[][] board) {
        int m = board.length, n = board[0].length;
        boolean found = true;

        while (found) {
            found = false;

            // Mark candies to be crushed
            for (int i = 0; i < m; i++) {
                for (int j = 0; j < n; j++) {
                    int val = Math.abs(board[i][j]);
                    if (val == 0)
                        continue;

                    // Check horizontal
                    if (j + 2 < n && Math.abs(board[i][j + 1]) == val && Math.abs(board[i][j + 2]) == val) {
                        board[i][j] = board[i][j + 1] = board[i][j + 2] = -val;
                        found = true;
                    }

                    // Check vertical
                    if (i + 2 < m && Math.abs(board[i + 1][j]) == val && Math.abs(board[i + 2][j]) == val) {
                        board[i][j] = board[i + 1][j] = board[i + 2][j] = -val;
                        found = true;
                    }
                }
            }

            // Drop candies
            if (found) {
                for (int j = 0; j < n; j++) {
                    int write = m - 1;
                    for (int i = m - 1; i >= 0; i--) {
                        if (board[i][j] > 0) {
                            board[write--][j] = board[i][j];
                        }
                    }
                    while (write >= 0) {
                        board[write--][j] = 0;
                    }
                }
            }
        }

        return board;
    }

    public static void main(String[] args) {
        CandyCrush solution = new CandyCrush();

        int[][] board = { { 110, 5, 112, 113, 114 }, { 210, 211, 5, 213, 214 }, { 310, 311, 3, 313, 314 },
                { 410, 411, 412, 5, 414 }, { 5, 1, 512, 3, 3 }, { 610, 4, 1, 613, 614 }, { 710, 1, 2, 713, 714 },
                { 810, 1, 2, 1, 1 }, { 1, 1, 2, 2, 2 }, { 4, 1, 4, 4, 1014 } };
        int[][] result = solution.candyCrush(board);

        for (int[] row : result) {
            System.out.println(java.util.Arrays.toString(row));
        }
    }
}
