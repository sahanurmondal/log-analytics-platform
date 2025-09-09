package twopointers.medium;

/**
 * LeetCode 167: Two Sum II - Input Array Is Sorted
 * https://leetcode.com/problems/two-sum-ii-input-array-is-sorted/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 100+ interviews)
 *
 * Description:
 * Given a 1-indexed array of integers numbers that is already sorted in
 * non-decreasing order, find two numbers such that they add up to a specific
 * target number. Return the indices of the two numbers, index1 and index2,
 * added by one as an integer array [index1, index2] of length 2.
 *
 * Constraints:
 * - 2 <= numbers.length <= 3 * 10^4
 * - -1000 <= numbers[i] <= 1000
 * - numbers is sorted in non-decreasing order
 * - -1000 <= target <= 1000
 * - The tests are generated such that there is exactly one solution
 * 
 * Follow-up Questions:
 * 1. Can you solve it in O(1) space complexity?
 * 2. Can you solve it without binary search?
 * 3. Can you extend to find multiple pairs that sum to target?
 */
public class TwoSumII {

    // Approach 1: Two Pointers - O(n) time, O(1) space
    public int[] twoSum(int[] numbers, int target) {
        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                return new int[] { left + 1, right + 1 }; // 1-indexed
            } else if (sum < target) {
                left++; // Need larger sum
            } else {
                right--; // Need smaller sum
            }
        }

        // Should never reach here given problem constraints
        return new int[] { -1, -1 };
    }

    // Approach 2: Binary Search - O(n log n) time, O(1) space
    public int[] twoSumBinarySearch(int[] numbers, int target) {
        for (int i = 0; i < numbers.length - 1; i++) {
            int complement = target - numbers[i];
            int left = i + 1;
            int right = numbers.length - 1;

            // Binary search for complement
            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (numbers[mid] == complement) {
                    return new int[] { i + 1, mid + 1 }; // 1-indexed
                } else if (numbers[mid] < complement) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }
        }

        return new int[] { -1, -1 };
    }

    // Follow-up: Find all pairs that sum to target
    public java.util.List<int[]> findAllPairs(int[] numbers, int target) {
        java.util.List<int[]> result = new java.util.ArrayList<>();
        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                result.add(new int[] { left + 1, right + 1 }); // 1-indexed

                // Skip duplicates
                int leftVal = numbers[left];
                int rightVal = numbers[right];
                while (left < right && numbers[left] == leftVal)
                    left++;
                while (left < right && numbers[right] == rightVal)
                    right--;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }

        return result;
    }

    public static void main(String[] args) {
        TwoSumII solution = new TwoSumII();

        // Test Case 1: Basic example
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { 2, 7, 11, 15 }, 9))); // [1,2]

        // Test Case 2: Adjacent elements
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { 2, 3, 4 }, 6))); // [1,3]

        // Test Case 3: Negative numbers
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { -1, 0 }, -1))); // [1,2]

        // Test Case 4: Large array
        int[] large = new int[30000];
        for (int i = 0; i < 30000; i++)
            large[i] = i - 15000;
        System.out.println(java.util.Arrays.toString(solution.twoSum(large, 0))); // Should find pair that sums to 0

        // Test Case 5: All negative
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { -5, -3, -1 }, -8))); // [1,2]

        // Test Case 6: Duplicates
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { 1, 2, 2, 3 }, 4))); // [2,3]

        // Test Case 7: Edge - minimum array
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { 1, 2 }, 3))); // [1,2]

        // Test Case 8: Large target
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { 1, 2, 3, 4, 5 }, 9))); // [4,5]

        // Test Case 9: Zero target
        System.out.println(java.util.Arrays.toString(solution.twoSum(new int[] { -3, -1, 0, 2, 3 }, 0))); // [2,4]

        // Test Case 10: Binary search approach
        System.out.println("Binary Search:");
        System.out.println(java.util.Arrays.toString(solution.twoSumBinarySearch(new int[] { 2, 7, 11, 15 }, 9))); // [1,2]

        // Test Case 11: Find all pairs follow-up
        System.out.println("All pairs:");
        java.util.List<int[]> allPairs = solution.findAllPairs(new int[] { 1, 2, 2, 3, 3, 4 }, 5);
        for (int[] pair : allPairs) {
            System.out.println(java.util.Arrays.toString(pair));
        }
    }
}
