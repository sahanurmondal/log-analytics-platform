package grid.medium;

/**
 * LeetCode 1314: Matrix Block Sum
 * https://leetcode.com/problems/matrix-block-sum/
 *
 * Description:
 * Given a m x n matrix mat and an integer k, return a matrix answer where each
 * answer[i][j] is the sum of all elements
 * mat[r][c] for:
 * - i - k <= r <= i + k
 * - j - k <= c <= j + k
 * - (r, c) is a valid position in the matrix
 *
 * Constraints:
 * - m == mat.length
 * - n == mat[i].length
 * - 1 <= m, n, k <= 100
 * - 1 <= mat[i][j] <= 100
 */
public class MatrixBlockSum {

    public int[][] matrixBlockSum(int[][] mat, int k) {
        int m = mat.length, n = mat[0].length;

        // Build prefix sum array
        int[][] prefix = new int[m + 1][n + 1];
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefix[i][j] = mat[i - 1][j - 1] + prefix[i - 1][j] + prefix[i][j - 1] - prefix[i - 1][j - 1];
            }
        }

        int[][] result = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int r1 = Math.max(0, i - k);
                int c1 = Math.max(0, j - k);
                int r2 = Math.min(m - 1, i + k);
                int c2 = Math.min(n - 1, j + k);

                result[i][j] = prefix[r2 + 1][c2 + 1] - prefix[r1][c2 + 1] - prefix[r2 + 1][c1] + prefix[r1][c1];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        MatrixBlockSum solution = new MatrixBlockSum();

        int[][] mat = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        int k = 1;
        int[][] result = solution.matrixBlockSum(mat, k);

        for (int[] row : result) {
            System.out.println(java.util.Arrays.toString(row));
        }
        // Output: [[12,21,16],[27,45,33],[24,39,28]]
    }
}
