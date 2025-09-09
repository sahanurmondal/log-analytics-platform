package searching.hard;

import java.util.*;

/**
 * LeetCode 410: Split Array Largest Sum
 * https://leetcode.com/problems/split-array-largest-sum/
 * 
 * Companies: Google, Facebook, Amazon
 * Frequency: High
 *
 * Description: Split array into m non-empty continuous subarrays to minimize
 * the largest sum.
 *
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - 0 <= nums[i] <= 10^6
 * - 1 <= m <= min(50, nums.length)
 * 
 * Follow-up Questions:
 * 1. Can you return the actual split points?
 * 2. What if we want to maximize the smallest sum?
 * 3. Can you handle weighted splits?
 */
public class SplitArrayLargestSum {

    // Approach 1: Binary search on answer - O(n * log(sum)) time, O(1) space
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

    private boolean canSplit(int[] nums, int m, long target) {
        int splits = 1;
        long currentSum = 0;

        for (int num : nums) {
            if (currentSum + num > target) {
                splits++;
                currentSum = num;
                if (splits > m)
                    return false;
            } else {
                currentSum += num;
            }
        }

        return true;
    }

    // Approach 2: Dynamic Programming - O(n^2 * m) time, O(n * m) space
    public int splitArrayDP(int[] nums, int m) {
        int n = nums.length;
        long[] prefixSum = new long[n + 1];

        for (int i = 0; i < n; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }

        long[][] dp = new long[m + 1][n + 1];
        for (int i = 0; i <= m; i++) {
            Arrays.fill(dp[i], Long.MAX_VALUE);
        }

        dp[0][0] = 0;

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
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

    // Follow-up 1: Return actual split points
    public List<Integer> getSplitPoints(int[] nums, int m) {
        List<Integer> splitPoints = new ArrayList<>();
        long target = splitArray(nums, m);

        int splits = 1;
        long currentSum = 0;

        for (int i = 0; i < nums.length; i++) {
            if (currentSum + nums[i] > target && splits < m) {
                splitPoints.add(i);
                splits++;
                currentSum = nums[i];
            } else {
                currentSum += nums[i];
            }
        }

        return splitPoints;
    }

    // Follow-up 2: Maximize the smallest sum
    public int maximizeSmallestSum(int[] nums, int m) {
        long left = 0, right = 0;

        for (int num : nums) {
            left = Math.max(left, num);
            right += num;
        }

        long result = 0;

        while (left <= right) {
            long mid = left + (right - left) / 2;
            if (canSplitWithMinSum(nums, m, mid)) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return (int) result;
    }

    private boolean canSplitWithMinSum(int[] nums, int m, long minSum) {
        int splits = 1;
        long currentSum = 0;

        for (int num : nums) {
            currentSum += num;
            if (currentSum >= minSum) {
                if (splits == m)
                    return true;
                splits++;
                currentSum = 0;
            }
        }

        return false;
    }

    // Follow-up 3: Weighted splits
    public int splitArrayWeighted(int[] nums, int m, double[] weights) {
        long left = 0, right = 0;

        for (int num : nums) {
            left = Math.max(left, num);
            right += num;
        }

        while (left < right) {
            long mid = left + (right - left) / 2;
            if (canSplitWeighted(nums, m, mid, weights)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return (int) left;
    }

    private boolean canSplitWeighted(int[] nums, int m, long target, double[] weights) {
        int splits = 1;
        double currentSum = 0;

        for (int i = 0; i < nums.length; i++) {
            double weightedValue = nums[i] * weights[i];
            if (currentSum + weightedValue > target) {
                splits++;
                currentSum = weightedValue;
                if (splits > m)
                    return false;
            } else {
                currentSum += weightedValue;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        SplitArrayLargestSum solution = new SplitArrayLargestSum();

        // Test case 1: Basic case
        int[] nums1 = { 7, 2, 5, 10, 8 };
        System.out.println("Test 1 - Basic case (m=2):");
        System.out.println("Expected: 18, Got: " + solution.splitArray(nums1, 2));
        System.out.println("DP approach: " + solution.splitArrayDP(nums1, 2));

        // Test case 2: Single element per subarray
        int[] nums2 = { 1, 2, 3, 4, 5 };
        System.out.println("\nTest 2 - Single elements (m=5):");
        System.out.println("Expected: 5, Got: " + solution.splitArray(nums2, 5));

        // Test case 3: All in one subarray
        System.out.println("\nTest 3 - All in one (m=1):");
        System.out.println("Expected: 15, Got: " + solution.splitArray(nums2, 1));

        // Test case 4: Large numbers
        int[] nums3 = { 1000000, 1000000 };
        System.out.println("\nTest 4 - Large numbers (m=2):");
        System.out.println("Expected: 1000000, Got: " + solution.splitArray(nums3, 2));

        // Test case 5: Equal splits
        int[] nums4 = { 1, 4, 4 };
        System.out.println("\nTest 5 - Equal splits (m=3):");
        System.out.println("Expected: 4, Got: " + solution.splitArray(nums4, 3));

        // Edge case: Single element
        int[] nums5 = { 10 };
        System.out.println("\nEdge case - Single element (m=1):");
        System.out.println("Expected: 10, Got: " + solution.splitArray(nums5, 1));

        // Follow-up 1: Split points
        System.out.println("\nFollow-up 1 - Split points:");
        List<Integer> points = solution.getSplitPoints(nums1, 2);
        System.out.println("Split at indices: " + points);

        // Follow-up 2: Maximize smallest sum
        System.out.println("\nFollow-up 2 - Maximize smallest sum:");
        System.out.println("Max smallest sum: " + solution.maximizeSmallestSum(nums1, 2));

        // Follow-up 3: Weighted splits
        double[] weights = { 1.0, 2.0, 1.5, 0.5, 1.2 };
        System.out.println("\nFollow-up 3 - Weighted splits:");
        System.out.println("Weighted result: " + solution.splitArrayWeighted(nums1, 2, weights));

        // Performance test
        int[] largeNums = new int[1000];
        for (int i = 0; i < largeNums.length; i++) {
            largeNums[i] = i + 1;
        }
        long startTime = System.currentTimeMillis();
        solution.splitArray(largeNums, 50);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (1000 elements, m=50): " + (endTime - startTime) + "ms");

        // Verification: Show split details
        System.out.println("\nVerification for [7,2,5,10,8] with m=2:");
        System.out.println("Optimal splits: [7,2,5] (sum=14) and [10,8] (sum=18)");
        System.out.println("Largest sum: 18");
    }
}
