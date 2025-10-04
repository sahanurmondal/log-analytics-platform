package binarysearch.medium;

/**
 * LeetCode 162: Find Peak Element
 * https://leetcode.com/problems/find-peak-element/
 *
 * Description:
 * A peak element is an element that is strictly greater than its neighbors.
 * Given a 0-indexed integer array nums, find a peak element, and return its
 * index.
 * If the array contains multiple peaks, return the index to any of the peaks.
 * You may imagine that nums[-1] = nums[n] = -∞.
 * You must write an algorithm that runs in O(log n) time.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe, Uber
 * Difficulty: Medium
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= nums.length <= 1000
 * - -2^31 <= nums[i] <= 2^31 - 1
 * - nums[i] != nums[i + 1] for all valid i
 *
 * Follow-ups:
 * - Can you find all peak elements?
 * - What if the array can have duplicates?
 * - How would you handle a 2D array (find peak in 2D matrix)?
 */
public class FindPeakElementExtended {

    // Binary Search - O(log n) time, O(1) space
    public int findPeakElement(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Compare with right neighbor
            if (nums[mid] > nums[mid + 1]) {
                // Peak is on the left side (including mid)
                right = mid;
            } else {
                // Peak is on the right side
                left = mid + 1;
            }
        }

        return left; // left == right at this point
    }

    // Alternative implementation comparing with both neighbors
    public int findPeakElementAlternative(int[] nums) {
        int n = nums.length;

        // Handle edge cases
        if (n == 1)
            return 0;
        if (nums[0] > nums[1])
            return 0;
        if (nums[n - 1] > nums[n - 2])
            return n - 1;

        int left = 1;
        int right = n - 2;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] > nums[mid - 1] && nums[mid] > nums[mid + 1]) {
                return mid;
            } else if (nums[mid] < nums[mid + 1]) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1; // Should never reach here
    }

    // Recursive approach
    public int findPeakElementRecursive(int[] nums) {
        return findPeakRecursiveHelper(nums, 0, nums.length - 1);
    }

    private int findPeakRecursiveHelper(int[] nums, int left, int right) {
        if (left == right) {
            return left;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] > nums[mid + 1]) {
            return findPeakRecursiveHelper(nums, left, mid);
        } else {
            return findPeakRecursiveHelper(nums, mid + 1, right);
        }
    }

    // Linear search for comparison - O(n) time
    public int findPeakElementLinear(int[] nums) {
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            boolean leftOk = (i == 0) || (nums[i] > nums[i - 1]);
            boolean rightOk = (i == n - 1) || (nums[i] > nums[i + 1]);

            if (leftOk && rightOk) {
                return i;
            }
        }

        return -1; // Should never reach here
    }

    // Find all peaks - O(n) time
    public java.util.List<Integer> findAllPeaks(int[] nums) {
        java.util.List<Integer> peaks = new java.util.ArrayList<>();
        int n = nums.length;

        for (int i = 0; i < n; i++) {
            boolean leftOk = (i == 0) || (nums[i] > nums[i - 1]);
            boolean rightOk = (i == n - 1) || (nums[i] > nums[i + 1]);

            if (leftOk && rightOk) {
                peaks.add(i);
            }
        }

        return peaks;
    }

    // Handle duplicates version (modified problem)
    public int findPeakElementWithDuplicates(int[] nums) {
        int n = nums.length;
        if (n == 1)
            return 0;

        for (int i = 0; i < n; i++) {
            boolean leftOk = (i == 0) || (nums[i] >= nums[i - 1]);
            boolean rightOk = (i == n - 1) || (nums[i] >= nums[i + 1]);

            if (leftOk && rightOk) {
                return i;
            }
        }

        return -1;
    }

    // Template pattern approach
    public int findPeakElementTemplate(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (isPeak(nums, mid)) {
                return mid;
            }

            if (nums[mid] < nums[mid + 1]) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        return left;
    }

    private boolean isPeak(int[] nums, int index) {
        int n = nums.length;
        boolean leftOk = (index == 0) || (nums[index] > nums[index - 1]);
        boolean rightOk = (index == n - 1) || (nums[index] > nums[index + 1]);
        return leftOk && rightOk;
    }

    // Find peak in 2D array (bonus follow-up)
    public int[] findPeakGrid(int[][] mat) {
        int m = mat.length;
        int left = 0, right = m - 1;

        while (left <= right) {
            int midRow = left + (right - left) / 2;
            int maxCol = findMaxInRow(mat[midRow]);

            boolean isTopSmaller = (midRow == 0) || (mat[midRow][maxCol] > mat[midRow - 1][maxCol]);
            boolean isBottomSmaller = (midRow == m - 1) || (mat[midRow][maxCol] > mat[midRow + 1][maxCol]);

            if (isTopSmaller && isBottomSmaller) {
                return new int[] { midRow, maxCol };
            } else if (!isTopSmaller) {
                right = midRow - 1;
            } else {
                left = midRow + 1;
            }
        }

        return new int[] { -1, -1 };
    }

    private int findMaxInRow(int[] row) {
        int maxIndex = 0;
        for (int i = 1; i < row.length; i++) {
            if (row[i] > row[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static void main(String[] args) {
        FindPeakElementExtended solution = new FindPeakElementExtended();

        // Test Case 1: [1,2,3,1]
        int[] nums1 = { 1, 2, 3, 1 };
        System.out.println(solution.findPeakElement(nums1)); // Expected: 2

        // Test Case 2: [1,2,1,3,5,6,4]
        int[] nums2 = { 1, 2, 1, 3, 5, 6, 4 };
        System.out.println(solution.findPeakElement(nums2)); // Expected: 1 or 5

        // Test Case 3: Single element
        int[] nums3 = { 1 };
        System.out.println(solution.findPeakElement(nums3)); // Expected: 0

        // Test Case 4: Two elements - ascending
        int[] nums4 = { 1, 2 };
        System.out.println(solution.findPeakElement(nums4)); // Expected: 1

        // Test Case 5: Two elements - descending
        int[] nums5 = { 2, 1 };
        System.out.println(solution.findPeakElement(nums5)); // Expected: 0

        // Test Case 6: Mountain array
        int[] nums6 = { 1, 2, 3, 4, 5, 4, 3, 2, 1 };
        System.out.println(solution.findPeakElement(nums6)); // Expected: 4

        // Test Case 7: Valley array
        int[] nums7 = { 5, 4, 3, 2, 1, 2, 3, 4, 5 };
        System.out.println(solution.findPeakElement(nums7)); // Expected: 0 or 8

        // Test alternative implementation
        System.out.println("Alternative: " + solution.findPeakElementAlternative(nums1)); // Expected: 2

        // Test recursive version
        System.out.println("Recursive: " + solution.findPeakElementRecursive(nums1)); // Expected: 2

        // Test linear version
        System.out.println("Linear: " + solution.findPeakElementLinear(nums1)); // Expected: 2

        // Test find all peaks
        java.util.List<Integer> allPeaks = solution.findAllPeaks(nums2);
        System.out.println("All peaks: " + allPeaks); // Expected: [1, 5]

        // Test with negative numbers
        int[] nums8 = { -1, -2, -3, -1 };
        System.out.println("Negative numbers: " + solution.findPeakElement(nums8)); // Expected: 0 or 3

        // Test 2D peak finding
        int[][] mat = {
                { 1, 4 },
                { 3, 2 }
        };
        int[] peak2D = solution.findPeakGrid(mat);
        System.out.println("2D Peak: [" + peak2D[0] + ", " + peak2D[1] + "]"); // Expected: [0, 1]

        // Large test case
        int[] nums9 = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
        System.out.println("Large array: " + solution.findPeakElement(nums9)); // Expected: 8
    }
}
