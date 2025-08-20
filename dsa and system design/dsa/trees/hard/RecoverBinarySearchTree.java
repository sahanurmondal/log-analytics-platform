package trees.hard;

import java.util.*;

/**
 * LeetCode 99: Recover Binary Search Tree
 * https://leetcode.com/problems/recover-binary-search-tree/
 * 
 * Companies: Google, Amazon, Microsoft
 * Frequency: High
 *
 * Description: You are given the root of a binary search tree where exactly two
 * nodes were swapped by mistake. Recover the tree without changing its
 * structure.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 1000]
 * - -2^31 <= Node.val <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Can you solve with O(1) space?
 * 2. Can you handle multiple swapped pairs?
 * 3. Can you detect if tree is already valid BST?
 */
public class RecoverBinarySearchTree {

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

    private TreeNode first = null, second = null, prev = null;

    // Approach 1: Morris Traversal (O(1) space)
    public void recoverTree(TreeNode root) {
        TreeNode current = root;
        TreeNode predecessor = null;
        TreeNode first = null, second = null, prev = null;

        while (current != null) {
            if (current.left == null) {
                // Process current node
                if (prev != null && prev.val > current.val) {
                    if (first == null)
                        first = prev;
                    second = current;
                }
                prev = current;
                current = current.right;
            } else {
                // Find predecessor
                predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    predecessor.right = current;
                    current = current.left;
                } else {
                    predecessor.right = null;
                    // Process current node
                    if (prev != null && prev.val > current.val) {
                        if (first == null)
                            first = prev;
                        second = current;
                    }
                    prev = current;
                    current = current.right;
                }
            }
        }

        // Swap the values
        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    // Follow-up 1: Inorder traversal with O(n) space
    public void recoverTreeInorder(TreeNode root) {
        first = second = prev = null;
        inorder(root);

        if (first != null && second != null) {
            int temp = first.val;
            first.val = second.val;
            second.val = temp;
        }
    }

    private void inorder(TreeNode node) {
        if (node == null)
            return;

        inorder(node.left);

        if (prev != null && prev.val > node.val) {
            if (first == null)
                first = prev;
            second = node;
        }
        prev = node;

        inorder(node.right);
    }

    // Follow-up 2: Handle multiple swapped pairs
    public List<int[]> findAllSwappedPairs(TreeNode root) {
        List<int[]> swappedPairs = new ArrayList<>();
        List<TreeNode> inorderList = new ArrayList<>();
        collectInorder(root, inorderList);

        List<Integer> sortedValues = new ArrayList<>();
        for (TreeNode node : inorderList) {
            sortedValues.add(node.val);
        }
        Collections.sort(sortedValues);

        for (int i = 0; i < inorderList.size(); i++) {
            if (inorderList.get(i).val != sortedValues.get(i)) {
                // Find the correct position
                for (int j = i + 1; j < inorderList.size(); j++) {
                    if (inorderList.get(j).val == sortedValues.get(i)) {
                        swappedPairs.add(new int[] { i, j });
                        break;
                    }
                }
            }
        }

        return swappedPairs;
    }

