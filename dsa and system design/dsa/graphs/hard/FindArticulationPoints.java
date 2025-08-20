package graphs.hard;

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
 * An articulation point is a vertex which, when removed, increases the number
 * of connected components.
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
public class FindArticulationPoints {
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
            adj.get(edge[1]).add(edge[0]); // Undirected
        }

        List<Integer> artPoints = new ArrayList<>();
        boolean[] visited = new boolean[n];
        int[] disc = new int[n]; // Discovery time
        int[] low = new int[n]; // Earliest visited vertex
        boolean[] isAP = new boolean[n]; // To avoid duplicates

        // DFS for each unvisited vertex
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(i, -1, visited, disc, low, isAP, adj);
            }
        }

        // Collect all articulation points
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
            // Skip parent
            if (v == parent)
                continue;

            if (!visited[v]) {
                children++;
                dfs(v, u, visited, disc, low, isAP, adj);

                // Check if u is an articulation point
                low[u] = Math.min(low[u], low[v]);

                // Root is AP if it has more than one child in DFS tree
                if (parent == -1 && children > 1) {
                    isAP[u] = true;
                }

                // Non-root is AP if low value of any of its children is >= discovery value of u
                if (parent != -1 && low[v] >= disc[u]) {
                    isAP[u] = true;
                }
            } else {
                // Update low value if v is already visited
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // Approach 2: Component Counting - O(V*(V+E)) time, O(V+E) space
    // Brute force approach by removing each vertex and counting components
    public List<Integer> findArticulationPointsBruteForce(int n, int[][] edges) {
        List<Integer> artPoints = new ArrayList<>();

        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        // Count original connected components
        boolean[] visited = new boolean[n];
        int originalComponents = 0;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsCount(i, visited, adj);
                originalComponents++;
            }
        }

        // Try removing each vertex
        for (int i = 0; i < n; i++) {
            // Create a new graph without vertex i
            boolean[] skip = new boolean[n];
            skip[i] = true;

            // Count components after removal
            Arrays.fill(visited, false);
            int components = 0;
            for (int j = 0; j < n; j++) {
                if (!skip[j] && !visited[j]) {
                    dfsCountWithSkip(j, visited, adj, skip);
                    components++;
                }
            }

            // If removing i increases components, it's an articulation point
            if (components > originalComponents) {
                artPoints.add(i);
            }
        }

        return artPoints;
    }

    private void dfsCount(int u, boolean[] visited, List<List<Integer>> adj) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!visited[v]) {
                dfsCount(v, visited, adj);
            }
        }
    }

    private void dfsCountWithSkip(int u, boolean[] visited, List<List<Integer>> adj, boolean[] skip) {
        visited[u] = true;
        for (int v : adj.get(u)) {
            if (!skip[v] && !visited[v]) {
                dfsCountWithSkip(v, visited, adj, skip);
            }
        }
    }

    // Follow-up 2: Count components created by removing each vertex
    public int[] countComponentsAfterRemoval(int n, int[][] edges) {
        int[] componentCounts = new int[n];

        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        // For each vertex, count components after its removal
        for (int i = 0; i < n; i++) {
            boolean[] skip = new boolean[n];
            skip[i] = true;

            boolean[] visited = new boolean[n];
            int components = 0;

            for (int j = 0; j < n; j++) {
                if (!skip[j] && !visited[j]) {
                    dfsCountWithSkip(j, visited, adj, skip);
                    components++;
                }
            }

            componentCounts[i] = components;
        }

        return componentCounts;
    }

    public static void main(String[] args) {
        FindArticulationPoints fap = new FindArticulationPoints();

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

        // Test case 4: Bridge edge creates articulation points
        int[][] edges4 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 2, 3 }, { 3, 4 }, { 4, 5 }, { 5, 6 }, { 6, 4 } };
        System.out.println("Articulation points 4: " + fap.findArticulationPoints(7, edges4));
        // Output: [2, 3]

        // Test case 5: Component counting
        System.out.println("Component counts: " + Arrays.toString(fap.countComponentsAfterRemoval(5, edges1)));
        // Should show higher count for articulation points
    }
}
