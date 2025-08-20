package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 438: Find All Anagrams in a String
 * https://leetcode.com/problems/find-all-anagrams-in-a-string/
 * 
 * Companies: Facebook, Amazon, Google
 * Frequency: High
 *
 * Description: Given two strings s and p, return a list of all start indices of
 * p's anagrams in s.
 *
 * Constraints:
 * - 1 <= s.length, p.length <= 3 * 10^4
 * - s and p consist of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you find all anagrams with at most k mismatches?
 * 2. Can you find the longest/shortest anagram substring?
 * 3. Can you optimize for large strings?
 */
public class FindAllAnagramsInString {

    // Approach 1: Sliding window + frequency array
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        int[] pCount = new int[26], sCount = new int[26];
        for (char c : p.toCharArray())
            pCount[c - 'a']++;
        int n = s.length(), m = p.length();
        for (int i = 0; i < n; i++) {
            sCount[s.charAt(i) - 'a']++;
            if (i >= m)
                sCount[s.charAt(i - m) - 'a']--;
            if (i >= m - 1 && Arrays.equals(pCount, sCount))
                result.add(i - m + 1);
        }
        return result;
    }

    // Follow-up 1: All anagrams with at most k mismatches
    public List<Integer> findAnagramsWithMismatches(String s, String p, int k) {
        List<Integer> result = new ArrayList<>();
        int[] pCount = new int[26], sCount = new int[26];
        for (char c : p.toCharArray())
            pCount[c - 'a']++;
        int n = s.length(), m = p.length();
        for (int i = 0; i < n; i++) {
            sCount[s.charAt(i) - 'a']++;
            if (i >= m)
                sCount[s.charAt(i - m) - 'a']--;
            if (i >= m - 1) {
                int mismatches = 0;
                for (int j = 0; j < 26; j++)
                    mismatches += Math.abs(pCount[j] - sCount[j]);
                if (mismatches / 2 <= k)
                    result.add(i - m + 1);
            }
        }
        return result;
    }

    // Follow-up 2: Longest anagram substring
    public int longestAnagramSubstring(String s, String p) {
        int maxLen = 0;
        List<Integer> indices = findAnagrams(s, p);
        if (indices.isEmpty())
            return 0;
        for (int idx : indices)
            maxLen = Math.max(maxLen, p.length());
        return maxLen;
    }

    // Follow-up 3: Shortest anagram substring
    public int shortestAnagramSubstring(String s, String p) {
        int minLen = Integer.MAX_VALUE;
        List<Integer> indices = findAnagrams(s, p);
        if (indices.isEmpty())
            return 0;
        for (int idx : indices)
            minLen = Math.min(minLen, p.length());
        return minLen;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindAllAnagramsInString solution = new FindAllAnagramsInString();

        // Test case 1: Basic case
        String s1 = "cbaebabacd", p1 = "abc";
        System.out.println("Test 1 - s: " + s1 + ", p: " + p1 + " Expected: [0,6]");
        System.out.println("Result: " + solution.findAnagrams(s1, p1));

        // Test case 2: No anagrams
        String s2 = "abcdefg", p2 = "hij";
        System.out.println("\nTest 2 - No anagrams:");
        System.out.println("Result: " + solution.findAnagrams(s2, p2));

        // Test case 3: At most k mismatches
        System.out.println("\nTest 3 - At most 1 mismatch:");
        System.out.println(solution.findAnagramsWithMismatches(s1, p1, 1));

        // Test case 4: Longest anagram substring
        System.out.println("\nTest 4 - Longest anagram substring:");
        System.out.println(solution.longestAnagramSubstring(s1, p1));

        // Test case 5: Shortest anagram substring
        System.out.println("\nTest 5 - Shortest anagram substring:");
        System.out.println(solution.shortestAnagramSubstring(s1, p1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty string: " + solution.findAnagrams("", ""));
        System.out.println("Single char: " + solution.findAnagrams("a", "a"));
        System.out.println("All anagrams: " + solution.findAnagrams("abab", "ab"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            sb.append("abc");
        long start = System.nanoTime();
        List<Integer> result = solution.findAnagrams(sb.toString(), "abc");
        long end = System.nanoTime();
        System.out.println("Result size: " + result.size() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
