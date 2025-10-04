package strings.medium;

import java.util.*;

/**
 * LeetCode 1047: Remove All Adjacent Duplicates In String
 * https://leetcode.com/problems/remove-all-adjacent-duplicates-in-string/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: You are given a string s consisting of lowercase English
 * letters. Remove all duplicate adjacent characters.
 *
 * Constraints:
 * - 1 <= s.length <= 10^5
 * - s consists of lowercase English letters
 * 
 * Follow-up Questions:
 * 1. Can you remove k adjacent duplicates?
 * 2. Can you preserve one copy of duplicates?
 * 3. Can you handle case-insensitive duplicates?
 */
public class RemoveDuplicates {

    // Approach 1: Stack approach (O(n) time, O(n) space)
    public String removeDuplicates(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek() == c) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        StringBuilder result = new StringBuilder();
        for (char c : stack)
            result.append(c);
        return result.toString();
    }

    // Follow-up 1: Remove k adjacent duplicates
    public String removeDuplicatesK(String s, int k) {
        Stack<int[]> stack = new Stack<>(); // [character, count]
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && stack.peek()[0] == c) {
                stack.peek()[1]++;
                if (stack.peek()[1] == k)
                    stack.pop();
            } else {
                stack.push(new int[] { c, 1 });
            }
        }
        StringBuilder result = new StringBuilder();
        for (int[] pair : stack) {
            for (int i = 0; i < pair[1]; i++) {
                result.append((char) pair[0]);
            }
        }
        return result.toString();
    }

    // Follow-up 2: Preserve one copy of duplicates
    public String removeDuplicatesKeepOne(String s) {
        StringBuilder result = new StringBuilder();
        char prev = '\0';
        for (char c : s.toCharArray()) {
            if (c != prev) {
                result.append(c);
                prev = c;
            }
        }
        return result.toString();
    }

    // Follow-up 3: Case-insensitive duplicates
    public String removeDuplicatesIgnoreCase(String s) {
        Stack<Character> stack = new Stack<>();
        for (char c : s.toCharArray()) {
            if (!stack.isEmpty() && Character.toLowerCase(stack.peek()) == Character.toLowerCase(c)) {
                stack.pop();
            } else {
                stack.push(c);
            }
        }
        StringBuilder result = new StringBuilder();
        for (char c : stack)
            result.append(c);
        return result.toString();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        RemoveDuplicates solution = new RemoveDuplicates();

        // Test case 1: Basic case
        String s1 = "abbaca";
        System.out.println("Test 1 - s: " + s1 + " Expected: ca");
        System.out.println("Result: " + solution.removeDuplicates(s1));

        // Test case 2: Remove k duplicates
        String s2 = "deeedbbcccbdaa";
        int k = 3;
        System.out.println("\nTest 2 - Remove " + k + " duplicates:");
        System.out.println("Result: " + solution.removeDuplicatesK(s2, k));

        // Test case 3: Keep one copy
        String s3 = "aabbccddee";
        System.out.println("\nTest 3 - Keep one copy:");
        System.out.println("Result: " + solution.removeDuplicatesKeepOne(s3));

        // Test case 4: Case-insensitive
        String s4 = "aAbBcC";
        System.out.println("\nTest 4 - Case-insensitive:");
        System.out.println("Result: " + solution.removeDuplicatesIgnoreCase(s4));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty string: '" + solution.removeDuplicates("") + "'");
        System.out.println("Single char: '" + solution.removeDuplicates("a") + "'");
        System.out.println("All same: '" + solution.removeDuplicates("aaaa") + "'");
        System.out.println("No duplicates: '" + solution.removeDuplicates("abcdef") + "'");

        // Stress test
        System.out.println("\nStress test:");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 50000; i++) {
            sb.append("ab");
        }
        long start = System.nanoTime();
        String result = solution.removeDuplicates(sb.toString());
        long end = System.nanoTime();
        System.out.println("Result length: " + result.length() + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
