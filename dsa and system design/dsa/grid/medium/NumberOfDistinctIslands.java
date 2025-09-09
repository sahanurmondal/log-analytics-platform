package grid.medium;

import java.util.*;

/**
 * LeetCode 694: Number of Distinct Islands
 * https://leetcode.com/problems/number-of-distinct-islands/
 *
 * Description:
 * You are given an m x n binary matrix grid. An island is a group of 1's
 * (representing land)
 * connected 4-directionally (horizontal or vertical.) You may assume all four
 * edges of the grid are surrounded by water.
 * An island is considered to be the same as another if and only if one island
 * can be translated
 * (and not rotated or reflected) to equal the other.
 * Return the number of distinct islands.
 *
 * Constraints:
 * - m == grid.length
 * - n == grid[i].length
 * - 1 <= m, n <= 50
 * - grid[i][j] is either 0 or 1
 */
public class NumberOfDistinctIslands {

    private int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };
    private String[] dirStrings = { "R", "D", "L", "U" };

    public int numDistinctIslands(int[][] grid) {
        Set<String> shapes = new HashSet<>();

        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if (grid[i][j] == 1) {
                    StringBuilder shape = new StringBuilder();
                    dfs(grid, i, j, shape, "S"); // Start
                    shapes.add(shape.toString());
                }
            }
        }

        return shapes.size();
    }

    private void dfs(int[][] grid, int r, int c, StringBuilder shape, String direction) {
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] == 0) {
            return;
        }

        grid[r][c] = 0;
        shape.append(direction);

        for (int i = 0; i < 4; i++) {
            dfs(grid, r + directions[i][0], c + directions[i][1], shape, dirStrings[i]);
        }

        shape.append("B"); // Backtrack marker
    }

    public static void main(String[] args) {
        NumberOfDistinctIslands solution = new NumberOfDistinctIslands();

        int[][] grid1 = { { 1, 1, 0, 0, 0 }, { 1, 1, 0, 0, 0 }, { 0, 0, 0, 1, 1 }, { 0, 0, 0, 1, 1 } };
        System.out.println(solution.numDistinctIslands(grid1)); // 1

        int[][] grid2 = { { 1, 1, 0, 1, 1 }, { 1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 1 }, { 1, 1, 0, 1, 1 } };
        System.out.println(solution.numDistinctIslands(grid2)); // 3
    }
}
