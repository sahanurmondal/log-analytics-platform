package graphs.medium;

import java.util.*;

/**
 * LeetCode 994: Rotting Oranges
 * https://leetcode.com/problems/rotting-oranges/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 12+ interviews)
 *
 * Description: Find minimum time for all oranges to rot.
 *
 * Constraints:
 * - 1 <= grid.length <= 10
 * - 1 <= grid[i].length <= 10
 * - grid[i][j] is 0, 1, or 2
 * 
 * Follow-up Questions:
 * 1. Can you handle 3D grids?
 * 2. Can you track which oranges rot at each step?
 * 3. Can you optimize for sparse grids?
 */
public class RottingOranges {
    // Approach 1: Multi-source BFS - O(mn) time, O(mn) space
    public int orangesRotting(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        int fresh = 0, minutes = 0;
        Queue<int[]> queue = new LinkedList<>();

        // Find all rotten oranges and count fresh ones
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[] { i, j });
                } else if (grid[i][j] == 1) {
                    fresh++;
                }
            }
        }

        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size();
            // Process all rotten oranges at current minute
            for (int i = 0; i < size; i++) {
                int[] current = queue.poll();
                for (int[] dir : directions) {
                    int newRow = current[0] + dir[0];
                    int newCol = current[1] + dir[1];

                    if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                            grid[newRow][newCol] == 1) {
                        grid[newRow][newCol] = 2;
                        fresh--;
                        queue.offer(new int[] { newRow, newCol });
                    }
                }
            }
            minutes++;
        }

        return fresh == 0 ? minutes : -1;
    }

    // Follow-up: Track rotting process
    public List<List<int[]>> orangesRottingWithSteps(int[][] grid) {
        int m = grid.length, n = grid[0].length;
        List<List<int[]>> steps = new ArrayList<>();
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                if (grid[i][j] == 2) {
                    queue.offer(new int[] { i, j });
                }
            }
        }

        int[][] directions = { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } };

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<int[]> currentStep = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                int[] current = queue.poll();
                for (int[] dir : directions) {
                    int newRow = current[0] + dir[0];
                    int newCol = current[1] + dir[1];

                    if (newRow >= 0 && newRow < m && newCol >= 0 && newCol < n &&
                            grid[newRow][newCol] == 1) {
                        grid[newRow][newCol] = 2;
                        int[] newRotten = { newRow, newCol };
                        queue.offer(newRotten);
                        currentStep.add(newRotten);
                    }
                }
            }

            if (!currentStep.isEmpty()) {
                steps.add(currentStep);
            }
        }

        return steps;
    }

    // Follow-up: 3D version
    public int orangesRotting3D(int[][][] grid) {
        int x = grid.length, y = grid[0].length, z = grid[0][0].length;
        int fresh = 0, minutes = 0;
        Queue<int[]> queue = new LinkedList<>();

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                for (int k = 0; k < z; k++) {
                    if (grid[i][j][k] == 2) {
                        queue.offer(new int[] { i, j, k });
                    } else if (grid[i][j][k] == 1) {
                        fresh++;
                    }
                }
            }
        }

        int[][] directions = { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 1, 0 }, { 0, -1, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };

        while (!queue.isEmpty() && fresh > 0) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] current = queue.poll();
                for (int[] dir : directions) {
                    int nx = current[0] + dir[0];
                    int ny = current[1] + dir[1];
                    int nz = current[2] + dir[2];

                    if (nx >= 0 && nx < x && ny >= 0 && ny < y && nz >= 0 && nz < z &&
                            grid[nx][ny][nz] == 1) {
                        grid[nx][ny][nz] = 2;
                        fresh--;
                        queue.offer(new int[] { nx, ny, nz });
                    }
                }
            }
            minutes++;
        }

        return fresh == 0 ? minutes : -1;
    }

    public static void main(String[] args) {
        RottingOranges ro = new RottingOranges();

        // Basic case
        int[][] grid1 = { { 2, 1, 1 }, { 1, 1, 0 }, { 0, 1, 1 } };
        System.out.println(ro.orangesRotting(grid1)); // 4

        // No fresh oranges
        int[][] grid2 = { { 2, 1, 1 }, { 0, 1, 1 }, { 1, 0, 1 } };
        System.out.println(ro.orangesRotting(grid2)); // -1

        // All rotten initially
        int[][] grid3 = { { 2, 2, 2 }, { 2, 2, 2 } };
        System.out.println(ro.orangesRotting(grid3)); // 0

        // With steps tracking
        int[][] grid4 = { { 2, 1, 1 }, { 1, 1, 0 }, { 0, 1, 1 } };
        List<List<int[]>> steps = ro.orangesRottingWithSteps(grid4);
        System.out.println("Steps: " + steps.size());

        // Edge cases
        int[][] grid5 = { { 0, 2 } };
        System.out.println(ro.orangesRotting(grid5)); // 0

        int[][] grid6 = { { 1 } };
        System.out.println(ro.orangesRotting(grid6)); // -1
    }
}
