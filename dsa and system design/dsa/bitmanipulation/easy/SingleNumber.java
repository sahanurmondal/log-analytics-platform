package bitmanipulation.easy;

/**
 * LeetCode 136: Single Number
 * https://leetcode.com/problems/single-number/
 *
 * Description: Given a non-empty array of integers nums, every element appears
 * twice except for one. Find that single one.
 * You must implement a solution with a linear runtime complexity and use only
 * constant extra space.
 * 
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - -3 * 10^4 <= nums[i] <= 3 * 10^4
 * - Each element in the array appears twice except for one element which
 * appears only once
 *
 * Follow-up:
 * - Can you solve it without using extra memory?
 * - What if there are three duplicates instead of two?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. XOR Properties: a ^ a = 0, a ^ 0 = a
 * 2. XOR all elements - duplicates cancel out
 * 3. Alternative: Use HashSet but requires O(n) space
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft, Apple
 */
public class SingleNumber {

    // Main optimized solution - XOR
    public int singleNumber(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    // Alternative solution - HashSet (O(n) space)
    public int singleNumberHashSet(int[] nums) {
        java.util.Set<Integer> set = new java.util.HashSet<>();

        for (int num : nums) {
            if (set.contains(num)) {
                set.remove(num);
            } else {
                set.add(num);
            }
        }

        return set.iterator().next();
    }

    // Alternative solution - Mathematical approach
    public int singleNumberMath(int[] nums) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        int sumOfSet = 0, sumOfNums = 0;

        for (int num : nums) {
            if (!set.contains(num)) {
                set.add(num);
                sumOfSet += num;
            }
            sumOfNums += num;
        }

        return 2 * sumOfSet - sumOfNums;
    }

    public static void main(String[] args) {
        SingleNumber solution = new SingleNumber();

        // Test Case 1: Normal case
        System.out.println(solution.singleNumber(new int[] { 2, 2, 1 })); // Expected: 1

        // Test Case 2: Multiple duplicates
        System.out.println(solution.singleNumber(new int[] { 4, 1, 2, 1, 2 })); // Expected: 4

        // Test Case 3: Single element
        System.out.println(solution.singleNumber(new int[] { 1 })); // Expected: 1

        // Test Case 4: Negative numbers
        System.out.println(solution.singleNumber(new int[] { -1, -1, -2 })); // Expected: -2

        // Test Case 5: Zero included
        System.out.println(solution.singleNumber(new int[] { 0, 1, 0 })); // Expected: 1

        // Test Case 6: Large numbers
        System.out.println(solution.singleNumber(new int[] { 30000, -30000, 30000 })); // Expected: -30000

        // Test Case 7: Test HashSet approach
        System.out.println(solution.singleNumberHashSet(new int[] { 2, 2, 1 })); // Expected: 1

        // Test Case 8: Test Math approach
        System.out.println(solution.singleNumberMath(new int[] { 4, 1, 2, 1, 2 })); // Expected: 4

        // Test Case 9: Edge case with zero
        System.out.println(solution.singleNumber(new int[] { 1, 0, 1 })); // Expected: 0

        // Test Case 10: Complex case
        System.out.println(solution.singleNumber(new int[] { 1, 2, 3, 4, 5, 1, 2, 3, 4 })); // Expected: 5
    }
}
