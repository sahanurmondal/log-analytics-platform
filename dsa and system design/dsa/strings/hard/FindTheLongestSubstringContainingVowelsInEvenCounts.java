package strings.hard;

import java.util.*;

/**
 * LeetCode 1371: Find the Longest Substring Containing Vowels in Even Counts
 * https://leetcode.com/problems/find-the-longest-substring-containing-vowels-in-even-counts/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the string s, return the size of the longest substring
 * containing each vowel an even number of times.
 *
 * Constraints:
 * - 1 <= s.length <= 5 * 10^5
 * - s contains only lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you find all such substrings?
 * 2. Can you handle odd counts requirement?
 * 3. Can you optimize for large strings?
 */
public class FindTheLongestSubstringContainingVowelsInEvenCounts {

    // Approach 1: Bitmask + HashMap - O(n) time, O(1) space
    public int findTheLongestSubstring(String s) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, -1);
        int mask = 0, maxLen = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 'a')
                mask ^= 1;
            else if (c == 'e')
                mask ^= 2;
            else if (c == 'i')
                mask ^= 4;
            else if (c == 'o')
                mask ^= 8;
            else if (c == 'u')
                mask ^= 16;

            if (map.containsKey(mask)) {
                maxLen = Math.max(maxLen, i - map.get(mask));
            } else {
                map.put(mask, i);
            }
        }
        return maxLen;
    }

    // Follow-up 1: Find all such substrings
    public List<String> findAllSubstrings(String s) {
        List<String> result = new ArrayList<>();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, -1);
        int mask = 0, maxLen = 0;

        // First find max length
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 'a')
                mask ^= 1;
            else if (c == 'e')
                mask ^= 2;
            else if (c == 'i')
                mask ^= 4;
            else if (c == 'o')
                mask ^= 8;
            else if (c == 'u')
                mask ^= 16;

            if (map.containsKey(mask)) {
                maxLen = Math.max(maxLen, i - map.get(mask));
            } else {
                map.put(mask, i);
            }
        }

        // Find all substrings of max length
        for (int i = 0; i <= s.length() - maxLen; i++) {
            String sub = s.substring(i, i + maxLen);
            if (hasEvenVowelCounts(sub)) {
                result.add(sub);
            }
        }
        return result;
    }

    // Follow-up 2: Handle odd counts requirement
    public int findLongestSubstringOddVowels(String s) {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(0, -1);
        int mask = 0, maxLen = 0;
        int targetMask = (1 << 5) - 1; // All vowels odd

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == 'a')
                mask ^= 1;
            else if (c == 'e')
                mask ^= 2;
            else if (c == 'i')
                mask ^= 4;
            else if (c == 'o')
                mask ^= 8;
            else if (c == 'u')
                mask ^= 16;

            if (map.containsKey(mask ^ targetMask)) {
                maxLen = Math.max(maxLen, i - map.get(mask ^ targetMask));
            }
            if (!map.containsKey(mask)) {
                map.put(mask, i);
            }
        }
        return maxLen;
    }

    // Helper: Check if string has even vowel counts
    private boolean hasEvenVowelCounts(String s) {
        int[] count = new int[5];
        for (char c : s.toCharArray()) {
            if (c == 'a')
                count[0]++;
            else if (c == 'e')
                count[1]++;
            else if (c == 'i')
                count[2]++;
            else if (c == 'o')
                count[3]++;
            else if (c == 'u')
                count[4]++;
        }
        for (int cnt : count) {
            if (cnt % 2 != 0)
                return false;
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindTheLongestSubstringContainingVowelsInEvenCounts solution = new FindTheLongestSubstringContainingVowelsInEvenCounts();

        // Test case 1: Basic case
        String s1 = "eleetminicoworoep";
        System.out.println("Test 1 - s: " + s1 + " Expected: 13");
        System.out.println("Result: " + solution.findTheLongestSubstring(s1));

        // Test case 2: All consonants
        String s2 = "bcdfg";
        System.out.println("\nTest 2 - All consonants:");
        System.out.println("Result: " + solution.findTheLongestSubstring(s2));

        // Test case 3: Find all substrings
        String s3 = "aeiou";
        System.out.println("\nTest 3 - Find all substrings:");
        List<String> allSubs = solution.findAllSubstrings(s3);
        for (String sub : allSubs)
            System.out.println(sub);

        // Test case 4: Odd vowels requirement
        System.out.println("\nTest 4 - Odd vowels requirement:");
        System.out.println("Result: " + solution.findLongestSubstringOddVowels(s1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single vowel: " + solution.findTheLongestSubstring("a"));
        System.out.println("Empty string: " + solution.findTheLongestSubstring(""));
        System.out.println("No vowels: " + solution.findTheLongestSubstring("bcdfg"));
        System.out.println("All same vowel: " + solution.findTheLongestSubstring("aaaa"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50000; i++) {
            sb.append("aeiou");
        }
        long start = System.nanoTime();
        int result = solution.findTheLongestSubstring(sb.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
