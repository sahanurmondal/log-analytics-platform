package dp.easy;

/**
 * LeetCode 17.01: The Masseuse LCCI
 * https://leetcode.com/problems/the-masseuse-lcci/
 *
 * Description:
 * A popular masseuse receives a sequence of back-to-back appointment requests
 * and is debating which ones to accept.
 * She needs a 15-minute break between appointments and therefore she cannot
 * accept any two appointments that are adjacent to each other.
 * Given a sequence of back-to-back appointment requests (all multiples of 15
 * minutes, none overlap, and none can be moved),
 * find the optimal (highest total booked minutes) set the masseuse can honor.
 * Return the number of minutes.
 *
 * Constraints:
 * - 0 <= nums.length <= 10^6
 * - 1 <= nums[i] <= 10^6
 *
 * Company Tags: Facebook
 * Difficulty: Easy
 */
public class MasseuseLCCI {

    // Approach 1: Space Optimized DP - O(n) time, O(1) space
    public int massage(int[] nums) {
        if (nums.length == 0)
            return 0;
        if (nums.length == 1)
            return nums[0];

        int prev2 = 0; // dp[i-2]
        int prev1 = nums[0]; // dp[i-1]

        for (int i = 1; i < nums.length; i++) {
            int current = Math.max(prev1, prev2 + nums[i]);
            prev2 = prev1;
            prev1 = current;
        }

        return prev1;
    }

    // Approach 2: DP Array - O(n) time, O(n) space
    public int massageDP(int[] nums) {
        if (nums.length == 0)
            return 0;
        if (nums.length == 1)
            return nums[0];

        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        dp[1] = Math.max(nums[0], nums[1]);

        for (int i = 2; i < nums.length; i++) {
            dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
        }

        return dp[nums.length - 1];
    }

    public static void main(String[] args) {
        MasseuseLCCI solution = new MasseuseLCCI();

        System.out.println("=== The Masseuse LCCI Test Cases ===");

        int[] nums1 = { 1, 2, 3, 1 };
        System.out.println("Appointments: " + java.util.Arrays.toString(nums1));
        System.out.println("Max Minutes: " + solution.massage(nums1));
        System.out.println("Expected: 4\n");

        int[] nums2 = { 2, 7, 9, 3, 1 };
        System.out.println("Appointments: " + java.util.Arrays.toString(nums2));
        System.out.println("Max Minutes: " + solution.massage(nums2));
        System.out.println("Expected: 12\n");
    }
}
