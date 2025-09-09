package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 2062: Count Vowel Substrings of a String
 * https://leetcode.com/problems/count-vowel-substrings-of-a-string/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a string word, return the number of substrings that
 * contain all five vowels.
 *
 * Constraints:
 * - 1 <= word.length <= 100
 * - word consists of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you count substrings with at least k vowels?
 * 2. Can you find the longest/shortest such substring?
 * 3. Can you optimize for large strings?
 */
public class CountVowelSubstrings {

    // Approach 1: Sliding window + set
    public int countVowelSubstrings(String word) {
        int n = word.length(), res = 0;
        Set<Character> vowels = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
        for (int i = 0; i < n; i++) {
            Set<Character> seen = new HashSet<>();
            for (int j = i; j < n; j++) {
                char c = word.charAt(j);
                if (!vowels.contains(c))
                    break;
                seen.add(c);
                if (seen.size() == 5)
                    res++;
            }
        }
        return res;
    }

    // Follow-up 1: Count substrings with at least k vowels
    public int countVowelSubstringsAtLeastK(String word, int k) {
        int n = word.length(), res = 0;
        Set<Character> vowels = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
        for (int i = 0; i < n; i++) {
            Set<Character> seen = new HashSet<>();
            for (int j = i; j < n; j++) {
                char c = word.charAt(j);
                if (!vowels.contains(c))
                    break;
                seen.add(c);
                if (seen.size() >= k)
                    res++;
            }
        }
        return res;
    }

    // Follow-up 2: Longest vowel substring
    public int longestVowelSubstring(String word) {
        int n = word.length(), maxLen = 0;
        Set<Character> vowels = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
        for (int i = 0; i < n; i++) {
            Set<Character> seen = new HashSet<>();
            for (int j = i; j < n; j++) {
                char c = word.charAt(j);
                if (!vowels.contains(c))
                    break;
                seen.add(c);
                if (seen.size() == 5)
                    maxLen = Math.max(maxLen, j - i + 1);
            }
        }
        return maxLen == 0 ? -1 : maxLen;
    }

    // Follow-up 3: Shortest vowel substring
    public int shortestVowelSubstring(String word) {
        int n = word.length(), minLen = Integer.MAX_VALUE;
        Set<Character> vowels = new HashSet<>(Arrays.asList('a', 'e', 'i', 'o', 'u'));
        for (int i = 0; i < n; i++) {
            Set<Character> seen = new HashSet<>();
            for (int j = i; j < n; j++) {
                char c = word.charAt(j);
                if (!vowels.contains(c))
                    break;
                seen.add(c);
                if (seen.size() == 5)
                    minLen = Math.min(minLen, j - i + 1);
            }
        }
        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CountVowelSubstrings solution = new CountVowelSubstrings();

        // Test case 1: Basic case
        String word1 = "aeiouu";
        System.out.println("Test 1 - word: " + word1 + " Expected: 2");
        System.out.println("Result: " + solution.countVowelSubstrings(word1));

        // Test case 2: No vowel substring
        String word2 = "abc";
        System.out.println("\nTest 2 - No vowel substring:");
        System.out.println("Result: " + solution.countVowelSubstrings(word2));

        // Test case 3: At least k vowels
        System.out.println("\nTest 3 - At least 3 vowels:");
        System.out.println(solution.countVowelSubstringsAtLeastK(word1, 3));

        // Test case 4: Longest vowel substring
        System.out.println("\nTest 4 - Longest vowel substring:");
        System.out.println(solution.longestVowelSubstring(word1));

        // Test case 5: Shortest vowel substring
        System.out.println("\nTest 5 - Shortest vowel substring:");
        System.out.println(solution.shortestVowelSubstring(word1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty string: " + solution.countVowelSubstrings(""));
        System.out.println("Single char: " + solution.countVowelSubstrings("a"));
        System.out.println("All vowels: " + solution.countVowelSubstrings("aeiou"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++)
            sb.append("aeiou");
        long start = System.nanoTime();
        int result = solution.countVowelSubstrings(sb.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
