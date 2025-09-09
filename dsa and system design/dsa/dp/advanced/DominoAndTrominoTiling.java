package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 790: Domino and Tromino Tiling
 * https://leetcode.com/problems/domino-and-tromino-tiling/
 *
 * Description:
 * You have two types of tiles: a 2 x 1 domino shape and a tromino shape. You
 * may rotate these shapes.
 * Given an integer n, return the number of ways to tile an 2 x n board. Since
 * the answer may be very large, return it modulo 10^9 + 7.
 *
 * Constraints:
 * - 1 <= n <= 1000
 *
 * Follow-up:
 * - Can you derive the recurrence relation?
 * - What if we have different tile shapes?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class DominoAndTrominoTiling {

    private static final int MOD = 1000000007;

    // Approach 1: DP with State Tracking - O(n) time, O(n) space
    public int numTilings(int n) {
        if (n <= 2)
            return n;

        // dp[i][0] = ways to fill 2×i board completely
        // dp[i][1] = ways to fill 2×i board with top-right corner missing
        // dp[i][2] = ways to fill 2×i board with bottom-right corner missing
        long[][] dp = new long[n + 1][3];

        dp[0][0] = 1;
        dp[1][0] = 1;
        dp[2][0] = 2;
        dp[2][1] = 1;
        dp[2][2] = 1;

        for (int i = 3; i <= n; i++) {
            dp[i][0] = (dp[i - 1][0] + dp[i - 2][0] + dp[i - 1][1] + dp[i - 1][2]) % MOD;
            dp[i][1] = (dp[i - 2][0] + dp[i - 1][2]) % MOD;
            dp[i][2] = (dp[i - 2][0] + dp[i - 1][1]) % MOD;
        }

        return (int) dp[n][0];
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int numTilingsOptimized(int n) {
        if (n <= 2)
            return n;

        long dp0_prev2 = 1, dp0_prev1 = 1, dp0_curr = 2;
        long dp1_prev1 = 1, dp2_prev1 = 1;

        for (int i = 3; i <= n; i++) {
            long new_dp0 = (dp0_prev1 + dp0_prev2 + dp1_prev1 + dp2_prev1) % MOD;
            long new_dp1 = (dp0_prev2 + dp2_prev1) % MOD;
            long new_dp2 = (dp0_prev2 + dp1_prev1) % MOD;

            dp0_prev2 = dp0_prev1;
            dp0_prev1 = dp0_curr;
            dp0_curr = new_dp0;
            dp1_prev1 = new_dp1;
            dp2_prev1 = new_dp2;
        }

        return (int) dp0_curr;
    }

    // Approach 3: Mathematical Recurrence - O(n) time, O(n) space
    public int numTilingsMath(int n) {
        if (n <= 2)
            return n;

        long[] dp = new long[n + 1];
        dp[0] = 1;
        dp[1] = 1;
        dp[2] = 2;

        for (int i = 3; i <= n; i++) {
            dp[i] = (2 * dp[i - 1] + dp[i - 3]) % MOD;
        }

        return (int) dp[n];
    }

    // Approach 4: Matrix Exponentiation - O(log n) time, O(1) space
    public int numTilingsMatrix(int n) {
        if (n <= 2)
            return n;

        // State vector: [f(n), f(n-1), f(n-2)]
        // Transition matrix based on recurrence f(n) = 2*f(n-1) + f(n-3)
        long[][] matrix = {
                { 2, 1, 0 },
                { 1, 0, 0 },
                { 0, 1, 0 }
        };

        long[][] result = matrixPower(matrix, n - 2);

        // Initial state: [f(2), f(1), f(0)] = [2, 1, 1]
        return (int) ((result[0][0] * 2 + result[0][1] * 1 + result[0][2] * 1) % MOD);
    }

    private long[][] matrixPower(long[][] matrix, int n) {
        int size = matrix.length;
        long[][] result = new long[size][size];

        // Initialize result as identity matrix
        for (int i = 0; i < size; i++) {
            result[i][i] = 1;
        }

        while (n > 0) {
            if (n % 2 == 1) {
                result = multiplyMatrix(result, matrix);
            }
            matrix = multiplyMatrix(matrix, matrix);
            n /= 2;
        }

        return result;
    }

    private long[][] multiplyMatrix(long[][] a, long[][] b) {
        int size = a.length;
        long[][] result = new long[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] = (result[i][j] + a[i][k] * b[k][j]) % MOD;
                }
            }
        }

        return result;
    }

    // Approach 5: Memoization - O(n) time, O(n) space
    public int numTilingsMemo(int n) {
        Long[] memo = new Long[n + 1];
        return (int) numTilingsMemoHelper(n, memo);
    }

    private long numTilingsMemoHelper(int n, Long[] memo) {
        if (n <= 0)
            return 1;
        if (n == 1)
            return 1;
        if (n == 2)
            return 2;

        if (memo[n] != null)
            return memo[n];

        long result = (2 * numTilingsMemoHelper(n - 1, memo) + numTilingsMemoHelper(n - 3, memo)) % MOD;
        memo[n] = result;
        return result;
    }

    public static void main(String[] args) {
        DominoAndTrominoTiling solution = new DominoAndTrominoTiling();

        System.out.println("=== Domino and Tromino Tiling Test Cases ===");

        // Test Case 1: Small examples
        for (int n = 1; n <= 5; n++) {
            System.out.println("n = " + n);
            System.out.println("DP State: " + solution.numTilings(n));
            System.out.println("Optimized: " + solution.numTilingsOptimized(n));
            System.out.println("Mathematical: " + solution.numTilingsMath(n));
            System.out.println("Matrix: " + solution.numTilingsMatrix(n));
            System.out.println("Memoization: " + solution.numTilingsMemo(n));
            System.out.println();
        }

        // Test specific cases
        System.out.println("Test - n = 3, Expected: 5");
        System.out.println("Result: " + solution.numTilings(3));
        System.out.println();

        System.out.println("Test - n = 4, Expected: 11");
        System.out.println("Result: " + solution.numTilings(4));
        System.out.println();

        performanceTest();
    }

    private static void performanceTest() {
        DominoAndTrominoTiling solution = new DominoAndTrominoTiling();

        int n = 1000;

        System.out.println("=== Performance Test (n: " + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.numTilings(n);
        long end = System.nanoTime();
        System.out.println("DP State: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.numTilingsOptimized(n);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.numTilingsMath(n);
        end = System.nanoTime();
        System.out.println("Mathematical: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result4 = solution.numTilingsMatrix(n);
        end = System.nanoTime();
        System.out.println("Matrix Exponentiation: " + result4 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
