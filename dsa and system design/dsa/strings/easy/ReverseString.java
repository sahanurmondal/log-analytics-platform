package strings.easy;

import java.util.*;

/**
 * LeetCode 344: Reverse String
 * https://leetcode.com/problems/reverse-string/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, Apple, Bloomberg
 * Frequency: Very High (Asked in 1200+ interviews)
 *
 * Description:
 * Write a function that reverses a string. The input string is given as an
 * array of characters s.
 * You must do this by modifying the input array in-place with O(1) extra
 * memory.
 * 
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s[i] is a printable ascii character
 * 
 * Follow-up Questions:
 * 1. How would you reverse only alphabetic characters?
 * 2. Can you reverse words in a string instead of characters?
 * 3. What about reversing substrings based on delimiters?
 * 4. How to reverse with custom conditions?
 * 5. Can you implement different reversal algorithms?
 * 6. What about reversing Unicode strings properly?
 */
public class ReverseString {

    // Approach 1: Two Pointers (Optimal) - O(n) time, O(1) space
    public static void reverseString(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while (left < right) {
            // Swap characters
            char temp = s[left];
            s[left] = s[right];
            s[right] = temp;

            left++;
            right--;
        }
    }

    // Approach 2: Two Pointers with XOR swap - O(n) time, O(1) space
    public static void reverseStringXOR(char[] s) {
        int left = 0;
        int right = s.length - 1;

        while (left < right) {
            // XOR swap (only works if characters are different)
            if (s[left] != s[right]) {
                s[left] ^= s[right];
                s[right] ^= s[left];
                s[left] ^= s[right];
            }

            left++;
            right--;
        }
    }

    // Approach 3: Recursion - O(n) time, O(n) space (call stack)
    public static void reverseStringRecursive(char[] s) {
        reverseHelper(s, 0, s.length - 1);
    }

    private static void reverseHelper(char[] s, int left, int right) {
        if (left >= right) {
            return;
        }

        // Swap characters
        char temp = s[left];
        s[left] = s[right];
        s[right] = temp;

        // Recursive call
        reverseHelper(s, left + 1, right - 1);
    }

    // Approach 4: Using Collections.reverse() - O(n) time, O(1) space
    public static void reverseStringCollections(char[] s) {
        List<Character> list = new ArrayList<>();

        // Convert to list
        for (char c : s) {
            list.add(c);
        }

        // Reverse
        Collections.reverse(list);

        // Copy back
        for (int i = 0; i < s.length; i++) {
            s[i] = list.get(i);
        }
    }

    // Approach 5: Stack-based - O(n) time, O(n) space
    public static void reverseStringStack(char[] s) {
        Stack<Character> stack = new Stack<>();

        // Push all characters
        for (char c : s) {
            stack.push(c);
        }

        // Pop and fill array
        for (int i = 0; i < s.length; i++) {
            s[i] = stack.pop();
        }
    }

    // Follow-up 1: Reverse only alphabetic characters
    public static class ReverseAlphabetic {

        public static void reverseOnlyLetters(char[] s) {
            int left = 0;
            int right = s.length - 1;

            while (left < right) {
                // Find next letter from left
                while (left < right && !Character.isLetter(s[left])) {
                    left++;
                }

                // Find next letter from right
                while (left < right && !Character.isLetter(s[right])) {
                    right--;
                }

                // Swap letters
                if (left < right) {
                    char temp = s[left];
                    s[left] = s[right];
                    s[right] = temp;

                    left++;
                    right--;
                }
            }
        }

        // Reverse only alphabetic, preserving case
        public static void reverseLettersPreserveCase(char[] s) {
            int left = 0;
            int right = s.length - 1;

            while (left < right) {
                while (left < right && !Character.isLetter(s[left])) {
                    left++;
                }

                while (left < right && !Character.isLetter(s[right])) {
                    right--;
                }

                if (left < right) {
                    // Swap but preserve case
                    char leftChar = s[left];
                    char rightChar = s[right];

                    if (Character.isUpperCase(leftChar) && Character.isLowerCase(rightChar)) {
                        s[left] = Character.toUpperCase(rightChar);
                        s[right] = Character.toLowerCase(leftChar);
                    } else if (Character.isLowerCase(leftChar) && Character.isUpperCase(rightChar)) {
                        s[left] = Character.toLowerCase(rightChar);
                        s[right] = Character.toUpperCase(leftChar);
                    } else {
                        s[left] = rightChar;
                        s[right] = leftChar;
                    }

                    left++;
                    right--;
                }
            }
        }

