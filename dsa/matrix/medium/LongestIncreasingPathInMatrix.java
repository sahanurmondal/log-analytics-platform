package matrix.medium;

// No imports needed

/**
 * LeetCode 329: Longest Increasing Path in a Matrix
 * https://leetcode.com/problems/longest-increasing-path-in-a-matrix/
 *
 * Description:
 * Given an m x n integers matrix, return the length of the longest increasing
 * path in matrix.
 *
 * Constraints:
 * - 1 <= m, n <= 200
 * - 0 <= matrix[i][j] <= 2^31 - 1
 */
public class LongestIncreasingPathInMatrix {
    // Follow-up: Can also solve using topological sort (Kahn's algorithm) for DAG
    public int longestIncreasingPath(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        int[][] memo = new int[m][n];
        int max = 0;
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                max = Math.max(max, dfs(matrix, i, j, memo));
            }
        }
        return max;
    }

    private int dfs(int[][] matrix, int i, int j, int[][] memo) {
        if (memo[i][j] > 0)
            return memo[i][j];
        int m = matrix.length, n = matrix[0].length, max = 1;
        int[][] dirs = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
        for (int[] d : dirs) {
            int x = i + d[0], y = j + d[1];
            if (x >= 0 && x < m && y >= 0 && y < n && matrix[x][y] > matrix[i][j]) {
                max = Math.max(max, 1 + dfs(matrix, x, y, memo));
            }
        }
        memo[i][j] = max;
        return max;
    }

    public static void main(String[] args) {
        LongestIncreasingPathInMatrix solution = new LongestIncreasingPathInMatrix();
        System.out.println(solution.longestIncreasingPath(new int[][] { { 9, 9, 4 }, { 6, 6, 8 }, { 2, 1, 1 } })); // 4
        System.out.println(solution.longestIncreasingPath(new int[][] { { 3, 4, 5 }, { 3, 2, 6 }, { 2, 2, 1 } })); // 4
        // Edge Case: All same
        System.out.println(solution.longestIncreasingPath(new int[][] { { 1, 1 }, { 1, 1 } })); // 1
        // Edge Case: Single cell
        System.out.println(solution.longestIncreasingPath(new int[][] { { 5 } })); // 1
    }
}
