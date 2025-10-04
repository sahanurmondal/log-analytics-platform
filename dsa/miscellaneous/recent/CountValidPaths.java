package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Count Valid Paths in Grid with Constraints
 * 
 * Description:
 * Given an m x n grid, count the number of paths from top-left to bottom-right
 * where you can only move right or down, and the path sum equals a target
 * value.
 * 
 * Companies: Google, Microsoft, Apple
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class CountValidPaths {

    public int countPaths(int[][] grid, int target) {
        Map<String, Integer> memo = new HashMap<>();
        return dfs(grid, 0, 0, 0, target, memo);
    }

    private int dfs(int[][] grid, int i, int j, int currentSum, int target, Map<String, Integer> memo) {
        if (i >= grid.length || j >= grid[0].length)
            return 0;

        currentSum += grid[i][j];

        if (i == grid.length - 1 && j == grid[0].length - 1) {
            return currentSum == target ? 1 : 0;
        }

        String key = i + "," + j + "," + currentSum;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int paths = dfs(grid, i + 1, j, currentSum, target, memo) +
                dfs(grid, i, j + 1, currentSum, target, memo);

        memo.put(key, paths);
        return paths;
    }

    // DP approach
    @SuppressWarnings("unchecked")
    public int countPathsDP(int[][] grid, int target) {
        int m = grid.length, n = grid[0].length;

        // dp[i][j] = map of sum -> count of ways to reach (i,j) with that sum
        Map<Integer, Integer>[][] dp = new Map[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dp[i][j] = new HashMap<>();
            }
        }

        dp[0][0].put(grid[0][0], 1);

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 && j == 0)
                    continue;

                // From top
                if (i > 0) {
                    for (Map.Entry<Integer, Integer> entry : dp[i - 1][j].entrySet()) {
                        int sum = entry.getKey() + grid[i][j];
                        int count = entry.getValue();
                        dp[i][j].put(sum, dp[i][j].getOrDefault(sum, 0) + count);
                    }
                }

                // From left
                if (j > 0) {
                    for (Map.Entry<Integer, Integer> entry : dp[i][j - 1].entrySet()) {
                        int sum = entry.getKey() + grid[i][j];
                        int count = entry.getValue();
                        dp[i][j].put(sum, dp[i][j].getOrDefault(sum, 0) + count);
                    }
                }
            }
        }

        return dp[m - 1][n - 1].getOrDefault(target, 0);
    }

    public static void main(String[] args) {
        CountValidPaths solution = new CountValidPaths();

        int[][] grid = {
                { 1, 2, 3 },
                { 4, 5, 6 },
                { 7, 8, 9 }
        };
        int target = 21;

        System.out.println(solution.countPaths(grid, target)); // 2
        System.out.println(solution.countPathsDP(grid, target)); // 2
    }
}
