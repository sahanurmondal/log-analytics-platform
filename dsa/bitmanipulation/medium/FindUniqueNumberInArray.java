package bitmanipulation.medium;

/**
 * Variation: Find Unique Number in Array
 *
 * Description:
 * Given an array where every element appears k times except for one, find that
 * single one.
 *
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - -3 * 10^4 <= nums[i] <= 3 * 10^4
 * - 2 <= k <= 10
 *
 * LeetCode Link: https://leetcode.com/problems/single-number-ii/
 * Problem No: 137 (Generalized for k)
 *
 * Follow-ups:
 * 1. Can you solve it in O(1) extra space?
 * 2. Can you generalize for any k?
 * 3. What if the array contains more than one unique number?
 */
public class FindUniqueNumberInArray {
    /**
     * Main solution: Generalized bit counting for any k
     * O(32 * n) time, O(1) space
     */
    public int singleNumber(int[] nums, int k) {
        int result = 0;
        for (int i = 0; i < 32; i++) {
            int sum = 0;
            for (int num : nums) {
                sum += (num >> i) & 1;
            }
            if (sum % k != 0) {
                result |= (1 << i);
            }
        }
        return result;
    }

    /**
     * Follow-up 1: O(1) extra space for k=3 (classic)
     * Use two variables to track bits appearing once and twice
     */
    public int singleNumberK3(int[] nums) {
        int ones = 0, twos = 0;
        for (int num : nums) {
            ones = (ones ^ num) & ~twos;
            twos = (twos ^ num) & ~ones;
        }
        return ones;
    }

    /**
     * Follow-up 2: Find all unique numbers (if more than one)
     * Returns a list of all numbers appearing once
     */
    public java.util.List<Integer> findAllUniqueNumbers(int[] nums, int k) {
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        for (int num : nums)
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        java.util.List<Integer> res = new java.util.ArrayList<>();
        for (java.util.Map.Entry<Integer, Integer> e : freq.entrySet()) {
            if (e.getValue() == 1)
                res.add(e.getKey());
        }
        return res;
    }

    public static void main(String[] args) {
        FindUniqueNumberInArray solution = new FindUniqueNumberInArray();
        // Edge Case 1: Normal case
        System.out.println(solution.singleNumber(new int[] { 2, 2, 2, 3 }, 3)); // 3
        // Edge Case 2: All negatives
        System.out.println(solution.singleNumber(new int[] { -1, -1, -1, -2 }, 3)); // -2
        // Edge Case 3: Large input
        int[] large = new int[30001];
        for (int i = 0; i < 30000; i++)
            large[i] = i / 3;
        large[30000] = 99999;
        System.out.println(solution.singleNumber(large, 3)); // 99999

        // Follow-up 1: k=3, O(1) space
        System.out.println(solution.singleNumberK3(new int[] { 2, 2, 2, 3 })); // 3
        System.out.println(solution.singleNumberK3(new int[] { -1, -1, -1, -2 })); // -2

        // Follow-up 2: More than one unique number
        int[] arr = { 2, 2, 2, 3, 4, 4, 4, 5 };
        System.out.println(solution.findAllUniqueNumbers(arr, 3)); // [3, 5]
    }
    // ...existing code...
}
