package slidingwindow.medium;

import java.util.*;

/**
 * LeetCode 930: Binary Subarrays With Sum
 * https://leetcode.com/problems/binary-subarrays-with-sum/
 * 
 * Companies: Google, Facebook, Amazon
 * Frequency: High
 *
 * Description: Given a binary array nums and an integer goal, return the number
 * of non-empty subarrays with a sum equal to goal.
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - nums[i] is either 0 or 1
 * - 0 <= goal <= nums.length
 * 
 * Follow-up Questions:
 * 1. Can you solve for any integer array?
 * 2. Can you find the longest/shortest such subarray?
 * 3. Can you count subarrays with sum at most goal?
 */
public class BinarySubarraysWithSum {

    // Approach 1: Prefix sum + HashMap (O(n) time, O(n) space)
    public int numSubarraysWithSum(int[] nums, int goal) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        int sum = 0, res = 0;
        for (int num : nums) {
            sum += num;
            res += prefixCount.getOrDefault(sum - goal, 0);
            prefixCount.put(sum, prefixCount.getOrDefault(sum, 0) + 1);
        }
        return res;
    }

    // Approach 2: Sliding window for at most goal - at most (goal-1)
    public int numSubarraysWithSumSliding(int[] nums, int goal) {
        return atMost(nums, goal) - atMost(nums, goal - 1);
    }

    private int atMost(int[] nums, int goal) {
        if (goal < 0)
            return 0;
        int left = 0, sum = 0, res = 0;
        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];
            while (sum > goal)
                sum -= nums[left++];
            res += right - left + 1;
        }
        return res;
    }

    // Follow-up 1: For any integer array
    public int numSubarraysWithSumGeneral(int[] nums, int goal) {
        Map<Integer, Integer> prefixCount = new HashMap<>();
        prefixCount.put(0, 1);
        int sum = 0, res = 0;
        for (int num : nums) {
            sum += num;
            res += prefixCount.getOrDefault(sum - goal, 0);
            prefixCount.put(sum, prefixCount.getOrDefault(sum, 0) + 1);
        }
        return res;
    }

    // Follow-up 2: Longest subarray with sum == goal
    public int longestSubarrayWithSum(int[] nums, int goal) {
        Map<Integer, Integer> prefixIndex = new HashMap<>();
        prefixIndex.put(0, -1);
        int sum = 0, maxLen = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (prefixIndex.containsKey(sum - goal)) {
                maxLen = Math.max(maxLen, i - prefixIndex.get(sum - goal));
            }
            if (!prefixIndex.containsKey(sum))
                prefixIndex.put(sum, i);
        }
        return maxLen;
    }

    // Follow-up 3: Shortest subarray with sum == goal
    public int shortestSubarrayWithSum(int[] nums, int goal) {
        Map<Integer, Integer> prefixIndex = new HashMap<>();
        prefixIndex.put(0, -1);
        int sum = 0, minLen = Integer.MAX_VALUE;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
            if (prefixIndex.containsKey(sum - goal)) {
                minLen = Math.min(minLen, i - prefixIndex.get(sum - goal));
            }
            if (!prefixIndex.containsKey(sum))
                prefixIndex.put(sum, i);
        }
        return minLen == Integer.MAX_VALUE ? -1 : minLen;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        BinarySubarraysWithSum solution = new BinarySubarraysWithSum();

        // Test case 1: Basic case
        int[] nums1 = { 1, 0, 1, 0, 1 };
        int goal1 = 2;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", goal: " + goal1 + " Expected: 4");
        System.out.println("Result: " + solution.numSubarraysWithSum(nums1, goal1));

        // Test case 2: All zeros
        int[] nums2 = { 0, 0, 0, 0, 0 };
        int goal2 = 0;
        System.out.println("\nTest 2 - All zeros, goal 0:");
        System.out.println("Result: " + solution.numSubarraysWithSum(nums2, goal2));

        // Test case 3: General integer array
        int[] nums3 = { 2, 1, 3, 2, 1 };
        int goal3 = 4;
        System.out.println("\nTest 3 - General integer array:");
        System.out.println("Result: " + solution.numSubarraysWithSumGeneral(nums3, goal3));

        // Test case 4: Longest subarray
        System.out.println("\nTest 4 - Longest subarray with sum:");
        System.out.println(solution.longestSubarrayWithSum(nums1, goal1));

        // Test case 5: Shortest subarray
        System.out.println("\nTest 5 - Shortest subarray with sum:");
        System.out.println(solution.shortestSubarrayWithSum(nums1, goal1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " + solution.numSubarraysWithSum(new int[] {}, 0));
        System.out.println("Single element: " + solution.numSubarraysWithSum(new int[] { 1 }, 1));
        System.out.println("Goal larger than sum: " + solution.numSubarraysWithSum(nums1, 10));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int result = solution.numSubarraysWithSum(large, 5000);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
