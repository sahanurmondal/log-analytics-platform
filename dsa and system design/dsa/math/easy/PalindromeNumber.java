package math.easy;

import java.math.BigInteger;
import java.util.*;

/**
 * LeetCode 9: Palindrome Number
 * https://leetcode.com/problems/palindrome-number/
 * 
 * Companies: Amazon, Meta, Apple, Microsoft, Google, Bloomberg, Adobe
 * Frequency: Very High (Asked in 1200+ interviews)
 *
 * Description:
 * Given an integer x, return true if x is a palindrome, and false otherwise.
 * 
 * An integer is a palindrome when it reads the same backward as forward.
 * For example, 121 is a palindrome while 123 is not.
 *
 * Constraints:
 * - -2^31 <= x <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Could you solve it without converting the integer to a string?
 * 2. How to handle overflow when reversing?
 * 3. What about palindromic numbers in different bases?
 * 4. Can you solve it by reversing only half the number?
 * 5. How to find all palindromes in a range efficiently?
 */
public class PalindromeNumber {

    // Approach 1: String conversion approach - O(log n) time, O(log n) space
    public boolean isPalindrome(int x) {
        if (x < 0) {
            return false; // Negative numbers are not palindromes
        }

        String str = String.valueOf(x);
        int left = 0;
        int right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    // Approach 2: Mathematical reversal approach - O(log n) time, O(1) space
    public boolean isPalindromeMath(int x) {
        if (x < 0) {
            return false;
        }

        long original = x;
        long reversed = 0;

        while (x > 0) {
            reversed = reversed * 10 + x % 10;
            x /= 10;
        }

        return original == reversed;
    }

    // Approach 3: Half reversal approach (optimal) - O(log n) time, O(1) space
    public boolean isPalindromeHalf(int x) {
        // Special cases
        if (x < 0 || (x % 10 == 0 && x != 0)) {
            return false;
        }

        int reversedHalf = 0;

        // Reverse only half of the number
        while (x > reversedHalf) {
            reversedHalf = reversedHalf * 10 + x % 10;
            x /= 10;
        }

        // For even length numbers: x == reversedHalf
        // For odd length numbers: x == reversedHalf / 10
        return x == reversedHalf || x == reversedHalf / 10;
    }

    // Approach 4: Recursive approach - O(log n) time, O(log n) space
    public boolean isPalindromeRecursive(int x) {
        if (x < 0) {
            return false;
        }

        return isPalindromeHelper(x, x, new int[] { 0 });
    }

    private boolean isPalindromeHelper(int original, int x, int[] reversedRef) {
        if (x == 0) {
            return original == reversedRef[0];
        }

        if (!isPalindromeHelper(original, x / 10, reversedRef)) {
            return false;
        }

        reversedRef[0] = reversedRef[0] * 10 + x % 10;
        return original == reversedRef[0];
    }

    // Approach 5: Digit array approach - O(log n) time, O(log n) space
    public boolean isPalindromeArray(int x) {
        if (x < 0) {
            return false;
        }

        if (x < 10) {
            return true;
        }

        List<Integer> digits = new ArrayList<>();

        while (x > 0) {
            digits.add(x % 10);
            x /= 10;
        }

        int left = 0;
        int right = digits.size() - 1;

        while (left < right) {
            if (!digits.get(left).equals(digits.get(right))) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    // Follow-up 1: Handle overflow when reversing
    public boolean isPalindromeSafe(int x) {
        if (x < 0) {
            return false;
        }

        try {
            // Use long to handle potential overflow
            long original = x;
            long reversed = 0;

            while (x > 0) {
                long newReversed = reversed * 10 + x % 10;

                // Check for overflow
                if (newReversed / 10 != reversed) {
                    return false; // Overflow occurred
                }

                reversed = newReversed;
                x /= 10;
            }

            return original == reversed;
        } catch (ArithmeticException e) {
            return false;
        }
    }

    // Follow-up 2: BigInteger approach for very large numbers
    public boolean isPalindromeBigInteger(BigInteger x) {
        if (x.signum() < 0) {
            return false;
        }

        String str = x.toString();
        return isPalindromeString(str);
    }

    // Follow-up 3: Palindromic numbers in different bases
    public boolean isPalindromeBase(int x, int base) {
        if (x < 0 || base < 2) {
            return false;
        }

        List<Integer> digits = new ArrayList<>();

        if (x == 0) {
            return true;
        }

        while (x > 0) {
            digits.add(x % base);
            x /= base;
        }

        int left = 0;
        int right = digits.size() - 1;

        while (left < right) {
            if (!digits.get(left).equals(digits.get(right))) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    // Follow-up 4: Find all palindromes in a range
    public List<Integer> findPalindromes(int start, int end) {
        List<Integer> palindromes = new ArrayList<>();

        for (int i = start; i <= end; i++) {
            if (isPalindromeHalf(i)) {
                palindromes.add(i);
            }
        }

        return palindromes;
    }

    // Follow-up 5: Generate palindromes efficiently
    public List<Integer> generatePalindromes(int limit) {
        List<Integer> palindromes = new ArrayList<>();

        // Single digit palindromes
        for (int i = 1; i <= 9 && i <= limit; i++) {
            palindromes.add(i);
        }

        // Multi-digit palindromes
        for (int length = 2;; length++) {
            List<Integer> lengthPalindromes = generatePalindromesOfLength(length);

            boolean added = false;
            for (int palindrome : lengthPalindromes) {
                if (palindrome <= limit) {
                    palindromes.add(palindrome);
                    added = true;
                } else {
                    break;
                }
            }

            if (!added || lengthPalindromes.get(0) > limit) {
                break;
            }
        }

        return palindromes;
    }

    // Advanced: Check if a number can become palindrome by changing one digit
    public boolean canBecomePalindrome(int x) {
        if (x < 0) {
            return false;
        }

        String str = String.valueOf(x);
        int differences = 0;
        int left = 0;
        int right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                differences++;
                if (differences > 1) {
                    return false;
                }
            }
            left++;
            right--;
        }

        return differences <= 1;
    }

    // Advanced: Find the closest palindrome
    public int closestPalindrome(int x) {
        if (x < 0) {
            return -1;
        }

        // Check smaller numbers
        for (int i = x - 1; i >= 0; i--) {
            if (isPalindromeHalf(i)) {
                int smaller = i;

                // Check larger numbers
                for (int j = x + 1;; j++) {
                    if (isPalindromeHalf(j)) {
                        int larger = j;

                        // Return the closest one
                        return (x - smaller <= larger - x) ? smaller : larger;
                    }

                    // Prevent infinite loop for very large numbers
                    if (j - x > 1000) {
                        return smaller;
                    }
                }
            }

            // Prevent infinite loop for large numbers
            if (x - i > 1000) {
                break;
            }
        }

        // If no smaller palindrome found, find larger
        for (int j = x + 1;; j++) {
            if (isPalindromeHalf(j)) {
                return j;
            }

            if (j - x > 1000) {
                return -1; // Not found within reasonable range
            }
        }
    }

    // Advanced: Count palindromes in range
    public int countPalindromes(int start, int end) {
        int count = 0;

        for (int i = start; i <= end; i++) {
            if (isPalindromeHalf(i)) {
                count++;
            }
        }

        return count;
    }

    // Advanced: Largest palindrome less than n
    public int largestPalindromeLessThan(int n) {
        for (int i = n - 1; i >= 0; i--) {
            if (isPalindromeHalf(i)) {
                return i;
            }
        }
        return -1;
    }

    // Advanced: Smallest palindrome greater than n
    public int smallestPalindromeGreaterThan(int n) {
        for (int i = n + 1; i <= Integer.MAX_VALUE; i++) {
            if (isPalindromeHalf(i)) {
                return i;
            }
            // Prevent overflow
            if (i == Integer.MAX_VALUE) {
                break;
            }
        }
        return -1;
    }

    // Advanced: Sum of palindromes in range
    public long sumPalindromes(int start, int end) {
        long sum = 0;

        for (int i = start; i <= end; i++) {
            if (isPalindromeHalf(i)) {
                sum += i;
            }
        }

        return sum;
    }

    // Advanced: Check if concatenation of two numbers is palindrome
    public boolean isConcatPalindrome(int a, int b) {
        String concat = String.valueOf(a) + String.valueOf(b);
        return isPalindromeString(concat);
    }

    // Advanced: Digital root palindrome check
    public boolean isDigitalRootPalindrome(int x) {
        while (x >= 10) {
            int sum = 0;
            while (x > 0) {
                sum += x % 10;
                x /= 10;
            }
            x = sum;
        }
        return true; // Single digits are always palindromes
    }

    // Helper methods
    private boolean isPalindromeString(String str) {
        int left = 0;
        int right = str.length() - 1;

        while (left < right) {
            if (str.charAt(left) != str.charAt(right)) {
                return false;
            }
            left++;
            right--;
        }

        return true;
    }

    private List<Integer> generatePalindromesOfLength(int length) {
        List<Integer> palindromes = new ArrayList<>();

        if (length == 1) {
            for (int i = 1; i <= 9; i++) {
                palindromes.add(i);
            }
            return palindromes;
        }

        int halfLength = (length + 1) / 2;
        int start = (int) Math.pow(10, halfLength - 1);
        int end = (int) Math.pow(10, halfLength) - 1;

        for (int i = start; i <= end; i++) {
            String half = String.valueOf(i);
            String fullPalindrome;

            if (length % 2 == 0) {
                // Even length
                fullPalindrome = half + new StringBuilder(half).reverse().toString();
            } else {
                // Odd length
                fullPalindrome = half + new StringBuilder(half).reverse().substring(1);
            }

            try {
                int palindrome = Integer.parseInt(fullPalindrome);
                palindromes.add(palindrome);
            } catch (NumberFormatException e) {
                // Skip if number is too large
                break;
            }
        }

        return palindromes;
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(int[] testNumbers) {
        Map<String, Long> results = new HashMap<>();

        // Test string approach
        long start = System.nanoTime();
        for (int num : testNumbers) {
            isPalindrome(num);
        }
        results.put("String", System.nanoTime() - start);

        // Test math approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            isPalindromeMath(num);
        }
        results.put("Math", System.nanoTime() - start);

        // Test half reversal approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            isPalindromeHalf(num);
        }
        results.put("HalfReversal", System.nanoTime() - start);

        // Test array approach
        start = System.nanoTime();
        for (int num : testNumbers) {
            isPalindromeArray(num);
        }
        results.put("Array", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        PalindromeNumber solution = new PalindromeNumber();

        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");
        int[] testNumbers = { 121, -121, 10, 1221, 12321, 0, 1, 11, 123 };

        for (int num : testNumbers) {
            boolean result1 = solution.isPalindrome(num);
            boolean result2 = solution.isPalindromeMath(num);
            boolean result3 = solution.isPalindromeHalf(num);
            boolean result4 = solution.isPalindromeArray(num);

            System.out.printf("Number: %d | String: %b | Math: %b | Half: %b | Array: %b%n",
                    num, result1, result2, result3, result4);

            // Verify all approaches give same result
            boolean consistent = result1 == result2 && result2 == result3 && result3 == result4;
            if (!consistent) {
                System.out.println("  WARNING: Inconsistent results!");
            }
        }

        // Test Case 2: Edge cases
        System.out.println("\n=== Test Case 2: Edge Cases ===");
        int[] edgeCases = { Integer.MAX_VALUE, Integer.MIN_VALUE, 0, 1, 9, 99, 101, 1001 };

        for (int num : edgeCases) {
            boolean result = solution.isPalindromeHalf(num);
            System.out.println("Number: " + num + " -> " + result);
        }

        // Test Case 3: Different bases
        System.out.println("\n=== Test Case 3: Different Bases ===");
        int number = 9; // 9 in decimal

        for (int base = 2; base <= 10; base++) {
            boolean result = solution.isPalindromeBase(number, base);
            System.out.println("Number " + number + " in base " + base + ": " + result);
        }

        // Test Case 4: Find palindromes in range
        System.out.println("\n=== Test Case 4: Palindromes in Range ===");
        List<Integer> palindromes = solution.findPalindromes(1, 100);
        System.out.println("Palindromes from 1 to 100: " + palindromes);
        System.out.println("Count: " + palindromes.size());

        // Test Case 5: Generate palindromes efficiently
        System.out.println("\n=== Test Case 5: Generate Palindromes ===");
        List<Integer> generated = solution.generatePalindromes(1000);
        System.out.println("Generated palindromes up to 1000 (first 20): " +
                generated.subList(0, Math.min(20, generated.size())));
        System.out.println("Total count: " + generated.size());

        // Test Case 6: Can become palindrome
        System.out.println("\n=== Test Case 6: Can Become Palindrome ===");
        int[] candidates = { 121, 122, 123, 1221, 1231, 1234 };

        for (int num : candidates) {
            boolean canBecome = solution.canBecomePalindrome(num);
            System.out.println("Number " + num + " can become palindrome: " + canBecome);
        }

        // Test Case 7: Closest palindrome
        System.out.println("\n=== Test Case 7: Closest Palindrome ===");
        int[] searchNumbers = { 100, 120, 150, 200, 999 };

        for (int num : searchNumbers) {
            int closest = solution.closestPalindrome(num);
            System.out.println("Closest palindrome to " + num + ": " + closest);
        }

        // Test Case 8: Count palindromes
        System.out.println("\n=== Test Case 8: Count Palindromes ===");
        int count1 = solution.countPalindromes(1, 100);
        int count2 = solution.countPalindromes(100, 200);
        int count3 = solution.countPalindromes(1, 1000);

        System.out.println("Palindromes in [1, 100]: " + count1);
        System.out.println("Palindromes in [100, 200]: " + count2);
        System.out.println("Palindromes in [1, 1000]: " + count3);

        // Test Case 9: Largest/Smallest palindromes
        System.out.println("\n=== Test Case 9: Largest/Smallest Palindromes ===");
        int largest = solution.largestPalindromeLessThan(1000);
        int smallest = solution.smallestPalindromeGreaterThan(1000);

        System.out.println("Largest palindrome less than 1000: " + largest);
        System.out.println("Smallest palindrome greater than 1000: " + smallest);

        // Test Case 10: Sum of palindromes
        System.out.println("\n=== Test Case 10: Sum of Palindromes ===");
        long sum1 = solution.sumPalindromes(1, 100);
        long sum2 = solution.sumPalindromes(1, 1000);

        System.out.println("Sum of palindromes in [1, 100]: " + sum1);
        System.out.println("Sum of palindromes in [1, 1000]: " + sum2);

        // Test Case 11: Concatenation palindromes
        System.out.println("\n=== Test Case 11: Concatenation Palindromes ===");
        int[][] pairs = { { 12, 21 }, { 1, 1 }, { 10, 1 }, { 123, 321 } };

        for (int[] pair : pairs) {
            boolean result = solution.isConcatPalindrome(pair[0], pair[1]);
            System.out.println("Concat of " + pair[0] + " and " + pair[1] + ": " + result);
        }

        // Test Case 12: BigInteger palindromes
        System.out.println("\n=== Test Case 12: BigInteger Palindromes ===");
        BigInteger[] bigNumbers = {
                new BigInteger("12321"),
                new BigInteger("123454321"),
                new BigInteger("12345678987654321"),
                new BigInteger("1234567890987654321")
        };

        for (BigInteger bigNum : bigNumbers) {
            boolean result = solution.isPalindromeBigInteger(bigNum);
            System.out.println("BigInteger " + bigNum + ": " + result);
        }

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        int[] performanceTest = new int[10000];
        Random random = new Random(42);

        for (int i = 0; i < performanceTest.length; i++) {
            performanceTest[i] = random.nextInt(Integer.MAX_VALUE);
        }

        Map<String, Long> performance = solution.comparePerformance(performanceTest);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Memory usage test
        System.out.println("\n=== Memory Usage Test ===");
        Runtime runtime = Runtime.getRuntime();

        runtime.gc();
        long memBefore = runtime.totalMemory() - runtime.freeMemory();

        List<Integer> memoryTest = solution.findPalindromes(1, 10000);

        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        long memUsed = memAfter - memBefore;

        System.out.println("Memory used for finding palindromes 1-10000: " + memUsed / 1024 + " KB");
        System.out.println("Palindromes found: " + memoryTest.size());

        // Verification test
        System.out.println("\n=== Verification Test ===");
        boolean allCorrect = true;

        for (int i = 1; i <= 1000; i++) {
            boolean string = solution.isPalindrome(i);
            boolean math = solution.isPalindromeMath(i);
            boolean half = solution.isPalindromeHalf(i);
            boolean array = solution.isPalindromeArray(i);

            if (!(string == math && math == half && half == array)) {
                System.out.println("Inconsistency found at: " + i);
                allCorrect = false;
            }
        }

        System.out.println("All approaches consistent for 1-1000: " + allCorrect);

        System.out.println("\nPalindrome Number testing completed successfully!");
    }
}
