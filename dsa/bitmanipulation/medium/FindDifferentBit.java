package bitmanipulation.medium;

/**
 * LeetCode 1076: Find Different Bit
 * https://leetcode.com/problems/different-bit/
 * Difficulty: Medium
 *
 * Description:
 * Given two integers m and n, return the position of the rightmost different
 * bit (1-based).
 * Return -1 if the numbers are identical.
 * 
 * Example:
 * Input: m = 11 (1011), n = 9 (1001)
 * Output: 2
 * Explanation: The second bit from right is different (1 in m, 0 in n)
 *
 * Constraints:
 * - 0 <= m, n <= 2^31 - 1
 * 
 * Follow-up:
 * 1. Can you solve it in O(1) time?
 * 2. Can you solve it with minimum operations?
 * 3. What if you need to find all different bits?
 */
public class FindDifferentBit {

    // Approach 1: Using XOR and rightmost set bit - O(1) time, O(1) space
    public int findDifferentBit(int m, int n) {
        if (m == n)
            return -1;

        // XOR to find different bits
        int xor = m ^ n;

        // Find position of rightmost set bit
        return Integer.numberOfTrailingZeros(xor) + 1;
    }

    // Approach 2: Using bit manipulation - O(32) time, O(1) space
    public int findDifferentBitIterate(int m, int n) {
        if (m == n)
            return -1;

        for (int i = 0; i < 32; i++) {
            if (((m >> i) & 1) != ((n >> i) & 1)) {
                return i + 1;
            }
        }
        return -1; // Should never reach here given constraints
    }

    // Approach 3: Using Brian Kernighan's algorithm - O(1) time, O(1) space
    public int findDifferentBitKernighan(int m, int n) {
        if (m == n)
            return -1;

        // Get rightmost set bit of XOR
        int xor = m ^ n;
        return Integer.numberOfTrailingZeros(xor & -xor) + 1;
    }

    // Approach 4: Using binary search on bits - O(log 32) time, O(1) space
    public int findDifferentBitBinarySearch(int m, int n) {
        if (m == n)
            return -1;

        int left = 0, right = 31;
        while (left < right) {
            int mid = (left + right) / 2;
            int mask = (1 << (mid + 1)) - 1;
            if ((m & mask) != (n & mask)) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left + 1;
    }

    // Follow-up: Find all different bits
    public int[] findAllDifferentBits(int m, int n) {
        if (m == n)
            return new int[0];

        int xor = m ^ n;
        int count = Integer.bitCount(xor);
        int[] result = new int[count];
        int index = 0;

        for (int i = 0; i < 32; i++) {
            if ((xor & (1 << i)) != 0) {
                result[index++] = i + 1;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindDifferentBit solution = new FindDifferentBit();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.findDifferentBit(11, 9)); // 2

        // Test Case 2: Same numbers
        System.out.println("Test 2: " + solution.findDifferentBit(8, 8)); // -1

        // Test Case 3: Zero and one
        System.out.println("Test 3: " + solution.findDifferentBit(0, 1)); // 1

        // Test Case 4: Powers of 2
        System.out.println("Test 4: " + solution.findDifferentBit(16, 24)); // 4

        // Test all approaches
        int[][] testCases = {
                { 11, 9 }, // Normal case
                { 8, 8 }, // Same numbers
                { 0, 1 }, // Zero and one
                { 16, 24 }, // Powers of 2
                { 0, Integer.MAX_VALUE } // Large difference
        };

        System.out.println("\nTesting all approaches:");
        for (int[] test : testCases) {
            int result1 = solution.findDifferentBit(test[0], test[1]);
            int result2 = solution.findDifferentBitIterate(test[0], test[1]);
            int result3 = solution.findDifferentBitKernighan(test[0], test[1]);
            int result4 = solution.findDifferentBitBinarySearch(test[0], test[1]);

            boolean consistent = result1 == result2 && result2 == result3 && result3 == result4;
            System.out.printf("Numbers %d, %d: %d (consistent: %b)%n",
                    test[0], test[1], result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int iterations = 10000000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findDifferentBit(600000000, 2147483645);
        }
        System.out.println("XOR: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findDifferentBitIterate(600000000, 2147483645);
        }
        System.out.println("Iterate: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findDifferentBitKernighan(600000000, 2147483645);
        }
        System.out.println("Kernighan: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findDifferentBitBinarySearch(600000000, 2147483645);
        }
        System.out.println("Binary Search: " + (System.currentTimeMillis() - start) + "ms");

        // Test finding all different bits
        System.out.println("\nFind all different bits:");
        int m = 11, n = 9;
        int[] allDiffs = solution.findAllDifferentBits(m, n);
        System.out.printf("Different bits between %d and %d: %s%n",
                m, n, java.util.Arrays.toString(allDiffs));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Max vs 0: " + solution.findDifferentBit(0, Integer.MAX_VALUE));
        System.out.println("Adjacent numbers: " + solution.findDifferentBit(15, 14));
        System.out.println("Sparse vs dense: " +
                solution.findDifferentBit(0x55555555, 0xAAAAAAAA));
    }
}
