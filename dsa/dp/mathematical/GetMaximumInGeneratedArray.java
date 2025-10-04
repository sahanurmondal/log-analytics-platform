package dp.mathematical;

/**
 * LeetCode 1646: Get Maximum in Generated Array
 * https://leetcode.com/problems/get-maximum-in-generated-array/
 *
 * Description:
 * You are given an integer n. A 0-indexed integer array nums of length n + 1 is
 * generated in the following way:
 * - nums[0] = 0
 * - nums[1] = 1
 * - nums[2 * i] = nums[i] when 2 <= 2 * i <= n
 * - nums[2 * i + 1] = nums[i] + nums[i + 1] when 2 <= 2 * i + 1 <= n
 * Return the maximum integer in the array nums.
 *
 * Constraints:
 * - 0 <= n <= 100
 *
 * Company Tags: Google
 * Difficulty: Easy
 */
public class GetMaximumInGeneratedArray {

    // Approach 1: Simulation - O(n) time, O(n) space
    public int getMaximumGenerated(int n) {
        if (n == 0)
            return 0;
        if (n == 1)
            return 1;

        int[] nums = new int[n + 1];
        nums[0] = 0;
        nums[1] = 1;

        int max = 1;

        for (int i = 2; i <= n; i++) {
            if (i % 2 == 0) {
                nums[i] = nums[i / 2];
            } else {
                nums[i] = nums[i / 2] + nums[i / 2 + 1];
            }
            max = Math.max(max, nums[i]);
        }

        return max;
    }

    // Approach 2: Space Optimized - O(n) time, O(1) space
    public int getMaximumGeneratedOptimized(int n) {
        if (n == 0)
            return 0;
        if (n == 1)
            return 1;

        int[] nums = new int[n + 1];
        nums[0] = 0;
        nums[1] = 1;

        int max = 1;

        for (int i = 1; i <= n / 2; i++) {
            if (2 * i <= n) {
                nums[2 * i] = nums[i];
                max = Math.max(max, nums[2 * i]);
            }

            if (2 * i + 1 <= n) {
                nums[2 * i + 1] = nums[i] + nums[i + 1];
                max = Math.max(max, nums[2 * i + 1]);
            }
        }

        return max;
    }

    public static void main(String[] args) {
        GetMaximumInGeneratedArray solution = new GetMaximumInGeneratedArray();

        System.out.println("=== Get Maximum in Generated Array Test Cases ===");

        for (int n = 0; n <= 15; n++) {
            System.out.println("n = " + n + ": " + solution.getMaximumGenerated(n));
        }
    }
}
