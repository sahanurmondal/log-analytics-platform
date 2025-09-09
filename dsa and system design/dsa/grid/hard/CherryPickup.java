package grid.hard;

/**
 * LeetCode 741: Cherry Pickup
 * https://leetcode.com/problems/cherry-pickup/
 *
 * Description:
 * You are given an n x n grid representing a field of cherries, each cell is
 * one of three possible integers.
 * - 0 means the cell is empty, so you can pass through.
 * - 1 means the cell contains a cherry that you can pick up and pass through.
 * - -1 means the cell contains a thorn that blocks your way.
 * Return the maximum number of cherries you can collect by following the rules
 * below:
 * - Starting at the position (0, 0) and reaching (n - 1, n - 1) by moving right
 * or down through valid path cells (cells with value 0 or 1).
 * - After reaching (n - 1, n - 1), returning to (0, 0) by moving left or up
 * through valid path cells.
 * - When passing through a path cell containing a cherry, you pick it up, and
 * the cell becomes an empty cell 0.
 * - If there is no valid path between (0, 0) and (n - 1, n - 1), then no
 * cherries can be collected.
 *
 * Constraints:
 * - n == grid.length
 * - n == grid[i].length
 * - 1 <= n <= 50
 * - grid[i][j] is -1, 0, or 1
 * - grid[0][0] != -1
 * - grid[n - 1][n - 1] != -1
 */
public class CherryPickup {

    public int cherryPickup(int[][] grid) {
        int n = grid.length;
        Integer[][][] memo = new Integer[n][n][n];

        int result = dfs(grid, 0, 0, 0, memo);
        return Math.max(0, result);
    }

    private int dfs(int[][] grid, int r1, int c1, int r2, Integer[][][] memo) {
        int n = grid.length;
        int c2 = r1 + c1 - r2;

        // Check bounds and thorns
        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n ||
                grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return Integer.MIN_VALUE;
        }

        // Reached destination
        if (r1 == n - 1 && c1 == n - 1) {
            return grid[r1][c1];
        }

        if (memo[r1][c1][r2] != null) {
            return memo[r1][c1][r2];
        }

        int cherries = 0;
        if (r1 == r2 && c1 == c2) {
            cherries = grid[r1][c1];
        } else {
            cherries = grid[r1][c1] + grid[r2][c2];
        }

        // Try all possible moves
        int maxCherries = Math.max(
                Math.max(dfs(grid, r1 + 1, c1, r2 + 1, memo), // both down
                        dfs(grid, r1, c1 + 1, r2, memo)), // p1 right, p2 down
                Math.max(dfs(grid, r1 + 1, c1, r2, memo), // p1 down, p2 right
                        dfs(grid, r1, c1 + 1, r2 + 1, memo)) // both right
        );

        cherries += maxCherries;
        memo[r1][c1][r2] = cherries;
        return cherries;
    }

    public static void main(String[] args) {
        CherryPickup solution = new CherryPickup();

        int[][] grid1 = { { 0, 1, -1 }, { 1, 0, -1 }, { 1, 1, 1 } };
        System.out.println(solution.cherryPickup(grid1)); // 5

        int[][] grid2 = { { 1, 1, -1 }, { 1, -1, 1 }, { -1, 1, 1 } };
        System.out.println(solution.cherryPickup(grid2)); // 0
    }
}
