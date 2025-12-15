package arrays.hard;

import java.util.*;

/**
 * LeetCode 239: Sliding Window Maximum
 * https://leetcode.com/problems/sliding-window-maximum/
 *
 * Description:
 * You are given an array of integers nums, there is a sliding window of size k
 * which is moving
 * from the very left of the array to the very right. Return the max sliding
 * window.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * - 1 <= k <= nums.length
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(k)
 * 
 * Algorithm:
 * 1. Use deque to maintain indices of elements in decreasing order
 * 2. Remove indices outside current window
 * 3. Front of deque always contains maximum element index
 */
public class SlidingWindowMaximum {
    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums == null || nums.length == 0)
            return new int[0];

        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> deque = new ArrayDeque<>();

        for (int i = 0; i < n; i++) {
            // Remove indices outside current window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove indices with smaller values
            while (!deque.isEmpty() && nums[deque.peek()] < nums[i]) {
                deque.poll();
            }

            deque.offer(i);

            // Add to result when window size reached
            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SlidingWindowMaximum solution = new SlidingWindowMaximum();

        // Test Case 1: Normal case
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 1, 3, -1, -3, 5, 3, 6, 7 }, 3))); // Expected:
                                                                                                                   // [3,3,5,5,6,7]

        // Test Case 2: Edge case - k equals array length
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 1, -1 }, 1))); // Expected: [1,-1]

        // Test Case 3: Corner case - single element
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 1 }, 1))); // Expected: [1]

        // Test Case 4: Large input - increasing sequence
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 1, 2, 3, 4, 5 }, 3))); // Expected:
                                                                                                        // [3,4,5]

        // Test Case 5: Minimum input - k=1
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 7, 2, 4 }, 1))); // Expected: [7,2,4]

        // Test Case 6: Special case - decreasing sequence
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 5, 4, 3, 2, 1 }, 2))); // Expected:
                                                                                                        // [5,4,3,2]

        // Test Case 7: Boundary case - all same elements
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 3, 3, 3, 3 }, 2))); // Expected:
                                                                                                     // [3,3,3]

        // Test Case 8: Negative numbers
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { -7, -8, 7, 5, 7, 1, 6, 0 }, 4))); // Expected:
                                                                                                                   // [7,7,7,7,7]

        // Test Case 9: Mixed positive/negative
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 1, -1, 2, -2, 3 }, 3))); // Expected:
                                                                                                          // [2,2,3]

        // Test Case 10: Large window
        System.out.println(Arrays.toString(solution.maxSlidingWindow(new int[] { 9, 11 }, 2))); // Expected: [11]
    }
}