        // Reverse alphabetic characters by type (vowels, consonants)
        public static void reverseByCharacterType(char[] s) {
            // Reverse vowels
            reverseVowels(s);

            // Reverse consonants
            reverseConsonants(s);
        }

        private static void reverseVowels(char[] s) {
            Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');
            int left = 0;
            int right = s.length - 1;

            while (left < right) {
                while (left < right && !vowels.contains(s[left])) {
                    left++;
                }

                while (left < right && !vowels.contains(s[right])) {
                    right--;
                }

                if (left < right) {
                    char temp = s[left];
                    s[left] = s[right];
                    s[right] = temp;

                    left++;
                    right--;
                }
            }
        }

        private static void reverseConsonants(char[] s) {
            Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u', 'A', 'E', 'I', 'O', 'U');
            int left = 0;
            int right = s.length - 1;

            while (left < right) {
                while (left < right && (!Character.isLetter(s[left]) || vowels.contains(s[left]))) {
                    left++;
                }

                while (left < right && (!Character.isLetter(s[right]) || vowels.contains(s[right]))) {
                    right--;
                }

                if (left < right) {
                    char temp = s[left];
                    s[left] = s[right];
                    s[right] = temp;

                    left++;
                    right--;
                }
            }
        }
    }

    // Follow-up 2: Reverse words in string
    public static class ReverseWords {

        public static String reverseWords(String s) {
            String[] words = s.trim().split("\\s+");
            StringBuilder result = new StringBuilder();

            for (int i = words.length - 1; i >= 0; i--) {
                result.append(words[i]);
                if (i > 0) {
                    result.append(" ");
                }
            }

            return result.toString();
        }

        // Reverse words in-place (array of characters)
        public static void reverseWordsInPlace(char[] s) {
            // First reverse the entire string
            reverse(s, 0, s.length - 1);

            // Then reverse each word
            int start = 0;
            for (int i = 0; i <= s.length; i++) {
                if (i == s.length || s[i] == ' ') {
                    reverse(s, start, i - 1);
                    start = i + 1;
                }
            }
        }

        private static void reverse(char[] s, int start, int end) {
            while (start < end) {
                char temp = s[start];
                s[start] = s[end];
                s[end] = temp;
                start++;
                end--;
            }
        }

        // Reverse word order but keep individual words unchanged
        public static String reverseWordOrder(String s) {
            List<String> words = Arrays.asList(s.trim().split("\\s+"));
            Collections.reverse(words);
            return String.join(" ", words);
        }

        // Reverse characters within each word
        public static String reverseCharsInWords(String s) {
            String[] words = s.split(" ");
            StringBuilder result = new StringBuilder();

            for (int i = 0; i < words.length; i++) {
                char[] wordChars = words[i].toCharArray();
                reverseString(wordChars);
                result.append(new String(wordChars));

                if (i < words.length - 1) {
                    result.append(" ");
                }
            }

            return result.toString();
        }
    }

    // Follow-up 3: Reverse substrings based on delimiters
    public static class ReverseByDelimiter {

        public static String reverseByDelimiter(String s, char delimiter) {
            String[] parts = s.split(String.valueOf(delimiter));
            StringBuilder result = new StringBuilder();

            for (int i = parts.length - 1; i >= 0; i--) {
                result.append(parts[i]);
                if (i > 0) {
                    result.append(delimiter);
                }
            }

            return result.toString();
        }

        // Reverse each section between delimiters
        public static String reverseSectionsBetweenDelimiters(String s, char delimiter) {
            StringBuilder result = new StringBuilder();
            int start = 0;

            for (int i = 0; i <= s.length(); i++) {
                if (i == s.length() || s.charAt(i) == delimiter) {
                    // Reverse the section
                    String section = s.substring(start, i);
                    char[] sectionChars = section.toCharArray();
                    reverseString(sectionChars);
                    result.append(new String(sectionChars));

                    if (i < s.length()) {
                        result.append(delimiter);
                    }

                    start = i + 1;
                }
            }

            return result.toString();
        }

