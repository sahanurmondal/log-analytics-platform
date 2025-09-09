package heap.hard;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.PriorityQueue;

/**
 * LeetCode 239: Sliding Window Maximum
 * https://leetcode.com/problems/sliding-window-maximum/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description:
 * You are given an array of integers `nums`, there is a sliding window of size
 * `k` which is moving from the very left of the array to the very right. You
 * can only see the `k` numbers in the window. Each time the sliding window
 * moves right by one position.
 * Return the max sliding window.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * - 1 <= k <= nums.length
 * 
 * Follow-up Questions:
 * 1. Can you solve this in linear time?
 * 2. How does the Deque approach work and why is it efficient?
 * 3. What is the time complexity of the heap-based solution?
 */
public class SlidingWindowMaximum {

    // Approach 1: Deque (Monotonic Queue) - O(n) time, O(k) space
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k == 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>(); // Stores indices

        for (int i = 0; i < n; i++) {
            // Remove indices from the front that are out of the current window
            if (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove indices from the back whose elements are smaller than the current
            // element
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i);

            // The max for the current window is at the front of the deque
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    // Approach 2: Max-Heap - O(n log k) time, O(k) space
    public int[] maxSlidingWindowWithHeap(int[] nums, int k) {
        if (nums == null || nums.length == 0 || k == 0) {
            return new int[0];
        }

        int n = nums.length;
        int[] result = new int[n - k + 1];
        // Max-heap stores pairs of [value, index]
        PriorityQueue<int[]> maxHeap = new PriorityQueue<>((a, b) -> b[0] - a[0]);

        for (int i = 0; i < n; i++) {
            // Add current element to the heap
            maxHeap.offer(new int[] { nums[i], i });

            // If the window is full
            if (i >= k - 1) {
                // Remove max elements from the heap that are outside the current window
                while (maxHeap.peek()[1] < i - k + 1) {
                    maxHeap.poll();
                }
                // The top of the heap is the max for the current window
                result[i - k + 1] = maxHeap.peek()[0];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SlidingWindowMaximum solution = new SlidingWindowMaximum();

        // Test case 1
        int[] nums1 = { 1, 3, -1, -3, 5, 3, 6, 7 };
        int k1 = 3;
        System.out.println("Max Window (Deque): " + Arrays.toString(solution.maxSlidingWindow(nums1, k1))); // [3, 3, 5,
                                                                                                            // 5, 6, 7]
        System.out.println("Max Window (Heap): " + Arrays.toString(solution.maxSlidingWindowWithHeap(nums1, k1))); // [3,
                                                                                                                   // 3,
                                                                                                                   // 5,
                                                                                                                   // 5,
                                                                                                                   // 6,
                                                                                                                   // 7]

        // Test case 2
        int[] nums2 = { 1 };
        int k2 = 1;
        System.out.println("Max Window 2: " + Arrays.toString(solution.maxSlidingWindow(nums2, k2))); // [1]

        // Test case 3
        int[] nums3 = { 1, -1 };
        int k3 = 1;
        System.out.println("Max Window 3: " + Arrays.toString(solution.maxSlidingWindow(nums3, k3))); // [1, -1]

        // Test case 4
        int[] nums4 = { 9, 11 };
        int k4 = 2;
        System.out.println("Max Window 4: " + Arrays.toString(solution.maxSlidingWindow(nums4, k4))); // [11]
    }
}
