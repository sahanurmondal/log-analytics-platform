package bitmanipulation.medium;

/**
 * LeetCode 268: Missing Number
 * https://leetcode.com/problems/missing-number/
 *
 * Description:
 * Given an array containing n distinct numbers in the range [0, n], return the
 * one that is missing from the array.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - 0 <= nums[i] <= n
 *
 * Follow-ups:
 * 1. Can you solve it in O(1) space and O(n) time?
 * 2. Can you solve it using bit manipulation (XOR)?
 * 3. Can you solve it using Gauss' formula (sum)?
 */
public class MissingNumber {
    /**
     * Main solution: XOR approach (O(n) time, O(1) space)
     */
    public int missingNumber(int[] nums) {
        int xor = 0;
        for (int i = 0; i < nums.length; i++) {
            xor ^= i ^ nums[i];
        }
        xor ^= nums.length;
        return xor;
    }

    /**
     * Follow-up 1: Gauss' formula (sum)
     */
    public int missingNumberSum(int[] nums) {
        int n = nums.length;
        int expected = n * (n + 1) / 2;
        int actual = 0;
        for (int num : nums)
            actual += num;
        return expected - actual;
    }

    /**
     * Follow-up 2: Find all missing numbers if array is not guaranteed to have only
     * one missing (generalized)
     */
    public java.util.List<Integer> findAllMissingNumbers(int[] nums) {
        int n = nums.length;
        boolean[] present = new boolean[n + 1];
        for (int num : nums) {
            if (num >= 0 && num <= n)
                present[num] = true;
        }
        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (int i = 0; i <= n; i++) {
            if (!present[i])
                res.add(i);
        }
        return res;
    }

    public static void main(String[] args) {
        MissingNumber solution = new MissingNumber();
        // Edge Case 1: Normal case
        System.out.println(solution.missingNumber(new int[] { 3, 0, 1 })); // 2
        // Edge Case 2: Missing is 0
        System.out.println(solution.missingNumber(new int[] { 1, 2, 3 })); // 0
        // Edge Case 3: Missing is n
        System.out.println(solution.missingNumber(new int[] { 0, 1, 2, 3 })); // 4
        // Edge Case 4: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 9999; i++)
            large[i] = i;
        large[9999] = 10000;
        System.out.println(solution.missingNumber(large)); // 9999

        // Follow-up 1: Gauss' formula
        System.out.println(solution.missingNumberSum(new int[] { 3, 0, 1 })); // 2
        System.out.println(solution.missingNumberSum(new int[] { 1, 2, 3 })); // 0
        System.out.println(solution.missingNumberSum(new int[] { 0, 1, 2, 3 })); // 4

        // Follow-up 2: Find all missing numbers (generalized)
        int[] arr = { 0, 1, 3, 5 };
        System.out.println(solution.findAllMissingNumbers(arr)); // [2, 4]
    }
    // ...existing code...
}
