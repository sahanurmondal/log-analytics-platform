package arrays.hard;

/**
 * LeetCode 410: Split Array Largest Sum
 * https://leetcode.com/problems/split-array-largest-sum/
 *
 * Description:
 * Given an array nums which consists of non-negative integers and an integer m,
 * you can split the array into m non-empty continuous subarrays.
 * Write an algorithm to minimize the largest sum among these m subarrays.
 *
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - 0 <= nums[i] <= 10^6
 * - 1 <= m <= min(50, nums.length)
 *
 * Follow-up:
 * - Can you solve it using binary search?
 * 
 * Time Complexity: O(n * log(sum))
 * Space Complexity: O(1)
 */
public class SplitArrayLargestSum {

    public int splitArray(int[] nums, int m) {
        long left = 0, right = 0;

        for (int num : nums) {
            left = Math.max(left, num);
            right += num;
        }

        while (left < right) {
            long mid = left + (right - left) / 2;

            if (canSplit(nums, m, mid)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return (int) left;
    }

    private boolean canSplit(int[] nums, int m, long maxSum) {
        int subarrays = 1;
        long currentSum = 0;

        for (int num : nums) {
            if (currentSum + num > maxSum) {
                subarrays++;
                currentSum = num;
                if (subarrays > m) {
                    return false;
                }
            } else {
                currentSum += num;
            }
        }

        return true;
    }

    // Alternative solution - Dynamic Programming
    public int splitArrayDP(int[] nums, int m) {
        int n = nums.length;
        long[] prefixSum = new long[n + 1];

        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        long[][] dp = new long[m + 1][n + 1];

        // Initialize with maximum values
        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                dp[i][j] = Long.MAX_VALUE;
            }
        }

        dp[0][0] = 0;

        for (int i = 1; i <= m; i++) {
            for (int j = i; j <= n; j++) {
                for (int k = i - 1; k < j; k++) {
                    if (dp[i - 1][k] != Long.MAX_VALUE) {
                        long sum = prefixSum[j] - prefixSum[k];
                        dp[i][j] = Math.min(dp[i][j], Math.max(dp[i - 1][k], sum));
                    }
                }
            }
        }

        return (int) dp[m][n];
    }

    public static void main(String[] args) {
        SplitArrayLargestSum solution = new SplitArrayLargestSum();

        // Test Case 1: Normal case
        System.out.println(solution.splitArray(new int[] { 7, 2, 5, 10, 8 }, 2)); // Expected: 18

        // Test Case 2: Edge case - single subarray
        System.out.println(solution.splitArray(new int[] { 1, 2, 3, 4, 5 }, 1)); // Expected: 15

        // Test Case 3: Corner case - each element in separate subarray
        System.out.println(solution.splitArray(new int[] { 1, 4, 4 }, 3)); // Expected: 4

        // Test Case 4: All same elements
        System.out.println(solution.splitArray(new int[] { 2, 2, 2, 2 }, 2)); // Expected: 4

        // Test Case 5: Single element
        System.out.println(solution.splitArray(new int[] { 1 }, 1)); // Expected: 1

        // Test Case 6: Two elements
        System.out.println(solution.splitArray(new int[] { 1, 2 }, 2)); // Expected: 2

        // Test Case 7: Large numbers
        System.out.println(solution.splitArray(new int[] { 1000000 }, 1)); // Expected: 1000000

        // Test Case 8: Zero elements
        System.out.println(solution.splitArray(new int[] { 0, 0, 0 }, 2)); // Expected: 0

        // Test Case 9: Increasing sequence
        System.out.println(solution.splitArray(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 }, 5)); // Expected: 15

        // Test Case 10: Decreasing sequence
        System.out.println(solution.splitArray(new int[] { 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 }, 3)); // Expected: 20
    }
}