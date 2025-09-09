package heap.hard;

import java.util.PriorityQueue;
import java.util.Random;

/**
 * LeetCode 215: Kth Largest Element in an Array
 * https://leetcode.com/problems/kth-largest-element-in-an-array/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Very High (Asked in 20+ interviews)
 *
 * Description:
 * Given an integer array `nums` and an integer `k`, return the `k`th largest
 * element in the array.
 * Note that it is the `k`th largest element in the sorted order, not the `k`th
 * distinct element.
 *
 * Constraints:
 * - 1 <= k <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve this in O(n) average time? (Quickselect)
 * 2. Compare the heap and Quickselect approaches in terms of time/space
 * complexity and performance in practice.
 * 3. What if the data is a stream?
 */
public class FindKthLargestNumberInArray {

    // Approach 1: Min-Heap - O(n log k) time, O(k) space
    public int findKthLargest(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();
        for (int num : nums) {
            minHeap.offer(num);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }
        return minHeap.peek();
    }

    // Approach 2: Quickselect - O(n) average time, O(n^2) worst case, O(1) space
    public int findKthLargestQuickselect(int[] nums, int k) {
        int left = 0, right = nums.length - 1;
        // The index we are looking for is (n - k) in a 0-indexed array sorted
        // ascendingly.
        int targetIndex = nums.length - k;

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
        // Randomized pivot to avoid worst-case O(n^2)
        int pivotIndex = new Random().nextInt(right - left + 1) + left;
        int pivotValue = nums[pivotIndex];
        swap(nums, pivotIndex, right); // Move pivot to the end

        int storeIndex = left;
        for (int i = left; i < right; i++) {
            if (nums[i] < pivotValue) {
                swap(nums, i, storeIndex);
                storeIndex++;
            }
        }
        swap(nums, storeIndex, right); // Move pivot to its final sorted place
        return storeIndex;
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        FindKthLargestNumberInArray solution = new FindKthLargestNumberInArray();

        // Test case 1
        int[] nums1 = { 3, 2, 1, 5, 6, 4 };
        int k1 = 2;
        System.out.println("Kth largest 1 (Heap): " + solution.findKthLargest(nums1, k1)); // 5
        System.out.println("Kth largest 1 (Quickselect): " + solution.findKthLargestQuickselect(nums1, k1)); // 5

        // Test case 2
        int[] nums2 = { 3, 2, 3, 1, 2, 4, 5, 5, 6 };
        int k2 = 4;
        System.out.println("Kth largest 2: " + solution.findKthLargest(nums2, k2)); // 4
    }
}
