package binarysearch.hard;

/**
 * LeetCode 4: Median of Two Sorted Arrays
 * URL: <a href="https://leetcode.com/problems/median-of-two-sorted-arrays/">https://leetcode.com/problems/median-of-two-sorted-arrays/</a>
 * Company Tags: Google, Amazon, Facebook, Microsoft, Apple, Adobe, Bloomberg
 * Frequency: Very High
 *
 * Problem:
 * Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.
 * The overall run time complexity should be O(log (m+n)).
 *
 * Example 1:
 * Input: nums1 = [1,3], nums2 = [2]
 * Output: 2.00000
 * Explanation: merged array = [1,2,3] and median is 2.
 *
 * Example 2:
 * Input: nums1 = [1,2], nums2 = [3,4]
 * Output: 2.50000
 * Explanation: merged array = [1,2,3,4] and median is (2 + 3) / 2 = 2.5.
 *
 * Constraints:
 * nums1.length == m
 * nums2.length == n
 * 0 <= m <= 1000
 * 0 <= n <= 1000
 * 1 <= m + n <= 2000
 * -10^6 <= nums1[i], nums2[i] <= 10^6
 *
 * Follow-up:
 * How would you find the k-th smallest element in two sorted arrays?
 * The median is a special case of this problem. If the total number of elements is N = m + n,
 * - If N is odd, the median is the (N/2 + 1)-th smallest element.
 * - If N is even, the median is the average of the (N/2)-th and (N/2 + 1)-th smallest elements.
 */
public class MedianOfTwoSortedArrays {

