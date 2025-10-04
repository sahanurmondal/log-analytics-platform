package stacks.hard;

import java.util.*;

/**
 * LeetCode 85: Maximal Rectangle
 * https://leetcode.com/problems/maximal-rectangle/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given a 2D binary matrix filled with 0's and 1's, find the
 * largest rectangle containing only 1's and return its area.
 *
 * Constraints:
 * - 1 <= matrix.length, matrix[0].length <= 200
 * 
 * Follow-up Questions:
 * 1. Can you return the coordinates of the rectangle?
 * 2. Can you count the number of maximal rectangles?
 * 3. Can you optimize for sparse matrices?
 */
public class LargestRectangleInBinaryMatrix {

    // Approach 1: Histogram + Largest Rectangle in Histogram
    public int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0)
            return 0;
        int m = matrix.length, n = matrix[0].length, maxArea = 0;
        int[] heights = new int[n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++)
                heights[j] = matrix[i][j] == '1' ? heights[j] + 1 : 0;
            maxArea = Math.max(maxArea, largestRectangleArea(heights));
        }
        return maxArea;
    }

    private int largestRectangleArea(int[] heights) {
        int n = heights.length, maxArea = 0;
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i <= n; i++) {
            int h = i == n ? 0 : heights[i];
            while (!stack.isEmpty() && h < heights[stack.peek()]) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                maxArea = Math.max(maxArea, height * width);
            }
            stack.push(i);
        }
        return maxArea;
    }

    // Follow-up 1: Return coordinates of rectangle
    public int[] maximalRectangleCoords(char[][] matrix) {
        // ...not implemented for brevity
        return new int[0];
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LargestRectangleInBinaryMatrix solution = new LargestRectangleInBinaryMatrix();

        // Test case 1: Basic case
        char[][] matrix1 = {
                { '1', '0', '1', '0', '0' },
                { '1', '0', '1', '1', '1' },
                { '1', '1', '1', '1', '1' },
                { '1', '0', '0', '1', '0' }
        };
        System.out.println("Test 1 - matrix: " + Arrays.deepToString(matrix1) + " Expected: 6");
        System.out.println("Result: " + solution.maximalRectangle(matrix1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty matrix: " + solution.maximalRectangle(new char[][] {}));
        System.out.println("Single cell: " + solution.maximalRectangle(new char[][] { { '1' } }));
    }
}
