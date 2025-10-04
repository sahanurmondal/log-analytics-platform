package sorting.medium;

/**
 * LeetCode 75: Sort Colors
 * https://leetcode.com/problems/sort-colors/
 *
 * Description:
 * Given an array nums with n objects colored red, white, or blue, sort them
 * in-place so that objects of the same color are adjacent.
 * We will use the integers 0, 1, and 2 to represent the color red, white, and
 * blue, respectively.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 300
 * - nums[i] is either 0, 1, or 2
 *
 * ASCII Art:
 * Input: [2,0,2,1,1,0]
 * Output: [0,0,1,1,2,2]
 * 
 * Dutch National Flag Algorithm:
 * low=0, mid=0, high=5
 * [2,0,2,1,1,0]
 * ↑ ↑ ↑
 * low mid high
 *
 * Follow-up:
 * - Can you solve it in one pass using constant space (Dutch National Flag)?
 * - Can you extend to k colors?
 * - Can you solve it without using the built-in sort function?
 */
/**
 * LeetCode 75: Sort Colors
 * https://leetcode.com/problems/sort-colors/
 *
 * Description:
 * Given an array nums with n objects colored red, white, or blue, sort them
 * in-place so that objects of the same color are adjacent.
 * We will use the integers 0, 1, and 2 to represent the color red, white, and
 * blue, respectively.
 *
 * Constraints:
 * - n == nums.length
 * - 1 <= n <= 300
 * - nums[i] is either 0, 1, or 2
 *
 * ASCII Art:
 * Input: [2,0,2,1,1,0]
 * Output: [0,0,1,1,2,2]
 * 
 * Dutch National Flag Algorithm:
 * low=0, mid=0, high=5
 * [2,0,2,1,1,0]
 * ↑ ↑ ↑
 * low mid high
 *
 * Follow-up:
 * - Can you solve it in one pass using constant space (Dutch National Flag)?
 * - Can you extend to k colors?
 * - Can you solve it without using the built-in sort function?
 */
public class SortColors {
    public void sortColors(int[] nums) {
        int low = 0, mid = 0, high = nums.length - 1;

        while (mid <= high) {
            if (nums[mid] == 0) {
                swap(nums, low, mid);
                low++;
                mid++;
            } else if (nums[mid] == 1) {
                mid++;
            } else { // nums[mid] == 2
                swap(nums, mid, high);
                high--;
                // Don't increment mid as we need to check swapped element
            }
        }
    }

    private void swap(int[] nums, int i, int j) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }

    public static void main(String[] args) {
        SortColors solution = new SortColors();

        // Test case 1
        int[] nums1 = { 2, 0, 2, 1, 1, 0 };
        solution.sortColors(nums1);
        System.out.println(java.util.Arrays.toString(nums1)); // [0,0,1,1,2,2]

        // Test case 2
        int[] nums2 = { 2, 0, 1 };
        solution.sortColors(nums2);
        System.out.println(java.util.Arrays.toString(nums2)); // [0,1,2]

        // Edge Case: All same color
        int[] nums3 = { 1, 1, 1, 1 };
        solution.sortColors(nums3);
        System.out.println(java.util.Arrays.toString(nums3)); // [1,1,1,1]

        // Edge Case: Already sorted
        int[] nums4 = { 0, 1, 2 };
        solution.sortColors(nums4);
        System.out.println(java.util.Arrays.toString(nums4)); // [0,1,2]

        // Edge Case: Reverse sorted
        int[] nums5 = { 2, 1, 0 };
        solution.sortColors(nums5);
        System.out.println(java.util.Arrays.toString(nums5)); // [0,1,2]

        // Edge Case: Single element
        int[] nums6 = { 2 };
        solution.sortColors(nums6);
        System.out.println(java.util.Arrays.toString(nums6)); // [2]
    }
}
