package graphs.hard;

import java.util.*;

/**
 * Minimum Cut in Flow Network
 * https://en.wikipedia.org/wiki/Minimum_cut
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Hard (Asked in 4+ interviews)
 *
 * Description: Find minimum cut that separates source from sink in a flow
 * network.
 *
 * Constraints:
 * - 1 <= n <= 10^3
 * - 0 <= edges.length <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve for undirected graphs?
 * 2. Can you handle multiple sources/sinks?
 * 3. Can you find all minimum cuts?
 */
public class FindMinimumCut {
    // Approach 1: Max Flow + Min Cut - O(VE^2) time, O(V^2) space
    public List<int[]> minCut(int n, int[][] edges, int source, int sink) {
        int[][] capacity = new int[n][n];
        for (int[] e : edges)
            capacity[e[0]][e[1]] += e[2];

        // Find max flow using Edmonds-Karp
        int[] parent = new int[n];
        while (bfs(capacity, parent, source, sink)) {
            int pathFlow = Integer.MAX_VALUE;
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                pathFlow = Math.min(pathFlow, capacity[u][v]);
            }
            for (int v = sink; v != source; v = parent[v]) {
                int u = parent[v];
                capacity[u][v] -= pathFlow;
                capacity[v][u] += pathFlow;
            }
        }

        // Find reachable nodes from source in residual graph
        boolean[] visited = new boolean[n];
        dfs(source, capacity, visited);

        List<int[]> cut = new ArrayList<>();
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                if (visited[u] && !visited[v] && hasOriginalEdge(edges, u, v)) {
                    cut.add(new int[] { u, v });
                }
            }
        }
        return cut;
    }

    private boolean bfs(int[][] capacity, int[] parent, int s, int t) {
        boolean[] visited = new boolean[capacity.length];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(s);
        visited[s] = true;
        parent[s] = -1;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < capacity.length; v++) {
                if (!visited[v] && capacity[u][v] > 0) {
                    parent[v] = u;
                    visited[v] = true;
                    if (v == t)
                        return true;
                    queue.offer(v);
                }
            }
        }
        return false;
    }

    private void dfs(int u, int[][] capacity, boolean[] visited) {
        visited[u] = true;
        for (int v = 0; v < capacity.length; v++) {
            if (capacity[u][v] > 0 && !visited[v]) {
                dfs(v, capacity, visited);
            }
        }
    }

    private boolean hasOriginalEdge(int[][] edges, int u, int v) {
        for (int[] e : edges) {
            if (e[0] == u && e[1] == v)
                return true;
        }
        return false;
    }

    // Approach 2: Stoer-Wagner for undirected graphs - O(V^3) time
    // ...existing code...

    public static void main(String[] args) {
        FindMinimumCut fmc = new FindMinimumCut();
        // Basic case
        int[][] edges1 = { { 0, 1, 10 }, { 0, 2, 10 }, { 1, 2, 2 }, { 1, 3, 4 }, { 1, 4, 8 }, { 2, 4, 9 }, { 3, 5, 10 },
                { 4, 5, 10 } };
        System.out.println(fmc.minCut(6, edges1, 0, 5)); // Should find minimum cut

        // Disconnected graph
        int[][] edges2 = { { 0, 1, 5 }, { 2, 3, 5 } };
        System.out.println(fmc.minCut(4, edges2, 0, 3)); // []

        // Single edge
        int[][] edges3 = { { 0, 1, 1 } };
        System.out.println(fmc.minCut(2, edges3, 0, 1)); // [[0,1]]
    }
}
