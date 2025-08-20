package arrays.hard;

/**
 * LeetCode 493: Reverse Pairs
 * https://leetcode.com/problems/reverse-pairs/
 *
 * Description:
 * Given an integer array nums, return the number of reverse pairs in the array.
 * A reverse pair is a pair (i, j) where 0 <= i < j < nums.length and nums[i] >
 * 2 * nums[j].
 *
 * Constraints:
 * - 1 <= nums.length <= 5 * 10^4
 * - -2^31 <= nums[i] <= 2^31 - 1
 *
 * Follow-up:
 * - Can you solve it using merge sort?
 * 
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 */
public class ReversePairs {

    public int reversePairs(int[] nums) {
        return mergeSort(nums, 0, nums.length - 1);
    }

    private int mergeSort(int[] nums, int left, int right) {
        if (left >= right)
            return 0;

        int mid = left + (right - left) / 2;
        int count = mergeSort(nums, left, mid) + mergeSort(nums, mid + 1, right);

        // Count reverse pairs
        int j = mid + 1;
        for (int i = left; i <= mid; i++) {
            while (j <= right && nums[i] > 2L * nums[j]) {
                j++;
            }
            count += j - (mid + 1);
        }

        // Merge the arrays
        merge(nums, left, mid, right);
        return count;
    }

    private void merge(int[] nums, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (nums[i] <= nums[j]) {
                temp[k++] = nums[i++];
            } else {
                temp[k++] = nums[j++];
            }
        }

        while (i <= mid)
            temp[k++] = nums[i++];
        while (j <= right)
            temp[k++] = nums[j++];

        System.arraycopy(temp, 0, nums, left, temp.length);
    }

    // Alternative solution - Brute force
    public int reversePairsBruteForce(int[] nums) {
        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[i] > 2L * nums[j]) {
                    count++;
                }
            }
        }
        return count;
    }

    public static void main(String[] args) {
        ReversePairs solution = new ReversePairs();

        // Test Case 1: Normal case
        System.out.println(solution.reversePairs(new int[] { 1, 3, 2, 3, 1 })); // Expected: 2

        // Test Case 2: Edge case - no reverse pairs
        System.out.println(solution.reversePairs(new int[] { 2, 4, 3, 5, 1 })); // Expected: 3

        // Test Case 3: Corner case - single element
        System.out.println(solution.reversePairs(new int[] { 1 })); // Expected: 0

        // Test Case 4: All decreasing
        System.out.println(solution.reversePairs(new int[] { 5, 4, 3, 2, 1 })); // Expected: 4

        // Test Case 5: All increasing
        System.out.println(solution.reversePairs(new int[] { 1, 2, 3, 4, 5 })); // Expected: 0

        // Test Case 6: Negative numbers
        System.out.println(solution.reversePairs(new int[] { -5, -1, -3 })); // Expected: 1

        // Test Case 7: Mixed positive/negative
        System.out.println(solution.reversePairs(new int[] { 1, -1, 0 })); // Expected: 1

        // Test Case 8: Large numbers
        System.out.println(solution.reversePairs(new int[] { 2147483647, -2147483648 })); // Expected: 1

        // Test Case 9: Duplicates
        System.out.println(solution.reversePairs(new int[] { 1, 1, 1 })); // Expected: 0

        // Test Case 10: Complex pattern
        System.out.println(solution.reversePairs(new int[] { 233, 2000000001, 234, 2000000006, 235, 2000000003, 236,
                2000000007, 237, 2000000002, 2000000005, 233, 233 })); // Expected: 40
    }
}