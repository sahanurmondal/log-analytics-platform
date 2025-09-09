package strings.hard;

import java.util.*;

/**
 * LeetCode 28: Find the Index of the First Occurrence in a String (KMP
 * Implementation)
 * https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/
 *
 * Description: Given two strings needle and haystack, return the index of the
 * first occurrence
 * of needle in haystack, or -1 if needle is not part of haystack.
 * 
 * Constraints:
 * - 1 <= haystack.length, needle.length <= 10^4
 * - haystack and needle consist of only lowercase English characters
 *
 * Follow-up:
 * - Can you implement KMP algorithm for O(n+m) time complexity?
 * - What about Rabin-Karp with rolling hash?
 * - How would you handle multiple pattern matching?
 * 
 * Time Complexity: O(n + m) for KMP, O(nm) for naive
 * Space Complexity: O(m) for KMP, O(1) for naive
 * 
 * Algorithm:
 * 1. KMP: Build failure function, then match with no backtracking
 * 2. Rabin-Karp: Use rolling hash for efficient pattern matching
 * 3. Z-Algorithm: Linear time string matching
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class KMPStringMatching {

    // Main optimized solution - KMP Algorithm
    public int strStr(String haystack, String needle) {
        if (needle.isEmpty())
            return 0;

        int[] lps = buildLPS(needle);
        int i = 0, j = 0; // i for haystack, j for needle

        while (i < haystack.length()) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;
            }

            if (j == needle.length()) {
                return i - j; // Found match
            } else if (i < haystack.length() && haystack.charAt(i) != needle.charAt(j)) {
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }

        return -1;
    }

    // Build Longest Prefix Suffix (LPS) array for KMP
    private int[] buildLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int len = 0;
        int i = 1;

        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(len)) {
                len++;
                lps[i] = len;
                i++;
            } else {
                if (len != 0) {
                    len = lps[len - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }

        return lps;
    }

    // Alternative solution - Rabin-Karp with Rolling Hash
    public int strStrRabinKarp(String haystack, String needle) {
        if (needle.isEmpty())
            return 0;
        if (needle.length() > haystack.length())
            return -1;

        int base = 256;
        int mod = 101;
        int m = needle.length();
        int n = haystack.length();

        // Calculate hash of needle and first window of haystack
        int patternHash = 0, windowHash = 0;
        int h = 1;

        // Calculate h = pow(base, m-1) % mod
        for (int i = 0; i < m - 1; i++) {
            h = (h * base) % mod;
        }

        // Calculate hash of pattern and first window
        for (int i = 0; i < m; i++) {
            patternHash = (base * patternHash + needle.charAt(i)) % mod;
            windowHash = (base * windowHash + haystack.charAt(i)) % mod;
        }

        // Slide the pattern over text one by one
        for (int i = 0; i <= n - m; i++) {
            if (patternHash == windowHash) {
                // Check character by character
                if (haystack.substring(i, i + m).equals(needle)) {
                    return i;
                }
            }

            // Calculate hash of next window
            if (i < n - m) {
                windowHash = (base * (windowHash - haystack.charAt(i) * h) + haystack.charAt(i + m)) % mod;
                if (windowHash < 0) {
                    windowHash += mod;
                }
            }
        }

        return -1;
    }

    // Follow-up optimization - Z Algorithm
    public int strStrZAlgorithm(String haystack, String needle) {
        if (needle.isEmpty())
            return 0;

        String combined = needle + "$" + haystack;
        int[] z = buildZArray(combined);

        for (int i = 0; i < z.length; i++) {
            if (z[i] == needle.length()) {
                return i - needle.length() - 1;
            }
        }

        return -1;
    }

    private int[] buildZArray(String s) {
        int n = s.length();
        int[] z = new int[n];
        int left = 0, right = 0;

        for (int i = 1; i < n; i++) {
            if (i <= right) {
                z[i] = Math.min(right - i + 1, z[i - left]);
            }

            while (i + z[i] < n && s.charAt(z[i]) == s.charAt(i + z[i])) {
                z[i]++;
            }

            if (i + z[i] - 1 > right) {
                left = i;
                right = i + z[i] - 1;
            }
        }

        return z;
    }

    public static void main(String[] args) {
        KMPStringMatching solution = new KMPStringMatching();

        // Test Case 1: Normal case
        System.out.println(solution.strStr("sadbutsad", "sad")); // Expected: 0

        // Test Case 2: Edge case - not found
        System.out.println(solution.strStr("leetcode", "leeto")); // Expected: -1

        // Test Case 3: Corner case - empty needle
        System.out.println(solution.strStr("hello", "")); // Expected: 0

        // Test Case 4: Pattern at end
        System.out.println(solution.strStr("hello", "lo")); // Expected: 3

        // Test Case 5: Pattern longer than text
        System.out.println(solution.strStr("a", "aa")); // Expected: -1

        // Test Case 6: Repeated pattern
        System.out.println(solution.strStr("aaaa", "aa")); // Expected: 0

        // Test Case 7: Single character match
        System.out.println(solution.strStr("abc", "c")); // Expected: 2

        // Test Case 8: Entire string match
        System.out.println(solution.strStr("hello", "hello")); // Expected: 0

        // Test Case 9: Complex pattern
        System.out.println(solution.strStr("abcabcabcabc", "abcabc")); // Expected: 0

        // Test Case 10: No match with similar pattern
        System.out.println(solution.strStr("ababcababa", "ababa")); // Expected: 5
    }
}
