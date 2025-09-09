package math.medium;

/**
 * LeetCode 50: Pow(x, n)
 * https://leetcode.com/problems/powx-n/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Implement pow(x, n), which calculates x raised to the power n.
 *
 * Constraints:
 * - -100.0 < x < 100.0
 * - -2^31 <= n <= 2^31-1
 *
 * Follow-ups:
 * 1. Can you handle negative exponents?
 * 2. Can you optimize for large n?
 * 3. Can you compute pow for BigDecimal?
 */
public class PowXN {
    public double myPow(double x, int n) {
        if (n == 0)
            return 1.0;
        long N = n;
        if (N < 0) {
            x = 1 / x;
            N = -N;
        }
        double res = 1.0;
        while (N > 0) {
            if ((N & 1) == 1)
                res *= x;
            x *= x;
            N >>= 1;
        }
        return res;
    }

    // Follow-up 1: Negative exponents (already handled above)
    // Follow-up 2: Optimize for large n (already handled above)
    // Follow-up 3: BigDecimal pow
    public java.math.BigDecimal bigPow(java.math.BigDecimal x, int n) {
        if (n == 0)
            return java.math.BigDecimal.ONE;
        if (n < 0)
            return java.math.BigDecimal.ONE.divide(bigPow(x, -n), 20, java.math.RoundingMode.HALF_UP);
        java.math.BigDecimal res = java.math.BigDecimal.ONE;
        while (n > 0) {
            if ((n & 1) == 1)
                res = res.multiply(x);
            x = x.multiply(x);
            n >>= 1;
        }
        return res;
    }

    public static void main(String[] args) {
        PowXN solution = new PowXN();
        System.out.println(solution.myPow(2.0, 10)); // 1024.0
        System.out.println(solution.myPow(2.0, -2)); // 0.25
        System.out.println(solution.bigPow(java.math.BigDecimal.valueOf(2.0), 10)); // 1024
    }
}
