package sorting.medium;

import java.util.*;

/**
 * LeetCode 1122: Relative Sort Array
 * https://leetcode.com/problems/relative-sort-array/
 *
 * Description:
 * Given two arrays arr1 and arr2, the elements of arr2 are distinct, and all
 * elements in arr2 are also in arr1.
 * Sort the elements of arr1 such that the relative ordering of items in arr1
 * are the same as in arr2.
 *
 * Constraints:
 * - 1 <= arr1.length, arr2.length <= 1000
 * - 0 <= arr1[i], arr2[i] <= 1000
 * - All the elements of arr2 are distinct
 * - Each arr2[i] is in arr1
 *
 * Follow-up:
 * - Can you solve it using counting sort?
 * - Can you solve it using custom comparator?
 * - Can you optimize for very large value ranges?
 */
public class RelativeSortArray {
        public int[] relativeSortArray(int[] arr1, int[] arr2) {
                // Create order map from arr2
                Map<Integer, Integer> orderMap = new HashMap<>();
                for (int i = 0; i < arr2.length; i++) {
                        orderMap.put(arr2[i], i);
                }

                // Convert to list for custom sorting
                List<Integer> list = new ArrayList<>();
                for (int num : arr1) {
                        list.add(num);
                }

                // Custom sort: elements in arr2 by their order, others by natural order
                list.sort((a, b) -> {
                        if (orderMap.containsKey(a) && orderMap.containsKey(b)) {
                                return orderMap.get(a) - orderMap.get(b);
                        } else if (orderMap.containsKey(a)) {
                                return -1; // a comes first
                        } else if (orderMap.containsKey(b)) {
                                return 1; // b comes first
                        } else {
                                return a - b; // natural order for elements not in arr2
                        }
                });

                // Convert back to array
                int[] result = new int[arr1.length];
                for (int i = 0; i < list.size(); i++) {
                        result[i] = list.get(i);
                }

                return result;
        }

        public static void main(String[] args) {
                RelativeSortArray solution = new RelativeSortArray();

                System.out.println(java.util.Arrays.toString(solution
                                .relativeSortArray(new int[] { 2, 3, 1, 3, 2, 4, 6, 7, 9, 2, 19 },
                                                new int[] { 2, 1, 4, 3, 9, 6 })));
                // [2,2,2,1,4,3,3,9,6,7,19]

                System.out.println(java.util.Arrays
                                .toString(solution.relativeSortArray(new int[] { 28, 6, 22, 8, 44, 17 },
                                                new int[] { 22, 28, 8, 6 })));
                // [22,28,8,6,17,44]

                // Edge Case: All elements in arr2
                System.out.println(
                                java.util.Arrays.toString(solution.relativeSortArray(new int[] { 2, 1, 3 },
                                                new int[] { 1, 2, 3 })));
                // [1,2,3]

                // Edge Case: Some elements not in arr2
                System.out.println(
                                java.util.Arrays.toString(solution.relativeSortArray(new int[] { 2, 1, 3, 4 },
                                                new int[] { 2, 1 })));
                // [2,1,3,4]

                // Edge Case: Duplicates in arr1
                System.out.println(java.util.Arrays
                                .toString(solution.relativeSortArray(new int[] { 1, 1, 1, 2, 2, 3 },
                                                new int[] { 3, 2, 1 })));
                // [3,2,2,1,1,1]
        }
}
