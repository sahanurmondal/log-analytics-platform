package arrays.medium;

/**
 * LeetCode 238: Product of Array Except Self
 * https://leetcode.com/problems/product-of-array-except-self/
 *
 * Description:
 * Given an array nums of n integers where n > 1, return an array output such
 * that output[i] is equal to the product of all the elements of nums except
 * nums[i].
 * Solve it without division and in O(n).
 *
 * Constraints:
 * - 2 <= nums.length <= 10^5
 * - -30 <= nums[i] <= 30
 *
 * Follow-up:
 * - Can you do it in O(1) extra space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1) - excluding output array
 * 
 * Algorithm:
 * 1. First pass: Calculate left products for each position
 * 2. Second pass: Calculate right products and multiply with left products
 */
public class ProductOfArrayExceptSelf {
    public int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];

        // First pass: calculate left products
        result[0] = 1;
        for (int i = 1; i < n; i++) {
            result[i] = result[i - 1] * nums[i - 1];
        }

        // Second pass: calculate right products and multiply with left products
        int rightProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            result[i] *= rightProduct;
            rightProduct *= nums[i];
        }

        return result;
    }

    public static void main(String[] args) {
        ProductOfArrayExceptSelf solution = new ProductOfArrayExceptSelf();
        // Edge Case 1: Normal case
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { 1, 2, 3, 4 }))); // [24,12,8,6]
        // Edge Case 2: Contains zero
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { 0, 1, 2, 3 }))); // [6,0,0,0]
        // Edge Case 3: Multiple zeros
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { 0, 0, 2, 3 }))); // [0,0,0,0]
        // Edge Case 4: Negative numbers
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { -1, 2, -3, 4 }))); // [-24,12,-8,6]
        // Edge Case 5: All ones
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { 1, 1, 1, 1 }))); // [1,1,1,1]
        // Edge Case 6: Large input
        // int[] large = new int[100000]; java.util.Arrays.fill(large, 2);
        // System.out.println(java.util.Arrays.toString(solution.productExceptSelf(large)));
        // Edge Case 7: Single negative
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { -1, 1, 1, 1 }))); // [1,-1,-1,-1]
        // Edge Case 8: Two elements
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { 2, 3 }))); // [3,2]
        // Edge Case 9: Alternating sign
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { -1, 2, -3, 4, -5 }))); // [-120,60,-40,30,-24]
        // Edge Case 10: All zeros
        System.out.println(java.util.Arrays.toString(solution.productExceptSelf(new int[] { 0, 0, 0, 0 }))); // [0,0,0,0]
    }
}
