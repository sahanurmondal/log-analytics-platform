package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 413: Arithmetic Slices
 * https://leetcode.com/problems/arithmetic-slices/
 *
 * Description:
 * An integer array is called arithmetic if it consists of at least three
 * elements and if the difference
 * between any two consecutive elements is the same.
 * Given an integer array nums, return the number of arithmetic subarrays of
 * nums.
 * A subarray is a contiguous subsequence of the array.
 *
 * Constraints:
 * - 1 <= nums.length <= 5000
 * - -1000 <= nums[i] <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * - Can you find all arithmetic slices?
 * 
 * Company Tags: Google, Microsoft, Amazon, Facebook
 * Difficulty: Medium
 */
public class ArithmeticSlices {

    // Approach 1: Brute Force - O(n^3) time, O(1) space
    public int numberOfArithmeticSlicesBruteForce(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return 0;

        int count = 0;

        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 2; j < n; j++) {
                if (isArithmetic(nums, i, j)) {
                    count++;
                }
            }
        }

        return count;
    }

    private boolean isArithmetic(int[] nums, int start, int end) {
        if (end - start < 2)
            return false;

        int diff = nums[start + 1] - nums[start];
        for (int i = start + 1; i < end; i++) {
            if (nums[i + 1] - nums[i] != diff) {
                return false;
            }
        }
        return true;
    }

    // Approach 2: DP with Array - O(n) time, O(n) space
    public int numberOfArithmeticSlicesDP(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return 0;

        int[] dp = new int[n];
        int totalCount = 0;

        for (int i = 2; i < n; i++) {
            if (nums[i] - nums[i - 1] == nums[i - 1] - nums[i - 2]) {
                dp[i] = dp[i - 1] + 1;
                totalCount += dp[i];
            }
        }

        return totalCount;
    }

    // Approach 3: Space Optimized DP - O(n) time, O(1) space
    public int numberOfArithmeticSlicesOptimized(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return 0;

        int current = 0;
        int totalCount = 0;

        for (int i = 2; i < n; i++) {
            if (nums[i] - nums[i - 1] == nums[i - 1] - nums[i - 2]) {
                current++;
                totalCount += current;
            } else {
                current = 0;
            }
        }

        return totalCount;
    }

    // Approach 4: Mathematical Formula - O(n) time, O(1) space
    public int numberOfArithmeticSlicesMath(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return 0;

        int totalCount = 0;
        int consecutiveCount = 0;

        for (int i = 2; i < n; i++) {
            if (nums[i] - nums[i - 1] == nums[i - 1] - nums[i - 2]) {
                consecutiveCount++;
            } else {
                if (consecutiveCount > 0) {
                    // For k consecutive arithmetic pairs, number of subarrays = k*(k+1)/2
                    totalCount += consecutiveCount * (consecutiveCount + 1) / 2;
                    consecutiveCount = 0;
                }
            }
        }

        // Handle the last sequence
        if (consecutiveCount > 0) {
            totalCount += consecutiveCount * (consecutiveCount + 1) / 2;
        }

        return totalCount;
    }

    // Approach 5: Two Pointers - O(n) time, O(1) space
    public int numberOfArithmeticSlicesTwoPointers(int[] nums) {
        int n = nums.length;
        if (n < 3)
            return 0;

        int totalCount = 0;
        int i = 0;

        while (i < n - 2) {
            int j = i + 1;

            // Find the longest arithmetic sequence starting at i
            while (j < n - 1 && nums[j + 1] - nums[j] == nums[j] - nums[j - 1]) {
                j++;
            }

            // Count arithmetic slices in this sequence
            int length = j - i + 1;
            if (length >= 3) {
                // Number of arithmetic slices of length >= 3 in a sequence of length n
                // is (n-2) + (n-3) + ... + 1 = (n-2)*(n-1)/2
                int arithmeticSlices = (length - 2) * (length - 1) / 2;
                totalCount += arithmeticSlices;
            }

            i = j;
        }

        return totalCount;
    }

    // Bonus: Get all arithmetic slices
    public java.util.List<int[]> getAllArithmeticSlices(int[] nums) {
        java.util.List<int[]> result = new java.util.ArrayList<>();
        int n = nums.length;
        if (n < 3)
            return result;

        for (int i = 0; i < n - 2; i++) {
            for (int j = i + 2; j < n; j++) {
                if (isArithmetic(nums, i, j)) {
                    result.add(Arrays.copyOfRange(nums, i, j + 1));
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ArithmeticSlices solution = new ArithmeticSlices();

        System.out.println("=== Arithmetic Slices Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 1, 2, 3, 4 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("Brute Force: " + solution.numberOfArithmeticSlicesBruteForce(nums1));
        System.out.println("DP: " + solution.numberOfArithmeticSlicesDP(nums1));
        System.out.println("Optimized: " + solution.numberOfArithmeticSlicesOptimized(nums1));
        System.out.println("Mathematical: " + solution.numberOfArithmeticSlicesMath(nums1));
        System.out.println("Two Pointers: " + solution.numberOfArithmeticSlicesTwoPointers(nums1));

        java.util.List<int[]> slices1 = solution.getAllArithmeticSlices(nums1);
        System.out.println("All slices:");
        for (int[] slice : slices1) {
            System.out.println("  " + Arrays.toString(slice));
        }
        System.out.println("Expected: 3\n");

        // Test Case 2: Another example
        int[] nums2 = { 1, 3, 5, 7, 9 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("Optimized: " + solution.numberOfArithmeticSlicesOptimized(nums2));
        System.out.println("Expected: 6\n");

        // Test Case 3: No arithmetic slices
        int[] nums3 = { 1, 1, 2, 5, 7 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("Optimized: " + solution.numberOfArithmeticSlicesOptimized(nums3));
        System.out.println("Expected: 0\n");

        // Test Case 4: Minimum length
        int[] nums4 = { 1, 2 };
        System.out.println("Test 4 - Array: " + Arrays.toString(nums4));
        System.out.println("Optimized: " + solution.numberOfArithmeticSlicesOptimized(nums4));
        System.out.println("Expected: 0\n");

        performanceTest();
    }

    private static void performanceTest() {
        ArithmeticSlices solution = new ArithmeticSlices();

        // Generate arithmetic sequence for performance testing
        int[] largeArray = new int[5000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = i * 2; // Arithmetic sequence with difference 2
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.numberOfArithmeticSlicesDP(largeArray);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.numberOfArithmeticSlicesOptimized(largeArray);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.numberOfArithmeticSlicesMath(largeArray);
        end = System.nanoTime();
        System.out.println("Mathematical: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result4 = solution.numberOfArithmeticSlicesTwoPointers(largeArray);
        end = System.nanoTime();
        System.out.println("Two Pointers: " + result4 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
