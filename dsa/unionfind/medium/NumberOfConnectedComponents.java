package unionfind.medium;

import unionfind.UnionFind;

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
