package hashmaps.medium;

import java.util.*;

/**
 * LeetCode 652: Find Duplicate Subtrees
 * https://leetcode.com/problems/find-duplicate-subtrees/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: Medium (Asked in 8+ interviews)
 *
 * Description: Given the root of a binary tree, return all duplicate subtrees.
 * For each kind of duplicate subtrees, you only need to return the root node of
 * any one of them.
 *
 * Constraints:
 * - The number of nodes in the tree will be in the range [1, 5000].
 * - -200 <= Node.val <= 200
 * 
 * Follow-up Questions:
 * 1. How does serialization help in identifying subtrees?
 * 2. Can you optimize the serialization process?
 * 3. What is the time and space complexity of this approach?
 */
public class FindDuplicateSubtrees {

    // Definition for a binary tree node.
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int val) {
            this.val = val;
        }
    }

    // Approach 1: Post-order Traversal with Serialization - O(n^2) time (due to
    // string concatenation), O(n^2) space
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        Map<String, Integer> count = new HashMap<>();
        List<TreeNode> result = new ArrayList<>();
        postorder(root, count, result);
        return result;
    }

    private String postorder(TreeNode node, Map<String, Integer> count, List<TreeNode> result) {
        if (node == null) {
            return "#";
        }

        String serial = node.val + "," + postorder(node.left, count, result) + ","
                + postorder(node.right, count, result);

        count.put(serial, count.getOrDefault(serial, 0) + 1);

        if (count.get(serial) == 2) {
            result.add(node);
        }

        return serial;
    }

    // Approach 2: Optimized Serialization with IDs - O(n) time, O(n) space
    private int currentId = 1;
    private Map<String, Integer> serialToId = new HashMap<>();
    private Map<Integer, Integer> idCount = new HashMap<>();
    private List<TreeNode> resultOptimized = new ArrayList<>();

    public List<TreeNode> findDuplicateSubtreesOptimized(TreeNode root) {
        postorderOptimized(root);
        return resultOptimized;
    }

    private int postorderOptimized(TreeNode node) {
        if (node == null)
            return 0;

        String serial = node.val + "," + postorderOptimized(node.left) + "," + postorderOptimized(node.right);

        int id = serialToId.computeIfAbsent(serial, k -> currentId++);
        idCount.put(id, idCount.getOrDefault(id, 0) + 1);

        if (idCount.get(id) == 2) {
            resultOptimized.add(node);
        }

        return id;
    }

    public static void main(String[] args) {
        FindDuplicateSubtrees solution = new FindDuplicateSubtrees();

        // Test case 1
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.right.left = new TreeNode(2);
        root1.right.right = new TreeNode(4);
        root1.right.left.left = new TreeNode(4);

        List<TreeNode> duplicates1 = solution.findDuplicateSubtrees(root1);
        System.out.println("Duplicates 1: ");
        for (TreeNode node : duplicates1) {
            System.out.print(node.val + " "); // Expected: 2 4
        }
        System.out.println();

        // Test case 2 (Optimized)
        FindDuplicateSubtrees solution2 = new FindDuplicateSubtrees();
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        root2.left.left = new TreeNode(4);
        root2.right.left = new TreeNode(2);
        root2.right.right = new TreeNode(4);
        root2.right.left.left = new TreeNode(4);
        List<TreeNode> duplicates2 = solution2.findDuplicateSubtreesOptimized(root2);
        System.out.println("Duplicates 2 (Optimized): ");
        for (TreeNode node : duplicates2) {
            System.out.print(node.val + " "); // Expected: 4 2
        }
        System.out.println();
    }
}
