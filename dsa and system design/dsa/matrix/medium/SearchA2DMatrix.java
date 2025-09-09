package matrix.medium;

/**
 * LeetCode 74: Search a 2D Matrix
 * https://leetcode.com/problems/search-a-2d-matrix/
 *
 * Description:
 * Write an efficient algorithm that searches for a value in an m x n matrix.
 *
 * Constraints:
 * - 1 <= m, n <= 100
 * - -10^4 <= matrix[i][j], target <= 10^4
 */
public class SearchA2DMatrix {
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

    public static void main(String[] args) {
        SearchA2DMatrix solution = new SearchA2DMatrix();
        int[][] matrix = { { 1, 4, 7, 11 }, { 2, 5, 8, 12 }, { 3, 6, 9, 16 } };
        System.out.println(solution.searchMatrix(matrix, 5)); // true
        System.out.println(solution.searchMatrix(matrix, 13)); // false
        // Edge Case: Single element
        System.out.println(solution.searchMatrix(new int[][] { { 1 } }, 1)); // true
        // Edge Case: Target at corners
        System.out.println(solution.searchMatrix(matrix, 1)); // true
        System.out.println(solution.searchMatrix(matrix, 16)); // true
    }
}
