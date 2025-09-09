package math.medium;

/**
 * LeetCode 171: Excel Sheet Column Number
 * https://leetcode.com/problems/excel-sheet-column-number/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a column title as appear in an Excel sheet, return its corresponding
 * column number.
 *
 * Constraints:
 * - 1 <= s.length <= 7
 *
 * Follow-ups:
 * 1. Can you convert column number to title?
 * 2. Can you handle very large columns?
 * 3. Can you handle lowercase letters?
 */
public class ExcelSheetColumnNumber {
    public int titleToNumber(String s) {
        int res = 0;
        for (char c : s.toCharArray())
            res = res * 26 + (c - 'A' + 1);
        return res;
    }

    // Follow-up 1: Convert number to title
    public String numberToTitle(int n) {
        StringBuilder sb = new StringBuilder();
        while (n > 0) {
            n--;
            sb.append((char) ('A' + n % 26));
            n /= 26;
        }
        return sb.reverse().toString();
    }

    // Follow-up 2: Handle very large columns (use BigInteger)
    public java.math.BigInteger titleToNumberBig(String s) {
        java.math.BigInteger res = java.math.BigInteger.ZERO;
        for (char c : s.toCharArray())
            res = res.multiply(java.math.BigInteger.valueOf(26)).add(java.math.BigInteger.valueOf(c - 'A' + 1));
        return res;
    }

    // Follow-up 3: Handle lowercase letters
    public int titleToNumberLower(String s) {
        int res = 0;
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c))
                res = res * 26 + (c - 'a' + 1);
            else
                res = res * 26 + (c - 'A' + 1);
        }
        return res;
    }

    public static void main(String[] args) {
        ExcelSheetColumnNumber solution = new ExcelSheetColumnNumber();
        System.out.println(solution.titleToNumber("AB")); // 28
        System.out.println(solution.numberToTitle(28)); // AB
        System.out.println(solution.titleToNumberBig("ZZZZZZZ")); // BigInteger
        System.out.println(solution.titleToNumberLower("ab")); // 28
    }
}
