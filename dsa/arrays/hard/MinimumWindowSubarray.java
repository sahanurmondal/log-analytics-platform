package arrays.hard;

/**
 * LeetCode 209: Minimum Size Subarray Sum
 * https://leetcode.com/problems/minimum-size-subarray-sum/
 *
 * Description:
 * Given an array of positive integers nums and a positive integer target,
 * return the minimal length of a contiguous subarray whose sum is greater than
 * or equal to target.
 * If there is no such subarray, return 0 instead.
 *
 * Constraints:
 * - 1 <= target <= 10^9
 * - 1 <= nums.length <= 10^5
 * - 1 <= nums[i] <= 10^5
 *
 * Follow-up:
 * - Can you solve it in O(n) time complexity?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class MinimumWindowSubarray {

    public int minSubArrayLen(int target, int[] nums) {
        int left = 0, sum = 0;
        int minLen = Integer.MAX_VALUE;

        for (int right = 0; right < nums.length; right++) {
            sum += nums[right];

            while (sum >= target) {
                minLen = Math.min(minLen, right - left + 1);
                sum -= nums[left];
                left++;
            }
        }

        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    // Alternative solution - Binary search
    public int minSubArrayLenBinarySearch(int target, int[] nums) {
        int n = nums.length;
        int[] prefixSum = new int[n + 1];

        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        int minLen = Integer.MAX_VALUE;

        for (int i = 0; i < n; i++) {
            int targetSum = target + prefixSum[i];
            int bound = binarySearch(prefixSum, i + 1, n, targetSum);
            if (bound != -1) {
                minLen = Math.min(minLen, bound - i);
            }
        }

        return minLen == Integer.MAX_VALUE ? 0 : minLen;
    }

    private int binarySearch(int[] prefixSum, int left, int right, int target) {
        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (prefixSum[mid] >= target) {
                if (mid == left || prefixSum[mid - 1] < target) {
                    return mid;
                }
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        MinimumWindowSubarray solution = new MinimumWindowSubarray();

        // Test Case 1: Normal case
        System.out.println(solution.minSubArrayLen(7, new int[] { 2, 3, 1, 2, 4, 3 })); // Expected: 2

        // Test Case 2: Edge case - single element sufficient
        System.out.println(solution.minSubArrayLen(4, new int[] { 1, 4, 4 })); // Expected: 1

        // Test Case 3: Corner case - no valid subarray
        System.out.println(solution.minSubArrayLen(11, new int[] { 1, 1, 1, 1, 1, 1, 1, 1 })); // Expected: 0

        // Test Case 4: Entire array needed
        System.out.println(solution.minSubArrayLen(15, new int[] { 1, 2, 3, 4, 5 })); // Expected: 5

        // Test Case 5: Single element array - sufficient
        System.out.println(solution.minSubArrayLen(3, new int[] { 5 })); // Expected: 1

        // Test Case 6: Single element array - insufficient
        System.out.println(solution.minSubArrayLen(10, new int[] { 5 })); // Expected: 0

        // Test Case 7: All same elements
        System.out.println(solution.minSubArrayLen(10, new int[] { 3, 3, 3, 3, 3 })); // Expected: 4

        // Test Case 8: Large target
        System.out.println(solution.minSubArrayLen(1000000000, new int[] { 100000, 100000, 100000 })); // Expected: 0

        // Test Case 9: Target equals single element
        System.out.println(solution.minSubArrayLen(5, new int[] { 1, 2, 5, 3, 1 })); // Expected: 1

        // Test Case 10: Complex pattern
        System.out.println(solution.minSubArrayLen(6, new int[] { 10, 2, 3 })); // Expected: 1
    }
}