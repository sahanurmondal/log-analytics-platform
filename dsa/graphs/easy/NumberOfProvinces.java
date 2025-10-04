package graphs.easy;

/**
 * LeetCode 547: Number of Provinces
 * https://leetcode.com/problems/number-of-provinces/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given an n x n matrix isConnected, return the number of
 * provinces (connected components).
 *
 * Constraints:
 * - 1 <= n <= 200
 * - isConnected[i][j] is 1 or 0
 * 
 * Follow-up Questions:
 * 1. Can you solve with Union-Find?
 * 2. Can you solve with DFS/BFS?
 */
public class NumberOfProvinces {
    // Approach 1: DFS - O(n^2) time, O(n) space
    public int findCircleNumDFS(int[][] isConnected) {
        int n = isConnected.length, count = 0;
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                dfs(isConnected, visited, i);
                count++;
            }
        }
        return count;
    }

    private void dfs(int[][] g, boolean[] visited, int u) {
        visited[u] = true;
        for (int v = 0; v < g.length; v++)
            if (g[u][v] == 1 && !visited[v])
                dfs(g, visited, v);
    }

    // Approach 2: Union-Find - O(n^2) time, O(n) space
    public int findCircleNumUF(int[][] isConnected) {
        int n = isConnected.length;
        int[] parent = new int[n];
        for (int i = 0; i < n; i++)
            parent[i] = i;
        for (int i = 0; i < n; i++)
            for (int j = i + 1; j < n; j++)
                if (isConnected[i][j] == 1)
                    union(parent, i, j);
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
        if (p[x] != x)
            p[x] = find(p, p[x]);
        return p[x];
    }

    // Approach 3: BFS - O(n^2) time, O(n) space
    public int findCircleNumBFS(int[][] isConnected) {
        int n = isConnected.length, count = 0;
        boolean[] visited = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {
                bfs(isConnected, visited, i);
                count++;
            }
        }
        return count;
    }

    private void bfs(int[][] g, boolean[] visited, int start) {
        java.util.Queue<Integer> queue = new java.util.LinkedList<>();
        queue.offer(start);
        visited[start] = true;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v = 0; v < g.length; v++) {
                if (g[u][v] == 1 && !visited[v]) {
                    visited[v] = true;
                    queue.offer(v);
                }
            }
        }
    }

    public static void main(String[] args) {
        NumberOfProvinces np = new NumberOfProvinces();
        // Basic cases
        int[][] g1 = { { 1, 1, 0 }, { 1, 1, 0 }, { 0, 0, 1 } };
        int[][] g2 = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };
        System.out.println(np.findCircleNumDFS(g1)); // 2
        System.out.println(np.findCircleNumUF(g2)); // 3
        System.out.println(np.findCircleNumBFS(g1)); // 2

        // Edge case: Single node
        int[][] g3 = { { 1 } };
        System.out.println(np.findCircleNumDFS(g3)); // 1
        System.out.println(np.findCircleNumUF(g3)); // 1
        System.out.println(np.findCircleNumBFS(g3)); // 1

        // Edge case: All connected
        int[][] g4 = { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } };
        System.out.println(np.findCircleNumDFS(g4)); // 1

        // Edge case: No connections except self
        int[][] g5 = { { 1, 0, 0, 0 }, { 0, 1, 0, 0 }, { 0, 0, 1, 0 }, { 0, 0, 0, 1 } };
        System.out.println(np.findCircleNumUF(g5)); // 4

        // Edge case: Large input
        int n = 100;
        int[][] g6 = new int[n][n];
        for (int i = 0; i < n; i++)
            g6[i][i] = 1;
        System.out.println(np.findCircleNumBFS(g6)); // 100
    }
}
