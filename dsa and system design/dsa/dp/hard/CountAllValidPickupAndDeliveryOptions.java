package dp.hard;

/**
 * LeetCode 1359: Count All Valid Pickup and Delivery Options
 * https://leetcode.com/problems/count-all-valid-pickup-and-delivery-options/
 *
 * Description:
 * Given n orders, each order consists of pickup and delivery. Return the number
 * of valid sequences.
 *
 * Constraints:
 * - 1 <= n <= 500
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Company Tags: Google, Amazon
 * Difficulty: Hard
 */
public class CountAllValidPickupAndDeliveryOptions {

    private static final int MOD = 1000000007;

    // Approach 1: Mathematical Formula - O(n) time, O(1) space
    public int countOrders(int n) {
        long result = 1;

        for (int i = 1; i <= n; i++) {
            // For i orders, we have 2*i positions
            // We can place pickup in any of (2*i-1) positions
            // After placing pickup, we can place delivery in any of (2*i-1) remaining
            // positions
            // But we need delivery after pickup, so we have i valid positions for delivery
            result = (result * (2L * i - 1) * i) % MOD;
        }

        return (int) result;
    }

    // Approach 2: DP - O(n) time, O(n) space
    public int countOrdersDP(int n) {
        long[] dp = new long[n + 1];
        dp[0] = 1;
        dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            long spaces = 2L * i - 1;
            dp[i] = (dp[i - 1] * spaces * i) % MOD;
        }

        return (int) dp[n];
    }

    // Approach 3: Recursive with Memoization - O(n) time, O(n) space
    public int countOrdersMemo(int n) {
        Long[] memo = new Long[n + 1];
        return (int) countHelper(n, memo);
    }

    private long countHelper(int n, Long[] memo) {
        if (n == 0 || n == 1)
            return 1;

        if (memo[n] != null)
            return memo[n];

        long result = (countHelper(n - 1, memo) * (2L * n - 1) * n) % MOD;
        memo[n] = result;
        return result;
    }

    // Approach 4: Combinatorial Explanation - O(n) time, O(1) space
    public int countOrdersCombinatorial(int n) {
        // For n orders, we have 2n positions
        // We need to choose n positions for pickups: C(2n, n)
        // Then arrange deliveries: each delivery must come after its pickup
        // This gives us Catalan number pattern

        long result = 1;

        for (int i = 1; i <= n; i++) {
            // Number of ways to insert i-th order into existing sequence
            // We have 2*(i-1) + 1 = 2*i-1 positions
            // We can place pickup in any position, delivery must come after
            result = (result * (2L * i - 1) * i) % MOD;
        }

        return (int) result;
    }

    public static void main(String[] args) {
        CountAllValidPickupAndDeliveryOptions solution = new CountAllValidPickupAndDeliveryOptions();

        System.out.println("=== Count All Valid Pickup and Delivery Options Test Cases ===");

        // Test cases
        for (int n = 1; n <= 10; n++) {
            System.out.println("n = " + n + ":");
            System.out.println("  Formula: " + solution.countOrders(n));
            System.out.println("  DP: " + solution.countOrdersDP(n));
            System.out.println("  Memoization: " + solution.countOrdersMemo(n));
            System.out.println("  Combinatorial: " + solution.countOrdersCombinatorial(n));
        }

        System.out.println("\nExpected: n=1: 1, n=2: 6, n=3: 90, n=4: 2520, n=5: 113400");

        performanceTest();
    }

    private static void performanceTest() {
        CountAllValidPickupAndDeliveryOptions solution = new CountAllValidPickupAndDeliveryOptions();

        int n = 500;
        System.out.println("\n=== Performance Test (n = " + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.countOrders(n);
        long end = System.nanoTime();
        System.out.println("Formula: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.countOrdersDP(n);
        end = System.nanoTime();
        System.out.println("DP: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
