package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 349: Intersection of Two Arrays
 * https://leetcode.com/problems/intersection-of-two-arrays/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 7+ interviews)
 *
 * Description: Given two integer arrays `nums1` and `nums2`, return an array of
 * their intersection. Each element in the result must be unique and you may
 * return the result in any order.
 *
 * Constraints:
 * - 1 <= nums1.length, nums2.length <= 1000
 * - 0 <= nums1[i], nums2[i] <= 1000
 * 
 * Follow-up Questions:
 * 1. What if the arrays are sorted?
 * 2. How is this different from "Intersection of Two Arrays II"?
 * 3. Can you solve it using only sets?
 */
public class FindIntersectionOfTwoArrays {

    // Approach 1: Two HashSets - O(n + m) time, O(n + m) space
    public int[] intersection(int[] nums1, int[] nums2) {
        Set<Integer> set1 = new HashSet<>();
        for (int num : nums1) {
            set1.add(num);
        }

        Set<Integer> intersectionSet = new HashSet<>();
        for (int num : nums2) {
            if (set1.contains(num)) {
                intersectionSet.add(num);
            }
        }

        return intersectionSet.stream().mapToInt(i -> i).toArray();
    }

    // Approach 2: Sorting and Two Pointers - O(n log n + m log m) time, O(1) space
    // (excluding result)
    public int[] intersectionSorted(int[] nums1, int[] nums2) {
        Arrays.sort(nums1);
        Arrays.sort(nums2);

        int i = 0, j = 0;
        Set<Integer> resultSet = new HashSet<>();

        while (i < nums1.length && j < nums2.length) {
            if (nums1[i] < nums2[j]) {
                i++;
            } else if (nums1[i] > nums2[j]) {
                j++;
            } else {
                resultSet.add(nums1[i]);
                i++;
                j++;
            }
        }

        return resultSet.stream().mapToInt(k -> k).toArray();
    }

    public static void main(String[] args) {
        FindIntersectionOfTwoArrays solution = new FindIntersectionOfTwoArrays();

        // Test case 1
        int[] nums1 = { 1, 2, 2, 1 };
        int[] nums2 = { 2, 2 };
        System.out.println("Intersection 1 (Set): " + Arrays.toString(solution.intersection(nums1, nums2))); // [2]

        // Test case 2
        int[] nums3 = { 4, 9, 5 };
        int[] nums4 = { 9, 4, 9, 8, 4 };
        System.out.println("Intersection 2 (Sorted): " + Arrays.toString(solution.intersectionSorted(nums3, nums4))); // [4,
                                                                                                                      // 9]
                                                                                                                      // or
                                                                                                                      // [9,
                                                                                                                      // 4]

        // Test case 3: No intersection
        int[] nums5 = { 1, 2, 3 };
        int[] nums6 = { 4, 5, 6 };
        System.out.println("Intersection 3: " + Arrays.toString(solution.intersection(nums5, nums6))); // []
    }
}
