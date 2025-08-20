package trees.hard;

import java.util.*;

/**
 * LeetCode 689: Maximum Sum of 3 Non-Overlapping Subarrays
 * https://leetcode.com/problems/maximum-sum-of-3-non-overlapping-subarrays/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Hard
 *
 * Description: Given an integer array nums and an integer k, find three
 * non-overlapping subarrays of length k with maximum sum.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - 1 <= nums[i] <= 10^6
 * - 1 <= k <= floor(nums.length / 3)
 * 
 * Follow-up Questions:
 * 1. Can you generalize to m non-overlapping subarrays?
 * 2. Can you find all possible combinations with max sum?
 * 3. Can you optimize for different subarray lengths?
 */
public class MaximumSumOfThreeNonOverlappingSubtrees {

    // Approach 1: Dynamic Programming with prefix tracking
    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int n = nums.length;
        int[] sums = new int[n - k + 1];

        // Calculate all subarray sums of length k
        int sum = 0;
        for (int i = 0; i < k; i++)
            sum += nums[i];
        sums[0] = sum;

        for (int i = k; i < n; i++) {
            sum += nums[i] - nums[i - k];
            sums[i - k + 1] = sum;
        }

        // Find best left subarray ending at or before each position
        int[] left = new int[sums.length];
        int best = 0;
        for (int i = 0; i < sums.length; i++) {
            if (sums[i] > sums[best])
                best = i;
            left[i] = best;
        }

        // Find best right subarray starting at or after each position
        int[] right = new int[sums.length];
        best = sums.length - 1;
        for (int i = sums.length - 1; i >= 0; i--) {
            if (sums[i] >= sums[best])
                best = i;
            right[i] = best;
        }

        // Find the best middle subarray
        int[] result = new int[3];
        int maxSum = 0;
        for (int i = k; i <= sums.length - k - 1; i++) {
            int l = left[i - k], r = right[i + k];
            int totalSum = sums[l] + sums[i] + sums[r];
            if (totalSum > maxSum) {
                maxSum = totalSum;
                result[0] = l;
                result[1] = i;
                result[2] = r;
            }
        }

