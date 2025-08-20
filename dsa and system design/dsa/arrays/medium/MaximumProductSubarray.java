package arrays.medium;

/**
 * LeetCode 152: Maximum Product Subarray
 * https://leetcode.com/problems/maximum-product-subarray/
 *
 * Description:
 * Given an integer array nums, find a contiguous non-empty subarray within the
 * array that has the largest product,
 * and return the product.
 *
 * Constraints:
 * - 1 <= nums.length <= 2 * 10^4
 * - -10 <= nums[i] <= 10
 * - The product of any prefix or suffix of nums is guaranteed to fit in a
 * 32-bit integer
 *
 * Follow-up:
 * - Can you solve it in O(n) time and O(1) space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Track both maximum and minimum products ending at each position
 * 2. Handle negative numbers by swapping max and min when current number is
 * negative
 * 3. Reset to current number when previous product becomes 0
 */
public class MaximumProductSubarray {
    public int maxProduct(int[] nums) {
        if (nums.length == 0)
            return 0;

        int maxProduct = nums[0];
        int currentMax = nums[0];
        int currentMin = nums[0];

        for (int i = 1; i < nums.length; i++) {
            if (nums[i] < 0) {
                // Swap max and min when multiplying by negative
                int temp = currentMax;
                currentMax = currentMin;
                currentMin = temp;
            }

            currentMax = Math.max(nums[i], currentMax * nums[i]);
            currentMin = Math.min(nums[i], currentMin * nums[i]);

            maxProduct = Math.max(maxProduct, currentMax);
        }

        return maxProduct;
    }

    public static void main(String[] args) {
        MaximumProductSubarray solution = new MaximumProductSubarray();

        // Test Case 1: Normal case
        System.out.println(solution.maxProduct(new int[] { 2, 3, -2, 4 })); // Expected: 6

        // Test Case 2: Edge case - all negative
        System.out.println(solution.maxProduct(new int[] { -2, 0, -1 })); // Expected: 0

        // Test Case 3: Corner case - single element
        System.out.println(solution.maxProduct(new int[] { -2 })); // Expected: -2

        // Test Case 4: Large input - mixed signs
        System.out.println(solution.maxProduct(new int[] { -1, -3, -10, 0, 60 })); // Expected: 60

        // Test Case 5: Minimum input
        System.out.println(solution.maxProduct(new int[] { 1 })); // Expected: 1

        // Test Case 6: Special case - zeros
        System.out.println(solution.maxProduct(new int[] { 0, 2 })); // Expected: 2

        // Test Case 7: Boundary case - even negatives
        System.out.println(solution.maxProduct(new int[] { -1, -2, -3, -4 })); // Expected: 24

        // Test Case 8: Odd negatives
        System.out.println(solution.maxProduct(new int[] { -1, -2, -3 })); // Expected: 6

        // Test Case 9: Large positive product
        System.out.println(solution.maxProduct(new int[] { 2, 3, 4, 5 })); // Expected: 120

        // Test Case 10: Zero in middle
        System.out.println(solution.maxProduct(new int[] { 2, 3, 0, 4, 5 })); // Expected: 20
    }
}
