package dp.easy;

import java.util.*;

/**
 * LeetCode 983: Minimum Cost For Tickets
 * https://leetcode.com/problems/minimum-cost-for-tickets/
 *
 * Description:
 * You have planned some train traveling one year in advance. The days of the
 * year in which you will travel are given as an integer array days.
 * Each day is an integer from 1 to 365.
 * Train tickets are sold in three different ways:
 * - a 1-day pass is sold for costs[0] dollars,
 * - a 7-day pass is sold for costs[1] dollars, and
 * - a 30-day pass is sold for costs[2] dollars.
 * Return the minimum number of dollars you need to travel every day in the
 * given list of days.
 *
 * Constraints:
 * - 1 <= days.length <= 365
 * - 1 <= days[i] <= 365
 * - days is in strictly increasing order.
 * - costs.length == 3
 * - 1 <= costs[i] <= 1000
 *
 * Company Tags: Google, Amazon
 * Difficulty: Easy
 */
public class MinimumCostForTickets {

    // Approach 1: DP - O(365) time, O(365) space
    public int mincostTickets(int[] days, int[] costs) {
        Set<Integer> travelDays = new HashSet<>();
        for (int day : days) {
            travelDays.add(day);
        }

        int[] dp = new int[366];

        for (int i = 1; i <= 365; i++) {
            if (!travelDays.contains(i)) {
                dp[i] = dp[i - 1];
            } else {
                dp[i] = Math.min(
                        dp[i - 1] + costs[0],
                        Math.min(
                                dp[Math.max(0, i - 7)] + costs[1],
                                dp[Math.max(0, i - 30)] + costs[2]));
            }
        }

        return dp[365];
    }

    // Approach 2: Memoization - O(days.length) time, O(days.length) space
    public int mincostTicketsMemo(int[] days, int[] costs) {
        Integer[] memo = new Integer[days.length];
        return helper(days, costs, 0, memo);
    }

    private int helper(int[] days, int[] costs, int index, Integer[] memo) {
        if (index >= days.length)
            return 0;

        if (memo[index] != null)
            return memo[index];

        int day1 = costs[0] + helper(days, costs, index + 1, memo);

        int j = index;
        while (j < days.length && days[j] < days[index] + 7)
            j++;
        int day7 = costs[1] + helper(days, costs, j, memo);

        j = index;
        while (j < days.length && days[j] < days[index] + 30)
            j++;
        int day30 = costs[2] + helper(days, costs, j, memo);

        memo[index] = Math.min(day1, Math.min(day7, day30));
        return memo[index];
    }

    public static void main(String[] args) {
        MinimumCostForTickets solution = new MinimumCostForTickets();

        System.out.println("=== Minimum Cost For Tickets Test Cases ===");

        int[] days1 = { 1, 4, 6, 7, 8, 20 };
        int[] costs1 = { 2, 7, 15 };
        System.out.println("Days: " + Arrays.toString(days1));
        System.out.println("Costs: " + Arrays.toString(costs1));
        System.out.println("Min Cost: " + solution.mincostTickets(days1, costs1));
        System.out.println("Expected: 11\n");
    }
}
