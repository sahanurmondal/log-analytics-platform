package slidingwindow.hard;

import java.util.*;

/**
 * LeetCode 862: Shortest Subarray with Sum at Least K
 * https://leetcode.com/problems/shortest-subarray-with-sum-at-least-k/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given an integer array nums and an integer k, return the length of the shortest 
 * non-empty subarray of nums with a sum at least k. If there is no such subarray, return -1.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^5 <= nums[i] <= 10^5
 * - 1 <= k <= 10^9
 * 
 * Follow-up Questions:
 * 1. Find the longest subarray with sum at least k.
 * 2. Handle negative numbers efficiently.
 * 3. Find all subarrays with sum at least k.
 * 4. Find the shortest subarray with sum exactly k.
 */
public class ShortestSubarrayWithSumAtLeastK {

    // Approach 1: Monotonic queue (deque) - O(n) time
    public int shortestSubarray(int[] nums, int k) {
        int n = nums.length;
        long[] prefix = new long[n + 1];
        for (int i = 0; i < n; i++) prefix[i + 1] = prefix[i] + nums[i];
        int minLen = n + 1;
        Deque<Integer> dq = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            while (!dq.isEmpty() && prefix[i] - prefix[dq.peekFirst()] >= k) {
                minLen = Math.min(minLen, i - dq.pollFirst());
            }
            while (!dq.isEmpty() && prefix[i] <= prefix[dq.peekLast()]) dq.pollLast();
            dq.offerLast(i);
        }
        return minLen <= n ? minLen : -1;
    }

    // Follow-up 1: Longest subarray with sum at least k
    public int longestSubarray(int[] nums, int k) {
        int n = nums.length, maxLen = 0;
        for (int left = 0, right = 0, sum = 0; right < n; right++) {
            sum += nums[right];
            while (left <= right && sum >= k) {
                maxLen = Math.max(maxLen, right - left + 1);
                sum -= nums[left++];
            }
        }
        return maxLen == 0 ? -1 : maxLen;
    }

    // Follow-up 2: All subarrays with sum at least k
    public List<int[]> allSubarraysWithSumAtLeastK(int[] nums, int k) {
        List<int[]> result = new ArrayList<>();
        int n = nums.length;
        for (int left = 0; left < n; left++) {
            int sum = 0;
            for (int right = left; right < n; right++) {
                sum += nums[right];
                if (sum >= k) result.add(new int[]{left, right});
            }
        }
        return result;
    }

    // Follow-up 3: Shortest subarray with sum exactly k
    public int shortestSubarrayWithExactSum(int[] nums, int k) {
        int n = nums.length, minLen = n + 1;
        for (int left = 0; left < n; left++) {
            int sum = 0;
            for (int right = left; right < n; right++) {
                sum += nums[right];
                if (sum == k) minLen = Math.min(minLen, right - left + 1);
            }
        }
        return minLen <= n ? minLen : -1;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ShortestSubarrayWithSumAtLeastK solution = new ShortestSubarrayWithSumAtLeastK();

        // Test case 1: Basic case
        int[] nums1 = {2, -1, 2};
        int k1 = 3;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", k: " + k1 + " Expected: 3");
        System.out.println("Result: " + solution.shortestSubarray(nums1, k1));

        // Test case 2: No valid subarray
        int[] nums2 = {1, 2};
        int k2 = 4;
        System.out.println("\nTest 2 - nums: " + Arrays.toString(nums2) + ", k: " + k2 + " Expected: -1");
        System.out.println("Result: " + solution.shortestSubarray(nums2, k2));

        // Test case 3: Negative numbers
        int[] nums3 = {-2, -1, 2, 1};
        int k3 = 1;
        System.out.println("\nTest 3 - Negative numbers:");
        System.out.println("Result: " + solution.shortestSubarray(nums3, k3));

        // Test case 4: Longest subarray
        int[] nums4 = {1, 2, 3, 4, 5};
        int k4 = 6;
        System.out.println("\nTest 4 - Longest subarray:");
        System.out.println(solution.longestSubarray(nums4, k4));

        // Test case 5: All subarrays with sum at least k
        System.out.println("\nTest 5 - All subarrays with sum at least k:");
        List<int[]> allSubs = solution.allSubarraysWithSumAtLeastK(nums1, k1);
        for (int[] sub : allSubs) System.out.println(Arrays.toString(sub));

        // Test case 6: Shortest subarray with exact sum
        int[] nums5 = {1, 2, 3, 4, 5};
        int k5 = 9;
        System.out.println("\nTest 6 - Shortest subarray with exact sum:");
        System.out.println(solution.shortestSubarrayWithExactSum(nums5, k5));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty nums: " + solution.shortestSubarray(new int[]{}, 1));
        System.out.println("Single element: " + solution.shortestSubarray(new int[]{5}, 5));
        System.out.println("All negative: " + solution.shortestSubarray(new int[]{-1, -2, -3}, 1));

        // Stress test
        System.out.println("\nStress test:");
        int[] large = new int[10000];
        Arrays.fill(large, 1);
        long start = System.nanoTime();
        int result = solution.shortestSubarray(large, 5000);
        long end = System.nanoTime();
        System.out.println("Result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
