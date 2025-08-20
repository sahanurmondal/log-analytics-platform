package dp.medium;

/**
 * LeetCode 70: Climbing Stairs
 * https://leetcode.com/problems/climbing-stairs/
 *
 * Description:
 * You are climbing a staircase. It takes n steps to reach the top. Each time
 * you can climb 1 or 2 steps.
 * Return the number of distinct ways to climb to the top.
 *
 * Constraints:
 * - 1 <= n <= 45
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Adobe, Amazon, Apple, Microsoft, Google
 * Difficulty: Easy/Medium
 */
public class ClimbingStairs {

    // Approach 1: Dynamic Programming (Bottom-up) - O(n) time, O(n) space
    public int climbStairs(int n) {
        if (n <= 2)
            return n;

        int[] dp = new int[n + 1];
        dp[1] = 1;
        dp[2] = 2;

        for (int i = 3; i <= n; i++) {
            dp[i] = dp[i - 1] + dp[i - 2];
        }

        return dp[n];
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int climbStairsOptimized(int n) {
        if (n <= 2)
            return n;

        int prev2 = 1; // dp[i-2]
        int prev1 = 2; // dp[i-1]

        for (int i = 3; i <= n; i++) {
            int current = prev1 + prev2;
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    // Approach 3: Recursive with Memoization - O(n) time, O(n) space
    public int climbStairsMemo(int n) {
        Integer[] memo = new Integer[n + 1];
        return climbStairsRecursive(n, memo);
    }

    private int climbStairsRecursive(int n, Integer[] memo) {
        if (n <= 2)
            return n;
        if (memo[n] != null)
            return memo[n];

        memo[n] = climbStairsRecursive(n - 1, memo) + climbStairsRecursive(n - 2, memo);
        return memo[n];
    }

    // Approach 4: Matrix Exponentiation - O(log n) time, O(1) space
    public int climbStairsMatrix(int n) {
        if (n <= 2)
            return n;

        long[][] result = { { 1, 0 }, { 0, 1 } }; // Identity matrix
        long[][] base = { { 1, 1 }, { 1, 0 } }; // Fibonacci matrix

        matrixPower(result, base, n - 1);

        return (int) (result[0][0] * 2 + result[0][1] * 1);
    }

    private void matrixPower(long[][] result, long[][] base, int n) {
        while (n > 0) {
            if (n % 2 == 1) {
                multiply(result, base);
            }
            multiply(base, base);
            n /= 2;
        }
    }

    private void multiply(long[][] a, long[][] b) {
        long[][] temp = new long[2][2];
        temp[0][0] = a[0][0] * b[0][0] + a[0][1] * b[1][0];
        temp[0][1] = a[0][0] * b[0][1] + a[0][1] * b[1][1];
        temp[1][0] = a[1][0] * b[0][0] + a[1][1] * b[1][0];
        temp[1][1] = a[1][0] * b[0][1] + a[1][1] * b[1][1];

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 2; j++) {
                a[i][j] = temp[i][j];
            }
        }
    }

    // Approach 5: Mathematical Formula (Binet's Formula) - O(1) time, O(1) space
    public int climbStairsMath(int n) {
        double sqrt5 = Math.sqrt(5);
        double phi = (1 + sqrt5) / 2;
        double psi = (1 - sqrt5) / 2;

        return (int) Math.round((Math.pow(phi, n + 1) - Math.pow(psi, n + 1)) / sqrt5);
    }

    public static void main(String[] args) {
        ClimbingStairs solution = new ClimbingStairs();

        System.out.println("=== Climbing Stairs Test Cases ===");

        // Test case 1: n = 1
        System.out.println("n = 1:");
        System.out.println("DP: " + solution.climbStairs(1)); // Expected: 1
        System.out.println("Optimized: " + solution.climbStairsOptimized(1)); // Expected: 1
        System.out.println("Memoization: " + solution.climbStairsMemo(1)); // Expected: 1

        // Test case 2: n = 2
        System.out.println("\nn = 2:");
        System.out.println("DP: " + solution.climbStairs(2)); // Expected: 2
        System.out.println("Optimized: " + solution.climbStairsOptimized(2)); // Expected: 2
        System.out.println("Memoization: " + solution.climbStairsMemo(2)); // Expected: 2

        // Test case 3: n = 3
        System.out.println("\nn = 3:");
        System.out.println("DP: " + solution.climbStairs(3)); // Expected: 3
        System.out.println("Optimized: " + solution.climbStairsOptimized(3)); // Expected: 3
        System.out.println("Matrix: " + solution.climbStairsMatrix(3)); // Expected: 3

        // Test case 4: n = 5
        System.out.println("\nn = 5:");
        System.out.println("DP: " + solution.climbStairs(5)); // Expected: 8
        System.out.println("Optimized: " + solution.climbStairsOptimized(5)); // Expected: 8
        System.out.println("Matrix: " + solution.climbStairsMatrix(5)); // Expected: 8
        System.out.println("Math: " + solution.climbStairsMath(5)); // Expected: 8

        // Test case 5: n = 10
        System.out.println("\nn = 10:");
        System.out.println("DP: " + solution.climbStairs(10)); // Expected: 89
        System.out.println("Optimized: " + solution.climbStairsOptimized(10)); // Expected: 89
        System.out.println("Matrix: " + solution.climbStairsMatrix(10)); // Expected: 89

        // Performance comparison
        performanceTest();
    }

    private static void performanceTest() {
        System.out.println("\n=== Performance Comparison (n = 40) ===");
        ClimbingStairs solution = new ClimbingStairs();

        long startTime, endTime;
        int n = 40;

        // Test DP approach
        startTime = System.nanoTime();
        int result1 = solution.climbStairs(n);
        endTime = System.nanoTime();
        System.out.println("DP: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimized approach
        startTime = System.nanoTime();
        int result2 = solution.climbStairsOptimized(n);
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test memoization approach
        startTime = System.nanoTime();
        int result3 = solution.climbStairsMemo(n);
        endTime = System.nanoTime();
        System.out.println("Memoization: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test matrix approach
        startTime = System.nanoTime();
        int result4 = solution.climbStairsMatrix(n);
        endTime = System.nanoTime();
        System.out.println("Matrix: " + result4 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test mathematical approach
        startTime = System.nanoTime();
        int result5 = solution.climbStairsMath(n);
        endTime = System.nanoTime();
        System.out.println("Math: " + result5 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }
}
