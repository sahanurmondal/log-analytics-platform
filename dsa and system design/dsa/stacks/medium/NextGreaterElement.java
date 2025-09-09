package stacks.medium;

import java.util.*;

/**
 * LeetCode 496: Next Greater Element I
 * https://leetcode.com/problems/next-greater-element-i/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given two arrays nums1 and nums2, return an array of next
 * greater elements for nums1 in nums2.
 *
 * Constraints:
 * - 1 <= nums1.length, nums2.length <= 1000
 * - 0 <= nums1[i], nums2[i] <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you find next smaller element?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle duplicate values?
 */
public class NextGreaterElement {

        // Approach 1: Monotonic stack + hashmap
        public int[] nextGreaterElement(int[] nums1, int[] nums2) {
                Map<Integer, Integer> map = new HashMap<>();
                Stack<Integer> stack = new Stack<>();
                for (int num : nums2) {
                        while (!stack.isEmpty() && num > stack.peek())
                                map.put(stack.pop(), num);
                        stack.push(num);
                }
                while (!stack.isEmpty())
                        map.put(stack.pop(), -1);
                int[] res = new int[nums1.length];
                for (int i = 0; i < nums1.length; i++)
                        res[i] = map.get(nums1[i]);
                return res;
        }

        // Follow-up 1: Next smaller element
        public int[] nextSmallerElement(int[] nums1, int[] nums2) {
                Map<Integer, Integer> map = new HashMap<>();
                Stack<Integer> stack = new Stack<>();
                for (int num : nums2) {
                        while (!stack.isEmpty() && num < stack.peek())
                                map.put(stack.pop(), num);
                        stack.push(num);
                }
                while (!stack.isEmpty())
                        map.put(stack.pop(), -1);
                int[] res = new int[nums1.length];
                for (int i = 0; i < nums1.length; i++)
                        res[i] = map.get(nums1[i]);
                return res;
        }

        // Comprehensive test cases
        public static void main(String[] args) {
                NextGreaterElement solution = new NextGreaterElement();

                // Test case 1: Basic case
                int[] nums1 = { 4, 1, 2 }, nums2 = { 1, 3, 4, 2 };
                System.out.println("Test 1 - nums1: " + Arrays.toString(nums1) + ", nums2: " + Arrays.toString(nums2));
                System.out.println("Result: " + Arrays.toString(solution.nextGreaterElement(nums1, nums2)));

                // Test case 2: Next smaller
                System.out.println("\nTest 2 - Next smaller:");
                System.out.println(Arrays.toString(solution.nextSmallerElement(nums1, nums2)));

                // Edge cases
                System.out.println("\nEdge cases:");
                System.out.println("Single element: "
                                + Arrays.toString(solution.nextGreaterElement(new int[] { 5 }, new int[] { 5 })));
                System.out.println("All same: "
                                + Arrays.toString(solution.nextGreaterElement(new int[] { 2, 2 }, new int[] { 2, 2 })));
        }
}
