package twopointers.hard;

import java.util.*;

/**
 * LeetCode 18: 4Sum
 * URL: https://leetcode.com/problems/4sum/
 * Difficulty: Medium (but in hard folder due to complexity)
 * 
 * Companies: Amazon, Google, Microsoft, Facebook, Apple, LinkedIn
 * Frequency: High
 * 
 * Description:
 * Given an array nums of n integers and target, return all unique quadruplets
 * [nums[a], nums[b], nums[c], nums[d]] such that nums[a] + nums[b] + nums[c] +
 * nums[d] == target.
 * 
 * Constraints:
 * - 1 <= nums.length <= 200
 * - -10^9 <= nums[i] <= 10^9
 * - -10^9 <= target <= 10^9
 * - Solution set must not contain duplicate quadruplets
 * 
 * Follow-up Questions:
 * 1. Can you solve it in O(n^3) time?
 * 2. How would you generalize to k-sum?
 * 3. Can you avoid duplicate quadruplets efficiently?
 * 4. How would you optimize for space complexity?
 * 5. How would you handle integer overflow?
 */
public class FourSum {

    // Approach 1: Sorting + Two Pointers (Nested)
    public List<List<Integer>> fourSum(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 4)
            return result;

        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            // Skip duplicates for first number
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            // Early termination optimizations
            if ((long) nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] > target)
                break;
            if ((long) nums[i] + nums[n - 3] + nums[n - 2] + nums[n - 1] < target)
                continue;

            for (int j = i + 1; j < n - 2; j++) {
                // Skip duplicates for second number
                if (j > i + 1 && nums[j] == nums[j - 1])
                    continue;

                // Early termination for inner loop
                if ((long) nums[i] + nums[j] + nums[j + 1] + nums[j + 2] > target)
                    break;
                if ((long) nums[i] + nums[j] + nums[n - 2] + nums[n - 1] < target)
                    continue;

                int left = j + 1, right = n - 1;

                while (left < right) {
                    long sum = (long) nums[i] + nums[j] + nums[left] + nums[right];

                    if (sum == target) {
                        result.add(Arrays.asList(nums[i], nums[j], nums[left], nums[right]));

                        // Skip duplicates for third number
                        while (left < right && nums[left] == nums[left + 1])
                            left++;
                        // Skip duplicates for fourth number
                        while (left < right && nums[right] == nums[right - 1])
                            right--;

                        left++;
                        right--;
                    } else if (sum < target) {
                        left++;
                    } else {
                        right--;
                    }
                }
            }
        }

        return result;
    }

    // Approach 2: HashMap-based solution
    public List<List<Integer>> fourSumHashMap(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 4)
            return result;

        Arrays.sort(nums);
        Set<List<Integer>> uniqueQuads = new HashSet<>();

        for (int i = 0; i < nums.length - 3; i++) {
            for (int j = i + 1; j < nums.length - 2; j++) {
                Set<Integer> seen = new HashSet<>();

                for (int k = j + 1; k < nums.length; k++) {
                    long complement = (long) target - nums[i] - nums[j] - nums[k];

                    if (complement >= Integer.MIN_VALUE && complement <= Integer.MAX_VALUE) {
                        if (seen.contains((int) complement)) {
                            List<Integer> quad = Arrays.asList(nums[i], nums[j], (int) complement, nums[k]);
                            Collections.sort(quad);
                            uniqueQuads.add(quad);
                        }
                    }
                    seen.add(nums[k]);
                }
            }
        }

        return new ArrayList<>(uniqueQuads);
    }

    // Approach 3: Generalized k-Sum solution
    public List<List<Integer>> kSum(int[] nums, int target, int k) {
        Arrays.sort(nums);
        return kSumHelper(nums, target, 0, k);
    }

    private List<List<Integer>> kSumHelper(int[] nums, long target, int start, int k) {
        List<List<Integer>> result = new ArrayList<>();

        if (start == nums.length || nums[start] * k > target || target > nums[nums.length - 1] * k) {
            return result;
        }

        if (k == 2) {
            return twoSum(nums, target, start);
        }

        for (int i = start; i < nums.length; i++) {
            if (i == start || nums[i - 1] != nums[i]) {
                List<List<Integer>> subResults = kSumHelper(nums, target - nums[i], i + 1, k - 1);
                for (List<Integer> subResult : subResults) {
                    List<Integer> newResult = new ArrayList<>();
                    newResult.add(nums[i]);
                    newResult.addAll(subResult);
                    result.add(newResult);
                }
            }
        }

        return result;
    }

    private List<List<Integer>> twoSum(int[] nums, long target, int start) {
        List<List<Integer>> result = new ArrayList<>();
        int left = start, right = nums.length - 1;

        while (left < right) {
            long sum = (long) nums[left] + nums[right];

            if (sum < target || (left > start && nums[left] == nums[left - 1])) {
                left++;
            } else if (sum > target || (right < nums.length - 1 && nums[right] == nums[right + 1])) {
                right--;
            } else {
                result.add(Arrays.asList(nums[left++], nums[right--]));
            }
        }

        return result;
    }

    // Approach 4: Optimized with early pruning
    public List<List<Integer>> fourSumOptimized(int[] nums, int target) {
        List<List<Integer>> result = new ArrayList<>();
        if (nums == null || nums.length < 4)
            return result;

        Arrays.sort(nums);
        int n = nums.length;

        for (int i = 0; i < n - 3; i++) {
            if (i > 0 && nums[i] == nums[i - 1])
                continue;

            // Pruning conditions
            if (nums[i] > target && target > 0)
                break;
            if (nums[i] + nums[n - 1] + nums[n - 2] + nums[n - 3] < target)
                continue;
            if (nums[i] + nums[i + 1] + nums[i + 2] + nums[i + 3] > target)
                break;

            List<List<Integer>> threeSums = threeSumForTarget(nums, i + 1, target - nums[i]);
            for (List<Integer> threeSum : threeSums) {
                List<Integer> fourSum = new ArrayList<>();
                fourSum.add(nums[i]);
                fourSum.addAll(threeSum);
                result.add(fourSum);
            }
        }

        return result;
    }

    private List<List<Integer>> threeSumForTarget(int[] nums, int start, int target) {
        List<List<Integer>> result = new ArrayList<>();

        for (int i = start; i < nums.length - 2; i++) {
            if (i > start && nums[i] == nums[i - 1])
                continue;

            int left = i + 1, right = nums.length - 1;
            while (left < right) {
                int sum = nums[i] + nums[left] + nums[right];

                if (sum == target) {
                    result.add(Arrays.asList(nums[i], nums[left], nums[right]));
                    while (left < right && nums[left] == nums[left + 1])
                        left++;
                    while (left < right && nums[right] == nums[right - 1])
                        right--;
                    left++;
                    right--;
                } else if (sum < target) {
                    left++;
                } else {
                    right--;
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        FourSum solution = new FourSum();

        // Test Case 1: Basic example
        int[] nums1 = { 1, 0, -1, 0, -2, 2 };
        System.out.println("Basic: " + solution.fourSum(nums1, 0));
        // Expected: [[-2,-1,1,2],[-2,0,0,2],[-1,0,0,1]]

        // Test Case 2: All same elements
        int[] nums2 = { 2, 2, 2, 2, 2 };
        System.out.println("All same: " + solution.fourSum(nums2, 8)); // [[2,2,2,2]]

        // Test Case 3: No valid quadruplets
        int[] nums3 = { 1, 2, 3 };
        System.out.println("No valid: " + solution.fourSum(nums3, 10)); // []

        // Test Case 4: Minimum array size
        int[] nums4 = { 1, 2, 3, 4 };
        System.out.println("Minimum size: " + solution.fourSum(nums4, 10)); // [[1,2,3,4]]

        // Test Case 5: All zeros
        int[] nums5 = { 0, 0, 0, 0 };
        System.out.println("All zeros: " + solution.fourSum(nums5, 0)); // [[0,0,0,0]]

        // Test Case 6: HashMap approach
        System.out.println("HashMap: " + solution.fourSumHashMap(nums1, 0));

        // Test Case 7: k-Sum generalized (k=4)
        System.out.println("kSum: " + solution.kSum(nums1, 0, 4));

        // Test Case 8: Mixed positive and negative
        int[] nums8 = { -3, -2, -1, 0, 0, 1, 2, 3 };
        System.out.println("Mixed signs: " + solution.fourSum(nums8, 0));

        // Test Case 9: Large numbers
        int[] nums9 = { 1000000000, 1000000000, 1000000000, 1000000000 };
        System.out.println("Large numbers: " + solution.fourSum(nums9, -294967268)); // []

        // Test Case 10: Optimized approach
        System.out.println("Optimized: " + solution.fourSumOptimized(nums1, 0));

        // Test Case 11: Negative target
        int[] nums11 = { -1, -2, -3, -4, -5 };
        System.out.println("Negative target: " + solution.fourSum(nums11, -10)); // [[-5,-4,-1,0], etc.]

        // Test Case 12: Single valid solution
        int[] nums12 = { 1, 2, 3, 4, 5 };
        System.out.println("Single solution: " + solution.fourSum(nums12, 14)); // [[2,3,4,5]]
    }
}
