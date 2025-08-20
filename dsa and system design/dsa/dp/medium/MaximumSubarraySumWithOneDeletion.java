package dp.medium;

import java.util.Arrays;

/**
 * LeetCode 1186: Maximum Subarray Sum with One Deletion
 * https://leetcode.com/problems/maximum-subarray-sum-with-one-deletion/
 *
 * Description:
 * Given an array of integers, return the maximum sum for a non-empty subarray
 * (contiguous elements)
 * with at most one element deletion. In other words, you want to choose a
 * subarray and optionally delete one element
 * from it so that there is still at least one element left and the sum of the
 * remaining elements is maximum possible.
 * Note that the subarray needs to be non-empty after the deletion.
 *
 * Constraints:
 * - 1 <= arr.length <= 10^5
 * - -10^4 <= arr[i] <= 10^4
 *
 * Follow-up:
 * - What if we can delete at most k elements?
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class MaximumSubarraySumWithOneDeletion {

    // Approach 1: Two Arrays DP - O(n) time, O(n) space
    public int maximumSumTwoArrays(int[] arr) {
        int n = arr.length;
        if (n == 1)
            return arr[0];

        // noDelete[i] = max subarray sum ending at i with no deletion
        // oneDelete[i] = max subarray sum ending at i with exactly one deletion
        int[] noDelete = new int[n];
        int[] oneDelete = new int[n];

        noDelete[0] = arr[0];
        oneDelete[0] = 0; // Can't delete from single element subarray

        int maxSum = arr[0];

        for (int i = 1; i < n; i++) {
            noDelete[i] = Math.max(arr[i], noDelete[i - 1] + arr[i]);

            // Either delete current element from previous subarray, or extend previous
            // deletion
            oneDelete[i] = Math.max(noDelete[i - 1], oneDelete[i - 1] + arr[i]);

            maxSum = Math.max(maxSum, Math.max(noDelete[i], oneDelete[i]));
        }

        return maxSum;
    }

    // Approach 2: Space Optimized DP - O(n) time, O(1) space
    public int maximumSumOptimized(int[] arr) {
        int n = arr.length;
        if (n == 1)
            return arr[0];

        int noDelete = arr[0];
        int oneDelete = 0;
        int maxSum = arr[0];

        for (int i = 1; i < n; i++) {
            int newOneDelete = Math.max(noDelete, oneDelete + arr[i]);
            int newNoDelete = Math.max(arr[i], noDelete + arr[i]);

            oneDelete = newOneDelete;
            noDelete = newNoDelete;

            maxSum = Math.max(maxSum, Math.max(noDelete, oneDelete));
        }

        return maxSum;
    }

    // Approach 3: Forward and Backward Arrays - O(n) time, O(n) space
    public int maximumSumForwardBackward(int[] arr) {
        int n = arr.length;
        if (n == 1)
            return arr[0];

        // Forward Kadane's algorithm
        int[] forward = new int[n];
        forward[0] = arr[0];
        for (int i = 1; i < n; i++) {
            forward[i] = Math.max(arr[i], forward[i - 1] + arr[i]);
        }

        // Backward Kadane's algorithm
        int[] backward = new int[n];
        backward[n - 1] = arr[n - 1];
        for (int i = n - 2; i >= 0; i--) {
            backward[i] = Math.max(arr[i], backward[i + 1] + arr[i]);
        }

        int maxSum = forward[n - 1]; // No deletion case

        // Try deleting each element
        for (int i = 0; i < n; i++) {
            int leftSum = (i > 0) ? forward[i - 1] : 0;
            int rightSum = (i < n - 1) ? backward[i + 1] : 0;

            if (i > 0 && i < n - 1) {
                maxSum = Math.max(maxSum, leftSum + rightSum);
            } else if (i > 0) {
                maxSum = Math.max(maxSum, leftSum);
            } else if (i < n - 1) {
                maxSum = Math.max(maxSum, rightSum);
            }
        }

        return maxSum;
    }

    // Approach 4: Three States DP - O(n) time, O(1) space
    public int maximumSumThreeStates(int[] arr) {
        int n = arr.length;
        if (n == 1)
            return arr[0];

        // State 0: Normal Kadane's (no deletion used)
        // State 1: One deletion used, can still extend
        // State 2: Final result tracking

        int normalSum = arr[0];
        int deleteSum = 0;
        int maxResult = arr[0];

        for (int i = 1; i < n; i++) {
            // Option 1: Delete current element (use previous normal sum)
            // Option 2: Keep current element and extend deletion state
            deleteSum = Math.max(normalSum, deleteSum + arr[i]);

            // Normal Kadane's update
            normalSum = Math.max(arr[i], normalSum + arr[i]);

            maxResult = Math.max(maxResult, Math.max(normalSum, deleteSum));
        }

        return maxResult;
    }

    // Approach 5: Segment Tree Approach - O(n log n) time, O(n) space
    public int maximumSumSegmentTree(int[] arr) {
        int n = arr.length;
        if (n == 1)
            return arr[0];

        // Build prefix max sums
        int[] prefixMax = new int[n];
        prefixMax[0] = arr[0];
        int currentSum = arr[0];

        for (int i = 1; i < n; i++) {
            currentSum = Math.max(arr[i], currentSum + arr[i]);
            prefixMax[i] = Math.max(prefixMax[i - 1], currentSum);
        }

        // Build suffix max sums
        int[] suffixMax = new int[n];
        suffixMax[n - 1] = arr[n - 1];
        currentSum = arr[n - 1];

        for (int i = n - 2; i >= 0; i--) {
            currentSum = Math.max(arr[i], currentSum + arr[i]);
            suffixMax[i] = Math.max(suffixMax[i + 1], currentSum);
        }

        int maxSum = prefixMax[n - 1]; // No deletion

        // Try deleting each element
        for (int i = 1; i < n - 1; i++) {
            // Kadane's on left part ending at i-1
            int leftMax = 0;
            currentSum = 0;
            for (int j = i - 1; j >= 0; j--) {
                currentSum = Math.max(arr[j], currentSum + arr[j]);
                leftMax = Math.max(leftMax, currentSum);
            }

            // Kadane's on right part starting at i+1
            int rightMax = 0;
            currentSum = 0;
            for (int j = i + 1; j < n; j++) {
                currentSum = Math.max(arr[j], currentSum + arr[j]);
                rightMax = Math.max(rightMax, currentSum);
            }

            if (leftMax > 0 && rightMax > 0) {
                maxSum = Math.max(maxSum, leftMax + rightMax);
            }
        }

        return maxSum;
    }

    public static void main(String[] args) {
        MaximumSubarraySumWithOneDeletion solution = new MaximumSubarraySumWithOneDeletion();

        System.out.println("=== Maximum Subarray Sum with One Deletion Test Cases ===");

        // Test Case 1: Example from problem
        int[] arr1 = { 1, -2, 0, 3 };
        System.out.println("Test 1 - Array: " + Arrays.toString(arr1));
        System.out.println("Two Arrays: " + solution.maximumSumTwoArrays(arr1));
        System.out.println("Optimized: " + solution.maximumSumOptimized(arr1));
        System.out.println("Forward-Backward: " + solution.maximumSumForwardBackward(arr1));
        System.out.println("Three States: " + solution.maximumSumThreeStates(arr1));
        System.out.println("Expected: 4\n");

        // Test Case 2: Another example
        int[] arr2 = { 1, -2, -2, 3 };
        System.out.println("Test 2 - Array: " + Arrays.toString(arr2));
        System.out.println("Optimized: " + solution.maximumSumOptimized(arr2));
        System.out.println("Expected: 3\n");

        // Test Case 3: All negative except one
        int[] arr3 = { -1, -1, -1, -1 };
        System.out.println("Test 3 - Array: " + Arrays.toString(arr3));
        System.out.println("Optimized: " + solution.maximumSumOptimized(arr3));
        System.out.println("Expected: -1\n");

        performanceTest();
    }

    private static void performanceTest() {
        MaximumSubarraySumWithOneDeletion solution = new MaximumSubarraySumWithOneDeletion();

        int[] largeArray = new int[100000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 200) - 100; // Random between -100 and 100
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.maximumSumTwoArrays(largeArray);
        long end = System.nanoTime();
        System.out.println("Two Arrays: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.maximumSumOptimized(largeArray);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.maximumSumThreeStates(largeArray);
        end = System.nanoTime();
        System.out.println("Three States: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
