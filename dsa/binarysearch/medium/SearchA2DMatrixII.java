package binarysearch.medium;

/**
 * LeetCode 240: Search a 2D Matrix II
 * https://leetcode.com/problems/search-a-2d-matrix-ii/
 *
 * Description:
 * Write an efficient algorithm that searches for a value target in an m x n
 * integer matrix.
 * This matrix has the following properties:
 * - Integers in each row are sorted in ascending from left to right.
 * - Integers in each column are sorted in ascending from top to bottom.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg, Adobe,
 * Uber, LinkedIn
 * Difficulty: Medium
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - m == matrix.length
 * - n == matrix[i].length
 * - 1 <= n, m <= 300
 * - -10^9 <= matrix[i][j] <= 10^9
 * - Each row is sorted in non-decreasing order.
 * - Each column is sorted in non-decreasing order.
 * - -10^9 <= target <= 10^9
 *
 * Follow-ups:
 * - What if matrix elements can be negative?
 * - How to find all occurrences of target?
 * - Can you solve this with divide and conquer?
 */
public class SearchA2DMatrixII {

    // Start from top-right corner - O(m + n) time, O(1) space
    public boolean searchMatrix(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int row = 0;
        int col = matrix[0].length - 1;

        while (row < matrix.length && col >= 0) {
            if (matrix[row][col] == target) {
                return true;
            } else if (matrix[row][col] > target) {
                col--; // Move left
            } else {
                row++; // Move down
            }
        }

        return false;
    }

    // Start from bottom-left corner - O(m + n) time, O(1) space
    public boolean searchMatrixAlt(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int row = matrix.length - 1;
        int col = 0;

        while (row >= 0 && col < matrix[0].length) {
            if (matrix[row][col] == target) {
                return true;
            } else if (matrix[row][col] > target) {
                row--; // Move up
            } else {
                col++; // Move right
            }
        }

        return false;
    }

    // Binary search on each row - O(m * log n) time, O(1) space
    public boolean searchMatrixBinarySearch(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        for (int[] row : matrix) {
            if (binarySearch(row, target)) {
                return true;
            }
        }

        return false;
    }

    private boolean binarySearch(int[] row, int target) {
        int left = 0, right = row.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (row[mid] == target) {
                return true;
            } else if (row[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    // Divide and conquer approach - O(n^1.585) time, O(log n) space
    public boolean searchMatrixDivideConquer(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        return divideConquerHelper(matrix, target, 0, 0, matrix.length - 1, matrix[0].length - 1);
    }

    private boolean divideConquerHelper(int[][] matrix, int target, int top, int left, int bottom, int right) {
        if (top > bottom || left > right) {
            return false;
        }

        if (top == bottom && left == right) {
            return matrix[top][left] == target;
        }

        int midRow = top + (bottom - top) / 2;
        int midCol = left + (right - left) / 2;

        if (matrix[midRow][midCol] == target) {
            return true;
        } else if (matrix[midRow][midCol] > target) {
            // Search top-left, top-right, and bottom-left
            return divideConquerHelper(matrix, target, top, left, midRow - 1, right) ||
                    divideConquerHelper(matrix, target, midRow, left, bottom, midCol - 1);
        } else {
            // Search top-right, bottom-left, and bottom-right
            return divideConquerHelper(matrix, target, top, midCol + 1, midRow, right) ||
                    divideConquerHelper(matrix, target, midRow + 1, left, bottom, right);
        }
    }

    // Find position of target (return coordinates)
    public int[] findTargetPosition(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return new int[] { -1, -1 };
        }

        int row = 0;
        int col = matrix[0].length - 1;

        while (row < matrix.length && col >= 0) {
            if (matrix[row][col] == target) {
                return new int[] { row, col };
            } else if (matrix[row][col] > target) {
                col--;
            } else {
                row++;
            }
        }

        return new int[] { -1, -1 };
    }

    // Find all occurrences of target
    public java.util.List<int[]> findAllOccurrences(int[][] matrix, int target) {
        java.util.List<int[]> result = new java.util.ArrayList<>();

        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return result;
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == target) {
                    result.add(new int[] { i, j });
                }
            }
        }

