package graphs.hard;

import java.util.*;

/**
 * LeetCode 787: Cheapest Flights Within K Stops (Bellman-Ford variation)
 * https://leetcode.com/problems/cheapest-flights-within-k-stops/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 7+ interviews)
 *
 * Description: Find shortest path with negative weights, detect negative
 * cycles.
 *
 * Constraints:
 * - 1 <= n <= 100
 * - 0 <= flights.length <= n * (n - 1) / 2
 * - -10^4 <= price <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you use SPFA for better average performance?
 * 2. Can you handle path reconstruction?
 * 3. Can you detect and handle negative cycles?
 */
public class ShortestPathWithNegativeWeights {
    // Approach 1: Bellman-Ford Algorithm - O(VE) time, O(V) space
    public int[] bellmanFord(int n, int[][] edges, int src) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Relax edges n-1 times
        for (int i = 1; i < n; i++) {
            for (int[] e : edges) {
                if (dist[e[0]] != Integer.MAX_VALUE && dist[e[1]] > dist[e[0]] + e[2]) {
                    dist[e[1]] = dist[e[0]] + e[2];
                }
            }
        }

        // Check for negative cycles
        for (int[] e : edges) {
            if (dist[e[0]] != Integer.MAX_VALUE && dist[e[1]] > dist[e[0]] + e[2]) {
                return null; // Negative cycle detected
            }
        }
        return dist;
    }

    // Approach 2: SPFA (Shortest Path Faster Algorithm) - O(kE) average time
    public int[] spfa(int n, int[][] edges, int src) {
        List<int[]>[] adj = new List[n];
        for (int i = 0; i < n; i++)
            adj[i] = new ArrayList<>();
        for (int[] e : edges)
            adj[e[0]].add(new int[] { e[1], e[2] });

        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;
        boolean[] inQueue = new boolean[n];
        int[] count = new int[n];
        Queue<Integer> queue = new LinkedList<>();
        queue.offer(src);
        inQueue[src] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            inQueue[u] = false;
            for (int[] nei : adj[u]) {
                int v = nei[0], w = nei[1];
                if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    if (!inQueue[v]) {
                        queue.offer(v);
                        inQueue[v] = true;
                        count[v]++;
                        if (count[v] > n)
                            return null; // Negative cycle
                    }
                }
            }
        }
        return dist;
    }

    // Follow-up: Path reconstruction
    public List<Integer> reconstructPath(int n, int[][] edges, int src, int dest) {
        int[] dist = new int[n], parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[src] = 0;

        for (int i = 1; i < n; i++) {
            for (int[] e : edges) {
                if (dist[e[0]] != Integer.MAX_VALUE && dist[e[1]] > dist[e[0]] + e[2]) {
                    dist[e[1]] = dist[e[0]] + e[2];
                    parent[e[1]] = e[0];
                }
            }
        }

        LinkedList<Integer> path = new LinkedList<>();
        for (int at = dest; at != -1; at = parent[at]) {
            path.addFirst(at);
        }
        return path.getFirst() == src ? path : Collections.emptyList();
    }

    public static void main(String[] args) {
        ShortestPathWithNegativeWeights spnw = new ShortestPathWithNegativeWeights();
        // Basic case
        int[][] edges1 = { { 0, 1, 1 }, { 1, 2, -3 }, { 2, 3, 2 } };
        System.out.println(Arrays.toString(spnw.bellmanFord(4, edges1, 0))); // [0,1,-2,0]

        // Negative cycle
        int[][] edges2 = { { 0, 1, -1 }, { 1, 2, -2 }, { 2, 0, -3 } };
        System.out.println(Arrays.toString(spnw.bellmanFord(3, edges2, 0))); // null

        // SPFA test
        System.out.println(Arrays.toString(spnw.spfa(4, edges1, 0))); // [0,1,-2,0]

        // Path reconstruction
        System.out.println(spnw.reconstructPath(4, edges1, 0, 3)); // [0,1,2,3]

        // Disconnected graph
        int[][] edges3 = { { 0, 1, 1 }, { 2, 3, 1 } };
        System.out.println(Arrays.toString(spnw.bellmanFord(4, edges3, 0))); // [0,1,MAX,MAX]
    }
}
