package trees.medium;

import java.util.*;

/**
 * LeetCode 530: Minimum Absolute Difference in BST
 * https://leetcode.com/problems/minimum-absolute-difference-in-bst/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a Binary Search Tree (BST), return the minimum
 * absolute difference between the values of any two different nodes in the
 * tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [2, 10^4]
 * - 0 <= Node.val <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you find the actual nodes with minimum difference?
 * 2. Can you handle duplicate values?
 * 3. Can you find k smallest differences?
 */
public class FindMinimumAbsoluteDifferenceInBST {

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

    private int minDiff = Integer.MAX_VALUE;
    private TreeNode prev = null;

    // Approach 1: Inorder traversal
    public int getMinimumDifference(TreeNode root) {
        minDiff = Integer.MAX_VALUE;
        prev = null;
        inorder(root);
        return minDiff;
    }

    private void inorder(TreeNode node) {
        if (node == null)
            return;

        inorder(node.left);

        if (prev != null) {
            minDiff = Math.min(minDiff, node.val - prev.val);
        }
        prev = node;

        inorder(node.right);
    }

    // Follow-up 1: Find the actual nodes with minimum difference
    private TreeNode node1 = null, node2 = null;

    public TreeNode[] findMinDifferenceNodes(TreeNode root) {
        minDiff = Integer.MAX_VALUE;
        prev = null;
        node1 = node2 = null;

        findMinDiffNodes(root);
        return new TreeNode[] { node1, node2 };
    }

    private void findMinDiffNodes(TreeNode node) {
        if (node == null)
            return;

        findMinDiffNodes(node.left);

        if (prev != null) {
            int diff = node.val - prev.val;
            if (diff < minDiff) {
                minDiff = diff;
                node1 = prev;
                node2 = node;
            }
        }
        prev = node;

        findMinDiffNodes(node.right);
    }

    // Follow-up 2: Handle duplicate values
    public int getMinimumDifferenceWithDuplicates(TreeNode root) {
        List<Integer> values = new ArrayList<>();
        collectValues(root, values);
        Collections.sort(values);

        int minDiff = Integer.MAX_VALUE;
        for (int i = 1; i < values.size(); i++) {
            minDiff = Math.min(minDiff, values.get(i) - values.get(i - 1));
        }

        return minDiff;
    }

    private void collectValues(TreeNode node, List<Integer> values) {
        if (node == null)
            return;

        values.add(node.val);
        collectValues(node.left, values);
        collectValues(node.right, values);
    }

    // Follow-up 3: Find k smallest differences
    public List<Integer> findKSmallestDifferences(TreeNode root, int k) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        prev = null;

        findAllDifferences(root, maxHeap, k);

        List<Integer> result = new ArrayList<>(maxHeap);
        Collections.sort(result);
        return result;
    }

    private void findAllDifferences(TreeNode node, PriorityQueue<Integer> heap, int k) {
        if (node == null)
            return;

        findAllDifferences(node.left, heap, k);

        if (prev != null) {
            int diff = node.val - prev.val;
            if (heap.size() < k) {
                heap.offer(diff);
            } else if (diff < heap.peek()) {
                heap.poll();
                heap.offer(diff);
            }
        }
        prev = node;

        findAllDifferences(node.right, heap, k);
    }

    // Additional: Get all differences between adjacent nodes
    public List<Integer> getAllAdjacentDifferences(TreeNode root) {
        List<Integer> differences = new ArrayList<>();
        prev = null;
        collectAllDifferences(root, differences);
        return differences;
    }

    private void collectAllDifferences(TreeNode node, List<Integer> differences) {
        if (node == null)
            return;

        collectAllDifferences(node.left, differences);

        if (prev != null) {
            differences.add(node.val - prev.val);
        }
        prev = node;

        collectAllDifferences(node.right, differences);
    }

    // Additional: Iterative approach
    public int getMinimumDifferenceIterative(TreeNode root) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        TreeNode prev = null;
        int minDiff = Integer.MAX_VALUE;

        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();

            if (prev != null) {
                minDiff = Math.min(minDiff, current.val - prev.val);
            }
            prev = current;
            current = current.right;
        }

        return minDiff;
    }

    // Additional: Find maximum difference
    public int getMaximumDifference(TreeNode root) {
        int[] minMax = findMinMax(root);
        return minMax[1] - minMax[0];
    }

    private int[] findMinMax(TreeNode node) {
        if (node == null)
            return new int[] { Integer.MAX_VALUE, Integer.MIN_VALUE };

        if (node.left == null && node.right == null) {
            return new int[] { node.val, node.val };
        }

        int[] left = findMinMax(node.left);
        int[] right = findMinMax(node.right);

        int min = Math.min(node.val, Math.min(left[0], right[0]));
        int max = Math.max(node.val, Math.max(left[1], right[1]));

        return new int[] { min, max };
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMinimumAbsoluteDifferenceInBST solution = new FindMinimumAbsoluteDifferenceInBST();

        // Test case 1: Basic BST
        TreeNode root1 = new TreeNode(4);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(6);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(3);

        System.out.println("Test 1 - Basic BST:");
        System.out.println("Min difference: " + solution.getMinimumDifference(root1));
        System.out.println("Min difference (iterative): " + solution.getMinimumDifferenceIterative(root1));
        System.out.println("Max difference: " + solution.getMaximumDifference(root1));

        // Test case 2: Find actual nodes
        TreeNode[] minNodes = solution.findMinDifferenceNodes(root1);
        System.out.println("\nTest 2 - Min difference nodes: " + minNodes[0].val + " and " + minNodes[1].val);

        // Test case 3: All adjacent differences
        System.out.println("\nTest 3 - All adjacent differences:");
        List<Integer> allDiffs = solution.getAllAdjacentDifferences(root1);
        System.out.println("Differences: " + allDiffs);

        // Test case 4: K smallest differences
        System.out.println("\nTest 4 - 3 smallest differences:");
        List<Integer> kSmallest = solution.findKSmallestDifferences(root1, 3);
        System.out.println("K smallest: " + kSmallest);

        // Test case 5: Tree with duplicates
        TreeNode root2 = new TreeNode(1);
        root2.right = new TreeNode(3);
        root2.right.left = new TreeNode(2);

        System.out.println("\nTest 5 - Tree with potential duplicates:");
        System.out.println("Min difference: " + solution.getMinimumDifferenceWithDuplicates(root2));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode twoNodes = new TreeNode(1);
        twoNodes.right = new TreeNode(3);
        System.out.println("Two nodes: " + solution.getMinimumDifference(twoNodes));

        TreeNode sequential = new TreeNode(0);
        sequential.right = new TreeNode(2236);
        sequential.right.left = new TreeNode(1277);
        sequential.right.right = new TreeNode(2776);
        sequential.right.left.left = new TreeNode(519);
        System.out.println("Sequential-like BST: " + solution.getMinimumDifference(sequential));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeBST(1000);

        long start = System.nanoTime();
        int result = solution.getMinimumDifference(largeTree);
        long end = System.nanoTime();
        System.out.println("Large BST result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeBST(int nodes) {
        if (nodes <= 0)
            return null;

        // Build a balanced BST with values 1, 2, 3, ..., nodes
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
}
