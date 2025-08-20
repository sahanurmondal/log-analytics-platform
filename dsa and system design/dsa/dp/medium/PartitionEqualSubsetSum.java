package dp.medium;

import java.util.*;

/**
 * LeetCode 416: Partition Equal Subset Sum
 * https://leetcode.com/problems/partition-equal-subset-sum/
 *
 * Description:
 * Given a non-empty array nums containing only positive integers, find if the
 * array can be partitioned
 * into two subsets such that the sum of elements in both subsets is equal.
 *
 * Constraints:
 * - 1 <= nums.length <= 200
 * - 1 <= nums[i] <= 100
 *
 * Follow-up:
 * - Can you solve it in O(sum) space?
 * - What if we need to find the actual partition?
 *
 * Company Tags: Google, Amazon, Microsoft, Facebook, Apple
 * Difficulty: Medium
 */
public class PartitionEqualSubsetSum {

    // Approach 1: 2D DP - O(n * sum) time, O(n * sum) space
    public boolean canPartition(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0)
            return false;

        int target = sum / 2;
        int n = nums.length;
        boolean[][] dp = new boolean[n + 1][target + 1];

        // Base case: sum 0 can always be achieved
        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= target; j++) {
                // Don't take current number
                dp[i][j] = dp[i - 1][j];

                // Take current number if possible
                if (j >= nums[i - 1]) {
                    dp[i][j] = dp[i][j] || dp[i - 1][j - nums[i - 1]];
                }
            }
        }

        return dp[n][target];
    }

    // Approach 2: 1D DP - O(n * sum) time, O(sum) space
    public boolean canPartitionOptimized(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0)
            return false;

        int target = sum / 2;
        boolean[] dp = new boolean[target + 1];
        dp[0] = true;

        for (int num : nums) {
            // Process from right to left to avoid using updated values
            for (int j = target; j >= num; j--) {
                dp[j] = dp[j] || dp[j - num];
            }
        }

        return dp[target];
    }

    // Approach 3: BitSet Optimization - O(n * sum) time, O(sum) space
    public boolean canPartitionBitSet(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0)
            return false;

        int target = sum / 2;
        BitSet dp = new BitSet(target + 1);
        dp.set(0);

        for (int num : nums) {
            BitSet temp = new BitSet(target + 1);
            for (int i = dp.nextSetBit(0); i >= 0; i = dp.nextSetBit(i + 1)) {
                if (i + num <= target) {
                    temp.set(i + num);
                }
            }
            dp.or(temp);
        }

        return dp.get(target);
    }

    // Approach 4: Memoization - O(n * sum) time, O(n * sum) space
    public boolean canPartitionMemo(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0)
            return false;

        int target = sum / 2;
        Boolean[][] memo = new Boolean[nums.length][target + 1];
        return canPartitionMemoHelper(nums, 0, target, memo);
    }

    private boolean canPartitionMemoHelper(int[] nums, int index, int target, Boolean[][] memo) {
        if (target == 0)
            return true;
        if (index >= nums.length || target < 0)
            return false;

        if (memo[index][target] != null)
            return memo[index][target];

        boolean result = canPartitionMemoHelper(nums, index + 1, target, memo) ||
                canPartitionMemoHelper(nums, index + 1, target - nums[index], memo);

        memo[index][target] = result;
        return result;
    }

    // Approach 5: Get Actual Partition - O(n * sum) time, O(n * sum) space
    public List<List<Integer>> getPartition(int[] nums) {
        int sum = Arrays.stream(nums).sum();
        if (sum % 2 != 0)
            return new ArrayList<>();

        int target = sum / 2;
        int n = nums.length;
        boolean[][] dp = new boolean[n + 1][target + 1];

        for (int i = 0; i <= n; i++) {
            dp[i][0] = true;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= target; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= nums[i - 1]) {
                    dp[i][j] = dp[i][j] || dp[i - 1][j - nums[i - 1]];
                }
            }
        }

        if (!dp[n][target])
            return new ArrayList<>();

        // Reconstruct partition
        List<Integer> subset1 = new ArrayList<>();
        List<Integer> subset2 = new ArrayList<>();

        int i = n, j = target;
        while (i > 0 && j > 0) {
            if (!dp[i - 1][j]) {
                // Must have taken nums[i-1]
                subset1.add(nums[i - 1]);
                j -= nums[i - 1];
            } else {
                subset2.add(nums[i - 1]);
            }
            i--;
        }

        // Add remaining elements to subset2
        while (i > 0) {
            subset2.add(nums[i - 1]);
            i--;
        }

        return Arrays.asList(subset1, subset2);
    }

    public static void main(String[] args) {
        PartitionEqualSubsetSum solution = new PartitionEqualSubsetSum();

        System.out.println("=== Partition Equal Subset Sum Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 1, 5, 11, 5 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("2D DP: " + solution.canPartition(nums1));
        System.out.println("1D DP: " + solution.canPartitionOptimized(nums1));
        System.out.println("BitSet: " + solution.canPartitionBitSet(nums1));
        System.out.println("Memoization: " + solution.canPartitionMemo(nums1));

        List<List<Integer>> partition1 = solution.getPartition(nums1);
        if (!partition1.isEmpty()) {
            System.out.println("Partition: " + partition1.get(0) + " and " + partition1.get(1));
        }
        System.out.println("Expected: true\n");

        // Test Case 2: Cannot partition
        int[] nums2 = { 1, 2, 3, 5 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("1D DP: " + solution.canPartitionOptimized(nums2));
        System.out.println("Expected: false\n");

        // Test Case 3: Single element
        int[] nums3 = { 1 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("1D DP: " + solution.canPartitionOptimized(nums3));
        System.out.println("Expected: false\n");

        performanceTest();
    }

    private static void performanceTest() {
        PartitionEqualSubsetSum solution = new PartitionEqualSubsetSum();

        int[] largeArray = new int[100];
        int sum = 0;
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 50) + 1;
            sum += largeArray[i];
        }

        // Ensure even sum for better test
        if (sum % 2 != 0)
            largeArray[0]++;

        System.out.println("=== Performance Test (Array length: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        boolean result1 = solution.canPartition(largeArray);
        long end = System.nanoTime();
        System.out.println("2D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result2 = solution.canPartitionOptimized(largeArray);
        end = System.nanoTime();
        System.out.println("1D DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        boolean result3 = solution.canPartitionBitSet(largeArray);
        end = System.nanoTime();
        System.out.println("BitSet: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
