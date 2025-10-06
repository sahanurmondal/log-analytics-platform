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
public class CheapestFlightsWithinKStops {
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

    /**
     * LeetCode 787: Cheapest Flights Within K Stops
     * Use Bellman-Ford variant: perform K+1 relaxation rounds (paths with up to K+1 edges)
     * We use a temp copy each round so we only consider paths with at most the current
     * number of edges (prevents reusing updated distances within the same iteration).
     *
     * Time: O(min(K+1, n-1) * (E + N)), Space: O(N)
     */
    public int cheapestFlightsWithinKStops(int n, int[][] flights, int src, int dst, int K) {
        final int INF = Integer.MAX_VALUE / 4;
        int[] dist = new int[n];
        Arrays.fill(dist, INF);
        dist[src] = 0;

        // If K is large enough to allow any simple path, cap to n-1 edges
        int rounds = Math.min(K + 1, Math.max(0, n - 1));

        int[] next = new int[n];
        for (int iter = 0; iter < rounds; iter++) {
            // copy current distances into next (avoid allocation each loop)
            System.arraycopy(dist, 0, next, 0, n);

            boolean changed = false;
            for (int[] f : flights) {
                int u = f[0], v = f[1], w = f[2];
                if (dist[u] < INF && dist[u] + w < next[v]) {
                    next[v] = dist[u] + w;
                    changed = true;
                }
            }

            // swap arrays
            int[] tmp = dist;
            dist = next;
            next = tmp;

            if (!changed) break; // early exit if no improvement
        }
        return dist[dst] >= INF ? -1 : dist[dst];
    }

    /**
     * Simplified Dijkstra variant with stops tracking.
     * State: (cost, node, stops). Process in cost order.
     * Key insight: We can reach same node multiple times with different stops,
     * but only process if this gives us a better cost OR uses fewer stops.
     *
     * Time: O(E * K * log(E*K)), Space: O(N + E*K) for PQ
     * Simpler to code and understand than 2D DP version.
     */
    public int cheapestFlightsWithinKStopsDijkstra(int n, int[][] flights, int src, int dst, int K) {
        // Build adjacency list
        List<int[]>[] adj = new ArrayList[n];
        for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        for (int[] f : flights) {
            adj[f[0]].add(new int[] { f[1], f[2] }); // [dest, cost]
        }

        // PQ: [cost, node, stops] - sort by cost (cheapest first)
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[0] - b[0]);
        pq.offer(new int[] { 0, src, 0 }); // cost=0, at src, 0 stops used

        // Track minimum cost to reach each node (for pruning)
        int[] minCost = new int[n];
        Arrays.fill(minCost, Integer.MAX_VALUE);
        minCost[src] = 0;

        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int cost = cur[0], node = cur[1], stops = cur[2];

            // Found destination - return immediately (guaranteed cheapest due to PQ)
            if (node == dst) return cost;

            // Can't take more flights (already used K stops)
            if (stops > K) continue;

            // Explore neighbors
            for (int[] nei : adj[node]) {
                int nextNode = nei[0], price = nei[1];
                int newCost = cost + price;

                // Only add to queue if this path is cheaper than previous best
                // This pruning keeps queue size manageable
                if (newCost < minCost[nextNode]) {
                    minCost[nextNode] = newCost;
                    pq.offer(new int[] { newCost, nextNode, stops + 1 });
                }
            }
        }

        return -1; // Destination not reachable within K stops
    }

    public static void main(String[] args) {
        CheapestFlightsWithinKStops solution = new CheapestFlightsWithinKStops();
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

        // Cheapest Flights (Bellman-Ford variant) quick tests
        int[][] flights1 = { {0,1,100}, {1,2,100}, {0,2,500} };
        System.out.println(solution.cheapestFlightsWithinKStops(3, flights1, 0, 2, 1) == 200); // via 1 within 1 stop
        System.out.println(solution.cheapestFlightsWithinKStops(3, flights1, 0, 2, 0) == 500); // direct only
        // Dijkstra variant tests
        System.out.println(solution.cheapestFlightsWithinKStopsDijkstra(3, flights1, 0, 2, 1) == 200);
        System.out.println(solution.cheapestFlightsWithinKStopsDijkstra(3, flights1, 0, 2, 0) == 500);

        int[][] flights2 = { {0,1,1}, {1,2,1}, {0,2,5}, {2,3,1}, {1,3,6} };
        System.out.println(solution.cheapestFlightsWithinKStops(4, flights2, 0, 3, 2) == 3); // 0->1->2->3 cost 3
        System.out.println(solution.cheapestFlightsWithinKStopsDijkstra(4, flights2, 0, 3, 2) == 3);
    }
}
