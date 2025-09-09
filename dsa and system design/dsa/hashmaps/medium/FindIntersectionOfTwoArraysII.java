package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 350: Intersection of Two Arrays II
 * https://leetcode.com/problems/intersection-of-two-arrays-ii/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 8+ interviews)
 *
 * Description: Given two integer arrays `nums1` and `nums2`, return an array of
 * their intersection. Each element in the result must appear as many times as
 * it shows in both arrays and you may return the result in any order.
 *
 * Constraints:
 * - 1 <= nums1.length, nums2.length <= 1000
 * - 0 <= nums1[i], nums2[i] <= 1000
 * 
 * Follow-up Questions:
 * 1. What if the given array is already sorted? How would you optimize your
 * algorithm?
 * 2. What if `nums1`'s size is small compared to `nums2`'s size? Which
 * algorithm is better?
 * 3. What if elements of `nums2` are stored on disk, and memory is limited such
 * that you cannot load all elements into memory at once?
 */
public class FindIntersectionOfTwoArraysII {

    // Approach 1: HashMap - O(n + m) time, O(min(n, m)) space
    public int[] intersect(int[] nums1, int[] nums2) {
        if (nums1.length > nums2.length) {
            return intersect(nums2, nums1); // Ensure nums1 is the smaller array
        }

        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums1) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        List<Integer> resultList = new ArrayList<>();
        for (int num : nums2) {
            if (freqMap.containsKey(num) && freqMap.get(num) > 0) {
                resultList.add(num);
                freqMap.put(num, freqMap.get(num) - 1);
            }
        }

        return resultList.stream().mapToInt(i -> i).toArray();
    }

    // Approach 2: Sorting and Two Pointers - O(n log n + m log m) time, O(1) space
    // (excluding result)
    public int[] intersectSorted(int[] nums1, int[] nums2) {
        Arrays.sort(nums1);
        Arrays.sort(nums2);

        int i = 0, j = 0;
        List<Integer> resultList = new ArrayList<>();

        while (i < nums1.length && j < nums2.length) {
            if (nums1[i] < nums2[j]) {
                i++;
            } else if (nums1[i] > nums2[j]) {
                j++;
            } else {
                resultList.add(nums1[i]);
                i++;
                j++;
            }
        }

        return resultList.stream().mapToInt(k -> k).toArray();
    }

    public static void main(String[] args) {
        FindIntersectionOfTwoArraysII solution = new FindIntersectionOfTwoArraysII();

        // Test case 1
        int[] nums1 = { 1, 2, 2, 1 };
        int[] nums2 = { 2, 2 };
        System.out.println("Intersection 1 (Map): " + Arrays.toString(solution.intersect(nums1, nums2))); // [2, 2]

        // Test case 2
        int[] nums3 = { 4, 9, 5 };
        int[] nums4 = { 9, 4, 9, 8, 4 };
        System.out.println("Intersection 2 (Sorted): " + Arrays.toString(solution.intersectSorted(nums3, nums4))); // [4,
                                                                                                                   // 9]

        // Test case 3: No intersection
        int[] nums5 = { 1, 2, 3 };
        int[] nums6 = { 4, 5, 6 };
        System.out.println("Intersection 3: " + Arrays.toString(solution.intersect(nums5, nums6))); // []
    }
}
