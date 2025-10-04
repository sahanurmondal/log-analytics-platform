package math.hard;

/**
 * LeetCode 204: Count Primes (Hard Variant)
 * https://leetcode.com/problems/count-primes/
 *
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description:
 * Count the number of prime numbers less than a non-negative number n.
 *
 * Constraints:
 * - 0 <= n <= 5 * 10^6
 *
 * Follow-ups:
 * 1. Can you count primes in a range [L, R] efficiently?
 * 2. Can you return the list of primes?
 * 3. Can you count twin primes?
 */
public class CountPrimesHard {
    public int countPrimes(int n) {
        if (n < 2)
            return 0;
        boolean[] isPrime = new boolean[n];
        java.util.Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int i = 2; i * i < n; i++) {
            if (isPrime[i]) {
                for (int j = i * i; j < n; j += i)
                    isPrime[j] = false;
            }
        }
        int count = 0;
        for (boolean p : isPrime)
            if (p)
                count++;
        return count;
    }

    // Follow-up 1: Count primes in [L, R] using segmented sieve
    public int countPrimesInRange(int L, int R) {
        int count = 0;
        boolean[] isPrime = sieve(R);
        for (int i = Math.max(L, 2); i <= R; i++)
            if (isPrime[i])
                count++;
        return count;
    }

    private boolean[] sieve(int n) {
        boolean[] isPrime = new boolean[n + 1];
        java.util.Arrays.fill(isPrime, true);
        isPrime[0] = isPrime[1] = false;
        for (int i = 2; i * i <= n; i++)
            if (isPrime[i])
                for (int j = i * i; j <= n; j += i)
                    isPrime[j] = false;
        return isPrime;
    }

    // Follow-up 2: Return list of primes
    public java.util.List<Integer> listPrimes(int n) {
        boolean[] isPrime = sieve(n - 1);
        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (int i = 2; i < n; i++)
            if (isPrime[i])
                res.add(i);
        return res;
    }

    // Follow-up 3: Count twin primes
    public int countTwinPrimes(int n) {
        boolean[] isPrime = sieve(n - 1);
        int count = 0;
        for (int i = 2; i < n - 2; i++)
            if (isPrime[i] && isPrime[i + 2])
                count++;
        return count;
    }

    public static void main(String[] args) {
        CountPrimesHard solution = new CountPrimesHard();
        System.out.println(solution.countPrimes(10)); // 4
        System.out.println(solution.countPrimesInRange(10, 20)); // 4
        System.out.println(solution.listPrimes(20)); // [2,3,5,7,11,13,17,19]
        System.out.println(solution.countTwinPrimes(20)); // 4
    }
}
