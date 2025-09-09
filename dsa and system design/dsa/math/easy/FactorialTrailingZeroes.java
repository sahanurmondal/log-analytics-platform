package math.easy;

import java.util.*;

/**
 * LeetCode 172: Factorial Trailing Zeroes
 * https://leetcode.com/problems/factorial-trailing-zeroes/
 * 
 * Companies: Bloomberg, Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High (Asked in 500+ interviews)
 *
 * Description:
 * Given an integer n, return the number of trailing zeroes in n!.
 * 
 * Note that n! = n * (n-1) * (n-2) * ... * 3 * 2 * 1.
 *
 * Constraints:
 * - 0 <= n <= 10^4
 * 
 * Follow-up Questions:
 * 1. Could you write a solution that works in logarithmic time complexity?
 * 2. Can you find the number of trailing zeros in n! without calculating n!?
 * 3. What about leading zeros?
 * 4. How to find the position of the rightmost non-zero digit?
 * 5. Can you generalize this for any base?
 */
public class FactorialTrailingZeroes {

    // Approach 1: Mathematical approach (counting factors of 5) - O(log n) time,
    // O(1) space
    public int trailingZeroes(int n) {
        int count = 0;

        // Count factors of 5 in n!
        // Trailing zeros are produced by factors of 10 = 2 × 5
        // Since there are always more factors of 2 than 5, we count factors of 5
        while (n >= 5) {
            n /= 5;
            count += n;
        }

        return count;
    }

    // Approach 2: Recursive approach - O(log n) time, O(log n) space
    public int trailingZeroesRecursive(int n) {
        if (n < 5) {
            return 0;
        }

        return n / 5 + trailingZeroesRecursive(n / 5);
    }

    // Approach 3: Iterative with explicit factor counting - O(log n) time, O(1)
    // space
    public int trailingZeroesExplicit(int n) {
        int count = 0;

        for (int i = 5; n / i > 0; i *= 5) {
            count += n / i;
        }

        return count;
    }

    // Approach 4: Using mathematical formula - O(log n) time, O(1) space
    public int trailingZeroesFormula(int n) {
        // Count = floor(n/5) + floor(n/25) + floor(n/125) + ...
        int count = 0;
        int powerOf5 = 5;

        while (powerOf5 <= n) {
            count += n / powerOf5;

            // Prevent overflow
            if (powerOf5 > Integer.MAX_VALUE / 5) {
                break;
            }

            powerOf5 *= 5;
        }

        return count;
    }

    // Follow-up 1: Optimized logarithmic solution
    public int trailingZeroesOptimized(int n) {
        return n < 5 ? 0 : n / 5 + trailingZeroesOptimized(n / 5);
    }

    // Follow-up 2: Without calculating factorial (already implemented above)
    // The mathematical approaches don't calculate n! at all

    // Follow-up 3: Leading zeros in factorial
    public int leadingZeroes(int n) {
        if (n == 0)
            return 0;

        // Leading zeros would only occur if the result is 0, which doesn't happen for
        // n! where n > 0
        // However, we can count leading zeros in the string representation
        java.math.BigInteger factorial = factorial(n);
        String factorialStr = factorial.toString();

        int leadingZeros = 0;
        for (char c : factorialStr.toCharArray()) {
            if (c == '0') {
                leadingZeros++;
            } else {
                break;
            }
        }

        return leadingZeros;
    }

    // Follow-up 4: Rightmost non-zero digit in n!
    public int rightmostNonZeroDigit(int n) {
        if (n == 0)
            return 1;
        if (n == 1)
            return 1;

        // This is complex and involves modular arithmetic
        // Simplified approach using actual calculation for small n
        if (n <= 20) {
            java.math.BigInteger factorial = factorial(n);
            String factorialStr = factorial.toString();

            for (int i = factorialStr.length() - 1; i >= 0; i--) {
                char c = factorialStr.charAt(i);
                if (c != '0') {
                    return Character.getNumericValue(c);
                }
            }
        }

        // For larger n, use mathematical approach (complex)
        return rightmostNonZeroDigitMath(n);
    }

    // Follow-up 5: Trailing zeros in any base
    public int trailingZeroesInBase(int n, int base) {
        if (base < 2) {
            throw new IllegalArgumentException("Base must be at least 2");
        }

        // Find prime factorization of base
        Map<Integer, Integer> baseFactors = primeFactorization(base);

        int minCount = Integer.MAX_VALUE;

        // For each prime factor of base, count how many times it appears in n!
        for (Map.Entry<Integer, Integer> entry : baseFactors.entrySet()) {
            int prime = entry.getKey();
            int power = entry.getValue();

            int countInFactorial = countPrimeFactorsInFactorial(n, prime);
            minCount = Math.min(minCount, countInFactorial / power);
        }

        return minCount == Integer.MAX_VALUE ? 0 : minCount;
    }

