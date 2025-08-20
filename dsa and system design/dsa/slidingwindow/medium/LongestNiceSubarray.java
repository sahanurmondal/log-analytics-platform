package slidingwindow.medium;

/**
 * LeetCode 2401: Longest Nice Subarray
 * https://leetcode.com/problems/longest-nice-subarray/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 3+ interviews)
 *
 * Description: Given an array of integers, return the length of the longest
 * subarray where every pair of elements has a bitwise AND of zero.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 1 <= nums[i] <= 10^9
 *
 * Follow-up Questions:
 * 1. How to return the actual subarray?
 * 2. What if you want at most k overlapping bits?
 * 3. How to solve for very large arrays efficiently?
 */
public class LongestNiceSubarray {
    // Approach 1: Sliding Window with Bitmask - O(n) time, O(1) space
    public int longestNiceSubarray(int[] nums) {
        int left = 0, mask = 0, maxLen = 0;
        for (int right = 0; right < nums.length; right++) {
            while ((mask & nums[right]) != 0) {
                mask ^= nums[left++];
            }
            mask |= nums[right];
            maxLen = Math.max(maxLen, right - left + 1);
        }
        return maxLen;
    }

    // Approach 2: Brute Force - O(n^2) time, O(1) space
    public int longestNiceSubarrayBrute(int[] nums) {
        int maxLen = 0;
        for (int i = 0; i < nums.length; i++) {
            int mask = 0;
            for (int j = i; j < nums.length; j++) {
                if ((mask & nums[j]) != 0)
                    break;
                mask |= nums[j];
                maxLen = Math.max(maxLen, j - i + 1);
            }
        }
        return maxLen;
    }

    // Follow-up 1: Return actual subarray
    public int[] getLongestNiceSubarray(int[] nums) {
        int left = 0, mask = 0, maxLen = 0, start = 0;
        for (int right = 0; right < nums.length; right++) {
            while ((mask & nums[right]) != 0) {
                mask ^= nums[left++];
            }
            mask |= nums[right];
            if (right - left + 1 > maxLen) {
                maxLen = right - left + 1;
                start = left;
            }
        }
        int[] res = new int[maxLen];
        System.arraycopy(nums, start, res, 0, maxLen);
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LongestNiceSubarray sol = new LongestNiceSubarray();
        // Test 1: Basic
        System.out.println("Test 1: Expected 3 -> " + sol.longestNiceSubarray(new int[] { 1, 3, 8, 48, 10 }));
        // Test 2: All unique bits
        System.out.println("Test 2: Expected 5 -> " + sol.longestNiceSubarray(new int[] { 1, 2, 4, 8, 16 }));
        // Test 3: All same
        System.out.println("Test 3: Expected 1 -> " + sol.longestNiceSubarray(new int[] { 7, 7, 7, 7 }));
        // Test 4: Brute force approach
        System.out.println("Test 4: Expected 3 -> " + sol.longestNiceSubarrayBrute(new int[] { 1, 3, 8, 48, 10 }));
        // Test 5: Get actual subarray
        System.out.println("Test 5: Expected [8,48,10] -> "
                + java.util.Arrays.toString(sol.getLongestNiceSubarray(new int[] { 1, 3, 8, 48, 10 })));
        // Test 6: Edge case, single element
        System.out.println("Test 6: Expected 1 -> " + sol.longestNiceSubarray(new int[] { 42 }));
        // Test 7: Edge case, empty array
        System.out.println("Test 7: Expected 0 -> " + sol.longestNiceSubarray(new int[] {}));
        // Test 8: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = 1 << (i % 30);
        System.out.println("Test 8: Large input -> " + sol.longestNiceSubarray(large));
        // Test 9: Overlapping bits
        System.out.println("Test 9: Expected 2 -> " + sol.longestNiceSubarray(new int[] { 3, 5, 6, 7 }));
        // Test 10: All zeros
        System.out.println("Test 10: Expected 100 -> " + sol.longestNiceSubarray(new int[100]));
        // Test 11: k overlapping bits (not implemented)
        // Test 12: Subarray at end
        System.out.println("Test 12: Expected 2 -> " + sol.longestNiceSubarray(new int[] { 8, 8, 1, 2 }));
        // Test 13: Subarray at start
        System.out.println("Test 13: Expected 2 -> " + sol.longestNiceSubarray(new int[] { 1, 2, 8, 8 }));
        // Test 14: Large numbers
        System.out.println("Test 14: Expected 2 -> " + sol.longestNiceSubarray(new int[] { 1000000000, 1000000000 }));
        // Test 15: All powers of two
        int[] pow2 = new int[20];
        for (int i = 0; i < 20; i++)
            pow2[i] = 1 << i;
        System.out.println("Test 15: Expected 20 -> " + sol.longestNiceSubarray(pow2));
    }
}
