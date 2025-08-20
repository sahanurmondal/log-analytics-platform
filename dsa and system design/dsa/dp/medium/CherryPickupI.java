package dp.medium;

import java.util.*;

/**
 * LeetCode 741: Cherry Pickup
 * https://leetcode.com/problems/cherry-pickup/
 *
 * Description:
 * You are given an n x n grid representing a field of cherries, each cell is
 * one of three possible integers:
 * - 0 means the cell is empty, so you can pass through
 * - 1 means the cell contains a cherry that you can pick up and pass through
 * - -1 means the cell contains a thorn that blocks your way
 * Given the grid, return the maximum number of cherries you can collect by
 * following the rules below:
 * - Starting at the position (0, 0) and reaching the position (n - 1, n - 1) by
 * moving right or down.
 * - After reaching (n - 1, n - 1), returning to (0, 0) by moving left or up.
 * - When passing through a position, if it contains a cherry, you pick it up,
 * and the cell becomes an empty cell 0.
 * - If there is no valid path between (0, 0) and (n - 1, n - 1), then no
 * cherries can be collected.
 *
 * Constraints:
 * - n == grid.length
 * - n == grid[i].length
 * - 1 <= n <= 50
 * - grid[i][j] is -1, 0, or 1.
 * - grid[0][0] != -1
 * - grid[n - 1][n - 1] != -1
 *
 * Follow-up:
 * - What if we have k people walking simultaneously?
 * - Can you optimize space complexity?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard (categorized as Medium for this collection)
 */
public class CherryPickupI {

    // Approach 1: 3D DP with Memoization - O(n^3) time, O(n^3) space
    public int cherryPickup(int[][] grid) {
        int n = grid.length;
        Integer[][][] memo = new Integer[n][n][n];
        int result = cherryPickupHelper(grid, 0, 0, 0, memo);
        return Math.max(0, result);
    }

    private int cherryPickupHelper(int[][] grid, int r1, int c1, int r2, Integer[][][] memo) {
        int n = grid.length;
        int c2 = r1 + c1 - r2; // Since both paths have same number of steps

        // Out of bounds or hit thorn
        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n ||
                grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return Integer.MIN_VALUE;
        }

        // Reached destination
        if (r1 == n - 1 && c1 == n - 1) {
            return grid[r1][c1];
        }

        if (memo[r1][c1][r2] != null) {
            return memo[r1][c1][r2];
        }

        int cherries = 0;

        // Collect cherries
        if (r1 == r2 && c1 == c2) {
            cherries += grid[r1][c1]; // Same cell, collect once
        } else {
            cherries += grid[r1][c1] + grid[r2][c2]; // Different cells
        }

        // Try all four combinations of moves
        int maxFuture = Math.max(
                Math.max(
                        cherryPickupHelper(grid, r1 + 1, c1, r2 + 1, memo), // Both go down
                        cherryPickupHelper(grid, r1 + 1, c1, r2, memo) // First down, second right
                ),
                Math.max(
                        cherryPickupHelper(grid, r1, c1 + 1, r2 + 1, memo), // First right, second down
                        cherryPickupHelper(grid, r1, c1 + 1, r2, memo) // Both go right
                ));

        if (maxFuture == Integer.MIN_VALUE) {
            cherries = Integer.MIN_VALUE;
        } else {
            cherries += maxFuture;
        }

