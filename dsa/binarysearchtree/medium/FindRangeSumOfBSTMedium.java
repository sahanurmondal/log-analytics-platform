package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * LeetCode 938: Range Sum of BST (Medium Variant)
 * https://leetcode.com/problems/range-sum-of-bst/
 * 
 * Companies: Facebook, Amazon, Google, Microsoft
 * Frequency: High
 *
 * Description:
 * Given the root node of a binary search tree and two integers low and high,
 * return the sum of values of all nodes with a value in the inclusive range
 * [low, high].
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 2 * 10^4].
 * - 1 <= Node.val <= 10^5
 * - 1 <= low <= high <= 10^5
 * - All Node.val are unique.
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively?
 * 2. How would you optimize for very large trees?
 * 3. What if the tree had duplicate values?
 */
public class FindRangeSumOfBSTMedium {

    // Approach 1: Recursive DFS - O(n) time, O(h) space
    public int rangeSumBST(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        // If current node is outside range, prune entire subtree
        if (root.val < low) {
            return rangeSumBST(root.right, low, high);
        }
        if (root.val > high) {
            return rangeSumBST(root.left, low, high);
        }

        // Current node is in range, include it and check both subtrees
        return root.val + rangeSumBST(root.left, low, high) + rangeSumBST(root.right, low, high);
    }

    // Approach 2: Iterative DFS using Stack - O(n) time, O(h) space
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

            // Only add left child if current value > low
            if (node.left != null && node.val > low) {
                stack.push(node.left);
            }

