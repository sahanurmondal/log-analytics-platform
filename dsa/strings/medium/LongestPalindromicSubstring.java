package strings.medium;

/**
 * LeetCode 5: Longest Palindromic Substring
 * https://leetcode.com/problems/longest-palindromic-substring/
 *
 * Description: Given a string s, return the longest palindromic substring in s.
 * 
 * Constraints:
 * - 1 <= s.length <= 1000
 * - s consist of only digits and English letters
 *
 * Follow-up:
 * - Can you solve it in O(n) time using Manacher's algorithm?
 * - What if we need to return all longest palindromes?
 * 
 * Time Complexity: O(n^2) for expand around centers, O(n) for Manacher's
 * Space Complexity: O(1) for expand around centers, O(n) for Manacher's
 * 
 * Algorithm:
 * 1. Expand Around Centers: For each possible center, expand outwards
 * 2. Manacher's Algorithm: Linear time palindrome detection
 * 3. Dynamic Programming: Bottom-up approach
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft, Apple
 */
public class LongestPalindromicSubstring {

    // Main optimized solution - Expand Around Centers
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 2)
            return s;

        int start = 0, maxLen = 0;

        for (int i = 0; i < s.length(); i++) {
            // Check for odd length palindromes
            int len1 = expandAroundCenter(s, i, i);
            // Check for even length palindromes
            int len2 = expandAroundCenter(s, i, i + 1);

            int len = Math.max(len1, len2);
            if (len > maxLen) {
                maxLen = len;
                start = i - (len - 1) / 2;
            }
        }

        return s.substring(start, start + maxLen);
    }

    private int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    // Alternative solution - Dynamic Programming
    public String longestPalindromeDP(String s) {
        int n = s.length();
        boolean[][] dp = new boolean[n][n];
        String result = "";

        // Every single character is a palindrome
        for (int i = 0; i < n; i++) {
            dp[i][i] = true;
            result = s.substring(i, i + 1);
        }

        // Check for 2-character palindromes
        for (int i = 0; i < n - 1; i++) {
            if (s.charAt(i) == s.charAt(i + 1)) {
                dp[i][i + 1] = true;
                result = s.substring(i, i + 2);
            }
        }

        // Check for palindromes of length 3 and more
        for (int len = 3; len <= n; len++) {
            for (int i = 0; i < n - len + 1; i++) {
                int j = i + len - 1;
                if (s.charAt(i) == s.charAt(j) && dp[i + 1][j - 1]) {
                    dp[i][j] = true;
                    result = s.substring(i, j + 1);
                }
            }
        }

        return result;
    }

    // Follow-up optimization - Manacher's Algorithm
    public String longestPalindromeManacher(String s) {
        if (s == null || s.length() == 0)
            return "";

        // Transform string: "abc" -> "^#a#b#c#$"
        StringBuilder transformed = new StringBuilder("^#");
        for (char c : s.toCharArray()) {
            transformed.append(c).append("#");
        }
        transformed.append("$");

        String T = transformed.toString();
        int n = T.length();
        int[] P = new int[n];
        int center = 0, right = 0;

        for (int i = 1; i < n - 1; i++) {
            int mirror = 2 * center - i;

            if (i < right) {
                P[i] = Math.min(right - i, P[mirror]);
            }

            // Try to expand palindrome centered at i
            while (T.charAt(i + (1 + P[i])) == T.charAt(i - (1 + P[i]))) {
                P[i]++;
            }

            // If palindrome centered at i extends past right, adjust center and right
            if (i + P[i] > right) {
                center = i;
                right = i + P[i];
            }
        }

        // Find the longest palindrome
        int maxLen = 0;
        int centerIndex = 0;
        for (int i = 1; i < n - 1; i++) {
            if (P[i] > maxLen) {
                maxLen = P[i];
                centerIndex = i;
            }
        }

        int start = (centerIndex - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }

    public static void main(String[] args) {
        LongestPalindromicSubstring solution = new LongestPalindromicSubstring();

        // Test Case 1: Normal case
        System.out.println(solution.longestPalindrome("babad")); // Expected: "bab" or "aba"

        // Test Case 2: Edge case - even length palindrome
        System.out.println(solution.longestPalindrome("cbbd")); // Expected: "bb"

        // Test Case 3: Corner case - single character
        System.out.println(solution.longestPalindrome("a")); // Expected: "a"

        // Test Case 4: No palindrome longer than 1
        System.out.println(solution.longestPalindrome("abcdef")); // Expected: "a"

        // Test Case 5: Entire string is palindrome
        System.out.println(solution.longestPalindrome("racecar")); // Expected: "racecar"

        // Test Case 6: Empty string
        System.out.println(solution.longestPalindrome("")); // Expected: ""

        // Test Case 7: All same characters
        System.out.println(solution.longestPalindrome("aaaa")); // Expected: "aaaa"

        // Test Case 8: Complex case
        System.out.println(solution.longestPalindrome("abacabad")); // Expected: "abacaba"

        // Test Case 9: Palindrome at start
        System.out.println(solution.longestPalindrome("abaxyz")); // Expected: "aba"

        // Test Case 10: Palindrome at end
        System.out.println(solution.longestPalindrome("xyzaba")); // Expected: "aba"
    }
}
