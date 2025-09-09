package graphs.easy;

/**
 * LeetCode 1791: Find Center of Star Graph
 * https://leetcode.com/problems/find-center-of-star-graph/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given edges of a star graph, return the center node.
 *
 * Constraints:
 * - 3 <= n <= 10^5
 * - edges.length == n-1
 * 
 * Follow-up Questions:
 * 1. Can you solve in O(1) time?
 */
public class FindCenterOfStarGraph {
    // Approach 1: O(1) time, O(1) space
    public int findCenter(int[][] edges) {
        // The center is the only node appearing in both edges[0] and edges[1]
        return edges[0][0] == edges[1][0] || edges[0][0] == edges[1][1]
                ? edges[0][0]
                : edges[0][1];
    }

    public static void main(String[] args) {
        FindCenterOfStarGraph fc = new FindCenterOfStarGraph();
        // Basic test
        System.out.println(fc.findCenter(new int[][] { { 1, 2 }, { 2, 3 }, { 4, 2 } }) == 2);

        // Center is first element
        System.out.println(fc.findCenter(new int[][] { { 5, 1 }, { 5, 2 }, { 5, 3 }, { 5, 4 } }) == 5);

        // Center is second element
        System.out.println(fc.findCenter(new int[][] { { 1, 6 }, { 2, 6 }, { 3, 6 }, { 4, 6 }, { 5, 6 } }) == 6);

        // Large star graph
        int n = 1000;
        int[][] edges = new int[n - 1][2];
        for (int i = 0; i < n - 1; i++)
            edges[i] = new int[] { n, i + 1 };
        System.out.println(fc.findCenter(edges) == 1000);

        // Center is in both positions in first two edges
        System.out.println(fc.findCenter(new int[][] { { 7, 8 }, { 8, 9 }, { 8, 10 } }) == 8);

        // Minimal valid star graph
        System.out.println(fc.findCenter(new int[][] { { 2, 3 }, { 2, 1 } }) == 2);

        // Center is negative value
        System.out.println(fc.findCenter(new int[][] { { -5, 1 }, { -5, 2 }, { -5, 3 } }) == -5);

        // Center is zero
        System.out.println(fc.findCenter(new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 } }) == 0);

        // Center is at both positions in first edge
        System.out.println(fc.findCenter(new int[][] { { 4, 4 }, { 4, 5 }, { 4, 6 } }) == 4);
    }
}