            // Only add right child if current value < high
            if (node.right != null && node.val < high) {
                stack.push(node.right);
            }
        }

        return sum;
    }

    // Approach 3: BFS using Queue - O(n) time, O(w) space where w is max width
    public int rangeSumBSTBFS(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int sum = 0;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node.val >= low && node.val <= high) {
                sum += node.val;
            }

            // Prune left subtree if current value <= low
            if (node.left != null && node.val > low) {
                queue.offer(node.left);
            }

            // Prune right subtree if current value >= high
            if (node.right != null && node.val < high) {
                queue.offer(node.right);
            }
        }

        return sum;
    }

    // Approach 4: Morris Traversal - O(n) time, O(1) space
    public int rangeSumBSTMorris(TreeNode root, int low, int high) {
        int sum = 0;
        TreeNode current = root;

        while (current != null) {
            if (current.left == null) {
                // Process current node
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
                    // Make current as right child of predecessor
                    predecessor.right = current;
                    current = current.left;
                } else {
                    // Revert changes
                    predecessor.right = null;
                    // Process current node
                    if (current.val >= low && current.val <= high) {
                        sum += current.val;
                    }
                    current = current.right;
                }
            }
        }

        return sum;
    }

    // Approach 5: Optimized with Early Termination - O(k) time where k is nodes in
    // range
    public int rangeSumBSTOptimized(TreeNode root, int low, int high) {
        return dfsOptimized(root, low, high);
    }

    private int dfsOptimized(TreeNode node, int low, int high) {
        if (node == null)
            return 0;

        int sum = 0;

        // If current node is in range, add it
        if (node.val >= low && node.val <= high) {
            sum += node.val;
        }

        // Only traverse left if there might be valid nodes
        if (node.val > low) {
            sum += dfsOptimized(node.left, low, high);
        }

        // Only traverse right if there might be valid nodes
        if (node.val < high) {
            sum += dfsOptimized(node.right, low, high);
        }

        return sum;
    }

    // Helper method for inorder traversal to verify BST property
    public java.util.List<Integer> inorderTraversal(TreeNode root) {
        java.util.List<Integer> result = new java.util.ArrayList<>();
        inorderHelper(root, result);
        return result;
    }

    private void inorderHelper(TreeNode node, java.util.List<Integer> result) {
        if (node == null)
            return;
        inorderHelper(node.left, result);
        result.add(node.val);
        inorderHelper(node.right, result);
    }

    public static void main(String[] args) {
        FindRangeSumOfBSTMedium solution = new FindRangeSumOfBSTMedium();

        // Test case 1: Balanced BST
        TreeNode root1 = new TreeNode(10);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(15);
        root1.left.left = new TreeNode(3);
        root1.left.right = new TreeNode(7);
        root1.right.right = new TreeNode(18);

        int low1 = 7, high1 = 15;
        System.out.println("Test Case 1: BST with range [" + low1 + ", " + high1 + "]");
        System.out.println("Inorder: " + solution.inorderTraversal(root1));
        System.out.println("Recursive: " + solution.rangeSumBST(root1, low1, high1));
        System.out.println("Iterative: " + solution.rangeSumBSTIterative(root1, low1, high1));
        System.out.println("BFS: " + solution.rangeSumBSTBFS(root1, low1, high1));
        System.out.println("Morris: " + solution.rangeSumBSTMorris(root1, low1, high1));
        System.out.println("Optimized: " + solution.rangeSumBSTOptimized(root1, low1, high1));

        // Test case 2: Right skewed tree
        TreeNode root2 = new TreeNode(10);
        root2.right = new TreeNode(15);
        root2.right.left = new TreeNode(13);
        root2.right.right = new TreeNode(20);
        root2.right.right.left = new TreeNode(17);

        int low2 = 13, high2 = 17;
        System.out.println("\nTest Case 2: Right skewed BST with range [" + low2 + ", " + high2 + "]");
        System.out.println("Inorder: " + solution.inorderTraversal(root2));
        System.out.println("Recursive: " + solution.rangeSumBST(root2, low2, high2));
        System.out.println("Iterative: " + solution.rangeSumBSTIterative(root2, low2, high2));

        // Test case 3: Single node
        TreeNode root3 = new TreeNode(5);
        int low3 = 1, high3 = 10;
        System.out.println("\nTest Case 3: Single node with range [" + low3 + ", " + high3 + "]");
        System.out.println("Result: " + solution.rangeSumBST(root3, low3, high3));

        // Test case 4: No nodes in range
        TreeNode root4 = new TreeNode(10);
        root4.left = new TreeNode(5);
        root4.right = new TreeNode(15);
        int low4 = 20, high4 = 25;
        System.out.println("\nTest Case 4: No nodes in range [" + low4 + ", " + high4 + "]");
        System.out.println("Result: " + solution.rangeSumBST(root4, low4, high4));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindRangeSumOfBSTMedium solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger BST
        TreeNode largeRoot = createLargeBST();
        int low = 25, high = 75;

        long startTime, endTime;

        // Test recursive approach
        startTime = System.nanoTime();
        int result1 = solution.rangeSumBST(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + result1 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test iterative approach
        startTime = System.nanoTime();
        int result2 = solution.rangeSumBSTIterative(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result2 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test BFS approach
        startTime = System.nanoTime();
        int result3 = solution.rangeSumBSTBFS(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("BFS: " + result3 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        int result4 = solution.rangeSumBSTMorris(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Morris: " + result4 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimized approach
        startTime = System.nanoTime();
        int result5 = solution.rangeSumBSTOptimized(largeRoot, low, high);
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result5 + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Verify all approaches produce the same result
        System.out.println("\nVerification:");
        boolean allSame = (result1 == result2 && result2 == result3 &&
                result3 == result4 && result4 == result5);
        System.out.println("All approaches produce same result: " + allSame);
    }

    private static TreeNode createLargeBST() {
        TreeNode root = new TreeNode(50);

        // Left subtree
        root.left = new TreeNode(30);
        root.left.left = new TreeNode(20);
        root.left.right = new TreeNode(40);
        root.left.left.left = new TreeNode(10);
        root.left.left.right = new TreeNode(25);
        root.left.right.left = new TreeNode(35);
        root.left.right.right = new TreeNode(45);

        // Right subtree
        root.right = new TreeNode(70);
        root.right.left = new TreeNode(60);
        root.right.right = new TreeNode(80);
        root.right.left.left = new TreeNode(55);
        root.right.left.right = new TreeNode(65);
        root.right.right.left = new TreeNode(75);
        root.right.right.right = new TreeNode(85);

        return root;
    }
}