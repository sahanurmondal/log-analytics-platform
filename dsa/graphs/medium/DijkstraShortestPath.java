package graphs.medium;

import java.util.*;

/**
 * LeetCode 743: Network Delay Time (Dijkstra's variation)
 * https://leetcode.com/problems/network-delay-time/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 8+ interviews)
 *
 * Description: Find the shortest path from a source to all other vertices in a
 * weighted graph.
 *
 * Constraints:
 * - 1 <= n <= 100
 * - 1 <= edges.length <= 6000
 * - 0 <= weight <= 100
 * 
 * Follow-up Questions:
 * 1. Can you implement Bellman-Ford for graphs with negative weights?
 * 2. Can you reconstruct the shortest path?
 * 3. How would you handle a graph with cycles?
 */
public class DijkstraShortestPath {

    // Approach 1: Dijkstra's Algorithm - O(E log V) time, O(V+E) space
    public int[] dijkstra(int n, int[][] edges, int src) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(new int[] { edge[1], edge[2] });
        }

        // Distance array
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Priority queue for vertices to visit
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { src, 0 }); // (vertex, distance)

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];
            int d = curr[1];

            if (d > dist[u])
                continue;

            // Explore neighbors
            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];

                if (dist[v] > dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new int[] { v, dist[v] });
                }
            }
        }

        return dist;
    }

    // Approach 2: Bellman-Ford Algorithm - O(VE) time, O(V) space
    public int[] bellmanFord(int n, int[][] edges, int src) {
        // Distance array
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[src] = 0;

        // Relax edges n-1 times
        for (int i = 1; i < n; i++) {
            for (int[] edge : edges) {
                int u = edge[0];
                int v = edge[1];
                int weight = edge[2];

                if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                }
            }
        }

        // Check for negative cycles
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int weight = edge[2];

            if (dist[u] != Integer.MAX_VALUE && dist[v] > dist[u] + weight) {
                // Negative cycle detected
                return null;
            }
        }

        return dist;
    }

    // Follow-up 2: Reconstruct the shortest path
    public List<Integer> reconstructPath(int n, int[][] edges, int src, int dest) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(new int[] { edge[1], edge[2] });
        }

        // Distance and parent arrays
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[src] = 0;

        // Priority queue
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { src, 0 });

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];

            if (u == dest)
                break;

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

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        if (dist[dest] == Integer.MAX_VALUE) {
            return path; // No path
        }

        int curr = dest;
        while (curr != -1) {
            path.add(curr);
            curr = parent[curr];
        }

        Collections.reverse(path);
        return path;
    }

    public static void main(String[] args) {
        DijkstraShortestPath dsp = new DijkstraShortestPath();

        // Test case 1: Simple graph
        int[][] edges1 = { { 0, 1, 4 }, { 0, 2, 1 }, { 1, 3, 1 }, { 2, 1, 2 }, { 2, 3, 5 } };
        System.out.println("Dijkstra distances: " + Arrays.toString(dsp.dijkstra(4, edges1, 0)));
        // Output: [0, 3, 1, 4]

        // Test case 2: Graph with negative weights (Bellman-Ford)
        int[][] edges2 = { { 0, 1, -1 }, { 0, 2, 4 }, { 1, 2, 3 }, { 1, 3, 2 }, { 1, 4, 2 }, { 3, 2, 5 }, { 3, 1, 1 },
                { 4, 3, -3 } };
        System.out.println("Bellman-Ford distances: " + Arrays.toString(dsp.bellmanFord(5, edges2, 0)));
        // Output: [0, -1, 2, -2, 1]

        // Test case 3: Reconstruct path
        System.out.println("Shortest path: " + dsp.reconstructPath(4, edges1, 0, 3));
        // Output: [0, 2, 1, 3]

        // Test case 4: Disconnected graph
        int[][] edges4 = { { 0, 1, 1 }, { 2, 3, 1 } };
        System.out.println("Dijkstra distances (disconnected): " + Arrays.toString(dsp.dijkstra(4, edges4, 0)));
        // Output: [0, 1, MAX, MAX]

        // Test case 5: Negative cycle
        int[][] edges5 = { { 0, 1, -1 }, { 1, 2, -2 }, { 2, 0, -3 } };
        System.out.println("Bellman-Ford (negative cycle): " + Arrays.toString(dsp.bellmanFord(3, edges5, 0)));
        // Output: null
    }
}
