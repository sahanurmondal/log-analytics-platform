package slidingwindow.medium;

/**
 * LeetCode 1100: Find K-Length Substrings With No Repeated Characters
 * https://leetcode.com/problems/find-k-length-substrings-with-no-repeated-characters/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given a string s and an integer k, return the number of
 * substrings of length k with no repeated characters.
 *
 * Constraints:
 * - 1 <= s.length <= 10^4
 * - 1 <= k <= s.length
 * - s consists of lowercase English letters
 *
 * Follow-up Questions:
 * 1. How to find all such substrings instead of just the count?
 * 2. What if s contains uppercase letters or digits?
 * 3. How to solve for very large k efficiently?
 */
public class FindKLengthSubstringsWithNoRepeatedCharacters {
    // Approach 1: Sliding Window with HashSet - O(n) time, O(k) space
    public int numKLenSubstrNoRepeats(String s, int k) {
        if (k > s.length())
            return 0;
        int count = 0;
        java.util.Set<Character> window = new java.util.HashSet<>();
        int left = 0;
        for (int right = 0; right < s.length(); right++) {
            while (window.contains(s.charAt(right))) {
                window.remove(s.charAt(left++));
            }
            window.add(s.charAt(right));
            if (right - left + 1 == k) {
                count++;
                window.remove(s.charAt(left++));
            }
        }
        return count;
    }

    // Approach 2: Sliding Window with Frequency Array - O(n) time, O(1) space
    public int numKLenSubstrNoRepeatsFreq(String s, int k) {
        if (k > s.length())
            return 0;
        int[] freq = new int[128];
        int left = 0, count = 0, unique = 0;
        for (int right = 0; right < s.length(); right++) {
            if (freq[s.charAt(right)]++ == 0)
                unique++;
            while (unique < right - left + 1) {
                if (--freq[s.charAt(left++)] == 0)
                    unique--;
            }
            if (right - left + 1 == k && unique == k) {
                count++;
            }
        }
        return count;
    }

    // Follow-up 1: Return all substrings
    public java.util.List<String> getAllKLenSubstrNoRepeats(String s, int k) {
        java.util.List<String> res = new java.util.ArrayList<>();
        java.util.Set<Character> window = new java.util.HashSet<>();
        int left = 0;
        for (int right = 0; right < s.length(); right++) {
            while (window.contains(s.charAt(right))) {
                window.remove(s.charAt(left++));
            }
            window.add(s.charAt(right));
            if (right - left + 1 == k) {
                res.add(s.substring(left, right + 1));
                window.remove(s.charAt(left++));
            }
        }
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindKLengthSubstringsWithNoRepeatedCharacters sol = new FindKLengthSubstringsWithNoRepeatedCharacters();
        // Test 1: Basic
        System.out.println("Test 1: Expected 2 -> " + sol.numKLenSubstrNoRepeats("havefunonleetcode", 5));
        // Test 2: All unique
        System.out.println("Test 2: Expected 1 -> " + sol.numKLenSubstrNoRepeats("abcde", 5));
        // Test 3: All repeated
        System.out.println("Test 3: Expected 0 -> " + sol.numKLenSubstrNoRepeats("aaaaa", 2));
        // Test 4: k > s.length
        System.out.println("Test 4: Expected 0 -> " + sol.numKLenSubstrNoRepeats("abc", 4));
        // Test 5: k = 1
        System.out.println("Test 5: Expected 5 -> " + sol.numKLenSubstrNoRepeats("abcde", 1));
        // Test 6: Empty string
        System.out.println("Test 6: Expected 0 -> " + sol.numKLenSubstrNoRepeats("", 3));
        // Test 7: Frequency array approach
        System.out.println("Test 7: Expected 2 -> " + sol.numKLenSubstrNoRepeatsFreq("havefunonleetcode", 5));
        // Test 8: Get all substrings
        System.out.println("Test 8: Expected [havef, avefu] -> " + sol.getAllKLenSubstrNoRepeats("havefun", 5));
        // Test 9: Edge case, k = s.length
        System.out.println("Test 9: Expected 1 -> " + sol.numKLenSubstrNoRepeats("abcdef", 6));
        // Test 10: Edge case, k = 0
        System.out.println("Test 10: Expected 0 -> " + sol.numKLenSubstrNoRepeats("abc", 0));
        // Test 11: Uppercase letters
        System.out.println("Test 11: Expected 1 -> " + sol.numKLenSubstrNoRepeats("Abcde", 5));
        // Test 12: Digits
        System.out.println("Test 12: Expected 1 -> " + sol.numKLenSubstrNoRepeats("12345", 5));
        // Test 13: Mixed case
        System.out.println("Test 13: Expected 0 -> " + sol.numKLenSubstrNoRepeats("aAaAaA", 2));
        // Test 14: Large k
        System.out.println("Test 14: Expected 0 -> " + sol.numKLenSubstrNoRepeats("abc", 10));
        // Test 15: Large input
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            sb.append((char) ('a' + (i % 26)));
        System.out.println("Test 15: Large input -> " + sol.numKLenSubstrNoRepeats(sb.toString(), 26));
    }
}
