package recursion.medium;

/**
 * LeetCode 52: N-Queens II
 * https://leetcode.com/problems/n-queens-ii/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Return the number of distinct solutions to the n-queens puzzle.
 *
 * Constraints:
 * - 1 <= n <= 9
 *
 * Follow-ups:
 * 1. Can you generate all solutions?
 * 2. Can you optimize for large n?
 * 3. Can you solve for other board sizes?
 */
public class NQueensII {
    public int totalNQueens(int n) {
        return dfs(n, 0, 0, 0, 0);
    }

    private int dfs(int n, int row, int cols, int diags1, int diags2) {
        if (row == n)
            return 1;
        int count = 0;
        int available = ((1 << n) - 1) & ~(cols | diags1 | diags2);
        while (available != 0) {
            int bit = available & -available;
            available &= available - 1;
            count += dfs(n, row + 1, cols | bit, (diags1 | bit) << 1, (diags2 | bit) >> 1);
        }
        return count;
    }

    // Follow-up 1: Generate all solutions
    public java.util.List<java.util.List<String>> solveNQueens(int n) {
        java.util.List<java.util.List<String>> res = new java.util.ArrayList<>();
        dfsGen(n, 0, new int[n], res);
        return res;
    }

    private void dfsGen(int n, int row, int[] queens, java.util.List<java.util.List<String>> res) {
        if (row == n) {
            java.util.List<String> board = new java.util.ArrayList<>();
            for (int i = 0; i < n; i++) {
                char[] rowArr = new char[n];
                java.util.Arrays.fill(rowArr, '.');
                rowArr[queens[i]] = 'Q';
                board.add(new String(rowArr));
            }
            res.add(board);
            return;
        }
        for (int col = 0; col < n; col++) {
            boolean valid = true;
            for (int i = 0; i < row; i++) {
                if (queens[i] == col || Math.abs(queens[i] - col) == row - i) {
                    valid = false;
                    break;
                }
            }
            if (valid) {
                queens[row] = col;
                dfsGen(n, row + 1, queens, res);
            }
        }
    }

    // Follow-up 2: Optimize for large n (already handled above)
    // Follow-up 3: Solve for other board sizes (not implemented)

    public static void main(String[] args) {
        NQueensII solution = new NQueensII();
        System.out.println(solution.totalNQueens(4)); // 2
        System.out.println(solution.solveNQueens(4)); // [[".Q..","...Q","Q...","..Q."],["..Q.","Q...","...Q",".Q.."]]
    }
}
