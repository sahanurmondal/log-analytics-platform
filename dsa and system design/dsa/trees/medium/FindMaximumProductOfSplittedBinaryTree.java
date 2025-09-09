package trees.medium;

import java.util.*;

/**
 * LeetCode 1339: Maximum Product of Splitted Binary Tree
 * https://leetcode.com/problems/maximum-product-of-splitted-binary-tree/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, split the tree into two
 * subtrees by removing one edge such that the product of the sums is maximized.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 5 * 10^4]
 * - 1 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you find the actual edge to remove?
 * 2. Can you handle multiple optimal splits?
 * 3. Can you minimize the difference instead?
 */
public class FindMaximumProductOfSplittedBinaryTree {

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

    private static final int MOD = 1000000007;
    private long maxProduct = 0;
    private long totalSum = 0;

    // Approach 1: Two-pass DFS
    public int maxProduct(TreeNode root) {
        maxProduct = 0;
        totalSum = calculateSum(root);
        findMaxProduct(root);
        return (int) (maxProduct % MOD);
    }

    private long calculateSum(TreeNode node) {
        if (node == null)
            return 0;
        return node.val + calculateSum(node.left) + calculateSum(node.right);
    }

    private long findMaxProduct(TreeNode node) {
        if (node == null)
            return 0;

        long leftSum = findMaxProduct(node.left);
        long rightSum = findMaxProduct(node.right);
        long subtreeSum = node.val + leftSum + rightSum;

        // Check if removing edge to left child gives better product
        if (node.left != null) {
            long product = leftSum * (totalSum - leftSum);
            maxProduct = Math.max(maxProduct, product);
        }

        // Check if removing edge to right child gives better product
        if (node.right != null) {
            long product = rightSum * (totalSum - rightSum);
            maxProduct = Math.max(maxProduct, product);
        }

        return subtreeSum;
    }

    // Follow-up 1: Find the actual edge to remove
    private TreeNode optimalParent = null;
    private boolean removeLeftChild = false;

    public TreeNode[] findOptimalSplit(TreeNode root) {
        maxProduct = 0;
        totalSum = calculateSum(root);
        optimalParent = null;
        removeLeftChild = false;

        findOptimalSplitHelper(root);

        if (optimalParent == null)
            return new TreeNode[] { root, null };

        TreeNode removedSubtree = removeLeftChild ? optimalParent.left : optimalParent.right;
        return new TreeNode[] { root, removedSubtree };
    }

    private long findOptimalSplitHelper(TreeNode node) {
        if (node == null)
            return 0;

        long leftSum = findOptimalSplitHelper(node.left);
        long rightSum = findOptimalSplitHelper(node.right);
        long subtreeSum = node.val + leftSum + rightSum;

        if (node.left != null) {
            long product = leftSum * (totalSum - leftSum);
            if (product > maxProduct) {
                maxProduct = product;
                optimalParent = node;
                removeLeftChild = true;
            }
        }

        if (node.right != null) {
            long product = rightSum * (totalSum - rightSum);
            if (product > maxProduct) {
                maxProduct = product;
                optimalParent = node;
                removeLeftChild = false;
            }
        }

        return subtreeSum;
    }

    // Follow-up 2: Handle multiple optimal splits
    public List<TreeNode[]> findAllOptimalSplits(TreeNode root) {
        List<TreeNode[]> allSplits = new ArrayList<>();
        maxProduct = 0;
        totalSum = calculateSum(root);

        // First pass to find max product
        findMaxProduct(root);

        // Second pass to collect all splits with max product
        collectOptimalSplits(root, allSplits);

        return allSplits;
    }

