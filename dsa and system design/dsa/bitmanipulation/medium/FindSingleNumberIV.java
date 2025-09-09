package bitmanipulation.medium;

import java.util.*;

/**
 * LeetCode 137: Single Number IV
 * https://leetcode.com/problems/single-number-iv/
 * Difficulty: Medium
 *
 * Description:
 * Given an array where every element appears four times except for one element
 * which appears exactly once. Find that single element.
 * 
 * Example:
 * Input: nums = [2,2,2,2,3]
 * Output: 3
 * Explanation: All elements except 3 appear four times
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - -3 * 10^4 <= nums[i] <= 3 * 10^4
 * 
 * Follow-up:
 * 1. Can you solve it without extra space?
 * 2. Can you solve it in O(n) time complexity?
 * 3. What if elements appear k times instead of 4?
 */
public class FindSingleNumberIV {

    // Approach 1: Bit counting - O(32n) time, O(1) space
    public int singleNumber(int[] nums) {
        int result = 0;

        // Count bits at each position
        for (int i = 0; i < 32; i++) {
            int sum = 0;
            for (int num : nums) {
                sum += (num >> i) & 1;
            }
            // If sum is not divisible by 4, the single number has a 1 at this position
            if (sum % 4 != 0) {
                result |= (1 << i);
            }
        }

        return result;
    }

    // Approach 2: Using bit mask - O(n) time, O(1) space
    public int singleNumberMask(int[] nums) {
        int ones = 0, twos = 0, threes = 0;

        for (int num : nums) {
            twos |= ones & num;
            ones ^= num;
            threes = ones & twos;

            // Clear bits that are set in threes
            ones &= ~threes;
            twos &= ~threes;
        }

        return ones;
    }

    // Approach 3: Using HashMap - O(n) time, O(n) space
    public int singleNumberHash(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();

        for (int num : nums) {
            count.merge(num, 1, Integer::sum);
        }

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getValue();
            }
        }

        return 0; // Should never reach here given constraints
    }

    // Approach 4: Sort and check - O(n log n) time, O(1) space
    public int singleNumberSort(int[] nums) {
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 1; i += 4) {
            if (i + 3 >= nums.length || nums[i] != nums[i + 3]) {
                return nums[i];
            }
        }

        return nums[nums.length - 1];
    }

    // Follow-up: Find single number when others appear k times
    public int singleNumberK(int[] nums, int k) {
        int result = 0;

        for (int i = 0; i < 32; i++) {
            int sum = 0;
            for (int num : nums) {
                sum += (num >> i) & 1;
            }
            if (sum % k != 0) {
                result |= (1 << i);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindSingleNumberIV solution = new FindSingleNumberIV();

        // Test Case 1: Normal case
        System.out.println("Test 1: " +
                solution.singleNumber(new int[] { 2, 2, 2, 2, 3 })); // 3

        // Test Case 2: All negatives
        System.out.println("Test 2: " +
                solution.singleNumber(new int[] { -1, -1, -1, -1, -2 })); // -2

        // Test Case 3: Mixed numbers
        System.out.println("Test 3: " +
                solution.singleNumber(new int[] { 1, 1, 1, 1, 2, 2, 2, 2, 3 })); // 3

        // Test all approaches
        int[][] testCases = {
                { 2, 2, 2, 2, 3 },
                { -1, -1, -1, -1, -2 },
                { 1, 1, 1, 1, 2, 2, 2, 2, 3 },
                { 7, 7, 7, 7, 1, 1, 1, 1, 4 },
                { 5 }
        };

        System.out.println("\nTesting all approaches:");
        for (int[] test : testCases) {
            int result1 = solution.singleNumber(test);
            int result2 = solution.singleNumberMask(test);
            int result3 = solution.singleNumberHash(test);
            int result4 = solution.singleNumberSort(test.clone());

            boolean consistent = result1 == result2 && result2 == result3 && result3 == result4;
            System.out.printf("Array %s: %d (consistent: %b)%n",
                    Arrays.toString(test), result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int size = 30001;
        int[] largeArray = new int[size];
        for (int i = 0; i < size - 1; i++) {
            largeArray[i] = i / 4;
        }
        largeArray[size - 1] = 99999;

        long start;
        int result;

        start = System.currentTimeMillis();
        result = solution.singleNumber(largeArray);
        System.out.println("Bit counting: " + (System.currentTimeMillis() - start) +
                "ms, Result: " + result);

        start = System.currentTimeMillis();
        result = solution.singleNumberMask(largeArray);
        System.out.println("Bit mask: " + (System.currentTimeMillis() - start) +
                "ms, Result: " + result);

        start = System.currentTimeMillis();
        result = solution.singleNumberHash(largeArray);
        System.out.println("HashMap: " + (System.currentTimeMillis() - start) +
                "ms, Result: " + result);

        start = System.currentTimeMillis();
        result = solution.singleNumberSort(largeArray.clone());
        System.out.println("Sort: " + (System.currentTimeMillis() - start) +
                "ms, Result: " + result);

        // Test k-times appearance
        System.out.println("\nTesting k-times appearance:");
        int[] kTest = { 1, 1, 1, 2, 2, 2, 3 };
        System.out.println("k=3: " + solution.singleNumberK(kTest, 3)); // 3

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single element: " +
                solution.singleNumber(new int[] { 5 })); // 5
        System.out.println("All same except one: " +
                solution.singleNumber(new int[] { 1, 1, 1, 1, 1, 1, 1, 1, 2 })); // 2
        System.out.println("Max value: " +
                solution.singleNumber(new int[] {
                        Integer.MAX_VALUE, Integer.MAX_VALUE,
                        Integer.MAX_VALUE, Integer.MAX_VALUE, 42
                })); // 42
    }
}
