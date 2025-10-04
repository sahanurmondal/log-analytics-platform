package graphs.medium;

import java.util.*;

/**
 * LeetCode 261: Graph Valid Tree (Cycle Detection variation)
 * https://leetcode.com/problems/graph-valid-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 6+ interviews)
 *
 * Description: Detect if a cycle exists in an undirected graph.
 *
 * Constraints:
 * - 1 <= n <= 2000
 * - 0 <= edges.length <= 5000
 * 
 * Follow-up Questions:
 * 1. Can you implement both DFS and BFS approaches?
 * 2. Can you use Union-Find for cycle detection?
 * 3. Can you return the cycle if one exists?
 */
public class DetectCycleInUndirectedGraph {

    // Approach 1: DFS - O(V+E) time, O(V+E) space
    public boolean hasCycleDFS(int n, int[][] edges) {
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

        // DFS from each unvisited node
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                if (dfs(i, -1, adj, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean dfs(int u, int parent, List<List<Integer>> adj, boolean[] visited) {
        visited[u] = true;

        for (int v : adj.get(u)) {
            if (v == parent)
                continue;

            if (visited[v]) {
                return true; // Cycle detected
            }

            if (dfs(v, u, adj, visited)) {
                return true;
            }
        }

        return false;
    }

    // Approach 2: BFS - O(V+E) time, O(V) space
    public boolean hasCycleBFS(int n, int[][] edges) {
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

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                Queue<int[]> queue = new LinkedList<>();
                queue.offer(new int[] { i, -1 }); // (node, parent)
                visited[i] = true;

                while (!queue.isEmpty()) {
                    int[] curr = queue.poll();
                    int u = curr[0];
                    int parent = curr[1];

                    for (int v : adj.get(u)) {
                        if (v == parent)
                            continue;

                        if (visited[v]) {
                            return true; // Cycle detected
                        }

                        visited[v] = true;
                        queue.offer(new int[] { v, u });
                    }
                }
            }
        }

        return false;
    }

    // Approach 3: Union-Find - O(E * alpha(V)) time, O(V) space
    public boolean hasCycleUnionFind(int n, int[][] edges) {
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];

            int rootU = find(parent, u);
            int rootV = find(parent, v);

            if (rootU == rootV) {
                return true; // Cycle detected
            }

            union(parent, u, v);
        }

        return false;
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
        }
        return parent[x];
    }

    private void union(int[] parent, int x, int y) {
        parent[find(parent, x)] = find(parent, y);
    }

    // Follow-up 3: Return the cycle if one exists
    public List<Integer> findCycle(int n, int[][] edges) {
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
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                List<Integer> cycle = findCycleDFS(i, -1, adj, visited, parent);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Integer> findCycleDFS(int u, int p, List<List<Integer>> adj, boolean[] visited, int[] parent) {
        visited[u] = true;
        parent[u] = p;

        for (int v : adj.get(u)) {
            if (v == p)
                continue;

            if (visited[v]) {
                // Cycle detected, reconstruct it
                List<Integer> cycle = new ArrayList<>();
                int curr = u;
                while (curr != v) {
                    cycle.add(curr);
                    curr = parent[curr];
                }
                cycle.add(v);
                Collections.reverse(cycle);
                return cycle;
            }

            List<Integer> cycle = findCycleDFS(v, u, adj, visited, parent);
            if (cycle != null) {
                return cycle;
            }
        }

        return null;
    }

    public static void main(String[] args) {
        DetectCycleInUndirectedGraph dcug = new DetectCycleInUndirectedGraph();

        // Test case 1: No cycle
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 3 } };
        System.out.println("Has cycle (DFS) 1: " + dcug.hasCycleDFS(4, edges1)); // false
        System.out.println("Has cycle (BFS) 1: " + dcug.hasCycleBFS(4, edges1)); // false
        System.out.println("Has cycle (UF) 1: " + dcug.hasCycleUnionFind(4, edges1)); // false

        // Test case 2: Cycle
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 0 } };
        System.out.println("Has cycle (DFS) 2: " + dcug.hasCycleDFS(3, edges2)); // true
        System.out.println("Has cycle (BFS) 2: " + dcug.hasCycleBFS(3, edges2)); // true
        System.out.println("Has cycle (UF) 2: " + dcug.hasCycleUnionFind(3, edges2)); // true

        // Test case 3: Find cycle
        System.out.println("Found cycle: " + dcug.findCycle(3, edges2)); // [0, 1, 2]

        // Test case 4: Disconnected graph with cycle
        int[][] edges4 = { { 0, 1 }, { 2, 3 }, { 3, 4 }, { 4, 2 } };
        System.out.println("Has cycle (DFS) 4: " + dcug.hasCycleDFS(5, edges4)); // true

        // Test case 5: Disconnected graph without cycle
        int[][] edges5 = { { 0, 1 }, { 2, 3 } };
        System.out.println("Has cycle (DFS) 5: " + dcug.hasCycleDFS(4, edges5)); // false
    }
}
