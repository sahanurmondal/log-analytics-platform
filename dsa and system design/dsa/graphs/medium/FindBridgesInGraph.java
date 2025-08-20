package graphs.medium;

import java.util.*;

/**
 * LeetCode 1192: Critical Connections in a Network
 * https://leetcode.com/problems/critical-connections-in-a-network/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Find all bridges in an undirected graph.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= connections.length <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find articulation points as well?
 * 2. How would you handle a dynamically changing graph?
 * 3. Can you find biconnected components?
 */
public class FindBridgesInGraph {
    private int time = 0;

    // Approach 1: Tarjan's Algorithm - O(V+E) time, O(V) space
    public List<List<Integer>> findBridges(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        List<List<Integer>> bridges = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int[] disc = new int[n];
        int[] low = new int[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, -1, visited, disc, low, bridges, adj);
            }
        }

        return bridges;
    }

    private void dfs(int u, int parent, boolean[] visited, int[] disc, int[] low,
            List<List<Integer>> bridges, List<List<Integer>> adj) {
        visited[u] = true;
        disc[u] = low[u] = ++time;

        for (int v : adj.get(u)) {
            if (v == parent)
                continue;

            if (!visited[v]) {
                dfs(v, u, visited, disc, low, bridges, adj);

                low[u] = Math.min(low[u], low[v]);

                if (low[v] > disc[u]) {
                    bridges.add(Arrays.asList(u, v));
                }
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // Approach 2: Brute Force - O(E*(V+E)) time, O(V+E) space
    public List<List<Integer>> findBridgesBruteForce(int n, int[][] edges) {
        List<List<Integer>> bridges = new ArrayList<>();

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        int originalComponents = countComponents(n, adj);

        for (int[] edge : edges) {
            removeEdge(adj, edge[0], edge[1]);

            int newComponents = countComponents(n, adj);

            if (newComponents > originalComponents) {
                bridges.add(Arrays.asList(edge[0], edge[1]));
            }

            addEdge(adj, edge[0], edge[1]);
        }

        return bridges;
    }

    private int countComponents(int n, List<List<Integer>> adj) {
        boolean[] visited = new boolean[n];
        int components = 0;

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsCount(i, visited, adj);
                components++;
            }
        }

        return components;
    }

    private void dfsCount(int u, boolean[] visited, List<List<Integer>> adj) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                dfsCount(v, visited, adj);
            }
        }
    }

    private void removeEdge(List<List<Integer>> adj, int u, int v) {
        adj.get(u).remove(Integer.valueOf(v));
        adj.get(v).remove(Integer.valueOf(u));
    }

    private void addEdge(List<List<Integer>> adj, int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    // Follow-up 1: Find articulation points
    public List<Integer> findArticulationPoints(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        List<Integer> artPoints = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int[] disc = new int[n];
        int[] low = new int[n];
        boolean[] isAP = new boolean[n];

        time = 0; // Reset time

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsAP(i, -1, visited, disc, low, isAP, adj);
            }
        }

        for (int i = 0; i < n; i++) {
            if (isAP[i]) {
                artPoints.add(i);
            }
        }

        return artPoints;
    }

    private void dfsAP(int u, int parent, boolean[] visited, int[] disc, int[] low,
            boolean[] isAP, List<List<Integer>> adj) {
        int children = 0;
        visited[u] = true;
        disc[u] = low[u] = ++time;

        for (int v : adj.get(u)) {
            if (v == parent)
                continue;

            if (!visited[v]) {
                children++;
                dfsAP(v, u, visited, disc, low, isAP, adj);

                low[u] = Math.min(low[u], low[v]);

                if (parent == -1 && children > 1) {
                    isAP[u] = true;
                }
                if (parent != -1 && low[v] >= disc[u]) {
                    isAP[u] = true;
                }
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    public static void main(String[] args) {
        FindBridgesInGraph fbg = new FindBridgesInGraph();

        // Test case 1: Simple graph with one bridge
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 } };
        System.out.println("Bridges 1: " + fbg.findBridges(4, edges1));
        // Output: [[1, 3]]

        // Test case 2: No bridges
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 0 } };
        System.out.println("Bridges 2: " + fbg.findBridges(3, edges2));
        // Output: []

        // Test case 3: Multiple bridges
        int[][] edges3 = { { 0, 1 }, { 1, 2 }, { 2, 3 } };
        System.out.println("Bridges 3: " + fbg.findBridges(4, edges3));
        // Output: [[2, 3], [1, 2], [0, 1]]

        // Test case 4: Brute force approach
        System.out.println("Bridges (brute force): " + fbg.findBridgesBruteForce(4, edges1));
        // Output: [[1, 3]]

        // Test case 5: Find articulation points
        System.out.println("Articulation points: " + fbg.findArticulationPoints(4, edges1));
        // Output: [1]
    }
}
