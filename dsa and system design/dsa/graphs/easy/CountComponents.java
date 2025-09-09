package graphs.easy;

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
public class CountComponents {
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

    public static void main(String[] args) {
        CountComponents cc = new CountComponents();
        System.out.println(cc.countComponentsDFS(5, new int[][] { { 0, 1 }, { 1, 2 }, { 3, 4 } }) == 2);
        System.out.println(cc.countComponentsUF(5, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 } }) == 1);
        System.out.println(cc.countComponentsDFS(1, new int[][] {}) == 1);

        // Disconnected nodes
        System.out.println(cc.countComponentsUF(4, new int[][] { { 0, 1 } }) == 3);

        // All connected
        System.out.println(cc.countComponentsDFS(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } }) == 1);

        // No edges
        System.out.println(cc.countComponentsUF(3, new int[][] {}) == 3);
    }
}
