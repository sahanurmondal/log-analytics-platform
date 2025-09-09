package graphs.easy;

/**
 * LeetCode 1971: Find if Path Exists in Graph
 * https://leetcode.com/problems/find-if-path-exists-in-graph/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Given an undirected graph, determine if there is a path between
 * source and destination.
 *
 * Constraints:
 * - 1 <= n <= 2 * 10^5
 * - 0 <= edges.length <= 2 * 10^5
 * 
 * Follow-up Questions:
 * 1. Can you solve with DFS/BFS?
 * 2. Can you solve with Union-Find?
 */
public class FindIfPathExists {
    // Approach 1: DFS - O(V+E) time, O(V) space
    public boolean validPathDFS(int n, int[][] edges, int source, int destination) {
        java.util.List<Integer>[] adj = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new java.util.ArrayList<>();
        for (int[] e : edges) {
            adj[e[0]].add(e[1]);
            adj[e[1]].add(e[0]);
        }
        boolean[] visited = new boolean[n];
        return dfs(adj, visited, source, destination);
    }

    private boolean dfs(java.util.List<Integer>[] adj, boolean[] visited, int u, int dest) {
        if (u == dest)
            return true;
        visited[u] = true;
        for (int v : adj[u])
            if (!visited[v] && dfs(adj, visited, v, dest))
                return true;
        return false;
    }

    // Approach 2: Union-Find - O(E*α(V)) time
    public boolean validPathUF(int n, int[][] edges, int source, int destination) {
        int[] parent = new int[n];
        for (int i = 0; i < n; i++)
            parent[i] = i;
        for (int[] e : edges)
            union(parent, e[0], e[1]);
        return find(parent, source) == find(parent, destination);
    }

    private void union(int[] p, int x, int y) {
        p[find(p, x)] = find(p, y);
    }

    private int find(int[] p, int x) {
        return p[x] == x ? x : (p[x] = find(p, p[x]));
    }

    public static void main(String[] args) {
        FindIfPathExists fpe = new FindIfPathExists();
        System.out.println(fpe.validPathDFS(3, new int[][] { { 0, 1 }, { 1, 2 } }, 0, 2)); // true
        System.out.println(fpe.validPathUF(6, new int[][] { { 0, 1 }, { 0, 2 }, { 3, 5 }, { 5, 4 }, { 4, 3 } }, 0, 5)); // false
        System.out.println(fpe.validPathDFS(1, new int[][] {}, 0, 0)); // true

        // No path
        System.out.println(fpe.validPathDFS(4, new int[][] { { 0, 1 }, { 2, 3 } }, 0, 3) == false);

        // Path exists with cycle
        System.out.println(fpe.validPathUF(4, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } }, 0, 2)); // true

        // Path to self
        System.out.println(fpe.validPathDFS(5, new int[][] { { 0, 1 }, { 1, 2 } }, 3, 3)); // true
    }
}
