package backtracking.medium;

import java.util.*;

/**
 * LeetCode 131: Palindrome Partitioning
 * https://leetcode.com/problems/palindrome-partitioning/
 *
 * Description: Given a string s, partition s such that every substring of the
 * partition is a palindrome.
 * Return all possible palindrome partitioning of s.
 * 
 * Constraints:
 * - 1 <= s.length <= 16
 * - s contains only lowercase English letters
 *
 * Follow-up:
 * - Can you precompute palindrome information?
 * - What about minimum cuts (Palindrome Partitioning II)?
 * 
 * Time Complexity: O(N * 2^N) where N is string length
 * Space Complexity: O(N)
 * 
 * Algorithm:
 * 1. Backtracking: Try all possible partitions
 * 2. Precompute palindromes: Use DP to check palindromes
 * 3. Expand around centers: Check palindromes efficiently
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class PalindromePartitioning {

    // Main optimized solution - Backtracking with palindrome check
    public List<List<String>> partition(String s) {
        List<List<String>> result = new ArrayList<>();
        backtrack(s, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(String s, int start, List<String> current, List<List<String>> result) {
        if (start == s.length()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (isPalindrome(s, start, end)) {
                current.add(s.substring(start, end + 1));
                backtrack(s, end + 1, current, result);
                current.remove(current.size() - 1);
            }
        }
    }

    private boolean isPalindrome(String s, int start, int end) {
        while (start < end) {
            if (s.charAt(start++) != s.charAt(end--)) {
                return false;
            }
        }
        return true;
    }

    // Alternative solution - Precompute palindromes
    public List<List<String>> partitionPrecompute(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];

        // Precompute all palindromes
        for (int i = 0; i < n; i++) {
            for (int j = 0; j <= i; j++) {
                if (s.charAt(i) == s.charAt(j) && (i - j <= 2 || dp[j + 1][i - 1])) {
                    dp[j][i] = true;
                }
            }
        }

        List<List<String>> result = new ArrayList<>();
        backtrackPrecompute(s, 0, new ArrayList<>(), result, dp);
        return result;
    }

    private void backtrackPrecompute(String s, int start, List<String> current,
            List<List<String>> result, boolean[][] dp) {
        if (start == s.length()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            if (dp[start][end]) {
                current.add(s.substring(start, end + 1));
                backtrackPrecompute(s, end + 1, current, result, dp);
                current.remove(current.size() - 1);
            }
        }
    }

    // Follow-up optimization - Expand around centers
    public List<List<String>> partitionExpandCenters(String s) {
        List<List<String>> result = new ArrayList<>();
        Set<String> palindromes = new HashSet<>();

        // Find all palindromes using expand around centers
        for (int i = 0; i < s.length(); i++) {
            expandAroundCenter(s, i, i, palindromes); // Odd length
            expandAroundCenter(s, i, i + 1, palindromes); // Even length
        }

        backtrackWithSet(s, 0, new ArrayList<>(), result, palindromes);
        return result;
    }

    private void expandAroundCenter(String s, int left, int right, Set<String> palindromes) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            palindromes.add(s.substring(left, right + 1));
            left--;
            right++;
        }
    }

    private void backtrackWithSet(String s, int start, List<String> current,
            List<List<String>> result, Set<String> palindromes) {
        if (start == s.length()) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int end = start; end < s.length(); end++) {
            String substring = s.substring(start, end + 1);
            if (palindromes.contains(substring)) {
                current.add(substring);
                backtrackWithSet(s, end + 1, current, result, palindromes);
                current.remove(current.size() - 1);
            }
        }
    }

    public static void main(String[] args) {
        PalindromePartitioning solution = new PalindromePartitioning();

        // Test Case 1: Normal case
        System.out.println(solution.partition("aab")); // Expected: [["a","a","b"],["aa","b"]]

        // Test Case 2: All same characters
        System.out.println(solution.partition("aaa")); // Expected: [["a","a","a"],["a","aa"],["aa","a"],["aaa"]]

        // Test Case 3: Single character
        System.out.println(solution.partition("a")); // Expected: [["a"]]

        // Test Case 4: No palindromes except single chars
        System.out.println(solution.partition("abc")); // Expected: [["a","b","c"]]

        // Test Case 5: Entire string is palindrome
        System.out.println(solution.partition("aba")); // Expected: [["a","b","a"],["aba"]]

        // Test Case 6: Multiple palindromes
        System.out.println(solution.partition("abccba")); // Expected: multiple partitions

        // Test Case 7: Even length palindrome
        System.out.println(solution.partition("abba")); // Expected: [["a","b","b","a"],["a","bb","a"],["abba"]]

        // Test Case 8: Test precompute approach
        System.out.println(solution.partitionPrecompute("aab").size()); // Expected: 2

        // Test Case 9: Complex case
        System.out.println(solution.partition("racecar")); // Expected: multiple including ["racecar"]

        // Test Case 10: Maximum constraint
        System.out.println(solution.partition("aaaaaaaaaaaaaaaa").size()); // Expected: 2^15 = 32768
    }
}
