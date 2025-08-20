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
 * Companies: Microsoft, Amazon, Google, Facebook, Apple, Bloomberg, Uber,
 * Netflix, Adobe
 * Difficulty: Medium
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - 0 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * - nums is a non-decreasing array.
 * - -10^9 <= target <= 10^9
 *
 * Follow-ups:
 * - What if the array contains duplicates?
 * - How to find kth occurrence of target?
 * - Can you solve this with one pass?
 */
public class FindFirstAndLastPosition {

    // Two Binary Searches - O(log n) time, O(1) space
    public int[] searchRange(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        int first = findFirst(nums, target);
        if (first == -1) {
            return new int[] { -1, -1 }; // Target not found
        }

        int last = findLast(nums, target);
        return new int[] { first, last };
    }

    private int findFirst(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                right = mid - 1; // Continue searching in left half
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    private int findLast(int[] nums, int target) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                left = mid + 1; // Continue searching in right half
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Alternative: Single modified binary search approach
    public int[] searchRangeAlt(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        int leftBound = findBound(nums, target, true);
        if (leftBound == -1) {
            return new int[] { -1, -1 };
        }

        int rightBound = findBound(nums, target, false);
        return new int[] { leftBound, rightBound };
    }

    private int findBound(int[] nums, int target, boolean isLeft) {
        int left = 0, right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                result = mid;
                if (isLeft) {
                    right = mid - 1; // Search left for first occurrence
                } else {
                    left = mid + 1; // Search right for last occurrence
                }
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return result;
    }

    // Approach 3: Using lower_bound and upper_bound concept
    public int[] searchRangeBounds(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        int lowerBound = lowerBound(nums, target);
        int upperBound = upperBound(nums, target);

        if (lowerBound == nums.length || nums[lowerBound] != target) {
            return new int[] { -1, -1 };
        }

        return new int[] { lowerBound, upperBound - 1 };
    }

    // Find first position where nums[i] >= target
    private int lowerBound(int[] nums, int target) {
        int left = 0, right = nums.length;

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

    // Find first position where nums[i] > target
    private int upperBound(int[] nums, int target) {
        int left = 0, right = nums.length;

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

    // Approach 4: Recursive solution
    public int[] searchRangeRecursive(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        int first = findFirstRecursive(nums, target, 0, nums.length - 1);
        if (first == -1) {
            return new int[] { -1, -1 };
        }

        int last = findLastRecursive(nums, target, 0, nums.length - 1);
        return new int[] { first, last };
    }

    private int findFirstRecursive(int[] nums, int target, int left, int right) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            int leftResult = findFirstRecursive(nums, target, left, mid - 1);
            return leftResult != -1 ? leftResult : mid;
        } else if (nums[mid] < target) {
            return findFirstRecursive(nums, target, mid + 1, right);
        } else {
            return findFirstRecursive(nums, target, left, mid - 1);
        }
    }

    private int findLastRecursive(int[] nums, int target, int left, int right) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            int rightResult = findLastRecursive(nums, target, mid + 1, right);
            return rightResult != -1 ? rightResult : mid;
        } else if (nums[mid] < target) {
            return findLastRecursive(nums, target, mid + 1, right);
        } else {
            return findLastRecursive(nums, target, left, mid - 1);
        }
    }

    // Approach 5: Linear scan from found position (for comparison)
    public int[] searchRangeLinear(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        // First find any occurrence
        int index = binarySearch(nums, target);
        if (index == -1) {
            return new int[] { -1, -1 };
        }

        // Expand left and right
        int left = index, right = index;

        while (left > 0 && nums[left - 1] == target) {
            left--;
        }

        while (right < nums.length - 1 && nums[right + 1] == target) {
            right++;
        }

        return new int[] { left, right };
    }

    private int binarySearch(int[] nums, int target) {
        int left = 0, right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    // Count occurrences of target
    public int countOccurrences(int[] nums, int target) {
        int[] range = searchRange(nums, target);
        if (range[0] == -1) {
            return 0;
        }
        return range[1] - range[0] + 1;
    }

    // Find kth occurrence (1-indexed)
    public int findKthOccurrence(int[] nums, int target, int k) {
        int[] range = searchRange(nums, target);
        if (range[0] == -1) {
            return -1;
        }

        int count = range[1] - range[0] + 1;
        if (k < 1 || k > count) {
            return -1;
        }

        return range[0] + k - 1;
    }

    // Find all occurrences
    public int[] findAllOccurrences(int[] nums, int target) {
        int[] range = searchRange(nums, target);
        if (range[0] == -1) {
            return new int[0];
        }

        int count = range[1] - range[0] + 1;
        int[] result = new int[count];

        for (int i = 0; i < count; i++) {
            result[i] = range[0] + i;
        }

        return result;
    }

    // Check if target exists in range
    public boolean existsInRange(int[] nums, int target) {
        int[] range = searchRange(nums, target);
        return range[0] != -1;
    }

    // Extended version for multiple targets
    public int[][] searchMultipleRanges(int[] nums, int[] targets) {
        int[][] results = new int[targets.length][2];

        for (int i = 0; i < targets.length; i++) {
            results[i] = searchRange(nums, targets[i]);
        }

        return results;
    }

    // Performance optimized version with early termination
    public int[] searchRangeOptimized(int[] nums, int target) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        // Quick check for single element
        if (nums.length == 1) {
            return nums[0] == target ? new int[] { 0, 0 } : new int[] { -1, -1 };
        }

        // Quick bounds check
        if (target < nums[0] || target > nums[nums.length - 1]) {
            return new int[] { -1, -1 };
        }

        return searchRange(nums, target);
    }

    // Validate solution
    public boolean validateSolution(int[] nums, int target, int[] result) {
        if (result[0] == -1 && result[1] == -1) {
            // Check target doesn't exist
            for (int num : nums) {
                if (num == target) {
                    return false;
                }
            }
            return true;
        }

        // Check bounds
        if (result[0] < 0 || result[1] >= nums.length || result[0] > result[1]) {
            return false;
        }

        // Check all elements in range are target
        for (int i = result[0]; i <= result[1]; i++) {
            if (nums[i] != target) {
                return false;
            }
        }

        // Check elements outside range are not target
        if (result[0] > 0 && nums[result[0] - 1] == target) {
            return false;
        }
        if (result[1] < nums.length - 1 && nums[result[1] + 1] == target) {
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        FindFirstAndLastPosition solution = new FindFirstAndLastPosition();

        // Test Case 1: [5,7,7,8,8,10], target = 8
        int[] nums1 = { 5, 7, 7, 8, 8, 10 };
        int[] result1 = solution.searchRange(nums1, 8);
        System.out.println(java.util.Arrays.toString(result1)); // Expected: [3, 4]

        // Test Case 2: [5,7,7,8,8,10], target = 6
        int[] result2 = solution.searchRange(nums1, 6);
        System.out.println(java.util.Arrays.toString(result2)); // Expected: [-1, -1]

        // Test Case 3: [], target = 0
        int[] nums3 = {};
        int[] result3 = solution.searchRange(nums3, 0);
        System.out.println(java.util.Arrays.toString(result3)); // Expected: [-1, -1]

        // Test Case 4: Single element
        int[] nums4 = { 1 };
        int[] result4 = solution.searchRange(nums4, 1);
        System.out.println(java.util.Arrays.toString(result4)); // Expected: [0, 0]

        // Test Case 5: All same elements
        int[] nums5 = { 2, 2, 2, 2, 2 };
        int[] result5 = solution.searchRange(nums5, 2);
        System.out.println(java.util.Arrays.toString(result5)); // Expected: [0, 4]

        // Test Case 6: Target at boundaries
        int[] nums6 = { 1, 2, 3, 4, 5 };
        System.out.println(java.util.Arrays.toString(solution.searchRange(nums6, 1))); // Expected: [0, 0]
        System.out.println(java.util.Arrays.toString(solution.searchRange(nums6, 5))); // Expected: [4, 4]

        // Test alternative implementations
        System.out.println("Alternative: " + java.util.Arrays.toString(solution.searchRangeAlt(nums1, 8))); // Expected:
                                                                                                            // [3, 4]
        System.out.println("Bounds: " + java.util.Arrays.toString(solution.searchRangeBounds(nums1, 8))); // Expected:
                                                                                                          // [3, 4]
        System.out.println("Recursive: " + java.util.Arrays.toString(solution.searchRangeRecursive(nums1, 8))); // Expected:
                                                                                                                // [3,
                                                                                                                // 4]
        System.out.println("Linear: " + java.util.Arrays.toString(solution.searchRangeLinear(nums1, 8))); // Expected:
                                                                                                          // [3, 4]

        // Test count occurrences
        System.out.println("Count of 8: " + solution.countOccurrences(nums1, 8)); // Expected: 2
        System.out.println("Count of 7: " + solution.countOccurrences(nums1, 7)); // Expected: 2
        System.out.println("Count of 6: " + solution.countOccurrences(nums1, 6)); // Expected: 0

        // Test kth occurrence
        System.out.println("1st occurrence of 8: " + solution.findKthOccurrence(nums1, 8, 1)); // Expected: 3
        System.out.println("2nd occurrence of 8: " + solution.findKthOccurrence(nums1, 8, 2)); // Expected: 4
        System.out.println("3rd occurrence of 8: " + solution.findKthOccurrence(nums1, 8, 3)); // Expected: -1

        // Test all occurrences
        int[] allOccurrences = solution.findAllOccurrences(nums1, 7);
        System.out.println("All occurrences of 7: " + java.util.Arrays.toString(allOccurrences)); // Expected: [1, 2]

        // Test exists in range
        System.out.println("8 exists: " + solution.existsInRange(nums1, 8)); // Expected: true
        System.out.println("6 exists: " + solution.existsInRange(nums1, 6)); // Expected: false

        // Test multiple targets
        int[] targets = { 7, 8, 6, 10 };
        int[][] multipleResults = solution.searchMultipleRanges(nums1, targets);
        System.out.println("Multiple targets:");
        for (int i = 0; i < targets.length; i++) {
            System.out.println("Target " + targets[i] + ": " + java.util.Arrays.toString(multipleResults[i]));
        }

        // Test validation
        System.out.println("Solution valid: " + solution.validateSolution(nums1, 8, result1)); // Expected: true
        System.out.println("Solution valid: " + solution.validateSolution(nums1, 6, result2)); // Expected: true

        // Performance test with large array
        int[] large = new int[100000];
        for (int i = 0; i < 100000; i++) {
            large[i] = i / 1000; // Creates ranges of 1000 identical numbers
        }

        long startTime = System.currentTimeMillis();
        int[] largeResult = solution.searchRange(large, 50);
        long endTime = System.currentTimeMillis();
        System.out.println("Large array result: " + java.util.Arrays.toString(largeResult) +
                " (time: " + (endTime - startTime) + "ms)");

        // Compare different approaches performance
        int[] testArray = new int[10000];
        for (int i = 0; i < 10000; i++) {
            testArray[i] = i / 100;
        }

        // Binary search approach
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.searchRange(testArray, 50);
        }
        long binaryTime = System.currentTimeMillis() - startTime;

        // Linear approach
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            solution.searchRangeLinear(testArray, 50);
        }
        long linearTime = System.currentTimeMillis() - startTime;

        System.out.println("Binary search time (1000 runs): " + binaryTime + "ms");
        System.out.println("Linear search time (1000 runs): " + linearTime + "ms");

        // Edge cases
        int[] edge1 = { 1, 1, 1, 1, 1 };
        System.out.println("All same: " + java.util.Arrays.toString(solution.searchRange(edge1, 1))); // Expected: [0,
                                                                                                      // 4]

        int[] edge2 = { 1, 2, 3 };
        System.out.println("No duplicates, found: " + java.util.Arrays.toString(solution.searchRange(edge2, 2))); // Expected:
                                                                                                                  // [1,
                                                                                                                  // 1]
        System.out.println("No duplicates, not found: " + java.util.Arrays.toString(solution.searchRange(edge2, 4))); // Expected:
                                                                                                                      // [-1,
                                                                                                                      // -1]

        // Test optimized version
        System.out.println("Optimized: " + java.util.Arrays.toString(solution.searchRangeOptimized(nums1, 8))); // Expected:
                                                                                                                // [3,
                                                                                                                // 4]
    }
}
