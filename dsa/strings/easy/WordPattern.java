package strings.easy;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * LeetCode 290: Word Pattern
 *
 * Given a pattern and a string s, find if s follows the same pattern.
 * Here follow means a full match, such that there is a bijection between a letter in pattern
 * and a non-empty word in s. Specifically:
 * - Each letter in pattern must map to exactly one unique word in s.
 * - Each unique word in s must map to exactly one letter in pattern.
 *
 * Example 1:
 * Input: pattern = "abba", s = "redbluebluered"
 * Output: true
 *
 * Example 2:
 * Input: pattern = "aaaa", s = "asdasdasdasd"
 * Output: true
 *
 * Example 3:
 * Input: pattern = "abab", s = "redblueredblue"
 * Output: true
 */
public class WordPattern {

    /**
     * Solution: Two Hash Maps
     * Time: O(n + m), Space: O(1) - max 26 letters and words
     *
     * Similar to IsomorphicStrings, we need bidirectional mapping
     * pattern[i] <-> words[i]
     */
    public boolean wordPattern(String pattern, String s) {
        String[] words = s.split(" ");

        if (pattern.length() != words.length) {
            return false;
        }

        Map<Character, String> charToWord = new HashMap<>();
        Map<String, Character> wordToChar = new HashMap<>();

        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            String word = words[i];

            // Check char -> word mapping
            if (charToWord.containsKey(c)) {
                if (!charToWord.get(c).equals(word)) {
                    return false;
                }
            } else {
                charToWord.put(c, word);
            }

            // Check word -> char mapping (ensure one-to-one)
            if (wordToChar.containsKey(word)) {
                if (wordToChar.get(word) != c) {
                    return false;
                }
            } else {
                wordToChar.put(word, c);
            }
        }

        return true;
    }

    /**
     * Alternative: Single Map with transform
     * Time: O(n + m), Space: O(n + m)
     */
    public boolean wordPatternV2(String pattern, String s) {
        String[] words = s.split(" ");

        if (pattern.length() != words.length) {
            return false;
        }

        // Transform pattern to IDs
        String patternTransform = transform(pattern);

        // Transform words to IDs
        String wordsTransform = transform(words);

        return patternTransform.equals(wordsTransform);
    }

    private String transform(String s) {
        Map<Character, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int id = 0;

        for (char c : s.toCharArray()) {
            if (!map.containsKey(c)) {
                map.put(c, id++);
            }
            sb.append(map.get(c)).append("#");
        }

        return sb.toString();
    }

    private String transform(String[] words) {
        Map<String, Integer> map = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        int id = 0;

        for (String word : words) {
            if (!map.containsKey(word)) {
                map.put(word, id++);
            }
            sb.append(map.get(word)).append("#");
        }

        return sb.toString();
    }

    public static void main(String[] args) {
        WordPattern solution = new WordPattern();

        // Test case 1
        System.out.println(solution.wordPattern("abba", "redbluebluered")); // true

        // Test case 2
        System.out.println(solution.wordPattern("aaaa", "asdasdasdasd")); // true

        // Test case 3
        System.out.println(solution.wordPattern("abab", "redblueredblue")); // true

        // Test case 4
        System.out.println(solution.wordPattern("abb", "foo bar bar")); // true

        // Test case 5
        System.out.println(solution.wordPattern("aaa", "aa aa aa aa")); // false
    }
}

