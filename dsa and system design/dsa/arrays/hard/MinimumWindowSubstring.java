package arrays.hard;

import java.util.*;

/**
 * LeetCode 76: Minimum Window Substring
 * https://leetcode.com/problems/minimum-window-substring/
 *
 * Description:
 * Given two strings s and t of lengths m and n respectively, return the minimum
 * window substring
 * of s such that every character in t (including duplicates) is included in the
 * window.
 *
 * Constraints:
 * - m == s.length
 * - n == t.length
 * - 1 <= m, n <= 10^5
 * - s and t consist of uppercase and lowercase English letters
 *
 * Follow-up:
 * - Can you find an algorithm that runs in O(m + n) time?
 * 
 * Time Complexity: O(m + n)
 * Space Complexity: O(m + n)
 * 
 * Algorithm:
 * 1. Use sliding window with two pointers
 * 2. Expand right pointer until all characters of t are included
 * 3. Contract left pointer while maintaining validity
 */
public class MinimumWindowSubstring {
    public String minWindow(String s, String t) {
        if (s.length() < t.length())
            return "";

        Map<Character, Integer> tCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            tCount.put(c, tCount.getOrDefault(c, 0) + 1);
        }

        int left = 0, right = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        int required = tCount.size();
        int formed = 0;

        Map<Character, Integer> windowCount = new HashMap<>();

        while (right < s.length()) {
            char c = s.charAt(right);
            windowCount.put(c, windowCount.getOrDefault(c, 0) + 1);

            if (tCount.containsKey(c) && windowCount.get(c).intValue() == tCount.get(c).intValue()) {
                formed++;
            }

            while (left <= right && formed == required) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                char leftChar = s.charAt(left);
                windowCount.put(leftChar, windowCount.get(leftChar) - 1);
                if (tCount.containsKey(leftChar) && windowCount.get(leftChar) < tCount.get(leftChar)) {
                    formed--;
                }
                left++;
            }

            right++;
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    public static void main(String[] args) {
        MinimumWindowSubstring solution = new MinimumWindowSubstring();

        // Test Case 1: Normal case
        System.out.println(solution.minWindow("ADOBECODEBANC", "ABC")); // Expected: "BANC"

        // Test Case 2: Edge case - no valid window
        System.out.println(solution.minWindow("a", "aa")); // Expected: ""

        // Test Case 3: Corner case - exact match
        System.out.println(solution.minWindow("ab", "b")); // Expected: "b"

        // Test Case 4: Large input - whole string needed
        System.out.println(solution.minWindow("abc", "cba")); // Expected: "abc"

        // Test Case 5: Minimum input - single char
        System.out.println(solution.minWindow("a", "a")); // Expected: "a"

        // Test Case 6: Special case - duplicates in t
        System.out.println(solution.minWindow("ADOBECODEBANC", "AABC")); // Expected: "ADOBEC"

        // Test Case 7: Boundary case - t longer than s
        System.out.println(solution.minWindow("a", "ab")); // Expected: ""

        // Test Case 8: Multiple valid windows
        System.out.println(solution.minWindow("abbc", "bc")); // Expected: "bc"

        // Test Case 9: Case sensitive
        System.out.println(solution.minWindow("Ab", "A")); // Expected: "A"

        // Test Case 10: All characters same
        System.out.println(solution.minWindow("aaaa", "aa")); // Expected: "aa"
    }
}
