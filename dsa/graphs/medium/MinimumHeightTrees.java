package graphs.medium;

import java.util.*;

/**
 * LeetCode 310: Minimum Height Trees
 * https://leetcode.com/problems/minimum-height-trees/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 6+ interviews)
 *
 * Description: Find all vertices that can be roots of minimum height trees.
 *
 * Constraints:
 * - 1 <= n <= 2 * 10^4
 * - edges.length == n - 1
 * 
 * Follow-up Questions:
 * 1. Can you prove that there can be at most two minimum height trees?
 * 2. How would you handle a graph with cycles?
 * 3. Can you find the diameter of the tree?
 */
public class MinimumHeightTrees {

    // Approach 1: Topological Sort (Peeling Leaves) - O(V+E) time, O(V+E) space
    public List<Integer> findMinHeightTrees(int n, int[][] edges) {
        if (n == 1) {
            return Collections.singletonList(0);
        }

        // Build adjacency list and degree array
        List<Set<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new HashSet<>());
        }

        int[] degree = new int[n];
        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
            degree[edge[0]]++;
            degree[edge[1]]++;
        }

        // Find initial leaves
        Queue<Integer> leaves = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (degree[i] == 1) {
                leaves.offer(i);
            }
        }

        // Iteratively remove leaves
        int remainingNodes = n;
        while (remainingNodes > 2) {
            int size = leaves.size();
            remainingNodes -= size;

            for (int i = 0; i < size; i++) {
                int leaf = leaves.poll();
                for (int neighbor : adj.get(leaf)) {
                    degree[neighbor]--;
                    if (degree[neighbor] == 1) {
                        leaves.offer(neighbor);
                    }
                }
            }
        }

        return new ArrayList<>(leaves);
    }

    // Approach 2: Two BFS (Find Diameter) - O(V+E) time, O(V+E) space
    public List<Integer> findMinHeightTreesBFS(int n, int[][] edges) {
        if (n == 1) {
            return Collections.singletonList(0);
        }

        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        // Find one endpoint of the diameter
        int[] dist1 = bfs(n, adj, 0);
        int endpoint1 = 0;
        for (int i = 0; i < n; i++) {
            if (dist1[i] > dist1[endpoint1]) {
                endpoint1 = i;
            }
        }

        // Find the other endpoint and path
        int[] dist2 = bfs(n, adj, endpoint1);
        int endpoint2 = 0;
        for (int i = 0; i < n; i++) {
            if (dist2[i] > dist2[endpoint2]) {
                endpoint2 = i;
            }
        }

        // Reconstruct the diameter path
        List<Integer> diameterPath = new ArrayList<>();
        int[] parent = new int[n];
        bfsWithPath(n, adj, endpoint1, parent);

        int curr = endpoint2;
        while (curr != -1) {
            diameterPath.add(curr);
            curr = parent[curr];
        }

        // Find the middle of the diameter
        int size = diameterPath.size();
        if (size % 2 == 1) {
            return Collections.singletonList(diameterPath.get(size / 2));
        } else {
            return Arrays.asList(diameterPath.get(size / 2 - 1), diameterPath.get(size / 2));
        }
    }

    private int[] bfs(int n, List<List<Integer>> adj, int start) {
        int[] dist = new int[n];
        Arrays.fill(dist, -1);
        dist[start] = 0;

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adj.get(u)) {
                if (dist[v] == -1) {
                    dist[v] = dist[u] + 1;
                    queue.offer(v);
                }
            }
        }

        return dist;
    }

    private void bfsWithPath(int n, List<List<Integer>> adj, int start, int[] parent) {
        Arrays.fill(parent, -1);

        Queue<Integer> queue = new LinkedList<>();
        queue.offer(start);

        boolean[] visited = new boolean[n];
        visited[start] = true;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (int v : adj.get(u)) {
                if (!visited[v]) {
                    visited[v] = true;
                    parent[v] = u;
                    queue.offer(v);
                }
            }
        }
    }

    // Follow-up 3: Find the diameter of the tree
    public int findDiameter(int n, int[][] edges) {
        if (n <= 1)
            return 0;

        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(edge[1]);
            adj.get(edge[1]).add(edge[0]);
        }

        int[] dist1 = bfs(n, adj, 0);
        int endpoint1 = 0;
        for (int i = 0; i < n; i++) {
            if (dist1[i] > dist1[endpoint1]) {
                endpoint1 = i;
            }
        }

        int[] dist2 = bfs(n, adj, endpoint1);
        int diameter = 0;
        for (int d : dist2) {
            diameter = Math.max(diameter, d);
        }

        return diameter;
    }

    public static void main(String[] args) {
        MinimumHeightTrees mht = new MinimumHeightTrees();

        // Test case 1: LeetCode example 1
        int[][] edges1 = { { 1, 0 }, { 1, 2 }, { 1, 3 } };
        System.out.println("Min height trees 1: " + mht.findMinHeightTrees(4, edges1)); // [1]

        // Test case 2: LeetCode example 2
        int[][] edges2 = { { 0, 3 }, { 1, 3 }, { 2, 3 }, { 4, 3 }, { 5, 4 } };
        System.out.println("Min height trees 2: " + mht.findMinHeightTrees(6, edges2)); // [3, 4]

        // Test case 3: Two BFS approach
        System.out.println("Min height trees (BFS) 1: " + mht.findMinHeightTreesBFS(4, edges1)); // [1]

        // Test case 4: Find diameter
        System.out.println("Diameter: " + mht.findDiameter(6, edges2)); // 3

        // Test case 5: Single node
        int[][] edges5 = {};
        System.out.println("Min height trees 5: " + mht.findMinHeightTrees(1, edges5)); // [0]
    }
}
