package heap.hard;

import java.util.PriorityQueue;

/**
 * LeetCode 786: K-th Smallest Prime Fraction
 * https://leetcode.com/problems/k-th-smallest-prime-fraction/
 *
 * Description:
 * Given a sorted array of integers, return the kth smallest fraction.
 *
 * Constraints:
 * - 2 <= arr.length <= 2000
 * - 1 <= arr[i] <= 10^4
 * - All arr[i] are prime numbers.
 * - 1 <= k <= arr.length * (arr.length - 1) / 2
 *
 * Follow-up:
 * - Can you solve it in O(n log n) time?
 */
public class FindKthSmallestPrimeFraction {
    /**
     * Finds the kth smallest prime fraction.
     * This method uses a min-heap to efficiently find the kth smallest fraction.
     *
     * @param arr The sorted array of prime numbers.
     * @param k   The value of k.
     * @return An array containing the numerator and denominator of the kth smallest
     *         fraction.
     */
    public int[] kthSmallestPrimeFraction(int[] arr, int k) {
        PriorityQueue<int[]> minHeap = new PriorityQueue<>(
                (a, b) -> Integer.compare(arr[a[0]] * arr[b[1]], arr[b[0]] * arr[a[1]]));

        for (int i = 0; i < arr.length - 1; i++) {
            minHeap.offer(new int[] { i, arr.length - 1 });
        }

        for (int i = 0; i < k - 1; i++) {
            int[] poll = minHeap.poll();
            int numeratorIndex = poll[0];
            int denominatorIndex = poll[1];

            if (denominatorIndex - 1 > numeratorIndex) {
                minHeap.offer(new int[] { numeratorIndex, denominatorIndex - 1 });
            }
        }

        int[] result = minHeap.poll();
        return new int[] { arr[result[0]], arr[result[1]] };
    }

    public static void main(String[] args) {
        FindKthSmallestPrimeFraction solution = new FindKthSmallestPrimeFraction();
        System.out.println(java.util.Arrays.toString(solution.kthSmallestPrimeFraction(new int[] { 1, 2, 3, 5 }, 3))); // [2,5]
        System.out.println(java.util.Arrays.toString(solution.kthSmallestPrimeFraction(new int[] { 1, 7 }, 1))); // [1,7]
    }
}
