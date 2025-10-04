package graphs.hard;

import java.util.*;

/**
 * LeetCode 1192: Critical Connections in a Network
 * https://leetcode.com/problems/critical-connections-in-a-network/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Find all bridges in an undirected graph.
 * A bridge is an edge which, when removed, increases the number of connected
 * components.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= connections.length <= 10^5
 * - connections[i].length == 2
 * 
 * Follow-up Questions:
 * 1. Can you find articulation points (cut vertices) as well?
 * 2. How would you handle a dynamically changing graph?
 * 3. Can you find biconnected components?
 */
public class FindBridgesInGraph {
    private int time = 0;

    // Approach 1: Tarjan's Algorithm - O(V+E) time, O(V) space
    public List<List<Integer>> criticalConnections(int n, List<List<Integer>> connections) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (List<Integer> edge : connections) {
            adj.get(edge.get(0)).add(edge.get(1));
            adj.get(edge.get(1)).add(edge.get(0));
        }

        List<List<Integer>> bridges = new ArrayList<>();
        int[] disc = new int[n]; // Discovery time
        int[] low = new int[n]; // Earliest reachable vertex
        Arrays.fill(disc, -1);

        // Run DFS from each unvisited vertex
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, -1, disc, low, bridges, adj);
            }
        }

        return bridges;
    }

    private void dfs(int u, int parent, int[] disc, int[] low,
            List<List<Integer>> bridges, List<List<Integer>> adj) {
        // Initialize discovery time and low value
        disc[u] = low[u] = ++time;

        // Go through all neighbors
        for (int v : adj.get(u)) {
            // Skip parent
            if (v == parent)
                continue;

            // If not discovered
            if (disc[v] == -1) {
                dfs(v, u, disc, low, bridges, adj);

                // Update low value of u
                low[u] = Math.min(low[u], low[v]);

                // If lowest vertex reachable from v is below u, it's a bridge
                if (low[v] > disc[u]) {
                    bridges.add(Arrays.asList(u, v));
                }
            } else {
                // Update low value if v is already visited
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // Approach 2: Naive Edge Removal - O(E*(V+E)) time, O(V+E) space
    public List<List<Integer>> findBridgesBruteForce(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        // Calculate original component count
        boolean[] visited = new boolean[n];
        int originalComponents = 0;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsCount(i, visited, adj);
                originalComponents++;
            }
        }

        List<List<Integer>> bridges = new ArrayList<>();

        // Try removing each edge
        for (int[] edge : edges) {
            // Remove edge temporarily
            removeEdge(adj, edge[0], edge[1]);

            // Count components
            Arrays.fill(visited, false);
            int components = 0;
            for (int i = 0; i < n; i++) {
                if (!visited[i]) {
                    dfsCount(i, visited, adj);
                    components++;
                }
            }

            // If removing the edge increases components, it's a bridge
            if (components > originalComponents) {
                bridges.add(Arrays.asList(edge[0], edge[1]));
            }

            // Restore the edge
            addEdge(adj, edge[0], edge[1]);
        }

        return bridges;
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

    // Follow-up 3: Find biconnected components
    public List<List<Integer>> findBiconnectedComponents(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        List<List<Integer>> bicomponents = new ArrayList<>();
        int[] disc = new int[n];
        int[] low = new int[n];
        Arrays.fill(disc, -1);

        Stack<int[]> stack = new Stack<>();
        time = 0; // Reset time

        // Run DFS for each unvisited vertex
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfsBicomp(i, -1, disc, low, stack, bicomponents, adj);
            }
        }

        return bicomponents;
    }

    private void dfsBicomp(int u, int parent, int[] disc, int[] low, Stack<int[]> stack,
            List<List<Integer>> bicomponents, List<List<Integer>> adj) {
        disc[u] = low[u] = ++time;

        for (int v : adj.get(u)) {
            if (v == parent)
                continue;

            int[] edge = new int[] { u, v };

            if (disc[v] == -1) {
                stack.push(edge);
                dfsBicomp(v, u, disc, low, stack, bicomponents, adj);

                low[u] = Math.min(low[u], low[v]);

                // If a bridge or articulation point is found
                if (low[v] >= disc[u]) {
                    List<Integer> component = new ArrayList<>();
                    int[] e;
                    do {
                        e = stack.pop();
                        component.add(e[0]);
                        component.add(e[1]);
                    } while (!(e[0] == u && e[1] == v) && !(e[0] == v && e[1] == u));

                    // Remove duplicates
                    Set<Integer> uniqueVertices = new HashSet<>(component);
                    bicomponents.add(new ArrayList<>(uniqueVertices));
                }
            } else if (disc[v] < disc[u]) {
                stack.push(edge);
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    // Helper method for converting edge arrays
    private List<List<Integer>> toEdgeList(int[][] edges) {
        List<List<Integer>> result = new ArrayList<>();
        for (int[] edge : edges) {
            result.add(Arrays.asList(edge[0], edge[1]));
        }
        return result;
    }

    public static void main(String[] args) {
        FindBridgesInGraph fbg = new FindBridgesInGraph();

        // Test case 1: Simple graph with one bridge
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 } };
        System.out.println("Bridges 1: " + fbg.criticalConnections(4, fbg.toEdgeList(edges1)));
        // Output: [[1, 3]]

        // Test case 2: No bridges
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 0 } };
        System.out.println("Bridges 2: " + fbg.criticalConnections(3, fbg.toEdgeList(edges2)));
        // Output: []

        // Test case 3: Multiple bridges
        int[][] edges3 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 }, { 3, 4 }, { 4, 5 }, { 5, 3 } };
        System.out.println("Bridges 3: " + fbg.criticalConnections(6, fbg.toEdgeList(edges3)));
        // Output: [[1, 3]]

        // Test case 4: LeetCode example
        int[][] edges4 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 } };
        System.out.println("Bridges 4 (brute force): " + fbg.findBridgesBruteForce(4, edges4));
        // Output: [[1, 3]]

        // Test case 5: Biconnected components
        System.out.println("Biconnected components: " + fbg.findBiconnectedComponents(4, edges1));
        // Output: Biconnected components
    }
}
