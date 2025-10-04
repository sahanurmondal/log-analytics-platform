package graphs.medium;

import java.util.*;

/**
 * LeetCode 1135: Connecting Cities With Minimum Cost (Kruskal's variation)
 * https://leetcode.com/problems/connecting-cities-with-minimum-cost/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Find the minimum spanning tree using Kruskal's algorithm.
 *
 * Constraints:
 * - 1 <= n <= 10000
 * - 1 <= connections.length <= 10000
 * - 1 <= cost <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you implement Prim's algorithm as an alternative?
 * 2. How would you handle a disconnected graph?
 * 3. Can you return the edges of the MST?
 */
public class KruskalMinimumSpanningTree {

    // Approach 1: Kruskal's Algorithm - O(E log E) time, O(V+E) space
    public int kruskalMST(int n, int[][] edges) {
        // Sort edges by weight
        Arrays.sort(edges, Comparator.comparingInt(a -> a[2]));

        // Union-Find data structure
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        int totalWeight = 0;
        int edgesAdded = 0;

        for (int[] edge : edges) {
            int u = edge[0] - 1; // Adjust for 1-based indexing
            int v = edge[1] - 1;
            int weight = edge[2];

            if (find(parent, u) != find(parent, v)) {
                union(parent, u, v);
                totalWeight += weight;
                edgesAdded++;
            }
        }

        return edgesAdded == n - 1 ? totalWeight : -1;
    }

    private int find(int[] parent, int x) {
        if (parent[x] != x) {
            parent[x] = find(parent, parent[x]);
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
            int u = edge[0] - 1;
            int v = edge[1] - 1;
            int weight = edge[2];
            adj.get(u).add(new int[] { v, weight });
            adj.get(v).add(new int[] { u, weight });
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

            visited[vertex] = true;
            totalWeight += weight;
            visitedCount++;

            for (int[] neighbor : adj.get(vertex)) {
                int nextVertex = neighbor[0];
                int nextWeight = neighbor[1];

                if (!visited[nextVertex]) {
                    pq.offer(new int[] { nextVertex, nextWeight });
                }
            }
        }

        return visitedCount == n ? totalWeight : -1;
    }

    // Follow-up 3: Return edges of the MST
    public List<int[]> getMSTEdges(int n, int[][] edges) {
        // Sort edges by weight
        Arrays.sort(edges, Comparator.comparingInt(a -> a[2]));

        // Union-Find data structure
        int[] parent = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }

        List<int[]> mstEdges = new ArrayList<>();

        for (int[] edge : edges) {
            int u = edge[0] - 1;
            int v = edge[1] - 1;

            if (find(parent, u) != find(parent, v)) {
                union(parent, u, v);
                mstEdges.add(edge);
            }
        }

        return mstEdges.size() == n - 1 ? mstEdges : new ArrayList<>();
    }

    public static void main(String[] args) {
        KruskalMinimumSpanningTree kmst = new KruskalMinimumSpanningTree();

        // Test case 1: Simple connected graph
        int[][] edges1 = { { 1, 2, 5 }, { 1, 3, 6 }, { 2, 3, 1 } };
        System.out.println("Kruskal MST 1: " + kmst.kruskalMST(3, edges1)); // 6

        // Test case 2: Disconnected graph
        int[][] edges2 = { { 1, 2, 1 }, { 3, 4, 1 } };
        System.out.println("Kruskal MST 2: " + kmst.kruskalMST(4, edges2)); // -1

        // Test case 3: Prim's algorithm
        System.out.println("Prim MST 1: " + kmst.primMST(3, edges1)); // 6

        // Test case 4: Get MST edges
        List<int[]> mstEdges = kmst.getMSTEdges(3, edges1);
        System.out.print("MST Edges: ");
        for (int[] edge : mstEdges) {
            System.out.print(Arrays.toString(edge) + " ");
        }
        System.out.println();

        // Test case 5: Single city
        int[][] edges5 = {};
        System.out.println("Kruskal MST 5: " + kmst.kruskalMST(1, edges5)); // 0
    }
}
