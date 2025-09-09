package binarysearchtree.medium;

import java.util.*;

/**
 * LeetCode 95: Unique Binary Search Trees II
 * https://leetcode.com/problems/unique-binary-search-trees-ii/
 *
 * Description: Given an integer n, return all the structurally unique BST's
 * which has exactly n nodes of unique values from 1 to n.
 * 
 * Constraints:
 * - 1 <= n <= 8
 *
 * Follow-up:
 * - Can you optimize with memoization?
 * 
 * Time Complexity: O(G(n)) where G(n) is nth Catalan number
 * Space Complexity: O(G(n))
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class UniqueBinarySearchTreesII {

    static class TreeNode {
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

    // Main optimized solution - Recursive
    public List<TreeNode> generateTrees(int n) {
        if (n == 0)
            return new ArrayList<>();
        return generateTrees(1, n);
    }

    private List<TreeNode> generateTrees(int start, int end) {
        List<TreeNode> result = new ArrayList<>();

        if (start > end) {
            result.add(null);
            return result;
        }

        for (int i = start; i <= end; i++) {
            List<TreeNode> leftSubtrees = generateTrees(start, i - 1);
            List<TreeNode> rightSubtrees = generateTrees(i + 1, end);

            for (TreeNode left : leftSubtrees) {
                for (TreeNode right : rightSubtrees) {
                    TreeNode root = new TreeNode(i);
                    root.left = left;
                    root.right = right;
                    result.add(root);
                }
            }
        }

        return result;
    }

    // Alternative solution - With memoization
    private Map<String, List<TreeNode>> memo = new HashMap<>();

    public List<TreeNode> generateTreesMemo(int n) {
        if (n == 0)
            return new ArrayList<>();
        return generateTreesMemo(1, n);
    }

    private List<TreeNode> generateTreesMemo(int start, int end) {
        String key = start + "," + end;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        List<TreeNode> result = new ArrayList<>();

        if (start > end) {
            result.add(null);
            memo.put(key, result);
            return result;
        }

        for (int i = start; i <= end; i++) {
            List<TreeNode> leftSubtrees = generateTreesMemo(start, i - 1);
            List<TreeNode> rightSubtrees = generateTreesMemo(i + 1, end);

            for (TreeNode left : leftSubtrees) {
                for (TreeNode right : rightSubtrees) {
                    TreeNode root = new TreeNode(i);
                    root.left = left;
                    root.right = right;
                    result.add(root);
                }
            }
        }

        memo.put(key, result);
        return result;
    }

    public static void main(String[] args) {
        UniqueBinarySearchTreesII solution = new UniqueBinarySearchTreesII();

        List<TreeNode> trees3 = solution.generateTrees(3);
        System.out.println("n=3: " + trees3.size() + " trees"); // Expected: 5

        List<TreeNode> trees1 = solution.generateTrees(1);
        System.out.println("n=1: " + trees1.size() + " trees"); // Expected: 1
    }
}
