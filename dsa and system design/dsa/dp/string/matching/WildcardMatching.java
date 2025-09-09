package dp.string.matching;

import java.util.*;

/**
 * LeetCode 44: Wildcard Matching
 * https://leetcode.com/problems/wildcard-matching/
 *
 * Description:
 * Given an input string (s) and a pattern (p), implement wildcard pattern
 * matching with support for '?' and '*' where:
 * - '?' Matches any single character.
 * - '*' Matches any sequence of characters (including the empty sequence).
 * The matching should cover the entire input string (not partial).
 *
 * Constraints:
 * - 0 <= s.length, p.length <= 2000
 * - s contains only lowercase English letters.
 * - p contains only lowercase English letters, '?' or '*'.
 *
 * Follow-up:
 * - Can you solve it in O(m+n) time?
 * - What if we need to find the matching substring?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Hard
 */
public class WildcardMatching {

    // Approach 1: Recursive with Memoization - O(m*n) time, O(m*n) space
    public boolean isMatch(String s, String p) {
        Boolean[][] memo = new Boolean[s.length() + 1][p.length() + 1];
        return isMatchHelper(s, p, 0, 0, memo);
    }

    private boolean isMatchHelper(String s, String p, int i, int j, Boolean[][] memo) {
        if (memo[i][j] != null)
            return memo[i][j];

        if (j == p.length()) {
            memo[i][j] = (i == s.length());
            return memo[i][j];
        }

        if (i == s.length()) {
            // Check if remaining pattern is all '*'
            for (int k = j; k < p.length(); k++) {
                if (p.charAt(k) != '*') {
                    memo[i][j] = false;
                    return false;
                }
            }
            memo[i][j] = true;
            return true;
        }

        boolean result;
        if (p.charAt(j) == '*') {
            // Three choices: skip *, match one char, match multiple chars
            result = isMatchHelper(s, p, i, j + 1, memo) || // Skip *
                    isMatchHelper(s, p, i + 1, j + 1, memo) || // Match one char
                    isMatchHelper(s, p, i + 1, j, memo); // Match multiple chars
        } else if (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i)) {
            result = isMatchHelper(s, p, i + 1, j + 1, memo);
        } else {
            result = false;
        }

