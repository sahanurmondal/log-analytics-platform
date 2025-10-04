package dp.advanced;

import java.util.*;

/**
 * LeetCode 139: Word Break
 * https://leetcode.com/problems/word-break/
 *
 * Description:
 * Given a string s and a dictionary of strings wordDict, return true if s can
 * be segmented into a space-separated sequence of one or more dictionary words.
 *
 * Constraints:
 * - 1 <= s.length <= 300
 * - 1 <= wordDict.length <= 1000
 * - 1 <= wordDict[i].length <= 20
 * - s and wordDict[i] consist of only lowercase English letters.
 *
 * Follow-up:
 * - Can you solve it in O(n^2) time?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class WordBreak {

    // Approach 1: DP with HashSet - O(n^3) time, O(n) space
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

    // Approach 2: DP with Length Optimization - O(n^2*m) time, O(n) space
    public boolean wordBreakOptimized(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        int maxLen = wordDict.stream().mapToInt(String::length).max().orElse(0);

        boolean[] dp = new boolean[s.length() + 1];
        dp[0] = true;

        for (int i = 1; i <= s.length(); i++) {
            for (int j = Math.max(0, i - maxLen); j < i; j++) {
                if (dp[j] && wordSet.contains(s.substring(j, i))) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[s.length()];
    }

    // Approach 3: Memoization - O(n^3) time, O(n) space
    public boolean wordBreakMemo(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Boolean[] memo = new Boolean[s.length()];
        return wordBreakMemoHelper(s, 0, wordSet, memo);
    }

    private boolean wordBreakMemoHelper(String s, int start, Set<String> wordSet, Boolean[] memo) {
        if (start == s.length())
            return true;

        if (memo[start] != null)
            return memo[start];

        for (int end = start + 1; end <= s.length(); end++) {
            String word = s.substring(start, end);
            if (wordSet.contains(word) && wordBreakMemoHelper(s, end, wordSet, memo)) {
                memo[start] = true;
                return true;
            }
        }

        memo[start] = false;
        return false;
    }

    // Approach 4: BFS - O(n^3) time, O(n) space
    public boolean wordBreakBFS(String s, List<String> wordDict) {
        Set<String> wordSet = new HashSet<>(wordDict);
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[s.length()];

        queue.offer(0);

        while (!queue.isEmpty()) {
            int start = queue.poll();
            if (visited[start])
                continue;
            visited[start] = true;

            for (int end = start + 1; end <= s.length(); end++) {
                if (wordSet.contains(s.substring(start, end))) {
                    if (end == s.length())
                        return true;
                    queue.offer(end);
                }
            }
        }

        return false;
    }

    public static void main(String[] args) {
        WordBreak solution = new WordBreak();

        System.out.println("=== Word Break Test Cases ===");

        // Test Case 1: Normal case
        List<String> dict1 = Arrays.asList("leet", "code");
        System.out.println("Test 1 - s: \"leetcode\", dict: " + dict1);
        System.out.println("DP: " + solution.wordBreak("leetcode", dict1));
        System.out.println("Optimized: " + solution.wordBreakOptimized("leetcode", dict1));
        System.out.println("Memoization: " + solution.wordBreakMemo("leetcode", dict1));
        System.out.println("BFS: " + solution.wordBreakBFS("leetcode", dict1));
        System.out.println("Expected: true\n");

        // Test Case 2: No solution
        List<String> dict2 = Arrays.asList("cats", "dog", "sand", "and", "cat");
        System.out.println("Test 2 - s: \"applepenapple\", dict: " + dict2);
        System.out.println("DP: " + solution.wordBreak("applepenapple", dict2));
        System.out.println("Expected: false\n");

        // Test Case 3: Single word
        List<String> dict3 = Arrays.asList("apple");
        System.out.println("Test 3 - s: \"apple\", dict: " + dict3);
        System.out.println("DP: " + solution.wordBreak("apple", dict3));
        System.out.println("Expected: true\n");

        // Test Case 4: Empty string
        System.out.println("Test 4 - s: \"\", dict: " + Arrays.asList("a"));
        System.out.println("DP: " + solution.wordBreak("", Arrays.asList("a")));
        System.out.println("Expected: true\n");

        performanceTest();
    }

    private static void performanceTest() {
        WordBreak solution = new WordBreak();

        // Create large test case
        String s = "a".repeat(100);
        List<String> dict = Arrays.asList("a", "aa", "aaa", "aaaa");

        System.out.println("=== Performance Test (String length: " + s.length() + ") ===");

        long start = System.nanoTime();
        boolean result1 = solution.wordBreak(s, dict);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result2 = solution.wordBreakOptimized(s, dict);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
