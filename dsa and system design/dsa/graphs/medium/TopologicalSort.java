package graphs.medium;

import java.util.*;

/**
 * LeetCode 210: Course Schedule II (Topological Sort)
 * https://leetcode.com/problems/course-schedule-ii/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Return order to finish all courses given prerequisites
 * (topological sort).
 *
 * Constraints:
 * - 1 <= numCourses <= 10^5
 * - prerequisites.length <= 5000
 * 
 * Follow-up Questions:
 * 1. Can you solve with DFS?
 * 2. Can you detect cycles?
 */
public class TopologicalSort {
    // Approach 1: BFS (Kahn's Algorithm) - O(V+E) time
    public int[] findOrder(int numCourses, int[][] prerequisites) {
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
        int[] order = new int[numCourses];
        int idx = 0;
        while (!q.isEmpty()) {
            int u = q.poll();
            order[idx++] = u;
            for (int v : adj.get(u))
                if (--indegree[v] == 0)
                    q.offer(v);
        }
        return idx == numCourses ? order : new int[0];
    }

    // Approach 2: DFS - O(V+E) time, O(V+E) space
    public int[] findOrderDFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++)
            adj.add(new ArrayList<>());
        for (int[] p : prerequisites)
            adj.get(p[1]).add(p[0]);
        int[] visited = new int[numCourses]; // 0=unvisited, 1=visiting, 2=visited
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < numCourses; i++)
            if (!dfs(i, adj, visited, order))
                return new int[0];
        Collections.reverse(order);
        return order.stream().mapToInt(x -> x).toArray();
    }

    private boolean dfs(int u, List<List<Integer>> adj, int[] visited, List<Integer> order) {
        if (visited[u] == 1)
            return false; // cycle
        if (visited[u] == 2)
            return true;
        visited[u] = 1;
        for (int v : adj.get(u))
            if (!dfs(v, adj, visited, order))
                return false;
        visited[u] = 2;
        order.add(u);
        return true;
    }

    // Helper: Detect cycle only (returns true if cycle exists)
    public boolean hasCycle(int numCourses, int[][] prerequisites) {
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++)
            adj.add(new ArrayList<>());
        for (int[] p : prerequisites)
            adj.get(p[1]).add(p[0]);
        int[] visited = new int[numCourses];
        for (int i = 0; i < numCourses; i++)
            if (cycleDFS(i, adj, visited))
                return true;
        return false;
    }

    private boolean cycleDFS(int u, List<List<Integer>> adj, int[] visited) {
        if (visited[u] == 1)
            return true;
        if (visited[u] == 2)
            return false;
        visited[u] = 1;
        for (int v : adj.get(u))
            if (cycleDFS(v, adj, visited))
                return true;
        visited[u] = 2;
        return false;
    }

    public static void main(String[] args) {
        TopologicalSort ts = new TopologicalSort();
        System.out.println(Arrays.toString(ts.findOrder(2, new int[][] { { 1, 0 } }))); // [0,1]
        System.out.println(Arrays.toString(ts.findOrder(2, new int[][] { { 1, 0 }, { 0, 1 } }))); // []

        // No prerequisites
        System.out.println(Arrays.toString(ts.findOrder(3, new int[][] {}))); // [0,1,2]

        // Multiple valid orders
        int[] order = ts.findOrder(4, new int[][] { { 1, 0 }, { 2, 0 }, { 3, 1 }, { 3, 2 } });
        System.out.println(Arrays.toString(order)); // [0,1,2,3] or [0,2,1,3]

        // Single course
        System.out.println(Arrays.toString(ts.findOrder(1, new int[][] {}))); // [0]

        // Disconnected graph
        System.out.println(Arrays.toString(ts.findOrder(3, new int[][] { { 1, 0 } }))); // [0,1,2] or [2,0,1]

        // DFS approach tests
        System.out.println(Arrays.toString(ts.findOrderDFS(2, new int[][] { { 1, 0 } }))); // [0,1]
        System.out.println(Arrays.toString(ts.findOrderDFS(2, new int[][] { { 1, 0 }, { 0, 1 } }))); // []
        System.out.println(Arrays.toString(ts.findOrderDFS(3, new int[][] {}))); // [0,1,2]
        int[] orderDFS = ts.findOrderDFS(4, new int[][] { { 1, 0 }, { 2, 0 }, { 3, 1 }, { 3, 2 } });
        System.out.println(Arrays.toString(orderDFS)); // [0,2,1,3] or [0,1,2,3]
        System.out.println(Arrays.toString(ts.findOrderDFS(1, new int[][] {}))); // [0]
        System.out.println(Arrays.toString(ts.findOrderDFS(3, new int[][] { { 1, 0 } }))); // [2,0,1] or [0,1,2]

        // Cycle detection tests
        System.out.println(ts.hasCycle(2, new int[][] { { 1, 0 } }) == false);
        System.out.println(ts.hasCycle(2, new int[][] { { 1, 0 }, { 0, 1 } }) == true);
        System.out.println(ts.hasCycle(3, new int[][] {})); // false
        System.out.println(ts.hasCycle(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } })); // true
    }
}
