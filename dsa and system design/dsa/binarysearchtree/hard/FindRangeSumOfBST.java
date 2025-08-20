package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode 938: Range Sum of BST (Hard Variant)
 * https://leetcode.com/problems/range-sum-of-bst/
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: High
 *
 * Description:
 * Given a BST and a range [low, high], return the sum of values of all nodes
 * with value in the range [low, high] inclusive. This hard variant includes
 * additional optimizations and advanced techniques.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - 1 <= low <= high <= 10^4
 * - All Node.val are unique
 * 
 * Follow-up Questions:
 * 1. Can you avoid visiting nodes outside the range completely?
 * 2. What if the tree is threaded BST?
 * 3. Can you solve using Morris traversal?
 * 4. What if we need to handle concurrent modifications?
 */
public class FindRangeSumOfBST {

    // Approach 1: Optimized DFS with pruning - O(k) time, O(h) space where k is
    // nodes in range
    public int rangeSumBST(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        // Prune left subtree if root is at the lower bound
        if (root.val < low) {
            return rangeSumBST(root.right, low, high);
        }

        // Prune right subtree if root is at the upper bound
        if (root.val > high) {
            return rangeSumBST(root.left, low, high);
        }

        // Root is in range, include it and explore both subtrees
        return root.val + rangeSumBST(root.left, low, high) + rangeSumBST(root.right, low, high);
    }

    // Approach 2: Morris Traversal (Advanced) - O(n) time, O(1) space
    public int rangeSumBSTMorris(TreeNode root, int low, int high) {
        int sum = 0;
        TreeNode current = root;

        while (current != null) {
            if (current.left == null) {
                // Visit current node
                if (current.val >= low && current.val <= high) {
                    sum += current.val;
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
                        sum += current.val;
                    }
                    current = current.right;
                }
            }
        }

        return sum;
    }

    // Approach 3: Iterative with Stack and Pruning - O(k) time, O(h) space
    public int rangeSumBSTIterative(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        stack.push(root);
        int sum = 0;

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();

            if (node.val >= low && node.val <= high) {
                sum += node.val;
            }

            // Only add children that might contain valid nodes
            if (node.left != null && node.val > low) {
                stack.push(node.left);
            }
            if (node.right != null && node.val < high) {
                stack.push(node.right);
            }
        }

        return sum;
    }

    // Approach 4: Range Query with Memoization - O(n) time, O(n) space
    private java.util.Map<String, Integer> memo = new java.util.HashMap<>();

    public int rangeSumBSTMemo(TreeNode root, int low, int high) {
        memo.clear();
        return rangeSumHelper(root, low, high);
    }

    private int rangeSumHelper(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        String key = root.val + "," + low + "," + high;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        int result = 0;
        if (root.val < low) {
            result = rangeSumHelper(root.right, low, high);
        } else if (root.val > high) {
            result = rangeSumHelper(root.left, low, high);
        } else {
            result = root.val + rangeSumHelper(root.left, low, high) + rangeSumHelper(root.right, low, high);
        }

        memo.put(key, result);
        return result;
    }

    public static void main(String[] args) {
        FindRangeSumOfBST solution = new FindRangeSumOfBST();

        // Test case 1: [10,5,15,3,7,null,18], L = 7, R = 15
        TreeNode root1 = new TreeNode(10);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(15);
        root1.left.left = new TreeNode(3);
        root1.left.right = new TreeNode(7);
        root1.right.right = new TreeNode(18);

        System.out.println("Test Case 1 (Range [7,15]):");
        System.out.println("DFS Pruning: " + solution.rangeSumBST(root1, 7, 15));
        System.out.println("Morris: " + solution.rangeSumBSTMorris(root1, 7, 15));
        System.out.println("Iterative: " + solution.rangeSumBSTIterative(root1, 7, 15));
        System.out.println("Memoized: " + solution.rangeSumBSTMemo(root1, 7, 15));

        // Test case 2: [10,5,15,3,7,13,18,1,null,6], L = 6, R = 10
        TreeNode root2 = new TreeNode(10);
        root2.left = new TreeNode(5);
        root2.right = new TreeNode(15);
        root2.left.left = new TreeNode(3);
        root2.left.right = new TreeNode(7);
        root2.right.left = new TreeNode(13);
        root2.right.right = new TreeNode(18);
        root2.left.left.left = new TreeNode(1);
        root2.left.right.left = new TreeNode(6);

        System.out.println("\nTest Case 2 (Range [6,10]):");
        System.out.println("DFS Pruning: " + solution.rangeSumBST(root2, 6, 10));
        System.out.println("Morris: " + solution.rangeSumBSTMorris(root2, 6, 10));
        System.out.println("Iterative: " + solution.rangeSumBSTIterative(root2, 6, 10));
        System.out.println("Memoized: " + solution.rangeSumBSTMemo(root2, 6, 10));

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node in range
        TreeNode single = new TreeNode(5);
        System.out.println("Single node in range: " + solution.rangeSumBST(single, 3, 7));
        System.out.println("Single node out of range: " + solution.rangeSumBST(single, 6, 10));

        // Empty tree
        System.out.println("Empty tree: " + solution.rangeSumBST(null, 1, 10));

        // Entire tree in range
        System.out.println("Entire tree in range: " + solution.rangeSumBST(root1, 1, 20));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindRangeSumOfBST solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger BST for performance testing
        TreeNode largeRoot = createBalancedBST(1, 1000);
        int low = 300, high = 700;

        long startTime, endTime;

        // Test DFS approach
        startTime = System.nanoTime();
        int result1 = solution.rangeSumBST(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("DFS Pruning: " + result1 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        int result2 = solution.rangeSumBSTMorris(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Morris: " + result2 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test iterative approach
        startTime = System.nanoTime();
        int result3 = solution.rangeSumBSTIterative(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result3 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test memoized approach
        startTime = System.nanoTime();
        int result4 = solution.rangeSumBSTMemo(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Memoized: " + result4 + " (Time: " + (endTime - startTime) / 1_000_000.0 + " ms)");
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
