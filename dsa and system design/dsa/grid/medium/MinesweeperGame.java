package grid.medium;

/**
 * LeetCode 529: Minesweeper
 * https://leetcode.com/problems/minesweeper/
 *
 * Description:
 * You are given an m x n char matrix board representing the game board where:
 * 'M' represents an unrevealed mine,
 * 'E' represents an unrevealed empty square,
 * 'B' represents a revealed blank square that has no adjacent mines,
 * digit ('1' to '8') represents how many mines are adjacent to this revealed
 * square, and
 * 'X' represents a revealed mine.
 * You are also given an integer array click where click = [clickr, clickc]
 * represents the next click position among all the unrevealed squares ('M' or
 * 'E').
 * Return the board after revealing this position according to the following
 * rules:
 * - If a mine is revealed, then the game is over. You should change it to 'X'.
 * - If an empty square with no adjacent mines is revealed, then change it to a
 * revealed blank 'B' and all of its adjacent unrevealed squares should be
 * revealed recursively.
 * - If an empty square with at least one adjacent mine is revealed, then change
 * it to a digit ('1' to '8') representing the number of adjacent mines.
 * Return the board when no more squares will be revealed.
 *
 * Constraints:
 * - m == board.length
 * - n == board[i].length
 * - 1 <= m, n <= 50
 * - board[i][j] is either 'M', 'E', 'B', or a digit from '1' to '8'
 * - click.length == 2
 * - 0 <= clickr < m
 * - 0 <= clickc < n
 * - board[clickr][clickc] is either 'M' or 'E'
 */
public class MinesweeperGame {

    private int[][] directions = { { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, -1 }, { 0, 1 }, { 1, -1 }, { 1, 0 },
            { 1, 1 } };

    public char[][] updateBoard(char[][] board, int[] click) {
        int r = click[0], c = click[1];

        if (board[r][c] == 'M') {
            board[r][c] = 'X';
            return board;
        }

        dfs(board, r, c);
        return board;
    }

    private void dfs(char[][] board, int r, int c) {
        if (r < 0 || r >= board.length || c < 0 || c >= board[0].length || board[r][c] != 'E') {
            return;
        }

        int mineCount = countMines(board, r, c);

        if (mineCount > 0) {
            board[r][c] = (char) ('0' + mineCount);
        } else {
            board[r][c] = 'B';
            for (int[] dir : directions) {
                dfs(board, r + dir[0], c + dir[1]);
            }
        }
    }

    private int countMines(char[][] board, int r, int c) {
        int count = 0;
        for (int[] dir : directions) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            if (nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length && board[nr][nc] == 'M') {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        MinesweeperGame solution = new MinesweeperGame();

        char[][] board = {
                { 'E', 'E', 'E', 'E', 'E' },
                { 'E', 'E', 'M', 'E', 'E' },
                { 'E', 'E', 'E', 'E', 'E' },
                { 'E', 'E', 'E', 'E', 'E' }
        };
        int[] click = { 3, 0 };

        char[][] result = solution.updateBoard(board, click);
        for (char[] row : result) {
            System.out.println(java.util.Arrays.toString(row));
        }
    }
}
