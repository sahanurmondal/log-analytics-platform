package backtracking.hard;

import java.util.*;

/**
 * LeetCode 140: Word Break II
 * https://leetcode.com/problems/word-break-ii/
 *
 * Description: Given a string s and a dictionary of strings wordDict, add
 * spaces in s to construct a sentence
 * where each word is a valid dictionary word. Return all such possible
 * sentences in any order.
 * 
 * Constraints:
 * - 1 <= s.length <= 20
 * - 1 <= wordDict.length <= 1000
 * - 1 <= wordDict[i].length <= 10
 *
 * Follow-up:
 * - Can you optimize with memoization?
 * 
 * Time Complexity: O(2^n)
 * Space Complexity: O(2^n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class WordBreakII {

    public List<String> wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Map<String, List<String>> memo = new HashMap<>();
        return backtrack(s, wordSet, memo);
    }

    private List<String> backtrack(String s, Set<String> wordSet, Map<String, List<String>> memo) {
        if (memo.containsKey(s)) {
            return memo.get(s);
        }

        List<String> result = new ArrayList<>();

        if (s.isEmpty()) {
            result.add("");
            return result;
        }

        for (String word : wordSet) {
            if (s.startsWith(word)) {
                String remaining = s.substring(word.length());
                List<String> subResults = backtrack(remaining, wordSet, memo);

                for (String subResult : subResults) {
                    result.add(word + (subResult.isEmpty() ? "" : " " + subResult));
                }
            }
        }

        memo.put(s, result);
        return result;
    }

    // Alternative solution - Without memoization
    public List<String> wordBreakSimple(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        List<String> result = new ArrayList<>();
        backtrackSimple(s, 0, wordSet, new ArrayList<>(), result);
        return result;
    }

    private void backtrackSimple(String s, int start, Set<String> wordSet,
            List<String> current, List<String> result) {
        if (start == s.length()) {
            result.add(String.join(" ", current));
            return;
        }

        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            if (wordSet.contains(word)) {
                current.add(word);
                backtrackSimple(s, end, wordSet, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        WordBreakII solution = new WordBreakII();

        System.out.println(solution.wordBreak("catsanddog", Arrays.asList("cat", "cats", "and", "sand", "dog")));
        // Expected: ["cats and dog","cat sand dog"]

        System.out.println(solution.wordBreak("pineapplepenapple",
                Arrays.asList("apple", "pen", "applepen", "pine", "pineapple")));
        // Expected: ["pine apple pen apple","pineapple pen apple","pine applepen
        // apple"]

        System.out.println(solution.wordBreak("catsandog", Arrays.asList("cats", "dog", "sand", "and", "cat")));
        // Expected: []
    }
}
