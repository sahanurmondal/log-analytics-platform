package bitmanipulation.hard;

import java.util.*;

/**
 * LeetCode 847: Shortest Path Visiting All Nodes
 * https://leetcode.com/problems/shortest-path-visiting-all-nodes/
 *
 * Description: You have an undirected, connected graph of n nodes labeled from
 * 0 to n - 1.
 * You are given an array graph where graph[i] is a list of all the nodes
 * connected to node i.
 * Return the length of the shortest path that visits every node. You may start
 * and stop at any node,
 * you may revisit nodes multiple times, and you may reuse edges.
 * 
 * Constraints:
 * - n == graph.length
 * - 1 <= n <= 12
 * - 0 <= graph[i].length < n
 * - graph[i] does not contain i
 * - graph[i] does not contain any element twice
 * - The given graph is connected and undirected
 *
 * Follow-up:
 * - Can you use BFS with state compression?
 * - What about using bitmasks to represent visited nodes?
 * 
 * Time Complexity: O(n^2 * 2^n)
 * Space Complexity: O(n * 2^n)
 * 
 * Company Tags: Google, Facebook
 */
public class ShortestPathVisitingAllNodes {

    // Main optimized solution - BFS with bitmask
    public int shortestPathLength(int[][] graph) {
        int n = graph.length;
        if (n == 1)
            return 0;

        int finalMask = (1 << n) - 1;
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // Start from each node
        for (int i = 0; i < n; i++) {
            queue.offer(new int[] { i, 1 << i }); // {node, mask}
            visited.add(i + "," + (1 << i));
        }

        int steps = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] current = queue.poll();
                int node = current[0];
                int mask = current[1];

                for (int neighbor : graph[node]) {
                    int newMask = mask | (1 << neighbor);
                    String state = neighbor + "," + newMask;

                    if (newMask == finalMask) {
                        return steps + 1;
                    }

                    if (!visited.contains(state)) {
                        visited.add(state);
                        queue.offer(new int[] { neighbor, newMask });
                    }
                }
            }
            steps++;
        }

        return -1;
    }

    // Alternative solution - DP with bitmask
    public int shortestPathLengthDP(int[][] graph) {
        int n = graph.length;
        int finalMask = (1 << n) - 1;

        // dp[mask][i] = minimum cost to reach state mask ending at node i
        int[][] dp = new int[1 << n][n];

        // Initialize DP array
        for (int i = 0; i < (1 << n); i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }

        // Base case: start from each node
        for (int i = 0; i < n; i++) {
            dp[1 << i][i] = 0;
        }

        // Fill DP table
        for (int mask = 0; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if (dp[mask][u] == Integer.MAX_VALUE)
                    continue;

                for (int v : graph[u]) {
                    int newMask = mask | (1 << v);
                    dp[newMask][v] = Math.min(dp[newMask][v], dp[mask][u] + 1);
                }
            }
        }

        int result = Integer.MAX_VALUE;
        for (int i = 0; i < n; i++) {
            result = Math.min(result, dp[finalMask][i]);
        }

        return result;
    }

    public static void main(String[] args) {
        ShortestPathVisitingAllNodes solution = new ShortestPathVisitingAllNodes();

        System.out.println(solution.shortestPathLength(new int[][] { { 1, 2, 3 }, { 0 }, { 0 }, { 0 } })); // Expected:
                                                                                                           // 4
        System.out
                .println(solution.shortestPathLength(new int[][] { { 1 }, { 0, 2, 4 }, { 1, 3, 4 }, { 2 }, { 1, 2 } })); // Expected:
                                                                                                                         // 4
        System.out.println(solution.shortestPathLengthDP(new int[][] { { 1, 2, 3 }, { 0 }, { 0 }, { 0 } })); // Expected:
                                                                                                             // 4
    }
}