        return result;
    }

    // Follow-up 1: Generalize to m non-overlapping subarrays
    public int[] maxSumOfMSubarrays(int[] nums, int k, int m) {
        int n = nums.length;
        int[] sums = calculateSubarraySums(nums, k);

        // dp[i][j] = max sum using j subarrays from first i positions
        int[][] dp = new int[sums.length + 1][m + 1];
        int[][] indices = new int[sums.length + 1][m + 1];

        for (int i = k; i <= sums.length; i++) {
            for (int j = 1; j <= Math.min(i / k, m); j++) {
                // Don't take current subarray
                if (dp[i - 1][j] > dp[i - k][j - 1] + sums[i - 1]) {
                    dp[i][j] = dp[i - 1][j];
                    indices[i][j] = indices[i - 1][j];
                } else {
                    // Take current subarray
                    dp[i][j] = dp[i - k][j - 1] + sums[i - 1];
                    indices[i][j] = i - 1;
                }
            }
        }

        // Reconstruct result
        int[] result = new int[m];
        int pos = sums.length, count = m;
        while (count > 0) {
            int idx = indices[pos][count];
            result[count - 1] = idx;
            pos = idx;
            count--;
        }

        return result;
    }

    // Follow-up 2: Find all combinations with maximum sum
    public List<int[]> findAllMaxCombinations(int[] nums, int k) {
        List<int[]> result = new ArrayList<>();
        int[] maxResult = maxSumOfThreeSubarrays(nums, k);
        int maxSum = calculateTotalSum(nums, k, maxResult);

        findAllCombinations(nums, k, 0, new ArrayList<>(), result, maxSum);
        return result;
    }

    private void findAllCombinations(int[] nums, int k, int start, List<Integer> current,
            List<int[]> result, int targetSum) {
        if (current.size() == 3) {
            int sum = 0;
            for (int idx : current) {
                for (int i = 0; i < k; i++) {
                    sum += nums[idx + i];
                }
            }
            if (sum == targetSum) {
                result.add(current.stream().mapToInt(i -> i).toArray());
            }
            return;
        }

        for (int i = start; i <= nums.length - k - (2 - current.size()) * k; i++) {
            current.add(i);
            findAllCombinations(nums, k, i + k, current, result, targetSum);
            current.remove(current.size() - 1);
        }
    }

    // Follow-up 3: Different subarray lengths
    public int[] maxSumDifferentLengths(int[] nums, int[] lengths) {
        // For simplicity, assume lengths array has 3 elements
        int k1 = lengths[0], k2 = lengths[1], k3 = lengths[2];

        int[] sums1 = calculateSubarraySums(nums, k1);
        int[] sums2 = calculateSubarraySums(nums, k2);
        int[] sums3 = calculateSubarraySums(nums, k3);

        int maxSum = 0;
        int[] result = new int[3];

        for (int i = 0; i < sums1.length; i++) {
            for (int j = i + k1; j < sums2.length; j++) {
                for (int l = j + k2; l < sums3.length; l++) {
                    int totalSum = sums1[i] + sums2[j] + sums3[l];
                    if (totalSum > maxSum) {
                        maxSum = totalSum;
                        result[0] = i;
                        result[1] = j;
                        result[2] = l;
                    }
                }
            }
        }

        return result;
    }

    // Helper methods
    private int[] calculateSubarraySums(int[] nums, int k) {
        int n = nums.length;
        int[] sums = new int[n - k + 1];

        int sum = 0;
        for (int i = 0; i < k; i++)
            sum += nums[i];
        sums[0] = sum;

        for (int i = k; i < n; i++) {
            sum += nums[i] - nums[i - k];
            sums[i - k + 1] = sum;
        }

        return sums;
    }

    private int calculateTotalSum(int[] nums, int k, int[] indices) {
        int sum = 0;
        for (int idx : indices) {
            for (int i = 0; i < k; i++) {
                sum += nums[idx + i];
            }
        }
        return sum;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MaximumSumOfThreeNonOverlappingSubtrees solution = new MaximumSumOfThreeNonOverlappingSubtrees();

        // Test case 1: Basic case
        int[] nums1 = { 1, 2, 1, 2, 6, 7, 5, 1 };
        int k1 = 2;
        System.out.println("Test 1 - nums: " + Arrays.toString(nums1) + ", k: " + k1);
        int[] result1 = solution.maxSumOfThreeSubarrays(nums1, k1);
        System.out.println("Result: " + Arrays.toString(result1));

        // Test case 2: Generalize to m subarrays
        System.out.println("\nTest 2 - 4 non-overlapping subarrays:");
        int[] result2 = solution.maxSumOfMSubarrays(nums1, 1, 4);
        System.out.println("Result: " + Arrays.toString(result2));

        // Test case 3: All max combinations
        System.out.println("\nTest 3 - All max combinations:");
        List<int[]> allMax = solution.findAllMaxCombinations(nums1, k1);
        for (int[] combo : allMax) {
            System.out.println(Arrays.toString(combo));
        }

        // Test case 4: Different lengths
        int[] lengths = { 1, 2, 1 };
        System.out.println("\nTest 4 - Different lengths " + Arrays.toString(lengths) + ":");
        int[] result4 = solution.maxSumDifferentLengths(nums1, lengths);
        System.out.println("Result: " + Arrays.toString(result4));

        // Edge cases
        System.out.println("\nEdge cases:");

        // Minimum valid input
        int[] minNums = { 1, 2, 3, 4, 5, 6 };
        int[] minResult = solution.maxSumOfThreeSubarrays(minNums, 1);
        System.out.println("Minimum case: " + Arrays.toString(minResult));

        // All same values
        int[] sameNums = { 5, 5, 5, 5, 5, 5, 5, 5 };
        int[] sameResult = solution.maxSumOfThreeSubarrays(sameNums, 2);
        System.out.println("All same values: " + Arrays.toString(sameResult));

        // Stress test
        System.out.println("\nStress test:");
        int[] largeNums = new int[2000];
        Random rand = new Random(42);
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = rand.nextInt(1000) + 1;
        }

        long start = System.nanoTime();
        int[] largeResult = solution.maxSumOfThreeSubarrays(largeNums, 50);
        long end = System.nanoTime();
        System.out.println("Large array result: " + Arrays.toString(largeResult));
        System.out.println("Time: " + (end - start) / 1_000_000 + " ms");
    }
}
