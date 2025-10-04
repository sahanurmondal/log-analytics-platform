package slidingwindow.medium;

/**
 * LeetCode 1493: Longest Subarray of 1's After Deleting One Element
 * https://leetcode.com/problems/longest-subarray-of-1s-after-deleting-one-element/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given a binary array nums, return the length of the longest
 * subarray containing only 1's after deleting one element.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - nums[i] is either 0 or 1
 *
 * Follow-up Questions:
 * 1. What if you can delete up to k elements?
 * 2. How to return the actual subarray?
 * 3. How to solve for very large arrays efficiently?
 */
public class LongestSubarrayOf1sAfterDeletingOneElement {
    // Approach 1: Sliding Window (at most 1 zero) - O(n) time, O(1) space
    public int longestSubarray(int[] nums) {
        int left = 0, zeroCount = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0)
                zeroCount++;
            while (zeroCount > 1) {
                if (nums[left++] == 0)
                    zeroCount--;
            }
            maxLen = Math.max(maxLen, right - left);
        }
        return maxLen;
    }

    // Approach 2: Prefix Sum - O(n) time, O(n) space
    public int longestSubarrayPrefixSum(int[] nums) {
        int n = nums.length, maxLen = 0, prevZero = -1, prevPrevZero = -1;
        for (int i = 0; i < n; i++) {
            if (nums[i] == 0) {
                prevPrevZero = prevZero;
                prevZero = i;
            }
            maxLen = Math.max(maxLen, i - prevPrevZero - 1);
        }
        return maxLen;
    }

    // Follow-up 1: Delete up to k elements
    public int longestSubarrayKDeletes(int[] nums, int k) {
        int left = 0, zeroCount = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            if (nums[right] == 0)
                zeroCount++;
            while (zeroCount > k) {
                if (nums[left++] == 0)
                    zeroCount--;
            }
            maxLen = Math.max(maxLen, right - left + 1 - k);
        }
        return maxLen;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LongestSubarrayOf1sAfterDeletingOneElement sol = new LongestSubarrayOf1sAfterDeletingOneElement();
        // Test 1: Basic
        System.out.println("Test 1: Expected 3 -> " + sol.longestSubarray(new int[] { 1, 1, 0, 1 }));
        // Test 2: All ones
        System.out.println("Test 2: Expected 3 -> " + sol.longestSubarray(new int[] { 1, 1, 1 }));
        // Test 3: All zeros
        System.out.println("Test 3: Expected 0 -> " + sol.longestSubarray(new int[] { 0, 0, 0 }));
        // Test 4: Prefix sum approach
        System.out.println("Test 4: Expected 3 -> " + sol.longestSubarrayPrefixSum(new int[] { 1, 1, 0, 1 }));
        // Test 5: k deletes
        System.out
                .println("Test 5: Expected 4 -> " + sol.longestSubarrayKDeletes(new int[] { 1, 1, 0, 1, 1, 0, 1 }, 2));
        // Test 6: Edge case, single element
        System.out.println("Test 6: Expected 0 -> " + sol.longestSubarray(new int[] { 1 }));
        // Test 7: Edge case, empty array
        System.out.println("Test 7: Expected 0 -> " + sol.longestSubarray(new int[] {}));
        // Test 8: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = 1;
        System.out.println("Test 8: Large input -> " + sol.longestSubarray(large));
        // Test 9: Alternating ones and zeros
        System.out.println("Test 9: Expected 1 -> " + sol.longestSubarray(new int[] { 1, 0, 1, 0, 1 }));
        // Test 10: k = 0
        System.out.println("Test 10: Expected 5 -> " + sol.longestSubarrayKDeletes(new int[] { 1, 1, 1, 1, 1 }, 0));
        // Test 11: k = nums.length
        System.out.println("Test 11: Expected 0 -> " + sol.longestSubarrayKDeletes(new int[] { 0, 0, 0, 0 }, 4));
        // Test 12: Subarray at end
        System.out.println("Test 12: Expected 2 -> " + sol.longestSubarray(new int[] { 0, 1, 1 }));
        // Test 13: Subarray at start
        System.out.println("Test 13: Expected 2 -> " + sol.longestSubarray(new int[] { 1, 1, 0 }));
        // Test 14: Large numbers
        int[] nums14 = new int[100];
        for (int i = 0; i < 100; i++)
            nums14[i] = i % 2;
        System.out.println("Test 14: Expected 1 -> " + sol.longestSubarray(nums14));
        // Test 15: All zeros, k deletes
        System.out.println("Test 15: Expected 0 -> " + sol.longestSubarrayKDeletes(new int[] { 0, 0, 0, 0 }, 2));
    }
}
