package arrays.easy;

import java.util.*;

/**
 * LeetCode 1: Two Sum
 * https://leetcode.com/problems/two-sum/
 */
public class TwoSum {
    // Main solution - HashMap
    public int[] twoSum(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                return new int[] { map.get(complement), i };
            }
            map.put(nums[i], i);
        }

        return new int[0];
    }

    // Alternative solution - Brute force
    public int[] twoSumBruteForce(int[] nums, int target) {
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] + nums[j] == target) {
                    return new int[] { i, j };
                }
            }
        }
        return new int[0];
    }

    // Follow-up solution - Two pointers (requires sorting, changes indices)
    public int[] twoSumTwoPointers(int[] nums, int target) {
        int[][] numsWithIndex = new int[nums.length][2];
        for (int i = 0; i < nums.length; i++) {
            numsWithIndex[i] = new int[] { nums[i], i };
        }

        Arrays.sort(numsWithIndex, (a, b) -> a[0] - b[0]);

        int left = 0, right = nums.length - 1;
        while (left < right) {
            int sum = numsWithIndex[left][0] + numsWithIndex[right][0];
            if (sum == target) {
                return new int[] { numsWithIndex[left][1], numsWithIndex[right][1] };
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return new int[] {};
    }

     // Follow-up 2: Find all pairs
    public List<List<Integer>> findAllPairs(int[] nums, int target) {
        Map<Integer, Integer> map = new HashMap<>();
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if (map.containsKey(complement)) {
                result.add(Arrays.asList(map.get(complement), i));
            }
            map.put(nums[i], i);
        }
        return result;
    }

    public static void main(String[] args) {
        TwoSum solution = new TwoSum();

        // Test Case 1: Normal case
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 2, 7, 11, 15 }, 9))); // Expected: [0,1]

        // Test Case 2: Edge case - negative numbers
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 3, 2, 4 }, 6))); // Expected: [1,2]

        // Test Case 3: Corner case - duplicates
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 3, 3 }, 6))); // Expected: [0,1]

        // Test Case 4: Large numbers
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -1000000000, 1000000000 }, 0))); // Expected:
                                                                                                        // [0,1]

        // Test Case 5: Minimum input
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 1, 2 }, 3))); // Expected: [0,1]

        // Test Case 6: Special case - zero target
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -1, 1 }, 0))); // Expected: [0,1]

        // Test Case 7: All negatives
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -3, -2, -4 }, -6))); // Expected: [1,2]

        // Test Case 8: Large array
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 }, 17))); // Expected:
                                                                                                           // [7,8]

        // Test Case 9: Alternating signs
        System.out.println(Arrays.toString(solution.twoSum(new int[] { -5, 5, -10, 10 }, 0))); // Expected: [0,1]

        // Test Case 10: Same number different indices
        System.out.println(Arrays.toString(solution.twoSum(new int[] { 2, 5, 5, 11 }, 10))); // Expected: [1,2]
    }
}
