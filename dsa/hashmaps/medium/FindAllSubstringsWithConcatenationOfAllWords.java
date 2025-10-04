package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 30: Substring with Concatenation of All Words
 * https://leetcode.com/problems/substring-with-concatenation-of-all-words/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Hard (Asked in 8+ interviews)
 *
 * Description: You are given a string `s` and an array of strings `words` of
 * the same length. Return all starting indices of substring(s) in `s` that is a
 * concatenation of each word in `words` exactly once, in any order, and without
 * any intervening characters.
 *
 * Constraints:
 * - 1 <= s.length <= 10^4
 * - 1 <= words.length <= 5000
 * - 1 <= words[i].length <= 30
 * - s and words[i] consist of lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. How can you optimize the sliding window approach?
 * 2. What if the words can have different lengths?
 * 3. How does the complexity change with the number of words and their length?
 */
public class FindAllSubstringsWithConcatenationOfAllWords {

    // Approach 1: Sliding Window with HashMap - O((N - L*M) * L) time, O(M*L) space
    // N = s.length, M = words.length, L = words[0].length
    public List<Integer> findSubstring(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || words == null || s.length() == 0 || words.length == 0) {
            return result;
        }

        int wordLen = words[0].length();
        int numWords = words.length;
        int totalLen = wordLen * numWords;

        if (s.length() < totalLen) {
            return result;
        }

        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        for (int i = 0; i <= s.length() - totalLen; i++) {
            String sub = s.substring(i, i + totalLen);
            Map<String, Integer> seen = new HashMap<>();
            int j = 0;
            while (j < totalLen) {
                String word = sub.substring(j, j + wordLen);
                seen.put(word, seen.getOrDefault(word, 0) + 1);
                j += wordLen;
            }

            if (wordCount.equals(seen)) {
                result.add(i);
            }
        }

        return result;
    }

    // Approach 2: Optimized Sliding Window - O(N*L) time, O(M*L) space
    public List<Integer> findSubstringOptimized(String s, String[] words) {
        List<Integer> result = new ArrayList<>();
        if (s == null || words == null || s.length() == 0 || words.length == 0) {
            return result;
        }

        int wordLen = words[0].length();
        int numWords = words.length;
        int totalLen = wordLen * numWords;

        Map<String, Integer> wordCount = new HashMap<>();
        for (String word : words) {
            wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
        }

        for (int i = 0; i < wordLen; i++) {
            int left = i;
            int count = 0;
            Map<String, Integer> seen = new HashMap<>();

            for (int j = i; j <= s.length() - wordLen; j += wordLen) {
                String word = s.substring(j, j + wordLen);

                if (wordCount.containsKey(word)) {
                    seen.put(word, seen.getOrDefault(word, 0) + 1);
                    count++;

                    while (seen.get(word) > wordCount.get(word)) {
                        String leftWord = s.substring(left, left + wordLen);
                        seen.put(leftWord, seen.get(leftWord) - 1);
                        count--;
                        left += wordLen;
                    }

                    if (count == numWords) {
                        result.add(left);
                        String leftWord = s.substring(left, left + wordLen);
                        seen.put(leftWord, seen.get(leftWord) - 1);
                        count--;
                        left += wordLen;
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

    public static void main(String[] args) {
        FindAllSubstringsWithConcatenationOfAllWords solution = new FindAllSubstringsWithConcatenationOfAllWords();

        // Test case 1
        String s1 = "barfoothefoobarman";
        String[] words1 = { "foo", "bar" };
        System.out.println("Result 1: " + solution.findSubstring(s1, words1)); // [0, 9]
        System.out.println("Result 1 (Optimized): " + solution.findSubstringOptimized(s1, words1)); // [0, 9]

        // Test case 2
        String s2 = "wordgoodgoodgoodbestword";
        String[] words2 = { "word", "good", "best", "word" };
        System.out.println("Result 2: " + solution.findSubstring(s2, words2)); // []

        // Test case 3
        String s3 = "barfoofoobarthefoobarman";
        String[] words3 = { "bar", "foo", "the" };
        System.out.println("Result 3: " + solution.findSubstring(s3, words3)); // [6, 9, 12]
    }
}