        // Reverse with multiple delimiters
        public static String reverseByMultipleDelimiters(String s, Set<Character> delimiters) {
            List<String> parts = new ArrayList<>();
            List<Character> separators = new ArrayList<>();
            StringBuilder currentPart = new StringBuilder();

            for (char c : s.toCharArray()) {
                if (delimiters.contains(c)) {
                    parts.add(currentPart.toString());
                    separators.add(c);
                    currentPart = new StringBuilder();
                } else {
                    currentPart.append(c);
                }
            }
            parts.add(currentPart.toString());

            // Reverse parts
            Collections.reverse(parts);

            // Rebuild string
            StringBuilder result = new StringBuilder();
            for (int i = 0; i < parts.size(); i++) {
                result.append(parts.get(i));
                if (i < separators.size()) {
                    result.append(separators.get(separators.size() - 1 - i));
                }
            }

            return result.toString();
        }
    }

    // Follow-up 4: Reverse with custom conditions
    public static class CustomConditions {

        @FunctionalInterface
        public interface ReverseCondition {
            boolean shouldReverse(char c);
        }

        public static void reverseWithCondition(char[] s, ReverseCondition condition) {
            int left = 0;
            int right = s.length - 1;

            while (left < right) {
                // Find next character that meets condition from left
                while (left < right && !condition.shouldReverse(s[left])) {
                    left++;
                }

                // Find next character that meets condition from right
                while (left < right && !condition.shouldReverse(s[right])) {
                    right--;
                }

                // Swap if both characters meet condition
                if (left < right) {
                    char temp = s[left];
                    s[left] = s[right];
                    s[right] = temp;

                    left++;
                    right--;
                }
            }
        }

        // Reverse only digits
        public static void reverseDigits(char[] s) {
            reverseWithCondition(s, Character::isDigit);
        }

        // Reverse only uppercase letters
        public static void reverseUppercase(char[] s) {
            reverseWithCondition(s, Character::isUpperCase);
        }

        // Reverse only specific characters
        public static void reverseSpecificChars(char[] s, Set<Character> targetChars) {
            reverseWithCondition(s, targetChars::contains);
        }

        // Reverse with position-based condition
        public static void reverseEvenPositions(char[] s) {
            List<Character> evenPosChars = new ArrayList<>();
            List<Integer> evenPositions = new ArrayList<>();

            // Collect characters at even positions
            for (int i = 0; i < s.length; i += 2) {
                evenPosChars.add(s[i]);
                evenPositions.add(i);
            }

            // Reverse and put back
            Collections.reverse(evenPosChars);

            for (int i = 0; i < evenPositions.size(); i++) {
                s[evenPositions.get(i)] = evenPosChars.get(i);
            }
        }
    }

    // Follow-up 5: Different reversal algorithms
    public static class DifferentAlgorithms {

        // Bubble-style reversal
        public static void bubbleReverse(char[] s) {
            for (int i = 0; i < s.length / 2; i++) {
                for (int j = i; j < s.length - 1 - i; j++) {
                    // Bubble the character to the right
                    char temp = s[j];
                    s[j] = s[j + 1];
                    s[j + 1] = temp;
                }
            }
        }

        // Cyclic rotation approach
        public static void cyclicReverse(char[] s) {
            int n = s.length;

            for (int i = 0; i < n / 2; i++) {
                char temp = s[i];

                // Shift elements
                for (int j = i; j < n - 1 - i; j++) {
                    s[j] = s[j + 1];
                }

                s[n - 1 - i] = temp;
            }
        }

        // Reverse using array rotation
        public static void rotationReverse(char[] s) {
            int n = s.length;

            // Reverse by rotating the array n-1 times
            for (int i = 0; i < n - 1; i++) {
                rotateLeft(s, 1);
            }
        }

        private static void rotateLeft(char[] s, int positions) {
            int n = s.length;
            positions = positions % n;

            // Store elements to be rotated
            char[] temp = new char[positions];
            System.arraycopy(s, 0, temp, 0, positions);

            // Shift remaining elements
            System.arraycopy(s, positions, s, 0, n - positions);

            // Place stored elements at end
            System.arraycopy(temp, 0, s, n - positions, positions);
        }

        // Divide and conquer reversal
        public static void divideAndConquerReverse(char[] s) {
            divideAndConquerHelper(s, 0, s.length - 1);
        }

