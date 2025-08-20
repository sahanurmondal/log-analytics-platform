package design.medium;

import java.util.*;

/**
 * LeetCode 155: Min Stack
 * https://leetcode.com/problems/min-stack/
 *
 * Description:
 * Design a stack that supports push, pop, top, and retrieving the minimum
 * element in constant time.
 *
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 3 * 10^4 calls will be made to push, pop, top, and getMin.
 *
 * Follow-up:
 * - Can you optimize for thread safety?
 * - Can you generalize for max stack?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Two stacks: One for values, one for minimums
 * 2. Single stack with pairs: Store (value, currentMin)
 * 3. Linked list approach: Each node stores value and min
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class DesignStackWithMin {
    private Stack<Integer> stack;
    private Stack<Integer> minStack;

    // Main optimized solution - Two stacks
    public DesignStackWithMin() {
        stack = new Stack<>();
        minStack = new Stack<>();
    }

    public void push(int val) {
        stack.push(val);
        if (minStack.isEmpty() || val <= minStack.peek()) {
            minStack.push(val);
        }
    }

    public int pop() {
        if (stack.isEmpty())
            return -1;

        int val = stack.pop();
        if (val == minStack.peek()) {
            minStack.pop();
        }
        return val;
    }

    public int top() {
        return stack.isEmpty() ? -1 : stack.peek();
    }

    public int getMin() {
        return minStack.isEmpty() ? -1 : minStack.peek();
    }

    // Alternative solution - Single stack with pairs
    static class MinStackSingleStack {
        private Stack<int[]> stack; // [value, currentMin]

        public MinStackSingleStack() {
            stack = new Stack<>();
        }

        public void push(int val) {
            int currentMin = stack.isEmpty() ? val : Math.min(val, stack.peek()[1]);
            stack.push(new int[] { val, currentMin });
        }

        public int pop() {
            return stack.isEmpty() ? -1 : stack.pop()[0];
        }

        public int top() {
            return stack.isEmpty() ? -1 : stack.peek()[0];
        }

        public int getMin() {
            return stack.isEmpty() ? -1 : stack.peek()[1];
        }
    }

    // Follow-up optimization - Linked List approach
    static class MinStackLinkedList {
        private Node head;

        class Node {
            int val;
            int min;
            Node next;

            Node(int val, int min, Node next) {
                this.val = val;
                this.min = min;
                this.next = next;
            }
        }

        public void push(int val) {
            if (head == null) {
                head = new Node(val, val, null);
            } else {
                head = new Node(val, Math.min(val, head.min), head);
            }
        }

        public int pop() {
            if (head == null)
                return -1;
            int val = head.val;
            head = head.next;
            return val;
        }

        public int top() {
            return head == null ? -1 : head.val;
        }

        public int getMin() {
            return head == null ? -1 : head.min;
        }
    }

    public static void main(String[] args) {
        DesignStackWithMin stack = new DesignStackWithMin();
        stack.push(-2);
        stack.push(0);
        stack.push(-3);
        System.out.println(stack.getMin()); // -3
        stack.pop();
        System.out.println(stack.top()); // 0
        System.out.println(stack.getMin()); // -2
        // Edge Case: Pop from empty stack
        stack.pop();
        stack.pop();
        System.out.println(stack.pop()); // Should handle gracefully
        // Edge Case: Get min from empty stack
        System.out.println(stack.getMin()); // Should handle gracefully
        // Edge Case: Push duplicate min
        stack.push(-2);
        stack.push(-2);
        System.out.println(stack.getMin()); // -2
        stack.pop();
        System.out.println(stack.getMin()); // -2
    }
}
