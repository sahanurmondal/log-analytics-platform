package searching.medium;

/**
 * LeetCode 540: Single Element in a Sorted Array
 * https://leetcode.com/problems/single-element-in-a-sorted-array/
 *
 * Description:
 * You are given a sorted array consisting of only integers where every element
 * appears exactly twice, except for one element which appears exactly once.
 *
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - 0 <= nums[i] <= 10^5
 *
 * Follow-ups:
 * 1. Can you find the index of the single element?
 * 2. Can you handle unsorted arrays?
 * 3. Can you find all single elements if there are multiple?
 */
public class SingleElementInSortedArray {
    public int singleNonDuplicate(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if ((mid % 2 == 0 && nums[mid] == nums[mid + 1]) ||
                (mid % 2 == 1 && nums[mid] == nums[mid - 1]))
                left = mid + 1;
            else
                right = mid;
        }
        return nums[left];
    }

    // Follow-up 1: Find index of single element
    public int singleNonDuplicateIndex(int[] nums) {
        int left = 0, right = nums.length - 1;
        while (left < right) {
            int mid = left + (right - left) / 2;
            if ((mid % 2 == 0 && nums[mid] == nums[mid + 1]) ||
                (mid % 2 == 1 && nums[mid] == nums[mid - 1]))
                left = mid + 1;
            else
                right = mid;
        }
        return left;
    }

    // Follow-up 2: Unsorted array (use XOR)
    public int singleNonDuplicateUnsorted(int[] nums) {
        int res = 0;
        for (int num : nums) res ^= num;
        return res;
    }

    // Follow-up 3: Multiple singles (use HashMap)
    public java.util.List<Integer> allSingles(int[] nums) {
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        for (int num : nums) freq.put(num, freq.getOrDefault(num, 0) + 1);
        java.util.List<Integer> singles = new java.util.ArrayList<>();
        for (int num : freq.keySet()) if (freq.get(num) == 1) singles.add(num);
        return singles;
    }

    public static void main(String[] args) {
        SingleElementInSortedArray solution = new SingleElementInSortedArray();
        int[] nums1 = {1,1,2,3,3,4,4,8,8};
        System.out.println("Basic: " + solution.singleNonDuplicate(nums1)); // 2
        int[] nums2 = {1,1,2,2,3};
        System.out.println("Single at end: " + solution.singleNonDuplicate(nums2)); // 3
        int[] nums3 = {0,1,1,2,2};
        System.out.println("Single at start: " + solution.singleNonDuplicate(nums3)); // 0
        System.out.println("Index: " + solution.singleNonDuplicateIndex(nums1)); // 2
        int[] nums4 = {4,1,2,1,2};
        System.out.println("Unsorted: " + solution.singleNonDuplicateUnsorted(nums4)); // 4
        int[] nums5 = {1,2,2,3,4,4,5};
        System.out.println("All singles: " + solution.allSingles(nums5)); // [1,3,5]
    }
}
