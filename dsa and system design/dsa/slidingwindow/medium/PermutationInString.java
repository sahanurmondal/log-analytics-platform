package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 567: Permutation in String
 * https://leetcode.com/problems/permutation-in-string/
 * 
 * Companies: Facebook, Amazon, Google
 * Frequency: High
 *
 * Description: Given two strings s1 and s2, return true if s2 contains a
 * permutation of s1.
 *
 * Constraints:
 * - 1 <= s1.length, s2.length <= 10^4
 * - s1 and s2 consist of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you find all starting indices of permutations?
 * 2. Can you optimize for large strings?
 * 3. Can you handle case-insensitive matching?
 */
public class PermutationInString {

    // Approach 1: Sliding window + frequency array
    public boolean checkInclusion(String s1, String s2) {
        int[] s1Count = new int[26], s2Count = new int[26];
        for (char c : s1.toCharArray())
            s1Count[c - 'a']++;
        int n = s2.length(), m = s1.length();
        for (int i = 0; i < n; i++) {
            s2Count[s2.charAt(i) - 'a']++;
            if (i >= m)
                s2Count[s2.charAt(i - m) - 'a']--;
            if (i >= m - 1 && Arrays.equals(s1Count, s2Count))
                return true;
        }
        return false;
    }

    // Follow-up 1: All starting indices of permutations
    public List<Integer> allPermutationIndices(String s1, String s2) {
        List<Integer> result = new ArrayList<>();
        int[] s1Count = new int[26], s2Count = new int[26];
        for (char c : s1.toCharArray())
            s1Count[c - 'a']++;
        int n = s2.length(), m = s1.length();
        for (int i = 0; i < n; i++) {
            s2Count[s2.charAt(i) - 'a']++;
            if (i >= m)
                s2Count[s2.charAt(i - m) - 'a']--;
            if (i >= m - 1 && Arrays.equals(s1Count, s2Count))
                result.add(i - m + 1);
        }
        return result;
    }

    // Follow-up 2: Case-insensitive matching
    public boolean checkInclusionIgnoreCase(String s1, String s2) {
        return checkInclusion(s1.toLowerCase(), s2.toLowerCase());
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        PermutationInString solution = new PermutationInString();

        // Test case 1: Basic case
        String s1 = "ab", s2 = "eidbaooo";
        System.out.println("Test 1 - s1: " + s1 + ", s2: " + s2 + " Expected: true");
        System.out.println("Result: " + solution.checkInclusion(s1, s2));

        // Test case 2: No permutation
        String s3 = "ab", s4 = "eidboaoo";
        System.out.println("\nTest 2 - No permutation:");
        System.out.println("Result: " + solution.checkInclusion(s3, s4));

        // Test case 3: All indices
        System.out.println("\nTest 3 - All permutation indices:");
        System.out.println(solution.allPermutationIndices(s1, s2));

        // Test case 4: Case-insensitive
        System.out.println("\nTest 4 - Case-insensitive:");
        System.out.println(solution.checkInclusionIgnoreCase("Ab", "EIDbaooo"));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty strings: " + solution.checkInclusion("", ""));
        System.out.println("Single char: " + solution.checkInclusion("a", "a"));
        System.out.println("Permutation at end: " + solution.checkInclusion("ab", "oooba"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10000; i++)
            sb.append("ab");
        long start = System.nanoTime();
        boolean result = solution.checkInclusion("ab", sb.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
