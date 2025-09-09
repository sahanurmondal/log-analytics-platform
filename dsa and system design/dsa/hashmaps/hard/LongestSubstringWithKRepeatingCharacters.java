package hashmaps.hard;

import java.util.Arrays;

/**
 * LeetCode 395: Longest Substring with At Least K Repeating Characters
 * https://leetcode.com/problems/longest-substring-with-at-least-k-repeating-characters/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 7+ interviews)
 *
 * Description: Given a string `s` and an integer `k`, return the length of the
 * longest substring of `s` such that the frequency of each character in this
 * substring is greater than or equal to `k`.
 *
 * Constraints:
 * - 1 <= s.length <= 10^4
 * - 1 <= k <= 10^5
 * - s consists of only lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. Can you explain the divide and conquer approach?
 * 2. How does the sliding window approach work for this problem?
 * 3. What is the time complexity of each approach?
 */
public class LongestSubstringWithKRepeatingCharacters {

    // Approach 1: Divide and Conquer - O(n^2) worst case, O(n log n) average case
    // time, O(n) space for recursion
    public int longestSubstring(String s, int k) {
        if (s.length() < k)
            return 0;

        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }

        for (int i = 0; i < s.length(); i++) {
            if (count[s.charAt(i) - 'a'] < k) {
                int left = longestSubstring(s.substring(0, i), k);
                int right = longestSubstring(s.substring(i + 1), k);
                return Math.max(left, right);
            }
        }

        return s.length();
    }

    // Approach 2: Sliding Window - O(26 * n) -> O(n) time, O(1) space
    public int longestSubstringSlidingWindow(String s, int k) {
        int maxLength = 0;
        for (int numUniqueTarget = 1; numUniqueTarget <= 26; numUniqueTarget++) {
            int[] count = new int[26];
            int left = 0;
            int numUnique = 0;
            int numNoLessThanK = 0;

            for (int right = 0; right < s.length(); right++) {
                char c = s.charAt(right);
                if (count[c - 'a'] == 0)
                    numUnique++;
                count[c - 'a']++;
                if (count[c - 'a'] == k)
                    numNoLessThanK++;

                while (numUnique > numUniqueTarget) {
                    char leftChar = s.charAt(left);
                    if (count[leftChar - 'a'] == k)
                        numNoLessThanK--;
                    count[leftChar - 'a']--;
                    if (count[leftChar - 'a'] == 0)
                        numUnique--;
                    left++;
                }

                if (numUnique == numUniqueTarget && numUnique == numNoLessThanK) {
                    maxLength = Math.max(maxLength, right - left + 1);
                }
            }
        }
        return maxLength;
    }

    public static void main(String[] args) {
        LongestSubstringWithKRepeatingCharacters solution = new LongestSubstringWithKRepeatingCharacters();

        // Test case 1
        String s1 = "aaabb";
        int k1 = 3;
        System.out.println("Length 1 (D&C): " + solution.longestSubstring(s1, k1)); // 3
        System.out.println("Length 1 (Sliding Window): " + solution.longestSubstringSlidingWindow(s1, k1)); // 3

        // Test case 2
        String s2 = "ababbc";
        int k2 = 2;
        System.out.println("Length 2: " + solution.longestSubstring(s2, k2)); // 5

        // Test case 3
        String s3 = "bbaaacbd";
        int k3 = 3;
        System.out.println("Length 3: " + solution.longestSubstring(s3, k3)); // 3
    }
}