    /**
     * Solution Approach: Binary Search on the smaller array
     *
     * Algorithm:
     * The core idea is to partition the two arrays into two halves each, a 'left part' and a 'right part'.
     * We want to find a partition such that:
     * 1. The total number of elements in the combined left parts is equal to the total number of elements in the combined right parts (or one more for odd total length).
     * 2. Every element in the combined left part is less than or equal to every element in the combined right part.
     *
     * To achieve this, we can binary search for the correct partition point in the smaller array (say `nums1`).
     * 1. Ensure `nums1` is the smaller array to optimize the binary search range.
     * 2. Initialize `low = 0`, `high = m` (length of `nums1`).
     * 3. The total length is `(m + n)`. The combined left part should have `(m + n + 1) / 2` elements.
     * 4. Loop while `low <= high`:
     *    a. Pick a partition point in `nums1`: `partitionX = (low + high) / 2`.
     *    b. The corresponding partition point in `nums2` is `partitionY = (m + n + 1) / 2 - partitionX`.
     *    c. Get the boundary elements:
     *       - `maxLeftX`: the max element on the left of `partitionX` in `nums1`.
     *       - `minRightX`: the min element on the right of `partitionX` in `nums1`.
     *       - `maxLeftY`: the max element on the left of `partitionY` in `nums2`.
     *       - `minRightY`: the min element on the right of `partitionY` in `nums2`.
     *       (Handle edge cases where a partition is 0 or the length of the array).
     *    d. If `maxLeftX <= minRightY` and `maxLeftY <= minRightX`, we have found the correct partition.
     *       - If total length is even, median is `(max(maxLeftX, maxLeftY) + min(minRightX, minRightY)) / 2.0`.
     *       - If total length is odd, median is `max(maxLeftX, maxLeftY)`.
     *    e. If `maxLeftX > minRightY`, our partition in `nums1` is too large. Move to the left: `high = partitionX - 1`.
     *    f. If `maxLeftX < minRightY`, our partition in `nums1` is too small. Move to the right: `low = partitionX + 1`.
     *
     * Time Complexity: O(log(min(m, n))), because we perform binary search on the smaller of the two arrays.
     * Space Complexity: O(1).
     */
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        // Ensure nums1 is the smaller array
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }

        int m = nums1.length;
        int n = nums2.length;
        int low = 0;
        int high = m;

        while (low <= high) {
            int partitionX = (low + high) / 2;
            int partitionY = (m + n + 1) / 2 - partitionX;

            int maxLeftX = (partitionX == 0) ? Integer.MIN_VALUE : nums1[partitionX - 1];
            int minRightX = (partitionX == m) ? Integer.MAX_VALUE : nums1[partitionX];

            int maxLeftY = (partitionY == 0) ? Integer.MIN_VALUE : nums2[partitionY - 1];
            int minRightY = (partitionY == n) ? Integer.MAX_VALUE : nums2[partitionY];

            if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
                // Correct partition found
                if ((m + n) % 2 == 0) {
                    // Even number of elements
                    return (Math.max(maxLeftX, maxLeftY) + Math.min(minRightX, minRightY)) / 2.0;
                } else {
                    // Odd number of elements
                    return (double) Math.max(maxLeftX, maxLeftY);
                }
            } else if (maxLeftX > minRightY) {
                // Move towards left in nums1
                high = partitionX - 1;
            } else {
                // Move towards right in nums1
                low = partitionX + 1;
            }
        }

        // Should not happen if inputs are sorted arrays
        throw new IllegalArgumentException("Input arrays are not sorted.");
    }

    /**
     * Follow-up Solution: Find k-th smallest element in two sorted arrays.
     *
     * Algorithm:
     * This is a classic divide-and-conquer approach. We want to find the k-th element.
     * 1. Compare the elements at index `k/2 - 1` in both arrays. Let's call them `pivot1` from `nums1` and `pivot2` from `nums2`.
     * 2. The smaller of `pivot1` and `pivot2`, say `pivot1`, and all elements before it in `nums1` (a total of `k/2` elements) cannot be the k-th element.
     *    Why? Because even if all `k/2 - 1` elements from `nums2` before `pivot2` are smaller than `pivot1`, `pivot1` can be at most the `(k/2 - 1) + (k/2 - 1) + 1 = k-1`-th element.
     * 3. So, we can discard these `k/2` elements from `nums1`. Now we need to find the `(k - k/2)`-th element from the rest of the arrays.
     * 4. We recursively call the function on the remaining parts of the arrays with the updated `k`.
     *
     * Base Cases:
     * - If one array is empty, the k-th element is the k-th element of the other array.
     * - If `k == 1`, the k-th element is `min(nums1[0], nums2[0])`.
     *
     * Time Complexity: O(log(k)), which is O(log(m+n)) for the median problem. In each step, we reduce k by a factor of 2.
     * Space Complexity: O(log(k)) due to recursion stack. An iterative version would be O(1).
     */
    public int findKthElement(int[] nums1, int[] nums2, int k) {
        return findKth(nums1, 0, nums2, 0, k);
    }

    private int findKth(int[] nums1, int start1, int[] nums2, int start2, int k) {
        if (start1 >= nums1.length) {
            return nums2[start2 + k - 1];
        }
        if (start2 >= nums2.length) {
            return nums1[start1 + k - 1];
        }
        if (k == 1) {
            return Math.min(nums1[start1], nums2[start2]);
        }

        int mid = k / 2;
        int pivot1 = (start1 + mid - 1 < nums1.length) ? nums1[start1 + mid - 1] : Integer.MAX_VALUE;
        int pivot2 = (start2 + mid - 1 < nums2.length) ? nums2[start2 + mid - 1] : Integer.MAX_VALUE;

        if (pivot1 < pivot2) {
            return findKth(nums1, start1 + mid, nums2, start2, k - mid);
        } else {
            return findKth(nums1, start1, nums2, start2 + mid, k - mid);
        }
    }

    public static void main(String[] args) {
        MedianOfTwoSortedArrays solution = new MedianOfTwoSortedArrays();

        // Example 1: Odd total length
        int[] nums1 = {1, 3};
        int[] nums2 = {2};
        System.out.println("Median: " + solution.findMedianSortedArrays(nums1, nums2)); // Expected: 2.0

        // Example 2: Even total length
        int[] nums3 = {1, 2};
        int[] nums4 = {3, 4};
        System.out.println("Median: " + solution.findMedianSortedArrays(nums3, nums4)); // Expected: 2.5

        // Edge Cases
        // 1. One array is empty
        System.out.println("One empty array: " + solution.findMedianSortedArrays(new int[]{}, new int[]{1, 2, 3, 4, 5})); // Expected: 3.0
        // 2. Arrays with no overlap
        System.out.println("No overlap: " + solution.findMedianSortedArrays(new int[]{1, 2, 3}, new int[]{4, 5, 6})); // Expected: 3.5
        // 3. Arrays with all elements from one array smaller than the other
        System.out.println("All smaller: " + solution.findMedianSortedArrays(new int[]{1, 2}, new int[]{3, 4, 5, 6})); // Expected: 3.5
        // 4. Arrays with interleaved elements
        System.out.println("Interleaved: " + solution.findMedianSortedArrays(new int[]{1, 5, 8}, new int[]{2, 3, 6, 7})); // Expected: 5.0

        System.out.println("\n--- Follow-up: Find K-th Element ---");
        int[] a1 = {2, 3, 6, 7, 9};
        int[] a2 = {1, 4, 8, 10};
        // Combined: 1, 2, 3, 4, 6, 7, 8, 9, 10
        System.out.println("5th smallest: " + solution.findKthElement(a1, a2, 5)); // Expected: 6
        System.out.println("1st smallest: " + solution.findKthElement(a1, a2, 1)); // Expected: 1
        System.out.println("9th smallest: " + solution.findKthElement(a1, a2, 9)); // Expected: 10
    }
}
