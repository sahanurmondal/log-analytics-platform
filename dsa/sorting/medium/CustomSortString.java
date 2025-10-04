package sorting.medium;

import java.util.*;

/**
 * LeetCode 791: Custom Sort String
 * https://leetcode.com/problems/custom-sort-string/
 *
 * Description:
 * You are given two strings order and s. All the characters of order are unique
 * and were sorted in some custom order previously.
 * Permute the characters of s so that they match the order that order was
 * sorted.
 *
 * Constraints:
 * - 1 <= order.length <= 26
 * - 1 <= s.length <= 200
 * - order and s consist of lowercase English letters only
 * - All the characters of order are unique
 *
 * Follow-up:
 * - Can you solve it using counting sort?
 * - Can you solve it using a custom comparator?
 * - Can you handle Unicode characters?
 */
public class CustomSortString {
    public String customSortString(String order, String s) {
        // Count frequency of characters in s
        Map<Character, Integer> count = new HashMap<>();
        for (char c : s.toCharArray()) {
            count.put(c, count.getOrDefault(c, 0) + 1);
        }

        StringBuilder result = new StringBuilder();

        // First, append characters in the order specified
        for (char c : order.toCharArray()) {
            if (count.containsKey(c)) {
                int freq = count.get(c);
                for (int i = 0; i < freq; i++) {
                    result.append(c);
                }
                count.remove(c); // Remove from count as we've processed it
            }
        }

        // Then append remaining characters not in order
        for (Map.Entry<Character, Integer> entry : count.entrySet()) {
            char c = entry.getKey();
            int freq = entry.getValue();
            for (int i = 0; i < freq; i++) {
                result.append(c);
            }
        }

        return result.toString();
    }

    public static void main(String[] args) {
        CustomSortString solution = new CustomSortString();

        System.out.println(solution.customSortString("cba", "abcd")); // "cbad"
        System.out.println(solution.customSortString("cbafg", "abcd")); // "cbad"

        // Edge Case: s has no characters from order
        System.out.println(solution.customSortString("abc", "def")); // "def"

        // Edge Case: s has all characters from order
        System.out.println(solution.customSortString("abc", "cab")); // "abc"

        // Edge Case: Repeated characters in s
        System.out.println(solution.customSortString("abc", "aabbcc")); // "aabbcc"

        // Edge Case: Empty s
        System.out.println(solution.customSortString("abc", "")); // ""

        // Edge Case: Single character
        System.out.println(solution.customSortString("a", "a")); // "a"
    }
}
