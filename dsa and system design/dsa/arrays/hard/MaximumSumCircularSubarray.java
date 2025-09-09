package arrays.hard;

/**
 * LeetCode 918: Maximum Sum Circular Subarray
 * https://leetcode.com/problems/maximum-sum-circular-subarray/
 *
 * Description:
 * Given a circular integer array nums of length n, return the maximum possible
 * sum of a non-empty subarray of nums.
 * A circular array means the end of the array connects to the beginning of the
 * array.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 3 * 10^4
 * - -3 * 10^4 <= nums[i] <= 3 * 10^4
 *
 * Follow-up:
 * - Can you solve it using Kadane's algorithm?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class MaximumSumCircularSubarray {

    public int maxSubarraySumCircular(int[] nums) {
        // Case 1: Maximum subarray is non-circular (normal Kadane's)
        int maxKadane = kadaneMax(nums);

        // Case 2: Maximum subarray is circular
        // This equals total sum - minimum subarray
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        int minKadane = kadaneMin(nums);
        int maxCircular = totalSum - minKadane;

        // If all numbers are negative, maxCircular will be 0
        // In this case, return maxKadane
        return maxCircular == 0 ? maxKadane : Math.max(maxKadane, maxCircular);
    }

    private int kadaneMax(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    private int kadaneMin(int[] nums) {
        int minSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.min(nums[i], currentSum + nums[i]);
            minSum = Math.min(minSum, currentSum);
        }

        return minSum;
    }

    // Alternative solution - Prefix/Suffix approach
    public int maxSubarraySumCircularPrefix(int[] nums) {
        int n = nums.length;

        // Case 1: Normal maximum subarray
        int normalMax = kadaneMax(nums);

        // Case 2: Circular maximum subarray
        int[] rightSum = new int[n];
        rightSum[n - 1] = nums[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            rightSum[i] = rightSum[i + 1] + nums[i];
        }

        int[] maxRight = new int[n];
        maxRight[n - 1] = rightSum[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            maxRight[i] = Math.max(maxRight[i + 1], rightSum[i]);
        }

        int circularMax = Integer.MIN_VALUE;
        int leftSum = 0;
        for (int i = 0; i < n - 1; i++) {
            leftSum += nums[i];
            circularMax = Math.max(circularMax, leftSum + maxRight[i + 1]);
        }

        return Math.max(normalMax, circularMax);
    }

    public static void main(String[] args) {
        MaximumSumCircularSubarray solution = new MaximumSumCircularSubarray();

        // Test Case 1: Normal case
        System.out.println(solution.maxSubarraySumCircular(new int[] { 1, -2, 3, -2 })); // Expected: 3

        // Test Case 2: Edge case - circular is better
        System.out.println(solution.maxSubarraySumCircular(new int[] { 5, -3, 5 })); // Expected: 10

        // Test Case 3: Corner case - all negative
        System.out.println(solution.maxSubarraySumCircular(new int[] { -3, -2, -3 })); // Expected: -2

        // Test Case 4: Single element
        System.out.println(solution.maxSubarraySumCircular(new int[] { 3 })); // Expected: 3

        // Test Case 5: All positive
        System.out.println(solution.maxSubarraySumCircular(new int[] { 1, 2, 3, 4, 5 })); // Expected: 15

        // Test Case 6: Mixed values
        System.out.println(solution.maxSubarraySumCircular(new int[] { -2, -3, -1 })); // Expected: -1

        // Test Case 7: Circular wrapping needed
        System.out.println(solution.maxSubarraySumCircular(new int[] { 3, -1, 2, -1 })); // Expected: 4

        // Test Case 8: Two elements
        System.out.println(solution.maxSubarraySumCircular(new int[] { -2, 1 })); // Expected: 1

        // Test Case 9: Complex pattern
        System.out.println(solution.maxSubarraySumCircular(new int[] { 9, -4, -7, 9 })); // Expected: 18

        // Test Case 10: Large negative in middle
        System.out.println(solution.maxSubarraySumCircular(new int[] { 8, -1, -3, -9, 2, 4, 6 })); // Expected: 22
    }
}
