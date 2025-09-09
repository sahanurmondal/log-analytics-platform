package sorting.medium;

/**
 * LeetCode 179: Largest Number
 * https://leetcode.com/problems/largest-number/
 *
 * Description:
 * Given a list of non-negative integers nums, arrange them such that they form
 * the largest number and return it.
 *
 * Constraints:
 * - 1 <= nums.length <= 100
 * - 0 <= nums[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it using a custom comparator?
 * - How do you handle the edge case where all numbers are zero?
 * - Can you prove the correctness of your comparator?
 */
import java.util.Arrays;

/**
 * LeetCode 179: Largest Number
 * https://leetcode.com/problems/largest-number/
 *
 * Description:
 * Given a list of non-negative integers nums, arrange them such that they form
 * the largest number and return it.
 *
 * Constraints:
 * - 1 <= nums.length <= 100
 * - 0 <= nums[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it using a custom comparator?
 * - How do you handle the edge case where all numbers are zero?
 * - Can you prove the correctness of your comparator?
 */
public class LargestNumber {
    public String largestNumber(int[] nums) {
        String[] strs = new String[nums.length];
        for (int i = 0; i < nums.length; i++) {
            strs[i] = String.valueOf(nums[i]);
        }

        // Custom comparator: compare which concatenation is larger
        Arrays.sort(strs, (a, b) -> (b + a).compareTo(a + b));

        // Handle edge case: all zeros
        if (strs[0].equals("0")) {
            return "0";
        }

        StringBuilder result = new StringBuilder();
        for (String str : strs) {
            result.append(str);
        }

        return result.toString();
    }

    public static void main(String[] args) {
        LargestNumber solution = new LargestNumber();

        System.out.println(solution.largestNumber(new int[] { 10, 2 })); // "210"
        System.out.println(solution.largestNumber(new int[] { 3, 30, 34, 5, 9 })); // "9534330"

        // Edge Case: All zeros
        System.out.println(solution.largestNumber(new int[] { 0, 0 })); // "0"

        // Edge Case: Single digit numbers
        System.out.println(solution.largestNumber(new int[] { 1, 2, 3, 4, 5 })); // "54321"

        // Edge Case: Numbers with different lengths
        System.out.println(solution.largestNumber(new int[] { 54, 546, 548, 60 })); // "6054854654"

        // Edge Case: Single number
        System.out.println(solution.largestNumber(new int[] { 1 })); // "1"

        // Edge Case: Leading zeros
        System.out.println(solution.largestNumber(new int[] { 0, 9, 8, 1 })); // "9810"
    }
}
