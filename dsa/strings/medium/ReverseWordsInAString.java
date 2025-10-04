package strings.medium;

import java.util.*;

/**
 * LeetCode 151: Reverse Words in a String
 * https://leetcode.com/problems/reverse-words-in-a-string/
 * 
 * Companies: Microsoft, Amazon, Google
 * Frequency: High
 *
 * Description: Given an input string s, reverse the order of the words.
 *
 * Constraints:
 * - 1 <= s.length <= 10^4
 * - s contains English letters, digits, and spaces
 * 
 * Follow-up Questions:
 * 1. Can you reverse characters in each word?
 * 2. Can you handle multiple spaces?
 * 3. Can you do it in-place?
 */
public class ReverseWordsInAString {

    // Approach 1: Split and reverse (O(n) time, O(n) space)
    public String reverseWords(String s) {
        String[] words = s.trim().split("\\s+");
        Collections.reverse(Arrays.asList(words));
        return String.join(" ", words);
    }

    // Follow-up 1: Reverse characters in each word
    public String reverseCharactersInWords(String s) {
        String[] words = s.trim().split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = new StringBuilder(words[i]).reverse().toString();
        }
        return String.join(" ", words);
    }

    // Follow-up 2: Handle multiple spaces manually
    public String reverseWordsManual(String s) {
        List<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (c == ' ') {
                if (word.length() > 0) {
                    words.add(word.toString());
                    word.setLength(0);
                }
            } else {
                word.append(c);
            }
        }
        if (word.length() > 0)
            words.add(word.toString());

        Collections.reverse(words);
        return String.join(" ", words);
    }

    // Follow-up 3: In-place reversal (char array)
    public String reverseWordsInPlace(String s) {
        char[] chars = s.toCharArray();

        // Remove extra spaces
        int writeIdx = 0;
        boolean spaceFound = false;
        for (int readIdx = 0; readIdx < chars.length; readIdx++) {
            if (chars[readIdx] != ' ') {
                if (spaceFound && writeIdx > 0)
                    chars[writeIdx++] = ' ';
                chars[writeIdx++] = chars[readIdx];
                spaceFound = false;
            } else {
                spaceFound = true;
            }
        }

        // Reverse entire string
        reverse(chars, 0, writeIdx - 1);

        // Reverse each word
        int start = 0;
        for (int i = 0; i <= writeIdx; i++) {
            if (i == writeIdx || chars[i] == ' ') {
                reverse(chars, start, i - 1);
                start = i + 1;
            }
        }

        return new String(chars, 0, writeIdx);
    }

    private void reverse(char[] chars, int start, int end) {
        while (start < end) {
            char temp = chars[start];
            chars[start] = chars[end];
            chars[end] = temp;
            start++;
            end--;
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ReverseWordsInAString solution = new ReverseWordsInAString();

        // Test case 1: Basic case
        String s1 = "the sky is blue";
        System.out.println("Test 1 - s: '" + s1 + "' Expected: 'blue is sky the'");
        System.out.println("Result: '" + solution.reverseWords(s1) + "'");

        // Test case 2: Reverse characters in words
        System.out.println("\nTest 2 - Reverse characters in words:");
        System.out.println("Result: '" + solution.reverseCharactersInWords(s1) + "'");

        // Test case 3: Multiple spaces
        String s2 = "  hello    world  ";
        System.out.println("\nTest 3 - Multiple spaces:");
        System.out.println("Manual: '" + solution.reverseWordsManual(s2) + "'");
        System.out.println("In-place: '" + solution.reverseWordsInPlace(s2) + "'");

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single word: '" + solution.reverseWords("hello") + "'");
        System.out.println("Leading/trailing spaces: '" + solution.reverseWords("  hello world  ") + "'");
        System.out.println("Single character: '" + solution.reverseWords("a") + "'");

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            sb.append("word").append(i).append(" ");
        }
        long start = System.nanoTime();
        String result = solution.reverseWordsInPlace(sb.toString());
        long end = System.nanoTime();
        System.out
                .println("Result words: " + result.split(" ").length + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
