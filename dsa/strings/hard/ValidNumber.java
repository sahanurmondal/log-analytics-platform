package strings.hard;

import java.util.*;
import java.util.regex.Pattern;

/**
 * LeetCode 65: Valid Number
 * https://leetcode.com/problems/valid-number/
 * 
 * Companies: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg, Uber
 * Frequency: High (Asked in 800+ interviews)
 *
 * Description:
 * A valid number can be split up into these components (in order):
 * 1. A decimal number or an integer.
 * 2. (Optional) An 'e' or 'E', followed by an integer.
 * 
 * A decimal number can be split up into these components (in order):
 * 1. (Optional) A sign character ('+' or '-').
 * 2. One of the following formats:
 * - At least one digit, followed by a dot '.'.
 * - At least one digit, followed by a dot '.', followed by at least one digit.
 * - A dot '.', followed by at least one digit.
 * 
 * An integer can be split up into these components (in order):
 * 1. (Optional) A sign character ('+' or '-').
 * 2. At least one digit.
 * 
 * Follow-up Questions:
 * 1. How would you handle different number formats (hexadecimal, binary,
 * octal)?
 * 2. Can you validate floating-point numbers with different precision
 * requirements?
 * 3. What about scientific notation with different bases?
 * 4. How to handle locale-specific number formats?
 * 5. Can you implement a number parser that extracts the actual value?
 * 6. What about validating complex numbers or fractions?
 */
public class ValidNumber {

    // Approach 1: Finite State Machine (Optimal) - O(n) time, O(1) space
    public static boolean isNumber(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }

        s = s.trim();
        if (s.length() == 0) {
            return false;
        }

