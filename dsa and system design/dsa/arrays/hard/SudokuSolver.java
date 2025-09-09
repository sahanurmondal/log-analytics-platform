package arrays.hard;

/**
 * LeetCode 37: Sudoku Solver
 * https://leetcode.com/problems/sudoku-solver/
 *
 * Description:
 * Write a program to solve a Sudoku puzzle by filling the empty cells.
 * A sudoku solution must satisfy all of the following rules:
 * Each of the digits 1-9 must occur exactly once in each row, column, and 3x3
 * sub-box.
 *
 * Constraints:
 * - board.length == 9
 * - board[i].length == 9
 * - board[i][j] is a digit or '.'
 * - It is guaranteed that the input board has only one solution
 *
 * Follow-up:
 * - Can you solve it using constraint propagation?
 * 
 * Time Complexity: O(9^(n*n)) where n=9
 * Space Complexity: O(n*n)
 * 
 * Algorithm:
 * 1. Use backtracking to try digits 1-9 in empty cells
 * 2. Check validity for row, column, and 3x3 box
 * 3. Backtrack if no valid digit found
 */
public class SudokuSolver {
    public void solveSudoku(char[][] board) {
        solve(board);
    }

    private boolean solve(char[][] board) {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board[row][col] == '.') {
                    for (char digit = '1'; digit <= '9'; digit++) {
                        if (isValid(board, row, col, digit)) {
                            board[row][col] = digit;

                            if (solve(board)) {
                                return true;
                            }

                            board[row][col] = '.'; // Backtrack
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(char[][] board, int row, int col, char digit) {
        // Check row
        for (int j = 0; j < 9; j++) {
            if (board[row][j] == digit)
                return false;
        }

        // Check column
        for (int i = 0; i < 9; i++) {
            if (board[i][col] == digit)
                return false;
        }

        // Check 3x3 box
        int boxRow = (row / 3) * 3;
        int boxCol = (col / 3) * 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (board[i][j] == digit)
                    return false;
            }
        }

        return true;
    }

    private void printBoard(char[][] board) {
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    // -------------------- Optimized solver (bitmask + heuristic)
    // --------------------
    public boolean solveSudokuOptimized(char[][] board) {
        if (board == null || board.length != 9 || board[0].length != 9)
            return false;
        if (!isValidInitialBoard(board))
            return false;

        int[] rowMask = new int[9];
        int[] colMask = new int[9];
        int[] boxMask = new int[9];

        java.util.List<int[]> empties = new java.util.ArrayList<>();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = board[r][c];
                if (ch >= '1' && ch <= '9') {
                    int d = ch - '0';
                    int bit = 1 << d;
                    rowMask[r] |= bit;
                    colMask[c] |= bit;
                    int bi = (r / 3) * 3 + (c / 3);
                    boxMask[bi] |= bit;
                } else {
                    empties.add(new int[] { r, c });
                }
            }
        }

        int n = empties.size();

        // convert list to array for fast swaps
        int[][] pos = new int[n][2];
        for (int i = 0; i < n; i++)
            pos[i] = empties.get(i);

        return dfsOptimized(board, pos, 0, rowMask, colMask, boxMask);
    }

    private boolean dfsOptimized(char[][] board, int[][] pos, int idx, int[] rowMask, int[] colMask, int[] boxMask) {
        final int ALL = 0x3FE;
        int n = pos.length;
        if (idx == n)
            return true;

        // choose empty with minimal candidates among pos[idx..n-1]
        int best = -1, bestCount = 10, bestMask = 0;
        for (int i = idx; i < n; i++) {
            int r = pos[i][0], c = pos[i][1];
            int bi = (r / 3) * 3 + (c / 3);
            int used = rowMask[r] | colMask[c] | boxMask[bi];
            int avail = ALL & ~used;
            int count = Integer.bitCount(avail);
            if (count == 0)
                return false; // dead end
            if (count < bestCount) {
                bestCount = count;
                best = i;
                bestMask = avail;
                if (count == 1)
                    break;
            }
        }

        // swap best to idx
        int[] tmp = pos[idx];
        pos[idx] = pos[best];
        pos[best] = tmp;

        int r = pos[idx][0], c = pos[idx][1];
        int bi = (r / 3) * 3 + (c / 3);
        int mask = bestMask;

        while (mask != 0) {
            int bit = mask & -mask;
            int d = Integer.numberOfTrailingZeros(bit);
            board[r][c] = (char) ('0' + d);
            rowMask[r] |= bit;
            colMask[c] |= bit;
            boxMask[bi] |= bit;

            if (dfsOptimized(board, pos, idx + 1, rowMask, colMask, boxMask))
                return true;

            // backtrack
            board[r][c] = '.';
            rowMask[r] &= ~bit;
            colMask[c] &= ~bit;
            boxMask[bi] &= ~bit;

            mask &= mask - 1; // remove lowest bit
        }

        // restore swap
        tmp = pos[idx];
        pos[idx] = pos[best];
        pos[best] = tmp;

        return false;
    }

