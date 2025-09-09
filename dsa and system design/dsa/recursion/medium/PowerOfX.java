package recursion.medium;

import java.util.*;

/**
 * LeetCode 50: Pow(x, n)
 * https://leetcode.com/problems/powx-n/
 * 
 * Companies: Facebook, Google, Amazon, Microsoft, Apple, LinkedIn
 * Frequency: High (Asked in 500+ interviews)
 *
 * Description:
 * Implement pow(x, n), which calculates x raised to the power n (i.e., x^n).
 * 
 * Constraints:
 * - -100.0 < x < 100.0
 * - -2^31 <= n <= 2^31-1
 * - n is an integer.
 * - Either x is not zero or n > 0.
 * - -10^4 <= x^n <= 10^4
 * 
 * Follow-up Questions:
 * 1. How would you handle very large exponents efficiently?
 * 2. Can you implement using different approaches (iterative, memoization)?
 * 3. How to handle floating point precision issues?
 * 4. What about modular exponentiation?
 * 5. How to implement for different number types (BigInteger, complex numbers)?
 */
public class PowerOfX {

    // Approach 1: Fast Exponentiation (Recursive) - O(log n) time, O(log n) space
    public static double myPow(double x, int n) {
        if (n == 0)
            return 1.0;

        // Handle negative exponent
        if (n < 0) {
            // Handle Integer.MIN_VALUE overflow
            if (n == Integer.MIN_VALUE) {
                return 1.0 / (x * myPow(x, Integer.MAX_VALUE));
            }
            return 1.0 / myPow(x, -n);
        }

        // Fast exponentiation
        if (n % 2 == 0) {
            double half = myPow(x, n / 2);
            return half * half;
        } else {
            return x * myPow(x, n - 1);
        }
    }

    // Approach 2: Iterative Fast Exponentiation - O(log n) time, O(1) space
    public static double myPowIterative(double x, int n) {
        if (n == 0)
            return 1.0;

        long absN = Math.abs((long) n);
        double result = 1.0;
        double currentPower = x;

        while (absN > 0) {
            if (absN % 2 == 1) {
                result *= currentPower;
            }
            currentPower *= currentPower;
            absN /= 2;
        }

        return n < 0 ? 1.0 / result : result;
    }

    // Approach 3: Binary representation approach
    public static double myPowBinary(double x, int n) {
        if (n == 0)
            return 1.0;

        long absN = Math.abs((long) n);
        double result = 1.0;
        double base = x;

        // Process each bit of the exponent
        while (absN > 0) {
            if ((absN & 1) == 1) {
                result *= base;
            }
            base *= base;
            absN >>= 1;
        }

        return n < 0 ? 1.0 / result : result;
    }

    // Follow-up 1: Handling very large exponents with BigInteger
    public static class BigIntegerPower {

        public static double myPowBigInt(double x, java.math.BigInteger n) {
            if (n.equals(java.math.BigInteger.ZERO))
                return 1.0;

            boolean isNegative = n.compareTo(java.math.BigInteger.ZERO) < 0;
            if (isNegative) {
                n = n.abs();
            }

            double result = 1.0;
            double base = x;

            while (n.compareTo(java.math.BigInteger.ZERO) > 0) {
                if (n.testBit(0)) { // Check if least significant bit is 1
                    result *= base;
                }
                base *= base;
                n = n.shiftRight(1); // Divide by 2
            }

            return isNegative ? 1.0 / result : result;
        }
    }

    // Follow-up 2: Memoization approach
    public static class MemoizedPower {
        private Map<String, Double> memo;

        public MemoizedPower() {
            memo = new HashMap<>();
        }

        public double myPow(double x, int n) {
            String key = x + "," + n;
            if (memo.containsKey(key)) {
                return memo.get(key);
            }

            double result = calculatePow(x, n);
            memo.put(key, result);
            return result;
        }