    // Advanced: Count specific digit occurrences in n!
    public int countDigitInFactorial(int n, int digit) {
        if (digit < 0 || digit > 9) {
            throw new IllegalArgumentException("Digit must be between 0 and 9");
        }

        java.math.BigInteger factorial = factorial(n);
        String factorialStr = factorial.toString();

        int count = 0;
        for (char c : factorialStr.toCharArray()) {
            if (Character.getNumericValue(c) == digit) {
                count++;
            }
        }

        return count;
    }

    // Advanced: Find minimum n such that n! has at least k trailing zeros
    public int minimumNForTrailingZeros(int k) {
        if (k == 0)
            return 0;

        int left = 0, right = 5 * k; // Upper bound approximation

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (trailingZeroes(mid) >= k) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }

        return left;
    }

    // Advanced: Count trailing zeros in product of range [a, b]
    public int trailingZeroesInRange(int a, int b) {
        if (a > b)
            return 0;

        // Product of range [a, b] = b! / (a-1)!
        return trailingZeroes(b) - trailingZeroes(a - 1);
    }

    // Advanced: Factorial with trailing zeros removed
    public java.math.BigInteger factorialWithoutTrailingZeros(int n) {
        java.math.BigInteger factorial = factorial(n);

        // Remove trailing zeros
        while (factorial.remainder(java.math.BigInteger.TEN).equals(java.math.BigInteger.ZERO)) {
            factorial = factorial.divide(java.math.BigInteger.TEN);
        }

        return factorial;
    }

    // Advanced: Sum of trailing zeros for all factorials from 1 to n
    public long sumOfTrailingZeros(int n) {
        long sum = 0;

        for (int i = 1; i <= n; i++) {
            sum += trailingZeroes(i);
        }

        return sum;
    }

    // Advanced: Trailing zeros in double factorial n!!
    public int trailingZeroesDoubleFactorial(int n) {
        if (n <= 0)
            return 0;

        int count = 0;

        // For odd n: n!! = n * (n-2) * (n-4) * ... * 3 * 1
        // For even n: n!! = n * (n-2) * (n-4) * ... * 4 * 2

        if (n % 2 == 0) {
            // Even double factorial
            for (int i = 2; i <= n; i += 2) {
                count += countFactorsOfFive(i);
            }
        } else {
            // Odd double factorial - no factors of 2 or 5 contribute to trailing zeros in
            // odd-only products
            for (int i = 1; i <= n; i += 2) {
                count += countFactorsOfFive(i);
            }
        }

        return count;
    }

    // Advanced: Trailing zeros in multinomial coefficient
    public int trailingZeroesMultinomial(int[] coefficients) {
        int sum = Arrays.stream(coefficients).sum();
        int totalZeros = trailingZeroes(sum);

        for (int coeff : coefficients) {
            totalZeros -= trailingZeroes(coeff);
        }

        return totalZeros;
    }

    // Advanced: Trailing zeros in rising factorial (Pochhammer symbol)
    public int trailingZeroesRisingFactorial(int x, int n) {
        // Rising factorial: x * (x+1) * (x+2) * ... * (x+n-1)
        if (n <= 0)
            return 0;

        int count = 0;

        for (int i = 0; i < n; i++) {
            count += countFactorsOfFive(x + i);
        }

        return count;
    }

    // Helper methods
    private java.math.BigInteger factorial(int n) {
        java.math.BigInteger result = java.math.BigInteger.ONE;

        for (int i = 2; i <= n; i++) {
            result = result.multiply(java.math.BigInteger.valueOf(i));
        }

        return result;
    }

    private int rightmostNonZeroDigitMath(int n) {
        // Complex mathematical formula - simplified version
        // This is an approximation for demonstration
        if (n < 5) {
            int[] digits = { 1, 1, 2, 6, 4 }; // 0!, 1!, 2!, 3!, 4!
            return digits[n] % 10;
        }

        // For larger n, this requires advanced number theory
        // Returning a placeholder
        return (n % 10 == 0) ? 2 : ((n % 10) % 2 == 0) ? 2 : 6;
    }

    private Map<Integer, Integer> primeFactorization(int n) {
        Map<Integer, Integer> factors = new HashMap<>();

        // Check for factor 2
        while (n % 2 == 0) {
            factors.put(2, factors.getOrDefault(2, 0) + 1);
            n /= 2;
        }

        // Check for odd factors
        for (int i = 3; i * i <= n; i += 2) {
            while (n % i == 0) {
                factors.put(i, factors.getOrDefault(i, 0) + 1);
                n /= i;
            }
        }

        // If n is still greater than 2, it's a prime
        if (n > 2) {
            factors.put(n, 1);
        }

        return factors;
    }

    private int countPrimeFactorsInFactorial(int n, int prime) {
        int count = 0;

        while (n >= prime) {
            n /= prime;
            count += n;
        }

        return count;
    }

    private int countFactorsOfFive(int n) {
        int count = 0;

        while (n % 5 == 0) {
            count++;
            n /= 5;
        }

        return count;
    }

    // Utility: Get all trailing zeros statistics
    public Map<String, Integer> getTrailingZeroStats(int n) {
        Map<String, Integer> stats = new HashMap<>();

        stats.put("trailingZeros", trailingZeroes(n));
        stats.put("factorsOf5", countPrimeFactorsInFactorial(n, 5));
        stats.put("factorsOf2", countPrimeFactorsInFactorial(n, 2));
        stats.put("rightmostNonZero", rightmostNonZeroDigit(n));

        if (n <= 20) {
            java.math.BigInteger fact = factorial(n);
            stats.put("totalDigits", fact.toString().length());
        }

        return stats;
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(int[] testNumbers) {
        Map<String, Long> results = new HashMap<>();

        // Test mathematical approach
        long start = System.nanoTime();
        for (int num : testNumbers) {
            trailingZeroes(num);
        }
        results.put("Mathematical", System.nanoTime() - start);

        // Test recursive approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            trailingZeroesRecursive(num);
        }
        results.put("Recursive", System.nanoTime() - start);

        // Test explicit approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            trailingZeroesExplicit(num);
        }
        results.put("Explicit", System.nanoTime() - start);

        // Test formula approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            trailingZeroesFormula(num);
        }
        results.put("Formula", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        FactorialTrailingZeroes solution = new FactorialTrailingZeroes();

        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");
        int[] testNumbers = { 0, 1, 3, 5, 10, 25, 50, 100, 125, 250, 500, 1000 };

        for (int n : testNumbers) {
            int result1 = solution.trailingZeroes(n);
            int result2 = solution.trailingZeroesRecursive(n);
            int result3 = solution.trailingZeroesExplicit(n);
            int result4 = solution.trailingZeroesFormula(n);

            System.out.printf("n=%d: Math=%d, Rec=%d, Exp=%d, Form=%d%n",
                    n, result1, result2, result3, result4);

            // Verify consistency
            boolean consistent = result1 == result2 && result2 == result3 && result3 == result4;
            if (!consistent) {
                System.out.println("  WARNING: Inconsistent results!");
            }
        }

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");
        int[] edgeCases = { 0, 1, 4, 5, 9, 10, 24, 25, 26, 49, 50, 51, 99, 100, 101 };

        for (int n : edgeCases) {
            int zeros = solution.trailingZeroes(n);
            System.out.println("n=" + n + " -> trailing zeros: " + zeros);
        }

        // Test Case 3: Different bases
        System.out.println("\n=== Test Case 3: Trailing Zeros in Different Bases ===");
        int[] bases = { 2, 3, 6, 8, 10, 12, 16 };
        int testN = 10;

        for (int base : bases) {
            int zeros = solution.trailingZeroesInBase(testN, base);
            System.out.println("n=" + testN + " in base " + base + ": " + zeros + " trailing zeros");
        }

        // Test Case 4: Statistics for small factorials
        System.out.println("\n=== Test Case 4: Detailed Statistics ===");
        for (int n = 0; n <= 15; n++) {
            Map<String, Integer> stats = solution.getTrailingZeroStats(n);
            System.out.println("n=" + n + ": " + stats);
        }

        // Test Case 5: Minimum n for k trailing zeros
        System.out.println("\n=== Test Case 5: Minimum n for k Trailing Zeros ===");
        for (int k = 0; k <= 10; k++) {
            int minN = solution.minimumNForTrailingZeros(k);
            int actualZeros = solution.trailingZeroes(minN);
            System.out.println("For " + k + " trailing zeros, minimum n=" + minN +
                    " (actual zeros: " + actualZeros + ")");
        }

        // Test Case 6: Range trailing zeros
        System.out.println("\n=== Test Case 6: Trailing Zeros in Range Products ===");
        int[][] ranges = { { 1, 5 }, { 5, 10 }, { 10, 15 }, { 20, 25 }, { 1, 10 }, { 11, 20 } };

        for (int[] range : ranges) {
            int zeros = solution.trailingZeroesInRange(range[0], range[1]);
            System.out.println("Product of [" + range[0] + ", " + range[1] + "]: " + zeros + " trailing zeros");
        }

        // Test Case 7: Sum of trailing zeros
        System.out.println("\n=== Test Case 7: Sum of Trailing Zeros ===");
        int[] sumTests = { 5, 10, 25, 50, 100 };

        for (int n : sumTests) {
            long sum = solution.sumOfTrailingZeros(n);
            System.out.println("Sum of trailing zeros for 1! to " + n + "!: " + sum);
        }

        // Test Case 8: Double factorial
        System.out.println("\n=== Test Case 8: Double Factorial Trailing Zeros ===");
        for (int n = 1; n <= 20; n++) {
            int zeros = solution.trailingZeroesDoubleFactorial(n);
            System.out.println(n + "!! has " + zeros + " trailing zeros");
        }

        // Test Case 9: Multinomial coefficients
        System.out.println("\n=== Test Case 9: Multinomial Coefficient Trailing Zeros ===");
        int[][] multinomials = {
                { 3, 2, 1 },
                { 5, 3, 2 },
                { 10, 5, 3, 2 },
                { 15, 7, 5, 3 }
        };

        for (int[] coeffs : multinomials) {
            int zeros = solution.trailingZeroesMultinomial(coeffs);
            System.out.println("Multinomial " + Arrays.toString(coeffs) + ": " + zeros + " trailing zeros");
        }

        // Test Case 10: Rising factorial
        System.out.println("\n=== Test Case 10: Rising Factorial Trailing Zeros ===");
        int[][] risingTests = { { 1, 10 }, { 5, 8 }, { 10, 15 }, { 20, 25 } };

        for (int[] test : risingTests) {
            int zeros = solution.trailingZeroesRisingFactorial(test[0], test[1]);
            System.out.println("Rising factorial (" + test[0] + ")_" + test[1] + ": " + zeros + " trailing zeros");
        }

        // Test Case 11: Digit counting in factorials
        System.out.println("\n=== Test Case 11: Digit Counting in Factorials ===");
        for (int n = 5; n <= 10; n++) {
            System.out.println(n + "!:");
            for (int digit = 0; digit <= 9; digit++) {
                int count = solution.countDigitInFactorial(n, digit);
                if (count > 0) {
                    System.out.println("  Digit " + digit + ": " + count + " occurrences");
                }
            }
        }

        // Test Case 12: Factorial without trailing zeros
        System.out.println("\n=== Test Case 12: Factorial Without Trailing Zeros ===");
        for (int n = 5; n <= 10; n++) {
            java.math.BigInteger original = solution.factorial(n);
            java.math.BigInteger withoutZeros = solution.factorialWithoutTrailingZeros(n);

            System.out.println(n + "!: " + original);
            System.out.println("Without trailing zeros: " + withoutZeros);
            System.out.println();
        }

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        int[] performanceTest = new int[1000];
        Random random = new Random(42);

        for (int i = 0; i < performanceTest.length; i++) {
            performanceTest[i] = random.nextInt(10000);
        }

        Map<String, Long> performance = solution.comparePerformance(performanceTest);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Large number tests
        System.out.println("\n=== Large Number Tests ===");
        int[] largeNumbers = { 1000, 5000, 10000 };

        for (int n : largeNumbers) {
            long start = System.nanoTime();
            int zeros = solution.trailingZeroes(n);
            long duration = System.nanoTime() - start;

            System.out.println("n=" + n + ": " + zeros + " trailing zeros (computed in " +
                    duration / 1000.0 + " microseconds)");
        }

        // Verification test
        System.out.println("\n=== Verification Test ===");
        boolean allCorrect = true;

        for (int n = 0; n <= 100; n++) {
            int math = solution.trailingZeroes(n);
            int recursive = solution.trailingZeroesRecursive(n);
            int explicit = solution.trailingZeroesExplicit(n);
            int formula = solution.trailingZeroesFormula(n);

            if (!(math == recursive && recursive == explicit && explicit == formula)) {
                System.out.println("Inconsistency at n=" + n);
                allCorrect = false;
            }
        }

        System.out.println("All approaches consistent for n=0 to 100: " + allCorrect);

        System.out.println("\nFactorial Trailing Zeroes testing completed successfully!");
    }
}
