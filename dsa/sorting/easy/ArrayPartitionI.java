package sorting.easy;

import java.util.*;

/**
 * LeetCode 561: Array Partition I
 * URL: https://leetcode.com/problems/array-partition-i/
 * Difficulty: Easy
 * Companies: Amazon, Apple, Microsoft
 * Frequency: Medium
 * 
 * Description:
 * Given an integer array nums of 2n integers, group these integers into n pairs
 * (a1, b1), (a2, b2), ..., (an, bn)
 * such that the sum of min(ai, bi) for all i is maximized. Return the maximized
 * sum.
 * 
 * Constraints:
 * - 1 <= n <= 10^4
 * - nums.length == 2 * n
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve it without sorting when the range is small?
 * 2. What if we want to maximize the sum of max(ai, bi) instead?
 * 3. Can you extend this to k-tuples instead of pairs?
 * 4. How would you solve this if pairs must be adjacent in the original array?
 */
public class ArrayPartitionI {
    // Sorting approach - O(n log n)
    public int arrayPairSum(int[] nums) {
        Arrays.sort(nums);
        int sum = 0;
        for (int i = 0; i < nums.length; i += 2) {
            sum += nums[i];
        }
        return sum;
    }

    // Counting sort approach - O(n) when range is known
    public int arrayPairSumCountingSort(int[] nums) {
        int offset = 10000;
        int[] count = new int[20001];

        for (int num : nums) {
            count[num + offset]++;
        }

        int sum = 0;
        boolean isEven = true;

        for (int i = 0; i < count.length; i++) {
            while (count[i] > 0) {
                if (isEven) {
                    sum += i - offset;
                }
                isEven = !isEven;
                count[i]--;
            }
        }

        return sum;
    }

    // Follow-up 2: Maximize sum of max(ai, bi)
    public int arrayPairSumMax(int[] nums) {
        Arrays.sort(nums);
        int sum = 0;
        for (int i = 1; i < nums.length; i += 2) {
            sum += nums[i];
        }
        return sum;
    }

    // Follow-up 3: K-tuples extension
    public int arrayKTupleSum(int[] nums, int k) {
        Arrays.sort(nums);
        int sum = 0;
        for (int i = 0; i < nums.length; i += k) {
            sum += nums[i]; // Take minimum of each k-tuple
        }
        return sum;
    }

    // Follow-up 4: Adjacent pairs only
    public int arrayPairSumAdjacent(int[] nums) {
        int sum = 0;
        for (int i = 0; i < nums.length; i += 2) {
            sum += Math.min(nums[i], nums[i + 1]);
        }
        return sum;
    }

    // Bucket sort optimization for small range
    public int arrayPairSumBucket(int[] nums) {
        int[] bucket = new int[20001];
        for (int num : nums) {
            bucket[num + 10000]++;
        }

        int sum = 0;
        boolean isMin = true;

        for (int i = 0; i < bucket.length; i++) {
            while (bucket[i] > 0) {
                if (isMin) {
                    sum += i - 10000;
                }
                isMin = !isMin;
                bucket[i]--;
            }
        }

        return sum;
    }

    public static void main(String[] args) {
        ArrayPartitionI solution = new ArrayPartitionI();

        // Test Case 1: Basic example
        System.out.println("Test 1: " + solution.arrayPairSum(new int[] { 1, 4, 3, 2 })); // Expected: 4

        // Test Case 2: Larger array
        System.out.println("Test 2: " + solution.arrayPairSum(new int[] { 6, 2, 6, 5, 1, 2 })); // Expected: 9

        // Test Case 3: Minimum case
        System.out.println("Test 3: " + solution.arrayPairSum(new int[] { 1, 1 })); // Expected: 1

        // Test Case 4: Negative numbers
        System.out.println("Test 4: " + solution.arrayPairSum(new int[] { -1, 0, 1, 2 })); // Expected: 0

        // Test Case 5: Descending order
        System.out.println("Test 5: " + solution.arrayPairSum(new int[] { 10, 9, 8, 7, 6, 5 })); // Expected: 21

        // Test Case 6: All same numbers
        System.out.println("Test 6: " + solution.arrayPairSum(new int[] { 3, 3, 3, 3 })); // Expected: 6

        // Test Case 7: Large numbers
        System.out.println("Test 7: " + solution.arrayPairSum(new int[] { 100, 200, 300, 400 })); // Expected: 400

        // Test Case 8: Mixed positive/negative
        System.out.println("Test 8: " + solution.arrayPairSum(new int[] { -10, -5, 0, 5, 10, 15 })); // Expected: 0

        // Test Case 9: Consecutive numbers
        System.out.println("Test 9: " + solution.arrayPairSum(new int[] { 1, 2, 3, 4, 5, 6 })); // Expected: 9

        // Test Case 10: Zero included
        System.out.println("Test 10: " + solution.arrayPairSum(new int[] { 0, 0, 1, 1 })); // Expected: 1

        // Test Case 11: Counting sort approach
        System.out.println("Test 11 (Counting): " + solution.arrayPairSumCountingSort(new int[] { 1, 4, 3, 2 })); // Expected:
                                                                                                                  // 4

        // Test Case 12: Power of 2 numbers
        System.out.println("Test 12: " + solution.arrayPairSum(new int[] { 1, 2, 4, 8 })); // Expected: 3

        // Test Case 13: Boundary values
        System.out.println("Test 13: " + solution.arrayPairSum(new int[] { -10000, 10000, -9999, 9999 })); // Expected:
                                                                                                           // -9999

        // Test Case 14: Duplicates mixed
        System.out.println("Test 14: " + solution.arrayPairSum(new int[] { 7, 3, 1, 7, 3, 1 })); // Expected: 11

        // Test Case 15: Maximum sum variant
        System.out.println("Test 15 (Max): " + solution.arrayPairSumMax(new int[] { 1, 4, 3, 2 })); // Expected: 7
    }
}