        return result;
    }

    // Count occurrences of target
    public int countOccurrences(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return 0;
        }

        int count = 0;
        for (int[] row : matrix) {
            for (int val : row) {
                if (val == target) {
                    count++;
                }
            }
        }

        return count;
    }

    // Find minimum element in matrix
    public int findMin(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Matrix is empty");
        }

        return matrix[0][0]; // Top-left is always minimum in sorted matrix
    }

    // Find maximum element in matrix
    public int findMax(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Matrix is empty");
        }

        int m = matrix.length;
        int n = matrix[0].length;
        return matrix[m - 1][n - 1]; // Bottom-right is always maximum in sorted matrix
    }

    // Find kth smallest element
    public int kthSmallest(int[][] matrix, int k) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            throw new IllegalArgumentException("Matrix is empty");
        }

        int m = matrix.length, n = matrix[0].length;
        int left = matrix[0][0], right = matrix[m - 1][n - 1];

        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = countLessOrEqual(matrix, mid);

            if (count < k) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private int countLessOrEqual(int[][] matrix, int target) {
        int count = 0;
        int row = matrix.length - 1;
        int col = 0;

        while (row >= 0 && col < matrix[0].length) {
            if (matrix[row][col] <= target) {
                count += row + 1;
                col++;
            } else {
                row--;
            }
        }

        return count;
    }

    // Search in range [minVal, maxVal]
    public boolean searchInRange(int[][] matrix, int target, int minVal, int maxVal) {
        if (target < minVal || target > maxVal) {
            return false;
        }

        return searchMatrix(matrix, target);
    }

    // Find elements in range [minVal, maxVal]
    public java.util.List<Integer> findElementsInRange(int[][] matrix, int minVal, int maxVal) {
        java.util.List<Integer> result = new java.util.ArrayList<>();

        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return result;
        }

        for (int[] row : matrix) {
            for (int val : row) {
                if (val >= minVal && val <= maxVal) {
                    result.add(val);
                }
            }
        }

        return result;
    }

    // Optimized search with early termination
    public boolean searchMatrixOptimized(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int m = matrix.length, n = matrix[0].length;

        // Quick bounds check
        if (target < matrix[0][0] || target > matrix[m - 1][n - 1]) {
            return false;
        }

        // Use the efficient corner approach
        return searchMatrix(matrix, target);
    }

    // Search with path tracking
    public java.util.List<int[]> searchWithPath(int[][] matrix, int target) {
        java.util.List<int[]> path = new java.util.ArrayList<>();

        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return path;
        }

        int row = 0;
        int col = matrix[0].length - 1;

        while (row < matrix.length && col >= 0) {
            path.add(new int[] { row, col });

            if (matrix[row][col] == target) {
                return path;
            } else if (matrix[row][col] > target) {
                col--;
            } else {
                row++;
            }
        }

        return path; // Path taken even if not found
    }

    // Validate matrix properties
    public boolean isValidMatrix(int[][] matrix) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        int m = matrix.length, n = matrix[0].length;

        // Check rows are sorted
        for (int i = 0; i < m; i++) {
            for (int j = 1; j < n; j++) {
                if (matrix[i][j] < matrix[i][j - 1]) {
                    return false;
                }
            }
        }

        // Check columns are sorted
        for (int j = 0; j < n; j++) {
            for (int i = 1; i < m; i++) {
                if (matrix[i][j] < matrix[i - 1][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    // Linear search for comparison
    public boolean searchMatrixLinear(int[][] matrix, int target) {
        if (matrix == null || matrix.length == 0 || matrix[0].length == 0) {
            return false;
        }

        for (int[] row : matrix) {
            for (int val : row) {
                if (val == target) {
                    return true;
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        SearchA2DMatrixII solution = new SearchA2DMatrixII();

        // Test Case 1: Standard matrix
        int[][] matrix1 = {
                { 1, 4, 7, 11, 15 },
                { 2, 5, 8, 12, 19 },
                { 3, 6, 9, 16, 22 },
                { 10, 13, 14, 17, 24 },
                { 18, 21, 23, 26, 30 }
        };

        System.out.println(solution.searchMatrix(matrix1, 5)); // Expected: true
        System.out.println(solution.searchMatrix(matrix1, 14)); // Expected: true
        System.out.println(solution.searchMatrix(matrix1, 20)); // Expected: false

        // Test Case 2: Single element
        int[][] matrix2 = { { 1 } };
        System.out.println(solution.searchMatrix(matrix2, 1)); // Expected: true
        System.out.println(solution.searchMatrix(matrix2, 2)); // Expected: false

        // Test Case 3: Single row
        int[][] matrix3 = { { 1, 3, 5, 7, 9 } };
        System.out.println(solution.searchMatrix(matrix3, 5)); // Expected: true
        System.out.println(solution.searchMatrix(matrix3, 6)); // Expected: false

        // Test Case 4: Single column
        int[][] matrix4 = { { 1 }, { 3 }, { 5 }, { 7 }, { 9 } };
        System.out.println(solution.searchMatrix(matrix4, 5)); // Expected: true
        System.out.println(solution.searchMatrix(matrix4, 6)); // Expected: false

        // Test alternative implementations
        System.out.println("Alternative: " + solution.searchMatrixAlt(matrix1, 5)); // Expected: true
        System.out.println("Binary Search: " + solution.searchMatrixBinarySearch(matrix1, 5)); // Expected: true
        System.out.println("Divide Conquer: " + solution.searchMatrixDivideConquer(matrix1, 5)); // Expected: true
        System.out.println("Linear: " + solution.searchMatrixLinear(matrix1, 5)); // Expected: true

        // Test find position
        int[] position = solution.findTargetPosition(matrix1, 14);
        System.out.println("Position of 14: [" + position[0] + ", " + position[1] + "]"); // Expected: [3, 2]

        // Test find all occurrences
        int[][] matrixWithDuplicates = {
                { 1, 2, 2, 3 },
                { 2, 3, 4, 5 },
                { 4, 5, 6, 7 }
        };

        java.util.List<int[]> allOccurrences = solution.findAllOccurrences(matrixWithDuplicates, 2);
        System.out.println("All occurrences of 2:");
        for (int[] pos : allOccurrences) {
            System.out.println("[" + pos[0] + ", " + pos[1] + "]");
        }

        // Test count occurrences
        System.out.println("Count of 2: " + solution.countOccurrences(matrixWithDuplicates, 2)); // Expected: 3

        // Test min/max
        System.out.println("Min element: " + solution.findMin(matrix1)); // Expected: 1
        System.out.println("Max element: " + solution.findMax(matrix1)); // Expected: 30

        // Test kth smallest
        System.out.println("3rd smallest: " + solution.kthSmallest(matrix1, 3)); // Expected: 3
        System.out.println("10th smallest: " + solution.kthSmallest(matrix1, 10)); // Expected: 11

        // Test search in range
        System.out.println("Search 14 in range [10,20]: " + solution.searchInRange(matrix1, 14, 10, 20)); // Expected:
                                                                                                          // true
        System.out.println("Search 25 in range [10,20]: " + solution.searchInRange(matrix1, 25, 10, 20)); // Expected:
                                                                                                          // false

        // Test elements in range
        java.util.List<Integer> elementsInRange = solution.findElementsInRange(matrix1, 10, 15);
        System.out.println("Elements in range [10,15]: " + elementsInRange);

        // Test optimized version
        System.out.println("Optimized: " + solution.searchMatrixOptimized(matrix1, 14)); // Expected: true

        // Test search with path
        java.util.List<int[]> searchPath = solution.searchWithPath(matrix1, 14);
        System.out.println("Search path for 14:");
        for (int[] step : searchPath) {
            System.out.println("(" + step[0] + ", " + step[1] + ") -> " + matrix1[step[0]][step[1]]);
        }

        // Test matrix validation
        System.out.println("Is valid matrix: " + solution.isValidMatrix(matrix1)); // Expected: true

        int[][] invalidMatrix = { { 1, 3, 2 }, { 4, 5, 6 } };
        System.out.println("Is invalid matrix valid: " + solution.isValidMatrix(invalidMatrix)); // Expected: false

        // Performance comparison
        int[][] largeMatrix = new int[100][100];
        for (int i = 0; i < 100; i++) {
            for (int j = 0; j < 100; j++) {
                largeMatrix[i][j] = i * 100 + j;
            }
        }

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.searchMatrix(largeMatrix, 5050);
        }
        long cornerTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.searchMatrixBinarySearch(largeMatrix, 5050);
        }
        long binaryTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.searchMatrixLinear(largeMatrix, 5050);
        }
        long linearTime = System.currentTimeMillis() - startTime;

        System.out.println("Corner approach time (1000 runs): " + cornerTime + "ms");
        System.out.println("Binary search time (1000 runs): " + binaryTime + "ms");
        System.out.println("Linear search time (1000 runs): " + linearTime + "ms");

        // Edge cases
        int[][] empty = {};
        System.out.println("Empty matrix: " + solution.searchMatrix(empty, 1)); // Expected: false

        int[][] nullMatrix = null;
        System.out.println("Null matrix: " + solution.searchMatrix(nullMatrix, 1)); // Expected: false

        // Test with negative numbers
        int[][] negativeMatrix = {
                { -5, -3, -1, 1, 3 },
                { -4, -2, 0, 2, 4 },
                { -3, -1, 1, 3, 5 }
        };

        System.out.println("Search -2 in negative matrix: " + solution.searchMatrix(negativeMatrix, -2)); // Expected:
                                                                                                          // true
        System.out.println("Search 0 in negative matrix: " + solution.searchMatrix(negativeMatrix, 0)); // Expected:
                                                                                                        // true
        System.out.println("Search -6 in negative matrix: " + solution.searchMatrix(negativeMatrix, -6)); // Expected:
                                                                                                          // false

        // Test boundary values
        System.out.println("Search min value: " + solution.searchMatrix(matrix1, 1)); // Expected: true
        System.out.println("Search max value: " + solution.searchMatrix(matrix1, 30)); // Expected: true
        System.out.println("Search below min: " + solution.searchMatrix(matrix1, 0)); // Expected: false
        System.out.println("Search above max: " + solution.searchMatrix(matrix1, 31)); // Expected: false

        // Stress test with different targets
        int[] testTargets = { 1, 5, 11, 14, 20, 30 };
        for (int target : testTargets) {
            boolean found = solution.searchMatrix(matrix1, target);
            int[] pos = solution.findTargetPosition(matrix1, target);
            System.out.println("Target " + target + ": found=" + found +
                    ", position=[" + pos[0] + ", " + pos[1] + "]");
        }
    }
}
