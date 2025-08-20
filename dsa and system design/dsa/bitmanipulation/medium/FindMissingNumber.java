package bitmanipulation.medium;

/**
 * LeetCode 268: Missing Number
 * https://leetcode.com/problems/missing-number/
 *
 * Description:
 * Given an array nums containing n distinct numbers in the range [0, n],
 * return the only number in the range that is missing from the array.
 * 
 * Example:
 * Input: nums = [3,0,1]
 * Output: 2
 * Explanation: n = 3 since there are 3 numbers, so all numbers are in the range
 * [0,3].
 * 2 is the missing number since it does not appear in nums.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 10^4
 * - 0 <= nums[i] <= n
 * - All the numbers of nums are unique
 * 
 * Follow-up:
 * 1. Can you implement a solution using bit manipulation?
 * 2. Could you solve it without extra space?
 * 3. Could you modify the input array to solve it?
 */
public class FindMissingNumber {

    // Approach 1: XOR - O(n) time, O(1) space
    public int missingNumber(int[] nums) {
        int missing = nums.length;
        for (int i = 0; i < nums.length; i++) {
            missing ^= i ^ nums[i];
        }
        return missing;
    }

    // Approach 2: Gauss Formula - O(n) time, O(1) space
    public int missingNumberGauss(int[] nums) {
        int n = nums.length;
        int expectedSum = (n * (n + 1)) / 2;
        int actualSum = 0;
        for (int num : nums) {
            actualSum += num;
        }
        return expectedSum - actualSum;
    }

    // Approach 3: Binary Search - O(n log n) time, O(1) space
    public int missingNumberBinarySearch(int[] nums) {
        java.util.Arrays.sort(nums);
        int left = 0, right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] > mid) {
                right = mid;
            } else {
                left = mid + 1;
            }
        }
        return left;
    }

    // Approach 4: Bit Manipulation with Position - O(n) time, O(1) space
    public int missingNumberBitPosition(int[] nums) {
        int result = 0;
        int i = 0;

        // XOR all numbers from 0 to n-1 with their indices
        for (; i < nums.length; i++) {
            result ^= (i ^ nums[i]);
        }
        // XOR with the last number n
        result ^= i;

        return result;
    }

    // Approach 5: In-place modification - O(n) time, O(1) space
    public int missingNumberInPlace(int[] nums) {
        int n = nums.length;

        // Mark visited numbers
        for (int i = 0; i < n; i++) {
            int val = Math.abs(nums[i]);
            if (val < n) {
                nums[val] = nums[val] == 0 ? -(n + 1) : -Math.abs(nums[val]);
            }
        }

        // Find unmarked index
        for (int i = 0; i < n; i++) {
            if (nums[i] >= 0) {
                return i;
            }
        }

        return n;
    }

    public static void main(String[] args) {
        FindMissingNumber solution = new FindMissingNumber();

        // Test Case 1: Normal case
        System.out.println("Test 1: " +
                solution.missingNumber(new int[] { 3, 0, 1 })); // 2

        // Test Case 2: Missing first number
        System.out.println("Test 2: " +
                solution.missingNumber(new int[] { 1, 2, 3 })); // 0

        // Test Case 3: Missing last number
        System.out.println("Test 3: " +
                solution.missingNumber(new int[] { 0, 1, 2 })); // 3

        // Test Case 4: Single element
        System.out.println("Test 4: " +
                solution.missingNumber(new int[] { 0 })); // 1

        // Test all approaches
        int[][] testCases = {
                { 3, 0, 1 }, // Missing middle
                { 1, 2, 3 }, // Missing first
                { 0, 1, 2 }, // Missing last
                { 0 }, // Single element
                { 9, 6, 4, 2, 3, 5, 7, 0, 1 } // Larger array
        };

        System.out.println("\nTesting all approaches:");
        for (int[] test : testCases) {
            int[] copy = test.clone(); // Create copy for in-place approach
            int result1 = solution.missingNumber(test);
            int result2 = solution.missingNumberGauss(test);
            int result3 = solution.missingNumberBinarySearch(test);
            int result4 = solution.missingNumberBitPosition(test);
            int result5 = solution.missingNumberInPlace(copy);

            boolean consistent = result1 == result2 && result2 == result3 &&
                    result3 == result4 && result4 == result5;
            System.out.printf("Array %s: %d (consistent: %b)%n",
                    java.util.Arrays.toString(test), result1, consistent);
        }

        // Performance test
        System.out.println("\nPerformance test:");
        int size = 10000;
        int[] largeArray = new int[size];
        for (int i = 0; i < size - 1; i++) {
            largeArray[i] = i;
        }
        largeArray[size - 1] = size + 1; // Missing number is size

        long start;
        int iterations = 1000;

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.missingNumber(largeArray);
        }
        System.out.println("XOR: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.missingNumberGauss(largeArray);
        }
        System.out.println("Gauss: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.missingNumberBinarySearch(largeArray.clone());
        }
        System.out.println("Binary Search: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.missingNumberBitPosition(largeArray);
        }
        System.out.println("Bit Position: " + (System.currentTimeMillis() - start) + "ms");

        start = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            solution.missingNumberInPlace(largeArray.clone());
        }
        System.out.println("In-place: " + (System.currentTimeMillis() - start) + "ms");

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty array: " +
                solution.missingNumber(new int[] {})); // 0
        System.out.println("All consecutive except last: " +
                solution.missingNumber(new int[] { 0, 1, 2, 3, 4, 6, 7, 8, 9, 10 })); // 5
        System.out.println("Random order: " +
                solution.missingNumber(new int[] { 9, 6, 4, 2, 3, 5, 7, 0, 1 })); // 8
    }
}
