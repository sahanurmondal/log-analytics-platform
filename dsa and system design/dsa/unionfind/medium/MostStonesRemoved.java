package unionfind.medium;

import java.util.*;

/**
 * LeetCode 947: Most Stones Removed with Same Row or Column
 * https://leetcode.com/problems/most-stones-removed-with-same-row-or-column/
 *
 * Description:
 * On a 2D plane, we place n stones at some integer coordinate points.
 * Each coordinate point may have at most one stone.
 * A stone can be removed if it shares a row or column with another stone that
 * has not been removed.
 * Given an array stones, return the largest possible number of stones that can
 * be removed.
 *
 * Constraints:
 * - 1 <= stones.length <= 1000
 * - 0 <= xi, yi <= 10^4
 *
 * Visual Example:
 * stones = [[0,0],[0,1],[1,0],[1,2],[2,1],[2,2]]
 * 
 * Grid visualization:
 * 0 1 2
 * -----
 * X X . | 0
 * X . X | 1
 * . X X | 2
 * 
 * Can remove 5 stones, leaving 1
 *
 * Follow-up:
 * - Can you solve it using DFS as well?
 * - How would you handle 3D coordinates?
 */
public class MostStonesRemoved {

    class UnionFind {
        private Map<Integer, Integer> parent;
        private int components;

        public UnionFind() {
            parent = new HashMap<>();
            components = 0;
        }

        public int find(int x) {
            if (!parent.containsKey(x)) {
                parent.put(x, x);
                components++;
            }

            if (parent.get(x) != x) {
                parent.put(x, find(parent.get(x)));
            }
            return parent.get(x);
        }

        public void union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX != rootY) {
                parent.put(rootX, rootY);
                components--;
            }
        }

        public int getComponents() {
            return components;
        }
    }

    public int removeStones(int[][] stones) {
        UnionFind uf = new UnionFind();

        // Union stones that share row or column
        // Use different encoding for rows and columns to avoid conflicts
        for (int[] stone : stones) {
            int row = stone[0];
            int col = stone[1] + 10001; // Offset to distinguish from rows
            uf.union(row, col);
        }

        return stones.length - uf.getComponents();
    }

    public static void main(String[] args) {
        MostStonesRemoved solution = new MostStonesRemoved();

        // Test case 1: Multiple connected components
        System.out.println(
                solution.removeStones(new int[][] { { 0, 0 }, { 0, 1 }, { 1, 0 }, { 1, 2 }, { 2, 1 }, { 2, 2 } })); // 5

        // Test case 2: All stones in same row
        System.out.println(solution.removeStones(new int[][] { { 0, 0 }, { 0, 2 }, { 1, 1 }, { 2, 0 }, { 2, 2 } })); // 3

        // Test case 3: Single stone
        System.out.println(solution.removeStones(new int[][] { { 0, 0 } })); // 0

        // Test case 4: No stones share row/column
        System.out.println(solution.removeStones(new int[][] { { 0, 0 }, { 1, 1 }, { 2, 2 } })); // 0

        // Test case 5: All stones in same column
        System.out.println(solution.removeStones(new int[][] { { 0, 0 }, { 1, 0 }, { 2, 0 } })); // 2
    }
}
