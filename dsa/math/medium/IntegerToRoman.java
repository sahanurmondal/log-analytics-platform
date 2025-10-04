package math.medium;

/**
 * LeetCode 12: Integer to Roman
 * https://leetcode.com/problems/integer-to-roman/
 *
 * Description:
 * Convert an integer to a Roman numeral.
 *
 * Constraints:
 * - 1 <= num <= 3999
 *
 * Follow-up:
 * - Can you solve it in O(1) time?
 */
public class IntegerToRoman {
    public String intToRoman(int num) {
        String[] thousands = { "", "M", "MM", "MMM" };
        String[] hundreds = { "", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM" };
        String[] tens = { "", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC" };
        String[] ones = { "", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX" };

        return thousands[num / 1000] +
                hundreds[(num % 1000) / 100] +
                tens[(num % 100) / 10] +
                ones[num % 10];
    }

    public static void main(String[] args) {
        IntegerToRoman solution = new IntegerToRoman();
        // Edge Case 1: Normal case
        System.out.println(solution.intToRoman(1994)); // "MCMXCIV"
        // Edge Case 2: Minimum value
        System.out.println(solution.intToRoman(1)); // "I"
        // Edge Case 3: Maximum value
        System.out.println(solution.intToRoman(3999)); // "MMMCMXCIX"
        // Edge Case 4: Value with all symbols
        System.out.println(solution.intToRoman(58)); // "LVIII"
        // Edge Case 5: Value with subtractive notation
        System.out.println(solution.intToRoman(4)); // "IV"
    }
}
