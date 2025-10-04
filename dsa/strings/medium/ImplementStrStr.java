package strings.medium;

/**
 * LeetCode 28: Find the Index of the First Occurrence in a String
 * https://leetcode.com/problems/find-the-index-of-the-first-occurrence-in-a-string/
 *
 * Description: Return the index of the first occurrence of needle in haystack,
 * or -1 if needle is not part of haystack.
 *
 * Constraints:
 * - 1 <= haystack.length, needle.length <= 10^4
 * - haystack and needle consist of only lowercase English characters
 *
 * Follow-up:
 * - Can you implement KMP algorithm?
 * - What about Rabin-Karp algorithm?
 */
public class ImplementStrStr {

    // Approach 1: Brute Force - O(nm) time, O(1) space
    public int strStr(String haystack, String needle) {
        if (needle.isEmpty())
            return 0;

        int n = haystack.length();
        int m = needle.length();

        for (int i = 0; i <= n - m; i++) {
            int j = 0;
            while (j < m && haystack.charAt(i + j) == needle.charAt(j)) {
                j++;
            }
            if (j == m)
                return i;
        }

        return -1;
    }

    // Approach 2: KMP Algorithm - O(n + m) time, O(m) space
    public int strStrKMP(String haystack, String needle) {
        if (needle.isEmpty())
            return 0;

        int[] lps = computeLPS(needle);
        int i = 0, j = 0;

        while (i < haystack.length()) {
            if (haystack.charAt(i) == needle.charAt(j)) {
                i++;
                j++;
            }

            if (j == needle.length()) {
                return i - j;
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

    private int[] computeLPS(String pattern) {
        int[] lps = new int[pattern.length()];
        int len = 0;
        int i = 1;

        while (i < pattern.length()) {
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

    // Approach 3: Rabin-Karp Algorithm - O(n + m) average, O(nm) worst case
    public int strStrRabinKarp(String haystack, String needle) {
        if (needle.isEmpty())
            return 0;

        int n = haystack.length();
        int m = needle.length();

        if (m > n)
            return -1;

        final int BASE = 256;
        final int MOD = 101;

        int needleHash = 0;
        int windowHash = 0;
        int h = 1;

        // Calculate h = pow(BASE, m-1) % MOD
        for (int i = 0; i < m - 1; i++) {
            h = (h * BASE) % MOD;
        }

        // Calculate hash for needle and first window
        for (int i = 0; i < m; i++) {
            needleHash = (BASE * needleHash + needle.charAt(i)) % MOD;
            windowHash = (BASE * windowHash + haystack.charAt(i)) % MOD;
        }

        // Slide the window
        for (int i = 0; i <= n - m; i++) {
            if (needleHash == windowHash) {
                // Check character by character
                if (haystack.substring(i, i + m).equals(needle)) {
                    return i;
                }
            }

            // Calculate hash for next window
            if (i < n - m) {
                windowHash = (BASE * (windowHash - haystack.charAt(i) * h) +
                        haystack.charAt(i + m)) % MOD;

                if (windowHash < 0) {
                    windowHash += MOD;
                }
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        ImplementStrStr solution = new ImplementStrStr();

        // Test cases
        System.out.println(solution.strStr("sadbutsad", "sad")); // 0
        System.out.println(solution.strStr("leetcode", "leeto")); // -1
        System.out.println(solution.strStr("hello", "ll")); // 2

        // Test KMP
        System.out.println(solution.strStrKMP("sadbutsad", "sad")); // 0
        System.out.println(solution.strStrKMP("leetcode", "leeto")); // -1

        // Test Rabin-Karp
        System.out.println(solution.strStrRabinKarp("sadbutsad", "sad")); // 0
        System.out.println(solution.strStrRabinKarp("leetcode", "leeto")); // -1

        // Edge cases
        System.out.println(solution.strStr("", "")); // 0
        System.out.println(solution.strStr("a", "a")); // 0
        System.out.println(solution.strStr("a", "aa")); // -1
    }
}
