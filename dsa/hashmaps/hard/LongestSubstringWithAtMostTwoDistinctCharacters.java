package hashmaps.hard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 159: Longest Substring with At Most Two Distinct Characters
 * https://leetcode.com/problems/longest-substring-with-at-most-two-distinct-characters/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 9+ interviews)
 *
 * Description: Given a string `s`, return the length of the longest substring
 * that contains at most two distinct characters.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of English letters.
 * 
 * Follow-up Questions:
 * 1. How is this a specific case of "Longest Substring with At Most K Distinct
 * Characters"?
 * 2. Can you optimize the space complexity?
 * 3. How would you find the actual substring?
 */
public class LongestSubstringWithAtMostTwoDistinctCharacters {

    // Approach 1: Sliding Window with HashMap - O(n) time, O(1) space (since k=2)
    public int lengthOfLongestSubstringTwoDistinct(String s) {
        if (s.length() < 3) {
            return s.length();
        }

        Map<Character, Integer> rightmostPosition = new HashMap<>();
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            rightmostPosition.put(c, right);

            if (rightmostPosition.size() == 3) {
                // Find the leftmost character to remove
                int del_idx = Collections.min(rightmostPosition.values());
                rightmostPosition.remove(s.charAt(del_idx));
                left = del_idx + 1;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        LongestSubstringWithAtMostTwoDistinctCharacters solution = new LongestSubstringWithAtMostTwoDistinctCharacters();

        // Test case 1
        String s1 = "eceba";
        System.out.println("Length 1: " + solution.lengthOfLongestSubstringTwoDistinct(s1)); // 3

        // Test case 2
        String s2 = "ccaabbb";
        System.out.println("Length 2: " + solution.lengthOfLongestSubstringTwoDistinct(s2)); // 5

        // Test case 3
        String s3 = "abaccc";
        System.out.println("Length 3: " + solution.lengthOfLongestSubstringTwoDistinct(s3)); // 4
    }
}
