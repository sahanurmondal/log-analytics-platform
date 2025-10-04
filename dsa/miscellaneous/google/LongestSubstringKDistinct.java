package miscellaneous.google;

import java.util.*;

/**
 * LeetCode 340: Longest Substring with At Most K Distinct Characters
 * https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/
 *
 * Description:
 * Given a string s and an integer k, return the length of the longest substring
 * of s that contains at most k distinct characters.
 * 
 * Company: Google
 * Difficulty: Medium
 * Asked: Very frequently in 2023-2024
 * 
 * Constraints:
 * - 1 <= s.length <= 5 * 10^4
 * - 0 <= k <= 50
 * - s consists of lowercase English letters
 */
public class LongestSubstringKDistinct {

    public int lengthOfLongestSubstringKDistinct(String s, int k) {
        if (k == 0)
            return 0;

        Map<Character, Integer> charCount = new HashMap<>();
        int left = 0, maxLen = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);

            while (charCount.size() > k) {
                char leftChar = s.charAt(left);
                charCount.put(leftChar, charCount.get(leftChar) - 1);
                if (charCount.get(leftChar) == 0) {
                    charCount.remove(leftChar);
                }
                left++;
            }

            maxLen = Math.max(maxLen, right - left + 1);
        }

        return maxLen;
    }

    public static void main(String[] args) {
        LongestSubstringKDistinct solution = new LongestSubstringKDistinct();

        System.out.println(solution.lengthOfLongestSubstringKDistinct("eceba", 2)); // 3
        System.out.println(solution.lengthOfLongestSubstringKDistinct("aa", 1)); // 2
        System.out.println(solution.lengthOfLongestSubstringKDistinct("abaccc", 2)); // 4
    }
}
