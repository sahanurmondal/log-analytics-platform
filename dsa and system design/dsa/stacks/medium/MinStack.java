package stacks.medium;

import java.util.*;

/**
 * LeetCode 155: Min Stack
 * https://leetcode.com/problems/min-stack/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Design a stack that supports push, pop, top, and retrieving the
 * minimum element in constant time.
 *
 * Constraints:
 * - Operations: push, pop, top, getMin
 * 
 * Follow-up Questions:
 * 1. Can you support getMax?
 * 2. Can you support getMedian?
 * 3. Can you optimize for large number of operations?
 */
public class MinStack {
    private Stack<Integer> stack = new Stack<>();
    private Stack<Integer> minStack = new Stack<>();

    public void push(int x) {
        stack.push(x);
        if (minStack.isEmpty() || x <= minStack.peek())
            minStack.push(x);
    }

    public void pop() {
        if (stack.pop().equals(minStack.peek()))
            minStack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }

    // Follow-up 1: getMax
    private Stack<Integer> maxStack = new Stack<>();

    public int getMax() {
        return maxStack.peek();
    }

    public void pushWithMax(int x) {
        push(x);
        if (maxStack.isEmpty() || x >= maxStack.peek())
            maxStack.push(x);
    }

    public void popWithMax() {
        if (stack.pop().equals(maxStack.peek()))
            maxStack.pop();
        if (stack.pop().equals(minStack.peek()))
            minStack.pop();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MinStack solution = new MinStack();

        // Test case 1: Basic case
        solution.push(-2);
        solution.push(0);
        solution.push(-3);
        System.out.println("Test 1 - getMin Expected: -3");
        System.out.println("Result: " + solution.getMin());
        solution.pop();
        System.out.println("Top: " + solution.top());
        System.out.println("getMin: " + solution.getMin());

        // Test case 2: getMax
        solution.pushWithMax(5);
        solution.pushWithMax(1);
        solution.pushWithMax(7);
        System.out.println("\nTest 2 - getMax:");
        System.out.println(solution.getMax());

        // Edge cases
        System.out.println("\nEdge cases:");
        MinStack emptyStack = new MinStack();
        emptyStack.push(1);
        System.out.println("Single element min: " + emptyStack.getMin());
    }
}
