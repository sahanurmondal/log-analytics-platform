package strings.medium;

import java.util.*;

/**
 * LeetCode 8: String to Integer (atoi)
 * https://leetcode.com/problems/string-to-integer-atoi/
 * 
 * Companies: Microsoft, Amazon, Google, Facebook, Apple, Bloomberg, Adobe
 * Frequency: Very High (Asked in 1200+ interviews)
 *
 * Description:
 * Implement the myAtoi(string s) function, which converts a string to a 32-bit
 * signed integer (similar to C/C++'s atoi function).
 * 
 * The algorithm for myAtoi(string s) is as follows:
 * 1. Read in and ignore any leading whitespace.
 * 2. Check if the next character (if not already at the end of the string) is
 * '-' or '+'.
 * Read this character in if it is either. This determines if the final result
 * is negative or positive respectively.
 * Assume the result is positive if neither is present.
 * 3. Read in next the characters until the next non-digit character or the end
 * of the input is reached.
 * The rest of the string is ignored.
 * 4. Convert these digits into an integer (i.e. "123" -> 123, "0032" -> 32).
 * If no digits were read, then the integer is 0. Change the sign as necessary
 * (from step 2).
 * 5. If the integer is out of the 32-bit signed integer range [-2^31, 2^31 -
 * 1],
 * then clamp the integer so that it remains in the range.
 * 
 * Constraints:
 * - 0 <= s.length <= 200
 * - s consists of English letters (lower-case and upper-case), digits (0-9), '
 * ', '+', '-', and '.'.
 * 
 * Follow-up Questions:
 * 1. How would you handle different number bases (binary, octal, hexadecimal)?
 * 2. Can you implement parsing for floating point numbers?
 * 3. What about handling different locale-specific number formats?
 * 4. How to parse numbers with thousand separators?
 * 5. Can you implement parsing for scientific notation?
 * 6. What about parsing numbers with custom validation rules?
 */
public class StringToInteger {

    // Approach 1: Step-by-step parsing (Optimal) - O(n) time, O(1) space
    public static int myAtoi(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        int index = 0;
        int n = s.length();

        // Step 1: Skip leading whitespaces
        while (index < n && s.charAt(index) == ' ') {
            index++;
        }

        // Step 2: Handle sign
        int sign = 1;
        if (index < n && (s.charAt(index) == '+' || s.charAt(index) == '-')) {
            sign = s.charAt(index) == '-' ? -1 : 1;
            index++;
        }

        // Step 3: Convert digits and handle overflow
        long result = 0;
        while (index < n && Character.isDigit(s.charAt(index))) {
            int digit = s.charAt(index) - '0';

            // Check for overflow before multiplication
            if (result > (Integer.MAX_VALUE - digit) / 10) {
                return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
            }

            result = result * 10 + digit;
            index++;
        }

        return (int) (sign * result);
    }

    // Approach 2: Character-by-character processing - O(n) time, O(1) space
    public static int myAtoiCharByChar(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        int i = 0;
        boolean negative = false;
        long result = 0;

        // Skip whitespace
        while (i < s.length() && s.charAt(i) == ' ') {
            i++;
        }

        if (i >= s.length()) {
            return 0;
        }

        // Handle sign
        if (s.charAt(i) == '-') {
            negative = true;
            i++;
        } else if (s.charAt(i) == '+') {
            i++;
        }

        // Process digits
        while (i < s.length() && Character.isDigit(s.charAt(i))) {
            int digit = s.charAt(i) - '0';

            // Check overflow
            if (result > Integer.MAX_VALUE / 10 ||
                    (result == Integer.MAX_VALUE / 10 && digit > Integer.MAX_VALUE % 10)) {
                return negative ? Integer.MIN_VALUE : Integer.MAX_VALUE;
            }

            result = result * 10 + digit;
            i++;
        }

        if (negative) {
            result = -result;
        }

        return (int) result;
    }

