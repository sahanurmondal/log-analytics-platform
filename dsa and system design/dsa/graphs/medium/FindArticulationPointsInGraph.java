package graphs.medium;

import java.util.*;

/**
 * LeetCode 1192 variant: Articulation Points (Critical Vertices)
 * https://en.wikipedia.org/wiki/Biconnected_component
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 6+ interviews)
 *
 * Description: Find all articulation points (cut vertices) in an undirected
 * graph.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= edges.length <= 2*10^5
 * 
 * Follow-up Questions:
 * 1. Can you find bridges as well?
 * 2. Can you count how many components would be created by removing each point?
 * 3. Can you handle dynamic graph updates?
 */
public class FindArticulationPointsInGraph {
    private int time = 0;

    // Approach 1: Tarjan's Algorithm - O(V+E) time, O(V) space
    public List<Integer> findArticulationPoints(int n, int[][] edges) {
        // Build adjacency list
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

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, -1, visited, disc, low, isAP, adj);
            }
        }

        for (int i = 0; i < n; i++) {
            if (isAP[i]) {
                artPoints.add(i);
            }
        }

        return artPoints;
    }

    private void dfs(int u, int parent, boolean[] visited, int[] disc, int[] low,
            boolean[] isAP, List<List<Integer>> adj) {
        int children = 0;
        visited[u] = true;
        disc[u] = low[u] = ++time;

        for (int v : adj.get(u)) {
            if (v == parent)
                continue;

            if (!visited[v]) {
                children++;
                dfs(v, u, visited, disc, low, isAP, adj);

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

    // Approach 2: Brute Force - O(V*(V+E)) time, O(V+E) space
    public List<Integer> findArticulationPointsBruteForce(int n, int[][] edges) {
        List<Integer> artPoints = new ArrayList<>();

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        int originalComponents = countComponents(n, adj, -1);

        for (int i = 0; i < n; i++) {
            int newComponents = countComponents(n, adj, i);
            if (newComponents > originalComponents) {
                artPoints.add(i);
            }
        }

        return artPoints;
    }

    private int countComponents(int n, List<List<Integer>> adj, int removed) {
        boolean[] visited = new boolean[n];
        int components = 0;

        for (int i = 0; i < n; i++) {
            if (i != removed && !visited[i]) {
                dfsCount(i, visited, adj, removed);
                components++;
            }
        }

        return components;
    }

    private void dfsCount(int u, boolean[] visited, List<List<Integer>> adj, int removed) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (v != removed && !visited[v]) {
                dfsCount(v, visited, adj, removed);
            }
        }
    }

    // Follow-up 1: Find bridges
    public List<List<Integer>> findBridges(int n, int[][] edges) {
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

        time = 0; // Reset time

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsBridges(i, -1, visited, disc, low, bridges, adj);
            }
        }

        return bridges;
    }

    private void dfsBridges(int u, int parent, boolean[] visited, int[] disc, int[] low,
            List<List<Integer>> bridges, List<List<Integer>> adj) {
        visited[u] = true;
        disc[u] = low[u] = ++time;

        for (int v : adj.get(u)) {
            if (v == parent)
                continue;

            if (!visited[v]) {
                dfsBridges(v, u, visited, disc, low, bridges, adj);

                low[u] = Math.min(low[u], low[v]);

                if (low[v] > disc[u]) {
                    bridges.add(Arrays.asList(u, v));
                }
            } else {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    public static void main(String[] args) {
        FindArticulationPointsInGraph fap = new FindArticulationPointsInGraph();

        // Test case 1: Simple graph with one articulation point
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 }, { 3, 4 } };
        System.out.println("Articulation points 1: " + fap.findArticulationPoints(5, edges1));
        // Output: [1]

        // Test case 2: No articulation points (cycle)
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 } };
        System.out.println("Articulation points 2: " + fap.findArticulationPoints(4, edges2));
        // Output: []

        // Test case 3: Multiple articulation points
        int[][] edges3 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 }, { 3, 4 }, { 4, 5 }, { 5, 3 } };
        System.out.println("Articulation points 3: " + fap.findArticulationPoints(6, edges3));
        // Output: [1, 3]

        // Test case 4: Find bridges
        System.out.println("Bridges: " + fap.findBridges(5, edges1));
        // Output: [[3, 4], [1, 3]]

        // Test case 5: Brute force approach
        System.out.println("Articulation points (brute force): " + fap.findArticulationPointsBruteForce(5, edges1));
        // Output: [1]
    }
}