    private long collectOptimalSplits(TreeNode node, List<TreeNode[]> allSplits) {
        if (node == null)
            return 0;

        long leftSum = collectOptimalSplits(node.left, allSplits);
        long rightSum = collectOptimalSplits(node.right, allSplits);
        long subtreeSum = node.val + leftSum + rightSum;

        if (node.left != null) {
            long product = leftSum * (totalSum - leftSum);
            if (product == maxProduct) {
                allSplits.add(new TreeNode[] { node, node.left });
            }
        }

        if (node.right != null) {
            long product = rightSum * (totalSum - rightSum);
            if (product == maxProduct) {
                allSplits.add(new TreeNode[] { node, node.right });
            }
        }

        return subtreeSum;
    }

    // Follow-up 3: Minimize the difference instead
    private long minDifference = Long.MAX_VALUE;

    public int minDifferenceSplit(TreeNode root) {
        minDifference = Long.MAX_VALUE;
        totalSum = calculateSum(root);
        findMinDifference(root);
        return (int) minDifference;
    }

    private long findMinDifference(TreeNode node) {
        if (node == null)
            return 0;

        long leftSum = findMinDifference(node.left);
        long rightSum = findMinDifference(node.right);
        long subtreeSum = node.val + leftSum + rightSum;

        if (node.left != null) {
            long diff = Math.abs(leftSum - (totalSum - leftSum));
            minDifference = Math.min(minDifference, diff);
        }

        if (node.right != null) {
            long diff = Math.abs(rightSum - (totalSum - rightSum));
            minDifference = Math.min(minDifference, diff);
        }

        return subtreeSum;
    }

    // Helper: Calculate subtree sums
    public Map<TreeNode, Long> getSubtreeSums(TreeNode root) {
        Map<TreeNode, Long> sums = new HashMap<>();
        calculateSubtreeSums(root, sums);
        return sums;
    }

    private long calculateSubtreeSums(TreeNode node, Map<TreeNode, Long> sums) {
        if (node == null)
            return 0;

        long leftSum = calculateSubtreeSums(node.left, sums);
        long rightSum = calculateSubtreeSums(node.right, sums);
        long subtreeSum = node.val + leftSum + rightSum;

        sums.put(node, subtreeSum);
        return subtreeSum;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumProductOfSplittedBinaryTree solution = new FindMaximumProductOfSplittedBinaryTree();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);
        root1.right.left = new TreeNode(6);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Max product: " + solution.maxProduct(root1));

        TreeNode[] optimalSplit = solution.findOptimalSplit(root1);
        System.out.println("Optimal split found: " + (optimalSplit[1] != null ? "Yes" : "No"));
        if (optimalSplit[1] != null) {
            System.out.println("Removed subtree root: " + optimalSplit[1].val);
        }

        // Test case 2: All optimal splits
        System.out.println("\nTest 2 - All optimal splits:");
        List<TreeNode[]> allSplits = solution.findAllOptimalSplits(root1);
        System.out.println("Number of optimal splits: " + allSplits.size());
        for (int i = 0; i < allSplits.size(); i++) {
            System.out.println("Split " + (i + 1) + ": Remove subtree rooted at " + allSplits.get(i)[1].val);
        }

        // Test case 3: Minimize difference
        System.out.println("\nTest 3 - Minimize difference:");
        System.out.println("Min difference: " + solution.minDifferenceSplit(root1));

        // Test case 4: Subtree sums
        System.out.println("\nTest 4 - Subtree sums:");
        Map<TreeNode, Long> sums = solution.getSubtreeSums(root1);
        for (Map.Entry<TreeNode, Long> entry : sums.entrySet()) {
            System.out.println("Node " + entry.getKey().val + ": sum = " + entry.getValue());
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode simple = new TreeNode(1);
        simple.left = new TreeNode(1);
        System.out.println("Two node tree: " + solution.maxProduct(simple));

        TreeNode linear = new TreeNode(2);
        linear.left = new TreeNode(3);
        linear.left.left = new TreeNode(4);
        linear.left.left.left = new TreeNode(5);
        System.out.println("Linear tree: " + solution.maxProduct(linear));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.maxProduct(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
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
