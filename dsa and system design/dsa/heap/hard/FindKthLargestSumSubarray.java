package heap.hard;

import java.util.PriorityQueue;

/**
 * Variation: Find Kth Largest Sum Subarray
 *
 * Description:
 * Given an array, return the kth largest sum of contiguous subarrays.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^9 <= nums[i] <= 10^9
 * - 1 <= k <= nums.length * (nums.length + 1) / 2
 */
public class FindKthLargestSumSubarray {
    /**
     * Finds the kth largest sum of a contiguous subarray.
     * This method uses a min-heap to keep track of the k largest sums found so far.
     *
     * @param nums The input array.
     * @param k    The value of k.
     * @return The kth largest sum.
     */
    public int kthLargestSum(int[] nums, int k) {
        PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);
        for (int i = 0; i < nums.length; i++) {
            int sum = 0;
            for (int j = i; j < nums.length; j++) {
                sum += nums[j];
                if (minHeap.size() < k) {
                    minHeap.offer(sum);
                } else if (sum > minHeap.peek()) {
                    minHeap.poll();
                    minHeap.offer(sum);
                }
            }
        }
        return minHeap.peek();
    }

    public static void main(String[] args) {
        FindKthLargestSumSubarray solution = new FindKthLargestSumSubarray();
        System.out.println(solution.kthLargestSum(new int[] { 1, 2, 3 }, 2)); // 5
        System.out.println(solution.kthLargestSum(new int[] { -1, -2, -3 }, 1)); // -1
        // Edge Case 1: All same
        System.out.println(solution.kthLargestSum(new int[] { 2, 2, 2 }, 2)); // 4
        // Edge Case 2: Single element
        System.out.println(solution.kthLargestSum(new int[] { 42 }, 1)); // 42
        // Edge Case 3: Large input
        int[] large = new int[10000];
        for (int i = 0; i < 10000; i++)
            large[i] = 1;
        System.out.println(solution.kthLargestSum(large, 1)); // 1
    }
}
