package graphs.medium;

import java.util.*;

/**
 * LeetCode 1135: Connecting Cities With Minimum Cost (Kruskal)
 * https://leetcode.com/problems/connecting-cities-with-minimum-cost/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Find minimum cost to connect all cities (MST).
 *
 * Constraints:
 * - 1 <= n <= 10000
 * - 1 <= connections.length <= 10000
 * 
 * Follow-up Questions:
 * 1. Can you solve with Prim's algorithm?
 * 2. Can you handle disconnected graphs?
 */
public class MinimumSpanningTreeKruskal {
    // Approach 1: Kruskal's Algorithm - O(E log E) time
    public int minimumCost(int n, int[][] connections) {
        Arrays.sort(connections, Comparator.comparingInt(a -> a[2]));
        int[] parent = new int[n + 1];
        for (int i = 1; i <= n; i++)
            parent[i] = i;
        int cost = 0, edges = 0;
        for (int[] c : connections) {
            int u = find(parent, c[0]), v = find(parent, c[1]);
            if (u != v) {
                parent[u] = v;
                cost += c[2];
                edges++;
            }
        }
        return edges == n - 1 ? cost : -1;
    }

    private int find(int[] p, int x) {
        if (p[x] != x)
            p[x] = find(p, p[x]);
        return p[x];
    }

    // Approach 2: Prim's Algorithm
    // ...implement if needed...
    public static void main(String[] args) {
        MinimumSpanningTreeKruskal mst = new MinimumSpanningTreeKruskal();
        System.out.println(mst.minimumCost(3, new int[][] { { 1, 2, 5 }, { 1, 3, 6 }, { 2, 3, 1 } }) == 6);

        // Disconnected graph
        System.out.println(mst.minimumCost(4, new int[][] { { 1, 2, 1 }, { 3, 4, 1 } }) == -1);

        // Single node
        System.out.println(mst.minimumCost(1, new int[][] {}) == 0);

        // All connected
        System.out.println(mst.minimumCost(2, new int[][] { { 1, 2, 1 } }) == 1);

        // Multiple edges between same nodes
        System.out.println(mst.minimumCost(3, new int[][] { { 1, 2, 2 }, { 1, 2, 1 }, { 2, 3, 1 } }) == 2);

        // Large fully connected graph
        int n = 10;
        List<int[]> edges = new ArrayList<>();
        for (int i = 1; i <= n; i++)
            for (int j = i + 1; j <= n; j++)
                edges.add(new int[] { i, j, 1 });
        System.out.println(mst.minimumCost(n, edges.toArray(new int[0][])) == n - 1);
    }
}
