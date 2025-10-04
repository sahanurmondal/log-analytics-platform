package searching.medium;

/**
 * LeetCode 240: Search a 2D Matrix II
 * https://leetcode.com/problems/search-a-2d-matrix-ii/
 *
 * Description:
 * Write an efficient algorithm that searches for a target value in an m x n
 * integer matrix.
 * The matrix has properties: integers in each row are sorted in ascending from
 * left to right, and integers in each column are sorted in ascending from top
 * to bottom.
 *
 * Constraints:
 * - m == matrix.length
 * - n == matrix[0].length
 * - 1 <= m, n <= 300
 * - -10^9 <= matrix[i][j] <= 10^9
 * - Each row is sorted in non-decreasing order
 * - Each column is sorted in non-decreasing order
 * - -10^9 <= target <= 10^9
 *
 * ASCII Art:
 * matrix = [[1, 4, 7, 11, 15],
 * [2, 5, 8, 12, 19],
 * [3, 6, 9, 16, 22],
 * [10, 13, 14, 17, 24],
 * [18, 21, 23, 26, 30]]
 * 
 * Start from top-right: 15 → 11 → 7 → 8 → 12 → target found!
 *
 * Follow-up:
 * - Can you solve it in O(m + n) time?
 * - What if you start from different corners?
 */
public class Search2DMatrixII {
    public boolean searchMatrix(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int row = 0, col = n - 1;
        while (row < m && col >= 0) {
            if (matrix[row][col] == target) return true;
            else if (matrix[row][col] > target) col--;
            else row++;
        }
        return false;
    }

    // Follow-up 1: Return position of target
    public int[] searchMatrixPosition(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int row = 0, col = n - 1;
        while (row < m && col >= 0) {
            if (matrix[row][col] == target) return new int[]{row, col};
            else if (matrix[row][col] > target) col--;
            else row++;
        }
        return new int[]{-1, -1};
    }

    // Follow-up 2: Count number of occurrences
    public int countOccurrences(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int row = 0, col = n - 1, count = 0;
        while (row < m && col >= 0) {
            if (matrix[row][col] == target) {
                count++;
                col--;
            } else if (matrix[row][col] > target) col--;
            else row++;
        }
        return count;
    }

    // Follow-up 3: Search for closest value
    public int[] searchClosest(int[][] matrix, int target) {
        int m = matrix.length, n = matrix[0].length;
        int row = 0, col = n - 1;
        int minDiff = Integer.MAX_VALUE, bestRow = -1, bestCol = -1;
        while (row < m && col >= 0) {
            int val = matrix[row][col];
            int diff = Math.abs(val - target);
            if (diff < minDiff) {
                minDiff = diff;
                bestRow = row;
                bestCol = col;
            }
            if (val == target) break;
            else if (val > target) col--;
            else row++;
        }
        return new int[]{bestRow, bestCol};
    }

    public static void main(String[] args) {
        Search2DMatrixII solution = new Search2DMatrixII();
        int[][] mat1 = {{1,4,7,11,15},{2,5,8,12,19},{3,6,9,16,22},{10,13,14,17,24},{18,21,23,26,30}};
        System.out.println("Basic: " + solution.searchMatrix(mat1, 5)); // true
        System.out.println("Not found: " + solution.searchMatrix(mat1, 20)); // false
        int[][] mat2 = {{10}};
        System.out.println("Single element: " + solution.searchMatrix(mat2, 10)); // true
        System.out.println("Position: " + java.util.Arrays.toString(solution.searchMatrixPosition(mat1, 16))); // [2,3]
        int[][] mat3 = {{1,2,2},{2,2,3},{3,4,5}};
        System.out.println("Count occurrences: " + solution.countOccurrences(mat3, 2)); // 4
        System.out.println("Closest: " + java.util.Arrays.toString(solution.searchClosest(mat1, 20))); // [1,4]
    }
}
