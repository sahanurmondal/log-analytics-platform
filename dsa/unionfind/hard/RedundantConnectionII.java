package unionfind.hard;

import unionfind.UnionFind;
import java.util.*;

/**
 * LeetCode 685: Redundant Connection II
 * https://leetcode.com/problems/redundant-connection-ii/
 *
 * Description:
 * In this problem, a rooted tree is a directed graph such that, there is
 * exactly one node
 * (the root) for which all other nodes are descendants of this node, plus every
 * node has
 * exactly one parent, except for the root node which has no parents.
 * The given input is a directed graph that started as a rooted tree with n
 * nodes
 * (with distinct values from 1 to n), with one additional directed edge added.
 * The added edge has two different vertices chosen from 1 to n, and was not an
 * edge
 * that already existed.
 * Return an edge that can be removed so that the resulting graph is a rooted
 * tree of n nodes.
 *
 * Constraints:
 * - n == edges.length
 * - 3 <= n <= 1000
 * - edges[i].length == 2
 * - 1 <= ai, bi <= n
 * - ai != bi
 */
public class RedundantConnectionII {

    public int[] findRedundantDirectedConnection(int[][] edges) {
        int n = edges.length;
        int[] parent = new int[n + 1];
        int[] candidate1 = null, candidate2 = null;

        // Find node with two parents
        for (int[] edge : edges) {
            int u = edge[0], v = edge[1];
            if (parent[v] == 0) {
                parent[v] = u;
            } else {
                candidate1 = new int[] { parent[v], v };
                candidate2 = new int[] { u, v };
                edge[1] = 0; // invalidate this edge
                break;
            }
        }

        // Union Find to detect cycle
        UnionFind uf = new UnionFind(n+1);
        for (int[] edge : edges) {
            if (edge[1] == 0)
                continue; // skip invalidated edge
            if (!uf.union(edge[0], edge[1])) {
                if (candidate1 == null) {
                    return edge;
                }
                return candidate1;
            }
        }

        return candidate2;
    }

    public static void main(String[] args) {
        RedundantConnectionII solution = new RedundantConnectionII();

        System.out.println("=== LeetCode 685: Redundant Connection II Test Cases ===\n");

        // Case 1: Node with two parents, NO cycle
        // Tree: 1→2→3, but also 1→3 (node 3 has two parents: 2 and 1)
        // Remove the second parent edge [1,3] to get valid tree: 1→2→3
        System.out.println("Case 1: Node with two parents, NO cycle");
        int[][] case1 = { { 1, 2 }, { 1, 3 }, { 2, 3 } };
        System.out.println("Input: " + Arrays.deepToString(case1));
        System.out.println("Expected: [1,3] (second parent edge - candidate2)");
        System.out.println("Actual: " + Arrays.toString(solution.findRedundantDirectedConnection(case1)));
        System.out.println("Returns candidate2 because:");
        System.out.println("  - Node 3 has two parents: 1 and 2");
        System.out.println("  - After invalidating [1,3], edges [1,2] and [2,3] form valid tree");
        System.out.println("  - No cycle detected, so return candidate2 = [1,3]");
        System.out.println();

        // Case 1b: Another example where candidate2 is returned
        System.out.println("Case 1b: Node with two parents, NO cycle (different structure)");
        int[][] case1b = { { 1, 2 }, { 2, 3 }, { 1, 3 } };
        System.out.println("Input: " + Arrays.deepToString(case1b));
        System.out.println("Expected: [1,3] (second parent edge - candidate2)");
        System.out.println("Actual: " + Arrays.toString(solution.findRedundantDirectedConnection(case1b)));
        System.out.println("Returns candidate2 because:");
        System.out.println("  - Node 3 has two parents: 2 and 1");
        System.out.println("  - After invalidating [1,3], edges [1,2] and [2,3] form valid tree");
        System.out.println("  - No cycle detected, so return candidate2 = [1,3]");
        System.out.println();

        // Case 2: Simple cycle, NO node with two parents
        // Cycle: 1→2→3→1, each node has exactly one parent but forms cycle
        // Remove any edge in the cycle (return the one that closes it)
        System.out.println("Case 2: Simple cycle, NO node with two parents");
        int[][] case2 = { { 1, 2 }, { 2, 3 }, { 3, 1 } };
        System.out.println("Input: " + Arrays.deepToString(case2));
        System.out.println("Expected: [3,1] (edge that closes cycle)");
        System.out.println("Actual: " + Arrays.toString(solution.findRedundantDirectedConnection(case2)));
        System.out.println();

        // Case 3: Both node with two parents AND cycle
        // Complex: 1→2→3→4→1 (cycle) + 1→5 + extra edge 2→1 creates both issues
        // Node 1 has two parents: 4 and 2
        // There's also a cycle involving nodes 1,2,3,4
        // Must remove first parent edge [4,1] to break both issues
        System.out.println("Case 3: Both node with two parents AND cycle");
        int[][] case3 = { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 4, 1 }, { 1, 5 } };
        System.out.println("Input: " + Arrays.deepToString(case3));
        System.out.println("Expected: [4,1] (first parent edge that causes cycle)");
        System.out.println("Actual: " + Arrays.toString(solution.findRedundantDirectedConnection(case3)));
        System.out.println();

        // Additional edge case: Multiple candidates but different structure
        System.out.println("Additional Cases:");

        // Case 4: Node with two parents at the end
        int[][] case4 = { { 2, 1 }, { 3, 1 }};
        System.out.println("Case 4: " + Arrays.toString(solution.findRedundantDirectedConnection(case4)));

        // Case 5: Root node gets second parent
        int[][] case5 = { { 1, 2 }, { 1, 3 }, { 3, 4 }, { 3, 1 } };
        System.out.println("Case 5: " + Arrays.toString(solution.findRedundantDirectedConnection(case5)));
    }
}
