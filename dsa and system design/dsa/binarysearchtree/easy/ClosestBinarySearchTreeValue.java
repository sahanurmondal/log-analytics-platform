package binarysearchtree.easy;

/**
 * LeetCode 270: Closest Binary Search Tree Value
 * https://leetcode.com/problems/closest-binary-search-tree-value/
 *
 * Description: Given the root of a binary search tree and a target value,
 * return the value in the BST that is closest to the target.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 0 <= Node.val <= 10^9
 * - -10^9 <= target <= 10^9
 *
 * Follow-up:
 * - Can you solve it iteratively?
 * 
 * Time Complexity: O(h)
 * Space Complexity: O(h) for recursive, O(1) for iterative
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class ClosestBinarySearchTreeValue {

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

    // Main optimized solution - Iterative
    public int closestValue(TreeNode root, double target) {
        int closest = root.val;

        while (root != null) {
            if (Math.abs(root.val - target) < Math.abs(closest - target)) {
                closest = root.val;
            }

            root = target < root.val ? root.left : root.right;
        }

        return closest;
    }

    // Alternative solution - Recursive
    public int closestValueRecursive(TreeNode root, double target) {
        int val = root.val;
        TreeNode child = target < val ? root.left : root.right;

        if (child == null)
            return val;

        int childClosest = closestValueRecursive(child, target);
        return Math.abs(val - target) < Math.abs(childClosest - target) ? val : childClosest;
    }

    public static void main(String[] args) {
        ClosestBinarySearchTreeValue solution = new ClosestBinarySearchTreeValue();

        TreeNode root = new TreeNode(4);
        root.left = new TreeNode(2);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(3);

        System.out.println(solution.closestValue(root, 3.714286)); // Expected: 4
        System.out.println(solution.closestValueRecursive(root, 3.714286)); // Expected: 4
    }
}
