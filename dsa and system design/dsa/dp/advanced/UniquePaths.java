package dp.advanced;

/**
 * LeetCode 62: Unique Paths
 * https://leetcode.com/problems/unique-paths/
 *
 * Description:
 * There is a robot on an m x n grid. The robot is initially located at the
 * top-left corner (i.e., grid[0][0]).
 * The robot tries to move to the bottom-right corner (i.e., grid[m - 1][n -
 * 1]).
 * The robot can only move either down or right at any point in time.
 * Given the two integers m and n, return the number of possible unique paths
 * that the robot can take to reach the bottom-right corner.
 *
 * Constraints:
 * - 1 <= m, n <= 100
 * - The test cases are generated so that the answer will be in the range of a
 * 32-bit signed integer.
 *
 * Follow-up:
 * - What if some obstacles are added to the grids? How would you modify the
 * solution?
 * - Can you solve it using only O(min(m,n)) extra space?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg, Uber
 * Difficulty: Medium
 */
public class UniquePaths {

    // Approach 1: Recursive (Brute Force) - O(2^(m+n)) time, O(m+n) space
    public int uniquePathsRecursive(int m, int n) {
        return uniquePathsHelper(0, 0, m, n);
    }

    private int uniquePathsHelper(int row, int col, int m, int n) {
        // Base case: reached destination
        if (row == m - 1 && col == n - 1)
            return 1;

        // Out of bounds
        if (row >= m || col >= n)
            return 0;

        // Move right or down
        return uniquePathsHelper(row + 1, col, m, n) + uniquePathsHelper(row, col + 1, m, n);
    }

    // Approach 2: Memoization (Top-down DP) - O(m*n) time, O(m*n) space
    public int uniquePathsMemoization(int m, int n) {
        int[][] memo = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                memo[i][j] = -1;
            }
        }
        return uniquePathsMemoHelper(0, 0, m, n, memo);
    }

    private int uniquePathsMemoHelper(int row, int col, int m, int n, int[][] memo) {
        if (row == m - 1 && col == n - 1)
            return 1;
        if (row >= m || col >= n)
            return 0;

        if (memo[row][col] != -1)
            return memo[row][col];

        memo[row][col] = uniquePathsMemoHelper(row + 1, col, m, n, memo) +
                uniquePathsMemoHelper(row, col + 1, m, n, memo);
        return memo[row][col];
    }

    // Approach 3: Tabulation (Bottom-up DP) - O(m*n) time, O(m*n) space
    public int uniquePathsTabulation(int m, int n) {
        int[][] dp = new int[m][n];

        // Initialize first row and column
        for (int i = 0; i < m; i++)
            dp[i][0] = 1;
        for (int j = 0; j < n; j++)
            dp[0][j] = 1;

        // Fill the DP table
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }

        return dp[m - 1][n - 1];
    }

    // Approach 4: Space Optimized - O(m*n) time, O(min(m,n)) space
    public int uniquePathsOptimized(int m, int n) {
        // Use smaller dimension for space optimization
        if (m > n)
            return uniquePathsOptimized(n, m);

        int[] prev = new int[m];
        int[] curr = new int[m];

        // Initialize
        for (int i = 0; i < m; i++)
            prev[i] = 1;

        for (int j = 1; j < n; j++) {
            curr[0] = 1;
            for (int i = 1; i < m; i++) {
                curr[i] = curr[i - 1] + prev[i];
            }
            prev = curr.clone();
        }

        return prev[m - 1];
    }

    // Approach 5: Mathematical Solution (Combinatorics) - O(min(m,n)) time, O(1)
    // space
    public int uniquePathsMath(int m, int n) {
        // Total moves needed: (m-1) down + (n-1) right = (m+n-2)
        // Choose (m-1) positions for down moves: C(m+n-2, m-1)
        int totalMoves = m + n - 2;
        int downMoves = m - 1;

        long result = 1;
        for (int i = 0; i < Math.min(downMoves, n - 1); i++) {
            result = result * (totalMoves - i) / (i + 1);
        }

        return (int) result;
    }

    public static void main(String[] args) {
        UniquePaths solution = new UniquePaths();

        System.out.println("=== Unique Paths Test Cases ===");

        // Test Case 1: Small grid
        int m1 = 3, n1 = 7;
        System.out.println("Test 1 - Grid " + m1 + "x" + n1 + ":");
        System.out.println("Recursive: " + solution.uniquePathsRecursive(m1, n1));
        System.out.println("Memoization: " + solution.uniquePathsMemoization(m1, n1));
        System.out.println("Tabulation: " + solution.uniquePathsTabulation(m1, n1));
        System.out.println("Optimized: " + solution.uniquePathsOptimized(m1, n1));
        System.out.println("Mathematical: " + solution.uniquePathsMath(m1, n1));
        System.out.println("Expected: 28\n");

        // Test Case 2: Square grid
        int m2 = 3, n2 = 3;
        System.out.println("Test 2 - Grid " + m2 + "x" + n2 + ":");
        System.out.println("Recursive: " + solution.uniquePathsRecursive(m2, n2));
        System.out.println("Memoization: " + solution.uniquePathsMemoization(m2, n2));
        System.out.println("Tabulation: " + solution.uniquePathsTabulation(m2, n2));
        System.out.println("Optimized: " + solution.uniquePathsOptimized(m2, n2));
        System.out.println("Mathematical: " + solution.uniquePathsMath(m2, n2));
        System.out.println("Expected: 6\n");

        // Test Case 3: Edge case - single row
        int m3 = 1, n3 = 10;
        System.out.println("Test 3 - Grid " + m3 + "x" + n3 + ":");
        System.out.println("All approaches: " + solution.uniquePathsTabulation(m3, n3));
        System.out.println("Expected: 1\n");

        // Test Case 4: Edge case - single column
        int m4 = 10, n4 = 1;
        System.out.println("Test 4 - Grid " + m4 + "x" + n4 + ":");
        System.out.println("All approaches: " + solution.uniquePathsTabulation(m4, n4));
        System.out.println("Expected: 1\n");

        // Performance Test
        performanceTest();
    }

    private static void performanceTest() {
        UniquePaths solution = new UniquePaths();
        int m = 15, n = 15;

        System.out.println("=== Performance Test (Grid " + m + "x" + n + ") ===");

        // Test Memoization
        long start = System.nanoTime();
        int result1 = solution.uniquePathsMemoization(m, n);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        // Test Tabulation
        start = System.nanoTime();
        int result2 = solution.uniquePathsTabulation(m, n);
        end = System.nanoTime();
        System.out.println("Tabulation: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        // Test Optimized
        start = System.nanoTime();
        int result3 = solution.uniquePathsOptimized(m, n);
        end = System.nanoTime();
        System.out.println("Space Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        // Test Mathematical
        start = System.nanoTime();
        int result4 = solution.uniquePathsMath(m, n);
        end = System.nanoTime();
        System.out.println("Mathematical: " + result4 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
