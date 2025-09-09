package graphs.hard;

import java.util.*;

/**
 * LeetCode related to 802: Find Eventual Safe States
 * https://leetcode.com/problems/find-eventual-safe-states/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Find all strongly connected components (SCCs) in a directed
 * graph.
 * An SCC is a maximal subgraph where every vertex can reach every other vertex.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= edges.length <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you implement both Kosaraju's and Tarjan's algorithms?
 * 2. Can you identify sink SCCs (terminal SCCs)?
 * 3. How would you condense the graph into a DAG of SCCs?
 */
public class StronglyConnectedComponents {

    // Approach 1: Kosaraju's Algorithm - O(V+E) time, O(V+E) space
    public List<List<Integer>> findSCCsKosaraju(int n, int[][] edges) {
        // Build adjacency lists for graph and transposed graph
        List<List<Integer>> adj = new ArrayList<>();
        List<List<Integer>> transposed = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
            transposed.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            transposed.get(edge[1]).add(edge[0]);
        }

        // Step 1: DFS to find finish times (using stack)
        Stack<Integer> stack = new Stack<>();
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfsFirst(i, adj, visited, stack);
            }
        }

        // Step 2: Process vertices in order of finish times on transposed graph
        Arrays.fill(visited, false);
        List<List<Integer>> result = new ArrayList<>();

        while (!stack.isEmpty()) {
            int u = stack.pop();
            if (!visited[u]) {
                List<Integer> component = new ArrayList<>();
                dfsSecond(u, transposed, visited, component);
                result.add(component);
            }
        }

        return result;
    }

    private void dfsFirst(int u, List<List<Integer>> adj, boolean[] visited, Stack<Integer> stack) {
        visited[u] = true;

        for (int v : adj.get(u)) {
            if (!visited[v]) {
                dfsFirst(v, adj, visited, stack);
            }
        }

        stack.push(u);
    }

    private void dfsSecond(int u, List<List<Integer>> transposed, boolean[] visited, List<Integer> component) {
        visited[u] = true;
        component.add(u);

        for (int v : transposed.get(u)) {
            if (!visited[v]) {
                dfsSecond(v, transposed, visited, component);
            }
        }
    }

    // Approach 2: Tarjan's Algorithm - O(V+E) time, O(V) space
    public List<List<Integer>> findSCCsTarjan(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
        }

        int[] disc = new int[n]; // Discovery time
        int[] low = new int[n]; // Lowest reachable vertex
        boolean[] onStack = new boolean[n]; // Is vertex on stack
        Stack<Integer> stack = new Stack<>();
        Arrays.fill(disc, -1);

        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (disc[i] == -1) {
                dfs(i, disc, low, onStack, stack, adj, result);
            }
        }

        return result;
    }

    private int time = 0;

    private void dfs(int u, int[] disc, int[] low, boolean[] onStack, Stack<Integer> stack,
            List<List<Integer>> adj, List<List<Integer>> result) {
        disc[u] = low[u] = ++time;
        stack.push(u);
        onStack[u] = true;

        for (int v : adj.get(u)) {
            if (disc[v] == -1) {
                dfs(v, disc, low, onStack, stack, adj, result);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                low[u] = Math.min(low[u], disc[v]);
            }
        }

        // If u is the root of SCC
        if (low[u] == disc[u]) {
            List<Integer> component = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                component.add(v);
            } while (v != u);

            result.add(component);
        }
    }

    // Follow-up 2: Identify sink SCCs (terminal SCCs)
    public List<Integer> findSinkSCCs(int n, int[][] edges) {
        List<List<Integer>> sccs = findSCCsTarjan(n, edges);

        // Build adjacency list for graph
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
        }

        // Build a map from vertices to their SCC index
        Map<Integer, Integer> vertexToSCC = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                vertexToSCC.put(vertex, i);
            }
        }

        // Find which SCCs have edges to other SCCs
        boolean[] hasOutgoingEdges = new boolean[sccs.size()];
        for (int u = 0; u < n; u++) {
            int uSCC = vertexToSCC.get(u);
            for (int v : adj.get(u)) {
                int vSCC = vertexToSCC.get(v);
                if (uSCC != vSCC) {
                    hasOutgoingEdges[uSCC] = true;
                    break;
                }
            }
        }

        // Collect all vertices in sink SCCs
        List<Integer> sinkVertices = new ArrayList<>();
        for (int i = 0; i < sccs.size(); i++) {
            if (!hasOutgoingEdges[i]) {
                sinkVertices.addAll(sccs.get(i));
            }
        }

        return sinkVertices;
    }

    // Follow-up 3: Create condensed graph of SCCs
    public int[][] condensedGraph(int n, int[][] edges) {
        List<List<Integer>> sccs = findSCCsTarjan(n, edges);

        // Build a map from vertices to their SCC index
        Map<Integer, Integer> vertexToSCC = new HashMap<>();
        for (int i = 0; i < sccs.size(); i++) {
            for (int vertex : sccs.get(i)) {
                vertexToSCC.put(vertex, i);
            }
        }

        // Find edges between SCCs
        Set<String> condensedEdges = new HashSet<>();
        for (int[] edge : edges) {
            int fromSCC = vertexToSCC.get(edge[0]);
            int toSCC = vertexToSCC.get(edge[1]);
            if (fromSCC != toSCC) {
                condensedEdges.add(fromSCC + "," + toSCC);
            }
        }

        // Convert to edge array
        int[][] result = new int[condensedEdges.size()][2];
        int i = 0;
        for (String edge : condensedEdges) {
            String[] parts = edge.split(",");
            result[i][0] = Integer.parseInt(parts[0]);
            result[i][1] = Integer.parseInt(parts[1]);
            i++;
        }

        return result;
    }

    public static void main(String[] args) {
        StronglyConnectedComponents scc = new StronglyConnectedComponents();

        // Test case 1: Simple SCC
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 } };
        System.out.println("SCCs 1 (Kosaraju): " + scc.findSCCsKosaraju(3, edges1));
        System.out.println("SCCs 1 (Tarjan): " + scc.findSCCsTarjan(3, edges1));
        // Output: [[0, 1, 2]]

        // Test case 2: Multiple SCCs
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 0 }, { 1, 3 }, { 3, 4 }, { 4, 5 }, { 5, 3 } };
        System.out.println("SCCs 2: " + scc.findSCCsTarjan(6, edges2));
        // Output: [[0, 1, 2], [3, 4, 5]]

        // Test case 3: No cycles
        int[][] edges3 = { { 0, 1 }, { 1, 2 }, { 2, 3 } };
        System.out.println("SCCs 3: " + scc.findSCCsTarjan(4, edges3));
        // Output: [[3], [2], [1], [0]]

        // Test case 4: Sink SCCs
        System.out.println("Sink SCCs: " + scc.findSinkSCCs(6, edges2));
        // Output: [3, 4, 5]

        // Test case 5: Condensed graph
        int[][] condensed = scc.condensedGraph(6, edges2);
        System.out.println("Condensed graph edges: " + Arrays.deepToString(condensed));
        // Output: [[0, 1]]
    }
}
