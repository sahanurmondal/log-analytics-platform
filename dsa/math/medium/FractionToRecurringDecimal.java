package math.medium;

/**
 * LeetCode 166: Fraction to Recurring Decimal
 * https://leetcode.com/problems/fraction-to-recurring-decimal/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given two integers representing the numerator and denominator, return the
 * fraction in decimal form.
 *
 * Constraints:
 * - -2^31 <= numerator, denominator <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you handle very large numbers?
 * 2. Can you return the recurring part only?
 * 3. Can you handle negative numbers?
 */
public class FractionToRecurringDecimal {
    public String fractionToDecimal(int numerator, int denominator) {
        if (numerator == 0)
            return "0";
        StringBuilder sb = new StringBuilder();
        if ((numerator < 0) ^ (denominator < 0))
            sb.append("-");
        long num = Math.abs((long) numerator), den = Math.abs((long) denominator);
        sb.append(num / den);
        num %= den;
        if (num == 0)
            return sb.toString();
        sb.append(".");
        java.util.Map<Long, Integer> map = new java.util.HashMap<>();
        while (num != 0) {
            if (map.containsKey(num)) {
                sb.insert(map.get(num), "(");
                sb.append(")");
                break;
            }
            map.put(num, sb.length());
            num *= 10;
            sb.append(num / den);
            num %= den;
        }
        return sb.toString();
    }

    // Follow-up 1: Handle very large numbers (use BigInteger)
    public String fractionToDecimalBig(java.math.BigInteger numerator, java.math.BigInteger denominator) {
        if (numerator.equals(java.math.BigInteger.ZERO))
            return "0";
        StringBuilder sb = new StringBuilder();
        if (numerator.signum() != denominator.signum())
            sb.append("-");
        numerator = numerator.abs();
        denominator = denominator.abs();
        sb.append(numerator.divide(denominator));
        numerator = numerator.mod(denominator);
        if (numerator.equals(java.math.BigInteger.ZERO))
            return sb.toString();
        sb.append(".");
        java.util.Map<java.math.BigInteger, Integer> map = new java.util.HashMap<>();
        while (!numerator.equals(java.math.BigInteger.ZERO)) {
            if (map.containsKey(numerator)) {
                sb.insert(map.get(numerator), "(");
                sb.append(")");
                break;
            }
            map.put(numerator, sb.length());
            numerator = numerator.multiply(java.math.BigInteger.TEN);
            sb.append(numerator.divide(denominator));
            numerator = numerator.mod(denominator);
        }
        return sb.toString();
    }

    // Follow-up 2: Return recurring part only
    public String getRecurringPart(int numerator, int denominator) {
        if (numerator == 0)
            return "";
        long num = Math.abs((long) numerator), den = Math.abs((long) denominator);
        java.util.Map<Long, Integer> map = new java.util.HashMap<>();
        StringBuilder sb = new StringBuilder();
        num %= den;
        while (num != 0) {
            if (map.containsKey(num)) {
                return sb.substring(map.get(num));
            }
            map.put(num, sb.length());
            num *= 10;
            sb.append(num / den);
            num %= den;
        }
        return "";
    }

    // Follow-up 3: Handle negative numbers (already handled above)

    public static void main(String[] args) {
        FractionToRecurringDecimal solution = new FractionToRecurringDecimal();
        System.out.println(solution.fractionToDecimal(1, 2)); // "0.5"
        System.out.println(solution.fractionToDecimal(2, 3)); // "0.(6)"
        System.out.println(
                solution.fractionToDecimalBig(java.math.BigInteger.valueOf(1), java.math.BigInteger.valueOf(7))); // "0.(142857)"
        System.out.println(solution.getRecurringPart(2, 3)); // "6"
    }
}
