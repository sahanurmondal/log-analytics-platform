package unionfind.easy;

/**
 * LeetCode 323: Number of Connected Components in an Undirected Graph
 * https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/
 *
 * Description:
 * Given n nodes labeled from 0 to n-1 and a list of undirected edges,
 * write a function to find the number of connected components in an undirected
 * graph.
 *
 * Constraints:
 * - 1 <= n <= 2000
 * - 1 <= edges.length <= 5000
 * - edges[i].length == 2
 * - 0 <= ai <= bi < n
 *
 * Visual Example:
 * n = 5, edges = [[0,1],[1,2],[3,4]]
 * 
 * 0---1---2 3---4
 * 
 * Output: 2 (two connected components)
 *
 * Follow-up:
 * - Can you solve it using DFS/BFS as well?
 * - How would you handle dynamic edge additions?
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
                parent[x] = find(parent[x]); // Path compression
            }
            return parent[x];
        }

        public boolean union(int x, int y) {
            int rootX = find(x);
            int rootY = find(y);

            if (rootX == rootY)
                return false;

            // Union by rank
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

    public int countComponents(int n, int[][] edges) {
        UnionFind uf = new UnionFind(n);

        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }

        return uf.getComponents();
    }

    public static void main(String[] args) {
        NumberOfConnectedComponents solution = new NumberOfConnectedComponents();

        // Test case 1: Multiple components
        System.out.println(solution.countComponents(5, new int[][] { { 0, 1 }, { 1, 2 }, { 3, 4 } })); // 2

        // Test case 2: All connected
        System.out.println(solution.countComponents(3, new int[][] { { 0, 1 }, { 1, 2 } })); // 1

        // Test case 3: No edges
        System.out.println(solution.countComponents(4, new int[][] {})); // 4

        // Test case 4: Single node
        System.out.println(solution.countComponents(1, new int[][] {})); // 1

        // Test case 5: Self loop (should be ignored in undirected graph)
        System.out.println(solution.countComponents(2, new int[][] { { 0, 0 }, { 1, 1 } })); // 2
    }
}