        private static void divideAndConquerHelper(char[] s, int start, int end) {
            if (start >= end) {
                return;
            }

            int mid = start + (end - start) / 2;

            // Recursively reverse left and right halves
            divideAndConquerHelper(s, start, mid);
            divideAndConquerHelper(s, mid + 1, end);

            // Merge by swapping the halves
            int leftSize = mid - start + 1;
            int rightSize = end - mid;

            for (int i = 0; i < Math.min(leftSize, rightSize); i++) {
                char temp = s[start + i];
                s[start + i] = s[mid + 1 + i];
                s[mid + 1 + i] = temp;
            }
        }
    }

    // Follow-up 6: Unicode string handling
    public static class UnicodeHandling {

        // Reverse string preserving surrogate pairs
        public static String reverseUnicodeString(String s) {
            if (s == null || s.length() <= 1) {
                return s;
            }

            List<String> codePoints = new ArrayList<>();

            // Split into code points (handling surrogate pairs)
            for (int i = 0; i < s.length();) {
                int codePoint = s.codePointAt(i);
                String codePointStr = new String(Character.toChars(codePoint));
                codePoints.add(codePointStr);
                i += Character.charCount(codePoint);
            }

            // Reverse the list
            Collections.reverse(codePoints);

            // Join back
            return String.join("", codePoints);
        }

        // Reverse handling grapheme clusters
        public static String reverseGraphemeClusters(String s) {
            // This is a simplified version - real implementation would need ICU library
            List<String> clusters = new ArrayList<>();
            StringBuilder currentCluster = new StringBuilder();

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                currentCluster.append(c);

                // Simple heuristic: treat combining marks as part of cluster
                if (i == s.length() - 1 ||
                        (Character.getType(s.charAt(i + 1)) != Character.NON_SPACING_MARK &&
                                Character.getType(s.charAt(i + 1)) != Character.COMBINING_SPACING_MARK)) {
                    clusters.add(currentCluster.toString());
                    currentCluster = new StringBuilder();
                }
            }

            Collections.reverse(clusters);
            return String.join("", clusters);
        }

