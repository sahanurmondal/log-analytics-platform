package stacks.hard;

import java.util.*;

/**
 * LeetCode 1504: Count Submatrices With All Ones
 * https://leetcode.com/problems/count-submatrices-with-all-ones/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a binary matrix, return the number of submatrices with all
 * ones.
 *
 * Constraints:
 * - 1 <= matrix.length, matrix[0].length <= 150
 * - matrix[i][j] is 0 or 1
 * 
 * Follow-up Questions:
 * 1. Can you find the largest such submatrix?
 * 2. Can you count submatrices with at least k ones?
 * 3. Can you optimize for sparse matrices?
 */
public class CountSubmatricesWithAllOnes {

    // Approach 1: Histogram + Stack (O(m*n) time)
    public int numSubmat(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length, res = 0;
        int[] height = new int[n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                height[j] = matrix[i][j] == 0 ? 0 : height[j] + 1;
            res += countOneRow(height);
        }
        return res;
    }

    private int countOneRow(int[] height) {
        int n = height.length, res = 0;
        Stack<Integer> stack = new Stack<>();
        int[] sum = new int[n];
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && height[stack.peek()] >= height[i])
                stack.pop();
            if (!stack.isEmpty()) {
                int prev = stack.peek();
                sum[i] = sum[prev] + height[i] * (i - prev);
            } else {
                sum[i] = height[i] * (i + 1);
            }
            stack.push(i);
            res += sum[i];
        }
        return res;
    }

    // Follow-up 1: Largest submatrix with all ones
    public int largestSubmatrix(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length, maxArea = 0;
        int[] height = new int[n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                height[j] = matrix[i][j] == 0 ? 0 : height[j] + 1;
            int[] sorted = Arrays.copyOf(height, n);
            Arrays.sort(sorted);
            for (int k = 0; k < n; k++)
                maxArea = Math.max(maxArea, sorted[k] * (n - k));
        }
        return maxArea;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CountSubmatricesWithAllOnes solution = new CountSubmatricesWithAllOnes();

        // Test case 1: Basic case
        int[][] matrix1 = { { 1, 0, 1 }, { 1, 1, 0 }, { 1, 1, 0 } };
        System.out.println("Test 1 - matrix: " + Arrays.deepToString(matrix1) + " Expected: 13");
        System.out.println("Result: " + solution.numSubmat(matrix1));

        // Test case 2: Largest submatrix
        System.out.println("\nTest 2 - Largest submatrix:");
        System.out.println(solution.largestSubmatrix(matrix1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty matrix: " + solution.numSubmat(new int[][] {}));
        System.out.println("Single cell: " + solution.numSubmat(new int[][] { { 1 } }));
    }
}
