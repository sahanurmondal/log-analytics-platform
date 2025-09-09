package graphs.hard;

import java.util.*;

/**
 * LeetCode 980 variant: Hamiltonian Path (All Paths)
 * https://en.wikipedia.org/wiki/Hamiltonian_path
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Low (Asked in 2+ interviews)
 *
 * Description: Find a Hamiltonian Path in a graph - a path that visits each
 * vertex exactly once.
 *
 * Constraints:
 * - 1 <= n <= 12 (due to exponential complexity)
 * - 0 <= edges.length <= n*(n-1)/2
 * 
 * Follow-up Questions:
 * 1. Can you find all possible Hamiltonian paths?
 * 2. Can you determine if a Hamiltonian cycle exists?
 * 3. How would you solve the Traveling Salesman Problem?
 */
public class FindHamiltonianPath {

    // Approach 1: Backtracking - O(n!) time, O(n+e) space
    public List<Integer> findHamiltonianPath(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]); // Undirected graph
        }

        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();

        // Try starting from each vertex
        for (int i = 0; i < n; i++) {
            if (backtrack(i, n, adj, visited, path)) {
                return path;
            }
            // Reset for next starting point
            visited[i] = false;
            path.clear();
        }

        return Collections.emptyList(); // No Hamiltonian path found
    }

    private boolean backtrack(int vertex, int n, List<List<Integer>> adj, boolean[] visited, List<Integer> path) {
        // Add current vertex to path
        path.add(vertex);
        visited[vertex] = true;

        // If all vertices are visited, we found a Hamiltonian path
        if (path.size() == n) {
            return true;
        }

        // Try all unvisited neighbors
        for (int neighbor : adj.get(vertex)) {
            if (!visited[neighbor]) {
                if (backtrack(neighbor, n, adj, visited, path)) {
                    return true;
                }
            }
        }

        // If no path found, backtrack
        visited[vertex] = false;
        path.remove(path.size() - 1);
        return false;
    }

    // Approach 2: Dynamic Programming (for small graphs) - O(n^2 * 2^n) time, O(n *
    // 2^n) space
    public List<Integer> findHamiltonianPathDP(int n, int[][] edges) {
        // Build adjacency matrix for easier lookup
        boolean[][] adj = new boolean[n][n];
        for (int[] edge : edges) {
            adj[edge[0]][edge[1]] = true;
            adj[edge[1]][edge[0]] = true;
        }

        // dp[mask][i] = can we visit subset of vertices in mask ending at vertex i
        boolean[][] dp = new boolean[1 << n][n];
        // parent[mask][i] = previous vertex on path to (mask, i)
        int[][] parent = new int[1 << n][n];

        // Base cases: single vertex
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = true;
        }

        // Fill DP table
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int end = 0; end < n; end++) {
                if ((mask & (1 << end)) == 0)
                    continue;

                int prevMask = mask ^ (1 << end);
                if (prevMask == 0)
                    continue;

                for (int prev = 0; prev < n; prev++) {
                    if ((prevMask & (1 << prev)) == 0)
                        continue;
                    if (!adj[prev][end])
                        continue;

                    if (dp[prevMask][prev]) {
                        dp[mask][end] = true;
                        parent[mask][end] = prev;
                        break;
                    }
                }
            }
        }

        // Check if a Hamiltonian path exists
        int finalMask = (1 << n) - 1;
        int lastVertex = -1;
        for (int i = 0; i < n; i++) {
            if (dp[finalMask][i]) {
                lastVertex = i;
                break;
            }
        }

        if (lastVertex == -1) {
            return Collections.emptyList();
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        int currMask = finalMask;
        int currVertex = lastVertex;

        while (path.size() < n) {
            path.add(currVertex);
            int nextMask = currMask ^ (1 << currVertex);
            currVertex = parent[currMask][currVertex];
            currMask = nextMask;
        }

        Collections.reverse(path);
        return path;
    }

    // Follow-up 1: Find all Hamiltonian paths
    public List<List<Integer>> findAllHamiltonianPaths(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        List<List<Integer>> allPaths = new ArrayList<>();
        boolean[] visited = new boolean[n];

        for (int i = 0; i < n; i++) {
            backtrackAll(i, n, adj, visited, new ArrayList<>(), allPaths);
        }

        return allPaths;
    }

    private void backtrackAll(int vertex, int n, List<List<Integer>> adj, boolean[] visited,
            List<Integer> path, List<List<Integer>> allPaths) {
        path.add(vertex);
        visited[vertex] = true;

        if (path.size() == n) {
            allPaths.add(new ArrayList<>(path));
        } else {
            for (int neighbor : adj.get(vertex)) {
                if (!visited[neighbor]) {
                    backtrackAll(neighbor, n, adj, visited, path, allPaths);
                }
            }
        }

        visited[vertex] = false;
        path.remove(path.size() - 1);
    }

    // Follow-up 2: Check if a Hamiltonian cycle exists
    public boolean hasHamiltonianCycle(int n, int[][] edges) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        boolean[] visited = new boolean[n];
        List<Integer> path = new ArrayList<>();

        // Start from vertex 0
        path.add(0);
        visited[0] = true;

        if (backtrackCycle(0, 0, 1, n, adj, visited, path)) {
            return true;
        }

        return false;
    }

    private boolean backtrackCycle(int start, int current, int count, int n,
            List<List<Integer>> adj, boolean[] visited, List<Integer> path) {
        // If all vertices are visited and there's an edge back to start
        if (count == n) {
            for (int neighbor : adj.get(current)) {
                if (neighbor == start) {
                    return true;
                }
            }
            return false;
        }

        // Try all unvisited neighbors
        for (int neighbor : adj.get(current)) {
            if (!visited[neighbor]) {
                visited[neighbor] = true;
                path.add(neighbor);

                if (backtrackCycle(start, neighbor, count + 1, n, adj, visited, path)) {
                    return true;
                }

                visited[neighbor] = false;
                path.remove(path.size() - 1);
            }
        }

        return false;
    }

    public static void main(String[] args) {
        FindHamiltonianPath fhp = new FindHamiltonianPath();

        // Test case 1: Simple path
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println("Hamiltonian path 1: " + fhp.findHamiltonianPath(5, edges1));
        // Output: [0, 1, 2, 3, 4] or [4, 3, 2, 1, 0]

        // Test case 2: Complete graph (many paths)
        int[][] edges2 = { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 2 }, { 1, 3 }, { 2, 3 } };
        System.out.println("Hamiltonian path 2: " + fhp.findHamiltonianPath(4, edges2));
        // Output: Any permutation

        // Test case 3: No Hamiltonian path
        int[][] edges3 = { { 0, 1 }, { 0, 2 }, { 3, 4 } };
        System.out.println("Hamiltonian path 3: " + fhp.findHamiltonianPath(5, edges3));
        // Output: []

        // Test case 4: Small graph with all paths
        int[][] edges4 = { { 0, 1 }, { 0, 2 }, { 1, 2 } };
        System.out.println("All Hamiltonian paths: " + fhp.findAllHamiltonianPaths(3, edges4));
        // Output: Multiple paths

        // Test case 5: Hamiltonian cycle
        int[][] edges5 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 0 } };
        System.out.println("Has Hamiltonian cycle: " + fhp.hasHamiltonianCycle(4, edges5));
        // Output: true

        // Test case 6: No Hamiltonian cycle
        int[][] edges6 = { { 0, 1 }, { 1, 2 }, { 2, 3 } };
        System.out.println("Has Hamiltonian cycle: " + fhp.hasHamiltonianCycle(4, edges6));
        // Output: false
    }
}
