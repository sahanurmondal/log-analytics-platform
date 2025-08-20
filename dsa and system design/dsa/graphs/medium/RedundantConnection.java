package graphs.medium;

/**
 * LeetCode 684: Redundant Connection
 * https://leetcode.com/problems/redundant-connection/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Given a tree with one extra edge, return the edge that can be
 * removed to restore the tree.
 *
 * Constraints:
 * - 3 <= n <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you solve with Union-Find?
 * 2. Can you solve with DFS?
 */
public class RedundantConnection {
    // Approach 1: Union-Find - O(n) time, O(n) space
    public int[] findRedundantConnection(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1];
        for (int i = 1; i <= n; i++)
            parent[i] = i;
        for (int[] e : edges) {
            int u = find(parent, e[0]), v = find(parent, e[1]);
            if (u == v)
                return e;
            parent[u] = v;
        }
        return new int[0];
    }

    private int find(int[] p, int x) {
        if (p[x] != x)
            p[x] = find(p, p[x]);
        return p[x];
    }

    // Approach 2: DFS
    // ...implement if needed...
    public static void main(String[] args) {
        RedundantConnection rc = new RedundantConnection();
        System.out.println(java.util.Arrays.toString(rc.findRedundantConnection(new int[][]{{1,2},{1,3},{2,3}})).equals("[2, 3]"));

        // Multiple cycles
        System.out.println(java.util.Arrays.toString(rc.findRedundantConnection(new int[][]{{1,2},{2,3},{3,4},{4,1},{1,5}})).equals("[4, 1]"));

        // Minimal cycle
        System.out.println(java.util.Arrays.toString(rc.findRedundantConnection(new int[][]{{1,2},{2,1}})).equals("[2, 1]"));

        // No redundant edge (should return empty)
        System.out.println(java.util.Arrays.toString(rc.findRedundantConnection(new int[][]{{1,2},{2,3}})).equals("[]"));

        // Large graph
        int n = 100;
        int[][] edges = new int[n][2];
        for (int i = 0; i < n-1; i++) edges[i] = new int[]{i+1, i+2};
        edges[n-1] = new int[]{1, n};
        System.out.println(java.util.Arrays.toString(rc.findRedundantConnection(edges)).equals("[" + 1 + ", " + n + "]"));
    }
}
