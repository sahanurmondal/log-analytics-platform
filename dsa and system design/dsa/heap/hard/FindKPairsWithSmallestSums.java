package heap.hard;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * LeetCode 373: Find K Pairs with Smallest Sums
 * https://leetcode.com/problems/find-k-pairs-with-smallest-sums/
 *
 * Description:
 * Given two sorted arrays nums1 and nums2, return k pairs with the smallest
 * sums.
 *
 * Constraints:
 * - 1 <= nums1.length, nums2.length <= 10^5
 * - 1 <= k <= 10^4
 * - -10^9 <= nums1[i], nums2[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(k log k) time?
 */
public class FindKPairsWithSmallestSums {
    /**
     * Finds the k pairs with the smallest sums.
     * This method uses a min-heap to efficiently find the k smallest pairs.
     *
     * @param nums1 The first sorted array.
     * @param nums2 The second sorted array.
     * @param k     The number of pairs to return.
     * @return A list of the k pairs with the smallest sums.
     */
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums1.length == 0 || nums2.length == 0 || k == 0) {
            return result;
        }

        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> (a[0] + a[1]) - (b[0] + b[1]));

        for (int i = 0; i < nums1.length && i < k; i++) {
            minHeap.offer(new int[] { nums1[i], nums2[0], 0 });
        }

        while (k-- > 0 && !minHeap.isEmpty()) {
            int[] current = minHeap.poll();
            result.add(List.of(current[0], current[1]));
            int nums2Idx = current[2];
            if (nums2Idx < nums2.length - 1) {
                minHeap.offer(new int[] { current[0], nums2[nums2Idx + 1], nums2Idx + 1 });
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindKPairsWithSmallestSums solution = new FindKPairsWithSmallestSums();
        // Edge Case 1: Normal case
        System.out.println(solution.kSmallestPairs(new int[] { 1, 7, 11 }, new int[] { 2, 4, 6 }, 3)); // [[1,2],[1,4],[1,6]]
        // Edge Case 2: k > total pairs
        System.out.println(solution.kSmallestPairs(new int[] { 1, 1, 2 }, new int[] { 1, 2, 3 }, 10)); // All pairs
        // Edge Case 3: Empty arrays
        System.out.println(solution.kSmallestPairs(new int[] {}, new int[] { 1, 2, 3 }, 3)); // []
        // Edge Case 4: nums1 or nums2 with negative numbers
        System.out.println(solution.kSmallestPairs(new int[] { -1, 0 }, new int[] { -2, 2 }, 2)); // [[-1,-2],[0,-2]]
        // Edge Case 5: Large input
        int[] large1 = new int[1000];
        int[] large2 = new int[1000];
        for (int i = 0; i < 1000; i++) {
            large1[i] = i;
            large2[i] = i;
        }
        System.out.println(solution.kSmallestPairs(large1, large2, 5)); // Should be small pairs
    }
}
