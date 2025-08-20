package design.easy;

import java.util.*;

/**
 * LeetCode 232: Implement Queue using Stacks
 * https://leetcode.com/problems/implement-queue-using-stacks/
 *
 * Description: Implement a first in first out (FIFO) queue using only two
 * stacks.
 * 
 * Constraints:
 * - 1 <= x <= 9
 * - At most 100 calls will be made to push, pop, peek, and empty
 * - All the calls to pop and peek are valid
 *
 * Follow-up:
 * - Can you implement the queue such that each operation is amortized O(1) time
 * complexity?
 * 
 * Time Complexity: O(1) amortized for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class MyQueue {

    private Stack<Integer> input;
    private Stack<Integer> output;

    public MyQueue() {
        input = new Stack<>();
        output = new Stack<>();
    }

    public void push(int x) {
        input.push(x);
    }

    public int pop() {
        peek(); // Ensure output stack has elements
        return output.pop();
    }

    public int peek() {
        if (output.isEmpty()) {
            while (!input.isEmpty()) {
                output.push(input.pop());
            }
        }
        return output.peek();
    }

    public boolean empty() {
        return input.isEmpty() && output.isEmpty();
    }

    public static void main(String[] args) {
        MyQueue queue = new MyQueue();
        queue.push(1);
        queue.push(2);
        System.out.println(queue.peek()); // Expected: 1
        System.out.println(queue.pop()); // Expected: 1
        System.out.println(queue.empty()); // Expected: false
    }
}
