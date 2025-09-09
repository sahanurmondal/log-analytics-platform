package sorting.medium;

import java.util.*;

/**
 * LeetCode 360: Sort Transformed Array
 * URL: https://leetcode.com/problems/so public static void main(String[] args)
 * {
 * SortTransformedArray solution = new SortTransformedArray();
 * 
 * // Test Case 1: Positive quadratic coefficient
 * System.out.println("Test 1: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-4,-2,2,4}, 1, 3,
 * 5))); // Expected: [3,9,15,33]
 * 
 * // Test Case 2: Negative quadratic coefficient
 * System.out.println("Test 2: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-4,-2,2,4}, -1, 3,
 * 5))); // Expected: [-23,-5,1,7]
 * 
 * // Test Case 3: Linear function (a = 0)
 * System.out.println("Test 3: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{1,2,3}, 0, 2, 1)));
 * // Expected: [3,5,7]
 * 
 * // Test Case 4: Pure quadratic (b = 0, c = 0)
 * System.out.println("Test 4: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-1,0,1}, 1, 0, 0)));
 * // Expected: [0,0,1]
 * 
 * // Test Case 5: Parabola with vertex
 * System.out.println("Test 5: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-3,-1,2,4}, 2, -1,
 * 3))); // Expected: [6,5,15,35]
 * 
 * // Test Case 6: Single element
 * System.out.println("Test 6: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{5}, 1, 2, 3))); //
 * Expected: [38]
 * 
 * // Test Case 7: Negative coefficients
 * System.out.println("Test 7: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-2,-1,0,1,2}, -1, 2,
 * -1))); // Expected: [-6,-2,0,-2,-6]
 * 
 * // Test Case 8: Large coefficients
 * System.out.println("Test 8: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{1,2}, 10, -5, 1)));
 * // Expected: [6,31]
 * 
 * // Test Case 9: Zero array
 * System.out.println("Test 9: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{0,0,0}, 1, 1, 1)));
 * // Expected: [1,1,1]
 * 
 * // Test Case 10: Symmetric input
 * System.out.println("Test 10: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-2,-1,0,1,2}, 1, 0,
 * 1))); // Expected: [1,1,2,2,5]
 * 
 * // Test Case 11: Decreasing linear (negative b)
 * System.out.println("Test 11: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{1,2,3,4}, 0, -1,
 * 5))); // Expected: [1,2,3,4]
 * 
 * // Test Case 12: Mixed sign input
 * System.out.println("Test 12: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-5,0,5}, 1, 1, 1)));
 * // Expected: [1,21,31]
 * 
 * // Test Case 13: Vertex at input point
 * System.out.println("Test 13: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-1,0,1,2}, 1, -2,
 * 3))); // Expected: [2,3,6,11]
 * 
 * // Test Case 14: Large negative quadratic
 * System.out.println("Test 14: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{1,2,3}, -2, 0, 10)));
 * // Expected: [-8,2,8]
 * 
 * // Test Case 15: Edge boundary values
 * System.out.println("Test 15: " +
 * Arrays.toString(solution.sortTransformedArray(new int[]{-100,100}, 1, 1,
 * 1))); // Expected: [9902,10102]
 * }formed-array/
 * Difficulty: Medium
 * Companies: Google, Facebook, Microsoft
 * Frequency: Medium
 * 
 * Description:
 * Given a sorted integer array nums and integers a, b, c, apply a quadratic
 * function f(x) = ax^2 + bx + c to each element nums[i] in the array, and
 * return the array in a sorted order.
 * 
 * Constraints:
 * - 1 <= nums.length <= 200
 * - -100 <= nums[i], a, b, c <= 100
 * - nums is sorted in ascending order
 * 
 * Follow-up Questions:
 * 1. Can you solve it using two pointers without sorting?
 * 2. How would you handle very large coefficients?
 * 3. Can you extend this to cubic or higher-degree polynomials?
 * 4. How would you optimize for the case when a = 0 (linear function)?
 */
public class SortTransformedArray {
    public int[] sortTransformedArray(int[] nums, int a, int b, int c) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0, right = n - 1;
        int index = a >= 0 ? n - 1 : 0;

        while (left <= right) {
            int leftVal = transform(nums[left], a, b, c);
            int rightVal = transform(nums[right], a, b, c);

            if (a >= 0) {
                if (leftVal >= rightVal) {
                    result[index--] = leftVal;
                    left++;
                } else {
                    result[index--] = rightVal;
                    right--;
                }
            } else {
                if (leftVal <= rightVal) {
                    result[index++] = leftVal;
                    left++;
                } else {
                    result[index++] = rightVal;
                    right--;
                }
            }
        }

        return result;
    }

    private int transform(int x, int a, int b, int c) {
        return a * x * x + b * x + c;
    }

    // Follow-up 1: Linear function optimization (a = 0)
    public int[] sortTransformedArrayLinear(int[] nums, int b, int c) {
        int[] result = new int[nums.length];
        if (b >= 0) {
            for (int i = 0; i < nums.length; i++) {
                result[i] = b * nums[i] + c;
            }
        } else {
            for (int i = nums.length - 1; i >= 0; i--) {
                result[nums.length - 1 - i] = b * nums[i] + c;
            }
        }
        return result;
    }

    // Follow-up 2: Handle large coefficients with BigInteger
    public int[] sortTransformedArraySafe(int[] nums, long a, long b, long c) {
        int n = nums.length;
        int[] result = new int[n];
        int left = 0, right = n - 1;
        int index = a >= 0 ? n - 1 : 0;

        while (left <= right) {
            long leftVal = transformSafe(nums[left], a, b, c);
            long rightVal = transformSafe(nums[right], a, b, c);

            if (a >= 0) {
                if (leftVal >= rightVal) {
                    result[index--] = (int) leftVal;
                    left++;
                } else {
                    result[index--] = (int) rightVal;
                    right--;
                }
            } else {
                if (leftVal <= rightVal) {
                    result[index++] = (int) leftVal;
                    left++;
                } else {
                    result[index++] = (int) rightVal;
                    right--;
                }
            }
        }

        return result;
    }

    private long transformSafe(int x, long a, long b, long c) {
        return a * (long) x * x + b * x + c;
    }

    // Follow-up 3: Cubic polynomial extension
    public int[] sortTransformedArrayCubic(int[] nums, int a, int b, int c, int d) {
        int[] transformed = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            transformed[i] = transformCubic(nums[i], a, b, c, d);
        }
        Arrays.sort(transformed);
        return transformed;
    }

    private int transformCubic(int x, int a, int b, int c, int d) {
        return a * x * x * x + b * x * x + c * x + d;
    }

    public static void main(String[] args) {
        SortTransformedArray solution = new SortTransformedArray();
        System.out.println(Arrays.toString(solution.sortTransformedArray(new int[] { -4, -2, 2, 4 }, 1, 3, 5))); // [3,9,15,33]
        System.out.println(Arrays.toString(solution.sortTransformedArray(new int[] { -4, -2, 2, 4 }, -1, 3, 5))); // [-23,-5,1,7]
        System.out.println(Arrays.toString(solution.sortTransformedArray(new int[] { 1, 2, 3 }, 0, 2, 1))); // [3,5,7]
        System.out.println(Arrays.toString(solution.sortTransformedArray(new int[] { -1, 0, 1 }, 1, 0, 0))); // [0,0,1]
        System.out.println(Arrays.toString(solution.sortTransformedArray(new int[] { -3, -1, 2, 4 }, 2, -1, 3))); // [6,5,15,35]
    }
}
