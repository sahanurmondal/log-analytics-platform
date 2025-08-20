package grid.medium;

/**
 * LeetCode 48: Rotate Image
 * https://leetcode.com/problems/rotate-image/
 *
 * Description:
 * You are given an n x n 2D matrix representing an image, rotate the image by
 * 90 degrees (clockwise).
 * You have to rotate the image in-place, which means you have to modify the
 * input 2D matrix directly.
 * DO NOT allocate another 2D matrix and do the rotation.
 *
 * Constraints:
 * - n == matrix.length == matrix[i].length
 * - 1 <= n <= 20
 * - -1000 <= matrix[i][j] <= 1000
 */
public class RotateImage {

    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Transpose matrix
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[j][i];
                matrix[j][i] = temp;
            }
        }

        // Reverse each row
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n / 2; j++) {
                int temp = matrix[i][j];
                matrix[i][j] = matrix[i][n - 1 - j];
                matrix[i][n - 1 - j] = temp;
            }
        }
    }

    // Alternative: Direct rotation
    public void rotateLayer(int[][] matrix) {
        int n = matrix.length;

        for (int layer = 0; layer < n / 2; layer++) {
            int first = layer;
            int last = n - 1 - layer;

            for (int i = first; i < last; i++) {
                int offset = i - first;

                // Save top element
                int top = matrix[first][i];

                // Top = Left
                matrix[first][i] = matrix[last - offset][first];

                // Left = Bottom
                matrix[last - offset][first] = matrix[last][last - offset];

                // Bottom = Right
                matrix[last][last - offset] = matrix[i][last];

                // Right = Top
                matrix[i][last] = top;
            }
        }
    }

    public static void main(String[] args) {
        RotateImage solution = new RotateImage();

        int[][] matrix = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        solution.rotate(matrix);

        for (int[] row : matrix) {
            System.out.println(java.util.Arrays.toString(row));
        }
        // Output: [[7,4,1],[8,5,2],[9,6,3]]
    }
}
