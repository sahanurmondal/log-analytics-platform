package arrays.medium;

import java.util.*;

/**
 * LeetCode 3: Longest Substring Without Repeating Characters
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 *
 * Description:
 * Given a string s, find the length of the longest substring without repeating
 * characters.
 *
 * Constraints:
 * - 0 <= s.length <= 5 * 10^4
 * - s consists of English letters, digits, symbols and spaces.
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * - How would you handle Unicode characters?
 */
public class LongestSubstringWithoutRepeatingCharacters {
    // Main solution - Sliding window with HashMap
    public int lengthOfLongestSubstring(String s) {
        Map<Character, Integer> charIndex = new HashMap<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);

            if (charIndex.containsKey(c) && charIndex.get(c) >= left) {
                left = charIndex.get(c) + 1;
            }

            charIndex.put(c, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Alternative solution - Using Set
    public int lengthOfLongestSubstringSet(String s) {
        Set<Character> seen = new HashSet<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            while (seen.contains(s.charAt(right))) {
                seen.remove(s.charAt(left));
                left++;
            }
            seen.add(s.charAt(right));
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        LongestSubstringWithoutRepeatingCharacters solution = new LongestSubstringWithoutRepeatingCharacters();
        // Edge Case 1: Normal case
        System.out.println(solution.lengthOfLongestSubstring("abcabcbb")); // 3
        // Edge Case 2: All same characters
        System.out.println(solution.lengthOfLongestSubstring("bbbbbb")); // 1
        // Edge Case 3: Empty string
        System.out.println(solution.lengthOfLongestSubstring("")); // 0
        // Edge Case 4: String with spaces
        System.out.println(solution.lengthOfLongestSubstring("a b c d e f")); // 7
        // Edge Case 5: String with symbols
        System.out.println(solution.lengthOfLongestSubstring("!@#!!@#")); // 3
        // Edge Case 6: Longest at the end
        System.out.println(solution.lengthOfLongestSubstring("pwwkew")); // 3
        // Edge Case 7: Single character
        System.out.println(solution.lengthOfLongestSubstring("a")); // 1
        // Edge Case 8: All unique
        System.out.println(solution.lengthOfLongestSubstring("abcdefg")); // 7
    }
}
