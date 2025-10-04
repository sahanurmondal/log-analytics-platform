package graphs.medium;
import java.util.*;
/**
 * LeetCode 787: Cheapest Flights Within K Stops (Bellman-Ford variation)
 * https://leetcode.com/problems/cheapest-flights-within-k-stops/
 *
 * Description:
 * Given a weighted directed graph, return the shortest path from source to all
 * nodes. Detect negative cycles.
 *
 * Constraints:
 * - 1 <= n <= 1000
 * - 0 <= edges.length <= n * n
 * - -10^6 <= weight <= 10^6
 *
 * Follow-up Questions:
 * 1. Can you solve it with edge relaxation?
 * 2. Can you solve it with queue-based Bellman-Ford (SPFA)?
 * 3. Can you reconstruct the shortest path?
 */
public class BellmanFordShortestPath {
    // Approach 1: Standard Bellman-Ford with edge relaxation - O(n*E) time, O(n)
    // space
    public int[] bellmanFord(int n, int[][] edges, int source) {
        int[] dist = new int[n];
        java.util.Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        // Relax all edges n-1 times
        for (int i = 1; i < n; i++) {
            boolean updated = false;
            for (int[] e : edges) {
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    updated = true;
                }
            }
            if (!updated)
                break; // Early stop if no update
        }
        // Detect negative cycle
        for (int[] e : edges) {
            int u = e[0], v = e[1], w = e[2];
            if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + w) {
                return null; // Negative cycle detected
            }
        }
        return dist;
    }

    // Approach 2: Queue-based Bellman-Ford (SPFA) - O(n*E) worst-case, faster in
    // practice
    public int[] bellmanFordSPFA(int n, int[][] edges, int source) {
        java.util.List<int[]>[] adj = new java.util.ArrayList[n];
        for (int i = 0; i < n; i++)
            adj[i] = new java.util.ArrayList<>();
        for (int[] e : edges)
            adj[e[0]].add(new int[] { e[1], e[2] });
        int[] dist = new int[n];
        java.util.Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;
        boolean[] inQueue = new boolean[n];
        int[] count = new int[n];
        java.util.Queue<Integer> q = new java.util.LinkedList<>();
        q.offer(source);
        inQueue[source] = true;
        while (!q.isEmpty()) {
            int u = q.poll();
            inQueue[u] = false;
            for (int[] nei : adj[u]) {
                int v = nei[0], w = nei[1];
                if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    if (!inQueue[v]) {
                        q.offer(v);
                        inQueue[v] = true;
                        count[v]++;
                        if (count[v] > n)
                            return null; // Negative cycle detected
                    }
                }
            }
        }
        return dist;
    }

    // Follow-up: Reconstruct shortest path from source to target
    public java.util.List<Integer> getPath(int n, int[][] edges, int source, int target) {
        int[] dist = new int[n];
        int[] prev = new int[n];
        java.util.Arrays.fill(dist, Integer.MAX_VALUE);
        java.util.Arrays.fill(prev, -1);
        dist[source] = 0;
        for (int i = 1; i < n; i++) {
            for (int[] e : edges) {
                int u = e[0], v = e[1], w = e[2];
                if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    prev[v] = u;
                }
            }
        }
        // Negative cycle check omitted for brevity
        java.util.LinkedList<Integer> path = new java.util.LinkedList<>();
        for (int at = target; at != -1; at = prev[at])
            path.addFirst(at);
        if (path.getFirst() != source)
            return java.util.Collections.emptyList();
        return path;
    }

    public static void main(String[] args) {
        BellmanFordShortestPath solution = new BellmanFordShortestPath();
        // Edge Case 1: Normal case
        System.out.println(java.util.Arrays.toString(solution.bellmanFord(5,
                new int[][] { { 0, 1, 2 }, { 0, 2, 4 }, { 1, 2, 1 }, { 2, 3, 1 }, { 3, 4, 3 } }, 0)).equals("[0, 2, 3, 4, 7]"));
        // Edge Case 2: Negative cycle
        System.out.println(solution.bellmanFord(3, new int[][] { { 0, 1, -1 }, { 1, 2, -1 }, { 2, 0, -1 } }, 0) == null);
        // Edge Case 3: Single node
        System.out.println(java.util.Arrays.toString(solution.bellmanFord(1, new int[][] {}, 0)).equals("[0]"));
        // SPFA test
        System.out.println(java.util.Arrays.toString(solution.bellmanFordSPFA(5,
                new int[][] { { 0, 1, 2 }, { 0, 2, 4 }, { 1, 2, 1 }, { 2, 3, 1 }, { 3, 4, 3 } }, 0)).equals("[0, 2, 3, 4, 7]"));
        // Path reconstruction test
        System.out.println(solution.getPath(5,
                new int[][] { { 0, 1, 2 }, { 0, 2, 4 }, { 1, 2, 1 }, { 2, 3, 1 }, { 3, 4, 3 } }, 0, 4).equals(java.util.Arrays.asList(0,1,2,3,4)));

        // Unreachable nodes
        System.out.println(java.util.Arrays.toString(solution.bellmanFord(3, new int[][]{{0,1,1}}, 0)).equals("[0, 1, 2147483647]"));

        // Multiple edges between same nodes
        System.out.println(java.util.Arrays.toString(solution.bellmanFord(3, new int[][]{{0,1,5},{0,1,1},{1,2,1}}, 0)).equals("[0, 1, 2]"));

        // Large graph
        int n = 100;
        List<int[]> edges = new ArrayList<>();
        for (int i = 0; i < n-1; i++) edges.add(new int[]{i, i+1, 1});
        System.out.println(java.util.Arrays.toString(solution.bellmanFord(n, edges.toArray(new int[0][]), 0)).startsWith("[0, 1, 2"));
    }
}

