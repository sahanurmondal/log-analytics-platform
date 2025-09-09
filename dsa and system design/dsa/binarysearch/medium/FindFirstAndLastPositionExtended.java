package binarysearch.medium;

/**
 * LeetCode 34: Find First and Last Position of Element in Sorted Array
 * https://leetcode.com/problems/find-first-and-last-position-of-element-in-sorted-array/
 *
 * Description:
 * Given an array of integers nums sorted in non-decreasing order, find the
 * starting and ending position of a given target value.
 * If target is not found in the array, return [-1, -1].
 * You must write an algorithm with O(log n) runtime complexity.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe, Uber, Netflix
 * Difficulty: Medium
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - 0 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * - nums is a non-decreasing array
 * - -10^9 <= target <= 10^9
 *
 * Follow-ups:
 * - Can you solve this with only one binary search pass?
 * - What if we need to find the count of target elements?
 * - How would you handle floating-point numbers?
 */
public class FindFirstAndLastPositionExtended {

    // Two binary searches approach - O(log n) time, O(1) space
    public int[] searchRange(int[] nums, int target) {
        int[] result = new int[2];
        result[0] = findFirst(nums, target);
        result[1] = findLast(nums, target);
        return result;
    }

    private int findFirst(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                right = mid - 1; // Continue searching left for first occurrence
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    private int findLast(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                left = mid + 1; // Continue searching right for last occurrence
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Template-based approach
    public int[] searchRangeTemplate(int[] nums, int target) {
        int first = findFirstTemplate(nums, target);
        int last = findLastTemplate(nums, target);
        return new int[] { first, last };
    }

    private int findFirstTemplate(int[] nums, int target) {
        int left = 0;
        int right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return (left < nums.length && nums[left] == target) ? left : -1;
    }

    private int findLastTemplate(int[] nums, int target) {
        int left = 0;
        int right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        left--; // Adjust to get the last position
        return (left >= 0 && nums[left] == target) ? left : -1;
    }

    // Recursive approach
    public int[] searchRangeRecursive(int[] nums, int target) {
        int first = findFirstRecursive(nums, target, 0, nums.length - 1);
        int last = findLastRecursive(nums, target, 0, nums.length - 1);
        return new int[] { first, last };
    }

    private int findFirstRecursive(int[] nums, int target, int left, int right) {
        if (left > right)
            return -1;

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            // Check if it's the first occurrence
            int leftResult = findFirstRecursive(nums, target, left, mid - 1);
            return (leftResult != -1) ? leftResult : mid;
        } else if (nums[mid] < target) {
            return findFirstRecursive(nums, target, mid + 1, right);
        } else {
            return findFirstRecursive(nums, target, left, mid - 1);
        }
    }

    private int findLastRecursive(int[] nums, int target, int left, int right) {
        if (left > right)
            return -1;

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            // Check if it's the last occurrence
            int rightResult = findLastRecursive(nums, target, mid + 1, right);
            return (rightResult != -1) ? rightResult : mid;
        } else if (nums[mid] < target) {
            return findLastRecursive(nums, target, mid + 1, right);
        } else {
            return findLastRecursive(nums, target, left, mid - 1);
        }
    }

    // One-pass approach using bounds
    public int[] searchRangeOnPass(int[] nums, int target) {
        int n = nums.length;
        if (n == 0)
            return new int[] { -1, -1 };

        // Find leftmost position where we can insert target
        int left = lowerBound(nums, target);

        // If target is not found
        if (left == n || nums[left] != target) {
            return new int[] { -1, -1 };
        }

        // Find rightmost position where we can insert target
        int right = upperBound(nums, target) - 1;

        return new int[] { left, right };
    }

    private int lowerBound(int[] nums, int target) {
        int left = 0;
        int right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private int upperBound(int[] nums, int target) {
        int left = 0;
        int right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (nums[mid] <= target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    // Utility method to count occurrences
    public int countOccurrences(int[] nums, int target) {
        int[] range = searchRange(nums, target);
        return (range[0] == -1) ? 0 : range[1] - range[0] + 1;
    }

    public static void main(String[] args) {
        FindFirstAndLastPositionExtended solution = new FindFirstAndLastPositionExtended();

        // Test Case 1: [5,7,7,8,8,10], target = 8
        int[] nums1 = { 5, 7, 7, 8, 8, 10 };
        int[] result1 = solution.searchRange(nums1, 8);
        System.out.println("[" + result1[0] + ", " + result1[1] + "]"); // Expected: [3, 4]

        // Test Case 2: [5,7,7,8,8,10], target = 6
        int[] result2 = solution.searchRange(nums1, 6);
        System.out.println("[" + result2[0] + ", " + result2[1] + "]"); // Expected: [-1, -1]

        // Test Case 3: [], target = 0
        int[] nums2 = {};
        int[] result3 = solution.searchRange(nums2, 0);
        System.out.println("[" + result3[0] + ", " + result3[1] + "]"); // Expected: [-1, -1]

        // Test Case 4: Single element found
        int[] nums3 = { 1 };
        int[] result4 = solution.searchRange(nums3, 1);
        System.out.println("[" + result4[0] + ", " + result4[1] + "]"); // Expected: [0, 0]

        // Test Case 5: Single element not found
        int[] result5 = solution.searchRange(nums3, 0);
        System.out.println("[" + result5[0] + ", " + result5[1] + "]"); // Expected: [-1, -1]

        // Test Case 6: All elements are the same
        int[] nums4 = { 2, 2, 2, 2 };
        int[] result6 = solution.searchRange(nums4, 2);
        System.out.println("[" + result6[0] + ", " + result6[1] + "]"); // Expected: [0, 3]

        // Test Case 7: Target at beginning
        int[] nums5 = { 1, 1, 2, 3, 4 };
        int[] result7 = solution.searchRange(nums5, 1);
        System.out.println("[" + result7[0] + ", " + result7[1] + "]"); // Expected: [0, 1]

        // Test Case 8: Target at end
        int[] result8 = solution.searchRange(nums5, 4);
        System.out.println("[" + result8[0] + ", " + result8[1] + "]"); // Expected: [4, 4]

        // Test template approach
        int[] resultTemplate = solution.searchRangeTemplate(nums1, 8);
        System.out.println("Template: [" + resultTemplate[0] + ", " + resultTemplate[1] + "]"); // Expected: [3, 4]

        // Test recursive approach
        int[] resultRecursive = solution.searchRangeRecursive(nums1, 8);
        System.out.println("Recursive: [" + resultRecursive[0] + ", " + resultRecursive[1] + "]"); // Expected: [3, 4]

        // Test one-pass approach
        int[] resultOnePass = solution.searchRangeOnPass(nums1, 8);
        System.out.println("One-pass: [" + resultOnePass[0] + ", " + resultOnePass[1] + "]"); // Expected: [3, 4]

        // Test count occurrences
        System.out.println("Count of 8: " + solution.countOccurrences(nums1, 8)); // Expected: 2
        System.out.println("Count of 7: " + solution.countOccurrences(nums1, 7)); // Expected: 2
        System.out.println("Count of 6: " + solution.countOccurrences(nums1, 6)); // Expected: 0
    }
}
