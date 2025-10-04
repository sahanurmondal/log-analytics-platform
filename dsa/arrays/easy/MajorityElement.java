package arrays.easy;

import java.util.*;

/**
 * LeetCode 169: Majority Element
 * https://leetcode.com/problems/majority-element/
 *
 * Description:
 * Given an array nums of size n, return the majority element.
 * The majority element is the element that appears more than ⌊n / 2⌋ times.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 5 * 10^4
 * - -2^31 <= nums[i] <= 2^31 - 1
 *
 * Follow-up:
 * - Could you solve the problem in linear time and in O(1) space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 */
public class MajorityElement {

    // Main solution - Boyer-Moore Voting Algorithm
    public int majorityElement(int[] nums) {
        int candidate = nums[0];
        int count = 1;

        for (int i = 1; i < nums.length; i++) {
            if (count == 0) {
                candidate = nums[i];
                count = 1;
            } else if (nums[i] == candidate) {
                count++;
            } else {
                count--;
            }
        }

        return candidate;
    }

    // Alternative solution - HashMap
    public int majorityElementHashMap(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();
        int majority = nums.length / 2;

        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
            if (map.get(num) > majority) {
                return num;
            }
        }

        return -1;
    }

    // Alternative solution - Sorting
    public int majorityElementSort(int[] nums) {
        Arrays.sort(nums);
        return nums[nums.length / 2];
    }

    // Follow-up solution - Randomization
    public int majorityElementRandom(int[] nums) {
        Random rand = new Random();
        int majority = nums.length / 2;

        while (true) {
            int candidate = nums[rand.nextInt(nums.length)];
            int count = 0;
            for (int num : nums) {
                if (num == candidate)
                    count++;
            }
            if (count > majority)
                return candidate;
        }
    }

    public static void main(String[] args) {
        MajorityElement solution = new MajorityElement();

        // Test Case 1: Normal case
        System.out.println(solution.majorityElement(new int[] { 3, 2, 3 })); // Expected: 3

        // Test Case 2: Edge case - all same
        System.out.println(solution.majorityElement(new int[] { 2, 2, 1, 1, 1, 2, 2 })); // Expected: 2

        // Test Case 3: Corner case - single element
        System.out.println(solution.majorityElement(new int[] { 1 })); // Expected: 1

        // Test Case 4: Large input - majority at start
        System.out.println(solution.majorityElement(new int[] { 1, 1, 1, 2, 3 })); // Expected: 1

        // Test Case 5: Majority at end
        System.out.println(solution.majorityElement(new int[] { 1, 2, 3, 3, 3 })); // Expected: 3

        // Test Case 6: Special case - negative numbers
        System.out.println(solution.majorityElement(new int[] { -1, -1, -1, 1, 1 })); // Expected: -1

        // Test Case 7: Two elements
        System.out.println(solution.majorityElement(new int[] { 1, 2, 1 })); // Expected: 1

        // Test Case 8: Large majority
        System.out.println(solution.majorityElement(new int[] { 5, 5, 5, 5, 1, 2, 3 })); // Expected: 5

        // Test Case 9: Minimum majority
        System.out.println(solution.majorityElement(new int[] { 1, 2, 1, 2, 1 })); // Expected: 1

        // Test Case 10: All same elements
        System.out.println(solution.majorityElement(new int[] { 7, 7, 7, 7, 7 })); // Expected: 7
    }
}
