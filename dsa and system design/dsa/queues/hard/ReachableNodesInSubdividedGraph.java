package queues.hard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * LeetCode 882: Reachable Nodes In Subdivided Graph
 * https://leetcode.com/problems/reachable-nodes-in-subdivided-graph/
 *
 * Description:
 * You are given an undirected graph, represented as a list of edges.
 *
 * Constraints:
 * - 0 <= edges.length <= 10^4
 * - edges[i].length == 3
 * - 0 <= u_i, v_i < n
 * - 0 <= cnt_i <= 10^4
 * - 0 <= maxMoves <= 10^9
 * - 1 <= n <= 3000
 *
 * Follow-up:
 * - Can you solve it using Dijkstra's algorithm?
 * - Can you optimize for very large graphs?
 */
public class ReachableNodesInSubdividedGraph {
    public int reachableNodes(int[][] edges, int maxMoves, int n) {
        Map<Integer, List<int[]>> graph = new HashMap<>();
        for (int i = 0; i < n; i++) {
            graph.put(i, new ArrayList<>());
        }

        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], w = edge[2];
            graph.get(u).add(new int[] { v, w });
            graph.get(v).add(new int[] { u, w });
        }

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> a[1] - b[1]);
        pq.offer(new int[] { 0, 0 }); // {node, distance}
        Map<Integer, Integer> dist = new HashMap<>();

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int node = curr[0], d = curr[1];

            if (dist.containsKey(node))
                continue;
            dist.put(node, d);

            for (int[] neighbor : graph.get(node)) {
                int next = neighbor[0], weight = neighbor[1];
                if (!dist.containsKey(next) && d + weight + 1 <= maxMoves) {
                    pq.offer(new int[] { next, d + weight + 1 });
                }
            }
        }

        int result = dist.size(); // Original nodes we can reach

        // Count subdivided nodes on edges
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1], cnt = edge[2];
            int a = dist.getOrDefault(u, maxMoves + 1);
            int b = dist.getOrDefault(v, maxMoves + 1);

            result += Math.min(cnt, Math.max(0, maxMoves - a) + Math.max(0, maxMoves - b));
        }

        return result;
    }

    public static void main(String[] args) {
        ReachableNodesInSubdividedGraph solution = new ReachableNodesInSubdividedGraph();
        System.out.println(solution.reachableNodes(new int[][] { { 0, 1, 10 }, { 0, 2, 1 }, { 1, 2, 2 } }, 6, 3)); // 13
        System.out.println(
                solution.reachableNodes(new int[][] { { 0, 1, 4 }, { 1, 2, 6 }, { 0, 2, 8 }, { 1, 3, 1 } }, 10, 4)); // 23
        System.out.println(solution.reachableNodes(
                new int[][] { { 1, 2, 4 }, { 1, 4, 5 }, { 1, 3, 1 }, { 2, 3, 4 }, { 3, 4, 5 } }, 17, 5)); // 1
        // Edge Case: No edges
        System.out.println(solution.reachableNodes(new int[][] {}, 10, 1)); // 1
        // Edge Case: Single edge
        System.out.println(solution.reachableNodes(new int[][] { { 0, 1, 1 } }, 1, 2)); // 3
    }
}
