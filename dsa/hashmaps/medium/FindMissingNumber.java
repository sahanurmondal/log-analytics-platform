package hashmaps.medium;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LeetCode 268: Missing Number
 * https://leetcode.com/problems/missing-number/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 10+ interviews)
 *
 * Description: Given an array `nums` containing `n` distinct numbers in the
 * range `[0, n]`, return the only number in the range that is missing from the
 * array.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 10^4
 * - 0 <= nums[i] <= n
 * - All the numbers of `nums` are unique.
 * 
 * Follow-up Questions:
 * 1. Can you solve this with O(1) extra space complexity and O(n) runtime
 * complexity?
 * 2. How would you solve this using bit manipulation (XOR)?
 * 3. What if there were multiple missing numbers?
 */
public class FindMissingNumber {

    // Approach 1: HashSet - O(n) time, O(n) space
    public int missingNumber(int[] nums) {
        Set<Integer> numSet = new HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        int n = nums.length;
        for (int i = 0; i <= n; i++) {
            if (!numSet.contains(i)) {
                return i;
            }
        }

        return -1; // Should not happen based on problem constraints
    }

    // Approach 2: Gauss' Formula (Summation) - O(n) time, O(1) space
    public int missingNumberMath(int[] nums) {
        int n = nums.length;
        int expectedSum = n * (n + 1) / 2;
        int actualSum = 0;
        for (int num : nums) {
            actualSum += num;
        }
        return expectedSum - actualSum;
    }

    // Approach 3: Bit Manipulation (XOR) - O(n) time, O(1) space
    public int missingNumberXOR(int[] nums) {
        int missing = nums.length;
        for (int i = 0; i < nums.length; i++) {
            missing ^= i ^ nums[i];
        }
        return missing;
    }

    // Approach 4: Sorting - O(n log n) time, O(1) or O(n) space
    public int missingNumberSort(int[] nums) {
        Arrays.sort(nums);
        if (nums[nums.length - 1] != nums.length) {
            return nums.length;
        }
        if (nums[0] != 0) {
            return 0;
        }
        for (int i = 1; i < nums.length; i++) {
            if (nums[i] != nums[i - 1] + 1) {
                return nums[i - 1] + 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        FindMissingNumber solution = new FindMissingNumber();

        // Test case 1
        int[] nums1 = { 3, 0, 1 };
        System.out.println("Missing (Set): " + solution.missingNumber(nums1)); // 2
        System.out.println("Missing (Math): " + solution.missingNumberMath(nums1)); // 2
        System.out.println("Missing (XOR): " + solution.missingNumberXOR(nums1)); // 2
        System.out.println("Missing (Sort): " + solution.missingNumberSort(nums1)); // 2

        // Test case 2
        int[] nums2 = { 0, 1 };
        System.out.println("Missing 2: " + solution.missingNumber(nums2)); // 2

        // Test case 3
        int[] nums3 = { 9, 6, 4, 2, 3, 5, 7, 0, 1 };
        System.out.println("Missing 3: " + solution.missingNumber(nums3)); // 8
    }
}
