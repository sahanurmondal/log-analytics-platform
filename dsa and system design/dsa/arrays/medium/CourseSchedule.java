package arrays.medium;

import java.util.*;

/**
 * LeetCode 207: Course Schedule
 * https://leetcode.com/problems/course-schedule/
 *
 * Description:
 * There are a total of numCourses courses you have to take, labeled from 0 to
 * numCourses - 1.
 * You are given an array prerequisites where prerequisites[i] = [ai, bi]
 * indicates that you must
 * take course bi first if you want to take course ai. Return true if you can
 * finish all courses.
 *
 * Constraints:
 * - 1 <= numCourses <= 10^5
 * - 0 <= prerequisites.length <= 5000
 * - prerequisites[i].length == 2
 * - 0 <= ai, bi < numCourses
 * - All pairs prerequisites[i] are unique
 *
 * Follow-up:
 * - Can you solve it using BFS (Kahn's algorithm)?
 * 
 * Time Complexity: O(V + E) where V = numCourses, E = prerequisites.length
 * Space Complexity: O(V + E)
 * 
 * Algorithm:
 * 1. Build adjacency list and calculate in-degrees
 * 2. Use BFS starting from nodes with 0 in-degree
 * 3. Process nodes and reduce in-degrees of neighbors
 * 4. Check if all nodes are processed (no cycle)
 */
public class CourseSchedule {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        List<List<Integer>> graph = new ArrayList<>();
        int[] inDegree = new int[numCourses];

        // Initialize graph
        for (int i = 0; i < numCourses; i++) {
            graph.add(new ArrayList<>());
        }

        // Build graph and calculate in-degrees
        for (int[] prereq : prerequisites) {
            graph.get(prereq[1]).add(prereq[0]);
            inDegree[prereq[0]]++;
        }

        // BFS with nodes having 0 in-degree
        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < numCourses; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
            }
        }

        int processed = 0;
        while (!queue.isEmpty()) {
            int course = queue.poll();
            processed++;

            for (int neighbor : graph.get(course)) {
                inDegree[neighbor]--;
                if (inDegree[neighbor] == 0) {
                    queue.offer(neighbor);
                }
            }
        }

        return processed == numCourses;
    }

    public static void main(String[] args) {
        CourseSchedule solution = new CourseSchedule();

        // Test Case 1: Normal case - possible
        System.out.println(solution.canFinish(2, new int[][] { { 1, 0 } })); // Expected: true

        // Test Case 2: Edge case - cycle
        System.out.println(solution.canFinish(2, new int[][] { { 1, 0 }, { 0, 1 } })); // Expected: false

        // Test Case 3: Corner case - no prerequisites
        System.out.println(solution.canFinish(3, new int[][] {})); // Expected: true

        // Test Case 4: Large input - chain
        System.out.println(solution.canFinish(4, new int[][] { { 1, 0 }, { 2, 1 }, { 3, 2 } })); // Expected: true

        // Test Case 5: Minimum input - single course
        System.out.println(solution.canFinish(1, new int[][] {})); // Expected: true

        // Test Case 6: Special case - self-loop
        System.out.println(solution.canFinish(1, new int[][] { { 0, 0 } })); // Expected: false

        // Test Case 7: Boundary case - complex valid
        System.out.println(solution.canFinish(4, new int[][] { { 1, 0 }, { 2, 0 }, { 3, 1 }, { 3, 2 } })); // Expected:
                                                                                                           // true

        // Test Case 8: Multiple cycles
        System.out.println(solution.canFinish(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } })); // Expected: false

        // Test Case 9: Disconnected components
        System.out.println(solution.canFinish(4, new int[][] { { 1, 0 }, { 3, 2 } })); // Expected: true

        // Test Case 10: Complex dependency
        System.out.println(solution.canFinish(5, new int[][] { { 1, 4 }, { 2, 4 }, { 3, 1 }, { 3, 2 } })); // Expected:
                                                                                                           // true
    }
}
