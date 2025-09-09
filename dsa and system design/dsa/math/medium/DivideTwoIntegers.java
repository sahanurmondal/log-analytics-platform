package math.medium;

/**
 * LeetCode 29: Divide Two Integers
 * https://leetcode.com/problems/divide-two-integers/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Divide two integers without using multiplication, division, and mod operator.
 *
 * Constraints:
 * - -2^31 <= dividend, divisor <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you handle overflow?
 * 2. Can you optimize for large numbers?
 * 3. Can you implement division for BigInteger?
 */
public class DivideTwoIntegers {
    public int divide(int dividend, int divisor) {
        if (dividend == Integer.MIN_VALUE && divisor == -1)
            return Integer.MAX_VALUE;
        long dvd = Math.abs((long) dividend), dvs = Math.abs((long) divisor);
        int res = 0;
        while (dvd >= dvs) {
            long temp = dvs, multiple = 1;
            while (dvd >= (temp << 1)) {
                temp <<= 1;
                multiple <<= 1;
            }
            dvd -= temp;
            res += multiple;
        }
        return ((dividend > 0) == (divisor > 0)) ? res : -res;
    }

    // Follow-up 1: Handle overflow (already handled above)
    // Follow-up 2: Optimize for large numbers (already handled above)
    // Follow-up 3: BigInteger division
    public java.math.BigInteger divideBig(java.math.BigInteger dividend, java.math.BigInteger divisor) {
        return dividend.divide(divisor);
    }

    public static void main(String[] args) {
        DivideTwoIntegers solution = new DivideTwoIntegers();
        System.out.println(solution.divide(10, 3)); // 3
        System.out.println(solution.divide(7, -3)); // -2
        System.out.println(solution.divide(0, 1)); // 0
        System.out.println(solution.divide(Integer.MIN_VALUE, -1)); // 2147483647
        System.out.println(solution.divide(42, 1)); // 42
        System.out.println(
                solution.divideBig(java.math.BigInteger.valueOf(100000000000L), java.math.BigInteger.valueOf(3))); // 33333333333
    }
}
