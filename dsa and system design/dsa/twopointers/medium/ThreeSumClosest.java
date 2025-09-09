package twopointers.medium;

/**
 * LeetCode 16: 3Sum Closest
 * https://leetcode.com/problems/3sum-closest/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 70+ interviews)
 *
 * Description:
 * Given an integer array nums of length n and an integer target, find three
 * integers in nums such that the sum is closest to target. Return the sum of
 * the three integers.
 * You may assume that each input would have exactly one solution.
 *
 * Constraints:
 * - 3 <= nums.length <= 1000
 * - -1000 <= nums[i] <= 1000
 * - -10^4 <= target <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve it in O(n^2) time?
 * 2. Can you handle the case where multiple triplets have the same closest sum?
 * 3. Can you extend to k-sum closest?
 */
public class ThreeSumClosest {

    // Approach 1: Sorting + Two Pointers - O(n²) time, O(1) space
    public int threeSumClosest(int[] nums, int target) {
        java.util.Arrays.sort(nums);
        int closestSum = nums[0] + nums[1] + nums[2];
        int minDiff = Math.abs(closestSum - target);

        for (int i = 0; i < nums.length - 2; i++) {
            // Skip duplicates for first element
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int currentSum = nums[i] + nums[left] + nums[right];
                int currentDiff = Math.abs(currentSum - target);

                // Update closest sum if current is closer
                if (currentDiff < minDiff) {
                    minDiff = currentDiff;
                    closestSum = currentSum;
                }

                // If exact match found, return immediately
                if (currentSum == target) {
                    return currentSum;
                }

                if (currentSum < target) {
                    left++;
                    // Skip duplicates
                    while (left < right && nums[left] == nums[left - 1])
                        left++;
                } else {
                    right--;
                    // Skip duplicates
                    while (left < right && nums[right] == nums[right + 1])
                        right--;
                }
            }
        }

        return closestSum;
    }

    // Approach 2: Brute Force with Early Termination - O(n³) time, O(1) space
    public int threeSumClosestBruteForce(int[] nums, int target) {
        int closestSum = nums[0] + nums[1] + nums[2];
        int minDiff = Math.abs(closestSum - target);

        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    int currentSum = nums[i] + nums[j] + nums[k];
                    int currentDiff = Math.abs(currentSum - target);

                    if (currentDiff < minDiff) {
                        minDiff = currentDiff;
                        closestSum = currentSum;

                        // Early termination if exact match
                        if (currentDiff == 0)
                            return closestSum;
                    }
                }
            }
        }

        return closestSum;
    }

    // Follow-up: Find all triplets with closest sum
    public java.util.List<java.util.List<Integer>> findAllClosestTriplets(int[] nums, int target) {
        java.util.Arrays.sort(nums);
        java.util.List<java.util.List<Integer>> result = new java.util.ArrayList<>();

        int closestSum = nums[0] + nums[1] + nums[2];
        int minDiff = Math.abs(closestSum - target);

        for (int i = 0; i < nums.length - 2; i++) {
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            int left = i + 1;
            int right = nums.length - 1;

            while (left < right) {
                int currentSum = nums[i] + nums[left] + nums[right];
                int currentDiff = Math.abs(currentSum - target);

                if (currentDiff < minDiff) {
                    minDiff = currentDiff;
                    closestSum = currentSum;
                    result.clear();
                    result.add(java.util.Arrays.asList(nums[i], nums[left], nums[right]));
                } else if (currentDiff == minDiff) {
                    result.add(java.util.Arrays.asList(nums[i], nums[left], nums[right]));
                }

                if (currentSum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        ThreeSumClosest solution = new ThreeSumClosest();

        // Test Case 1: Basic example
        System.out.println("Basic: " + solution.threeSumClosest(new int[] { -1, 2, 1, -4 }, 1)); // 2

        // Test Case 2: All same numbers
        System.out.println("All same: " + solution.threeSumClosest(new int[] { 0, 0, 0 }, 1)); // 0

        // Test Case 3: Exact match
        System.out.println("Exact: " + solution.threeSumClosest(new int[] { 1, 1, 1 }, 3)); // 3

        // Test Case 4: All negative
        System.out.println("All negative: " + solution.threeSumClosest(new int[] { -5, -3, -1 }, -10)); // -9

        // Test Case 5: Large target
        System.out.println("Large target: " + solution.threeSumClosest(new int[] { 1, 2, 3 }, 10000)); // 6

        // Test Case 6: Minimum array size
        System.out.println("Minimum: " + solution.threeSumClosest(new int[] { 1, 2, 3 }, 0)); // 6

        // Test Case 7: Mixed positive and negative
        System.out.println("Mixed: " + solution.threeSumClosest(new int[] { -100, -98, -2, -1 }, -101)); // -101

        // Test Case 8: Large array
        System.out.println("Large array: " + solution.threeSumClosest(new int[] { 4, 0, 5, -5, 3, 3, 0, -4, -5 }, -2)); // -2

        // Test Case 9: Brute force comparison
        System.out.println("Brute force: " + solution.threeSumClosestBruteForce(new int[] { -1, 2, 1, -4 }, 1)); // 2

        // Test Case 10: All closest triplets
        java.util.List<java.util.List<Integer>> allClosest = solution.findAllClosestTriplets(new int[] { -1, 2, 1, -4 },
                1);
        System.out.println("All closest triplets: " + allClosest);

        // Test Case 11: Duplicates
        System.out.println("Duplicates: " + solution.threeSumClosest(new int[] { 1, 1, 1, 0 }, -100)); // 2

        // Test Case 12: Close to zero
        System.out.println("Close to zero: " + solution.threeSumClosest(new int[] { -1, 0, 1, 1, 55 }, 3)); // 2
    }
}
