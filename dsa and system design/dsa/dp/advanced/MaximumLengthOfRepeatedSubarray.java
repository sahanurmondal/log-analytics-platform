package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 718: Maximum Length of Repeated Subarray
 * https://leetcode.com/problems/maximum-length-of-repeated-subarray/
 *
 * Description:
 * Given two integer arrays nums1 and nums2, return the maximum length of a
 * subarray that appears in both arrays.
 *
 * Constraints:
 * - 1 <= nums1.length, nums2.length <= 1000
 * - 0 <= nums1[i], nums2[i] <= 100
 *
 * Follow-up:
 * - Can you solve it in O(min(m,n)) space?
 * - What if we need to find all such subarrays?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class MaximumLengthOfRepeatedSubarray {

    // Approach 1: Brute Force - O(m*n*(min(m,n))) time, O(1) space
    public int findLengthBruteForce(int[] nums1, int[] nums2) {
        int maxLength = 0;

        for (int i = 0; i < nums1.length; i++) {
            for (int j = 0; j < nums2.length; j++) {
                int length = 0;
                while (i + length < nums1.length && j + length < nums2.length &&
                        nums1[i + length] == nums2[j + length]) {
                    length++;
                }
                maxLength = Math.max(maxLength, length);
            }
        }

        return maxLength;
    }

    // Approach 2: 2D DP - O(m*n) time, O(m*n) space
    public int findLengthDP(int[] nums1, int[] nums2) {
        int m = nums1.length, n = nums2.length;
        int[][] dp = new int[m + 1][n + 1];
        int maxLength = 0;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    maxLength = Math.max(maxLength, dp[i][j]);
                }
            }
        }

        return maxLength;
    }

    // Approach 3: Space Optimized DP - O(m*n) time, O(min(m,n)) space
    public int findLengthOptimized(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return findLengthOptimized(nums2, nums1);
        }

        int m = nums1.length, n = nums2.length;
        int[] prev = new int[m + 1];
        int[] curr = new int[m + 1];
        int maxLength = 0;

        for (int j = 1; j <= n; j++) {
            for (int i = 1; i <= m; i++) {
                if (nums1[i - 1] == nums2[j - 1]) {
                    curr[i] = prev[i - 1] + 1;
                    maxLength = Math.max(maxLength, curr[i]);
                } else {
                    curr[i] = 0;
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
            Arrays.fill(curr, 0);
        }

        return maxLength;
    }

    // Approach 4: Rolling Hash - O((m+n)*min(m,n)*log(min(m,n))) time, O(min(m,n))
    // space
    public int findLengthRollingHash(int[] nums1, int[] nums2) {
        int left = 0, right = Math.min(nums1.length, nums2.length) + 1;

        while (left < right) {
            int mid = (left + right) / 2;
            if (hasCommonSubarray(nums1, nums2, mid)) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left - 1;
    }

    private boolean hasCommonSubarray(int[] nums1, int[] nums2, int length) {
        if (length == 0)
            return true;

        long base = 101;
        long mod = 1000000007;
        long basePow = 1;

        for (int i = 0; i < length - 1; i++) {
            basePow = (basePow * base) % mod;
        }

        java.util.Set<Long> hashes1 = new java.util.HashSet<>();

        // Calculate hash for all subarrays of length in nums1
        long hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (hash * base + nums1[i]) % mod;
        }
        hashes1.add(hash);

        for (int i = length; i < nums1.length; i++) {
            hash = (hash - (nums1[i - length] * basePow) % mod + mod) % mod;
            hash = (hash * base + nums1[i]) % mod;
            hashes1.add(hash);
        }

        // Check if any subarray of length in nums2 has matching hash
        hash = 0;
        for (int i = 0; i < length; i++) {
            hash = (hash * base + nums2[i]) % mod;
        }
        if (hashes1.contains(hash))
            return true;

        for (int i = length; i < nums2.length; i++) {
            hash = (hash - (nums2[i - length] * basePow) % mod + mod) % mod;
            hash = (hash * base + nums2[i]) % mod;
            if (hashes1.contains(hash))
                return true;
        }

        return false;
    }

    // Approach 5: Diagonal DP - O(m*n) time, O(1) space
    public int findLengthDiagonal(int[] nums1, int[] nums2) {
        int maxLength = 0;

        // Check diagonals starting from first row
        for (int k = 0; k < nums2.length; k++) {
            maxLength = Math.max(maxLength, findLengthFromPosition(nums1, nums2, 0, k));
        }

        // Check diagonals starting from first column
        for (int k = 1; k < nums1.length; k++) {
            maxLength = Math.max(maxLength, findLengthFromPosition(nums1, nums2, k, 0));
        }

        return maxLength;
    }

    private int findLengthFromPosition(int[] nums1, int[] nums2, int i, int j) {
        int maxLength = 0;
        int currentLength = 0;

        while (i < nums1.length && j < nums2.length) {
            if (nums1[i] == nums2[j]) {
                currentLength++;
                maxLength = Math.max(maxLength, currentLength);
            } else {
                currentLength = 0;
            }
            i++;
            j++;
        }

        return maxLength;
    }

    public static void main(String[] args) {
        MaximumLengthOfRepeatedSubarray solution = new MaximumLengthOfRepeatedSubarray();

        System.out.println("=== Maximum Length of Repeated Subarray Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1_1 = { 1, 2, 3, 2, 1 };
        int[] nums2_1 = { 3, 2, 1, 4, 7 };
        System.out.println("Test 1 - nums1: " + Arrays.toString(nums1_1));
        System.out.println("nums2: " + Arrays.toString(nums2_1));
        System.out.println("Brute Force: " + solution.findLengthBruteForce(nums1_1, nums2_1));
        System.out.println("DP: " + solution.findLengthDP(nums1_1, nums2_1));
        System.out.println("Optimized: " + solution.findLengthOptimized(nums1_1, nums2_1));
        System.out.println("Rolling Hash: " + solution.findLengthRollingHash(nums1_1, nums2_1));
        System.out.println("Diagonal: " + solution.findLengthDiagonal(nums1_1, nums2_1));
        System.out.println("Expected: 3\n");

        // Test Case 2: No common subarray
        int[] nums1_2 = { 0, 0, 0, 0, 0 };
        int[] nums2_2 = { 1, 1, 1, 1, 1 };
        System.out.println("Test 2 - nums1: " + Arrays.toString(nums1_2));
        System.out.println("nums2: " + Arrays.toString(nums2_2));
        System.out.println("DP: " + solution.findLengthDP(nums1_2, nums2_2));
        System.out.println("Expected: 0\n");

        // Test Case 3: Identical arrays
        int[] nums1_3 = { 1, 2, 3 };
        int[] nums2_3 = { 1, 2, 3 };
        System.out.println("Test 3 - nums1: " + Arrays.toString(nums1_3));
        System.out.println("nums2: " + Arrays.toString(nums2_3));
        System.out.println("DP: " + solution.findLengthDP(nums1_3, nums2_3));
        System.out.println("Expected: 3\n");

        performanceTest();
    }

    private static void performanceTest() {
        MaximumLengthOfRepeatedSubarray solution = new MaximumLengthOfRepeatedSubarray();

        int[] largeNums1 = new int[1000];
        int[] largeNums2 = new int[1000];

        for (int i = 0; i < 1000; i++) {
            largeNums1[i] = (int) (Math.random() * 100);
            largeNums2[i] = (int) (Math.random() * 100);
        }

        System.out.println("=== Performance Test (Array size: 1000 each) ===");

        long start = System.nanoTime();
        int result1 = solution.findLengthDP(largeNums1, largeNums2);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.findLengthOptimized(largeNums1, largeNums2);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.findLengthDiagonal(largeNums1, largeNums2);
        end = System.nanoTime();
        System.out.println("Diagonal: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
