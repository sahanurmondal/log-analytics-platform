package unionfind.medium;

import java.util.*;

/**
 * LeetCode 305: Number of Islands II
 * https://leetcode.com/problems/number-of-islands-ii/
 *
 * Description:
 * You are given an empty 2D binary grid grid of size m x n. The grid represents
 * a map
 * where 0's represent water and 1's represent land. Initially, all the cells of
 * grid are water cells.
 * We can perform an addLand operation which turns the water at position into a
 * land.
 * Given an array positions where positions[i] = [ri, ci] is the position to
 * perform the ith addLand operation,
 * return an array that contains the number of islands after each addLand
 * operation.
 *
 * Constraints:
 * - 1 <= m, n, positions.length <= 10^4
 * - 1 <= m * n <= 10^4
 * - positions[i].length == 2
 * - 0 <= ri < m
 * - 0 <= ci < n
 *
 * Visual Example:
 * m = 3, n = 3, positions = [[0,0],[0,1],[1,2],[2,1]]
 * 
 * Step 1: [0,0] Step 2: [0,1] Step 3: [1,2] Step 4: [2,1]
 * 1 0 0 1 1 0 1 1 0 1 1 0
 * 0 0 0 → 1 0 0 0 → 1 0 0 1 → 2 0 1 1 → 3
 * 0 0 0 0 0 0 0 0 0 0 0 0
 * 
 * Output: [1,1,2,3]
 *
 * Follow-up:
 * - Can you solve it with better time complexity?
 * - How would you handle remove operations?
 */
public class NumberOfIslandsII {

    class UnionFind {
        private int[] parent;
        private int[] rank;
        private int count;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            count = 0;
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
                count--;
            }
        }

        public void addIsland() {
            count++;
        }

        public int getCount() {
            return count;
        }
    }

    public List<Integer> numIslandsAfterEachAddLand(int m, int n, int[][] positions) {
        List<Integer> result = new ArrayList<>();
        UnionFind uf = new UnionFind(m * n);
        boolean[][] grid = new boolean[m][n];
        int[][] directions = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

        for (int[] pos : positions) {
            int row = pos[0];
            int col = pos[1];

            if (grid[row][col]) {
                result.add(uf.getCount());
                continue;
            }

            grid[row][col] = true;
            uf.addIsland();

            int id = row * n + col;

            // Check all 4 directions
            for (int[] dir : directions) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n && grid[newRow][newCol]) {
                    int neighborId = newRow * n + newCol;
                    uf.union(id, neighborId);
                }
            }

            result.add(uf.getCount());
        }

        return result;
    }

    public static void main(String[] args) {
        NumberOfIslandsII solution = new NumberOfIslandsII();

        // Test case 1: Basic example
        System.out.println(solution.numIslandsAfterEachAddLand(3, 3,
                new int[][] { { 0, 0 }, { 0, 1 }, { 1, 2 }, { 2, 1 } })); // [1,1,2,3]

        // Test case 2: All positions form one island
        System.out.println(solution.numIslandsAfterEachAddLand(1, 1,
                new int[][] { { 0, 0 } })); // [1]

        // Test case 3: Merging islands
        System.out.println(solution.numIslandsAfterEachAddLand(3, 3,
                new int[][] { { 0, 0 }, { 0, 2 }, { 1, 1 }, { 0, 1 } })); // [1,2,3,2]

        // Test case 4: Duplicate positions
        System.out.println(solution.numIslandsAfterEachAddLand(2, 2,
                new int[][] { { 0, 0 }, { 0, 0 }, { 1, 1 } })); // [1,1,2]
    }
}
