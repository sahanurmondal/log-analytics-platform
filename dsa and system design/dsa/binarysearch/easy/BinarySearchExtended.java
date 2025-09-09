package binarysearch.easy;

/**
 * LeetCode 704: Binary Search
 * https://leetcode.com/problems/binary-search/
 *
 * Description:
 * Given an array of integers nums which is sorted in ascending order, and an
 * integer target,
 * write a function to search target in nums. If target exists, then return its
 * index. Otherwise, return -1.
 * You must write an algorithm with O(log n) runtime complexity.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe
 * Difficulty: Easy
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^4 <= nums[i], target <= 10^4
 * - All integers in nums are unique
 * - nums is sorted in ascending order
 *
 * Follow-ups:
 * - Can you implement all different binary search templates?
 * - What if the array is very large and doesn't fit in memory?
 * - How would you handle floating-point numbers?
 */
public class BinarySearchExtended {

    // Standard Binary Search - O(log n) time, O(1) space
    public int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

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

    // Template I: Standard template (left <= right)
    public int searchTemplateI(int[] nums, int target) {
        if (nums.length == 0)
            return -1;

        int left = 0;
        int right = nums.length - 1;

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

    // Template II: Advanced template (left < right)
    public int searchTemplateII(int[] nums, int target) {
        if (nums.length == 0)
            return -1;

        int left = 0;
        int right = nums.length;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }

        // Post-processing
        return (left < nums.length && nums[left] == target) ? left : -1;
    }

    // Template III: Complex template (left + 1 < right)
    public int searchTemplateIII(int[] nums, int target) {
        if (nums.length == 0)
            return -1;

        int left = 0;
        int right = nums.length - 1;

        while (left + 1 < right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] < target) {
                left = mid;
            } else {
                right = mid;
            }
        }

        // Post-processing
        if (nums[left] == target)
            return left;
        if (nums[right] == target)
            return right;

        return -1;
    }

    // Recursive implementation
    public int searchRecursive(int[] nums, int target) {
        return searchRecursiveHelper(nums, target, 0, nums.length - 1);
    }

    private int searchRecursiveHelper(int[] nums, int target, int left, int right) {
        if (left > right) {
            return -1;
        }

        int mid = left + (right - left) / 2;

        if (nums[mid] == target) {
            return mid;
        } else if (nums[mid] < target) {
            return searchRecursiveHelper(nums, target, mid + 1, right);
        } else {
            return searchRecursiveHelper(nums, target, left, mid - 1);
        }
    }

    // Binary search for floating-point numbers
    public boolean searchFloat(double[] nums, double target, double epsilon) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (Math.abs(nums[mid] - target) <= epsilon) {
                return true;
            } else if (nums[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return false;
    }

    // Find lower bound (first position where element >= target)
    public int lowerBound(int[] nums, int target) {
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

    // Find upper bound (first position where element > target)
    public int upperBound(int[] nums, int target) {
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

    // Binary search in descending order array
    public int searchDescending(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (nums[mid] == target) {
                return mid;
            } else if (nums[mid] > target) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    // Generic binary search with comparator
    public <T extends Comparable<T>> int searchGeneric(T[] nums, T target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int cmp = nums[mid].compareTo(target);

            if (cmp == 0) {
                return mid;
            } else if (cmp < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return -1;
    }

    // Binary search with custom predicate
    public int searchWithPredicate(int[] nums, java.util.function.Predicate<Integer> predicate) {
        int left = 0;
        int right = nums.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (predicate.test(nums[mid])) {
                result = mid;
                right = mid - 1; // Continue searching for the first occurrence
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    // Count occurrences using binary search
    public int countOccurrences(int[] nums, int target) {
        int first = lowerBound(nums, target);
        int last = upperBound(nums, target);

        return (first < nums.length && nums[first] == target) ? last - first : 0;
    }

    public static void main(String[] args) {
        BinarySearchExtended solution = new BinarySearchExtended();

        // Test Case 1: Standard case
        int[] nums1 = { -1, 0, 3, 5, 9, 12 };
        System.out.println(solution.search(nums1, 9)); // Expected: 4

        // Test Case 2: Target not found
        System.out.println(solution.search(nums1, 2)); // Expected: -1

        // Test Case 3: Single element - found
        int[] nums2 = { 5 };
        System.out.println(solution.search(nums2, 5)); // Expected: 0

        // Test Case 4: Single element - not found
        System.out.println(solution.search(nums2, 2)); // Expected: -1

        // Test Case 5: Target at beginning
        System.out.println(solution.search(nums1, -1)); // Expected: 0

        // Test Case 6: Target at end
        System.out.println(solution.search(nums1, 12)); // Expected: 5

        // Test different templates
        System.out.println("Template I: " + solution.searchTemplateI(nums1, 9)); // Expected: 4
        System.out.println("Template II: " + solution.searchTemplateII(nums1, 9)); // Expected: 4
        System.out.println("Template III: " + solution.searchTemplateIII(nums1, 9)); // Expected: 4

        // Test recursive approach
        System.out.println("Recursive: " + solution.searchRecursive(nums1, 9)); // Expected: 4

        // Test floating-point search
        double[] floats = { 1.1, 2.2, 3.3, 4.4, 5.5 };
        System.out.println("Float search: " + solution.searchFloat(floats, 3.3, 0.01)); // Expected: true
        System.out.println("Float search (not found): " + solution.searchFloat(floats, 3.31, 0.01)); // Expected: false

        // Test bounds
        int[] nums3 = { 1, 2, 2, 2, 3, 4, 5 };
        System.out.println("Lower bound of 2: " + solution.lowerBound(nums3, 2)); // Expected: 1
        System.out.println("Upper bound of 2: " + solution.upperBound(nums3, 2)); // Expected: 4

        // Test descending array
        int[] descending = { 12, 9, 5, 3, 0, -1 };
        System.out.println("Descending search: " + solution.searchDescending(descending, 5)); // Expected: 2

        // Test generic search with strings
        String[] strings = { "apple", "banana", "cherry", "date", "elderberry" };
        System.out.println("Generic search: " + solution.searchGeneric(strings, "cherry")); // Expected: 2

        // Test with predicate (find first even number)
        int[] nums4 = { 1, 3, 4, 6, 8, 10 };
        System.out.println("First even: " + solution.searchWithPredicate(nums4, x -> x % 2 == 0)); // Expected: 2

        // Test count occurrences
        System.out.println("Count of 2: " + solution.countOccurrences(nums3, 2)); // Expected: 3
        System.out.println("Count of 6: " + solution.countOccurrences(nums3, 6)); // Expected: 0

        // Large array test
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++) {
            large[i] = i * 2;
        }
        System.out.println("Large array search: " + solution.search(large, 5000)); // Expected: 2500

        // Edge cases
        int[] empty = {};
        System.out.println("Empty array: " + solution.searchTemplateII(empty, 1)); // Expected: -1

        // Negative numbers
        int[] negative = { -10, -5, -2, 0, 3, 7 };
        System.out.println("Negative search: " + solution.search(negative, -5)); // Expected: 1

        // All same elements
        int[] same = { 5, 5, 5, 5, 5 };
        System.out.println("All same: " + solution.search(same, 5)); // Expected: any valid index (0-4)
    }
}
