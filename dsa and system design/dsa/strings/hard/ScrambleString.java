package strings.hard;

import java.util.*;

/**
 * LeetCode 87: Scramble String
 * https://leetcode.com/problems/scramble-string/
 * 
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description: Given two strings s1 and s2 of the same length, return true if
 * s2 is a scrambled string of s1.
 *
 * Constraints:
 * - 1 <= s1.length, s2.length <= 30
 * - s1 and s2 consist of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you count the number of ways to scramble?
 * 2. Can you generate all possible scrambles?
 * 3. Can you optimize for large strings?
 */
public class ScrambleString {

    // Approach 1: Recursive with memoization - O(n^4) time
    public boolean isScramble(String s1, String s2) {
        Map<String, Boolean> memo = new HashMap<>();
        return isScrambleHelper(s1, s2, memo);
    }

    private boolean isScrambleHelper(String s1, String s2, Map<String, Boolean> memo) {
        if (s1.equals(s2))
            return true;
        if (s1.length() != s2.length())
            return false;

        String key = s1 + "," + s2;
        if (memo.containsKey(key))
            return memo.get(key);

        if (!hasSameChars(s1, s2)) {
            memo.put(key, false);
            return false;
        }

        for (int i = 1; i < s1.length(); i++) {
            // Case 1: No swap
            if (isScrambleHelper(s1.substring(0, i), s2.substring(0, i), memo) &&
                    isScrambleHelper(s1.substring(i), s2.substring(i), memo)) {
                memo.put(key, true);
                return true;
            }

            // Case 2: Swap
            if (isScrambleHelper(s1.substring(0, i), s2.substring(s2.length() - i), memo) &&
                    isScrambleHelper(s1.substring(i), s2.substring(0, s2.length() - i), memo)) {
                memo.put(key, true);
                return true;
            }
        }

        memo.put(key, false);
        return false;
    }

    // Follow-up 1: Count number of ways to scramble
    public int countScrambleWays(String s1, String s2) {
        Map<String, Integer> memo = new HashMap<>();
        return countScrambleHelper(s1, s2, memo);
    }

    private int countScrambleHelper(String s1, String s2, Map<String, Integer> memo) {
        if (s1.equals(s2))
            return 1;
        if (s1.length() != s2.length() || !hasSameChars(s1, s2))
            return 0;

        String key = s1 + "," + s2;
        if (memo.containsKey(key))
            return memo.get(key);

        int count = 0;
        for (int i = 1; i < s1.length(); i++) {
            // Case 1: No swap
            count += countScrambleHelper(s1.substring(0, i), s2.substring(0, i), memo) *
                    countScrambleHelper(s1.substring(i), s2.substring(i), memo);

            // Case 2: Swap
            count += countScrambleHelper(s1.substring(0, i), s2.substring(s2.length() - i), memo) *
                    countScrambleHelper(s1.substring(i), s2.substring(0, s2.length() - i), memo);
        }

        memo.put(key, count);
        return count;
    }

    // Follow-up 2: Generate all possible scrambles
    public List<String> generateAllScrambles(String s) {
        Set<String> result = new HashSet<>();
        generateScrambleHelper(s, result);
        return new ArrayList<>(result);
    }

    private void generateScrambleHelper(String s, Set<String> result) {
        if (s.length() <= 1) {
            result.add(s);
            return;
        }

        result.add(s);
        for (int i = 1; i < s.length(); i++) {
            String left = s.substring(0, i);
            String right = s.substring(i);

            List<String> leftScrambles = generateAllScrambles(left);
            List<String> rightScrambles = generateAllScrambles(right);

            for (String l : leftScrambles) {
                for (String r : rightScrambles) {
                    result.add(l + r);
                    result.add(r + l);
                }
            }
        }
    }

    // Helper method to check if two strings have same characters
    private boolean hasSameChars(String s1, String s2) {
        if (s1.length() != s2.length())
            return false;
        int[] count = new int[26];
        for (int i = 0; i < s1.length(); i++) {
            count[s1.charAt(i) - 'a']++;
            count[s2.charAt(i) - 'a']--;
        }
        for (int c : count) {
            if (c != 0)
                return false;
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ScrambleString solution = new ScrambleString();

        // Test case 1: Basic case
        String s1 = "great", s2 = "rgeat";
        System.out.println("Test 1 - s1: " + s1 + ", s2: " + s2 + " Expected: true");
        System.out.println("Result: " + solution.isScramble(s1, s2));

        // Test case 2: Count scramble ways
        System.out.println("\nTest 2 - Count scramble ways:");
        System.out.println("Ways: " + solution.countScrambleWays(s1, s2));

        // Test case 3: Generate all scrambles (small string)
        String s3 = "abc";
        System.out.println("\nTest 3 - All scrambles of '" + s3 + "':");
        List<String> scrambles = solution.generateAllScrambles(s3);
        System.out.println("Count: " + scrambles.size());
        for (String scramble : scrambles) {
            System.out.print(scramble + " ");
        }
        System.out.println();

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Same strings: " + solution.isScramble("abc", "abc"));
        System.out.println("Different lengths: " + solution.isScramble("abc", "abcd"));
        System.out.println("Single char: " + solution.isScramble("a", "a"));
        System.out.println("Different chars: " + solution.isScramble("abc", "def"));

        // Stress test
        System.out.println("\nStress test:");
        String large1 = "abcdefghijklmn";
        String large2 = "nmlkjihgfedcba";
        long start = System.nanoTime();
        boolean result = solution.isScramble(large1, large2);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
