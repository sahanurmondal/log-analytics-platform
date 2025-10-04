package dp.advanced;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 887: Super Egg Drop
 * https://leetcode.com/problems/super-egg-drop/
 *
 * Description:
 * You are given k identical eggs and you have access to a building with n
 * floors.
 * Each egg is identical and if an egg breaks, you cannot use it again.
 * You know that there exists a floor f where 0 <= f <= n such that any egg
 * dropped at a floor higher than f will break,
 * and any egg dropped at or below floor f will not break.
 * In each move, you may take an unbroken egg and drop it from any floor x
 * (where 1 <= x <= n).
 * Your goal is to know with certainty what the value of f is.
 * Return the minimum number of moves that you need to know with certainty what
 * f is, regardless of the initial value of f.
 *
 * Constraints:
 * - 1 <= k <= 100
 * - 1 <= n <= 10000
 *
 * Follow-up:
 * - What if we have infinite eggs?
 * - Can you solve it in O(k*n) time?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class SuperEggDrop {

    // Approach 1: Recursive with Memoization - O(k*n^2) time, O(k*n) space
    public int superEggDropMemo(int k, int n) {
        Map<String, Integer> memo = new HashMap<>();
        return superEggDropHelper(k, n, memo);
    }

    private int superEggDropHelper(int k, int n, Map<String, Integer> memo) {
        if (n <= 1 || k == 1)
            return n;

        String key = k + "," + n;
        if (memo.containsKey(key))
            return memo.get(key);

        int result = Integer.MAX_VALUE;

        // Try dropping from each floor
        for (int floor = 1; floor <= n; floor++) {
            // Egg breaks: k-1 eggs, floor-1 floors below
            // Egg doesn't break: k eggs, n-floor floors above
            int breakCase = superEggDropHelper(k - 1, floor - 1, memo);
            int notBreakCase = superEggDropHelper(k, n - floor, memo);

            // Take worst case + 1 (current drop)
            int worstCase = 1 + Math.max(breakCase, notBreakCase);
            result = Math.min(result, worstCase);
        }

        memo.put(key, result);
        return result;
    }

    // Approach 2: DP with Binary Search Optimization - O(k*n*log(n)) time, O(k*n)
    // space
    public int superEggDropDPBinarySearch(int k, int n) {
        int[][] dp = new int[k + 1][n + 1];

        // Base cases
        for (int i = 1; i <= n; i++) {
            dp[1][i] = i; // With 1 egg, need to try from floor 1 to i
        }

        for (int i = 1; i <= k; i++) {
            dp[i][0] = 0; // 0 floors means 0 moves
            dp[i][1] = 1; // 1 floor means 1 move
        }

        for (int eggs = 2; eggs <= k; eggs++) {
            for (int floors = 2; floors <= n; floors++) {
                dp[eggs][floors] = Integer.MAX_VALUE;

                // Binary search for optimal floor
                int left = 1, right = floors;
                while (left <= right) {
                    int mid = (left + right) / 2;
                    int breakCase = dp[eggs - 1][mid - 1];
                    int notBreakCase = dp[eggs][floors - mid];

                    if (breakCase > notBreakCase) {
                        right = mid - 1;
                        dp[eggs][floors] = Math.min(dp[eggs][floors], 1 + breakCase);
                    } else {
                        left = mid + 1;
                        dp[eggs][floors] = Math.min(dp[eggs][floors], 1 + notBreakCase);
                    }
                }
            }
        }

        return dp[k][n];
    }

    // Approach 3: Mathematical DP (Reverse thinking) - O(k*moves) time, O(k*moves)
    // space
    public int superEggDropMath(int k, int n) {
        // dp[m][k] = maximum floors we can handle with m moves and k eggs
        int[][] dp = new int[n + 1][k + 1];
        int moves = 0;

        while (dp[moves][k] < n) {
            moves++;
            for (int eggs = 1; eggs <= k; eggs++) {
                dp[moves][eggs] = 1 + dp[moves - 1][eggs - 1] + dp[moves - 1][eggs];
            }
        }

        return moves;
    }

    // Approach 4: Space Optimized Mathematical DP - O(k*moves) time, O(k) space
    public int superEggDropOptimized(int k, int n) {
        int[] dp = new int[k + 1];
        int moves = 0;

        while (dp[k] < n) {
            moves++;
            for (int eggs = k; eggs >= 1; eggs--) {
                dp[eggs] = 1 + dp[eggs - 1] + dp[eggs];
            }
        }

        return moves;
    }

    // Approach 5: Pure Mathematical Solution - O(k*log(n)) time, O(1) space
    public int superEggDropPureMath(int k, int n) {
        if (n <= 1)
            return n;
        if (k == 1)
            return n;

        // Binary search on number of moves
        int left = 1, right = n;

        while (left < right) {
            int mid = (left + right) / 2;
            if (canSolve(k, n, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    private boolean canSolve(int k, int n, int moves) {
        long floors = 0;
        long combination = 1;

        for (int i = 1; i <= k && floors < n; i++) {
            combination = combination * (moves - i + 1) / i;
            floors += combination;
        }

        return floors >= n;
    }

    public static void main(String[] args) {
        SuperEggDrop solution = new SuperEggDrop();

        System.out.println("=== Super Egg Drop Test Cases ===");

        // Test Case 1: Example from problem
        int k1 = 1, n1 = 2;
        System.out.println("Test 1 - k: " + k1 + ", n: " + n1);
        System.out.println("Memoization: " + solution.superEggDropMemo(k1, n1));
        System.out.println("DP Binary Search: " + solution.superEggDropDPBinarySearch(k1, n1));
        System.out.println("Mathematical: " + solution.superEggDropMath(k1, n1));
        System.out.println("Optimized: " + solution.superEggDropOptimized(k1, n1));
        System.out.println("Pure Math: " + solution.superEggDropPureMath(k1, n1));
        System.out.println("Expected: 2\n");

        // Test Case 2: More eggs
        int k2 = 2, n2 = 6;
        System.out.println("Test 2 - k: " + k2 + ", n: " + n2);
        System.out.println("Mathematical: " + solution.superEggDropMath(k2, n2));
        System.out.println("Optimized: " + solution.superEggDropOptimized(k2, n2));
        System.out.println("Expected: 3\n");

        // Test Case 3: Many floors
        int k3 = 3, n3 = 14;
        System.out.println("Test 3 - k: " + k3 + ", n: " + n3);
        System.out.println("Mathematical: " + solution.superEggDropMath(k3, n3));
        System.out.println("Pure Math: " + solution.superEggDropPureMath(k3, n3));
        System.out.println("Expected: 4\n");

        performanceTest();
    }

    private static void performanceTest() {
        SuperEggDrop solution = new SuperEggDrop();

        int k = 10, n = 1000;

        System.out.println("=== Performance Test (k: " + k + ", n: " + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.superEggDropMath(k, n);
        long end = System.nanoTime();
        System.out.println("Mathematical: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.superEggDropOptimized(k, n);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.superEggDropPureMath(k, n);
        end = System.nanoTime();
        System.out.println("Pure Math: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
