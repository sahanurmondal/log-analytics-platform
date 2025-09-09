package strings.easy;

import java.util.*;

/**
 * LeetCode 415: Add Strings
 * https://leetcode.com/problems/add-strings/
 * 
 * Companies: Amazon, Apple, Meta, Google, Microsoft, Adobe, Bloomberg
 * Frequency: High (Asked in 600+ interviews)
 *
 * Description:
 * Given two non-negative integers, num1 and num2 represented as string,
 * return the sum of num1 and num2 as a string.
 *
 * You must solve the problem without using any built-in library for handling
 * large integers (such as BigInteger). You must also not convert the inputs
 * to integers directly.
 *
 * Constraints:
 * - 1 <= num1.length, num2.length <= 10^4
 * - num1 and num2 consist of only digits.
 * - num1 and num2 don't have any leading zeros except for the zero itself.
 * 
 * Follow-up Questions:
 * 1. Can you implement subtraction of strings?
 * 2. What about multiplication of strings?
 * 3. How to handle negative numbers?
 * 4. Can you optimize for very large numbers?
 * 5. What if numbers are in different bases?
 */
public class AddStrings {

    // Approach 1: Two-pointer with carry - O(max(m,n)) time, O(max(m,n)) space
    public String addStrings(String num1, String num2) {
        StringBuilder result = new StringBuilder();
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int carry = 0;

        while (i >= 0 || j >= 0 || carry > 0) {
            int digit1 = (i >= 0) ? num1.charAt(i) - '0' : 0;
            int digit2 = (j >= 0) ? num2.charAt(j) - '0' : 0;

            int sum = digit1 + digit2 + carry;
            result.append(sum % 10);
            carry = sum / 10;

            i--;
            j--;
        }

        return result.reverse().toString();
    }

    // Approach 2: Recursive approach - O(max(m,n)) time, O(max(m,n)) space
    public String addStringsRecursive(String num1, String num2) {
        return addHelper(num1, num2, num1.length() - 1, num2.length() - 1, 0);
    }

    private String addHelper(String num1, String num2, int i, int j, int carry) {
        if (i < 0 && j < 0 && carry == 0) {
            return "";
        }

        int digit1 = (i >= 0) ? num1.charAt(i) - '0' : 0;
        int digit2 = (j >= 0) ? num2.charAt(j) - '0' : 0;

        int sum = digit1 + digit2 + carry;
        String currentDigit = String.valueOf(sum % 10);
        String remaining = addHelper(num1, num2, i - 1, j - 1, sum / 10);

        return remaining + currentDigit;
    }

    // Approach 3: Using character array for optimization - O(max(m,n)) time,
    // O(max(m,n)) space
    public String addStringsCharArray(String num1, String num2) {
        int maxLen = Math.max(num1.length(), num2.length()) + 1;
        char[] result = new char[maxLen];

        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int k = maxLen - 1;
        int carry = 0;

        while (i >= 0 || j >= 0 || carry > 0) {
            int digit1 = (i >= 0) ? num1.charAt(i) - '0' : 0;
            int digit2 = (j >= 0) ? num2.charAt(j) - '0' : 0;

            int sum = digit1 + digit2 + carry;
            result[k] = (char) ((sum % 10) + '0');
            carry = sum / 10;

            i--;
            j--;
            k--;
        }

        // Find the first non-zero character (skip leading zeros)
        int start = 0;
        while (start < maxLen && result[start] == 0) {
            start++;
        }

        return new String(result, start, maxLen - start);
    }

    // Approach 4: Optimized for equal length strings - O(n) time, O(n) space
    public String addStringsEqualLength(String num1, String num2) {
        if (num1.length() != num2.length()) {
            // Pad shorter string with leading zeros
            if (num1.length() < num2.length()) {
                num1 = "0".repeat(num2.length() - num1.length()) + num1;
            } else {
                num2 = "0".repeat(num1.length() - num2.length()) + num2;
            }
        }

        StringBuilder result = new StringBuilder();
        int carry = 0;

        for (int i = num1.length() - 1; i >= 0; i--) {
            int sum = (num1.charAt(i) - '0') + (num2.charAt(i) - '0') + carry;
            result.append(sum % 10);
            carry = sum / 10;
        }

        if (carry > 0) {
            result.append(carry);
        }

        return result.reverse().toString();
    }

    // Follow-up 1: Subtract strings
    public String subtractStrings(String num1, String num2) {
        // Assume num1 >= num2 for simplicity
        if (compareStrings(num1, num2) < 0) {
            return "-" + subtractHelper(num2, num1);
        }

        if (compareStrings(num1, num2) == 0) {
            return "0";
        }

        return subtractHelper(num1, num2);
    }

