package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 1277: Count Square Submatrices with All Ones
 * https://leetcode.com/problems/count-square-submatrices-with-all-ones/
 *
 * Description:
 * Given a m * n matrix of ones and zeros, return how many square submatrices
 * have all ones.
 *
 * Constraints:
 * - 1 <= arr.length <= 300
 * - 1 <= arr[0].length <= 300
 * - 0 <= arr[i][j] <= 1
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - What if we need to find rectangles instead of squares?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class CountSquareSubmatricesWithAllOnes {

    // Approach 1: Brute Force - O(m*n*(min(m,n))^3) time, O(1) space
    public int countSquaresBruteForce(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int maxSize = Math.min(m - i, n - j);

                for (int size = 1; size <= maxSize; size++) {
                    if (isSquareValid(matrix, i, j, size)) {
                        count++;
                    } else {
                        break;
                    }
                }
            }
        }

        return count;
    }

    private boolean isSquareValid(int[][] matrix, int row, int col, int size) {
        for (int i = row; i < row + size; i++) {
            for (int j = col; j < col + size; j++) {
                if (matrix[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    // Approach 2: DP - O(m*n) time, O(m*n) space
    public int countSquaresDP(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] dp = new int[m][n];
        int count = 0;

        // Initialize first row and column
        for (int i = 0; i < m; i++) {
            dp[i][0] = matrix[i][0];
            count += dp[i][0];
        }

        for (int j = 1; j < n; j++) {
            dp[0][j] = matrix[0][j];
            count += dp[0][j];
        }

        // Fill DP table
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 1) {
                    dp[i][j] = Math.min(Math.min(dp[i - 1][j], dp[i][j - 1]), dp[i - 1][j - 1]) + 1;
                }
                count += dp[i][j];
            }
        }

        return count;
    }

    // Approach 3: Space Optimized DP - O(m*n) time, O(n) space
    public int countSquaresOptimized(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[] prev = new int[n];
        int[] curr = new int[n];
        int count = 0;

        // Initialize first row
        for (int j = 0; j < n; j++) {
            prev[j] = matrix[0][j];
            count += prev[j];
        }

        for (int i = 1; i < m; i++) {
            curr[0] = matrix[i][0];
            count += curr[0];

            for (int j = 1; j < n; j++) {
                if (matrix[i][j] == 1) {
                    curr[j] = Math.min(Math.min(prev[j], curr[j - 1]), prev[j - 1]) + 1;
                } else {
                    curr[j] = 0;
                }
                count += curr[j];
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return count;
    }

    // Approach 4: In-place DP - O(m*n) time, O(1) space
    public int countSquaresInPlace(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int count = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == 1 && i > 0 && j > 0) {
                    matrix[i][j] = Math.min(Math.min(matrix[i - 1][j], matrix[i][j - 1]), matrix[i - 1][j - 1]) + 1;
                }
                count += matrix[i][j];
            }
        }

        return count;
    }

    // Approach 5: Stack-based approach - O(m*n) time, O(n) space
    public int countSquaresStack(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int count = 0;

        int[] heights = new int[n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                heights[j] = matrix[i][j] == 0 ? 0 : heights[j] + 1;
            }

            count += countSquaresInRow(heights);
        }

        return count;
    }

    private int countSquaresInRow(int[] heights) {
        int n = heights.length;
        int count = 0;

        for (int i = 0; i < n; i++) {
            int minHeight = heights[i];
            for (int j = i; j < n; j++) {
                minHeight = Math.min(minHeight, heights[j]);
                int size = j - i + 1;
                if (minHeight >= size) {
                    count++;
                } else {
                    break;
                }
            }
        }

        return count;
    }

    public static void main(String[] args) {
        CountSquareSubmatricesWithAllOnes solution = new CountSquareSubmatricesWithAllOnes();

        System.out.println("=== Count Square Submatrices with All Ones Test Cases ===");

        // Test Case 1: Example from problem
        int[][] matrix1 = {
                { 0, 1, 1, 1 },
                { 1, 1, 1, 1 },
                { 0, 1, 1, 1 }
        };
        System.out.println("Test 1 - Matrix:");
        printMatrix(matrix1);
        System.out.println("Brute Force: " + solution.countSquaresBruteForce(matrix1));
        System.out.println("DP: " + solution.countSquaresDP(matrix1));
        System.out.println("Optimized: " + solution.countSquaresOptimized(matrix1));
        // Note: In-place modifies matrix, test separately
        int[][] matrix1Copy = {
                { 0, 1, 1, 1 },
                { 1, 1, 1, 1 },
                { 0, 1, 1, 1 }
        };
        System.out.println("In-place: " + solution.countSquaresInPlace(matrix1Copy));
        System.out.println("Expected: 15\n");

        // Test Case 2: Another example
        int[][] matrix2 = {
                { 1, 0, 1 },
                { 1, 1, 0 },
                { 1, 1, 0 }
        };
        System.out.println("Test 2 - Matrix:");
        printMatrix(matrix2);
        System.out.println("DP: " + solution.countSquaresDP(matrix2));
        System.out.println("Expected: 7\n");

        performanceTest();
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        CountSquareSubmatricesWithAllOnes solution = new CountSquareSubmatricesWithAllOnes();

        int m = 300, n = 300;
        int[][] largeMatrix = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                largeMatrix[i][j] = Math.random() > 0.5 ? 1 : 0;
            }
        }

        System.out.println("=== Performance Test (Matrix size: " + m + "x" + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.countSquaresDP(largeMatrix);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.countSquaresOptimized(largeMatrix);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
