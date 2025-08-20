package bitmanipulation.medium;

import java.util.*;

/**
 * LeetCode 137: Single Number II
 * https://leetcode.com/problems/single-number-ii/
 *
 * Description: Given an integer array nums where every element appears exactly
 * three times except for one, which appears exactly once. Find the single
 * element and return it.
 * You must implement a solution with linear runtime complexity and use constant
 * extra space.
 * 
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - -2^31 <= nums[i] <= 2^31 - 1
 * - Each element in nums appears exactly three times except for one element
 * which appears once
 *
 * Follow-up:
 * - Can you solve it using bit manipulation only?
 * - What about using digital circuit logic?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class SingleNumberII {

    // Main optimized solution - Bit manipulation with two variables
    public int singleNumber(int[] nums) {
        int ones = 0, twos = 0;

        for (int num : nums) {
            ones = (ones ^ num) & ~twos;
            twos = (twos ^ num) & ~ones;
        }

        return ones;
    }

    // Alternative solution - Count bits at each position
    public int singleNumberBitCount(int[] nums) {
        int result = 0;

        for (int i = 0; i < 32; i++) {
            int count = 0;
            for (int num : nums) {
                if ((num >> i & 1) == 1) {
                    count++;
                }
            }
            result |= (count % 3) << i;
        }

        return result;
    }

    // Alternative solution - Using HashMap
    public int singleNumberHashMap(int[] nums) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }

        return -1;
    }

    public static void main(String[] args) {
        SingleNumberII solution = new SingleNumberII();

        System.out.println(solution.singleNumber(new int[] { 2, 2, 3, 2 })); // Expected: 3
        System.out.println(solution.singleNumber(new int[] { 0, 1, 0, 1, 0, 1, 99 })); // Expected: 99
        System.out.println(solution.singleNumberBitCount(new int[] { 2, 2, 3, 2 })); // Expected: 3
        System.out.println(solution.singleNumberHashMap(new int[] { 0, 1, 0, 1, 0, 1, 99 })); // Expected: 99
    }
}
