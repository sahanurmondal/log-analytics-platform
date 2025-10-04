package binarysearch.medium;

/**
 * LeetCode 540: Single Element in a Sorted Array
 * https://leetcode.com/problems/single-element-in-a-sorted-array/
 *
 * Description:
 * You are given a sorted array consisting of only integers where every element
 * appears exactly twice,
 * except for one element which appears exactly once.
 * Return the single element that appears only once.
 * Your solution must run in O(log n) time and O(1) space.
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, LinkedIn, Bloomberg,
 * Adobe
 * Difficulty: Medium
 * Asked: 2023-2024 (High Frequency)
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 0 <= nums[i] <= 10^5
 * - nums.length is odd
 * - All elements appear exactly twice, except for one element which appears
 * once
 *
 * Follow-ups:
 * - What if the array is not sorted?
 * - Can you solve this with XOR approach?
 * - What if multiple elements appear once?
 */
public class SingleElementInSortedArray {

    // Binary Search - O(log n) time, O(1) space
    public int singleNonDuplicate(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Ensure mid is even for consistent comparison
            if (mid % 2 == 1) {
                mid--;
            }

            // Check if the pair is intact
            if (nums[mid] == nums[mid + 1]) {
                // Pair is intact, single element is on the right
                left = mid + 2;
            } else {
                // Pair is broken, single element is on the left (including mid)
                right = mid;
            }
        }

