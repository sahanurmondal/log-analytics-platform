package dp.hard;

/**
 * LeetCode 568: Maximum Vacation Days
 * https://leetcode.com/problems/maximum-vacation-days/
 *
 * Description:
 * Given flights and days matrices, return the maximum vacation days you can
 * take.
 *
 * Constraints:
 * - 1 <= flights.length, flights[i].length <= 100
 * - 1 <= days.length, days[i].length <= 100
 * - 0 <= flights[i][j], days[i][j] <= 7
 *
 * Follow-up:
 * - Can you solve it in O(n^2*w) time?
 * 
 * Company Tags: Google, Amazon
 * Difficulty: Hard
 */
public class MaximumVacationDays {

    // Approach 1: DP - O(n^2*w) time, O(n*w) space
    public int maxVacationDays(int[][] flights, int[][] days) {
        int n = flights.length; // cities
        int w = days[0].length; // weeks

        // dp[i][j] = max vacation days staying at city i for week j
        int[][] dp = new int[n][w];

        // Initialize with impossible values
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < w; j++) {
                dp[i][j] = Integer.MIN_VALUE;
            }
        }

        // Week 0: can start at city 0 or fly to other cities
        dp[0][0] = days[0][0];
        for (int city = 1; city < n; city++) {
            if (flights[0][city] == 1) {
                dp[city][0] = days[city][0];
            }
        }

        // Fill remaining weeks
        for (int week = 1; week < w; week++) {
            for (int city = 0; city < n; city++) {
                // Stay in same city
                if (dp[city][week - 1] != Integer.MIN_VALUE) {
                    dp[city][week] = Math.max(dp[city][week], dp[city][week - 1] + days[city][week]);
                }

                // Fly from other cities
                for (int fromCity = 0; fromCity < n; fromCity++) {
                    if (fromCity != city && flights[fromCity][city] == 1
                            && dp[fromCity][week - 1] != Integer.MIN_VALUE) {
                        dp[city][week] = Math.max(dp[city][week], dp[fromCity][week - 1] + days[city][week]);
                    }
                }
            }
        }

        // Find maximum vacation days in last week
        int result = 0;
        for (int city = 0; city < n; city++) {
            result = Math.max(result, dp[city][w - 1]);
        }

        return result;
    }

    // Approach 2: Space Optimized DP - O(n^2*w) time, O(n) space
    public int maxVacationDaysOptimized(int[][] flights, int[][] days) {
        int n = flights.length;
        int w = days[0].length;

        int[] prev = new int[n];
        int[] curr = new int[n];

        // Initialize with impossible values
        for (int i = 0; i < n; i++) {
            prev[i] = Integer.MIN_VALUE;
        }

        // Week 0
        prev[0] = days[0][0];
        for (int city = 1; city < n; city++) {
            if (flights[0][city] == 1) {
                prev[city] = days[city][0];
            }
        }

        // Process remaining weeks
        for (int week = 1; week < w; week++) {
            for (int city = 0; city < n; city++) {
                curr[city] = Integer.MIN_VALUE;

                // Stay in same city
                if (prev[city] != Integer.MIN_VALUE) {
                    curr[city] = Math.max(curr[city], prev[city] + days[city][week]);
                }

                // Fly from other cities
                for (int fromCity = 0; fromCity < n; fromCity++) {
                    if (fromCity != city && flights[fromCity][city] == 1 && prev[fromCity] != Integer.MIN_VALUE) {
                        curr[city] = Math.max(curr[city], prev[fromCity] + days[city][week]);
                    }
                }
            }

            // Swap arrays
            int[] temp = prev;
            prev = curr;
            curr = temp;
        }

        int result = 0;
        for (int city = 0; city < n; city++) {
            result = Math.max(result, prev[city]);
        }

        return result;
    }

    public static void main(String[] args) {
        MaximumVacationDays solution = new MaximumVacationDays();

        System.out.println("=== Maximum Vacation Days Test Cases ===");

        // Test Case 1: Example from problem
        int[][] flights1 = { { 0, 1, 1 }, { 1, 0, 1 }, { 1, 1, 0 } };
        int[][] days1 = { { 1, 3, 1 }, { 6, 0, 3 }, { 3, 3, 3 } };
        System.out.println("Test 1:");
        System.out.println("DP: " + solution.maxVacationDays(flights1, days1));
        System.out.println("Optimized: " + solution.maxVacationDaysOptimized(flights1, days1));
        System.out.println("Expected: 12\n");

        // Test Case 2: No flights
        int[][] flights2 = { { 0, 0, 0 }, { 0, 0, 0 }, { 0, 0, 0 } };
        int[][] days2 = { { 1, 1, 1 }, { 7, 7, 7 }, { 7, 7, 7 } };
        System.out.println("Test 2 (No flights):");
        System.out.println("DP: " + solution.maxVacationDays(flights2, days2));
        System.out.println("Expected: 3\n");

        performanceTest();
    }

    private static void performanceTest() {
        MaximumVacationDays solution = new MaximumVacationDays();

        int n = 50, w = 50;
        int[][] flights = new int[n][n];
        int[][] days = new int[n][w];

        // Create random test case
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                flights[i][j] = (i != j && Math.random() < 0.3) ? 1 : 0;
            }
            for (int j = 0; j < w; j++) {
                days[i][j] = (int) (Math.random() * 8);
            }
        }

        System.out.println("=== Performance Test (Cities: " + n + ", Weeks: " + w + ") ===");

        long start = System.nanoTime();
        int result = solution.maxVacationDaysOptimized(flights, days);
        long end = System.nanoTime();
        System.out.println("Optimized: " + result + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
