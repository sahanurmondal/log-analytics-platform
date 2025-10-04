package grid.medium;

/**
 * LeetCode 463: Island Perimeter
 * https://leetcode.com/problems/island-perimeter/
 *
 * Description:
 * You are given row x col grid representing a map where grid[i][j] = 1
 * represents land and grid[i][j] = 0 represents water.
 * Grid cells are connected horizontally/vertically (not diagonally).
 * The grid is completely surrounded by water, and there is exactly one island
 * (i.e., one or more connected land cells).
 * The island doesn't have "lakes", meaning the water inside isn't connected to
 * the water around the island.
 * One cell is a square with side length 1. The grid is rectangular, width and
 * height don't exceed 100.
 * Determine the perimeter of the island.
 *
 * Constraints:
 * - row == grid.length
 * - col == grid[i].length
 * - 1 <= row, col <= 100
 * - grid[i][j] is 0 or 1
 * - There is exactly one island in grid
 */
public class IslandPerimeter {

    public int islandPerimeter(int[][] grid) {
        int perimeter = 0;
        int rows = grid.length, cols = grid[0].length;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (grid[i][j] == 1) {
                    perimeter += 4; // Start with 4 sides

                    // Subtract shared sides
                    if (i > 0 && grid[i - 1][j] == 1)
                        perimeter--;
                    if (i < rows - 1 && grid[i + 1][j] == 1)
                        perimeter--;
                    if (j > 0 && grid[i][j - 1] == 1)
                        perimeter--;
                    if (j < cols - 1 && grid[i][j + 1] == 1)
                        perimeter--;
                }
            }
        }

        return perimeter;
    }

    public static void main(String[] args) {
        IslandPerimeter solution = new IslandPerimeter();

        int[][] grid = { { 0, 1, 0, 0 }, { 1, 1, 1, 0 }, { 0, 1, 0, 0 }, { 1, 1, 0, 0 } };
        System.out.println(solution.islandPerimeter(grid)); // 16
    }
}
