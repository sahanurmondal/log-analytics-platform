package dp.advanced;

import java.util.Arrays;

/**
 * LeetCode 879: Profitable Schemes
 * https://leetcode.com/problems/profitable-schemes/
 *
 * Description:
 * There is a group of n members, and a list of various crimes they could
 * commit.
 * The ith crime generates a profit[i] and requires group[i] members to
 * participate in it.
 * If a member participates in one crime, that member can't participate in
 * another crime.
 * Let's call a profitable scheme any subset of these crimes that generates at
 * least minProfit profit,
 * and the total number of members participating in that subset of crimes is at
 * most n.
 * Return the number of schemes that can be chosen. Since the answer may be very
 * large, return it modulo 10^9 + 7.
 *
 * Constraints:
 * - 1 <= n <= 100
 * - 0 <= minProfit <= 100
 * - 1 <= group.length == profit.length <= 100
 * - 0 <= group[i] <= 100
 * - 0 <= profit[i] <= 100
 *
 * Follow-up:
 * - Can you solve it in O(n * minProfit) space?
 * - What if we have negative profits?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class ProfitableSchemes {

    private static final int MOD = 1000000007;

    // Approach 1: 3D DP with Memoization - O(crimes * n * minProfit) time, O(crimes
    // * n * minProfit) space
    public int profitableSchemsMemo(int n, int minProfit, int[] group, int[] profit) {
        Integer[][][] memo = new Integer[group.length][n + 1][minProfit + 1];
        return profitableSchemsHelper(0, n, minProfit, group, profit, memo);
    }

    private int profitableSchemsHelper(int index, int remainingMembers, int remainingProfit,
            int[] group, int[] profit, Integer[][][] memo) {
        // Base case: no more crimes to consider
        if (index == group.length) {
            return remainingProfit <= 0 ? 1 : 0;
        }

        if (memo[index][remainingMembers][remainingProfit] != null) {
            return memo[index][remainingMembers][remainingProfit];
        }

        int result = 0;

        // Option 1: Skip current crime
        result = profitableSchemsHelper(index + 1, remainingMembers, remainingProfit, group, profit, memo);

        // Option 2: Take current crime (if we have enough members)
        if (group[index] <= remainingMembers) {
            int newRemainingProfit = Math.max(0, remainingProfit - profit[index]);
            result = (result + profitableSchemsHelper(index + 1, remainingMembers - group[index],
                    newRemainingProfit, group, profit, memo)) % MOD;
        }

        memo[index][remainingMembers][remainingProfit] = result;
        return result;
    }

    // Approach 2: Bottom-up 3D DP - O(crimes * n * minProfit) time, O(crimes * n *
    // minProfit) space
    public int profitableSchemes3D(int n, int minProfit, int[] group, int[] profit) {
        int crimes = group.length;
        int[][][] dp = new int[crimes + 1][n + 1][minProfit + 1];

        // Base case: no crimes, achieved minimum profit
        for (int members = 0; members <= n; members++) {
            dp[crimes][members][0] = 1;
        }

        // Fill DP table backwards
        for (int i = crimes - 1; i >= 0; i--) {
            for (int members = 0; members <= n; members++) {
                for (int profitNeeded = 0; profitNeeded <= minProfit; profitNeeded++) {
                    // Skip current crime
                    dp[i][members][profitNeeded] = dp[i + 1][members][profitNeeded];

                    // Take current crime
                    if (group[i] <= members) {
                        int newProfitNeeded = Math.max(0, profitNeeded - profit[i]);
                        dp[i][members][profitNeeded] = (dp[i][members][profitNeeded] +
                                dp[i + 1][members - group[i]][newProfitNeeded]) % MOD;
                    }
                }
            }
        }

        return dp[0][n][minProfit];
    }

    // Approach 3: Space Optimized 2D DP - O(crimes * n * minProfit) time, O(n *
    // minProfit) space
    public int profitableSchemesOptimized(int n, int minProfit, int[] group, int[] profit) {
        int[][] dp = new int[n + 1][minProfit + 1];

        // Base case: 0 profit needed
        for (int members = 0; members <= n; members++) {
            dp[members][0] = 1;
        }

        for (int i = 0; i < group.length; i++) {
            int[][] newDp = new int[n + 1][minProfit + 1];

            for (int members = 0; members <= n; members++) {
                for (int profitNeeded = 0; profitNeeded <= minProfit; profitNeeded++) {
                    // Skip current crime
                    newDp[members][profitNeeded] = dp[members][profitNeeded];

                    // Take current crime
                    if (group[i] <= members) {
                        int newProfitNeeded = Math.max(0, profitNeeded - profit[i]);
                        newDp[members][profitNeeded] = (newDp[members][profitNeeded] +
                                dp[members - group[i]][newProfitNeeded]) % MOD;
                    }
                }
            }

            dp = newDp;
        }

        return dp[n][minProfit];
    }

    // Approach 4: In-place DP - O(crimes * n * minProfit) time, O(n * minProfit)
    // space
    public int profitableSchemesInPlace(int n, int minProfit, int[] group, int[] profit) {
        int[][] dp = new int[n + 1][minProfit + 1];

        // Initialize base case
        for (int members = 0; members <= n; members++) {
            dp[members][0] = 1;
        }

        for (int i = 0; i < group.length; i++) {
            // Process in reverse order to avoid using updated values
            for (int members = n; members >= group[i]; members--) {
                for (int profitNeeded = minProfit; profitNeeded >= 0; profitNeeded--) {
                    int newProfitNeeded = Math.max(0, profitNeeded - profit[i]);
                    dp[members][profitNeeded] = (dp[members][profitNeeded] +
                            dp[members - group[i]][newProfitNeeded]) % MOD;
                }
            }
        }

        return dp[n][minProfit];
    }

    // Approach 5: DFS with Pruning - O(2^crimes) worst case, O(crimes) space
    public int profitableSchemesDFS(int n, int minProfit, int[] group, int[] profit) {
        return dfs(0, n, minProfit, group, profit);
    }

    private int dfs(int index, int remainingMembers, int remainingProfit, int[] group, int[] profit) {
        // Pruning: if remaining profit is 0 or negative, we found a valid scheme
        if (remainingProfit <= 0) {
            return countWays(index, remainingMembers, group);
        }

        // Base case: no more crimes
        if (index >= group.length) {
            return 0;
        }

        int result = 0;

        // Skip current crime
        result = dfs(index + 1, remainingMembers, remainingProfit, group, profit);

        // Take current crime
        if (group[index] <= remainingMembers) {
            result = (result + dfs(index + 1, remainingMembers - group[index],
                    remainingProfit - profit[index], group, profit)) % MOD;
        }

        return result;
    }

    private int countWays(int index, int remainingMembers, int[] group) {
        // Count ways to use remaining crimes with remaining members
        if (index >= group.length)
            return 1;

        int result = countWays(index + 1, remainingMembers, group); // Skip

        if (group[index] <= remainingMembers) {
            result = (result + countWays(index + 1, remainingMembers - group[index], group)) % MOD;
        }

        return result;
    }

    public static void main(String[] args) {
        ProfitableSchemes solution = new ProfitableSchemes();

        System.out.println("=== Profitable Schemes Test Cases ===");

        // Test Case 1: Example from problem
        int n1 = 5, minProfit1 = 3;
        int[] group1 = { 2, 2 };
        int[] profit1 = { 2, 3 };
        System.out.println("Test 1 - n: " + n1 + ", minProfit: " + minProfit1);
        System.out.println("Group: " + Arrays.toString(group1) + ", Profit: " + Arrays.toString(profit1));
        System.out.println("Memoization: " + solution.profitableSchemsMemo(n1, minProfit1, group1, profit1));
        System.out.println("3D DP: " + solution.profitableSchemes3D(n1, minProfit1, group1, profit1));
        System.out.println("Optimized: " + solution.profitableSchemesOptimized(n1, minProfit1, group1, profit1));
        System.out.println("In-place: " + solution.profitableSchemesInPlace(n1, minProfit1, group1, profit1));
        System.out.println("Expected: 2\n");

        // Test Case 2: Another example
        int n2 = 10, minProfit2 = 5;
        int[] group2 = { 2, 3, 5 };
        int[] profit2 = { 6, 7, 8 };
        System.out.println("Test 2 - n: " + n2 + ", minProfit: " + minProfit2);
        System.out.println("Group: " + Arrays.toString(group2) + ", Profit: " + Arrays.toString(profit2));
        System.out.println("Optimized: " + solution.profitableSchemesOptimized(n2, minProfit2, group2, profit2));
        System.out.println("Expected: 7\n");

        // Test Case 3: Edge case
        int n3 = 1, minProfit3 = 1;
        int[] group3 = { 1 };
        int[] profit3 = { 1 };
        System.out.println("Test 3 - n: " + n3 + ", minProfit: " + minProfit3);
        System.out.println("Group: " + Arrays.toString(group3) + ", Profit: " + Arrays.toString(profit3));
        System.out.println("Optimized: " + solution.profitableSchemesOptimized(n3, minProfit3, group3, profit3));
        System.out.println("Expected: 1\n");

        performanceTest();
    }

    private static void performanceTest() {
        ProfitableSchemes solution = new ProfitableSchemes();

        int n = 100, minProfit = 50;
        int[] group = new int[50];
        int[] profit = new int[50];

        for (int i = 0; i < 50; i++) {
            group[i] = (int) (Math.random() * 10) + 1;
            profit[i] = (int) (Math.random() * 20);
        }

        System.out.println("=== Performance Test (n: " + n + ", crimes: " + group.length + ") ===");

        long start = System.nanoTime();
        int result1 = solution.profitableSchemes3D(n, minProfit, group, profit);
        long end = System.nanoTime();
        System.out.println("3D DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.profitableSchemesOptimized(n, minProfit, group, profit);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.profitableSchemesInPlace(n, minProfit, group, profit);
        end = System.nanoTime();
        System.out.println("In-place: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
