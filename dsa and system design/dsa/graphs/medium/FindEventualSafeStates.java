package graphs.medium;

import java.util.*;

/**
 * LeetCode 802: Find Eventual Safe States
 * https://leetcode.com/problems/find-eventual-safe-states/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Return all nodes that are eventually safe (no cycles reachable).
 *
 * Constraints:
 * - 1 <= graph.length <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve with DFS and coloring?
 * 2. Can you solve with reverse graph and BFS?
 */
public class FindEventualSafeStates {
    // Approach 1: DFS with coloring - O(V+E) time
    public List<Integer> eventualSafeNodes(int[][] graph) {
        int n = graph.length;
        int[] color = new int[n]; // 0=unknown, 1=visiting, 2=safe
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < n; i++)
            if (dfs(graph, color, i))
                res.add(i);
        return res;
    }

    private boolean dfs(int[][] g, int[] color, int u) {
        if (color[u] > 0)
            return color[u] == 2;
        color[u] = 1;
        for (int v : g[u])
            if (!dfs(g, color, v))
                return false;
        color[u] = 2;
        return true;
    }

    // Approach 2: Reverse graph + BFS
    // ...implement if needed...
    public static void main(String[] args) {
        FindEventualSafeStates fes = new FindEventualSafeStates();
        int[][] graph = { { 1, 2 }, { 2, 3 }, { 5 }, { 0 }, { 5 }, {}, {} };
        System.out.println(fes.eventualSafeNodes(graph).equals(java.util.Arrays.asList(2, 4, 5, 6)));

        // All nodes safe
        int[][] graph2 = { {}, {}, {}, {} };
        System.out.println(fes.eventualSafeNodes(graph2).equals(java.util.Arrays.asList(0, 1, 2, 3)));

        // All nodes in cycle
        int[][] graph3 = { { 1 }, { 2 }, { 0 } };
        System.out.println(fes.eventualSafeNodes(graph3).isEmpty());

        // Single node
        int[][] graph4 = { {} };
        System.out.println(fes.eventualSafeNodes(graph4).equals(java.util.Arrays.asList(0)));

        // Self loop
        int[][] graph5 = { { 0 } };
        System.out.println(fes.eventualSafeNodes(graph5).isEmpty());

        // Large acyclic graph
        int n = 100;
        int[][] graph6 = new int[n][];
        for (int i = 0; i < n; i++)
            graph6[i] = new int[] {};
        List<Integer> safe = fes.eventualSafeNodes(graph6);
        boolean allSafe = safe.size() == n && safe.get(0) == 0 && safe.get(n - 1) == n - 1;
        System.out.println(allSafe);
    }
}
