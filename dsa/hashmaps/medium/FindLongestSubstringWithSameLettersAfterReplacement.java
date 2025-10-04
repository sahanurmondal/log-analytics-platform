package hashmaps.medium;

/**
 * LeetCode 424: Longest Repeating Character Replacement
 * https://leetcode.com/problems/longest-repeating-character-replacement/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: You are given a string `s` and an integer `k`. You can choose
 * any character of the string and change it to any other uppercase English
 * character. You can perform this operation at most `k` times. Return the
 * length of the longest substring containing the same letter you can get after
 * performing the above operations.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - 0 <= k <= s.length
 * - s consists of only uppercase English letters.
 * 
 * Follow-up Questions:
 * 1. Can you explain why the sliding window does not need to shrink?
 * 2. How would you adapt this for a larger character set?
 * 3. What is the time and space complexity?
 */
public class FindLongestSubstringWithSameLettersAfterReplacement {

    // Approach 1: Sliding Window - O(n) time, O(1) space (since charset is fixed)
    public int characterReplacement(String s, int k) {
        int[] count = new int[26];
        int left = 0;
        int maxCount = 0;
        int maxLength = 0;

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            count[c - 'A']++;
            maxCount = Math.max(maxCount, count[c - 'A']);

            // If the window size minus the most frequent character count is greater than k,
            // we need to shrink the window from the left.
            if (right - left + 1 - maxCount > k) {
                count[s.charAt(left) - 'A']--;
                left++;
            }

            maxLength = Math.max(maxLength, right - left + 1);
        }

        return maxLength;
    }

    public static void main(String[] args) {
        FindLongestSubstringWithSameLettersAfterReplacement solution = new FindLongestSubstringWithSameLettersAfterReplacement();

        // Test case 1
        String s1 = "ABAB";
        int k1 = 2;
        System.out.println("Length 1: " + solution.characterReplacement(s1, k1)); // 4

        // Test case 2
        String s2 = "AABABBA";
        int k2 = 1;
        System.out.println("Length 2: " + solution.characterReplacement(s2, k2)); // 4
        // Edge Case: k == 0
        System.out.println(solution.characterReplacement("ABAB", 0)); // 2
        // Edge Case: All same
        System.out.println(solution.characterReplacement("AAAA", 2)); // 4
        // Edge Case: Large input
        String s = "A".repeat(100000);
        System.out.println(solution.characterReplacement(s, 100)); // 100000
    }
}
