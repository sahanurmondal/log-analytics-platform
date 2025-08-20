package graphs.hard;

import java.util.*;

/**
 * LeetCode 1857: Largest Color Value in a Directed Graph (Longest Path
 * variation)
 * https://leetcode.com/problems/largest-color-value-in-a-directed-graph/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 4+ interviews)
 *
 * Description: Find the longest path in a directed acyclic graph (DAG).
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= edges.length <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you handle graphs with cycles?
 * 2. Can you find the actual longest path?
 * 3. Can you find the longest path between two specific vertices?
 */
public class FindLongestPathInDAG {

    // Approach 1: Topological Sort + DP - O(V+E) time, O(V+E) space
    public int longestPath(int n, int[][] edges) {
        // Build adjacency list and calculate indegrees
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        int[] indegree = new int[n];
        for (int[] edge : edges) {
            adj.get(edge[0]).add(new int[] { edge[1], edge[2] }); // (neighbor, weight)
            indegree[edge[1]]++;
        }

        // Topological sort using Kahn's algorithm
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        // DP to find longest path
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);

        // Initialize distances for start nodes
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                dist[i] = 0;
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];

                // Relax edge
                if (dist[u] != Integer.MIN_VALUE && dist[v] < dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                }

                indegree[v]--;
                if (indegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        // Find max distance
        int maxDist = 0;
        for (int d : dist) {
            maxDist = Math.max(maxDist, d);
        }

        return maxDist;
    }

    // Approach 2: DFS + Memoization - O(V+E) time, O(V+E) space
    public int longestPathDFS(int n, int[][] edges) {
        // Build adjacency list
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] edge : edges) {
            adj.get(edge[0]).add(new int[] { edge[1], edge[2] });
        }

        // Memoization table
        int[] memo = new int[n];
        Arrays.fill(memo, -1);

        int maxDist = 0;
        for (int i = 0; i < n; i++) {
            maxDist = Math.max(maxDist, dfs(i, adj, memo));
        }

        return maxDist;
    }

    private int dfs(int u, List<List<int[]>> adj, int[] memo) {
        if (memo[u] != -1) {
            return memo[u];
        }

        int max = 0;
        for (int[] neighbor : adj.get(u)) {
            int v = neighbor[0];
            int weight = neighbor[1];
            max = Math.max(max, weight + dfs(v, adj, memo));
        }

        memo[u] = max;
        return max;
    }

    // Follow-up 2: Find the actual longest path
    public List<Integer> findActualLongestPath(int n, int[][] edges) {
        // Build adjacency list and calculate indegrees
        List<List<int[]>> adj = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }

        int[] indegree = new int[n];
        for (int[] edge : edges) {
            adj.get(edge[0]).add(new int[] { edge[1], edge[2] });
            indegree[edge[1]]++;
        }

        // Topological sort
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        // DP to find longest path and parent pointers
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(parent, -1);

        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                dist[i] = 0;
            }
        }

        while (!queue.isEmpty()) {
            int u = queue.poll();

            for (int[] neighbor : adj.get(u)) {
                int v = neighbor[0];
                int weight = neighbor[1];

                if (dist[u] != Integer.MIN_VALUE && dist[v] < dist[u] + weight) {
                    dist[v] = dist[u] + weight;
                    parent[v] = u;
                }

                indegree[v]--;
                if (indegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        // Find end of longest path
        int maxDist = 0;
        int endNode = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                endNode = i;
            }
        }

        // Reconstruct path
        List<Integer> path = new ArrayList<>();
        if (endNode != -1) {
            int curr = endNode;
            while (curr != -1) {
                path.add(curr);
                curr = parent[curr];
            }
            Collections.reverse(path);
        }

        return path;
    }

    public static void main(String[] args) {
        FindLongestPathInDAG flp = new FindLongestPathInDAG();

        // Test case 1: Simple DAG
        int[][] edges1 = { { 0, 1, 5 }, { 0, 2, 3 }, { 1, 3, 6 }, { 2, 3, 7 } };
        System.out.println("Longest path 1 (Topo Sort): " + flp.longestPath(4, edges1)); // 13
        System.out.println("Longest path 1 (DFS): " + flp.longestPathDFS(4, edges1)); // 13

        // Test case 2: Multiple paths
        int[][] edges2 = { { 0, 1, 2 }, { 0, 2, 1 }, { 1, 3, 3 }, { 2, 3, 4 } };
        System.out.println("Longest path 2: " + flp.longestPath(4, edges2)); // 5

        // Test case 3: Disconnected DAG
        int[][] edges3 = { { 0, 1, 1 }, { 2, 3, 1 } };
        System.out.println("Longest path 3: " + flp.longestPath(4, edges3)); // 1

        // Test case 4: Find actual path
        System.out.println("Actual longest path: " + flp.findActualLongestPath(4, edges1));
        // Output: [0, 2, 3]

        // Test case 5: Empty graph
        int[][] edges5 = {};
        System.out.println("Longest path 5: " + flp.longestPath(3, edges5)); // 0
    }
}
