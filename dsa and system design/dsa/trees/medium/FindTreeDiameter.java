package trees.medium;

import java.util.*;

/**
 * Advanced Variation: Tree Diameter (Alternative Implementation)
 * 
 * Description: Given a tree represented as an adjacency list, find its diameter
 * (longest path between any two nodes).
 *
 * Constraints:
 * - 1 <= n <= 10^4
 * - The input represents a valid tree
 * 
 * Follow-up Questions:
 * 1. Can you find the actual diameter path?
 * 2. Can you handle weighted edges?
 * 3. Can you find all diameters of equal length?
 */
public class FindTreeDiameter {

    // Approach 1: Two DFS approach (most efficient for general trees)
    public int treeDiameter(int[][] edges) {
        if (edges.length == 0)
            return 0;

        int n = edges.length + 1;
        Map<Integer, List<Integer>> graph = buildGraph(edges, n);

        // First DFS to find the farthest node from node 0
        int[] firstDFS = bfs(graph, 0, n);
        int farthestNode = firstDFS[0];

        // Second DFS from the farthest node to find the actual diameter
        int[] secondDFS = bfs(graph, farthestNode, n);

        return secondDFS[1];
    }

    private int[] bfs(Map<Integer, List<Integer>> graph, int start, int n) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        Queue<Integer> distQueue = new LinkedList<>();

        queue.offer(start);
        distQueue.offer(0);
        visited[start] = true;

        int farthestNode = start;
        int maxDistance = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            int dist = distQueue.poll();

            if (dist > maxDistance) {
                maxDistance = dist;
                farthestNode = node;
            }

