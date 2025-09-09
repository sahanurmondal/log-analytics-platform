package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 1043: Partition Array for Maximum Sum
 * https://leetcode.com/problems/partition-array-for-maximum-sum/
 *
 * Description:
 * Given an integer array arr, partition the array into (contiguous) subarrays
 * of length at most k.
 * After partitioning, each subarray has their values changed to become the
 * maximum value of that subarray.
 * Return the largest sum of the given array after partitioning. Test cases are
 * generated so that the
 * answer fits in a 32-bit integer.
 *
 * Constraints:
 * - 1 <= arr.length <= 500
 * - 0 <= arr[i] <= 10^9
 * - 1 <= k <= arr.length
 *
 * Follow-up:
 * - Can you solve it in O(n) space?
 * - What if k can be larger than array length?
 *
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class PartitionArrayForMaximumSum {

    // Approach 1: Recursive (Brute Force) - O(k^n) time, O(n) space
    public int maxSumAfterPartitioningRecursive(int[] arr, int k) {
        return maxSumHelper(arr, 0, k);
    }

    private int maxSumHelper(int[] arr, int start, int k) {
        if (start >= arr.length)
            return 0;

        int maxSum = 0;
        int maxInPartition = 0;

        // Try all possible partition sizes from 1 to k
        for (int i = start; i < Math.min(start + k, arr.length); i++) {
            maxInPartition = Math.max(maxInPartition, arr[i]);
            int partitionSum = maxInPartition * (i - start + 1);
            int remainingSum = maxSumHelper(arr, i + 1, k);
            maxSum = Math.max(maxSum, partitionSum + remainingSum);
        }

        return maxSum;
    }

    // Approach 2: Memoization (Top-down DP) - O(n*k) time, O(n) space
    public int maxSumAfterPartitioningMemo(int[] arr, int k) {
        int[] memo = new int[arr.length];
        Arrays.fill(memo, -1);
        return maxSumMemoHelper(arr, 0, k, memo);
    }

    private int maxSumMemoHelper(int[] arr, int start, int k, int[] memo) {
        if (start >= arr.length)
            return 0;

        if (memo[start] != -1)
            return memo[start];

        int maxSum = 0;
        int maxInPartition = 0;

        for (int i = start; i < Math.min(start + k, arr.length); i++) {
            maxInPartition = Math.max(maxInPartition, arr[i]);
            int partitionSum = maxInPartition * (i - start + 1);
            int remainingSum = maxSumMemoHelper(arr, i + 1, k, memo);
            maxSum = Math.max(maxSum, partitionSum + remainingSum);
        }

        memo[start] = maxSum;
        return maxSum;
    }

    // Approach 3: Tabulation (Bottom-up DP) - O(n*k) time, O(n) space
    public int maxSumAfterPartitioningDP(int[] arr, int k) {
        int n = arr.length;
        int[] dp = new int[n + 1];

        for (int i = n - 1; i >= 0; i--) {
            int maxSum = 0;
            int maxInPartition = 0;

            for (int j = i; j < Math.min(i + k, n); j++) {
                maxInPartition = Math.max(maxInPartition, arr[j]);
                int partitionSum = maxInPartition * (j - i + 1);
                maxSum = Math.max(maxSum, partitionSum + dp[j + 1]);
            }

            dp[i] = maxSum;
        }

        return dp[0];
    }

    // Approach 4: Forward DP - O(n*k) time, O(n) space
    public int maxSumAfterPartitioningForward(int[] arr, int k) {
        int n = arr.length;
        int[] dp = new int[n + 1];

        for (int i = 1; i <= n; i++) {
            int maxInPartition = 0;

            for (int j = 1; j <= Math.min(i, k); j++) {
                maxInPartition = Math.max(maxInPartition, arr[i - j]);
                dp[i] = Math.max(dp[i], dp[i - j] + maxInPartition * j);
            }
        }

        return dp[n];
    }

    // Approach 5: Space Optimized (Rolling Array) - O(n*k) time, O(k) space
    public int maxSumAfterPartitioningOptimized(int[] arr, int k) {
        int n = arr.length;
        int[] dp = new int[k + 1];

        for (int i = 1; i <= n; i++) {
            int maxInPartition = 0;
            int temp = dp[i % (k + 1)];

            for (int j = 1; j <= Math.min(i, k); j++) {
                maxInPartition = Math.max(maxInPartition, arr[i - j]);
                int prevIndex = (i - j) % (k + 1);
                dp[i % (k + 1)] = Math.max(dp[i % (k + 1)], dp[prevIndex] + maxInPartition * j);
            }
        }

        return dp[n % (k + 1)];
    }

    public static void main(String[] args) {
        PartitionArrayForMaximumSum solution = new PartitionArrayForMaximumSum();

        System.out.println("=== Partition Array for Maximum Sum Test Cases ===");

        // Test Case 1: Example from problem
        int[] arr1 = { 1, 15, 7, 9, 2, 5, 10 };
        int k1 = 3;
        System.out.println("Test 1 - Array: " + Arrays.toString(arr1) + ", k: " + k1);
        System.out.println("Recursive: " + solution.maxSumAfterPartitioningRecursive(arr1, k1));
        System.out.println("Memoization: " + solution.maxSumAfterPartitioningMemo(arr1, k1));
        System.out.println("DP: " + solution.maxSumAfterPartitioningDP(arr1, k1));
        System.out.println("Forward DP: " + solution.maxSumAfterPartitioningForward(arr1, k1));
        System.out.println("Optimized: " + solution.maxSumAfterPartitioningOptimized(arr1, k1));
        System.out.println("Expected: 84\n");

        // Test Case 2: Another example
        int[] arr2 = { 1, 4, 1, 5, 7, 3, 6, 1, 9, 9, 3 };
        int k2 = 4;
        System.out.println("Test 2 - Array: " + Arrays.toString(arr2) + ", k: " + k2);
        System.out.println("DP: " + solution.maxSumAfterPartitioningDP(arr2, k2));
        System.out.println("Expected: 83\n");

        // Test Case 3: k equals array length
        int[] arr3 = { 1, 2, 3 };
        int k3 = 3;
        System.out.println("Test 3 - Array: " + Arrays.toString(arr3) + ", k: " + k3);
        System.out.println("DP: " + solution.maxSumAfterPartitioningDP(arr3, k3));
        System.out.println("Expected: 9\n");

        performanceTest();
    }

    private static void performanceTest() {
        PartitionArrayForMaximumSum solution = new PartitionArrayForMaximumSum();

        int[] largeArray = new int[500];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000000000);
        }
        int k = 50;

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ", k: " + k + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maxSumAfterPartitioningMemo(largeArray, k);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maxSumAfterPartitioningDP(largeArray, k);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maxSumAfterPartitioningForward(largeArray, k);
        end = System.nanoTime();
        System.out.println("Forward DP: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
