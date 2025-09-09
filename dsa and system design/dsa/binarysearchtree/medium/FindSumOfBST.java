package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * Variation: Find Sum of BST
 * Related to tree traversal problems
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: Medium
 *
 * Description:
 * Given a BST, return the sum of all node values.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively?
 * 2. What if you need sum in a specific range?
 * 3. Can you solve it without recursion stack overflow?
 */
public class FindSumOfBST {

    // Approach 1: Recursive DFS - O(n) time, O(h) space
    public int sum(TreeNode root) {
        if (root == null)
            return 0;

        return root.val + sum(root.left) + sum(root.right);
    }

    // Approach 2: Iterative using Stack (DFS) - O(n) time, O(h) space
    public int sumIterativeDFS(TreeNode root) {
        if (root == null)
            return 0;

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        stack.push(root);
        int totalSum = 0;

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            totalSum += node.val;

            if (node.right != null) {
                stack.push(node.right);
            }
            if (node.left != null) {
                stack.push(node.left);
            }
        }

        return totalSum;
    }

    // Approach 3: Iterative using Queue (BFS) - O(n) time, O(w) space where w is
    // max width
    public int sumIterativeBFS(TreeNode root) {
        if (root == null)
            return 0;

        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int totalSum = 0;

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            totalSum += node.val;

            if (node.left != null) {
                queue.offer(node.left);
            }
            if (node.right != null) {
                queue.offer(node.right);
            }
        }

        return totalSum;
    }

    // Approach 4: Morris Traversal - O(n) time, O(1) space
    public int sumMorris(TreeNode root) {
        if (root == null)
            return 0;

        TreeNode current = root;
        int totalSum = 0;

        while (current != null) {
            if (current.left == null) {
                totalSum += current.val;
                current = current.right;
            } else {
                TreeNode predecessor = current.left;
                while (predecessor.right != null && predecessor.right != current) {
                    predecessor = predecessor.right;
                }

                if (predecessor.right == null) {
                    predecessor.right = current;
                    current = current.left;
                } else {
                    predecessor.right = null;
                    totalSum += current.val;
                    current = current.right;
                }
            }
        }

        return totalSum;
    }

    // Approach 5: Sum in range [low, high] - leverages BST properties
    public int sumInRange(TreeNode root, int low, int high) {
        if (root == null)
            return 0;

        if (root.val < low) {
            // Only explore right subtree
            return sumInRange(root.right, low, high);
        } else if (root.val > high) {
            // Only explore left subtree
            return sumInRange(root.left, low, high);
        } else {
            // Root is in range, explore both subtrees
            return root.val + sumInRange(root.left, low, high) + sumInRange(root.right, low, high);
        }
    }

    // Approach 6: Sum of nodes at specific level
    public int sumAtLevel(TreeNode root, int targetLevel) {
        if (root == null)
            return 0;

        java.util.Queue<TreeNode> queue = new java.util.LinkedList<>();
        queue.offer(root);
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int levelSize = queue.size();
            int levelSum = 0;

            for (int i = 0; i < levelSize; i++) {
                TreeNode node = queue.poll();

                if (currentLevel == targetLevel) {
                    levelSum += node.val;
                }

                if (node.left != null) {
                    queue.offer(node.left);
                }
                if (node.right != null) {
                    queue.offer(node.right);
                }
            }

            if (currentLevel == targetLevel) {
                return levelSum;
            }

            currentLevel++;
        }

        return 0; // Level not found
    }

    // Helper: Build BST for testing
    public TreeNode buildBST(int[] nums) {
        if (nums.length == 0)
            return null;

        TreeNode root = new TreeNode(nums[0]);
        for (int i = 1; i < nums.length; i++) {
            insertIntoBST(root, nums[i]);
        }

        return root;
    }

    private TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }

        return root;
    }

    public static void main(String[] args) {
        FindSumOfBST solution = new FindSumOfBST();

        // Test Case 1: Normal BST
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(3);
        root1.right = new TreeNode(7);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(4);
        root1.right.right = new TreeNode(10);

        System.out.println("=== Test Case 1: Normal BST [1,3,4,5,7,10] ===");
        System.out.println("Sum (Recursive): " + solution.sum(root1)); // Expected: 30
        System.out.println("Sum (DFS): " + solution.sumIterativeDFS(root1)); // Expected: 30
        System.out.println("Sum (BFS): " + solution.sumIterativeBFS(root1)); // Expected: 30
        System.out.println("Sum (Morris): " + solution.sumMorris(root1)); // Expected: 30
        System.out.println("Sum in range [3,7]: " + solution.sumInRange(root1, 3, 7)); // Expected: 19
        System.out.println("Sum at level 2: " + solution.sumAtLevel(root1, 2)); // Expected: 15
        System.out.println();

        // Test Case 2: Single node
        TreeNode root2 = new TreeNode(42);
        System.out.println("=== Test Case 2: Single Node ===");
        System.out.println("Sum: " + solution.sum(root2)); // Expected: 42
        System.out.println("Sum in range [40,50]: " + solution.sumInRange(root2, 40, 50)); // Expected: 42
        System.out.println("Sum at level 0: " + solution.sumAtLevel(root2, 0)); // Expected: 42
        System.out.println();

        // Test Case 3: Empty tree
        TreeNode root3 = null;
        System.out.println("=== Test Case 3: Empty Tree ===");
        System.out.println("Sum: " + solution.sum(root3)); // Expected: 0
        System.out.println();

        // Test Case 4: Right skewed tree
        TreeNode root4 = new TreeNode(1);
        root4.right = new TreeNode(2);
        root4.right.right = new TreeNode(3);
        root4.right.right.right = new TreeNode(4);

        System.out.println("=== Test Case 4: Right Skewed Tree [1,2,3,4] ===");
        System.out.println("Sum (Recursive): " + solution.sum(root4)); // Expected: 10
        System.out.println("Sum (BFS): " + solution.sumIterativeBFS(root4)); // Expected: 10
        System.out.println("Sum in range [2,3]: " + solution.sumInRange(root4, 2, 3)); // Expected: 5
        System.out.println();

        // Test Case 5: BST with negative values
        TreeNode root5 = solution.buildBST(new int[] { 0, -5, 10, -8, -2, 5, 15 });
        System.out.println("=== Test Case 5: BST with Negative Values ===");
        System.out.println("Sum (Recursive): " + solution.sum(root5)); // Expected: 15
        System.out.println("Sum (DFS): " + solution.sumIterativeDFS(root5)); // Expected: 15
        System.out.println("Sum in range [-5,5]: " + solution.sumInRange(root5, -5, 5)); // Expected: -10
        System.out.println();

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        TreeNode largeBST = solution.buildBST(new int[] { 50, 25, 75, 12, 37, 62, 87, 6, 18, 31, 43, 56, 68, 81, 93 });

        long startTime, endTime;

        startTime = System.nanoTime();
        int recursiveResult = solution.sum(largeBST);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + recursiveResult + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int dfsResult = solution.sumIterativeDFS(largeBST);
        endTime = System.nanoTime();
        System.out.println("DFS: " + dfsResult + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int bfsResult = solution.sumIterativeBFS(largeBST);
        endTime = System.nanoTime();
        System.out.println("BFS: " + bfsResult + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int morrisResult = solution.sumMorris(largeBST);
        endTime = System.nanoTime();
        System.out.println("Morris: " + morrisResult + " (Time: " + (endTime - startTime) + " ns)");
    }
}
