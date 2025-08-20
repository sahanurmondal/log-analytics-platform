package dp.hard;

import java.util.Arrays;

/**
 * LeetCode 1406: Stone Game III
 * https://leetcode.com/problems/stone-game-iii/
 *
 * Description:
 * Alice and Bob continue their games with piles of stones. There are several
 * stones arranged in a row,
 * and each stone has an associated value which is an integer given in the array
 * stoneValue.
 * Alice and Bob take turns, with Alice starting first. On each player's turn,
 * that player can take 1, 2, or 3 stones
 * from the first remaining stones in the row.
 * The score of each player is the sum of the values of the stones taken. The
 * score of each player is initially 0.
 * The objective of the game is to end with the highest score, and the winner is
 * the player with the highest score.
 * Assuming Alice and Bob play optimally, return "Alice" if Alice will win,
 * "Bob" if Bob will win, or "Tie" if they end in a tie.
 *
 * Constraints:
 * - 1 <= stoneValue.length <= 5 * 10^4
 * - -1000 <= stoneValue[i] <= 1000
 *
 * Follow-up:
 * - What if players can take k stones instead of 1, 2, or 3?
 * - Can you solve it in O(1) space?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Hard
 */
public class StoneGameIII {

    // Approach 1: Minimax with Memoization - O(n) time, O(n) space
    public String stoneGameIIIMemo(int[] stoneValue) {
        int n = stoneValue.length;
        Integer[] memo = new Integer[n];
        int diff = minimax(stoneValue, 0, memo);

        if (diff > 0)
            return "Alice";
        else if (diff < 0)
            return "Bob";
        else
            return "Tie";
    }

    private int minimax(int[] stoneValue, int index, Integer[] memo) {
        if (index >= stoneValue.length)
            return 0;

        if (memo[index] != null)
            return memo[index];

        int maxDiff = Integer.MIN_VALUE;
        int currentSum = 0;

        // Try taking 1, 2, or 3 stones
        for (int i = 0; i < 3 && index + i < stoneValue.length; i++) {
            currentSum += stoneValue[index + i];
            // Current player's gain minus opponent's best gain
            int diff = currentSum - minimax(stoneValue, index + i + 1, memo);
            maxDiff = Math.max(maxDiff, diff);
        }

        memo[index] = maxDiff;
        return maxDiff;
    }

    // Approach 2: Bottom-up DP - O(n) time, O(n) space
    public String stoneGameIIIDP(int[] stoneValue) {
        int n = stoneValue.length;
        int[] dp = new int[n + 3]; // Add padding for easier computation

        // Fill DP array from right to left
        for (int i = n - 1; i >= 0; i--) {
            int maxDiff = Integer.MIN_VALUE;
            int currentSum = 0;

            for (int j = 0; j < 3 && i + j < n; j++) {
                currentSum += stoneValue[i + j];
                maxDiff = Math.max(maxDiff, currentSum - dp[i + j + 1]);
            }

            dp[i] = maxDiff;
        }

        if (dp[0] > 0)
            return "Alice";
        else if (dp[0] < 0)
            return "Bob";
        else
            return "Tie";
    }

    // Approach 3: Space Optimized DP - O(n) time, O(1) space
    public String stoneGameIIIOptimized(int[] stoneValue) {
        int n = stoneValue.length;
        int dp0 = 0, dp1 = 0, dp2 = 0; // dp[i+1], dp[i+2], dp[i+3]

        for (int i = n - 1; i >= 0; i--) {
            int maxDiff = Integer.MIN_VALUE;
            int currentSum = 0;

            // Try taking 1 stone
            currentSum += stoneValue[i];
            maxDiff = Math.max(maxDiff, currentSum - dp0);

            // Try taking 2 stones
            if (i + 1 < n) {
                currentSum += stoneValue[i + 1];
                maxDiff = Math.max(maxDiff, currentSum - dp1);
            }

            // Try taking 3 stones
            if (i + 2 < n) {
                currentSum += stoneValue[i + 2];
                maxDiff = Math.max(maxDiff, currentSum - dp2);
            }

            // Shift values
            dp2 = dp1;
            dp1 = dp0;
            dp0 = maxDiff;
        }

        if (dp0 > 0)
            return "Alice";
        else if (dp0 < 0)
            return "Bob";
        else
            return "Tie";
    }

