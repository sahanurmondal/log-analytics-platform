package dp.linear.basic;

/**
 * LeetCode 1137: N-th Tribonacci Number
 * https://leetcode.com/problems/n-th-tribonacci-number/
 *
 * Description:
 * The Tribonacci sequence Tn is defined as follows:
 * T0 = 0, T1 = 1, T2 = 1, and Tn+3 = Tn + Tn+1 + Tn+2 for n >= 0.
 * Given n, return the value of Tn.
 *
 * Constraints:
 * - 0 <= n <= 37
 *
 * Company Tags: Google, Amazon
 * Difficulty: Easy
 */
public class NthTribonacciNumber {

    // Approach 1: DP Array - O(n) time, O(n) space
    public int tribonacci(int n) {
        if (n == 0)
            return 0;
        if (n <= 2)
            return 1;

        int[] dp = new int[n + 1];
        dp[0] = 0;
        dp[1] = 1;
        dp[2] = 1;

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2] + dp[i - 3];
        }

        return dp[n];
    }

    // Approach 2: Space Optimized - O(n) time, O(1) space
    public int tribonacciOptimized(int n) {
        if (n == 0)
            return 0;
        if (n <= 2)
            return 1;

        int a = 0, b = 1, c = 1;

        for (int i = 3; i <= n; i++) {
            int temp = a + b + c;
            a = b;
            b = c;
            c = temp;
        }

        return c;
    }

    // Approach 3: Matrix Exponentiation - O(log n) time, O(1) space
    public int tribonacciMatrix(int n) {
        if (n == 0)
            return 0;
        if (n <= 2)
            return 1;

        long[][] matrix = { { 0, 1, 1 }, { 1, 0, 1 }, { 1, 1, 0 } };
        long[][] result = matrixPower(matrix, n - 2);

        return (int) (result[0][0] + result[0][1]);
    }

    private long[][] matrixPower(long[][] matrix, int n) {
        long[][] result = { { 1, 0, 0 }, { 0, 1, 0 }, { 0, 0, 1 } };

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
        long[][] result = new long[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    result[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        NthTribonacciNumber solution = new NthTribonacciNumber();

        System.out.println("=== N-th Tribonacci Number Test Cases ===");

        for (int n = 0; n <= 10; n++) {
            System.out.println("T(" + n + ") = " + solution.tribonacci(n) +
                    " (Optimized: " + solution.tribonacciOptimized(n) +
                    ", Matrix: " + solution.tribonacciMatrix(n) + ")");
        }
    }
}
