package unionfind.hard;

/**
 * LeetCode 685: Redundant Connection II
 * https://leetcode.com/problems/redundant-connection-ii/
 *
 * Description:
 * In this problem, a rooted tree is a directed graph such that, there is
 * exactly one node
 * (the root) for which all other nodes are descendants of this node, plus every
 * node has
 * exactly one parent, except for the root node which has no parents.
 * The given input is a directed graph that started as a rooted tree with n
 * nodes
 * (with distinct values from 1 to n), with one additional directed edge added.
 * The added edge has two different vertices chosen from 1 to n, and was not an
 * edge
 * that already existed.
 * Return an edge that can be removed so that the resulting graph is a rooted
 * tree of n nodes.
 *
 * Constraints:
 * - n == edges.length
 * - 3 <= n <= 1000
 * - edges[i].length == 2
 * - 1 <= ai, bi <= n
 * - ai != bi
 */
public class RedundantConnectionII {

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
                return false;
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

    public int[] findRedundantDirectedConnection(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1];
        int[] candidate1 = null, candidate2 = null;

        // Find node with two parents
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];
            if (parent[v] == 0) {
                parent[v] = u;
            } else {
                candidate1 = new int[] { parent[v], v };
                candidate2 = new int[] { u, v };
                edge[1] = 0; // invalidate this edge
            }
        }

        // Union Find to detect cycle
        UnionFind uf = new UnionFind(n);
        for (int[] edge : edges) {
            if (edge[1] == 0)
                continue; // skip invalidated edge
            if (!uf.union(edge[0], edge[1])) {
                if (candidate1 == null) {
                    return edge;
                }
                return candidate1;
            }
        }

        return candidate2;
    }

    public static void main(String[] args) {
        RedundantConnectionII solution = new RedundantConnectionII();

        // Test case 1
        int[][] edges1 = { { 1, 2 }, { 1, 3 }, { 2, 3 } };
        System.out.println(java.util.Arrays.toString(solution.findRedundantDirectedConnection(edges1))); // [2,3]

        // Test case 2
        int[][] edges2 = { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 4, 1 }, { 1, 5 } };
        System.out.println(java.util.Arrays.toString(solution.findRedundantDirectedConnection(edges2))); // [4,1]
    }
}
