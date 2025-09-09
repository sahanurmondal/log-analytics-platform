package unionfind.medium;

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

    class UnionFind {
        private int[] parent;
        private int[] rank;
        private int regions;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            regions = n;
            for (int i = 0; i < n; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                if (rank[rootX] < rank[rootY]) {
                    parent[rootX] = rootY;
                } else if (rank[rootX] > rank[rootY]) {
                    parent[rootY] = rootX;
                } else {
                    parent[rootY] = rootX;
                    rank[rootX]++;
                }
                regions--;
            }
        }

        public int getRegions() {
            return regions;
        }
    }

    public int regionsBySlashes(String[] grid) {
        int n = grid.length;
        UnionFind uf = new UnionFind(4 * n * n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int root = 4 * (i * n + j);
                char c = grid[i].charAt(j);

                // Connect triangles within the same cell
                if (c != '\\') {
                    uf.union(root + 0, root + 1); // top-right
                    uf.union(root + 2, root + 3); // bottom-left
                }
                if (c != '/') {
                    uf.union(root + 0, root + 3); // top-left
                    uf.union(root + 1, root + 2); // bottom-right
                }

                // Connect with adjacent cells
                if (i + 1 < n) {
                    uf.union(root + 2, 4 * ((i + 1) * n + j) + 0);
                }
                if (j + 1 < n) {
                    uf.union(root + 1, 4 * (i * n + j + 1) + 3);
                }
            }
        }

        return uf.getRegions();
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
