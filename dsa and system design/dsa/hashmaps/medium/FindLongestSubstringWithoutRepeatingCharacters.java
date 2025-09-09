package hashmaps.medium;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * LeetCode 3: Longest Substring Without Repeating Characters
 * https://leetcode.com/problems/longest-substring-without-repeating-characters/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 30+ interviews)
 *
 * Description: Given a string `s`, find the length of the longest substring
 * without repeating characters.
 *
 * Constraints:
 * - 0 <= s.length <= 5 * 10^4
 * - s consists of English letters, digits, symbols and spaces.
 * 
 * Follow-up Questions:
 * 1. Can you solve this with a sliding window approach?
 * 2. How can you optimize the sliding window using a HashMap or an array?
 * 3. What if the character set is limited to ASCII or lowercase English
 * letters?
 */
public class FindLongestSubstringWithoutRepeatingCharacters {

    // Approach 1: Sliding Window with HashMap - O(n) time, O(min(n, m)) space
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0)
            return 0;
        Map<Character, Integer> map = new HashMap<>();
        int maxLength = 0, left = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (map.containsKey(c)) {
                left = Math.max(left, map.get(c) + 1);
            }
            map.put(c, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }
        return maxLength;
    }

    // Approach 2: Sliding Window with HashSet - O(n) time, O(min(n, m)) space
    public int lengthOfLongestSubstringWithSet(String s) {
        Set<Character> set = new HashSet<>();
        int left = 0, right = 0, maxLength = 0;
        while (right < s.length()) {
            if (!set.contains(s.charAt(right))) {
                set.add(s.charAt(right));
                right++;
                maxLength = Math.max(maxLength, set.size());
            } else {
                set.remove(s.charAt(left));
                left++;
            }
        }
        return maxLength;
    }

    // Approach 3: Optimized for ASCII - O(n) time, O(1) space
    public int lengthOfLongestSubstringOptimized(String s) {
        int[] index = new int[128];
        int left = 0, maxLength = 0;
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            left = Math.max(left, index[c]);
            maxLength = Math.max(maxLength, right - left + 1);
            index[c] = right + 1;
        }
        return maxLength;
    }

    public static void main(String[] args) {
        FindLongestSubstringWithoutRepeatingCharacters solution = new FindLongestSubstringWithoutRepeatingCharacters();
        System.out.println(solution.lengthOfLongestSubstring("abcabcbb")); // 3
        System.out.println(solution.lengthOfLongestSubstring("bbbbb")); // 1
        System.out.println(solution.lengthOfLongestSubstring("pwwkew")); // 3
        // Edge Case: Empty string
        System.out.println(solution.lengthOfLongestSubstring("")); // 0
        // Edge Case: All unique
        System.out.println(solution.lengthOfLongestSubstring("abcdef")); // 6
        // Edge Case: Large input
        String s = "a".repeat(50000);
        System.out.println(solution.lengthOfLongestSubstring(s)); // 1
    }
}
