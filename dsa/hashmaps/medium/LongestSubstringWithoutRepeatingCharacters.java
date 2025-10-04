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
public class LongestSubstringWithoutRepeatingCharacters {

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
                // Move the left pointer to the right of the last occurrence of the current
                // character
                left = Math.max(left, map.get(currentChar) + 1);
            }

            map.put(currentChar, right);
            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    // Approach 2: Sliding Window with HashSet - O(2n) -> O(n) time, O(min(n, m))
    // space
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

    // Follow-up 3: Optimized for limited character set (e.g., ASCII) - O(n) time,
    // O(m) space
    public int lengthOfLongestSubstringOptimized(String s) {
        int[] charIndex = new int[128]; // Assuming ASCII character set
        int left = 0, maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char currentChar = s.charAt(right);
            left = Math.max(left, charIndex[currentChar]);
            maxLength = Math.max(maxLength, right - left + 1);
            charIndex[currentChar] = right + 1; // Store next possible start position
        }

        return maxLength;
    }

    public static void main(String[] args) {
        LongestSubstringWithoutRepeatingCharacters solution = new LongestSubstringWithoutRepeatingCharacters();

        // Test case 1
        String s1 = "abcabcbb";
        System.out.println("Length 1 (Map): " + solution.lengthOfLongestSubstring(s1)); // 3
        System.out.println("Length 1 (Set): " + solution.lengthOfLongestSubstringWithSet(s1)); // 3
        System.out.println("Length 1 (Optimized): " + solution.lengthOfLongestSubstringOptimized(s1)); // 3

        // Test case 2
        String s2 = "bbbbb";
        System.out.println("Length 2: " + solution.lengthOfLongestSubstring(s2)); // 1

        // Test case 3
        String s3 = "pwwkew";
        System.out.println("Length 3: " + solution.lengthOfLongestSubstring(s3)); // 3

        // Test case 4: Empty string
        String s4 = "";
        System.out.println("Length 4: " + solution.lengthOfLongestSubstring(s4)); // 0

        // Test case 5: String with space
        String s5 = "a b c";
        System.out.println("Length 5: " + solution.lengthOfLongestSubstring(s5)); // 3
    }
}
