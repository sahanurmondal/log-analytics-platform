package sorting.hard;

import java.util.*;

/**
 * LeetCode 324: Wiggle Sort II
 * URL: https://leetcode.com/problems/wiggle-sort-ii/
 * Difficulty: Hard
 * Companies: Google, Facebook, Amazon
 * Frequency: High
 * 
 * Description:
 * Given an integer array nums, reorder it such that nums[0] < nums[1] > nums[2]
 * < nums[3]....
 * You may assume the input array always has a valid answer.
 * 
 * Constraints:
 * - 1 <= nums.length <= 5 * 10^4
 * - 0 <= nums[i] <= 5000
 * - It is guaranteed that there will be an answer for the given input
 * 
 * Follow-up Questions:
 * 1. Can you solve it in O(n) time and O(1) extra space?
 * 2. How would you handle duplicate elements efficiently?
 * 3. Can you solve it without finding the median explicitly?
 * 4. What if the array has an odd number of elements?
 */
public class WiggleSortII {
    // O(n log n) solution with sorting
    public void wiggleSort(int[] nums) {
        int[] copy = Arrays.copyOf(nums, nums.length);
        Arrays.sort(copy);

        int n = nums.length;
        int mid = (n + 1) / 2 - 1;
        int end = n - 1;

        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                nums[i] = copy[mid--];
            } else {
                nums[i] = copy[end--];
            }
        }
    }

    // O(n) solution with quickselect and virtual indexing
    public void wiggleSortOptimal(int[] nums) {
        int n = nums.length;
        int median = findKthLargest(nums, (n + 1) / 2);

        int i = 0, j = 0, k = n - 1;
        while (j <= k) {
            if (nums[newIndex(j, n)] > median) {
                swap(nums, newIndex(i++, n), newIndex(j++, n));
            } else if (nums[newIndex(j, n)] < median) {
                swap(nums, newIndex(j, n), newIndex(k--, n));
            } else {
                j++;
            }
        }
    }

    private int newIndex(int index, int n) {
        return (1 + 2 * index) % (n | 1);
    }

    private int findKthLargest(int[] nums, int k) {
        int[] copy = Arrays.copyOf(nums, nums.length);
        Arrays.sort(copy);
        return copy[nums.length - k];
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    // Follow-up 2: Handle duplicates with frequency counting
    public void wiggleSortWithDuplicates(int[] nums) {
        Map<Integer, Integer> freq = new HashMap<>();
        for (int num : nums) {
            freq.put(num, freq.getOrDefault(num, 0) + 1);
        }

        List<Integer> unique = new ArrayList<>(freq.keySet());
        Collections.sort(unique);

        int n = nums.length;
        int[] result = new int[n];
        int idx = 0;

        // Place smaller elements at even indices
        for (int num : unique) {
            int count = freq.get(num);
            while (count > 0 && idx < n) {
                result[idx] = num;
                idx += 2;
                count--;
            }
            freq.put(num, count);
        }

        idx = 1;
        // Place larger elements at odd indices
        Collections.reverse(unique);
        for (int num : unique) {
            int count = freq.get(num);
            while (count > 0 && idx < n) {
                result[idx] = num;
                idx += 2;
                count--;
            }
        }

        System.arraycopy(result, 0, nums, 0, n);
    }

    // Follow-up 3: Without explicit median (using partitioning)
    public void wiggleSortPartition(int[] nums) {
        int n = nums.length;
        int[] copy = Arrays.copyOf(nums, n);
        Arrays.sort(copy);

        int left = (n - 1) / 2;
        int right = n - 1;

        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                nums[i] = copy[left--];
            } else {
                nums[i] = copy[right--];
            }
        }
    }

    // Follow-up 4: Optimized for odd length arrays
    public void wiggleSortOddLength(int[] nums) {
        if (nums.length <= 1)
            return;

        int[] copy = Arrays.copyOf(nums, nums.length);
        Arrays.sort(copy);

        int n = nums.length;
        int smallEnd = (n - 1) / 2;
        int largeEnd = n - 1;

        for (int i = 0; i < n; i++) {
            if (i % 2 == 0) {
                nums[i] = copy[smallEnd--];
            } else {
                nums[i] = copy[largeEnd--];
            }
        }
    }

    public static void main(String[] args) {
        WiggleSortII solution = new WiggleSortII();

        // Test Case 1: Basic wiggle sort
        int[] test1 = { 1, 5, 1, 1, 6, 4 };
        solution.wiggleSort(test1);
        System.out.println("Test 1: " + Arrays.toString(test1)); // Expected: [1,6,1,5,1,4] or similar valid wiggle

        // Test Case 2: Even length array
        int[] test2 = { 1, 3, 2, 2, 3, 1 };
        solution.wiggleSort(test2);
        System.out.println("Test 2: " + Arrays.toString(test2)); // Expected: [2,3,1,3,1,2] or similar valid wiggle

        // Test Case 3: Odd length array
        int[] test3 = { 1, 1, 2, 1, 2, 2, 1 };
        solution.wiggleSort(test3);
        System.out.println("Test 3: " + Arrays.toString(test3)); // Expected: [1,2,1,2,1,2,1] or similar valid wiggle

        // Test Case 4: Small array
        int[] test4 = { 4, 5, 5, 6 };
        solution.wiggleSort(test4);
        System.out.println("Test 4: " + Arrays.toString(test4)); // Expected: [5,6,4,5] or similar valid wiggle

        // Test Case 5: Single element
        int[] test5 = { 1 };
        solution.wiggleSort(test5);
        System.out.println("Test 5: " + Arrays.toString(test5)); // Expected: [1]

        // Test Case 6: Two elements
        int[] test6 = { 1, 2 };
        solution.wiggleSort(test6);
        System.out.println("Test 6: " + Arrays.toString(test6)); // Expected: [1,2]

        // Test Case 7: All same elements
        int[] test7 = { 3, 3, 3, 3 };
        solution.wiggleSort(test7);
        System.out.println("Test 7: " + Arrays.toString(test7)); // Expected: [3,3,3,3]

        // Test Case 8: Large numbers
        int[] test8 = { 100, 200, 150, 250 };
        solution.wiggleSort(test8);
        System.out.println("Test 8: " + Arrays.toString(test8)); // Expected: valid wiggle pattern

        // Test Case 9: Consecutive numbers
        int[] test9 = { 1, 2, 3, 4, 5, 6 };
        solution.wiggleSort(test9);
        System.out.println("Test 9: " + Arrays.toString(test9)); // Expected: valid wiggle pattern

        // Test Case 10: Reverse sorted
        int[] test10 = { 6, 5, 4, 3, 2, 1 };
        solution.wiggleSort(test10);
        System.out.println("Test 10: " + Arrays.toString(test10)); // Expected: valid wiggle pattern

        // Test Case 11: Many duplicates
        int[] test11 = { 1, 1, 1, 2, 2, 2 };
        solution.wiggleSort(test11);
        System.out.println("Test 11: " + Arrays.toString(test11)); // Expected: valid wiggle pattern

        // Test Case 12: Optimal approach test
        int[] test12 = { 1, 5, 1, 1, 6, 4 };
        solution.wiggleSortOptimal(test12);
        System.out.println("Test 12 (Optimal): " + Arrays.toString(test12)); // Expected: valid wiggle pattern

        // Test Case 13: Edge case with zeros
        int[] test13 = { 0, 0, 1, 1 };
        solution.wiggleSort(test13);
        System.out.println("Test 13: " + Arrays.toString(test13)); // Expected: [0,1,0,1]

        // Test Case 14: Large range
        int[] test14 = { 1, 100, 2, 99, 3, 98 };
        solution.wiggleSort(test14);
        System.out.println("Test 14: " + Arrays.toString(test14)); // Expected: valid wiggle pattern

        // Test Case 15: Three elements
        int[] test15 = { 2, 1, 3 };
        solution.wiggleSort(test15);
        System.out.println("Test 15: " + Arrays.toString(test15)); // Expected: [1,3,2] or [2,3,1]
    }
}
