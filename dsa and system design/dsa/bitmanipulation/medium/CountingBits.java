package bitmanipulation.medium;

/**
 * LeetCode 338: Counting Bits
 * https://leetcode.com/problems/counting-bits/
 *
 * Description:
 * Given a non-negative integer num, return an array of the number of 1's in the
 * binary representation of every number in the range [0, num].
 * 
 * Example:
 * Input: num = 5
 * Output: [0,1,1,2,1,2]
 * Explanation:
 * 0 --> 0 (0)
 * 1 --> 1 (1)
 * 2 --> 10 (1)
 * 3 --> 11 (2)
 * 4 --> 100 (1)
 * 5 --> 101 (2)
 *
 * Constraints:
 * - 0 <= num <= 10^5
 * 
 * Follow-up:
 * 1. Can you do it in O(n) time?
 * 2. Can you do it without using built-in functions?
 * 3. Can you find multiple patterns to solve this?
 */
public class CountingBits {

    // Approach 1: DP with Least Significant Bit - O(n) time, O(1) space
    public int[] countBits(int num) {
        int[] result = new int[num + 1];
        for (int i = 1; i <= num; i++) {
            result[i] = result[i >> 1] + (i & 1);
        }
        return result;
    }

    // Approach 2: DP with Last Set Bit - O(n) time, O(1) space
    public int[] countBitsLastSetBit(int num) {
        int[] result = new int[num + 1];
        for (int i = 1; i <= num; i++) {
            result[i] = result[i & (i - 1)] + 1;
        }
        return result;
    }

    // Approach 3: Brian Kernighan's Algorithm - O(nlogn) time, O(1) space
    public int[] countBitsBrianKernighan(int num) {
        int[] result = new int[num + 1];
        for (int i = 0; i <= num; i++) {
            int count = 0;
            int n = i;
            while (n != 0) {
                n &= (n - 1);
                count++;
            }
            result[i] = count;
        }
        return result;
    }

    // Approach 4: Built-in Function - O(n) time, O(1) space
    public int[] countBitsBuiltin(int num) {
        int[] result = new int[num + 1];
        for (int i = 0; i <= num; i++) {
            result[i] = Integer.bitCount(i);
        }
        return result;
    }

    // Approach 5: Using lookup table - O(n) time, O(1) space
    public int[] countBitsLookup(int num) {
        int[] result = new int[num + 1];
        byte[] lookup = new byte[256];

        // Precompute lookup table for 8 bits
        for (int i = 0; i < 256; i++) {
            lookup[i] = (byte) (lookup[i / 2] + (i & 1));
        }

        for (int i = 0; i <= num; i++) {
            result[i] = lookup[i & 0xff] +
                    lookup[(i >> 8) & 0xff] +
                    lookup[(i >> 16) & 0xff] +
                    lookup[(i >> 24) & 0xff];
        }
        return result;
    }

    public static void main(String[] args) {
        CountingBits solution = new CountingBits();

        // Test Case 1: Normal case
        System.out.println("Test 1: " +
                java.util.Arrays.toString(solution.countBits(5))); // [0,1,1,2,1,2]

        // Test Case 2: Zero
        System.out.println("Test 2: " +
                java.util.Arrays.toString(solution.countBits(0))); // [0]

        // Test Case 3: Power of 2
        System.out.println("Test 3: " +
                java.util.Arrays.toString(solution.countBits(8))); // [0,1,1,2,1,2,2,3,1]

        // Compare all approaches
        int num = 15;
        int[][] results = new int[5][];
        results[0] = solution.countBits(num); // DP with LSB
        results[1] = solution.countBitsLastSetBit(num); // DP with Last Set Bit
        results[2] = solution.countBitsBrianKernighan(num);// Brian Kernighan's
        results[3] = solution.countBitsBuiltin(num); // Built-in
        results[4] = solution.countBitsLookup(num); // Lookup Table

        // Verify all approaches give same result
        boolean allMatch = true;
        for (int i = 1; i < results.length; i++) {
            if (!java.util.Arrays.equals(results[0], results[i])) {
                allMatch = false;
                System.out.println("Mismatch in approach " + i);
            }
        }
        System.out.println("All approaches consistent: " + allMatch);

        // Performance test
        int testNum = 100000;
        long[] times = new long[5];

        for (int i = 0; i < 5; i++) {
            long start = System.currentTimeMillis();
            switch (i) {
                case 0:
                    solution.countBits(testNum);
                    break;
                case 1:
                    solution.countBitsLastSetBit(testNum);
                    break;
                case 2:
                    solution.countBitsBrianKernighan(testNum);
                    break;
                case 3:
                    solution.countBitsBuiltin(testNum);
                    break;
                case 4:
                    solution.countBitsLookup(testNum);
                    break;
            }
            times[i] = System.currentTimeMillis() - start;
        }

        System.out.println("\nPerformance Results:");
        System.out.println("DP with LSB: " + times[0] + "ms");
        System.out.println("DP with Last Set Bit: " + times[1] + "ms");
        System.out.println("Brian Kernighan: " + times[2] + "ms");
        System.out.println("Built-in: " + times[3] + "ms");
        System.out.println("Lookup Table: " + times[4] + "ms");
    }
}
