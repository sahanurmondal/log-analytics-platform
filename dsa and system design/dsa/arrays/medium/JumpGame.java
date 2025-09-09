package arrays.medium;

/**
 * LeetCode 55: Jump Game
 * https://leetcode.com/problems/jump-game/
 * * Description:
 * Given an array of non-negative integers nums, you are initially positioned at the first index of the array. Each element in the array represents your maximum jump length at that position. Determine if you are able to reach the last index.
 *
 * Input: int[] nums
 * Output: boolean (true if can reach last index)
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - 0 <= nums[i] <= 10^5
 *
 * Solution Approaches:
 * 1. Greedy (O(n) time, O(1) space)
 *    Steps:
 *      a. Track the farthest index reachable so far.
 *      b. If current index > farthest, return false.
 *      c. If farthest >= last index, return true.
 *    Time: O(n) for one pass.
 *    Space: O(1).
 *      - Example: nums = [2,3,1,1,4] → true
 * 2. DP (O(n^2) time, O(n) space)
 *    Steps:
 *      a. For each index, check if reachable from previous indices.
 *    Time: O(n^2).
 *    Space: O(n).
 */
public class JumpGame {
    // Main solution - Greedy approach
    public boolean canJump(int[] nums) {
        int maxReach = 0;

        for (int i = 0; i < nums.length; i++) {
            if (i > maxReach) {
                return false;
            }
            maxReach = Math.max(maxReach, i + nums[i]);
            if (maxReach >= nums.length - 1) {
                return true;
            }
        }

        return true;
    }

    // Alternative solution - Dynamic Programming
    public boolean canJumpDP(int[] nums) {
        boolean[] dp = new boolean[nums.length];
        dp[0] = true;

        for (int i = 0; i < nums.length; i++) {
            if (!dp[i])
                continue;

            for (int j = 1; j <= nums[i] && i + j < nums.length; j++) {
                dp[i + j] = true;
            }
        }

        return dp[nums.length - 1];
    }

    // Alternative solution - Backtracking from end
    public boolean canJumpBacktrack(int[] nums) {
        int lastGoodIndex = nums.length - 1;

        for (int i = nums.length - 2; i >= 0; i--) {
            if (i + nums[i] >= lastGoodIndex) {
                lastGoodIndex = i;
            }
        }

        return lastGoodIndex == 0;
    }

    public static void main(String[] args) {
        JumpGame solution = new JumpGame();
        // Edge Case 1: Normal case
        System.out.println(solution.canJump(new int[] { 2, 3, 1, 1, 4 })); // true
        // Edge Case 2: Cannot reach end
        System.out.println(solution.canJump(new int[] { 3, 2, 1, 0, 4 })); // false
        // Edge Case 3: Single element
        System.out.println(solution.canJump(new int[] { 0 })); // true
        // Edge Case 4: All zeros except first
        System.out.println(solution.canJump(new int[] { 1, 0, 0, 0 })); // false
        // Edge Case 5: Large input, all ones
        int[] large = new int[10000];
        for (int i = 0; i < 9999; i++)
            large[i] = 1;
        large[9999] = 0;
        System.out.println(solution.canJump(large)); // true
        // Edge Case 6: Large input, all zeros
        int[] zeros = new int[10000];
        System.out.println(solution.canJump(zeros)); // false
    }
}
