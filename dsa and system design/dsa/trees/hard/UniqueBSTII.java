package trees.hard;

import trees.TreeNode;
import java.util.*;

/**
 * LeetCode 95: Unique Binary Search Trees II
 * https://leetcode.com/problems/unique-binary-search-trees-ii/
 *
 * Description:
 * Given n, generate all structurally unique BSTs that store values 1...n.
 *
 * Constraints:
 * - 1 <= n <= 8
 *
 * Follow-up:
 * - Can you solve it recursively?
 * - Can you optimize with memoization?
 * 
 * Time Complexity: O(G(n)) where G(n) is nth Catalan number
 * Space Complexity: O(G(n))
 * 
 * Algorithm:
 * 1. Recursive: For each root, combine left and right subtrees
 * 2. Memoization: Cache results for ranges
 * 3. Dynamic Programming: Build from smaller ranges
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class UniqueBSTII {

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

    // Helper method to print tree structure
    private void printTree(TreeNode root) {
        if (root == null) {
            System.out.print("null ");
            return;
        }
        System.out.print(root.val + " ");
        printTree(root.left);
        printTree(root.right);
    }

    // Helper method to count nodes
    private int countNodes(TreeNode root) {
        if (root == null)
            return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    public static void main(String[] args) {
        UniqueBSTII solution = new UniqueBSTII();

        // Test Case 1: n = 3
        List<TreeNode> trees3 = solution.generateTrees(3);
        System.out.println("n=3: " + trees3.size() + " trees"); // Expected: 5

        // Test Case 2: n = 1
        List<TreeNode> trees1 = solution.generateTrees(1);
        System.out.println("n=1: " + trees1.size() + " trees"); // Expected: 1

        // Test Case 3: n = 2
        List<TreeNode> trees2 = solution.generateTrees(2);
        System.out.println("n=2: " + trees2.size() + " trees"); // Expected: 2

        // Test Case 4: n = 4
        List<TreeNode> trees4 = solution.generateTrees(4);
        System.out.println("n=4: " + trees4.size() + " trees"); // Expected: 14

        // Test Case 5: n = 0
        List<TreeNode> trees0 = solution.generateTrees(0);
        System.out.println("n=0: " + trees0.size() + " trees"); // Expected: 0

        // Test Case 6: Test memoized version
        List<TreeNode> treesMemo = solution.generateTreesMemo(3);
        System.out.println("n=3 (memo): " + treesMemo.size() + " trees"); // Expected: 5

        // Test Case 7: n = 5
        List<TreeNode> trees5 = solution.generateTrees(5);
        System.out.println("n=5: " + trees5.size() + " trees"); // Expected: 42

        // Test Case 8: Verify each tree has correct number of nodes
        for (TreeNode tree : trees3) {
            System.out.println("Tree nodes: " + solution.countNodes(tree)); // All should be 3
        }

        // Test Case 9: n = 6
        List<TreeNode> trees6 = solution.generateTrees(6);
        System.out.println("n=6: " + trees6.size() + " trees"); // Expected: 132

        // Test Case 10: Maximum constraint n = 8
        List<TreeNode> trees8 = solution.generateTrees(8);
        System.out.println("n=8: " + trees8.size() + " trees"); // Expected: 1430
    }
}

