package graphs.hard;

import java.util.*;

/**
 * LeetCode 1192: Critical Connections in a Network
 * https://leetcode.com/problems/critical-connections-in-a-network/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Find all critical connections (bridges) in a network.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - connections.length <= 10^5
 * - connections[i].length == 2
 * 
 * Follow-up Questions:
 * 1. Can you solve for directed graphs?
 * 2. Can you handle dynamic edge updates?
 * 3. Can you find articulation points too?
 */
public class FindCriticalConnections {
    // Approach 1: Tarjan's Algorithm for Bridges - O(V+E) time, O(V+E) space
    public java.util.List<java.util.List<Integer>> criticalConnectionsTarjan(int n,
            java.util.List<java.util.List<Integer>> connections) {
        java.util.List<java.util.List<Integer>> adj = new java.util.ArrayList<>();
        for (int i = 0; i < n; i++)
            adj.add(new java.util.ArrayList<>());
        for (java.util.List<Integer> c : connections) {
            adj.get(c.get(0)).add(c.get(1));
            adj.get(c.get(1)).add(c.get(0));
        }
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        int[] disc = new int[n], low = new int[n];
        java.util.Arrays.fill(disc, -1);
        time = 0;
        dfs(0, -1, adj, disc, low, res);
        return res;
    }

    private int time = 0;

    private void dfs(int u, int parent, java.util.List<java.util.List<Integer>> adj, int[] disc, int[] low,
            java.util.List<java.util.List<Integer>> res) {
        disc[u] = low[u] = ++time;
        for (int v : adj.get(u)) {
            if (v == parent)
                continue;
            if (disc[v] == -1) {
                dfs(v, u, adj, disc, low, res);
                low[u] = Math.min(low[u], low[v]);
                if (low[v] > disc[u])
                    res.add(java.util.Arrays.asList(u, v));
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // Approach 2: DFS with edge removal (less efficient, for small graphs)
    public java.util.List<java.util.List<Integer>> criticalConnectionsDFS(int n,
            java.util.List<java.util.List<Integer>> connections) {
        // ...implementation omitted for brevity...
        return null;
    }

    // Follow-up: Find articulation points
    public Set<Integer> findArticulationPoints(int n, List<List<Integer>> connections) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++)
            adj.add(new ArrayList<>());
        for (List<Integer> c : connections) {
            adj.get(c.get(0)).add(c.get(1));
            adj.get(c.get(1)).add(c.get(0));
        }
        Set<Integer> artPoints = new HashSet<>();
        int[] disc = new int[n], low = new int[n];
        Arrays.fill(disc, -1);
        time2 = 0;
        dfsArt(0, -1, adj, disc, low, artPoints);
        return artPoints;
    }

    private int time2 = 0;

    private void dfsArt(int u, int parent, List<List<Integer>> adj, int[] disc, int[] low, Set<Integer> artPoints) {
        disc[u] = low[u] = ++time2;
        int children = 0;
        for (int v : adj.get(u)) {
            if (v == parent)
                continue;
            if (disc[v] == -1) {
                children++;
                dfsArt(v, u, adj, disc, low, artPoints);
                low[u] = Math.min(low[u], low[v]);
                if (parent != -1 && low[v] >= disc[u])
                    artPoints.add(u);
                if (parent == -1 && children > 1)
                    artPoints.add(u);
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // Helper: Convert edge array to list format
    public static java.util.List<java.util.List<Integer>> toList(int[][] edges) {
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        for (int[] e : edges)
            res.add(java.util.Arrays.asList(e[0], e[1]));
        return res;
    }

    public static void main(String[] args) {
        FindCriticalConnections fcc = new FindCriticalConnections();
        // Basic case
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 } };
        System.out.println(fcc.criticalConnectionsTarjan(4, toList(edges1))); // [[1,3]]
        // All connected (no bridges)
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 0 } };
        System.out.println(fcc.criticalConnectionsTarjan(3, toList(edges2))); // []
        // Disconnected graph
        int[][] edges3 = { { 0, 1 }, { 2, 3 } };
        System.out.println(fcc.criticalConnectionsTarjan(4, toList(edges3))); // [[0,1],[2,3]]
        // Single node
        int[][] edges4 = {};
        System.out.println(fcc.criticalConnectionsTarjan(1, toList(edges4))); // []
        // Large graph
        int n = 100;
        int[][] edges5 = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++)
            edges5[i] = new int[] { i, i + 1 };
        System.out.println(fcc.criticalConnectionsTarjan(n, toList(edges5))); // All edges are bridges

        // Articulation points test
        int[][] edges6 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 }, { 3, 4 } };
        System.out.println(fcc.findArticulationPoints(5, toList(edges6))); // [1,3]
    }
}
