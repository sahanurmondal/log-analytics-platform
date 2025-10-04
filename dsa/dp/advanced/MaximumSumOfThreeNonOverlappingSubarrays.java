package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 689: Maximum Sum of 3 Non-Overlapping Subarrays
 * https://leetcode.com/problems/maximum-sum-of-3-non-overlapping-subarrays/
 *
 * Description:
 * Given an integer array nums and an integer k, find three non-overlapping
 * subarrays of length k
 * with maximum sum and return them. Return the result as a list of indices
 * representing the starting position
 * of each interval (0-indexed). If there are multiple answers, return the
 * lexicographically smallest one.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - 1 <= nums[i] <= 10^6
 * - 1 <= k <= floor(nums.length / 3)
 *
 * Follow-up:
 * - What if we need to find m non-overlapping subarrays?
 * - Can you solve it in O(n) time?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard (categorized as Medium for this collection)
 */
public class MaximumSumOfThreeNonOverlappingSubarrays {

    // Approach 1: Sliding Window + DP - O(n) time, O(n) space
    public int[] maxSumOfThreeSubarrays(int[] nums, int k) {
        int n = nums.length;

        // Calculate sum of all k-length subarrays
        int[] sums = new int[n - k + 1];
        int windowSum = 0;

        // Initial window
        for (int i = 0; i < k; i++) {
            windowSum += nums[i];
        }
        sums[0] = windowSum;

        // Sliding window
        for (int i = 1; i < sums.length; i++) {
            windowSum = windowSum - nums[i - 1] + nums[i + k - 1];
            sums[i] = windowSum;
        }

        // Find best left subarray for each position
        int[] left = new int[sums.length];
        int bestLeft = 0;
        for (int i = 0; i < sums.length; i++) {
            if (sums[i] > sums[bestLeft]) {
                bestLeft = i;
            }
            left[i] = bestLeft;
        }

        // Find best right subarray for each position
        int[] right = new int[sums.length];
        int bestRight = sums.length - 1;
        for (int i = sums.length - 1; i >= 0; i--) {
            if (sums[i] >= sums[bestRight]) {
                bestRight = i;
            }
            right[i] = bestRight;
        }

        // Find the best middle subarray
        int maxSum = 0;
        int[] result = new int[3];

        for (int mid = k; mid < sums.length - k; mid++) {
            int leftIdx = left[mid - k];
            int rightIdx = right[mid + k];
            int totalSum = sums[leftIdx] + sums[mid] + sums[rightIdx];

            if (totalSum > maxSum) {
                maxSum = totalSum;
                result[0] = leftIdx;
                result[1] = mid;
                result[2] = rightIdx;
            }
        }

        return result;
    }

    // Approach 2: 2D DP - O(n) time, O(n) space
    public int[] maxSumOfThreeSubarraysDP(int[] nums, int k) {
        int n = nums.length;

        // Calculate k-length subarray sums
        int[] sums = new int[n - k + 1];
        int sum = 0;
        for (int i = 0; i < k; i++) {
            sum += nums[i];
        }
        sums[0] = sum;

        for (int i = 1; i < sums.length; i++) {
            sum = sum - nums[i - 1] + nums[i + k - 1];
            sums[i] = sum;
        }

        // DP arrays
        int[][] dp = new int[4][sums.length + 1]; // dp[i][j] = max sum using i subarrays ending at or before j
        int[][] pos = new int[4][sums.length + 1]; // position of last subarray

        for (int i = 1; i <= 3; i++) {
            for (int j = k * i - 1; j < sums.length; j++) {
                // Option 1: Don't use current subarray
                dp[i][j + 1] = dp[i][j];
                pos[i][j + 1] = pos[i][j];

                // Option 2: Use current subarray
                int prevSum = (i == 1) ? 0 : dp[i - 1][j - k + 1];
                int currentSum = prevSum + sums[j];

                if (currentSum > dp[i][j + 1]) {
                    dp[i][j + 1] = currentSum;
                    pos[i][j + 1] = j;
                }
            }
        }

        // Reconstruct solution
        int[] result = new int[3];
        int currPos = pos[3][sums.length];
        result[2] = currPos;

        currPos = pos[2][currPos - k + 1];
        result[1] = currPos;

        currPos = pos[1][currPos - k + 1];
        result[0] = currPos;

        return result;
    }

