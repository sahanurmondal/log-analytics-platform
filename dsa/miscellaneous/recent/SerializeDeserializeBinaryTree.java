package miscellaneous.recent;

import java.util.*;

/**
 * Recent Problem: Serialize and Deserialize Binary Tree
 * 
 * Description:
 * Design an algorithm to serialize and deserialize a binary tree.
 * 
 * Companies: Facebook, Amazon, Google
 * Difficulty: Hard
 * Asked: 2023-2024
 */
public class SerializeDeserializeBinaryTree {

    class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            sb.append("null,");
        } else {
            sb.append(node.val).append(",");
            serializeHelper(node.left, sb);
            serializeHelper(node.right, sb);
        }
    }

    public TreeNode deserialize(String data) {
        Queue<String> nodes = new LinkedList<>(Arrays.asList(data.split(",")));
        return deserializeHelper(nodes);
    }

    private TreeNode deserializeHelper(Queue<String> nodes) {
        String val = nodes.poll();
        if ("null".equals(val)) {
            return null;
        }

        TreeNode node = new TreeNode(Integer.parseInt(val));
        node.left = deserializeHelper(nodes);
        node.right = deserializeHelper(nodes);
        return node;
    }

    public static void main(String[] args) {
        SerializeDeserializeBinaryTree solution = new SerializeDeserializeBinaryTree();

        TreeNode root = solution.new TreeNode(1);
        root.left = solution.new TreeNode(2);
        root.right = solution.new TreeNode(3);
        root.right.left = solution.new TreeNode(4);
        root.right.right = solution.new TreeNode(5);

        String serialized = solution.serialize(root);
        System.out.println("Serialized: " + serialized);

        TreeNode deserialized = solution.deserialize(serialized);
        System.out.println("Deserialized successfully");
    }
}
