package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 442: Find All Duplicates in an Array
 * https://leetcode.com/problems/find-all-duplicates-in-an-array/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 7+ interviews)
 *
 * Description: Given an integer array `nums` of length `n` where all the
 * integers of `nums` are in the range `[1, n]` and each integer appears once or
 * twice, return an array of all the integers that appear twice.
 * You must write an algorithm that runs in O(n) time and uses only constant
 * extra space.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 10^5
 * - 1 <= nums[i] <= n
 * - Each element in `nums` appears once or twice.
 * 
 * Follow-up Questions:
 * 1. Can you solve this without modifying the input array? (Using a HashSet)
 * 2. How does the in-place modification approach work?
 * 3. What if the numbers were not in the range `[1, n]`?
 */
public class FindAllDuplicatesInArray {

    // Approach 1: In-place modification (Cyclic Sort idea) - O(n) time, O(1) space
    public List<Integer> findDuplicates(int[] nums) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            int index = Math.abs(nums[i]) - 1;

            // If the number at this index is already negative, it means we've seen it
            // before.
            if (nums[index] < 0) {
                result.add(index + 1);
            } else {
                // Mark the number at this index as visited by making it negative.
                nums[index] = -nums[index];
            }
        }
        return result;
    }

    // Approach 2: Using a HashSet - O(n) time, O(n) space
    public List<Integer> findDuplicatesWithSet(int[] nums) {
        Set<Integer> seen = new HashSet<>();
        List<Integer> result = new ArrayList<>();
        for (int num : nums) {
            if (!seen.add(num)) {
                result.add(num);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        FindAllDuplicatesInArray solution = new FindAllDuplicatesInArray();

        // Test case 1
        int[] nums1 = { 4, 3, 2, 7, 8, 2, 3, 1 };
        System.out.println("Duplicates 1 (In-place): " + solution.findDuplicates(nums1)); // [2, 3]

        // Test case 2
        int[] nums2 = { 1, 1, 2 };
        System.out.println("Duplicates 2 (Set): " + solution.findDuplicatesWithSet(nums2)); // [1]

        // Test case 3
        int[] nums3 = { 1 };
        System.out.println("Duplicates 3: " + solution.findDuplicates(nums3)); // []

        // Test case 4: All elements appear twice
        int[] nums4 = { 2, 2, 1, 1 };
        System.out.println("Duplicates 4: " + solution.findDuplicates(nums4)); // [2, 1]
    }
}
