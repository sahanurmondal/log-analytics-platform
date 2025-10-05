package unionfind.medium;

import unionfind.UnionFind;

/**
 * LeetCode 959: Regions Cut By Slashes
 * https://leetcode.com/problems/regions-cut-by-slashes/
 *
 * Description:
 * An n x n grid is composed of 1 x 1 squares where each 1 x 1 square consists
 * of a '/', '\', or blank space ' '.
 * These characters divide the square into contiguous regions.
 * Given the grid represented as a string array, return the number of regions.
 *
 * Constraints:
 * - n == grid.length == grid[i].length
 * - 1 <= n <= 30
 * - grid[i][j] is either '/', '\', or ' '
 */
public class RegionsCutBySlashes {

    public int regionsBySlashes(String[] grid) {
        int n = grid.length;
        UnionFind uf = new UnionFind(4 * n * n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int root = 4 * (i * n + j);
                char c = grid[i].charAt(j);

                // Connect triangles within the same cell based on the character
                if (c == ' ') {
                    // Space: connect all 4 triangles
                    uf.union(root + 0, root + 1);
                    uf.union(root + 1, root + 2);
                    uf.union(root + 2, root + 3);
                } else if (c == '/') {
                    // Forward slash: connect top with left, right with bottom
                    uf.union(root + 0, root + 3);
                    uf.union(root + 1, root + 2);
                } else if (c == '\\') {
                    // Backslash: connect top with right, left with bottom
                    uf.union(root + 0, root + 1);
                    uf.union(root + 2, root + 3);
                }

                // Connect with adjacent cells (right and bottom)
                if (j + 1 < n) {
                    // Connect right side (1) to left side of next cell (3)
                    uf.union(root + 1, 4 * (i * n + j + 1) + 3);
                }
                if (i + 1 < n) {
                    // Connect bottom side (2) to top side of next cell (0)
                    uf.union(root + 2, 4 * ((i + 1) * n + j) + 0);
                }
            }
        }

        return uf.getComponents();
    }

    public static void main(String[] args) {
        RegionsCutBySlashes solution = new RegionsCutBySlashes();

        // Test case 1
        String[] grid1 = { " /", "/ " };
        System.out.println(solution.regionsBySlashes(grid1)); // 2

        // Test case 2
        String[] grid2 = { " /", "  " };
        System.out.println(solution.regionsBySlashes(grid2)); // 1

        // Test case 3
        String[] grid3 = { "/\\", "\\/" };
        System.out.println(solution.regionsBySlashes(grid3)); // 5
    }
}
