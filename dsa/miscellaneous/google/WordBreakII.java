package miscellaneous.google;

import java.util.*;
import java.util.stream.Collectors;

/**
 * LeetCode 140: Word Break II
 * https://leetcode.com/problems/word-break-ii/
 *
 * Description:
 * Given a string s and a dictionary of words, add spaces in s to construct a
 * sentence
 * where each word is a valid dictionary word. Return all such possible
 * sentences in any order.
 * 
 * Company: Google
 * Difficulty: Hard
 * Asked: Frequently in 2023-2024
 * 
 * Constraints:
 * - 1 <= s.length <= 20
 * - 1 <= wordDict.length <= 1000
 * - 1 <= wordDict[i].length <= 10
 * - s and wordDict[i] consist of only lowercase English letters
 * - All the strings of wordDict are unique
 */
public class WordBreakII {

    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Map<String, List<String>> memo = new HashMap<>();
        return wordBreakHelper(s, wordSet, memo);
    }

    private List<String> wordBreakHelper(String s, Set<String> wordSet, Map<String, List<String>> memo) {
        if (memo.containsKey(s)) {
            return memo.get(s);
        }

        List<String> result = new ArrayList<>();
        if (s.isEmpty()) {
            result.add("");
            return result;
        }

        for (int i = 1; i <= s.length(); i++) {
            String prefix = s.substring(0, i);
            if (wordSet.contains(prefix)) {
                List<String> suffixes = wordBreakHelper(s.substring(i), wordSet, memo);
                for (String suffix : suffixes) {
                    result.add(prefix + (suffix.isEmpty() ? "" : " " + suffix));
                }
            }
        }

        memo.put(s, result);
        return result;
    }

    public static void main(String[] args) {
        WordBreakII solution = new WordBreakII();

        // Test case 1
        List<String> wordDict1 = Arrays.asList("cat", "cats", "and", "sand", "dog");
        System.out.println(solution.wordBreak("catsanddog", wordDict1));
        // Output: ["cats and dog","cat sand dog"]

        // Test case 2
        List<String> wordDict2 = Arrays.asList("apple", "pen", "applepen", "pine", "pineapple");
        System.out.println(solution.wordBreak("pineapplepenapple", wordDict2));
        // Output: ["pine apple pen apple","pineapple pen apple","pine applepen apple"]
    }
}
