package dp.medium;

import java.util.Arrays;

/**
 * LeetCode 376: Wiggle Subsequence (Longest Alternating Subsequence)
 * https://leetcode.com/problems/wiggle-subsequence/
 *
 * Description:
 * A wiggle sequence is a sequence where the differences between successive
 * numbers strictly alternate between positive and negative.
 * The first difference (if one exists) may be either positive or negative.
 * A sequence with one element and a sequence with two non-equal elements are
 * trivially wiggle sequences.
 * Given an integer array nums, return the length of the longest wiggle
 * subsequence of nums.
 *
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - 0 <= nums[i] <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * - What if we need the actual subsequence?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class LongestAlternatingSubsequence {

    // Approach 1: DP with Two States - O(n) time, O(1) space
    public int wiggleMaxLength(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return n;

        int up = 1; // Length of longest wiggle ending with up
        int down = 1; // Length of longest wiggle ending with down

        for (int i = 1; i < n; i++) {
            if (nums[i] > nums[i - 1]) {
                up = down + 1;
            } else if (nums[i] < nums[i - 1]) {
                down = up + 1;
            }
        }

        return Math.max(up, down);
    }

    // Approach 2: DP with Arrays - O(n) time, O(n) space
    public int wiggleMaxLengthArray(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return n;

        int[] up = new int[n];
        int[] down = new int[n];

        up[0] = down[0] = 1;

        for (int i = 1; i < n; i++) {
            if (nums[i] > nums[i - 1]) {
                up[i] = down[i - 1] + 1;
                down[i] = down[i - 1];
            } else if (nums[i] < nums[i - 1]) {
                down[i] = up[i - 1] + 1;
                up[i] = up[i - 1];
            } else {
                up[i] = up[i - 1];
                down[i] = down[i - 1];
            }
        }

        return Math.max(up[n - 1], down[n - 1]);
    }

    // Approach 3: Greedy Peak Valley - O(n) time, O(1) space
    public int wiggleMaxLengthGreedy(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return n;

        int count = 1;
        int prevDiff = 0;

        for (int i = 1; i < n; i++) {
            int diff = nums[i] - nums[i - 1];

            if ((diff > 0 && prevDiff <= 0) || (diff < 0 && prevDiff >= 0)) {
                count++;
                prevDiff = diff;
            }
        }

        return count;
    }

    // Approach 4: Two Pointers with Peak/Valley Detection - O(n) time, O(1) space
    public int wiggleMaxLengthTwoPointers(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return n;

        int count = 1;
        int i = 0;

        while (i < n - 1) {
            // Skip duplicates
            while (i < n - 1 && nums[i] == nums[i + 1]) {
                i++;
            }

            if (i == n - 1)
                break;

            boolean isIncreasing = nums[i] < nums[i + 1];
            i++;
            count++;

            // Continue in same direction until we find a peak/valley
            while (i < n - 1) {
                if (isIncreasing && nums[i] > nums[i + 1]) {
                    // Found peak, switch to decreasing
                    isIncreasing = false;
                    count++;
                } else if (!isIncreasing && nums[i] < nums[i + 1]) {
                    // Found valley, switch to increasing
                    isIncreasing = true;
                    count++;
                } else if (nums[i] == nums[i + 1]) {
                    // Skip duplicates
                    while (i < n - 1 && nums[i] == nums[i + 1]) {
                        i++;
                    }
                    continue;
                }
                i++;
            }
        }

        return count;
    }

    // Approach 5: Get Actual Wiggle Subsequence - O(n) time, O(n) space
    public int[] getWiggleSubsequence(int[] nums) {
        int n = nums.length;
        if (n <= 1)
            return nums;

        java.util.List<Integer> result = new java.util.ArrayList<>();
        result.add(nums[0]);

        int prevDiff = 0;

        for (int i = 1; i < n; i++) {
            int diff = nums[i] - nums[i - 1];

            if ((diff > 0 && prevDiff <= 0) || (diff < 0 && prevDiff >= 0)) {
                result.add(nums[i]);
                prevDiff = diff;
            }
        }

        return result.stream().mapToInt(i -> i).toArray();
    }

    public static void main(String[] args) {
        LongestAlternatingSubsequence solution = new LongestAlternatingSubsequence();

        System.out.println("=== Longest Alternating Subsequence Test Cases ===");

        // Test Case 1: Example from problem
        int[] nums1 = { 1, 7, 4, 9, 2, 5 };
        System.out.println("Test 1 - Array: " + Arrays.toString(nums1));
        System.out.println("DP Two States: " + solution.wiggleMaxLength(nums1));
        System.out.println("DP Arrays: " + solution.wiggleMaxLengthArray(nums1));
        System.out.println("Greedy: " + solution.wiggleMaxLengthGreedy(nums1));
        System.out.println("Two Pointers: " + solution.wiggleMaxLengthTwoPointers(nums1));
        System.out.println("Actual Subsequence: " + Arrays.toString(solution.getWiggleSubsequence(nums1)));
        System.out.println("Expected: 6\n");

        // Test Case 2: Monotonic sequence
        int[] nums2 = { 1, 2, 3, 4, 5 };
        System.out.println("Test 2 - Array: " + Arrays.toString(nums2));
        System.out.println("DP Two States: " + solution.wiggleMaxLength(nums2));
        System.out.println("Expected: 2\n");

        // Test Case 3: With duplicates
        int[] nums3 = { 1, 17, 5, 10, 13, 15, 10, 5, 16, 8 };
        System.out.println("Test 3 - Array: " + Arrays.toString(nums3));
        System.out.println("DP Two States: " + solution.wiggleMaxLength(nums3));
        System.out.println("Expected: 7\n");

        // Test Case 4: All same elements
        int[] nums4 = { 2, 2, 2, 2, 2 };
        System.out.println("Test 4 - Array: " + Arrays.toString(nums4));
        System.out.println("DP Two States: " + solution.wiggleMaxLength(nums4));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        LongestAlternatingSubsequence solution = new LongestAlternatingSubsequence();

        int[] largeArray = new int[1000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 1000);
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.wiggleMaxLength(largeArray);
        long end = System.nanoTime();
        System.out.println("DP Two States: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.wiggleMaxLengthGreedy(largeArray);
        end = System.nanoTime();
        System.out.println("Greedy: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.wiggleMaxLengthTwoPointers(largeArray);
        end = System.nanoTime();
        System.out.println("Two Pointers: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
