package sorting.hard;

/**
 * LeetCode 321: Create Maximum Number
 * https://leetcode.com/problems/create-maximum-number/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given two arrays of length m and n, create the maximum number of length k.
 *
 * Constraints:
 * - 1 <= m, n <= 500
 * - 0 <= k <= m + n
 * - 0 <= nums1[i], nums2[i] <= 9
 *
 * Follow-ups:
 * 1. Can you generate all possible maximum numbers?
 * 2. Can you optimize for large k?
 * 3. Can you handle more than two arrays?
 */
public class CreateMaximumNumber {
    public int[] maxNumber(int[] nums1, int[] nums2, int k) {
        int[] res = new int[k];
        for (int i = Math.max(0, k - nums2.length); i <= Math.min(k, nums1.length); i++) {
            int[] candidate = merge(maxArray(nums1, i), maxArray(nums2, k - i));
            if (greater(candidate, 0, res, 0))
                res = candidate;
        }
        return res;
    }

    private int[] maxArray(int[] nums, int k) {
        int[] res = new int[k];
        int j = 0;
        for (int i = 0; i < nums.length; i++) {
            while (j > 0 && nums.length - i + j > k && res[j - 1] < nums[i])
                j--;
            if (j < k)
                res[j++] = nums[i];
        }
        return res;
    }

    private int[] merge(int[] nums1, int[] nums2) {
        int[] res = new int[nums1.length + nums2.length];
        int i = 0, j = 0, r = 0;
        while (i < nums1.length || j < nums2.length)
            res[r++] = greater(nums1, i, nums2, j) ? nums1[i++] : nums2[j++];
        return res;
    }

    private boolean greater(int[] nums1, int i, int[] nums2, int j) {
        while (i < nums1.length && j < nums2.length && nums1[i] == nums2[j]) {
            i++;
            j++;
        }
        return j == nums2.length || (i < nums1.length && nums1[i] > nums2[j]);
    }

    // Follow-up 1: Generate all possible maximum numbers (not implemented)
    // Follow-up 2: Optimize for large k (already handled above)
    // Follow-up 3: Handle more than two arrays (not implemented)

    public static void main(String[] args) {
        CreateMaximumNumber solution = new CreateMaximumNumber();

        System.out.println(java.util.Arrays
                .toString(solution.maxNumber(new int[] { 3, 4, 6, 5 }, new int[] { 9, 1, 2, 5, 8, 3 }, 5)));
        // [9,8,6,5,3]

        System.out.println(java.util.Arrays.toString(solution.maxNumber(new int[] { 6, 7 }, new int[] { 6, 0, 4 }, 5)));
        // [6,7,6,0,4]

        System.out.println(java.util.Arrays.toString(solution.maxNumber(new int[] { 3, 9 }, new int[] { 8, 9 }, 3)));
        // [9,8,9]

        // Edge Case: k equals total length
        System.out.println(java.util.Arrays.toString(solution.maxNumber(new int[] { 1, 2 }, new int[] { 3, 4 }, 4)));
        // [3,4,1,2]

        // Edge Case: One array empty
        System.out.println(java.util.Arrays.toString(solution.maxNumber(new int[] {}, new int[] { 1, 2, 3 }, 2)));
        // [2,3]

        // Edge Case: k = 1
        System.out.println(
                java.util.Arrays.toString(solution.maxNumber(new int[] { 2, 5, 6 }, new int[] { 4, 2, 3 }, 1)));
        // [6]
    }
}
