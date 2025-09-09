package graphs.medium;

import java.util.*;

/**
 * A* Shortest Path Algorithm
 * https://en.wikipedia.org/wiki/A*_search_algorithm
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 4+ interviews)
 *
 * Description: Find the shortest path from a source to a destination using A*
 * search,
 * which is an extension of Dijkstra's algorithm with a heuristic function.
 *
 * Constraints:
 * - 1 <= n <= 10^4
 * - 0 <= edges.length <= 2*10^4
 * - 0 <= weight <= 100
 * 
 * Follow-up Questions:
 * 1. How does the choice of heuristic affect performance?
 * 2. Can you implement A* for a grid-based graph?
 * 3. How does A* compare to Dijkstra's algorithm?
 */
public class AStarShortestPath {

    // Approach 1: A* Algorithm - O(E log V) time, O(V+E) space
    public int aStar(int n, int[][] edges, int src, int dest, int[] heuristic) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(new int[] { edge[1], edge[2] });
        }

        // Distance from source
        int[] gScore = new int[n];
        Arrays.fill(gScore, Integer.MAX_VALUE);
        gScore[src] = 0;

        // Priority queue for vertices to visit, ordered by fScore = gScore + heuristic
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { src, heuristic[src] }); // (vertex, fScore)

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];

            if (u == dest) {
                return gScore[u];
            }

            // Explore neighbors
            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];

                int tentativeGScore = gScore[u] + weight;

                if (tentativeGScore < gScore[v]) {
                    gScore[v] = tentativeGScore;
                    int fScore = gScore[v] + heuristic[v];
                    pq.offer(new int[] { v, fScore });
                }
            }
        }

        return -1; // No path found
    }

    // Approach 2: Dijkstra's Algorithm (for comparison) - O(E log V) time, O(V+E)
    // space
    public int dijkstra(int n, int[][] edges, int src, int dest) {
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

        // Priority queue
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.offer(new int[] { src, 0 });

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int u = curr[0];

            if (u == dest) {
                return dist[u];
            }

            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];

                if (dist[v] > dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                    pq.offer(new int[] { v, dist[v] });
                }
            }
        }

        return -1;
    }

    // Follow-up 2: A* for a grid-based graph
    public int aStarGrid(int[][] grid, int[] start, int[] end) {
        int rows = grid.length;
        int cols = grid[0].length;

        // Distance from source
        int[][] gScore = new int[rows][cols];
        for (int[] row : gScore) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }
        gScore[start[0]][start[1]] = 0;

        // Priority queue
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[2]));
        pq.offer(new int[] { start[0], start[1], heuristic(start, end) });

        int[][] directions = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int r = curr[0];
            int c = curr[1];

            if (r == end[0] && c == end[1]) {
                return gScore[r][c];
            }

            // Explore neighbors
            for (int[] dir : directions) {
                int nr = r + dir[0];
                int nc = c + dir[1];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols && grid[nr][nc] == 0) {
                    int tentativeGScore = gScore[r][c] + 1;

                    if (tentativeGScore < gScore[nr][nc]) {
                        gScore[nr][nc] = tentativeGScore;
                        int fScore = gScore[nr][nc] + heuristic(new int[] { nr, nc }, end);
                        pq.offer(new int[] { nr, nc, fScore });
                    }
                }
            }
        }

        return -1;
    }

    // Manhattan distance heuristic for grid
    private int heuristic(int[] a, int[] b) {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }

    public static void main(String[] args) {
        AStarShortestPath asp = new AStarShortestPath();

        // Test case 1: Simple graph
        int[][] edges1 = { { 0, 1, 4 }, { 0, 2, 1 }, { 1, 3, 1 }, { 2, 1, 2 }, { 2, 3, 5 } };
        int[] heuristic1 = { 4, 1, 3, 0 }; // Heuristic for destination 3
        System.out.println("A* shortest path: " + asp.aStar(4, edges1, 0, 3, heuristic1));
        // Output: 4

        // Test case 2: Comparison with Dijkstra
        System.out.println("Dijkstra shortest path: " + asp.dijkstra(4, edges1, 0, 3));
        // Output: 4

        // Test case 3: Grid-based A*
        int[][] grid = {
                { 0, 0, 0, 0, 0 },
                { 0, 1, 1, 0, 0 },
                { 0, 0, 0, 1, 0 },
                { 0, 1, 0, 0, 0 },
                { 0, 0, 0, 1, 0 }
        };
        int[] start = { 0, 0 };
        int[] end = { 4, 4 };
        System.out.println("A* grid path: " + asp.aStarGrid(grid, start, end));
        // Output: 8

        // Test case 4: No path
        int[][] edges4 = { { 0, 1, 1 }, { 2, 3, 1 } };
        int[] heuristic4 = { 2, 1, 1, 0 };
        System.out.println("A* (no path): " + asp.aStar(4, edges4, 0, 3, heuristic4));
        // Output: -1
    }
}
