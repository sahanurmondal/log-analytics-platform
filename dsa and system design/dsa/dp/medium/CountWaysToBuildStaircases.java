package dp.medium;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * LeetCode 1641: Count Sorted Vowel Strings (adapted as Count Ways To Build
 * Staircases)
 * Custom Problem: Count Ways To Build Staircases
 *
 * Description:
 * Given n identical cubes, count the number of ways to build a staircase where:
 * - Each step must have at least one cube
 * - Each step must have strictly fewer cubes than the step below it
 * - All n cubes must be used
 *
 * Constraints:
 * - 1 <= n <= 1000
 *
 * Follow-up:
 * - Can you solve it in O(n^2) time?
 * - What if steps can have equal numbers of cubes?
 * 
 * Company Tags: Google, Amazon, Microsoft, Facebook
 * Difficulty: Medium
 */
public class CountWaysToBuildStaircases {

    // Approach 1: Recursive with Memoization - O(n^3) time, O(n^2) space
    public int waysToMakeStaircase(int n) {
        Map<String, Integer> memo = new HashMap<>();
        return countWays(n, n - 1, memo);
    }

    private int countWays(int remaining, int maxStepSize, Map<String, Integer> memo) {
        if (remaining == 0)
            return 1;
        if (remaining < 0 || maxStepSize <= 0)
            return 0;

        String key = remaining + "," + maxStepSize;
        if (memo.containsKey(key))
            return memo.get(key);

        int ways = 0;

        // Try all possible step sizes from 1 to min(maxStepSize, remaining)
        for (int stepSize = 1; stepSize <= Math.min(maxStepSize, remaining); stepSize++) {
            ways += countWays(remaining - stepSize, stepSize - 1, memo);
        }

        memo.put(key, ways);
        return ways;
    }

    // Approach 2: 2D DP Bottom-up - O(n^3) time, O(n^2) space
    public int waysToMakeStaircaseDP(int n) {
        // dp[i][j] = number of ways to use i cubes with largest step size at most j
        int[][] dp = new int[n + 1][n + 1];

        // Base case: 0 cubes can be arranged in 1 way (empty staircase)
        for (int j = 0; j <= n; j++) {
            dp[0][j] = 1;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                // Don't use step of size j
                dp[i][j] = dp[i][j - 1];

                // Use step of size j (if possible)
                if (i >= j) {
                    dp[i][j] += dp[i - j][j - 1];
                }
            }
        }

        return dp[n][n - 1]; // Use n cubes with max step size n-1
    }

    // Approach 3: Space Optimized DP - O(n^3) time, O(n) space
    public int waysToMakeStaircaseOptimized(int n) {
        int[] prev = new int[n + 1];
        int[] curr = new int[n + 1];

        Arrays.fill(prev, 1); // Base case

        for (int i = 1; i <= n; i++) {
            curr[0] = 0; // Can't have max step size 0 with positive cubes

            for (int j = 1; j <= n; j++) {
                curr[j] = curr[j - 1]; // Don't use step size j

                if (i >= j) {
                    curr[j] += prev[j - 1]; // Use step size j
                }
            }

            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        return prev[n - 1];
    }

    // Approach 4: Mathematical Approach (Partition Function) - O(n^2) time, O(n^2)
    // space
    public int waysToMakeStaircaseMath(int n) {
        // This is equivalent to finding partitions of n into distinct parts
        int[][] dp = new int[n + 1][n + 1];

        for (int i = 0; i <= n; i++) {
            dp[0][i] = 1; // Empty partition
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= n; j++) {
                dp[i][j] = dp[i][j - 1]; // Don't include j

                if (i >= j) {
                    dp[i][j] += dp[i - j][j - 1]; // Include j
                }
            }
        }

        return dp[n][n] - 1; // Subtract 1 to exclude the partition with single part n
    }

    // Approach 5: Get All Valid Staircases - O(2^n) time, O(2^n) space
    public java.util.List<java.util.List<Integer>> getAllStaircases(int n) {
        java.util.List<java.util.List<Integer>> result = new java.util.ArrayList<>();
        java.util.List<Integer> current = new java.util.ArrayList<>();
        generateStaircases(n, n - 1, current, result);
        return result;
    }

    private void generateStaircases(int remaining, int maxStepSize,
            java.util.List<Integer> current,
            java.util.List<java.util.List<Integer>> result) {
        if (remaining == 0) {
            result.add(new java.util.ArrayList<>(current));
            return;
        }

        if (remaining < 0 || maxStepSize <= 0)
            return;

        // Try all possible step sizes
        for (int stepSize = Math.min(maxStepSize, remaining); stepSize >= 1; stepSize--) {
            current.add(stepSize);
            generateStaircases(remaining - stepSize, stepSize - 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static void main(String[] args) {
        CountWaysToBuildStaircases solution = new CountWaysToBuildStaircases();

        System.out.println("=== Count Ways To Build Staircases Test Cases ===");

        // Test Case 1: Small example
        int n1 = 4;
        System.out.println("Test 1 - n: " + n1);
        System.out.println("Recursive: " + solution.waysToMakeStaircase(n1));
        System.out.println("DP: " + solution.waysToMakeStaircaseDP(n1));
        System.out.println("Optimized: " + solution.waysToMakeStaircaseOptimized(n1));
        System.out.println("Mathematical: " + solution.waysToMakeStaircaseMath(n1));

        java.util.List<java.util.List<Integer>> staircases1 = solution.getAllStaircases(n1);
        System.out.println("All staircases:");
        for (java.util.List<Integer> staircase : staircases1) {
            System.out.println("  " + staircase);
        }
        System.out.println("Expected: 1 (only [3,1])\n");

        // Test Case 2: Larger example
        int n2 = 6;
        System.out.println("Test 2 - n: " + n2);
        System.out.println("DP: " + solution.waysToMakeStaircaseDP(n2));

        java.util.List<java.util.List<Integer>> staircases2 = solution.getAllStaircases(n2);
        System.out.println("All staircases:");
        for (java.util.List<Integer> staircase : staircases2) {
            System.out.println("  " + staircase);
        }
        System.out.println("Expected: 2 ([4,2], [3,2,1])\n");

        // Test Case 3: Edge case
        int n3 = 3;
        System.out.println("Test 3 - n: " + n3);
        System.out.println("DP: " + solution.waysToMakeStaircaseDP(n3));
        System.out.println("Expected: 1 ([2,1])\n");

        performanceTest();
    }

    private static void performanceTest() {
        CountWaysToBuildStaircases solution = new CountWaysToBuildStaircases();

        int n = 50;

        System.out.println("=== Performance Test (n: " + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.waysToMakeStaircaseDP(n);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.waysToMakeStaircaseOptimized(n);
        end = System.nanoTime();
        System.out.println("Optimized: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result3 = solution.waysToMakeStaircaseMath(n);
        end = System.nanoTime();
        System.out.println("Mathematical: " + result3 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
