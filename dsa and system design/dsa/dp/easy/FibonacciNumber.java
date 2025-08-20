package dp.easy;

import java.util.*;

/**
 * LeetCode 509: Fibonacci Number
 * https://leetcode.com/problems/fibonacci-number/
 *
 * Description:
 * The Fibonacci numbers, commonly denoted F(n) form a sequence, called the
 * Fibonacci sequence,
 * such that each number is the sum of the two preceding ones, starting from 0
 * and 1.
 * Given n, calculate F(n).
 *
 * Constraints:
 * - 0 <= n <= 30
 *
 * Company Tags: Goldman Sachs, Amazon, Apple
 * Difficulty: Easy
 */
public class FibonacciNumber {

    // Approach 1: Basic DP - O(n) time, O(n) space
    public int fib(int n) {
        if (n <= 1)
            return n;

        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    // Approach 2: Space Optimized - O(n) time, O(1) space
    public int fibOptimized(int n) {
        if (n <= 1)
            return n;

        int prev2 = 0, prev1 = 1;

        for (int i = 2; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    // Approach 3: Recursion with Memoization - O(n) time, O(n) space
    public int fibMemo(int n) {
        Integer[] memo = new Integer[n + 1];
        return fibMemoHelper(n, memo);
    }

    private int fibMemoHelper(int n, Integer[] memo) {
        if (n <= 1)
            return n;

        if (memo[n] != null)
            return memo[n];

        memo[n] = fibMemoHelper(n - 1, memo) + fibMemoHelper(n - 2, memo);
        return memo[n];
    }

    // Approach 4: Matrix Exponentiation - O(log n) time, O(1) space
    public int fibMatrix(int n) {
        if (n <= 1)
            return n;

        long[][] matrix = { { 1, 1 }, { 1, 0 } };
        long[][] result = matrixPower(matrix, n - 1);

        return (int) result[0][0];
    }

    private long[][] matrixPower(long[][] matrix, int n) {
        long[][] result = { { 1, 0 }, { 0, 1 } }; // Identity matrix

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
        return new long[][] {
                { a[0][0] * b[0][0] + a[0][1] * b[1][0], a[0][0] * b[0][1] + a[0][1] * b[1][1] },
                { a[1][0] * b[0][0] + a[1][1] * b[1][0], a[1][0] * b[0][1] + a[1][1] * b[1][1] }
        };
    }

    // Approach 5: Golden Ratio Formula - O(1) time, O(1) space
    public int fibFormula(int n) {
        double phi = (1 + Math.sqrt(5)) / 2;
        double psi = (1 - Math.sqrt(5)) / 2;

        return (int) Math.round((Math.pow(phi, n) - Math.pow(psi, n)) / Math.sqrt(5));
    }

    public static void main(String[] args) {
        FibonacciNumber solution = new FibonacciNumber();

        System.out.println("=== Fibonacci Number Test Cases ===");

        for (int n = 0; n <= 10; n++) {
            System.out.println("F(" + n + ") = " + solution.fib(n) +
                    " (Optimized: " + solution.fibOptimized(n) +
                    ", Matrix: " + solution.fibMatrix(n) +
                    ", Formula: " + solution.fibFormula(n) + ")");
        }
    }
}
