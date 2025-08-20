package queues.medium;

import java.util.*;

/**
 * LeetCode 232: Implement Queue using Stacks
 * https://leetcode.com/problems/implement-queue-using-stacks/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 55+ interviews)
 *
 * Description:
 * Implement a first in first out (FIFO) queue using only two stacks. The
 * implemented
 * queue should support all the functions of a normal queue (push, peek, pop,
 * and empty).
 *
 * Constraints:
 * - 1 <= x <= 9
 * - At most 100 calls will be made to push, pop, peek, and empty.
 * - All the calls to pop and peek are valid.
 * 
 * Follow-up Questions:
 * 1. Can you implement the queue such that each operation is amortized O(1)
 * time complexity?
 * 2. Can you extend to support priority queue functionality?
 * 3. What if we need to support unlimited size?
 * 4. How would you implement using only one stack?
 */
public class ImplementQueueUsingStacks {

    // Approach 1: Two Stacks with Transfer on Pop/Peek - Amortized O(1)
    private Stack<Integer> inputStack;
    private Stack<Integer> outputStack;

    public ImplementQueueUsingStacks() {
        inputStack = new Stack<>();
        outputStack = new Stack<>();
    }

    // Push operation - O(1) time
    public void push(int x) {
        inputStack.push(x);
    }

    // Pop operation - Amortized O(1) time
    public int pop() {
        peek(); // Ensure outputStack has elements
        return outputStack.pop();
    }

    // Peek operation - Amortized O(1) time
    public int peek() {
        if (outputStack.isEmpty()) {
            // Transfer all elements from input to output stack
            while (!inputStack.isEmpty()) {
                outputStack.push(inputStack.pop());
            }
        }
        return outputStack.peek();
    }

    // Empty check - O(1) time
    public boolean empty() {
        return inputStack.isEmpty() && outputStack.isEmpty();
    }

    // Alternative Implementation: Transfer on Push - O(n) push, O(1) pop/peek
    static class QueueUsingStacksV2 {
        private Stack<Integer> stack1;
        private Stack<Integer> stack2;

        public QueueUsingStacksV2() {
            stack1 = new Stack<>();
            stack2 = new Stack<>();
        }

        // Push operation - O(n) time
        public void push(int x) {
            // Move all elements to stack2
            while (!stack1.isEmpty()) {
                stack2.push(stack1.pop());
            }

            // Push new element
            stack1.push(x);

            // Move back all elements
            while (!stack2.isEmpty()) {
                stack1.push(stack2.pop());
            }
        }

        // Pop operation - O(1) time
        public int pop() {
            return stack1.pop();
        }

        // Peek operation - O(1) time
        public int peek() {
            return stack1.peek();
        }

        // Empty check - O(1) time
        public boolean empty() {
            return stack1.isEmpty();
        }
    }

    // Follow-up 1: Queue with Priority Support
    static class PriorityQueueUsingStacks {
        private Stack<int[]> inputStack; // [value, priority]
        private Stack<int[]> outputStack;

        public PriorityQueueUsingStacks() {
            inputStack = new Stack<>();
            outputStack = new Stack<>();
        }

        public void push(int x, int priority) {
            inputStack.push(new int[] { x, priority });
        }

        public int pop() {
            if (outputStack.isEmpty()) {
                transferWithPriority();
            }
            return outputStack.pop()[0];
        }

        public int peek() {
            if (outputStack.isEmpty()) {
                transferWithPriority();
            }
            return outputStack.peek()[0];
        }

        private void transferWithPriority() {
            List<int[]> temp = new ArrayList<>();
            while (!inputStack.isEmpty()) {
                temp.add(inputStack.pop());
            }

            // Sort by priority (higher priority first)
            temp.sort((a, b) -> Integer.compare(b[1], a[1]));

            for (int[] item : temp) {
                outputStack.push(item);
            }
        }

        public boolean empty() {
            return inputStack.isEmpty() && outputStack.isEmpty();
        }
    }

    // Follow-up 2: Queue using Single Stack (requires recursion)
    static class QueueUsingSingleStack {
        private Stack<Integer> stack;

        public QueueUsingSingleStack() {
            stack = new Stack<>();
        }

        public void push(int x) {
            stack.push(x);
        }

        public int pop() {
            if (stack.size() == 1) {
                return stack.pop();
            }

            int item = stack.pop();
            int result = pop();
            stack.push(item);
            return result;
        }

        public int peek() {
            if (stack.size() == 1) {
                return stack.peek();
            }

            int item = stack.pop();
            int result = peek();
            stack.push(item);
            return result;
        }

        public boolean empty() {
            return stack.isEmpty();
        }
    }

    // Helper methods for testing
    public int size() {
        return inputStack.size() + outputStack.size();
    }

