package heap.medium;

import java.util.List;

/**
 * Variation: Find K Pairs with Smallest Sums (Medium Variant)
 *
 * Description:
 * Given two sorted arrays nums1 and nums2, return k pairs with the smallest
 * sums.
 *
 * Constraints:
 * - 1 <= nums1.length, nums2.length <= 10^4
 * - 1 <= k <= 10^3
 * - -10^4 <= nums1[i], nums2[i] <= 10^4
 */
import java.util.ArrayList;
import java.util.Arrays;
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
 * - 1 <= nums1.length, nums2.length <= 10^4
 * - 1 <= k <= 10^3
 * - -10^4 <= nums1[i], nums2[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it using a min-heap efficiently?
 * - Can you optimize for very large k values?
 */
public class FindKPairsWithSmallestSumsMedium {
    public List<List<Integer>> kSmallestPairs(int[] nums1, int[] nums2, int k) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums1.length == 0 || nums2.length == 0 || k == 0)
            return result;

        PriorityQueue<int[]> pq = new PriorityQueue<>(
                (a, b) -> (nums1[a[0]] + nums2[a[1]]) - (nums1[b[0]] + nums2[b[1]]));

        // Initialize with pairs from nums1[0] and all elements in nums2
        for (int j = 0; j < Math.min(nums2.length, k); j++) {
            pq.offer(new int[] { 0, j });
        }

        while (k > 0 && !pq.isEmpty()) {
            int[] curr = pq.poll();
            int i = curr[0], j = curr[1];
            result.add(Arrays.asList(nums1[i], nums2[j]));
            k--;

            // Add next pair with same j but i+1
            if (i + 1 < nums1.length) {
                pq.offer(new int[] { i + 1, j });
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FindKPairsWithSmallestSumsMedium solution = new FindKPairsWithSmallestSumsMedium();
        System.out.println(solution.kSmallestPairs(new int[] { 1, 7, 11 }, new int[] { 2, 4, 6 }, 3)); // [[1,2],[1,4],[1,6]]
        System.out.println(solution.kSmallestPairs(new int[] { 1, 1, 2 }, new int[] { 1, 2, 3 }, 10)); // All pairs
        System.out.println(solution.kSmallestPairs(new int[] {}, new int[] { 1, 2, 3 }, 3)); // []
        // Edge Case 1: k == 1
        System.out.println(solution.kSmallestPairs(new int[] { 1 }, new int[] { 2 }, 1)); // [[1,2]]
        // Edge Case 2: nums1 or nums2 with negative numbers
        System.out.println(solution.kSmallestPairs(new int[] { -1, 0 }, new int[] { -2, 2 }, 2)); // [[-1,-2],[0,-2]]
        // Edge Case 3: Large input
        int[] large1 = new int[1000];
        int[] large2 = new int[1000];
        for (int i = 0; i < 1000; i++) {
            large1[i] = i;
            large2[i] = i;
        }
        System.out.println(solution.kSmallestPairs(large1, large2, 5)); // Should be small pairs
    }
}
