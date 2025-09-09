package trees.hard;

import trees.TreeNode;
import java.util.*;

/**
 * LeetCode 297: Serialize and Deserialize Binary Tree
 * https://leetcode.com/problems/serialize-and-deserialize-binary-tree/
 *
 * Description:
 * Design an algorithm to serialize and deserialize a binary tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -1000 <= Node.val <= 1000
 *
 * Follow-up:
 * - Can you solve it iteratively?
 */
public class SerializeAndDeserializeBinaryTree {
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
            return;
        }

        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    public TreeNode deserialize(String data) {
        Queue<String> queue = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserializeHelper(queue);
    }

    private TreeNode deserializeHelper(Queue<String> queue) {
        String val = queue.poll();
        if ("null".equals(val)) {
            return null;
        }

        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(queue);
        node.right = deserializeHelper(queue);
        return node;
    }

    public static void main(String[] args) {
        SerializeAndDeserializeBinaryTree solution = new SerializeAndDeserializeBinaryTree();
        // Edge Case 1: Normal case
        TreeNode root1 = new TreeNode(1, new TreeNode(2), new TreeNode(3, new TreeNode(4), new TreeNode(5)));
        String data1 = solution.serialize(root1);
        TreeNode root1Deserialized = solution.deserialize(data1);
        // Edge Case 2: Single node
        TreeNode root2 = new TreeNode(1);
        String data2 = solution.serialize(root2);
        TreeNode root2Deserialized = solution.deserialize(data2);
        // Edge Case 3: Empty tree
        String data3 = solution.serialize(null);
        TreeNode root3Deserialized = solution.deserialize(data3);
    }
}
