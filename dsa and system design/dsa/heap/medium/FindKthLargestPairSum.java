package heap.medium;

import java.util.PriorityQueue;

/**
 * Variation: Find Kth Largest Pair Sum
 *
 * Description:
 * Given an array, find the kth largest sum of pairs.
 *
 * Constraints:
 * - 2 <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 * - 1 <= k <= nums.length * (nums.length - 1) / 2
 */
public class FindKthLargestPairSum {
    public int kthLargestPairSum(int[] nums, int k) {
        // This follows the same pattern as FindKthLargest.java (already implemented)
        // Using min heap to track k largest pair sums
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int sum = nums[i] + nums[j];
                minHeap.offer(sum);
                if (minHeap.size() > k) {
                    minHeap.poll();
                }
            }
        }

        return minHeap.peek();
    }

    public static void main(String[] args) {
        FindKthLargestPairSum solution = new FindKthLargestPairSum();
        System.out.println(solution.kthLargestPairSum(new int[] { 1, 2, 3, 4 }, 2)); // 6
        System.out.println(solution.kthLargestPairSum(new int[] { -1, -2, -3, -4 }, 1)); // -1
        // Edge Case 1: All same
        System.out.println(solution.kthLargestPairSum(new int[] { 2, 2, 2 }, 2)); // 4
        // Edge Case 2: Single pair
        System.out.println(solution.kthLargestPairSum(new int[] { 42, 43 }, 1)); // 85
        // Edge Case 3: Large input
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++)
            large[i] = i;
        System.out.println(solution.kthLargestPairSum(large, 1)); // 1998
    }
}
