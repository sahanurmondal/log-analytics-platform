package slidingwindow.medium;

/**
 * Variation: Longest Subarray with At Most K Distinct Elements
 *
 * Description:
 * Given an integer array nums and an integer k, return the length of the
 * longest subarray that contains at most k distinct elements.
 *
 * Constraints:
 * - 1 <= nums.length <= 5 * 10^4
 * - 1 <= nums[i] <= 10^4
 * - 1 <= k <= nums.length
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * - Can you extend to exactly k distinct elements?
 * - Can you handle negative numbers?
 *
 * LeetCode 340: Longest Substring with At Most K Distinct Characters
 * https://leetcode.com/problems/longest-substring-with-at-most-k-distinct-characters/
 *
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 8+ interviews)
 *
 * Description: Given a string s and integer k, return the length of the longest
 * substring with at most k distinct characters.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - 0 <= k <= 10^5
 * - s consists of English letters
 *
 * Follow-up Questions:
 * 1. How to return the actual substring?
 * 2. What if s contains digits or special characters?
 * 3. How to solve for very large k efficiently?
 */
public class LongestSubarrayWithAtMostKDistinctCharacters {
    // Approach 1: Sliding Window with HashMap - O(n) time, O(k) space
    public int longestSubarray(String s, int k) {
        int left = 0, maxLen = 0;
        java.util.Map<Character, Integer> count = new java.util.HashMap<>();
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            count.put(c, count.getOrDefault(c, 0) + 1);
            while (count.size() > k) {
                char leftChar = s.charAt(left++);
                count.put(leftChar, count.get(leftChar) - 1);
                if (count.get(leftChar) == 0)
                    count.remove(leftChar);
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Approach 2: Sliding Window with Array - O(n) time, O(1) space
    public int longestSubarrayArray(String s, int k) {
        int[] freq = new int[128];
        int left = 0, maxLen = 0, distinct = 0;
        for (int right = 0; right < s.length(); right++) {
            if (freq[s.charAt(right)]++ == 0)
                distinct++;
            while (distinct > k) {
                if (--freq[s.charAt(left++)] == 0)
                    distinct--;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Follow-up 1: Return actual substring
    public String getLongestSubarray(String s, int k) {
        int left = 0, maxLen = 0, start = 0;
        java.util.Map<Character, Integer> count = new java.util.HashMap<>();
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            count.put(c, count.getOrDefault(c, 0) + 1);
            while (count.size() > k) {
                char leftChar = s.charAt(left++);
                count.put(leftChar, count.get(leftChar) - 1);
                if (count.get(leftChar) == 0)
                    count.remove(leftChar);
            }
            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                start = left;
            }
        }
        return s.substring(start, start + maxLen);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LongestSubarrayWithAtMostKDistinctCharacters sol = new LongestSubarrayWithAtMostKDistinctCharacters();
        // Test 1: Basic
        System.out.println("Test 1: Expected 3 -> " + sol.longestSubarray("eceba", 2));
        // Test 2: All unique
        System.out.println("Test 2: Expected 1 -> " + sol.longestSubarray("abcde", 1));
        // Test 3: All same
        System.out.println("Test 3: Expected 5 -> " + sol.longestSubarray("aaaaa", 2));
        // Test 4: Array approach
        System.out.println("Test 4: Expected 3 -> " + sol.longestSubarrayArray("eceba", 2));
        // Test 5: Get actual substring
        System.out.println("Test 5: Expected 'ece' -> " + sol.getLongestSubarray("eceba", 2));
        // Test 6: Edge case, k = 0
        System.out.println("Test 6: Expected 0 -> " + sol.longestSubarray("abc", 0));
        // Test 7: Edge case, empty string
        System.out.println("Test 7: Expected 0 -> " + sol.longestSubarray("", 2));
        // Test 8: Large input
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            sb.append((char) ('a' + (i % 26)));
        System.out.println("Test 8: Large input -> " + sol.longestSubarray(sb.toString(), 5));
        // Test 9: k = s.length
        System.out.println("Test 9: Expected 5 -> " + sol.longestSubarray("abcde", 5));
        // Test 10: k = 1
        System.out.println("Test 10: Expected 1 -> " + sol.longestSubarray("abcde", 1));
        // Test 11: k = 2, all distinct
        System.out.println("Test 11: Expected 2 -> " + sol.longestSubarray("abcdef", 2));
        // Test 12: Substring at end
        System.out.println("Test 12: Expected 2 -> " + sol.longestSubarray("aabbcc", 2));
        // Test 13: Substring at start
        System.out.println("Test 13: Expected 2 -> " + sol.longestSubarray("aabbcc", 2));
        // Test 14: Large k
        System.out.println("Test 14: Expected 10000 -> " + sol.longestSubarray(sb.toString(), 26));
        // Test 15: Special characters
        System.out.println("Test 15: Expected 3 -> " + sol.longestSubarray("a!b@c#", 3));
    }
}
