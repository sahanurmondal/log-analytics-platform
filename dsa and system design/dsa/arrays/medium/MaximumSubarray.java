package arrays.medium;

/**
 * LeetCode 53: Maximum Subarray
 * https://leetcode.com/problems/maximum-subarray/
 *
 * Description:
 * Given an integer array nums, find the contiguous subarray (containing at
 * least one number)
 * which has the largest sum and return its sum.
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^5
 * - -10^5 <= nums[i] <= 10^5
 *
 * Follow-up:
 * - If you have figured out the O(n) solution, try coding another solution
 * using the divide and conquer approach
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class MaximumSubarray {

    // Main solution - Kadane's Algorithm
    public int maxSubArray(int[] nums) {
        int maxSum = nums[0];
        int currentSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            currentSum = Math.max(nums[i], currentSum + nums[i]);
            maxSum = Math.max(maxSum, currentSum);
        }

        return maxSum;
    }

    // Follow-up solution - Divide and Conquer
    public int maxSubArrayDivideConquer(int[] nums) {
        return maxSubArrayHelper(nums, 0, nums.length - 1);
    }

    private int maxSubArrayHelper(int[] nums, int left, int right) {
        if (left == right)
            return nums[left];

        int mid = left + (right - left) / 2;

        // Maximum subarray in left half
        int leftMax = maxSubArrayHelper(nums, left, mid);

        // Maximum subarray in right half
        int rightMax = maxSubArrayHelper(nums, mid + 1, right);

        // Maximum subarray crossing the middle
        int leftSum = Integer.MIN_VALUE;
        int sum = 0;
        for (int i = mid; i >= left; i--) {
            sum += nums[i];
            leftSum = Math.max(leftSum, sum);
        }

        int rightSum = Integer.MIN_VALUE;
        sum = 0;
        for (int i = mid + 1; i <= right; i++) {
            sum += nums[i];
            rightSum = Math.max(rightSum, sum);
        }

        int crossSum = leftSum + rightSum;

        return Math.max(Math.max(leftMax, rightMax), crossSum);
    }

    // Alternative solution - DP approach (for understanding)
    public int maxSubArrayDP(int[] nums) {
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        int maxSum = nums[0];

        for (int i = 1; i < nums.length; i++) {
            dp[i] = Math.max(nums[i], dp[i - 1] + nums[i]);
            maxSum = Math.max(maxSum, dp[i]);
        }

        return maxSum;
    }

    public static void main(String[] args) {
        MaximumSubarray solution = new MaximumSubarray();

        // Test Case 1: Normal case
        System.out.println(solution.maxSubArray(new int[] { -2, 1, -3, 4, -1, 2, 1, -5, 4 })); // Expected: 6

        // Test Case 2: Edge case - single element
        System.out.println(solution.maxSubArray(new int[] { 1 })); // Expected: 1

        // Test Case 3: Corner case - all negatives
        System.out.println(solution.maxSubArray(new int[] { -2, -1, -3 })); // Expected: -1

        // Test Case 4: Large input - all positives
        System.out.println(solution.maxSubArray(new int[] { 1, 2, 3, 4, 5 })); // Expected: 15

        // Test Case 5: Mixed positive/negative
        System.out.println(solution.maxSubArray(new int[] { 5, 4, -1, 7, 8 })); // Expected: 23

        // Test Case 6: Special case - single negative
        System.out.println(solution.maxSubArray(new int[] { -1 })); // Expected: -1

        // Test Case 7: Zero included
        System.out.println(solution.maxSubArray(new int[] { -2, 0, -1 })); // Expected: 0

        // Test Case 8: Alternating pattern
        System.out.println(solution.maxSubArray(new int[] { 1, -1, 1, -1, 1 })); // Expected: 1

        // Test Case 9: Large negative in middle
        System.out.println(solution.maxSubArray(new int[] { 1, 2, -100, 3, 4 })); // Expected: 7

        // Test Case 10: Two separate positive segments
        System.out.println(solution.maxSubArray(new int[] { 1, 2, -5, 4, 5 })); // Expected: 9
    }
}
