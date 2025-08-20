package heap.hard;

import java.util.Collections;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * LeetCode 215 (variation): Kth Smallest Element in an Array
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 20+ interviews)
 *
 * Description:
 * Given an integer array `nums` and an integer `k`, return the `k`th smallest
 * element in the array.
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve this in O(n) average time? (Quickselect)
 * 2. Compare the heap and Quickselect approaches.
 * 3. What if the data is a stream?
 */
public class FindKthSmallestNumberInArray {

    // Approach 1: Max-Heap - O(n log k) time, O(k) space
    public int findKthSmallest(int[] nums, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        for (int num : nums) {
            maxHeap.offer(num);
            if (maxHeap.size() > k) {
                maxHeap.poll();
            }
        }
        return maxHeap.peek();
    }

    // Approach 2: Quickselect - O(n) average time, O(n^2) worst case, O(1) space
    public int findKthSmallestQuickselect(int[] nums, int k) {
        int left = 0, right = nums.length - 1;
        // The index we are looking for is (k - 1) in a 0-indexed array sorted
        // ascendingly.
        int targetIndex = k - 1;

        while (left <= right) {
            int pivotIndex = partition(nums, left, right);
            if (pivotIndex == targetIndex) {
                return nums[pivotIndex];
            } else if (pivotIndex < targetIndex) {
                left = pivotIndex + 1;
            } else {
                right = pivotIndex - 1;
            }
        }
        return -1; // Should not happen
    }

    private int partition(int[] nums, int left, int right) {
        int pivotIndex = new Random().nextInt(right - left + 1) + left;
        int pivotValue = nums[pivotIndex];
        swap(nums, pivotIndex, right);

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (nums[i] < pivotValue) {
                swap(nums, i, storeIndex);
                storeIndex++;
            }
        }
        swap(nums, storeIndex, right);
        return storeIndex;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        FindKthSmallestNumberInArray solution = new FindKthSmallestNumberInArray();

        // Test case 1
        int[] nums1 = { 3, 2, 1, 5, 6, 4 };
        int k1 = 2;
        System.out.println("Kth smallest 1 (Heap): " + solution.findKthSmallest(nums1, k1)); // 2
        System.out.println("Kth smallest 1 (Quickselect): " + solution.findKthSmallestQuickselect(nums1, k1)); // 2

        // Test case 2
        int[] nums2 = { 3, 2, 3, 1, 2, 4, 5, 5, 6 };
        int k2 = 4;
        System.out.println("Kth smallest 2: " + solution.findKthSmallest(nums2, k2)); // 3
    }
}
