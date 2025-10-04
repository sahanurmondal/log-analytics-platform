package graphs.easy;
 import java.util.*;
 import java.util.stream.Collectors;
 import java.util.stream.IntStream;

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
        List<List<Integer>> adjList = IntStream.range(0,n)
                .mapToObj(i -> new ArrayList<Integer>())
                .collect(Collectors.toList());

        Arrays.stream(edges).forEach(edge -> {
            adjList.get(edge[0]).add(edge[1]);
            adjList.get(edge[1]).add(edge[0]);
        });
        boolean[] visited = new boolean[n];
        int count = 0;
        for (int i = 0; i < n; i++)
            if (!visited[i]) {
                dfs(adjList, visited, i);
                count++;
            }
        return count;
    }

    private void dfs(List<List<Integer>> adj, boolean[] visited, int u) {
        visited[u] = true;
        for (int v : adj.get(u))
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

    // Approach 3: UF - with rank

    public  int countComponentsUFRank(int n, int[][] edges){
        UnionFind uf = new UnionFind(n);

        for (int[] e : edges)
            uf.union(e[0], e[1]);

        return uf.countComponents(n);
    }

    public static void main(String[] args) {
        CountComponents cc = new CountComponents();
        System.out.println(cc.countComponentsDFS(5, new int[][] { { 0, 1 }, { 1, 2 }, { 3, 4 } }) == 2);
        System.out.println(cc.countComponentsUF(5, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 } }) == 1);
        System.out.println(cc.countComponentsUFRank(5, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 } }) == 1);
        System.out.println(cc.countComponentsDFS(1, new int[][] {}) == 1);

        // Disconnected nodes
        System.out.println(cc.countComponentsUF(4, new int[][] { { 0, 1 } }) == 3);

        // All connected
        System.out.println(cc.countComponentsDFS(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } }) == 1);

        // No edges
        System.out.println(cc.countComponentsUF(3, new int[][] {}) == 3);
    }
}
