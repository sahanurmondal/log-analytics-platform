package backtracking.hard;

import java.util.*;

/**
 * LeetCode 51: N-Queens
 * URL: https://leetcode.com/problems/n-queens/
 * Difficulty: Hard
 * Companies: Google, Amazon, Microsoft, Facebook, Apple
 * Frequency: High
 *
 * Description:
 * The n-queens puzzle is the problem of placing n chess queens on an n×n
 * chessboard
 * such that no two queens attack each other. Given an integer n, return all
 * distinct
 * solutions to the n-queens puzzle. You may return the answer in any order.
 *
 * Constraints:
 * - 1 <= n <= 9
 *
 * Follow-up Questions:
 * 1. Can you optimize using bit manipulation?
 * 2. How to count only the number of solutions?
 * 3. Can you find just one solution quickly?
 * 4. What's the pattern for larger n values?
 */
public class FindAllValidNQueensBoards {

    // Approach 1: Standard backtracking - O(n!)
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        char[][] board = new char[n][n];

        // Initialize board with dots
        for (int i = 0; i < n; i++) {
            Arrays.fill(board[i], '.');
        }

        backtrack(board, 0, result);
        return result;
    }

    private void backtrack(char[][] board, int row, List<List<String>> result) {
        if (row == board.length) {
            result.add(constructBoard(board));
            return;
        }

        for (int col = 0; col < board.length; col++) {
            if (isValid(board, row, col)) {
                board[row][col] = 'Q';
                backtrack(board, row + 1, result);
                board[row][col] = '.';
            }
        }
    }

    private boolean isValid(char[][] board, int row, int col) {
        int n = board.length;

        // Check column
        for (int i = 0; i < row; i++) {
            if (board[i][col] == 'Q')
                return false;
        }

        // Check diagonal (top-left to bottom-right)
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] == 'Q')
                return false;
        }

        // Check anti-diagonal (top-right to bottom-left)
        for (int i = row - 1, j = col + 1; i >= 0 && j < n; i--, j++) {
            if (board[i][j] == 'Q')
                return false;
        }

        return true;
    }

    private List<String> constructBoard(char[][] board) {
        List<String> result = new ArrayList<>();
        for (char[] row : board) {
            result.add(new String(row));
        }
        return result;
    }

    // Approach 2: Optimized with sets for O(1) conflict checking - O(n!)
    public List<List<String>> solveNQueensOptimized(int n) {
        List<List<String>> result = new ArrayList<>();
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>(); // row - col
        Set<Integer> diag2 = new HashSet<>(); // row + col

        backtrackOptimized(n, 0, new ArrayList<>(), cols, diag1, diag2, result);
        return result;
    }

    private void backtrackOptimized(int n, int row, List<Integer> queens,
            Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2,
            List<List<String>> result) {
        if (row == n) {
            result.add(constructBoardFromQueens(queens, n));
            return;
        }

        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || diag1.contains(row - col) || diag2.contains(row + col)) {
                continue;
            }

            queens.add(col);
            cols.add(col);
            diag1.add(row - col);
            diag2.add(row + col);

            backtrackOptimized(n, row + 1, queens, cols, diag1, diag2, result);

            queens.remove(queens.size() - 1);
            cols.remove(col);
            diag1.remove(row - col);
            diag2.remove(row + col);
        }
    }

    private List<String> constructBoardFromQueens(List<Integer> queens, int n) {
        List<String> board = new ArrayList<>();
        for (int queen : queens) {
            StringBuilder row = new StringBuilder();
            for (int col = 0; col < n; col++) {
                row.append(col == queen ? 'Q' : '.');
            }
            board.add(row.toString());
        }
        return board;
    }

    // Approach 3: Bit manipulation - O(n!) with better constants
    public List<List<String>> solveNQueensBitwise(int n) {
        List<List<String>> result = new ArrayList<>();
        backtrackBitwise(n, 0, 0, 0, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrackBitwise(int n, int row, int cols, int diag1, int diag2,
            List<Integer> queens, List<List<String>> result) {
        if (row == n) {
            result.add(constructBoardFromQueens(queens, n));
            return;
        }

        int availablePositions = ((1 << n) - 1) & (~(cols | diag1 | diag2));

        while (availablePositions != 0) {
            int position = availablePositions & (-availablePositions);
            availablePositions &= availablePositions - 1;

            int col = Integer.bitCount(position - 1);
            queens.add(col);

            backtrackBitwise(n, row + 1, cols | position, (diag1 | position) << 1,
                    (diag2 | position) >> 1, queens, result);

            queens.remove(queens.size() - 1);
        }
    }

    // Follow-up 2: Count only solutions - O(n!)
    public int totalNQueens(int n) {
        return countSolutions(n, 0, new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    private int countSolutions(int n, int row, Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2) {
        if (row == n)
            return 1;

        int count = 0;
        for (int col = 0; col < n; col++) {
            if (!cols.contains(col) && !diag1.contains(row - col) && !diag2.contains(row + col)) {
                cols.add(col);
                diag1.add(row - col);
                diag2.add(row + col);

                count += countSolutions(n, row + 1, cols, diag1, diag2);

                cols.remove(col);
                diag1.remove(row - col);
                diag2.remove(row + col);
            }
        }
        return count;
    }

    // Follow-up 3: Find one solution quickly
    public List<String> findOneSolution(int n) {
        char[][] board = new char[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(board[i], '.');
        }

        if (findOneSolutionHelper(board, 0)) {
            return constructBoard(board);
        }
        return new ArrayList<>();
    }

    private boolean findOneSolutionHelper(char[][] board, int row) {
        if (row == board.length)
            return true;

        for (int col = 0; col < board.length; col++) {
            if (isValid(board, row, col)) {
                board[row][col] = 'Q';
                if (findOneSolutionHelper(board, row + 1)) {
                    return true;
                }
                board[row][col] = '.';
            }
        }
        return false;
    }

    // Legacy method name for compatibility
    public List<List<String>> solveAllNQueens(int n) {
        return solveNQueens(n);
    }

    public static void main(String[] args) {
        FindAllValidNQueensBoards solution = new FindAllValidNQueensBoards();

        // Test Case 1: N=4 (classic example)
        System.out.println("Test 1: " + solution.solveNQueens(4));
        // Expected: 2 solutions

        // Test Case 2: N=1 (trivial case)
        System.out.println("Test 2: " + solution.solveNQueens(1));
        // Expected: [["Q"]]

        // Test Case 3: N=2 (no solution)
        System.out.println("Test 3: " + solution.solveNQueens(2));
        // Expected: []

        // Test Case 4: N=3 (no solution)
        System.out.println("Test 4: " + solution.solveNQueens(3));
        // Expected: []

        // Test Case 5: Optimized approach
        System.out.println("Test 5 (Optimized): " + solution.solveNQueensOptimized(4));
        // Expected: 2 solutions

        // Test Case 6: Bitwise approach
        System.out.println("Test 6 (Bitwise): " + solution.solveNQueensBitwise(4));
        // Expected: 2 solutions

        // Test Case 7: Count total solutions
        System.out.println("Test 7 (Count): " + solution.totalNQueens(8));
        // Expected: 92

        // Test Case 8: Find one solution
        System.out.println("Test 8 (One): " + solution.findOneSolution(8));
        // Expected: one valid solution

        // Test Case 9: N=5 solutions
        System.out.println("Test 9: " + solution.solveNQueens(5).size());
        // Expected: 10 solutions

        // Test Case 10: Performance comparison
        long start = System.currentTimeMillis();
        int count10 = solution.solveNQueens(8).size();
        long end = System.currentTimeMillis();
        System.out.println("Test 10 (Performance): " + count10 + " solutions in " + (end - start) + "ms");

        // Test Case 11: Bitwise count for N=8
        System.out.println("Test 11 (Bitwise Count): " + solution.solveNQueensBitwise(8).size());
        // Expected: 92

        // Test Case 12: Large N (if feasible)
        System.out.println("Test 12: " + solution.totalNQueens(6));
        // Expected: 4

        // Test Case 13: Consistency check
        boolean consistent = solution.solveNQueens(4).size() == solution.solveNQueensOptimized(4).size();
        System.out.println("Test 13 (Consistency): " + consistent);
        // Expected: true

        // Test Case 14: Single solution timing
        start = System.currentTimeMillis();
        solution.findOneSolution(9);
        end = System.currentTimeMillis();
        System.out.println("Test 14 (One Solution Time): " + (end - start) + "ms for N=9");

        // Test Case 15: All approaches for N=4
        System.out.println("Test 15 (All Approaches): " +
                solution.solveNQueens(4).size() + " = " +
                solution.solveNQueensOptimized(4).size() + " = " +
                solution.solveNQueensBitwise(4).size());
        // Expected: 2 = 2 = 2
    }
}
