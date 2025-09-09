package searching.medium;

/**
 * LeetCode 74: Search a 2D Matrix
 * https://leetcode.com/problems/search-a-2d-matrix/
 *
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description:
 * Given a matrix with each row sorted and the first integer of each row greater
 * than the last integer of the previous row,
 * search for a target value.
 *
 * Constraints:
 * - m == matrix.length
 * - n == matrix[0].length
 * - 1 <= m, n <= 100
 * - -10^4 <= matrix[i][j], target <= 10^4
 *
 * Follow-ups:
 * 1. Can you return the position of the target?
 * 2. Can you search in a jagged matrix?
 * 3. Can you search for the closest value?
 */
public class Search2DMatrix {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int val = matrix[mid / n][mid % n];
            if (val == target)
                return true;
            else if (val < target)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return false;
    }

    // Follow-up 1: Return position of target
    public int[] searchMatrixPosition(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int val = matrix[mid / n][mid % n];
            if (val == target)
                return new int[] { mid / n, mid % n };
            else if (val < target)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return new int[] { -1, -1 };
    }

    // Follow-up 2: Search in jagged matrix
    public boolean searchJaggedMatrix(int[][] matrix, int target) {
        for (int[] row : matrix) {
            int left = 0, right = row.length - 1;
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (row[mid] == target)
                    return true;
                else if (row[mid] < target)
                    left = mid + 1;
                else
                    right = mid - 1;
            }
        }
        return false;
    }

    // Follow-up 3: Search for closest value
    public int[] searchClosest(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int left = 0, right = m * n - 1;
        int closest = Integer.MAX_VALUE, pos = -1;
        while (left <= right) {
            int mid = left + (right - left) / 2;
            int val = matrix[mid / n][mid % n];
            int diff = Math.abs(val - target);
            if (diff < closest) {
                closest = diff;
                pos = mid;
            }
            if (val == target)
                break;
            else if (val < target)
                left = mid + 1;
            else
                right = mid - 1;
        }
        return pos == -1 ? new int[] { -1, -1 } : new int[] { pos / n, pos % n };
    }

    public static void main(String[] args) {
        Search2DMatrix solution = new Search2DMatrix();
        // Basic case
        int[][] mat1 = { { 1, 3, 5, 7 }, { 10, 11, 16, 20 }, { 23, 30, 34, 60 } };
        System.out.println("Basic: " + solution.searchMatrix(mat1, 3)); // true
        // Not found
        System.out.println("Not found: " + solution.searchMatrix(mat1, 13)); // false
        // Edge: Single element
        int[][] mat2 = { { 10 } };
        System.out.println("Single element: " + solution.searchMatrix(mat2, 10)); // true
        // Follow-up 1: Position
        System.out.println("Position: " + java.util.Arrays.toString(solution.searchMatrixPosition(mat1, 16))); // [1,2]
        // Follow-up 2: Jagged matrix
        int[][] mat3 = { { 1, 2, 3 }, { 4, 5 }, { 6, 7, 8, 9 } };
        System.out.println("Jagged: " + solution.searchJaggedMatrix(mat3, 5)); // true
        // Follow-up 3: Closest value
        System.out.println("Closest: " + java.util.Arrays.toString(solution.searchClosest(mat1, 13))); // [1,2]
    }
}
