package miscellaneous.recent;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Recent Problem: Longest Increasing Path in Matrix with Obstacles
 * 
 * Description:
 * Given an m x n integers matrix, return the length of the longest increasing
 * path.
 * You can move in four directions (up, down, left, right) but cannot move
 * through obstacles.
 * 
 * Companies: Google, Facebook, Amazon
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class LongestIncreasingPath {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    private int[][] memo;

    public int longestIncreasingPath(int[][] matrix) {
        if (matrix == null || matrix.length == 0)
            return 0;

        int m = matrix.length, n = matrix[0].length;
        memo = new int[m][n];
        int maxPath = 0;

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] != -1) { // -1 represents obstacle
                    maxPath = Math.max(maxPath, dfs(matrix, i, j));
                }
            }
        }

        return maxPath;
    }

    private int dfs(int[][] matrix, int i, int j) {
        if (memo[i][j] != 0)
            return memo[i][j];

        int maxLength = 1;

        for (int[] dir : directions) {
            int ni = i + dir[0];
            int nj = j + dir[1];

            if (ni >= 0 && ni < matrix.length && nj >= 0 && nj < matrix[0].length &&
                    matrix[ni][nj] != -1 && matrix[ni][nj] > matrix[i][j]) {
                maxLength = Math.max(maxLength, 1 + dfs(matrix, ni, nj));
            }
        }

        memo[i][j] = maxLength;
        return maxLength;
    }

    // Topological sort approach
    public int longestIncreasingPathTopological(int[][] matrix) {
        if (matrix == null || matrix.length == 0)
            return 0;

        int m = matrix.length, n = matrix[0].length;
        int[][] indegree = new int[m][n];

        // Calculate indegrees
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] == -1)
                    continue;

                for (int[] dir : directions) {
                    int ni = i + dir[0];
                    int nj = j + dir[1];

                    if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                            matrix[ni][nj] != -1 && matrix[ni][nj] > matrix[i][j]) {
                        indegree[ni][nj]++;
                    }
                }
            }
        }

        Queue<int[]> queue = new LinkedList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] != -1 && indegree[i][j] == 0) {
                    queue.offer(new int[] { i, j });
                }
            }
        }

        int maxLength = 0;
        while (!queue.isEmpty()) {
            int size = queue.size();
            maxLength++;

            for (int k = 0; k < size; k++) {
                int[] curr = queue.poll();
                int i = curr[0], j = curr[1];

                for (int[] dir : directions) {
                    int ni = i + dir[0];
                    int nj = j + dir[1];

                    if (ni >= 0 && ni < m && nj >= 0 && nj < n &&
                            matrix[ni][nj] != -1 && matrix[ni][nj] > matrix[i][j]) {
                        indegree[ni][nj]--;
                        if (indegree[ni][nj] == 0) {
                            queue.offer(new int[] { ni, nj });
                        }
                    }
                }
            }
        }

        return maxLength;
    }

    public static void main(String[] args) {
        LongestIncreasingPath solution = new LongestIncreasingPath();

        int[][] matrix = {
                { 9, 9, 4 },
                { 6, 6, 8 },
                { 2, 1, 1 }
        };

        System.out.println(solution.longestIncreasingPath(matrix)); // 4
        System.out.println(solution.longestIncreasingPathTopological(matrix)); // 4
    }
}
