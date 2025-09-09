package math.medium;

/**
 * LeetCode 69: Sqrt(x)
 * https://leetcode.com/problems/sqrtx/
 *
 * Description:
 * Implement int sqrt(int x). Compute and return the square root of x,
 * where x is guaranteed to be a non-negative integer.
 * 
 * Since the return type is an integer, the decimal digits are truncated,
 * and only the integer part of the result is returned.
 *
 * Example:
 * Input: x = 8
 * Output: 2
 * Explanation: The square root of 8 is 2.82842..., and since the decimal part
 * is truncated, 2 is returned.
 *
 * Constraints:
 * - 0 <= x <= 2^31-1
 *
 * Follow-up:
 * 1. Can you solve it in O(log x) time?
 * 2. Can you handle edge cases without using built-in functions?
 * 3. How would you handle floating-point precision for decimal places?
 */
public class SqrtX {

    // Approach 1: Binary Search - O(log x) time, O(1) space
    public int mySqrt(int x) {
        if (x == 0)
            return 0;

        long left = 1;
        long right = x;

        while (left <= right) {
            long mid = left + (right - left) / 2;
            long square = mid * mid;

            if (square == x) {
                return (int) mid;
            } else if (square < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return (int) right;
    }

    // Approach 2: Newton's Method - O(log x) time, O(1) space
    public int mySqrtNewton(int x) {
        if (x == 0)
            return 0;

        long r = x;
        while (r * r > x) {
            r = (r + x / r) / 2;
        }

        return (int) r;
    }

    // Approach 3: Bit Manipulation - O(log x) time, O(1) space
    public int mySqrtBit(int x) {
        if (x == 0)
            return 0;

        int h = 0;
        // Find highest bit position
        while ((long) (1 << h) * (1 << h) <= x) {
            h++;
        }
        h--;

        int result = 1 << h;
        // Check remaining bits
        for (h--; h >= 0; h--) {
            long nextTry = result | (1L << h);
            if (nextTry * nextTry <= x) {
                result = (int) nextTry;
            }
        }

        return result;
    }

    // Approach 4: Linear Search with optimization - O(sqrt(x)) time, O(1) space
    public int mySqrtLinear(int x) {
        if (x == 0)
            return 0;

        int i = 1;
        while (true) {
            // Use division to avoid overflow
            if (i > x / i) {
                return i - 1;
            }
            i++;
        }
    }

    // Follow-up: Square root with decimal precision
    public double mySqrtPrecise(int x, int precision) {
        if (x == 0)
            return 0;

        double result = mySqrt(x);
        double increment = 0.1;

        for (int i = 0; i < precision; i++) {
            while (result * result <= x) {
                result += increment;
            }
            result -= increment;
            increment /= 10;
        }

        return result;
    }

    public static void main(String[] args) {
        SqrtX solution = new SqrtX();

        // Test basic cases
        System.out.println("Basic test cases:");
        System.out.println("sqrt(4) = " + solution.mySqrt(4)); // 2
        System.out.println("sqrt(8) = " + solution.mySqrt(8)); // 2
        System.out.println("sqrt(9) = " + solution.mySqrt(9)); // 3

        // Test edge cases
        System.out.println("\nEdge cases:");
        System.out.println("sqrt(0) = " + solution.mySqrt(0)); // 0
        System.out.println("sqrt(1) = " + solution.mySqrt(1)); // 1
        System.out.println("sqrt(2147395600) = " + solution.mySqrt(2147395600)); // 46340

        // Compare all approaches
        int[][] testCases = {
                { 0, 0 },
                { 1, 1 },
                { 4, 2 },
                { 8, 2 },
                { 9, 3 },
                { 16, 4 },
                { 2147395600, 46340 }
        };

        System.out.println("\nComparing all approaches:");
        for (int[] test : testCases) {
            int x = test[0];
            int expected = test[1];

            int result1 = solution.mySqrt(x);
            int result2 = solution.mySqrtNewton(x);
            int result3 = solution.mySqrtBit(x);
            int result4 = solution.mySqrtLinear(x);

            boolean consistent = result1 == expected &&
                    result2 == expected &&
                    result3 == expected &&
                    result4 == expected;

            System.out.printf("x = %d: %d (consistent: %b)%n",
                    x, result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int iterations = 1000000;
        int testValue = 2147395600;

        long start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.mySqrt(testValue);
        }
        System.out.println("Binary Search: " +
                (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.mySqrtNewton(testValue);
        }
        System.out.println("Newton's Method: " +
                (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.mySqrtBit(testValue);
        }
        System.out.println("Bit Manipulation: " +
                (System.currentTimeMillis() - start) + "ms");

        // Don't test linear search for large numbers
        testValue = 10000;
        start = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.mySqrtLinear(testValue);
        }
        System.out.println("Linear Search (small number): " +
                (System.currentTimeMillis() - start) + "ms");

        // Test precision extension
        System.out.println("\nTesting precision extension:");
        double[] preciseTests = { 2, 3, 10 };
        for (double x : preciseTests) {
            System.out.printf("sqrt(%.0f) with 4 decimal places: %.4f%n",
                    x, solution.mySqrtPrecise((int) x, 4));
        }
    }
}
