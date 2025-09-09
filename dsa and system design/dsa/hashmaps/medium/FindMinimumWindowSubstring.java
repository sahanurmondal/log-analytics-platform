package hashmaps.medium;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 76: Minimum Window Substring
 * https://leetcode.com/problems/minimum-window-substring/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Hard (Asked in 15+ interviews)
 *
 * Description: Given two strings `s` and `t` of lengths `m` and `n`
 * respectively, return the minimum window substring of `s` such that every
 * character in `t` (including duplicates) is included in the window. If there
 * is no such substring, return the empty string `""`.
 *
 * Constraints:
 * - m == s.length
 * - n == t.length
 * - 1 <= m, n <= 10^5
 * - s and t consist of uppercase and lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. Can you explain the sliding window logic in detail?
 * 2. How does the time complexity become O(m+n)?
 * 3. What if `t` could contain any Unicode character?
 */
public class FindMinimumWindowSubstring {

    // Approach 1: Sliding Window with HashMap - O(m+n) time, O(k) space where k is
    // charset size of t
    public String minWindow(String s, String t) {
        if (s.length() < t.length()) {
            return "";
        }

        Map<Character, Integer> tCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            tCount.put(c, tCount.getOrDefault(c, 0) + 1);
        }

        int left = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        int required = tCount.size();
        int formed = 0;

        Map<Character, Integer> windowCount = new HashMap<>();

        for (int right = 0; right < s.length(); right++) {
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
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    public static void main(String[] args) {
        FindMinimumWindowSubstring solution = new FindMinimumWindowSubstring();

        // Test case 1
        String s1 = "ADOBECODEBANC", t1 = "ABC";
        System.out.println("Min Window 1: " + solution.minWindow(s1, t1)); // "BANC"

        // Test case 2
        String s2 = "a", t2 = "a";
        System.out.println("Min Window 2: " + solution.minWindow(s2, t2)); // "a"

        // Test case 3
        String s3 = "a", t3 = "aa";
        System.out.println("Min Window 3: " + solution.minWindow(s3, t3)); // ""

        // Test case 4
        String s4 = "a", t4 = "b";
        System.out.println("Min Window 4: " + solution.minWindow(s4, t4)); // ""
    }
}
