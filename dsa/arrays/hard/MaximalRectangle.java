package arrays.hard;

import java.util.*;

/**
 * LeetCode 85: Maximal Rectangle
 * https://leetcode.com/problems/maximal-rectangle/
 *
 * Description:
 * Given a rows x cols binary matrix filled with 0's and 1's, find the largest
 * rectangle
 * containing only 1's and return its area.
 *
 * Constraints:
 * - rows == matrix.length
 * - cols == matrix[i].length
 * - 1 <= rows, cols <= 200
 * - matrix[i][j] is '0' or '1'
 *
 * Follow-up:
 * - Can you solve it using the largest rectangle in histogram approach?
 * 
 * Time Complexity: O(rows * cols)
 * Space Complexity: O(cols)
 * 
 * Algorithm:
 * 1. Convert each row to histogram heights
 * 2. Apply largest rectangle in histogram for each row
 * 3. Track maximum area across all rows
 */
public class MaximalRectangle {
        public int maximalRectangle(char[][] matrix) {
                if (matrix == null || matrix.length == 0 || matrix[0].length == 0)
                        return 0;

                int rows = matrix.length;
                int cols = matrix[0].length;
                int[] heights = new int[cols];
                int maxArea = 0;

                for (int i = 0; i < rows; i++) {
                        // Update heights for current row
                        for (int j = 0; j < cols; j++) {
                                if (matrix[i][j] == '1') {
                                        heights[j]++;
                                } else {
                                        heights[j] = 0;
                                }
                        }

                        // Calculate max rectangle for current histogram
                        maxArea = Math.max(maxArea, largestRectangleArea(heights));
                }

                return maxArea;
        }

        private int largestRectangleArea(int[] heights) {
                Stack<Integer> stack = new Stack<>();
                int maxArea = 0;

                for (int i = 0; i <= heights.length; i++) {
                        int currentHeight = (i == heights.length) ? 0 : heights[i];

                        while (!stack.isEmpty() && currentHeight < heights[stack.peek()]) {
                                int height = heights[stack.pop()];
                                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                                maxArea = Math.max(maxArea, height * width);
                        }

                        stack.push(i);
                }

                return maxArea;
        }

        public static void main(String[] args) {
                MaximalRectangle solution = new MaximalRectangle();

                // Test Case 1: Normal case
                char[][] matrix1 = {
                                { '1', '0', '1', '0', '0' },
                                { '1', '0', '1', '1', '1' },
                                { '1', '1', '1', '1', '1' },
                                { '1', '0', '0', '1', '0' }
                };
                System.out.println(solution.maximalRectangle(matrix1)); // Expected: 6

                // Test Case 2: Edge case - all zeros
                char[][] matrix2 = { { '0' } };
                System.out.println(solution.maximalRectangle(matrix2)); // Expected: 0

                // Test Case 3: Corner case - all ones
                char[][] matrix3 = { { '1' } };
                System.out.println(solution.maximalRectangle(matrix3)); // Expected: 1

                // Test Case 4: Large input - full rectangle
                char[][] matrix4 = {
                                { '1', '1', '1' },
                                { '1', '1', '1' },
                                { '1', '1', '1' }
                };
                System.out.println(solution.maximalRectangle(matrix4)); // Expected: 9

                // Test Case 5: Minimum input - single row
                char[][] matrix5 = { { '1', '1', '0', '1' } };
                System.out.println(solution.maximalRectangle(matrix5)); // Expected: 2

                // Test Case 6: Special case - single column
                char[][] matrix6 = { { '1' }, { '1' }, { '0' }, { '1' } };
                System.out.println(solution.maximalRectangle(matrix6)); // Expected: 2

                // Test Case 7: Boundary case - no rectangle
                char[][] matrix7 = {
                                { '1', '0', '1' },
                                { '0', '1', '0' },
                                { '1', '0', '1' }
                };
                System.out.println(solution.maximalRectangle(matrix7)); // Expected: 1

                // Test Case 8: L-shaped pattern
                char[][] matrix8 = {
                                { '1', '1', '0' },
                                { '1', '0', '0' },
                                { '1', '0', '0' }
                };
                System.out.println(solution.maximalRectangle(matrix8)); // Expected: 3

                // Test Case 9: Staircase pattern
                char[][] matrix9 = {
                                { '0', '1', '1' },
                                { '1', '1', '1' },
                                { '1', '1', '0' }
                };
                System.out.println(solution.maximalRectangle(matrix9)); // Expected: 4

                // Test Case 10: Empty matrix
                char[][] matrix10 = {};
                System.out.println(solution.maximalRectangle(matrix10)); // Expected: 0
        }
}
