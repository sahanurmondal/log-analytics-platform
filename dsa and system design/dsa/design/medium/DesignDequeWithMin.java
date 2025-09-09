package design.medium;

import java.util.*;

/**
 * LeetCode Problem: Design Deque with Min/Max Operations
 * URL: https://leetcode.com/problems/min-stack/ (variation)
 * Difficulty: Medium
 * 
 * Companies: Amazon, Google, Microsoft, Apple, Facebook
 * Frequency: High
 * 
 * Description:
 * Design a double-ended queue (deque) that supports push/pop operations at both
 * ends
 * and retrieving the minimum element in constant time.
 * 
 * Constraints:
 * - -2^31 <= val <= 2^31 - 1
 * - At most 3 * 10^4 calls will be made to operations
 * - Operations must be performed in O(1) time
 * 
 * Follow-up Questions:
 * 1. How would you make this thread-safe?
 * 2. How would you implement max deque version?
 * 3. How would you support both min and max simultaneously?
 * 4. How would you handle duplicate minimum values?
 * 5. How would you optimize for memory usage?
 */
public class DesignDequeWithMin {
    private Deque<Integer> deque;
    private Deque<Integer> minDeque; // Monotonic deque to track minimums

    // Approach 1: Monotonic Deque Implementation
    public DesignDequeWithMin() {
        deque = new ArrayDeque<>();
        minDeque = new ArrayDeque<>();
    }

    /**
     * Push element to front
     * Time: O(1), Space: O(1)
     */
    public void pushFront(int val) {
        deque.offerFirst(val);

        // Maintain monotonic property in minDeque
        while (!minDeque.isEmpty() && minDeque.peekLast() > val) {
            minDeque.pollLast();
        }
        minDeque.offerLast(val);
    }

    /**
     * Push element to back
     * Time: O(1), Space: O(1)
     */
    public void pushBack(int val) {
        deque.offerLast(val);

        // Maintain monotonic property in minDeque
        while (!minDeque.isEmpty() && minDeque.peekLast() > val) {
            minDeque.pollLast();
        }
        minDeque.offerLast(val);
    }

    /**
     * Pop element from front
     * Time: O(1), Space: O(1)
     */
    public void popFront() {
        if (deque.isEmpty())
            return;

        int removed = deque.pollFirst();
        if (!minDeque.isEmpty() && minDeque.peekFirst() == removed) {
            minDeque.pollFirst();
        }
    }

    /**
     * Pop element from back
     * Time: O(1), Space: O(1)
     */
    public void popBack() {
        if (deque.isEmpty())
            return;

        int removed = deque.pollLast();
        if (!minDeque.isEmpty() && minDeque.peekLast() == removed) {
            minDeque.pollLast();
        }
    }

    /**
     * Get minimum element
     * Time: O(1), Space: O(1)
     */
    public int getMin() {
        return minDeque.isEmpty() ? -1 : minDeque.peekFirst();
    }

    public boolean isEmpty() {
        return deque.isEmpty();
    }

    public int size() {
        return deque.size();
    }

    public int peekFront() {
        return deque.isEmpty() ? -1 : deque.peekFirst();
    }

    public int peekBack() {
        return deque.isEmpty() ? -1 : deque.peekLast();
    }

    // Approach 2: Min-Max Deque (supports both min and max)
    static class MinMaxDeque {
        private Deque<Integer> deque;
        private Deque<Integer> minDeque;
        private Deque<Integer> maxDeque;

        public MinMaxDeque() {
            deque = new ArrayDeque<>();
            minDeque = new ArrayDeque<>();
            maxDeque = new ArrayDeque<>();
        }

        public void pushBack(int val) {
            deque.offerLast(val);

            // Maintain min deque
            while (!minDeque.isEmpty() && minDeque.peekLast() > val) {
                minDeque.pollLast();
            }
            minDeque.offerLast(val);

            // Maintain max deque
            while (!maxDeque.isEmpty() && maxDeque.peekLast() < val) {
                maxDeque.pollLast();
            }
            maxDeque.offerLast(val);
        }

