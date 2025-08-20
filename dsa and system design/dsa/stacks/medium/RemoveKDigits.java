package stacks.medium;

import java.util.*;

/**
 * LeetCode 402: Remove K Digits
 * https://leetcode.com/problems/remove-k-digits/
 * 
 * Companies: Google, Amazon
 * Frequency: High
 *
 * Description: Given a non-negative integer num represented as a string, remove
 * k digits so that the new number is the smallest possible.
 *
 * Constraints:
 * - 1 <= num.length <= 10^5
 * - 0 <= k <= num.length
 * 
 * Follow-up Questions:
 * 1. Can you remove digits to get the largest number?
 * 2. Can you optimize for large k?
 * 3. Can you handle leading zeros?
 */
public class RemoveKDigits {

    // Approach 1: Monotonic stack
    public String removeKDigits(String num, int k) {
        int n = num.length();
        Stack<Character> stack = new Stack<>();
        for (char c : num.toCharArray()) {
            while (k > 0 && !stack.isEmpty() && stack.peek() > c) {
                stack.pop();
                k--;
            }
            stack.push(c);
        }
        while (k-- > 0)
            stack.pop();
        StringBuilder sb = new StringBuilder();
        for (char c : stack)
            sb.append(c);
        while (sb.length() > 1 && sb.charAt(0) == '0')
            sb.deleteCharAt(0);
        return sb.length() == 0 ? "0" : sb.toString();
    }

    // Follow-up 1: Remove digits to get largest number
    public String removeKDigitsForLargest(String num, int k) {
        int n = num.length();
        Stack<Character> stack = new Stack<>();
        for (char c : num.toCharArray()) {
            while (k > 0 && !stack.isEmpty() && stack.peek() < c) {
                stack.pop();
                k--;
            }
            stack.push(c);
        }
        while (k-- > 0)
            stack.pop();
        StringBuilder sb = new StringBuilder();
        for (char c : stack)
            sb.append(c);
        while (sb.length() > 1 && sb.charAt(0) == '0')
            sb.deleteCharAt(0);
        return sb.length() == 0 ? "0" : sb.toString();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        RemoveKDigits solution = new RemoveKDigits();

        // Test case 1: Basic case
        String num1 = "1432219";
        int k1 = 3;
        System.out.println("Test 1 - num: " + num1 + ", k: " + k1 + " Expected: 1219");
        System.out.println("Result: " + solution.removeKDigits(num1, k1));

        // Test case 2: Largest number
        System.out.println("\nTest 2 - Largest number:");
        System.out.println(solution.removeKDigitsForLargest(num1, k1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("All zeros: " + solution.removeKDigits("0000", 2));
        System.out.println("Single digit: " + solution.removeKDigits("9", 1));
        System.out.println("Remove all digits: " + solution.removeKDigits("12345", 5));
    }
}
