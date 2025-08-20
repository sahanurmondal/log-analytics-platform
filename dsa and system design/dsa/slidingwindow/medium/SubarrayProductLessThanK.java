package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 713: Subarray Product Less Than K
 * https://leetcode.com/problems/subarray-product-less-than-k/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given an array of positive integers nums and an integer k,
 * return the number of contiguous subarrays where the product of all elements
 * is less than k.
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - 1 <= nums[i] <= 1000
 * - 0 <= k <= 10^6
 * 
 * Follow-up Questions:
 * 1. Can you find the longest/shortest such subarray?
 * 2. Can you count subarrays with product at most k?
 * 3. Can you handle zeros in the array?
 */
public class SubarrayProductLessThanK {

    // Approach 1: Sliding window (O(n) time)
    public int numSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1)
            return 0;
        int prod = 1, left = 0, res = 0;
        for (int right = 0; right < nums.length; right++) {
            prod *= nums[right];
            while (prod >= k)
                prod /= nums[left++];
            res += right - left + 1;
        }
        return res;
    }

    // Follow-up 1: Longest subarray with product < k
    public int longestSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1)
            return 0;
        int prod = 1, left = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            prod *= nums[right];
            while (prod >= k)
                prod /= nums[left++];
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Follow-up 2: Shortest subarray with product < k
    public int shortestSubarrayProductLessThanK(int[] nums, int k) {
        if (k <= 1)
            return 0;
        int prod = 1, left = 0, minLen = Integer.MAX_VALUE;
        for (int right = 0; right < nums.length; right++) {
            prod *= nums[right];
            while (prod >= k)
                prod /= nums[left++];
            if (prod < k)
                minLen = Math.min(minLen, right - left + 1);
        }
        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    // Follow-up 3: Handle zeros in the array
    public int numSubarrayProductLessThanKWithZeros(int[] nums, int k) {
        int res = 0, left = 0, prod = 1;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0) {
                res += (right - left + 1) * (nums.length - right);
                left = right + 1;
                prod = 1;
            } else {
                prod *= nums[right];
                while (left <= right && prod >= k)
                    prod /= nums[left++];
                res += right - left + 1;
            }
        }
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        SubarrayProductLessThanK solution = new SubarrayProductLessThanK();

        // Test case 1: Basic case
        int[] nums1 = { 10, 5, 2, 6 };
        int k1 = 100;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", k: " + k1 + " Expected: 8");
        System.out.println("Result: " + solution.numSubarrayProductLessThanK(nums1, k1));

        // Test case 2: Longest subarray
        System.out.println("\nTest 2 - Longest subarray:");
        System.out.println(solution.longestSubarrayProductLessThanK(nums1, k1));

        // Test case 3: Shortest subarray
        System.out.println("\nTest 3 - Shortest subarray:");
        System.out.println(solution.shortestSubarrayProductLessThanK(nums1, k1));

        // Test case 4: Zeros in array
        int[] nums2 = { 1, 0, 2, 3 };
        int k2 = 2;
        System.out.println("\nTest 4 - Zeros in array:");
        System.out.println(solution.numSubarrayProductLessThanKWithZeros(nums2, k2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " + solution.numSubarrayProductLessThanK(new int[] {}, 1));
        System.out.println("Single element: " + solution.numSubarrayProductLessThanK(new int[] { 1 }, 2));
        System.out.println("k <= 1: " + solution.numSubarrayProductLessThanK(nums1, 1));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int result = solution.numSubarrayProductLessThanK(large, 10000);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