        memo[i][j] = result;
        return result;
    }

    // Approach 2: Bottom-up DP - O(m*n) time, O(m*n) space
    public boolean isMatchDP(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        dp[0][0] = true;

        // Handle patterns like a*b*c*
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '*') {
                    dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
                } else if (p.charAt(j - 1) == '?' || p.charAt(j - 1) == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }

        return dp[m][n];
    }

    // Approach 3: Space Optimized DP - O(m*n) time, O(n) space
    public boolean isMatchOptimized(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[] prev = new boolean[n + 1];
        boolean[] curr = new boolean[n + 1];

        prev[0] = true;

        // Handle patterns like a*b*c*
        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                prev[j] = prev[j - 1];
            }
        }

        for (int i = 1; i <= m; i++) {
            curr[0] = false;

            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '*') {
                    curr[j] = prev[j] || curr[j - 1];
                } else if (p.charAt(j - 1) == '?' || p.charAt(j - 1) == s.charAt(i - 1)) {
                    curr[j] = prev[j - 1];
                } else {
                    curr[j] = false;
                }
            }

            boolean[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n];
    }

    // Approach 4: Two Pointers - O(m*n) worst case, O(1) space
    public boolean isMatchTwoPointers(String s, String p) {
        int sIndex = 0, pIndex = 0;
        int starIndex = -1, match = 0;

        while (sIndex < s.length()) {
            // If characters match or pattern has '?'
            if (pIndex < p.length() && (p.charAt(pIndex) == '?' || s.charAt(sIndex) == p.charAt(pIndex))) {
                sIndex++;
                pIndex++;
            }
            // If pattern character is '*'
            else if (pIndex < p.length() && p.charAt(pIndex) == '*') {
                starIndex = pIndex;
                match = sIndex;
                pIndex++;
            }
            // If no match, but we have seen '*' before, backtrack
            else if (starIndex != -1) {
                pIndex = starIndex + 1;
                match++;
                sIndex = match;
            }
            // No match and no '*' to backtrack
            else {
                return false;
            }
        }

        // Skip any trailing '*' in pattern
        while (pIndex < p.length() && p.charAt(pIndex) == '*') {
            pIndex++;
        }

        return pIndex == p.length();
    }

    // Approach 5: Get Matching Substring - O(m*n) time, O(m*n) space
    public String getMatchingSubstring(String s, String p) {
        if (!isMatch(s, p))
            return "";

        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];

        dp[0][0] = true;

        for (int j = 1; j <= n; j++) {
            if (p.charAt(j - 1) == '*') {
                dp[0][j] = dp[0][j - 1];
            }
        }

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '*') {
                    dp[i][j] = dp[i - 1][j] || dp[i][j - 1];
                } else if (p.charAt(j - 1) == '?' || p.charAt(j - 1) == s.charAt(i - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                }
            }
        }

        // Reconstruct matching
        StringBuilder result = new StringBuilder();
        int i = m, j = n;

        while (i > 0 && j > 0) {
            if (p.charAt(j - 1) == '*') {
                if (dp[i - 1][j]) {
                    result.append(s.charAt(i - 1));
                    i--;
                } else {
                    j--;
                }
            } else if (p.charAt(j - 1) == '?' || p.charAt(j - 1) == s.charAt(i - 1)) {
                result.append(s.charAt(i - 1));
                i--;
                j--;
            }
        }

        return result.reverse().toString();
    }

    public static void main(String[] args) {
        WildcardMatching solution = new WildcardMatching();

        System.out.println("=== Wildcard Matching Test Cases ===");

        // Test Case 1: Basic example
        String s1 = "aa", p1 = "a";
        System.out.println("Test 1 - s: \"" + s1 + "\", p: \"" + p1 + "\"");
        System.out.println("Recursive: " + solution.isMatch(s1, p1));
        System.out.println("DP: " + solution.isMatchDP(s1, p1));
        System.out.println("Optimized: " + solution.isMatchOptimized(s1, p1));
        System.out.println("Two Pointers: " + solution.isMatchTwoPointers(s1, p1));
        System.out.println("Expected: false\n");

        // Test Case 2: With star
        String s2 = "aa", p2 = "*";
        System.out.println("Test 2 - s: \"" + s2 + "\", p: \"" + p2 + "\"");
        System.out.println("Recursive: " + solution.isMatch(s2, p2));
        System.out.println("Matching substring: \"" + solution.getMatchingSubstring(s2, p2) + "\"");
        System.out.println("Expected: true\n");

        // Test Case 3: Complex pattern
        String s3 = "cb", p3 = "?a";
        System.out.println("Test 3 - s: \"" + s3 + "\", p: \"" + p3 + "\"");
        System.out.println("Two Pointers: " + solution.isMatchTwoPointers(s3, p3));
        System.out.println("Expected: false\n");

        // Test Case 4: More complex
        String s4 = "adceb", p4 = "*a*b*";
        System.out.println("Test 4 - s: \"" + s4 + "\", p: \"" + p4 + "\"");
        System.out.println("Two Pointers: " + solution.isMatchTwoPointers(s4, p4));
        System.out.println("Expected: true\n");

        performanceTest();
    }

    private static void performanceTest() {
        WildcardMatching solution = new WildcardMatching();

        String s = "a".repeat(1000);
        String p = "*" + "a".repeat(500) + "*";

        System.out.println("=== Performance Test (s length: " + s.length() + ", p length: " + p.length() + ") ===");

        long start = System.nanoTime();
        boolean result1 = solution.isMatchDP(s, p);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result2 = solution.isMatchOptimized(s, p);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result3 = solution.isMatchTwoPointers(s, p);
        end = System.nanoTime();
        System.out.println("Two Pointers: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
