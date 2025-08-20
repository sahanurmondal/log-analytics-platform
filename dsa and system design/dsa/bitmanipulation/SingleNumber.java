package bitmanipulation;

/**
 * LeetCode 136: Single Number
 *
 * Description:
 * Given a non-empty array of integers, every element appears twice except for
 * one. Find that single one.
 *
 * Input: int[] nums
 * Output: int (the single number)
 * 
 * Constraints:
 * - 1 <= nums.length <= 3 * 10^4
 * - Each element appears twice except for one.
 * 
 * Solution Approaches:
 * 1. XOR (O(n) time, O(1) space)
 * Steps:
 * a. XOR all elements; result is the single number.
 * Time: O(n) for one pass.
 * Space: O(1).
 */
public class SingleNumber {
    /**
     * Main solution: XOR approach (O(n) time, O(1) space)
     */
    public int singleNumber(int[] nums) {
        int res = 0;
        for (int num : nums)
            res ^= num;
        return res;
    }

    /**
     * Follow-up: HashSet approach (O(n) time, O(n) space)
     */
    public int singleNumberSet(int[] nums) {
        java.util.Set<Integer> set = new java.util.HashSet<>();
        for (int num : nums) {
            if (!set.add(num))
                set.remove(num);
        }
        return set.iterator().next();
    }

    public static void main(String[] args) {
        SingleNumber solution = new SingleNumber();
        // Edge Case 1: Normal case
        System.out.println(solution.singleNumber(new int[] { 2, 2, 1 })); // 1
        // Edge Case 2: All elements same except one
        System.out.println(solution.singleNumber(new int[] { 4, 1, 2, 1, 2 })); // 4
        // Edge Case 3: Single element
        System.out.println(solution.singleNumber(new int[] { 99 })); // 99
        // Edge Case 4: Large input
        int[] large = new int[30001];
        for (int i = 0; i < 30000; i++)
            large[i] = i / 2;
        large[30000] = 123456;
        System.out.println(solution.singleNumber(large)); // 123456
        // Edge Case 5: Negative numbers
        System.out.println(solution.singleNumber(new int[] { -1, -1, -2 })); // -2
        // Edge Case 6: All negative except one positive
        System.out.println(solution.singleNumber(new int[] { -3, -3, 7 })); // 7
        // Edge Case 7: All positive except one negative
        System.out.println(solution.singleNumber(new int[] { 5, 5, -9 })); // -9
        // Edge Case 8: Zeros
        System.out.println(solution.singleNumber(new int[] { 0, 0, 8 })); // 8
        // Edge Case 9: Alternating values
        System.out.println(solution.singleNumber(new int[] { 1, 2, 1 })); // 2
        // Edge Case 10: Duplicates at ends
        System.out.println(solution.singleNumber(new int[] { 6, 7, 7, 6, 8 })); // 8

        // HashSet follow-up
        System.out.println(solution.singleNumberSet(new int[] { 2, 2, 1 })); // 1
        System.out.println(solution.singleNumberSet(new int[] { 4, 1, 2, 1, 2 })); // 4
    }
}
