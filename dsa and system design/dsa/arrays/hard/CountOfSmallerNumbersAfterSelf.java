package arrays.hard;

import java.util.*;

/**
 * LeetCode 315: Count of Smaller Numbers After Self
 * https://leetcode.com/problems/count-of-smaller-numbers-after-self/
 *
 * Description:
 * Given an integer array nums, return an integer array counts where counts[i]
 * is the number of smaller elements to the right of nums[i].
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it using merge sort?
 * 
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Use merge sort with index tracking
 * 2. During merge, count smaller elements from right array
 * 3. Update count array based on merge operations
 */
public class CountOfSmallerNumbersAfterSelf {
    private int[] counts;

    public List<Integer> countSmaller(int[] nums) {
        int n = nums.length;
        counts = new int[n];
        int[][] valueIndex = new int[n][2];

        for (int i = 0; i < n; i++) {
            valueIndex[i] = new int[] { nums[i], i };
        }

        mergeSort(valueIndex, 0, n - 1);

        List<Integer> result = new ArrayList<>();
        for (int count : counts) {
            result.add(count);
        }
        return result;
    }

    private void mergeSort(int[][] arr, int left, int right) {
        if (left >= right)
            return;

        int mid = left + (right - left) / 2;
        mergeSort(arr, left, mid);
        mergeSort(arr, mid + 1, right);
        merge(arr, left, mid, right);
    }

    private void merge(int[][] arr, int left, int mid, int right) {
        int[][] temp = new int[right - left + 1][2];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (arr[i][0] <= arr[j][0]) {
                counts[arr[i][1]] += j - mid - 1;
                temp[k++] = arr[i++];
            } else {
                temp[k++] = arr[j++];
            }
        }

        while (i <= mid) {
            counts[arr[i][1]] += j - mid - 1;
            temp[k++] = arr[i++];
        }

        while (j <= right) {
            temp[k++] = arr[j++];
        }

        System.arraycopy(temp, 0, arr, left, temp.length);
    }

    public static void main(String[] args) {
        CountOfSmallerNumbersAfterSelf solution = new CountOfSmallerNumbersAfterSelf();

        // Test Case 1: Normal case
        System.out.println(solution.countSmaller(new int[] { 5, 2, 6, 1 })); // Expected: [2,1,1,0]

        // Test Case 2: Edge case - increasing order
        System.out.println(solution.countSmaller(new int[] { -1 })); // Expected: [0]

        // Test Case 3: Corner case - all same
        System.out.println(solution.countSmaller(new int[] { -1, -1 })); // Expected: [0,0]

        // Test Case 4: Large input - decreasing order
        System.out.println(solution.countSmaller(new int[] { 5, 4, 3, 2, 1 })); // Expected: [4,3,2,1,0]

        // Test Case 5: Minimum input - single element
        System.out.println(solution.countSmaller(new int[] { 1 })); // Expected: [0]

        // Test Case 6: Special case - mixed order
        System.out.println(solution.countSmaller(new int[] { 1, 3, 2 })); // Expected: [0,1,0]

        // Test Case 7: Boundary case - negative numbers
        System.out.println(solution.countSmaller(new int[] { -2, -1, 0, 1 })); // Expected: [0,0,0,0]

        // Test Case 8: All negative
        System.out.println(solution.countSmaller(new int[] { -5, -2, -3 })); // Expected: [1,0,0]

        // Test Case 9: Duplicates
        System.out.println(solution.countSmaller(new int[] { 2, 2, 2 })); // Expected: [0,0,0]

        // Test Case 10: Large range
        System.out.println(solution.countSmaller(new int[] { 10000, -10000, 0 })); // Expected: [2,0,0]
    }
}