        private double calculatePow(double x, int n) {
            if (n == 0)
                return 1.0;

            if (n < 0) {
                if (n == Integer.MIN_VALUE) {
                    return 1.0 / (x * myPow(x, Integer.MAX_VALUE));
                }
                return 1.0 / myPow(x, -n);
            }

            if (n % 2 == 0) {
                double half = myPow(x, n / 2);
                return half * half;
            } else {
                return x * myPow(x, n - 1);
            }
        }

        public void clearCache() {
            memo.clear();
        }

        public int getCacheSize() {
            return memo.size();
        }
    }

    // Follow-up 3: Precision-aware implementation
    public static class PrecisionAwarePower {
        private static final double EPSILON = 1e-10;

        public static double myPowPrecise(double x, int n, double epsilon) {
            if (Math.abs(x) < epsilon && n < 0) {
                throw new ArithmeticException("Division by zero");
            }

            if (n == 0)
                return 1.0;

            long absN = Math.abs((long) n);
            double result = 1.0;
            double base = x;

            while (absN > 0) {
                if (absN % 2 == 1) {
                    result *= base;
                }
                base *= base;
                absN /= 2;

                // Check for overflow/underflow
                if (Double.isInfinite(result) || Double.isNaN(result)) {
                    throw new ArithmeticException("Result overflow");
                }
            }

            return n < 0 ? 1.0 / result : result;
        }

        public static boolean equals(double a, double b, double epsilon) {
            return Math.abs(a - b) < epsilon;
        }
    }

    // Follow-up 4: Modular exponentiation
    public static class ModularPower {

        public static long modPow(long base, long exp, long mod) {
            if (mod == 1)
                return 0;

            long result = 1;
            base = base % mod;

            while (exp > 0) {
                if (exp % 2 == 1) {
                    result = (result * base) % mod;
                }
                exp = exp >> 1;
                base = (base * base) % mod;
            }

            return result;
        }

        public static java.math.BigInteger modPowBig(java.math.BigInteger base,
                java.math.BigInteger exp,
                java.math.BigInteger mod) {
            return base.modPow(exp, mod);
        }
    }

    // Follow-up 5: Complex number power
    public static class ComplexPower {
        public static class Complex {
            double real, imag;

            public Complex(double real, double imag) {
                this.real = real;
                this.imag = imag;
            }

            public Complex multiply(Complex other) {
                double newReal = this.real * other.real - this.imag * other.imag;
                double newImag = this.real * other.imag + this.imag * other.real;
                return new Complex(newReal, newImag);
            }

            public Complex power(int n) {
                if (n == 0)
                    return new Complex(1, 0);

                if (n < 0) {
                    Complex reciprocal = reciprocal();
                    return reciprocal.power(-n);
                }

                Complex result = new Complex(1, 0);
                Complex base = new Complex(this.real, this.imag);

                while (n > 0) {
                    if (n % 2 == 1) {
                        result = result.multiply(base);
                    }
                    base = base.multiply(base);
                    n /= 2;
                }

                return result;
            }

            public Complex reciprocal() {
                double denominator = real * real + imag * imag;
                return new Complex(real / denominator, -imag / denominator);
            }

            @Override
            public String toString() {
                if (imag >= 0) {
                    return String.format("%.3f + %.3fi", real, imag);
                } else {
                    return String.format("%.3f - %.3fi", real, -imag);
                }
            }
        }
    }

    // Advanced: Matrix exponentiation
    public static class MatrixPower {
        public static long[][] matrixPower(long[][] matrix, int n) {
            int size = matrix.length;
            long[][] result = identityMatrix(size);
            long[][] base = cloneMatrix(matrix);

            while (n > 0) {
                if (n % 2 == 1) {
                    result = multiplyMatrices(result, base);
                }
                base = multiplyMatrices(base, base);
                n /= 2;
            }

            return result;
        }

        private static long[][] identityMatrix(int size) {
            long[][] identity = new long[size][size];
            for (int i = 0; i < size; i++) {
                identity[i][i] = 1;
            }
            return identity;
        }

        private static long[][] cloneMatrix(long[][] matrix) {
            int rows = matrix.length;
            int cols = matrix[0].length;
            long[][] clone = new long[rows][cols];

            for (int i = 0; i < rows; i++) {
                System.arraycopy(matrix[i], 0, clone[i], 0, cols);
            }

            return clone;
        }

