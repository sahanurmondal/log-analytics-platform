package heap.hard;

import java.util.Arrays;

/**
 * LeetCode 719: Find K-th Smallest Pair Distance
 * https://leetcode.com/problems/find-k-th-smallest-pair-distance/
 * 
 * Companies: Amazon, Microsoft, Google
 * Frequency: Hard
 *
 * Description:
 * The distance of a pair of integers `a` and `b` is defined as the absolute
 * difference between `a` and `b`.
 * Given an integer array `nums` and an integer `k`, return the `k`-th smallest
 * distance among all the pairs `nums[i]` and `nums[j]` where `0 <= i < j <
 * nums.length`.
 *
 * Constraints:
 * - n == nums.length
 * - 2 <= n <= 10^4
 * - 0 <= nums[i] <= 10^6
 * - 1 <= k <= n * (n - 1) / 2
 * 
 * Follow-up Questions:
 * 1. Why is binary searching on the answer a good approach here?
 * 2. How do you efficiently count the number of pairs with a distance less than
 * or equal to a given `mid` value?
 * 3. What is the time complexity of the two-pointer counting method?
 */
public class FindKthSmallestPairDistance {

    // Approach 1: Binary Search on the Answer - O(n log n + n log W) time, O(1)
    // space. W is the range of nums.
    public int smallestDistancePair(int[] nums, int k) {
        // Sort the array to easily calculate distances and use two pointers.
        Arrays.sort(nums);
        int n = nums.length;

        // The smallest possible distance is 0, the largest is the difference between
        // max and min elements.
        int low = 0;
        int high = nums[n - 1] - nums[0];

        while (low < high) {
            int mid = low + (high - low) / 2;

            // Count pairs with distance <= mid
            int count = 0;
            int left = 0;
            for (int right = 0; right < n; right++) {
                while (nums[right] - nums[left] > mid) {
                    left++;
                }
                // All pairs from `left` to `right-1` with `right` have distance <= mid
                count += right - left;
            }

            if (count >= k) {
                // Too many pairs, or just right. Try a smaller distance.
                high = mid;
            } else {
                // Not enough pairs. Need a larger distance.
                low = mid + 1;
            }
        }

        return low;
    }

    public static void main(String[] args) {
        FindKthSmallestPairDistance solution = new FindKthSmallestPairDistance();

        // Test case 1
        int[] nums1 = { 1, 3, 1 };
        int k1 = 1;
        System.out.println("Smallest distance 1: " + solution.smallestDistancePair(nums1, k1)); // 0

        // Test case 2
        int[] nums2 = { 1, 1, 1 };
        int k2 = 2;
        System.out.println("Smallest distance 2: " + solution.smallestDistancePair(nums2, k2)); // 0

        // Test case 3
        int[] nums3 = { 1, 6, 1 };
        int k3 = 3;
        System.out.println("Smallest distance 3: " + solution.smallestDistancePair(nums3, k3)); // 5

        // Test case 4
        int[] nums4 = { 62, 100, 4, 8, 55, 78 };
        int k4 = 10;
        System.out.println("Smallest distance 4: " + solution.smallestDistancePair(nums4, k4)); // 47
    }
}