            for (int neighbor : graph.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                    distQueue.offer(dist + 1);
                }
            }
        }

        return new int[] { farthestNode, maxDistance };
    }

    // Follow-up 1: Find the actual diameter path
    public List<Integer> findDiameterPath(int[][] edges) {
        if (edges.length == 0)
            return new ArrayList<>();

        int n = edges.length + 1;
        Map<Integer, List<Integer>> graph = buildGraph(edges, n);

        // Find one end of the diameter
        int[] firstDFS = bfsWithParent(graph, 0, n);
        int farthestNode = firstDFS[0];

        // Find the other end and construct the path
        return findPathBFS(graph, farthestNode, n);
    }

    private int[] bfsWithParent(Map<Integer, List<Integer>> graph, int start, int n) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        Queue<Integer> distQueue = new LinkedList<>();

        queue.offer(start);
        distQueue.offer(0);
        visited[start] = true;

        int farthestNode = start;
        int maxDistance = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            int dist = distQueue.poll();

            if (dist > maxDistance) {
                maxDistance = dist;
                farthestNode = node;
            }

            for (int neighbor : graph.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.offer(neighbor);
                    distQueue.offer(dist + 1);
                }
            }
        }

        return new int[] { farthestNode, maxDistance };
    }

    private List<Integer> findPathBFS(Map<Integer, List<Integer>> graph, int start, int n) {
        boolean[] visited = new boolean[n];
        Queue<Integer> queue = new LinkedList<>();
        Queue<Integer> distQueue = new LinkedList<>();
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        queue.offer(start);
        distQueue.offer(0);
        visited[start] = true;

        int farthestNode = start;
        int maxDistance = 0;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            int dist = distQueue.poll();

            if (dist > maxDistance) {
                maxDistance = dist;
                farthestNode = node;
            }

            for (int neighbor : graph.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = node;
                    queue.offer(neighbor);
                    distQueue.offer(dist + 1);
                }
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        int current = farthestNode;
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }
        Collections.reverse(path);
        return path;
    }

    // Follow-up 2: Handle weighted edges
    public int weightedTreeDiameter(int[][] edges, int[] weights) {
        if (edges.length == 0)
            return 0;

        int n = edges.length + 1;
        Map<Integer, List<int[]>> weightedGraph = buildWeightedGraph(edges, weights, n);

        int[] firstDFS = weightedBFS(weightedGraph, 0, n);
        int farthestNode = firstDFS[0];

        int[] secondDFS = weightedBFS(weightedGraph, farthestNode, n);
        return secondDFS[1];
    }

    private Map<Integer, List<int[]>> buildWeightedGraph(int[][] edges, int[] weights, int n) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int i = 0; i < edges.length; i++) {
            int u = edges[i][0], v = edges[i][1];
            int weight = weights[i];
            graph.get(u).add(new int[] { v, weight });
            graph.get(v).add(new int[] { u, weight });
        }

        return graph;
    }

    private int[] weightedBFS(Map<Integer, List<int[]>> graph, int start, int n) {
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[] { start, 0 });

        int farthestNode = start;
        int maxDistance = 0;

        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int node = current[0];
            int d = current[1];

            if (d > dist[node])
                continue;

            if (d > maxDistance) {
                maxDistance = d;
                farthestNode = node;
            }

            for (int[] edge : graph.get(node)) {
                int neighbor = edge[0];
                int weight = edge[1];
                int newDist = d + weight;

                if (newDist < dist[neighbor]) {
                    dist[neighbor] = newDist;
                    pq.offer(new int[] { neighbor, newDist });
                }
            }
        }

        return new int[] { farthestNode, maxDistance };
    }

    // Follow-up 3: Find all diameters of equal length
    public List<List<Integer>> findAllDiameterPaths(int[][] edges) {
        List<List<Integer>> allPaths = new ArrayList<>();

        if (edges.length == 0)
            return allPaths;

        int n = edges.length + 1;
        Map<Integer, List<Integer>> graph = buildGraph(edges, n);
        int diameter = treeDiameter(edges);

        // Check all possible starting points
        for (int start = 0; start < n; start++) {
            List<Integer> path = findLongestPathFrom(graph, start, n);
            if (path.size() - 1 == diameter) { // path length = nodes - 1
                allPaths.add(path);
            }
        }

        return allPaths;
    }

    private List<Integer> findLongestPathFrom(Map<Integer, List<Integer>> graph, int start, int n) {
        boolean[] visited = new boolean[n];
        int[] parent = new int[n];
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);
        visited[start] = true;

        int farthestNode = start;

        while (!queue.isEmpty()) {
            int node = queue.poll();
            farthestNode = node;

            for (int neighbor : graph.get(node)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = node;
                    queue.offer(neighbor);
                }
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        int current = farthestNode;
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }
        Collections.reverse(path);
        return path;
    }

    // Helper method to build graph
    private Map<Integer, List<Integer>> buildGraph(int[][] edges, int n) {
        Map<Integer, List<Integer>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int[] edge : edges) {
            graph.get(edge[0]).add(edge[1]);
            graph.get(edge[1]).add(edge[0]);
        }

        return graph;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindTreeDiameter solution = new FindTreeDiameter();

        // Test case 1: Basic tree
        int[][] edges1 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 1, 4 } };
        System.out.println("Test 1 - Basic tree:");
        System.out.println("Diameter: " + solution.treeDiameter(edges1));
        System.out.println("Diameter path: " + solution.findDiameterPath(edges1));

        // Test case 2: Linear tree
        int[][] edges2 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println("\nTest 2 - Linear tree:");
        System.out.println("Diameter: " + solution.treeDiameter(edges2));
        System.out.println("Diameter path: " + solution.findDiameterPath(edges2));

        // Test case 3: Weighted tree
        int[] weights = { 1, 2, 3, 4 };
        System.out.println("\nTest 3 - Weighted tree:");
        System.out.println("Weighted diameter: " + solution.weightedTreeDiameter(edges1, weights));

        // Test case 4: All diameter paths
        System.out.println("\nTest 4 - All diameter paths:");
        List<List<Integer>> allPaths = solution.findAllDiameterPaths(edges1);
        for (int i = 0; i < allPaths.size(); i++) {
            System.out.println("Path " + (i + 1) + ": " + allPaths.get(i));
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        int[][] singleEdge = { { 0, 1 } };
        System.out.println("Single edge: " + solution.treeDiameter(singleEdge));

        int[][] emptyEdges = {};
        System.out.println("No edges: " + solution.treeDiameter(emptyEdges));

        // Stress test
        System.out.println("\nStress test:");
        int[][] largeEdges = buildLargeTreeEdges(1000);

        long start = System.nanoTime();
        int result = solution.treeDiameter(largeEdges);
        long end = System.nanoTime();
        System.out.println("Large tree diameter: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static int[][] buildLargeTreeEdges(int nodes) {
        int[][] edges = new int[nodes - 1][2];
        Random rand = new Random(42);

        for (int i = 1; i < nodes; i++) {
            int parent = rand.nextInt(i);
            edges[i - 1] = new int[] { parent, i };
        }

        return edges;
    }
}
