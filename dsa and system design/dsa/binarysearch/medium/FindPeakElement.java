package binarysearch.medium;

/**
 * LeetCode 162: Find Peak Element
 * URL: <a href="https://leetcode.com/problems/find-peak-element/">https://leetcode.com/problems/find-peak-element/</a>
 * Company Tags: Facebook (Meta), Google, Amazon, Microsoft, Bloomberg
 * Frequency: High
 *
 * Problem:
 * A peak element is an element that is strictly greater than its neighbors.
 * Given a 0-indexed integer array nums, find a peak element, and return its index. If the array contains multiple peaks, return the index to any of the peaks.
 * You may imagine that nums[-1] = nums[n] = -∞. In other words, an element is always considered to be strictly greater than a neighbor that is outside the array.
 * You must write an algorithm that runs in O(log n) time.
 *
 * Example 1:
 * Input: nums = [1,2,3,1]
 * Output: 2
 * Explanation: 3 is a peak element and your function should return the index number 2.
 *
 * Example 2:
 * Input: nums = [1,2,1,3,5,6,4]
 * Output: 5
 * Explanation: Your function can return either index number 1 where the peak element is 2, or index number 5 where the peak element is 6.
 *
 * Constraints:
 * 1 <= nums.length <= 1000
 * -2^31 <= nums[i] <= 2^31 - 1
 * nums[i] != nums[i + 1] for all valid i.
 */
public class FindPeakElement {

    /**
     * Solution Approach: Binary Search
     *
     * Algorithm:
     * The condition `nums[i] != nums[i+1]` is key. It ensures there are no plateaus.
     * The fact that `nums[-1]` and `nums[n]` are -∞ guarantees a peak exists.
     * We can use binary search. At any `mid` point, we check `nums[mid]` against its right neighbor `nums[mid+1]`.
     * 1. Initialize `left = 0`, `right = nums.length - 1`.
     * 2. Loop while `left < right`. We use `<` instead of `<=` to ensure the loop terminates and `left` and `right` converge to a single peak element.
     *    a. Calculate `mid = left + (right - left) / 2`.
     *    b. Compare `nums[mid]` with `nums[mid + 1]`.
     *       - If `nums[mid] < nums[mid + 1]`, it means we are on an "uphill" slope. A peak must exist to the right of `mid`. So, we discard the left half by setting `left = mid + 1`.
     *       - If `nums[mid] > nums[mid + 1]`, it means `mid` could be a peak, or we are on a "downhill" slope. A peak is either at `mid` or to its left. So, we discard the right half by setting `right = mid`.
     * 3. The loop terminates when `left == right`. This index is guaranteed to be a peak.
     *
     * Time Complexity: O(log n), as it's a standard binary search.
     * Space Complexity: O(1).
     */
    public int findPeakElement(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < nums[mid + 1]) {
                // We are on the ascending slope, so a peak is to the right.
                left = mid + 1;
            } else {
                // We are on a descending slope or at a peak, so a peak is at mid or to the left.
                right = mid;
            }
        }
        // left and right converge to the peak index.
        return left;
    }

    public static void main(String[] args) {
        FindPeakElement solution = new FindPeakElement();

        // Example 1
        int[] nums1 = {1, 2, 3, 1};
        System.out.println("Peak index for " + java.util.Arrays.toString(nums1) + ": " + solution.findPeakElement(nums1)); // Expected: 2

        // Example 2
        int[] nums2 = {1, 2, 1, 3, 5, 6, 4};
        System.out.println("Peak index for " + java.util.Arrays.toString(nums2) + ": " + solution.findPeakElement(nums2)); // Expected: 1 or 5

        // Edge Cases
        // 1. Array with one element
        System.out.println("Single element: " + solution.findPeakElement(new int[]{10})); // Expected: 0
        // 2. Peak at the beginning
        System.out.println("Peak at start: " + solution.findPeakElement(new int[]{5, 4, 3, 2, 1})); // Expected: 0
        // 3. Peak at the end
        System.out.println("Peak at end: " + solution.findPeakElement(new int[]{1, 2, 3, 4, 5})); // Expected: 4
    }
}

