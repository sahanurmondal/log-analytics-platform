package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 239: Sliding Window Maximum
 * https://leetcode.com/problems/sliding-window-maximum/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft
 * Frequency: Very High
 *
 * Description: Given an array nums and a window size k, return the maximum
 * value in each window.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 1 <= k <= nums.length
 * 
 * Follow-up Questions:
 * 1. Support variable window sizes.
 * 2. Support moving minimum.
 * 3. Support moving sum.
 * 4. Support moving average.
 */
public class SlidingWindowMaximumDeque {

    // Approach 1: Deque
    public int[] maxSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!dq.isEmpty() && dq.peekFirst() <= i - k)
                dq.pollFirst();
            while (!dq.isEmpty() && nums[dq.peekLast()] < nums[i])
                dq.pollLast();
            dq.offerLast(i);
            if (i >= k - 1)
                result[i - k + 1] = nums[dq.peekFirst()];
        }
        return result;
    }

    // Follow-up 1: Variable window sizes
    public List<Integer> maxSlidingWindowVariable(int[] nums, int[] windowSizes) {
        List<Integer> result = new ArrayList<>();
        for (int k : windowSizes) {
            int[] maxs = maxSlidingWindow(nums, k);
            for (int v : maxs)
                result.add(v);
        }
        return result;
    }

    // Follow-up 2: Moving minimum
    public int[] minSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            while (!dq.isEmpty() && dq.peekFirst() <= i - k)
                dq.pollFirst();
            while (!dq.isEmpty() && nums[dq.peekLast()] > nums[i])
                dq.pollLast();
            dq.offerLast(i);
            if (i >= k - 1)
                result[i - k + 1] = nums[dq.peekFirst()];
        }
        return result;
    }

    // Follow-up 3: Moving sum
    public int[] sumSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        int[] result = new int[n - k + 1];
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += nums[i];
            if (i >= k)
                sum -= nums[i - k];
            if (i >= k - 1)
                result[i - k + 1] = sum;
        }
        return result;
    }

    // Follow-up 4: Moving average
    public double[] avgSlidingWindow(int[] nums, int k) {
        int n = nums.length;
        double[] result = new double[n - k + 1];
        int sum = 0;
        for (int i = 0; i < n; i++) {
            sum += nums[i];
            if (i >= k)
                sum -= nums[i - k];
            if (i >= k - 1)
                result[i - k + 1] = sum * 1.0 / k;
        }
        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        SlidingWindowMaximumDeque solution = new SlidingWindowMaximumDeque();

        // Test case 1: Basic case
        int[] nums1 = { 1, 3, -1, -3, 5, 3, 6, 7 };
        int k1 = 3;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", k: " + k1);
        System.out.println("Result: " + Arrays.toString(solution.maxSlidingWindow(nums1, k1)));

        // Test case 2: Variable window sizes
        int[] windowSizes = { 2, 3 };
        System.out.println("\nTest 2 - Variable window sizes:");
        System.out.println(solution.maxSlidingWindowVariable(nums1, windowSizes));

        // Test case 3: Moving minimum
        System.out.println("\nTest 3 - Moving minimum:");
        System.out.println(Arrays.toString(solution.minSlidingWindow(nums1, k1)));

        // Test case 4: Moving sum
        System.out.println("\nTest 4 - Moving sum:");
        System.out.println(Arrays.toString(solution.sumSlidingWindow(nums1, k1)));

        // Test case 5: Moving average
        System.out.println("\nTest 5 - Moving average:");
        System.out.println(Arrays.toString(solution.avgSlidingWindow(nums1, k1)));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty nums: " + Arrays.toString(solution.maxSlidingWindow(new int[] {}, 1)));
        System.out.println("Single element: " + Arrays.toString(solution.maxSlidingWindow(new int[] { 5 }, 1)));
        System.out.println(
                "Window size equals array: " + Arrays.toString(solution.maxSlidingWindow(nums1, nums1.length)));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int[] result = solution.maxSlidingWindow(large, 5000);
        long end = System.nanoTime();
        System.out.println("Result length: " + result.length + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
