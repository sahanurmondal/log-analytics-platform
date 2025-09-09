package arrays.medium;

import java.util.*;

/**
 * LeetCode 167: Two Sum II - Input Array Is Sorted
 * https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/
 *
 * Description:
 * Given a 1-indexed array of integers numbers that is already sorted in
 * non-decreasing order,
 * find two numbers such that they add up to a specific target number.
 *
 * Constraints:
 * - 2 <= numbers.length <= 3 * 10^4
 * - -1000 <= numbers[i] <= 1000
 * - numbers is sorted in non-decreasing order
 * - -1000 <= target <= 1000
 * - The tests are generated such that there is exactly one solution
 *
 * Follow-up:
 * - Your solution must use only constant extra space
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class TwoSumII {

    // Main solution - Two pointers (optimal for sorted array)
    public int[] twoSum(int[] numbers, int target) {
        int left = 0, right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];
            if (sum == target) {
                return new int[] { left + 1, right + 1 }; // 1-indexed
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return new int[] {};
    }

    // Alternative solution - Binary search
    public int[] twoSumBinarySearch(int[] numbers, int target) {
        for (int i = 0; i < numbers.length - 1; i++) {
            int complement = target - numbers[i];
            int left = i + 1, right = numbers.length - 1;

            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (numbers[mid] == complement) {
                    return new int[] { i + 1, mid + 1 };
                } else if (numbers[mid] < complement) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return new int[] {};
    }

    // Alternative solution - HashMap (not using sorted property)
    public int[] twoSumHashMap(int[] numbers, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < numbers.length; i++) {
            int complement = target - numbers[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement) + 1, i + 1 };
            }
            map.put(numbers[i], i);
        }

        return new int[] {};
    }

    public static void main(String[] args) {
        TwoSumII solution = new TwoSumII();

        // Test Case 1: Normal case
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 2, 7, 11, 15 }, 9))); // Expected: [1,2]

        // Test Case 2: Edge case - negative numbers
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 2, 3, 4 }, 6))); // Expected: [1,3]

        // Test Case 3: Corner case - duplicates
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -1, 0 }, -1))); // Expected: [1,2]

        // Test Case 4: Large input - far apart indices
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 10))); // Expected:
                                                                                                           // [1,9]

        // Test Case 5: Minimum input
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 1, 2 }, 3))); // Expected: [1,2]

        // Test Case 6: Special case - zero target
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -1, 1 }, 0))); // Expected: [1,2]

        // Test Case 7: All negatives
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -3, -2, -1 }, -4))); // Expected: [1,3]

        // Test Case 8: Adjacent elements
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 1, 2, 3, 4 }, 3))); // Expected: [1,2]

        // Test Case 9: Large target
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -1000, 0, 1000 }, 0))); // Expected: [1,3]

        // Test Case 10: Same numbers
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 3, 3 }, 6))); // Expected: [1,2]
    }
}
