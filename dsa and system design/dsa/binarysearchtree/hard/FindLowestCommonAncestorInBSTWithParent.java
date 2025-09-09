package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode 1650: Lowest Common Ancestor of a Binary Tree III (Hard)
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree-iii/
 * 
 * Companies: Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High
 *
 * Description:
 * Given two nodes of a binary tree with parent pointers, find their lowest
 * common ancestor (LCA). This hard variant explores multiple advanced
 * approaches.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^5].
 * - -10^9 <= Node.val <= 10^9
 * - All node values are unique
 * - p != q
 * 
 * Follow-up Questions:
 * 1. Can you solve it without calculating heights?
 * 2. What if the tree is very deep?
 * 3. Can you solve it in one pass?
 * 4. What's the optimal space complexity?
 */
public class FindLowestCommonAncestorInBSTWithParent {

    // Approach 1: Two Pointers (Linked List Intersection) - O(h) time, O(1) space
    public TreeNodeA lowestCommonAncestor(TreeNodeA p, TreeNodeA q) {
        if (p == null || q == null)
            return null;

        TreeNodeA a = p, b = q;

        // Similar to finding intersection in two linked lists
        while (a != b) {
            a = (a == null) ? q : a.parent;
            b = (b == null) ? p : b.parent;
        }

        return a;
    }

    // Approach 2: Height-Based Alignment - O(h) time, O(1) space
    public TreeNodeA lowestCommonAncestorHeight(TreeNodeA p, TreeNodeA q) {
        if (p == null || q == null)
            return null;

        // Calculate heights from root
        int heightP = getHeight(p);
        int heightQ = getHeight(q);

        // Align both nodes to same level
        while (heightP > heightQ) {
            p = p.parent;
            heightP--;
        }
        while (heightQ > heightP) {
            q = q.parent;
            heightQ--;
        }

        // Move both up until they meet
        while (p != q) {
            p = p.parent;
            q = q.parent;
        }

        return p;
    }

    private int getHeight(TreeNodeA node) {
        int height = 0;
        while (node.parent != null) {
            height++;
            node = node.parent;
        }
        return height;
    }

    // Approach 3: HashSet Path Tracking - O(h) time, O(h) space
    public TreeNodeA lowestCommonAncestorHashSet(TreeNodeA p, TreeNodeA q) {
        if (p == null || q == null)
            return null;

        java.util.Set<TreeNodeA> ancestors = new java.util.HashSet<>();

        // Store all ancestors of p
        TreeNodeA current = p;
        while (current != null) {
            ancestors.add(current);
            current = current.parent;
        }

        // Find first common ancestor in q's path
        current = q;
        while (current != null) {
            if (ancestors.contains(current)) {
                return current;
            }
            current = current.parent;
        }

        return null;
    }

    // Approach 4: BST Properties Optimization - O(h) time, O(1) space
    public TreeNodeA lowestCommonAncestorBST(TreeNodeA p, TreeNodeA q) {
        if (p == null || q == null)
            return null;

        // Use BST properties to optimize
        int minVal = Math.min(p.val, q.val);
        int maxVal = Math.max(p.val, q.val);

        // Start from either node and go up
        TreeNodeA current = p;
        while (current != null) {
            if (current.val >= minVal && current.val <= maxVal) {
                // Current node is between p and q values
                return current;
            }
            current = current.parent;
        }

        return null;
    }

    // Approach 5: Path Collection and Comparison - O(h) time, O(h) space
    public TreeNodeA lowestCommonAncestorPaths(TreeNodeA p, TreeNodeA q) {
        if (p == null || q == null)
            return null;

        java.util.List<TreeNodeA> pathP = getPathToRoot(p);
        java.util.List<TreeNodeA> pathQ = getPathToRoot(q);

        // Reverse both paths so they start from root
        java.util.Collections.reverse(pathP);
        java.util.Collections.reverse(pathQ);

        TreeNodeA lca = null;
        int minLength = Math.min(pathP.size(), pathQ.size());

        for (int i = 0; i < minLength; i++) {
            if (pathP.get(i) == pathQ.get(i)) {
                lca = pathP.get(i);
            } else {
                break;
            }
        }

        return lca;
    }

    private java.util.List<TreeNodeA> getPathToRoot(TreeNodeA node) {
        java.util.List<TreeNodeA> path = new java.util.ArrayList<>();
        while (node != null) {
            path.add(node);
            node = node.parent;
        }
        return path;
    }

