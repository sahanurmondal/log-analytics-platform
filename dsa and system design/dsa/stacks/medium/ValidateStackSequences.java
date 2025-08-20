package stacks.medium;

import java.util.*;

/**
 * LeetCode 946: Validate Stack Sequences
 * https://leetcode.com/problems/validate-stack-sequences/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given two integer sequences pushed and popped, return true if
 * they could result from a sequence of stack operations.
 *
 * Constraints:
 * - 1 <= pushed.length == popped.length <= 1000
 * - 0 <= pushed[i], popped[i] <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you return the sequence of operations?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle duplicate values?
 */
public class ValidateStackSequences {

    // Approach 1: Simulation
    public boolean validateStackSequences(int[] pushed, int[] popped) {
        Stack<Integer> stack = new Stack<>();
        int j = 0;
        for (int x : pushed) {
            stack.push(x);
            while (!stack.isEmpty() && stack.peek() == popped[j]) {
                stack.pop();
                j++;
            }
        }
        return stack.isEmpty();
    }

    // Follow-up 1: Return sequence of operations
    public List<String> getOperationSequence(int[] pushed, int[] popped) {
        List<String> ops = new ArrayList<>();
        Stack<Integer> stack = new Stack<>();
        int j = 0;
        for (int x : pushed) {
            stack.push(x);
            ops.add("push " + x);
            while (!stack.isEmpty() && stack.peek() == popped[j]) {
                stack.pop();
                ops.add("pop " + popped[j]);
                j++;
            }
        }
        return ops;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ValidateStackSequences solution = new ValidateStackSequences();

        // Test case 1: Basic case
        int[] pushed1 = { 1, 2, 3, 4, 5 }, popped1 = { 4, 5, 3, 2, 1 };
        System.out.println("Test 1 - pushed: " + Arrays.toString(pushed1) + ", popped: " + Arrays.toString(popped1)
                + " Expected: true");
        System.out.println("Result: " + solution.validateStackSequences(pushed1, popped1));

        // Test case 2: Operation sequence
        System.out.println("\nTest 2 - Operation sequence:");
        System.out.println(solution.getOperationSequence(pushed1, popped1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " + solution.validateStackSequences(new int[] { 1 }, new int[] { 1 }));
        System.out.println("All same: " + solution.validateStackSequences(new int[] { 2, 2 }, new int[] { 2, 2 }));
    }
}
