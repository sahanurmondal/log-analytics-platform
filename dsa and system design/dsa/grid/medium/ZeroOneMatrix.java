package grid.medium;

import java.util.*;

/**
 * LeetCode 542: 01 Matrix
 * https://leetcode.com/problems/01-matrix/
 * 
 * Companies: Amazon, Google, Meta, Microsoft, Apple
 * Frequency: High (Asked in 190+ interviews)
 *
 * Description:
 * Given an m x n binary matrix mat, return the distance of the nearest 0 for
 * each cell.
 * The distance between two adjacent cells is 1.
 *
 * Constraints:
 * - m == mat.length
 * - n == mat[i].length
 * - 1 <= m, n <= 10^4
 * - 1 <= m * n <= 10^4
 * - mat[i][j] is either 0 or 1
 * - There is at least one 0 in mat
 * 
 * Follow-up Questions:
 * 1. Can you solve it without extra space (in-place)?
 * 2. What if we want to find distance to nearest 1 instead?
 * 3. Can you solve it with multiple target values?
 */
public class ZeroOneMatrix {

    // Approach 1: Multi-source BFS - O(m*n) time, O(m*n) space
    public int[][] updateMatrix(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;
        int[][] result = new int[m][n];
        Queue<int[]> queue = new LinkedList<>();

        // Initialize result matrix and add all 0s to queue
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 0) {
                    result[i][j] = 0;
                    queue.offer(new int[] { i, j });
                } else {
                    result[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0], col = cell[1];

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n) {
                    if (result[newRow][newCol] > result[row][col] + 1) {
                        result[newRow][newCol] = result[row][col] + 1;
                        queue.offer(new int[] { newRow, newCol });
                    }
                }
            }
        }

        return result;
    }

    // Approach 2: Dynamic Programming (Two Pass) - O(m*n) time, O(1) space
    public int[][] updateMatrixDP(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;
        int[][] result = new int[m][n];

        // Initialize
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 0) {
                    result[i][j] = 0;
                } else {
                    result[i][j] = Integer.MAX_VALUE - 1; // Avoid overflow
                }
            }
        }

        // First pass: top-left to bottom-right
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (result[i][j] != 0) {
                    if (i > 0) {
                        result[i][j] = Math.min(result[i][j], result[i - 1][j] + 1);
                    }
                    if (j > 0) {
                        result[i][j] = Math.min(result[i][j], result[i][j - 1] + 1);
                    }
                }
            }
        }

        // Second pass: bottom-right to top-left
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (result[i][j] != 0) {
                    if (i < m - 1) {
                        result[i][j] = Math.min(result[i][j], result[i + 1][j] + 1);
                    }
                    if (j < n - 1) {
                        result[i][j] = Math.min(result[i][j], result[i][j + 1] + 1);
                    }
                }
            }
        }

        return result;
    }

    // Follow-up 1: In-place solution
    public int[][] updateMatrixInPlace(int[][] mat) {
        int m = mat.length;
        int n = mat[0].length;

        // First pass: mark 1s as MAX_VALUE
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 1) {
                    mat[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        // Forward pass
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] != 0) {
                    if (i > 0 && mat[i - 1][j] != Integer.MAX_VALUE) {
                        mat[i][j] = Math.min(mat[i][j], mat[i - 1][j] + 1);
                    }
                    if (j > 0 && mat[i][j - 1] != Integer.MAX_VALUE) {
                        mat[i][j] = Math.min(mat[i][j], mat[i][j - 1] + 1);
                    }
                }
            }
        }

        // Backward pass
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                if (mat[i][j] != 0) {
                    if (i < m - 1 && mat[i + 1][j] != Integer.MAX_VALUE) {
                        mat[i][j] = Math.min(mat[i][j], mat[i + 1][j] + 1);
                    }
                    if (j < n - 1 && mat[i][j + 1] != Integer.MAX_VALUE) {
                        mat[i][j] = Math.min(mat[i][j], mat[i][j + 1] + 1);
                    }
                }
            }
        }

        return mat;
    }

    // Follow-up 2: Distance to nearest 1
    public int[][] updateMatrixToNearestOne(int[][] mat) {
        // Flip the matrix (0 <-> 1) and use same algorithm
        int m = mat.length;
        int n = mat[0].length;
        int[][] flipped = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                flipped[i][j] = 1 - mat[i][j];
            }
        }

        return updateMatrix(flipped);
    }

    // Follow-up 3: Distance to multiple target values
    public int[][] updateMatrixMultipleTargets(int[][] mat, Set<Integer> targets) {
        int m = mat.length;
        int n = mat[0].length;
        int[][] result = new int[m][n];
        Queue<int[]> queue = new LinkedList<>();

        // Initialize result matrix and add all target cells to queue
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (targets.contains(mat[i][j])) {
                    result[i][j] = 0;
                    queue.offer(new int[] { i, j });
                } else {
                    result[i][j] = Integer.MAX_VALUE;
                }
            }
        }

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0], col = cell[1];

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n) {
                    if (result[newRow][newCol] > result[row][col] + 1) {
                        result[newRow][newCol] = result[row][col] + 1;
                        queue.offer(new int[] { newRow, newCol });
                    }
                }
            }
        }

        return result;
    }

    // Helper: Print matrix
    private void printMatrix(int[][] matrix, String title) {
        System.out.println(title + ":");
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
        System.out.println();
    }

    // Helper: Clone matrix
    private int[][] cloneMatrix(int[][] matrix) {
        int m = matrix.length;
        int n = matrix[0].length;
        int[][] clone = new int[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                clone[i][j] = matrix[i][j];
            }
        }

        return clone;
    }

    // Helper: Verify result correctness
    private boolean verifyResult(int[][] mat, int[][] result) {
        int m = mat.length;
        int n = mat[0].length;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (mat[i][j] == 0 && result[i][j] != 0) {
                    return false;
                }

                // Check if distance is correct by BFS
                int actualDistance = findNearestZeroDistance(mat, i, j);
                if (result[i][j] != actualDistance) {
                    return false;
                }
            }
        }

        return true;
    }

    // Helper: Find actual distance to nearest 0 using BFS
    private int findNearestZeroDistance(int[][] mat, int startRow, int startCol) {
        if (mat[startRow][startCol] == 0) {
            return 0;
        }

        int m = mat.length;
        int n = mat[0].length;
        boolean[][] visited = new boolean[m][n];
        Queue<int[]> queue = new LinkedList<>();

        queue.offer(new int[] { startRow, startCol, 0 });
        visited[startRow][startCol] = true;

        int[][] directions = { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } };

        while (!queue.isEmpty()) {
            int[] cell = queue.poll();
            int row = cell[0], col = cell[1], dist = cell[2];

            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n && !visited[newRow][newCol]) {
                    if (mat[newRow][newCol] == 0) {
                        return dist + 1;
                    }

                    visited[newRow][newCol] = true;
                    queue.offer(new int[] { newRow, newCol, dist + 1 });
                }
            }
        }

        return -1; // Should never reach here if there's at least one 0
    }

    public static void main(String[] args) {
        ZeroOneMatrix solution = new ZeroOneMatrix();

        // Test Case 1: Standard case
        int[][] mat1 = {
                { 0, 0, 0 },
                { 0, 1, 0 },
                { 0, 0, 0 }
        };

        solution.printMatrix(mat1, "Original Matrix 1");
        int[][] result1 = solution.updateMatrix(solution.cloneMatrix(mat1));
        solution.printMatrix(result1, "BFS Result 1");

        int[][] result1DP = solution.updateMatrixDP(solution.cloneMatrix(mat1));
        solution.printMatrix(result1DP, "DP Result 1");

        System.out.println("BFS verification: " + solution.verifyResult(mat1, result1));
        System.out.println("DP verification: " + solution.verifyResult(mat1, result1DP));

        // Test Case 2: Complex case
        int[][] mat2 = {
                { 0, 0, 0 },
                { 0, 1, 0 },
                { 1, 1, 1 }
        };

        solution.printMatrix(mat2, "Original Matrix 2");
        int[][] result2 = solution.updateMatrix(solution.cloneMatrix(mat2));
        solution.printMatrix(result2, "BFS Result 2");

        // Test Case 3: Single cell
        int[][] mat3 = { { 0 } };
        int[][] result3 = solution.updateMatrix(mat3);
        solution.printMatrix(result3, "Single Cell Result");

        // Follow-up tests
        System.out.println("Follow-up 1 - In-place solution:");
        int[][] inPlaceResult = solution.updateMatrixInPlace(solution.cloneMatrix(mat2));
        solution.printMatrix(inPlaceResult, "In-place Result");

        System.out.println("Follow-up 2 - Distance to nearest 1:");
        int[][] nearestOneResult = solution.updateMatrixToNearestOne(mat1);
        solution.printMatrix(nearestOneResult, "Distance to Nearest 1");

        System.out.println("Follow-up 3 - Multiple targets (0 and 2):");
        int[][] mat4 = {
                { 0, 1, 2 },
                { 1, 1, 1 },
                { 2, 1, 0 }
        };
        Set<Integer> targets = new HashSet<>(Arrays.asList(0, 2));
        int[][] multiTargetResult = solution.updateMatrixMultipleTargets(mat4, targets);
        solution.printMatrix(mat4, "Original Matrix with multiple values");
        solution.printMatrix(multiTargetResult, "Distance to Nearest 0 or 2");
    }
}
