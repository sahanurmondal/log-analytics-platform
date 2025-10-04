package binarysearchtree.hard;

import java.util.*;

/**
 * LeetCode 315: Count of Smaller Numbers After Self
 * https://leetcode.com/problems/count-of-smaller-numbers-after-self/
 *
 * Description: Given an integer array nums, return an integer array counts
 * where counts[i] is the number of smaller elements to the right of nums[i].
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^5
 * - -10^4 <= nums[i] <= 10^4
 *
 * Follow-up:
 * - Can you solve it using Binary Indexed Tree or Segment Tree?
 * 
 * Time Complexity: O(n log n)
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class CountOfSmallerNumbersAfterSelf {

    // Main optimized solution - BST approach
    public List<Integer> countSmallerBST(int[] nums) {
        Integer[] result = new Integer[nums.length];

        for (int i = nums.length - 1; i >= 0; i--) {
            result[i] = 0;
        }

        TreeNode root = null;
        for (int i = nums.length - 1; i >= 0; i--) {
            root = insertAndCount(root, nums[i], result, i);
        }

        return Arrays.asList(result);
    }

    class TreeNode {
        int val;
        int count;
        int leftSize;
        TreeNode left, right;

        TreeNode(int val) {
            this.val = val;
            this.count = 1;
            this.leftSize = 0;
        }
    }

    private TreeNode insertAndCount(TreeNode root, int val, Integer[] result, int index) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val == root.val) {
            root.count++;
            result[index] += root.leftSize;
        } else if (val < root.val) {
            root.leftSize++;
            root.left = insertAndCount(root.left, val, result, index);
        } else {
            result[index] += root.leftSize + root.count;
            root.right = insertAndCount(root.right, val, result, index);
        }

        return root;
    }

    // Alternative solution - Merge Sort
    public List<Integer> countSmallerMergeSort(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int[] indices = new int[n];

        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        mergeSort(nums, indices, result, 0, n - 1);

        List<Integer> list = new ArrayList<>();
        for (int count : result) {
            list.add(count);
        }
        return list;
    }

    private void mergeSort(int[] nums, int[] indices, int[] result, int left, int right) {
        if (left >= right)
            return;

        int mid = left + (right - left) / 2;
        mergeSort(nums, indices, result, left, mid);
        mergeSort(nums, indices, result, mid + 1, right);
        merge(nums, indices, result, left, mid, right);
    }

    private void merge(int[] nums, int[] indices, int[] result, int left, int mid, int right) {
        int[] temp = new int[right - left + 1];
        int i = left, j = mid + 1, k = 0;

        while (i <= mid && j <= right) {
            if (nums[indices[j]] < nums[indices[i]]) {
                temp[k++] = indices[j++];
            } else {
                result[indices[i]] += j - mid - 1;
                temp[k++] = indices[i++];
            }
        }

        while (i <= mid) {
            result[indices[i]] += j - mid - 1;
            temp[k++] = indices[i++];
        }

        while (j <= right) {
            temp[k++] = indices[j++];
        }

        System.arraycopy(temp, 0, indices, left, temp.length);
    }

    public static void main(String[] args) {
        CountOfSmallerNumbersAfterSelf solution = new CountOfSmallerNumbersAfterSelf();

        System.out.println(solution.countSmallerBST(new int[] { 5, 2, 6, 1 })); // Expected: [2,1,1,0]
        System.out.println(solution.countSmallerMergeSort(new int[] { -1 })); // Expected: [0]
        System.out.println(solution.countSmallerBST(new int[] { -1, -1 })); // Expected: [0,0]
    }
}
