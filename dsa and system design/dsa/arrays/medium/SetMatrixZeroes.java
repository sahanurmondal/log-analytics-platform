package arrays.medium;

/**
 * LeetCode 73: Set Matrix Zeroes
 * https://leetcode.com/problems/set-matrix-zeroes/
 */
public class SetMatrixZeroes {
    // Main solution - O(1) space using first row/column as markers
    public void setZeroes(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean firstRowZero = false, firstColZero = false;

        // Check if first row should be zero
        for (int j = 0; j < n; j++) {
            if (matrix[0][j] == 0) {
                firstRowZero = true;
                break;
            }
        }

        // Check if first column should be zero
        for (int i = 0; i < m; i++) {
            if (matrix[i][0] == 0) {
                firstColZero = true;
                break;
            }
        }

        // Use first row and column as markers
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 0) {
                    matrix[i][0] = 0;
                    matrix[0][j] = 0;
                }
            }
        }

        // Set zeros based on markers
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][0] == 0 || matrix[0][j] == 0) {
                    matrix[i][j] = 0;
                }
            }
        }

        // Handle first row
        if (firstRowZero) {
            for (int j = 0; j < n; j++) {
                matrix[0][j] = 0;
            }
        }

        // Handle first column
        if (firstColZero) {
            for (int i = 0; i < m; i++) {
                matrix[i][0] = 0;
            }
        }
    }

    // Alternative solution - Using extra space
    public void setZeroesExtraSpace(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        boolean[] zeroRows = new boolean[m];
        boolean[] zeroCols = new boolean[n];

        // Mark zero rows and columns
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 0) {
                    zeroRows[i] = true;
                    zeroCols[j] = true;
                }
            }
        }

        // Set zeros
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (zeroRows[i] || zeroCols[j]) {
                    matrix[i][j] = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        SetMatrixZeroes solution = new SetMatrixZeroes();
        // Edge Case 1: Normal case
        int[][] m1 = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };
        solution.setZeroes(m1);
        printMatrix(m1);
        // Edge Case 2: All zeroes
        int[][] m2 = { { 0, 0 }, { 0, 0 } };
        solution.setZeroes(m2);
        printMatrix(m2);
        // Edge Case 3: No zeroes
        int[][] m3 = { { 1, 2 }, { 3, 4 } };
        solution.setZeroes(m3);
        printMatrix(m3);
        // Edge Case 4: Zero at edge
        int[][] m4 = { { 0, 2 }, { 3, 4 } };
        solution.setZeroes(m4);
        printMatrix(m4);
        // Edge Case 5: Large matrix
        int[][] m5 = new int[200][200];
        m5[100][100] = 0;
        solution.setZeroes(m5);
        printMatrix(m5);
        // Edge Case 6: Single row
        int[][] m6 = { { 1, 0, 3 } };
        solution.setZeroes(m6);
        printMatrix(m6);
        // Edge Case 7: Single column
        int[][] m7 = { { 1 }, { 0 }, { 3 } };
        solution.setZeroes(m7);
        printMatrix(m7);

        // Additional Test Cases
        int[][] matrix1 = { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };
        solution.setZeroes(matrix1);
        // Expected: [[1,0,1],[0,0,0],[1,0,1]]
        printMatrix(matrix1);

        int[][] matrix2 = { { 0, 1, 2, 0 }, { 3, 4, 5, 2 }, { 1, 3, 1, 5 } };
        solution.setZeroes(matrix2);
        // Expected: [[0,0,0,0],[0,4,5,0],[0,3,1,0]]
        printMatrix(matrix2);
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            for (int val : row)
                System.out.print(val + " ");
            System.out.println();
        }
        System.out.println();
    }
}
