package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 340: Longest Substring with At Most K Distinct Characters
 * https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/
 * 
 * Advanced Variation: Minimum Window with Required Character Frequencies
 *
 * Description:
 * Given a string s and a map of required character frequencies,
 * find the minimum window that contains all characters with at least their
 * required frequencies.
 *
 * Companies: Google, Facebook, Amazon, Microsoft
 * Frequency: High (Asked in 200+ interviews)
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of lowercase English letters
 * - 1 <= required frequencies <= s.length
 *
 * Follow-up:
 * 1. Can you handle updates to required frequencies?
 * 2. Can you find all minimum windows?
 * 3. Can you handle case-insensitive matching?
 * 4. Can you extend to find windows with exact frequencies?
 */
public class MinimumWindowWithCharacterFrequency {
    public String minWindow(String s, String pattern) {
        if (s == null || pattern == null || s.length() == 0 || pattern.length() == 0) {
            return "";
        }

        // Build frequency map for pattern
        Map<Character, Integer> patternMap = new HashMap<>();
        for (char c : pattern.toCharArray()) {
            patternMap.put(c, patternMap.getOrDefault(c, 0) + 1);
        }

        int required = patternMap.size();
        int left = 0, right = 0;
        int formed = 0;

        Map<Character, Integer> windowCounts = new HashMap<>();

        int[] ans = { -1, 0, 0 }; // length, left, right

        while (right < s.length()) {
            char c = s.charAt(right);
            windowCounts.put(c, windowCounts.getOrDefault(c, 0) + 1);

            if (patternMap.containsKey(c) &&
                    windowCounts.get(c).intValue() == patternMap.get(c).intValue()) {
                formed++;
            }

            while (left <= right && formed == required) {
                if (ans[0] == -1 || right - left + 1 < ans[0]) {
                    ans[0] = right - left + 1;
                    ans[1] = left;
                    ans[2] = right;
                }

                char leftChar = s.charAt(left);
                windowCounts.put(leftChar, windowCounts.get(leftChar) - 1);
                if (patternMap.containsKey(leftChar) &&
                        windowCounts.get(leftChar).intValue() < patternMap.get(leftChar).intValue()) {
                    formed--;
                }

                left++;
            }

            right++;
        }

        return ans[0] == -1 ? "" : s.substring(ans[1], ans[2] + 1);
    }

    public static void main(String[] args) {
        MinimumWindowWithCharacterFrequency solution = new MinimumWindowWithCharacterFrequency();
        System.out.println(solution.minWindow("ADOBECODEBANC", "AABC")); // Should find window with A:2, B:1, C:1
        System.out.println(solution.minWindow("aa", "aa")); // "aa"
        // Edge Case: Pattern not satisfiable
        System.out.println(solution.minWindow("a", "aa")); // ""
        // Edge Case: Multiple valid windows
        System.out.println(solution.minWindow("AABBCC", "ABC")); // "AABBC"
        // Edge Case: Pattern longer than string
        System.out.println(solution.minWindow("ab", "abc")); // ""
    }
}
