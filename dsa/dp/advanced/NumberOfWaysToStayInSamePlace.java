package dp.advanced;

import java.util.*;

/**
 * LeetCode 1269: Number of Ways to Stay in the Same Place After Some Steps
 * https://leetcode.com/problems/number-of-ways-to-stay-in-the-same-place-after-some-steps/
 *
 * Description:
 * You have a pointer at index 0 in an array of size arrLen. At each step, you
 * can move 1 position to the left, 1 position to the right in the array, or
 * stay in the same place.
 * Given two integers steps and arrLen, return the number of ways such that your
 * pointer is still at index 0 after exactly steps steps.
 * Since the answer may be too large, return it modulo 10^9 + 7.
 *
 * Constraints:
 * - 1 <= steps <= 500
 * - 1 <= arrLen <= 10^6
 *
 * Follow-up:
 * - Can you optimize space complexity?
 * - What if we want to end at any position?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class NumberOfWaysToStayInSamePlace {

    private static final int MOD = 1000000007;

    // Approach 1: 2D DP - O(steps * min(steps, arrLen)) time, O(steps * min(steps,
    // arrLen)) space
    public int numWaysToStayInPlace(int steps, int arrLen) {
        // We can't go beyond steps/2 positions from start
        int maxPos = Math.min(arrLen - 1, steps);

        long[][] dp = new long[steps + 1][maxPos + 1];
        dp[0][0] = 1;

        for (int step = 1; step <= steps; step++) {
            for (int pos = 0; pos <= maxPos; pos++) {
                // Stay in same position
                dp[step][pos] = dp[step - 1][pos];

                // Come from left
                if (pos > 0) {
                    dp[step][pos] = (dp[step][pos] + dp[step - 1][pos - 1]) % MOD;
                }

                // Come from right
                if (pos < maxPos) {
                    dp[step][pos] = (dp[step][pos] + dp[step - 1][pos + 1]) % MOD;
                }
            }
        }

        return (int) dp[steps][0];
    }

    // Approach 2: Space Optimized DP - O(steps * min(steps, arrLen)) time,
    // O(min(steps, arrLen)) space
    public int numWaysToStayInPlaceOptimized(int steps, int arrLen) {
        int maxPos = Math.min(arrLen - 1, steps);

        long[] prev = new long[maxPos + 1];
        long[] curr = new long[maxPos + 1];

        prev[0] = 1;

        for (int step = 1; step <= steps; step++) {
            Arrays.fill(curr, 0);

            for (int pos = 0; pos <= maxPos; pos++) {
                // Stay in same position
                curr[pos] = prev[pos];

                // Come from left
                if (pos > 0) {
                    curr[pos] = (curr[pos] + prev[pos - 1]) % MOD;
                }

                // Come from right
                if (pos < maxPos) {
                    curr[pos] = (curr[pos] + prev[pos + 1]) % MOD;
                }
            }

            long[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return (int) prev[0];
    }

    // Approach 3: Memoization - O(steps * min(steps, arrLen)) time, O(steps *
    // min(steps, arrLen)) space
    public int numWaysToStayInPlaceMemo(int steps, int arrLen) {
        int maxPos = Math.min(arrLen - 1, steps);
        Long[][] memo = new Long[steps + 1][maxPos + 1];
        return (int) numWaysMemoHelper(steps, 0, maxPos, memo);
    }

    private long numWaysMemoHelper(int steps, int pos, int maxPos, Long[][] memo) {
        if (pos < 0 || pos > maxPos)
            return 0;
        if (steps == 0)
            return pos == 0 ? 1 : 0;

        if (memo[steps][pos] != null)
            return memo[steps][pos];

        long ways = 0;

        // Stay in same position
        ways = (ways + numWaysMemoHelper(steps - 1, pos, maxPos, memo)) % MOD;

        // Move left
        ways = (ways + numWaysMemoHelper(steps - 1, pos - 1, maxPos, memo)) % MOD;

        // Move right
        ways = (ways + numWaysMemoHelper(steps - 1, pos + 1, maxPos, memo)) % MOD;

        memo[steps][pos] = ways;
        return ways;
    }

    // Approach 4: Matrix Exponentiation - O(k^3 * log(steps)) time, O(k^2) space
    // where k = min(steps, arrLen)
    public int numWaysToStayInPlaceMatrix(int steps, int arrLen) {
        int maxPos = Math.min(arrLen - 1, steps);
        int size = maxPos + 1;

        // Build transition matrix
        long[][] matrix = new long[size][size];

        for (int i = 0; i < size; i++) {
            matrix[i][i] = 1; // Stay in same position

            if (i > 0)
                matrix[i][i - 1] = 1; // Come from left
            if (i < size - 1)
                matrix[i][i + 1] = 1; // Come from right
        }

        // Compute matrix^steps
        long[][] result = matrixPower(matrix, steps);

        return (int) result[0][0];
    }

    private long[][] matrixPower(long[][] matrix, int power) {
        int n = matrix.length;
        long[][] result = new long[n][n];

        // Initialize as identity matrix
        for (int i = 0; i < n; i++) {
            result[i][i] = 1;
        }

        long[][] base = new long[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(matrix[i], 0, base[i], 0, n);
        }

        while (power > 0) {
            if (power % 2 == 1) {
                result = multiplyMatrix(result, base);
            }
            base = multiplyMatrix(base, base);
            power /= 2;
        }

        return result;
    }

    private long[][] multiplyMatrix(long[][] a, long[][] b) {
        int n = a.length;
        long[][] result = new long[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                for (int k = 0; k < n; k++) {
                    result[i][j] = (result[i][j] + a[i][k] * b[k][j]) % MOD;
                }
            }
        }

        return result;
    }

    // Approach 5: Get All Possible Paths - O(3^steps) time, O(3^steps) space
    public List<List<Integer>> getAllPossiblePaths(int steps, int arrLen) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        currentPath.add(0); // Start at position 0

        getAllPathsHelper(steps, 0, arrLen - 1, currentPath, result);
        return result;
    }

    private void getAllPathsHelper(int stepsLeft, int currentPos, int maxPos,
            List<Integer> currentPath, List<List<Integer>> result) {
        if (stepsLeft == 0) {
            if (currentPos == 0) {
                result.add(new ArrayList<>(currentPath));
            }
            return;
        }

        // Stay in same position
        currentPath.add(currentPos);
        getAllPathsHelper(stepsLeft - 1, currentPos, maxPos, currentPath, result);
        currentPath.remove(currentPath.size() - 1);

        // Move left
        if (currentPos > 0) {
            currentPath.add(currentPos - 1);
            getAllPathsHelper(stepsLeft - 1, currentPos - 1, maxPos, currentPath, result);
            currentPath.remove(currentPath.size() - 1);
        }

        // Move right
        if (currentPos < maxPos) {
            currentPath.add(currentPos + 1);
            getAllPathsHelper(stepsLeft - 1, currentPos + 1, maxPos, currentPath, result);
            currentPath.remove(currentPath.size() - 1);
        }
    }

    public static void main(String[] args) {
        NumberOfWaysToStayInSamePlace solution = new NumberOfWaysToStayInSamePlace();

        System.out.println("=== Number of Ways to Stay in Same Place Test Cases ===");

        // Test Case 1: Example from problem
        int steps1 = 3, arrLen1 = 2;
        System.out.println("Test 1 - steps: " + steps1 + ", arrLen: " + arrLen1);
        System.out.println("2D DP: " + solution.numWaysToStayInPlace(steps1, arrLen1));
        System.out.println("Optimized: " + solution.numWaysToStayInPlaceOptimized(steps1, arrLen1));
        System.out.println("Memoization: " + solution.numWaysToStayInPlaceMemo(steps1, arrLen1));
        System.out.println("Matrix: " + solution.numWaysToStayInPlaceMatrix(steps1, arrLen1));

        List<List<Integer>> paths1 = solution.getAllPossiblePaths(steps1, arrLen1);
        System.out.println("All possible paths (" + paths1.size() + " total):");
        for (List<Integer> path : paths1) {
            System.out.println("  " + path);
        }
        System.out.println("Expected: 4\n");

        // Test Case 2: Larger example
        int steps2 = 2, arrLen2 = 4;
        System.out.println("Test 2 - steps: " + steps2 + ", arrLen: " + arrLen2);
        System.out.println("Optimized: " + solution.numWaysToStayInPlaceOptimized(steps2, arrLen2));
        System.out.println("Expected: 2\n");

        // Test Case 3: Edge case
        int steps3 = 4, arrLen3 = 2;
        System.out.println("Test 3 - steps: " + steps3 + ", arrLen: " + arrLen3);
        System.out.println("Optimized: " + solution.numWaysToStayInPlaceOptimized(steps3, arrLen3));
        System.out.println("Expected: 8\n");

        performanceTest();
    }

    private static void performanceTest() {
        NumberOfWaysToStayInSamePlace solution = new NumberOfWaysToStayInSamePlace();

        int steps = 500, arrLen = 1000000;

        System.out.println("=== Performance Test (steps: " + steps + ", arrLen: " + arrLen + ") ===");

        long start = System.nanoTime();
        int result1 = solution.numWaysToStayInPlace(steps, arrLen);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.numWaysToStayInPlaceOptimized(steps, arrLen);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.numWaysToStayInPlaceMemo(steps, arrLen);
        end = System.nanoTime();
        System.out.println("Memoization: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
