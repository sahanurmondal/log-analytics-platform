package math.hard;

/**
 * LeetCode 628: Maximum Product of Three Numbers
 * https://leetcode.com/problems/maximum-product-of-three-numbers/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Find the maximum product of any three numbers in an array.
 *
 * Constraints:
 * - 3 <= n <= 10^4
 * - -1000 <= nums[i] <= 1000
 *
 * Follow-ups:
 * 1. Can you do it in one pass?
 * 2. Can you handle overflow?
 * 3. Can you generalize to k numbers?
 */
public class MaximumProductOfThreeNumbers {
    public int maximumProduct(int[] nums) {
        int min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
        int max1 = Integer.MIN_VALUE, max2 = Integer.MIN_VALUE, max3 = Integer.MIN_VALUE;
        for (int n : nums) {
            if (n <= min1) {
                min2 = min1;
                min1 = n;
            } else if (n <= min2) {
                min2 = n;
            }
            if (n >= max1) {
                max3 = max2;
                max2 = max1;
                max1 = n;
            } else if (n >= max2) {
                max3 = max2;
                max2 = n;
            } else if (n >= max3) {
                max3 = n;
            }
        }
        return Math.max(min1 * min2 * max1, max1 * max2 * max3);
    }

    // Follow-up 1: One pass (already handled above)
    // Follow-up 2: Handle overflow (use long)
    public long maximumProductLong(int[] nums) {
        long min1 = Integer.MAX_VALUE, min2 = Integer.MAX_VALUE;
        long max1 = Integer.MIN_VALUE, max2 = Integer.MIN_VALUE, max3 = Integer.MIN_VALUE;
        for (int n : nums) {
            if (n <= min1) {
                min2 = min1;
                min1 = n;
            } else if (n <= min2) {
                min2 = n;
            }
            if (n >= max1) {
                max3 = max2;
                max2 = max1;
                max1 = n;
            } else if (n >= max2) {
                max3 = max2;
                max2 = n;
            } else if (n >= max3) {
                max3 = n;
            }
        }
        return Math.max(min1 * min2 * max1, max1 * max2 * max3);
    }

    // Follow-up 3: Generalize to k numbers
    public int maximumProductK(int[] nums, int k) {
        java.util.Arrays.sort(nums);
        int n = nums.length;
        int prod = 1;
        int i = 0, j = n - 1;
        if (k % 2 == 1) {
            prod *= nums[j--];
            k--;
        }
        while (k > 0) {
            int left = nums[i] * nums[i + 1];
            int right = nums[j] * nums[j - 1];
            if (left > right) {
                prod *= left;
                i += 2;
            } else {
                prod *= right;
                j -= 2;
            }
            k -= 2;
        }
        return prod;
    }

    public static void main(String[] args) {
        MaximumProductOfThreeNumbers solution = new MaximumProductOfThreeNumbers();
        System.out.println(solution.maximumProduct(new int[] { 1, 2, 3 })); // 6
        System.out.println(solution.maximumProduct(new int[] { -10, -10, 5, 2 })); // 500
        System.out.println(solution.maximumProductLong(new int[] { 100000, 100000, 100000 })); // 1000000000000
        System.out.println(solution.maximumProductK(new int[] { -10, -10, 5, 2 }, 3)); // 500
    }
}
