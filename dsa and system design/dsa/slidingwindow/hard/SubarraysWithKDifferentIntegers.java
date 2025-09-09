package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 992: Subarrays with K Different Integers
 * https://leetcode.com/problems/subarrays-with-k-different-integers/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given an array nums and an integer k, return the number of
 * subarrays with exactly k different integers.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 1 <= nums[i] <= nums.length
 * - 1 <= k <= nums.length
 * 
 * Follow-up Questions:
 * 1. Find all subarrays with at most k different integers.
 * 2. Find the longest subarray with exactly k different integers.
 * 3. Find the shortest subarray with exactly k different integers.
 * 4. Handle arbitrary integer ranges.
 */
public class SubarraysWithKDifferentIntegers {

    // Approach 1: At most K - At most (K-1)
    public int subarraysWithKDistinct(int[] nums, int k) {
        return atMostK(nums, k) - atMostK(nums, k - 1);
    }

    private int atMostK(int[] nums, int k) {
        int left = 0, res = 0;
        Map<Integer, Integer> count = new HashMap<>();
        for (int right = 0; right < nums.length; right++) {
            count.put(nums[right], count.getOrDefault(nums[right], 0) + 1);
            while (count.size() > k) {
                count.put(nums[left], count.get(nums[left]) - 1);
                if (count.get(nums[left]) == 0)
                    count.remove(nums[left]);
                left++;
            }
            res += right - left + 1;
        }
        return res;
    }

    // Follow-up 1: All subarrays with at most k different integers
    public int subarraysWithAtMostKDistinct(int[] nums, int k) {
        return atMostK(nums, k);
    }

    // Follow-up 2: Longest subarray with exactly k different integers
    public int longestSubarrayWithKDistinct(int[] nums, int k) {
        int left = 0, maxLen = 0;
        Map<Integer, Integer> count = new HashMap<>();
        for (int right = 0; right < nums.length; right++) {
            count.put(nums[right], count.getOrDefault(nums[right], 0) + 1);
            while (count.size() > k) {
                count.put(nums[left], count.get(nums[left]) - 1);
                if (count.get(nums[left]) == 0)
                    count.remove(nums[left]);
                left++;
            }
            if (count.size() == k)
                maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Follow-up 3: Shortest subarray with exactly k different integers
    public int shortestSubarrayWithKDistinct(int[] nums, int k) {
        int left = 0, minLen = Integer.MAX_VALUE;
        Map<Integer, Integer> count = new HashMap<>();
        for (int right = 0; right < nums.length; right++) {
            count.put(nums[right], count.getOrDefault(nums[right], 0) + 1);
            while (count.size() > k) {
                count.put(nums[left], count.get(nums[left]) - 1);
                if (count.get(nums[left]) == 0)
                    count.remove(nums[left]);
                left++;
            }
            while (count.size() == k) {
                minLen = Math.min(minLen, right - left + 1);
                count.put(nums[left], count.get(nums[left]) - 1);
                if (count.get(nums[left]) == 0)
                    count.remove(nums[left]);
                left++;
            }
        }
        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }

    // Follow-up 4: Arbitrary integer ranges
    public int subarraysWithKDistinctArbitrary(int[] nums, int k) {
        return subarraysWithKDistinct(nums, k);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        SubarraysWithKDifferentIntegers solution = new SubarraysWithKDifferentIntegers();

        // Test case 1: Basic case
        int[] nums1 = { 1, 2, 1, 2, 3 };
        int k1 = 2;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", k: " + k1 + " Expected: 7");
        System.out.println("Result: " + solution.subarraysWithKDistinct(nums1, k1));

        // Test case 2: At most k
        System.out.println("\nTest 2 - At most k:");
        System.out.println(solution.subarraysWithAtMostKDistinct(nums1, k1));

        // Test case 3: Longest subarray
        System.out.println("\nTest 3 - Longest subarray:");
        System.out.println(solution.longestSubarrayWithKDistinct(nums1, k1));

        // Test case 4: Shortest subarray
        System.out.println("\nTest 4 - Shortest subarray:");
        System.out.println(solution.shortestSubarrayWithKDistinct(nums1, k1));

        // Test case 5: Arbitrary integer ranges
        int[] nums2 = { 10, 20, 10, 30, 40 };
        int k2 = 3;
        System.out.println("\nTest 5 - Arbitrary integer ranges:");
        System.out.println(solution.subarraysWithKDistinctArbitrary(nums2, k2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty nums: " + solution.subarraysWithKDistinct(new int[] {}, 1));
        System.out.println("Single element: " + solution.subarraysWithKDistinct(new int[] { 5 }, 1));
        System.out.println("All same: " + solution.subarraysWithKDistinct(new int[] { 1, 1, 1, 1 }, 1));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int result = solution.subarraysWithKDistinct(large, 1);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
