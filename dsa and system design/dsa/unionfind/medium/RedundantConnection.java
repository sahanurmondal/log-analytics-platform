package unionfind.medium;

/**
 * LeetCode 684: Redundant Connection
 * https://leetcode.com/problems/redundant-connection/
 *
 * Description:
 * In this problem, a tree is an undirected graph that is connected and has no
 * cycles.
 * You are given a graph that started as a tree with n nodes labeled from 1 to
 * n,
 * with one additional edge added. Return an edge that can be removed so that
 * the
 * resulting graph is a tree of n nodes.
 *
 * Constraints:
 * - n == edges.length
 * - 3 <= n <= 1000
 * - edges[i].length == 2
 * - 1 <= ai < bi <= edges.length
 *
 * Visual Example:
 * Input: edges = [[1,2],[1,3],[2,3]]
 * 
 * 1---2
 * | |
 * +---3
 * 
 * Output: [2,3] (removing this creates a tree)
 *
 * Follow-up:
 * - What if there are multiple redundant edges?
 * - Can you solve it using DFS cycle detection?
 */
public class RedundantConnection {

    class UnionFind {
        private int[] parent;
        private int[] rank;

        public UnionFind(int n) {
            parent = new int[n + 1];
            rank = new int[n + 1];
            for (int i = 1; i <= n; i++) {
                parent[i] = i;
            }
        }

        public int find(int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return false; // Already connected - this edge creates a cycle
            }

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

            return true;
        }
    }

    public int[] findRedundantConnection(int[][] edges) {
        UnionFind uf = new UnionFind(edges.length);

        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) {
                return edge; // This edge creates a cycle
            }
        }

        return new int[0]; // Should not reach here given constraints
    }

    public static void main(String[] args) {
        RedundantConnection solution = new RedundantConnection();

        // Test case 1: Simple cycle
        System.out.println(java.util.Arrays.toString(
                solution.findRedundantConnection(new int[][] { { 1, 2 }, { 1, 3 }, { 2, 3 } }))); // [2,3]

        // Test case 2: Larger graph
        System.out.println(java.util.Arrays.toString(
                solution.findRedundantConnection(new int[][] { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 1, 4 }, { 1, 5 } }))); // [1,4]

        // Test case 3: Line with one extra edge
        System.out.println(java.util.Arrays.toString(
                solution.findRedundantConnection(new int[][] { { 1, 2 }, { 2, 3 }, { 1, 3 } }))); // [1,3]
    }
}
