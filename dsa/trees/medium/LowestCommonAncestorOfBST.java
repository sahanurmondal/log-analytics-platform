package trees.medium;

import java.util.*;

/**
 * LeetCode 235: Lowest Common Ancestor of a Binary Search Tree
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-search-tree/
 * 
 * Companies: Amazon, Google, Microsoft, Facebook
 * Frequency: Very High
 *
 * Description: Given a binary search tree (BST), find the lowest common
 * ancestor (LCA) of two given nodes in the BST.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^5]
 * - All Node.val are unique
 * - p != q
 * - p and q will exist in the tree
 * 
 * Follow-up Questions:
 * 1. Can you solve iteratively?
 * 2. Can you find LCA of multiple nodes?
 * 3. Can you handle the case where nodes might not exist?
 */
public class LowestCommonAncestorOfBST {

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

    // Approach 1: Recursive leveraging BST property
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null)
            return null;

        // If both nodes are in left subtree
        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }

        // If both nodes are in right subtree
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        }

        // If nodes are on different sides, current root is LCA
        return root;
    }

    // Follow-up 1: Iterative approach
    public TreeNode lowestCommonAncestorIterative(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode current = root;

        while (current != null) {
            if (p.val < current.val && q.val < current.val) {
                current = current.left;
            } else if (p.val > current.val && q.val > current.val) {
                current = current.right;
            } else {
                return current;
            }
        }

        return null;
    }

    // Follow-up 2: LCA of multiple nodes
    public TreeNode lowestCommonAncestorMultiple(TreeNode root, List<TreeNode> nodes) {
        if (nodes.isEmpty())
            return null;
        if (nodes.size() == 1)
            return nodes.get(0);

        int minVal = nodes.stream().mapToInt(n -> n.val).min().orElse(Integer.MAX_VALUE);
        int maxVal = nodes.stream().mapToInt(n -> n.val).max().orElse(Integer.MIN_VALUE);

        return findLCAInRange(root, minVal, maxVal);
    }

    private TreeNode findLCAInRange(TreeNode root, int minVal, int maxVal) {
        if (root == null)
            return null;

        if (root.val < minVal) {
            return findLCAInRange(root.right, minVal, maxVal);
        } else if (root.val > maxVal) {
            return findLCAInRange(root.left, minVal, maxVal);
        } else {
            return root;
        }
    }

    // Follow-up 3: Handle nodes that might not exist
    public TreeNode lowestCommonAncestorSafe(TreeNode root, int val1, int val2) {
        TreeNode node1 = findNode(root, val1);
        TreeNode node2 = findNode(root, val2);

        if (node1 == null || node2 == null)
            return null;

        return lowestCommonAncestor(root, node1, node2);
    }

    private TreeNode findNode(TreeNode root, int val) {
        if (root == null)
            return null;

        if (root.val == val)
            return root;
        else if (val < root.val)
            return findNode(root.left, val);
        else
            return findNode(root.right, val);
    }

    // Additional: Find distance between two nodes
    public int distanceBetweenNodes(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode lca = lowestCommonAncestor(root, p, q);
        return distanceFromRoot(lca, p) + distanceFromRoot(lca, q);
    }

    private int distanceFromRoot(TreeNode root, TreeNode target) {
        if (root == null)
            return -1;
        if (root.val == target.val)
            return 0;

        if (target.val < root.val) {
            int leftDist = distanceFromRoot(root.left, target);
            return leftDist == -1 ? -1 : leftDist + 1;
        } else {
            int rightDist = distanceFromRoot(root.right, target);
            return rightDist == -1 ? -1 : rightDist + 1;
        }
    }

    // Additional: Find all ancestors of a node
    public List<TreeNode> findAllAncestors(TreeNode root, TreeNode target) {
        List<TreeNode> ancestors = new ArrayList<>();
        findAncestorsHelper(root, target, ancestors);
        return ancestors;
    }

    private boolean findAncestorsHelper(TreeNode root, TreeNode target, List<TreeNode> ancestors) {
        if (root == null)
            return false;

        if (root.val == target.val)
            return true;

        ancestors.add(root);

        if (target.val < root.val) {
            if (findAncestorsHelper(root.left, target, ancestors))
                return true;
        } else {
            if (findAncestorsHelper(root.right, target, ancestors))
                return true;
        }

        ancestors.remove(ancestors.size() - 1);
        return false;
    }

    // Additional: Check if one node is ancestor of another
    public boolean isAncestor(TreeNode root, TreeNode ancestor, TreeNode descendant) {
        if (ancestor.val == descendant.val)
            return false;

        TreeNode lca = lowestCommonAncestor(root, ancestor, descendant);
        return lca.val == ancestor.val;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        LowestCommonAncestorOfBST solution = new LowestCommonAncestorOfBST();

        // Build test BST
        TreeNode root = new TreeNode(6);
        root.left = new TreeNode(2);
        root.right = new TreeNode(8);
        root.left.left = new TreeNode(0);
        root.left.right = new TreeNode(4);
        root.right.left = new TreeNode(7);
        root.right.right = new TreeNode(9);
        root.left.right.left = new TreeNode(3);
        root.left.right.right = new TreeNode(5);

        TreeNode p = root.left; // Node 2
        TreeNode q = root.right; // Node 8

        System.out.println("Test 1 - Basic LCA:");
        TreeNode lca1 = solution.lowestCommonAncestor(root, p, q);
        System.out.println("LCA of 2 and 8: " + lca1.val);

        System.out.println("LCA (iterative): " + solution.lowestCommonAncestorIterative(root, p, q).val);

        // Test case 2: LCA of nodes in same subtree
        TreeNode p2 = root.left; // Node 2
        TreeNode q2 = root.left.right; // Node 4
        TreeNode lca2 = solution.lowestCommonAncestor(root, p2, q2);
        System.out.println("\nTest 2 - LCA of 2 and 4: " + lca2.val);

        // Test case 3: Multiple nodes LCA
        List<TreeNode> nodes = Arrays.asList(
                root.left.left, // 0
                root.left.right.left, // 3
                root.left.right.right // 5
        );
        TreeNode lcaMultiple = solution.lowestCommonAncestorMultiple(root, nodes);
        System.out.println("\nTest 3 - LCA of [0,3,5]: " + lcaMultiple.val);

        // Test case 4: Safe LCA with values
        System.out.println("\nTest 4 - Safe LCA:");
        TreeNode safeLCA = solution.lowestCommonAncestorSafe(root, 3, 5);
        System.out.println("Safe LCA of 3 and 5: " + (safeLCA != null ? safeLCA.val : "null"));
        System.out.println("Safe LCA of non-existent nodes: " + solution.lowestCommonAncestorSafe(root, 99, 100));

        // Test case 5: Distance between nodes
        System.out.println("\nTest 5 - Distance between nodes:");
        int distance = solution.distanceBetweenNodes(root, root.left.left, root.left.right.right);
        System.out.println("Distance between 0 and 5: " + distance);

        // Test case 6: All ancestors
        System.out.println("\nTest 6 - All ancestors of node 3:");
        List<TreeNode> ancestors = solution.findAllAncestors(root, root.left.right.left);
        System.out.print("Ancestors: ");
        for (TreeNode ancestor : ancestors) {
            System.out.print(ancestor.val + " ");
        }
        System.out.println();

        // Test case 7: Ancestor check
        System.out.println("\nTest 7 - Ancestor checks:");
        System.out.println("Is 2 ancestor of 3: " + solution.isAncestor(root, root.left, root.left.right.left));
        System.out.println("Is 3 ancestor of 2: " + solution.isAncestor(root, root.left.right.left, root.left));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode simple = new TreeNode(1);
        simple.right = new TreeNode(2);
        System.out.println("Simple tree LCA: " + solution.lowestCommonAncestor(simple, simple, simple.right).val);

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeBST(1000);
        TreeNode node1 = findNodeWithValue(largeTree, 100);
        TreeNode node2 = findNodeWithValue(largeTree, 900);

        long start = System.nanoTime();
        TreeNode result = solution.lowestCommonAncestor(largeTree, node1, node2);
        long end = System.nanoTime();
        System.out.println("Large BST LCA: " + result.val + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeBST(int nodes) {
        TreeNode root = new TreeNode(500);

        for (int i = 1; i <= nodes; i++) {
            if (i != 500) {
                insertIntoBST(root, i);
            }
        }

        return root;
    }

    private static TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null)
            return new TreeNode(val);

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }

        return root;
    }

    private static TreeNode findNodeWithValue(TreeNode root, int val) {
        if (root == null)
            return null;
        if (root.val == val)
            return root;

        if (val < root.val)
            return findNodeWithValue(root.left, val);
        else
            return findNodeWithValue(root.right, val);
    }
}
