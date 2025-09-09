
package matrix.hard;

/**
 * LeetCode 741: Cherry Pickup
 * <a href="https://leetcode.com/problems/cherry-pickup/">LeetCode Link</a>
 *
 * You are given an n x n grid representing a field of cherries, each cell is
 * one of three possible integers.
 *
 * Constraints:
 * <ul>
 * <li>1 &lt;= n &lt;= 50</li>
 * <li>grid[i][j] is -1, 0, or 1</li>
 * </ul>
 *
 * Approach:
 * Dynamic Programming (DP) with memoization. The problem is modeled as two
 * people starting from (0,0) and moving to (n-1,n-1) simultaneously, picking
 * cherries along the way. The DP state is defined by their positions, and
 * transitions consider all possible moves. Obstacles (-1) are handled, and
 * cherries are only picked once per cell.
 */

public class CherryPickup {

    /**
     * Returns the maximum number of cherries that can be picked up from the grid.
     * Uses DP with memoization to simulate two traversals as one.
     *
     * @param grid n x n grid with values -1 (obstacle), 0 (empty), 1 (cherry)
     * @return maximum cherries picked, or 0 if no valid path
     */
    public int cherryPickup(int[][] grid) {
        int n = grid.length;
        Integer[][][] dp = new Integer[n][n][n];
        int res = Math.max(0, dfs(grid, dp, 0, 0, 0));
        return res;
    }

    /**
     * Helper DFS with memoization for two people moving from (0,0) to (n-1,n-1).
     *
     * @param grid the grid
     * @param dp   memoization table
     * @param r1   row of person 1
     * @param c1   col of person 1
     * @param r2   row of person 2
     * @return max cherries collected from these positions
     */
    private int dfs(int[][] grid, Integer[][][] dp, int r1, int c1, int r2) {
        int n = grid.length;
        int c2 = r1 + c1 - r2;
        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n ||
                grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return Integer.MIN_VALUE;
        }
        if (r1 == n - 1 && c1 == n - 1) {
            return grid[r1][c1];
        }
        if (dp[r1][c1][r2] != null) {
            return dp[r1][c1][r2];
        }
        int cherries = grid[r1][c1];
        if (r1 != r2 || c1 != c2) {
            cherries += grid[r2][c2];
        }
        int max = Math.max(
                Math.max(dfs(grid, dp, r1 + 1, c1, r2 + 1), dfs(grid, dp, r1, c1 + 1, r2)),
                Math.max(dfs(grid, dp, r1 + 1, c1, r2), dfs(grid, dp, r1, c1 + 1, r2 + 1)));
        cherries += max;
        dp[r1][c1][r2] = cherries;
        return cherries;
    }

    /**
     * Main method for running test cases.
     */
    public static void main(String[] args) {
        CherryPickup solution = new CherryPickup();
        int[][] grid1 = { { 0, 1, -1 }, { 1, 0, -1 }, { 1, 1, 1 } };
        System.out.println(solution.cherryPickup(grid1)); // Expected: 5

        int[][] grid2 = { { 1, -1 }, { -1, 1 } };
        System.out.println(solution.cherryPickup(grid2)); // Expected: 0

        int[][] grid3 = { { 1 } };
        System.out.println(solution.cherryPickup(grid3)); // Expected: 1
    }
}
