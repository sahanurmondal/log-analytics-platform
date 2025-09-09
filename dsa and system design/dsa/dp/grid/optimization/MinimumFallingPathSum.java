package dp.grid.optimization;

import java.util.Arrays;

/**
 * LeetCode 931: Minimum Falling Path Sum
 * https://leetcode.com/problems/minimum-falling-path-sum/
 *
 * Description:
 * Given an n x n array of integers matrix, return the minimum sum of any
 * falling path through matrix.
 * A falling path starts at any element in the first row and chooses the element
 * in the next row that is
 * either directly below or diagonally left/right. Specifically, the next
 * element from position (row, col)
 * will be (row + 1, col - 1), (row + 1, col), or (row + 1, col + 1).
 *
 * Constraints:
 * - n == matrix.length == matrix[i].length
 * - 1 <= n <= 100
 * - -100 <= matrix[i][j] <= 100
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - What if we can move in 4 directions?
 *
 * Company Tags: Amazon, Google, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class MinimumFallingPathSum {

    // Approach 1: Recursive (Brute Force) - O(3^n) time, O(n) space
    public int minFallingPathSumRecursive(int[][] matrix) {
        int n = matrix.length;
        int minSum = Integer.MAX_VALUE;

        for (int col = 0; col < n; col++) {
            minSum = Math.min(minSum, minFallingPathHelper(matrix, 0, col));
        }

        return minSum;
    }

    private int minFallingPathHelper(int[][] matrix, int row, int col) {
        int n = matrix.length;

        // Base case: out of bounds
        if (col < 0 || col >= n)
            return Integer.MAX_VALUE;

        // Base case: reached last row
        if (row == n - 1)
            return matrix[row][col];

        // Try all three possible moves
        int left = minFallingPathHelper(matrix, row + 1, col - 1);
        int straight = minFallingPathHelper(matrix, row + 1, col);
        int right = minFallingPathHelper(matrix, row + 1, col + 1);

        return matrix[row][col] + Math.min(Math.min(left, straight), right);
    }

    // Approach 2: Memoization (Top-down DP) - O(n^2) time, O(n^2) space
    public int minFallingPathSumMemo(int[][] matrix) {
        int n = matrix.length;
        Integer[][] memo = new Integer[n][n];
        int minSum = Integer.MAX_VALUE;

        for (int col = 0; col < n; col++) {
            minSum = Math.min(minSum, minFallingPathMemoHelper(matrix, 0, col, memo));
        }

        return minSum;
    }

    private int minFallingPathMemoHelper(int[][] matrix, int row, int col, Integer[][] memo) {
        int n = matrix.length;

        if (col < 0 || col >= n)
            return Integer.MAX_VALUE;
        if (row == n - 1)
            return matrix[row][col];

        if (memo[row][col] != null)
            return memo[row][col];

        int left = minFallingPathMemoHelper(matrix, row + 1, col - 1, memo);
        int straight = minFallingPathMemoHelper(matrix, row + 1, col, memo);
        int right = minFallingPathMemoHelper(matrix, row + 1, col + 1, memo);

        memo[row][col] = matrix[row][col] + Math.min(Math.min(left, straight), right);
        return memo[row][col];
    }

    // Approach 3: Tabulation (Bottom-up DP) - O(n^2) time, O(n^2) space
    public int minFallingPathSumDP(int[][] matrix) {
        int n = matrix.length;
        int[][] dp = new int[n][n];

        // Initialize last row
        for (int col = 0; col < n; col++) {
            dp[n - 1][col] = matrix[n - 1][col];
        }

        // Fill from bottom to top
        for (int row = n - 2; row >= 0; row--) {
            for (int col = 0; col < n; col++) {
                int minFromBelow = dp[row + 1][col];

                if (col > 0) {
                    minFromBelow = Math.min(minFromBelow, dp[row + 1][col - 1]);
                }

                if (col < n - 1) {
                    minFromBelow = Math.min(minFromBelow, dp[row + 1][col + 1]);
                }

                dp[row][col] = matrix[row][col] + minFromBelow;
            }
        }

        // Find minimum in first row
        int result = dp[0][0];
        for (int col = 1; col < n; col++) {
            result = Math.min(result, dp[0][col]);
        }

        return result;
    }

    // Approach 4: Space Optimized - O(n^2) time, O(n) space
    public int minFallingPathSumOptimized(int[][] matrix) {
        int n = matrix.length;
        int[] prev = new int[n];
        int[] curr = new int[n];

        // Initialize with last row
        System.arraycopy(matrix[n - 1], 0, prev, 0, n);

        // Process from bottom to top
        for (int row = n - 2; row >= 0; row--) {
            for (int col = 0; col < n; col++) {
                int minFromBelow = prev[col];

                if (col > 0) {
                    minFromBelow = Math.min(minFromBelow, prev[col - 1]);
                }

                if (col < n - 1) {
                    minFromBelow = Math.min(minFromBelow, prev[col + 1]);
                }

                curr[col] = matrix[row][col] + minFromBelow;
            }

            // Swap arrays
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        // Find minimum in result
        int result = prev[0];
        for (int col = 1; col < n; col++) {
            result = Math.min(result, prev[col]);
        }

        return result;
    }

    // Approach 5: In-place Modification - O(n^2) time, O(1) space
    public int minFallingPathSumInPlace(int[][] matrix) {
        int n = matrix.length;

        // Process from second row to last
        for (int row = 1; row < n; row++) {
            for (int col = 0; col < n; col++) {
                int minFromAbove = matrix[row - 1][col];

                if (col > 0) {
                    minFromAbove = Math.min(minFromAbove, matrix[row - 1][col - 1]);
                }

                if (col < n - 1) {
                    minFromAbove = Math.min(minFromAbove, matrix[row - 1][col + 1]);
                }

                matrix[row][col] += minFromAbove;
            }
        }

        // Find minimum in last row
        int result = matrix[n - 1][0];
        for (int col = 1; col < n; col++) {
            result = Math.min(result, matrix[n - 1][col]);
        }

        return result;
    }

    public static void main(String[] args) {
        MinimumFallingPathSum solution = new MinimumFallingPathSum();

        System.out.println("=== Minimum Falling Path Sum Test Cases ===");

        // Test Case 1: Example from problem
        int[][] matrix1 = { { 2, 1, 3 }, { 6, 5, 4 }, { 7, 8, 9 } };
        System.out.println("Test 1 - Matrix:");
        printMatrix(matrix1);
        System.out.println("Recursive: " + solution.minFallingPathSumRecursive(matrix1));
        System.out.println("Memoization: " + solution.minFallingPathSumMemo(matrix1));
        System.out.println("DP: " + solution.minFallingPathSumDP(matrix1));
        System.out.println("Optimized: " + solution.minFallingPathSumOptimized(matrix1));
        // Note: In-place modifies the matrix, so test separately
        int[][] matrix1Copy = { { 2, 1, 3 }, { 6, 5, 4 }, { 7, 8, 9 } };
        System.out.println("In-place: " + solution.minFallingPathSumInPlace(matrix1Copy));
        System.out.println("Expected: 13\n");

        // Test Case 2: Single element
        int[][] matrix2 = { { -19 } };
        System.out.println("Test 2 - Matrix:");
        printMatrix(matrix2);
        System.out.println("DP: " + solution.minFallingPathSumDP(matrix2));
        System.out.println("Expected: -19\n");

        // Test Case 3: Negative values
        int[][] matrix3 = { { -48 } };
        System.out.println("Test 3 - Matrix:");
        printMatrix(matrix3);
        System.out.println("DP: " + solution.minFallingPathSumDP(matrix3));
        System.out.println("Expected: -48\n");

        performanceTest();
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        MinimumFallingPathSum solution = new MinimumFallingPathSum();

        int n = 100;
        int[][] largeMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                largeMatrix[i][j] = (int) (Math.random() * 201) - 100; // -100 to 100
            }
        }

        System.out.println("=== Performance Test (Matrix size: " + n + "x" + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.minFallingPathSumMemo(largeMatrix);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.minFallingPathSumDP(largeMatrix);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.minFallingPathSumOptimized(largeMatrix);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
