package arrays.medium;

/**
 * LeetCode 198: House Robber
 * https://leetcode.com/problems/house-robber/
 *
 * Description:
 * You are a professional robber planning to rob houses along a street. Each
 * house has a certain amount of money stashed,
 * the only constraint stopping you from robbing each of them is that adjacent
 * houses have security systems connected
 * and it will automatically contact the police if two adjacent houses were
 * broken into on the same night.
 *
 * Constraints:
 * - 1 <= nums.length <= 100
 * - 0 <= nums[i] <= 400
 *
 * Follow-up:
 * - Can you solve it in O(1) space?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. Use two variables to track max money with/without robbing current house
 * 2. For each house, decide whether to rob or skip based on maximum profit
 * 3. Update variables for next iteration
 */
public class HouseRobber {
    public int rob(int[] nums) {
        if (nums.length == 0)
            return 0;
        if (nums.length == 1)
            return nums[0];

        int prevMax = 0;
        int currMax = 0;

        for (int num : nums) {
            int temp = currMax;
            currMax = Math.max(prevMax + num, currMax);
            prevMax = temp;
        }

        return currMax;
    }

    public static void main(String[] args) {
        HouseRobber solution = new HouseRobber();

        // Test Case 1: Normal case
        System.out.println(solution.rob(new int[] { 1, 2, 3, 1 })); // Expected: 4

        // Test Case 2: Edge case - alternating high values
        System.out.println(solution.rob(new int[] { 2, 7, 9, 3, 1 })); // Expected: 12

        // Test Case 3: Corner case - single house
        System.out.println(solution.rob(new int[] { 5 })); // Expected: 5

        // Test Case 4: Large input - increasing values
        System.out.println(solution.rob(new int[] { 5, 1, 3, 9, 4, 2, 6, 8 })); // Expected: 24

        // Test Case 5: Minimum input - two houses
        System.out.println(solution.rob(new int[] { 2, 1 })); // Expected: 2

        // Test Case 6: Special case - all zeros
        System.out.println(solution.rob(new int[] { 0, 0, 0, 0 })); // Expected: 0

        // Test Case 7: Boundary case - decreasing values
        System.out.println(solution.rob(new int[] { 9, 8, 7, 6, 5 })); // Expected: 21

        // Test Case 8: High first and last
        System.out.println(solution.rob(new int[] { 100, 1, 1, 100 })); // Expected: 200

        // Test Case 9: All same values
        System.out.println(solution.rob(new int[] { 3, 3, 3, 3 })); // Expected: 6

        // Test Case 10: Peak in middle
        System.out.println(solution.rob(new int[] { 1, 2, 10, 2, 1 })); // Expected: 12
    }
}
