package twopointers.medium;

/**
 * LeetCode 977: Squares of a Sorted Array
 * https://leetcode.com/problems/squares-of-a-sorted-array/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a sorted array, return an array of the squares of each number sorted in
 * non-decreasing order.
 *
 * Constraints:
 * - 1 <= A.length <= 10^4
 * - -10^4 <= A[i] <= 10^4
 *
 * Follow-ups:
 * 1. Can you do it in-place?
 * 2. Can you handle negative numbers efficiently?
 * 3. Can you generalize to k-th power?
 */
public class SquaresOfSortedArray {
    public int[] sortedSquares(int[] A) {
        int n = A.length;
        int[] res = new int[n];
        int left = 0, right = n - 1, idx = n - 1;
        while (left <= right) {
            int l = A[left] * A[left], r = A[right] * A[right];
            if (l > r) {
                res[idx--] = l;
                left++;
            } else {
                res[idx--] = r;
                right--;
            }
        }
        return res;
    }

    // Follow-up 1: In-place (not possible due to negative numbers)
    // Follow-up 2: Efficient for negatives (already handled above)
    // Follow-up 3: Generalize to k-th power
    public int[] sortedKthPower(int[] A, int k) {
        int n = A.length;
        int[] res = new int[n];
        int left = 0, right = n - 1, idx = n - 1;
        while (left <= right) {
            int l = (int) Math.pow(A[left], k), r = (int) Math.pow(A[right], k);
            if (l > r) {
                res[idx--] = l;
                left++;
            } else {
                res[idx--] = r;
                right--;
            }
        }
        return res;
    }

    public static void main(String[] args) {
        SquaresOfSortedArray solution = new SquaresOfSortedArray();
        // Basic case
        int[] A1 = { -4, -1, 0, 3, 10 };
        System.out.println("Basic: " + java.util.Arrays.toString(solution.sortedSquares(A1))); // [0,1,9,16,100]

        // Edge: All negatives
        int[] A2 = { -7, -3, -1 };
        System.out.println("All negatives: " + java.util.Arrays.toString(solution.sortedSquares(A2))); // [1,9,49]

        // Edge: All positives
        int[] A3 = { 1, 2, 3, 4 };
        System.out.println("All positives: " + java.util.Arrays.toString(solution.sortedSquares(A3))); // [1,4,9,16]

        // Edge: Single element
        int[] A4 = { 5 };
        System.out.println("Single element: " + java.util.Arrays.toString(solution.sortedSquares(A4))); // [25]

        // Follow-up: k-th power
        int[] A5 = { -2, -1, 0, 1, 2 };
        System.out.println("K=3 power: " + java.util.Arrays.toString(solution.sortedKthPower(A5, 3))); // [0,1,1,8,8]
    }
}
