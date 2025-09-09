package dp.advanced;

import java.util.Arrays;
import java.util.Stack;

/**
 * LeetCode 1504: Count Submatrices With All Ones
 * https://leetcode.com/problems/count-submatrices-with-all-ones/
 *
 * Description:
 * Given an m x n binary matrix mat, return the number of submatrices that have
 * all ones.
 *
 * Constraints:
 * - 1 <= m, n <= 150
 * - mat[i][j] is either 0 or 1.
 *
 * Follow-up:
 * - Can you solve it in O(m * n) time?
 * - What if we need to find the actual submatrices?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class CountSubmatricesWithAllOnes {

    // Approach 1: Brute Force - O(m^2 * n^2) time, O(1) space
    public int numSubmatBruteForce(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int count = 0;

        for (int r1 = 0; r1 < m; r1++) {
            for (int c1 = 0; c1 < n; c1++) {
                for (int r2 = r1; r2 < m; r2++) {
                    for (int c2 = c1; c2 < n; c2++) {
                        if (isAllOnes(mat, r1, c1, r2, c2)) {
                            count++;
                        }
                    }
                }
            }
        }

        return count;
    }

    private boolean isAllOnes(int[][] mat, int r1, int c1, int r2, int c2) {
        for (int i = r1; i <= r2; i++) {
            for (int j = c1; j <= c2; j++) {
                if (mat[i][j] == 0)
                    return false;
            }
        }
        return true;
    }

    // Approach 2: Optimized Brute Force with Early Termination - O(m^2 * n^2) time,
    // O(1) space
    public int numSubmatOptimized(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int count = 0;

        for (int r1 = 0; r1 < m; r1++) {
            for (int c1 = 0; c1 < n; c1++) {
                if (mat[r1][c1] == 0)
                    continue;

                for (int r2 = r1; r2 < m; r2++) {
                    boolean validRow = true;

                    for (int c2 = c1; c2 < n; c2++) {
                        if (mat[r2][c2] == 0) {
                            validRow = false;
                            break;
                        }

                        // Check if all previous rows also have 1 at column c2
                        boolean validSubmatrix = true;
                        for (int k = r1; k < r2; k++) {
                            if (mat[k][c2] == 0) {
                                validSubmatrix = false;
                                break;
                            }
                        }

                        if (validSubmatrix) {
                            count++;
                        } else {
                            break;
                        }
                    }

                    if (!validRow)
                        break;
                }
            }
        }

        return count;
    }

    // Approach 3: Histogram Approach - O(m * n^2) time, O(n) space
    public int numSubmatHistogram(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int[] heights = new int[n];
        int count = 0;

        for (int i = 0; i < m; i++) {
            // Update heights array
            for (int j = 0; j < n; j++) {
                heights[j] = mat[i][j] == 0 ? 0 : heights[j] + 1;
            }

            // Count rectangles in current histogram
            count += countRectanglesInHistogram(heights);
        }

        return count;
    }

    private int countRectanglesInHistogram(int[] heights) {
        int n = heights.length;
        int count = 0;

        for (int i = 0; i < n; i++) {
            int minHeight = heights[i];
            for (int j = i; j < n; j++) {
                minHeight = Math.min(minHeight, heights[j]);
                count += minHeight;
            }
        }

        return count;
    }

    // Approach 4: Stack-based Histogram Optimization - O(m * n) time, O(n) space
    public int numSubmatStack(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int[] heights = new int[n];
        int count = 0;

        for (int i = 0; i < m; i++) {
            // Update heights
            for (int j = 0; j < n; j++) {
                heights[j] = mat[i][j] == 0 ? 0 : heights[j] + 1;
            }

            count += countWithStack(heights);
        }

        return count;
    }

    private int countWithStack(int[] heights) {
        int n = heights.length;
        Stack<Integer> stack = new Stack<>();
        int count = 0;

        for (int i = 0; i <= n; i++) {
            int h = (i == n) ? 0 : heights[i];

            while (!stack.isEmpty() && heights[stack.peek()] >= h) {
                int height = heights[stack.pop()];
                int width = stack.isEmpty() ? i : i - stack.peek() - 1;
                count += height * width * (width + 1) / 2;
            }

            stack.push(i);
        }

        return count;
    }

    // Approach 5: DP with Contribution - O(m * n^2) time, O(n) space
    public int numSubmatDP(int[][] mat) {
        int m = mat.length, n = mat[0].length;
        int count = 0;

        for (int i = 0; i < m; i++) {
            int[] heights = new int[n];

            for (int j = i; j < m; j++) {
                // Update heights for current bottom row
                for (int k = 0; k < n; k++) {
                    if (mat[j][k] == 0) {
                        heights[k] = 0;
                    } else {
                        heights[k] = (j == i) ? 1 : heights[k] + 1;
                    }
                }

                // Count valid submatrices ending at row j
                count += countValidSubmatrices(heights, j - i + 1);
            }
        }

        return count;
    }

    private int countValidSubmatrices(int[] heights, int requiredHeight) {
        int n = heights.length;
        int count = 0;

        for (int i = 0; i < n; i++) {
            if (heights[i] >= requiredHeight) {
                int j = i;
                while (j < n && heights[j] >= requiredHeight) {
                    j++;
                }
                int length = j - i;
                count += length * (length + 1) / 2;
                i = j - 1;
            }
        }

        return count;
    }

    public static void main(String[] args) {
        CountSubmatricesWithAllOnes solution = new CountSubmatricesWithAllOnes();

        System.out.println("=== Count Submatrices With All Ones Test Cases ===");

        // Test Case 1: Example from problem
        int[][] mat1 = {
                { 1, 0, 1 },
                { 1, 1, 0 },
                { 1, 1, 0 }
        };
        System.out.println("Test 1 - Matrix:");
        printMatrix(mat1);
        System.out.println("Brute Force: " + solution.numSubmatBruteForce(mat1));
        System.out.println("Optimized: " + solution.numSubmatOptimized(mat1));
        System.out.println("Histogram: " + solution.numSubmatHistogram(mat1));
        System.out.println("Stack: " + solution.numSubmatStack(mat1));
        System.out.println("DP: " + solution.numSubmatDP(mat1));
        System.out.println("Expected: 13\n");

        // Test Case 2: All ones
        int[][] mat2 = {
                { 1, 1, 1 },
                { 1, 1, 1 }
        };
        System.out.println("Test 2 - Matrix:");
        printMatrix(mat2);
        System.out.println("Stack: " + solution.numSubmatStack(mat2));
        System.out.println("Expected: 21\n");

        performanceTest();
    }

    private static void printMatrix(int[][] mat) {
        for (int[] row : mat) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        CountSubmatricesWithAllOnes solution = new CountSubmatricesWithAllOnes();

        int m = 50, n = 50;
        int[][] largeMat = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                largeMat[i][j] = Math.random() > 0.3 ? 1 : 0;
            }
        }

        System.out.println("=== Performance Test (Matrix size: " + m + "x" + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.numSubmatHistogram(largeMat);
        long end = System.nanoTime();
        System.out.println("Histogram: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.numSubmatStack(largeMat);
        end = System.nanoTime();
        System.out.println("Stack: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.numSubmatDP(largeMat);
        end = System.nanoTime();
        System.out.println("DP: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
