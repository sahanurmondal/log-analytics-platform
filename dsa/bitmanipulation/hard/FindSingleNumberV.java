package bitmanipulation.hard;

import java.util.*;

/**
 * LeetCode Problem: Single Number V (Generalized)
 * URL: https://leetcode.com/problems/single-number-ii/ (variation - k=5 instead
 * of k=3)
 * Difficulty: Hard
 * 
 * Companies: Amazon, Google, Microsoft, Apple, Facebook
 * Frequency: Medium
 * 
 * Description:
 * Given an array where every element appears exactly five times except for one
 * element which appears exactly once, find that single element.
 * 
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^5
 * - -3 * 10^5 <= nums[i] <= 3 * 10^5
 * - All elements appear 5 times except one which appears once
 * 
 * Follow-up Questions:
 * 1. How would you solve for any k appearances?
 * 2. How would you handle multiple single numbers?
 * 3. Can you solve without extra space?
 * 4. How would you optimize for very large arrays?
 * 5. How would you handle floating point numbers?
 */
public class FindSingleNumberV {

    // Approach 1: Bit Manipulation - Count bits at each position
    public int singleNumber(int[] nums) {
        int result = 0;

        // Check each bit position (32 bits for int)
        for (int i = 0; i < 32; i++) {
            int count = 0;

            // Count how many numbers have bit set at position i
            for (int num : nums) {
                if ((num >> i & 1) == 1) {
                    count++;
                }
            }

            // If count is not divisible by 5, the single number has this bit set
            if (count % 5 != 0) {
                result |= (1 << i);
            }
        }

        return result;
    }

    // Approach 2: Digital Circuit Simulation (Finite State Machine)
    public int singleNumberFSM(int[] nums) {
        int ones = 0, twos = 0, threes = 0, fours = 0;

        for (int num : nums) {
            // Update counters using finite state machine
            int newFours = (threes & num) | (fours & ~num);
            int newThrees = (twos & num & ~fours) | (threes & ~num);
            int newTwos = (ones & num & ~threes & ~fours) | (twos & ~num & ~threes);
            int newOnes = (num & ~ones & ~twos & ~threes & ~fours) | (ones & ~num);

            ones = newOnes;
            twos = newTwos;
            threes = newThrees;
            fours = newFours;
        }

        return ones;
    }

    // Approach 3: HashMap counting (less optimal but clearer)
    public int singleNumberHashMap(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();

        for (int num : nums) {
            count.put(num, count.getOrDefault(num, 0) + 1);
        }

        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1) {
                return entry.getKey();
            }
        }

        return 0; // Should never reach here given constraints
    }

    // Approach 4: Generalized solution for any k
    public int singleNumberGeneralized(int[] nums, int k) {
        int result = 0;

        for (int i = 0; i < 32; i++) {
            int count = 0;
            for (int num : nums) {
                if ((num >> i & 1) == 1) {
                    count++;
                }
            }

            if (count % k != 0) {
                result |= (1 << i);
            }
        }

        return result;
    }

    // Approach 5: Using sorting (O(n log n) time)
    public int singleNumberSorting(int[] nums) {
        Arrays.sort(nums);

        for (int i = 0; i < nums.length; i += 5) {
            if (i + 4 >= nums.length || nums[i] != nums[i + 4]) {
                return nums[i];
            }
        }

        return 0;
    }

    // Follow-up: Find two single numbers (each appears once, others appear 5 times)
    public int[] findTwoSingleNumbers(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();

        for (int num : nums) {
            count.put(num, count.getOrDefault(num, 0) + 1);
        }

        List<Integer> singles = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1) {
                singles.add(entry.getKey());
            }
        }

        return singles.stream().mapToInt(i -> i).toArray();
    }

    // Follow-up: Memory-optimized for very large arrays
    public int singleNumberMemoryOptimized(int[] nums) {
        // Use only O(1) space by processing in chunks if needed
        int result = 0;
        int[] bitCounts = new int[32];

        // Count bits in single pass
        for (int num : nums) {
            for (int i = 0; i < 32; i++) {
                if ((num >> i & 1) == 1) {
                    bitCounts[i]++;
                }
            }
        }

        // Reconstruct result
        for (int i = 0; i < 32; i++) {
            if (bitCounts[i] % 5 != 0) {
                result |= (1 << i);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindSingleNumberV solution = new FindSingleNumberV();

        // Test Case 1: Basic example
        int[] test1 = { 2, 2, 2, 2, 2, 3 };
        System.out.println("Basic test: " + solution.singleNumber(test1)); // 3
        System.out.println("FSM approach: " + solution.singleNumberFSM(test1)); // 3
        System.out.println("HashMap approach: " + solution.singleNumberHashMap(test1)); // 3

        // Test Case 2: Negative numbers
        int[] test2 = { -1, -1, -1, -1, -1, -2 };
        System.out.println("Negative test: " + solution.singleNumber(test2)); // -2

        // Test Case 3: Zero and positive
        int[] test3 = { 0, 1, 1, 1, 1, 1 };
        System.out.println("With zero: " + solution.singleNumber(test3)); // 0

        // Test Case 4: Large numbers
        int[] test4 = { 100000, 100000, 100000, 100000, 100000, 99999 };
        System.out.println("Large numbers: " + solution.singleNumber(test4)); // 99999

        // Test Case 5: Single element
        int[] test5 = { 42 };
        System.out.println("Single element: " + solution.singleNumber(test5)); // 42

        // Test Case 6: Generalized k=3
        int[] test6 = { 2, 2, 2, 3 };
        System.out.println("Generalized k=3: " + solution.singleNumberGeneralized(test6, 3)); // 3

        // Test Case 7: Sorting approach
        int[] test7 = { 5, 5, 5, 5, 5, 7, 7, 7, 7, 7, 8 };
        System.out.println("Sorting approach: " + solution.singleNumberSorting(test7)); // 8

        // Test Case 8: Two single numbers
        int[] test8 = { 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 3, 4 };
        int[] twoSingles = solution.findTwoSingleNumbers(test8);
        System.out.println("Two singles: " + Arrays.toString(twoSingles)); // [3, 4]

        // Test Case 9: Memory optimized
        int[] test9 = { 9, 9, 9, 9, 9, 10 };
        System.out.println("Memory optimized: " + solution.singleNumberMemoryOptimized(test9)); // 10

        // Test Case 10: Mixed positive and negative
        int[] test10 = { -5, -5, -5, -5, -5, 7, 7, 7, 7, 7, -10 };
        System.out.println("Mixed signs: " + solution.singleNumber(test10)); // -10

        // Test Case 11: All same except one different
        int[] test11 = new int[26]; // 25 ones + 1 different
        Arrays.fill(test11, 0, 25, 1);
        test11[25] = 999;
        System.out.println("Many duplicates: " + solution.singleNumber(test11)); // 999

        // Test Case 12: Boundary values
        int[] test12 = { Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE,
                Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE };
        System.out.println("Boundary: " + solution.singleNumber(test12)); // Integer.MIN_VALUE
    }
}
