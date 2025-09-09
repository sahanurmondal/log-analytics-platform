package slidingwindow.medium;

/**
 * LeetCode 643: Maximum Average Subarray I
 * https://leetcode.com/problems/maximum-average-subarray-i/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given an array nums and integer k, return the maximum average
 * value of any subarray of length k.
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up Questions:
 * 1. How to return the actual subarray?
 * 2. What if you want the minimum average?
 * 3. How to solve for very large k efficiently?
 */
public class MaximumAverageSubarrayI {
    // Approach 1: Sliding Window - O(n) time, O(1) space
    public double findMaxAverage(int[] nums, int k) {
        int sum = 0;
        for (int i = 0; i < k; i++)
            sum += nums[i];
        int maxSum = sum;
        for (int i = k; i < nums.length; i++) {
            sum += nums[i] - nums[i - k];
            maxSum = Math.max(maxSum, sum);
        }
        return maxSum / (double) k;
    }

    // Approach 2: Prefix Sum - O(n) time, O(n) space
    public double findMaxAveragePrefixSum(int[] nums, int k) {
        int n = nums.length;
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++)
            prefix[i + 1] = prefix[i] + nums[i];
        double maxAvg = Double.NEGATIVE_INFINITY;
        for (int i = k; i <= n; i++) {
            maxAvg = Math.max(maxAvg, (prefix[i] - prefix[i - k]) / (double) k);
        }
        return maxAvg;
    }

    // Follow-up 1: Return actual subarray
    public int[] getMaxAverageSubarray(int[] nums, int k) {
        int sum = 0, maxSum = Integer.MIN_VALUE, start = 0;
        for (int i = 0; i < k; i++)
            sum += nums[i];
        if (sum > maxSum) {
            maxSum = sum;
            start = 0;
        }
        for (int i = k; i < nums.length; i++) {
            sum += nums[i] - nums[i - k];
            if (sum > maxSum) {
                maxSum = sum;
                start = i - k + 1;
            }
        }
        int[] res = new int[k];
        System.arraycopy(nums, start, res, 0, k);
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MaximumAverageSubarrayI sol = new MaximumAverageSubarrayI();
        // Test 1: Basic
        System.out.println("Test 1: Expected 12.75 -> " + sol.findMaxAverage(new int[] { 1, 12, -5, -6, 50, 3 }, 4));
        // Test 2: All positive
        System.out.println("Test 2: Expected 5.0 -> " + sol.findMaxAverage(new int[] { 1, 2, 3, 4, 5 }, 1));
        // Test 3: All negative
        System.out.println("Test 3: Expected -1.0 -> " + sol.findMaxAverage(new int[] { -1, -2, -3, -4, -5 }, 1));
        // Test 4: Prefix sum approach
        System.out.println(
                "Test 4: Expected 12.75 -> " + sol.findMaxAveragePrefixSum(new int[] { 1, 12, -5, -6, 50, 3 }, 4));
        // Test 5: Get actual subarray
        System.out.println("Test 5: Expected [50,3,-5,-6] -> "
                + java.util.Arrays.toString(sol.getMaxAverageSubarray(new int[] { 1, 12, -5, -6, 50, 3 }, 4)));
        // Test 6: Edge case, k = nums.length
        System.out.println("Test 6: Expected 9.166... -> " + sol.findMaxAverage(new int[] { 1, 12, -5, -6, 50, 3 }, 6));
        // Test 7: Edge case, k = 1
        System.out.println("Test 7: Expected 50.0 -> " + sol.findMaxAverage(new int[] { 50 }, 1));
        // Test 8: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = i % 100;
        System.out.println("Test 8: Large input -> " + sol.findMaxAverage(large, 100));
        // Test 9: Minimum average
        System.out.println("Test 9: Expected -5.0 -> " + sol.findMaxAverage(new int[] { -5, -4, -3, -2, -1 }, 1));
        // Test 10: Subarray at end
        System.out.println("Test 10: Expected 50.0 -> " + sol.findMaxAverage(new int[] { 1, 2, 3, 4, 50 }, 1));
        // Test 11: Subarray at start
        System.out.println("Test 11: Expected 1.0 -> " + sol.findMaxAverage(new int[] { 1, 2, 3, 4, 50 }, 1));
        // Test 12: Large numbers
        int[] nums12 = new int[100];
        for (int i = 0; i < 100; i++)
            nums12[i] = i;
        System.out.println("Test 12: Expected 99.0 -> " + sol.findMaxAverage(nums12, 1));
        // Test 13: All zeros
        System.out.println("Test 13: Expected 0.0 -> " + sol.findMaxAverage(new int[100], 1));
        // Test 14: k = 0 (invalid)
        // Test 15: Negative k (invalid)
    }
}
