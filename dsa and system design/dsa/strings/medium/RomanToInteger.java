package strings.medium;

import java.util.*;

/**
 * LeetCode 13: Roman to Integer
 * https://leetcode.com/problems/roman-to-integer/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given a roman numeral, convert it to an integer.
 *
 * Constraints:
 * - 1 <= s.length <= 15
 * - s contains only characters ('I', 'V', 'X', 'L', 'C', 'D', 'M')
 * - 1 <= num <= 3999
 * 
 * Follow-up Questions:
 * 1. Can you convert integer to roman?
 * 2. Can you validate roman numerals?
 * 3. Can you handle extended roman numerals?
 */
public class RomanToInteger {

    private static final Map<Character, Integer> romanMap = new HashMap<>();
    static {
        romanMap.put('I', 1);
        romanMap.put('V', 5);
        romanMap.put('X', 10);
        romanMap.put('L', 50);
        romanMap.put('C', 100);
        romanMap.put('D', 500);
        romanMap.put('M', 1000);
    }

    // Approach 1: Left to right scanning (O(n) time)
    public int romanToInt(String s) {
        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            int current = romanMap.get(s.charAt(i));
            if (i + 1 < s.length() && current < romanMap.get(s.charAt(i + 1))) {
                result -= current;
            } else {
                result += current;
            }
        }
        return result;
    }

    // Follow-up 1: Convert integer to roman
    public String intToRoman(int num) {
        int[] values = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 };
        String[] symbols = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" };

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            while (num >= values[i]) {
                result.append(symbols[i]);
                num -= values[i];
            }
        }
        return result.toString();
    }

    // Follow-up 2: Validate roman numerals
    public boolean isValidRoman(String s) {
        if (s == null || s.isEmpty())
            return false;

        // Convert to int and back to roman, check if same
        try {
            int num = romanToInt(s);
            String converted = intToRoman(num);
            return s.equals(converted);
        } catch (Exception e) {
            return false;
        }
    }

    // Follow-up 3: Extended roman numerals with overlines (thousands multiplier)
    public int romanToIntExtended(String s) {
        // For this example, assume overline represented by lowercase
        // a=1000*I, b=1000*V, etc.
        Map<Character, Integer> extendedMap = new HashMap<>(romanMap);
        extendedMap.put('i', 1000); // I with overline
        extendedMap.put('v', 5000); // V with overline
        extendedMap.put('x', 10000); // X with overline

        int result = 0;
        for (int i = 0; i < s.length(); i++) {
            int current = extendedMap.getOrDefault(s.charAt(i), 0);
            if (current == 0)
                continue; // Invalid character

            if (i + 1 < s.length() && current < extendedMap.getOrDefault(s.charAt(i + 1), 0)) {
                result -= current;
            } else {
                result += current;
            }
        }
        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        RomanToInteger solution = new RomanToInteger();

        // Test case 1: Basic cases
        String[] romans = { "III", "LVIII", "MCMXC" };
        int[] expected = { 3, 58, 1990 };
        for (int i = 0; i < romans.length; i++) {
            System.out.println("Test " + (i + 1) + " - Roman: " + romans[i] + " Expected: " + expected[i]);
            System.out.println("Result: " + solution.romanToInt(romans[i]));
        }

        // Test case 2: Integer to roman
        System.out.println("\nTest - Integer to Roman:");
        for (int num : expected) {
            System.out.println(num + " -> " + solution.intToRoman(num));
        }

        // Test case 3: Validate roman numerals
        System.out.println("\nTest - Validate Roman:");
        String[] testRomans = { "IV", "IIII", "XL", "XXXX" };
        for (String roman : testRomans) {
            System.out.println(roman + " is valid: " + solution.isValidRoman(roman));
        }

        // Test case 4: Extended roman numerals
        System.out.println("\nTest - Extended Roman:");
        System.out.println("iV (1004): " + solution.romanToIntExtended("iV"));
        System.out.println("vMCM (6900): " + solution.romanToIntExtended("vMCM"));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single I: " + solution.romanToInt("I"));
        System.out.println("Single M: " + solution.romanToInt("M"));
        System.out.println("Subtractive IV: " + solution.romanToInt("IV"));
        System.out.println("Subtractive IX: " + solution.romanToInt("IX"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++)
            sb.append("M");
        long start = System.nanoTime();
        int result = solution.romanToInt(sb.toString());
        long end = System.nanoTime();
        System.out.println("1000 Ms: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
