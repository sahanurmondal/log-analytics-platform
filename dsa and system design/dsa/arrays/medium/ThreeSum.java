package arrays.medium;

import java.util.*;

/**
 * LeetCode 15: 3Sum
 * https://leetcode.com/problems/3sum/
 *
 * Description:
 * Given an integer array nums, return all the triplets [nums[i], nums[j],
 * nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] +
 * nums[k] == 0.
 *
 * Constraints:
 * - 3 <= nums.length <= 3000
 * - -10^5 <= nums[i] <= 10^5
 *
 * ASCII Art:
 * nums = [-1, 0, 1, 2, -1, -4]
 * After sorting: [-4, -1, -1, 0, 1, 2]
 * 
 * Fix first element: -1
 * Use two pointers for remaining: [-1, 0, 1, 2]
 * ↑ ↑
 * left right
 * -1 + (-1) + 2 = 0 ✓
 *
 * Follow-up:
 * - Can you solve it without using extra space for the result?
 * - Can you optimize to avoid duplicate triplets efficiently?
 * - Can you extend to k-sum?
 */
public class ThreeSum {

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result = new ArrayList<>();
        Arrays.sort(nums);

        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicate values for first element
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == 0) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));

                    // Skip duplicates for second and third elements
                    while (left < right && nums[left] == nums[left + 1])
                        left++;
                    while (left < right && nums[right] == nums[right - 1])
                        right--;

                    left++;
                    right--;
                } else if (sum < 0) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ThreeSum solution = new ThreeSum();

        // Test Case 1: Normal case
        System.out.println(solution.threeSum(new int[] { -1, 0, 1, 2, -1, -4 })); // Expected: [[-1,-1,2],[-1,0,1]]

        // Test Case 2: Edge case - no solution
        System.out.println(solution.threeSum(new int[] { 0, 1, 1 })); // Expected: []

        // Test Case 3: Corner case - no triplets
        System.out.println(solution.threeSum(new int[] { 1, 2, 3, 4, 5 })); // Expected: []

        // Test Case 4: Large input - all zeros
        System.out.println(solution.threeSum(new int[] { 0, 0, 0, 0 })); // Expected: [[0,0,0]]

        // Test Case 5: Minimum input - exactly 3 elements
        System.out.println(solution.threeSum(new int[] { -100000, 0, 100000 })); // Expected: [[-100000,0,100000]]

        // Test Case 6: Special case - duplicates
        System.out.println(solution.threeSum(new int[] { -2, 0, 0, 2, 2 })); // Expected: [[-2,0,2]]

        // Test Case 7: Boundary case - less than 3 elements
        System.out.println(solution.threeSum(new int[] { 1, 2 })); // Expected: []

        // Test Case 8: Negative numbers only
        System.out.println(solution.threeSum(new int[] { -3, -2, -1 })); // Expected: []

        // Test Case 9: Positive numbers only
        System.out.println(solution.threeSum(new int[] { 1, 2, 3 })); // Expected: []

        // Test Case 10: All same elements
        System.out.println(solution.threeSum(new int[] { 0, 0, 0 })); // Expected: [[0,0,0]]
    }
}
