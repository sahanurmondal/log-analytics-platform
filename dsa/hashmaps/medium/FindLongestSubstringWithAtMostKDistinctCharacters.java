package hashmaps.medium;

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
public class FindLongestSubstringWithAtMostKDistinctCharacters {

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

    // Follow-up 3: Find the actual substring
    public String findLongestSubstringKDistinct(String s, int k) {
        if (s == null || s.length() == 0 || k == 0) {
            return "";
        }

        Map<Character, Integer> map = new HashMap<>();
        int left = 0;
        int maxLength = 0;
        int start = 0;

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

            if (right - left + 1 > maxLength) {
                maxLength = right - left + 1;
                start = left;
            }
        }

        return s.substring(start, start + maxLength);
    }

    public static void main(String[] args) {
        FindLongestSubstringWithAtMostKDistinctCharacters solution = new FindLongestSubstringWithAtMostKDistinctCharacters();

        // Test case 1
        String s1 = "eceba";
        int k1 = 2;
        System.out.println("Length 1: " + solution.lengthOfLongestSubstringKDistinct(s1, k1)); // 3
        System.out.println("Substring 1: " + solution.findLongestSubstringKDistinct(s1, k1)); // "ece"

        // Test case 2
        String s2 = "aa";
        int k2 = 1;
        System.out.println("Length 2: " + solution.lengthOfLongestSubstringKDistinct(s2, k2)); // 2

        // Test case 3: k is larger than number of unique chars
        String s3 = "abc";
        int k3 = 4;
        System.out.println("Length 3: " + solution.lengthOfLongestSubstringKDistinct(s3, k3)); // 3

        // Test case 4: k is 0
        String s4 = "abc";
        int k4 = 0;
        System.out.println("Length 4: " + solution.lengthOfLongestSubstringKDistinct(s4, k4)); // 0
    }
}
