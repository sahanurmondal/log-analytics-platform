package unionfind.medium;

/**
 * LeetCode 261: Graph Valid Tree
 * https://leetcode.com/problems/graph-valid-tree/
 *
 * Description:
 * You have a graph of n nodes labeled from 0 to n - 1. You are given an integer
 * n and a list of edges
 * where edges[i] = [ai, bi] indicates that there is an undirected edge between
 * nodes ai and bi in the graph.
 * Return true if the edges of the given graph make up a valid tree, and false
 * otherwise.
 *
 * Constraints:
 * - 1 <= n <= 2000
 * - 0 <= edges.length <= 5000
 * - edges[i].length == 2
 * - 0 <= ai, bi < n
 * - ai != bi
 * - There are no self-loops or repeated edges
 */
public class GraphValidTree {

    class UnionFind {
        private int[] parent;
        private int[] rank;
        private int components;

        public UnionFind(int n) {
            parent = new int[n];
            rank = new int[n];
            components = n;
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

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY) {
                return false; // Cycle detected
            }

            if (rank[rootX] < rank[rootY]) {
                parent[rootX] = rootY;
            } else if (rank[rootX] > rank[rootY]) {
                parent[rootY] = rootX;
            } else {
                parent[rootY] = rootX;
                rank[rootX]++;
            }

            components--;
            return true;
        }

        public int getComponents() {
            return components;
        }
    }

    public boolean validTree(int n, int[][] edges) {
        // A tree must have exactly n-1 edges
        if (edges.length != n - 1) {
            return false;
        }

        UnionFind uf = new UnionFind(n);

        // Check for cycles
        for (int[] edge : edges) {
            if (!uf.union(edge[0], edge[1])) {
                return false;
            }
        }

        // Check if all nodes are connected (1 component)
        return uf.getComponents() == 1;
    }

    public static void main(String[] args) {
        GraphValidTree solution = new GraphValidTree();

        // Test case 1: Valid tree
        int[][] edges1 = { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 4 } };
        System.out.println(solution.validTree(5, edges1)); // true

        // Test case 2: Has cycle
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 1, 3 }, { 1, 4 } };
        System.out.println(solution.validTree(5, edges2)); // false

        // Test case 3: Not connected
        int[][] edges3 = { { 0, 1 }, { 2, 3 } };
        System.out.println(solution.validTree(4, edges3)); // false
    }
}
