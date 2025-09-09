package strings.hard;

import java.util.*;

/**
 * LeetCode 5: Longest Palindromic Substring (Hard Variations)
 * https://leetcode.com/problems/longest-palindromic-substring/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook
 * Frequency: Very High
 *
 * Description: Given a string s, return the longest palindromic substring in s.
 *
 * Constraints:
 * - 1 <= s.length <= 1000
 * - s consist of only digits and English letters
 * 
 * Follow-up Questions:
 * 1. Can you find all longest palindromic substrings?
 * 2. Can you count total palindromic substrings?
 * 3. Can you handle case-insensitive palindromes?
 */
public class LongestPalindromicSubstringHard {

    // Approach 1: Expand around centers - O(n^2) time, O(1) space
    public String longestPalindrome(String s) {
        if (s == null || s.length() < 1)
            return "";
        int start = 0, end = 0;
        for (int i = 0; i < s.length(); i++) {
            int len1 = expandAroundCenter(s, i, i);
            int len2 = expandAroundCenter(s, i, i + 1);
            int len = Math.max(len1, len2);
            if (len > end - start) {
                start = i - (len - 1) / 2;
                end = i + len / 2;
            }
        }
        return s.substring(start, end + 1);
    }

    // Approach 2: Manacher's Algorithm - O(n) time, O(n) space
    public String longestPalindromeManacher(String s) {
        String processed = preprocessString(s);
        int n = processed.length();
        int[] P = new int[n];
        int center = 0, right = 0;
        int maxLen = 0, centerIndex = 0;

        for (int i = 0; i < n; i++) {
            int mirror = 2 * center - i;
            if (i < right)
                P[i] = Math.min(right - i, P[mirror]);

            try {
                while (processed.charAt(i + (1 + P[i])) == processed.charAt(i - (1 + P[i]))) {
                    P[i]++;
                }
            } catch (StringIndexOutOfBoundsException e) {
            }

            if (i + P[i] > right) {
                center = i;
                right = i + P[i];
            }

            if (P[i] > maxLen) {
                maxLen = P[i];
                centerIndex = i;
            }
        }

        int start = (centerIndex - maxLen) / 2;
        return s.substring(start, start + maxLen);
    }

    // Follow-up 1: Find all longest palindromic substrings
    public List<String> findAllLongestPalindromes(String s) {
        List<String> result = new ArrayList<>();
        String longest = longestPalindrome(s);
        int maxLen = longest.length();

        for (int i = 0; i <= s.length() - maxLen; i++) {
            String sub = s.substring(i, i + maxLen);
            if (isPalindrome(sub))
                result.add(sub);
        }
        return result;
    }

    // Follow-up 2: Count total palindromic substrings
    public int countPalindromicSubstrings(String s) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            count += expandAroundCenter(s, i, i);
            count += expandAroundCenter(s, i, i + 1);
        }
        return count;
    }

    // Follow-up 3: Case-insensitive palindromes
    public String longestPalindromeIgnoreCase(String s) {
        return longestPalindrome(s.toLowerCase());
    }

    // Helper methods
    private int expandAroundCenter(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 1;
    }

    private String preprocessString(String s) {
        StringBuilder sb = new StringBuilder("^#");
        for (char c : s.toCharArray()) {
            sb.append(c).append('#');
        }
        return sb.append('$').toString();
    }

    private boolean isPalindrome(String s) {
        int left = 0, right = s.length() - 1;
        while (left < right) {
            if (s.charAt(left) != s.charAt(right))
                return false;
            left++;
            right--;
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LongestPalindromicSubstringHard solution = new LongestPalindromicSubstringHard();

        // Test case 1: Basic case
        String s1 = "babad";
        System.out.println("Test 1 - s: " + s1 + " Expected: bab or aba");
        System.out.println("Result: " + solution.longestPalindrome(s1));
        System.out.println("Manacher: " + solution.longestPalindromeManacher(s1));

        // Test case 2: All longest palindromes
        System.out.println("\nTest 2 - All longest palindromes:");
        System.out.println(solution.findAllLongestPalindromes(s1));

        // Test case 3: Count palindromic substrings
        String s2 = "aaa";
        System.out.println("\nTest 3 - Count palindromic substrings in '" + s2 + "':");
        System.out.println(solution.countPalindromicSubstrings(s2));

        // Test case 4: Case-insensitive
        String s3 = "RaceCar";
        System.out.println("\nTest 4 - Case-insensitive:");
        System.out.println(solution.longestPalindromeIgnoreCase(s3));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty string: '" + solution.longestPalindrome("") + "'");
        System.out.println("Single char: '" + solution.longestPalindrome("a") + "'");
        System.out.println("No palindrome: '" + solution.longestPalindrome("abc") + "'");
        System.out.println("All same chars: '" + solution.longestPalindrome("aaaa") + "'");

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 500; i++)
            sb.append("ab");
        long start = System.nanoTime();
        String result = solution.longestPalindromeManacher(sb.toString());
        long end = System.nanoTime();
        System.out.println("Result length: " + result.length() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
