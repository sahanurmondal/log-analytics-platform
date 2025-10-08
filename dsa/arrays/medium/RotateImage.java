package arrays.medium;

import java.util.*;

/**
 * LeetCode 48: Rotate Image
 * https://leetcode.com/problems/rotate-image/
 *
 * Mathematical Derivation of Rotation Coordinates:
 * ================================================
 *
 * STEP 1: Understanding 90° Clockwise Rotation
 * --------------------------------------------
 * When rotating a point 90° clockwise around the center of an n×n matrix:
 * - The center of the matrix is at ((n-1)/2, (n-1)/2)
 * - We need to find where (row, col) moves after rotation
 *
 * STEP 2: Coordinate System Transform
 * -----------------------------------
 * Convert from matrix coordinates to Cartesian coordinates (center at origin):
 * - x_cartesian = col - (n-1)/2
 * - y_cartesian = (n-1)/2 - row  (Note: y-axis is flipped in matrices)
 *
 * STEP 3: Apply 90° Clockwise Rotation Formula
 * --------------------------------------------
 * In Cartesian coordinates, rotating (x, y) by 90° clockwise gives (y, -x):
 * - x_new = y_cartesian
 * - y_new = -x_cartesian
 *
 * STEP 4: Convert Back to Matrix Coordinates
 * ------------------------------------------
 * Transform back from Cartesian to matrix coordinates:
 * - col_new = x_new + (n-1)/2 = y_cartesian + (n-1)/2
 * - row_new = (n-1)/2 - y_new = (n-1)/2 - (-x_cartesian)
 *
 * STEP 5: Substitute and Simplify
 * -------------------------------
 * Substituting the expressions from STEP 2:
 *
 * col_new = [(n-1)/2 - row] + (n-1)/2
 *         = n - 1 - row
 *
 * row_new = (n-1)/2 - [-(col - (n-1)/2)]
 *         = (n-1)/2 + col - (n-1)/2
 *         = col
 *
 * CONCLUSION: (row, col) → (col, n-1-row)
 *
 * STEP 6: Derive the 4-Position Cycle
 * -----------------------------------
 * Starting from (i, j), apply the rotation formula repeatedly:
 *
 * 1st rotation: (i, j) → (j, n-1-i)
 *    Explanation: row=i, col=j → new_row=j, new_col=n-1-i
 *
 * 2nd rotation: (j, n-1-i) → (n-1-i, n-1-j)
 *    Explanation: row=j, col=n-1-i → new_row=n-1-i, new_col=n-1-j
 *
 * 3rd rotation: (n-1-i, n-1-j) → (n-1-j, i)
 *    Explanation: row=n-1-i, col=n-1-j → new_row=n-1-j, new_col=i
 *
 * 4th rotation: (n-1-j, i) → (i, j)
 *    Explanation: row=n-1-j, col=i → new_row=i, new_col=j (back to start!)
 *
 * VERIFICATION: Let's verify with a 4×4 matrix (n=4), position (1, 2):
 * -------------------------------------------------------------------
 * Start:        (1, 2)
 * After 90°:    (2, 4-1-1) = (2, 2)  ✗ Wait, let me recalculate...
 *
 * Actually for (1,2) in 4×4:
 * (1, 2) → (2, 3-1) = (2, 2)  ✗ This doesn't look right...
 *
 * Let me trace through correctly:
 * Position (1, 2) should move to (2, 2)... No, let me visualize:
 *
 * Original:     After 90° CW:
 * 0 1 2 3       C 8 4 0
 * 4 5 6 7  →    D 9 5 1
 * 8 9 A B       E A 6 2
 * C D E F       F B 7 3
 *
 * Position (1,2) contains '6':
 * After rotation, '6' is at position (2, 2)
 * Using formula: (1, 2) → (2, 4-1-1) = (2, 2) ✓
 *
 * THE FOUR POSITIONS IN THE CYCLE:
 * ================================
 * For any starting position (i, j), these four cells swap in a cycle:
 *
 * Position 1: (i, j)           - Top edge element
 * Position 2: (j, n-1-i)       - Right edge element
 * Position 3: (n-1-i, n-1-j)   - Bottom edge element
 * Position 4: (n-1-j, i)       - Left edge element
 *
 * SWAP LOGIC:
 * -----------
 * To rotate in-place, we perform: 4 → 3 → 2 → 1 → temp (where temp holds 4)
 *
 * temp = Position 4
 * Position 4 = Position 3
 * Position 3 = Position 2
 * Position 2 = Position 1
 * Position 1 = temp
 *
 * This moves each element to its correct rotated position in one cycle.
 */
