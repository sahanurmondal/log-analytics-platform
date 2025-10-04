package bitmanipulation.medium;

/**
 * LeetCode 136: Find Single Bit
 * https://leetcode.com/problems/single-bit/
 * Difficulty: Medium
 *
 * Description:
 * Given an integer, return the position (0-based) of the only set bit.
 * If the number has zero or more than one bit set, return -1.
 * 
 * Example:
 * Input: n = 8 (1000 in binary)
 * Output: 3
 * Explanation: Only the 4th bit (0-based index 3) is set
 *
 * Constraints:
 * - 0 <= n <= 2^31 - 1
 * 
 * Follow-up:
 * 1. Can you do it without using any built-in functions?
 * 2. Can you do it in O(1) time?
 * 3. What if you need to find positions of multiple set bits?
 */
public class FindSingleBit {

    // Approach 1: Using power of 2 check - O(1) time, O(1) space
    public int findSingleBit(int n) {
        // Check if n is a power of 2
        if (n <= 0 || (n & (n - 1)) != 0) {
            return -1;
        }
        return Integer.numberOfTrailingZeros(n);
    }

    // Approach 2: Bit scanning - O(32) time, O(1) space
    public int findSingleBitScan(int n) {
        if (n <= 0)
            return -1;

        int count = 0;
        int position = -1;

        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                count++;
                position = i;
                if (count > 1)
                    return -1;
            }
        }

        return count == 1 ? position : -1;
    }

    // Approach 3: Using logarithm - O(1) time, O(1) space
    public int findSingleBitLog(int n) {
        if (n <= 0 || (n & (n - 1)) != 0) {
            return -1;
        }
        return (int) (Math.log(n) / Math.log(2));
    }

    // Approach 4: Binary search on bits - O(log 32) time, O(1) space
    public int findSingleBitBinarySearch(int n) {
        if (n <= 0 || (n & (n - 1)) != 0) {
            return -1;
        }

        int left = 0, right = 30;
        while (left < right) {
            int mid = (left + right) / 2;
            if ((n >>> mid) > 1) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }

    // Follow-up: Find positions of all set bits
    public int[] findAllSetBits(int n) {
        if (n <= 0)
            return new int[0];

        int count = Integer.bitCount(n);
        int[] positions = new int[count];
        int index = 0;

        for (int i = 0; i < 32; i++) {
            if ((n & (1 << i)) != 0) {
                positions[index++] = i;
            }
        }

        return positions;
    }

    public static void main(String[] args) {
        FindSingleBit solution = new FindSingleBit();

        // Test Case 1: Normal case
        System.out.println("Test 1: " + solution.findSingleBit(8)); // 3

        // Test Case 2: Multiple bits set
        System.out.println("Test 2: " + solution.findSingleBit(10)); // -1

        // Test Case 3: Zero
        System.out.println("Test 3: " + solution.findSingleBit(0)); // -1

        // Test Case 4: Power of 2
        System.out.println("Test 4: " + solution.findSingleBit(16)); // 4

        // Test all approaches
        int[] testCases = { 8, 10, 0, 16, 1 << 30, 7, 1 };

        System.out.println("\nTesting all approaches:");
        for (int test : testCases) {
            int result1 = solution.findSingleBit(test);
            int result2 = solution.findSingleBitScan(test);
            int result3 = solution.findSingleBitLog(test);
            int result4 = solution.findSingleBitBinarySearch(test);

            boolean consistent = result1 == result2 && result2 == result3 && result3 == result4;
            System.out.printf("Number %d: %d (consistent: %b)%n", test, result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int iterations = 10000000;
        long start;

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findSingleBit(1 << 30);
        }
        System.out.println("Power of 2: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findSingleBitScan(1 << 30);
        }
        System.out.println("Scan: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findSingleBitLog(1 << 30);
        }
        System.out.println("Log: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.findSingleBitBinarySearch(1 << 30);
        }
        System.out.println("Binary Search: " + (System.currentTimeMillis() - start) + "ms");

        // Test finding all set bits
        System.out.println("\nAll set bits test:");
        int[] cases = { 10, 15, 1 << 30, 0, 1 };
        for (int test : cases) {
            System.out.printf("Number %d: %s%n", test,
                    java.util.Arrays.toString(solution.findAllSetBits(test)));
        }

        // Special patterns
        System.out.println("\nSpecial patterns:");
        System.out.println("Highest bit: " +
                solution.findSingleBit(Integer.MIN_VALUE >>> 1)); // 30
        System.out.println("Alternating bits: " +
                solution.findSingleBit(0x55555555)); // -1
        System.out.println("Lowest bit: " +
                solution.findSingleBit(1)); // 0
    }
}
