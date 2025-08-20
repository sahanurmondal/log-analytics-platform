package bitmanipulation.hard;

/**
 * LeetCode 1879: Minimum XOR Sum of Two Arrays
 * https://leetcode.com/problems/minimum-xor-sum-of-two-arrays/
 *
 * Description: You are given two integer arrays nums1 and nums2 of length n.
 * The XOR sum of the two integer arrays is (nums1[0] XOR nums2[0]) + (nums1[1]
 * XOR nums2[1]) + ... + (nums1[n-1] XOR nums2[n-1]).
 * Rearrange the elements of nums2 such that the resulting XOR sum is minimized.
 * Return the minimized XOR sum.
 * 
 * Constraints:
 * - n == nums1.length == nums2.length
 * - 1 <= n <= 14
 * - 0 <= nums1[i], nums2[i] <= 10^7
 *
 * Follow-up:
 * - Can you solve it using dynamic programming with bitmasks?
 * - What about the time complexity?
 * 
 * Time Complexity: O(n * 2^n)
 * Space Complexity: O(2^n)
 * 
 * Company Tags: Google
 */
public class MinimumXORSumOfTwoArrays {

    // Main optimized solution - DP with bitmask
    public int minimumXORSum(int[] nums1, int[] nums2) {
        int n = nums1.length;
        int[] dp = new int[1 << n];

        for (int mask = 0; mask < (1 << n); mask++) {
            int bits = Integer.bitCount(mask);
            if (bits == 0)
                continue;

            dp[mask] = Integer.MAX_VALUE;

            // Try pairing nums1[bits-1] with each available element in nums2
            for (int j = 0; j < n; j++) {
                if ((mask & (1 << j)) != 0) {
                    int prevMask = mask ^ (1 << j);
                    dp[mask] = Math.min(dp[mask], dp[prevMask] + (nums1[bits - 1] ^ nums2[j]));
                }
            }
        }

        return dp[(1 << n) - 1];
    }

    // Alternative solution - Recursive with memoization
    private Integer[][] memo;

    public int minimumXORSumMemo(int[] nums1, int[] nums2) {
        int n = nums1.length;
        memo = new Integer[n][1 << n];
        return dfs(0, 0, nums1, nums2);
    }

    private int dfs(int i, int mask, int[] nums1, int[] nums2) {
        if (i == nums1.length)
            return 0;
        if (memo[i][mask] != null)
            return memo[i][mask];

        int result = Integer.MAX_VALUE;

        for (int j = 0; j < nums2.length; j++) {
            if ((mask & (1 << j)) == 0) {
                int newMask = mask | (1 << j);
                result = Math.min(result, (nums1[i] ^ nums2[j]) + dfs(i + 1, newMask, nums1, nums2));
            }
        }

        return memo[i][mask] = result;
    }

    public static void main(String[] args) {
        MinimumXORSumOfTwoArrays solution = new MinimumXORSumOfTwoArrays();

        System.out.println(solution.minimumXORSum(new int[] { 1, 2 }, new int[] { 2, 3 })); // Expected: 2
        System.out.println(solution.minimumXORSum(new int[] { 1, 0, 3 }, new int[] { 5, 3, 4 })); // Expected: 8
        System.out.println(solution.minimumXORSumMemo(new int[] { 1, 2 }, new int[] { 2, 3 })); // Expected: 2
    }
}
