package graphs.hard;

import java.util.*;

/**
 * LeetCode 1584: Minimum Cost to Connect All Points
 * https://leetcode.com/problems/min-cost-to-connect-all-points/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given points in 2D, connect all points with minimum cost (MST).
 *
 * Constraints:
 * - 1 <= points.length <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you solve with Kruskal's algorithm?
 * 2. Can you solve with Prim's algorithm?
 */
public class MinimumCostToConnectAllPoints {
    // Approach 1: Prim's Algorithm - O(n^2) time, O(n) space
    public int minCostConnectPoints(int[][] points) {
        int n = points.length, res = 0;
        boolean[] vis = new boolean[n];
        int[] minDist = new int[n];
        Arrays.fill(minDist, Integer.MAX_VALUE);
        minDist[0] = 0;
        for (int i = 0; i < n; i++) {
            int u = -1;
            for (int j = 0; j < n; j++)
                if (!vis[j] && (u == -1 || minDist[j] < minDist[u]))
                    u = j;
            vis[u] = true;
            res += minDist[u];
            for (int v = 0; v < n; v++)
                if (!vis[v])
                    minDist[v] = Math.min(minDist[v],
                            Math.abs(points[u][0] - points[v][0]) + Math.abs(points[u][1] - points[v][1]));
        }
        return res;
    }

    // Approach 2: Kruskal's Algorithm
    // ...implement if needed...
    public static void main(String[] args) {
        MinimumCostToConnectAllPoints mccap = new MinimumCostToConnectAllPoints();
        int[][] points = {{0,0},{2,2},{3,10},{5,2},{7,0}};
        System.out.println(mccap.minCostConnectPoints(points) == 20);

        // Single point
        int[][] points2 = {{1,1}};
        System.out.println(mccap.minCostConnectPoints(points2) == 0);

        // Two points
        int[][] points3 = {{1,1},{4,5}};
        System.out.println(mccap.minCostConnectPoints(points3) == 7);

        // All points in a line
        int[][] points4 = {{0,0},{0,1},{0,2},{0,3}};
        System.out.println(mccap.minCostConnectPoints(points4) == 3);

        // Large input
        int n = 100;
        int[][] points5 = new int[n][2];
        for (int i = 0; i < n; i++) points5[i] = new int[]{i,0};
        System.out.println(mccap.minCostConnectPoints(points5) == n-1);
    }
}
