package unionfind.medium;

import unionfind.UnionFind;

/**
 * LeetCode 547: Number of Provinces (Friend Circles)
 * https://leetcode.com/problems/number-of-provinces/
 *
 * Description:
 * There are n cities. Some of them are connected, while some are not.
 * If city a is connected directly with city b, and city b is connected directly
 * with city c,
 * then city a is connected indirectly with city c.
 * A province is a group of directly or indirectly connected cities and no other
 * cities outside of the group.
 * You are given an n x n matrix isConnected where isConnected[i][j] = 1 if the
 * ith city and the jth city
 * are directly connected, and isConnected[i][j] = 0 otherwise.
 * Return the total number of provinces.
 *
 * Constraints:
 * - 1 <= n <= 200
 * - n == isConnected.length
 * - n == isConnected[i].length
 * - isConnected[i][j] is 1 or 0
 * - isConnected[i][i] == 1
 * - isConnected[i][j] == isConnected[j][i]
 *
 * Visual Example:
 * Input: isConnected = [[1,1,0],[1,1,0],[0,0,1]]
 * 
 * City connections:
 * 0---1 2
 * 
 * Output: 2 (two provinces: {0,1} and {2})
 *
 * Follow-up:
 * - Can you solve it using DFS/BFS as well?
 * - How would you handle dynamic friend additions?
 */
public class FriendCircles {

    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        UnionFind uf = new UnionFind(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) {
                    uf.union(i, j);
                }
            }
        }

        return uf.getComponents();
    }

    public static void main(String[] args) {
        FriendCircles solution = new FriendCircles();

        // Test case 1: Two provinces
        System.out.println(solution.findCircleNum(new int[][] { { 1, 1, 0 }, { 1, 1, 0 }, { 0, 0, 1 } })); // 2

        // Test case 2: All separate
        System.out.println(solution.findCircleNum(new int[][] { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } })); // 3

        // Test case 3: All connected
        System.out.println(solution.findCircleNum(new int[][] { { 1, 1, 1 }, { 1, 1, 1 }, { 1, 1, 1 } })); // 1

        // Test case 4: Single city
        System.out.println(solution.findCircleNum(new int[][] { { 1 } })); // 1

        // Test case 5: Chain connection
        System.out.println(
                solution.findCircleNum(new int[][] { { 1, 1, 0, 0 }, { 1, 1, 1, 0 }, { 0, 1, 1, 0 }, { 0, 0, 0, 1 } })); // 2
    }
}
