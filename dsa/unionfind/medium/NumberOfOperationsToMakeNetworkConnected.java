package unionfind.medium;

import unionfind.UnionFind;

/**
 * LeetCode 1319: Number of Operations to Make Network Connected
 * https://leetcode.com/problems/number-of-operations-to-make-network-connected/
 *
 * Description:
 * You are given n computers numbered from 0 to n-1 connected by ethernet cables
 * represented as connections
 * where connections[i] = [a, b] represents a connection between computers a and
 * b.
 * Any computer can reach any other computer directly or indirectly through the
 * network.
 * Given an initial computer network connections, you can extract certain cables
 * between two directly
 * connected computers and place them between any pair of disconnected computers
 * to make them directly connected.
 * Return the minimum number of times you need to do this to make all the
 * computers connected.
 * If it's not possible, return -1.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 1 <= connections.length <= min(n*(n-1)/2, 10^5)
 * - connections[i].length == 2
 * - 0 <= connections[i][0], connections[i][1] < n
 * - connections[i][0] != connections[i][1]
 * - There are no repeated connections.
 *
 * Visual Example:
 * n = 4, connections = [[0,1],[0,2],[1,2]]
 * 
 * Initial: 0---1---2 3 (isolated)
 * |___|
 * 
 * We have 1 extra cable (cycle between 0,1,2)
 * Need 1 operation to connect component {0,1,2} with {3}
 * 
 * Output: 1
 *
 * Follow-up:
 * - Can you solve it without Union-Find?
 * - How would you handle weighted edges?
 */
public class NumberOfOperationsToMakeNetworkConnected {

    public int makeConnected(int n, int[][] connections) {
        // Need at least n-1 cables to connect n computers
        if (connections.length < n - 1) {
            return -1;
        }

        UnionFind uf = new UnionFind(n);
        int redundantCables = 0;

        for (int[] connection : connections) {
            if (!uf.union(connection[0], connection[1])) {
                redundantCables++;
            }
        }
        // Need (components - 1) operations to connect all components
        return uf.getComponents() - 1;
    }

    public static void main(String[] args) {
        NumberOfOperationsToMakeNetworkConnected solution = new NumberOfOperationsToMakeNetworkConnected();

        // Test case 1: Basic example
        System.out.println(solution.makeConnected(4, new int[][] { { 0, 1 }, { 0, 2 }, { 1, 2 } })); // 1

        // Test case 2: Not enough cables
        System.out.println(solution.makeConnected(6, new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 2 } })); // -1

        // Test case 3: Already connected
        System.out.println(solution.makeConnected(6, new int[][] { { 0, 1 }, { 0, 2 }, { 0, 3 }, { 1, 2 }, { 1, 3 } })); // 2

        // Test case 4: All isolated
        System.out.println(solution.makeConnected(5, new int[][] {})); // -1

        // Test case 5: Chain connection
        System.out.println(solution.makeConnected(4, new int[][] { { 0, 1 }, { 1, 2 }, { 2, 3 } })); // 0
    }
}
