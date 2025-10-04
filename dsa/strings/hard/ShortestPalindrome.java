package strings.hard;

import java.util.*;

/**
 * LeetCode 214: Shortest Palindrome
 * https://leetcode.com/problems/shortest-palindrome/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a string s, add characters in front of s to make it a
 * palindrome. Return the shortest palindrome.
 *
 * Constraints:
 * - 0 <= s.length <= 5 * 10^4
 * - s consists of lowercase English letters only
 * 
 * Follow-up Questions:
 * 1. Can you add characters at the end instead?
 * 2. Can you handle case-insensitive strings?
 * 3. Can you optimize for large strings?
 */
public class ShortestPalindrome {

    // Approach 1: KMP algorithm - O(n) time, O(n) space
    public String shortestPalindrome(String s) {
        if (s == null || s.length() <= 1)
            return s;

        String rev = new StringBuilder(s).reverse().toString();
        String combined = s + "#" + rev;
        int[] kmp = buildKMPTable(combined);

        int overlap = kmp[combined.length() - 1];
        return rev.substring(0, s.length() - overlap) + s;
    }

    // Approach 2: Brute force - O(n^2) time, O(n) space
    public String shortestPalindromeBruteForce(String s) {
        int n = s.length();
        for (int i = 0; i < n; i++) {
            if (isPalindrome(s.substring(i))) {
                return new StringBuilder(s.substring(0, i)).reverse().toString() + s;
            }
        }
        return s;
    }

    // Follow-up 1: Add characters at the end to make palindrome
    public String shortestPalindromeAtEnd(String s) {
        if (s == null || s.length() <= 1)
            return s;

        String rev = new StringBuilder(s).reverse().toString();
        String combined = rev + "#" + s;
        int[] kmp = buildKMPTable(combined);

        int overlap = kmp[combined.length() - 1];
        return s + rev.substring(0, s.length() - overlap);
    }

    // Follow-up 2: Case-insensitive palindrome
    public String shortestPalindromeIgnoreCase(String s) {
        return shortestPalindrome(s.toLowerCase());
    }

    // Follow-up 3: Get all possible shortest palindromes
    public List<String> getAllShortestPalindromes(String s) {
        List<String> result = new ArrayList<>();
        String shortest = shortestPalindrome(s);
        int minLen = shortest.length();

        // Find all ways to create palindrome of minimum length
        for (int i = 0; i <= s.length(); i++) {
            String prefix = new StringBuilder(s.substring(0, i)).reverse().toString();
            String candidate = prefix + s;
            if (candidate.length() == minLen && isPalindrome(candidate)) {
                result.add(candidate);
            }
        }
        return result;
    }

    // Helper methods
    private int[] buildKMPTable(String pattern) {
        int[] table = new int[pattern.length()];
        int i = 1, j = 0;

        while (i < pattern.length()) {
            if (pattern.charAt(i) == pattern.charAt(j)) {
                table[i] = j + 1;
                i++;
                j++;
            } else if (j > 0) {
                j = table[j - 1];
            } else {
                table[i] = 0;
                i++;
            }
        }
        return table;
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
        ShortestPalindrome solution = new ShortestPalindrome();

        // Test case 1: Basic case
        String s1 = "aacecaaa";
        System.out.println("Test 1 - s: " + s1 + " Expected: aaacecaaa");
        System.out.println("Result: " + solution.shortestPalindrome(s1));
        System.out.println("Brute force: " + solution.shortestPalindromeBruteForce(s1));

        // Test case 2: Add at end
        System.out.println("\nTest 2 - Add at end:");
        System.out.println("Result: " + solution.shortestPalindromeAtEnd(s1));

        // Test case 3: Case-insensitive
        String s2 = "AacecaaA";
        System.out.println("\nTest 3 - Case-insensitive:");
        System.out.println("Result: " + solution.shortestPalindromeIgnoreCase(s2));

        // Test case 4: All shortest palindromes
        String s3 = "abc";
        System.out.println("\nTest 4 - All shortest palindromes for '" + s3 + "':");
        List<String> allShortest = solution.getAllShortestPalindromes(s3);
        for (String pal : allShortest) {
            System.out.println(pal);
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty string: '" + solution.shortestPalindrome("") + "'");
        System.out.println("Single char: '" + solution.shortestPalindrome("a") + "'");
        System.out.println("Already palindrome: '" + solution.shortestPalindrome("aba") + "'");
        System.out.println("All same chars: '" + solution.shortestPalindrome("aaaa") + "'");

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 1000; i++)
            sb.append("ab");
        long start = System.nanoTime();
        String result = solution.shortestPalindrome(sb.toString());
        long end = System.nanoTime();
        System.out.println("Result length: " + result.length() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
