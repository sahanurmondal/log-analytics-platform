package grid.hard;

/**
 * LeetCode 52: N-Queens II
 * https://leetcode.com/problems/n-queens-ii/
 *
 * Description:
 * The n-queens puzzle is the problem of placing n queens on an n x n chessboard
 * such that no two queens attack each other.
 * Given an integer n, return the number of distinct solutions to the n-queens
 * puzzle.
 *
 * Constraints:
 * - 1 <= n <= 9
 */
public class NQueensII {

    public int totalNQueens(int n) {
        return solve(n, 0, new boolean[n], new boolean[2 * n], new boolean[2 * n]);
    }

    private int solve(int n, int row, boolean[] cols, boolean[] diag1, boolean[] diag2) {
        if (row == n) {
            return 1;
        }

        int count = 0;
        for (int col = 0; col < n; col++) {
            int d1 = row - col + n;
            int d2 = row + col;

            if (!cols[col] && !diag1[d1] && !diag2[d2]) {
                cols[col] = diag1[d1] = diag2[d2] = true;
                count += solve(n, row + 1, cols, diag1, diag2);
                cols[col] = diag1[d1] = diag2[d2] = false;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        NQueensII solution = new NQueensII();

        System.out.println(solution.totalNQueens(4)); // 2
        System.out.println(solution.totalNQueens(1)); // 1
        System.out.println(solution.totalNQueens(8)); // 92
    }
}