        public void popFront() {
            if (deque.isEmpty())
                return;

            int removed = deque.pollFirst();
            if (!minDeque.isEmpty() && minDeque.peekFirst() == removed) {
                minDeque.pollFirst();
            }
            if (!maxDeque.isEmpty() && maxDeque.peekFirst() == removed) {
                maxDeque.pollFirst();
            }
        }

        public int getMin() {
            return minDeque.isEmpty() ? -1 : minDeque.peekFirst();
        }

        public int getMax() {
            return maxDeque.isEmpty() ? -1 : maxDeque.peekFirst();
        }
    }

    public static void main(String[] args) {
        DesignDequeWithMin deque = new DesignDequeWithMin();

        // Test Case 1: Basic operations
        deque.pushFront(3);
        deque.pushBack(2);
        deque.pushFront(1);
        System.out.println("Min after pushes: " + deque.getMin()); // 1
        System.out.println("Front: " + deque.peekFront()); // 1
        System.out.println("Back: " + deque.peekBack()); // 2

        // Test Case 2: Pop operations
        deque.popFront();
        System.out.println("Min after pop front: " + deque.getMin()); // 2
        deque.popBack();
        System.out.println("Min after pop back: " + deque.getMin()); // 3

        // Test Case 3: Empty deque
        deque.popFront();
        System.out.println("Min when empty: " + deque.getMin()); // -1
        System.out.println("Is empty: " + deque.isEmpty()); // true

        // Test Case 4: Single element
        deque.pushFront(5);
        System.out.println("Single element min: " + deque.getMin()); // 5

        // Test Case 5: Negative numbers
        DesignDequeWithMin negDeque = new DesignDequeWithMin();
        negDeque.pushBack(-1);
        negDeque.pushFront(-5);
        negDeque.pushBack(-2);
        System.out.println("Negative min: " + negDeque.getMin()); // -5

        // Test Case 6: Duplicates
        DesignDequeWithMin dupDeque = new DesignDequeWithMin();
        dupDeque.pushBack(1);
        dupDeque.pushBack(1);
        dupDeque.pushFront(1);
        System.out.println("Duplicate min: " + dupDeque.getMin()); // 1
        dupDeque.popFront();
        System.out.println("After pop duplicate: " + dupDeque.getMin()); // 1

        // Test Case 7: Min-Max deque
        MinMaxDeque minMaxDeque = new MinMaxDeque();
        minMaxDeque.pushBack(3);
        minMaxDeque.pushBack(1);
        minMaxDeque.pushBack(4);
        System.out.println("MinMax - Min: " + minMaxDeque.getMin()); // 1
        System.out.println("MinMax - Max: " + minMaxDeque.getMax()); // 4

        // Test Case 8: Ascending order
        DesignDequeWithMin ascDeque = new DesignDequeWithMin();
        ascDeque.pushBack(1);
        ascDeque.pushBack(2);
        ascDeque.pushBack(3);
        System.out.println("Ascending min: " + ascDeque.getMin()); // 1
        ascDeque.popFront();
        System.out.println("After pop ascending: " + ascDeque.getMin()); // 2

        // Test Case 9: Descending order
        DesignDequeWithMin descDeque = new DesignDequeWithMin();
        descDeque.pushBack(5);
        descDeque.pushBack(3);
        descDeque.pushBack(1);
        System.out.println("Descending min: " + descDeque.getMin()); // 1
        descDeque.popBack();
        System.out.println("After pop descending: " + descDeque.getMin()); // 3

        // Test Case 10: Large sequence
        DesignDequeWithMin largeDeque = new DesignDequeWithMin();
        for (int i = 10; i >= 1; i--) {
            largeDeque.pushBack(i);
        }
        System.out.println("Large sequence min: " + largeDeque.getMin()); // 1
    }
}
