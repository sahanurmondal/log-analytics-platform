package arrays.hard;

import java.util.*;

/**
 * LeetCode 992: Subarrays with K Different Integers
 * https://leetcode.com/problems/subarrays-with-k-different-integers/
 *
 * Description:
 * Given an integer array nums and an integer k, return the number of good
 * subarrays of nums.
 * A good array is an array where the number of different integers in it is
 * exactly k.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - 1 <= nums[i], k <= nums.length
 *
 * Follow-up:
 * - Can you solve it using sliding window?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(k)
 */
public class SubarraysWithKDifferentIntegers {

    public int subarraysWithKDistinct(int[] nums, int k) {
        return atMostK(nums, k) - atMostK(nums, k - 1);
    }

    private int atMostK(int[] nums, int k) {
        if (k == 0)
            return 0;

        Map<Integer, Integer> count = new HashMap<>();
        int left = 0, result = 0;

        for (int right = 0; right < nums.length; right++) {
            if (count.getOrDefault(nums[right], 0) == 0) {
                k--;
            }
            count.put(nums[right], count.getOrDefault(nums[right], 0) + 1);

            while (k < 0) {
                count.put(nums[left], count.get(nums[left]) - 1);
                if (count.get(nums[left]) == 0) {
                    k++;
                }
                left++;
            }

            result += right - left + 1;
        }

        return result;
    }

    // Alternative solution - Direct sliding window
    public int subarraysWithKDistinctDirect(int[] nums, int k) {
        return slidingWindow(nums, k);
    }

    private int slidingWindow(int[] nums, int k) {
        Map<Integer, Integer> count = new HashMap<>();
        int left = 0, result = 0;

        for (int right = 0; right < nums.length; right++) {
            count.put(nums[right], count.getOrDefault(nums[right], 0) + 1);

            while (count.size() > k) {
                count.put(nums[left], count.get(nums[left]) - 1);
                if (count.get(nums[left]) == 0) {
                    count.remove(nums[left]);
                }
                left++;
            }

            if (count.size() == k) {
                int temp = left;
                while (count.size() == k) {
                    result++;
                    count.put(nums[temp], count.get(nums[temp]) - 1);
                    if (count.get(nums[temp]) == 0) {
                        count.remove(nums[temp]);
                    }
                    temp++;
                }

                // Restore the state
                while (temp > left) {
                    temp--;
                    count.put(nums[temp], count.getOrDefault(nums[temp], 0) + 1);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SubarraysWithKDifferentIntegers solution = new SubarraysWithKDifferentIntegers();

        // Test Case 1: Normal case
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 1, 2, 3 }, 2)); // Expected: 7

        // Test Case 2: Edge case - k = 1
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 1, 3, 4 }, 3)); // Expected: 3

        // Test Case 3: Corner case - all same elements
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 1, 1, 1 }, 1)); // Expected: 10

        // Test Case 4: k equals array length
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 3 }, 3)); // Expected: 1

        // Test Case 5: Single element
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1 }, 1)); // Expected: 1

        // Test Case 6: k = 0
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 3 }, 0)); // Expected: 0

        // Test Case 7: k greater than distinct elements
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 1, 2 }, 3)); // Expected: 0

        // Test Case 8: Complex pattern
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 3, 4, 5 }, 1)); // Expected: 5

        // Test Case 9: Repeated pattern
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 1, 2, 1 }, 2)); // Expected: 10

        // Test Case 10: Large k
        System.out.println(solution.subarraysWithKDistinct(new int[] { 1, 2, 3, 4, 5, 6 }, 4)); // Expected: 6
    }
}