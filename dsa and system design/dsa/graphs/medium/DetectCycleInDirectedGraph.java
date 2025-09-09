package graphs.medium;

import java.util.*;

/**
 * LeetCode 207: Course Schedule
 * https://leetcode.com/problems/course-schedule/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Detect if a cycle exists in a directed graph.
 *
 * Constraints:
 * - 1 <= numCourses <= 2000
 * - 0 <= prerequisites.length <= 5000
 * 
 * Follow-up Questions:
 * 1. Can you implement both DFS and BFS (Kahn's algorithm) approaches?
 * 2. Can you return the cycle if one exists?
 * 3. How would you handle a graph with multiple connected components?
 */
public class DetectCycleInDirectedGraph {

    // Approach 1: DFS with coloring - O(V+E) time, O(V+E) space
    public boolean hasCycleDFS(int numCourses, int[][] prerequisites) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] prereq : prerequisites) {
            adj.get(prereq[1]).add(prereq[0]);
        }

        // 0: unvisited, 1: visiting, 2: visited
        int[] visited = new int[numCourses];

        // DFS from each unvisited node
        for (int i = 0; i < numCourses; i++) {
            if (visited[i] == 0) {
                if (dfs(i, adj, visited)) {
                    return true; // Cycle detected
                }
            }
        }

        return false;
    }

    private boolean dfs(int u, List<List<Integer>> adj, int[] visited) {
        visited[u] = 1; // Mark as visiting

        for (int v : adj.get(u)) {
            if (visited[v] == 1) {
                return true; // Cycle detected
            }
            if (visited[v] == 0) {
                if (dfs(v, adj, visited)) {
                    return true;
                }
            }
        }

        visited[u] = 2; // Mark as visited
        return false;
    }

    // Approach 2: BFS (Kahn's Algorithm) - O(V+E) time, O(V+E) space
    public boolean hasCycleBFS(int numCourses, int[][] prerequisites) {
        // Build adjacency list and calculate indegrees
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }

        int[] indegree = new int[numCourses];
        for (int[] prereq : prerequisites) {
            adj.get(prereq[1]).add(prereq[0]);
            indegree[prereq[0]]++;
        }

        // Add all nodes with indegree 0 to queue
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        int visitedCount = 0;
        while (!queue.isEmpty()) {
            int u = queue.poll();
            visitedCount++;

            for (int v : adj.get(u)) {
                indegree[v]--;
                if (indegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }

        // If not all nodes are visited, there's a cycle
        return visitedCount != numCourses;
    }

    // Follow-up 2: Return the cycle if one exists
    public List<Integer> findCycle(int numCourses, int[][] prerequisites) {
        // Build adjacency list
        List<List<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            adj.add(new ArrayList<>());
        }

        for (int[] prereq : prerequisites) {
            adj.get(prereq[1]).add(prereq[0]);
        }

        int[] visited = new int[numCourses];
        int[] parent = new int[numCourses];
        Arrays.fill(parent, -1);

        for (int i = 0; i < numCourses; i++) {
            if (visited[i] == 0) {
                List<Integer> cycle = findCycleDFS(i, adj, visited, parent);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        return new ArrayList<>();
    }

    private List<Integer> findCycleDFS(int u, List<List<Integer>> adj, int[] visited, int[] parent) {
        visited[u] = 1; // Visiting

        for (int v : adj.get(u)) {
            parent[v] = u;
            if (visited[v] == 1) {
                // Cycle detected, reconstruct it
                List<Integer> cycle = new ArrayList<>();
                int curr = u;
                while (curr != v) {
                    cycle.add(curr);
                    curr = parent[curr];
                }
                cycle.add(v);
                Collections.reverse(cycle);
                return cycle;
            }
            if (visited[v] == 0) {
                List<Integer> cycle = findCycleDFS(v, adj, visited, parent);
                if (cycle != null) {
                    return cycle;
                }
            }
        }

        visited[u] = 2; // Visited
        return null;
    }

    public static void main(String[] args) {
        DetectCycleInDirectedGraph dcdg = new DetectCycleInDirectedGraph();

        // Test case 1: No cycle
        int[][] prereqs1 = { { 1, 0 } };
        System.out.println("Has cycle (DFS) 1: " + dcdg.hasCycleDFS(2, prereqs1)); // false
        System.out.println("Has cycle (BFS) 1: " + dcdg.hasCycleBFS(2, prereqs1)); // false

        // Test case 2: Cycle
        int[][] prereqs2 = { { 1, 0 }, { 0, 1 } };
        System.out.println("Has cycle (DFS) 2: " + dcdg.hasCycleDFS(2, prereqs2)); // true
        System.out.println("Has cycle (BFS) 2: " + dcdg.hasCycleBFS(2, prereqs2)); // true

        // Test case 3: Find cycle
        System.out.println("Found cycle: " + dcdg.findCycle(2, prereqs2)); // [0, 1]

        // Test case 4: More complex graph with cycle
        int[][] prereqs4 = { { 0, 1 }, { 1, 2 }, { 2, 3 }, { 3, 1 } };
        System.out.println("Has cycle (DFS) 4: " + dcdg.hasCycleDFS(4, prereqs4)); // true
        System.out.println("Found cycle 4: " + dcdg.findCycle(4, prereqs4)); // [1, 2, 3]

        // Test case 5: Disconnected graph with cycle
        int[][] prereqs5 = { { 0, 1 }, { 2, 3 }, { 3, 2 } };
        System.out.println("Has cycle (DFS) 5: " + dcdg.hasCycleDFS(4, prereqs5)); // true
    }
}
