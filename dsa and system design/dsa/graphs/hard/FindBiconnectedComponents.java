package graphs.hard;

import java.util.*;

/**
 * Biconnected Components in Graph
 * https://en.wikipedia.org/wiki/Biconnected_component
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 4+ interviews)
 *
 * Description: Find all biconnected components in an undirected graph.
 * A biconnected component is a maximal biconnected subgraph with no
 * articulation points.
 *
 * Constraints:
 * - 1 <= n <= 10^4
 * - 0 <= edges.length <= 2*10^4
 * 
 * Follow-up Questions:
 * 1. Can you identify articulation points?
 * 2. Can you determine the number of connected components after removing an
 * edge?
 * 3. Can you extend this to find triconnected components?
 */
public class FindBiconnectedComponents {
    private int time = 0;

    // Approach 1: Tarjan's Algorithm - O(V+E) time, O(V+E) space
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

        List<List<Integer>> result = new ArrayList<>();
        int[] disc = new int[n]; // Discovery time
        int[] low = new int[n]; // Earliest visited vertex reachable from subtree
        Arrays.fill(disc, -1); // -1 means not visited

        Stack<int[]> stack = new Stack<>(); // Store edges in stack

        // DFS on each connected component
        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, -1, disc, low, stack, adj, result);
            }
        }

        return result;
    }

    private void dfs(int u, int parent, int[] disc, int[] low, Stack<int[]> stack,
            List<List<Integer>> adj, List<List<Integer>> result) {
        disc[u] = low[u] = ++time;

        for (int v : adj.get(u)) {
            // Skip parent
            if (v == parent)
                continue;

            // If not visited yet
            if (disc[v] == -1) {
                stack.push(new int[] { u, v });
                dfs(v, u, disc, low, stack, adj, result);

                // Check if articulation point (biconnected component boundary)
                low[u] = Math.min(low[u], low[v]);

                if (low[v] >= disc[u]) {
                    // Found a biconnected component
                    List<Integer> component = new ArrayList<>();
                    int[] edge;
                    do {
                        edge = stack.pop();
                        component.add(edge[0]);
                        component.add(edge[1]);
                    } while (!(edge[0] == u && edge[1] == v) && !(edge[0] == v && edge[1] == u));

                    // Remove duplicates and add component to result
                    Set<Integer> uniqueVertices = new HashSet<>(component);
                    result.add(new ArrayList<>(uniqueVertices));
                }
            }
            // Back edge to already visited vertex
            else if (disc[v] < disc[u]) {
                low[u] = Math.min(low[u], disc[v]);
                stack.push(new int[] { u, v });
            }
        }
    }

    // Approach 2: Alternative with explicit articulation points - O(V+E) time,
    // O(V+E) space
    public List<List<Integer>> findBiconnectedComponentsWithArticulationPoints(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        // Find articulation points first
        Set<Integer> artPoints = findArticulationPoints(n, edges);

        // Then decompose graph into biconnected components
        List<List<Integer>> result = new ArrayList<>();
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i] && !artPoints.contains(i)) {
                List<Integer> component = new ArrayList<>();
                dfsComponent(i, -1, visited, adj, component, artPoints);
                if (!component.isEmpty()) {
                    result.add(component);
                }
            }
        }

        return result;
    }

    private void dfsComponent(int u, int parent, boolean[] visited, List<List<Integer>> adj,
            List<Integer> component, Set<Integer> artPoints) {
        visited[u] = true;
        component.add(u);

        for (int v : adj.get(u)) {
            if (v != parent && !visited[v] && !artPoints.contains(v)) {
                dfsComponent(v, u, visited, adj, component, artPoints);
            }
        }
    }

    // Follow-up 1: Find articulation points
    public Set<Integer> findArticulationPoints(int n, int[][] edges) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        Set<Integer> artPoints = new HashSet<>();
        int[] disc = new int[n];
        int[] low = new int[n];
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        time = 0; // Reset time

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsAP(i, visited, disc, low, parent, artPoints, adj);
            }
        }

        return artPoints;
    }

    private void dfsAP(int u, boolean[] visited, int[] disc, int[] low, int[] parent,
            Set<Integer> artPoints, List<List<Integer>> adj) {
        int children = 0;
        visited[u] = true;
        disc[u] = low[u] = ++time;

        for (int v : adj.get(u)) {
            if (!visited[v]) {
                children++;
                parent[v] = u;
                dfsAP(v, visited, disc, low, parent, artPoints, adj);

                low[u] = Math.min(low[u], low[v]);

                // Articulation point conditions
                if (parent[u] == -1 && children > 1) {
                    artPoints.add(u); // Root with multiple children
                }
                if (parent[u] != -1 && low[v] >= disc[u]) {
                    artPoints.add(u); // Non-root node
                }
            } else if (v != parent[u]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }
    }

    public static void main(String[] args) {
        FindBiconnectedComponents fbc = new FindBiconnectedComponents();

        // Test case 1: Simple graph with one biconnected component
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 } };
        System.out.println("Biconnected components 1: " + fbc.findBiconnectedComponents(3, edges1));
        // Output: [[0, 1, 2]]

        // Test case 2: Graph with two biconnected components
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 }, { 3, 4 }, { 4, 1 } };
        System.out.println("Biconnected components 2: " + fbc.findBiconnectedComponents(5, edges2));
        // Output: [[1, 3, 4], [0, 1, 2]]

        // Test case 3: Graph with articulation point
        int[][] edges3 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 2, 3 }, { 3, 4 } };
        System.out.println("Articulation points: " + fbc.findArticulationPoints(5, edges3));
        // Output: [2]

        // Test case 4: Graph with no biconnected components (tree)
        int[][] edges4 = { { 0, 1 }, { 1, 2 }, { 1, 3 } };
        System.out.println("Biconnected components 4: " + fbc.findBiconnectedComponents(4, edges4));
        // Output: [[0, 1], [1, 2], [1, 3]]

        // Test case 5: Disconnected graph
        int[][] edges5 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 3, 4 } };
        System.out.println("Biconnected components 5: " + fbc.findBiconnectedComponents(5, edges5));
        // Output: [[0, 1, 2], [3, 4]]
    }
}
