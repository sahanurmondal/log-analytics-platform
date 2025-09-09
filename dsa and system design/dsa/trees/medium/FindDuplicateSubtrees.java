package trees.medium;

import java.util.*;

/**
 * LeetCode 652: Find Duplicate Subtrees (Alternative Implementation)
 * https://leetcode.com/problems/find-duplicate-subtrees/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return all duplicate subtrees.
 * For each kind of duplicate subtrees, you only need to return the root node of
 * any one of them.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 5000]
 * - -200 <= Node.val <= 200
 * 
 * Follow-up Questions:
 * 1. Can you use structural hashing?
 * 2. Can you find subtrees with specific frequencies?
 * 3. Can you optimize memory usage?
 */
public class FindDuplicateSubtrees {

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

    // Approach 1: Serialization with frequency tracking
    public List<TreeNode> findDuplicateSubtrees(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<String, Integer> frequencies = new HashMap<>();
        serialize(root, frequencies, result);
        return result;
    }

    private String serialize(TreeNode node, Map<String, Integer> frequencies, List<TreeNode> result) {
        if (node == null)
            return "#";

        String left = serialize(node.left, frequencies, result);
        String right = serialize(node.right, frequencies, result);
        String subtree = node.val + "," + left + "," + right;

        frequencies.put(subtree, frequencies.getOrDefault(subtree, 0) + 1);
        if (frequencies.get(subtree) == 2) {
            result.add(node);
        }

        return subtree;
    }

    // Follow-up 1: Structural hashing for better performance
    private Map<String, Integer> structureToId = new HashMap<>();
    private Map<Integer, Integer> idFrequency = new HashMap<>();
    private int nextId = 1;

    public List<TreeNode> findDuplicateSubtreesHashing(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        structureToId.clear();
        idFrequency.clear();
        nextId = 1;

        hashSubtrees(root, result);
        return result;
    }

    private int hashSubtrees(TreeNode node, List<TreeNode> result) {
        if (node == null)
            return 0;

        int leftId = hashSubtrees(node.left, result);
        int rightId = hashSubtrees(node.right, result);

        String structure = leftId + "," + node.val + "," + rightId;
        int id = structureToId.computeIfAbsent(structure, k -> nextId++);

        idFrequency.put(id, idFrequency.getOrDefault(id, 0) + 1);
        if (idFrequency.get(id) == 2) {
            result.add(node);
        }

        return id;
    }

    // Follow-up 2: Find subtrees with specific frequencies
    public Map<Integer, List<TreeNode>> findSubtreesByFrequency(TreeNode root) {
        Map<Integer, List<TreeNode>> frequencyMap = new HashMap<>();
        Map<String, List<TreeNode>> subtreeNodes = new HashMap<>();
        serializeWithAllNodes(root, subtreeNodes);

        for (Map.Entry<String, List<TreeNode>> entry : subtreeNodes.entrySet()) {
            int frequency = entry.getValue().size();
            frequencyMap.computeIfAbsent(frequency, k -> new ArrayList<>()).addAll(entry.getValue());
        }

        return frequencyMap;
    }

    private String serializeWithAllNodes(TreeNode node, Map<String, List<TreeNode>> subtreeNodes) {
        if (node == null)
            return "#";

        String left = serializeWithAllNodes(node.left, subtreeNodes);
        String right = serializeWithAllNodes(node.right, subtreeNodes);
        String subtree = node.val + "," + left + "," + right;

        subtreeNodes.computeIfAbsent(subtree, k -> new ArrayList<>()).add(node);
        return subtree;
    }

    // Follow-up 3: Memory optimized using Merkle tree approach
    public List<TreeNode> findDuplicateSubtreesOptimized(TreeNode root) {
        List<TreeNode> result = new ArrayList<>();
        Map<Long, Integer> hashFrequency = new HashMap<>();
        Map<Long, TreeNode> hashToNode = new HashMap<>();

        computeMerkleHash(root, hashFrequency, hashToNode, result);
        return result;
    }

