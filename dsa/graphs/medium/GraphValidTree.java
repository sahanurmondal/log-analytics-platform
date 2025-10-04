package graphs.medium;

import java.util.*;

/**
 * LeetCode 261: Graph Valid Tree
 * https://leetcode.com/problems/graph-valid-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given n nodes and edges, determine if the graph is a valid tree.
 *
 * Constraints:
 * - 1 <= n <= 2000
 * - 0 <= edges.length <= n-1
 * 
 * Follow-up Questions:
 * 1. Can you solve with Union-Find?
 * 2. Can you solve with DFS/BFS?
 */
public class GraphValidTree {
    // Approach 1: Union-Find - O(n) time, O(n) space
    public boolean validTreeUF(int n, int[][] edges) {
        if (edges.length != n - 1)
            return false;
        int[] parent = new int[n];
        for (int i = 0; i < n; i++)
            parent[i] = i;
        for (int[] e : edges) {
            int u = find(parent, e[0]), v = find(parent, e[1]);
            if (u == v)
                return false;
            parent[u] = v;
        }
        return true;
    }

    private int find(int[] p, int x) {
        if (p[x] != x)
            p[x] = find(p, p[x]);
        return p[x];
    }

    // Approach 2: DFS
    public boolean validTreeDFS(int n, int[][] edges) {
        if (edges.length != n - 1)
            return false;
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++)
            adj.add(new ArrayList<>());
        for (int[] e : edges) {
            adj.get(e[0]).add(e[1]);
            adj.get(e[1]).add(e[0]);
        }
        boolean[] visited = new boolean[n];
        if (hasCycle(adj, visited, 0, -1))
            return false;
        for (boolean v : visited)
            if (!v)
                return false;
        return true;
    }

    private boolean hasCycle(List<List<Integer>> adj, boolean[] visited, int u, int parent) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                if (hasCycle(adj, visited, v, u))
                    return true;
            } else if (v != parent)
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        GraphValidTree gvt = new GraphValidTree();
        System.out.println(gvt.validTreeUF(5, new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 4 } })); // true
        System.out.println(
                gvt.validTreeDFS(5, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 1, 3 }, { 1, 4 } }) == false);

        // Single node
        System.out.println(gvt.validTreeUF(1, new int[][] {}) == true);

        // Disconnected graph
        System.out.println(gvt.validTreeDFS(4, new int[][] { { 0, 1 }, { 2, 3 } }) == false);

        // Cycle
        System.out.println(gvt.validTreeUF(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } }) == false);

        // All connected, no cycle
        System.out.println(gvt.validTreeDFS(3, new int[][] { { 0, 1 }, { 1, 2 } }) == true);

        // Large tree
        int n = 100;
        int[][] edges = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++)
            edges[i] = new int[] { i, i + 1 };
        System.out.println(gvt.validTreeUF(n, edges) == true);
    }
}
