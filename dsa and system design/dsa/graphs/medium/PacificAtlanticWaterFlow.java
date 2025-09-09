package graphs.medium;

import java.util.*;

/**
 * LeetCode 417: Pacific Atlantic Water Flow
 * https://leetcode.com/problems/pacific-atlantic-water-flow/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description: There is an m x n rectangular island that borders both the
 * Pacific Ocean and Atlantic Ocean.
 * The Pacific Ocean touches the island's left and top edges, and the Atlantic
 * Ocean touches the island's
 * right and bottom edges. The island is partitioned into a grid of square
 * cells. You are given an m x n
 * integer matrix heights where heights[r][c] represents the height above sea
 * level of the cell at coordinate (r, c).
 * 
 * The island receives a lot of rain, and the rain water can flow to neighboring
 * cells directly north, south,
 * east, and west if the neighboring cell's height is less than or equal to the
 * current cell's height.
 * Water can flow from any cell adjacent to an ocean into the ocean.
 * 
 * Return a 2D list of grid coordinates result where result[i] = [ri, ci]
 * denotes that rain water can flow
 * from cell (ri, ci) to both the Pacific and Atlantic oceans.
 *
 * Constraints:
 * - m == heights.length
 * - n == heights[r].length
 * - 1 <= m, n <= 200
 * - 0 <= heights[r][c] <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you optimize for sparse grids?
 * 2. How would you handle dynamic height changes?
 * 3. Can you find the path with minimum height difference?
 */
public class PacificAtlanticWaterFlow {

    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    // Approach 1: DFS from Ocean Borders - O(M*N) time, O(M*N) space
    public List<List<Integer>> pacificAtlantic(int[][] heights) {
        List<List<Integer>> result = new ArrayList<>();
        if (heights == null || heights.length == 0 || heights[0].length == 0) {
            return result;
        }

        int m = heights.length, n = heights[0].length;
        boolean[][] pacific = new boolean[m][n];
        boolean[][] atlantic = new boolean[m][n];

        // DFS from Pacific borders (top and left edges)
        for (int i = 0; i < m; i++) {
            dfs(heights, pacific, i, 0, heights[i][0]); // Left edge
        }
        for (int j = 0; j < n; j++) {
            dfs(heights, pacific, 0, j, heights[0][j]); // Top edge
        }

        // DFS from Atlantic borders (bottom and right edges)
        for (int i = 0; i < m; i++) {
            dfs(heights, atlantic, i, n - 1, heights[i][n - 1]); // Right edge
        }
        for (int j = 0; j < n; j++) {
            dfs(heights, atlantic, m - 1, j, heights[m - 1][j]); // Bottom edge
        }

        // Find cells that can reach both oceans
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (pacific[i][j] && atlantic[i][j]) {
                    result.add(Arrays.asList(i, j));
                }
            }
        }

        return result;
    }

    private void dfs(int[][] heights, boolean[][] visited, int row, int col, int prevHeight) {
        int m = heights.length, n = heights[0].length;

        // Check bounds and conditions
        if (row < 0 || row >= m || col < 0 || col >= n ||
                visited[row][col] || heights[row][col] < prevHeight) {
            return;
        }

        visited[row][col] = true;

        // Explore all four directions
        for (int[] dir : DIRECTIONS) {
            dfs(heights, visited, row + dir[0], col + dir[1], heights[row][col]);
        }
    }

    // Approach 2: BFS from Ocean Borders - O(M*N) time, O(M*N) space
    public List<List<Integer>> pacificAtlanticBFS(int[][] heights) {
        List<List<Integer>> result = new ArrayList<>();
        if (heights == null || heights.length == 0 || heights[0].length == 0) {
            return result;
        }

        int m = heights.length, n = heights[0].length;
        boolean[][] pacific = new boolean[m][n];
        boolean[][] atlantic = new boolean[m][n];

        Queue<int[]> pacificQueue = new LinkedList<>();
        Queue<int[]> atlanticQueue = new LinkedList<>();

        // Add Pacific border cells to queue
        for (int i = 0; i < m; i++) {
            pacificQueue.offer(new int[] { i, 0 });
            pacific[i][0] = true;
        }
        for (int j = 1; j < n; j++) {
            pacificQueue.offer(new int[] { 0, j });
            pacific[0][j] = true;
        }

        // Add Atlantic border cells to queue
        for (int i = 0; i < m; i++) {
            atlanticQueue.offer(new int[] { i, n - 1 });
            atlantic[i][n - 1] = true;
        }
        for (int j = 0; j < n - 1; j++) {
            atlanticQueue.offer(new int[] { m - 1, j });
            atlantic[m - 1][j] = true;
        }

        // BFS from Pacific
        bfs(heights, pacificQueue, pacific);

        // BFS from Atlantic
        bfs(heights, atlanticQueue, atlantic);

        // Find cells that can reach both oceans
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (pacific[i][j] && atlantic[i][j]) {
                    result.add(Arrays.asList(i, j));
                }
            }
        }

        return result;
    }

    private void bfs(int[][] heights, Queue<int[]> queue, boolean[][] visited) {
        int m = heights.length, n = heights[0].length;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int row = curr[0], col = curr[1];

            for (int[] dir : DIRECTIONS) {
                int newRow = row + dir[0];
                int newCol = col + dir[1];

                if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                        !visited[newRow][newCol] && heights[newRow][newCol] >= heights[row][col]) {
                    visited[newRow][newCol] = true;
                    queue.offer(new int[] { newRow, newCol });
                }
            }
        }
    }

    // Test cases
    public static void main(String[] args) {
        PacificAtlanticWaterFlow solution = new PacificAtlanticWaterFlow();

        // Test case 1: Basic example
        int[][] heights1 = {
                { 1, 2, 2, 3, 5 },
                { 3, 2, 3, 4, 4 },
                { 2, 4, 5, 3, 1 },
                { 6, 7, 1, 4, 5 },
                { 5, 1, 1, 2, 4 }
        };
        System.out.println("Test 1 - DFS: " + solution.pacificAtlantic(heights1));
        System.out.println("Test 1 - BFS: " + solution.pacificAtlanticBFS(heights1));
        // Expected: [[0,4],[1,3],[1,4],[2,2],[3,0],[3,1],[4,0]]

        // Test case 2: Single cell
        int[][] heights2 = { { 1 } };
        System.out.println("Test 2 - DFS: " + solution.pacificAtlantic(heights2));
        System.out.println("Test 2 - BFS: " + solution.pacificAtlanticBFS(heights2));
        // Expected: [[0,0]]

        // Test case 3: Small grid
        int[][] heights3 = {
                { 2, 1 },
                { 1, 2 }
        };
        System.out.println("Test 3 - DFS: " + solution.pacificAtlantic(heights3));
        System.out.println("Test 3 - BFS: " + solution.pacificAtlanticBFS(heights3));
        // Expected: [[0,0],[0,1],[1,0],[1,1]]

        // Performance test
        System.out.println("\n=== Performance Analysis ===");
        System.out.println("Time Complexity: O(M*N) where M*N is the grid size");
        System.out.println("Space Complexity: O(M*N) for visited arrays and recursion stack");
        System.out.println("Key Insight: Start from ocean borders and find reachable cells");
        System.out.println("Algorithm: Reverse thinking - instead of checking if each cell can reach oceans,");
        System.out.println("           check which cells can be reached from each ocean border");
    }
}
