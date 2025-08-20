package design.easy;

import java.util.*;

/**
 * LeetCode 155: Min Stack
 * https://leetcode.com/problems/min-stack/
 *
 * Description: Design a stack that supports push, pop, top, and retrieving the
 * minimum element in constant time.
 * 
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - Methods pop, top and getMin operations will always be called on non-empty
 * stacks
 * - At most 3 * 10^4 calls will be made to push, pop, top, and getMin
 *
 * Follow-up:
 * - Can you implement each operation in O(1) time complexity?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft, Apple
 */
public class MinStack {

    private Stack<Integer> stack;
    private Stack<Integer> minStack;

    public MinStack() {
        stack = new Stack<>();
        minStack = new Stack<>();
    }

    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        }
    }

    public void pop() {
        if (stack.peek().equals(minStack.peek())) {
            minStack.pop();
        }
        stack.pop();
    }

    public int top() {
        return stack.peek();
    }

    public int getMin() {
        return minStack.peek();
    }

    // Alternative implementation - Single stack with pairs
    static class MinStackSingleStack {
        private Stack<int[]> stack; // [value, currentMin]

        public MinStackSingleStack() {
            stack = new Stack<>();
        }

        public void push(int val) {
            int currentMin = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
            stack.push(new int[] { val, currentMin });
        }

        public void pop() {
            stack.pop();
        }

        public int top() {
            return stack.peek()[0];
        }

        public int getMin() {
            return stack.peek()[1];
        }
    }

    public static void main(String[] args) {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println(minStack.getMin()); // Expected: -3
        minStack.pop();
        System.out.println(minStack.top()); // Expected: 0
        System.out.println(minStack.getMin()); // Expected: -2
    }
}