    public static void main(String[] args) {
        FindLowestCommonAncestorInBSTWithParent solution = new FindLowestCommonAncestorInBSTWithParent();

        // Test case 1: Create a BST with parent pointers [6,2,8,0,4,7,9,null,null,3,5]
        TreeNodeA root = new TreeNodeA(6);
        TreeNodeA node2 = new TreeNodeA(2);
        TreeNodeA node8 = new TreeNodeA(8);
        TreeNodeA node0 = new TreeNodeA(0);
        TreeNodeA node4 = new TreeNodeA(4);
        TreeNodeA node7 = new TreeNodeA(7);
        TreeNodeA node9 = new TreeNodeA(9);
        TreeNodeA node3 = new TreeNodeA(3);
        TreeNodeA node5 = new TreeNodeA(5);

        // Build tree structure
        root.left = node2;
        root.right = node8;
        node2.parent = root;
        node8.parent = root;

        node2.left = node0;
        node2.right = node4;
        node0.parent = node2;
        node4.parent = node2;

        node8.left = node7;
        node8.right = node9;
        node7.parent = node8;
        node9.parent = node8;

        node4.left = node3;
        node4.right = node5;
        node3.parent = node4;
        node5.parent = node4;

        System.out.println("Test Case 1 (p=2, q=8):");
        System.out.println("Two Pointers: " + solution.lowestCommonAncestor(node2, node8).val);
        System.out.println("Height-based: " + solution.lowestCommonAncestorHeight(node2, node8).val);
        System.out.println("HashSet: " + solution.lowestCommonAncestorHashSet(node2, node8).val);
        System.out.println("BST Optimized: " + solution.lowestCommonAncestorBST(node2, node8).val);
        System.out.println("Paths: " + solution.lowestCommonAncestorPaths(node2, node8).val);

        System.out.println("\nTest Case 2 (p=2, q=4):");
        System.out.println("Two Pointers: " + solution.lowestCommonAncestor(node2, node4).val);
        System.out.println("Height-based: " + solution.lowestCommonAncestorHeight(node2, node4).val);
        System.out.println("HashSet: " + solution.lowestCommonAncestorHashSet(node2, node4).val);

        System.out.println("\nTest Case 3 (p=3, q=5):");
        System.out.println("Two Pointers: " + solution.lowestCommonAncestor(node3, node5).val);
        System.out.println("BST Optimized: " + solution.lowestCommonAncestorBST(node3, node5).val);

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Same node
        System.out.println("Same node (p=node2, q=node2): " + solution.lowestCommonAncestor(node2, node2).val);

        // Direct parent-child
        System.out.println("Parent-child (p=node4, q=node3): " + solution.lowestCommonAncestor(node4, node3).val);

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindLowestCommonAncestorInBSTWithParent solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a deeper tree for performance testing
        TreeNodeA deepRoot = createDeepBST(15); // Height 15
        TreeNodeA leftLeaf = getLeftmostLeaf(deepRoot);
        TreeNodeA rightLeaf = getRightmostLeaf(deepRoot);

        long startTime, endTime;

        // Test two pointers approach
        startTime = System.nanoTime();
        TreeNodeA result1 = solution.lowestCommonAncestor(leftLeaf, rightLeaf);
        endTime = System.nanoTime();
        System.out.println("Two Pointers: " + result1.val + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test height-based approach
        startTime = System.nanoTime();
        TreeNodeA result2 = solution.lowestCommonAncestorHeight(leftLeaf, rightLeaf);
        endTime = System.nanoTime();
        System.out.println("Height-based: " + result2.val + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test HashSet approach
        startTime = System.nanoTime();
        TreeNodeA result3 = solution.lowestCommonAncestorHashSet(leftLeaf, rightLeaf);
        endTime = System.nanoTime();
        System.out.println("HashSet: " + result3.val + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test BST optimized approach
        startTime = System.nanoTime();
        TreeNodeA result4 = solution.lowestCommonAncestorBST(leftLeaf, rightLeaf);
        endTime = System.nanoTime();
        System.out.println("BST Optimized: " + result4.val + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }

    private static TreeNodeA createDeepBST(int height) {
        if (height <= 0)
            return null;

        TreeNodeA root = new TreeNodeA(height * 10);
        if (height > 1) {
            root.left = createDeepBST(height - 1);
            root.right = createDeepBST(height - 1);
            if (root.left != null)
                root.left.parent = root;
            if (root.right != null)
                root.right.parent = root;

            // Adjust values to maintain BST property
            if (root.left != null)
                root.left.val = root.val - height;
            if (root.right != null)
                root.right.val = root.val + height;
        }

        return root;
    }

    private static TreeNodeA getLeftmostLeaf(TreeNodeA root) {
        while (root.left != null) {
            root = root.left;
        }
        return root;
    }

    private static TreeNodeA getRightmostLeaf(TreeNodeA root) {
        while (root.right != null) {
            root = root.right;
        }
        return root;
    }
}

class TreeNodeA {
    int val;
    TreeNodeA left, right, parent;

    TreeNodeA() {
    }

    TreeNodeA(int val) {
        this.val = val;
    }

    TreeNodeA(int val, TreeNodeA left, TreeNodeA right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}
