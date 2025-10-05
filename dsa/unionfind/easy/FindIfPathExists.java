package unionfind.easy;

import unionfind.UnionFind;

/**
 * LeetCode 1971: Find if Path Exists in Graph
 * https://leetcode.com/problems/find-if-path-exists-in-graph/
 *
 * Description:
 * There is a bi-directional graph with n vertices, where each vertex is labeled
 * from 0 to n - 1.
 * The edges in the graph are represented as a 2D integer array edges.
 * Given edges and the integers source and destination, return true if there is
 * a valid path
 * from source to destination, or false otherwise.
 *
 * Constraints:
 * - 1 <= n <= 2 * 10^5
 * - 0 <= edges.length <= 2 * 10^5
 * - edges[i].length == 2
 * - 0 <= ui, vi <= n - 1
 * - ui != vi
 * - 0 <= source, destination <= n - 1
 *
 * Visual Example:
 * n = 3, edges = [[0,1],[1,2],[2,0]], source = 0, destination = 2
 * 
 * 0---1
 * | |
 * +---2
 * 
 * Output: true (path exists: 0->2 or 0->1->2)
 *
 * Follow-up:
 * - Can you solve it using DFS/BFS as well?
 * - What if edges are added dynamically?
 */
public class FindIfPathExists {

    public boolean validPath(int n, int[][] edges, int source, int destination) {
        UnionFind uf = new UnionFind(n);

        for (int[] edge : edges) {
            uf.union(edge[0], edge[1]);
        }

        return uf.connected(source, destination);
    }

    public static void main(String[] args) {
        FindIfPathExists solution = new FindIfPathExists();

        // Test case 1: Path exists
        System.out.println(solution.validPath(3, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 0 } }, 0, 2)); // true

        // Test case 2: No path
        System.out
                .println(solution.validPath(6, new int[][] { { 0, 1 }, { 0, 2 }, { 3, 5 }, { 5, 4 }, { 4, 3 } }, 0, 5)); // false

        // Test case 3: Same source and destination
        System.out.println(solution.validPath(1, new int[][] {}, 0, 0)); // true

        // Test case 4: Direct connection
        System.out.println(solution.validPath(2, new int[][] { { 0, 1 } }, 0, 1)); // true

        // Test case 5: No edges, different nodes
        System.out.println(solution.validPath(3, new int[][] {}, 0, 2)); // false
    }
}
