package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 1358: Number of Substrings Containing All Three Characters
 * https://leetcode.com/problems/number-of-substrings-containing-all-three-characters/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given a string s consisting only of characters 'a', 'b', and
 * 'c',
 * return the number of substrings containing at least one occurrence of each
 * character.
 *
 * Constraints:
 * - 3 <= s.length <= 5 * 10^4
 * - s consists of only 'a', 'b', and 'c'
 * 
 * Follow-up Questions:
 * 1. Generalize to k distinct characters.
 * 2. Find the shortest substring containing all k characters.
 * 3. Count substrings with at least m occurrences of each character.
 * 4. Handle arbitrary character sets.
 */
public class NumberOfSubstringsContainingAllThreeCharacters {

    // Approach 1: Sliding window for 'a', 'b', 'c'
    public int numberOfSubstrings(String s) {
        int[] count = new int[3];
        int left = 0, res = 0;
        for (int right = 0; right < s.length(); right++) {
            count[s.charAt(right) - 'a']++;
            while (count[0] > 0 && count[1] > 0 && count[2] > 0) {
                res += s.length() - right;
                count[s.charAt(left) - 'a']--;
                left++;
            }
        }
        return res;
    }

    // Follow-up 1: Generalize to k distinct characters
    public int numberOfSubstringsKDistinct(String s, Set<Character> chars) {
        Map<Character, Integer> count = new HashMap<>();
        int left = 0, res = 0, k = chars.size();
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (chars.contains(c))
                count.put(c, count.getOrDefault(c, 0) + 1);
            while (count.size() == k && count.values().stream().allMatch(v -> v > 0)) {
                res += s.length() - right;
                char lc = s.charAt(left);
                if (chars.contains(lc)) {
                    count.put(lc, count.get(lc) - 1);
                    if (count.get(lc) == 0)
                        count.remove(lc);
                }
                left++;
            }
        }
        return res;
    }

    // Follow-up 2: Shortest substring containing all k characters
    public int shortestSubstringKDistinct(String s, Set<Character> chars) {
        Map<Character, Integer> count = new HashMap<>();
        int left = 0, minLen = Integer.MAX_VALUE, k = chars.size();
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (chars.contains(c))
                count.put(c, count.getOrDefault(c, 0) + 1);
            while (count.size() == k && count.values().stream().allMatch(v -> v > 0)) {
                minLen = Math.min(minLen, right - left + 1);
                char lc = s.charAt(left);
                if (chars.contains(lc)) {
                    count.put(lc, count.get(lc) - 1);
                    if (count.get(lc) == 0)
                        count.remove(lc);
                }
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }

    // Follow-up 3: Count substrings with at least m occurrences of each character
    public int numberOfSubstringsWithAtLeastM(String s, int m) {
        int[] count = new int[3];
        int left = 0, res = 0;
        for (int right = 0; right < s.length(); right++) {
            count[s.charAt(right) - 'a']++;
            while (count[0] >= m && count[1] >= m && count[2] >= m) {
                res += s.length() - right;
                count[s.charAt(left) - 'a']--;
                left++;
            }
        }
        return res;
    }

    // Follow-up 4: Arbitrary character sets
    public int numberOfSubstringsArbitrary(String s, Set<Character> chars) {
        Map<Character, Integer> count = new HashMap<>();
        int left = 0, res = 0, k = chars.size();
        for (int right = 0; right < s.length(); right++) {
            char c = s.charAt(right);
            if (chars.contains(c))
                count.put(c, count.getOrDefault(c, 0) + 1);
            while (count.size() == k && count.values().stream().allMatch(v -> v > 0)) {
                res += s.length() - right;
                char lc = s.charAt(left);
                if (chars.contains(lc)) {
                    count.put(lc, count.get(lc) - 1);
                    if (count.get(lc) == 0)
                        count.remove(lc);
                }
                left++;
            }
        }
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        NumberOfSubstringsContainingAllThreeCharacters solution = new NumberOfSubstringsContainingAllThreeCharacters();

        // Test case 1: Basic case
        String s1 = "abcabc";
        System.out.println("Test 1 - S: " + s1 + " Expected: 10");
        System.out.println("Result: " + solution.numberOfSubstrings(s1));

        // Test case 2: No valid substring
        String s2 = "aaabbb";
        System.out.println("\nTest 2 - S: " + s2 + " Expected: 0");
        System.out.println("Result: " + solution.numberOfSubstrings(s2));

        // Test case 3: All minimum windows
        Set<Character> chars = new HashSet<>(Arrays.asList('a', 'b', 'c'));
        System.out.println("\nTest 3 - Shortest substring containing all: " +
                solution.shortestSubstringKDistinct(s1, chars));

        // Test case 4: At least m occurrences
        String s3 = "aaabbbccc";
        System.out.println("\nTest 4 - At least 2 occurrences:");
        System.out.println(solution.numberOfSubstringsWithAtLeastM(s3, 2));

        // Test case 5: Arbitrary character set
        String s4 = "xyzxyzxyz";
        Set<Character> chars2 = new HashSet<>(Arrays.asList('x', 'y', 'z'));
        System.out.println("\nTest 5 - Arbitrary set:");
        System.out.println(solution.numberOfSubstringsArbitrary(s4, chars2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty S: " + solution.numberOfSubstrings(""));
        System.out.println("Single char: " + solution.numberOfSubstrings("a"));
        System.out.println("All same: " + solution.numberOfSubstrings("aaaa"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            sb.append("abc");
        long start = System.nanoTime();
        int result = solution.numberOfSubstrings(sb.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
