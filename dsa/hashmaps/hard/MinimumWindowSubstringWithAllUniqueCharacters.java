package hashmaps.hard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * LeetCode 76 (variation): Minimum Window Substring with All Unique Characters
 * https://leetcode.com/problems/minimum-window-substring/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Hard (Asked in 15+ interviews)
 *
 * Description: Given a string `s`, find the minimum window substring that
 * contains all unique characters of `s`.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of uppercase and lowercase English letters.
 * 
 * Follow-up Questions:
 * 1. How is this different from the original Minimum Window Substring problem?
 * 2. Can you explain the sliding window logic in detail?
 * 3. What is the time and space complexity?
 */
public class MinimumWindowSubstringWithAllUniqueCharacters {

    // Approach 1: Sliding Window with HashMap - O(n) time, O(k) space where k is
    // unique chars in s
    public String minWindow(String s) {
        Set<Character> uniqueChars = new HashSet<>();
        for (char c : s.toCharArray()) {
            uniqueChars.add(c);
        }

        String t = "";
        for (Character c : uniqueChars) {
            t += c;
        }

        if (s.length() < t.length()) {
            return "";
        }

        Map<Character, Integer> tCount = new HashMap<>();
        for (char c : t.toCharArray()) {
            tCount.put(c, tCount.getOrDefault(c, 0) + 1);
        }

        int left = 0;
        int minLen = Integer.MAX_VALUE;
        int minStart = 0;
        int required = tCount.size();
        int formed = 0;

        Map<Character, Integer> windowCount = new HashMap<>();

        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            windowCount.put(c, windowCount.getOrDefault(c, 0) + 1);

            if (tCount.containsKey(c) && windowCount.get(c).intValue() == tCount.get(c).intValue()) {
                formed++;
            }

            while (left <= right && formed == required) {
                if (right - left + 1 < minLen) {
                    minLen = right - left + 1;
                    minStart = left;
                }

                char leftChar = s.charAt(left);
                windowCount.put(leftChar, windowCount.get(leftChar) - 1);

                if (tCount.containsKey(leftChar) && windowCount.get(leftChar) < tCount.get(leftChar)) {
                    formed--;
                }

                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? "" : s.substring(minStart, minStart + minLen);
    }

    public static void main(String[] args) {
        MinimumWindowSubstringWithAllUniqueCharacters solution = new MinimumWindowSubstringWithAllUniqueCharacters();

        // Test case 1
        String s1 = "ADOBECODEBANC"; // Unique chars: A,D,O,B,E,C,N
        System.out.println("Min Window 1: " + solution.minWindow(s1)); // "CODEBANC"

        // Test case 2
        String s2 = "aabflmflnas"; // Unique chars: a,b,f,l,m,n,s
        System.out.println("Min Window 2: " + solution.minWindow(s2)); // "bflmflna"

        // Test case 3
        String s3 = "a";
        System.out.println("Min Window 3: " + solution.minWindow(s3)); // "a"
    }
}
