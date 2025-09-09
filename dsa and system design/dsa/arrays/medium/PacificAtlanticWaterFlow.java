package arrays.medium;

import java.util.*;

/**
 * LeetCode 417: Pacific Atlantic Water Flow
 * https://leetcode.com/problems/pacific-atlantic-water-flow/
 *
 * Description:
 * There is an m x n rectangular island that borders both the Pacific Ocean and
 * Atlantic Ocean.
 * Given an m x n integer matrix heights representing the height of each unit
 * cell in a region,
 * return a list of grid coordinates where water can flow to both the Pacific
 * and Atlantic oceans.
 *
 * Constraints:
 * - m == heights.length
 * - n == heights[r].length
 * - 1 <= m, n <= 200
 * - 0 <= heights[r][c] <= 10^5
 *
 * Follow-up:
 * - Can you solve it using DFS?
 * 
 * Time Complexity: O(m * n)
 * Space Complexity: O(m * n)
 * 
 * Algorithm:
 * 1. Use DFS from ocean borders instead of each cell
 * 2. Mark cells reachable from Pacific and Atlantic separately
 * 3. Find intersection of both reachable sets
 */
public class PacificAtlanticWaterFlow {
    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        if (heights == null || heights.length == 0)
            return new ArrayList<>();

        int m = heights.length, n = heights[0].length;
        boolean[][] pacific = new boolean[m][n];
        boolean[][] atlantic = new boolean[m][n];

        // DFS from Pacific borders (top and left)
        for (int i = 0; i < m; i++) {
            dfs(heights, pacific, i, 0, heights[i][0]);
            dfs(heights, atlantic, i, n - 1, heights[i][n - 1]);
        }

        for (int j = 0; j < n; j++) {
            dfs(heights, pacific, 0, j, heights[0][j]);
            dfs(heights, atlantic, m - 1, j, heights[m - 1][j]);
        }

        // Find intersection
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (pacific[i][j] && atlantic[i][j]) {
                    result.add(Arrays.asList(i, j));
                }
            }
        }

        return result;
    }

    private void dfs(int[][] heights, boolean[][] visited, int x, int y, int prevHeight) {
        if (x < 0 || x >= heights.length || y < 0 || y >= heights[0].length ||
                visited[x][y] || heights[x][y] < prevHeight) {
            return;
        }

        visited[x][y] = true;
        for (int[] dir : directions) {
            dfs(heights, visited, x + dir[0], y + dir[1], heights[x][y]);
        }
    }

    public static void main(String[] args) {
        PacificAtlanticWaterFlow solution = new PacificAtlanticWaterFlow();

        // Test Case 1: Normal case
        int[][] heights1 = { { 1, 2, 2, 3, 5 }, { 3, 2, 3, 4, 4 }, { 2, 4, 5, 3, 1 }, { 6, 7, 1, 4, 5 },
                { 5, 1, 1, 2, 4 } };
        System.out.println(solution.pacificAtlantic(heights1)); // Expected: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]

        // Test Case 2: Edge case - single cell
        int[][] heights2 = { { 2 } };
        System.out.println(solution.pacificAtlantic(heights2)); // Expected: [[0,0]]

        // Test Case 3: Corner case - all same height
        int[][] heights3 = { { 1, 1 }, { 1, 1 } };
        System.out.println(solution.pacificAtlantic(heights3)); // Expected: [[0,0],[0,1],[1,0],[1,1]]

        // Test Case 4: Large input - increasing heights
        int[][] heights4 = { { 1, 2, 3 }, { 4, 5, 6 }, { 7, 8, 9 } };
        System.out.println(solution.pacificAtlantic(heights4)); // Expected: [[0,2],[2,0],[2,1],[2,2]]

        // Test Case 5: Minimum input - 1x2
        int[][] heights5 = { { 1, 2 } };
        System.out.println(solution.pacificAtlantic(heights5)); // Expected: [[0,0],[0,1]]

        // Test Case 6: Special case - no flow to both
        int[][] heights6 = { { 1, 3, 2 }, { 4, 6, 5 } };
        System.out.println(solution.pacificAtlantic(heights6)); // Expected: [[0,0],[0,2],[1,0],[1,2]]

        // Test Case 7: Boundary case - 2x1
        int[][] heights7 = { { 1 }, { 2 } };
        System.out.println(solution.pacificAtlantic(heights7)); // Expected: [[0,0],[1,0]]

        // Test Case 8: Decreasing heights
        int[][] heights8 = { { 9, 8, 7 }, { 6, 5, 4 }, { 3, 2, 1 } };
        System.out.println(solution.pacificAtlantic(heights8)); // Expected:
                                                                // [[0,0],[0,1],[0,2],[1,0],[1,1],[1,2],[2,0],[2,1],[2,2]]

        // Test Case 9: Mountain in center
        int[][] heights9 = { { 1, 2, 1 }, { 2, 3, 2 }, { 1, 2, 1 } };
        System.out.println(solution.pacificAtlantic(heights9)); // Expected: [[0,0],[0,2],[1,1],[2,0],[2,2]]

        // Test Case 10: Valley pattern
        int[][] heights10 = { { 3, 1, 3 }, { 1, 2, 1 }, { 3, 1, 3 } };
        System.out.println(solution.pacificAtlantic(heights10)); // Expected: [[0,0],[0,2],[2,0],[2,2]]
    }
}
