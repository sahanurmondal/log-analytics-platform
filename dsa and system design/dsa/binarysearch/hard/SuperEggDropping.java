package binarysearch.hard;

/**
 * LeetCode 887: Super Egg Dropping
 * https://leetcode.com/problems/super-egg-dropping/
 *
 * Description:
 * You are given k identical eggs and you have access to a building with n
 * floors.
 * Each egg is identical and if an egg breaks, you cannot use it again.
 * You know that there exists a floor f where 0 <= f <= n such that any egg
 * dropped at a floor higher than f will break,
 * and any egg dropped at or below floor f will not break.
 * Each move, you may take an egg and drop it from any floor x (where 1 <= x <=
 * n).
 * If the egg breaks, you have k - 1 eggs left and must check floors 1 to x - 1.
 * If the egg doesn't break, you have k eggs left and must check floors x + 1 to
 * n.
 * Your goal is to find the minimum number of moves that you need to determine f
 * with certainty.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg, Uber,
 * Dropbox
 * Difficulty: Hard
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= k <= 100
 * - 1 <= n <= 10000
 *
 * Follow-ups:
 * - Can you solve this with O(kn) time complexity?
 * - What if eggs are not identical?
 * - How would you handle infinite floors?
 */
public class SuperEggDropping {

    // Binary Search + Memoization - O(k * n * log n) time, O(k * n) space
    public int superEggDrop(int k, int n) {
        Integer[][] memo = new Integer[k + 1][n + 1];
        return dp(k, n, memo);
    }

