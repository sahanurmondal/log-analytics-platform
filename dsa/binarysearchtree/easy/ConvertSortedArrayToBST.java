package binarysearchtree.easy;

/**
 * LeetCode 108: Convert Sorted Array to Binary Search Tree
 * https://leetcode.com/problems/convert-sorted-array-to-binary-search-tree/
 *
 * Description: Given an integer array nums where the elements are sorted in
 * ascending order,
 * convert it to a height-balanced binary search tree.
 * 
 * Constraints:
 * - 1 <= nums.length <= 10^4
 * - -10^4 <= nums[i] <= 10^4
 * - nums is sorted in a strictly increasing order
 *
 * Follow-up:
 * - Can you ensure the tree is height-balanced?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(log n)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class ConvertSortedArrayToBST {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode() {
        }

        TreeNode(int val) {
            this.val = val;
        }

        TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
    }

    // Main optimized solution - Divide and conquer
    public TreeNode sortedArrayToBST(int[] nums) {
        return buildBST(nums, 0, nums.length - 1);
    }

    private TreeNode buildBST(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }

        int mid = left + (right - left) / 2;
        TreeNode root = new TreeNode(nums[mid]);

        root.left = buildBST(nums, left, mid - 1);
        root.right = buildBST(nums, mid + 1, right);

        return root;
    }

    // Alternative solution - Choose left middle as root
    public TreeNode sortedArrayToBSTLeftMiddle(int[] nums) {
        return buildBSTLeftMiddle(nums, 0, nums.length - 1);
    }

    private TreeNode buildBSTLeftMiddle(int[] nums, int left, int right) {
        if (left > right) {
            return null;
        }

        int mid = (left + right) / 2;
        TreeNode root = new TreeNode(nums[mid]);

        root.left = buildBSTLeftMiddle(nums, left, mid - 1);
        root.right = buildBSTLeftMiddle(nums, mid + 1, right);

        return root;
    }

    public static void main(String[] args) {
        ConvertSortedArrayToBST solution = new ConvertSortedArrayToBST();

        int[] nums1 = { -10, -3, 0, 5, 9 };
        TreeNode root1 = solution.sortedArrayToBST(nums1);
        System.out.println("Root value: " + root1.val); // Expected: 0 (middle element)

        int[] nums2 = { 1, 3 };
        TreeNode root2 = solution.sortedArrayToBST(nums2);
        System.out.println("Root value: " + root2.val); // Expected: 1 or 3
    }
}
