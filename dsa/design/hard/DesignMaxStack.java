package design.hard;

import java.util.*;

/**
 * LeetCode 716: Max Stack
 * https://leetcode.com/problems/max-stack/
 *
 * Description: Design a max stack data structure that supports the stack
 * operations and supports finding the stack's maximum element.
 * 
 * Constraints:
 * - -10^7 <= x <= 10^7
 * - At most 10^5 calls will be made to push, pop, top, peekMax, and popMax
 *
 * Follow-up:
 * - Can you come up with a solution that supports O(1) for each top operation
 * and O(logn) for each other operation?
 * 
 * Time Complexity: O(1) for push/top/peekMax, O(log n) for pop/popMax
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon
 */
public class DesignMaxStack {

    class Node {
        int val;
        Node prev, next;

        Node(int val) {
            this.val = val;
        }
    }

    private Node head, tail;
    private TreeMap<Integer, List<Node>> maxMap;

    public DesignMaxStack() {
        head = new Node(0);
        tail = new Node(0);
        head.next = tail;
        tail.prev = head;
        maxMap = new TreeMap<>();
    }

    public void push(int x) {
        Node node = new Node(x);

        // Add to doubly linked list (stack)
        node.next = tail;
        node.prev = tail.prev;
        tail.prev.next = node;
        tail.prev = node;

        // Add to TreeMap
        maxMap.computeIfAbsent(x, k -> new ArrayList<>()).add(node);
    }

    public int pop() {
        Node node = tail.prev;
        int val = node.val;

        // Remove from doubly linked list
        removeNode(node);

        // Remove from TreeMap
        List<Node> list = maxMap.get(val);
        list.remove(list.size() - 1);
        if (list.isEmpty()) {
            maxMap.remove(val);
        }

        return val;
    }

    public int top() {
        return tail.prev.val;
    }

    public int peekMax() {
        return maxMap.lastKey();
    }

    public int popMax() {
        int max = peekMax();
        List<Node> list = maxMap.get(max);
        Node node = list.remove(list.size() - 1);

        if (list.isEmpty()) {
            maxMap.remove(max);
        }

        removeNode(node);
        return max;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Alternative implementation using two stacks
    static class MaxStackTwoStacks {
        private Stack<Integer> stack;
        private Stack<Integer> maxStack;

        public MaxStackTwoStacks() {
            stack = new Stack<>();
            maxStack = new Stack<>();
        }

        public void push(int x) {
            stack.push(x);
            if (maxStack.isEmpty() || x >= maxStack.peek()) {
                maxStack.push(x);
            }
        }

        public int pop() {
            int val = stack.pop();
            if (val == maxStack.peek()) {
                maxStack.pop();
            }
            return val;
        }

        public int top() {
            return stack.peek();
        }

        public int peekMax() {
            return maxStack.peek();
        }

        public int popMax() {
            int max = maxStack.peek();
            Stack<Integer> temp = new Stack<>();

            // Pop elements until we find max
            while (stack.peek() != max) {
                temp.push(pop());
            }

            // Pop the max
            pop();

            // Push back the temp elements
            while (!temp.isEmpty()) {
                push(temp.pop());
            }

            return max;
        }
    }

    public static void main(String[] args) {
        DesignMaxStack stk = new DesignMaxStack();
        stk.push(5);
        stk.push(1);
        stk.push(5);
        System.out.println(stk.top()); // Expected: 5
        System.out.println(stk.popMax()); // Expected: 5
        System.out.println(stk.top()); // Expected: 1
        System.out.println(stk.peekMax()); // Expected: 5
        System.out.println(stk.pop()); // Expected: 1
        System.out.println(stk.top()); // Expected: 5

        // Test two stacks approach
        MaxStackTwoStacks stk2 = new MaxStackTwoStacks();
        stk2.push(5);
        stk2.push(1);
        stk2.push(5);
        System.out.println("Two stacks - top: " + stk2.top()); // Expected: 5
        System.out.println("Two stacks - popMax: " + stk2.popMax()); // Expected: 5
        System.out.println("Two stacks - peekMax: " + stk2.peekMax()); // Expected: 5
    }
}
