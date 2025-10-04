package binarysearchtree.hard;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * LeetCode 449: Serialize and Deserialize BST
 * https://leetcode.com/problems/serialize-and-deserialize-bst/
 *
 * Description:
 * Design an algorithm to serialize and deserialize a BST.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^4 <= Node.val <= 10^4
 *
 * Follow-up:
 * - Can you solve it with preorder traversal?
 */
public class SerializeDeserializeBST {

    // Main solution: Preorder traversal - O(n) time, O(n) space
    public String serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        serializeHelper(root, sb);
        return sb.toString();
    }

    private void serializeHelper(TreeNode node, StringBuilder sb) {
        if (node == null) {
            return;
        }

        sb.append(node.val).append(",");
        serializeHelper(node.left, sb);
        serializeHelper(node.right, sb);
    }

    public TreeNode deserialize(String data) {
        if (data.isEmpty()) {
            return null;
        }

        String[] values = data.split(",");
        Queue<Integer> queue = new LinkedList<>();

        for (String val : values) {
            if (!val.isEmpty()) {
                queue.offer(Integer.parseInt(val));
            }
        }

        return deserializeHelper(queue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode deserializeHelper(Queue<Integer> queue, int minVal, int maxVal) {
        if (queue.isEmpty()) {
            return null;
        }

        int val = queue.peek();
        if (val < minVal || val > maxVal) {
            return null;
        }

        queue.poll();
        TreeNode root = new TreeNode(val);
        root.left = deserializeHelper(queue, minVal, val);
        root.right = deserializeHelper(queue, val, maxVal);

        return root;
    }

    // Alternative: Using level order traversal
    public String serializeLevelOrder(TreeNode root) {
        if (root == null)
            return "";

        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                sb.append("null,");
            } else {
                sb.append(node.val).append(",");
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }

        return sb.toString();
    }

    public TreeNode deserializeLevelOrder(String data) {
        if (data.isEmpty())
            return null;

        String[] values = data.split(",");
        TreeNode root = new TreeNode(Integer.parseInt(values[0]));
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        int i = 1;
        while (!queue.isEmpty() && i < values.length) {
            TreeNode node = queue.poll();

            if (!values[i].equals("null")) {
                node.left = new TreeNode(Integer.parseInt(values[i]));
                queue.offer(node.left);
            }
            i++;

            if (i < values.length && !values[i].equals("null")) {
                node.right = new TreeNode(Integer.parseInt(values[i]));
                queue.offer(node.right);
            }
            i++;
        }

        return root;
    }

    // Compact representation using only preorder
    public String serializeCompact(TreeNode root) {
        List<Integer> preorder = new ArrayList<>();
        preorderTraversal(root, preorder);

        StringBuilder sb = new StringBuilder();
        for (int val : preorder) {
            sb.append(val).append(",");
        }

        return sb.toString();
    }

    private void preorderTraversal(TreeNode node, List<Integer> preorder) {
        if (node == null)
            return;

        preorder.add(node.val);
        preorderTraversal(node.left, preorder);
        preorderTraversal(node.right, preorder);
    }

    public TreeNode deserializeCompact(String data) {
        if (data.isEmpty())
            return null;

        String[] values = data.split(",");
        int[] index = { 0 };

        return buildBST(values, index, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    private TreeNode buildBST(String[] values, int[] index, int minVal, int maxVal) {
        if (index[0] >= values.length)
            return null;

        int val = Integer.parseInt(values[index[0]]);
        if (val < minVal || val > maxVal) {
            return null;
        }

        index[0]++;
        TreeNode root = new TreeNode(val);
        root.left = buildBST(values, index, minVal, val);
        root.right = buildBST(values, index, val, maxVal);

        return root;
    }

    // Helper: Print inorder to verify BST property
    public void printInorder(TreeNode root) {
        if (root == null)
            return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    public static void main(String[] args) {
        SerializeDeserializeBST solution = new SerializeDeserializeBST();

        // Test Case 1: Normal BST
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(1);
        root1.right = new TreeNode(3);

        String serialized1 = solution.serialize(root1);
        System.out.println("Serialized: " + serialized1); // Expected: "2,1,3,"

        TreeNode deserialized1 = solution.deserialize(serialized1);
        System.out.print("Deserialized (inorder): ");
        solution.printInorder(deserialized1); // Expected: 1 2 3
        System.out.println();

        // Test Case 2: Empty tree
        String serialized2 = solution.serialize(null);
        System.out.println("Empty tree serialized: " + serialized2); // Expected: ""

        TreeNode deserialized2 = solution.deserialize(serialized2);
        System.out.println("Empty tree deserialized: " + deserialized2); // Expected: null

        // Test Case 3: Single node
        TreeNode root3 = new TreeNode(1);
        String serialized3 = solution.serialize(root3);
        TreeNode deserialized3 = solution.deserialize(serialized3);
        System.out.print("Single node (inorder): ");
        solution.printInorder(deserialized3); // Expected: 1
        System.out.println();

        // Test compact serialization
        String compact = solution.serializeCompact(root1);
        TreeNode compactDeserialized = solution.deserializeCompact(compact);
        System.out.print("Compact deserialized (inorder): ");
        solution.printInorder(compactDeserialized); // Expected: 1 2 3
        System.out.println();

        // Additional Edge Cases
        // Edge Case 1: Normal case (different structure)
        TreeNode rootA = new TreeNode(2, new TreeNode(1), new TreeNode(3));
        String dataA = solution.serialize(rootA);
        TreeNode treeA = solution.deserialize(dataA);
        System.out.print("Edge case A (inorder): ");
        solution.printInorder(treeA);
        System.out.println();

        // Edge Case 2: No tree (null)
        String dataB = solution.serialize(null);
        TreeNode treeB = solution.deserialize(dataB);
        System.out.println("Edge case B (null): " + (treeB == null ? "null" : "not null"));

        // Edge Case 3: Descending tree
        TreeNode rootC = new TreeNode(3, new TreeNode(2, new TreeNode(1), null), null);
        String dataC = solution.serialize(rootC);
        TreeNode treeC = solution.deserialize(dataC);
        System.out.print("Edge case C (inorder): ");
        solution.printInorder(treeC);
        System.out.println();
    }
}
