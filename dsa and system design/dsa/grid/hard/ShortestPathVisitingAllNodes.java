package grid.hard;

import java.util.*;

/**
 * LeetCode 847: Shortest Path Visiting All Nodes
 * https://leetcode.com/problems/shortest-path-visiting-all-nodes/
 *
 * Description:
 * You have an undirected, connected graph of n nodes labeled from 0 to n - 1.
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
 * - If graph[a] contains b, then graph[b] contains a
 * - The input graph is always connected
 */
public class ShortestPathVisitingAllNodes {

    public int shortestPathLength(int[][] graph) {
        int n = graph.length;
        if (n == 1)
            return 0;

        int finalMask = (1 << n) - 1;
        Queue<int[]> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        // Start from all nodes
        for (int i = 0; i < n; i++) {
            int mask = 1 << i;
            queue.offer(new int[] { i, mask, 0 });
            visited.add(i + "," + mask);
        }

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int node = curr[0];
            int mask = curr[1];
            int dist = curr[2];

            for (int neighbor : graph[node]) {
                int newMask = mask | (1 << neighbor);
                String state = neighbor + "," + newMask;

                if (newMask == finalMask) {
                    return dist + 1;
                }

                if (!visited.contains(state)) {
                    visited.add(state);
                    queue.offer(new int[] { neighbor, newMask, dist + 1 });
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        ShortestPathVisitingAllNodes solution = new ShortestPathVisitingAllNodes();

        int[][] graph1 = { { 1, 2, 3 }, { 0 }, { 0 }, { 0 } };
        System.out.println(solution.shortestPathLength(graph1)); // 4

        int[][] graph2 = { { 1 }, { 0, 2, 4 }, { 1, 3, 4 }, { 2 }, { 1, 2 } };
        System.out.println(solution.shortestPathLength(graph2)); // 4
    }
}