public class RotateImage {
    // Main solution - Transpose then reverse rows
    public void rotate(int[][] matrix) {
        int n = matrix.length;

        // Transpose the matrix
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

    // Alternative solution - Layer by layer rotation
    public void rotateLayerByLayer(int[][] matrix) {
        int n = matrix.length;

        for (int layer = 0; layer < n / 2; layer++) {
            int first = layer;
            int last = n - 1 - layer;

            for (int i = first; i < last; i++) {
                int offset = i - first;
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

    // Follow-up solution - Four-way swap
    public void rotateFourWay(int[][] matrix) {
        int n = matrix.length;

        for (int i = 0; i < (n + 1) / 2; i++) {
            for (int j = 0; j < n / 2; j++) {
                // Four-way swap
                int temp = matrix[n - 1 - j][i];
                matrix[n - 1 - j][i] = matrix[n - 1 - i][n - 1 - j];
                matrix[n - 1 - i][n - 1 - j] = matrix[j][n - 1 - i];
                matrix[j][n - 1 - i] = matrix[i][j];
                matrix[i][j] = temp;
            }
        }
    }

    private void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        RotateImage solution = new RotateImage();

        // Test Case 1: Normal case - 3x3
        int[][] matrix1 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        solution.rotate(matrix1);
        solution.printMatrix(matrix1); // Expected: [[7,4,1],[8,5,2],[9,6,3]]

        // Test Case 2: Edge case - 4x4
        int[][] matrix2 = { { 5, 1, 9, 11 }, { 2, 4, 8, 10 }, { 13, 3, 6, 7 }, { 15, 14, 12, 16 } };
        solution.rotate(matrix2);
        solution.printMatrix(matrix2); // Expected: [[15,13,2,5],[14,3,4,1],[12,6,8,9],[16,7,10,11]]

        // Test Case 3: Corner case - 1x1
        int[][] matrix3 = { { 1 } };
        solution.rotate(matrix3);
        solution.printMatrix(matrix3); // Expected: [[1]]

        // Test Case 4: 2x2 matrix
        int[][] matrix4 = { { 1, 2 }, { 3, 4 } };
        solution.rotate(matrix4);
        solution.printMatrix(matrix4); // Expected: [[3,1],[4,2]]

        // Test Case 5: Negative numbers
        int[][] matrix5 = { { -1, -2 }, { -3, -4 } };
        solution.rotate(matrix5);
        solution.printMatrix(matrix5); // Expected: [[-3,-1],[-4,-2]]

        // Test Case 6: Mixed values
        int[][] matrix6 = { { 0, 1 }, { 2, 3 } };
        solution.rotate(matrix6);
        solution.printMatrix(matrix6); // Expected: [[2,0],[3,1]]

        // Test Case 7: Large values
        int[][] matrix7 = { { 1000, -1000 }, { 500, -500 } };
        solution.rotate(matrix7);
        solution.printMatrix(matrix7); // Expected: [[500,1000],[-500,-1000]]

        // Test Case 8: 5x5 matrix
        int[][] matrix8 = { { 1, 2, 3, 4, 5 }, { 6, 7, 8, 9, 10 }, { 11, 12, 13, 14, 15 }, { 16, 17, 18, 19, 20 },
                { 21, 22, 23, 24, 25 } };
        solution.rotate(matrix8);
        solution.printMatrix(matrix8); // Expected:
                                       // [[21,16,11,6,1],[22,17,12,7,2],[23,18,13,8,3],[24,19,14,9,4],[25,20,15,10,5]]

        // Test Case 9: Same numbers
        int[][] matrix9 = { { 1, 1 }, { 1, 1 } };
        solution.rotate(matrix9);
        solution.printMatrix(matrix9); // Expected: [[1,1],[1,1]]

        // Test Case 10: Sequential numbers
        int[][] matrix10 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        solution.rotate(matrix10);
        solution.printMatrix(matrix10); // Expected: [[7,4,1],[8,5,2],[9,6,3]]
    }
}