    // Approach 4: Suffix Sum Optimization - O(n) time, O(n) space
    public String stoneGameIIISuffixSum(int[] stoneValue) {
        int n = stoneValue.length;
        int[] suffixSum = new int[n + 1];

        // Calculate suffix sums
        for (int i = n - 1; i >= 0; i--) {
            suffixSum[i] = suffixSum[i + 1] + stoneValue[i];
        }

        int[] dp = new int[n + 3];

        for (int i = n - 1; i >= 0; i--) {
            dp[i] = Integer.MIN_VALUE;

            for (int k = 1; k <= 3 && i + k <= n; k++) {
                // Take k stones: gain sum of k stones, opponent gets dp[i+k] from remaining
                int gain = suffixSum[i] - suffixSum[i + k] - dp[i + k];
                dp[i] = Math.max(dp[i], gain);
            }
        }

        int aliceScore = dp[0];
        int totalSum = suffixSum[0];
        int bobScore = totalSum - aliceScore;

        if (aliceScore > bobScore)
            return "Alice";
        else if (aliceScore < bobScore)
            return "Bob";
        else
            return "Tie";
    }

    // Approach 5: Alternative Minimax - O(n) time, O(n) space
    public String stoneGameIIIAlternative(int[] stoneValue) {
        int n = stoneValue.length;
        Integer[] memo = new Integer[n];

        int totalSum = Arrays.stream(stoneValue).sum();
        int aliceScore = solve(stoneValue, 0, memo);
        int bobScore = totalSum - aliceScore;

        if (aliceScore > bobScore)
            return "Alice";
        else if (aliceScore < bobScore)
            return "Bob";
        else
            return "Tie";
    }

    private int solve(int[] stoneValue, int index, Integer[] memo) {
        if (index >= stoneValue.length)
            return 0;

        if (memo[index] != null)
            return memo[index];

        int maxScore = 0;
        int currentSum = 0;

        for (int i = 0; i < 3 && index + i < stoneValue.length; i++) {
            currentSum += stoneValue[index + i];
            int remainingSum = Arrays.stream(stoneValue, index + i + 1, stoneValue.length).sum();
            int opponentScore = solve(stoneValue, index + i + 1, memo);
            int myScore = currentSum + (remainingSum - opponentScore);
            maxScore = Math.max(maxScore, myScore);
        }

        memo[index] = maxScore;
        return maxScore;
    }

    public static void main(String[] args) {
        StoneGameIII solution = new StoneGameIII();

        System.out.println("=== Stone Game III Test Cases ===");

        // Test Case 1: Example from problem
        int[] stoneValue1 = { 1, 2, 3, 7 };
        System.out.println("Test 1 - Array: " + Arrays.toString(stoneValue1));
        System.out.println("Memoization: " + solution.stoneGameIIIMemo(stoneValue1));
        System.out.println("DP: " + solution.stoneGameIIIDP(stoneValue1));
        System.out.println("Optimized: " + solution.stoneGameIIIOptimized(stoneValue1));
        System.out.println("Suffix Sum: " + solution.stoneGameIIISuffixSum(stoneValue1));
        System.out.println("Alternative: " + solution.stoneGameIIIAlternative(stoneValue1));
        System.out.println("Expected: Bob\n");

        // Test Case 2: Alice wins
        int[] stoneValue2 = { 1, 2, 3, -9 };
        System.out.println("Test 2 - Array: " + Arrays.toString(stoneValue2));
        System.out.println("Optimized: " + solution.stoneGameIIIOptimized(stoneValue2));
        System.out.println("Expected: Alice\n");

        // Test Case 3: Tie
        int[] stoneValue3 = { 1, 2, 3, 6 };
        System.out.println("Test 3 - Array: " + Arrays.toString(stoneValue3));
        System.out.println("Optimized: " + solution.stoneGameIIIOptimized(stoneValue3));
        System.out.println("Expected: Tie\n");

        performanceTest();
    }

    private static void performanceTest() {
        StoneGameIII solution = new StoneGameIII();

        int[] largeArray = new int[50000];
        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (int) (Math.random() * 2000) - 1000; // Random between -1000 and 1000
        }

        System.out.println("=== Performance Test (Array size: " + largeArray.length + ") ===");

        long start = System.nanoTime();
        String result1 = solution.stoneGameIIIMemo(largeArray);
        long end = System.nanoTime();
        System.out.println("Memoization: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        String result2 = solution.stoneGameIIIDP(largeArray);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        String result3 = solution.stoneGameIIIOptimized(largeArray);
        end = System.nanoTime();
        System.out.println("Optimized: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