    private boolean isValidInitialBoard(char[][] board) {
        int[] rowMask = new int[9];
        int[] colMask = new int[9];
        int[] boxMask = new int[9];

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                char ch = board[r][c];
                if (ch >= '1' && ch <= '9') {
                    int d = ch - '0';
                    int bit = 1 << d;
                    int bi = (r / 3) * 3 + (c / 3);
                    if ((rowMask[r] & bit) != 0)
                        return false;
                    if ((colMask[c] & bit) != 0)
                        return false;
                    if ((boxMask[bi] & bit) != 0)
                        return false;
                    rowMask[r] |= bit;
                    colMask[c] |= bit;
                    boxMask[bi] |= bit;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) {
        SudokuSolver solution = new SudokuSolver();

        // Test Case 1: Normal case - partially filled
        char[][] board1 = {
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
        solution.solveSudoku(board1);
        System.out.println("Board1 solved and valid? " + solution.isSolvedAndValid(board1));
        solution.printBoard(board1);

        // Test Case 2: Edge case - almost complete
        char[][] board2 = {
                { '5', '3', '4', '6', '7', '8', '9', '1', '2' },
                { '6', '7', '2', '1', '9', '5', '3', '4', '8' },
                { '1', '9', '8', '3', '4', '2', '5', '6', '7' },
                { '8', '5', '9', '7', '6', '1', '4', '2', '3' },
                { '4', '2', '6', '8', '5', '3', '7', '9', '1' },
                { '7', '1', '3', '9', '2', '4', '8', '5', '6' },
                { '9', '6', '1', '5', '3', '7', '2', '8', '4' },
                { '2', '8', '7', '4', '1', '9', '6', '3', '5' },
                { '3', '4', '5', '2', '8', '6', '1', '7', '.' }
        };
        solution.solveSudoku(board2);
        System.out.println("Board2 solved and valid? " + solution.isSolvedAndValid(board2));
        solution.printBoard(board2);

        // ---------------------- Enhanced tests for new methods ----------------------
        System.out.println("-- Running enhanced tests --");

        // Utility to run and time solver variants
        Runnable runNaive1 = () -> {
            char[][] b = cloneBoard(new char[][] {
                    { '5', '3', '.', '.', '7', '.', '.', '.', '.' },
                    { '6', '.', '.', '1', '9', '5', '.', '.', '.' },
                    { '.', '9', '8', '.', '.', '.', '.', '6', '.' },
                    { '8', '.', '.', '.', '6', '.', '.', '.', '3' },
                    { '4', '.', '.', '8', '.', '3', '.', '.', '1' },
                    { '7', '.', '.', '.', '2', '.', '.', '.', '6' },
                    { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
                    { '.', '.', '.', '4', '1', '9', '.', '.', '5' },
                    { '.', '.', '.', '.', '8', '.', '.', '7', '9' }
            });
            long t0 = System.nanoTime();
            solution.solveSudoku(b);
            long t1b = System.nanoTime();
            System.out.println("Naive solver time (ms): " + (t1b - t0) / 1_000_000.0);
            System.out.println("Naive solved and valid? " + solution.isSolvedAndValid(b));
        };

        Runnable runOptimized1 = () -> {
            char[][] b = cloneBoard(new char[][] {
                    { '5', '3', '.', '.', '7', '.', '.', '.', '.' },
                    { '6', '.', '.', '1', '9', '5', '.', '.', '.' },
                    { '.', '9', '8', '.', '.', '.', '.', '6', '.' },
                    { '8', '.', '.', '.', '6', '.', '.', '.', '3' },
                    { '4', '.', '.', '8', '.', '3', '.', '.', '1' },
                    { '7', '.', '.', '.', '2', '.', '.', '.', '6' },
                    { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
                    { '.', '.', '.', '4', '1', '9', '.', '.', '5' },
                    { '.', '.', '.', '.', '8', '.', '.', '7', '9' }
            });
            long t0 = System.nanoTime();
            boolean ok = solution.solveSudokuOptimized(b);
            long t1b = System.nanoTime();
            System.out.println("Optimized solver time (ms): " + (t1b - t0) / 1_000_000.0 + " solved=" + ok + " valid="
                    + solution.isSolvedAndValid(b));
        };

        // Run comparative timings
        runNaive1.run();
        runOptimized1.run();

        // Test: invalid initial board (duplicate in row)
        char[][] invalid = cloneBoard(new char[][] {
                { '5', '3', '5', '.', '7', '.', '.', '.', '.' }, // duplicate 5 in row
                { '6', '.', '.', '1', '9', '5', '.', '.', '.' },
                { '.', '9', '8', '.', '.', '.', '.', '6', '.' },
                { '8', '.', '.', '.', '6', '.', '.', '.', '3' },
                { '4', '.', '.', '8', '.', '3', '.', '.', '1' },
                { '7', '.', '.', '.', '2', '.', '.', '.', '6' },
                { '.', '6', '.', '.', '.', '.', '2', '8', '.' },
                { '.', '.', '.', '4', '1', '9', '.', '.', '5' },
                { '.', '.', '.', '.', '8', '.', '.', '7', '9' }
        });

        System.out.println("Invalid board valid? " + solution.isValidInitialBoard(invalid));

        // Test: empty board
        char[][] empty = new char[9][9];
        for (int i = 0; i < 9; i++)
            for (int j = 0; j < 9; j++)
                empty[i][j] = '.';
        System.out.println("Empty board solved? " + solution.solveSudokuOptimized(empty));

        // Hard puzzle (known difficult but solvable)
        char[][] hard = cloneBoard(new char[][] {
                { '.', '.', '5', '3', '.', '.', '.', '.', '.' },
                { '8', '.', '.', '.', '.', '.', '.', '2', '.' },
                { '.', '7', '.', '.', '1', '.', '5', '.', '.' },
                { '4', '.', '.', '.', '.', '5', '3', '.', '.' },
                { '.', '1', '.', '.', '7', '.', '.', '.', '6' },
                { '.', '.', '3', '2', '.', '.', '.', '8', '.' },
                { '.', '6', '.', '5', '.', '.', '.', '.', '9' },
                { '.', '.', '4', '.', '.', '.', '.', '3', '.' },
                { '.', '.', '.', '.', '.', '9', '7', '.', '.' }
        });
        long t0 = System.nanoTime();
        boolean solvedHard = solution.solveSudokuOptimized(hard);
        long t1 = System.nanoTime();
        System.out.println("Hard puzzle solved=" + solvedHard + ", time(ms)=" + (t1 - t0) / 1_000_000.0);
        if (solvedHard)
            solution.printBoard(hard);

        System.out.println("-- Enhanced tests completed --");
    }

    // Helper: deep clone a board
    private static char[][] cloneBoard(char[][] src) {
        char[][] dst = new char[src.length][src[0].length];
        for (int i = 0; i < src.length; i++)
            System.arraycopy(src[i], 0, dst[i], 0, src[i].length);
        return dst;
    }

    // Helper: quick validator for final solution (fully filled, valid)
    private boolean isSolvedAndValid(char[][] board) {
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (board[r][c] < '1' || board[r][c] > '9')
                    return false;
        // reuse initial validation by treating it as a filled board
        return isValidInitialBoard(board);
    }
}