package tries.hard;

import java.util.List;

/**
 * LeetCode 472: Concatenated Words
 * https://leetcode.com/problems/concatenated-words/
 *
 * Description:
 * Given an array of strings words (without duplicates), return all the
 * concatenated words in the given list of words.
 * A concatenated word is defined as a string that is comprised entirely of at
 * least two shorter words in the given array.
 *
 * Constraints:
 * - 1 <= words.length <= 10^4
 * - 1 <= words[i].length <= 30
 * - words[i] consists of only lowercase English letters
 * - All the strings of words are unique
 *
 * Follow-up:
 * - Can you solve it using a trie?
 * - Can you optimize using dynamic programming?
 * - Can you handle very long concatenated words efficiently?
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * LeetCode 472: Concatenated Words
 * https://leetcode.com/problems/concatenated-words/
 *
 * Description:
 * Given an array of strings words (without duplicates), return all the
 * concatenated words in the given list of words.
 * A concatenated word is defined as a string that is comprised entirely of at
 * least two shorter words in the given array.
 *
 * Constraints:
 * - 1 <= words.length <= 10^4
 * - 1 <= words[i].length <= 30
 * - words[i] consists of only lowercase English letters
 * - All the strings of words are unique
 *
 * Follow-up:
 * - Can you solve it using a trie?
 * - Can you optimize using dynamic programming?
 * - Can you handle very long concatenated words efficiently?
 */
public class ConcatenatedWords {
    public List<String> findAllConcatenatedWordsInADict(String[] words) {
        List<String> result = new ArrayList<>();
        Set<String> dict = new HashSet<>(Arrays.asList(words));
        for (String word : words) {
            if (isConcatenated(word, dict)) {
                result.add(word);
            }
        }
        return result;
    }

    private boolean isConcatenated(String word, Set<String> dict) {
        if (word.isEmpty()) {
            return false;
        }
        int n = word.length();
        boolean[] dp = new boolean[n + 1];
        dp[0] = true;
        for (int i = 1; i <= n; i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j]) {
                    String sub = word.substring(j, i);
                    if (dict.contains(sub)) {
                        if (sub.length() != n) { // Not the word itself
                            dp[i] = true;
                            break;
                        }
                    }
                }
            }
        }
        return dp[n];
    }

    public static void main(String[] args) {
        ConcatenatedWords solution = new ConcatenatedWords();

        System.out.println(solution.findAllConcatenatedWordsInADict(new String[] { "cat", "cats", "catsdogcats", "dog",
                "dogcatsdog", "hippopotamuses", "rat", "ratcatdogcat" }));
        // ["catsdogcats","dogcatsdog","ratcatdogcat"]

        System.out.println(solution.findAllConcatenatedWordsInADict(new String[] { "cat", "dog", "catdog" }));
        // ["catdog"]

        // Edge Case: No concatenated words
        System.out.println(solution.findAllConcatenatedWordsInADict(new String[] { "cat", "dog", "mouse" })); // []

        // Edge Case: All words are concatenated
        System.out.println(solution.findAllConcatenatedWordsInADict(new String[] { "a", "aa", "aaa", "aaaa" })); // ["aa","aaa","aaaa"]

        // Edge Case: Single word
        System.out.println(solution.findAllConcatenatedWordsInADict(new String[] { "word" })); // []

        // Edge Case: Complex concatenation
        System.out.println(solution.findAllConcatenatedWordsInADict(new String[] { "a", "b", "ab", "abc", "abcd" })); // ["ab"]
    }
}
