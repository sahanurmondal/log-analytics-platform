package design.easy;

import java.util.*;

/**
 * LeetCode 225: Implement Stack using Queues
 * https://leetcode.com/problems/implement-stack-using-queues/
 *
 * Description: Implement a last-in-first-out (LIFO) stack using only two
 * queues.
 * 
 * Constraints:
 * - 1 <= x <= 9
 * - At most 100 calls will be made to push, pop, top, and empty
 * - All the calls to pop and top are valid
 *
 * Follow-up:
 * - Can you implement the stack using only one queue?
 * 
 * Time Complexity: O(n) for push, O(1) for other operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class MyStack {

    private Queue<Integer> queue;

    public MyStack() {
        queue = new LinkedList<>();
    }

    public void push(int x) {
        queue.offer(x);
        int size = queue.size();
        // Rotate queue to make the new element at front
        for (int i = 0; i < size - 1; i++) {
            queue.offer(queue.poll());
        }
    }

    public int pop() {
        return queue.poll();
    }

    public int top() {
        return queue.peek();
    }

    public boolean empty() {
        return queue.isEmpty();
    }

    // Alternative implementation - Two queues
    static class MyStackTwoQueues {
        private Queue<Integer> q1;
        private Queue<Integer> q2;

        public MyStackTwoQueues() {
            q1 = new LinkedList<>();
            q2 = new LinkedList<>();
        }

        public void push(int x) {
            q2.offer(x);
            while (!q1.isEmpty()) {
                q2.offer(q1.poll());
            }
            Queue<Integer> temp = q1;
            q1 = q2;
            q2 = temp;
        }

        public int pop() {
            return q1.poll();
        }

        public int top() {
            return q1.peek();
        }

        public boolean empty() {
            return q1.isEmpty();
        }
    }

    public static void main(String[] args) {
        MyStack stack = new MyStack();
        stack.push(1);
        stack.push(2);
        System.out.println(stack.top()); // Expected: 2
        System.out.println(stack.pop()); // Expected: 2
        System.out.println(stack.empty()); // Expected: false
    }
}
