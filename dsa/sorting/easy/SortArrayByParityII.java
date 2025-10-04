package sorting.easy;

import java.util.*;

/**
 * LeetCode 922: Sort Array By Parity II
 * URL: https://leetcode.com/problems/sort-array-by-parity-ii/ Difficulty: Easy
 * Companies: Amazon, Google, Microsoft
 * Frequency: Medium
 * 
 * Description:
 * Given an array of integers nums, half of the integers in nums are odd, and
 * the other half are even.
 * Sort the array so that whenever nums[i] is odd, i is odd, and whenever
 * nums[i] is even, i is even.
 * Return any answer array that satisfies this condition.
 * 
 * Constraints:
 * - 2 <= nums.length <= 2 * 10^4
 * - nums.length is even
 * - Half of the integers in nums are even
 * - 0 <= nums[i] <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you solve it in-place?
 * 2. What if the number of even and odd elements is not equal?
 * 3. Can you solve it with only one pass?
 * 4. How would you handle negative numbers?
 */
public class SortArrayByParityII {
    // Two-pass approach
    public int[] sortArrayByParityII(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int evenIdx = 0, oddIdx = 1;

        for (int num : nums) {
            if (num % 2 == 0) {
                result[evenIdx] = num;
                evenIdx += 2;
            } else {
                result[oddIdx] = num;
                oddIdx += 2;
            }
        }

        return result;
    }

    // In-place approach
    public int[] sortArrayByParityIIInPlace(int[] nums) {
        int n = nums.length;
        int evenIdx = 0;

        for (int i = 1; i < n; i += 2) {
            if (nums[i] % 2 == 0) {
                while (nums[evenIdx] % 2 == 0) {
                    evenIdx += 2;
                }
                swap(nums, i, evenIdx);
            }
        }

        return nums;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    // Follow-up 2: Handle unequal even/odd counts
    public int[] sortArrayByParityIIUnequal(int[] nums) {
        List<Integer> evens = new ArrayList<>();
        List<Integer> odds = new ArrayList<>();

        for (int num : nums) {
            if (num % 2 == 0) {
                evens.add(num);
            } else {
                odds.add(num);
            }
        }

        int[] result = new int[nums.length];
        int evenIdx = 0, oddIdx = 1;

        for (int even : evens) {
            if (evenIdx < nums.length) {
                result[evenIdx] = even;
                evenIdx += 2;
            }
        }

        for (int odd : odds) {
            if (oddIdx < nums.length) {
                result[oddIdx] = odd;
                oddIdx += 2;
            }
        }

        return result;
    }

    // Follow-up 3: One pass with two pointers
    public int[] sortArrayByParityIIOnePass(int[] nums) {
        int evenIdx = 0, oddIdx = 1;

        while (evenIdx < nums.length && oddIdx < nums.length) {
            if (nums[evenIdx] % 2 == 0) {
                evenIdx += 2;
            } else if (nums[oddIdx] % 2 == 1) {
                oddIdx += 2;
            } else {
                swap(nums, evenIdx, oddIdx);
                evenIdx += 2;
                oddIdx += 2;
            }
        }

        return nums;
    }

    // Follow-up 4: Handle negative numbers with modular arithmetic
    public int[] sortArrayByParityIINegative(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int evenIdx = 0, oddIdx = 1;

        for (int num : nums) {
            if (((num % 2) + 2) % 2 == 0) { // Handle negative modulo
                result[evenIdx] = num;
                evenIdx += 2;
            } else {
                result[oddIdx] = num;
                oddIdx += 2;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        SortArrayByParityII solution = new SortArrayByParityII();

        // Test Case 1: Basic mixed array
        System.out.println("Test 1: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 4, 2, 5, 7 }))); // Expected:
                                                                                                                  // [4,5,2,7]

        // Test Case 2: Minimum size array
        System.out.println("Test 2: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 2, 3 }))); // Expected:
                                                                                                            // [2,3]

        // Test Case 3: Already sorted correctly
        System.out.println("Test 3: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 4, 1, 2, 3 }))); // Expected:
                                                                                                                  // [4,1,2,3]

        // Test Case 4: Reverse parity
        System.out.println("Test 4: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 1, 2, 3, 4 }))); // Expected:
                                                                                                                  // [2,1,4,3]

        // Test Case 5: All zeros and ones
        System.out.println("Test 5: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 0, 1, 0, 1 }))); // Expected:
                                                                                                                  // [0,1,0,1]

        // Test Case 6: Large even/odd numbers
        System.out.println("Test 6: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 8, 7, 6, 5 }))); // Expected:
                                                                                                                  // [8,7,6,5]

        // Test Case 7: Mixed large numbers
        System.out.println("Test 7: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 100, 99, 98, 97 }))); // Expected:
                                                                                                                       // valid
                                                                                                                       // arrangement

        // Test Case 8: Consecutive numbers
        System.out.println("Test 8: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 1, 2, 3, 4, 5, 6 }))); // Expected:
                                                                                                                        // valid
                                                                                                                        // arrangement

        // Test Case 9: Same even/odd values
        System.out.println("Test 9: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 2, 1, 2, 1 }))); // Expected:
                                                                                                                  // [2,1,2,1]

        // Test Case 10: Power of 2 numbers
        System.out
                .println("Test 10: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 1, 2, 4, 8, 16, 32 }))); // Expected:
                                                                                                                         // valid
                                                                                                                         // arrangement

        // Test Case 11: In-place method test
        int[] test11 = { 4, 2, 5, 7 };
        System.out.println("Test 11 (In-place): " + Arrays.toString(solution.sortArrayByParityIIInPlace(test11))); // Expected:
                                                                                                                   // valid
                                                                                                                   // arrangement

        // Test Case 12: Edge case with larger array
        System.out.println(
                "Test 12: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 1, 3, 5, 7, 2, 4, 6, 8 }))); // Expected:
                                                                                                                    // valid
                                                                                                                    // arrangement

        // Test Case 13: Random mixed order
        System.out.println("Test 13: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 6, 3, 8, 1 }))); // Expected:
                                                                                                                   // [6,3,8,1]

        // Test Case 14: All small numbers
        System.out.println("Test 14: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 0, 1, 2, 3 }))); // Expected:
                                                                                                                   // [0,1,2,3]

        // Test Case 15: Boundary values
        System.out.println(
                "Test 15: " + Arrays.toString(solution.sortArrayByParityII(new int[] { 999, 1000, 997, 998 }))); // Expected:
                                                                                                                 // valid
                                                                                                                 // arrangement
    }
}
