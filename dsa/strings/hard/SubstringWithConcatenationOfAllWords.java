package strings.hard;

import java.util.*;

/**
 * LeetCode 30: Substring with Concatenation of All Words
 * https://leetcode.com/problems/substring-with-concatenation-of-all-words/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given a string s and an array of strings words, return all
 * starting indices of substrings that are concatenations of each word exactly
 * once.
 *
 * Constraints:
 * - 1 <= s.length <= 10^4
 * - 0 <= words.length <= 5000
 * - 1 <= words[i].length <= 30
 * 
 * Follow-up Questions:
 * 1. Can you handle overlapping words?
 * 2. Can you find partial matches?
 * 3. Can you optimize for large inputs?
 */
public class SubstringWithConcatenationOfAllWords {

    // Approach 1: Sliding window + hashmap - O(n * m) time
    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || words == null || words.length == 0)
            return result;

        int wordLen = words[0].length();
        int totalLen = wordLen * words.length;
        if (s.length() < totalLen)
            return result;

        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        for (int i = 0; i < wordLen; i++) {
            int left = i, count = 0;
            Map<String, Integer> seen = new HashMap<>();

            for (int j = i; j <= s.length() - wordLen; j += wordLen) {
                String word = s.substring(j, j + wordLen);

                if (wordCount.containsKey(word)) {
                    seen.put(word, seen.getOrDefault(word, 0) + 1);
                    count++;

                    while (seen.get(word) > wordCount.get(word)) {
                        String leftWord = s.substring(left, left + wordLen);
                        seen.put(leftWord, seen.get(leftWord) - 1);
                        left += wordLen;
                        count--;
                    }

                    if (count == words.length) {
                        result.add(left);
                        String leftWord = s.substring(left, left + wordLen);
                        seen.put(leftWord, seen.get(leftWord) - 1);
                        left += wordLen;
                        count--;
                    }
                } else {
                    seen.clear();
                    count = 0;
                    left = j + wordLen;
                }
            }
        }

        return result;
    }

    // Follow-up 1: Handle overlapping words
    public List<Integer> findSubstringWithOverlap(String s, String[] words) {
        // For overlapping words, we need to check all possible starting positions
        List<Integer> result = new ArrayList<>();
        if (s == null || words == null || words.length == 0)
            return result;

        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        int wordLen = words[0].length();
        int totalLen = wordLen * words.length;

        for (int i = 0; i <= s.length() - totalLen; i++) {
            Map<String, Integer> seen = new HashMap<>();
            int j = 0;

            while (j < words.length) {
                String word = s.substring(i + j * wordLen, i + (j + 1) * wordLen);
                if (!wordCount.containsKey(word))
                    break;

                seen.put(word, seen.getOrDefault(word, 0) + 1);
                if (seen.get(word) > wordCount.get(word))
                    break;
                j++;
            }

            if (j == words.length)
                result.add(i);
        }

        return result;
    }

    // Follow-up 2: Find partial matches (at least k words)
    public List<Integer> findPartialMatches(String s, String[] words, int k) {
        List<Integer> result = new ArrayList<>();
        if (s == null || words == null || words.length == 0 || k <= 0)
            return result;

        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        int wordLen = words[0].length();
        int minLen = wordLen * k;

        for (int i = 0; i <= s.length() - minLen; i++) {
            Map<String, Integer> seen = new HashMap<>();
            int matches = 0;

            for (int j = 0; j < words.length && i + (j + 1) * wordLen <= s.length(); j++) {
                String word = s.substring(i + j * wordLen, i + (j + 1) * wordLen);
                if (wordCount.containsKey(word)) {
                    seen.put(word, seen.getOrDefault(word, 0) + 1);
                    if (seen.get(word) <= wordCount.get(word)) {
                        matches++;
                    }
                }
            }

            if (matches >= k)
                result.add(i);
        }

        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        SubstringWithConcatenationOfAllWords solution = new SubstringWithConcatenationOfAllWords();

        // Test case 1: Basic case
        String s1 = "barfoothefoobarman";
        String[] words1 = { "foo", "bar" };
        System.out.println("Test 1 - s: " + s1 + ", words: " + Arrays.toString(words1));
        System.out.println("Result: " + solution.findSubstring(s1, words1));

        // Test case 2: With overlapping
        System.out.println("\nTest 2 - With overlapping:");
        System.out.println("Result: " + solution.findSubstringWithOverlap(s1, words1));

        // Test case 3: Partial matches (at least 1 word)
        System.out.println("\nTest 3 - Partial matches (k=1):");
        System.out.println("Result: " + solution.findPartialMatches(s1, words1, 1));

        // Test case 4: Repeated words
        String s2 = "barfoobar";
        String[] words2 = { "bar", "foo", "bar" };
        System.out.println("\nTest 4 - Repeated words:");
        System.out.println("Result: " + solution.findSubstring(s2, words2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty string: " + solution.findSubstring("", words1));
        System.out.println("Empty words: " + solution.findSubstring(s1, new String[] {}));
        System.out.println("Single word: " + solution.findSubstring("foobar", new String[] { "foo" }));
        System.out.println("No match: " + solution.findSubstring("abcd", new String[] { "xy", "z" }));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++)
            sb.append("ab");
        String[] largeWords = new String[100];
        Arrays.fill(largeWords, "ab");
        long start = System.nanoTime();
        List<Integer> result = solution.findSubstring(sb.toString(), largeWords);
        long end = System.nanoTime();
        System.out.println("Result size: " + result.size() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
