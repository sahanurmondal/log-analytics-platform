package searching.medium;

import java.util.*;

/**
 * LeetCode 153: Find Minimum in Rotated Sorted Array
 * https://leetcode.com/problems/find-minimum-in-rotated-sorted-array/
 * 
 * Companies: Amazon, Microsoft, Facebook, Google, Apple, Bloomberg
 * Frequency: Very High (Asked in 700+ interviews)
 *
 * Description:
 * Suppose an array of length n sorted in ascending order is rotated between 1
 * and n times.
 * Given the sorted rotated array nums of unique elements, return the minimum
 * element of this array.
 * You must write an algorithm that runs in O(log n) time.
 * 
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 5000
 * - -5000 <= nums[i] <= 5000
 * - All the integers of nums are unique.
 * - nums is sorted and rotated between 1 and n times.
 * 
 * Follow-up Questions:
 * 1. How would you handle duplicate elements?
 * 2. Can you find the rotation point/pivot index?
 * 3. How to handle the case where array is not rotated?
 * 4. What about finding maximum element instead?
 * 5. Can you implement iterative and recursive solutions?
 * 6. How to optimize for specific patterns?
 */
public class FindMinimumInRotatedArray {

    // Approach 1: Binary Search (Iterative) - O(log n) time, O(1) space
    public static int findMin(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // If mid element is greater than right element,
            // minimum must be in right half
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                // Minimum is in left half (including mid)
                right = mid;
            }
        }

        return nums[left];
    }

    // Approach 2: Binary Search (Recursive) - O(log n) time, O(log n) space
    public static int findMinRecursive(int[] nums) {
        return findMinHelper(nums, 0, nums.length - 1);
    }

    private static int findMinHelper(int[] nums, int left, int right) {
        // Base case: single element or two elements
        if (left == right)
            return nums[left];
        if (left + 1 == right)
            return Math.min(nums[left], nums[right]);

        int mid = left + (right - left) / 2;

        // If array is not rotated in this segment
        if (nums[left] < nums[right]) {
            return nums[left];
        }

        // Find which half contains the minimum
        if (nums[mid] > nums[right]) {
            return findMinHelper(nums, mid + 1, right);
        } else {
            return findMinHelper(nums, left, mid);
        }
    }

    // Approach 3: Optimized for specific patterns
    public static int findMinOptimized(int[] nums) {
        int n = nums.length;

        // Edge case: single element
        if (n == 1)
            return nums[0];

        // Check if array is not rotated
        if (nums[0] < nums[n - 1])
            return nums[0];

        int left = 0;
        int right = n - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Found the rotation point
            if (mid > 0 && nums[mid] < nums[mid - 1]) {
                return nums[mid];
            }

            if (mid < n - 1 && nums[mid] > nums[mid + 1]) {
                return nums[mid + 1];
            }

            // Decide which half to search
            if (nums[mid] > nums[right]) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return nums[left];
    }

    // Follow-up 1: Handle duplicate elements (LeetCode 154)
    public static class FindMinWithDuplicates {

        public static int findMin(int[] nums) {
            int left = 0;
            int right = nums.length - 1;

            while (left < right) {
                int mid = left + (right - left) / 2;

                if (nums[mid] > nums[right]) {
                    left = mid + 1;
                } else if (nums[mid] < nums[right]) {
                    right = mid;
                } else {
                    // nums[mid] == nums[right], can't decide, reduce search space
                    right--;
                }
            }

            return nums[left];
        }

        // Alternative approach for duplicates
        public static int findMinDuplicatesLinearScan(int[] nums) {
            int left = 0;
            int right = nums.length - 1;

            while (left < right) {
                int mid = left + (right - left) / 2;

                if (nums[mid] > nums[right]) {
                    left = mid + 1;
                } else if (nums[mid] < nums[right]) {
                    right = mid;
                } else {
                    // Linear scan when all three are equal
                    if (nums[left] == nums[mid] && nums[mid] == nums[right]) {
                        int min = nums[left];
                        for (int i = left + 1; i <= right; i++) {
                            min = Math.min(min, nums[i]);
                        }
                        return min;
                    }
                    right--;
                }
            }

            return nums[left];
        }
    }

    // Follow-up 2: Find rotation point/pivot index
    public static class FindRotationPoint {

        public static int findRotationPoint(int[] nums) {
            int n = nums.length;

            // Array is not rotated
            if (nums[0] < nums[n - 1])
                return 0;

            int left = 0;
            int right = n - 1;

            while (left < right) {
                int mid = left + (right - left) / 2;

                // Found the rotation point
                if (nums[mid] > nums[mid + 1]) {
                    return mid + 1;
                }

                if (nums[mid] < nums[mid - 1]) {
                    return mid;
                }

                // Decide which half to search
                if (nums[mid] > nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return 0; // Array is not rotated
        }

        // Find number of rotations
        public static int countRotations(int[] nums) {
            return findRotationPoint(nums);
        }

        // Check if array is rotated
        public static boolean isRotated(int[] nums) {
            return findRotationPoint(nums) != 0;
        }
    }

    // Follow-up 3: Find maximum element
    public static class FindMaximum {

        public static int findMax(int[] nums) {
            int n = nums.length;

            // Array is not rotated
            if (nums[0] < nums[n - 1])
                return nums[n - 1];

            int left = 0;
            int right = n - 1;

            while (left < right) {
                int mid = left + (right - left) / 2;

                // Maximum is the element just before the minimum
                if (nums[mid] > nums[mid + 1]) {
                    return nums[mid];
                }

                if (nums[mid] < nums[mid - 1]) {
                    return nums[mid - 1];
                }

                // Decide which half to search
                if (nums[mid] > nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return nums[left];
        }

        // Alternative: find max using min
        public static int findMaxUsingMin(int[] nums) {
            int rotationPoint = FindRotationPoint.findRotationPoint(nums);

            if (rotationPoint == 0) {
                return nums[nums.length - 1];
            } else {
                return nums[rotationPoint - 1];
            }
        }
    }

    // Advanced: Multiple approaches comparison
    public static class MultipleApproaches {

        // Linear search (for comparison)
        public static int findMinLinear(int[] nums) {
            int min = nums[0];
            for (int num : nums) {
                min = Math.min(min, num);
            }
            return min;
        }

        // Modified binary search with early termination
        public static int findMinEarlyTermination(int[] nums) {
            int left = 0;
            int right = nums.length - 1;

            // Early termination: if already sorted
            if (nums[left] <= nums[right]) {
                return nums[left];
            }

            while (left < right) {
                int mid = left + (right - left) / 2;

                // Early termination: found exact rotation point
                if (mid > 0 && nums[mid] < nums[mid - 1]) {
                    return nums[mid];
                }
                if (mid < nums.length - 1 && nums[mid] > nums[mid + 1]) {
                    return nums[mid + 1];
                }

                if (nums[mid] > nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            return nums[left];
        }

        // Three-way comparison
        public static int findMinThreeWay(int[] nums) {
            int left = 0;
            int right = nums.length - 1;

            while (left < right) {
                int mid = left + (right - left) / 2;

                if (nums[left] < nums[mid] && nums[mid] < nums[right]) {
                    // Sorted segment
                    return nums[left];
                } else if (nums[left] > nums[mid]) {
                    // Minimum in left half
                    right = mid;
                } else {
                    // Minimum in right half
                    left = mid + 1;
                }
            }

            return nums[left];
        }
    }

    // Advanced: Handle special cases and edge conditions
    public static class RobustImplementation {

        public static int findMinRobust(int[] nums) {
            if (nums == null || nums.length == 0) {
                throw new IllegalArgumentException("Array cannot be null or empty");
            }

            int n = nums.length;

            // Handle single element
            if (n == 1)
                return nums[0];

            // Handle two elements
            if (n == 2)
                return Math.min(nums[0], nums[1]);

            int left = 0;
            int right = n - 1;

            // Array is not rotated
            if (nums[left] < nums[right]) {
                return nums[left];
            }

            while (left < right) {
                // Avoid infinite loop
                if (left + 1 == right) {
                    return Math.min(nums[left], nums[right]);
                }

                int mid = left + (right - left) / 2;

                // Check boundaries to avoid array out of bounds
                if (mid > 0 && nums[mid] < nums[mid - 1]) {
                    return nums[mid];
                }

                if (mid < n - 1 && nums[mid] > nums[mid + 1]) {
                    return nums[mid + 1];
                }

                // Standard binary search logic
                if (nums[mid] > nums[right]) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }

            return nums[left];
        }

        // With detailed step tracking
        public static class SearchResult {
            int minimum;
            int steps;
            List<String> trace;

            public SearchResult(int minimum, int steps, List<String> trace) {
                this.minimum = minimum;
                this.steps = steps;
                this.trace = trace;
            }

            @Override
            public String toString() {
                return String.format("Minimum: %d, Steps: %d, Trace: %s",
                        minimum, steps, trace);
            }
        }

        public static SearchResult findMinWithTrace(int[] nums) {
            List<String> trace = new ArrayList<>();
            int steps = 0;

            if (nums == null || nums.length == 0) {
                return new SearchResult(-1, 0, Arrays.asList("Error: empty array"));
            }

            int left = 0;
            int right = nums.length - 1;

            trace.add("Initial: left=" + left + ", right=" + right);

            while (left < right) {
                steps++;
                int mid = left + (right - left) / 2;

                trace.add("Step " + steps + ": left=" + left + ", mid=" + mid +
                        ", right=" + right + ", nums[mid]=" + nums[mid] +
                        ", nums[right]=" + nums[right]);

                if (nums[mid] > nums[right]) {
                    left = mid + 1;
                    trace.add("  Moving left to " + left);
                } else {
                    right = mid;
                    trace.add("  Moving right to " + right);
                }
            }

            trace.add("Final: minimum = " + nums[left]);
            return new SearchResult(nums[left], steps, trace);
        }
    }

    // Performance comparison utility
    public static class PerformanceComparison {

        public static void compareApproaches(int[] nums, int iterations) {
            System.out.println("=== Performance Comparison ===");
            System.out.println("Array size: " + nums.length + ", Iterations: " + iterations);

            // Standard binary search
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findMin(nums);
            }
            long standardTime = System.nanoTime() - start;

            // Recursive binary search
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findMinRecursive(nums);
            }
            long recursiveTime = System.nanoTime() - start;

            // Optimized approach
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                findMinOptimized(nums);
            }
            long optimizedTime = System.nanoTime() - start;

            // Linear search (for comparison)
            start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                MultipleApproaches.findMinLinear(nums);
            }
            long linearTime = System.nanoTime() - start;

            System.out.println("Standard Binary Search: " + standardTime / 1_000_000 + " ms");
            System.out.println("Recursive Binary Search: " + recursiveTime / 1_000_000 + " ms");
            System.out.println("Optimized Approach: " + optimizedTime / 1_000_000 + " ms");
            System.out.println("Linear Search: " + linearTime / 1_000_000 + " ms");
        }
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        int[] nums1 = { 3, 4, 5, 1, 2 };
        System.out.println("Input: " + Arrays.toString(nums1));
        System.out.println("Minimum (iterative): " + findMin(nums1));
        System.out.println("Minimum (recursive): " + findMinRecursive(nums1));
        System.out.println("Minimum (optimized): " + findMinOptimized(nums1));

        // Test Case 2: Different rotation points
        System.out.println("\n=== Test Case 2: Different Rotation Points ===");

        int[] nums2a = { 4, 5, 6, 7, 0, 1, 2 }; // Rotated at position 4
        int[] nums2b = { 2, 1 }; // Rotated at position 1
        int[] nums2c = { 1 }; // Single element

        System.out.println("Input: " + Arrays.toString(nums2a) + " -> Min: " + findMin(nums2a));
        System.out.println("Input: " + Arrays.toString(nums2b) + " -> Min: " + findMin(nums2b));
        System.out.println("Input: " + Arrays.toString(nums2c) + " -> Min: " + findMin(nums2c));

        // Test Case 3: Not rotated array
        System.out.println("\n=== Test Case 3: Not Rotated Array ===");

        int[] nums3 = { 1, 2, 3, 4, 5 };
        System.out.println("Input: " + Arrays.toString(nums3));
        System.out.println("Minimum: " + findMin(nums3));
        System.out.println("Is rotated: " + FindRotationPoint.isRotated(nums3));

        // Test Case 4: Find rotation point
        System.out.println("\n=== Test Case 4: Find Rotation Point ===");

        int[] nums4 = { 4, 5, 6, 7, 0, 1, 2 };
        System.out.println("Input: " + Arrays.toString(nums4));
        System.out.println("Rotation point: " + FindRotationPoint.findRotationPoint(nums4));
        System.out.println("Number of rotations: " + FindRotationPoint.countRotations(nums4));
        System.out.println("Is rotated: " + FindRotationPoint.isRotated(nums4));

        // Test Case 5: With duplicates
        System.out.println("\n=== Test Case 5: With Duplicates ===");

        int[] nums5a = { 1, 3, 5 };
        int[] nums5b = { 2, 2, 2, 0, 1 };
        int[] nums5c = { 1, 1, 1, 1 };

        System.out.println("Input: " + Arrays.toString(nums5a) + " -> Min: " +
                FindMinWithDuplicates.findMin(nums5a));
        System.out.println("Input: " + Arrays.toString(nums5b) + " -> Min: " +
                FindMinWithDuplicates.findMin(nums5b));
        System.out.println("Input: " + Arrays.toString(nums5c) + " -> Min: " +
                FindMinWithDuplicates.findMin(nums5c));

        // Test Case 6: Find maximum element
        System.out.println("\n=== Test Case 6: Find Maximum Element ===");

        int[] nums6 = { 3, 4, 5, 1, 2 };
        System.out.println("Input: " + Arrays.toString(nums6));
        System.out.println("Maximum: " + FindMaximum.findMax(nums6));
        System.out.println("Maximum (using min): " + FindMaximum.findMaxUsingMin(nums6));

        // Test Case 7: Multiple approaches comparison
        System.out.println("\n=== Test Case 7: Multiple Approaches ===");

        int[] nums7 = { 6, 7, 0, 1, 2, 4, 5 };
        System.out.println("Input: " + Arrays.toString(nums7));
        System.out.println("Linear search: " + MultipleApproaches.findMinLinear(nums7));
        System.out.println("Early termination: " + MultipleApproaches.findMinEarlyTermination(nums7));
        System.out.println("Three-way: " + MultipleApproaches.findMinThreeWay(nums7));

        // Test Case 8: Robust implementation with trace
        System.out.println("\n=== Test Case 8: Robust Implementation ===");

        int[] nums8 = { 4, 5, 6, 7, 0, 1, 2 };
        System.out.println("Input: " + Arrays.toString(nums8));

        RobustImplementation.SearchResult result = RobustImplementation.findMinWithTrace(nums8);
        System.out.println("Result: " + result);

        // Test Case 9: Edge cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        int[] singleElement = { 42 };
        int[] twoElements = { 2, 1 };
        int[] allSame = { 5, 5, 5, 5, 5 };

        System.out.println("Single element: " + Arrays.toString(singleElement) +
                " -> Min: " + findMin(singleElement));
        System.out.println("Two elements: " + Arrays.toString(twoElements) +
                " -> Min: " + findMin(twoElements));
        System.out.println("All same: " + Arrays.toString(allSame) +
                " -> Min: " + FindMinWithDuplicates.findMin(allSame));

        // Test Case 10: Large arrays
        System.out.println("\n=== Test Case 10: Large Arrays ===");

        // Create large rotated array
        int[] largeArray = new int[5000];
        int rotationPoint = 2000;

        for (int i = 0; i < largeArray.length; i++) {
            largeArray[i] = (i + rotationPoint) % largeArray.length;
        }

        long start = System.currentTimeMillis();
        int min = findMin(largeArray);
        long end = System.currentTimeMillis();

        System.out.println("Large array size: " + largeArray.length);
        System.out.println("Minimum found: " + min);
        System.out.println("Time taken: " + (end - start) + " ms");

        // Test Case 11: Stress test with random rotations
        System.out.println("\n=== Test Case 11: Stress Test ===");

        Random random = new Random(42); // Fixed seed for reproducibility
        int testCases = 1000;
        int passed = 0;

        for (int test = 0; test < testCases; test++) {
            int size = random.nextInt(20) + 1;
            int[] arr = new int[size];

            // Create sorted array
            for (int i = 0; i < size; i++) {
                arr[i] = i;
            }

            // Rotate it
            int rotations = random.nextInt(size);
            int[] rotated = new int[size];
            for (int i = 0; i < size; i++) {
                rotated[i] = arr[(i + rotations) % size];
            }

            int expected = 0; // Minimum is always 0 in this test
            int actual = findMin(rotated);

            if (actual == expected) {
                passed++;
            } else {
                System.out.println("Failed test: " + Arrays.toString(rotated) +
                        ", expected: " + expected + ", got: " + actual);
            }
        }

        System.out.println("Stress test results: " + passed + "/" + testCases + " passed");

        // Test Case 12: Performance comparison
        System.out.println("\n=== Test Case 12: Performance Comparison ===");

        int[] perfTestArray = new int[1000];
        for (int i = 0; i < perfTestArray.length; i++) {
            perfTestArray[i] = (i + 300) % perfTestArray.length;
        }

        PerformanceComparison.compareApproaches(perfTestArray, 10000);

        System.out.println("\nFind Minimum in Rotated Array testing completed successfully!");
    }
}
