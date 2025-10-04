package greedy.medium;

/**
 * LeetCode 1326: Minimum Number of Taps to Open to Water a Garden
 * https://leetcode.com/problems/minimum-number-of-taps-to-open-to-water-a-garden/
 * 
 * Companies: Amazon, Google, Microsoft, Meta
 * Frequency: Medium (Asked in 30+ interviews)
 *
 * Description:
 * There is a one-dimensional garden on the x-axis. The garden starts at point 0
 * and ends at point n.
 * There are n + 1 taps located at points [0, 1, 2, ..., n]. Given an integer n
 * and integer array ranges,
 * where ranges[i] means the i-th tap can water the area [i - ranges[i], i +
 * ranges[i]] if it was open.
 * Return the minimum number of taps to open to water the whole garden, return
 * -1 if the garden cannot be watered.
 *
 * Constraints:
 * - 1 <= n <= 10^4
 * - ranges.length == n + 1
 * - 0 <= ranges[i] <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve it using dynamic programming?
 * 2. Can you solve it using greedy approach?
 * 3. How would you modify for 2D garden?
 */
public class MinimumNumberOfTapsToOpenToWaterGarden {

    // Approach 1: Greedy with Interval Coverage - O(n²) time, O(1) space
    public int minTaps(int n, int[] ranges) {
        int taps = 0;
        int currentEnd = 0;
        int farthest = 0;
        int i = 0;

        while (currentEnd < n) {
            // Find the tap that can cover currentEnd and extends farthest
            while (i <= n && i - ranges[i] <= currentEnd) {
                farthest = Math.max(farthest, i + ranges[i]);
                i++;
            }

            // If no progress made, garden cannot be watered
            if (farthest <= currentEnd) {
                return -1;
            }

            taps++;
            currentEnd = farthest;
        }

        return taps;
    }

    // Approach 2: Dynamic Programming - O(n²) time, O(n) space
    public int minTapsDP(int n, int[] ranges) {
        int[] dp = new int[n + 1];
        java.util.Arrays.fill(dp, Integer.MAX_VALUE);
        dp[0] = 0;

        for (int i = 0; i <= n; i++) {
            if (ranges[i] == 0)
                continue;

            int left = Math.max(0, i - ranges[i]);
            int right = Math.min(n, i + ranges[i]);

            for (int j = left; j <= right; j++) {
                if (dp[left] != Integer.MAX_VALUE) {
                    dp[j] = Math.min(dp[j], dp[left] + 1);
                }
            }
        }

        return dp[n] == Integer.MAX_VALUE ? -1 : dp[n];
    }

    // Approach 3: Optimized Greedy with Jump Game Logic - O(n) time, O(1) space
    public int minTapsOptimized(int n, int[] ranges) {
        int[] maxReach = new int[n + 1];

        // For each position, find the maximum reach
        for (int i = 0; i <= n; i++) {
            int left = Math.max(0, i - ranges[i]);
            int right = Math.min(n, i + ranges[i]);
            maxReach[left] = Math.max(maxReach[left], right);
        }

        int taps = 0;
        int currentEnd = 0;
        int farthest = 0;

        for (int i = 0; i < n; i++) {
            farthest = Math.max(farthest, maxReach[i]);

            if (i == currentEnd) {
                if (farthest <= currentEnd) {
                    return -1; // Cannot proceed further
                }
                taps++;
                currentEnd = farthest;
            }
        }

        return taps;
    }

    public static void main(String[] args) {
        MinimumNumberOfTapsToOpenToWaterGarden solution = new MinimumNumberOfTapsToOpenToWaterGarden();

        // Test Case 1: Basic example
        System.out.println("Basic: " + solution.minTaps(5, new int[] { 3, 4, 1, 1, 0, 0 })); // 1

        // Test Case 2: Impossible case
        System.out.println("Impossible: " + solution.minTaps(3, new int[] { 0, 0, 0, 0 })); // -1

        // Test Case 3: All taps cover full garden
        System.out.println("Full coverage: " + solution.minTaps(4, new int[] { 4, 4, 4, 4, 4 })); // 1

        // Test Case 4: Single tap solution
        System.out.println("Single tap: " + solution.minTaps(2, new int[] { 2, 0, 0 })); // 1

        // Test Case 5: Multiple taps needed
        System.out.println("Multiple taps: " + solution.minTaps(7, new int[] { 1, 2, 1, 0, 2, 1, 0, 1 })); // 3

        // Test Case 6: Edge - minimum garden
        System.out.println("Minimum: " + solution.minTaps(1, new int[] { 1, 1 })); // 1

        // Test Case 7: No coverage at start
        System.out.println("No start coverage: " + solution.minTaps(3, new int[] { 0, 1, 1, 1 })); // -1

        // Test Case 8: Gap in coverage
        System.out.println("Gap coverage: " + solution.minTaps(4, new int[] { 1, 0, 0, 0, 1 })); // -1

        // Test approaches comparison
        System.out.println("DP approach: " + solution.minTapsDP(5, new int[] { 3, 4, 1, 1, 0, 0 })); // 1
        System.out.println("Optimized: " + solution.minTapsOptimized(5, new int[] { 3, 4, 1, 1, 0, 0 })); // 1

        // Test Case 9: Large ranges
        System.out.println("Large ranges: " + solution.minTaps(8, new int[] { 4, 0, 0, 0, 0, 0, 0, 0, 4 })); // 2
    }
}
