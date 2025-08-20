package graphs.hard;

import java.util.*;

/**
 * Find All Cycles in a Graph
 * https://en.wikipedia.org/wiki/Cycle_(graph_theory)
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 3+ interviews)
 *
 * Description: Find all simple cycles in a directed graph.
 * A simple cycle is a path that starts and ends at the same vertex, without
 * repeating edges or vertices.
 *
 * Constraints:
 * - 1 <= n <= 100
 * - 0 <= edges.length <= n^2
 * 
 * Follow-up Questions:
 * 1. Can you find all fundamental cycles in an undirected graph?
 * 2. Can you handle large graphs efficiently?
 * 3. Can you find the minimum length cycle?
 */
public class FindAllCyclesInGraph {

    // Approach 1: Johnson's Algorithm - O((V+E)(C+1)) time, O(V+E) space
    // where C is the number of cycles
    public List<List<Integer>> findAllCycles(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
        }

        List<List<Integer>> allCycles = new ArrayList<>();
        boolean[] blocked = new boolean[n];
        Set<Integer>[] B = new HashSet[n];

        for (int i = 0; i < n; i++) {
            B[i] = new HashSet<>();
        }

        for (int s = 0; s < n; s++) {
            // Reset blocked vertices and B sets for each start node
            Arrays.fill(blocked, false);
            for (int i = 0; i < n; i++) {
                B[i].clear();
            }

            findCyclesWithStart(s, s, blocked, B, new Stack<>(), allCycles, adj);
        }

        return allCycles;
    }

    private boolean findCyclesWithStart(int v, int start, boolean[] blocked,
            Set<Integer>[] B, Stack<Integer> stack,
            List<List<Integer>> allCycles, List<List<Integer>> adj) {
        boolean foundCycle = false;
        stack.push(v);
        blocked[v] = true;

        // Explore neighbors
        for (int w : adj.get(v)) {
            // Found a cycle
            if (w == start) {
                // Create a new cycle
                List<Integer> cycle = new ArrayList<>(stack);
                cycle.add(start);
                allCycles.add(cycle);
                foundCycle = true;
            }
            // Continue DFS
            else if (!blocked[w]) {
                if (findCyclesWithStart(w, start, blocked, B, stack, allCycles, adj)) {
                    foundCycle = true;
                }
            }
        }

        if (foundCycle) {
            // Unblock vertex
            unblock(v, blocked, B);
        } else {
            // No cycle found, update B sets
            for (int w : adj.get(v)) {
                B[w].add(v);
            }
        }

        stack.pop();
        return foundCycle;
    }

    private void unblock(int v, boolean[] blocked, Set<Integer>[] B) {
        blocked[v] = false;

        // Unblock all vertices that were waiting on v
        Iterator<Integer> it = B[v].iterator();
        while (it.hasNext()) {
            int w = it.next();
            it.remove();
            if (blocked[w]) {
                unblock(w, blocked, B);
            }
        }
    }

    // Approach 2: DFS Backtracking for smaller graphs - O(V! * V) time, O(V+E)
    // space
    public List<List<Integer>> findCyclesDFS(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
        }

        List<List<Integer>> allCycles = new ArrayList<>();
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            dfs(i, i, visited, new ArrayList<>(), allCycles, adj, -1);
            visited[i] = false; // Allow reusing start vertex in other cycles
        }

        return allCycles;
    }

    private void dfs(int vertex, int start, boolean[] visited, List<Integer> path,
            List<List<Integer>> allCycles, List<List<Integer>> adj, int parent) {
        // Check if we've already visited
        if (path.contains(vertex)) {
            // Found a cycle, but only add if it starts from 'start'
            if (vertex == start && path.size() > 2) {
                // Create a proper cycle starting from 'vertex'
                int startIdx = path.indexOf(vertex);
                List<Integer> cycle = new ArrayList<>(path.subList(startIdx, path.size()));
                cycle.add(vertex); // Close the cycle
                allCycles.add(cycle);
            }
            return;
        }

        // Mark as visited and add to path
        visited[vertex] = true;
        path.add(vertex);

        // Explore neighbors
        for (int neighbor : adj.get(vertex)) {
            if (!visited[neighbor] || neighbor == start) {
                dfs(neighbor, start, visited, path, allCycles, adj, vertex);
            }
        }

        // Backtrack
        visited[vertex] = false;
        path.remove(path.size() - 1);
    }

    // Follow-up 3: Find minimum length cycle - O(V^3) time using Floyd-Warshall
    public int findMinimumLengthCycle(int n, int[][] edges) {
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE / 2); // Avoid overflow
            dist[i][i] = 0;
        }

        // Initialize distances from edges
        for (int[] edge : edges) {
            dist[edge[0]][edge[1]] = 1;
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                }
            }
        }

        // Find minimum cycle length
        int minCycle = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            for (int[] edge : edges) {
                if (edge[0] != i && edge[1] != i) {
                    continue;
                }

                if (edge[0] == i) {
                    // Check for cycle: i -> edge[1] -> ... -> i
                    if (dist[edge[1]][i] < Integer.MAX_VALUE / 2) {
                        minCycle = Math.min(minCycle, 1 + dist[edge[1]][i]);
                    }
                }
            }
        }

        return minCycle == Integer.MAX_VALUE ? -1 : minCycle;
    }

    public static void main(String[] args) {
        FindAllCyclesInGraph fcg = new FindAllCyclesInGraph();

        // Test case 1: Simple directed cycle
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 0 } };
        System.out.println("All cycles 1: " + fcg.findAllCycles(3, edges1));
        // Output: [[0, 1, 2, 0]]

        // Test case 2: Multiple cycles
        int[][] edges2 = {
                { 0, 1 }, { 1, 2 }, { 2, 0 },
                { 2, 3 }, { 3, 4 }, { 4, 2 }
        };
        System.out.println("All cycles 2: " + fcg.findAllCycles(5, edges2));
        // Output: [[0, 1, 2, 0], [2, 3, 4, 2]]

        // Test case 3: No cycles
        int[][] edges3 = { { 0, 1 }, { 1, 2 }, { 2, 3 } };
        System.out.println("All cycles 3: " + fcg.findAllCycles(4, edges3));
        // Output: []

        // Test case 4: Self-loop
        int[][] edges4 = { { 0, 0 }, { 0, 1 }, { 1, 2 } };
        System.out.println("All cycles 4: " + fcg.findAllCycles(3, edges4));
        // Output: [[0, 0]]

        // Test case 5: Find minimum cycle length
        int[][] edges5 = {
                { 0, 1 }, { 1, 2 }, { 2, 0 },
                { 2, 3 }, { 3, 4 }, { 4, 2 }
        };
        System.out.println("Min cycle length: " + fcg.findMinimumLengthCycle(5, edges5));
        // Output: 3

        // Test with DFS approach for small graph
        System.out.println("DFS cycles: " + fcg.findCyclesDFS(3, edges1));
    }
}