    private String subtractHelper(String num1, String num2) {
        StringBuilder result = new StringBuilder();
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int borrow = 0;

        while (i >= 0) {
            int digit1 = num1.charAt(i) - '0';
            int digit2 = (j >= 0) ? num2.charAt(j) - '0' : 0;

            int diff = digit1 - digit2 - borrow;

            if (diff < 0) {
                diff += 10;
                borrow = 1;
            } else {
                borrow = 0;
            }

            result.append(diff);
            i--;
            j--;
        }

        // Remove leading zeros
        while (result.length() > 1 && result.charAt(result.length() - 1) == '0') {
            result.deleteCharAt(result.length() - 1);
        }

        return result.reverse().toString();
    }

    // Follow-up 2: Multiply strings (LeetCode 43)
    public String multiplyStrings(String num1, String num2) {
        if (num1.equals("0") || num2.equals("0")) {
            return "0";
        }

        int m = num1.length();
        int n = num2.length();
        int[] result = new int[m + n];

        // Reverse both strings to make calculation easier
        for (int i = m - 1; i >= 0; i--) {
            for (int j = n - 1; j >= 0; j--) {
                int digit1 = num1.charAt(i) - '0';
                int digit2 = num2.charAt(j) - '0';
                int product = digit1 * digit2;

                int pos1 = i + j;
                int pos2 = i + j + 1;
                int sum = product + result[pos2];

                result[pos2] = sum % 10;
                result[pos1] += sum / 10;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < result.length; i++) {
            if (!(sb.length() == 0 && result[i] == 0)) {
                sb.append(result[i]);
            }
        }

        return sb.length() == 0 ? "0" : sb.toString();
    }

    // Follow-up 3: Handle negative numbers
    public String addStringsWithSign(String num1, String num2) {
        boolean isNum1Negative = num1.startsWith("-");
        boolean isNum2Negative = num2.startsWith("-");

        if (isNum1Negative)
            num1 = num1.substring(1);
        if (isNum2Negative)
            num2 = num2.substring(1);

        if (isNum1Negative == isNum2Negative) {
            // Same sign: add and keep sign
            String result = addStrings(num1, num2);
            return isNum1Negative ? "-" + result : result;
        } else {
            // Different signs: subtract
            if (isNum1Negative) {
                // -num1 + num2 = num2 - num1
                return subtractStrings(num2, num1);
            } else {
                // num1 + (-num2) = num1 - num2
                return subtractStrings(num1, num2);
            }
        }
    }

    // Follow-up 4: Optimized for very large numbers (using blocks)
    public String addStringsFast(String num1, String num2) {
        // Process in blocks of 9 digits to use int arithmetic
        final int BLOCK_SIZE = 9;
        final int BASE = 1000000000; // 10^9

        List<Integer> blocks1 = stringToBlocks(num1, BLOCK_SIZE);
        List<Integer> blocks2 = stringToBlocks(num2, BLOCK_SIZE);
        List<Integer> result = new ArrayList<>();

        int carry = 0;
        int maxSize = Math.max(blocks1.size(), blocks2.size());

        for (int i = 0; i < maxSize || carry > 0; i++) {
            long sum = carry;

            if (i < blocks1.size()) {
                sum += blocks1.get(i);
            }

            if (i < blocks2.size()) {
                sum += blocks2.get(i);
            }

            result.add((int) (sum % BASE));
            carry = (int) (sum / BASE);
        }

        return blocksToString(result, BLOCK_SIZE);
    }

    // Follow-up 5: Different bases
    public String addStringsBase(String num1, String num2, int base) {
        StringBuilder result = new StringBuilder();
        int i = num1.length() - 1;
        int j = num2.length() - 1;
        int carry = 0;

        while (i >= 0 || j >= 0 || carry > 0) {
            int digit1 = (i >= 0) ? charToDigit(num1.charAt(i)) : 0;
            int digit2 = (j >= 0) ? charToDigit(num2.charAt(j)) : 0;

            int sum = digit1 + digit2 + carry;
            result.append(digitToChar(sum % base));
            carry = sum / base;

            i--;
            j--;
        }

        return result.reverse().toString();
    }

    // Advanced: Add multiple strings
    public String addMultipleStrings(String... numbers) {
        String result = "0";

        for (String num : numbers) {
            result = addStrings(result, num);
        }

        return result;
    }

    // Advanced: Power of string number
    public String powerString(String base, int exponent) {
        if (exponent == 0)
            return "1";
        if (exponent == 1)
            return base;

        String result = "1";
        String currentBase = base;

        while (exponent > 0) {
            if (exponent % 2 == 1) {
                result = multiplyStrings(result, currentBase);
            }
            currentBase = multiplyStrings(currentBase, currentBase);
            exponent /= 2;
        }

        return result;
    }

    // Helper methods
    private int compareStrings(String num1, String num2) {
        if (num1.length() != num2.length()) {
            return Integer.compare(num1.length(), num2.length());
        }
        return num1.compareTo(num2);
    }

    private List<Integer> stringToBlocks(String num, int blockSize) {
        List<Integer> blocks = new ArrayList<>();

        for (int i = num.length(); i > 0; i -= blockSize) {
            int start = Math.max(0, i - blockSize);
            String block = num.substring(start, i);
            blocks.add(Integer.parseInt(block));
        }

        return blocks;
    }

    private String blocksToString(List<Integer> blocks, int blockSize) {
        StringBuilder result = new StringBuilder();

        for (int i = blocks.size() - 1; i >= 0; i--) {
            if (i == blocks.size() - 1) {
                result.append(blocks.get(i));
            } else {
                result.append(String.format("%0" + blockSize + "d", blocks.get(i)));
            }
        }

        return result.toString();
    }

    private int charToDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        } else if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 10;
        } else if (c >= 'a' && c <= 'z') {
            return c - 'a' + 10;
        }
        throw new IllegalArgumentException("Invalid character: " + c);
    }

    private char digitToChar(int digit) {
        if (digit < 10) {
            return (char) (digit + '0');
        } else {
            return (char) (digit - 10 + 'A');
        }
    }

    // Helper: Validate string number
    public boolean isValidNumber(String num) {
        if (num == null || num.isEmpty()) {
            return false;
        }

        for (char c : num.toCharArray()) {
            if (c < '0' || c > '9') {
                return false;
            }
        }

        return true;
    }

    // Helper: Remove leading zeros
    public String removeLeadingZeros(String num) {
        int i = 0;
        while (i < num.length() - 1 && num.charAt(i) == '0') {
            i++;
        }
        return num.substring(i);
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(String num1, String num2) {
        Map<String, Long> results = new HashMap<>();

        // Test standard approach
        long start = System.nanoTime();
        addStrings(num1, num2);
        results.put("Standard", System.nanoTime() - start);

        // Test recursive approach
        start = System.nanoTime();
        addStringsRecursive(num1, num2);
        results.put("Recursive", System.nanoTime() - start);

        // Test char array approach
        start = System.nanoTime();
        addStringsCharArray(num1, num2);
        results.put("CharArray", System.nanoTime() - start);

        // Test equal length approach
        start = System.nanoTime();
        addStringsEqualLength(num1, num2);
        results.put("EqualLength", System.nanoTime() - start);

        // Test fast approach (for large numbers)
        if (num1.length() > 50 && num2.length() > 50) {
            start = System.nanoTime();
            addStringsFast(num1, num2);
            results.put("Fast", System.nanoTime() - start);
        }

        return results;
    }

    public static void main(String[] args) {
        AddStrings solution = new AddStrings();

        // Test Case 1: Standard examples
        System.out.println("=== Test Case 1: Standard Examples ===");
        String[][] testCases = {
                { "11", "123" }, // Expected: "134"
                { "456", "77" }, // Expected: "533"
                { "0", "0" }, // Expected: "0"
                { "1", "9" }, // Expected: "10"
                { "999", "1" }, // Expected: "1000"
                { "123456789", "987654321" } // Expected: "1111111110"
        };

        for (String[] test : testCases) {
            String result = solution.addStrings(test[0], test[1]);
            System.out.println(test[0] + " + " + test[1] + " = " + result);
        }

        // Test Case 2: Compare all approaches
        System.out.println("\n=== Test Case 2: Compare All Approaches ===");
        String num1 = "12345";
        String num2 = "6789";

        String standard = solution.addStrings(num1, num2);
        String recursive = solution.addStringsRecursive(num1, num2);
        String charArray = solution.addStringsCharArray(num1, num2);
        String equalLength = solution.addStringsEqualLength(num1, num2);

        System.out.println("Input: " + num1 + " + " + num2);
        System.out.println("Standard: " + standard);
        System.out.println("Recursive: " + recursive);
        System.out.println("CharArray: " + charArray);
        System.out.println("EqualLength: " + equalLength);

        boolean allSame = standard.equals(recursive) &&
                recursive.equals(charArray) &&
                charArray.equals(equalLength);
        System.out.println("All approaches consistent: " + allSame);

        // Follow-up 1: Subtraction
        System.out.println("\n=== Follow-up 1: Subtraction ===");
        String[][] subTests = {
                { "123", "45" }, // Expected: "78"
                { "1000", "1" }, // Expected: "999"
                { "100", "99" }, // Expected: "1"
                { "50", "100" } // Expected: "-50"
        };

        for (String[] test : subTests) {
            String result = solution.subtractStrings(test[0], test[1]);
            System.out.println(test[0] + " - " + test[1] + " = " + result);
        }

        // Follow-up 2: Multiplication
        System.out.println("\n=== Follow-up 2: Multiplication ===");
        String[][] multTests = {
                { "123", "456" }, // Expected: "56088"
                { "99", "99" }, // Expected: "9801"
                { "0", "123" }, // Expected: "0"
                { "12", "34" } // Expected: "408"
        };

        for (String[] test : multTests) {
            String result = solution.multiplyStrings(test[0], test[1]);
            System.out.println(test[0] + " * " + test[1] + " = " + result);
        }

        // Follow-up 3: Negative numbers
        System.out.println("\n=== Follow-up 3: Negative Numbers ===");
        String[][] negativeTests = {
                { "123", "-45" }, // Expected: "78"
                { "-123", "45" }, // Expected: "-78"
                { "-123", "-45" }, // Expected: "-168"
                { "100", "-200" } // Expected: "-100"
        };

        for (String[] test : negativeTests) {
            String result = solution.addStringsWithSign(test[0], test[1]);
            System.out.println(test[0] + " + " + test[1] + " = " + result);
        }

        // Follow-up 5: Different bases
        System.out.println("\n=== Follow-up 5: Different Bases ===");

        // Binary addition (base 2)
        String binary1 = "1010"; // 10 in decimal
        String binary2 = "1011"; // 11 in decimal
        String binaryResult = solution.addStringsBase(binary1, binary2, 2);
        System.out.println("Binary: " + binary1 + " + " + binary2 + " = " + binaryResult);

        // Hexadecimal addition (base 16)
        String hex1 = "1A"; // 26 in decimal
        String hex2 = "2B"; // 43 in decimal
        String hexResult = solution.addStringsBase(hex1, hex2, 16);
        System.out.println("Hex: " + hex1 + " + " + hex2 + " = " + hexResult);

        // Advanced: Multiple strings
        System.out.println("\n=== Advanced: Multiple Strings ===");
        String multiResult = solution.addMultipleStrings("123", "456", "789", "101112");
        System.out.println("123 + 456 + 789 + 101112 = " + multiResult);

        // Advanced: Power
        System.out.println("\n=== Advanced: Power ===");
        String powerResult = solution.powerString("12", 3);
        System.out.println("12^3 = " + powerResult);

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");

        // Generate large numbers for performance testing
        StringBuilder large1 = new StringBuilder();
        StringBuilder large2 = new StringBuilder();

        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            large1.append(random.nextInt(10));
            large2.append(random.nextInt(10));
        }

        String largeNum1 = large1.toString();
        String largeNum2 = large2.toString();

        Map<String, Long> performance = solution.comparePerformance(largeNum1, largeNum2);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Single digits
        System.out.println("Single digits: " + solution.addStrings("5", "5"));

        // Very different lengths
        System.out.println("Different lengths: " + solution.addStrings("1", "99999999"));

        // All nines
        System.out.println("All nines: " + solution.addStrings("999", "1"));

        // Leading zeros (after removal)
        String withZeros = solution.addStrings("000123", "000456");
        System.out.println("With leading zeros: " + withZeros);

        // Maximum carry propagation
        System.out.println("Max carry: " + solution.addStrings("9999", "1"));

        // Helper methods testing
        System.out.println("\n=== Helper Methods ===");

        System.out.println("Is '12345' valid: " + solution.isValidNumber("12345"));
        System.out.println("Is '123a45' valid: " + solution.isValidNumber("123a45"));
        System.out.println("Remove leading zeros '00012300': " +
                solution.removeLeadingZeros("00012300"));

        // Verification with Java BigInteger
        System.out.println("\n=== BigInteger Verification ===");
        String verifyNum1 = "123456789012345678901234567890";
        String verifyNum2 = "987654321098765432109876543210";

        String ourResult = solution.addStrings(verifyNum1, verifyNum2);

        java.math.BigInteger big1 = new java.math.BigInteger(verifyNum1);
        java.math.BigInteger big2 = new java.math.BigInteger(verifyNum2);
        String bigIntResult = big1.add(big2).toString();

        System.out.println("Our result: " + ourResult);
        System.out.println("BigInteger: " + bigIntResult);
        System.out.println("Results match: " + ourResult.equals(bigIntResult));

        // Stress test
        System.out.println("\n=== Stress Test ===");
        StringBuilder stress1 = new StringBuilder();
        StringBuilder stress2 = new StringBuilder();

        for (int i = 0; i < 10000; i++) {
            stress1.append("9");
            stress2.append("1");
        }

        long stressStart = System.currentTimeMillis();
        String stressResult = solution.addStrings(stress1.toString(), stress2.toString());
        long stressTime = System.currentTimeMillis() - stressStart;

        System.out.println("Stress test (10000 digits): " + stressTime + " ms");
        System.out.println("Result length: " + stressResult.length());
        System.out.println("Result starts with: " + stressResult.substring(0, Math.min(20, stressResult.length())));
    }
}
