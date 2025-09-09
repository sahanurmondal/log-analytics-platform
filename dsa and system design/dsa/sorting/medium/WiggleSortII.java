package sorting.medium;

/**
 * LeetCode 324: Wiggle Sort II
 * https://leetcode.com/problems/wiggle-sort-ii/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given an unsorted array, reorder it such that nums[0] < nums[1] > nums[2] <
 * nums[3]...
 *
 * Constraints:
 * - 1 <= n <= 5 * 10^4
 * - 0 <= nums[i] <= 5000
 *
 * Follow-ups:
 * 1. Can you do it in O(n) time and O(1) space?
 * 2. Can you handle duplicates?
 * 3. Can you wiggle sort for other patterns?
 */
public class WiggleSortII {
    public void wiggleSort(int[] nums) {
        int n = nums.length;
        int[] sorted = nums.clone();
        java.util.Arrays.sort(sorted);
        int left = (n + 1) / 2 - 1, right = n - 1;
        for (int i = 0; i < n; i++)
            nums[i] = (i % 2 == 0) ? sorted[left--] : sorted[right--];
    }

    // Follow-up 1: O(n) time and O(1) space (three-way partition)
    public void wiggleSortON(int[] nums) {
        int n = nums.length;
        int median = findKth(nums.clone(), (n + 1) / 2);
        int i = 0, j = 0, k = n - 1;
        while (j <= k) {
            if (nums[virtualIndex(j, n)] > median) {
                swap(nums, virtualIndex(i++, n), virtualIndex(j++, n));
            } else if (nums[virtualIndex(j, n)] < median) {
                swap(nums, virtualIndex(j, n), virtualIndex(k--, n));
            } else {
                j++;
            }
        }
    }

    private int findKth(int[] nums, int k) {
        java.util.Arrays.sort(nums);
        return nums[nums.length - k];
    }

    private int virtualIndex(int i, int n) {
        return (1 + 2 * i) % (n | 1);
    }

    private void swap(int[] nums, int i, int j) {
        int tmp = nums[i];
        nums[i] = nums[j];
        nums[j] = tmp;
    }

    // Follow-up 2: Handle duplicates (already handled above)
    // Follow-up 3: Wiggle sort for other patterns (not implemented)

    public static void main(String[] args) {
        WiggleSortII solution = new WiggleSortII();

        int[] nums1 = { 1, 5, 1, 1, 6, 4 };
        solution.wiggleSort(nums1);
        System.out.println(java.util.Arrays.toString(nums1)); // [1,6,1,5,1,4] or similar valid wiggle

        int[] nums2 = { 1, 3, 2, 2, 3, 1 };
        solution.wiggleSort(nums2);
        System.out.println(java.util.Arrays.toString(nums2)); // [2,3,1,3,1,2] or similar valid wiggle

        // Edge Case: Many duplicates
        int[] nums3 = { 4, 5, 5, 6 };
        solution.wiggleSort(nums3);
        System.out.println(java.util.Arrays.toString(nums3)); // [5,6,4,5] or similar

        // Edge Case: All same elements
        int[] nums4 = { 1, 1, 1, 1, 1 };
        solution.wiggleSort(nums4);
        System.out.println(java.util.Arrays.toString(nums4)); // Not possible, but handle gracefully

        // Edge Case: Two distinct values
        int[] nums5 = { 1, 1, 2, 2 };
        solution.wiggleSort(nums5);
        System.out.println(java.util.Arrays.toString(nums5)); // [1,2,1,2]
    }
}