        private static long[][] multiplyMatrices(long[][] a, long[][] b) {
            int rows = a.length;
            int cols = b[0].length;
            int common = b.length;

            long[][] result = new long[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    for (int k = 0; k < common; k++) {
                        result[i][j] += a[i][k] * b[k][j];
                    }
                }
            }

            return result;
        }
    }

    // Advanced: Generics-based power implementation
    public static class GenericPower {
        public interface Multipliable<T> {
            T multiply(T other);

            T one();
        }

        public static <T extends Multipliable<T>> T power(T base, int n) {
            if (n == 0)
                return base.one();

            if (n < 0) {
                throw new IllegalArgumentException("Negative exponents not supported for generic types");
            }

            T result = base.one();
            T currentBase = base;

            while (n > 0) {
                if (n % 2 == 1) {
                    result = result.multiply(currentBase);
                }
                currentBase = currentBase.multiply(currentBase);
                n /= 2;
            }

            return result;
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(double x, int n, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Base: " + x + ", Exponent: " + n + ", Iterations: " + iterations);

            // Recursive approach
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                myPow(x, n);
            }
            long recursiveTime = System.nanoTime() - start;

            // Iterative approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                myPowIterative(x, n);
            }
            long iterativeTime = System.nanoTime() - start;

            // Binary approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                myPowBinary(x, n);
            }
            long binaryTime = System.nanoTime() - start;

            // Built-in Math.pow
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                Math.pow(x, n);
            }
            long mathPowTime = System.nanoTime() - start;

            System.out.println("Recursive: " + recursiveTime / 1_000_000 + " ms");
            System.out.println("Iterative: " + iterativeTime / 1_000_000 + " ms");
            System.out.println("Binary: " + binaryTime / 1_000_000 + " ms");
            System.out.println("Math.pow: " + mathPowTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        System.out.println("2^10 = " + myPow(2.0, 10)); // 1024.0
        System.out.println("2.1^3 = " + myPow(2.1, 3)); // 9.261
        System.out.println("2^-2 = " + myPow(2.0, -2)); // 0.25

        // Test Case 2: Compare different approaches
        System.out.println("\n=== Test Case 2: Compare Approaches ===");

        double x = 2.5;
        int n = 15;

        System.out.println("Input: x=" + x + ", n=" + n);
        System.out.println("Recursive: " + myPow(x, n));
        System.out.println("Iterative: " + myPowIterative(x, n));
        System.out.println("Binary: " + myPowBinary(x, n));
        System.out.println("Math.pow: " + Math.pow(x, n));

        // Test Case 3: Edge cases
        System.out.println("\n=== Test Case 3: Edge Cases ===");

        System.out.println("Any number to power 0: " + myPow(123.456, 0)); // 1.0
        System.out.println("1 to any power: " + myPow(1.0, 1000000)); // 1.0
        System.out.println("-1 to even power: " + myPow(-1.0, 2)); // 1.0
        System.out.println("-1 to odd power: " + myPow(-1.0, 3)); // -1.0
        System.out.println("Negative exponent: " + myPow(2.0, -3)); // 0.125

        // Test Case 4: Integer.MIN_VALUE edge case
        System.out.println("\n=== Test Case 4: Integer.MIN_VALUE ===");

        System.out.println("2^Integer.MIN_VALUE: " + myPow(2.0, Integer.MIN_VALUE));
        System.out.println("0.5^Integer.MIN_VALUE: " + myPow(0.5, Integer.MIN_VALUE));

        // Test Case 5: Memoized power
        System.out.println("\n=== Test Case 5: Memoized Power ===");

        MemoizedPower memoizedPower = new MemoizedPower();

        System.out.println("First call 2^10: " + memoizedPower.myPow(2.0, 10));
        System.out.println("Second call 2^10: " + memoizedPower.myPow(2.0, 10));
        System.out.println("Cache size: " + memoizedPower.getCacheSize());

        // Test Case 6: Precision-aware power
        System.out.println("\n=== Test Case 6: Precision-aware Power ===");

        try {
            double result = PrecisionAwarePower.myPowPrecise(2.0, 10, 1e-10);
            System.out.println("Precise 2^10: " + result);

            // Test equality with epsilon
            double a = myPow(2.0, 10);
            double b = myPowIterative(2.0, 10);
            System.out.println("Results equal within epsilon: " +
                    PrecisionAwarePower.equals(a, b, 1e-10));
        } catch (ArithmeticException e) {
            System.out.println("Arithmetic exception: " + e.getMessage());
        }

        // Test Case 7: Modular exponentiation
        System.out.println("\n=== Test Case 7: Modular Exponentiation ===");

        System.out.println("2^10 mod 1000: " + ModularPower.modPow(2, 10, 1000)); // 24
        System.out.println("3^5 mod 7: " + ModularPower.modPow(3, 5, 7)); // 5

        // BigInteger modular power
        java.math.BigInteger bigBase = new java.math.BigInteger("123456789");
        java.math.BigInteger bigExp = new java.math.BigInteger("987654321");
        java.math.BigInteger bigMod = new java.math.BigInteger("1000000007");

        System.out.println("Big modular power: " +
                ModularPower.modPowBig(bigBase, bigExp, bigMod));

        // Test Case 8: Complex number power
        System.out.println("\n=== Test Case 8: Complex Number Power ===");

        ComplexPower.Complex complex = new ComplexPower.Complex(1, 1); // 1 + i

        System.out.println("(1+i)^0: " + complex.power(0)); // 1 + 0i
        System.out.println("(1+i)^1: " + complex.power(1)); // 1 + 1i
        System.out.println("(1+i)^2: " + complex.power(2)); // 0 + 2i
        System.out.println("(1+i)^3: " + complex.power(3)); // -2 + 2i
        System.out.println("(1+i)^4: " + complex.power(4)); // -4 + 0i

        // Test Case 9: Matrix exponentiation
        System.out.println("\n=== Test Case 9: Matrix Exponentiation ===");

        long[][] matrix = { { 1, 1 }, { 1, 0 } }; // Fibonacci matrix
        long[][] result = MatrixPower.matrixPower(matrix, 10);

        System.out.println("Fibonacci matrix^10:");
        for (long[] row : result) {
            System.out.println(Arrays.toString(row));
        }

        // The result should give us F(11) and F(10) in the first row
        System.out.println("F(11) = " + result[0][0] + ", F(10) = " + result[0][1]);

        // Test Case 10: BigInteger power
        System.out.println("\n=== Test Case 10: BigInteger Power ===");

        java.math.BigInteger bigExponent = new java.math.BigInteger("1000");
        double bigResult = BigIntegerPower.myPowBigInt(2.0, bigExponent);
        System.out.println("2^1000 (BigInteger): " + bigResult);

        // Test Case 11: Large exponents performance
        System.out.println("\n=== Test Case 11: Large Exponents ===");

        int largeExp = 1000000;

        long start = System.currentTimeMillis();
        double result1 = myPowIterative(1.0001, largeExp);
        long end = System.currentTimeMillis();

        System.out.println("1.0001^" + largeExp + " = " + result1);
        System.out.println("Time taken: " + (end - start) + " ms");

        // Test Case 12: Accuracy comparison
        System.out.println("\n=== Test Case 12: Accuracy Comparison ===");

        double base = 1.1;
        int exp = 100;

        double ourResult = myPow(base, exp);
        double mathResult = Math.pow(base, exp);
        double difference = Math.abs(ourResult - mathResult);

        System.out.println("Our result: " + ourResult);
        System.out.println("Math.pow result: " + mathResult);
        System.out.println("Difference: " + difference);
        System.out.println("Relative error: " + (difference / mathResult * 100) + "%");

        // Performance comparison
        PerformanceComparison.compareApproaches(2.5, 1000, 100000);

        System.out.println("\nPower of X testing completed successfully!");
    }
}
