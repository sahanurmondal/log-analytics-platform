package arrays.medium;

import java.util.*;

/**
 * LeetCode 139: Word Break
 * https://leetcode.com/problems/word-break/
 *
 * Description:
 * Given a string s and a dictionary of strings wordDict, return true if s can
 * be segmented into
 * a space-separated sequence of one or more dictionary words.
 *
 * Constraints:
 * - 1 <= s.length <= 300
 * - 1 <= wordDict.length <= 1000
 * - 1 <= wordDict[i].length <= 20
 * - s and wordDict[i] consist of only lowercase English letters
 * - All strings of wordDict are unique
 *
 * Follow-up:
 * - Can you solve it using BFS?
 * 
 * Time Complexity: O(n^2 + m*k) where n = s.length, m = wordDict.length, k =
 * avg word length
 * Space Complexity: O(n + m*k)
 * 
 * Algorithm:
 * 1. Use dynamic programming with boolean array
 * 2. For each position, check if any word from dict can end there
 * 3. Use set for O(1) word lookup
 */
public class WordBreak {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;

        for (int i = 1; i <= s.length(); i++) {
            for (int j = 0; j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[s.length()];
    }

    public static void main(String[] args) {
        WordBreak solution = new WordBreak();

        // Test Case 1: Normal case
        System.out.println(solution.wordBreak("leetcode", Arrays.asList("leet", "code"))); // Expected: true

        // Test Case 2: Edge case - impossible
        System.out.println(solution.wordBreak("catsandog", Arrays.asList("cats", "dog", "sand", "and", "cat"))); // Expected:
                                                                                                                 // false

        // Test Case 3: Corner case - single word
        System.out.println(solution.wordBreak("applepenapple", Arrays.asList("apple", "pen"))); // Expected: true

        // Test Case 4: Large input - repeated pattern
        System.out.println(solution.wordBreak("aaaaaaa", Arrays.asList("aaaa", "aa"))); // Expected: false

        // Test Case 5: Minimum input
        System.out.println(solution.wordBreak("a", Arrays.asList("a"))); // Expected: true

        // Test Case 6: Special case - overlapping words
        System.out.println(solution.wordBreak("cars", Arrays.asList("car", "ca", "rs"))); // Expected: true

        // Test Case 7: Boundary case - empty dict word
        System.out.println(solution.wordBreak("ab", Arrays.asList("a", "b"))); // Expected: true

        // Test Case 8: No match
        System.out.println(solution.wordBreak("abcd", Arrays.asList("a", "abc"))); // Expected: false

        // Test Case 9: Exact match
        System.out.println(solution.wordBreak("hello", Arrays.asList("hello"))); // Expected: true

        // Test Case 10: Multiple valid segmentations
        System.out.println(solution.wordBreak("abab", Arrays.asList("ab", "abab"))); // Expected: true
    }
}
