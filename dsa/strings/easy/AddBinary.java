package strings.easy;

/**
 * LeetCode 67: Add Binary
 *
 * Given two binary strings a and b, return their sum as a binary string.
 *
 * Example 1:
 * Input: a = "11", b = "1"
 * Output: "100"
 *
 * Example 2:
 * Input: a = "1010", b = "1011"
 * Output: "10101"
 */
public class AddBinary {

    /**
     * Solution: Simulate Binary Addition
     * Time: O(max(len(a), len(b))), Space: O(max(len(a), len(b)))
     *
     * Start from the rightmost bits and add with carry
     * Similar to adding two numbers with carry
     */
    public String addBinary(String a, String b) {
        StringBuilder result = new StringBuilder();
        int carry = 0;
        int i = a.length() - 1;
        int j = b.length() - 1;

        while (i >= 0 || j >= 0 || carry > 0) {
            int sum = carry;

            if (i >= 0) {
                sum += a.charAt(i) - '0';
                i--;
            }

            if (j >= 0) {
                sum += b.charAt(j) - '0';
                j--;
            }

            // Binary: sum can be 0, 1, 2, or 3
            // 0: bit=0, carry=0
            // 1: bit=1, carry=0
            // 2: bit=0, carry=1
            // 3: bit=1, carry=1
            result.append(sum % 2);
            carry = sum / 2;
        }

        return result.reverse().toString();
    }

    /**
     * Alternative: Using Integer.parseInt
     * Time: O(max(len(a), len(b))), Space: O(max(len(a), len(b)))
     *
     * Only works for strings that fit in integer range
     */
    public String addBinaryV2(String a, String b) {
        // Parse as binary integer
        long numA = Long.parseLong(a, 2);
        long numB = Long.parseLong(b, 2);

        // Add them
        long sum = numA + numB;

        // Convert back to binary
        return Long.toBinaryString(sum);
    }

    /**
     * Alternative: Using BigInteger for larger numbers
     */
    public String addBinaryV3(String a, String b) {
        java.math.BigInteger numA = new java.math.BigInteger(a, 2);
        java.math.BigInteger numB = new java.math.BigInteger(b, 2);

        java.math.BigInteger sum = numA.add(numB);

        return sum.toString(2);
    }

    public static void main(String[] args) {
        AddBinary solution = new AddBinary();

        // Test case 1
        System.out.println(solution.addBinary("11", "1"));      // "100" (3 + 1 = 4)

        // Test case 2
        System.out.println(solution.addBinary("1010", "1011")); // "10101" (10 + 11 = 21)

        // Test case 3
        System.out.println(solution.addBinary("0", "0"));       // "0"

        // Test case 4
        System.out.println(solution.addBinary("1", "111"));     // "1000" (1 + 7 = 8)

        // Test case 5
        System.out.println(solution.addBinary("1111", "1111")); // "11110" (15 + 15 = 30)
    }
}

