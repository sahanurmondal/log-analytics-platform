package heap.hard;

import java.util.PriorityQueue;

/**
 * LeetCode 407: Trapping Rain Water II
 * https://leetcode.com/problems/trapping-rain-water-ii/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: Hard
 *
 * Description:
 * Given an `m x n` integer matrix `heightMap` representing the height of each
 * unit cell in a 2D elevation map, return the volume of water it can trap after
 * raining.
 *
 * Constraints:
 * - m == heightMap.length
 * - n == heightMap[i].length
 * - 1 <= m, n <= 200
 * - 0 <= heightMap[i][j] <= 2 * 10^4
 * 
 * Follow-up Questions:
 * 1. Why does this problem require a different approach than the 1D Trapping
 * Rain Water?
 * 2. Explain the "wall" and "water level" concepts in the heap-based solution.
 * 3. What is the time and space complexity?
 */
public class TrappingRainWaterII {

    // Approach 1: Min-Heap (Dijkstra-like) - O(m*n log(m+n)) time, O(m*n) space
    public int trapRainWater(int[][] heightMap) {
        if (heightMap == null || heightMap.length <= 2 || heightMap[0].length <= 2) {
            return 0;
        }

        int m = heightMap.length;
        int n = heightMap[0].length;
        boolean[][] visited = new boolean[m][n];

        // Min-heap to store cells on the border, prioritized by height.
        // Cell is {height, row, col}
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> a[0] - b[0]);

        // 1. Add all border cells to the heap and mark as visited.
        // These cells form the initial "wall".
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (i == 0 || i == m - 1 || j == 0 || j == n - 1) {
                    minHeap.offer(new int[] { heightMap[i][j], i, j });
                    visited[i][j] = true;
                }
            }
        }

        int water = 0;
        int[][] dirs = { { 0, 1 }, { 0, -1 }, { 1, 0 }, { -1, 0 } };

        // 2. Process cells from the heap, starting with the lowest border cell.
        while (!minHeap.isEmpty()) {
            int[] cell = minHeap.poll();
            int height = cell[0];
            int row = cell[1];
            int col = cell[2];

            // Explore neighbors of the current cell.
            for (int[] dir : dirs) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n && !visited[newRow][newCol]) {
                    // The amount of water trapped at (newRow, newCol) is determined by the
                    // current "water level" (height of the cell we just polled).
                    // If the neighbor is lower, it traps water.
                    water += Math.max(0, height - heightMap[newRow][newCol]);

                    // Add the neighbor to the heap. Its height for the purpose of the "wall"
                    // is the maximum of its own height and the current water level.
                    minHeap.offer(new int[] { Math.max(height, heightMap[newRow][newCol]), newRow, newCol });
                    visited[newRow][newCol] = true;
                }
            }
        }

        return water;
    }

    public static void main(String[] args) {
        TrappingRainWaterII solution = new TrappingRainWaterII();

        // Test case 1
        int[][] heightMap1 = {
                { 1, 4, 3, 1, 3, 2 },
                { 3, 2, 1, 3, 2, 4 },
                { 2, 3, 3, 2, 3, 1 }
        };
        System.out.println("Trapped water 1: " + solution.trapRainWater(heightMap1)); // 4

        // Test case 2
        int[][] heightMap2 = {
                { 3, 3, 3, 3, 3 },
                { 3, 2, 2, 2, 3 },
                { 3, 2, 1, 2, 3 },
                { 3, 2, 2, 2, 3 },
                { 3, 3, 3, 3, 3 }
        };
        System.out.println("Trapped water 2: " + solution.trapRainWater(heightMap2)); // 10

        // Test case 3: No water trapped
        int[][] heightMap3 = {
                { 12, 13, 1, 12 },
                { 13, 4, 13, 12 },
                { 13, 8, 10, 12 },
                { 12, 13, 12, 12 },
                { 13, 13, 13, 13 }
        };
        System.out.println("Trapped water 3: " + solution.trapRainWater(heightMap3)); // 14
    }
}
