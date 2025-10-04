package backtracking.medium;

import java.util.*;

/**
 * LeetCode 797: All Paths From Source to Target
 * https://leetcode.com/problems/all-paths-from-source-to-target/
 *
 * Description: Given a directed acyclic graph (DAG) of n nodes labeled from 0
 * to n - 1,
 * find all possible paths from node 0 to node n - 1 and return them in any
 * order.
 *
 * Constraints:
 * - n == graph.length
 * - 2 <= n <= 15
 * - 0 <= graph[i].length <= n - 1
 * - graph[i][j] is in the range [0, n - 1]
 * - All the elements of graph[i] are unique
 * - The input graph is guaranteed to be a DAG
 *
 * Follow-up:
 * - Can you solve it iteratively?
 *
 * Time Complexity: O(2^n * n)
 * Space Complexity: O(2^n * n)
 *
 * Company Tags: Google, Facebook, Amazon
 */
public class AllPathsFromSourceToTarget {

    public List<List<Integer>> allPathsSourceTarget(int[][] graph) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> path = new ArrayList<>();
        path.add(0);

        dfs(graph, 0, graph.length - 1, path, result);
        return result;
    }

    private void dfs(int[][] graph, int node, int target, List<Integer> path, List<List<Integer>> result) {
        if (node == target) {
            result.add(new ArrayList<>(path));
            return;
        }

        for (int neighbor : graph[node]) {
            path.add(neighbor);
            dfs(graph, neighbor, target, path, result);
            path.remove(path.size() - 1);
        }
    }

    // Alternative solution - Iterative BFS
    public List<List<Integer>> allPathsSourceTargetBFS(int[][] graph) {
        List<List<Integer>> result = new ArrayList<>();
        Queue<List<Integer>> queue = new LinkedList<>();

        queue.offer(Arrays.asList(0));

        while (!queue.isEmpty()) {
            List<Integer> path = queue.poll();
            int node = path.get(path.size() - 1);

            if (node == graph.length - 1) {
                result.add(path);
            } else {
                for (int neighbor : graph[node]) {
                    List<Integer> newPath = new ArrayList<>(path);
                    newPath.add(neighbor);
                    queue.offer(newPath);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        AllPathsFromSourceToTarget solution = new AllPathsFromSourceToTarget();

        // Test Case 1
        int[][] graph1 = { { 1, 2 }, { 3 }, { 3 }, {} };
        System.out.println(solution.allPathsSourceTarget(graph1)); // Expected: [[0,1,3],[0,2,3]]

        // Test Case 2
        int[][] graph2 = { { 4, 3, 1 }, { 3, 2, 4 }, { 3 }, { 4 }, {} };
        System.out.println(solution.allPathsSourceTarget(graph2)); // Expected:
                                                                   // [[0,4],[0,3,4],[0,1,3,4],[0,1,2,3,4],[0,1,4]]

        // Test Case 3
        int[][] graph3 = { { 1 }, { 2 }, { 3 }, {} };
        System.out.println(solution.allPathsSourceTarget(graph3)); // Expected: [[0,1,2,3]]
    }
}
