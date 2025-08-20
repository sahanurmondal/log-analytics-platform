package bitmanipulation.hard;

/**
 * LeetCode 52: N-Queens II (Bit Manipulation Approach)
 * https://leetcode.com/problems/n-queens-ii/
 *
 * Description: The n-queens puzzle is the problem of placing n queens on an n×n
 * chessboard
 * such that no two queens attack each other. Given an integer n, return the
 * number of distinct solutions.
 * 
 * Constraints:
 * - 1 <= n <= 9
 *
 * Follow-up:
 * - Can you optimize using bit manipulation?
 * - What about using bitmasks for diagonals?
 * 
 * Time Complexity: O(N!)
 * Space Complexity: O(N)
 * 
 * Company Tags: Google, Facebook
 */
public class NQueensIIBitManipulation {

    // Main optimized solution - Bit manipulation
    public int totalNQueens(int n) {
        return backtrack(0, 0, 0, 0, n);
    }

    private int backtrack(int row, int cols, int diag1, int diag2, int n) {
        if (row == n)
            return 1;

        int count = 0;
        int available = ((1 << n) - 1) & (~(cols | diag1 | diag2));

        while (available != 0) {
            int pos = available & (-available); // Get rightmost bit
            available ^= pos; // Remove this bit

            count += backtrack(row + 1, cols | pos, (diag1 | pos) << 1, (diag2 | pos) >> 1, n);
        }

        return count;
    }

    // Alternative solution - Traditional backtracking with boolean arrays
    public int totalNQueensTraditional(int n) {
        boolean[] cols = new boolean[n];
        boolean[] diag1 = new boolean[2 * n - 1];
        boolean[] diag2 = new boolean[2 * n - 1];

        return backtrackTraditional(0, cols, diag1, diag2, n);
    }

    private int backtrackTraditional(int row, boolean[] cols, boolean[] diag1, boolean[] diag2, int n) {
        if (row == n)
            return 1;

        int count = 0;
        for (int col = 0; col < n; col++) {
            int d1 = row - col + n - 1;
            int d2 = row + col;

            if (cols[col] || diag1[d1] || diag2[d2])
                continue;

            cols[col] = diag1[d1] = diag2[d2] = true;
            count += backtrackTraditional(row + 1, cols, diag1, diag2, n);
            cols[col] = diag1[d1] = diag2[d2] = false;
        }

        return count;
    }

    public static void main(String[] args) {
        NQueensIIBitManipulation solution = new NQueensIIBitManipulation();

        System.out.println(solution.totalNQueens(4)); // Expected: 2
        System.out.println(solution.totalNQueens(1)); // Expected: 1
        System.out.println(solution.totalNQueens(8)); // Expected: 92
        System.out.println(solution.totalNQueens(9)); // Expected: 352
        System.out.println(solution.totalNQueensTraditional(4)); // Expected: 2
    }
}
