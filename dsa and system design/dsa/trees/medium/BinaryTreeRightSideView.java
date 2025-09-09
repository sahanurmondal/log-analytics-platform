package trees.medium;
import trees.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * LeetCode 199: Binary Tree Right Side View
 * https://leetcode.com/problems/binary-tree-right-side-view/
 *
 * Description:
 * Given a binary tree, return the values of the nodes you can see ordered from
 * top to bottom when looking from the right side.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 100].
 * - -100 <= Node.val <= 100
 *
 * Follow-up:
 * - Can you solve it recursively?
 */
public class BinaryTreeRightSideView {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                // If it's the rightmost element of this level, add to result
                if (i == size - 1) {
                    result.add(node.val);
                }
                // Offer left and right children to the queue
                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }
        }

        return result;
    }

    public static void main(String[] args) {
        BinaryTreeRightSideView solution = new BinaryTreeRightSideView();
        // Edge Case 1: Normal case
        TreeNode root1 = new TreeNode(1, new TreeNode(2, null, new TreeNode(5)),
                new TreeNode(3, null, new TreeNode(4)));
        System.out.println(solution.rightSideView(root1)); // [1,3,4]
        // Edge Case 2: Single node
        TreeNode root2 = new TreeNode(1);
        System.out.println(solution.rightSideView(root2)); // [1]
        // Edge Case 3: Empty tree
        System.out.println(solution.rightSideView(null)); // []
    }
}

