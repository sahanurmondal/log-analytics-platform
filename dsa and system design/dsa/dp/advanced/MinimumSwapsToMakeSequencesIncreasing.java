package dp.advanced;

import java.util.*;
/**
 * LeetCode 801: Minimum Swaps To Make Sequences Increasing
 * https://leetcode.com/problems/minimum-swaps-to-make-sequences-increasing/
 *
 * Description:
 * Given two sequences, return the minimum number of swaps to make both
 * sequences strictly increasing.
 *
 * Constraints:
 * - 1 <= A.length == B.length <= 10^5
 * - 0 <= A[i], B[i] <= 10^9
 *
 * Follow-up:
 * - Can you solve it in O(n) time?
 */
public class MinimumSwapsToMakeSequencesIncreasing {
    // Approach 1: DP with States - O(n) time, O(1) space
    public int minSwap(int[] A, int[] B) {
        int n = A.length;
        int swap = 1, noSwap = 0;

        for (int i = 1; i < n; i++) {
            int tempSwap = Integer.MAX_VALUE;
            int tempNoSwap = Integer.MAX_VALUE;

            // If current elements are already in increasing order
            if (A[i] > A[i - 1] && B[i] > B[i - 1]) {
                tempSwap = Math.min(tempSwap, swap + 1);
                tempNoSwap = Math.min(tempNoSwap, noSwap);
            }

            // If swapping makes them in increasing order
            if (A[i] > B[i - 1] && B[i] > A[i - 1]) {
                tempSwap = Math.min(tempSwap, noSwap + 1);
                tempNoSwap = Math.min(tempNoSwap, swap);
            }

            swap = tempSwap;
            noSwap = tempNoSwap;
        }

        return Math.min(swap, noSwap);
    }

    // Approach 2: DP Array - O(n) time, O(n) space
    public int minSwapDP(int[] A, int[] B) {
        int n = A.length;
        int[] swapDP = new int[n];
        int[] noSwapDP = new int[n];

        Arrays.fill(swapDP, Integer.MAX_VALUE);
        Arrays.fill(noSwapDP, Integer.MAX_VALUE);

        swapDP[0] = 1;
        noSwapDP[0] = 0;

        for (int i = 1; i < n; i++) {
            if (A[i] > A[i - 1] && B[i] > B[i - 1]) {
                swapDP[i] = Math.min(swapDP[i], swapDP[i - 1] + 1);
                noSwapDP[i] = Math.min(noSwapDP[i], noSwapDP[i - 1]);
            }

            if (A[i] > B[i - 1] && B[i] > A[i - 1]) {
                swapDP[i] = Math.min(swapDP[i], noSwapDP[i - 1] + 1);
                noSwapDP[i] = Math.min(noSwapDP[i], swapDP[i - 1]);
            }
        }

        return Math.min(swapDP[n - 1], noSwapDP[n - 1]);
    }

    public static void main(String[] args) {
        MinimumSwapsToMakeSequencesIncreasing solution = new MinimumSwapsToMakeSequencesIncreasing();
        // Edge Case 1: Normal case
        System.out.println(solution.minSwap(new int[] { 1, 3, 5, 4 }, new int[] { 1, 2, 3, 7 })); // 1
        System.out.println("DP Optimized: " + solution.minSwap(new int[] { 1, 3, 5, 4 }, new int[] { 1, 2, 3, 7 })); // 1
        System.out.println("DP Array: " + solution.minSwapDP(new int[] { 1, 3, 5, 4 }, new int[] { 1, 2, 3, 7 })); // 1
        // Edge Case 2: Already increasing
        System.out.println(solution.minSwap(new int[] { 1, 2, 3, 4 }, new int[] { 1, 2, 3, 4 })); // 0
        // Edge Case 3: All same
        System.out.println(solution.minSwap(new int[] { 2, 2, 2, 2 }, new int[] { 2, 2, 2, 2 })); // 0
        // Edge Case 4: Large input
        int[] largeA = new int[100000];
        int[] largeB = new int[100000];
        for (int i = 0; i < 100000; i++) {
            largeA[i] = i;
            largeB[i] = i;
        }
        System.out.println(solution.minSwap(largeA, largeB)); // 0
    }
}
