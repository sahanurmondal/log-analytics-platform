package unionfind.medium;

/**
 * LeetCode 323: Number of Connected Components in an Undirected Graph
 * https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/
 *
 * Description:
 * You have a graph of n nodes. You are given an integer n and an array edges
 * where
 * edges[i] = [ai, bi] indicates that there is an edge between ai and bi in the
 * graph.
 * Return the number of connected components in the graph.
 *
 * Constraints:
 * - 1 <= n <= 2000
 * - 1 <= edges.length <= 5000
 * - edges[i].length == 2
 * - 0 <= ai <= bi < n
 * - ai != bi
 * - There are no repeated edges
 */
public class NumberOfConnectedComponents {

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
                components--;
            }
        }

        public int getComponents() {
            return components;
        }
    }

    public int countComponents(int n, int[][] edges) {
        UnionFind uf = new UnionFind(n);

        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }

        return uf.getComponents();
    }

    public static void main(String[] args) {
        NumberOfConnectedComponents solution = new NumberOfConnectedComponents();

        // Test case 1
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 3, 4 } };
        System.out.println(solution.countComponents(5, edges1)); // 2

        // Test case 2
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println(solution.countComponents(5, edges2)); // 1
    }
}
