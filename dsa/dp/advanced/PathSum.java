package dp.advanced;

/**
 * LeetCode 112: Path Sum
 * https://leetcode.com/problems/path-sum/
 *
 * Description:
 * Given the root of a binary tree and an integer targetSum, return true if the
 * tree has a root-to-leaf path
 * such that adding up all the values along the path equals targetSum.
 * A leaf is a node with no children.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 5000].
 * - -1000 <= Node.val <= 1000
 * - -1000 <= targetSum <= 1000
 *
 * Company Tags: Google, Amazon, Microsoft
 * Difficulty: Easy
 */
public class PathSum {

    // Definition for a binary tree node
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

    // Approach 1: Recursive DFS - O(n) time, O(h) space
    public boolean hasPathSum(TreeNode root, int targetSum) {
        if (root == null)
            return false;

        if (root.left == null && root.right == null) {
            return root.val == targetSum;
        }

        int remainingSum = targetSum - root.val;
        return hasPathSum(root.left, remainingSum) || hasPathSum(root.right, remainingSum);
    }

    // Approach 2: Iterative with Stack - O(n) time, O(n) space
    public boolean hasPathSumIterative(TreeNode root, int targetSum) {
        if (root == null)
            return false;

        java.util.Stack<TreeNode> nodeStack = new java.util.Stack<>();
        java.util.Stack<Integer> sumStack = new java.util.Stack<>();

        nodeStack.push(root);
        sumStack.push(targetSum - root.val);

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            int currentSum = sumStack.pop();

            if (node.left == null && node.right == null && currentSum == 0) {
                return true;
            }

            if (node.left != null) {
                nodeStack.push(node.left);
                sumStack.push(currentSum - node.left.val);
            }

            if (node.right != null) {
                nodeStack.push(node.right);
                sumStack.push(currentSum - node.right.val);
            }
        }

        return false;
    }

    public static void main(String[] args) {
        PathSum solution = new PathSum();

        System.out.println("=== Path Sum Test Cases ===");

        // Create test tree: [5,4,8,11,null,13,4,7,2,null,null,null,1]
        TreeNode root = new TreeNode(5);
        root.left = new TreeNode(4);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(11);
        root.left.left.left = new TreeNode(7);
        root.left.left.right = new TreeNode(2);
        root.right.left = new TreeNode(13);
        root.right.right = new TreeNode(4);
        root.right.right.right = new TreeNode(1);

        System.out.println("Target Sum = 22: " + solution.hasPathSum(root, 22));
        System.out.println("Expected: true\n");
    }
}
