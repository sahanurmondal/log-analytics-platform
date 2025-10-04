package math.medium;

/**
 * LeetCode 43: Multiply Strings
 * https://leetcode.com/problems/multiply-strings/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Multiply two non-negative numbers represented as strings.
 *
 * Constraints:
 * - 1 <= num1.length, num2.length <= 200
 *
 * Follow-ups:
 * 1. Can you handle very large numbers?
 * 2. Can you multiply more than two numbers?
 * 3. Can you multiply negative numbers?
 */
public class MultiplyStrings {
    public String multiply(String num1, String num2) {
        int m = num1.length(), n = num2.length();
        int[] res = new int[m + n];
        for (int i = m - 1; i >= 0; i--)
            for (int j = n - 1; j >= 0; j--) {
                int mul = (num1.charAt(i) - '0') * (num2.charAt(j) - '0');
                int sum = mul + res[i + j + 1];
                res[i + j] += sum / 10;
                res[i + j + 1] = sum % 10;
            }
        StringBuilder sb = new StringBuilder();
        for (int r : res)
            if (!(sb.length() == 0 && r == 0))
                sb.append(r);
        return sb.length() == 0 ? "0" : sb.toString();
    }

    // Follow-up 1: Very large numbers (use BigInteger)
    public String multiplyBig(String num1, String num2) {
        java.math.BigInteger a = new java.math.BigInteger(num1);
        java.math.BigInteger b = new java.math.BigInteger(num2);
        return a.multiply(b).toString();
    }

    // Follow-up 2: Multiply more than two numbers
    public String multiplyMultiple(String[] nums) {
        java.math.BigInteger res = java.math.BigInteger.ONE;
        for (String num : nums)
            res = res.multiply(new java.math.BigInteger(num));
        return res.toString();
    }

    // Follow-up 3: Multiply negative numbers
    public String multiplyWithSign(String num1, String num2) {
        boolean neg = false;
        if (num1.startsWith("-")) {
            neg = !neg;
            num1 = num1.substring(1);
        }
        if (num2.startsWith("-")) {
            neg = !neg;
            num2 = num2.substring(1);
        }
        String prod = multiply(num1, num2);
        return neg && !prod.equals("0") ? "-" + prod : prod;
    }

    public static void main(String[] args) {
        MultiplyStrings solution = new MultiplyStrings();
        System.out.println(solution.multiply("123", "456")); // "56088"
        System.out.println(solution.multiplyBig("12345678901234567890", "98765432109876543210")); // BigInteger
        System.out.println(solution.multiplyMultiple(new String[] { "2", "3", "4" })); // "24"
        System.out.println(solution.multiplyWithSign("-123", "456")); // "-56088"
    }
}
