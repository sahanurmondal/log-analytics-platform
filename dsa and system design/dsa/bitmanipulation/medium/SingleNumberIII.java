package bitmanipulation.medium;

/**
 * LeetCode 260: Single Number III
 * https://leetcode.com/problems/single-number-iii/
 *
 * Description: Given an integer array nums, in which exactly two elements
 * appear only once and all the other elements appear exactly twice. Find the
 * two elements that appear only once.
 * 
 * Constraints:
 * - 2 <= nums.length <= 3 * 10^4
 * - -2^31 <= nums[i] <= 2^31 - 1
 * - Each integer in nums will appear twice, only two integers will appear once
 *
 * Follow-up:
 * - Can you solve it using XOR?
 * - What about the time complexity?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class SingleNumberIII {

    // Main optimized solution - XOR with bit differentiation
    public int[] singleNumber(int[] nums) {
        // XOR all numbers - result will be XOR of the two unique numbers
        int xor = 0;
        for (int num : nums) {
            xor ^= num;
        }

        // Find rightmost set bit to differentiate the two numbers
        int diff = xor & (-xor);

        int[] result = new int[2];
        for (int num : nums) {
            if ((num & diff) == 0) {
                result[0] ^= num;
            } else {
                result[1] ^= num;
            }
        }

        return result;
    }

    // Alternative solution - Using HashSet
    public int[] singleNumberHashSet(int[] nums) {
        java.util.Set<Integer> set = new java.util.HashSet<>();

        for (int num : nums) {
            if (set.contains(num)) {
                set.remove(num);
            } else {
                set.add(num);
            }
        }

        return set.stream().mapToInt(Integer::intValue).toArray();
    }

    public static void main(String[] args) {
        SingleNumberIII solution = new SingleNumberIII();

        System.out.println(java.util.Arrays.toString(solution.singleNumber(new int[] { 1, 2, 1, 3, 2, 5 }))); // Expected:
                                                                                                              // [3,5]
                                                                                                              // or
                                                                                                              // [5,3]
        System.out.println(java.util.Arrays.toString(solution.singleNumber(new int[] { -1, 0 }))); // Expected: [-1,0]
                                                                                                   // or [0,-1]
        System.out.println(java.util.Arrays.toString(solution.singleNumber(new int[] { 0, 1 }))); // Expected: [0,1] or
                                                                                                  // [1,0]
    }
}
