package hashmaps.hard;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 340: Longest Substring with At Most K Distinct Characters
 * https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given a string `s` and an integer `k`, return the length of the
 * longest substring of `s` that contains at most `k` distinct characters.
 *
 * Constraints:
 * - 1 <= s.length <= 5 * 10^4
 * - 0 <= k <= 50
 * 
 * Follow-up Questions:
 * 1. What if `k` is 2? (LeetCode 159)
 * 2. Can you solve this with a fixed-size array instead of a HashMap?
 * 3. How would you find the actual substring?
 */
public class LongestSubstringWithAtMostKDistinctCharacters {

    // Approach 1: Sliding Window with HashMap - O(n) time, O(k) space
    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (s == null || s.length() == 0 || k == 0) {
            return 0;
        }

        Map<Character, Integer> map = new HashMap<>();
        int left = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            map.put(c, map.getOrDefault(c, 0) + 1);

            while (map.size() > k) {
                char leftChar = s.charAt(left);
                map.put(leftChar, map.get(leftChar) - 1);
                if (map.get(leftChar) == 0) {
                    map.remove(leftChar);
                }
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        LongestSubstringWithAtMostKDistinctCharacters solution = new LongestSubstringWithAtMostKDistinctCharacters();

        // Test case 1
        String s1 = "eceba";
        int k1 = 2;
        System.out.println("Length 1: " + solution.lengthOfLongestSubstringKDistinct(s1, k1)); // 3

        // Test case 2
        String s2 = "aa";
        int k2 = 1;
        System.out.println("Length 2: " + solution.lengthOfLongestSubstringKDistinct(s2, k2)); // 2

        // Edge Case: k == 0
        System.out.println(solution.lengthOfLongestSubstringKDistinct("abc", 0)); // 0
        // Edge Case: Empty string
        System.out.println(solution.lengthOfLongestSubstringKDistinct("", 2)); // 0
        // Edge Case: Large input
        String s = "a".repeat(50000);
        System.out.println(solution.lengthOfLongestSubstringKDistinct(s, 1)); // 50000
    }
}
