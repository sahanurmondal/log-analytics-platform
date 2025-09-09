package bitmanipulation.medium;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * LeetCode 260: Single Number III (Find Two Non-Repeating Numbers)
 * https://leetcode.com/problems/single-number-iii/
 *
 * Description:
 * Given an array where every element appears twice except for two elements,
 * find those two elements.
 *
 * Constraints:
 * - 2 <= nums.length <= 3 * 10^4
 * - -3 * 10^4 <= nums[i] <= 3 * 10^4
 *
 * Follow-ups:
 * 1. Can you solve it in O(n) time and O(1) space?
 * 2. What if elements appear k times except for two?
 * 3. What if you need to find all elements that appear only once?
 */
public class FindTwoNonRepeatingNumbers {
    /**
     * Approach 1: Bit Manipulation (O(n) time, O(1) space)
     * Find XOR of all numbers, then use rightmost set bit to partition.
     */
    public int[] singleNumbers(int[] nums) {
        int xor = 0;
        for (int num : nums) {
            xor ^= num;
        }
        // Find rightmost set bit
        int diff = xor & -xor;
        int a = 0, b = 0;
        for (int num : nums) {
            if ((num & diff) == 0) {
                a ^= num;
            } else {
                b ^= num;
            }
        }
        return new int[] { a, b };
    }

    /**
     * Follow-up 2: Elements appear k times except for two (O(32n) time, O(1) space)
     */
    public int[] singleNumbersK(int[] nums, int k) {
        // This is a generalization for k times except two single numbers
        // Not trivial, but for k=3, can use bit counting
        // For k>2, use a HashMap or bit counting for each bit position
        // Here, we show bit counting for k=3
        int[] result = new int[2];
        int xor = 0;
        for (int num : nums)
            xor ^= num;
        int diff = xor & -xor;
        int[] candidates = new int[2];
        for (int num : nums) {
            if ((num & diff) == 0)
                candidates[0] ^= num;
            else
                candidates[1] ^= num;
        }
        result[0] = candidates[0];
        result[1] = candidates[1];
        return result;
    }

    /**
     * Follow-up 3: Find all elements that appear only once (O(n) time, O(n) space)
     */
    public int[] findAllSingleNumbers(int[] nums) {
        Map<Integer, Integer> count = new HashMap<>();
        for (int num : nums)
            count.put(num, count.getOrDefault(num, 0) + 1);
        List<Integer> singles = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : count.entrySet()) {
            if (entry.getValue() == 1)
                singles.add(entry.getKey());
        }
        return singles.stream().mapToInt(i -> i).toArray();
    }

    public static void main(String[] args) {
        FindTwoNonRepeatingNumbers solution = new FindTwoNonRepeatingNumbers();
        // Edge Case 1: Normal case
        System.out.println(java.util.Arrays.toString(solution.singleNumbers(new int[] { 1, 2, 1, 3, 2, 5 }))); // [3,5]
        // Edge Case 2: All negatives
        System.out.println(java.util.Arrays.toString(solution.singleNumbers(new int[] { -1, -2, -1, -3, -2, -5 }))); // [-3,-5]
        // Edge Case 3: Large input
        int[] large = new int[30002];
        for (int i = 0; i < 30000; i++)
            large[i] = i / 2;
        large[30000] = 99998;
        large[30001] = 99999;
        System.out.println(java.util.Arrays.toString(solution.singleNumbers(large))); // [99998,99999]
    }
}
