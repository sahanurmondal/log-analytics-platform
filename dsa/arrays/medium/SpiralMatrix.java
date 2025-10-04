package arrays.medium;

import java.util.*;

/**
 * LeetCode 54: Spiral Matrix
 * https://leetcode.com/problems/spiral-matrix/
 *
 * Description:
 * Given an m x n matrix, return all elements of the matrix in spiral order.
 *
 * Constraints:
 * - m == matrix.length
 * - n == matrix[i].length
 * - 1 <= m, n <= 10
 * - -100 <= matrix[i][j] <= 100
 *
 * Follow-up:
 * - Can you solve it using directions array?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(1) excluding output
 */
public class SpiralMatrix {

    // Main solution - Layer by layer
    public List<Integer> spiralOrder(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0)
            return result;

        int top = 0, bottom = matrix.length - 1;
        int left = 0, right = matrix[0].length - 1;

        while (top <= bottom && left <= right) {
            // Traverse right
            for (int col = left; col <= right; col++) {
                result.add(matrix[top][col]);
            }
            top++;

            // Traverse down
            for (int row = top; row <= bottom; row++) {
                result.add(matrix[row][right]);
            }
            right--;

            // Traverse left (if we still have rows)
            if (top <= bottom) {
                for (int col = right; col >= left; col--) {
                    result.add(matrix[bottom][col]);
                }
                bottom--;
            }

            // Traverse up (if we still have columns)
            if (left <= right) {
                for (int row = bottom; row >= top; row--) {
                    result.add(matrix[row][left]);
                }
                left++;
            }
        }

        return result;
    }

    // Follow-up solution - Using directions
    public List<Integer> spiralOrderDirections(int[][] matrix) {
        List<Integer> result = new ArrayList<>();
        if (matrix.length == 0)
            return result;

        int m = matrix.length, n = matrix[0].length;
        boolean[][] visited = new boolean[m][n];

        // Directions: right, down, left, up
        int[][] dirs = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        int dir = 0;

        int row = 0, col = 0;

        for (int i = 0; i < m * n; i++) {
            result.add(matrix[row][col]);
            visited[row][col] = true;

            int nextRow = row + dirs[dir][0];
            int nextCol = col + dirs[dir][1];

            // Change direction if out of bounds or visited
            if (nextRow < 0 || nextRow >= m || nextCol < 0 || nextCol >= n || visited[nextRow][nextCol]) {
                dir = (dir + 1) % 4;
                nextRow = row + dirs[dir][0];
                nextCol = col + dirs[dir][1];
            }

            row = nextRow;
            col = nextCol;
        }

        return result;
    }

    public static void main(String[] args) {
        SpiralMatrix solution = new SpiralMatrix();

        // Test Case 1: Normal case
        int[][] matrix1 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        System.out.println(solution.spiralOrder(matrix1)); // Expected: [1,2,3,6,9,8,7,4,5]

        // Test Case 2: Edge case - single row
        int[][] matrix2 = { { 1, 2, 3, 4 } };
        System.out.println(solution.spiralOrder(matrix2)); // Expected: [1,2,3,4]

        // Test Case 3: Corner case - single column
        int[][] matrix3 = { { 1 }, { 2 }, { 3 } };
        System.out.println(solution.spiralOrder(matrix3)); // Expected: [1,2,3]

        // Test Case 4: Single element
        int[][] matrix4 = { { 1 } };
        System.out.println(solution.spiralOrder(matrix4)); // Expected: [1]

        // Test Case 5: 2x2 matrix
        int[][] matrix5 = { { 1, 2 }, { 3, 4 } };
        System.out.println(solution.spiralOrder(matrix5)); // Expected: [1,2,4,3]

        // Test Case 6: Rectangle - more rows
        int[][] matrix6 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 }, { 10, 11, 12 } };
        System.out.println(solution.spiralOrder(matrix6)); // Expected: [1,2,3,6,9,12,11,10,7,4,5,8]

        // Test Case 7: Rectangle - more columns
        int[][] matrix7 = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 } };
        System.out.println(solution.spiralOrder(matrix7)); // Expected: [1,2,3,4,8,12,11,10,9,5,6,7]

        // Test Case 8: Negative numbers
        int[][] matrix8 = { { -1, -2 }, { -3, -4 } };
        System.out.println(solution.spiralOrder(matrix8)); // Expected: [-1,-2,-4,-3]

        // Test Case 9: Mixed values
        int[][] matrix9 = { { 1, 11 }, { 2, 12 }, { 3, 13 } };
        System.out.println(solution.spiralOrder(matrix9)); // Expected: [1,11,12,13,3,2]

        // Test Case 10: Large rectangle
        int[][] matrix10 = { { 1, 2, 3, 4, 5 } };
        System.out.println(solution.spiralOrder(matrix10)); // Expected: [1,2,3,4,5]
    }
}
