package graphs.medium;

import java.util.*;

/**
 * LeetCode 207: Course Schedule
 * https://leetcode.com/problems/course-schedule/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 20+ interviews)
 *
 * Description: There are a total of numCourses courses you have to take,
 * labeled from 0 to numCourses - 1.
 * You are given an array prerequisites where prerequisites[i] = [ai, bi]
 * indicates that you must take course bi first if you want to take course ai.
 * Return true if you can finish all courses. Otherwise, return false.
 *
 * Constraints:
 * - 1 <= numCourses <= 2000
 * - 0 <= prerequisites.length <= 5000
 * - prerequisites[i].length == 2
 * - 0 <= ai, bi < numCourses
 * - All the pairs prerequisites[i] are unique.
 * 
 * Follow-up Questions:
 * 1. How would you return the actual course order?
 * 2. What if we need to detect which courses form a cycle?
 * 3. Can you solve it using Union-Find?
 */
public class CourseSchedule {

    // Approach 1: Topological Sort (Kahn's Algorithm) - O(V + E) time, O(V + E)
    // space
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // Build adjacency list and in-degree array
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] prereq : prerequisites) {
            int course = prereq[0];
            int prerequisite = prereq[1];
            graph.get(prerequisite).add(course);
            inDegree[course]++;
        }

        // Start with courses having no prerequisites
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        int processedCourses = 0;
        while (!queue.isEmpty()) {
            int current = queue.poll();
            processedCourses++;

            // Process all courses that depend on current course
            for (int dependent : graph.get(current)) {
                inDegree[dependent]--;
                if (inDegree[dependent] == 0) {
                    queue.offer(dependent);
                }
            }
        }

        return processedCourses == numCourses;
    }

    // Approach 2: DFS Cycle Detection - O(V + E) time, O(V + E) space
    public boolean canFinishDFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] prereq : prerequisites) {
            graph.get(prereq[1]).add(prereq[0]);
        }

        // 0: unvisited, 1: visiting, 2: visited
        int[] state = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0 && hasCycle(graph, state, i)) {
                return false;
            }
        }

        return true;
    }

    private boolean hasCycle(List<List<Integer>> graph, int[] state, int node) {
        if (state[node] == 1)
            return true; // Back edge found
        if (state[node] == 2)
            return false; // Already processed

        state[node] = 1; // Mark as visiting

        for (int neighbor : graph.get(node)) {
            if (hasCycle(graph, state, neighbor)) {
                return true;
            }
        }

        state[node] = 2; // Mark as visited
        return false;
    }

    // Follow-up: Return actual course order
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] prereq : prerequisites) {
            graph.get(prereq[1]).add(prereq[0]);
            inDegree[prereq[0]]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        int[] result = new int[numCourses];
        int index = 0;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            result[index++] = current;

            for (int dependent : graph.get(current)) {
                inDegree[dependent]--;
                if (inDegree[dependent] == 0) {
                    queue.offer(dependent);
                }
            }
        }

        return index == numCourses ? result : new int[0];
    }

    public static void main(String[] args) {
        CourseSchedule solution = new CourseSchedule();

        // Test Case 1: Can finish
        int[][] prereq1 = { { 1, 0 } };
        System.out.println("Can finish [1,0]: " + solution.canFinish(2, prereq1)); // true

        // Test Case 2: Cycle exists
        int[][] prereq2 = { { 1, 0 }, { 0, 1 } };
        System.out.println("Can finish [1,0],[0,1]: " + solution.canFinish(2, prereq2)); // false

        // Test Case 3: Complex case
        int[][] prereq3 = { { 1, 0 }, { 2, 0 }, { 3, 1 }, { 3, 2 } };
        System.out.println("Course order: " + Arrays.toString(solution.findOrder(4, prereq3)));
    }
}
