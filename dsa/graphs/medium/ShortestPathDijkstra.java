package graphs.medium;

import java.util.*;

/**
 * LeetCode 743: Network Delay Time (Dijkstra)
 * https://leetcode.com/problems/network-delay-time/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Find shortest time for all nodes to receive signal from a
 * source.
 *
 * Constraints:
 * - 1 <= n <= 100
 * - 1 <= times.length <= 6000
 * - times[i] = [u, v, w]
 * 
 * Follow-up Questions:
 * 1. Can you solve with Bellman-Ford?
 * 2. Can you handle negative weights?
 */
public class ShortestPathDijkstra {
    // Approach 1: Dijkstra's Algorithm - O(E log V) time
    public int networkDelayTime(int[][] times, int n, int k) {
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++)
            adj.add(new ArrayList<>());
        for (int[] t : times)
            adj.get(t[0] - 1).add(new int[] { t[1] - 1, t[2] });
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k - 1] = 0;
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { k - 1, 0 });
        while (!pq.isEmpty()) {
            int[] cur = pq.poll();
            int u = cur[0], d = cur[1];
            if (d > dist[u])
                continue;
            for (int[] nei : adj.get(u)) {
                int v = nei[0], w = nei[1];
                if (dist[v] > d + w) {
                    dist[v] = d + w;
                    pq.offer(new int[] { v, dist[v] });
                }
            }
        }
        int max = Arrays.stream(dist).max().getAsInt();
        return max == Integer.MAX_VALUE ? -1 : max;
    }

    // Approach 2: Bellman-Ford - O(n*E) time, O(n) space
    public int networkDelayTimeBellmanFord(int[][] times, int n, int k) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k - 1] = 0;
        for (int i = 1; i < n; i++) {
            boolean updated = false;
            for (int[] t : times) {
                int u = t[0] - 1, v = t[1] - 1, w = t[2];
                if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                    updated = true;
                }
            }
            if (!updated)
                break;
        }
        int max = Arrays.stream(dist).max().getAsInt();
        return max == Integer.MAX_VALUE ? -1 : max;
    }

    public static void main(String[] args) {
        ShortestPathDijkstra spd = new ShortestPathDijkstra();
        System.out.println(spd.networkDelayTime(new int[][] { { 2, 1, 1 }, { 2, 3, 1 }, { 3, 4, 1 } }, 4, 2) == 2);
        System.out.println(spd.networkDelayTime(new int[][] { { 1, 2, 1 } }, 3, 1) == -1);
        System.out.println(spd.networkDelayTime(new int[][] {}, 1, 1) == 0);
        System.out.println(spd.networkDelayTime(new int[][] { { 1, 2, 1 }, { 1, 3, 4 }, { 2, 3, 1 } }, 3, 1) == 2);

        // Bellman-Ford tests
        System.out.println(
                spd.networkDelayTimeBellmanFord(new int[][] { { 2, 1, 1 }, { 2, 3, 1 }, { 3, 4, 1 } }, 4, 2) == 2);
        System.out.println(spd.networkDelayTimeBellmanFord(new int[][] { { 1, 2, 1 } }, 3, 1) == -1);
        System.out.println(spd.networkDelayTimeBellmanFord(new int[][] {}, 1, 1) == 0);
        System.out.println(
                spd.networkDelayTimeBellmanFord(new int[][] { { 1, 2, 1 }, { 1, 3, 4 }, { 2, 3, 1 } }, 3, 1) == 2);

        // Negative weights (should work, but no negative cycles in this problem)
        System.out.println(spd.networkDelayTimeBellmanFord(new int[][] { { 1, 2, -1 }, { 2, 3, 2 } }, 3, 1) == 1);

        // All nodes unreachable except source
        System.out.println(spd.networkDelayTimeBellmanFord(new int[][] { { 1, 2, 1 } }, 3, 3) == 0);

        // Multiple edges between same nodes
        System.out.println(
                spd.networkDelayTimeBellmanFord(new int[][] { { 1, 2, 5 }, { 1, 2, 1 }, { 2, 3, 1 } }, 3, 1) == 2);

        // Large graph
        int n = 100;
        List<int[]> edges = new ArrayList<>();
        for (int i = 1; i < n; i++)
            edges.add(new int[] { i, i + 1, 1 });
        System.out.println(spd.networkDelayTimeBellmanFord(edges.toArray(new int[0][]), n, 1) == n - 1);
    }
}
