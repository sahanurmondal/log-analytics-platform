package stacks.easy;

import java.util.*;

/**
 * LeetCode 1047: Remove All Adjacent Duplicates In String
 * https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/
 * 
 * Companies: Facebook, Amazon, Google, Microsoft, Apple, Bloomberg
 * Frequency: High (Asked in 700+ interviews)
 *
 * Description:
 * You are given a string s consisting of lowercase English letters.
 * A duplicate removal consists of choosing two adjacent and equal characters
 * and removing them.
 * We repeatedly make duplicate removals on s until we no longer can.
 * Return the final string after all such duplicate removals have been made.
 * It can be proven that the answer is unique.
 * 
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. How would you remove k adjacent duplicates instead of 2?
 * 2. Can you solve this without using extra space?
 * 3. What about removing duplicates with different patterns?
 * 4. How to handle case-insensitive duplicates?
 * 5. Can you track which characters were removed?
 * 6. What about removing non-adjacent duplicates?
 */
public class RemoveAllAdjacentDuplicatesInString {

    // Approach 1: Stack - O(n) time, O(n) space
    public static String removeDuplicates(String s) {
        Stack<Character> stack = new Stack<>();

        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek() == c) {
                stack.pop(); // Remove adjacent duplicate
            } else {
                stack.push(c);
            }
        }

        // Build result string
        StringBuilder result = new StringBuilder();
        for (char c : stack) {
            result.append(c);
        }

        return result.toString();
    }

    // Approach 2: StringBuilder (more efficient) - O(n) time, O(n) space
    public static String removeDuplicatesStringBuilder(String s) {
        StringBuilder sb = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c) {
                sb.deleteCharAt(sb.length() - 1); // Remove last character
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    // Approach 3: Two Pointers (In-place with char array) - O(n) time, O(1) extra
    // space
    public static String removeDuplicatesTwoPointers(String s) {
        char[] chars = s.toCharArray();
        int writeIndex = 0;

        for (int readIndex = 0; readIndex < chars.length; readIndex++) {
            if (writeIndex > 0 && chars[writeIndex - 1] == chars[readIndex]) {
                writeIndex--; // Remove previous character by moving write pointer back
            } else {
                chars[writeIndex] = chars[readIndex];
                writeIndex++;
            }
        }

        return new String(chars, 0, writeIndex);
    }

    // Approach 4: Recursive approach - O(n^2) worst case, O(n) space
    public static String removeDuplicatesRecursive(String s) {
        boolean found = false;
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++) {
            if (i < s.length() - 1 && s.charAt(i) == s.charAt(i + 1)) {
                // Skip both duplicate characters
                i++; // Skip the next character too
                found = true;
            } else {
                sb.append(s.charAt(i));
            }
        }

        // If duplicates were found, recursively process the result
        if (found) {
            return removeDuplicatesRecursive(sb.toString());
        }

        return sb.toString();
    }

    // Follow-up 1: Remove k adjacent duplicates
    public static class RemoveKDuplicates {

        // Using stack with count
        public static String removeDuplicates(String s, int k) {
            Stack<CharCount> stack = new Stack<>();

            for (char c : s.toCharArray()) {
                if (!stack.isEmpty() && stack.peek().ch == c) {
                    stack.peek().count++;
                    if (stack.peek().count == k) {
                        stack.pop();
                    }
                } else {
                    stack.push(new CharCount(c, 1));
                }
            }

            StringBuilder result = new StringBuilder();
            for (CharCount cc : stack) {
                for (int i = 0; i < cc.count; i++) {
                    result.append(cc.ch);
                }
            }

            return result.toString();
        }

        private static class CharCount {
            char ch;
            int count;

            CharCount(char ch, int count) {
                this.ch = ch;
                this.count = count;
            }
        }

        // Using StringBuilder with tracking
        public static String removeDuplicatesStringBuilder(String s, int k) {
            StringBuilder sb = new StringBuilder();
            int[] counts = new int[s.length()];

            for (char c : s.toCharArray()) {
                int len = sb.length();

                if (len > 0 && sb.charAt(len - 1) == c) {
                    counts[len - 1]++;
                    if (counts[len - 1] == k) {
                        // Remove k characters
                        sb.setLength(len - k + 1);
                    } else {
                        sb.append(c);
                    }
                } else {
                    sb.append(c);
                    counts[len] = 1;
                }
            }

            return sb.toString();
        }

        // Optimized version with character frequency tracking
        public static String removeDuplicatesOptimized(String s, int k) {
            StringBuilder sb = new StringBuilder();
            Stack<Integer> counts = new Stack<>();

            for (char c : s.toCharArray()) {
                if (sb.length() > 0 && sb.charAt(sb.length() - 1) == c) {
                    int count = counts.pop() + 1;
                    if (count == k) {
                        sb.setLength(sb.length() - k + 1);
                    } else {
                        sb.append(c);
                        counts.push(count);
                    }
                } else {
                    sb.append(c);
                    counts.push(1);
                }
            }

            return sb.toString();
        }
    }

    // Follow-up 2: In-place removal (no extra space except input modification)
    public static class InPlaceRemoval {

        public static String removeDuplicatesInPlace(String s) {
            char[] chars = s.toCharArray();
            int writeIndex = 0;

            for (int readIndex = 0; readIndex < chars.length; readIndex++) {
                chars[writeIndex] = chars[readIndex];

                // Check if we have a duplicate
                if (writeIndex > 0 && chars[writeIndex - 1] == chars[writeIndex]) {
                    writeIndex--; // Remove both characters by backing up
                } else {
                    writeIndex++;
                }
            }

            return new String(chars, 0, writeIndex);
        }

        // Alternative approach using marking
        public static String removeDuplicatesMarking(String s) {
            char[] chars = s.toCharArray();
            boolean[] removed = new boolean[chars.length];
            boolean found = true;

            while (found) {
                found = false;
                for (int i = 0; i < chars.length - 1; i++) {
                    if (!removed[i] && !removed[i + 1] && chars[i] == chars[i + 1]) {
                        removed[i] = true;
                        removed[i + 1] = true;
                        found = true;
                    }
                }
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < chars.length; i++) {
                if (!removed[i]) {
                    result.append(chars[i]);
                }
            }

            return result.toString();
        }
    }

    // Follow-up 3: Different patterns of duplicate removal
    public static class DifferentPatterns {

        // Remove all characters that appear exactly twice adjacently
        public static String removeExactlyTwoDuplicates(String s) {
            StringBuilder sb = new StringBuilder();
            int i = 0;

            while (i < s.length()) {
                char current = s.charAt(i);
                int count = 1;

                // Count consecutive occurrences
                while (i + count < s.length() && s.charAt(i + count) == current) {
                    count++;
                }

                // Keep characters that don't appear exactly twice
                if (count != 2) {
                    for (int j = 0; j < count; j++) {
                        sb.append(current);
                    }
                }

                i += count;
            }

            return sb.toString();
        }

        // Remove palindromic substrings of length 2
        public static String removePalindromicPairs(String s) {
            Stack<Character> stack = new Stack<>();

            for (char c : s.toCharArray()) {
                if (!stack.isEmpty() && stack.peek() == c) {
                    stack.pop(); // Remove palindromic pair
                } else {
                    stack.push(c);
                }
            }

            StringBuilder result = new StringBuilder();
            for (char c : stack) {
                result.append(c);
            }

            return result.toString();
        }

        // Remove all consecutive duplicates (not just pairs)
        public static String removeAllConsecutiveDuplicates(String s) {
            if (s.length() <= 1)
                return s;

            StringBuilder sb = new StringBuilder();
            char prev = s.charAt(0);
            boolean isDuplicate = false;

            for (int i = 1; i < s.length(); i++) {
                if (s.charAt(i) == prev) {
                    isDuplicate = true;
                } else {
                    if (!isDuplicate) {
                        sb.append(prev);
                    }
                    prev = s.charAt(i);
                    isDuplicate = false;
                }
            }

            // Add last character if it's not a duplicate
            if (!isDuplicate) {
                sb.append(prev);
            }

            return sb.toString();
        }
    }

    // Follow-up 4: Case-insensitive duplicates
    public static class CaseInsensitive {

        public static String removeDuplicatesCaseInsensitive(String s) {
            Stack<Character> stack = new Stack<>();

            for (char c : s.toCharArray()) {
                if (!stack.isEmpty() &&
                        Character.toLowerCase(stack.peek()) == Character.toLowerCase(c)) {
                    stack.pop(); // Remove case-insensitive duplicate
                } else {
                    stack.push(c);
                }
            }

            StringBuilder result = new StringBuilder();
            for (char c : stack) {
                result.append(c);
            }

            return result.toString();
        }

        // With StringBuilder
        public static String removeDuplicatesCaseInsensitiveStringBuilder(String s) {
            StringBuilder sb = new StringBuilder();

            for (char c : s.toCharArray()) {
                if (sb.length() > 0 &&
                        Character.toLowerCase(sb.charAt(sb.length() - 1)) == Character.toLowerCase(c)) {
                    sb.deleteCharAt(sb.length() - 1);
                } else {
                    sb.append(c);
                }
            }

            return sb.toString();
        }

        // Preserve original case of remaining characters
        public static String removeDuplicatesPreserveCase(String s) {
            StringBuilder sb = new StringBuilder();

            for (char c : s.toCharArray()) {
                if (sb.length() > 0) {
                    char last = sb.charAt(sb.length() - 1);
                    if (Character.toLowerCase(last) == Character.toLowerCase(c)) {
                        sb.deleteCharAt(sb.length() - 1);
                        continue;
                    }
                }
                sb.append(c);
            }

            return sb.toString();
        }
    }

    // Follow-up 5: Track removed characters
    public static class TrackRemovals {

        public static class RemovalResult {
            String finalString;
            List<String> removedPairs;
            int totalRemoved;

            public RemovalResult(String finalString, List<String> removedPairs, int totalRemoved) {
                this.finalString = finalString;
                this.removedPairs = removedPairs;
                this.totalRemoved = totalRemoved;
            }

            @Override
            public String toString() {
                return String.format("Final: '%s', Removed pairs: %s, Total removed: %d",
                        finalString, removedPairs, totalRemoved);
            }
        }

        public static RemovalResult removeDuplicatesWithTracking(String s) {
            Stack<Character> stack = new Stack<>();
            List<String> removedPairs = new ArrayList<>();
            int totalRemoved = 0;

            for (char c : s.toCharArray()) {
                if (!stack.isEmpty() && stack.peek() == c) {
                    char removed = stack.pop();
                    removedPairs.add("" + removed + c);
                    totalRemoved += 2;
                } else {
                    stack.push(c);
                }
            }

            StringBuilder result = new StringBuilder();
            for (char c : stack) {
                result.append(c);
            }

            return new RemovalResult(result.toString(), removedPairs, totalRemoved);
        }

        // Track removal positions
        public static class PositionalRemoval {
            String finalString;
            List<RemovalInfo> removals;

            public PositionalRemoval(String finalString, List<RemovalInfo> removals) {
                this.finalString = finalString;
                this.removals = removals;
            }

            @Override
            public String toString() {
                return String.format("Final: '%s', Removals: %s", finalString, removals);
            }
        }

        public static class RemovalInfo {
            int position;
            String removedPair;

            public RemovalInfo(int position, String removedPair) {
                this.position = position;
                this.removedPair = removedPair;
            }

            @Override
            public String toString() {
                return String.format("pos:%d='%s'", position, removedPair);
            }
        }

        public static PositionalRemoval removeDuplicatesWithPositions(String s) {
            List<Character> chars = new ArrayList<>();
            List<RemovalInfo> removals = new ArrayList<>();

            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);

                if (!chars.isEmpty() && chars.get(chars.size() - 1) == c) {
                    char removed = chars.remove(chars.size() - 1);
                    removals.add(new RemovalInfo(i - 1, "" + removed + c));
                } else {
                    chars.add(c);
                }
            }

            StringBuilder result = new StringBuilder();
            for (char c : chars) {
                result.append(c);
            }

            return new PositionalRemoval(result.toString(), removals);
        }
    }

    // Follow-up 6: Remove non-adjacent duplicates
    public static class NonAdjacentDuplicates {

        // Remove all occurrences of characters that appear more than once
        public static String removeAllDuplicateChars(String s) {
            Map<Character, Integer> freq = new HashMap<>();

            // Count frequencies
            for (char c : s.toCharArray()) {
                freq.put(c, freq.getOrDefault(c, 0) + 1);
            }

            // Keep only characters that appear exactly once
            StringBuilder result = new StringBuilder();
            for (char c : s.toCharArray()) {
                if (freq.get(c) == 1) {
                    result.append(c);
                }
            }

            return result.toString();
        }

        // Remove duplicates keeping first occurrence
        public static String removeDuplicatesKeepFirst(String s) {
            Set<Character> seen = new HashSet<>();
            StringBuilder result = new StringBuilder();

            for (char c : s.toCharArray()) {
                if (!seen.contains(c)) {
                    seen.add(c);
                    result.append(c);
                }
            }

            return result.toString();
        }

        // Remove duplicates keeping last occurrence
        public static String removeDuplicatesKeepLast(String s) {
            Map<Character, Integer> lastIndex = new HashMap<>();

            // Find last occurrence of each character
            for (int i = 0; i < s.length(); i++) {
                lastIndex.put(s.charAt(i), i);
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                if (lastIndex.get(s.charAt(i)) == i) {
                    result.append(s.charAt(i));
                }
            }

            return result.toString();
        }

        // Remove characters that have duplicates elsewhere
        public static String removeCharsWithDuplicates(String s) {
            Map<Character, List<Integer>> positions = new HashMap<>();

            // Record all positions
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (!positions.containsKey(c)) {
                    positions.put(c, new ArrayList<>());
                }
                positions.get(c).add(i);
            }

            StringBuilder result = new StringBuilder();
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (positions.get(c).size() == 1) {
                    result.append(c);
                }
            }

            return result.toString();
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(String s, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("String length: " + s.length() + ", Iterations: " + iterations);

            // Stack approach
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                removeDuplicates(s);
            }
            long stackTime = System.nanoTime() - start;

            // StringBuilder approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                removeDuplicatesStringBuilder(s);
            }
            long sbTime = System.nanoTime() - start;

            // Two pointers approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                removeDuplicatesTwoPointers(s);
            }
            long twoPointerTime = System.nanoTime() - start;

            // Recursive approach (for smaller strings)
            if (s.length() <= 100) {
                start = System.nanoTime();
                for (int i = 0; i < iterations; i++) {
                    removeDuplicatesRecursive(s);
                }
                long recursiveTime = System.nanoTime() - start;

                System.out.println("Stack: " + stackTime / 1_000_000 + " ms");
                System.out.println("StringBuilder: " + sbTime / 1_000_000 + " ms");
                System.out.println("Two Pointers: " + twoPointerTime / 1_000_000 + " ms");
                System.out.println("Recursive: " + recursiveTime / 1_000_000 + " ms");
            } else {
                System.out.println("Stack: " + stackTime / 1_000_000 + " ms");
                System.out.println("StringBuilder: " + sbTime / 1_000_000 + " ms");
                System.out.println("Two Pointers: " + twoPointerTime / 1_000_000 + " ms");
                System.out.println("Recursive: Skipped (string too long)");
            }
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        String s1 = "abbaca";
        System.out.println("Input: \"" + s1 + "\"");
        System.out.println("Stack: " + removeDuplicates(s1));
        System.out.println("StringBuilder: " + removeDuplicatesStringBuilder(s1));
        System.out.println("Two Pointers: " + removeDuplicatesTwoPointers(s1));
        System.out.println("Recursive: " + removeDuplicatesRecursive(s1));

        String s2 = "azxxzy";
        System.out.println("\nInput: \"" + s2 + "\"");
        System.out.println("Result: " + removeDuplicates(s2));

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");

        // Single character
        System.out.println("Single char 'a': " + removeDuplicates("a"));

        // Two same characters
        System.out.println("Two same 'aa': " + removeDuplicates("aa"));

        // Two different characters
        System.out.println("Two diff 'ab': " + removeDuplicates("ab"));

        // All same characters
        System.out.println("All same 'aaaa': " + removeDuplicates("aaaa"));

        // No duplicates
        System.out.println("No duplicates 'abcde': " + removeDuplicates("abcde"));

        // Complex case
        System.out.println("Complex 'aabbbabaay': " + removeDuplicates("aabbbabaay"));

        // Test Case 3: Remove k duplicates
        System.out.println("\n=== Test Case 3: Remove K Duplicates ===");

        String k1 = "abcd";
        int k = 2;
        System.out.println("Input: \"" + k1 + "\", k=" + k);
        System.out.println("Result: " + RemoveKDuplicates.removeDuplicates(k1, k));

        String k2 = "deeedbbcccbdaa";
        k = 3;
        System.out.println("Input: \"" + k2 + "\", k=" + k);
        System.out.println("Stack approach: " + RemoveKDuplicates.removeDuplicates(k2, k));
        System.out.println("StringBuilder: " + RemoveKDuplicates.removeDuplicatesStringBuilder(k2, k));
        System.out.println("Optimized: " + RemoveKDuplicates.removeDuplicatesOptimized(k2, k));

        String k3 = "pbbcggttciiippooaais";
        k = 2;
        System.out.println("Input: \"" + k3 + "\", k=" + k);
        System.out.println("Result: " + RemoveKDuplicates.removeDuplicates(k3, k));

        // Test Case 4: In-place removal
        System.out.println("\n=== Test Case 4: In-place Removal ===");

        String ip1 = "abbaca";
        System.out.println("Input: \"" + ip1 + "\"");
        System.out.println("In-place: " + InPlaceRemoval.removeDuplicatesInPlace(ip1));
        System.out.println("Marking: " + InPlaceRemoval.removeDuplicatesMarking(ip1));

        // Test Case 5: Different patterns
        System.out.println("\n=== Test Case 5: Different Patterns ===");

        String p1 = "aaabbcccddd";
        System.out.println("Input: \"" + p1 + "\"");
        System.out.println("Remove exactly two: " + DifferentPatterns.removeExactlyTwoDuplicates(p1));
        System.out.println("Remove palindromic pairs: " + DifferentPatterns.removePalindromicPairs(p1));
        System.out.println("Remove all consecutive: " + DifferentPatterns.removeAllConsecutiveDuplicates(p1));

        // Test Case 6: Case insensitive
        System.out.println("\n=== Test Case 6: Case Insensitive ===");

        String ci1 = "abBAcC";
        System.out.println("Input: \"" + ci1 + "\"");
        System.out.println("Case insensitive: " + CaseInsensitive.removeDuplicatesCaseInsensitive(ci1));
        System.out.println("StringBuilder: " + CaseInsensitive.removeDuplicatesCaseInsensitiveStringBuilder(ci1));
        System.out.println("Preserve case: " + CaseInsensitive.removeDuplicatesPreserveCase(ci1));

        // Test Case 7: Track removals
        System.out.println("\n=== Test Case 7: Track Removals ===");

        String tr1 = "abbaca";
        System.out.println("Input: \"" + tr1 + "\"");

        TrackRemovals.RemovalResult result = TrackRemovals.removeDuplicatesWithTracking(tr1);
        System.out.println("Tracking result: " + result);

        TrackRemovals.PositionalRemoval posResult = TrackRemovals.removeDuplicatesWithPositions(tr1);
        System.out.println("Positional result: " + posResult);

        // Test Case 8: Non-adjacent duplicates
        System.out.println("\n=== Test Case 8: Non-adjacent Duplicates ===");

        String na1 = "abccba";
        System.out.println("Input: \"" + na1 + "\"");
        System.out.println("Remove all duplicate chars: " + NonAdjacentDuplicates.removeAllDuplicateChars(na1));
        System.out.println("Keep first: " + NonAdjacentDuplicates.removeDuplicatesKeepFirst(na1));
        System.out.println("Keep last: " + NonAdjacentDuplicates.removeDuplicatesKeepLast(na1));
        System.out.println("Remove chars with duplicates: " + NonAdjacentDuplicates.removeCharsWithDuplicates(na1));

        // Test Case 9: Large string performance
        System.out.println("\n=== Test Case 9: Large String Performance ===");

        StringBuilder largeSB = new StringBuilder();
        Random random = new Random(42);
        int size = 10000;

        for (int i = 0; i < size; i++) {
            largeSB.append((char) ('a' + random.nextInt(5))); // Limited alphabet for more duplicates
        }

        String largeString = largeSB.toString();

        long start = System.currentTimeMillis();
        String largeResult = removeDuplicatesStringBuilder(largeString);
        long end = System.currentTimeMillis();

        System.out.println("Large string (size=" + size + "): " + (end - start) + " ms");
        System.out.println("Original length: " + largeString.length());
        System.out.println("Result length: " + largeResult.length());
        System.out.println(
                "Reduction: " + (100.0 * (largeString.length() - largeResult.length()) / largeString.length()) + "%");

        // Test Case 10: Stress test
        System.out.println("\n=== Test Case 10: Stress Test ===");

        int testCases = 1000;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int len = random.nextInt(50) + 1;
            StringBuilder testSB = new StringBuilder();

            for (int i = 0; i < len; i++) {
                testSB.append((char) ('a' + random.nextInt(10)));
            }

            String testString = testSB.toString();

            // Test all main approaches
            String result1 = removeDuplicates(testString);
            String result2 = removeDuplicatesStringBuilder(testString);
            String result3 = removeDuplicatesTwoPointers(testString);

            if (result1.equals(result2) && result2.equals(result3)) {
                passed++;
            } else {
                System.out.println("Failed test: \"" + testString + "\"");
                System.out.println("Stack: " + result1);
                System.out.println("StringBuilder: " + result2);
                System.out.println("TwoPointers: " + result3);
                break;
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Test Case 11: Specific pattern tests
        System.out.println("\n=== Test Case 11: Specific Pattern Tests ===");

        String[] patterns = {
                "aabbcc", // All pairs
                "abcabc", // No adjacent duplicates
                "aaabbbccc", // Groups of three
                "abccbaadef", // Mixed patterns
                "aabbbabaaba", // Complex nested
                "xyzzyx" // Palindromic with duplicates
        };

        for (String pattern : patterns) {
            System.out.println("\"" + pattern + "\" -> \"" + removeDuplicates(pattern) + "\"");
        }

        // Performance comparison
        String perfString = "aabbccddee" + "ffgghhiijj".repeat(10);
        PerformanceComparison.compareApproaches(perfString, 10000);

        System.out.println("\nRemove All Adjacent Duplicates testing completed successfully!");
    }
}
