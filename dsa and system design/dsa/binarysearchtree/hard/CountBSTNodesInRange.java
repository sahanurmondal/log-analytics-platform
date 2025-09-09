package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode Variation: Count BST Nodes in Range (Hard)
 * Related to: LeetCode 938 (Range Sum of BST)
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: High
 *
 * Description:
 * Given a BST and a range [low, high], count the number of nodes in the range
 * [low, high] inclusive. This problem requires advanced optimizations.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^8 <= Node.val <= 10^8
 * - low <= high
 * 
 * Follow-up Questions:
 * 1. Can you solve without visiting nodes outside the range?
 * 2. What if we need to handle concurrent modifications?
 * 3. Can you solve using Morris traversal?
 * 4. What if we need range counts for multiple queries?
 */
public class CountBSTNodesInRange {

    // Approach 1: DFS with Pruning - O(k) time, O(h) space where k is nodes in
    // range
    public int countNodesInRange(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        // Prune left subtree if root value is less than low
        if (root.val < low) {
            return countNodesInRange(root.right, low, high);
        }

        // Prune right subtree if root value is greater than high
        if (root.val > high) {
            return countNodesInRange(root.left, low, high);
        }

        // Root is in range, count it and explore both subtrees
        return 1 + countNodesInRange(root.left, low, high) + countNodesInRange(root.right, low, high);
    }

    // Approach 2: Iterative with Stack - O(k) time, O(h) space
    public int countNodesInRangeIterative(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        stack.push(root);
        int count = 0;

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();

            if (node.val >= low && node.val <= high) {
                count++;
            }

            // Only explore subtrees that might contain nodes in range
            if (node.left != null && node.val > low) {
                stack.push(node.left);
            }
            if (node.right != null && node.val < high) {
                stack.push(node.right);
            }
        }

        return count;
    }

    // Approach 3: Morris Traversal (Advanced) - O(n) time, O(1) space
    public int countNodesInRangeMorris(TreeNode root, int low, int high) {
        int count = 0;
        TreeNode current = root;

        while (current != null) {
            if (current.left == null) {
                // Visit current node
                if (current.val >= low && current.val <= high) {
                    count++;
                }
                current = current.right;
            } else {
                // Find inorder predecessor
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    // Create thread
                    predecessor.right = current;
                    current = current.left;
                } else {
                    // Remove thread and visit current
                    predecessor.right = null;
                    if (current.val >= low && current.val <= high) {
                        count++;
                    }
                    current = current.right;
                }
            }
        }

        return count;
    }

    // Approach 4: Range Query with Binary Search Properties - O(h + k) time, O(h)
    // space
    public int countNodesInRangeOptimal(TreeNode root, int low, int high) {
        return countSmaller(root, high + 1) - countSmaller(root, low);
    }

    // Count nodes with values smaller than target
    private int countSmaller(TreeNode root, int target) {
        if (root == null)
            return 0;

        if (root.val >= target) {
            return countSmaller(root.left, target);
        } else {
            return 1 + countAllNodes(root.left) + countSmaller(root.right, target);
        }
    }

    // Count all nodes in subtree
    private int countAllNodes(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + countAllNodes(root.left) + countAllNodes(root.right);
    }

    public static void main(String[] args) {
        CountBSTNodesInRange solution = new CountBSTNodesInRange();

        // Test case 1: Normal BST
        TreeNode root1 = new TreeNode(10);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(15);
        root1.left.left = new TreeNode(3);
        root1.left.right = new TreeNode(7);
        root1.right.left = new TreeNode(12);
        root1.right.right = new TreeNode(20);

        System.out.println("Test Case 1 (Range [7,15]):");
        System.out.println("DFS: " + solution.countNodesInRange(root1, 7, 15));
        System.out.println("Iterative: " + solution.countNodesInRangeIterative(root1, 7, 15));
        System.out.println("Morris: " + solution.countNodesInRangeMorris(root1, 7, 15));
        System.out.println("Optimal: " + solution.countNodesInRangeOptimal(root1, 7, 15));

        // Test case 2: No nodes in range
        System.out.println("\nTest Case 2 (Range [25,30] - no nodes):");
        System.out.println("DFS: " + solution.countNodesInRange(root1, 25, 30));
        System.out.println("Iterative: " + solution.countNodesInRangeIterative(root1, 25, 30));

        // Test case 3: All nodes in range
        System.out.println("\nTest Case 3 (Range [1,25] - all nodes):");
        System.out.println("DFS: " + solution.countNodesInRange(root1, 1, 25));
        System.out.println("Morris: " + solution.countNodesInRangeMorris(root1, 1, 25));

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node
        TreeNode single = new TreeNode(10);
        System.out.println("Single node in range: " + solution.countNodesInRange(single, 5, 15));
        System.out.println("Single node out of range: " + solution.countNodesInRange(single, 20, 30));

        // Empty tree
        System.out.println("Empty tree: " + solution.countNodesInRange(null, 1, 10));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(CountBSTNodesInRange solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger BST for performance testing
        TreeNode largeRoot = createBalancedBST(1, 1000);
        int low = 300, high = 700;

        long startTime, endTime;

        // Test DFS approach
        startTime = System.nanoTime();
        int result1 = solution.countNodesInRange(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("DFS: " + result1 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test iterative approach
        startTime = System.nanoTime();
        int result2 = solution.countNodesInRangeIterative(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result2 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        int result3 = solution.countNodesInRangeMorris(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Morris: " + result3 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimal approach
        startTime = System.nanoTime();
        int result4 = solution.countNodesInRangeOptimal(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Optimal: " + result4 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");
    }

    private static TreeNode createBalancedBST(int start, int end) {
        if (start > end)
            return null;

        int mid = start + (end - start) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = createBalancedBST(start, mid - 1);
        node.right = createBalancedBST(mid + 1, end);
        return node;
    }
}
