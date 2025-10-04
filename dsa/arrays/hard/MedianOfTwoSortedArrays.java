package arrays.hard;

/**
 * LeetCode 4: Median of Two Sorted Arrays
 * https://leetcode.com/problems/median-of-two-sorted-arrays/
 *
 * Description:
 * Given two sorted arrays nums1 and nums2 of size m and n respectively,
 * return the median of the two sorted arrays in O(log (m+n)) time.
 *
 * Constraints:
 * - nums1.length == m
 * - nums2.length == n
 * - 0 <= m <= 1000
 * - 0 <= n <= 1000
 * - 1 <= m + n <= 2000
 * - -10^6 <= nums1[i], nums2[i] <= 10^6
 *
 * Follow-up:
 * - Can you solve it in O(log(min(m,n))) time?
 * 
 * Time Complexity: O(log(min(m,n)))
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Binary search on smaller array to partition both arrays
 * 2. Ensure left partition <= right partition
 * 3. Calculate median based on total length parity
 */
public class MedianOfTwoSortedArrays {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return findMedianSortedArrays(nums2, nums1);
        }

        int m = nums1.length, n = nums2.length;
        int left = 0, right = m;

        while (left <= right) {
            int partX = (left + right) / 2;
            int partY = (m + n + 1) / 2 - partX;

            int maxLeftX = (partX == 0) ? Integer.MIN_VALUE : nums1[partX - 1];
            int minRightX = (partX == m) ? Integer.MAX_VALUE : nums1[partX];

            int maxLeftY = (partY == 0) ? Integer.MIN_VALUE : nums2[partY - 1];
            int minRightY = (partY == n) ? Integer.MAX_VALUE : nums2[partY];

            if (maxLeftX <= minRightY && maxLeftY <= minRightX) {
                if ((m + n) % 2 == 0) {
                    return (Math.max(maxLeftX, maxLeftY) + Math.min(minRightX, minRightY)) / 2.0;
                } else {
                    return Math.max(maxLeftX, maxLeftY);
                }
            } else if (maxLeftX > minRightY) {
                right = partX - 1;
            } else {
                left = partX + 1;
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        MedianOfTwoSortedArrays solution = new MedianOfTwoSortedArrays();

        // Test Case 1: Normal case - even total length
        System.out.println(solution.findMedianSortedArrays(new int[] { 1, 3 }, new int[] { 2 })); // Expected: 2.0

        // Test Case 2: Edge case - odd total length
        System.out.println(solution.findMedianSortedArrays(new int[] { 1, 2 }, new int[] { 3, 4 })); // Expected: 2.5

        // Test Case 3: Corner case - one empty array
        System.out.println(solution.findMedianSortedArrays(new int[] {}, new int[] { 1 })); // Expected: 1.0

        // Test Case 4: Large input - different sizes
        System.out.println(solution.findMedianSortedArrays(new int[] { 1, 3, 8 }, new int[] { 7, 9, 10, 11 })); // Expected:
                                                                                                                // 8.0

        // Test Case 5: Minimum input - single elements
        System.out.println(solution.findMedianSortedArrays(new int[] { 1 }, new int[] { 2 })); // Expected: 1.5

        // Test Case 6: Special case - no overlap
        System.out.println(solution.findMedianSortedArrays(new int[] { 1, 2 }, new int[] { 3, 4, 5 })); // Expected: 3.0

        // Test Case 7: Boundary case - complete overlap
        System.out.println(solution.findMedianSortedArrays(new int[] { 1, 1 }, new int[] { 1, 2 })); // Expected: 1.0

        // Test Case 8: Negative numbers
        System.out.println(solution.findMedianSortedArrays(new int[] { -5, -3, -1 }, new int[] { 0, 2, 4 })); // Expected:
                                                                                                              // -0.5

        // Test Case 9: Same arrays
        System.out.println(solution.findMedianSortedArrays(new int[] { 1, 2, 3 }, new int[] { 1, 2, 3 })); // Expected:
                                                                                                           // 2.0

        // Test Case 10: Large difference in sizes
        System.out.println(solution.findMedianSortedArrays(new int[] { 1 }, new int[] { 2, 3, 4, 5, 6 })); // Expected:
                                                                                                           // 3.5
    }
}
