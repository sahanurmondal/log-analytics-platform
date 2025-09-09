package slidingwindow.medium;

/**
 * LeetCode 1004: Max Consecutive Ones III
 * https://leetcode.com/problems/max-consecutive-ones-iii/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given a binary array nums and integer k, return the maximum
 * number of consecutive 1's in the array if you can flip at most k 0's.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 0 <= nums[i] <= 1
 * - 0 <= k <= nums.length
 *
 * Follow-up Questions:
 * 1. What if you can flip at most k elements of any value?
 * 2. How to return the actual subarray?
 * 3. How to solve for very large arrays efficiently?
 */
public class MaxConsecutiveOnesIII {
    // Approach 1: Sliding Window - O(n) time, O(1) space
    public int longestOnes(int[] nums, int k) {
        int left = 0, zeroCount = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0)
                zeroCount++;
            while (zeroCount > k) {
                if (nums[left++] == 0)
                    zeroCount--;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Approach 2: Prefix Sum - O(n) time, O(n) space
    public int longestOnesPrefixSum(int[] nums, int k) {
        int n = nums.length, left = 0, maxLen = 0, zeroCount = 0;
        for (int right = 0; right < n; right++) {
            if (nums[right] == 0)
                zeroCount++;
            while (zeroCount > k) {
                if (nums[left++] == 0)
                    zeroCount--;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Follow-up 1: Flip at most k elements of any value
    public int longestOnesAnyValue(int[] nums, int k) {
        int left = 0, flipCount = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] != 1)
                flipCount++;
            while (flipCount > k) {
                if (nums[left++] != 1)
                    flipCount--;
            }
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        MaxConsecutiveOnesIII sol = new MaxConsecutiveOnesIII();
        // Test 1: Basic
        System.out
                .println("Test 1: Expected 6 -> " + sol.longestOnes(new int[] { 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0 }, 2));
        // Test 2: All ones
        System.out
                .println("Test 2: Expected 11 -> " + sol.longestOnes(new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }, 2));
        // Test 3: All zeros
        System.out
                .println("Test 3: Expected 2 -> " + sol.longestOnes(new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 }, 2));
        // Test 4: Prefix sum approach
        System.out.println(
                "Test 4: Expected 6 -> " + sol.longestOnesPrefixSum(new int[] { 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0 }, 2));
        // Test 5: Flip any value
        System.out.println(
                "Test 5: Expected 6 -> " + sol.longestOnesAnyValue(new int[] { 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0 }, 2));
        // Test 6: Edge case, single element
        System.out.println("Test 6: Expected 1 -> " + sol.longestOnes(new int[] { 1 }, 1));
        // Test 7: Edge case, empty array
        System.out.println("Test 7: Expected 0 -> " + sol.longestOnes(new int[] {}, 2));
        // Test 8: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = i % 2;
        System.out.println("Test 8: Large input -> " + sol.longestOnes(large, 100));
        // Test 9: Alternating ones and zeros
        System.out.println("Test 9: Expected 3 -> " + sol.longestOnes(new int[] { 1, 0, 1, 0, 1 }, 1));
        // Test 10: k = 0
        System.out.println("Test 10: Expected 3 -> " + sol.longestOnes(new int[] { 1, 1, 1, 0, 0 }, 0));
        // Test 11: k = nums.length
        System.out.println("Test 11: Expected 5 -> " + sol.longestOnes(new int[] { 0, 0, 0, 0, 0 }, 5));
        // Test 12: Subarray at end
        System.out.println("Test 12: Expected 2 -> " + sol.longestOnes(new int[] { 0, 1, 1 }, 1));
        // Test 13: Subarray at start
        System.out.println("Test 13: Expected 2 -> " + sol.longestOnes(new int[] { 1, 1, 0 }, 1));
        // Test 14: Large numbers
        int[] nums14 = new int[100];
        for (int i = 0; i < 100; i++)
            nums14[i] = i % 2;
        System.out.println("Test 14: Expected 2 -> " + sol.longestOnes(nums14, 1));
        // Test 15: All zeros, k flips
        System.out.println("Test 15: Expected 2 -> " + sol.longestOnes(new int[] { 0, 0, 0, 0 }, 2));
    }
}
