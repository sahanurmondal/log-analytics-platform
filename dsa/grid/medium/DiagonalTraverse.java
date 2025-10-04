package grid.medium;

/**
 * LeetCode 498: Diagonal Traverse
 * https://leetcode.com/problems/diagonal-traverse/
 *
 * Description:
 * Given an m x n matrix mat, return an array of all the elements of the array
 * in a diagonal order.
 *
 * Constraints:
 * - m == mat.length
 * - n == mat[i].length
 * - 1 <= m, n <= 10^4
 * - 1 <= m * n <= 10^4
 * - -10^5 <= mat[i][j] <= 10^5
 */
public class DiagonalTraverse {

    public int[] findDiagonalOrder(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int[] result = new int[m * n];
        int index = 0;

        for (int d = 0; d < m + n - 1; d++) {
            if (d % 2 == 0) {
                // Going up
                int row = Math.min(d, m - 1);
                int col = d - row;
                while (row >= 0 && col < n) {
                    result[index++] = mat[row][col];
                    row--;
                    col++;
                }
            } else {
                // Going down
                int col = Math.min(d, n - 1);
                int row = d - col;
                while (col >= 0 && row < m) {
                    result[index++] = mat[row][col];
                    row++;
                    col--;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        DiagonalTraverse solution = new DiagonalTraverse();

        int[][] mat = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        int[] result = solution.findDiagonalOrder(mat);

        System.out.println(java.util.Arrays.toString(result));
        // Output: [1,2,4,7,5,3,6,8,9]
    }
}
