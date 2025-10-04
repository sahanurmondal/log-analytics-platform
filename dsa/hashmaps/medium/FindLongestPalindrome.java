package hashmaps.medium;

import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 409: Longest Palindrome
 * https://leetcode.com/problems/longest-palindrome/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 6+ interviews)
 *
 * Description: Given a string `s` which consists of lowercase or uppercase
 * letters, return the length of the longest palindrome that can be built with
 * those letters.
 *
 * Constraints:
 * - 1 <= s.length <= 2000
 * - s consists of lowercase and/or uppercase English letters.
 * 
 * Follow-up Questions:
 * 1. Can you construct the longest palindrome string itself?
 * 2. How would this change if the character set was larger (e.g., Unicode)?
 * 3. Can you solve this without a HashMap, using an array?
 */
public class FindLongestPalindrome {

    // Approach 1: HashMap - O(n) time, O(k) space where k is charset size
    public int longestPalindrome(String s) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : s.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        int length = 0;
        boolean hasOdd = false;

        for (int count : freqMap.values()) {
            if (count % 2 == 0) {
                length += count;
            } else {
                length += count - 1;
                hasOdd = true;
            }
        }

        return hasOdd ? length + 1 : length;
    }

    // Approach 2: Array as Frequency Map - O(n) time, O(1) space
    public int longestPalindromeWithArray(String s) {
        int[] freq = new int[128]; // ASCII characters
        for (char c : s.toCharArray()) {
            freq[c]++;
        }

        int length = 0;
        boolean hasOdd = false;

        for (int count : freq) {
            length += (count / 2) * 2;
            if (count % 2 == 1) {
                hasOdd = true;
            }
        }

        return hasOdd ? length + 1 : length;
    }

    public static void main(String[] args) {
        FindLongestPalindrome solution = new FindLongestPalindrome();

        // Test case 1
        String s1 = "abccccdd";
        System.out.println("Longest Palindrome 1 (Map): " + solution.longestPalindrome(s1)); // 7
        System.out.println("Longest Palindrome 1 (Array): " + solution.longestPalindromeWithArray(s1)); // 7

        // Test case 2
        String s2 = "a";
        System.out.println("Longest Palindrome 2: " + solution.longestPalindrome(s2)); // 1

        // Test case 3
        String s3 = "bb";
        System.out.println("Longest Palindrome 3: " + solution.longestPalindrome(s3)); // 2

        // Test case 4: Mixed case
        String s4 = "AaBbCc";
        System.out.println("Longest Palindrome 4: " + solution.longestPalindrome(s4)); // 1
    }
}