    private long computeMerkleHash(TreeNode node, Map<Long, Integer> hashFreq,
            Map<Long, TreeNode> hashToNode, List<TreeNode> result) {
        if (node == null)
            return 0;

        long leftHash = computeMerkleHash(node.left, hashFreq, hashToNode, result);
        long rightHash = computeMerkleHash(node.right, hashFreq, hashToNode, result);

        // Use a prime-based hash function
        long hash = node.val + 31L * leftHash + 37L * rightHash;

        hashFreq.put(hash, hashFreq.getOrDefault(hash, 0) + 1);
        if (hashFreq.get(hash) == 1) {
            hashToNode.put(hash, node);
        } else if (hashFreq.get(hash) == 2) {
            result.add(hashToNode.get(hash));
        }

        return hash;
    }

    // Helper: Print subtree structure
    private void printSubtree(TreeNode root, String prefix, boolean isLast) {
        if (root == null)
            return;

        System.out.println(prefix + (isLast ? "└── " : "├── ") + root.val);

        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printSubtree(root.left, prefix + (isLast ? "    " : "│   "), root.right == null);
            }
            if (root.right != null) {
                printSubtree(root.right, prefix + (isLast ? "    " : "│   "), true);
            }
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindDuplicateSubtrees solution = new FindDuplicateSubtrees();

        // Test case 1: Basic duplicate detection
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.right.left = new TreeNode(2);
        root1.right.right = new TreeNode(4);
        root1.right.left.left = new TreeNode(4);

        System.out.println("Test 1 - Basic duplicate detection:");
        List<TreeNode> duplicates1 = solution.findDuplicateSubtrees(root1);
        System.out.println("Found " + duplicates1.size() + " duplicate subtree types");
        for (int i = 0; i < duplicates1.size(); i++) {
            System.out.println("Duplicate " + (i + 1) + ":");
            solution.printSubtree(duplicates1.get(i), "", true);
        }

        // Test case 2: Structural hashing
        System.out.println("\nTest 2 - Structural hashing:");
        List<TreeNode> duplicates2 = solution.findDuplicateSubtreesHashing(root1);
        System.out.println("Found " + duplicates2.size() + " duplicate subtree types");

        // Test case 3: Subtrees by frequency
        System.out.println("\nTest 3 - Subtrees by frequency:");
        Map<Integer, List<TreeNode>> byFrequency = solution.findSubtreesByFrequency(root1);
        for (Map.Entry<Integer, List<TreeNode>> entry : byFrequency.entrySet()) {
            System.out.println("Frequency " + entry.getKey() + ": " + entry.getValue().size() + " subtrees");
        }

        // Test case 4: Memory optimized
        System.out.println("\nTest 4 - Memory optimized:");
        List<TreeNode> duplicates4 = solution.findDuplicateSubtreesOptimized(root1);
        System.out.println("Found " + duplicates4.size() + " duplicate subtree types");

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode single = new TreeNode(1);
        System.out.println("Single node: " + solution.findDuplicateSubtrees(single).size());

        TreeNode allSame = new TreeNode(0);
        allSame.left = new TreeNode(0);
        allSame.right = new TreeNode(0);
        allSame.right.left = new TreeNode(0);
        System.out.println("All same values: " + solution.findDuplicateSubtrees(allSame).size());

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildPatternTree(8); // Creates many duplicates

        long start = System.nanoTime();
        List<TreeNode> result1 = solution.findDuplicateSubtrees(largeTree);
        long end = System.nanoTime();
        System.out.println(
                "Standard approach: " + result1.size() + " duplicates in " + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        List<TreeNode> result2 = solution.findDuplicateSubtreesOptimized(largeTree);
        end = System.nanoTime();
        System.out.println(
                "Optimized approach: " + result2.size() + " duplicates in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildPatternTree(int levels) {
        if (levels <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        for (int level = 1; level < levels; level++) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                TreeNode node = queue.poll();
                // Create pattern that will have duplicates
                node.left = new TreeNode((level % 3) + 1);
                node.right = new TreeNode((level % 3) + 1);
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }

        return root;
    }
}
