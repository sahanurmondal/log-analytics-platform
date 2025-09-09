package trees.medium;

/**
 * LeetCode 129: Sum Root to Leaf Numbers
 * https://leetcode.com/problems/sum-root-to-leaf-numbers/
 *
 * Description:
 * Given a binary tree containing digits from 0-9 only, each root-to-leaf path
 * represents a number. Return the total sum of all root-to-leaf numbers.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 1000].
 * - 0 <= Node.val <= 9
 *
 * Follow-up:
 * - Can you solve it recursively?
 */
public class SumRootToLeafNumbers {

    public static class TreeNode {
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

    public int sumNumbers(TreeNode root) {
        return sumNumbersHelper(root, 0);
    }

    private int sumNumbersHelper(TreeNode node, int currentNumber) {
        if (node == null)
            return 0;

        currentNumber = currentNumber * 10 + node.val;

        // If leaf node, return the number
        if (node.left == null && node.right == null) {
            return currentNumber;
        }

        return sumNumbersHelper(node.left, currentNumber) +
                sumNumbersHelper(node.right, currentNumber);
    }

    public static void main(String[] args) {
        SumRootToLeafNumbers solution = new SumRootToLeafNumbers();
        // Edge Case 1: Normal case
        TreeNode root1 = new TreeNode(1, new TreeNode(2), new TreeNode(3));
        System.out.println(solution.sumNumbers(root1)); // 25
        // Edge Case 2: Single node
        TreeNode root2 = new TreeNode(5);
        System.out.println(solution.sumNumbers(root2)); // 5
    }
}