        return nums[left];
    }

    // Alternative approach without adjusting mid
    public int singleNonDuplicateAlt(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Determine if we should compare with left or right neighbor
            boolean shouldCompareWithRight = (mid % 2 == 0) ? true : false;

            if (shouldCompareWithRight) {
                if (mid + 1 < nums.length && nums[mid] == nums[mid + 1]) {
                    left = mid + 2;
                } else {
                    right = mid;
                }
            } else {
                if (nums[mid] == nums[mid - 1]) {
                    left = mid + 1;
                } else {
                    right = mid;
                }
            }
        }

        return nums[left];
    }

    // XOR approach - O(n) time, O(1) space (for comparison)
    public int singleNonDuplicateXOR(int[] nums) {
        int result = 0;
        for (int num : nums) {
            result ^= num;
        }
        return result;
    }

    // Binary search with explicit pair checking
    public int singleNonDuplicateExplicit(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            // Check if mid is part of a pair
            boolean hasPairWithLeft = (mid > 0 && nums[mid] == nums[mid - 1]);
            boolean hasPairWithRight = (mid < nums.length - 1 && nums[mid] == nums[mid + 1]);

            if (!hasPairWithLeft && !hasPairWithRight) {
                // Found the single element
                return nums[mid];
            }

            // Determine which side has odd number of elements
            int elementsOnLeft;
            if (hasPairWithLeft) {
                elementsOnLeft = mid - 1;
                if (elementsOnLeft % 2 == 1) {
                    right = mid - 2;
                } else {
                    left = mid + 1;
                }
            } else { // hasPairWithRight
                elementsOnLeft = mid;
                if (elementsOnLeft % 2 == 1) {
                    right = mid - 1;
                } else {
                    left = mid + 2;
                }
            }
        }

        return nums[left];
    }

    // Linear search for comparison - O(n) time
    public int singleNonDuplicateLinear(int[] nums) {
        for (int i = 0; i < nums.length; i += 2) {
            if (i == nums.length - 1 || nums[i] != nums[i + 1]) {
                return nums[i];
            }
        }
        return -1; // Should never reach here
    }

    // Recursive approach
    public int singleNonDuplicateRecursive(int[] nums) {
        return singleNonDuplicateRecursiveHelper(nums, 0, nums.length - 1);
    }

    private int singleNonDuplicateRecursiveHelper(int[] nums, int left, int right) {
        if (left == right) {
            return nums[left];
        }

        int mid = left + (right - left) / 2;

        // Ensure mid is even
        if (mid % 2 == 1) {
            mid--;
        }

        if (nums[mid] == nums[mid + 1]) {
            return singleNonDuplicateRecursiveHelper(nums, mid + 2, right);
        } else {
            return singleNonDuplicateRecursiveHelper(nums, left, mid);
        }
    }

    // Find position of single element (returns index)
    public int findSingleElementIndex(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (mid % 2 == 1) {
                mid--;
            }

            if (nums[mid] == nums[mid + 1]) {
                left = mid + 2;
            } else {
                right = mid;
            }
        }

        return left;
    }

    // Handle edge cases explicitly
    public int singleNonDuplicateRobust(int[] nums) {
        if (nums.length == 1) {
            return nums[0];
        }

        // Check if single element is at the beginning
        if (nums[0] != nums[1]) {
            return nums[0];
        }

        // Check if single element is at the end
        if (nums[nums.length - 1] != nums[nums.length - 2]) {
            return nums[nums.length - 1];
        }

        // Single element is in the middle
        return singleNonDuplicate(nums);
    }

    // Template pattern approach
    public int singleNonDuplicateTemplate(int[] nums) {
        int left = 0;
        int right = nums.length - 1;

        while (left < right) {
            int mid = left + (right - left) / 2;

            if (isSingleElement(nums, mid)) {
                return nums[mid];
            }

            // Determine which side to search
            if (shouldSearchLeft(nums, mid)) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return nums[left];
    }

    private boolean isSingleElement(int[] nums, int index) {
        boolean differentFromLeft = (index == 0 || nums[index] != nums[index - 1]);
        boolean differentFromRight = (index == nums.length - 1 || nums[index] != nums[index + 1]);
        return differentFromLeft && differentFromRight;
    }

    private boolean shouldSearchLeft(int[] nums, int mid) {
        // Complex logic to determine search direction
        boolean hasPairWithLeft = (mid > 0 && nums[mid] == nums[mid - 1]);
        boolean hasPairWithRight = (mid < nums.length - 1 && nums[mid] == nums[mid + 1]);

        if (hasPairWithLeft) {
            return (mid - 1) % 2 == 1;
        } else if (hasPairWithRight) {
            return mid % 2 == 1;
        }

        return false;
    }

    public static void main(String[] args) {
        SingleElementInSortedArray solution = new SingleElementInSortedArray();

        // Test Case 1: [1,1,2,3,3,4,4,8,8]
        int[] nums1 = { 1, 1, 2, 3, 3, 4, 4, 8, 8 };
        System.out.println(solution.singleNonDuplicate(nums1)); // Expected: 2

        // Test Case 2: [3,3,7,7,10,11,11]
        int[] nums2 = { 3, 3, 7, 7, 10, 11, 11 };
        System.out.println(solution.singleNonDuplicate(nums2)); // Expected: 10

        // Test Case 3: Single element
        int[] nums3 = { 1 };
        System.out.println(solution.singleNonDuplicate(nums3)); // Expected: 1

        // Test Case 4: Single element at beginning
        int[] nums4 = { 1, 2, 2, 3, 3 };
        System.out.println(solution.singleNonDuplicate(nums4)); // Expected: 1

        // Test Case 5: Single element at end
        int[] nums5 = { 1, 1, 2, 2, 3 };
        System.out.println(solution.singleNonDuplicate(nums5)); // Expected: 3

        // Test Case 6: Large numbers
        int[] nums6 = { 0, 0, 50, 50, 100 };
        System.out.println(solution.singleNonDuplicate(nums6)); // Expected: 100

        // Test alternative approach
        System.out.println("Alternative: " + solution.singleNonDuplicateAlt(nums1)); // Expected: 2

        // Test XOR approach
        System.out.println("XOR: " + solution.singleNonDuplicateXOR(nums1)); // Expected: 2

        // Test explicit approach
        System.out.println("Explicit: " + solution.singleNonDuplicateExplicit(nums1)); // Expected: 2

        // Test linear approach
        System.out.println("Linear: " + solution.singleNonDuplicateLinear(nums1)); // Expected: 2

        // Test recursive approach
        System.out.println("Recursive: " + solution.singleNonDuplicateRecursive(nums1)); // Expected: 2

        // Test find index
        System.out.println("Index: " + solution.findSingleElementIndex(nums1)); // Expected: 2

        // Test robust version
        System.out.println("Robust: " + solution.singleNonDuplicateRobust(nums1)); // Expected: 2

        // Test template approach
        System.out.println("Template: " + solution.singleNonDuplicateTemplate(nums1)); // Expected: 2

        // Edge cases
        int[] edge1 = { 1, 1, 2 };
        System.out.println("Edge case 1: " + solution.singleNonDuplicate(edge1)); // Expected: 2

        int[] edge2 = { 1, 2, 2 };
        System.out.println("Edge case 2: " + solution.singleNonDuplicate(edge2)); // Expected: 1

        int[] edge3 = { 1, 1, 2, 2, 3, 3, 4 };
        System.out.println("Edge case 3: " + solution.singleNonDuplicate(edge3)); // Expected: 4

        // Large test case
        int[] large = new int[10001];
        for (int i = 0; i < 5000; i++) {
            large[2 * i] = i;
            large[2 * i + 1] = i;
        }
        large[10000] = 5000; // Single element at the end

        long startTime = System.currentTimeMillis();
        int largeResult = solution.singleNonDuplicate(large);
        long endTime = System.currentTimeMillis();
        System.out.println("Large test: " + largeResult + " (time: " + (endTime - startTime) + "ms)"); // Expected: 5000

        // Performance comparison
        startTime = System.currentTimeMillis();
        solution.singleNonDuplicateXOR(large);
        long xorTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        solution.singleNonDuplicate(large);
        long binaryTime = System.currentTimeMillis() - startTime;

        System.out.println("XOR time: " + xorTime + "ms, Binary search time: " + binaryTime + "ms");
    }
}
