package design.easy;

import java.util.*;

/**
 * LeetCode 384: Shuffle an Array
 * https://leetcode.com/problems/shuffle-an-array/
 *
 * Description: Given an integer array nums, design an algorithm to randomly
 * shuffle the array.
 * All permutations of the array should be equally likely as a result of the
 * shuffling.
 * 
 * Constraints:
 * - 1 <= nums.length <= 50
 * - -10^6 <= nums[i] <= 10^6
 * - All the elements of nums are unique
 * - At most 10^4 calls in total will be made to reset and shuffle
 *
 * Follow-up:
 * - Can you implement the Fisher-Yates shuffle algorithm?
 * 
 * Time Complexity: O(n) for shuffle, O(n) for reset
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class Solution {

    private int[] original;
    private int[] array;
    private Random random;

    public Solution(int[] nums) {
        original = nums.clone();
        array = nums.clone();
        random = new Random();
    }

    public int[] reset() {
        array = original.clone();
        return array;
    }

    public int[] shuffle() {
        // Fisher-Yates shuffle algorithm
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            swap(array, i, j);
        }
        return array;
    }

    private void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    // Alternative implementation - Brute force shuffle
    static class SolutionBruteForce {
        private int[] original;
        private Random random;

        public SolutionBruteForce(int[] nums) {
            original = nums.clone();
            random = new Random();
        }

        public int[] reset() {
            return original.clone();
        }

        public int[] shuffle() {
            List<Integer> list = new ArrayList<>();
            for (int num : original) {
                list.add(num);
            }

            int[] result = new int[original.length];
            for (int i = 0; i < result.length; i++) {
                int index = random.nextInt(list.size());
                result[i] = list.remove(index);
            }

            return result;
        }
    }

    public static void main(String[] args) {
        Solution solution = new Solution(new int[] { 1, 2, 3 });
        System.out.println(Arrays.toString(solution.shuffle()));
        System.out.println(Arrays.toString(solution.reset()));
        System.out.println(Arrays.toString(solution.shuffle()));
    }
}
