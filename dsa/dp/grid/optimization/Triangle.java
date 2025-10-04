package dp.grid.optimization;

import java.util.*;

/**
 * LeetCode 120: Triangle
 * https://leetcode.com/problems/triangle/
 * 
 * Problem:
 * Given a triangle array, return the minimum path sum from top to bottom.
 * For each step, you may move to an adjacent number of the row below. More
 * formally, if you are on index i on the current row,
 * you may move to either index i or index i + 1 on the next row.
 * 
 * Example 1:
 * Input: triangle = [[2],[3,4],[6,5,7],[4,1,8,3]]
 * Output: 11
 * Explanation: The triangle looks like:
 * 2
 * 3 4
 * 6 5 7
 * 4 1 8 3
 * The minimum path sum from top to bottom is 2 + 3 + 5 + 1 = 11 (underlined
 * above).
 * 
 * Example 2:
 * Input: triangle = [[-10]]
 * Output: -10
 * 
 * Constraints:
 * 1 <= triangle.length <= 200
 * triangle[0].length == 1
 * triangle[i].length == triangle[i - 1].length + 1
 * -10^4 <= triangle[i][j] <= 10^4
 * 
 * Follow up: Could you do this using only O(n) extra space, where n is the
 * total number of rows in the triangle?
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Frequency: Very High
 */
public class Triangle {

