package trees.medium;

import java.util.*;

/**
 * LeetCode 236: Lowest Common Ancestor of a Binary Tree
 * https://leetcode.com/problems/lowest-common-ancestor-of-a-binary-tree/
 * 
 * Companies: Amazon, Google, Microsoft, Facebook
 * Frequency: Very High
 *
 * Description: Given a binary tree, find the lowest common ancestor (LCA) of
 * two given nodes.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^5]
 * - All Node.val are unique
 * - p != q
 * - p and q will exist in the tree
 * 
 * Follow-up Questions:
 * 1. Can you find LCA of multiple nodes?
 * 2. Can you preprocess for multiple queries?
 * 3. Can you handle nodes that might not exist?
 */
public class FindLCAOfBinaryTree {

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

    // Approach 1: Recursive DFS
    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null || root == p || root == q)
            return root;

        TreeNode left = lowestCommonAncestor(root.left, p, q);
        TreeNode right = lowestCommonAncestor(root.right, p, q);

        if (left != null && right != null)
            return root;
        return left != null ? left : right;
    }

    // Follow-up 1: Find LCA of multiple nodes
    public TreeNode lowestCommonAncestorMultiple(TreeNode root, List<TreeNode> nodes) {
        if (root == null || nodes.isEmpty())
            return null;
        if (nodes.contains(root))
            return root;

        TreeNode left = lowestCommonAncestorMultiple(root.left, nodes);
        TreeNode right = lowestCommonAncestorMultiple(root.right, nodes);

        if (left != null && right != null)
            return root;
        return left != null ? left : right;
    }

    // Follow-up 2: Preprocess for multiple queries using parent pointers
    public class LCAPreprocessor {
        private Map<TreeNode, TreeNode> parent = new HashMap<>();
        private Map<TreeNode, Integer> depth = new HashMap<>();

        public LCAPreprocessor(TreeNode root) {
            buildParentMap(root, null, 0);
        }

        private void buildParentMap(TreeNode node, TreeNode par, int d) {
            if (node == null)
                return;

            parent.put(node, par);
            depth.put(node, d);
            buildParentMap(node.left, node, d + 1);
            buildParentMap(node.right, node, d + 1);
        }

        public TreeNode findLCA(TreeNode p, TreeNode q) {
            // Make p and q at same depth
            while (depth.get(p) > depth.get(q))
                p = parent.get(p);
            while (depth.get(q) > depth.get(p))
                q = parent.get(q);

            // Move both up until they meet
            while (p != q) {
                p = parent.get(p);
                q = parent.get(q);
            }

            return p;
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

    // Alternative approach: Store paths and find divergence
    public TreeNode lowestCommonAncestorPaths(TreeNode root, TreeNode p, TreeNode q) {
        List<TreeNode> pathP = new ArrayList<>();
        List<TreeNode> pathQ = new ArrayList<>();

        if (!findPath(root, p, pathP) || !findPath(root, q, pathQ)) {
            return null;
        }

        TreeNode lca = null;
        int i = 0;
        while (i < pathP.size() && i < pathQ.size() && pathP.get(i) == pathQ.get(i)) {
            lca = pathP.get(i);
            i++;
        }

        return lca;
    }

    private boolean findPath(TreeNode root, TreeNode target, List<TreeNode> path) {
        if (root == null)
            return false;

        path.add(root);

        if (root == target)
            return true;

        if (findPath(root.left, target, path) || findPath(root.right, target, path)) {
            return true;
        }

        path.remove(path.size() - 1);
        return false;
    }

    // Helper: Find node by value
    private TreeNode findNode(TreeNode root, int val) {
        if (root == null)
            return null;
        if (root.val == val)
            return root;

        TreeNode left = findNode(root.left, val);
        if (left != null)
            return left;

        return findNode(root.right, val);
    }

    // Helper: Get distance between two nodes
    public int getDistance(TreeNode root, TreeNode p, TreeNode q) {
        TreeNode lca = lowestCommonAncestor(root, p, q);
        return getDistanceFromRoot(lca, p) + getDistanceFromRoot(lca, q);
    }

    private int getDistanceFromRoot(TreeNode root, TreeNode target) {
        if (root == null)
            return -1;
        if (root == target)
            return 0;

        int left = getDistanceFromRoot(root.left, target);
        if (left != -1)
            return left + 1;

        int right = getDistanceFromRoot(root.right, target);
        if (right != -1)
            return right + 1;

        return -1;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindLCAOfBinaryTree solution = new FindLCAOfBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(1);
        root1.left.left = new TreeNode(6);
        root1.left.right = new TreeNode(2);
        root1.right.left = new TreeNode(0);
        root1.right.right = new TreeNode(8);
        root1.left.right.left = new TreeNode(7);
        root1.left.right.right = new TreeNode(4);

        TreeNode p1 = root1.left; // 5
        TreeNode q1 = root1.right; // 1

        System.out.println("Test 1 - LCA of 5 and 1:");
        TreeNode lca1 = solution.lowestCommonAncestor(root1, p1, q1);
        System.out.println("Result: " + (lca1 != null ? lca1.val : "null"));

        // Test case 2: Multiple nodes LCA
        List<TreeNode> nodes = Arrays.asList(root1.left.left, root1.left.right.left, root1.left.right.right);
        System.out.println("\nTest 2 - LCA of multiple nodes [6, 7, 4]:");
        TreeNode lcaMultiple = solution.lowestCommonAncestorMultiple(root1, nodes);
        System.out.println("Result: " + (lcaMultiple != null ? lcaMultiple.val : "null"));

        // Test case 3: Preprocessor for multiple queries
        System.out.println("\nTest 3 - Preprocessor:");
        FindLCAOfBinaryTree.LCAPreprocessor preprocessor = solution.new LCAPreprocessor(root1);
        TreeNode lca3 = preprocessor.findLCA(root1.left.left, root1.left.right.right);
        System.out.println("LCA of 6 and 4: " + (lca3 != null ? lca3.val : "null"));

        // Test case 4: Safe LCA with values
        System.out.println("\nTest 4 - Safe LCA:");
        TreeNode safeLCA = solution.lowestCommonAncestorSafe(root1, 6, 4);
        System.out.println("Safe LCA of values 6 and 4: " + (safeLCA != null ? safeLCA.val : "null"));
        System.out.println("Safe LCA of non-existent nodes: " + solution.lowestCommonAncestorSafe(root1, 99, 100));

        // Test case 5: Path-based approach
        System.out.println("\nTest 5 - Path-based approach:");
        TreeNode pathLCA = solution.lowestCommonAncestorPaths(root1, p1, q1);
        System.out.println("Path-based LCA: " + (pathLCA != null ? pathLCA.val : "null"));

        // Test case 6: Distance between nodes
        System.out.println("\nTest 6 - Distance between nodes:");
        int distance = solution.getDistance(root1, root1.left.left, root1.left.right.right);
        System.out.println("Distance between 6 and 4: " + distance);

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode simple = new TreeNode(1);
        simple.left = new TreeNode(2);
        System.out.println("Parent-child LCA: " + solution.lowestCommonAncestor(simple, simple, simple.left).val);

        TreeNode linear = new TreeNode(1);
        linear.right = new TreeNode(2);
        linear.right.right = new TreeNode(3);
        System.out.println("Linear tree LCA: " + solution.lowestCommonAncestor(linear, linear, linear.right.right).val);

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);
        TreeNode node1 = solution.findNode(largeTree, 100);
        TreeNode node2 = solution.findNode(largeTree, 200);

        long start = System.nanoTime();
        TreeNode largeLCA = solution.lowestCommonAncestor(largeTree, node1, node2);
        long end = System.nanoTime();
        System.out.println("Large tree LCA: " + (largeLCA != null ? largeLCA.val : "null") + " in "
                + (end - start) / 1_000_000 + " ms");
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
                current.left = new TreeNode(count + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(count + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
