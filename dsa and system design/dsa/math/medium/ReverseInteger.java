package math.medium;

/**
 * LeetCode 7: Reverse Integer
 * https://leetcode.com/problems/reverse-integer/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description:
 * Reverse digits of an integer.
 *
 * Constraints:
 * - -2^31 <= x <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you handle overflow?
 * 2. Can you reverse for BigInteger?
 * 3. Can you reverse for negative numbers?
 */
public class ReverseInteger {
    public int reverse(int x) {
        int res = 0;
        while (x != 0) {
            int pop = x % 10;
            x /= 10;
            if (res > Integer.MAX_VALUE / 10 || (res == Integer.MAX_VALUE / 10 && pop > 7))
                return 0;
            if (res < Integer.MIN_VALUE / 10 || (res == Integer.MIN_VALUE / 10 && pop < -8))
                return 0;
            res = res * 10 + pop;
        }
        return res;
    }

    // Follow-up 1: Handle overflow (already handled above)
    // Follow-up 2: BigInteger reverse
    public java.math.BigInteger reverseBig(java.math.BigInteger x) {
        java.math.BigInteger res = java.math.BigInteger.ZERO;
        java.math.BigInteger ten = java.math.BigInteger.TEN;
        boolean neg = x.signum() < 0;
        x = x.abs();
        while (!x.equals(java.math.BigInteger.ZERO)) {
            res = res.multiply(ten).add(x.mod(ten));
            x = x.divide(ten);
        }
        return neg ? res.negate() : res;
    }

    // Follow-up 3: Negative numbers (already handled above)

    public static void main(String[] args) {
        ReverseInteger solution = new ReverseInteger();
        System.out.println(solution.reverse(123)); // 321
        System.out.println(solution.reverse(-123)); // -321
        System.out.println(solution.reverse(1534236469)); // 0 (overflow)
        System.out.println(solution.reverseBig(java.math.BigInteger.valueOf(-1234567890123456789L))); // -9876543210987654321
    }
}
