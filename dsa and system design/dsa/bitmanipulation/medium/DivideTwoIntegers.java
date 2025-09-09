package bitmanipulation.medium;

/**
 * LeetCode 29: Divide Two Integers
 * https://leetcode.com/problems/divide-two-integers/
 *
 * Description: Given two integers dividend and divisor, divide two integers
 * without using multiplication, division, and mod operator.
 * The integer division should truncate toward zero, which means losing its
 * fractional part.
 * Return the quotient after dividing dividend by divisor.
 * 
 * Constraints:
 * - -2^31 <= dividend, divisor <= 2^31 - 1
 * - divisor != 0
 *
 * Follow-up:
 * - Can you solve it using bit shifting?
 * - What about handling overflow?
 * 
 * Time Complexity: O(log^2 n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class DivideTwoIntegers {

    // Main optimized solution - Bit shifting
    public int divide(int dividend, int divisor) {
        // Handle overflow case
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        // Determine sign
        boolean negative = (dividend < 0) ^ (divisor < 0);

        // Work with positive values
        long absDividend = Math.abs((long) dividend);
        long absDivisor = Math.abs((long) divisor);

        long result = 0;

        while (absDividend >= absDivisor) {
            long temp = absDivisor;
            long multiple = 1;

            // Find the largest multiple of divisor that fits
            while (absDividend >= (temp << 1)) {
                temp <<= 1;
                multiple <<= 1;
            }

            absDividend -= temp;
            result += multiple;
        }

        return negative ? (int) -result : (int) result;
    }

    // Alternative solution - Exponential search
    public int divideExponential(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1) {
            return Integer.MAX_VALUE;
        }

        boolean negative = (dividend < 0) ^ (divisor < 0);
        long absDividend = Math.abs((long) dividend);
        long absDivisor = Math.abs((long) divisor);

        long result = 0;

        while (absDividend >= absDivisor) {
            long currentDivisor = absDivisor;
            long numDivisors = 1;

            while (absDividend >= currentDivisor) {
                absDividend -= currentDivisor;
                result += numDivisors;

                currentDivisor += currentDivisor;
                numDivisors += numDivisors;
            }
        }

        return negative ? (int) -result : (int) result;
    }

    public static void main(String[] args) {
        DivideTwoIntegers solution = new DivideTwoIntegers();

        System.out.println(solution.divide(10, 3)); // Expected: 3
        System.out.println(solution.divide(7, -3)); // Expected: -2
        System.out.println(solution.divide(0, 1)); // Expected: 0
        System.out.println(solution.divide(1, 1)); // Expected: 1
        System.out.println(solution.divide(Integer.MIN_VALUE, -1)); // Expected: 2147483647
        System.out.println(solution.divideExponential(10, 3)); // Expected: 3
    }
}
