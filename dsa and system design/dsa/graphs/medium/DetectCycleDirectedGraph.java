package graphs.medium;

import java.util.*;

/**
 * LeetCode 207: Course Schedule (Cycle Detection)
 * https://leetcode.com/problems/course-schedule/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given prerequisites, determine if you can finish all courses
 * (detect cycle in directed graph).
 *
 * Constraints:
 * - 1 <= numCourses <= 10^5
 * - prerequisites.length <= 5000
 * 
 * Follow-up Questions:
 * 1. Can you return the order (topological sort)?
 * 2. Can you solve with DFS?
 */
public class DetectCycleDirectedGraph {
    // Approach 1: BFS (Kahn's Algorithm) - O(V+E) time
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++)
            adj.add(new ArrayList<>());
        int[] indegree = new int[numCourses];
        for (int[] p : prerequisites) {
            adj.get(p[1]).add(p[0]);
            indegree[p[0]]++;
        }
        Queue<Integer> q = new LinkedList<>();
        for (int i = 0; i < numCourses; i++)
            if (indegree[i] == 0)
                q.offer(i);
        int cnt = 0;
        while (!q.isEmpty()) {
            int u = q.poll();
            cnt++;
            for (int v : adj.get(u))
                if (--indegree[v] == 0)
                    q.offer(v);
        }
        return cnt == numCourses;
    }

    // Approach 2: DFS
    // ...implement if needed...
    public static void main(String[] args) {
        DetectCycleDirectedGraph dcdg = new DetectCycleDirectedGraph();
        System.out.println(dcdg.canFinish(2, new int[][] { { 1, 0 } })); // true
        System.out.println(dcdg.canFinish(2, new int[][] { { 1, 0 }, { 0, 1 } })); // false

        // No prerequisites
        System.out.println(dcdg.canFinish(3, new int[][] {})); // true

        // Multiple courses, no cycle
        System.out.println(dcdg.canFinish(4, new int[][] { { 1, 0 }, { 2, 1 }, { 3, 2 } })); // true

        // Multiple courses, cycle
        System.out.println(dcdg.canFinish(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } })); // false

        // Single course
        System.out.println(dcdg.canFinish(1, new int[][] {})); // true

        // Large acyclic graph
        int n = 100;
        int[][] edges = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++)
            edges[i] = new int[] { i + 1, i };
        System.out.println(dcdg.canFinish(n, edges)); // true

        // Large cyclic graph
        edges[n - 1] = new int[] { 0, n - 1 };
        System.out.println(dcdg.canFinish(n, edges)); // false
    }
}