        boolean seenDigit = false;
        boolean seenDot = false;
        boolean seenE = false;
        boolean seenSignAfterE = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (Character.isDigit(c)) {
                seenDigit = true;
                seenSignAfterE = false;
            } else if (c == '.') {
                // Dot cannot appear after 'e' or if we've already seen a dot
                if (seenE || seenDot) {
                    return false;
                }
                seenDot = true;
            } else if (c == 'e' || c == 'E') {
                // 'e' must come after a digit and we can't have seen 'e' before
                if (!seenDigit || seenE) {
                    return false;
                }
                seenE = true;
                seenSignAfterE = false;
                seenDigit = false; // Must see digit after 'e'
            } else if (c == '+' || c == '-') {
                // Sign can only appear at beginning or right after 'e'
                if (i != 0 && s.charAt(i - 1) != 'e' && s.charAt(i - 1) != 'E') {
                    return false;
                }
                // If sign appears after 'e', mark it
                if (i > 0 && (s.charAt(i - 1) == 'e' || s.charAt(i - 1) == 'E')) {
                    if (seenSignAfterE) {
                        return false;
                    }
                    seenSignAfterE = true;
                }
            } else {
                // Invalid character
                return false;
            }
        }

        return seenDigit;
    }

    // Approach 2: Regex Pattern Matching - O(n) time, O(1) space
    public static boolean isNumberRegex(String s) {
        if (s == null) {
            return false;
        }

        s = s.trim();

        // Pattern: optional sign + (digits with optional dot + optional digits | dot +
        // digits) + optional exponent
        String pattern = "^[+-]?((\\d+\\.?\\d*)|(\\d*\\.\\d+))([eE][+-]?\\d+)?$";

        return Pattern.matches(pattern, s);
    }

    // Approach 3: Manual parsing with detailed validation - O(n) time, O(1) space
    public static boolean isNumberDetailed(String s) {
        if (s == null || s.length() == 0) {
            return false;
        }

        s = s.trim();
        if (s.length() == 0) {
            return false;
        }

        int index = 0;

        // Check for sign
        if (index < s.length() && (s.charAt(index) == '+' || s.charAt(index) == '-')) {
            index++;
        }

        // Parse the main number part
        boolean validMainPart = parseMainPart(s, index);
        if (!validMainPart) {
            return false;
        }

        // Find the position after main part
        while (index < s.length() &&
                (Character.isDigit(s.charAt(index)) || s.charAt(index) == '.')) {
            index++;
        }

        // Check for exponent
        if (index < s.length() && (s.charAt(index) == 'e' || s.charAt(index) == 'E')) {
            index++;

            // Check for sign after exponent
            if (index < s.length() && (s.charAt(index) == '+' || s.charAt(index) == '-')) {
                index++;
            }

            // Must have digits after exponent
            if (index >= s.length() || !Character.isDigit(s.charAt(index))) {
                return false;
            }

            // Skip remaining digits
            while (index < s.length() && Character.isDigit(s.charAt(index))) {
                index++;
            }
        }

        return index == s.length();
    }

    private static boolean parseMainPart(String s, int start) {
        int index = start;
        boolean hasDigit = false;
        boolean hasDot = false;

        while (index < s.length()) {
            char c = s.charAt(index);

            if (Character.isDigit(c)) {
                hasDigit = true;
                index++;
            } else if (c == '.' && !hasDot) {
                hasDot = true;
                index++;
            } else {
                break;
            }
        }

        return hasDigit;
    }

    // Approach 4: State machine with enum states - O(n) time, O(1) space
    public static boolean isNumberStateMachine(String s) {
        if (s == null) {
            return false;
        }

        s = s.trim();
        if (s.length() == 0) {
            return false;
        }

        State state = State.START;

        for (char c : s.toCharArray()) {
            switch (state) {
                case START:
                    if (c == '+' || c == '-') {
                        state = State.SIGN;
                    } else if (Character.isDigit(c)) {
                        state = State.DIGIT;
                    } else if (c == '.') {
                        state = State.DOT_WITHOUT_DIGIT;
                    } else {
                        return false;
                    }
                    break;

                case SIGN:
                    if (Character.isDigit(c)) {
                        state = State.DIGIT;
                    } else if (c == '.') {
                        state = State.DOT_WITHOUT_DIGIT;
                    } else {
                        return false;
                    }
                    break;

                case DIGIT:
                    if (Character.isDigit(c)) {
                        state = State.DIGIT;
                    } else if (c == '.') {
                        state = State.DOT_WITH_DIGIT;
                    } else if (c == 'e' || c == 'E') {
                        state = State.EXP;
                    } else {
                        return false;
                    }
                    break;

                case DOT_WITHOUT_DIGIT:
                    if (Character.isDigit(c)) {
                        state = State.DOT_WITH_DIGIT;
                    } else {
                        return false;
                    }
                    break;

                case DOT_WITH_DIGIT:
                    if (Character.isDigit(c)) {
                        state = State.DOT_WITH_DIGIT;
                    } else if (c == 'e' || c == 'E') {
                        state = State.EXP;
                    } else {
                        return false;
                    }
                    break;

                case EXP:
                    if (c == '+' || c == '-') {
                        state = State.EXP_SIGN;
                    } else if (Character.isDigit(c)) {
                        state = State.EXP_DIGIT;
                    } else {
                        return false;
                    }
                    break;

                case EXP_SIGN:
                    if (Character.isDigit(c)) {
                        state = State.EXP_DIGIT;
                    } else {
                        return false;
                    }
                    break;

                case EXP_DIGIT:
                    if (Character.isDigit(c)) {
                        state = State.EXP_DIGIT;
                    } else {
                        return false;
                    }
                    break;

                default:
                    return false;
            }
        }

        return state == State.DIGIT || state == State.DOT_WITH_DIGIT || state == State.EXP_DIGIT;
    }

    private enum State {
        START, SIGN, DIGIT, DOT_WITHOUT_DIGIT, DOT_WITH_DIGIT, EXP, EXP_SIGN, EXP_DIGIT
    }

    // Approach 5: Recursive descent parser - O(n) time, O(n) space
    public static boolean isNumberRecursive(String s) {
        if (s == null) {
            return false;
        }

        s = s.trim();
        if (s.length() == 0) {
            return false;
        }

        NumberParser parser = new NumberParser(s);
        return parser.parseNumber() && parser.isAtEnd();
    }

    private static class NumberParser {
        private final String input;
        private int position;

        public NumberParser(String input) {
            this.input = input;
            this.position = 0;
        }

        public boolean parseNumber() {
            return parseDecimalOrInteger() &&
                    (position >= input.length() || parseExponent());
        }

        private boolean parseDecimalOrInteger() {
            // Optional sign
            if (position < input.length() && (peek() == '+' || peek() == '-')) {
                advance();
            }

            return parseDecimal() || parseInteger();
        }

        private boolean parseDecimal() {
            int start = position;

            // Pattern 1: digits.digits
            if (parseDigits() && position < input.length() && peek() == '.') {
                advance();
                parseDigits(); // Optional digits after dot
                return true;
            }

            // Reset and try pattern 2: .digits
            position = start;
            if (position < input.length() && peek() == '.') {
                advance();
                return parseDigits();
            }

            // Reset and try pattern 3: digits.
            position = start;
            if (parseDigits() && position < input.length() && peek() == '.') {
                advance();
                return true;
            }

            position = start;
            return false;
        }

        private boolean parseInteger() {
            return parseDigits();
        }

        private boolean parseDigits() {
            int count = 0;
            while (position < input.length() && Character.isDigit(peek())) {
                advance();
                count++;
            }
            return count > 0;
        }

        private boolean parseExponent() {
            if (position >= input.length() || (peek() != 'e' && peek() != 'E')) {
                return false;
            }

            advance(); // consume 'e' or 'E'

            // Optional sign
            if (position < input.length() && (peek() == '+' || peek() == '-')) {
                advance();
            }

            return parseDigits();
        }

        private char peek() {
            return position < input.length() ? input.charAt(position) : '\0';
        }

        private void advance() {
            if (position < input.length()) {
                position++;
            }
        }

        public boolean isAtEnd() {
            return position >= input.length();
        }
    }

    // Follow-up 1: Different number formats
    public static class DifferentFormats {

        public static boolean isHexadecimal(String s) {
            if (s == null || s.length() == 0) {
                return false;
            }

            s = s.trim().toLowerCase();

            // Check for 0x prefix
            if (s.startsWith("0x")) {
                s = s.substring(2);
            }

            if (s.length() == 0) {
                return false;
            }

            for (char c : s.toCharArray()) {
                if (!((c >= '0' && c <= '9') || (c >= 'a' && c <= 'f'))) {
                    return false;
                }
            }

            return true;
        }

        public static boolean isBinary(String s) {
            if (s == null || s.length() == 0) {
                return false;
            }

            s = s.trim();

            // Check for 0b prefix
            if (s.startsWith("0b") || s.startsWith("0B")) {
                s = s.substring(2);
            }

            if (s.length() == 0) {
                return false;
            }

            for (char c : s.toCharArray()) {
                if (c != '0' && c != '1') {
                    return false;
                }
            }

            return true;
        }

        public static boolean isOctal(String s) {
            if (s == null || s.length() == 0) {
                return false;
            }

            s = s.trim();

            // Check for 0o prefix or leading 0
            if (s.startsWith("0o") || s.startsWith("0O")) {
                s = s.substring(2);
            } else if (s.startsWith("0") && s.length() > 1) {
                s = s.substring(1);
            }

            if (s.length() == 0) {
                return false;
            }

            for (char c : s.toCharArray()) {
                if (c < '0' || c > '7') {
                    return false;
                }
            }

            return true;
        }

        public static boolean isNumberAnyBase(String s, int base) {
            if (s == null || s.length() == 0 || base < 2 || base > 36) {
                return false;
            }

            s = s.trim();
            if (s.length() == 0) {
                return false;
            }

            // Handle sign
            int start = 0;
            if (s.charAt(0) == '+' || s.charAt(0) == '-') {
                start = 1;
            }

            if (start >= s.length()) {
                return false;
            }

            for (int i = start; i < s.length(); i++) {
                char c = Character.toLowerCase(s.charAt(i));
                int digit;

                if (c >= '0' && c <= '9') {
                    digit = c - '0';
                } else if (c >= 'a' && c <= 'z') {
                    digit = c - 'a' + 10;
                } else {
                    return false;
                }

                if (digit >= base) {
                    return false;
                }
            }

            return true;
        }
    }

    // Follow-up 2: Floating-point with precision requirements
    public static class PrecisionValidation {

        public static boolean isValidFloat(String s, int maxIntegerDigits, int maxDecimalDigits) {
            if (!isNumber(s)) {
                return false;
            }

            s = s.trim();

            // Remove sign
            if (s.startsWith("+") || s.startsWith("-")) {
                s = s.substring(1);
            }

            // Remove exponent part for precision check
            int eIndex = s.toLowerCase().indexOf('e');
            if (eIndex != -1) {
                s = s.substring(0, eIndex);
            }

            String[] parts = s.split("\\.");

            // Check integer part
            if (parts.length >= 1 && !parts[0].isEmpty()) {
                if (parts[0].length() > maxIntegerDigits) {
                    return false;
                }
            }

            // Check decimal part
            if (parts.length == 2 && !parts[1].isEmpty()) {
                if (parts[1].length() > maxDecimalDigits) {
                    return false;
                }
            }

            return true;
        }

        public static boolean isValidDoubleRange(String s, double min, double max) {
            if (!isNumber(s)) {
                return false;
            }

            try {
                double value = Double.parseDouble(s.trim());
                return value >= min && value <= max && !Double.isInfinite(value);
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public static boolean hasRequiredPrecision(String s, int requiredDecimalPlaces) {
            if (!isNumber(s)) {
                return false;
            }

            s = s.trim();

            // Remove exponent
            int eIndex = s.toLowerCase().indexOf('e');
            if (eIndex != -1) {
                s = s.substring(0, eIndex);
            }

            int dotIndex = s.indexOf('.');
            if (dotIndex == -1) {
                return requiredDecimalPlaces == 0;
            }

            String decimalPart = s.substring(dotIndex + 1);
            return decimalPart.length() == requiredDecimalPlaces;
        }
    }

    // Follow-up 3: Scientific notation with different bases
    public static class ScientificNotation {

        public static boolean isScientificNotation(String s) {
            if (!isNumber(s)) {
                return false;
            }

            s = s.trim().toLowerCase();
            return s.contains("e");
        }

        public static boolean isValidScientificBase(String s, int base) {
            if (s == null || s.length() == 0) {
                return false;
            }

            s = s.trim().toLowerCase();

            // Find the exponent separator
            int eIndex = -1;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == 'e' && base <= 14) { // 'e' is not a valid digit in base <= 14
                    eIndex = i;
                    break;
                } else if (c == 'p' && base <= 25) { // Use 'p' for bases where 'e' is a digit
                    eIndex = i;
                    break;
                }
            }

            if (eIndex == -1) {
                return DifferentFormats.isNumberAnyBase(s, base);
            }

            String mantissa = s.substring(0, eIndex);
            String exponent = s.substring(eIndex + 1);

            // Mantissa can be a decimal number in the given base
            return isValidMantissaInBase(mantissa, base) &&
                    DifferentFormats.isNumberAnyBase(exponent, 10); // Exponent is always decimal
        }

        private static boolean isValidMantissaInBase(String s, int base) {
            if (s == null || s.length() == 0) {
                return false;
            }

            // Handle sign
            int start = 0;
            if (s.charAt(0) == '+' || s.charAt(0) == '-') {
                start = 1;
            }

            boolean hasDot = false;
            boolean hasDigit = false;

            for (int i = start; i < s.length(); i++) {
                char c = Character.toLowerCase(s.charAt(i));

                if (c == '.') {
                    if (hasDot) {
                        return false;
                    }
                    hasDot = true;
                } else {
                    int digit;
                    if (c >= '0' && c <= '9') {
                        digit = c - '0';
                    } else if (c >= 'a' && c <= 'z') {
                        digit = c - 'a' + 10;
                    } else {
                        return false;
                    }

                    if (digit >= base) {
                        return false;
                    }

                    hasDigit = true;
                }
            }

            return hasDigit;
        }
    }

    // Follow-up 4: Locale-specific number formats
    public static class LocaleSpecific {

        public static boolean isValidEuropeanNumber(String s) {
            if (s == null) {
                return false;
            }

            // European format: comma as decimal separator, space/dot as thousands separator
            s = s.trim();

            // Remove thousands separators
            s = s.replace(" ", "").replace(".", "");

            // Replace comma with dot for standard validation
            if (s.contains(",")) {
                String[] parts = s.split(",");
                if (parts.length > 2) {
                    return false;
                }
                s = parts[0] + (parts.length == 2 ? "." + parts[1] : "");
            }

            return isNumber(s);
        }

        public static boolean isValidIndianNumber(String s) {
            if (s == null) {
                return false;
            }

            // Indian format: may have lakhs/crores separators
            s = s.trim();

            // Remove Indian number separators
            s = s.replace(",", "");

            return isNumber(s);
        }

        public static boolean isValidCurrencyNumber(String s, String currencySymbol) {
            if (s == null) {
                return false;
            }

            s = s.trim();

            // Remove currency symbol from beginning or end
            if (s.startsWith(currencySymbol)) {
                s = s.substring(currencySymbol.length()).trim();
            } else if (s.endsWith(currencySymbol)) {
                s = s.substring(0, s.length() - currencySymbol.length()).trim();
            }

            // Remove thousands separators
            s = s.replace(",", "");

            return isNumber(s);
        }
    }

    // Follow-up 5: Number parser that extracts actual value
    public static class NumberValueParser {

        public static class ParseResult {
            public final boolean isValid;
            public final double value;
            public final String error;

            public ParseResult(boolean isValid, double value, String error) {
                this.isValid = isValid;
                this.value = value;
                this.error = error;
            }
        }

        public static ParseResult parseNumber(String s) {
            if (s == null) {
                return new ParseResult(false, 0, "Input is null");
            }

            s = s.trim();
            if (s.length() == 0) {
                return new ParseResult(false, 0, "Input is empty");
            }

            if (!isNumber(s)) {
                return new ParseResult(false, 0, "Invalid number format");
            }

            try {
                double value = Double.parseDouble(s);
                if (Double.isInfinite(value)) {
                    return new ParseResult(false, 0, "Number is too large");
                }
                if (Double.isNaN(value)) {
                    return new ParseResult(false, 0, "Number is NaN");
                }
                return new ParseResult(true, value, null);
            } catch (NumberFormatException e) {
                return new ParseResult(false, 0, "Failed to parse: " + e.getMessage());
            }
        }

        public static ParseResult parseNumberInBase(String s, int base) {
            if (s == null) {
                return new ParseResult(false, 0, "Input is null");
            }

            s = s.trim();
            if (!DifferentFormats.isNumberAnyBase(s, base)) {
                return new ParseResult(false, 0, "Invalid number in base " + base);
            }

            try {
                // Handle sign
                boolean negative = false;
                if (s.startsWith("-")) {
                    negative = true;
                    s = s.substring(1);
                } else if (s.startsWith("+")) {
                    s = s.substring(1);
                }

                long value = Long.parseLong(s, base);
                if (negative) {
                    value = -value;
                }

                return new ParseResult(true, value, null);
            } catch (NumberFormatException e) {
                return new ParseResult(false, 0, "Failed to parse in base " + base + ": " + e.getMessage());
            }
        }
    }

    // Follow-up 6: Complex numbers and fractions
    public static class ComplexAndFractions {

        public static boolean isValidComplexNumber(String s) {
            if (s == null) {
                return false;
            }

            s = s.trim();

            // Format: a+bi or a-bi where a and b are real numbers
            // Also handle: bi, a, +bi, -bi

            if (s.equals("i") || s.equals("-i") || s.equals("+i")) {
                return true;
            }

            // Check for 'i' at the end
            if (s.endsWith("i")) {
                s = s.substring(0, s.length() - 1);

                if (s.length() == 0 || s.equals("+") || s.equals("-")) {
                    return true;
                }

                // Look for + or - in the middle (not at the beginning)
                for (int i = 1; i < s.length(); i++) {
                    char c = s.charAt(i);
                    if (c == '+' || c == '-') {
                        String realPart = s.substring(0, i);
                        String imagPart = s.substring(i + 1);

                        if (imagPart.length() == 0) {
                            imagPart = "1";
                        }

                        return isNumber(realPart) && isNumber(imagPart);
                    }
                }

                // Only imaginary part
                return isNumber(s);
            } else {
                // Only real part
                return isNumber(s);
            }
        }

        public static boolean isValidFraction(String s) {
            if (s == null) {
                return false;
            }

            s = s.trim();

            // Check for mixed number: whole fraction (e.g., "1 1/2")
            String[] spaceParts = s.split("\\s+");
            if (spaceParts.length == 2) {
                return isNumber(spaceParts[0]) && isValidProperFraction(spaceParts[1]);
            } else if (spaceParts.length == 1) {
                return isValidProperFraction(spaceParts[0]);
            }

            return false;
        }

        private static boolean isValidProperFraction(String s) {
            if (s == null || !s.contains("/")) {
                return false;
            }

            String[] parts = s.split("/");
            if (parts.length != 2) {
                return false;
            }

            String numerator = parts[0].trim();
            String denominator = parts[1].trim();

            if (!isNumber(numerator) || !isNumber(denominator)) {
                return false;
            }

            try {
                int denom = Integer.parseInt(denominator);
                return denom != 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        public static boolean isValidPercentage(String s) {
            if (s == null) {
                return false;
            }

            s = s.trim();

            if (!s.endsWith("%")) {
                return false;
            }

            String numberPart = s.substring(0, s.length() - 1).trim();
            return isNumber(numberPart);
        }
    }

    // Utility methods
    public static void compareApproaches(String[] testCases) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 100000;

        // Finite State Machine
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                isNumber(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("Finite State Machine: " + (end - start) / 1_000_000 + " ms");

        // Regex
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                isNumberRegex(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("Regex: " + (end - start) / 1_000_000 + " ms");

        // State Machine Enum
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                isNumberStateMachine(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("Enum State Machine: " + (end - start) / 1_000_000 + " ms");

        // Recursive
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            for (String testCase : testCases) {
                isNumberRecursive(testCase);
            }
        }
        end = System.nanoTime();
        System.out.println("Recursive: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic valid numbers
        System.out.println("=== Test Case 1: Basic Valid Numbers ===");

        String[] validNumbers = {
                "0", "1", "-1", "+1", "123", "-123", "+123",
                "0.1", "1.0", "-1.0", "+1.0", "123.456", "-123.456",
                ".1", "-.1", "+.1", "1.", "-1.", "+1.",
                "1e5", "1E5", "1e-5", "1E-5", "-1e5", "+1e5",
                "1.5e10", "-1.5e-10", "+1.5E+10",
                "0.5", "3.14159", "2.71828"
        };

        System.out.println("Testing valid numbers:");
        for (String num : validNumbers) {
            boolean fsm = isNumber(num);
            boolean regex = isNumberRegex(num);
            boolean stateMachine = isNumberStateMachine(num);
            boolean recursive = isNumberRecursive(num);

            System.out.printf("%-10s: FSM=%b, Regex=%b, State=%b, Recursive=%b%n",
                    "\"" + num + "\"", fsm, regex, stateMachine, recursive);

            if (!(fsm && regex && stateMachine && recursive)) {
                System.out.println("  WARNING: Inconsistent results!");
            }
        }

        // Test Case 2: Invalid numbers
        System.out.println("\n=== Test Case 2: Invalid Numbers ===");

        String[] invalidNumbers = {
                "", " ", "abc", "1a", "1..2", "1.2.3", "1e", "e1", "1e1e1",
                "++1", "--1", "+-1", "-+1", "1+", "1-", ".e1", "e", "E",
                "1.2e", "1.2e+", "1.2e-", "inf", "nan", "1 2", "1,2"
        };

        System.out.println("Testing invalid numbers:");
        for (String num : invalidNumbers) {
            boolean fsm = isNumber(num);
            boolean regex = isNumberRegex(num);
            boolean stateMachine = isNumberStateMachine(num);
            boolean recursive = isNumberRecursive(num);

            System.out.printf("%-10s: FSM=%b, Regex=%b, State=%b, Recursive=%b%n",
                    "\"" + num + "\"", fsm, regex, stateMachine, recursive);

            if (fsm || regex || stateMachine || recursive) {
                System.out.println("  WARNING: Should be invalid!");
            }
        }

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        String[] edgeCases = {
                "   123   ", "  +123  ", "  -123  ", "  1.23  ",
                "  1e5  ", "  .5  ", "  5.  ", "0000123",
                "123.000", ".000", "000.", "1e000", "1e+000"
        };

        System.out.println("Testing edge cases:");
        for (String num : edgeCases) {
            boolean result = isNumber(num);
            System.out.printf("%-15s: %b%n", "\"" + num + "\"", result);
        }

        // Test Case 4: Different formats
        System.out.println("\n=== Test Case 4: Different Number Formats ===");

        System.out.println("Hexadecimal validation:");
        String[] hexNumbers = { "0x1A2B", "1a2b", "0xGHIJ", "FF", "0x" };
        for (String hex : hexNumbers) {
            System.out.printf("%-10s: %b%n", hex, DifferentFormats.isHexadecimal(hex));
        }

        System.out.println("\nBinary validation:");
        String[] binNumbers = { "0b1010", "1010", "0b1012", "101", "0b" };
        for (String bin : binNumbers) {
            System.out.printf("%-10s: %b%n", bin, DifferentFormats.isBinary(bin));
        }

        System.out.println("\nOctal validation:");
        String[] octNumbers = { "0o777", "0777", "0o999", "123", "0o" };
        for (String oct : octNumbers) {
            System.out.printf("%-10s: %b%n", oct, DifferentFormats.isOctal(oct));
        }

        // Test Case 5: Precision validation
        System.out.println("\n=== Test Case 5: Precision Validation ===");

        String[] precisionTests = { "123.45", "1234.5", "123.456789", "123", ".123" };

        for (String test : precisionTests) {
            boolean valid3_2 = PrecisionValidation.isValidFloat(test, 3, 2);
            boolean valid5_3 = PrecisionValidation.isValidFloat(test, 5, 3);
            boolean exact2 = PrecisionValidation.hasRequiredPrecision(test, 2);

            System.out.printf("%-12s: 3,2=%b, 5,3=%b, exact2=%b%n", test, valid3_2, valid5_3, exact2);
        }

        // Test Case 6: Scientific notation
        System.out.println("\n=== Test Case 6: Scientific Notation ===");

        String[] scientificTests = { "1.5e10", "2.3E-5", "1.23e+10", "456", "abc" };

        for (String test : scientificTests) {
            boolean isScientific = ScientificNotation.isScientificNotation(test);
            boolean validBase10 = ScientificNotation.isValidScientificBase(test, 10);
            boolean validBase16 = ScientificNotation.isValidScientificBase(test, 16);

            System.out.printf("%-12s: scientific=%b, base10=%b, base16=%b%n",
                    test, isScientific, validBase10, validBase16);
        }

        // Test Case 7: Locale-specific
        System.out.println("\n=== Test Case 7: Locale-Specific Numbers ===");

        String[] europeanNumbers = { "123,45", "1.234,56", "1 234,56" };
        for (String num : europeanNumbers) {
            System.out.printf("European %-12s: %b%n", num, LocaleSpecific.isValidEuropeanNumber(num));
        }

        String[] currencyNumbers = { "$123.45", "123.45$", "€1,234.56", "¥1000" };
        for (String num : currencyNumbers) {
            String symbol = num.matches(".*[€¥].*") ? (num.contains("€") ? "€" : "¥") : "$";
            System.out.printf("Currency %-12s: %b%n", num, LocaleSpecific.isValidCurrencyNumber(num, symbol));
        }

        // Test Case 8: Number parsing
        System.out.println("\n=== Test Case 8: Number Parsing ===");

        String[] parseTests = { "123.45", "1.5e10", "invalid", "", null, "999999999999999999999" };

        for (String test : parseTests) {
            NumberValueParser.ParseResult result = NumberValueParser.parseNumber(test);
            if (result.isValid) {
                System.out.printf("%-20s: %.6f%n", String.valueOf(test), result.value);
            } else {
                System.out.printf("%-20s: ERROR - %s%n", String.valueOf(test), result.error);
            }
        }

        // Test Case 9: Complex numbers and fractions
        System.out.println("\n=== Test Case 9: Complex Numbers and Fractions ===");

        String[] complexTests = { "3+4i", "3-4i", "5i", "-2i", "i", "3", "invalid" };
        for (String test : complexTests) {
            System.out.printf("Complex %-10s: %b%n", test, ComplexAndFractions.isValidComplexNumber(test));
        }

        String[] fractionTests = { "1/2", "3 1/4", "22/7", "1/0", "invalid", "50%" };
        for (String test : fractionTests) {
            boolean fraction = ComplexAndFractions.isValidFraction(test);
            boolean percentage = ComplexAndFractions.isValidPercentage(test);
            System.out.printf("%-10s: fraction=%b, percentage=%b%n", test, fraction, percentage);
        }

        // Test Case 10: Stress test
        System.out.println("\n=== Test Case 10: Stress Test ===");

        Random random = new Random(42);
        int testCases = 10000;
        int passed = 0;

        for (int i = 0; i < testCases; i++) {
            String testNumber = generateRandomNumber(random);

            boolean fsm = isNumber(testNumber);
            boolean regex = isNumberRegex(testNumber);
            boolean stateMachine = isNumberStateMachine(testNumber);
            boolean recursive = isNumberRecursive(testNumber);

            if (fsm == regex && regex == stateMachine && stateMachine == recursive) {
                passed++;
            }
        }

        System.out.println("Stress test: " + passed + "/" + testCases + " consistent results");

        // Performance comparison
        String[] perfTestCases = { "123.45", "1.5e10", "invalid", ".123", "123.", "1e-5" };
        compareApproaches(perfTestCases);

        System.out.println("\nValid Number testing completed successfully!");
    }

    private static String generateRandomNumber(Random random) {
        StringBuilder sb = new StringBuilder();

        // Random sign
        if (random.nextBoolean()) {
            sb.append(random.nextBoolean() ? "+" : "-");
        }

        // Random digits before decimal
        if (random.nextBoolean()) {
            int digits = random.nextInt(5) + 1;
            for (int i = 0; i < digits; i++) {
                sb.append(random.nextInt(10));
            }
        }

        // Random decimal point and digits
        if (random.nextBoolean()) {
            sb.append(".");
            if (random.nextBoolean()) {
                int digits = random.nextInt(5) + 1;
                for (int i = 0; i < digits; i++) {
                    sb.append(random.nextInt(10));
                }
            }
        }

        // Random exponent
        if (random.nextBoolean()) {
            sb.append(random.nextBoolean() ? "e" : "E");
            if (random.nextBoolean()) {
                sb.append(random.nextBoolean() ? "+" : "-");
            }
            int expDigits = random.nextInt(3) + 1;
            for (int i = 0; i < expDigits; i++) {
                sb.append(random.nextInt(10));
            }
        }

        // Sometimes add invalid characters
        if (random.nextInt(10) == 0) {
            sb.append((char) ('a' + random.nextInt(26)));
        }

        return sb.toString();
    }
}
