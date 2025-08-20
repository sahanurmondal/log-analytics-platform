package bitmanipulation.easy;

/**
 * LeetCode 268: Missing Number
 * https://leetcode.com/problems/missing-number/
 *
 * Description: Given an array nums containing n distinct numbers in the range
 * [0, n], return the only number in the range that is missing from the array.
 * 
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 10^4
 * - 0 <= nums[i] <= n
 * - All the numbers of nums are unique
 *
 * Follow-up:
 * - Can you solve it using XOR?
 * - What about using mathematical formula?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(1)
 * 
 * Algorithm:
 * 1. XOR: XOR all numbers 0 to n with array elements
 * 2. Math: Sum(0 to n) - Sum(array elements)
 * 3. HashSet: Check which number is missing
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft, Apple
 */
public class MissingNumber {

    // Main optimized solution - XOR
    public int missingNumber(int[] nums) {
        int missing = nums.length;
        for (int i = 0; i < nums.length; i++) {
            missing ^= i ^ nums[i];
        }
        return missing;
    }

    // Alternative solution - Mathematical
    public int missingNumberMath(int[] nums) {
        int n = nums.length;
        int expectedSum = n * (n + 1) / 2;
        int actualSum = 0;

        for (int num : nums) {
            actualSum += num;
        }

        return expectedSum - actualSum;
    }

    // Alternative solution - HashSet
    public int missingNumberHashSet(int[] nums) {
        java.util.Set<Integer> numSet = new java.util.HashSet<>();
        for (int num : nums) {
            numSet.add(num);
        }

        for (int i = 0; i <= nums.length; i++) {
            if (!numSet.contains(i)) {
                return i;
            }
        }

        return -1; // Should never reach here
    }

    // Alternative solution - Binary search (if array was sorted)
    public int missingNumberBinarySearch(int[] nums) {
        java.util.Arrays.sort(nums);
        int left = 0, right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] == mid) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    public static void main(String[] args) {
        MissingNumber solution = new MissingNumber();

        // Test Case 1: Normal case
        System.out.println(solution.missingNumber(new int[] { 3, 0, 1 })); // Expected: 2

        // Test Case 2: Missing first number
        System.out.println(solution.missingNumber(new int[] { 1, 2 })); // Expected: 0

        // Test Case 3: Missing last number
        System.out.println(solution.missingNumber(new int[] { 0, 1 })); // Expected: 2

        // Test Case 4: Single element
        System.out.println(solution.missingNumber(new int[] { 1 })); // Expected: 0

        // Test Case 5: Large range
        System.out.println(solution.missingNumber(new int[] { 9, 6, 4, 2, 3, 5, 7, 0, 1 })); // Expected: 8

        // Test Case 6: Test mathematical approach
        System.out.println(solution.missingNumberMath(new int[] { 3, 0, 1 })); // Expected: 2

        // Test Case 7: Test HashSet approach
        System.out.println(solution.missingNumberHashSet(new int[] { 3, 0, 1 })); // Expected: 2

        // Test Case 8: Test binary search
        System.out.println(solution.missingNumberBinarySearch(new int[] { 3, 0, 1 })); // Expected: 2

        // Test Case 9: Empty except one
        System.out.println(solution.missingNumber(new int[] { 0 })); // Expected: 1

        // Test Case 10: Complex case
        System.out.println(solution.missingNumber(new int[] { 0, 1, 3, 4, 5 })); // Expected: 2
    }
}
