package heap.medium;

import java.util.PriorityQueue;

/**
 * Variation: Find Kth Smallest Sum Subarray (Medium Variant)
 *
 * Description:
 * Given an array, return the kth smallest sum of contiguous subarrays.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 * - 1 <= k <= nums.length * (nums.length + 1) / 2
 */
public class FindKthSmallestSumSubarray {
    public int kthSmallestSum(int[] nums, int k) {
        // Similar pattern to FindKthLargestSumSubarray.java (hard package)
        // Using max heap to track k smallest subarray sums
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                maxHeap.offer(sum);
                if (maxHeap.size() > k) {
                    maxHeap.poll();
                }
            }
        }

        return maxHeap.peek();
    }

    public static void main(String[] args) {
        FindKthSmallestSumSubarray solution = new FindKthSmallestSumSubarray();
        System.out.println(solution.kthSmallestSum(new int[] { 1, 2, 3 }, 2)); // 2
        System.out.println(solution.kthSmallestSum(new int[] { -1, -2, -3 }, 1)); // -6
        // Edge Case 1: All same
        System.out.println(solution.kthSmallestSum(new int[] { 2, 2, 2 }, 2)); // 2
        // Edge Case 2: Single element
        System.out.println(solution.kthSmallestSum(new int[] { 42 }, 1)); // 42
        // Edge Case 3: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = 1;
        System.out.println(solution.kthSmallestSum(large, 1)); // 1
    }
}
