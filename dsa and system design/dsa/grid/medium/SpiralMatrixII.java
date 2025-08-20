package grid.medium;

/**
 * LeetCode 59: Spiral Matrix II
 * https://leetcode.com/problems/spiral-matrix-ii/
 *
 * Description:
 * Given a positive integer n, generate an n x n matrix filled with elements
 * from 1 to n^2 in spiral order.
 *
 * Constraints:
 * - 1 <= n <= 20
 */
public class SpiralMatrixII {

    public int[][] generateMatrix(int n) {
        int[][] matrix = new int[n][n];
        int top = 0, bottom = n - 1, left = 0, right = n - 1;
        int num = 1;

        while (top <= bottom && left <= right) {
            // Traverse right
            for (int j = left; j <= right; j++) {
                matrix[top][j] = num++;
            }
            top++;

            // Traverse down
            for (int i = top; i <= bottom; i++) {
                matrix[i][right] = num++;
            }
            right--;

            // Traverse left
            if (top <= bottom) {
                for (int j = right; j >= left; j--) {
                    matrix[bottom][j] = num++;
                }
                bottom--;
            }

            // Traverse up
            if (left <= right) {
                for (int i = bottom; i >= top; i--) {
                    matrix[i][left] = num++;
                }
                left++;
            }
        }

        return matrix;
    }

    public static void main(String[] args) {
        SpiralMatrixII solution = new SpiralMatrixII();

        int[][] result = solution.generateMatrix(3);
        for (int[] row : result) {
            System.out.println(java.util.Arrays.toString(row));
        }
        // Output: [[1,2,3],[8,9,4],[7,6,5]]
    }
}
