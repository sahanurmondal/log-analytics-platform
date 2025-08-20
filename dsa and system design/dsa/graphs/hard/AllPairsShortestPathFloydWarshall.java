package graphs.hard;

import java.util.*;
/**
 * LeetCode [N/A]: All Pairs Shortest Path (Floyd-Warshall)
 * https://en.wikipedia.org/wiki/Floyd%E2%80%93Warshall_algorithm
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given a weighted directed graph, find shortest paths between all
 * pairs of nodes.
 *
 * Constraints:
 * - 1 <= n <= 500
 * - 0 <= edges.length <= n*n
 * - -10^9 <= edge weights <= 10^9
 * 
 * Follow-up Questions:
 * 1. Can you detect negative cycles?
 * 2. Can you optimize for sparse graphs?
 */
public class AllPairsShortestPathFloydWarshall {
    // Approach 1: Floyd-Warshall - O(n^3) time, O(n^2) space
    public int[][] floydWarshall(int n, int[][] edges) {
        final int INF = (int) 1e9;
        int[][] dist = new int[n][n];
        // Initialize distances
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = (i == j) ? 0 : INF;
            }
        }
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            dist[u][v] = Math.min(dist[u][v], w);
        }
        // Floyd-Warshall main loop
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] < INF && dist[k][j] < INF) {
                        dist[i][j] = Math.min(dist[i][j], dist[i][k] + dist[k][j]);
                    }
                }
            }
        }
        // Detect negative cycles
        for (int i = 0; i < n; i++) {
            if (dist[i][i] < 0) {
                // Mark all distances as -INF if negative cycle detected
                for (int x = 0; x < n; x++)
                    for (int y = 0; y < n; y++)
                        dist[x][y] = Integer.MIN_VALUE;
                break;
            }
        }
        // Replace INF with -1 for unreachable
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                if (dist[i][j] >= INF)
                    dist[i][j] = -1;
        return dist;
    }

    public static void main(String[] args) {
        AllPairsShortestPathFloydWarshall fw = new AllPairsShortestPathFloydWarshall();
        System.out.println(java.util.Arrays
                .deepToString(fw.floydWarshall(3, new int[][] { { 0, 1, 1 }, { 1, 2, 2 }, { 2, 0, 3 } })));
        // Edge Case: Negative cycle
        System.out.println(java.util.Arrays
                .deepToString(fw.floydWarshall(3, new int[][] { { 0, 1, -1 }, { 1, 2, -2 }, { 2, 0, -3 } })));
        // Edge Case: Disconnected graph
        System.out.println(java.util.Arrays.deepToString(fw.floydWarshall(3, new int[][] {})));

        // Single node
        System.out.println(java.util.Arrays.deepToString(fw.floydWarshall(1, new int[][] {})));

        // Multiple edges between same nodes
        System.out.println(java.util.Arrays
                .deepToString(fw.floydWarshall(3, new int[][] { { 0, 1, 5 }, { 0, 1, 1 }, { 1, 2, 1 }, { 2, 0, 2 } })));

        // Unreachable nodes
        System.out
                .println(java.util.Arrays.deepToString(fw.floydWarshall(4, new int[][] { { 0, 1, 1 }, { 2, 3, 1 } })));

        // Large graph
        int n = 50;
        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < n - 1; i++)
            edges.add(new int[] { i, i + 1, 1 });
        System.out.println(java.util.Arrays.deepToString(fw.floydWarshall(n, edges.toArray(new int[0][]))));
    }
}