    private int dp(int k, int n, Integer[][] memo) {
        if (n <= 1 || k == 1) {
            return n;
        }

        if (memo[k][n] != null) {
            return memo[k][n];
        }

        int left = 1, right = n;
        int result = Integer.MAX_VALUE;

        // Binary search for optimal floor
        while (left <= right) {
            int mid = left + (right - left) / 2;

            // If egg breaks at floor mid
            int breakCase = dp(k - 1, mid - 1, memo);
            // If egg doesn't break at floor mid
            int noBreakCase = dp(k, n - mid, memo);

            // We take the worst case (maximum) and add 1 for current move
            int worst = 1 + Math.max(breakCase, noBreakCase);
            result = Math.min(result, worst);

            if (breakCase > noBreakCase) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        memo[k][n] = result;
        return result;
    }

    // Mathematical approach using inverse thinking - O(k * log n) time, O(k) space
    public int superEggDropMath(int k, int n) {
        // dp[m][k] = maximum floors we can check with m moves and k eggs
        int[][] dp = new int[n + 1][k + 1];

        int m = 0;
        while (dp[m][k] < n) {
            m++;
            for (int i = 1; i <= k; i++) {
                // dp[m][i] = dp[m-1][i-1] (egg breaks) + dp[m-1][i] (egg doesn't break) + 1
                dp[m][i] = dp[m - 1][i - 1] + dp[m - 1][i] + 1;
            }
        }

        return m;
    }

    // Optimized mathematical approach - O(k * log n) time, O(k) space
    public int superEggDropOptimized(int k, int n) {
        int[] dp = new int[k + 1];
        int m = 0;

        while (dp[k] < n) {
            m++;
            for (int i = k; i > 0; i--) {
                dp[i] = dp[i] + dp[i - 1] + 1;
            }
        }

        return m;
    }

    // Traditional DP approach - O(k * n^2) time, O(k * n) space
    public int superEggDropTraditional(int k, int n) {
        int[][] dp = new int[k + 1][n + 1];

        // Base cases
        for (int i = 1; i <= k; i++) {
            dp[i][0] = 0; // 0 floors, 0 moves
            dp[i][1] = 1; // 1 floor, 1 move
        }

        for (int j = 1; j <= n; j++) {
            dp[1][j] = j; // 1 egg, j floors -> j moves
        }

        // Fill the dp table
        for (int i = 2; i <= k; i++) {
            for (int j = 2; j <= n; j++) {
                dp[i][j] = Integer.MAX_VALUE;

                for (int x = 1; x <= j; x++) {
                    int worst = 1 + Math.max(dp[i - 1][x - 1], dp[i][j - x]);
                    dp[i][j] = Math.min(dp[i][j], worst);
                }
            }
        }

        return dp[k][n];
    }

    // Recursive with memoization (basic approach)
    public int superEggDropRecursive(int k, int n) {
        return superEggDropRecursiveHelper(k, n, new java.util.HashMap<>());
    }

    private int superEggDropRecursiveHelper(int k, int n, java.util.Map<String, Integer> memo) {
        if (n <= 1 || k == 1) {
            return n;
        }

        String key = k + "," + n;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int result = Integer.MAX_VALUE;

        for (int floor = 1; floor <= n; floor++) {
            int breakCase = superEggDropRecursiveHelper(k - 1, floor - 1, memo);
            int noBreakCase = superEggDropRecursiveHelper(k, n - floor, memo);
            int worst = 1 + Math.max(breakCase, noBreakCase);
            result = Math.min(result, worst);
        }

        memo.put(key, result);
        return result;
    }

    // Binary search optimization for traditional DP
    public int superEggDropBinarySearchDP(int k, int n) {
        int[][] dp = new int[k + 1][n + 1];

        // Base cases
        for (int i = 1; i <= k; i++) {
            dp[i][0] = 0;
            dp[i][1] = 1;
        }

        for (int j = 1; j <= n; j++) {
            dp[1][j] = j;
        }

        // Fill the dp table with binary search optimization
        for (int i = 2; i <= k; i++) {
            for (int j = 2; j <= n; j++) {
                dp[i][j] = Integer.MAX_VALUE;

                int left = 1, right = j;
                while (left <= right) {
                    int mid = left + (right - left) / 2;
                    int breakCase = dp[i - 1][mid - 1];
                    int noBreakCase = dp[i][j - mid];
                    int worst = 1 + Math.max(breakCase, noBreakCase);

                    dp[i][j] = Math.min(dp[i][j], worst);

                    if (breakCase > noBreakCase) {
                        right = mid - 1;
                    } else {
                        left = mid + 1;
                    }
                }
            }
        }

        return dp[k][n];
    }

    // Theoretical minimum moves calculation
    public int theoreticalMinimum(int k, int n) {
        // This gives the theoretical lower bound
        return (int) Math.ceil(Math.log(n + 1) / Math.log(2));
    }

    // Utility method to print the optimal strategy
    public java.util.List<Integer> getOptimalStrategy(int k, int n) {
        java.util.List<Integer> strategy = new java.util.ArrayList<>();
        Integer[][] memo = new Integer[k + 1][n + 1];

        getStrategyHelper(k, n, 1, n, memo, strategy);
        return strategy;
    }

    private void getStrategyHelper(int k, int n, int low, int high, Integer[][] memo,
            java.util.List<Integer> strategy) {
        if (n <= 1 || k == 1) {
            for (int i = low; i <= high; i++) {
                strategy.add(i);
            }
            return;
        }

        int left = low, right = high;
        int bestFloor = low;
        int minMoves = Integer.MAX_VALUE;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int breakCase = dp(k - 1, mid - low, memo);
            int noBreakCase = dp(k, high - mid, memo);
            int worst = 1 + Math.max(breakCase, noBreakCase);

            if (worst < minMoves) {
                minMoves = worst;
                bestFloor = mid;
            }

            if (breakCase > noBreakCase) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        strategy.add(bestFloor);
    }

    public static void main(String[] args) {
        SuperEggDropping solution = new SuperEggDropping();

        // Test Case 1: k = 1, n = 2
        System.out.println(solution.superEggDrop(1, 2)); // Expected: 2

        // Test Case 2: k = 2, n = 6
        System.out.println(solution.superEggDrop(2, 6)); // Expected: 3

        // Test Case 3: k = 3, n = 14
        System.out.println(solution.superEggDrop(3, 14)); // Expected: 4

        // Test Case 4: k = 2, n = 10
        System.out.println(solution.superEggDrop(2, 10)); // Expected: 4

        // Test Case 5: k = 1, n = 100 (linear search)
        System.out.println(solution.superEggDrop(1, 100)); // Expected: 100

        // Test Case 6: k = 100, n = 10 (many eggs, few floors)
        System.out.println(solution.superEggDrop(100, 10)); // Expected: 4 (binary search)

        // Test mathematical approach
        System.out.println("Math approach: " + solution.superEggDropMath(2, 6)); // Expected: 3

        // Test optimized approach
        System.out.println("Optimized: " + solution.superEggDropOptimized(2, 6)); // Expected: 3

        // Test traditional DP
        System.out.println("Traditional DP: " + solution.superEggDropTraditional(2, 6)); // Expected: 3

        // Test recursive approach (small values only due to exponential time)
        System.out.println("Recursive: " + solution.superEggDropRecursive(2, 5)); // Expected: 3

        // Test binary search DP
        System.out.println("Binary Search DP: " + solution.superEggDropBinarySearchDP(2, 6)); // Expected: 3

        // Theoretical minimum
        System.out.println("Theoretical minimum: " + solution.theoreticalMinimum(2, 6)); // Expected: 3

        // Edge cases
        System.out.println("Edge case k=1, n=1: " + solution.superEggDrop(1, 1)); // Expected: 1
        System.out.println("Edge case k=2, n=1: " + solution.superEggDrop(2, 1)); // Expected: 1
        System.out.println("Edge case k=1, n=0: " + solution.superEggDrop(1, 0)); // Expected: 0

        // Large test case
        System.out.println("Large test k=10, n=1000: " + solution.superEggDropOptimized(10, 1000)); // Expected: 10

        // Performance comparison
        long start = System.currentTimeMillis();
        solution.superEggDrop(5, 100);
        long time1 = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        solution.superEggDropOptimized(5, 100);
        long time2 = System.currentTimeMillis() - start;

        System.out.println("Binary search time: " + time1 + "ms");
        System.out.println("Mathematical time: " + time2 + "ms");

        // Get optimal strategy for small case
        java.util.List<Integer> strategy = solution.getOptimalStrategy(2, 6);
        System.out.println("Optimal strategy for k=2, n=6: " + strategy);
    }
}
