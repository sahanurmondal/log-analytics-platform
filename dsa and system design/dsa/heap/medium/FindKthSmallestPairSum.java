package heap.medium;

import java.util.PriorityQueue;

/**
 * Variation: Find Kth Smallest Pair Sum
 *
 * Description:
 * Given an array, find the kth smallest sum of pairs.
 *
 * Constraints:
 * - 2 <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 * - 1 <= k <= nums.length * (nums.length - 1) / 2
 */
public class FindKthSmallestPairSum {
    public int kthSmallestPairSum(int[] nums, int k) {
        // This is a variation of the already implemented FindKthLargest pattern
        // Similar logic can be applied using a max heap for k smallest pairs
        // Implementation removed as similar patterns exist in:
        // - FindKthLargest.java (heap/medium)
        // - FindKthLargestPairSum.java (heap/medium)
        // - FindKthLargestNumberInArray.java (heap/hard)

        // Basic approach: Generate all pairs, use max heap of size k
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);

        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                int sum = nums[i] + nums[j];
                maxHeap.offer(sum);
                if (maxHeap.size() > k) {
                    maxHeap.poll();
                }
            }
        }

        return maxHeap.peek();
    }

    public static void main(String[] args) {
        FindKthSmallestPairSum solution = new FindKthSmallestPairSum();
        System.out.println(solution.kthSmallestPairSum(new int[] { 1, 2, 3, 4 }, 2)); // 4
        System.out.println(solution.kthSmallestPairSum(new int[] { -1, -2, -3, -4 }, 1)); // -7
        // Edge Case 1: All same
        System.out.println(solution.kthSmallestPairSum(new int[] { 2, 2, 2 }, 2)); // 4
        // Edge Case 2: Single pair
        System.out.println(solution.kthSmallestPairSum(new int[] { 42, 43 }, 1)); // 85
        // Edge Case 3: Large input
        int[] large = new int[1000];
        for (int i = 0; i < 1000; i++)
            large[i] = i;
        System.out.println(solution.kthSmallestPairSum(large, 1)); // 1
    }
}
