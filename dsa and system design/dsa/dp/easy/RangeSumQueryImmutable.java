package dp.easy;

/**
 * LeetCode 303: Range Sum Query - Immutable
 * https://leetcode.com/problems/range-sum-query-immutable/
 *
 * Description:
 * Given an integer array nums, handle multiple queries of the following type:
 * Calculate the sum of the elements of nums between indices left and right
 * inclusive where left <= right.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^5 <= nums[i] <= 10^5
 * - 0 <= left <= right < nums.length
 * - At most 10^4 calls will be made to sumRange.
 *
 * Company Tags: Google, Amazon, Microsoft
 * Difficulty: Easy
 */
public class RangeSumQueryImmutable {

    private int[] prefixSum;

    // Constructor - O(n) time, O(n) space
    public RangeSumQueryImmutable(int[] nums) {
        prefixSum = new int[nums.length + 1];

        for (int i = 0; i < nums.length; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }
    }

    // Query - O(1) time, O(1) space
    public int sumRange(int left, int right) {
        return prefixSum[right + 1] - prefixSum[left];
    }

    public static void main(String[] args) {
        int[] nums = { -2, 0, 3, -5, 2, -1 };
        RangeSumQueryImmutable solution = new RangeSumQueryImmutable(nums);

        System.out.println("=== Range Sum Query Test Cases ===");
        System.out.println("Array: " + java.util.Arrays.toString(nums));

        System.out.println("sumRange(0, 2) = " + solution.sumRange(0, 2)); // Expected: 1
        System.out.println("sumRange(2, 5) = " + solution.sumRange(2, 5)); // Expected: -1
        System.out.println("sumRange(0, 5) = " + solution.sumRange(0, 5)); // Expected: -3
    }
}
