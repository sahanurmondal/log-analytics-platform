package recursion.hard;

/**
 * LeetCode 44: Wildcard Matching
 * https://leetcode.com/problems/wildcard-matching/
 *
 * Companies: Google, Facebook
 * Frequency: High
 *
 * Description:
 * Implement wildcard pattern matching with support for '?' and '*'.
 *
 * Constraints:
 * - 0 <= s.length, p.length <= 2000
 *
 * Follow-ups:
 * 1. Can you optimize for large strings?
 * 2. Can you return all matching substrings?
 * 3. Can you handle multiple patterns?
 */
public class WildcardMatching {
    public boolean isMatch(String s, String p) {
        int m = s.length(), n = p.length();
        boolean[][] dp = new boolean[m + 1][n + 1];
        dp[0][0] = true;
        for (int j = 1; j <= n; j++)
            if (p.charAt(j - 1) == '*')
                dp[0][j] = dp[0][j - 1];
        for (int i = 1; i <= m; i++)
            for (int j = 1; j <= n; j++) {
                if (p.charAt(j - 1) == '*')
                    dp[i][j] = dp[i][j - 1] || dp[i - 1][j];
                else if (p.charAt(j - 1) == '?' || s.charAt(i - 1) == p.charAt(j - 1))
                    dp[i][j] = dp[i - 1][j - 1];
            }
        return dp[m][n];
    }

    // Follow-up 1: Optimize for large strings (use greedy)
    public boolean isMatchGreedy(String s, String p) {
        int i = 0, j = 0, star = -1, match = 0;
        while (i < s.length()) {
            if (j < p.length() && (p.charAt(j) == '?' || s.charAt(i) == p.charAt(j))) {
                i++;
                j++;
            } else if (j < p.length() && p.charAt(j) == '*') {
                star = j++;
                match = i;
            } else if (star != -1) {
                j = star + 1;
                i = ++match;
            } else
                return false;
        }
        while (j < p.length() && p.charAt(j) == '*')
            j++;
        return j == p.length();
    }

    // Follow-up 2: Return all matching substrings
    public java.util.List<String> allMatchingSubstrings(String s, String p) {
        java.util.List<String> res = new java.util.ArrayList<>();
        for (int i = 0; i < s.length(); i++)
            for (int j = i + 1; j <= s.length(); j++)
                if (isMatch(s.substring(i, j), p))
                    res.add(s.substring(i, j));
        return res;
    }

    // Follow-up 3: Handle multiple patterns
    public boolean isMatchMultiplePatterns(String s, String[] patterns) {
        for (String p : patterns)
            if (isMatch(s, p))
                return true;
        return false;
    }

    public static void main(String[] args) {
        WildcardMatching solution = new WildcardMatching();
        System.out.println(solution.isMatch("aa", "a")); // false
        System.out.println(solution.isMatchGreedy("aa", "*")); // true
        System.out.println(solution.allMatchingSubstrings("abcde", "a*e")); // ["abcde"]
        System.out.println(solution.isMatchMultiplePatterns("abc", new String[] { "a*", "?b*" })); // true
    }
}
