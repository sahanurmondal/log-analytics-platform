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
 * Companies: Microsoft, Amazon, Google, Facebook, Apple, Bloomberg, Adobe,
 * Uber, LinkedIn
 * Difficulty: Medium
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 5000
 * - -5000 <= nums[i] <= 5000
 * - All the integers of nums are unique.
 * - nums is sorted and rotated between 1 and n times.
 *
 * Follow-ups:
 * - What if array contains duplicates? (LeetCode 154)
 * - How to find the rotation count?
 * - How to find maximum in rotated array?
 */
public class FindMinimumInRotatedSortedArray {

    // Binary Search - O(log n) time, O(1) space
    public int findMin(int[] nums) {
        if (nums == null || nums.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }

        int left = 0, right = nums.length - 1;

        // Array is not rotated
        if (nums[left] < nums[right]) {
            return nums[left];
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            // If mid is greater than right, minimum is in right part
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            }
            // If mid is less than right, minimum is in left part (including mid)
            else {
                right = mid;
            }
        }

        return nums[left];
    }

    // Alternative implementation comparing with left
    public int findMinAlt(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        if (nums.length == 1) {
            return nums[0];
        }

        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Compare mid with left
            if (nums[mid] >= nums[left]) {
                // Left part is sorted
                if (nums[left] <= nums[right]) {
                    // Entire array is sorted
                    return nums[left];
                } else {
                    // Minimum is in right part
                    left = mid + 1;
                }
            } else {
                // Right part is sorted, minimum is in left part
                right = mid;
            }
        }

        return nums[left];
    }

    // Recursive approach
    public int findMinRecursive(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        return findMinHelper(nums, 0, nums.length - 1);
    }

    private int findMinHelper(int[] nums, int left, int right) {
        // Base case: single element
        if (left == right) {
            return nums[left];
        }

        // Array is sorted
        if (nums[left] < nums[right]) {
            return nums[left];
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] > nums[right]) {
            return findMinHelper(nums, mid + 1, right);
        } else {
            return findMinHelper(nums, left, mid);
        }
    }

    // Find minimum and its index
    public int[] findMinWithIndex(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new int[] { -1, -1 };
        }

        int left = 0, right = nums.length - 1;

        if (nums[left] < nums[right]) {
            return new int[] { nums[left], left };
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return new int[] { nums[left], left };
    }

    // Find rotation count (number of rotations)
    public int findRotationCount(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int left = 0, right = nums.length - 1;

        // Array is not rotated
        if (nums[left] < nums[right]) {
            return 0;
        }

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left; // Index of minimum element = rotation count
    }

    // Find maximum in rotated sorted array
    public int findMax(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int minIndex = findMinWithIndex(nums)[1];

        // Maximum is either at minIndex-1 or at the end
        if (minIndex == 0) {
            return nums[nums.length - 1]; // Array not rotated, max at end
        } else {
            return nums[minIndex - 1]; // Max is before the minimum
        }
    }

    // Check if array is rotated
    public boolean isRotated(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return false;
        }

        return nums[0] > nums[nums.length - 1];
    }

    // Find pivot point (where rotation happens)
    public int findPivot(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        // If array is not rotated, pivot is at the beginning
        if (nums[0] < nums[nums.length - 1]) {
            return 0;
        }

        int left = 0, right = nums.length - 1;

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

    // Handle duplicates (LeetCode 154 solution)
    public int findMinWithDuplicates(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int left = 0, right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else if (nums[mid] < nums[right]) {
                right = mid;
            } else {
                // nums[mid] == nums[right], can't determine which part to search
                right--; // Reduce search space
            }
        }

        return nums[left];
    }

    // Linear search for comparison
    public int findMinLinear(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int min = nums[0];
        for (int i = 1; i < nums.length; i++) {
            min = Math.min(min, nums[i]);
        }

        return min;
    }

    // Find original sorted array
    public int[] getOriginalArray(int[] nums) {
        if (nums == null || nums.length == 0) {
            return new int[0];
        }

        int pivot = findPivot(nums);
        int[] result = new int[nums.length];

        // Copy from pivot to end
        System.arraycopy(nums, pivot, result, 0, nums.length - pivot);

        // Copy from start to pivot
        if (pivot > 0) {
            System.arraycopy(nums, 0, result, nums.length - pivot, pivot);
        }

        return result;
    }

    // Validate if array is properly rotated sorted array
    public boolean isValidRotatedArray(int[] nums) {
        if (nums == null || nums.length <= 1) {
            return true;
        }

        int rotationCount = 0;

        for (int i = 0; i < nums.length - 1; i++) {
            if (nums[i] > nums[i + 1]) {
                rotationCount++;
                if (rotationCount > 1) {
                    return false; // More than one rotation point
                }
            }
        }

        // If there's one rotation, check wrap-around
        if (rotationCount == 1) {
            return nums[nums.length - 1] <= nums[0];
        }

        return true; // Array is sorted (0 rotations)
    }

    // Find all possible minimums in range
    public int findMinInRange(int[] nums, int start, int end) {
        if (nums == null || start < 0 || end >= nums.length || start > end) {
            return -1;
        }

        int min = nums[start];
        for (int i = start + 1; i <= end; i++) {
            min = Math.min(min, nums[i]);
        }

        return min;
    }

    // Performance optimized version
    public int findMinOptimized(int[] nums) {
        if (nums == null || nums.length == 0) {
            return -1;
        }

        int n = nums.length;

        // Quick checks
        if (n == 1)
            return nums[0];
        if (n == 2)
            return Math.min(nums[0], nums[1]);

        // Check if array is rotated
        if (nums[0] < nums[n - 1]) {
            return nums[0];
        }

        // Use standard binary search
        return findMin(nums);
    }

    public static void main(String[] args) {
        FindMinimumInRotatedSortedArray solution = new FindMinimumInRotatedSortedArray();

        // Test Case 1: [3,4,5,1,2]
        int[] nums1 = { 3, 4, 5, 1, 2 };
        System.out.println(solution.findMin(nums1)); // Expected: 1

        // Test Case 2: [4,5,6,7,0,1,2]
        int[] nums2 = { 4, 5, 6, 7, 0, 1, 2 };
        System.out.println(solution.findMin(nums2)); // Expected: 0

        // Test Case 3: [11,13,15,17]
        int[] nums3 = { 11, 13, 15, 17 };
        System.out.println(solution.findMin(nums3)); // Expected: 11

        // Test Case 4: Single element
        int[] nums4 = { 1 };
        System.out.println(solution.findMin(nums4)); // Expected: 1

        // Test Case 5: Two elements
        int[] nums5 = { 2, 1 };
        System.out.println(solution.findMin(nums5)); // Expected: 1

        // Test Case 6: Not rotated
        int[] nums6 = { 1, 2, 3, 4, 5 };
        System.out.println(solution.findMin(nums6)); // Expected: 1

        // Test alternative implementations
        System.out.println("Alternative: " + solution.findMinAlt(nums1)); // Expected: 1
        System.out.println("Recursive: " + solution.findMinRecursive(nums1)); // Expected: 1
        System.out.println("Linear: " + solution.findMinLinear(nums1)); // Expected: 1

        // Test minimum with index
        int[] minWithIndex = solution.findMinWithIndex(nums1);
        System.out.println("Min with index: value=" + minWithIndex[0] + ", index=" + minWithIndex[1]); // Expected:
                                                                                                       // value=1,
                                                                                                       // index=3

        // Test rotation count
        System.out.println("Rotation count: " + solution.findRotationCount(nums1)); // Expected: 3
        System.out.println("Rotation count (not rotated): " + solution.findRotationCount(nums6)); // Expected: 0

        // Test find maximum
        System.out.println("Maximum: " + solution.findMax(nums1)); // Expected: 5
        System.out.println("Maximum (not rotated): " + solution.findMax(nums6)); // Expected: 5

        // Test if rotated
        System.out.println("Is rotated: " + solution.isRotated(nums1)); // Expected: true
        System.out.println("Is rotated (not rotated): " + solution.isRotated(nums6)); // Expected: false

        // Test pivot point
        System.out.println("Pivot point: " + solution.findPivot(nums1)); // Expected: 3
        System.out.println("Pivot point (not rotated): " + solution.findPivot(nums6)); // Expected: 0

        // Test with duplicates
        int[] withDuplicates = { 2, 2, 2, 0, 1 };
        System.out.println("Min with duplicates: " + solution.findMinWithDuplicates(withDuplicates)); // Expected: 0

        // Test get original array
        int[] original = solution.getOriginalArray(nums1);
        System.out.println("Original array: " + java.util.Arrays.toString(original)); // Expected: [1, 2, 3, 4, 5]

        // Test validation
        System.out.println("Is valid rotated array: " + solution.isValidRotatedArray(nums1)); // Expected: true
        System.out.println("Is valid rotated array: " + solution.isValidRotatedArray(nums6)); // Expected: true

        int[] invalid = { 3, 1, 4, 2 }; // Invalid rotated array
        System.out.println("Is valid rotated array (invalid): " + solution.isValidRotatedArray(invalid)); // Expected:
                                                                                                          // false

        // Test min in range
        System.out.println("Min in range [1,3]: " + solution.findMinInRange(nums1, 1, 3)); // Expected: 1

        // Test optimized version
        System.out.println("Optimized: " + solution.findMinOptimized(nums1)); // Expected: 1

        // Performance test
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++) {
            large[i] = (i + 5000) % 10000;
        }

        long startTime = System.currentTimeMillis();
        int largeResult = solution.findMin(large);
        long binaryTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int largeResultLinear = solution.findMinLinear(large);
        long linearTime = System.currentTimeMillis() - startTime;

        System.out.println("Large array result (binary): " + largeResult + " (time: " + binaryTime + "ms)");
        System.out.println("Large array result (linear): " + largeResultLinear + " (time: " + linearTime + "ms)");

        // Edge cases
        int[] allSame = { 5, 5, 5, 5, 5 };
        System.out.println("All same elements: " + solution.findMin(allSame)); // Expected: 5

        int[] twoDistinct = { 1, 0 };
        System.out.println("Two distinct (rotated): " + solution.findMin(twoDistinct)); // Expected: 0

        int[] twoDistinctNotRotated = { 0, 1 };
        System.out.println("Two distinct (not rotated): " + solution.findMin(twoDistinctNotRotated)); // Expected: 0

        // Stress test with different rotation points
        int[] base = { 1, 2, 3, 4, 5 };
        for (int rotations = 0; rotations < base.length; rotations++) {
            int[] rotated = new int[base.length];
            for (int i = 0; i < base.length; i++) {
                rotated[i] = base[(i + rotations) % base.length];
            }

            int min = solution.findMin(rotated);
            int rotationCount = solution.findRotationCount(rotated);

            System.out.println("Rotations: " + rotations + ", Array: " + java.util.Arrays.toString(rotated) +
                    ", Min: " + min + ", Detected rotations: " + rotationCount);
        }

        // Test with negative numbers
        int[] negatives = { -1, -2, -3, -4, -5 };
        System.out.println("Negative numbers (not rotated): " + solution.findMin(negatives)); // Expected: -5

        int[] negativesRotated = { -3, -4, -5, -1, -2 };
        System.out.println("Negative numbers (rotated): " + solution.findMin(negativesRotated)); // Expected: -5

        // Mixed positive and negative
        int[] mixed = { 3, 4, 5, -1, 0, 1, 2 };
        System.out.println("Mixed numbers: " + solution.findMin(mixed)); // Expected: -1
    }
}
