package design.hard;

import java.util.*;

/**
 * Design Sliding Window Maximum Data Structure
 *
 * Description: Design a data structure that supports sliding window maximum
 * queries.
 * Support operations: addNum, getMaxInWindow, removeOldest
 * 
 * Constraints:
 * - 1 <= windowSize <= 1000
 * - -10^4 <= num <= 10^4
 * - At most 10^4 operations
 *
 * Follow-up:
 * - Can you make all operations O(1) amortized?
 * - What about supporting arbitrary window sizes?
 * 
 * Time Complexity: O(1) amortized for all operations
 * Space Complexity: O(k) where k is window size
 * 
 * Company Tags: Google, Amazon, Facebook
 */
public class DesignSlidingWindowMaximum {

    private Deque<Integer> deque; // Stores indices
    private List<Integer> window;
    private int maxSize;

    public DesignSlidingWindowMaximum(int windowSize) {
        this.maxSize = windowSize;
        this.deque = new ArrayDeque<>();
        this.window = new ArrayList<>();
    }

    public void addNum(int num) {
        // Add to window
        window.add(num);

        // Remove elements outside window
        while (!deque.isEmpty() && deque.peekFirst() <= window.size() - maxSize - 1) {
            deque.pollFirst();
        }

        // Remove elements smaller than current
        while (!deque.isEmpty() && window.get(deque.peekLast()) <= num) {
            deque.pollLast();
        }

        deque.offerLast(window.size() - 1);

        // Keep window size
        if (window.size() > maxSize) {
            window.remove(0);
            // Adjust indices in deque
            for (int i = 0; i < deque.size(); i++) {
                int[] temp = new int[deque.size()];
                int idx = 0;
                while (!deque.isEmpty()) {
                    temp[idx++] = deque.pollFirst() - 1;
                }
                for (int j = 0; j < idx; j++) {
                    if (temp[j] >= 0) {
                        deque.offerLast(temp[j]);
                    }
                }
                break;
            }
        }
    }

    public int getMaximum() {
        if (deque.isEmpty()) {
            throw new IllegalStateException("No elements in window");
        }
        return window.get(deque.peekFirst());
    }

    public int getWindowSize() {
        return window.size();
    }

    // Alternative implementation with cleaner logic
    static class SlidingWindowMaxAlternative {
        private Deque<Integer> maxDeque;
        private Queue<Integer> window;
        private int maxSize;

        public SlidingWindowMaxAlternative(int windowSize) {
            this.maxSize = windowSize;
            this.maxDeque = new ArrayDeque<>();
            this.window = new LinkedList<>();
        }

        public void addNum(int num) {
            // Remove oldest if window is full
            if (window.size() == maxSize) {
                int removed = window.poll();
                if (!maxDeque.isEmpty() && maxDeque.peekFirst() == removed) {
                    maxDeque.pollFirst();
                }
            }

            // Add new number
            window.offer(num);

            // Maintain decreasing order in maxDeque
            while (!maxDeque.isEmpty() && maxDeque.peekLast() < num) {
                maxDeque.pollLast();
            }
            maxDeque.offerLast(num);
        }

        public int getMaximum() {
            if (maxDeque.isEmpty()) {
                throw new IllegalStateException("No elements in window");
            }
            return maxDeque.peekFirst();
        }
    }

    public static void main(String[] args) {
        DesignSlidingWindowMaximum swm = new DesignSlidingWindowMaximum(3);

        swm.addNum(1);
        swm.addNum(3);
        swm.addNum(-1);
        System.out.println("Max after [1,3,-1]: " + swm.getMaximum()); // Expected: 3

        swm.addNum(-3);
        System.out.println("Max after [3,-1,-3]: " + swm.getMaximum()); // Expected: 3

        swm.addNum(5);
        System.out.println("Max after [-1,-3,5]: " + swm.getMaximum()); // Expected: 5

        swm.addNum(3);
        System.out.println("Max after [-3,5,3]: " + swm.getMaximum()); // Expected: 5

        swm.addNum(6);
        System.out.println("Max after [5,3,6]: " + swm.getMaximum()); // Expected: 6

        // Test alternative implementation
        System.out.println("\nTesting alternative implementation:");
        SlidingWindowMaxAlternative alt = new SlidingWindowMaxAlternative(3);
        int[] nums = { 1, 3, -1, -3, 5, 3, 6, 7 };

        for (int num : nums) {
            alt.addNum(num);
            System.out.println("Added " + num + ", max: " + alt.getMaximum());
        }
    }
}
