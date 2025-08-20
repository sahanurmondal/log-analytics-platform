package dp.medium;

/**
 * LeetCode 918: Maximum Sum Circular Subarray
 * https://leetcode.com/problems/maximum-sum-circular-subarray/
 *
 * Description:
 * Given a circular array, find the maximum possible sum of a non-empty
 * subarray.
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - -3 * 10^4 <= nums[i] <= 3 * 10^4
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Company Tags: Google, Amazon, Microsoft
 * Difficulty: Medium
 */
public class MaximumSumCircularSubarray {

    // Approach 1: Kadane's Algorithm + Circular - O(n) time, O(1) space
    public int maxSubarraySumCircular(int[] nums) {
        int totalSum = 0;
        int maxKadane = Integer.MIN_VALUE;
        int minKadane = Integer.MAX_VALUE;
        int currentMax = 0;
        int currentMin = 0;

        for (int num : nums) {
            totalSum += num;

            // Maximum subarray (standard Kadane's)
            currentMax = Math.max(currentMax + num, num);
            maxKadane = Math.max(maxKadane, currentMax);

            // Minimum subarray (modified Kadane's)
            currentMin = Math.min(currentMin + num, num);
            minKadane = Math.min(minKadane, currentMin);
        }

        // If all elements are negative, return maxKadane
        if (totalSum == minKadane) {
            return maxKadane;
        }

        // Return max of normal subarray or circular subarray
        return Math.max(maxKadane, totalSum - minKadane);
    }

    // Approach 2: Two Pass Kadane's - O(n) time, O(1) space
    public int maxSubarraySumCircularTwoPass(int[] nums) {
        int n = nums.length;

        // Case 1: Maximum subarray doesn't wrap around
        int maxNormal = kadaneMax(nums);

        // Case 2: Maximum subarray wraps around
        int totalSum = 0;
        for (int num : nums) {
            totalSum += num;
        }

        // Invert array for finding minimum subarray
        for (int i = 0; i < n; i++) {
            nums[i] = -nums[i];
        }

        int maxInverted = kadaneMax(nums);

        // Restore original array
        for (int i = 0; i < n; i++) {
            nums[i] = -nums[i];
        }

        int maxCircular = totalSum + maxInverted;

        // If all elements are negative
        if (maxCircular == 0) {
            return maxNormal;
        }

        return Math.max(maxNormal, maxCircular);
    }

    private int kadaneMax(int[] nums) {
        int maxSoFar = nums[0];
        int maxEndingHere = nums[0];

        for (int i = 1; i < nums.length; i++) {
            maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }

        return maxSoFar;
    }

    // Approach 3: Prefix/Suffix Maximum - O(n) time, O(n) space
    public int maxSubarraySumCircularPrefixSuffix(int[] nums) {
        int n = nums.length;

        // Case 1: Normal maximum subarray
        int maxNormal = kadaneMax(nums);

        // Case 2: Circular maximum subarray
        int[] prefixSum = new int[n];
        prefixSum[0] = nums[0];

        for (int i = 1; i < n; i++) {
            prefixSum[i] = prefixSum[i - 1] + nums[i];
        }

        int[] maxPrefix = new int[n];
        maxPrefix[0] = prefixSum[0];

        for (int i = 1; i < n; i++) {
            maxPrefix[i] = Math.max(maxPrefix[i - 1], prefixSum[i]);
        }

        int maxCircular = Integer.MIN_VALUE;
        int suffixSum = 0;

        for (int i = n - 1; i >= 1; i--) {
            suffixSum += nums[i];
            maxCircular = Math.max(maxCircular, suffixSum + maxPrefix[i - 1]);
        }

        return Math.max(maxNormal, maxCircular);
    }

    public static void main(String[] args) {
        MaximumSumCircularSubarray solution = new MaximumSumCircularSubarray();

        System.out.println("=== Maximum Sum Circular Subarray Test Cases ===");

        // Test Case 1: Normal case
        int[] nums1 = { 1, -2, 3, -2 };
        System.out.println("Test 1 - Array: " + java.util.Arrays.toString(nums1));
        System.out.println("Kadane + Circular: " + solution.maxSubarraySumCircular(nums1));
        System.out.println("Two Pass: " + solution.maxSubarraySumCircularTwoPass(nums1));
        System.out.println("Prefix/Suffix: " + solution.maxSubarraySumCircularPrefixSuffix(nums1));
        System.out.println("Expected: 3\n");

        // Test Case 2: All negatives
        int[] nums2 = { -3, -2, -3 };
        System.out.println("Test 2 - Array: " + java.util.Arrays.toString(nums2));
        System.out.println("Kadane + Circular: " + solution.maxSubarraySumCircular(nums2));
        System.out.println("Expected: -2\n");

        // Test Case 3: All positives
        int[] nums3 = { 3, 2, 3 };
        System.out.println("Test 3 - Array: " + java.util.Arrays.toString(nums3));
        System.out.println("Kadane + Circular: " + solution.maxSubarraySumCircular(nums3));
        System.out.println("Expected: 8\n");

        // Test Case 4: Single element
        int[] nums4 = { 5 };
        System.out.println("Test 4 - Array: " + java.util.Arrays.toString(nums4));
        System.out.println("Kadane + Circular: " + solution.maxSubarraySumCircular(nums4));
        System.out.println("Expected: 5\n");

        performanceTest();
    }

    private static void performanceTest() {
        MaximumSumCircularSubarray solution = new MaximumSumCircularSubarray();

        int[] largeArray = new int[30000];
        for (int i = 0; i < 30000; i++) {
            largeArray[i] = (int) (Math.random() * 200) - 100;
        }

        System.out.println("=== Performance Test (Array length: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result = solution.maxSubarraySumCircular(largeArray);
        long end = System.nanoTime();
        System.out.println("Kadane + Circular: " + result + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
