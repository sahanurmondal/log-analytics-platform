package stacks.hard;

import java.util.*;

/**
 * LeetCode 716: Max Stack
 * URL: https://leetcode.com/problems/max-stack/
 * Difficulty: Hard
 * 
 * Companies: Amazon, Google, Microsoft, LinkedIn, Apple
 * Frequency: High
 * 
 * Description:
 * Design a max stack data structure that supports push, pop, top, peekMax, and
 * popMax operations.
 * All operations should be efficient.
 * 
 * Constraints:
 * - -10^7 <= x <= 10^7
 * - At most 10^4 calls will be made to operations
 * - All operations should be as efficient as possible
 * 
 * Follow-up Questions:
 * 1. Can you implement all operations in O(log n) time?
 * 2. How would you extend to support kth maximum?
 * 3. How would you handle duplicate maximum values?
 * 4. How would you optimize for memory usage?
 * 5. How would you make it thread-safe?
 */
public class MaxStack {
    private Stack<Integer> stack;
    private Stack<Integer> maxStack;

    // Approach 1: Two Stack Implementation
    public MaxStack() {
        stack = new Stack<>();
        maxStack = new Stack<>();
    }

    /**
     * Push element onto stack
     * Time: O(1), Space: O(1)
     */
    public void push(int x) {
        stack.push(x);
        if (maxStack.isEmpty() || x >= maxStack.peek()) {
            maxStack.push(x);
        }
    }

    /**
     * Pop and return top element
     * Time: O(1), Space: O(1)
     */
    public int pop() {
        if (stack.isEmpty())
            return -1;

        int popped = stack.pop();
        if (!maxStack.isEmpty() && popped == maxStack.peek()) {
            maxStack.pop();
        }
        return popped;
    }

    /**
     * Get top element without removing
     * Time: O(1), Space: O(1)
     */
    public int top() {
        return stack.isEmpty() ? -1 : stack.peek();
    }

    /**
     * Get maximum element without removing
     * Time: O(1), Space: O(1)
     */
    public int peekMax() {
        return maxStack.isEmpty() ? -1 : maxStack.peek();
    }

    /**
     * Pop and return maximum element
     * Time: O(n), Space: O(n)
     */
    public int popMax() {
        if (maxStack.isEmpty())
            return -1;

        int max = maxStack.peek();
        Stack<Integer> temp = new Stack<>();

        // Pop elements until we find the max
        while (!stack.isEmpty() && stack.peek() != max) {
            temp.push(pop());
        }

        // Remove the max element
        int result = pop();

        // Restore the popped elements
        while (!temp.isEmpty()) {
            push(temp.pop());
        }

        return result;
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public int size() {
        return stack.size();
    }

    // Approach 2: TreeMap + Doubly Linked List (O(log n) for all operations)
    static class MaxStackOptimized {
        private Node head, tail;
        private TreeMap<Integer, List<Node>> map;
        private int id;

        class Node {
            int val, id;
            Node prev, next;

            Node(int val, int id) {
                this.val = val;
                this.id = id;
            }
        }

        public MaxStackOptimized() {
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
            map = new TreeMap<>();
            id = 0;
        }

        public void push(int x) {
            Node node = new Node(x, id++);
            map.computeIfAbsent(x, k -> new ArrayList<>()).add(node);
            addToTail(node);
        }

        public int pop() {
            return removeNode(tail.prev).val;
        }

        public int top() {
            return tail.prev.val;
        }

        public int peekMax() {
            return map.lastKey();
        }

        public int popMax() {
            int max = peekMax();
            List<Node> nodes = map.get(max);
            Node node = nodes.remove(nodes.size() - 1);
            if (nodes.isEmpty())
                map.remove(max);
            return removeNode(node).val;
        }

        private void addToTail(Node node) {
            node.prev = tail.prev;
            node.next = tail;
            tail.prev.next = node;
            tail.prev = node;
        }

        private Node removeNode(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
            return node;
        }
    }

    public static void main(String[] args) {
        MaxStack maxStack = new MaxStack();

        // Test Case 1: Basic operations
        maxStack.push(5);
        maxStack.push(1);
        maxStack.push(5);
        System.out.println("Top: " + maxStack.top()); // 5
        System.out.println("PopMax: " + maxStack.popMax()); // 5
        System.out.println("Top after popMax: " + maxStack.top()); // 1
        System.out.println("PeekMax: " + maxStack.peekMax()); // 5
        System.out.println("Pop: " + maxStack.pop()); // 1
        System.out.println("Top after pop: " + maxStack.top()); // 5

        // Test Case 2: Empty stack
        MaxStack empty = new MaxStack();
        System.out.println("Empty top: " + empty.top()); // -1
        System.out.println("Empty peekMax: " + empty.peekMax()); // -1

        // Test Case 3: Single element
        MaxStack single = new MaxStack();
        single.push(42);
        System.out.println("Single peekMax: " + single.peekMax()); // 42
        System.out.println("Single popMax: " + single.popMax()); // 42
        System.out.println("After popMax empty: " + single.isEmpty()); // true

        // Test Case 4: Duplicate maximums
        MaxStack dup = new MaxStack();
        dup.push(3);
        dup.push(3);
        dup.push(1);
        dup.push(3);
        System.out.println("Dup peekMax: " + dup.peekMax()); // 3
        System.out.println("Dup popMax: " + dup.popMax()); // 3
        System.out.println("Dup peekMax after pop: " + dup.peekMax()); // 3

        // Test Case 5: Negative numbers
        MaxStack neg = new MaxStack();
        neg.push(-1);
        neg.push(-5);
        neg.push(-2);
        System.out.println("Negative peekMax: " + neg.peekMax()); // -1

        // Test Case 6: TreeMap implementation
        MaxStackOptimized opt = new MaxStackOptimized();
        opt.push(5);
        opt.push(1);
        opt.push(5);
        System.out.println("Optimized top: " + opt.top()); // 5
        System.out.println("Optimized popMax: " + opt.popMax()); // 5
        System.out.println("Optimized peekMax: " + opt.peekMax()); // 5

        // Test Case 7: Ascending sequence
        MaxStack asc = new MaxStack();
        for (int i = 1; i <= 5; i++) {
            asc.push(i);
        }
        System.out.println("Ascending peekMax: " + asc.peekMax()); // 5
        asc.popMax();
        System.out.println("After popMax: " + asc.peekMax()); // 4

        // Test Case 8: Descending sequence
        MaxStack desc = new MaxStack();
        for (int i = 5; i >= 1; i--) {
            desc.push(i);
        }
        System.out.println("Descending peekMax: " + desc.peekMax()); // 5
        System.out.println("Descending top: " + desc.top()); // 1

        // Test Case 9: Mixed operations
        MaxStack mixed = new MaxStack();
        mixed.push(2);
        mixed.push(1);
        mixed.push(5);
        mixed.push(3);
        mixed.push(4);
        System.out.println("Mixed size: " + mixed.size()); // 5
        mixed.popMax(); // removes 5
        mixed.pop(); // removes 4
        System.out.println("Mixed final peekMax: " + mixed.peekMax()); // 3

        // Test Case 10: All same values
        MaxStack same = new MaxStack();
        same.push(7);
        same.push(7);
        same.push(7);
        System.out.println("Same peekMax: " + same.peekMax()); // 7
        same.popMax();
        System.out.println("Same after popMax: " + same.peekMax()); // 7
    }
}
