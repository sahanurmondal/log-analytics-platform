package math.hard;

/**
 * LeetCode 372: Super Pow
 * https://leetcode.com/problems/super-pow/
 *
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description:
 * Compute a^b mod 1337, where b is a large array.
 *
 * Constraints:
 * - 1 <= a <= 2^31 - 1
 * - 1 <= b.length <= 2000
 * - 0 <= b[i] <= 9
 *
 * Follow-ups:
 * 1. Can you generalize for any mod?
 * 2. Can you handle negative exponents?
 * 3. Can you compute a^b mod m for multiple queries efficiently?
 */
public class SuperPow {
    public int superPow(int a, int[] b) {
        return superPowMod(a, b, 1337);
    }

    private int superPowMod(int a, int[] b, int mod) {
        a %= mod;
        int res = 1;
        for (int i = b.length - 1; i >= 0; i--) {
            res = (res * pow(a, b[i], mod)) % mod;
            a = pow(a, 10, mod);
        }
        return res;
    }

    private int pow(int a, int k, int mod) {
        int res = 1;
        a %= mod;
        for (int i = 0; i < k; i++)
            res = (res * a) % mod;
        return res;
    }

    // Follow-up 1: Generalize for any mod
    public int superPowGeneral(int a, int[] b, int mod) {
        return superPowMod(a, b, mod);
    }

    // Follow-up 2: Handle negative exponents
    public int superPowNegative(int a, int[] b, int mod) {
        int exp = 0;
        for (int digit : b)
            exp = exp * 10 + digit;
        if (exp == 0)
            return 1;
        int inv = modInverse(a, mod);
        int res = 1;
        for (int i = 0; i < exp; i++)
            res = (res * inv) % mod;
        return res;
    }

    private int modInverse(int a, int mod) {
        int m0 = mod, y = 0, x = 1;
        if (mod == 1)
            return 0;
        while (a > 1) {
            int q = a / mod;
            int t = mod;
            mod = a % mod;
            a = t;
            t = y;
            y = x - q * y;
            x = t;
        }
        if (x < 0)
            x += m0;
        return x;
    }

    // Follow-up 3: Multiple queries efficiently (precompute powers)
    public int[] superPowMultipleQueries(int a, int[][] queries, int mod) {
        int[] res = new int[queries.length];
        for (int i = 0; i < queries.length; i++)
            res[i] = superPowMod(a, queries[i], mod);
        return res;
    }

    public static void main(String[] args) {
        SuperPow solution = new SuperPow();
        System.out.println(solution.superPow(2, new int[] { 3 })); // 8
        System.out.println(solution.superPowGeneral(2, new int[] { 10 }, 1000)); // 24
        System.out.println(solution.superPowNegative(2, new int[] { 3 }, 1337)); // modular inverse
        System.out.println(
                java.util.Arrays.toString(solution.superPowMultipleQueries(2, new int[][] { { 3 }, { 10 } }, 1337))); // [8,1024]
    }
}
