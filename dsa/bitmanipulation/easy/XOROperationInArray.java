package bitmanipulation.easy;

/**
 * LeetCode 1486: XOR Operation in an Array
 * https://leetcode.com/problems/xor-operation-in-an-array/
 *
 * Description: Given an integer n and an integer start, define an array nums
 * where nums[i] = start + 2*i (0-indexed) and n == nums.length.
 * Return the bitwise XOR of all elements of nums.
 * 
 * Constraints:
 * - 1 <= n <= 1000
 * - 0 <= start <= 1000
 *
 * Follow-up:
 * - Can you solve it without creating the array?
 * - What about using XOR properties?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Company Tags: Google
 */
public class XOROperationInArray {

    // Main optimized solution - Direct XOR
    public int xorOperation(int n, int start) {
        int result = 0;
        for (int i = 0; i < n; i++) {
            result ^= start + 2 * i;
        }
        return result;
    }

    // Alternative solution - Using array
    public int xorOperationArray(int n, int start) {
        int[] nums = new int[n];
        for (int i = 0; i < n; i++) {
            nums[i] = start + 2 * i;
        }

        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    public static void main(String[] args) {
        XOROperationInArray solution = new XOROperationInArray();

        System.out.println(solution.xorOperation(5, 0)); // Expected: 8
        System.out.println(solution.xorOperation(4, 3)); // Expected: 8
        System.out.println(solution.xorOperation(1, 7)); // Expected: 7
        System.out.println(solution.xorOperation(10, 5)); // Expected: 2
    }
}
