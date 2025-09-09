package graphs.hard;

import java.util.*;

/**
 * LeetCode 1584: Min Cost to Connect All Points (MST variation)
 * https://leetcode.com/problems/min-cost-to-connect-all-points/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 7+ interviews)
 *
 * Description: Find the minimum spanning tree in a connected, undirected graph.
 * A minimum spanning tree is a subset of edges that connects all vertices with
 * minimum possible total edge weight.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= edges.length <= n*(n-1)/2
 * - 1 <= edge weight <= 10^6
 * 
 * Follow-up Questions:
 * 1. Can you implement both Kruskal's and Prim's algorithms?
 * 2. How would you handle disconnected graphs?
 * 3. Can you find the maximum spanning tree?
 */
public class MinimumSpanningTree {

    // Approach 1: Kruskal's Algorithm - O(E log E) time, O(V+E) space
    public int kruskalMST(int n, int[][] edges) {
        // Sort edges by weight
        Arrays.sort(edges, Comparator.comparingInt(a -> a[2]));

        // Initialize Union-Find data structure
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int totalWeight = 0;
        int edgesAdded = 0;

        // Process edges in ascending order of weight
        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int weight = edge[2];

            // If including this edge doesn't form a cycle
            if (find(parent, u) != find(parent, v)) {
                union(parent, u, v);
                totalWeight += weight;
                edgesAdded++;

                // MST should have n-1 edges
                if (edgesAdded == n - 1) {
                    break;
                }
            }
        }

        // Check if we have a spanning tree (connected graph)
        return edgesAdded == n - 1 ? totalWeight : -1;
    }

    // Union-Find helper methods
    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]); // Path compression
        }
        return parent[x];
    }

    private void union(int[] parent, int x, int y) {
        parent[find(parent, x)] = find(parent, y);
    }

    // Approach 2: Prim's Algorithm - O(E log V) time, O(V+E) space
    public int primMST(int n, int[][] edges) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            int u = edge[0];
            int v = edge[1];
            int weight = edge[2];
            adj.get(u).add(new int[] { v, weight });
            adj.get(v).add(new int[] { u, weight }); // Undirected graph
        }

        // Priority queue to select minimum weight edge
        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        boolean[] visited = new boolean[n];
        int totalWeight = 0;
        int visitedCount = 0;

        // Start from vertex 0
        pq.offer(new int[] { 0, 0 }); // (vertex, weight)

        while (!pq.isEmpty() && visitedCount < n) {
            int[] curr = pq.poll();
            int vertex = curr[0];
            int weight = curr[1];

            if (visited[vertex])
                continue;

            // Add vertex to MST
            visited[vertex] = true;
            totalWeight += weight;
            visitedCount++;

            // Add all edges from current vertex
            for (int[] neighbor : adj.get(vertex)) {
                int nextVertex = neighbor[0];
                int nextWeight = neighbor[1];

                if (!visited[nextVertex]) {
                    pq.offer(new int[] { nextVertex, nextWeight });
                }
            }
        }

        // Check if we have a spanning tree (connected all vertices)
        return visitedCount == n ? totalWeight : -1;
    }

    // Follow-up 3: Maximum Spanning Tree - O(E log E) time, O(V+E) space
    public int maximumSpanningTree(int n, int[][] edges) {
        // Negate weights to turn min-heap into max-heap
        int[][] negatedEdges = new int[edges.length][3];
        for (int i = 0; i < edges.length; i++) {
            negatedEdges[i][0] = edges[i][0];
            negatedEdges[i][1] = edges[i][1];
            negatedEdges[i][2] = -edges[i][2]; // Negate weight
        }

        // Use Kruskal's algorithm with negated weights
        int result = kruskalMST(n, negatedEdges);

        // Negate the result back
        return result == -1 ? -1 : -result;
    }

    public static void main(String[] args) {
        MinimumSpanningTree mst = new MinimumSpanningTree();

        // Test case 1: Simple connected graph
        int[][] edges1 = {
                { 0, 1, 10 }, { 0, 2, 6 }, { 0, 3, 5 },
                { 1, 3, 15 }, { 2, 3, 4 }
        };
        System.out.println("Kruskal MST 1: " + mst.kruskalMST(4, edges1)); // 19
        System.out.println("Prim MST 1: " + mst.primMST(4, edges1)); // 19

        // Test case 2: Disconnected graph
        int[][] edges2 = {
                { 0, 1, 10 }, { 2, 3, 5 }
        };
        System.out.println("Kruskal MST 2: " + mst.kruskalMST(4, edges2)); // -1
        System.out.println("Prim MST 2: " + mst.primMST(4, edges2)); // -1

        // Test case 3: Complete graph
        int[][] edges3 = {
                { 0, 1, 3 }, { 0, 2, 1 }, { 0, 3, 4 },
                { 1, 2, 2 }, { 1, 3, 5 }, { 2, 3, 6 }
        };
        System.out.println("Kruskal MST 3: " + mst.kruskalMST(4, edges3)); // 6
        System.out.println("Maximum Spanning Tree: " + mst.maximumSpanningTree(4, edges3)); // 14

        // Test case 4: Single vertex
        int[][] edges4 = {};
        System.out.println("Kruskal MST 4: " + mst.kruskalMST(1, edges4)); // 0

        // Test case 5: Large graph with cycle
        int n = 5;
        int[][] edges5 = new int[n * (n - 1) / 2][3];
        int idx = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                edges5[idx++] = new int[] { i, j, i + j + 1 };
            }
        }
        System.out.println("Kruskal MST 5: " + mst.kruskalMST(n, edges5)); // 10
    }
}