        // Reverse while preserving text direction marks
        public static String reversePreservingDirectionMarks(String s) {
            List<Character> content = new ArrayList<>();
            List<Character> directionMarks = Arrays.asList('\u202A', '\u202B', '\u202C', '\u202D', '\u202E');

            // Extract non-direction characters
            for (char c : s.toCharArray()) {
                if (!directionMarks.contains(c)) {
                    content.add(c);
                }
            }

            // Reverse content
            Collections.reverse(content);

            // Rebuild with direction marks in original positions
            StringBuilder result = new StringBuilder();
            int contentIndex = 0;

            for (char c : s.toCharArray()) {
                if (directionMarks.contains(c)) {
                    result.append(c);
                } else {
                    result.append(content.get(contentIndex++));
                }
            }

            return result.toString();
        }
    }

    // Utility methods
    public static String arrayToString(char[] arr) {
        return new String(arr);
    }

    public static char[] stringToArray(String s) {
        return s.toCharArray();
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(char[] original, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Array length: " + original.length + ", Iterations: " + iterations);

            // Two pointers
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                char[] copy = Arrays.copyOf(original, original.length);
                reverseString(copy);
            }
            long twoPointerTime = System.nanoTime() - start;

            // XOR swap
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                char[] copy = Arrays.copyOf(original, original.length);
                reverseStringXOR(copy);
            }
            long xorTime = System.nanoTime() - start;

            // Recursive
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                char[] copy = Arrays.copyOf(original, original.length);
                reverseStringRecursive(copy);
            }
            long recursiveTime = System.nanoTime() - start;

            // Collections
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                char[] copy = Arrays.copyOf(original, original.length);
                reverseStringCollections(copy);
            }
            long collectionsTime = System.nanoTime() - start;

            System.out.println("Two Pointers: " + twoPointerTime / 1_000_000 + " ms");
            System.out.println("XOR Swap: " + xorTime / 1_000_000 + " ms");
            System.out.println("Recursive: " + recursiveTime / 1_000_000 + " ms");
            System.out.println("Collections: " + collectionsTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        char[] test1 = { 'h', 'e', 'l', 'l', 'o' };
        System.out.println("Original: " + arrayToString(test1));

        char[] copy1 = Arrays.copyOf(test1, test1.length);
        reverseString(copy1);
        System.out.println("Two Pointers: " + arrayToString(copy1));

        char[] copy2 = Arrays.copyOf(test1, test1.length);
        reverseStringXOR(copy2);
        System.out.println("XOR Swap: " + arrayToString(copy2));

        char[] copy3 = Arrays.copyOf(test1, test1.length);
        reverseStringRecursive(copy3);
        System.out.println("Recursive: " + arrayToString(copy3));

        char[] copy4 = Arrays.copyOf(test1, test1.length);
        reverseStringStack(copy4);
        System.out.println("Stack: " + arrayToString(copy4));

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");

        // Single character
        char[] single = { 'a' };
        reverseString(single);
        System.out.println("Single char: " + arrayToString(single));

        // Two characters
        char[] two = { 'a', 'b' };
        reverseString(two);
        System.out.println("Two chars: " + arrayToString(two));

        // Palindrome
        char[] palindrome = { 'r', 'a', 'c', 'e', 'c', 'a', 'r' };
        System.out.println("Palindrome before: " + arrayToString(palindrome));
        reverseString(palindrome);
        System.out.println("Palindrome after: " + arrayToString(palindrome));

        // Test Case 3: Reverse only letters
        System.out.println("\n=== Test Case 3: Reverse Only Letters ===");

        char[] mixed = { 'a', '1', 'b', '2', 'c', '3' };
        System.out.println("Original: " + arrayToString(mixed));

        char[] mixedCopy = Arrays.copyOf(mixed, mixed.length);
        ReverseAlphabetic.reverseOnlyLetters(mixedCopy);
        System.out.println("Letters only: " + arrayToString(mixedCopy));

        char[] caseTest = { 'A', 'b', 'C', 'd', 'E' };
        System.out.println("Case test before: " + arrayToString(caseTest));
        ReverseAlphabetic.reverseLettersPreserveCase(caseTest);
        System.out.println("Preserve case: " + arrayToString(caseTest));

        char[] vowelTest = { 'h', 'e', 'l', 'l', 'o' };
        System.out.println("Vowel test before: " + arrayToString(vowelTest));
        ReverseAlphabetic.reverseByCharacterType(vowelTest);
        System.out.println("By character type: " + arrayToString(vowelTest));

        // Test Case 4: Reverse words
        System.out.println("\n=== Test Case 4: Reverse Words ===");

        String sentence = "hello world java";
        System.out.println("Original: \"" + sentence + "\"");
        System.out.println("Reverse words: \"" + ReverseWords.reverseWords(sentence) + "\"");
        System.out.println("Reverse word order: \"" + ReverseWords.reverseWordOrder(sentence) + "\"");
        System.out.println("Reverse chars in words: \"" + ReverseWords.reverseCharsInWords(sentence) + "\"");

        char[] sentenceArray = sentence.toCharArray();
        ReverseWords.reverseWordsInPlace(sentenceArray);
        System.out.println("In-place word reverse: \"" + arrayToString(sentenceArray) + "\"");

        // Test Case 5: Reverse by delimiter
        System.out.println("\n=== Test Case 5: Reverse by Delimiter ===");

        String delimited = "a.b.c.d";
        System.out.println("Original: \"" + delimited + "\"");
        System.out.println("Reverse by '.': \"" + ReverseByDelimiter.reverseByDelimiter(delimited, '.') + "\"");
        System.out.println(
                "Reverse sections: \"" + ReverseByDelimiter.reverseSectionsBetweenDelimiters(delimited, '.') + "\"");

        String multiDelim = "a,b;c,d;e";
        Set<Character> delimiters = Set.of(',', ';');
        System.out.println("Multi-delimiter: \"" + multiDelim + "\"");
        System.out
                .println("Result: \"" + ReverseByDelimiter.reverseByMultipleDelimiters(multiDelim, delimiters) + "\"");

        // Test Case 6: Custom conditions
        System.out.println("\n=== Test Case 6: Custom Conditions ===");

        char[] digitTest = { 'a', '1', 'b', '2', 'c', '3', 'd' };
        System.out.println("Original: " + arrayToString(digitTest));

        char[] digitCopy = Arrays.copyOf(digitTest, digitTest.length);
        CustomConditions.reverseDigits(digitCopy);
        System.out.println("Reverse digits: " + arrayToString(digitCopy));

        char[] upperTest = { 'A', 'b', 'C', 'd', 'E', 'f' };
        System.out.println("Upper test: " + arrayToString(upperTest));
        CustomConditions.reverseUppercase(upperTest);
        System.out.println("Reverse uppercase: " + arrayToString(upperTest));

        char[] specificTest = { 'a', 'x', 'b', 'x', 'c', 'x', 'd' };
        Set<Character> targetChars = Set.of('x');
        System.out.println("Specific test: " + arrayToString(specificTest));
        CustomConditions.reverseSpecificChars(specificTest, targetChars);
        System.out.println("Reverse 'x' chars: " + arrayToString(specificTest));

        char[] evenTest = { 'a', 'b', 'c', 'd', 'e', 'f' };
        System.out.println("Even positions before: " + arrayToString(evenTest));
        CustomConditions.reverseEvenPositions(evenTest);
        System.out.println("Even positions after: " + arrayToString(evenTest));

        // Test Case 7: Different algorithms
        System.out.println("\n=== Test Case 7: Different Algorithms ===");

        char[] algoTest = { 'a', 'b', 'c', 'd', 'e' };

        char[] bubbleCopy = Arrays.copyOf(algoTest, algoTest.length);
        DifferentAlgorithms.bubbleReverse(bubbleCopy);
        System.out.println("Bubble reverse: " + arrayToString(bubbleCopy));

        char[] cyclicCopy = Arrays.copyOf(algoTest, algoTest.length);
        DifferentAlgorithms.cyclicReverse(cyclicCopy);
        System.out.println("Cyclic reverse: " + arrayToString(cyclicCopy));

        char[] rotationCopy = Arrays.copyOf(algoTest, algoTest.length);
        DifferentAlgorithms.rotationReverse(rotationCopy);
        System.out.println("Rotation reverse: " + arrayToString(rotationCopy));

        char[] dacCopy = Arrays.copyOf(algoTest, algoTest.length);
        DifferentAlgorithms.divideAndConquerReverse(dacCopy);
        System.out.println("Divide & conquer: " + arrayToString(dacCopy));

        // Test Case 8: Unicode handling
        System.out.println("\n=== Test Case 8: Unicode Handling ===");

        String unicode1 = "hello 🌟 world 🚀";
        System.out.println("Unicode original: \"" + unicode1 + "\"");
        System.out.println("Unicode reversed: \"" + UnicodeHandling.reverseUnicodeString(unicode1) + "\"");

        String withCombining = "café"; // e with accent
        System.out.println("With combining: \"" + withCombining + "\"");
        System.out.println("Grapheme clusters: \"" + UnicodeHandling.reverseGraphemeClusters(withCombining) + "\"");

        // Test Case 9: Large array performance
        System.out.println("\n=== Test Case 9: Large Array Performance ===");

        char[] largeArray = new char[10000];
        Random random = new Random(42);

        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (char) ('a' + random.nextInt(26));
        }

        long start = System.currentTimeMillis();
        reverseString(largeArray);
        long end = System.currentTimeMillis();

        System.out.println("Large array (10,000 chars): " + (end - start) + " ms");

        // Test Case 10: Stress test
        System.out.println("\n=== Test Case 10: Stress Test ===");

        int testCases = 1000;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int length = random.nextInt(50) + 1;
            char[] testArray = new char[length];

            for (int i = 0; i < length; i++) {
                testArray[i] = (char) ('a' + random.nextInt(26));
            }

            char[] original = Arrays.copyOf(testArray, testArray.length);

            // Test different approaches
            char[] testCopy1 = Arrays.copyOf(testArray, testArray.length);
            char[] testCopy2 = Arrays.copyOf(testArray, testArray.length);
            char[] testCopy3 = Arrays.copyOf(testArray, testArray.length);

            reverseString(testCopy1);
            reverseStringXOR(testCopy2);
            reverseStringRecursive(testCopy3);

            if (Arrays.equals(testCopy1, testCopy2) && Arrays.equals(testCopy2, testCopy3)) {
                // Verify it's actually reversed
                boolean isReversed = true;
                for (int i = 0; i < original.length; i++) {
                    if (original[i] != testCopy1[original.length - 1 - i]) {
                        isReversed = false;
                        break;
                    }
                }

                if (isReversed) {
                    passed++;
                }
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Performance comparison
        char[] perfArray = new char[1000];
        for (int i = 0; i < perfArray.length; i++) {
            perfArray[i] = (char) ('a' + i % 26);
        }

        PerformanceComparison.compareApproaches(perfArray, 10000);

        System.out.println("\nReverse String testing completed successfully!");
    }
}
