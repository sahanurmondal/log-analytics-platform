package binarysearchtree.hard;

import java.util.*;

/**
 * LeetCode 449: Serialize and Deserialize BST
 * https://leetcode.com/problems/serialize-and-deserialize-bst/
 *
 * Description: Design an algorithm to serialize and deserialize a binary search
 * tree.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - 0 <= Node.val <= 10^4
 * - The input tree is guaranteed to be a binary search tree
 *
 * Follow-up:
 * - Can you do it more efficiently than general binary tree serialization?
 * 
 * Time Complexity: O(n)
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class SerializeAndDeserializeBST {

    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    // Main optimized solution - Preorder traversal
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null)
            return;

        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    public TreeNode deserialize(String data) {
        if (data.isEmpty())
            return null;

        String[] values = data.split(",");
        Queue<Integer> queue = new LinkedList<>();
        for (String val : values) {
            if (!val.isEmpty()) {
                queue.offer(Integer.parseInt(val));
            }
        }

        return deserializeHelper(queue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode deserializeHelper(Queue<Integer> queue, int min, int max) {
        if (queue.isEmpty())
            return null;

        int val = queue.peek();
        if (val < min || val > max)
            return null;

        queue.poll();
        TreeNode root = new TreeNode(val);
        root.left = deserializeHelper(queue, min, val);
        root.right = deserializeHelper(queue, val, max);

        return root;
    }

    // Alternative solution - Using postorder
    public String serializePostorder(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        postorder(root, sb);
        return sb.toString();
    }

    private void postorder(TreeNode node, StringBuilder sb) {
        if (node == null)
            return;

        postorder(node.left, sb);
        postorder(node.right, sb);
        sb.append(node.val).append(",");
    }

    public TreeNode deserializePostorder(String data) {
        if (data.isEmpty())
            return null;

        String[] values = data.split(",");
        Stack<Integer> stack = new Stack<>();
        for (String val : values) {
            if (!val.isEmpty()) {
                stack.push(Integer.parseInt(val));
            }
        }

        return deserializePostorderHelper(stack, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode deserializePostorderHelper(Stack<Integer> stack, int min, int max) {
        if (stack.isEmpty())
            return null;

        int val = stack.peek();
        if (val < min || val > max)
            return null;

        stack.pop();
        TreeNode root = new TreeNode(val);
        root.right = deserializePostorderHelper(stack, val, max);
        root.left = deserializePostorderHelper(stack, min, val);

        return root;
    }

    public static void main(String[] args) {
        SerializeAndDeserializeBST codec = new SerializeAndDeserializeBST();

        TreeNode root = new TreeNode(2);
        root.left = new TreeNode(1);
        root.right = new TreeNode(3);

        String serialized = codec.serialize(root);
        TreeNode deserialized = codec.deserialize(serialized);

        System.out.println("Serialization/Deserialization successful");
    }
}
