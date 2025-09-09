package arrays.medium;

/**
 * LeetCode 31: Next Permutation
 * https://leetcode.com/problems/next-permutation/
 */
public class NextPermutation {
    public void nextPermutation(int[] nums) {
        int i = nums.length - 2;

        // Find first decreasing element from right
        while (i >= 0 && nums[i] >= nums[i + 1]) {
            i--;
        }

        if (i >= 0) {
            // Find element just larger than nums[i]
            int j = nums.length - 1;
            while (nums[j] <= nums[i]) {
                j--;
            }
            swap(nums, i, j);
        }

        // Reverse the suffix
        reverse(nums, i + 1);
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    private void reverse(int[] nums, int start) {
        int end = nums.length - 1;
        while (start < end) {
            swap(nums, start, end);
            start++;
            end--;
        }
    }

    public static void main(String[] args) {
        NextPermutation solution = new NextPermutation();
        int[] nums1 = { 1, 2, 3 };
        solution.nextPermutation(nums1);
        // Expected: [1,3,2]

        int[] nums2 = { 3, 2, 1 };
        solution.nextPermutation(nums2);
        // Expected: [1,2,3]

        int[] nums3 = { 1, 1, 5 };
        solution.nextPermutation(nums3);
        // Expected: [1,5,1]
    }
}