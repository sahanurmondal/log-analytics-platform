package dp.medium;

import java.util.*;

/**
 * LeetCode 1074: Number of Submatrices That Sum to Target
 * https://leetcode.com/problems/number-of-submatrices-that-sum-to-target/
 *
 * Description:
 * Given a matrix and a target, return the number of non-empty submatrices that
 * sum to target.
 *
 * Constraints:
 * - 1 <= matrix.length, matrix[0].length <= 100
 * - -1000 <= matrix[i][j] <= 1000
 * - -10^8 <= target <= 10^8
 *
 * Follow-up:
 * - Can you solve it in O(m^2*n) time?
 * 
 * Company Tags: Google, Amazon
 * Difficulty: Medium
 */
public class NumberOfSubmatricesThatSumToTarget {

    // Approach 1: Prefix Sum + HashMap - O(m^2*n) time, O(n) space
    public int numSubmatrixSumTarget(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int count = 0;

        // For each pair of rows
        for (int r1 = 0; r1 < m; r1++) {
            int[] colSum = new int[n];

            for (int r2 = r1; r2 < m; r2++) {
                // Add current row to column sums
                for (int c = 0; c < n; c++) {
                    colSum[c] += matrix[r2][c];
                }

                // Find subarrays in colSum that sum to target
                count += subarraySum(colSum, target);
            }
        }

        return count;
    }

    private int subarraySum(int[] nums, int target) {
        Map<Integer, Integer> prefixSum = new HashMap<>();
        prefixSum.put(0, 1);

        int sum = 0, count = 0;

        for (int num : nums) {
            sum += num;
            count += prefixSum.getOrDefault(sum - target, 0);
            prefixSum.put(sum, prefixSum.getOrDefault(sum, 0) + 1);
        }

        return count;
    }

    // Approach 2: Brute Force with Prefix Sum - O(m^2*n^2) time, O(1) space
    public int numSubmatrixSumTargetBruteForce(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int count = 0;

        // Build prefix sum matrix
        int[][] prefixSum = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefixSum[i][j] = matrix[i - 1][j - 1] + prefixSum[i - 1][j] +
                        prefixSum[i][j - 1] - prefixSum[i - 1][j - 1];
            }
        }

        // Check all possible submatrices
        for (int r1 = 0; r1 < m; r1++) {
            for (int c1 = 0; c1 < n; c1++) {
                for (int r2 = r1; r2 < m; r2++) {
                    for (int c2 = c1; c2 < n; c2++) {
                        int sum = prefixSum[r2 + 1][c2 + 1] - prefixSum[r1][c2 + 1] -
                                prefixSum[r2 + 1][c1] + prefixSum[r1][c1];

                        if (sum == target) {
                            count++;
                        }
                    }
                }
            }
        }

        return count;
    }

    public static void main(String[] args) {
        NumberOfSubmatricesThatSumToTarget solution = new NumberOfSubmatricesThatSumToTarget();

        System.out.println("=== Number of Submatrices That Sum to Target Test Cases ===");

        // Test Case 1: Normal case
        int[][] matrix1 = { { 0, 1, 0 }, { 1, 1, 1 }, { 0, 1, 0 } };
        System.out.println("Test 1 - Matrix: " + Arrays.deepToString(matrix1) + ", target: 0");
        System.out.println("Optimized: " + solution.numSubmatrixSumTarget(matrix1, 0));
        System.out.println("Brute Force: " + solution.numSubmatrixSumTargetBruteForce(matrix1, 0));
        System.out.println("Expected: 4\n");

        // Test Case 2: All zeros
        int[][] matrix2 = { { 0, 0 }, { 0, 0 } };
        System.out.println("Test 2 - All zeros, target: 0");
        System.out.println("Optimized: " + solution.numSubmatrixSumTarget(matrix2, 0));
        System.out.println("Expected: 10\n");

        // Test Case 3: All ones
        int[][] matrix3 = { { 1, 1 }, { 1, 1 } };
        System.out.println("Test 3 - All ones, target: 2");
        System.out.println("Optimized: " + solution.numSubmatrixSumTarget(matrix3, 2));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void performanceTest() {
        NumberOfSubmatricesThatSumToTarget solution = new NumberOfSubmatricesThatSumToTarget();

        int[][] largeMatrix = new int[50][50];
        for (int i = 0; i < 50; i++) {
            for (int j = 0; j < 50; j++) {
                largeMatrix[i][j] = (int) (Math.random() * 10) - 5;
            }
        }

        System.out.println("=== Performance Test (Matrix size: 50x50) ===");

        long start = System.nanoTime();
        int result = solution.numSubmatrixSumTarget(largeMatrix, 0);
        long end = System.nanoTime();
        System.out.println("Optimized: " + result + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
