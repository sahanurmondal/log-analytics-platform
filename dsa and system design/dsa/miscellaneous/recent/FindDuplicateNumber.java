package miscellaneous.recent;

/**
 * LeetCode 287: Find the Duplicate Number
 * https://leetcode.com/problems/find-the-duplicate-number/
 *
 * Description:
 * Given an array of integers nums containing n + 1 integers where each integer
 * is in the range [1, n] inclusive.
 * There is only one repeated number in nums, return this repeated number.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - nums.length == n + 1
 * - 1 <= nums[i] <= n
 * - All the integers in nums appear only once except for precisely one integer
 * which appears two or more times
 *
 * Follow-up:
 * - Can you solve it without modifying the array nums?
 * - Can you solve it using only constant extra space?
 * - Can you solve it with runtime complexity less than O(n^2)?
 * 
 * Companies: Amazon, Microsoft, Google
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class FindDuplicateNumber {

    public int findDuplicate(int[] nums) {
        // Floyd's Cycle Detection Algorithm
        int slow = nums[0];
        int fast = nums[0];

        // Find intersection point in the cycle
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Find entrance to the cycle
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }

        return slow;
    }

    public static void main(String[] args) {
        FindDuplicateNumber solution = new FindDuplicateNumber();
        int[] nums = { 1, 3, 4, 2, 2 };
        System.out.println(solution.findDuplicate(nums)); // 2
    }
}
