package design.medium;

import java.util.*;

/**
 * LeetCode 2642: Design Graph With Shortest Path Calculator
 * https://leetcode.com/problems/design-graph-with-shortest-path-calculator/
 *
 * Description: There is a directed weighted graph that consists of n nodes
 * numbered from 0 to n - 1.
 * The edges of the graph are given as a 2D integer array edges.
 * 
 * Constraints:
 * - 1 <= n <= 100
 * - 0 <= edges.length <= n * (n - 1)
 * - edges[i].length == 3
 * - 0 <= from_i, to_i <= n - 1
 * - from_i != to_i
 * - 1 <= edgeCost_i <= 10^6
 * - At most 100 calls will be made for addEdge and shortestPath in total
 *
 * Follow-up:
 * - Can you optimize for frequent shortest path queries?
 * 
 * Time Complexity: O(n^2) for constructor, O(n^2) for shortestPath, O(1) for
 * addEdge
 * Space Complexity: O(n^2)
 * 
 * Company Tags: Google, Facebook
 */
public class DesignGraph {

    private int[][] dist;
    private int n;

    public DesignGraph(int n, int[][] edges) {
        this.n = n;
        this.dist = new int[n][n];

        // Initialize distances
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], Integer.MAX_VALUE);
            dist[i][i] = 0;
        }

        // Add initial edges
        for (int[] edge : edges) {
            dist[edge[0]][edge[1]] = edge[2];
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][k] != Integer.MAX_VALUE &&
                            dist[k][j] != Integer.MAX_VALUE &&
                            dist[i][k] + dist[k][j] < dist[i][j]) {
                        dist[i][j] = dist[i][k] + dist[k][j];
                    }
                }
            }
        }
    }

    public void addEdge(int[] edge) {
        int from = edge[0], to = edge[1], cost = edge[2];

        // Update direct edge
        if (cost < dist[from][to]) {
            dist[from][to] = cost;

            // Update all pairs that might benefit from this new edge
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (dist[i][from] != Integer.MAX_VALUE &&
                            dist[to][j] != Integer.MAX_VALUE &&
                            dist[i][from] + cost + dist[to][j] < dist[i][j]) {
                        dist[i][j] = dist[i][from] + cost + dist[to][j];
                    }
                }
            }
        }
    }

    public int shortestPath(int node1, int node2) {
        return dist[node1][node2] == Integer.MAX_VALUE ? -1 : dist[node1][node2];
    }

    public static void main(String[] args) {
        DesignGraph g = new DesignGraph(4, new int[][] { { 0, 2, 5 }, { 0, 1, 2 }, { 1, 2, 1 }, { 3, 0, 3 } });
        System.out.println(g.shortestPath(3, 2)); // Expected: 6
        System.out.println(g.shortestPath(0, 3)); // Expected: -1
        g.addEdge(new int[] { 1, 3, 4 });
        System.out.println(g.shortestPath(0, 3)); // Expected: 6
    }
}