        memo[r1][c1][r2] = cherries;
        return cherries;
    }

    // Approach 2: Bottom-up 3D DP - O(n^3) time, O(n^3) space
    public int cherryPickupBottomUp(int[][] grid) {
        int n = grid.length;
        int[][][] dp = new int[n][n][n];

        // Initialize with negative infinity
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Arrays.fill(dp[i][j], Integer.MIN_VALUE);
            }
        }

        dp[0][0][0] = grid[0][0];

        for (int r1 = 0; r1 < n; r1++) {
            for (int c1 = 0; c1 < n; c1++) {
                for (int r2 = 0; r2 < n; r2++) {
                    int c2 = r1 + c1 - r2;

                    if (c2 < 0 || c2 >= n || dp[r1][c1][r2] == Integer.MIN_VALUE ||
                            grid[r1][c1] == -1 || grid[r2][c2] == -1) {
                        continue;
                    }

                    int cherries = dp[r1][c1][r2];

                    // Move person 1 down, person 2 down
                    if (r1 + 1 < n && r2 + 1 < n) {
                        int newCherries = cherries;
                        if (r1 + 1 == r2 + 1 && c1 == c2) {
                            newCherries += grid[r1 + 1][c1];
                        } else {
                            newCherries += grid[r1 + 1][c1] + grid[r2 + 1][c2];
                        }
                        dp[r1 + 1][c1][r2 + 1] = Math.max(dp[r1 + 1][c1][r2 + 1], newCherries);
                    }

                    // Move person 1 down, person 2 right
                    if (r1 + 1 < n && c2 + 1 < n) {
                        int newCherries = cherries;
                        if (r1 + 1 == r2 && c1 == c2 + 1) {
                            newCherries += grid[r1 + 1][c1];
                        } else {
                            newCherries += grid[r1 + 1][c1] + grid[r2][c2 + 1];
                        }
                        dp[r1 + 1][c1][r2] = Math.max(dp[r1 + 1][c1][r2], newCherries);
                    }

                    // Move person 1 right, person 2 down
                    if (c1 + 1 < n && r2 + 1 < n) {
                        int newCherries = cherries;
                        if (r1 == r2 + 1 && c1 + 1 == c2) {
                            newCherries += grid[r1][c1 + 1];
                        } else {
                            newCherries += grid[r1][c1 + 1] + grid[r2 + 1][c2];
                        }
                        dp[r1][c1 + 1][r2 + 1] = Math.max(dp[r1][c1 + 1][r2 + 1], newCherries);
                    }

                    // Move person 1 right, person 2 right
                    if (c1 + 1 < n && c2 + 1 < n) {
                        int newCherries = cherries;
                        if (r1 == r2 && c1 + 1 == c2 + 1) {
                            newCherries += grid[r1][c1 + 1];
                        } else {
                            newCherries += grid[r1][c1 + 1] + grid[r2][c2 + 1];
                        }
                        dp[r1][c1 + 1][r2] = Math.max(dp[r1][c1 + 1][r2], newCherries);
                    }
                }
            }
        }

        return Math.max(0, dp[n - 1][n - 1][n - 1]);
    }

    // Approach 3: Space Optimized DP - O(n^3) time, O(n^2) space
    public int cherryPickupSpaceOptimized(int[][] grid) {
        int n = grid.length;
        int[][] prev = new int[n][n];
        int[][] curr = new int[n][n];

        // Initialize with negative infinity
        for (int i = 0; i < n; i++) {
            Arrays.fill(prev[i], Integer.MIN_VALUE);
            Arrays.fill(curr[i], Integer.MIN_VALUE);
        }

        prev[0][0] = grid[0][0];

        for (int steps = 1; steps < 2 * n - 1; steps++) {
            // Reset current array
            for (int i = 0; i < n; i++) {
                Arrays.fill(curr[i], Integer.MIN_VALUE);
            }

            for (int r1 = Math.max(0, steps - n + 1); r1 <= Math.min(steps, n - 1); r1++) {
                int c1 = steps - r1;
                if (c1 < 0 || c1 >= n || grid[r1][c1] == -1)
                    continue;

                for (int r2 = Math.max(0, steps - n + 1); r2 <= Math.min(steps, n - 1); r2++) {
                    int c2 = steps - r2;
                    if (c2 < 0 || c2 >= n || grid[r2][c2] == -1)
                        continue;

                    int cherries = (r1 == r2 && c1 == c2) ? grid[r1][c1] : grid[r1][c1] + grid[r2][c2];

                    // Check all previous states
                    for (int pr1 = Math.max(0, r1 - 1); pr1 <= r1; pr1++) {
                        for (int pr2 = Math.max(0, r2 - 1); pr2 <= r2; pr2++) {
                            int pc1 = steps - 1 - pr1;
                            int pc2 = steps - 1 - pr2;

                            if (pc1 >= 0 && pc1 < n && pc2 >= 0 && pc2 < n &&
                                    prev[pr1][pr2] != Integer.MIN_VALUE &&
                                    ((pr1 == r1 - 1 && pc1 == c1) || (pr1 == r1 && pc1 == c1 - 1)) &&
                                    ((pr2 == r2 - 1 && pc2 == c2) || (pr2 == r2 && pc2 == c2 - 1))) {

                                curr[r1][r2] = Math.max(curr[r1][r2], prev[pr1][pr2] + cherries);
                            }
                        }
                    }
                }
            }

            // Swap arrays
            int[][] temp = prev;
            prev = curr;
            curr = temp;
        }

        return Math.max(0, prev[n - 1][n - 1]);
    }

    // Approach 4: DFS with Path Reconstruction - O(n^3) time, O(n^3) space
    public int cherryPickupWithPath(int[][] grid) {
        int n = grid.length;
        Integer[][][] memo = new Integer[n][n][n];
        String[][][] path = new String[n][n][n];

        int result = dfsWithPath(grid, 0, 0, 0, memo, path);

        if (result > 0) {
            System.out.println("Optimal path: " + path[0][0][0]);
        }

        return Math.max(0, result);
    }

    private int dfsWithPath(int[][] grid, int r1, int c1, int r2, Integer[][][] memo, String[][][] path) {
        int n = grid.length;
        int c2 = r1 + c1 - r2;

        if (r1 >= n || c1 >= n || r2 >= n || c2 >= n ||
                grid[r1][c1] == -1 || grid[r2][c2] == -1) {
            return Integer.MIN_VALUE;
        }

        if (r1 == n - 1 && c1 == n - 1) {
            path[r1][c1][r2] = "(" + r1 + "," + c1 + ")";
            return grid[r1][c1];
        }

        if (memo[r1][c1][r2] != null) {
            return memo[r1][c1][r2];
        }

        int cherries = (r1 == r2 && c1 == c2) ? grid[r1][c1] : grid[r1][c1] + grid[r2][c2];

        int[] results = new int[4];
        String[] moves = { "DD", "DR", "RD", "RR" };

        results[0] = dfsWithPath(grid, r1 + 1, c1, r2 + 1, memo, path);
        results[1] = dfsWithPath(grid, r1 + 1, c1, r2, memo, path);
        results[2] = dfsWithPath(grid, r1, c1 + 1, r2 + 1, memo, path);
        results[3] = dfsWithPath(grid, r1, c1 + 1, r2, memo, path);

        int maxResult = Integer.MIN_VALUE;
        int bestMove = -1;

        for (int i = 0; i < 4; i++) {
            if (results[i] > maxResult) {
                maxResult = results[i];
                bestMove = i;
            }
        }

        if (maxResult == Integer.MIN_VALUE) {
            memo[r1][c1][r2] = Integer.MIN_VALUE;
            return Integer.MIN_VALUE;
        }

        memo[r1][c1][r2] = cherries + maxResult;

        // Reconstruct path
        int nr1 = (bestMove == 0 || bestMove == 1) ? r1 + 1 : r1;
        int nc1 = (bestMove == 2 || bestMove == 3) ? c1 + 1 : c1;
        int nr2 = (bestMove == 0 || bestMove == 2) ? r2 + 1 : r2;

        String nextPath = path[nr1][nc1][nr2];
        path[r1][c1][r2] = "(" + r1 + "," + c1 + ")->" + (nextPath != null ? nextPath : "");

        return memo[r1][c1][r2];
    }

    // Approach 5: Iterative with Stack - O(n^3) time, O(n^3) space
    public int cherryPickupIterative(int[][] grid) {
        int n = grid.length;
        // Using map to avoid large array allocation
        Map<String, Integer> dp = new HashMap<>();

        // BFS approach
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[] { 0, 0, 0, grid[0][0] });
        dp.put("0,0,0", grid[0][0]);

        int maxCherries = 0;

        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int r1 = curr[0], c1 = curr[1], r2 = curr[2], cherries = curr[3];
            int c2 = r1 + c1 - r2;

            if (r1 == n - 1 && c1 == n - 1) {
                maxCherries = Math.max(maxCherries, cherries);
                continue;
            }

            // Try all four moves
            int[][] moves = { { 1, 0, 1 }, { 1, 0, 0 }, { 0, 1, 1 }, { 0, 1, 0 } };

            for (int[] move : moves) {
                int nr1 = r1 + move[0];
                int nc1 = c1 + move[1];
                int nr2 = r2 + move[2];
                int nc2 = nr1 + nc1 - nr2;

                if (nr1 < n && nc1 < n && nr2 < n && nc2 < n &&
                        grid[nr1][nc1] != -1 && grid[nr2][nc2] != -1) {

                    int newCherries = cherries;
                    if (nr1 == nr2 && nc1 == nc2) {
                        newCherries += grid[nr1][nc1];
                    } else {
                        newCherries += grid[nr1][nc1] + grid[nr2][nc2];
                    }

                    String key = nr1 + "," + nc1 + "," + nr2;
                    if (!dp.containsKey(key) || dp.get(key) < newCherries) {
                        dp.put(key, newCherries);
                        queue.offer(new int[] { nr1, nc1, nr2, newCherries });
                    }
                }
            }
        }

        return maxCherries;
    }

    public static void main(String[] args) {
        CherryPickupI solution = new CherryPickupI();

        System.out.println("=== Cherry Pickup I Test Cases ===");

        // Test Case 1: Example from problem
        int[][] grid1 = {
                { 0, 1, -1 },
                { 1, 0, -1 },
                { 1, 1, 1 }
        };
        System.out.println("Test 1 - Grid:");
        printGrid(grid1);
        System.out.println("Memoization: " + solution.cherryPickup(grid1));
        System.out.println("Bottom-up: " + solution.cherryPickupBottomUp(grid1));
        System.out.println("Space Optimized: " + solution.cherryPickupSpaceOptimized(grid1));
        System.out.println("Expected: 5\n");

        // Test Case 2: No valid path
        int[][] grid2 = {
                { 1, 1, -1 },
                { 1, -1, 1 },
                { -1, 1, 1 }
        };
        System.out.println("Test 2 - Grid:");
        printGrid(grid2);
        System.out.println("Memoization: " + solution.cherryPickup(grid2));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void printGrid(int[][] grid) {
        for (int[] row : grid) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static void performanceTest() {
        CherryPickupI solution = new CherryPickupI();

        int n = 20;
        int[][] largeGrid = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                double rand = Math.random();
                if (rand < 0.1) {
                    largeGrid[i][j] = -1; // 10% thorns
                } else if (rand < 0.6) {
                    largeGrid[i][j] = 1; // 50% cherries
                } else {
                    largeGrid[i][j] = 0; // 40% empty
                }
            }
        }
        largeGrid[0][0] = 1;
        largeGrid[n - 1][n - 1] = 1;

        System.out.println("=== Performance Test (Grid size: " + n + "x" + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.cherryPickup(largeGrid);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.cherryPickupSpaceOptimized(largeGrid);
        end = System.nanoTime();
        System.out.println("Space Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
