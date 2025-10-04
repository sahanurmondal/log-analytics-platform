package twopointers.easy;

import java.util.*;

/**
 * LeetCode 917: Reverse Only Letters
 * https://leetcode.com/problems/reverse-only-letters/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple, Uber
 * Frequency: High (Asked in 1200+ interviews)
 *
 * Description:
 * Given a string s, reverse the string according to the following rules:
 * - All the characters that are not English letters remain in the same
 * position.
 * - All the English letters (lowercase or uppercase) should be reversed.
 * 
 * Return s after reversing it.
 * 
 * Constraints:
 * - 1 <= s.length <= 100
 * - s consists of characters with ASCII values in the range [33, 122].
 * - s does not contain '\"' or '\\'.
 * 
 * Follow-up Questions:
 * 1. What if you need to reverse only vowels instead of all letters?
 * 2. How would you handle Unicode letters?
 * 3. Can you reverse letters while maintaining case sensitivity?
 * 4. What about reversing only digits or alphanumeric characters?
 * 5. How to reverse letters in-place with O(1) extra space?
 * 6. What about reversing letters in words separately?
 */
public class ReverseOnlyLetters {

    // Approach 1: Two pointers - O(n) time, O(n) space (for char array)
    public static String reverseOnlyLetters(String s) {
        char[] chars = s.toCharArray();
        int left = 0;
        int right = chars.length - 1;

        while (left < right) {
            // Move left pointer to find a letter
            while (left < right && !Character.isLetter(chars[left])) {
                left++;
            }

            // Move right pointer to find a letter
            while (left < right && !Character.isLetter(chars[right])) {
                right--;
            }

            // Swap letters
            if (left < right) {
                char temp = chars[left];
                chars[left] = chars[right];
                chars[right] = temp;
                left++;
                right--;
            }
        }

        return new String(chars);
    }

