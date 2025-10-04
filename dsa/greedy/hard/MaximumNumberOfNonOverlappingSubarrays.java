package greedy.hard;

/**
 * LeetCode 1546: Maximum Number of Non-Overlapping Subarrays With Sum Equals
 * Target
 * https://leetcode.com/problems/maximum-number-of-non-overlapping-subarrays-with-sum-equals-target/
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: Medium (Asked in 40+ interviews)
 *
 * Description:
 * Given an array nums and an integer target, return the maximum number of
 * non-empty non-overlapping subarrays such that the sum of values in each
 * subarray is equal to target.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * - 0 <= target <= 10^6
 * 
 * Follow-up Questions:
 * 1. Can you solve it in one pass?
 * 2. What if we want minimum number of subarrays?
 * 3. How to handle negative numbers efficiently?
 */
public class MaximumNumberOfNonOverlappingSubarrays {

    // Approach 1: Greedy with HashSet - O(n) time, O(n) space
    public int maxNonOverlapping(int[] nums, int target) {
        java.util.Set<Integer> prefixSums = new java.util.HashSet<>();
        prefixSums.add(0); // For subarrays starting from index 0

        int count = 0;
        int currentSum = 0;

        for (int num : nums) {
            currentSum += num;

            // Check if there exists a prefix sum such that currentSum - prefixSum = target
            if (prefixSums.contains(currentSum - target)) {
                count++;
                prefixSums.clear(); // Reset for next non-overlapping subarray
                prefixSums.add(0);
                currentSum = 0;
            } else {
                prefixSums.add(currentSum);
            }
        }

        return count;
    }

    // Approach 2: Greedy with Early Reset - O(n) time, O(n) space
    public int maxNonOverlappingOptimized(int[] nums, int target) {
        java.util.Set<Integer> seen = new java.util.HashSet<>();
        seen.add(0);

        int count = 0;
        int sum = 0;

        for (int num : nums) {
            sum += num;

            if (seen.contains(sum - target)) {
                // Found a valid subarray
                count++;
                seen.clear();
                seen.add(0);
                sum = 0;
            } else {
                seen.add(sum);
            }
        }

        return count;
    }

    // Approach 3: Recursive with Memoization - O(n²) time, O(n²) space
    public int maxNonOverlappingRecursive(int[] nums, int target) {
        java.util.Map<String, Integer> memo = new java.util.HashMap<>();
        return helper(nums, target, 0, memo);
    }

    private int helper(int[] nums, int target, int start, java.util.Map<String, Integer> memo) {
        if (start >= nums.length)
            return 0;

        String key = start + "";
        if (memo.containsKey(key))
            return memo.get(key);

        int maxCount = 0;
        int sum = 0;

        // Try all possible subarrays starting from 'start'
        for (int end = start; end < nums.length; end++) {
            sum += nums[end];

            if (sum == target) {
                // Found valid subarray, try to find more from end+1
                maxCount = Math.max(maxCount, 1 + helper(nums, target, end + 1, memo));
            }
        }

        // Also try skipping current position
        maxCount = Math.max(maxCount, helper(nums, target, start + 1, memo));

        memo.put(key, maxCount);
        return maxCount;
    }

    // Follow-up: Find actual subarrays
    public java.util.List<int[]> findSubarrays(int[] nums, int target) {
        java.util.List<int[]> result = new java.util.ArrayList<>();
        java.util.Set<Integer> prefixSums = new java.util.HashSet<>();
        prefixSums.add(0);

        int currentSum = 0;
        int start = 0;

        for (int i = 0; i < nums.length; i++) {
            currentSum += nums[i];

            if (prefixSums.contains(currentSum - target)) {
                result.add(new int[] { start, i });
                prefixSums.clear();
                prefixSums.add(0);
                currentSum = 0;
                start = i + 1;
            } else {
                prefixSums.add(currentSum);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        MaximumNumberOfNonOverlappingSubarrays solution = new MaximumNumberOfNonOverlappingSubarrays();

        // Test Case 1: Basic example
        System.out.println("Basic: " + solution.maxNonOverlapping(new int[] { 1, 1, 1, 1, 1 }, 2)); // 2

        // Test Case 2: Mixed positive/negative
        System.out.println("Mixed: " + solution.maxNonOverlapping(new int[] { -1, 3, 5, 1, 4, 2, -9 }, 6)); // 2

        // Test Case 3: No valid subarray
        System.out.println("No valid: " + solution.maxNonOverlapping(new int[] { 1, 2, 3 }, 10)); // 0

        // Test Case 4: Single element match
        System.out.println("Single match: " + solution.maxNonOverlapping(new int[] { 10 }, 10)); // 1

        // Test Case 5: All elements equal target
        System.out.println("All match: " + solution.maxNonOverlapping(new int[] { 2, 2, 2, 2 }, 2)); // 4

        // Test Case 6: Target is 0
        System.out.println("Target 0: " + solution.maxNonOverlapping(new int[] { 0, 0, 0 }, 0)); // 3

        // Test Case 7: Negative numbers
        System.out.println("Negatives: " + solution.maxNonOverlapping(new int[] { -1, -1, -1 }, -1)); // 3

        // Test Case 8: Complex case
        System.out.println("Complex: " + solution.maxNonOverlapping(new int[] { -2, 6, 6, 3, 5, 4, 1, 2, 8 }, 10)); // 3

        // Test Case 9: Optimized approach comparison
        System.out.println("Optimized: " + solution.maxNonOverlappingOptimized(new int[] { 1, 1, 1, 1, 1 }, 2)); // 2

        // Test Case 10: Recursive approach
        System.out.println("Recursive: " + solution.maxNonOverlappingRecursive(new int[] { 1, 1, 1, 1, 1 }, 2)); // 2

        // Test Case 11: Find actual subarrays
        java.util.List<int[]> subarrays = solution.findSubarrays(new int[] { 1, 1, 1, 1, 1 }, 2);
        System.out.print("Subarrays: ");
        for (int[] subarray : subarrays) {
            System.out.print("[" + subarray[0] + "," + subarray[1] + "] ");
        }
        System.out.println();
    }
}
