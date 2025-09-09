package hashmaps.hard;

import java.util.HashMap;
import java.util.Map;

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
public class FindLongestSubstringWithNoRepeatingCharacters {

    // Approach 1: Sliding Window with HashMap - O(n) time, O(min(n, m)) space where
    // m is charset size
    public int lengthOfLongestSubstring(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }

        Map<Character, Integer> map = new HashMap<>();
        int maxLength = 0;
        int left = 0;

        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);

            if (map.containsKey(currentChar)) {
                left = Math.max(left, map.get(currentChar) + 1);
            }

            map.put(currentChar, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        FindLongestSubstringWithNoRepeatingCharacters solution = new FindLongestSubstringWithNoRepeatingCharacters();

        // Test case 1
        String s1 = "abcabcbb";
        System.out.println("Length 1: " + solution.lengthOfLongestSubstring(s1)); // 3

        // Test case 2
        String s2 = "bbbbb";
        System.out.println("Length 2: " + solution.lengthOfLongestSubstring(s2)); // 1

        // Test case 3
        String s3 = "pwwkew";
        System.out.println("Length 3: " + solution.lengthOfLongestSubstring(s3)); // 3
        // Edge Case: Empty string
        System.out.println(solution.lengthOfLongestSubstring("")); // 0
        // Edge Case: All unique
        System.out.println(solution.lengthOfLongestSubstring("abcdef")); // 6
        // Edge Case: Large input
        String s = "a".repeat(100000);
        System.out.println(solution.lengthOfLongestSubstring(s)); // 1
    }
}
