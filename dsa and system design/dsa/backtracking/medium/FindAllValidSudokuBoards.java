package backtracking.medium;

import java.util.*;

/**
 * Variation: Find All Valid Sudoku Boards
 *
 * Description:
 * Given a partially filled 9x9 Sudoku board, return all possible valid
 * completions.
 *
 * Constraints:
 * - board is a 9x9 char array
 * - board[i][j] is '1'-'9' or '.'
 */
public class FindAllValidSudokuBoards {
    public List<char[][]> solveSudokuAll(char[][] board) {
        List<char[][]> solutions = new ArrayList<>();
        if (board == null || board.length != 9 || board[0].length != 9) {
            return solutions;
        }

        solve(board, solutions);
        return solutions;
    }

    private void solve(char[][] board, List<char[][]> solutions) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (board[i][j] == '.') {
                    for (char c = '1'; c <= '9'; c++) {
                        if (isValid(board, i, j, c)) {
                            board[i][j] = c;
                            solve(board, solutions);
                            board[i][j] = '.'; // backtrack
                        }
                    }
                    return; // If we can't fill this cell, no solution from this path
                }
            }
        }

        // If we reach here, board is complete and valid
        char[][] solution = new char[9][9];
        for (int i = 0; i < 9; i++) {
            solution[i] = board[i].clone();
        }
        solutions.add(solution);
    }

    private boolean isValid(char[][] board, int row, int col, char c) {
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == c)
                return false; // check row
            if (board[row][i] == c)
                return false; // check column
            if (board[3 * (row / 3) + i / 3][3 * (col / 3) + i % 3] == c)
                return false; // check 3x3 box
        }
        return true;
    }

    public static void main(String[] args) {
        FindAllValidSudokuBoards solution = new FindAllValidSudokuBoards();
        char[][] board = {
                { '5', '3', '.', '.', '7', '.', '.', '.', '.' },
                { '6', '.', '.', '1', '9', '5', '.', '.', '.' },
                { '.', '9', '8', '.', '.', '.', '.', '6', '.' },
                { '8', '.', '.', '.', '6', '.', '.', '.', '3' },
                { '4', '.', '.', '8', '.', '3', '.', '.', '1' },
                { '7', '.', '.', '.', '2', '.', '.', '.', '6' },
                { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
                { '.', '.', '.', '4', '1', '9', '.', '.', '5' },
                { '.', '.', '.', '.', '8', '.', '.', '7', '9' }
        };
        System.out.println(solution.solveSudokuAll(board)); // List of valid boards
        // Edge Case: Already solved board
        char[][] solved = {
                { '5', '3', '4', '6', '7', '8', '9', '1', '2' },
                { '6', '7', '2', '1', '9', '5', '3', '4', '8' },
                { '1', '9', '8', '3', '4', '2', '5', '6', '7' },
                { '8', '5', '9', '7', '6', '1', '4', '2', '3' },
                { '4', '2', '6', '8', '5', '3', '7', '9', '1' },
                { '7', '1', '3', '9', '2', '4', '8', '5', '6' },
                { '9', '6', '1', '5', '3', '7', '2', '8', '4' },
                { '2', '8', '7', '4', '1', '9', '6', '3', '5' },
                { '3', '4', '5', '2', '8', '6', '1', '7', '9' }
        };
        System.out.println(solution.solveSudokuAll(solved)); // [solved board]
        // Edge Case: Empty board
        char[][] empty = new char[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                empty[i][j] = '.';
        System.out.println(solution.solveSudokuAll(empty)); // List of all valid boards
        // Edge Case: Invalid board
        char[][] invalid = {
                { '5', '5', '.', '.', '7', '.', '.', '.', '.' },
                { '6', '.', '.', '1', '9', '5', '.', '.', '.' },
                { '.', '9', '8', '.', '.', '.', '.', '6', '.' },
                { '8', '.', '.', '.', '6', '.', '.', '.', '3' },
                { '4', '.', '.', '8', '.', '3', '.', '.', '1' },
                { '7', '.', '.', '.', '2', '.', '.', '.', '6' },
                { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
                { '.', '.', '.', '4', '1', '9', '.', '.', '5' },
                { '.', '.', '.', '.', '8', '.', '.', '7', '9' }
        };
        System.out.println(solution.solveSudokuAll(invalid)); // []
    }
}