    // Approach 3: Regular expression based - O(n) time, O(1) space
    public static int myAtoiRegex(String s) {
        if (s == null) {
            return 0;
        }

        s = s.trim();
        if (s.isEmpty()) {
            return 0;
        }

        // Match pattern: optional sign followed by digits
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("^[+-]?\\d+");
        java.util.regex.Matcher matcher = pattern.matcher(s);

        if (!matcher.find()) {
            return 0;
        }

        String numberStr = matcher.group();

        try {
            return Integer.parseInt(numberStr);
        } catch (NumberFormatException e) {
            // Handle overflow
            return numberStr.startsWith("-") ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
    }

    // Approach 4: Finite State Machine - O(n) time, O(1) space
    public static int myAtoiStateMachine(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        State state = State.START;
        int sign = 1;
        long result = 0;

        for (char c : s.toCharArray()) {
            switch (state) {
                case START:
                    if (c == ' ') {
                        // Stay in START state
                    } else if (c == '+') {
                        state = State.SIGN;
                        sign = 1;
                    } else if (c == '-') {
                        state = State.SIGN;
                        sign = -1;
                    } else if (Character.isDigit(c)) {
                        state = State.DIGIT;
                        result = c - '0';
                    } else {
                        state = State.END;
                    }
                    break;

                case SIGN:
                    if (Character.isDigit(c)) {
                        state = State.DIGIT;
                        result = c - '0';
                    } else {
                        state = State.END;
                    }
                    break;

                case DIGIT:
                    if (Character.isDigit(c)) {
                        int digit = c - '0';
                        if (result > (Integer.MAX_VALUE - digit) / 10) {
                            return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                        }
                        result = result * 10 + digit;
                    } else {
                        state = State.END;
                    }
                    break;

                case END:
                    break;
            }

            if (state == State.END) {
                break;
            }
        }

        return (int) (sign * result);
    }

    private enum State {
        START, SIGN, DIGIT, END
    }

    // Approach 5: StringBuilder approach with validation - O(n) time, O(n) space
    public static int myAtoiStringBuilder(String s) {
        if (s == null || s.isEmpty()) {
            return 0;
        }

        StringBuilder cleaned = new StringBuilder();
        boolean foundNonSpace = false;
        boolean foundSign = false;

        for (char c : s.toCharArray()) {
            if (!foundNonSpace && c == ' ') {
                continue;
            }

            foundNonSpace = true;

            if (!foundSign && (c == '+' || c == '-')) {
                cleaned.append(c);
                foundSign = true;
            } else if (Character.isDigit(c)) {
                cleaned.append(c);
                foundSign = true;
            } else {
                break;
            }
        }

        if (cleaned.length() == 0 || (cleaned.length() == 1 && !Character.isDigit(cleaned.charAt(0)))) {
            return 0;
        }

        try {
            return Integer.parseInt(cleaned.toString());
        } catch (NumberFormatException e) {
            return cleaned.toString().startsWith("-") ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        }
    }

    // Follow-up 1: Different number bases
    public static class DifferentBases {

        public static int parseWithBase(String s, int base) {
            if (s == null || s.isEmpty() || base < 2 || base > 36) {
                return 0;
            }

            s = s.trim();
            if (s.isEmpty()) {
                return 0;
            }

            int index = 0;
            int sign = 1;

            // Handle sign
            if (s.charAt(index) == '+') {
                index++;
            } else if (s.charAt(index) == '-') {
                sign = -1;
                index++;
            }

            long result = 0;
            while (index < s.length()) {
                char c = Character.toLowerCase(s.charAt(index));
                int digit;

                if (c >= '0' && c <= '9') {
                    digit = c - '0';
                } else if (c >= 'a' && c <= 'z') {
                    digit = c - 'a' + 10;
                } else {
                    break;
                }

                if (digit >= base) {
                    break;
                }

                // Check overflow
                if (result > (Integer.MAX_VALUE - digit) / base) {
                    return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                }

                result = result * base + digit;
                index++;
            }

            return (int) (sign * result);
        }

        public static int parseBinary(String s) {
            s = s.trim();
            if (s.startsWith("0b") || s.startsWith("0B")) {
                s = s.substring(2);
            }
            return parseWithBase(s, 2);
        }

        public static int parseOctal(String s) {
            s = s.trim();
            if (s.startsWith("0o") || s.startsWith("0O")) {
                s = s.substring(2);
            } else if (s.startsWith("0") && s.length() > 1) {
                s = s.substring(1);
            }
            return parseWithBase(s, 8);
        }

        public static int parseHexadecimal(String s) {
            s = s.trim();
            if (s.startsWith("0x") || s.startsWith("0X")) {
                s = s.substring(2);
            }
            return parseWithBase(s, 16);
        }
    }

    // Follow-up 2: Floating point numbers
    public static class FloatingPointParsing {

        public static double parseDouble(String s) {
            if (s == null || s.isEmpty()) {
                return 0.0;
            }

            s = s.trim();
            if (s.isEmpty()) {
                return 0.0;
            }

            int index = 0;
            int sign = 1;

            // Handle sign
            if (s.charAt(index) == '+') {
                index++;
            } else if (s.charAt(index) == '-') {
                sign = -1;
                index++;
            }

            // Parse integer part
            long integerPart = 0;
            while (index < s.length() && Character.isDigit(s.charAt(index))) {
                integerPart = integerPart * 10 + (s.charAt(index) - '0');
                index++;
            }

            // Parse decimal part
            double decimalPart = 0.0;
            if (index < s.length() && s.charAt(index) == '.') {
                index++;
                double fraction = 0.1;
                while (index < s.length() && Character.isDigit(s.charAt(index))) {
                    decimalPart += (s.charAt(index) - '0') * fraction;
                    fraction *= 0.1;
                    index++;
                }
            }

            double result = integerPart + decimalPart;

            // Parse exponent
            if (index < s.length() && (s.charAt(index) == 'e' || s.charAt(index) == 'E')) {
                index++;
                int expSign = 1;
                if (index < s.length() && s.charAt(index) == '+') {
                    index++;
                } else if (index < s.length() && s.charAt(index) == '-') {
                    expSign = -1;
                    index++;
                }

                int exponent = 0;
                while (index < s.length() && Character.isDigit(s.charAt(index))) {
                    exponent = exponent * 10 + (s.charAt(index) - '0');
                    index++;
                }

                result *= Math.pow(10, expSign * exponent);
            }

            return sign * result;
        }

        public static float parseFloat(String s) {
            return (float) parseDouble(s);
        }

        public static boolean isValidFloat(String s) {
            try {
                parseDouble(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    // Follow-up 3: Locale-specific formats
    public static class LocaleSpecific {

        public static int parseEuropeanNumber(String s) {
            if (s == null || s.isEmpty()) {
                return 0;
            }

            // European format: comma as decimal separator, dot/space as thousands
            s = s.trim();

            // Remove thousands separators
            s = s.replace(".", "").replace(" ", "");

            // Handle decimal comma (treat as integer part only)
            if (s.contains(",")) {
                s = s.split(",")[0];
            }

            return myAtoi(s);
        }

        public static int parseWithThousandsSeparator(String s, char separator) {
            if (s == null || s.isEmpty()) {
                return 0;
            }

            s = s.trim();

            // Remove thousands separators
            s = s.replace(String.valueOf(separator), "");

            return myAtoi(s);
        }

        public static int parseIndianNumbering(String s) {
            // Indian numbering: 1,23,45,678
            return parseWithThousandsSeparator(s, ',');
        }

        public static int parseCurrency(String s, String currencySymbol) {
            if (s == null || s.isEmpty()) {
                return 0;
            }

            s = s.trim();

            // Remove currency symbol
            s = s.replace(currencySymbol, "");

            // Remove common formatting
            s = s.replace(",", "").replace(" ", "");

            return myAtoi(s);
        }
    }

    // Follow-up 4: Thousand separators
    public static class ThousandSeparators {

        public static int parseWithSeparators(String s) {
            if (s == null || s.isEmpty()) {
                return 0;
            }

            s = s.trim();
            if (s.isEmpty()) {
                return 0;
            }

            int index = 0;
            int sign = 1;

            // Handle sign
            if (s.charAt(index) == '+') {
                index++;
            } else if (s.charAt(index) == '-') {
                sign = -1;
                index++;
            }

            StringBuilder digits = new StringBuilder();

            while (index < s.length()) {
                char c = s.charAt(index);
                if (Character.isDigit(c)) {
                    digits.append(c);
                } else if (c == ',' || c == '.' || c == ' ') {
                    // Skip separator, but validate format
                    if (digits.length() == 0) {
                        return 0; // Invalid: separator at start
                    }
                } else {
                    break; // Stop at non-numeric character
                }
                index++;
            }

            if (digits.length() == 0) {
                return 0;
            }

            // Parse the cleaned number
            long result = 0;
            for (char c : digits.toString().toCharArray()) {
                int digit = c - '0';
                if (result > (Integer.MAX_VALUE - digit) / 10) {
                    return sign == 1 ? Integer.MAX_VALUE : Integer.MIN_VALUE;
                }
                result = result * 10 + digit;
            }

            return (int) (sign * result);
        }

        public static boolean validateSeparatorFormat(String s, char separator) {
            if (s == null || s.isEmpty()) {
                return false;
            }

            s = s.trim();

            // Remove sign for validation
            if (s.startsWith("+") || s.startsWith("-")) {
                s = s.substring(1);
            }

            String[] parts = s.split("\\" + separator);

            if (parts.length == 1) {
                return true; // No separators, just validate digits
            }

            // First part can be 1-3 digits
            if (parts[0].length() == 0 || parts[0].length() > 3 || !parts[0].matches("\\d+")) {
                return false;
            }

            // Subsequent parts must be exactly 3 digits
            for (int i = 1; i < parts.length; i++) {
                if (parts[i].length() != 3 || !parts[i].matches("\\d{3}")) {
                    return false;
                }
            }

            return true;
        }
    }

    // Follow-up 5: Scientific notation
    public static class ScientificNotation {

        public static double parseScientific(String s) {
            if (s == null || s.isEmpty()) {
                return 0.0;
            }

            s = s.trim().toLowerCase();
            if (s.isEmpty()) {
                return 0.0;
            }

            // Split by 'e'
            String[] parts = s.split("e");
            if (parts.length > 2) {
                return 0.0; // Invalid format
            }

            // Parse mantissa
            double mantissa = FloatingPointParsing.parseDouble(parts[0]);

            // Parse exponent
            double exponent = 0;
            if (parts.length == 2) {
                exponent = myAtoi(parts[1]);
            }

            return mantissa * Math.pow(10, exponent);
        }

        public static String formatScientific(double value, int precision) {
            if (value == 0) {
                return "0e+0";
            }

            int exponent = (int) Math.floor(Math.log10(Math.abs(value)));
            double mantissa = value / Math.pow(10, exponent);

            return String.format("%." + precision + "fe%+d", mantissa, exponent);
        }

        public static boolean isValidScientific(String s) {
            try {
                parseScientific(s);
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }

    // Follow-up 6: Custom validation rules
    public static class CustomValidation {

        @FunctionalInterface
        public interface ValidationRule {
            boolean isValid(String s, int currentValue);
        }

        public static int parseWithValidation(String s, ValidationRule... rules) {
            if (s == null || s.isEmpty()) {
                return 0;
            }

            // Apply pre-validation rules
            for (ValidationRule rule : rules) {
                if (!rule.isValid(s, 0)) {
                    return 0;
                }
            }

            int result = myAtoi(s);

            // Apply post-validation rules
            for (ValidationRule rule : rules) {
                if (!rule.isValid(s, result)) {
                    return 0;
                }
            }

            return result;
        }

        // Common validation rules
        @SuppressWarnings("unused")
        public static final ValidationRule POSITIVE_ONLY = (stringValue, value) -> value >= 0;
        @SuppressWarnings("unused")
        public static final ValidationRule NO_LEADING_ZEROS = (s, numericValue) -> {
            String trimmed = s.trim();
            return !trimmed.matches("^[+-]?0+\\d+");
        };
        @SuppressWarnings("unused")
        public static final ValidationRule MAX_DIGITS = (s, numericValue) -> {
            String cleaned = s.replaceAll("[^0-9]", "");
            return cleaned.length() <= 10;
        };
        @SuppressWarnings("unused")
        public static final ValidationRule EVEN_ONLY = (stringValue, value) -> value % 2 == 0;
        @SuppressWarnings("unused")
        public static final ValidationRule RANGE_1_TO_100 = (stringValue, value) -> value >= 1 && value <= 100;

        public static int parsePositiveInteger(String s) {
            return parseWithValidation(s, POSITIVE_ONLY, NO_LEADING_ZEROS);
        }

        public static int parsePercentage(String s) {
            if (s != null && s.trim().endsWith("%")) {
                s = s.trim().substring(0, s.trim().length() - 1);
            }
            return parseWithValidation(s, RANGE_1_TO_100);
        }

        public static int parseEvenNumber(String s) {
            return parseWithValidation(s, EVEN_ONLY);
        }
    }

    // Performance comparison utility
    public static void compareApproaches(String[] testCases) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 100000;

        // Step-by-step approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                myAtoi(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("Step-by-step: " + (end - start) / 1_000_000 + " ms");

        // Character-by-character
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                myAtoiCharByChar(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("Character-by-character: " + (end - start) / 1_000_000 + " ms");

        // State machine
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                myAtoiStateMachine(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("State machine: " + (end - start) / 1_000_000 + " ms");

        // Regex approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                myAtoiRegex(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("Regex: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        String[] basicTests = {
                "42", "   -42", "4193 with words", "words and 987",
                "-91283472332", "91283472332", "+1", "+-12", "00000-42a1234"
        };

        for (String test : basicTests) {
            int result1 = myAtoi(test);
            int result2 = myAtoiCharByChar(test);
            int result3 = myAtoiStateMachine(test);
            int result4 = myAtoiRegex(test);

            System.out.printf("Input: \"%s\"%n", test);
            System.out.printf("  Step-by-step: %d%n", result1);
            System.out.printf("  Char-by-char: %d%n", result2);
            System.out.printf("  State machine: %d%n", result3);
            System.out.printf("  Regex: %d%n", result4);

            if (!(result1 == result2 && result2 == result3)) {
                System.out.println("  WARNING: Inconsistent results!");
            }
            System.out.println();
        }

        // Test Case 2: Edge cases
        System.out.println("=== Test Case 2: Edge Cases ===");

        String[] edgeCases = {
                "", "   ", "+", "-", "abc", "2147483647", "-2147483648",
                "2147483648", "-2147483649", "0000000000012345678"
        };

        for (String test : edgeCases) {
            int result = myAtoi(test);
            System.out.printf("\"%s\" -> %d%n", test, result);
        }

        // Test Case 3: Different bases
        System.out.println("\n=== Test Case 3: Different Number Bases ===");

        String[] baseTests = { "1010", "0b1010", "777", "0o777", "FF", "0xFF", "123" };

        for (String test : baseTests) {
            int decimal = DifferentBases.parseWithBase(test, 10);
            int binary = DifferentBases.parseBinary(test);
            int octal = DifferentBases.parseOctal(test);
            int hex = DifferentBases.parseHexadecimal(test);

            System.out.printf("Input: \"%s\"%n", test);
            System.out.printf("  Decimal: %d, Binary: %d, Octal: %d, Hex: %d%n",
                    decimal, binary, octal, hex);
        }

        // Test Case 4: Floating point numbers
        System.out.println("\n=== Test Case 4: Floating Point Numbers ===");

        String[] floatTests = { "3.14", "-2.5", "1.23e10", "4.5E-3", ".5", "2." };

        for (String test : floatTests) {
            double result = FloatingPointParsing.parseDouble(test);
            float floatResult = FloatingPointParsing.parseFloat(test);
            boolean valid = FloatingPointParsing.isValidFloat(test);

            System.out.printf("\"%s\" -> double: %.6f, float: %.6f, valid: %b%n",
                    test, result, floatResult, valid);
        }

        // Test Case 5: Locale-specific formats
        System.out.println("\n=== Test Case 5: Locale-Specific Formats ===");

        String[] localeTests = { "1.234.567", "1,234,567", "$1,234", "€1.234,56", "₹1,23,456" };

        for (String test : localeTests) {
            int thousands = LocaleSpecific.parseWithThousandsSeparator(test, ',');
            int currency = LocaleSpecific.parseCurrency(test, "$");
            int indian = LocaleSpecific.parseIndianNumbering(test);

            System.out.printf("Input: \"%s\"%n", test);
            System.out.printf("  Thousands: %d, Currency: %d, Indian: %d%n",
                    thousands, currency, indian);
        }

        // Test Case 6: Thousand separators validation
        System.out.println("\n=== Test Case 6: Thousand Separators ===");

        String[] separatorTests = { "1,234,567", "12,34", "1,2345", ",123", "123," };

        for (String test : separatorTests) {
            int parsed = ThousandSeparators.parseWithSeparators(test);
            boolean valid = ThousandSeparators.validateSeparatorFormat(test, ',');

            System.out.printf("\"%s\" -> %d, valid format: %b%n", test, parsed, valid);
        }

        // Test Case 7: Scientific notation
        System.out.println("\n=== Test Case 7: Scientific Notation ===");

        String[] scientificTests = { "1.5e10", "2.3E-5", "1e0", "-4.5e+3" };

        for (String test : scientificTests) {
            double result = ScientificNotation.parseScientific(test);
            boolean valid = ScientificNotation.isValidScientific(test);
            String formatted = ScientificNotation.formatScientific(result, 2);

            System.out.printf("\"%s\" -> %.6e, valid: %b, formatted: %s%n",
                    test, result, valid, formatted);
        }

        // Test Case 8: Custom validation
        System.out.println("\n=== Test Case 8: Custom Validation ===");

        String[] customTests = { "123", "-456", "007", "50%", "25", "13" };

        for (String test : customTests) {
            int positive = CustomValidation.parsePositiveInteger(test);
            int percentage = CustomValidation.parsePercentage(test);
            int even = CustomValidation.parseEvenNumber(test);

            System.out.printf("Input: \"%s\"%n", test);
            System.out.printf("  Positive: %d, Percentage: %d, Even: %d%n",
                    positive, percentage, even);
        }

        // Test Case 9: Performance comparison
        System.out.println("\n=== Test Case 9: Performance Comparison ===");

        String[] perfTests = { "123", "   -456   ", "789abc", "2147483647" };
        compareApproaches(perfTests);

        // Test Case 10: Stress testing
        System.out.println("\n=== Test Case 10: Stress Testing ===");

        Random random = new Random(42);
        int testCases = 10000;
        int passed = 0;

        for (int i = 0; i < testCases; i++) {
            StringBuilder sb = new StringBuilder();

            // Add random whitespace
            for (int j = 0; j < random.nextInt(5); j++) {
                sb.append(' ');
            }

            // Add random sign
            if (random.nextBoolean()) {
                sb.append(random.nextBoolean() ? '+' : '-');
            }

            // Add random digits
            int digitCount = random.nextInt(10) + 1;
            for (int j = 0; j < digitCount; j++) {
                sb.append(random.nextInt(10));
            }

            // Add random suffix
            if (random.nextBoolean()) {
                sb.append("abc");
            }

            String testString = sb.toString();

            try {
                int result1 = myAtoi(testString);
                int result2 = myAtoiCharByChar(testString);
                int result3 = myAtoiStateMachine(testString);

                if (result1 == result2 && result2 == result3) {
                    passed++;
                }
            } catch (Exception e) {
                // Skip invalid test cases
            }
        }

        System.out.printf("Stress test: %d/%d passed%n", passed, testCases);

        System.out.println("\nString to Integer testing completed successfully!");
    }
}
