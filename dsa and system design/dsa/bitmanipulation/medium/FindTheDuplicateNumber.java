package bitmanipulation.medium;

/**
 * LeetCode 287: Find the Duplicate Number
 * https://leetcode.com/problems/find-the-duplicate-number/
 *
 * Description: Given an array of integers nums containing n + 1 integers where
 * each integer is in the range [1, n] inclusive.
 * There is only one repeated number in nums, return this repeated number.
 * 
 * Constraints:
 * - 1 <= n <= 10^5
 * - nums.length == n + 1
 * - 1 <= nums[i] <= n
 * - All the integers in nums appear only once except for one integer which
 * appears two or more times
 *
 * Follow-up:
 * - Can you solve it using bit manipulation?
 * - What about Floyd's cycle detection?
 * 
 * Time Complexity: O(n log n) for bit manipulation, O(n) for Floyd's
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class FindTheDuplicateNumber {

    // Main optimized solution - Floyd's Cycle Detection
    public int findDuplicate(int[] nums) {
        int slow = nums[0];
        int fast = nums[0];

        // Find intersection point in the cycle
        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        } while (slow != fast);

        // Find the entrance to the cycle
        slow = nums[0];
        while (slow != fast) {
            slow = nums[slow];
            fast = nums[fast];
        }

        return slow;
    }

    // Alternative solution - Bit manipulation
    public int findDuplicateBitManipulation(int[] nums) {
        int n = nums.length - 1;
        int result = 0;

        for (int bit = 0; bit < 32; bit++) {
            int count1 = 0, count2 = 0;

            // Count 1s in the bit position for numbers 1 to n
            for (int i = 1; i <= n; i++) {
                if ((i & (1 << bit)) != 0) {
                    count1++;
                }
            }

            // Count 1s in the bit position for array elements
            for (int num : nums) {
                if ((num & (1 << bit)) != 0) {
                    count2++;
                }
            }

            // If count2 > count1, the duplicate has this bit set
            if (count2 > count1) {
                result |= (1 << bit);
            }
        }

        return result;
    }

    // Alternative solution - Binary search
    public int findDuplicateBinarySearch(int[] nums) {
        int left = 1, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;
            int count = 0;

            for (int num : nums) {
                if (num <= mid) {
                    count++;
                }
            }

            if (count <= mid) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    public static void main(String[] args) {
        FindTheDuplicateNumber solution = new FindTheDuplicateNumber();

        System.out.println(solution.findDuplicate(new int[] { 1, 3, 4, 2, 2 })); // Expected: 2
        System.out.println(solution.findDuplicate(new int[] { 3, 1, 3, 4, 2 })); // Expected: 3
        System.out.println(solution.findDuplicateBitManipulation(new int[] { 1, 3, 4, 2, 2 })); // Expected: 2
        System.out.println(solution.findDuplicateBinarySearch(new int[] { 3, 1, 3, 4, 2 })); // Expected: 3
    }
}
