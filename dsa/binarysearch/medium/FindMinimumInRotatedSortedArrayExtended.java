package binarysearch.medium;

/**
 * LeetCode 153: Find Minimum in Rotated Sorted Array
 * https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/
 *
 * Description:
 * Suppose an array of length n sorted in ascending order is rotated between 1
 * and n times.
 * Given the sorted rotated array nums of unique elements, return the minimum
 * element of this array.
 * You must write an algorithm that runs in O(log n) time.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe, Uber
 * Difficulty: Medium
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 5000
 * - -5000 <= nums[i] <= 5000
 * - All integers of nums are unique
 * - nums is sorted and rotated between 1 and n times
 *
 * Follow-ups:
 * - What if the array contains duplicates? (See LeetCode 154)
 * - Can you find the rotation count as well?
 * - How would you handle very large arrays that don't fit in memory?
 */
public class FindMinimumInRotatedSortedArrayExtended {

    // Binary Search - O(log n) time, O(1) space
    public int findMin(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // If mid element is greater than right element,
            // the minimum is in the right half
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                // The minimum is in the left half (including mid)
                right = mid;
            }
        }

        return nums[left];
    }

    // Alternative implementation comparing with left element
    public int findMinAlternative(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        // If array is not rotated
        if (nums[left] < nums[right]) {
            return nums[left];
        }

        while (left <= right) {
            int mid = left + (right - left) / 2;

            // Check if mid is the minimum
            if (mid > 0 && nums[mid] < nums[mid - 1]) {
                return nums[mid];
            }

            // Check if mid+1 is the minimum
            if (mid < nums.length - 1 && nums[mid] > nums[mid + 1]) {
                return nums[mid + 1];
            }

            // Decide which half to search
            if (nums[mid] >= nums[left]) {
                // Left half is sorted, minimum is in right half
                left = mid + 1;
            } else {
                // Right half is sorted, minimum is in left half
                right = mid - 1;
            }
        }

        return nums[0];
    }

    // Recursive approach
    public int findMinRecursive(int[] nums) {
        return findMinRecursiveHelper(nums, 0, nums.length - 1);
    }

    private int findMinRecursiveHelper(int[] nums, int left, int right) {
        // Base case: only one element
        if (left == right) {
            return nums[left];
        }

        // If array is sorted
        if (nums[left] < nums[right]) {
            return nums[left];
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] > nums[right]) {
            return findMinRecursiveHelper(nums, mid + 1, right);
        } else {
            return findMinRecursiveHelper(nums, left, mid);
        }
    }

    // Find minimum with rotation count
    public int[] findMinWithRotationCount(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return new int[] { nums[left], left }; // {minimum, rotation_count}
    }

    // Handle array with duplicates (LeetCode 154)
    public int findMinWithDuplicates(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else if (nums[mid] < nums[right]) {
                right = mid;
            } else {
                // nums[mid] == nums[right], can't determine which half to eliminate
                right--; // Reduce the search space by one
            }
        }

        return nums[left];
    }

    // Linear search for comparison - O(n) time
    public int findMinLinear(int[] nums) {
        int min = nums[0];
        for (int num : nums) {
            min = Math.min(min, num);
        }
        return min;
    }

    // Template pattern approach
    public int findMinTemplate(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        // Find the pivot point where rotation happened
        while (left < right) {
            int mid = left + (right - left) / 2;

            if (isMinimum(nums, mid)) {
                return nums[mid];
            }

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return nums[left];
    }

    private boolean isMinimum(int[] nums, int index) {
        int n = nums.length;
        int prev = nums[(index - 1 + n) % n];
        int next = nums[(index + 1) % n];
        return nums[index] <= prev && nums[index] <= next;
    }

    // Find the index of minimum element
    public int findMinIndex(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    // Check if array is rotated
    public boolean isRotated(int[] nums) {
        return nums[0] > nums[nums.length - 1];
    }

    // Find all possible minimums in case of duplicates
    public java.util.List<Integer> findAllMinimums(int[] nums) {
        int minValue = findMinWithDuplicates(nums);
        java.util.List<Integer> indices = new java.util.ArrayList<>();

        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == minValue) {
                indices.add(i);
            }
        }

        return indices;
    }

    public static void main(String[] args) {
        FindMinimumInRotatedSortedArrayExtended solution = new FindMinimumInRotatedSortedArrayExtended();

        // Test Case 1: [3,4,5,1,2]
        int[] nums1 = { 3, 4, 5, 1, 2 };
        System.out.println(solution.findMin(nums1)); // Expected: 1

        // Test Case 2: [4,5,6,7,0,1,2]
        int[] nums2 = { 4, 5, 6, 7, 0, 1, 2 };
        System.out.println(solution.findMin(nums2)); // Expected: 0

        // Test Case 3: [11,13,15,17] (no rotation)
        int[] nums3 = { 11, 13, 15, 17 };
        System.out.println(solution.findMin(nums3)); // Expected: 11

        // Test Case 4: Single element
        int[] nums4 = { 1 };
        System.out.println(solution.findMin(nums4)); // Expected: 1

        // Test Case 5: Two elements - rotated
        int[] nums5 = { 2, 1 };
        System.out.println(solution.findMin(nums5)); // Expected: 1

        // Test Case 6: Two elements - not rotated
        int[] nums6 = { 1, 2 };
        System.out.println(solution.findMin(nums6)); // Expected: 1

        // Test Case 7: All same elements
        int[] nums7 = { 1, 1, 1, 1 };
        System.out.println(solution.findMinWithDuplicates(nums7)); // Expected: 1

        // Test Case 8: Negative numbers
        int[] nums8 = { -1, -2, -3, -4, -5 };
        System.out.println(solution.findMin(nums8)); // Expected: -5

        // Test alternative implementation
        System.out.println("Alternative: " + solution.findMinAlternative(nums1)); // Expected: 1

        // Test recursive approach
        System.out.println("Recursive: " + solution.findMinRecursive(nums1)); // Expected: 1

        // Test with rotation count
        int[] result = solution.findMinWithRotationCount(nums1);
        System.out.println("Min: " + result[0] + ", Rotation count: " + result[1]); // Expected: Min: 1, Rotation count:
                                                                                    // 3

        // Test linear approach
        System.out.println("Linear: " + solution.findMinLinear(nums1)); // Expected: 1

        // Test minimum index
        System.out.println("Min index: " + solution.findMinIndex(nums1)); // Expected: 3

        // Test if rotated
        System.out.println("Is rotated: " + solution.isRotated(nums1)); // Expected: true
        System.out.println("Is rotated (sorted): " + solution.isRotated(nums3)); // Expected: false

        // Test with duplicates
        int[] duplicates = { 2, 2, 2, 0, 1 };
        System.out.println("With duplicates: " + solution.findMinWithDuplicates(duplicates)); // Expected: 0

        // Test all minimums
        int[] duplicates2 = { 1, 3, 1, 1, 1 };
        java.util.List<Integer> allMins = solution.findAllMinimums(duplicates2);
        System.out.println("All minimum indices: " + allMins); // Expected: [0, 2, 3, 4]

        // Large test case
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++) {
            large[i] = (i + 500) % 1000;
        }
        System.out.println("Large array minimum: " + solution.findMin(large)); // Expected: 0

        // Edge case: Maximum rotation
        int[] maxRotated = { 2, 3, 4, 5, 1 };
        System.out.println("Max rotated: " + solution.findMin(maxRotated)); // Expected: 1

        // Edge case: Minimum rotation (1 position)
        int[] minRotated = { 5, 1, 2, 3, 4 };
        System.out.println("Min rotated: " + solution.findMin(minRotated)); // Expected: 1
    }
}
