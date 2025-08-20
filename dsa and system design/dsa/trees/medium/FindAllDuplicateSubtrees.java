package trees.medium;

import java.util.*;

/**
 * LeetCode 652: Find Duplicate Subtrees
 * https://leetcode.com/problems/find-duplicate-subtrees/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return all duplicate subtrees.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 5000]
 * - -200 <= Node.val <= 200
 * 
 * Follow-up Questions:
 * 1. Can you optimize space complexity?
 * 2. Can you find subtrees that appear exactly k times?
 * 3. Can you handle very large trees?
 */
public class FindAllDuplicateSubtrees {

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

    // Approach 1: Serialization with HashMap
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> subtreeCount = new HashMap<>();
        serialize(root, subtreeCount, result);
        return result;
    }

    private String serialize(TreeNode node, Map<String, Integer> count, List<TreeNode> result) {
        if (node == null)
            return "null";

        String subtree = node.val + "," + serialize(node.left, count, result) + ","
                + serialize(node.right, count, result);

        count.put(subtree, count.getOrDefault(subtree, 0) + 1);
        if (count.get(subtree) == 2) {
            result.add(node);
        }

        return subtree;
    }

    // Follow-up 1: Space optimized using ID mapping
    private int treeId = 1;

    public List<TreeNode> findDuplicateSubtreesOptimized(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> subtreeToId = new HashMap<>();
        Map<Integer, Integer> idCount = new HashMap<>();
        treeId = 1;

        postorder(root, subtreeToId, idCount, result);
        return result;
    }

    private int postorder(TreeNode node, Map<String, Integer> subtreeToId, Map<Integer, Integer> idCount,
            List<TreeNode> result) {
        if (node == null)
            return 0;

        int leftId = postorder(node.left, subtreeToId, idCount, result);
        int rightId = postorder(node.right, subtreeToId, idCount, result);

        String subtree = leftId + "," + node.val + "," + rightId;
        int id = subtreeToId.computeIfAbsent(subtree, k -> treeId++);

        idCount.put(id, idCount.getOrDefault(id, 0) + 1);
        if (idCount.get(id) == 2) {
            result.add(node);
        }

        return id;
    }

    // Follow-up 2: Find subtrees that appear exactly k times
    public List<TreeNode> findSubtreesWithKOccurrences(TreeNode root, int k) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, List<TreeNode>> subtreeNodes = new HashMap<>();
        serializeWithNodes(root, subtreeNodes);

        for (List<TreeNode> nodes : subtreeNodes.values()) {
            if (nodes.size() == k) {
                result.addAll(nodes);
            }
        }

        return result;
    }

    private String serializeWithNodes(TreeNode node, Map<String, List<TreeNode>> subtreeNodes) {
        if (node == null)
            return "null";

        String left = serializeWithNodes(node.left, subtreeNodes);
        String right = serializeWithNodes(node.right, subtreeNodes);
        String subtree = node.val + "," + left + "," + right;

        subtreeNodes.computeIfAbsent(subtree, k -> new ArrayList<>()).add(node);

        return subtree;
    }

    // Follow-up 3: Handle large trees with rolling hash
    public List<TreeNode> findDuplicateSubtreesRollingHash(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<Long, Integer> hashCount = new HashMap<>();
        Map<Long, TreeNode> hashToNode = new HashMap<>();

        computeHash(root, hashCount, hashToNode, result);
        return result;
    }

    private long computeHash(TreeNode node, Map<Long, Integer> hashCount, Map<Long, TreeNode> hashToNode,
            List<TreeNode> result) {
        if (node == null)
            return 0;

        long leftHash = computeHash(node.left, hashCount, hashToNode, result);
        long rightHash = computeHash(node.right, hashCount, hashToNode, result);

        // Rolling hash computation
        long hash = node.val + 31 * leftHash + 37 * rightHash;

        hashCount.put(hash, hashCount.getOrDefault(hash, 0) + 1);
        if (hashCount.get(hash) == 1) {
            hashToNode.put(hash, node);
        } else if (hashCount.get(hash) == 2) {
            result.add(hashToNode.get(hash));
        }

        return hash;
    }

    // Helper: Print tree structure
    private void printTree(TreeNode root, String prefix, boolean isLast) {
        if (root == null)
            return;

        System.out.println(prefix + (isLast ? "└── " : "├── ") + root.val);

        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printTree(root.left, prefix + (isLast ? "    " : "│   "), root.right == null);
            }
            if (root.right != null) {
                printTree(root.right, prefix + (isLast ? "    " : "│   "), true);
            }
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindAllDuplicateSubtrees solution = new FindAllDuplicateSubtrees();

        // Test case 1: Basic case with duplicates
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.right.left = new TreeNode(2);
        root1.right.right = new TreeNode(4);
        root1.right.left.left = new TreeNode(4);

        System.out.println("Test 1 - Basic case:");
        List<TreeNode> duplicates1 = solution.findDuplicateSubtrees(root1);
        System.out.println("Found " + duplicates1.size() + " duplicate subtrees:");
        for (int i = 0; i < duplicates1.size(); i++) {
            System.out.println("Duplicate " + (i + 1) + ":");
            solution.printTree(duplicates1.get(i), "", true);
        }

        // Test case 2: Optimized approach
        System.out.println("\nTest 2 - Optimized approach:");
        List<TreeNode> duplicates2 = solution.findDuplicateSubtreesOptimized(root1);
        System.out.println("Found " + duplicates2.size() + " duplicate subtrees");

        // Test case 3: Find subtrees with exactly 3 occurrences
        TreeNode root3 = new TreeNode(0);
        root3.left = new TreeNode(0);
        root3.right = new TreeNode(0);
        root3.right.left = new TreeNode(0);

        System.out.println("\nTest 3 - Subtrees with exactly 3 occurrences:");
        List<TreeNode> exactK = solution.findSubtreesWithKOccurrences(root3, 3);
        System.out.println("Found " + exactK.size() + " subtrees with exactly 3 occurrences");

        // Test case 4: Rolling hash approach
        System.out.println("\nTest 4 - Rolling hash approach:");
        List<TreeNode> duplicates4 = solution.findDuplicateSubtreesRollingHash(root1);
        System.out.println("Found " + duplicates4.size() + " duplicate subtrees");

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode single = new TreeNode(1);
        System.out.println("Single node: " + solution.findDuplicateSubtrees(single).size());

        TreeNode identical = new TreeNode(1);
        identical.left = new TreeNode(1);
        identical.right = new TreeNode(1);
        System.out.println("All identical values: " + solution.findDuplicateSubtrees(identical).size());

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        List<TreeNode> result1 = solution.findDuplicateSubtrees(largeTree);
        long end = System.nanoTime();
        System.out.println("Standard approach (1000 nodes): " + result1.size() + " duplicates in "
                + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        List<TreeNode> result2 = solution.findDuplicateSubtreesOptimized(largeTree);
        end = System.nanoTime();
        System.out.println(
                "Optimized approach: " + result2.size() + " duplicates in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode((count % 10) + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode((count % 10) + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
