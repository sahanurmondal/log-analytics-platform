package math.medium;

/**
 * LeetCode 204: Count Primes
 * https://leetcode.com/problems/count-primes/
 *
 * Description:
 * Count the number of prime numbers less than a non-negative number n.
 *
 * Constraints:
 * - 0 <= n <= 5 * 10^6
 *
 * Follow-up:
 * - Can you solve it in O(n log log n) time?
 */
public class CountPrimes {
    public int countPrimes(int n) {
        if (n < 3)
            return 0;

        boolean[] isNotPrime = new boolean[n];
        int count = 0;

        for (int i = 2; i < n; i++) {
            if (!isNotPrime[i]) {
                count++;
                // Mark multiples of i as not prime
                for (long j = (long) i * i; j < n; j += i) {
                    isNotPrime[(int) j] = true;
                }
            }
        }

        return count;
    }

    public static void main(String[] args) {
        CountPrimes solution = new CountPrimes();
        // Edge Case 1: Normal case
        System.out.println(solution.countPrimes(10)); // 4
        // Edge Case 2: n = 0
        System.out.println(solution.countPrimes(0)); // 0
        // Edge Case 3: n = 1
        System.out.println(solution.countPrimes(1)); // 0
        // Edge Case 4: Large n
        System.out.println(solution.countPrimes(1000000)); // Should be large
    }
}
