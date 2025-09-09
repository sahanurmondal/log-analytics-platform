package hashmaps.hard;

/**
 * LeetCode 209: Minimum Size Subarray Sum
 * https://leetcode.com/problems/minimum-size-subarray-sum/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 8+ interviews)
 *
 * Description: Given an array of positive integers `nums` and a positive
 * integer `target`, return the minimal length of a contiguous subarray of which
 * the sum is greater than or equal to `target`. If there is no such subarray,
 * return 0 instead.
 *
 * Constraints:
 * - 1 <= target <= 10^9
 * - 1 <= nums.length <= 10^5
 * - 1 <= nums[i] <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you solve this in O(n) time?
 * 2. What if the array can contain negative numbers?
 * 3. Can you solve this in O(n log n) time? (Prefix Sum + Binary Search)
 */
public class SmallestSubarrayWithSumGreaterThanK {

    // Approach 1: Sliding Window - O(n) time, O(1) space
    public int minSubArrayLen(int target, int[] nums) {
        int minLength = Integer.MAX_VALUE;
        int left = 0;
        int currentSum = 0;

        for (int right = 0; right < nums.length; right++) {
            currentSum += nums[right];

            while (currentSum >= target) {
                minLength = Math.min(minLength, right - left + 1);
                currentSum -= nums[left];
                left++;
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    // Approach 2: Prefix Sum + Binary Search - O(n log n) time, O(n) space
    public int minSubArrayLenBinarySearch(int target, int[] nums) {
        int n = nums.length;
        int minLength = Integer.MAX_VALUE;
        int[] prefixSum = new int[n + 1];

        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        for (int i = 0; i <= n; i++) {
            int searchTarget = target + prefixSum[i];
            int end = binarySearch(prefixSum, i + 1, n, searchTarget);
            if (end <= n) {
                minLength = Math.min(minLength, end - i);
            }
        }

        return minLength == Integer.MAX_VALUE ? 0 : minLength;
    }

    private int binarySearch(int[] arr, int low, int high, int key) {
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] >= key) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return low;
    }

    public static void main(String[] args) {
        SmallestSubarrayWithSumGreaterThanK solution = new SmallestSubarrayWithSumGreaterThanK();

        // Test case 1
        int[] nums1 = { 2, 3, 1, 2, 4, 3 };
        int target1 = 7;
        System.out.println("Min length 1 (Sliding Window): " + solution.minSubArrayLen(target1, nums1)); // 2
        System.out.println("Min length 1 (Binary Search): " + solution.minSubArrayLenBinarySearch(target1, nums1)); // 2

        // Test case 2
        int[] nums2 = { 1, 4, 4 };
        int target2 = 4;
        System.out.println("Min length 2: " + solution.minSubArrayLen(target2, nums2)); // 1

        // Test case 3
        int[] nums3 = { 1, 1, 1, 1, 1, 1, 1, 1 };
        int target3 = 11;
        System.out.println("Min length 3: " + solution.minSubArrayLen(target3, nums3)); // 0
    }
}
