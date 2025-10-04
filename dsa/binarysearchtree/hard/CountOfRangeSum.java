package binarysearchtree.hard;

import java.util.*;

/**
 * LeetCode 327: Count of Range Sum
 * https://leetcode.com/problems/count-of-range-sum/
 *
 * Description: Given an integer array nums and two integers lower and upper,
 * return the number of range sums that lie in [lower, upper] inclusive.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -2^31 <= nums[i] <= 2^31 - 1
 * - -10^5 <= lower <= upper <= 10^5
 *
 * Follow-up:
 * - Can you solve it using segment tree or merge sort?
 * 
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class CountOfRangeSum {

    // Main optimized solution - Merge Sort
    public int countRangeSum(int[] nums, int lower, int upper) {
        long[] prefixSums = new long[nums.length + 1];
        for (int i = 0; i < nums.length; i++) {
            prefixSums[i + 1] = prefixSums[i] + nums[i];
        }

        return mergeSortAndCount(prefixSums, 0, prefixSums.length - 1, lower, upper);
    }

    private int mergeSortAndCount(long[] sums, int left, int right, int lower, int upper) {
        if (left >= right)
            return 0;

        int mid = left + (right - left) / 2;
        int count = mergeSortAndCount(sums, left, mid, lower, upper) +
                mergeSortAndCount(sums, mid + 1, right, lower, upper);

        // Count cross ranges
        int j = mid + 1, k = mid + 1;
        for (int i = left; i <= mid; i++) {
            while (j <= right && sums[j] - sums[i] < lower)
                j++;
            while (k <= right && sums[k] - sums[i] <= upper)
                k++;
            count += k - j;
        }

        // Merge
        merge(sums, left, mid, right);
        return count;
    }

    private void merge(long[] sums, int left, int mid, int right) {
        long[] temp = new long[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (sums[i] <= sums[j]) {
                temp[k++] = sums[i++];
            } else {
                temp[k++] = sums[j++];
            }
        }

        while (i <= mid)
            temp[k++] = sums[i++];
        while (j <= right)
            temp[k++] = sums[j++];

        System.arraycopy(temp, 0, sums, left, temp.length);
    }

    // Alternative solution - TreeMap (BST approach)
    public int countRangeSumTreeMap(int[] nums, int lower, int upper) {
        TreeMap<Long, Integer> map = new TreeMap<>();
        map.put(0L, 1);

        long prefixSum = 0;
        int count = 0;

        for (int num : nums) {
            prefixSum += num;

            // Count range sums in [lower, upper]
            Long from = map.ceilingKey(prefixSum - upper);
            Long to = map.floorKey(prefixSum - lower);

            if (from != null && to != null && from <= to) {
                for (Map.Entry<Long, Integer> entry : map.subMap(from, true, to, true).entrySet()) {
                    count += entry.getValue();
                }
            }

            map.put(prefixSum, map.getOrDefault(prefixSum, 0) + 1);
        }

        return count;
    }

    public static void main(String[] args) {
        CountOfRangeSum solution = new CountOfRangeSum();

        System.out.println(solution.countRangeSum(new int[] { -2, 5, -1 }, -2, 2)); // Expected: 3
        System.out.println(solution.countRangeSum(new int[] { 0 }, 0, 0)); // Expected: 1
        System.out.println(solution.countRangeSumTreeMap(new int[] { -2, 5, -1 }, -2, 2)); // Expected: 3
    }
}