    // Approach 2: Stack-based approach - O(n) time, O(n) space
    public static String reverseOnlyLettersStack(String s) {
        // Push all letters to stack
        Stack<Character> letterStack = new Stack<>();

        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                letterStack.push(c);
            }
        }

        // Build result by popping letters from stack
        StringBuilder result = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(letterStack.pop());
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // Approach 3: Two-pass approach - O(n) time, O(n) space
    public static String reverseOnlyLettersTwoPass(String s) {
        // First pass: extract all letters
        List<Character> letters = new ArrayList<>();

        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                letters.add(c);
            }
        }

        // Reverse the letters list
        Collections.reverse(letters);

        // Second pass: build result
        StringBuilder result = new StringBuilder();
        int letterIndex = 0;

        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(letters.get(letterIndex++));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // Approach 4: Recursive approach - O(n) time, O(n) space
    public static String reverseOnlyLettersRecursive(String s) {
        char[] chars = s.toCharArray();
        reverseLettersHelper(chars, 0, chars.length - 1);
        return new String(chars);
    }

    private static void reverseLettersHelper(char[] chars, int left, int right) {
        if (left >= right) {
            return;
        }

        // Find next letter from left
        while (left < right && !Character.isLetter(chars[left])) {
            left++;
        }

        // Find next letter from right
        while (left < right && !Character.isLetter(chars[right])) {
            right--;
        }

        // Swap and recurse
        if (left < right) {
            char temp = chars[left];
            chars[left] = chars[right];
            chars[right] = temp;
            reverseLettersHelper(chars, left + 1, right - 1);
        }
    }

    // Approach 5: Regex-based approach - O(n) time, O(n) space
    public static String reverseOnlyLettersRegex(String s) {
        // Extract all letters
        String letters = s.replaceAll("[^a-zA-Z]", "");

        // Reverse the letters
        String reversedLetters = new StringBuilder(letters).reverse().toString();

        // Replace letters in original string
        StringBuilder result = new StringBuilder();
        int letterIndex = 0;

        for (char c : s.toCharArray()) {
            if (Character.isLetter(c)) {
                result.append(reversedLetters.charAt(letterIndex++));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    // Follow-up 1: Reverse only vowels
    public static class VowelReversal {

        private static final Set<Character> VOWELS = Set.of('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');

        public static String reverseVowels(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !VOWELS.contains(chars[left])) {
                    left++;
                }

                while (left < right && !VOWELS.contains(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }

        public static String reverseVowelsStack(String s) {
            Stack<Character> vowelStack = new Stack<>();

            for (char c : s.toCharArray()) {
                if (VOWELS.contains(c)) {
                    vowelStack.push(c);
                }
            }

            StringBuilder result = new StringBuilder();

            for (char c : s.toCharArray()) {
                if (VOWELS.contains(c)) {
                    result.append(vowelStack.pop());
                } else {
                    result.append(c);
                }
            }

            return result.toString();
        }

        public static String reverseConsonants(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && (!Character.isLetter(chars[left]) || VOWELS.contains(chars[left]))) {
                    left++;
                }

                while (left < right && (!Character.isLetter(chars[right]) || VOWELS.contains(chars[right]))) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }
    }

    // Follow-up 2: Unicode support
    public static class UnicodeSupport {

        public static String reverseOnlyLettersUnicode(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !Character.isLetter(chars[left])) {
                    left++;
                }

                while (left < right && !Character.isLetter(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }

        public static String reverseOnlyASCIILetters(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !isASCIILetter(chars[left])) {
                    left++;
                }

                while (left < right && !isASCIILetter(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }

        private static boolean isASCIILetter(char c) {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        }

        public static String reverseOnlyAlphabetic(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !Character.isAlphabetic(chars[left])) {
                    left++;
                }

                while (left < right && !Character.isAlphabetic(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }
    }

    // Follow-up 3: Case-sensitive reversal
    public static class CaseSensitiveReversal {

        public static String reverseOnlyLowercase(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !Character.isLowerCase(chars[left])) {
                    left++;
                }

                while (left < right && !Character.isLowerCase(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }

        public static String reverseOnlyUppercase(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !Character.isUpperCase(chars[left])) {
                    left++;
                }

                while (left < right && !Character.isUpperCase(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }

        public static String reverseMaintainingCase(String s) {
            // Extract letters while preserving their positions
            List<Character> lowerLetters = new ArrayList<>();
            List<Character> upperLetters = new ArrayList<>();

            for (char c : s.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    lowerLetters.add(c);
                } else if (Character.isUpperCase(c)) {
                    upperLetters.add(c);
                }
            }

            // Reverse both lists
            Collections.reverse(lowerLetters);
            Collections.reverse(upperLetters);

            // Build result
            StringBuilder result = new StringBuilder();
            int lowerIndex = 0;
            int upperIndex = 0;

            for (char c : s.toCharArray()) {
                if (Character.isLowerCase(c)) {
                    result.append(lowerLetters.get(lowerIndex++));
                } else if (Character.isUpperCase(c)) {
                    result.append(upperLetters.get(upperIndex++));
                } else {
                    result.append(c);
                }
            }

            return result.toString();
        }
    }

    // Follow-up 4: Reverse digits and alphanumeric
    public static class AlphanumericReversal {

        public static String reverseOnlyDigits(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !Character.isDigit(chars[left])) {
                    left++;
                }

                while (left < right && !Character.isDigit(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }

        public static String reverseAlphanumeric(String s) {
            char[] chars = s.toCharArray();
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !Character.isLetterOrDigit(chars[left])) {
                    left++;
                }

                while (left < right && !Character.isLetterOrDigit(chars[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = chars[left];
                    chars[left] = chars[right];
                    chars[right] = temp;
                    left++;
                    right--;
                }
            }

            return new String(chars);
        }

        public static String reverseLettersAndDigitsSeparately(String s) {
            // Extract letters and digits separately
            List<Character> letters = new ArrayList<>();
            List<Character> digits = new ArrayList<>();

            for (char c : s.toCharArray()) {
                if (Character.isLetter(c)) {
                    letters.add(c);
                } else if (Character.isDigit(c)) {
                    digits.add(c);
                }
            }

            // Reverse both lists
            Collections.reverse(letters);
            Collections.reverse(digits);

            // Build result
            StringBuilder result = new StringBuilder();
            int letterIndex = 0;
            int digitIndex = 0;

            for (char c : s.toCharArray()) {
                if (Character.isLetter(c)) {
                    result.append(letters.get(letterIndex++));
                } else if (Character.isDigit(c)) {
                    result.append(digits.get(digitIndex++));
                } else {
                    result.append(c);
                }
            }

            return result.toString();
        }
    }

    // Follow-up 5: In-place with O(1) space (excluding input)
    public static class InPlaceReversal {

        // Note: String is immutable in Java, so we use char array
        public static char[] reverseOnlyLettersInPlace(char[] chars) {
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                while (left < right && !Character.isLetter(chars[left])) {
                    left++;
                }

                while (left < right && !Character.isLetter(chars[right])) {
                    right--;
                }

                if (left < right) {
                    // XOR swap to avoid extra variable
                    chars[left] ^= chars[right];
                    chars[right] ^= chars[left];
                    chars[left] ^= chars[right];
                    left++;
                    right--;
                }
            }

            return chars;
        }

        public static char[] reverseOnlyLettersInPlaceIterative(char[] chars) {
            int left = 0;
            int right = chars.length - 1;

            while (left < right) {
                // Skip non-letters from left
                if (!Character.isLetter(chars[left])) {
                    left++;
                    continue;
                }

                // Skip non-letters from right
                if (!Character.isLetter(chars[right])) {
                    right--;
                    continue;
                }

                // Swap letters
                char temp = chars[left];
                chars[left] = chars[right];
                chars[right] = temp;

                left++;
                right--;
            }

            return chars;
        }
    }

    // Follow-up 6: Reverse letters in words separately
    public static class WordWiseReversal {

        public static String reverseLettersInWords(String s) {
            String[] words = s.split(" ");
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < words.length; i++) {
                if (i > 0) {
                    result.append(" ");
                }
                result.append(reverseOnlyLetters(words[i]));
            }

            return result.toString();
        }

        public static String reverseLettersInWordsPreserveSpaces(String s) {
            StringBuilder result = new StringBuilder();
            StringBuilder currentWord = new StringBuilder();

            for (char c : s.toCharArray()) {
                if (c == ' ') {
                    if (currentWord.length() > 0) {
                        result.append(reverseOnlyLetters(currentWord.toString()));
                        currentWord.setLength(0);
                    }
                    result.append(c);
                } else {
                    currentWord.append(c);
                }
            }

            // Handle last word
            if (currentWord.length() > 0) {
                result.append(reverseOnlyLetters(currentWord.toString()));
            }

            return result.toString();
        }

        public static String reverseWordsAndLetters(String s) {
            // First reverse the entire string
            String reversed = new StringBuilder(s).reverse().toString();

            // Then reverse letters in each word
            return reverseLettersInWords(reversed);
        }

        public static String reverseOnlyLettersIgnoringWordBoundaries(String s) {
            // Extract all letters
            List<Character> letters = new ArrayList<>();

            for (char c : s.toCharArray()) {
                if (Character.isLetter(c)) {
                    letters.add(c);
                }
            }

            // Reverse letters
            Collections.reverse(letters);

            // Build result
            StringBuilder result = new StringBuilder();
            int letterIndex = 0;

            for (char c : s.toCharArray()) {
                if (Character.isLetter(c)) {
                    result.append(letters.get(letterIndex++));
                } else {
                    result.append(c);
                }
            }

            return result.toString();
        }
    }

    // Utility methods for testing
    public static void testAllApproaches(String input) {
        System.out.println("Input: \"" + input + "\"");

        String result1 = reverseOnlyLetters(input);
        String result2 = reverseOnlyLettersStack(input);
        String result3 = reverseOnlyLettersTwoPass(input);
        String result4 = reverseOnlyLettersRecursive(input);
        String result5 = reverseOnlyLettersRegex(input);

        System.out.println("Two pointers: \"" + result1 + "\"");
        System.out.println("Stack:        \"" + result2 + "\"");
        System.out.println("Two pass:     \"" + result3 + "\"");
        System.out.println("Recursive:    \"" + result4 + "\"");
        System.out.println("Regex:        \"" + result5 + "\"");

        boolean allEqual = result1.equals(result2) && result2.equals(result3) &&
                result3.equals(result4) && result4.equals(result5);

        System.out.println("All approaches agree: " + allEqual);
        System.out.println();
    }

    public static void performanceComparison(String input) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 100000;

        // Two pointers
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            reverseOnlyLetters(input);
        }
        end = System.nanoTime();
        System.out.println("Two pointers: " + (end - start) / 1_000_000 + " ms");

        // Stack
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            reverseOnlyLettersStack(input);
        }
        end = System.nanoTime();
        System.out.println("Stack:        " + (end - start) / 1_000_000 + " ms");

        // Two pass
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            reverseOnlyLettersTwoPass(input);
        }
        end = System.nanoTime();
        System.out.println("Two pass:     " + (end - start) / 1_000_000 + " ms");

        // Recursive
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            reverseOnlyLettersRecursive(input);
        }
        end = System.nanoTime();
        System.out.println("Recursive:    " + (end - start) / 1_000_000 + " ms");

        System.out.println();
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Examples ===");

        testAllApproaches("ab-cd");
        testAllApproaches("a-bC-dEf-ghIj");
        testAllApproaches("Test1ng-Leet=code-Q!");

        // Test Case 2: Edge cases
        System.out.println("=== Test Case 2: Edge Cases ===");

        testAllApproaches("a"); // Single letter
        testAllApproaches("1"); // Single non-letter
        testAllApproaches("ab"); // Two letters
        testAllApproaches("a1b"); // Letters separated by non-letter
        testAllApproaches("123"); // No letters
        testAllApproaches("!@#$%"); // Special characters only
        testAllApproaches(""); // Empty string (if allowed)

        // Test Case 3: Vowel reversal
        System.out.println("=== Test Case 3: Vowel Reversal ===");

        String[] vowelTests = { "hello", "leetcode", "aeiou", "bcdfg" };

        for (String test : vowelTests) {
            System.out.println("Original: \"" + test + "\"");
            System.out.println("Reverse vowels: \"" + VowelReversal.reverseVowels(test) + "\"");
            System.out.println("Reverse consonants: \"" + VowelReversal.reverseConsonants(test) + "\"");
            System.out.println();
        }

        // Test Case 4: Case-sensitive reversal
        System.out.println("=== Test Case 4: Case-sensitive Reversal ===");

        String caseTest = "AbC-dEf";
        System.out.println("Original: \"" + caseTest + "\"");
        System.out.println("Reverse lowercase: \"" + CaseSensitiveReversal.reverseOnlyLowercase(caseTest) + "\"");
        System.out.println("Reverse uppercase: \"" + CaseSensitiveReversal.reverseOnlyUppercase(caseTest) + "\"");
        System.out.println("Maintain case: \"" + CaseSensitiveReversal.reverseMaintainingCase(caseTest) + "\"");
        System.out.println();

        // Test Case 5: Alphanumeric reversal
        System.out.println("=== Test Case 5: Alphanumeric Reversal ===");

        String alphaTest = "a1b2c3!d4e5";
        System.out.println("Original: \"" + alphaTest + "\"");
        System.out.println("Reverse letters: \"" + reverseOnlyLetters(alphaTest) + "\"");
        System.out.println("Reverse digits: \"" + AlphanumericReversal.reverseOnlyDigits(alphaTest) + "\"");
        System.out.println("Reverse alphanumeric: \"" + AlphanumericReversal.reverseAlphanumeric(alphaTest) + "\"");
        System.out.println(
                "Reverse separately: \"" + AlphanumericReversal.reverseLettersAndDigitsSeparately(alphaTest) + "\"");
        System.out.println();

        // Test Case 6: Word-wise reversal
        System.out.println("=== Test Case 6: Word-wise Reversal ===");

        String wordTest = "hello world test";
        System.out.println("Original: \"" + wordTest + "\"");
        System.out.println("Reverse letters in words: \"" + WordWiseReversal.reverseLettersInWords(wordTest) + "\"");
        System.out
                .println("Preserve spaces: \"" + WordWiseReversal.reverseLettersInWordsPreserveSpaces(wordTest) + "\"");
        System.out.println("Ignore word boundaries: \""
                + WordWiseReversal.reverseOnlyLettersIgnoringWordBoundaries(wordTest) + "\"");
        System.out.println();

        // Test Case 7: In-place reversal
        System.out.println("=== Test Case 7: In-place Reversal ===");

        String inPlaceTest = "a-bC-dEf";
        char[] chars1 = inPlaceTest.toCharArray();
        char[] chars2 = inPlaceTest.toCharArray();

        System.out.println("Original: \"" + inPlaceTest + "\"");

        InPlaceReversal.reverseOnlyLettersInPlace(chars1);
        System.out.println("In-place (XOR): \"" + new String(chars1) + "\"");

        InPlaceReversal.reverseOnlyLettersInPlaceIterative(chars2);
        System.out.println("In-place (temp): \"" + new String(chars2) + "\"");
        System.out.println();

        // Test Case 8: Unicode support
        System.out.println("=== Test Case 8: Unicode Support ===");

        String unicodeTest = "café-naïve";
        System.out.println("Original: \"" + unicodeTest + "\"");
        System.out.println("Unicode letters: \"" + UnicodeSupport.reverseOnlyLettersUnicode(unicodeTest) + "\"");
        System.out.println("ASCII only: \"" + UnicodeSupport.reverseOnlyASCIILetters(unicodeTest) + "\"");
        System.out.println("Alphabetic: \"" + UnicodeSupport.reverseOnlyAlphabetic(unicodeTest) + "\"");
        System.out.println();

        // Test Case 9: Performance comparison
        System.out.println("=== Test Case 9: Performance Comparison ===");

        String perfTest = "This-is-a-much-longer-string-for-performance-testing-123!@#";
        performanceComparison(perfTest);

        // Test Case 10: Complex mixed input
        System.out.println("=== Test Case 10: Complex Mixed Input ===");

        String complexTest = "Hello123!@#World456$%^Test789&*()End";
        System.out.println("Complex input: \"" + complexTest + "\"");

        System.out.println("Letters only: \"" + reverseOnlyLetters(complexTest) + "\"");
        System.out.println("Vowels only: \"" + VowelReversal.reverseVowels(complexTest) + "\"");
        System.out.println("Digits only: \"" + AlphanumericReversal.reverseOnlyDigits(complexTest) + "\"");
        System.out.println("Alphanumeric: \"" + AlphanumericReversal.reverseAlphanumeric(complexTest) + "\"");
        System.out.println();

        // Test Case 11: Stress testing
        System.out.println("=== Test Case 11: Stress Testing ===");

        // Generate large string
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append((char) ('a' + (i % 26)));
            if (i % 10 == 9) {
                sb.append('-');
            }
        }
        String stressTest = sb.toString();

        long start = System.nanoTime();
        String stressResult = reverseOnlyLetters(stressTest);
        long end = System.nanoTime();

        System.out.println("Stress test (1000+ chars): " + (end - start) / 1_000_000 + " ms");
        System.out.println(
                "First 50 chars: \"" + stressResult.substring(0, Math.min(50, stressResult.length())) + "...\"");

        System.out.println("\nReverse Only Letters testing completed successfully!");
    }
}
