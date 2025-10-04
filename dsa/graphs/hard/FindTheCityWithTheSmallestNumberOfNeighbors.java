package graphs.hard;

import java.util.*;

/**
 * LeetCode 1334: Find the City With the Smallest Number of Neighbors at a
 * Threshold Distance
 * https://leetcode.com/problems/find-the-city-with-the-smallest-number-of-neighbors-at-a-threshold-distance/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Find the city with the smallest number of other cities within
 * the threshold distance.
 * If there are multiple such cities, return the one with the greatest number.
 *
 * Constraints:
 * - 2 <= n <= 100
 * - 1 <= edges.length <= n * (n - 1) / 2
 * - 1 <= distanceThreshold <= 10^4
 * - 1 <= edge weight <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you optimize the algorithm for sparse graphs?
 * 2. How would you handle dynamic graph updates?
 * 3. Can you find the city that can reach the most other cities within the
 * threshold?
 */
public class FindTheCityWithTheSmallestNumberOfNeighbors {

    // Approach 1: Floyd-Warshall Algorithm - O(n^3) time, O(n^2) space
    public int findTheCity(int n, int[][] edges, int distanceThreshold) {
        // Initialize distance matrix
        int[][] distances = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE / 2); // Avoid overflow
            distances[i][i] = 0; // Distance to self is 0
        }

        // Fill direct edge distances
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            int weight = edge[2];
            distances[from][to] = weight;
            distances[to][from] = weight; // Undirected graph
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    distances[i][j] = Math.min(distances[i][j], distances[i][k] + distances[k][j]);
                }
            }
        }

        // Find city with fewest reachable cities within threshold
        int minReachable = n;
        int resultCity = -1;

        for (int city = 0; city < n; city++) {
            int reachableCities = 0;
            for (int otherCity = 0; otherCity < n; otherCity++) {
                if (city != otherCity && distances[city][otherCity] <= distanceThreshold) {
                    reachableCities++;
                }
            }

            // Update result if fewer cities or same number but higher city number
            if (reachableCities <= minReachable) {
                minReachable = reachableCities;
                resultCity = city;
            }
        }

        return resultCity;
    }

    // Approach 2: Dijkstra's Algorithm - O(n * (E log V)) time, O(n^2) space
    // Better for sparse graphs
    public int findTheCityDijkstra(int n, int[][] edges, int distanceThreshold) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            int weight = edge[2];
            adj.get(from).add(new int[] { to, weight });
            adj.get(to).add(new int[] { from, weight }); // Undirected graph
        }

        // For each city, run Dijkstra to find reachable cities
        int minReachable = n;
        int resultCity = -1;

        for (int city = 0; city < n; city++) {
            int[] distances = dijkstra(n, adj, city);

            int reachableCities = 0;
            for (int otherCity = 0; otherCity < n; otherCity++) {
                if (city != otherCity && distances[otherCity] <= distanceThreshold) {
                    reachableCities++;
                }
            }

            // Update result if fewer cities or same number but higher city number
            if (reachableCities <= minReachable) {
                minReachable = reachableCities;
                resultCity = city;
            }
        }

        return resultCity;
    }

    private int[] dijkstra(int n, List<List<int[]>> adj, int start) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { start, 0 });

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int node = curr[0];
            int distance = curr[1];

            if (distance > dist[node])
                continue;

            for (int[] neighbor : adj.get(node)) {
                int nextNode = neighbor[0];
                int weight = neighbor[1];

                if (dist[nextNode] > dist[node] + weight) {
                    dist[nextNode] = dist[node] + weight;
                    pq.offer(new int[] { nextNode, dist[nextNode] });
                }
            }
        }

        return dist;
    }

    // Follow-up 3: Find city that can reach the most cities within threshold
    public int findCityWithMostReachable(int n, int[][] edges, int distanceThreshold) {
        // Initialize distance matrix
        int[][] distances = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(distances[i], Integer.MAX_VALUE / 2);
            distances[i][i] = 0;
        }

        // Fill direct edge distances
        for (int[] edge : edges) {
            int from = edge[0];
            int to = edge[1];
            int weight = edge[2];
            distances[from][to] = weight;
            distances[to][from] = weight;
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    distances[i][j] = Math.min(distances[i][j], distances[i][k] + distances[k][j]);
                }
            }
        }

        // Find city with most reachable cities
        int maxReachable = -1;
        int resultCity = -1;

        for (int city = 0; city < n; city++) {
            int reachableCities = 0;
            for (int otherCity = 0; otherCity < n; otherCity++) {
                if (city != otherCity && distances[city][otherCity] <= distanceThreshold) {
                    reachableCities++;
                }
            }

            if (reachableCities > maxReachable) {
                maxReachable = reachableCities;
                resultCity = city;
            }
        }

        return resultCity;
    }

    public static void main(String[] args) {
        FindTheCityWithTheSmallestNumberOfNeighbors solution = new FindTheCityWithTheSmallestNumberOfNeighbors();

        // Test case 1: LeetCode example 1
        int[][] edges1 = { { 0, 1, 3 }, { 1, 2, 1 }, { 1, 3, 4 }, { 2, 3, 1 } };
        System.out.println("City with smallest number of neighbors (Floyd-Warshall): " +
                solution.findTheCity(4, edges1, 4)); // 3
        System.out.println("City with smallest number of neighbors (Dijkstra): " +
                solution.findTheCityDijkstra(4, edges1, 4)); // 3

        // Test case 2: LeetCode example 2
        int[][] edges2 = { { 0, 1, 2 }, { 0, 4, 8 }, { 1, 2, 3 }, { 1, 4, 2 }, { 2, 3, 1 }, { 3, 4, 1 } };
        System.out.println("City with smallest number of neighbors (Floyd-Warshall): " +
                solution.findTheCity(5, edges2, 2)); // 0
        System.out.println("City with smallest number of neighbors (Dijkstra): " +
                solution.findTheCityDijkstra(5, edges2, 2)); // 0

        // Test case 3: Disconnected graph
        int[][] edges3 = { { 0, 1, 1 }, { 2, 3, 1 } };
        System.out.println("City with smallest number of neighbors (Floyd-Warshall): " +
                solution.findTheCity(4, edges3, 1)); // 3

        // Test case 4: All cities connected directly
        int[][] edges4 = { { 0, 1, 1 }, { 0, 2, 1 }, { 0, 3, 1 }, { 1, 2, 1 }, { 1, 3, 1 }, { 2, 3, 1 } };
        System.out.println("City with smallest number of neighbors (Floyd-Warshall): " +
                solution.findTheCity(4, edges4, 1)); // 3

        // Test case 5: Follow-up - find city with most reachable
        System.out.println("City with most reachable neighbors: " +
                solution.findCityWithMostReachable(4, edges1, 4)); // 2
    }
}