    // Follow-up 3: Validate BST and detect issues
    public boolean isValidBST(TreeNode root) {
        return validateBST(root, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    private boolean validateBST(TreeNode node, long min, long max) {
        if (node == null)
            return true;

        if (node.val <= min || node.val >= max)
            return false;

        return validateBST(node.left, min, node.val) &&
                validateBST(node.right, node.val, max);
    }

    public List<TreeNode> findInvalidNodes(TreeNode root) {
        List<TreeNode> invalidNodes = new ArrayList<>();
        findInvalidHelper(root, Long.MIN_VALUE, Long.MAX_VALUE, invalidNodes);
        return invalidNodes;
    }

    private void findInvalidHelper(TreeNode node, long min, long max, List<TreeNode> invalid) {
        if (node == null)
            return;

        if (node.val <= min || node.val >= max) {
            invalid.add(node);
        }

        findInvalidHelper(node.left, min, node.val, invalid);
        findInvalidHelper(node.right, node.val, max, invalid);
    }

    // Helper methods
    private void collectInorder(TreeNode node, List<TreeNode> list) {
        if (node == null)
            return;

        collectInorder(node.left, list);
        list.add(node);
        collectInorder(node.right, list);
    }

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
        RecoverBinarySearchTree solution = new RecoverBinarySearchTree();

        // Test case 1: Adjacent nodes swapped
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(2);

        System.out.println("Test 1 - Before recovery (adjacent swap):");
        solution.printTree(root1, "", true);
        System.out.println("Is valid BST: " + solution.isValidBST(root1));

        solution.recoverTree(root1);
        System.out.println("\nAfter recovery:");
        solution.printTree(root1, "", true);
        System.out.println("Is valid BST: " + solution.isValidBST(root1));

        // Test case 2: Non-adjacent nodes swapped
        TreeNode root2 = new TreeNode(3);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(4);
        root2.right.left = new TreeNode(2);

        System.out.println("\nTest 2 - Before recovery (non-adjacent swap):");
        solution.printTree(root2, "", true);

        solution.recoverTreeInorder(root2);
        System.out.println("\nAfter recovery:");
        solution.printTree(root2, "", true);

        // Test case 3: Detect invalid nodes
        TreeNode root3 = new TreeNode(5);
        root3.left = new TreeNode(8); // Invalid - should be < 5
        root3.right = new TreeNode(7);
        root3.left.left = new TreeNode(2);
        root3.left.right = new TreeNode(6);

        System.out.println("\nTest 3 - Find invalid nodes:");
        List<TreeNode> invalidNodes = solution.findInvalidNodes(root3);
        System.out.print("Invalid node values: ");
        for (TreeNode node : invalidNodes) {
            System.out.print(node.val + " ");
        }
        System.out.println();

        // Edge cases
        System.out.println("\nEdge cases:");

        // Minimum case (2 nodes)
        TreeNode minCase = new TreeNode(2);
        minCase.left = new TreeNode(3);
        System.out.println("Before fix (2 nodes): " + solution.isValidBST(minCase));
        solution.recoverTree(minCase);
        System.out.println("After fix: " + solution.isValidBST(minCase));

        // Already valid BST
        TreeNode validBST = new TreeNode(4);
        validBST.left = new TreeNode(2);
        validBST.right = new TreeNode(6);
        validBST.left.left = new TreeNode(1);
        validBST.left.right = new TreeNode(3);
        System.out.println("Valid BST check: " + solution.isValidBST(validBST));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeBST(500);
        // Swap two random nodes
        swapRandomNodes(largeTree);

        long start = System.nanoTime();
        solution.recoverTree(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree recovered in: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Is valid after recovery: " + solution.isValidBST(largeTree));
    }

    private static TreeNode buildLargeBST(int nodes) {
        if (nodes <= 0)
            return null;

        // Build a balanced BST
        return buildBalancedBST(1, nodes);
    }

    private static TreeNode buildBalancedBST(int start, int end) {
        if (start > end)
            return null;

        int mid = (start + end) / 2;
        TreeNode root = new TreeNode(mid);
        root.left = buildBalancedBST(start, mid - 1);
        root.right = buildBalancedBST(mid + 1, end);
        return root;
    }

    private static void swapRandomNodes(TreeNode root) {
        List<TreeNode> nodes = new ArrayList<>();
        collectNodes(root, nodes);

        if (nodes.size() >= 2) {
            Random rand = new Random(42);
            int i = rand.nextInt(nodes.size());
            int j = rand.nextInt(nodes.size());
            while (i == j)
                j = rand.nextInt(nodes.size());

            int temp = nodes.get(i).val;
            nodes.get(i).val = nodes.get(j).val;
            nodes.get(j).val = temp;
        }
    }

    private static void collectNodes(TreeNode node, List<TreeNode> nodes) {
        if (node == null)
            return;

        nodes.add(node);
        collectNodes(node.left, nodes);
        collectNodes(node.right, nodes);
    }
}
