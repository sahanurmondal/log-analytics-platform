package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Sliding Window Maximum with Multiple Queries
 * 
 * Description:
 * Given an array and multiple queries of sliding window maximum with different
 * window sizes, efficiently answer all queries.
 * 
 * Companies: Google, Microsoft, Amazon
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class SlidingWindowMaximum {

    public int[] maxSlidingWindow(int[] nums, int k) {
        if (nums.length == 0 || k == 0)
            return new int[0];

        Deque<Integer> deque = new ArrayDeque<>();
        int[] result = new int[nums.length - k + 1];

        for (int i = 0; i < nums.length; i++) {
            // Remove elements outside window
            while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
                deque.pollFirst();
            }

            // Remove smaller elements
            while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
                deque.pollLast();
            }

            deque.offerLast(i);

            if (i >= k - 1) {
                result[i - k + 1] = nums[deque.peekFirst()];
            }
        }

        return result;
    }

    // For multiple queries with different window sizes
    public List<int[]> multipleQueries(int[] nums, int[] windowSizes) {
        List<int[]> results = new ArrayList<>();

        for (int k : windowSizes) {
            results.add(maxSlidingWindow(nums, k));
        }

        return results;
    }

    public static void main(String[] args) {
        SlidingWindowMaximum solution = new SlidingWindowMaximum();

        int[] nums = { 1, 3, -1, -3, 5, 3, 6, 7 };
        int k = 3;

        int[] result = solution.maxSlidingWindow(nums, k);
        System.out.println(Arrays.toString(result)); // [3, 3, 5, 5, 6, 7]
    }
}
