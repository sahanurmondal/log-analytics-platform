package twopointers.medium;

/**
 * LeetCode 283: Move Zeroes
 * https://leetcode.com/problems/move-zeroes/
 *
 * Companies: Google, Facebook, Amazon
 * Frequency: High
 *
 * Description:
 * Move all zeroes to the end of the array while maintaining the relative order
 * of non-zero elements.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -2^31 <= nums[i] <= 2^31-1
 *
 * Follow-ups:
 * 1. Can you do it in-place?
 * 2. Can you minimize the number of operations?
 * 3. Can you handle negative numbers?
 */
public class MoveZeroes {
    public void moveZeroes(int[] nums) {
        int j = 0;
        for (int i = 0; i < nums.length; i++)
            if (nums[i] != 0)
                nums[j++] = nums[i];
        while (j < nums.length)
            nums[j++] = 0;
    }

    // Follow-up 1: In-place (already handled above)
    // Follow-up 2: Minimize operations (swap only when needed)
    public void moveZeroesMinOps(int[] nums) {
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                if (i != j) {
                    int tmp = nums[i];
                    nums[i] = nums[j];
                    nums[j] = tmp;
                }
                j++;
            }
        }
    }

    // Follow-up 3: Handle negative numbers (already handled above)

    public static void main(String[] args) {
        MoveZeroes solution = new MoveZeroes();
        // Basic case
        int[] nums1 = { 0, 1, 0, 3, 12 };
        solution.moveZeroes(nums1);
        System.out.println("Basic: " + java.util.Arrays.toString(nums1)); // [1,3,12,0,0]

        // Edge: All zeros
        int[] nums2 = { 0, 0, 0, 0 };
        solution.moveZeroes(nums2);
        System.out.println("All zeros: " + java.util.Arrays.toString(nums2)); // [0,0,0,0]

        // Edge: No zeros
        int[] nums3 = { 1, 2, 3, 4 };
        solution.moveZeroes(nums3);
        System.out.println("No zeros: " + java.util.Arrays.toString(nums3)); // [1,2,3,4]

        // Edge: Negative numbers
        int[] nums4 = { 0, -1, 0, -2, 3 };
        solution.moveZeroes(nums4);
        System.out.println("Negative numbers: " + java.util.Arrays.toString(nums4)); // [-1,-2,3,0,0]

        // Follow-up: Minimize operations
        int[] nums5 = { 0, 1, 0, 3, 12 };
        solution.moveZeroesMinOps(nums5);
        System.out.println("Min ops: " + java.util.Arrays.toString(nums5)); // [1,3,12,0,0]
    }
}
