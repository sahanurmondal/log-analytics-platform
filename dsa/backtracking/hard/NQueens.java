package backtracking.hard;

import java.util.*;

/**
 * LeetCode 51: N-Queens
 * https://leetcode.com/problems/n-queens/
 *
 * Description: The n-queens puzzle is the problem of placing n queens on an n×n
 * chessboard
 * such that no two queens attack each other.
 * 
 * Constraints:
 * - 1 <= n <= 9
 *
 * Follow-up:
 * - Can you count the number of solutions (N-Queens II)?
 * - What's the time complexity analysis?
 * 
 * Time Complexity: O(N!)
 * Space Complexity: O(N^2)
 * 
 * Algorithm:
 * 1. Backtracking: Place queens row by row
 * 2. Check conflicts using diagonals and columns
 * 3. Optimization: Use bit manipulation for faster conflict detection
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class NQueens {

    // Main optimized solution - Backtracking
    public List<List<String>> solveNQueens(int n) {
        List<List<String>> result = new ArrayList<>();
        int[] queens = new int[n]; // queens[i] = column position of queen in row i
        Arrays.fill(queens, -1);

        backtrack(queens, 0, result);
        return result;
    }

    private void backtrack(int[] queens, int row, List<List<String>> result) {
        if (row == queens.length) {
            result.add(generateBoard(queens));
            return;
        }

        for (int col = 0; col < queens.length; col++) {
            if (isValid(queens, row, col)) {
                queens[row] = col;
                backtrack(queens, row + 1, result);
                queens[row] = -1;
            }
        }
    }

    private boolean isValid(int[] queens, int row, int col) {
        for (int i = 0; i < row; i++) {
            int prevCol = queens[i];

            // Check column conflict
            if (prevCol == col)
                return false;

            // Check diagonal conflict
            if (Math.abs(prevCol - col) == Math.abs(i - row))
                return false;
        }
        return true;
    }

    private List<String> generateBoard(int[] queens) {
        List<String> board = new ArrayList<>();

        for (int i = 0; i < queens.length; i++) {
            StringBuilder row = new StringBuilder();
            for (int j = 0; j < queens.length; j++) {
                if (queens[i] == j) {
                    row.append('Q');
                } else {
                    row.append('.');
                }
            }
            board.add(row.toString());
        }

        return board;
    }

    // Alternative solution - Using sets for conflict detection
    public List<List<String>> solveNQueensSet(int n) {
        List<List<String>> result = new ArrayList<>();
        Set<Integer> cols = new HashSet<>();
        Set<Integer> diag1 = new HashSet<>(); // row - col
        Set<Integer> diag2 = new HashSet<>(); // row + col

        backtrackSet(0, n, cols, diag1, diag2, new ArrayList<>(), result);
        return result;
    }

    private void backtrackSet(int row, int n, Set<Integer> cols, Set<Integer> diag1,
            Set<Integer> diag2, List<String> board, List<List<String>> result) {
        if (row == n) {
            result.add(new ArrayList<>(board));
            return;
        }

        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || diag1.contains(row - col) || diag2.contains(row + col)) {
                continue;
            }

            // Place queen
            cols.add(col);
            diag1.add(row - col);
            diag2.add(row + col);

            StringBuilder rowStr = new StringBuilder();
            for (int i = 0; i < n; i++) {
                rowStr.append(i == col ? 'Q' : '.');
            }
            board.add(rowStr.toString());

            backtrackSet(row + 1, n, cols, diag1, diag2, board, result);

            // Remove queen
            board.remove(board.size() - 1);
            cols.remove(col);
            diag1.remove(row - col);
            diag2.remove(row + col);
        }
    }

    // Follow-up optimization - N-Queens II (count only)
    public int totalNQueens(int n) {
        return backtrackCount(0, n, new HashSet<>(), new HashSet<>(), new HashSet<>());
    }

    private int backtrackCount(int row, int n, Set<Integer> cols, Set<Integer> diag1, Set<Integer> diag2) {
        if (row == n)
            return 1;

        int count = 0;
        for (int col = 0; col < n; col++) {
            if (cols.contains(col) || diag1.contains(row - col) || diag2.contains(row + col)) {
                continue;
            }

            cols.add(col);
            diag1.add(row - col);
            diag2.add(row + col);

            count += backtrackCount(row + 1, n, cols, diag1, diag2);

            cols.remove(col);
            diag1.remove(row - col);
            diag2.remove(row + col);
        }

        return count;
    }

    public static void main(String[] args) {
        NQueens solution = new NQueens();

        // Test Case 1: N = 4
        List<List<String>> result4 = solution.solveNQueens(4);
        System.out.println("N=4 solutions: " + result4.size()); // Expected: 2
        for (List<String> board : result4) {
            for (String row : board) {
                System.out.println(row);
            }
            System.out.println();
        }

        // Test Case 2: N = 1
        System.out.println("N=1 solutions: " + solution.solveNQueens(1).size()); // Expected: 1

        // Test Case 3: N = 2 (impossible)
        System.out.println("N=2 solutions: " + solution.solveNQueens(2).size()); // Expected: 0

        // Test Case 4: N = 3 (impossible)
        System.out.println("N=3 solutions: " + solution.solveNQueens(3).size()); // Expected: 0

        // Test Case 5: N = 5
        System.out.println("N=5 solutions: " + solution.solveNQueens(5).size()); // Expected: 10

        // Test Case 6: N = 6
        System.out.println("N=6 solutions: " + solution.solveNQueens(6).size()); // Expected: 4

        // Test Case 7: N = 7
        System.out.println("N=7 solutions: " + solution.solveNQueens(7).size()); // Expected: 40

        // Test Case 8: N = 8
        System.out.println("N=8 solutions: " + solution.solveNQueens(8).size()); // Expected: 92

        // Test Case 9: Count only for N=8
        System.out.println("N=8 count only: " + solution.totalNQueens(8)); // Expected: 92

        // Test Case 10: Maximum constraint N=9
        System.out.println("N=9 solutions: " + solution.solveNQueens(9).size()); // Expected: 352
    }
}