    // Approach 3: Greedy with Backtracking - O(n^3) time, O(1) space
    public int[] maxSumOfThreeSubarraysGreedy(int[] nums, int k) {
        int n = nums.length;
        int maxSum = 0;
        int[] result = new int[3];

        // Try all possible combinations
        for (int i = 0; i <= n - 3 * k; i++) {
            for (int j = i + k; j <= n - 2 * k; j++) {
                for (int l = j + k; l <= n - k; l++) {
                    int sum1 = getSubarraySum(nums, i, k);
                    int sum2 = getSubarraySum(nums, j, k);
                    int sum3 = getSubarraySum(nums, l, k);
                    int totalSum = sum1 + sum2 + sum3;

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

    private int getSubarraySum(int[] nums, int start, int k) {
        int sum = 0;
        for (int i = start; i < start + k; i++) {
            sum += nums[i];
        }
        return sum;
    }

    // Approach 4: Optimized Sliding Window - O(n) time, O(1) space
    public int[] maxSumOfThreeSubarraysOptimized(int[] nums, int k) {
        int n = nums.length;

        // Calculate initial sums
        int sum1 = 0, sum2 = 0, sum3 = 0;
        for (int i = 0; i < k; i++) {
            sum1 += nums[i];
        }
        for (int i = k; i < 2 * k; i++) {
            sum2 += nums[i];
        }
        for (int i = 2 * k; i < 3 * k; i++) {
            sum3 += nums[i];
        }

        int maxSum1 = sum1, maxSum12 = sum1 + sum2, maxSum123 = sum1 + sum2 + sum3;
        int idx1 = 0, idx12_1 = 0, idx12_2 = k;
        int[] result = { 0, k, 2 * k };

        for (int i = 1; i <= n - 3 * k; i++) {
            // Update sums
            sum1 = sum1 - nums[i - 1] + nums[i + k - 1];
            sum2 = sum2 - nums[i + k - 1] + nums[i + 2 * k - 1];
            sum3 = sum3 - nums[i + 2 * k - 1] + nums[i + 3 * k - 1];

            // Update best single subarray
            if (sum1 > maxSum1) {
                maxSum1 = sum1;
                idx1 = i;
            }

            // Update best two subarrays
            if (maxSum1 + sum2 > maxSum12) {
                maxSum12 = maxSum1 + sum2;
                idx12_1 = idx1;
                idx12_2 = i + k;
            }

            // Update best three subarrays
            if (maxSum12 + sum3 > maxSum123) {
                maxSum123 = maxSum12 + sum3;
                result[0] = idx12_1;
                result[1] = idx12_2;
                result[2] = i + 2 * k;
            }
        }

        return result;
    }

    // Approach 5: Get Maximum Sum Value - O(n) time, O(n) space
    public int getMaxSumOfThreeSubarrays(int[] nums, int k) {
        int[] indices = maxSumOfThreeSubarrays(nums, k);

        int sum = 0;
        for (int idx : indices) {
            for (int i = idx; i < idx + k; i++) {
                sum += nums[i];
            }
        }

        return sum;
    }

    public static void main(String[] args) {
        MaximumSumOfThreeNonOverlappingSubarrays solution = new MaximumSumOfThreeNonOverlappingSubarrays();

        System.out.println("=== Maximum Sum of Three Non-Overlapping Subarrays Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 1, 2, 1, 2, 6, 7, 5, 1 };
        int k1 = 2;
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1) + ", k: " + k1);
        System.out.println("Sliding Window: " + Arrays.toString(solution.maxSumOfThreeSubarrays(nums1, k1)));
        System.out.println("DP: " + Arrays.toString(solution.maxSumOfThreeSubarraysDP(nums1, k1)));
        System.out.println("Greedy: " + Arrays.toString(solution.maxSumOfThreeSubarraysGreedy(nums1, k1)));
        System.out.println("Optimized: " + Arrays.toString(solution.maxSumOfThreeSubarraysOptimized(nums1, k1)));
        System.out.println("Max Sum: " + solution.getMaxSumOfThreeSubarrays(nums1, k1));
        System.out.println("Expected: [0, 3, 5], Sum: 23\n");

        // Test Case 2: Another example
        int[] nums2 = { 1, 2, 1, 2, 1, 2, 1, 2, 1 };
        int k2 = 2;
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2) + ", k: " + k2);
        System.out.println("Sliding Window: " + Arrays.toString(solution.maxSumOfThreeSubarrays(nums2, k2)));
        System.out.println("Expected: [0, 2, 4]\n");

        // Test Case 3: Larger k
        int[] nums3 = { 4, 3, 2, 1, 5, 6, 7, 8, 9 };
        int k3 = 3;
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3) + ", k: " + k3);
        System.out.println("Sliding Window: " + Arrays.toString(solution.maxSumOfThreeSubarrays(nums3, k3)));
        System.out.println("Max Sum: " + solution.getMaxSumOfThreeSubarrays(nums3, k3));
        System.out.println("Expected: [0, 3, 6]\n");

        performanceTest();
    }

    private static void performanceTest() {
        MaximumSumOfThreeNonOverlappingSubarrays solution = new MaximumSumOfThreeNonOverlappingSubarrays();

        int[] largeArray = new int[20000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000000) + 1;
        }
        int k = 100;

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ", k: " + k + ") ===");

        long start = System.nanoTime();
        int[] result1 = solution.maxSumOfThreeSubarrays(largeArray, k);
        long end = System.nanoTime();
        System.out.println(
                "Sliding Window: " + Arrays.toString(result1) + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int[] result2 = solution.maxSumOfThreeSubarraysDP(largeArray, k);
        end = System.nanoTime();
        System.out.println("DP: " + Arrays.toString(result2) + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int[] result3 = solution.maxSumOfThreeSubarraysOptimized(largeArray, k);
        end = System.nanoTime();
        System.out
                .println("Optimized: " + Arrays.toString(result3) + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
