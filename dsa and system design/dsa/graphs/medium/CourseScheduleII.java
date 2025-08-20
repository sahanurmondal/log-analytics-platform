package graphs.medium;

import java.util.*;

/**
 * LeetCode 210: Course Schedule II
 * https://leetcode.com/problems/course-schedule-ii/
 *
 * Description:
 * There are a total of numCourses courses you have to take, labeled from 0 to
 * numCourses - 1.
 * You are given an array prerequisites where prerequisites[i] = [ai, bi]
 * indicates that you must take course bi first if you want to take course ai.
 * Return the ordering of courses you should take to finish all courses. If
 * there are many valid answers, return any of them.
 * If it is impossible to finish all courses, return an empty array.
 *
 * Constraints:
 * - 1 <= numCourses <= 2000
 * - 0 <= prerequisites.length <= numCourses * (numCourses - 1)
 * - prerequisites[i].length == 2
 * - 0 <= ai, bi < numCourses
 *
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class CourseScheduleII {

    // Approach 1: Topological Sort (Kahn's Algorithm) - O(V+E) time, O(V+E) space
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] indegree = new int[numCourses];

        // Build graph
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] prerequisite : prerequisites) {
            int course = prerequisite[0];
            int prereq = prerequisite[1];
            graph.get(prereq).add(course);
            indegree[course]++;
        }

        // Start with courses having no prerequisites
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }

        List<Integer> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            int course = queue.poll();
            result.add(course);

            for (int nextCourse : graph.get(course)) {
                indegree[nextCourse]--;
                if (indegree[nextCourse] == 0) {
                    queue.offer(nextCourse);
                }
            }
        }

        return result.size() == numCourses ? result.stream().mapToInt(i -> i).toArray() : new int[0];
    }

    // Approach 2: DFS - O(V+E) time, O(V+E) space
    public int[] findOrderDFS(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        for (int[] prerequisite : prerequisites) {
            graph.get(prerequisite[1]).add(prerequisite[0]);
        }

        // 0: unvisited, 1: visiting, 2: visited
        int[] state = new int[numCourses];
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < numCourses; i++) {
            if (state[i] == 0 && !dfs(graph, i, state, result)) {
                return new int[0];
            }
        }

        Collections.reverse(result);
        return result.stream().mapToInt(i -> i).toArray();
    }

    private boolean dfs(List<List<Integer>> graph, int course, int[] state, List<Integer> result) {
        if (state[course] == 1)
            return false; // Cycle detected
        if (state[course] == 2)
            return true; // Already processed

        state[course] = 1; // Mark as visiting

        for (int nextCourse : graph.get(course)) {
            if (!dfs(graph, nextCourse, state, result)) {
                return false;
            }
        }

        state[course] = 2; // Mark as visited
        result.add(course);
        return true;
    }

    public static void main(String[] args) {
        CourseScheduleII solution = new CourseScheduleII();

        System.out.println("=== Course Schedule II Test Cases ===");

        // Test Case 1
        int[][] prerequisites1 = { { 1, 0 } };
        System.out.println("Test 1 - numCourses: 2, prerequisites: [[1,0]]");
        System.out.println("Topological Sort: " + Arrays.toString(solution.findOrder(2, prerequisites1)));
        System.out.println("DFS: " + Arrays.toString(solution.findOrderDFS(2, prerequisites1)));
        System.out.println("Expected: [0,1]\n");

        // Test Case 2
        int[][] prerequisites2 = { { 1, 0 }, { 2, 0 }, { 3, 1 }, { 3, 2 } };
        System.out.println("Test 2 - numCourses: 4, prerequisites: [[1,0],[2,0],[3,1],[3,2]]");
        System.out.println("Topological Sort: " + Arrays.toString(solution.findOrder(4, prerequisites2)));
        System.out.println("Expected: [0,1,2,3] or [0,2,1,3]\n");

        // Test Case 3: Impossible
        int[][] prerequisites3 = { { 1, 0 }, { 0, 1 } };
        System.out.println("Test 3 - numCourses: 2, prerequisites: [[1,0],[0,1]]");
        System.out.println("Topological Sort: " + Arrays.toString(solution.findOrder(2, prerequisites3)));
        System.out.println("Expected: []\n");
    }
}