    /**
     * Approach 1: Bottom-up DP with O(n^2) space
     * Time Complexity: O(n^2) where n is number of rows
     * Space Complexity: O(n^2)
     */
    public int minimumTotal(List<List<Integer>> triangle) {
        if (triangle == null || triangle.isEmpty()) {
            return 0;
        }

        int n = triangle.size();
        int[][] dp = new int[n][n];

        // Initialize last row
        for (int j = 0; j < triangle.get(n - 1).size(); j++) {
            dp[n - 1][j] = triangle.get(n - 1).get(j);
        }

        // Build dp table from bottom to top
        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                dp[i][j] = triangle.get(i).get(j) + Math.min(dp[i + 1][j], dp[i + 1][j + 1]);
            }
        }

        return dp[0][0];
    }

    /**
     * Approach 2: Bottom-up DP with O(n) space
     * Time Complexity: O(n^2)
     * Space Complexity: O(n)
     */
    public int minimumTotalOptimized(List<List<Integer>> triangle) {
        if (triangle == null || triangle.isEmpty()) {
            return 0;
        }

        int n = triangle.size();
        int[] dp = new int[n];

        // Initialize with last row
        for (int j = 0; j < n; j++) {
            dp[j] = triangle.get(n - 1).get(j);
        }

        // Build dp array from bottom to top
        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                dp[j] = triangle.get(i).get(j) + Math.min(dp[j], dp[j + 1]);
            }
        }

        return dp[0];
    }

    /**
     * Approach 3: In-place modification (O(1) extra space)
     * Time Complexity: O(n^2)
     * Space Complexity: O(1) if we can modify input
     */
    public int minimumTotalInPlace(List<List<Integer>> triangle) {
        if (triangle == null || triangle.isEmpty()) {
            return 0;
        }

        int n = triangle.size();

        // Start from second last row
        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                int current = triangle.get(i).get(j);
                int below = triangle.get(i + 1).get(j);
                int belowRight = triangle.get(i + 1).get(j + 1);
                triangle.get(i).set(j, current + Math.min(below, belowRight));
            }
        }

        return triangle.get(0).get(0);
    }

    /**
     * Approach 4: Top-down DP with memoization
     * Time Complexity: O(n^2)
     * Space Complexity: O(n^2)
     */
    public int minimumTotalTopDown(List<List<Integer>> triangle) {
        if (triangle == null || triangle.isEmpty()) {
            return 0;
        }

        int n = triangle.size();
        Integer[][] memo = new Integer[n][n];
        return dfs(triangle, 0, 0, memo);
    }

    private int dfs(List<List<Integer>> triangle, int row, int col, Integer[][] memo) {
        if (row == triangle.size()) {
            return 0;
        }

        if (memo[row][col] != null) {
            return memo[row][col];
        }

        int current = triangle.get(row).get(col);
        int down = dfs(triangle, row + 1, col, memo);
        int downRight = dfs(triangle, row + 1, col + 1, memo);

        memo[row][col] = current + Math.min(down, downRight);
        return memo[row][col];
    }

    /**
     * Follow-up: Return the actual path
     */
    public List<Integer> minimumTotalPath(List<List<Integer>> triangle) {
        if (triangle == null || triangle.isEmpty()) {
            return new ArrayList<>();
        }

        int n = triangle.size();
        int[][] dp = new int[n][n];
        int[][] path = new int[n][n]; // 0 for down, 1 for down-right

        // Initialize last row
        for (int j = 0; j < triangle.get(n - 1).size(); j++) {
            dp[n - 1][j] = triangle.get(n - 1).get(j);
        }

        // Build dp table and track path
        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                if (dp[i + 1][j] <= dp[i + 1][j + 1]) {
                    dp[i][j] = triangle.get(i).get(j) + dp[i + 1][j];
                    path[i][j] = 0; // go down
                } else {
                    dp[i][j] = triangle.get(i).get(j) + dp[i + 1][j + 1];
                    path[i][j] = 1; // go down-right
                }
            }
        }

        // Reconstruct path
        List<Integer> result = new ArrayList<>();
        int row = 0, col = 0;
        while (row < n) {
            result.add(triangle.get(row).get(col));
            if (row < n - 1) {
                col += path[row][col];
            }
            row++;
        }

        return result;
    }

    /**
     * Follow-up: Maximum path sum
     */
    public int maximumTotal(List<List<Integer>> triangle) {
        if (triangle == null || triangle.isEmpty()) {
            return 0;
        }

        int n = triangle.size();
        int[] dp = new int[n];

        // Initialize with last row
        for (int j = 0; j < n; j++) {
            dp[j] = triangle.get(n - 1).get(j);
        }

        // Build dp array from bottom to top
        for (int i = n - 2; i >= 0; i--) {
            for (int j = 0; j < triangle.get(i).size(); j++) {
                dp[j] = triangle.get(i).get(j) + Math.max(dp[j], dp[j + 1]);
            }
        }

        return dp[0];
    }

    public static void main(String[] args) {
        Triangle solution = new Triangle();

        // Test case 1
        List<List<Integer>> triangle1 = Arrays.asList(
                Arrays.asList(2),
                Arrays.asList(3, 4),
                Arrays.asList(6, 5, 7),
                Arrays.asList(4, 1, 8, 3));
        System.out.println("Test 1 - Minimum total: " + solution.minimumTotal(triangle1)); // 11
        System.out.println("Test 1 - Optimized: " + solution.minimumTotalOptimized(triangle1)); // 11
        System.out.println("Test 1 - Path: " + solution.minimumTotalPath(triangle1)); // [2, 3, 5, 1]

        // Test case 2
        List<List<Integer>> triangle2 = Arrays.asList(
                Arrays.asList(-10));
        System.out.println("Test 2 - Minimum total: " + solution.minimumTotal(triangle2)); // -10

        // Test case 3: All positive numbers
        List<List<Integer>> triangle3 = Arrays.asList(
                Arrays.asList(1),
                Arrays.asList(2, 3),
                Arrays.asList(4, 5, 6));
        System.out.println("Test 3 - Minimum total: " + solution.minimumTotal(triangle3)); // 7
        System.out.println("Test 3 - Maximum total: " + solution.maximumTotal(triangle3)); // 10

        // Test case 4: Mix of positive and negative
        List<List<Integer>> triangle4 = Arrays.asList(
                Arrays.asList(-1),
                Arrays.asList(2, 3),
                Arrays.asList(1, -1, -3));
        System.out.println("Test 4 - Minimum total: " + solution.minimumTotal(triangle4)); // -1

        System.out.println("\nAll test cases completed successfully!");
    }
}
