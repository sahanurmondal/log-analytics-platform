package trees.hard;

import java.util.*;

/**
 * LeetCode 96: Unique Binary Search Trees
 * https://leetcode.com/problems/unique-binary-search-trees/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given an integer n, return the number of structurally unique
 * BST's which has exactly n nodes of unique values from 1 to n.
 *
 * Constraints:
 * - 1 <= n <= 19
 * 
 * Follow-up Questions:
 * 1. Can you generate all unique BSTs?
 * 2. Can you count BSTs with specific root?
 * 3. Can you optimize using mathematical formula?
 */
public class CountUniqueBSTs {

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

    // Approach 1: Dynamic Programming (O(n^2) time)
    public int numTrees(int n) {
        int[] dp = new int[n + 1];
        dp[0] = dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                dp[i] += dp[j - 1] * dp[i - j];
            }
        }
        return dp[n];
    }

    // Follow-up 1: Generate all unique BSTs
    public List<TreeNode> generateTrees(int n) {
        if (n == 0)
            return new ArrayList<>();
        return generateTreesHelper(1, n);
    }

    private List<TreeNode> generateTreesHelper(int start, int end) {
        List<TreeNode> trees = new ArrayList<>();
        if (start > end) {
            trees.add(null);
            return trees;
        }

        for (int i = start; i <= end; i++) {
            List<TreeNode> leftTrees = generateTreesHelper(start, i - 1);
            List<TreeNode> rightTrees = generateTreesHelper(i + 1, end);

            for (TreeNode left : leftTrees) {
                for (TreeNode right : rightTrees) {
                    TreeNode root = new TreeNode(i);
                    root.left = left;
                    root.right = right;
                    trees.add(root);
                }
            }
        }
        return trees;
    }

    // Follow-up 2: Count BSTs with specific root
    public int numTreesWithRoot(int n, int root) {
        if (root < 1 || root > n)
            return 0;
        int[] dp = new int[n + 1];
        dp[0] = dp[1] = 1;

        for (int i = 2; i <= n; i++) {
            for (int j = 1; j <= i; j++) {
                dp[i] += dp[j - 1] * dp[i - j];
            }
        }

        return dp[root - 1] * dp[n - root];
    }

    // Follow-up 3: Catalan number formula
    public int numTreesCatalan(int n) {
        // Catalan number: C(n) = (2n)! / ((n+1)! * n!)
        // Or C(n) = C(2n, n) / (n+1)
        long result = 1;
        for (int i = 0; i < n; i++) {
            result = result * (n + i + 1) / (i + 1);
        }
        return (int) (result / (n + 1));
    }

    // Helper: Count nodes in tree
    private int countNodes(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    // Helper: Print tree structure
    private void printTree(TreeNode root, String prefix, boolean isLast) {
        if (root == null)
            return;
        System.out.println(prefix + (isLast ? "└── " : "├── ") + root.val);
        if (root.left != null || root.right != null) {
            if (root.left != null) {
                printTree(root.left, prefix + (isLast ? "    " : "│   "), root.right == null);
            }
            if (root.right != null) {
                printTree(root.right, prefix + (isLast ? "    " : "│   "), true);
            }
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CountUniqueBSTs solution = new CountUniqueBSTs();

        // Test case 1: Basic cases
        for (int i = 1; i <= 5; i++) {
            System.out.println("n=" + i + ": " + solution.numTrees(i) + " unique BSTs");
        }

        // Test case 2: Generate all trees for small n
        System.out.println("\nTest 2 - All unique BSTs for n=3:");
        List<TreeNode> trees = solution.generateTrees(3);
        for (int i = 0; i < trees.size(); i++) {
            System.out.println("Tree " + (i + 1) + ":");
            solution.printTree(trees.get(i), "", true);
            System.out.println();
        }

        // Test case 3: BSTs with specific root
        System.out.println("Test 3 - BSTs with specific root (n=5):");
        for (int root = 1; root <= 5; root++) {
            System.out.println("Root " + root + ": " + solution.numTreesWithRoot(5, root) + " BSTs");
        }

        // Test case 4: Catalan formula
        System.out.println("\nTest 4 - Catalan formula comparison:");
        for (int i = 1; i <= 10; i++) {
            int dp = solution.numTrees(i);
            int catalan = solution.numTreesCatalan(i);
            System.out.println("n=" + i + " DP:" + dp + " Catalan:" + catalan + " Match:" + (dp == catalan));
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("n=1: " + solution.numTrees(1));
        System.out.println("n=19 (max): " + solution.numTrees(19));

        // Stress test
        System.out.println("\nStress test:");
        long start = System.nanoTime();
        int result = solution.numTreesCatalan(15);
        long end = System.nanoTime();
        System.out.println("n=15 result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }
}
