package hashmaps.medium;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * LeetCode 438: Find All Anagrams in a String
 * https://leetcode.com/problems/find-all-anagrams-in-a-string/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given two strings `s` and `p`, return an array of all the start
 * indices of `p`'s anagrams in `s`.
 *
 * Constraints:
 * - 1 <= s.length, p.length <= 3 * 10^4
 * - s and p consist of lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. How can you optimize the comparison of character counts?
 * 2. What if the character set is larger (e.g., Unicode)?
 * 3. Can you solve this without using a HashMap?
 */
public class FindAllAnagramsInAString {

    // Approach 1: Sliding Window with Character Count Array - O(n) time, O(1) space
    // (since charset is fixed)
    public List<Integer> findAnagrams(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }

        int[] pCount = new int[26];
        int[] sCount = new int[26];

        // Initialize the first window
        for (int i = 0; i < p.length(); i++) {
            pCount[p.charAt(i) - 'a']++;
            sCount[s.charAt(i) - 'a']++;
        }

        // Check the first window
        if (Arrays.equals(pCount, sCount)) {
            result.add(0);
        }

        // Slide the window
        for (int i = p.length(); i < s.length(); i++) {
            // Add the new character to the window
            sCount[s.charAt(i) - 'a']++;
            // Remove the old character from the window
            sCount[s.charAt(i - p.length()) - 'a']--;

            // Check if the window is an anagram
            if (Arrays.equals(pCount, sCount)) {
                result.add(i - p.length() + 1);
            }
        }

        return result;
    }

    // Approach 2: Sliding Window with a single count variable - O(n) time, O(1)
    // space
    public List<Integer> findAnagramsOptimized(String s, String p) {
        List<Integer> result = new ArrayList<>();
        if (s.length() < p.length()) {
            return result;
        }

        int[] pCount = new int[26];
        for (char c : p.toCharArray()) {
            pCount[c - 'a']++;
        }

        int left = 0, right = 0, count = p.length();

        while (right < s.length()) {
            // If the current character is in p, decrement count
            if (pCount[s.charAt(right) - 'a'] >= 1) {
                count--;
            }
            pCount[s.charAt(right) - 'a']--;
            right++;

            // If window size is p.length and count is 0, we found an anagram
            if (count == 0) {
                result.add(left);
            }

            // If window size is p.length, slide the window
            if (right - left == p.length()) {
                // If the character leaving the window was in p, increment count
                if (pCount[s.charAt(left) - 'a'] >= 0) {
                    count++;
                }
                pCount[s.charAt(left) - 'a']++;
                left++;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindAllAnagramsInAString solution = new FindAllAnagramsInAString();

        // Test case 1
        String s1 = "cbaebabacd", p1 = "abc";
        System.out.println("Anagrams 1: " + solution.findAnagrams(s1, p1)); // [0, 6]
        System.out.println("Anagrams 1 (Optimized): " + solution.findAnagramsOptimized(s1, p1)); // [0, 6]

        // Test case 2
        String s2 = "abab", p2 = "ab";
        System.out.println("Anagrams 2: " + solution.findAnagrams(s2, p2)); // [0, 1, 2]

        // Test case 3: No anagrams
        String s3 = "abc", p3 = "d";
        System.out.println("Anagrams 3: " + solution.findAnagrams(s3, p3)); // []

        // Test case 4: s is shorter than p
        String s4 = "a", p4 = "ab";
        System.out.println("Anagrams 4: " + solution.findAnagrams(s4, p4)); // []
    }
}
