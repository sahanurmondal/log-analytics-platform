package dp.advanced;

import java.util.*;

/**
 * LeetCode 85: Maximal Rectangle
 * https://leetcode.com/problems/maximal-rectangle/
 *
 * Description:
 * Given a rows x cols binary matrix filled with 0's and 1's, find the largest
 * rectangle containing only 1's and return its area.
 *
 * Constraints:
 * - rows == matrix.length
 * - cols == matrix[i].length
 * - 1 <= rows, cols <= 200
 * - matrix[i][j] is '0' or '1'.
 *
 * Follow-up:
 * - Can you solve it in O(rows * cols) time?
 * - What if we need to find the coordinates of the rectangle?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class MaximalRectangle {

    // Approach 1: Histogram DP - O(rows * cols) time, O(cols) space
    public int maximalRectangle(char[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0)
            return 0;

        int rows = matrix.length, cols = matrix[0].length;
        int[] heights = new int[cols];
        int maxArea = 0;

        for (int i = 0; i < rows; i++) {
            // Update heights array
            for (int j = 0; j < cols; j++) {
                heights[j] = (matrix[i][j] == '1') ? heights[j] + 1 : 0;
            }

            // Find max rectangle in current histogram
            maxArea = Math.max(maxArea, largestRectangleInHistogram(heights));
        }

        return maxArea;
    }

    private int largestRectangleInHistogram(int[] heights) {
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int index = 0;

        while (index < heights.length) {
            if (stack.isEmpty() || heights[index] >= heights[stack.peek()]) {
                stack.push(index++);
            } else {
                int top = stack.pop();
                int area = heights[top] * (stack.isEmpty() ? index : index - stack.peek() - 1);
                maxArea = Math.max(maxArea, area);
            }
        }

        while (!stack.isEmpty()) {
            int top = stack.pop();
            int area = heights[top] * (stack.isEmpty() ? index : index - stack.peek() - 1);
            maxArea = Math.max(maxArea, area);
        }

        return maxArea;
    }

    // Approach 2: DP with Left/Right Arrays - O(rows * cols) time, O(cols) space
    public int maximalRectangleDP(char[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0)
            return 0;

        int rows = matrix.length, cols = matrix[0].length;
        int[] heights = new int[cols];
        int[] left = new int[cols];
        int[] right = new int[cols];
        Arrays.fill(right, cols);

        int maxArea = 0;

        for (int i = 0; i < rows; i++) {
            int currentLeft = 0, currentRight = cols;

            // Update heights
            for (int j = 0; j < cols; j++) {
                heights[j] = (matrix[i][j] == '1') ? heights[j] + 1 : 0;
            }

            // Update left boundaries
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == '1') {
                    left[j] = Math.max(left[j], currentLeft);
                } else {
                    left[j] = 0;
                    currentLeft = j + 1;
                }
            }

            // Update right boundaries
            for (int j = cols - 1; j >= 0; j--) {
                if (matrix[i][j] == '1') {
                    right[j] = Math.min(right[j], currentRight);
                } else {
                    right[j] = cols;
                    currentRight = j;
                }
            }

            // Calculate area
            for (int j = 0; j < cols; j++) {
                maxArea = Math.max(maxArea, (right[j] - left[j]) * heights[j]);
            }
        }

        return maxArea;
    }

    // Approach 3: Brute Force with Optimization - O(rows^2 * cols^2) time, O(1)
    // space
    public int maximalRectangleBruteForce(char[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0)
            return 0;

        int rows = matrix.length, cols = matrix[0].length;
        int maxArea = 0;

        for (int r1 = 0; r1 < rows; r1++) {
            int[] heights = new int[cols];

            for (int r2 = r1; r2 < rows; r2++) {
                // Update heights for current bottom row
                for (int c = 0; c < cols; c++) {
                    if (matrix[r2][c] == '1') {
                        heights[c]++;
                    } else {
                        heights[c] = 0;
                    }
                }

                // Find max width for each column
                int area = getMaxRectangleArea(heights, r2 - r1 + 1);
                maxArea = Math.max(maxArea, area);
            }
        }

        return maxArea;
    }

    private int getMaxRectangleArea(int[] heights, int requiredHeight) {
        int maxArea = 0;
        int cols = heights.length;

        for (int i = 0; i < cols; i++) {
            if (heights[i] >= requiredHeight) {
                int width = 0;
                int j = i;

                while (j < cols && heights[j] >= requiredHeight) {
                    width++;
                    j++;
                }

                maxArea = Math.max(maxArea, width * requiredHeight);
                i = j - 1; // Skip processed columns
            }
        }

        return maxArea;
    }

    // Approach 4: Get Rectangle Coordinates - O(rows * cols) time, O(cols) space
    public int[] getMaximalRectangleCoordinates(char[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0)
            return new int[] { 0, 0, 0, 0, 0 };

        int rows = matrix.length, cols = matrix[0].length;
        int[] heights = new int[cols];
        int maxArea = 0;
        int[] result = new int[5]; // {area, top, left, bottom, right}

        for (int i = 0; i < rows; i++) {
            // Update heights
            for (int j = 0; j < cols; j++) {
                heights[j] = (matrix[i][j] == '1') ? heights[j] + 1 : 0;
            }

            // Find max rectangle and its coordinates
            int[] rectInfo = largestRectangleWithCoordinates(heights, i);
            if (rectInfo[0] > maxArea) {
                maxArea = rectInfo[0];
                result[0] = rectInfo[0]; // area
                result[1] = rectInfo[1]; // top
                result[2] = rectInfo[2]; // left
                result[3] = i; // bottom
                result[4] = rectInfo[3]; // right
            }
        }

        return result;
    }

    private int[] largestRectangleWithCoordinates(int[] heights, int currentRow) {
        Stack<Integer> stack = new Stack<>();
        int maxArea = 0;
        int bestLeft = 0, bestRight = 0, bestTop = currentRow;
        int index = 0;

        while (index < heights.length) {
            if (stack.isEmpty() || heights[index] >= heights[stack.peek()]) {
                stack.push(index++);
            } else {
                int top = stack.pop();
                int height = heights[top];
                int width = stack.isEmpty() ? index : index - stack.peek() - 1;
                int area = height * width;

                if (area > maxArea) {
                    maxArea = area;
                    bestRight = index - 1;
                    bestLeft = stack.isEmpty() ? 0 : stack.peek() + 1;
                    bestTop = currentRow - height + 1;
                }
            }
        }

        while (!stack.isEmpty()) {
            int top = stack.pop();
            int height = heights[top];
            int width = stack.isEmpty() ? index : index - stack.peek() - 1;
            int area = height * width;

            if (area > maxArea) {
                maxArea = area;
                bestRight = index - 1;
                bestLeft = stack.isEmpty() ? 0 : stack.peek() + 1;
                bestTop = currentRow - height + 1;
            }
        }

        return new int[] { maxArea, bestTop, bestLeft, bestRight };
    }

    // Approach 5: Dynamic Programming 2D - O(rows * cols^2) time, O(rows * cols)
    // space
    public int maximalRectangle2D(char[][] matrix) {
        if (matrix.length == 0 || matrix[0].length == 0)
            return 0;

        int rows = matrix.length, cols = matrix[0].length;
        int[][] width = new int[rows][cols];
        int maxArea = 0;

        // Calculate width of consecutive 1's ending at each position
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == '1') {
                    width[i][j] = (j == 0) ? 1 : width[i][j - 1] + 1;
                }
            }
        }

        // For each cell, calculate max rectangle ending at that cell
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (matrix[i][j] == '1') {
                    int minWidth = width[i][j];

                    for (int k = i; k >= 0; k--) {
                        if (matrix[k][j] == '0')
                            break;

                        minWidth = Math.min(minWidth, width[k][j]);
                        int area = minWidth * (i - k + 1);
                        maxArea = Math.max(maxArea, area);
                    }
                }
            }
        }

        return maxArea;
    }

    public static void main(String[] args) {
        MaximalRectangle solution = new MaximalRectangle();

        System.out.println("=== Maximal Rectangle Test Cases ===");

        // Test Case 1: Example from problem
        char[][] matrix1 = {
                { '1', '0', '1', '0', '0' },
                { '1', '0', '1', '1', '1' },
                { '1', '1', '1', '1', '1' },
                { '1', '0', '0', '1', '0' }
        };
        System.out.println("Test 1 - Matrix:");
        printMatrix(matrix1);
        System.out.println("Histogram: " + solution.maximalRectangle(matrix1));
        System.out.println("DP Arrays: " + solution.maximalRectangleDP(matrix1));
        System.out.println("2D DP: " + solution.maximalRectangle2D(matrix1));

        int[] coords1 = solution.getMaximalRectangleCoordinates(matrix1);
        System.out.println("Rectangle coordinates - Area: " + coords1[0] +
                ", Top: " + coords1[1] + ", Left: " + coords1[2] +
                ", Bottom: " + coords1[3] + ", Right: " + coords1[4]);
        System.out.println("Expected: 6\n");

        // Test Case 2: All zeros
        char[][] matrix2 = {
                { '0', '0' },
                { '0', '0' }
        };
        System.out.println("Test 2 - Matrix:");
        printMatrix(matrix2);
        System.out.println("Histogram: " + solution.maximalRectangle(matrix2));
        System.out.println("Expected: 0\n");

        // Test Case 3: All ones
        char[][] matrix3 = {
                { '1', '1' },
                { '1', '1' }
        };
        System.out.println("Test 3 - Matrix:");
        printMatrix(matrix3);
        System.out.println("Histogram: " + solution.maximalRectangle(matrix3));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void printMatrix(char[][] matrix) {
        for (char[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        MaximalRectangle solution = new MaximalRectangle();

        int rows = 100, cols = 100;
        char[][] largeMatrix = new char[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                largeMatrix[i][j] = (Math.random() > 0.3) ? '1' : '0';
            }
        }

        System.out.println("=== Performance Test (Matrix size: " + rows + "x" + cols + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maximalRectangle(largeMatrix);
        long end = System.nanoTime();
        System.out.println("Histogram: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maximalRectangleDP(largeMatrix);
        end = System.nanoTime();
        System.out.println("DP Arrays: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maximalRectangle2D(largeMatrix);
        end = System.nanoTime();
        System.out.println("2D DP: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
