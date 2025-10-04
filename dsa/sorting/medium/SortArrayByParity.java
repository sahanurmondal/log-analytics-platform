package sorting.medium;

/**
 * LeetCode 905: Sort Array By Parity
 * https://leetcode.com/problems/sort-array-by-parity/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given an array, sort it so that all even integers come before all odd
 * integers.
 *
 * Constraints:
 * - 1 <= A.length <= 5000
 * - 0 <= A[i] <= 5000
 *
 * Follow-ups:
 * 1. Can you sort by parity and value?
 * 2. Can you do it in-place?
 * 3. Can you handle negative numbers?
 */
public class SortArrayByParity {
    public int[] sortArrayByParity(int[] A) {
        int i = 0, j = A.length - 1;
        while (i < j) {
            if (A[i] % 2 > A[j] % 2) {
                int tmp = A[i];
                A[i] = A[j];
                A[j] = tmp;
            }
            if (A[i] % 2 == 0)
                i++;
            if (A[j] % 2 == 1)
                j--;
        }
        return A;
    }

    // Follow-up 1: Sort by parity and value
    public int[] sortArrayByParityAndValue(int[] A) {
        Integer[] arr = new Integer[A.length];
        for (int i = 0; i < A.length; i++) {
            arr[i] = A[i];
        }
        java.util.Arrays.sort(arr, (a, b) -> {
            int pa = a % 2, pb = b % 2;
            if (pa != pb)
                return pa - pb;
            return a - b;
        });
        for (int i = 0; i < A.length; i++) {
            A[i] = arr[i];
        }
        return A;
    }

    // Follow-up 2: In-place (already handled above)
    // Follow-up 3: Handle negative numbers (already handled above)

    public static void main(String[] args) {
        SortArrayByParity solution = new SortArrayByParity();
        System.out.println(java.util.Arrays.toString(solution.sortArrayByParity(new int[] { 3, 1, 2, 4 }))); // [2,4,3,1]
        System.out.println(java.util.Arrays.toString(solution.sortArrayByParityAndValue(new int[] { 3, 1, 2, 4 }))); // [2,4,1,3]
    }
}
