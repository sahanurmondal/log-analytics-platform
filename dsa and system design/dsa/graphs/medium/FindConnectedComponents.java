package graphs.medium;

/**
 * LeetCode 323: Number of Connected Components in an Undirected Graph
 * https://leetcode.com/problems/number-of-connected-components-in-an-undirected-graph/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given n nodes and edges, return number of connected components.
 *
 * Constraints:
 * - 1 <= n <= 2000
 * - 0 <= edges.length <= n*(n-1)/2
 * 
 * Follow-up Questions:
 * 1. Can you solve with Union-Find?
 * 2. Can you solve with DFS/BFS?
 */
public class FindConnectedComponents {
    // Approach 1: DFS - O(n+e) time, O(n) space
    public int countComponentsDFS(int n, int[][] edges) {
        java.util.List<Integer>[] adj = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new java.util.ArrayList<>();
        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
        }
        boolean[] visited = new boolean[n];
        int count = 0;
        for (int i = 0; i < n; i++)
            if (!visited[i]) {
                dfs(adj, visited, i);
                count++;
            }
        return count;
    }

    private void dfs(java.util.List<Integer>[] adj, boolean[] visited, int u) {
        visited[u] = true;
        for (int v : adj[u])
            if (!visited[v])
                dfs(adj, visited, v);
    }

    // Approach 2: Union-Find - O(n+e) time, O(n) space
    public int countComponentsUF(int n, int[][] edges) {
        int[] parent = new int[n];
        for (int i = 0; i < n; i++)
            parent[i] = i;
        for (int[] e : edges)
            union(parent, e[0], e[1]);
        int count = 0;
        for (int i = 0; i < n; i++)
            if (parent[i] == i)
                count++;
        return count;
    }

    private void union(int[] p, int x, int y) {
        p[find(p, x)] = find(p, y);
    }

    private int find(int[] p, int x) {
        return p[x] == x ? x : (p[x] = find(p, p[x]));
    }

    // Approach 3: BFS - O(n+e) time, O(n) space
    public int countComponentsBFS(int n, int[][] edges) {
        java.util.List<Integer>[] adj = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new java.util.ArrayList<>();
        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
        }
        boolean[] visited = new boolean[n];
        int count = 0;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                bfs(adj, visited, i);
                count++;
            }
        }
        return count;
    }

    private void bfs(java.util.List<Integer>[] adj, boolean[] visited, int start) {
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        queue.offer(start);
        visited[start] = true;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adj[u]) {
                if (!visited[v]) {
                    visited[v] = true;
                    queue.offer(v);
                }
            }
        }
    }

    // Default method for main (calls DFS)
    public int countComponents(int n, int[][] edges) {
        return countComponentsDFS(n, edges);
    }

    public static void main(String[] args) {
        FindConnectedComponents fcc = new FindConnectedComponents();
        System.out.println(fcc.countComponents(5, new int[][] { { 0, 1 }, { 1, 2 }, { 3, 4 } }) == 2);

        // All connected
        System.out.println(fcc.countComponents(4, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 } }) == 1);

        // No edges
        System.out.println(fcc.countComponents(3, new int[][] {}) == 3);

        // Disconnected nodes
        System.out.println(fcc.countComponents(4, new int[][] { { 0, 1 } }) == 3);

        // Single node
        System.out.println(fcc.countComponents(1, new int[][] {}) == 1);

        // Large graph
        int n = 100;
        int[][] edges = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++)
            edges[i] = new int[] { i, i + 1 };
        System.out.println(fcc.countComponents(n, edges) == 1);

        // All disconnected
        int[][] edges2 = {};
        System.out.println(fcc.countComponents(10, edges2) == 10);

        // BFS approach
        System.out.println(fcc.countComponentsBFS(5, new int[][] { { 0, 1 }, { 1, 2 }, { 3, 4 } }) == 2);
        // Union-Find approach
        System.out.println(fcc.countComponentsUF(4, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 } }) == 1);
        System.out.println(fcc.countComponentsUF(3, new int[][] {}) == 3);
        System.out.println(fcc.countComponentsBFS(4, new int[][] { { 0, 1 } }) == 3);
        System.out.println(fcc.countComponentsUF(1, new int[][] {}) == 1);
    }
}
