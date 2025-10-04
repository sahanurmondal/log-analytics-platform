package arrays.hard;

import java.util.*;

/**
 * LeetCode 297: Serialize and Deserialize Binary Tree
 * https://leetcode.com/problems/serialize-and-deserialize-binary-tree/
 *
 * Description:
 * Design an algorithm to serialize and deserialize a binary tree.
 * Serialization is the process of converting a data structure into a sequence
 * of bits.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -1000 <= Node.val <= 1000
 *
 * Follow-up:
 * - Can you design different serialization methods?
 * 
 * Time Complexity: O(n) for both serialize and deserialize
 * Space Complexity: O(n)
 * 
 * Algorithm:
 * 1. Use preorder traversal for serialization with null markers
 * 2. Use queue for level-by-level deserialization
 * 3. Handle null nodes with special marker
 */
public class SerializeDeserializeBinaryTree {
    static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int x) {
            val = x;
        }
    }

    public class Codec {
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
            Queue<String> nodes = new LinkedList<>();
            nodes.addAll(Arrays.asList(data.split(",")));
            return deserializeHelper(nodes);
        }

        private TreeNode deserializeHelper(Queue<String> nodes) {
            String val = nodes.poll();
            if ("null".equals(val)) {
                return null;
            } else {
                TreeNode node = new TreeNode(Integer.valueOf(val));
                node.left = deserializeHelper(nodes);
                node.right = deserializeHelper(nodes);
                return node;
            }
        }
    }

    // Helper method to print tree (level order)
    private void printTree(TreeNode root) {
        if (root == null) {
            System.out.println("null");
            return;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        List<String> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                result.add("null");
            } else {
                result.add(String.valueOf(node.val));
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }

        System.out.println(result);
    }

    public static void main(String[] args) {
        SerializeDeserializeBinaryTree solution = new SerializeDeserializeBinaryTree();
        SerializeDeserializeBinaryTree.Codec codec = solution.new Codec();

        // Test Case 1: Normal case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.right.left = new TreeNode(4);
        root1.right.right = new TreeNode(5);
        String serialized1 = codec.serialize(root1);
        TreeNode deserialized1 = codec.deserialize(serialized1);
        solution.printTree(deserialized1); // Expected: [1,2,3,null,null,4,5]

        // Test Case 2: Edge case - empty tree
        String serialized2 = codec.serialize(null);
        TreeNode deserialized2 = codec.deserialize(serialized2);
        solution.printTree(deserialized2); // Expected: null

        // Test Case 3: Corner case - single node
        TreeNode root3 = new TreeNode(1);
        String serialized3 = codec.serialize(root3);
        TreeNode deserialized3 = codec.deserialize(serialized3);
        solution.printTree(deserialized3); // Expected: [1]

        // Test Case 4: Large input - left skewed
        TreeNode root4 = new TreeNode(1);
        root4.left = new TreeNode(2);
        root4.left.left = new TreeNode(3);
        String serialized4 = codec.serialize(root4);
        TreeNode deserialized4 = codec.deserialize(serialized4);
        solution.printTree(deserialized4); // Expected: [1,2,null,3]

        // Test Case 5: Right skewed tree
        TreeNode root5 = new TreeNode(1);
        root5.right = new TreeNode(2);
        root5.right.right = new TreeNode(3);
        String serialized5 = codec.serialize(root5);
        TreeNode deserialized5 = codec.deserialize(serialized5);
        solution.printTree(deserialized5); // Expected: [1,null,2,null,3]

        // Test Case 6: Negative values
        TreeNode root6 = new TreeNode(-1);
        root6.left = new TreeNode(-2);
        root6.right = new TreeNode(-3);
        String serialized6 = codec.serialize(root6);
        TreeNode deserialized6 = codec.deserialize(serialized6);
        solution.printTree(deserialized6); // Expected: [-1,-2,-3]

        // Test Case 7: Mixed positive/negative
        TreeNode root7 = new TreeNode(0);
        root7.left = new TreeNode(-1);
        root7.right = new TreeNode(1);
        String serialized7 = codec.serialize(root7);
        TreeNode deserialized7 = codec.deserialize(serialized7);
        solution.printTree(deserialized7); // Expected: [0,-1,1]

        // Test Case 8: Complete binary tree
        TreeNode root8 = new TreeNode(1);
        root8.left = new TreeNode(2);
        root8.right = new TreeNode(3);
        root8.left.left = new TreeNode(4);
        root8.left.right = new TreeNode(5);
        root8.right.left = new TreeNode(6);
        root8.right.right = new TreeNode(7);
        String serialized8 = codec.serialize(root8);
        TreeNode deserialized8 = codec.deserialize(serialized8);
        solution.printTree(deserialized8); // Expected: [1,2,3,4,5,6,7]

        // Test Case 9: Only left children
        TreeNode root9 = new TreeNode(1);
        root9.left = new TreeNode(2);
        root9.left.left = new TreeNode(4);
        String serialized9 = codec.serialize(root9);
        TreeNode deserialized9 = codec.deserialize(serialized9);
        solution.printTree(deserialized9); // Expected: [1,2,null,4]

        // Test Case 10: Large values
        TreeNode root10 = new TreeNode(1000);
        root10.left = new TreeNode(-1000);
        String serialized10 = codec.serialize(root10);
        TreeNode deserialized10 = codec.deserialize(serialized10);
        solution.printTree(deserialized10); // Expected: [1000,-1000]
    }
}
