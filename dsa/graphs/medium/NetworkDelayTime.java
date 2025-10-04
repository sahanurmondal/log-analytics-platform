package graphs.medium;

import java.util.*;

/**
 * LeetCode 743: Network Delay Time
 * https://leetcode.com/problems/network-delay-time/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Find the minimum time for a signal to reach all nodes from a
 * source.
 *
 * Constraints:
 * - 1 <= n <= 100
 * - 1 <= times.length <= 6000
 * - 1 <= u, v <= n
 * - 1 <= w <= 100
 * 
 * Follow-up Questions:
 * 1. Can you implement both Dijkstra's and Bellman-Ford algorithms?
 * 2. How would you handle negative edge weights?
 * 3. Can you reconstruct the path to the furthest node?
 */
public class NetworkDelayTime {

    // Approach 1: Dijkstra's Algorithm - O(E log V) time, O(V+E) space
    public int networkDelayTime(int[][] times, int n, int k) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] time : times) {
            adj.get(time[0] - 1).add(new int[] { time[1] - 1, time[2] });
        }

        // Distance array
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k - 1] = 0;

        // Priority queue
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { k - 1, 0 });

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];
            int d = curr[1];

            if (d > dist[u])
                continue;

            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];

                if (dist[v] > dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new int[] { v, dist[v] });
                }
            }
        }

        // Find max distance
        int maxDist = 0;
        for (int d : dist) {
            if (d == Integer.MAX_VALUE) {
                return -1; // Not all nodes are reachable
            }
            maxDist = Math.max(maxDist, d);
        }

        return maxDist;
    }

    // Approach 2: Bellman-Ford Algorithm - O(VE) time, O(V) space
    public int networkDelayTimeBellmanFord(int[][] times, int n, int k) {
        // Distance array
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[k - 1] = 0;

        // Relax edges n-1 times
        for (int i = 1; i < n; i++) {
            for (int[] time : times) {
                int u = time[0] - 1;
                int v = time[1] - 1;
                int w = time[2];

                if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + w) {
                    dist[v] = dist[u] + w;
                }
            }
        }

        // Find max distance
        int maxDist = 0;
        for (int d : dist) {
            if (d == Integer.MAX_VALUE) {
                return -1;
            }
            maxDist = Math.max(maxDist, d);
        }

        return maxDist;
    }

    // Follow-up 3: Reconstruct path to the furthest node
    public List<Integer> reconstructPathToFurthest(int[][] times, int n, int k) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] time : times) {
            adj.get(time[0] - 1).add(new int[] { time[1] - 1, time[2] });
        }

        // Distance and parent arrays
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[k - 1] = 0;

        // Priority queue
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { k - 1, 0 });

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];

            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];

                if (dist[v] > dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                    parent[v] = u;
                    pq.offer(new int[] { v, dist[v] });
                }
            }
        }

        // Find furthest node
        int maxDist = 0;
        int furthestNode = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] == Integer.MAX_VALUE) {
                return new ArrayList<>(); // Not all nodes reachable
            }
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                furthestNode = i;
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        if (furthestNode != -1) {
            int curr = furthestNode;
            while (curr != -1) {
                path.add(curr + 1); // Adjust for 1-based indexing
                curr = parent[curr];
            }
            Collections.reverse(path);
        }

        return path;
    }

    public static void main(String[] args) {
        NetworkDelayTime ndt = new NetworkDelayTime();

        // Test case 1: LeetCode example
        int[][] times1 = { { 2, 1, 1 }, { 2, 3, 1 }, { 3, 4, 1 } };
        System.out.println("Network delay time (Dijkstra): " + ndt.networkDelayTime(times1, 4, 2)); // 2
        System.out.println("Network delay time (Bellman-Ford): " + ndt.networkDelayTimeBellmanFord(times1, 4, 2)); // 2

        // Test case 2: Not all nodes reachable
        int[][] times2 = { { 1, 2, 1 } };
        System.out.println("Network delay time (unreachable): " + ndt.networkDelayTime(times2, 3, 1)); // -1

        // Test case 3: Reconstruct path
        System.out.println("Path to furthest node: " + ndt.reconstructPathToFurthest(times1, 4, 2)); // [2, 3, 4]

        // Test case 4: Single node
        int[][] times4 = {};
        System.out.println("Network delay time (single node): " + ndt.networkDelayTime(times4, 1, 1)); // 0

        // Test case 5: More complex graph
        int[][] times5 = { { 1, 2, 1 }, { 2, 3, 1 }, { 1, 3, 3 } };
        System.out.println("Network delay time (complex): " + ndt.networkDelayTime(times5, 3, 1)); // 2
    }
}
