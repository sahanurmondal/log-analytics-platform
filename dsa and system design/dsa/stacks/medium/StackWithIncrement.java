package stacks.medium;

import java.util.*;

/**
 * LeetCode 1381: Design a Stack With Increment Operation
 * https://leetcode.com/problems/design-a-stack-with-increment-operation/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Implement a stack with push, pop, and increment operations.
 *
 * Constraints:
 * - 1 <= maxSize <= 1000
 * - 1 <= val <= 100
 * - 1 <= k <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you support decrement?
 * 2. Can you optimize for large number of operations?
 * 3. Can you handle negative values?
 */
public class StackWithIncrement {
    private int[] stack;
    private int[] inc;
    private int top;

    public StackWithIncrement(int maxSize) {
        stack = new int[maxSize];
        inc = new int[maxSize];
        top = -1;
    }

    public void push(int x) {
        if (top + 1 < stack.length)
            stack[++top] = x;
    }

    public int pop() {
        if (top == -1)
            return -1;
        int res = stack[top] + inc[top];
        if (top > 0)
            inc[top - 1] += inc[top];
        inc[top] = 0;
        top--;
        return res;
    }

    public void increment(int k, int val) {
        int idx = Math.min(k, top + 1) - 1;
        if (idx >= 0)
            inc[idx] += val;
    }

    // Follow-up 1: Decrement operation
    public void decrement(int k, int val) {
        int idx = Math.min(k, top + 1) - 1;
        if (idx >= 0)
            inc[idx] -= val;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        StackWithIncrement solution = new StackWithIncrement(3);

        // Test case 1: Basic case
        solution.push(1);
        solution.push(2);
        System.out.println("Test 1 - pop: " + solution.pop());
        solution.push(2);
        solution.push(3);
        solution.increment(5, 100);
        System.out.println("pop: " + solution.pop());
        System.out.println("pop: " + solution.pop());
        System.out.println("pop: " + solution.pop());

        // Test case 2: Decrement
        solution.push(5);
        solution.decrement(1, 2);
        System.out.println("\nTest 2 - Decrement:");
        System.out.println(solution.pop());

        // Edge cases
        System.out.println("\nEdge cases:");
        StackWithIncrement emptyStack = new StackWithIncrement(1);
        System.out.println("Pop empty: " + emptyStack.pop());
    }
}