    public void printQueue() {
        List<Integer> elements = new ArrayList<>();

        // Add elements from outputStack (in reverse order since it's LIFO)
        Stack<Integer> tempOutput = new Stack<>();
        tempOutput.addAll(outputStack);
        List<Integer> outputElements = new ArrayList<>();
        while (!tempOutput.isEmpty()) {
            outputElements.add(tempOutput.pop());
        }
        Collections.reverse(outputElements);
        elements.addAll(outputElements);

        // Add elements from inputStack (in order)
        Stack<Integer> tempInput = new Stack<>();
        tempInput.addAll(inputStack);
        List<Integer> inputElements = new ArrayList<>();
        while (!tempInput.isEmpty()) {
            inputElements.add(tempInput.pop());
        }
        elements.addAll(inputElements);

        System.out.println("Queue contents: " + elements);
    }

    public static void main(String[] args) {
        ImplementQueueUsingStacks queue = new ImplementQueueUsingStacks();

        System.out.println("=== Test Case 1: Basic Operations ===");
        queue.push(1);
        queue.push(2);
        System.out.println("After pushing 1, 2:");
        queue.printQueue();
        System.out.println("Peek: " + queue.peek()); // Expected: 1
        System.out.println("Pop: " + queue.pop()); // Expected: 1
        System.out.println("After pop:");
        queue.printQueue();
        System.out.println("Empty: " + queue.empty()); // Expected: false
        System.out.println();

        System.out.println("=== Test Case 2: Multiple Operations ===");
        queue.push(3);
        queue.push(4);
        queue.push(5);
        System.out.println("After pushing 3, 4, 5:");
        queue.printQueue();
        System.out.println("Pop: " + queue.pop()); // Expected: 2
        System.out.println("Peek: " + queue.peek()); // Expected: 3
        System.out.println("Pop: " + queue.pop()); // Expected: 3
        System.out.println("After operations:");
        queue.printQueue();
        System.out.println();

        System.out.println("=== Test Case 3: Empty Queue ===");
        queue.pop(); // 4
        queue.pop(); // 5
        System.out.println("Empty: " + queue.empty()); // Expected: true
        System.out.println();

        System.out.println("=== Test Case 4: Alternative Implementation ===");
        QueueUsingStacksV2 queue2 = new QueueUsingStacksV2();
        queue2.push(10);
        queue2.push(20);
        queue2.push(30);
        System.out.println("V2 Peek: " + queue2.peek()); // Expected: 10
        System.out.println("V2 Pop: " + queue2.pop()); // Expected: 10
        System.out.println("V2 Pop: " + queue2.pop()); // Expected: 20
        System.out.println("V2 Empty: " + queue2.empty()); // Expected: false
        System.out.println();

        System.out.println("=== Test Case 5: Priority Queue ===");
        PriorityQueueUsingStacks pq = new PriorityQueueUsingStacks();
        pq.push(1, 3); // value=1, priority=3
        pq.push(2, 1); // value=2, priority=1
        pq.push(3, 2); // value=3, priority=2
        System.out.println("Priority Pop: " + pq.pop()); // Expected: 1 (highest priority)
        System.out.println("Priority Pop: " + pq.pop()); // Expected: 3 (second highest)
        System.out.println("Priority Pop: " + pq.pop()); // Expected: 2 (lowest)
        System.out.println();

        System.out.println("=== Test Case 6: Single Stack Implementation ===");
        QueueUsingSingleStack queue3 = new QueueUsingSingleStack();
        queue3.push(100);
        queue3.push(200);
        queue3.push(300);
        System.out.println("Single Stack Peek: " + queue3.peek()); // Expected: 100
        System.out.println("Single Stack Pop: " + queue3.pop()); // Expected: 100
        System.out.println("Single Stack Pop: " + queue3.pop()); // Expected: 200
        System.out.println("Single Stack Empty: " + queue3.empty()); // Expected: false

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        long startTime, endTime;
        int operations = 10000;

        // Test main implementation
        ImplementQueueUsingStacks perfQueue1 = new ImplementQueueUsingStacks();
        startTime = System.nanoTime();
        for (int i = 0; i < operations; i++) {
            perfQueue1.push(i);
            if (i % 2 == 0)
                perfQueue1.pop();
        }
        endTime = System.nanoTime();
        System.out.println("Main Implementation: " + (endTime - startTime) + " ns");

        // Test V2 implementation
        QueueUsingStacksV2 perfQueue2 = new QueueUsingStacksV2();
        startTime = System.nanoTime();
        for (int i = 0; i < operations / 10; i++) { // Fewer operations due to O(n) push
            perfQueue2.push(i);
            if (i % 2 == 0 && !perfQueue2.empty())
                perfQueue2.pop();
        }
        endTime = System.nanoTime();
        System.out.println("V2 Implementation: " + (endTime - startTime) + " ns");
    }
}
