package slidingwindow.medium;

/**
 * LeetCode 1052: Grumpy Bookstore Owner
 * https://leetcode.com/problems/grumpy-bookstore-owner/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 5+ interviews)
 *
 * Description: Given customers and grumpy arrays, and integer X, maximize
 * satisfied customers by choosing a window of X minutes to suppress grumpiness.
 *
 * Constraints:
 * - 1 <= customers.length == grumpy.length <= 2 * 10^4
 * - 0 <= customers[i] <= 1000
 * - 0 <= grumpy[i] <= 1
 * - 1 <= X <= customers.length
 *
 * Follow-up Questions:
 * 1. What if you can suppress grumpiness in multiple windows?
 * 2. How to return the actual window?
 * 3. How to solve for very large arrays efficiently?
 */
public class GrumpyBookstoreOwner {
    // Approach 1: Sliding Window - O(n) time, O(1) space
    public int maxSatisfied(int[] customers, int[] grumpy, int X) {
        int base = 0, gain = 0, maxGain = 0;
        for (int i = 0; i < customers.length; i++) {
            if (grumpy[i] == 0)
                base += customers[i];
        }
        for (int i = 0; i < X; i++) {
            if (grumpy[i] == 1)
                gain += customers[i];
        }
        maxGain = gain;
        for (int i = X; i < customers.length; i++) {
            if (grumpy[i] == 1)
                gain += customers[i];
            if (grumpy[i - X] == 1)
                gain -= customers[i - X];
            maxGain = Math.max(maxGain, gain);
        }
        return base + maxGain;
    }

    // Approach 2: Prefix Sum - O(n) time, O(n) space
    public int maxSatisfiedPrefixSum(int[] customers, int[] grumpy, int X) {
        int n = customers.length;
        int[] prefix = new int[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + (grumpy[i] == 1 ? customers[i] : 0);
        }
        int base = 0;
        for (int i = 0; i < n; i++) {
            if (grumpy[i] == 0)
                base += customers[i];
        }
        int maxGain = 0;
        for (int i = X; i <= n; i++) {
            maxGain = Math.max(maxGain, prefix[i] - prefix[i - X]);
        }
        return base + maxGain;
    }

    // Follow-up 1: Multiple windows
    public int maxSatisfiedMultipleWindows(int[] customers, int[] grumpy, int X, int windows) {
        // Greedy: Not optimal, but for demo
        int n = customers.length;
        boolean[] suppressed = new boolean[n];
        int total = 0;
        for (int w = 0; w < windows; w++) {
            int maxGain = 0, start = 0;
            for (int i = 0; i <= n - X; i++) {
                int gain = 0;
                for (int j = i; j < i + X; j++) {
                    if (grumpy[j] == 1 && !suppressed[j])
                        gain += customers[j];
                }
                if (gain > maxGain) {
                    maxGain = gain;
                    start = i;
                }
            }
            for (int j = start; j < start + X; j++)
                suppressed[j] = true;
            total += maxGain;
        }
        for (int i = 0; i < n; i++) {
            if (grumpy[i] == 0 || suppressed[i])
                total += customers[i];
        }
        return total;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        GrumpyBookstoreOwner sol = new GrumpyBookstoreOwner();
        // Test 1: Basic
        System.out.println("Test 1: Expected 16 -> "
                + sol.maxSatisfied(new int[] { 1, 0, 1, 2, 1, 1, 7, 5 }, new int[] { 0, 1, 0, 1, 0, 1, 0, 1 }, 3));
        // Test 2: All not grumpy
        System.out.println("Test 2: Expected 15 -> "
                + sol.maxSatisfied(new int[] { 1, 2, 3, 4, 5 }, new int[] { 0, 0, 0, 0, 0 }, 2));
        // Test 3: All grumpy
        System.out.println("Test 3: Expected 15 -> "
                + sol.maxSatisfied(new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 1, 1, 1, 1 }, 5));
        // Test 4: Prefix sum approach
        System.out.println("Test 4: Expected 16 -> " + sol.maxSatisfiedPrefixSum(new int[] { 1, 0, 1, 2, 1, 1, 7, 5 },
                new int[] { 0, 1, 0, 1, 0, 1, 0, 1 }, 3));
        // Test 5: Multiple windows
        System.out.println("Test 5: Expected 22 -> " + sol.maxSatisfiedMultipleWindows(
                new int[] { 1, 0, 1, 2, 1, 1, 7, 5 }, new int[] { 0, 1, 0, 1, 0, 1, 0, 1 }, 2, 2));
        // Test 6: Edge case, X = 1
        System.out.println("Test 6: Expected 15 -> "
                + sol.maxSatisfied(new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 1, 1, 1, 1 }, 1));
        // Test 7: Edge case, X = customers.length
        System.out.println("Test 7: Expected 15 -> "
                + sol.maxSatisfied(new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 1, 1, 1, 1 }, 5));
        // Test 8: Large input
        int[] large = new int[10000];
        int[] grumpyLarge = new int[10000];
        for (int i = 0; i < 10000; i++) {
            large[i] = 1;
            grumpyLarge[i] = i % 2;
        }
        System.out.println("Test 8: Large input -> " + sol.maxSatisfied(large, grumpyLarge, 100));
        // Test 9: All zeros
        System.out.println(
                "Test 9: Expected 0 -> " + sol.maxSatisfied(new int[] { 0, 0, 0, 0 }, new int[] { 1, 1, 1, 1 }, 2));
        // Test 10: All ones
        System.out.println(
                "Test 10: Expected 4 -> " + sol.maxSatisfied(new int[] { 1, 1, 1, 1 }, new int[] { 1, 1, 1, 1 }, 4));
        // Test 11: X = 0
        System.out
                .println("Test 11: Expected 6 -> " + sol.maxSatisfied(new int[] { 1, 2, 3 }, new int[] { 0, 1, 0 }, 0));
        // Test 12: X = customers.length
        System.out
                .println("Test 12: Expected 6 -> " + sol.maxSatisfied(new int[] { 1, 2, 3 }, new int[] { 0, 1, 0 }, 3));
        // Test 13: Multiple windows, edge case
        System.out.println("Test 13: Expected 15 -> "
                + sol.maxSatisfiedMultipleWindows(new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 1, 1, 1, 1 }, 2, 2));
        // Test 14: Prefix sum, edge case
        System.out.println("Test 14: Expected 15 -> "
                + sol.maxSatisfiedPrefixSum(new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 1, 1, 1, 1 }, 5));
        // Test 15: All not grumpy, X = 1
        System.out.println("Test 15: Expected 15 -> "
                + sol.maxSatisfied(new int[] { 1, 2, 3, 4, 5 }, new int[] { 0, 0, 0, 0, 0 }, 1));
    }
}
