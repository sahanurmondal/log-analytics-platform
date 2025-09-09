package dp.advanced;

/**
 * LeetCode 1220: Count Vowels Permutation
 * https://leetcode.com/problems/count-vowels-permutation/
 *
 * Description:
 * Given n, return the number of strings of length n that consist only of vowels
 * and are lexicographically sorted.
 *
 * Constraints:
 * - 1 <= n <= 2 * 10^4
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 * 
 * Company Tags: Google, Amazon
 * Difficulty: Hard
 */
public class CountVowelsPermutation {

    private static final int MOD = 1000000007;

    // Approach 1: DP - O(n) time, O(1) space
    public int countVowelPermutation(int n) {
        // a, e, i, o, u
        long a = 1, e = 1, i = 1, o = 1, u = 1;

        for (int len = 2; len <= n; len++) {
            long newA = e; // 'a' can only follow 'e'
            long newE = (a + i) % MOD; // 'e' can follow 'a' or 'i'
            long newI = (a + e + o + u) % MOD; // 'i' can follow 'a', 'e', 'o', 'u'
            long newO = (i + u) % MOD; // 'o' can follow 'i' or 'u'
            long newU = a; // 'u' can only follow 'a'

            a = newA;
            e = newE;
            i = newI;
            o = newO;
            u = newU;
        }

        return (int) ((a + e + i + o + u) % MOD);
    }

    // Approach 2: DP Array - O(n) time, O(n) space
    public int countVowelPermutationDP(int n) {
        // dp[i][j] = number of strings of length i ending with vowel j
        // j: 0=a, 1=e, 2=i, 3=o, 4=u
        long[][] dp = new long[n + 1][5];

        // Base case: length 1
        for (int j = 0; j < 5; j++) {
            dp[1][j] = 1;
        }

        for (int i = 2; i <= n; i++) {
            dp[i][0] = dp[i - 1][1]; // a follows e
            dp[i][1] = (dp[i - 1][0] + dp[i - 1][2]) % MOD; // e follows a, i
            dp[i][2] = (dp[i - 1][0] + dp[i - 1][1] + dp[i - 1][3] + dp[i - 1][4]) % MOD; // i follows a,e,o,u
            dp[i][3] = (dp[i - 1][2] + dp[i - 1][4]) % MOD; // o follows i, u
            dp[i][4] = dp[i - 1][0]; // u follows a
        }

        long result = 0;
        for (int j = 0; j < 5; j++) {
            result = (result + dp[n][j]) % MOD;
        }

        return (int) result;
    }

    // Approach 3: Matrix Exponentiation - O(log n) time, O(1) space
    public int countVowelPermutationMatrix(int n) {
        if (n == 1)
            return 5;

        // Transition matrix based on vowel rules
        long[][] matrix = {
                { 0, 1, 0, 0, 0 }, // a can go to e
                { 1, 0, 1, 0, 0 }, // e can go to a, i
                { 1, 1, 0, 1, 1 }, // i can go to a, e, o, u
                { 0, 0, 1, 0, 1 }, // o can go to i, u
                { 1, 0, 0, 0, 0 } // u can go to a
        };

        long[][] result = matrixPower(matrix, n - 1);

        long sum = 0;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                sum = (sum + result[i][j]) % MOD;
            }
        }

        return (int) sum;
    }

    private long[][] matrixPower(long[][] matrix, int n) {
        int size = matrix.length;
        long[][] result = new long[size][size];

        // Initialize as identity matrix
        for (int i = 0; i < size; i++) {
            result[i][i] = 1;
        }

        while (n > 0) {
            if (n % 2 == 1) {
                result = multiplyMatrix(result, matrix);
            }
            matrix = multiplyMatrix(matrix, matrix);
            n /= 2;
        }

        return result;
    }

    private long[][] multiplyMatrix(long[][] a, long[][] b) {
        int size = a.length;
        long[][] result = new long[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                for (int k = 0; k < size; k++) {
                    result[i][j] = (result[i][j] + a[i][k] * b[k][j]) % MOD;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        CountVowelsPermutation solution = new CountVowelsPermutation();

        System.out.println("=== Count Vowels Permutation Test Cases ===");

        // Test cases
        for (int n = 1; n <= 5; n++) {
            System.out.println("n = " + n + ":");
            System.out.println("  DP: " + solution.countVowelPermutation(n));
            System.out.println("  DP Array: " + solution.countVowelPermutationDP(n));
            System.out.println("  Matrix: " + solution.countVowelPermutationMatrix(n));
        }

        System.out.println("\nExpected: n=1: 5, n=2: 10, n=3: 19, n=4: 35, n=5: 68");

        performanceTest();
    }

    private static void performanceTest() {
        CountVowelsPermutation solution = new CountVowelsPermutation();

        int n = 20000;
        System.out.println("\n=== Performance Test (n = " + n + ") ===");

        long start = System.nanoTime();
        int result1 = solution.countVowelPermutation(n);
        long end = System.nanoTime();
        System.out.println("DP: " + result1 + " - Time: " + (end - start) / 1_000_000.0 + " ms");

        start = System.nanoTime();
        int result2 = solution.countVowelPermutationMatrix(n);
        end = System.nanoTime();
        System.out.println("Matrix: " + result2 + " - Time: " + (end - start) / 1_000_000.0 + " ms");
    }
}
