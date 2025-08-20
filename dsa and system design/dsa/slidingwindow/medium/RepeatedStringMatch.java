package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 686: Repeated String Match
 * https://leetcode.com/problems/repeated-string-match/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given two strings a and b, return the minimum number of times
 * you should repeat string a so that string b is a substring of it. If
 * impossible, return -1.
 *
 * Constraints:
 * - 1 <= a.length, b.length <= 10^4
 * - a and b consist of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you optimize for large strings?
 * 2. Can you handle case-insensitive matching?
 * 3. Can you find the minimum substring match?
 */
public class RepeatedStringMatch {

    // Approach 1: String concatenation and substring check
    public int repeatedStringMatch(String a, String b) {
        int count = 1;
        StringBuilder sb = new StringBuilder(a);
        while (sb.length() < b.length()) {
            sb.append(a);
            count++;
        }
        if (sb.indexOf(b) != -1)
            return count;
        sb.append(a);
        return sb.indexOf(b) != -1 ? count + 1 : -1;
    }

    // Follow-up 1: Case-insensitive matching
    public int repeatedStringMatchIgnoreCase(String a, String b) {
        return repeatedStringMatch(a.toLowerCase(), b.toLowerCase());
    }

    // Follow-up 2: Minimum substring match (returns start index or -1)
    public int minSubstringMatch(String a, String b) {
        int count = 1;
        StringBuilder sb = new StringBuilder(a);
        while (sb.length() < b.length()) {
            sb.append(a);
            count++;
        }
        int idx = sb.indexOf(b);
        if (idx != -1)
            return idx;
        sb.append(a);
        idx = sb.indexOf(b);
        return idx != -1 ? idx : -1;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        RepeatedStringMatch solution = new RepeatedStringMatch();

        // Test case 1: Basic case
        String a1 = "abcd", b1 = "cdabcdab";
        System.out.println("Test 1 - a: " + a1 + ", b: " + b1 + " Expected: 3");
        System.out.println("Result: " + solution.repeatedStringMatch(a1, b1));

        // Test case 2: Impossible case
        String a2 = "abc", b2 = "cabcabca";
        System.out.println("\nTest 2 - Impossible case:");
        System.out.println("Result: " + solution.repeatedStringMatch(a2, b2));

        // Test case 3: Case-insensitive
        System.out.println("\nTest 3 - Case-insensitive:");
        System.out.println(solution.repeatedStringMatchIgnoreCase("Abc", "CABcABCA"));

        // Test case 4: Minimum substring match
        System.out.println("\nTest 4 - Minimum substring match:");
        System.out.println(solution.minSubstringMatch(a1, b1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty strings: " + solution.repeatedStringMatch("", ""));
        System.out.println("Single char: " + solution.repeatedStringMatch("a", "a"));
        System.out.println("b longer than a: " + solution.repeatedStringMatch("a", "aaaaa"));

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sbA = new StringBuilder();
        for (int i = 0; i < 1000; i++)
            sbA.append("abc");
        StringBuilder sbB = new StringBuilder();
        for (int i = 0; i < 2000; i++)
            sbB.append("abc");
        long start = System.nanoTime();
        int result = solution.repeatedStringMatch(sbA.toString(), sbB.toString());
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
