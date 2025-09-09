package searching.hard;

import java.util.*;

/**
 * LeetCode 230: Kth Smallest Element in a BST
 * https://leetcode.com/problems/kth-smallest-element-in-a-bst/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given a BST, find the kth smallest element in it.
 *
 * Constraints:
 * - 1 <= k <= BST's total elements
 * - 1 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you handle frequent kth queries efficiently?
 * 2. What if the BST is modified frequently?
 * 3. Can you find kth largest element?
 */
public class KthSmallestElementInBST {

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

    // Approach 1: Inorder traversal - O(n) time, O(h) space
    public int kthSmallest(TreeNode root, int k) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return inorder.get(k - 1);
    }

    private void inorderTraversal(TreeNode node, List<Integer> inorder) {
        if (node == null)
            return;
        inorderTraversal(node.left, inorder);
        inorder.add(node.val);
        inorderTraversal(node.right, inorder);
    }

    // Approach 2: Early termination inorder - O(k) time, O(h) space
    private int count = 0;
    private int result = 0;

    public int kthSmallestOptimized(TreeNode root, int k) {
        count = 0;
        result = 0;
        inorderOptimized(root, k);
        return result;
    }

    private void inorderOptimized(TreeNode node, int k) {
        if (node == null || count >= k)
            return;

        inorderOptimized(node.left, k);
        count++;
        if (count == k) {
            result = node.val;
            return;
        }
        inorderOptimized(node.right, k);
    }

    // Approach 3: Iterative inorder - O(k) time, O(h) space
    public int kthSmallestIterative(TreeNode root, int k) {
        Stack<TreeNode> stack = new Stack<>();
        TreeNode curr = root;

        while (curr != null || !stack.isEmpty()) {
            while (curr != null) {
                stack.push(curr);
                curr = curr.left;
            }

            curr = stack.pop();
            k--;
            if (k == 0)
                return curr.val;
            curr = curr.right;
        }
        return -1;
    }

    // Follow-up 1: BST with size information for frequent queries
    static class TreeNodeWithSize {
        int val;
        int size; // Number of nodes in subtree
        TreeNodeWithSize left;
        TreeNodeWithSize right;

        TreeNodeWithSize(int val) {
            this.val = val;
            this.size = 1;
        }
    }

    public int kthSmallestWithSize(TreeNodeWithSize root, int k) {
        int leftSize = root.left != null ? root.left.size : 0;

        if (k <= leftSize) {
            return kthSmallestWithSize(root.left, k);
        } else if (k == leftSize + 1) {
            return root.val;
        } else {
            return kthSmallestWithSize(root.right, k - leftSize - 1);
        }
    }

    // Follow-up 2: Handle insertions and deletions
    public TreeNodeWithSize insert(TreeNodeWithSize root, int val) {
        if (root == null)
            return new TreeNodeWithSize(val);

        root.size++;
        if (val < root.val) {
            root.left = insert(root.left, val);
        } else {
            root.right = insert(root.right, val);
        }
        return root;
    }

    // Follow-up 3: Find kth largest element
    public int kthLargest(TreeNode root, int k) {
        List<Integer> inorder = new ArrayList<>();
        inorderTraversal(root, inorder);
        return inorder.get(inorder.size() - k);
    }

    private TreeNode buildBST(int[] vals) {
        TreeNode root = null;
        for (int val : vals) {
            root = insertToBST(root, val);
        }
        return root;
    }

    private TreeNode insertToBST(TreeNode root, int val) {
        if (root == null)
            return new TreeNode(val);
        if (val < root.val)
            root.left = insertToBST(root.left, val);
        else
            root.right = insertToBST(root.right, val);
        return root;
    }

    public static void main(String[] args) {
        KthSmallestElementInBST solution = new KthSmallestElementInBST();

        // Test case 1: Basic BST
        int[] vals1 = { 3, 1, 4, 2 };
        TreeNode root1 = solution.buildBST(vals1);
        System.out.println("Test 1 - Basic BST (k=1):");
        System.out.println("Expected: 1, Got: " + solution.kthSmallest(root1, 1));
        System.out.println("Optimized: " + solution.kthSmallestOptimized(root1, 1));
        System.out.println("Iterative: " + solution.kthSmallestIterative(root1, 1));

        // Test case 2: k = 2
        System.out.println("\nTest 2 - k=2:");
        System.out.println("Expected: 2, Got: " + solution.kthSmallest(root1, 2));

        // Test case 3: Larger BST
        int[] vals2 = { 5, 3, 6, 2, 4, 1 };
        TreeNode root2 = solution.buildBST(vals2);
        System.out.println("\nTest 3 - Larger BST (k=3):");
        System.out.println("Expected: 3, Got: " + solution.kthSmallest(root2, 3));

        // Test case 4: Single node
        TreeNode root3 = new TreeNode(1);
        System.out.println("\nTest 4 - Single node (k=1):");
        System.out.println("Expected: 1, Got: " + solution.kthSmallest(root3, 1));

        // Test case 5: Skewed tree
        int[] vals3 = { 1, 2, 3, 4, 5 };
        TreeNode root4 = solution.buildBST(vals3);
        System.out.println("\nTest 5 - Right skewed (k=3):");
        System.out.println("Expected: 3, Got: " + solution.kthSmallest(root4, 3));

        // Follow-up 3: Kth largest
        System.out.println("\nFollow-up 3 - Kth largest (k=2):");
        System.out.println("Expected: 4, Got: " + solution.kthLargest(root2, 2));

        // Performance test
        int[] largeBST = new int[10000];
        for (int i = 0; i < largeBST.length; i++) {
            largeBST[i] = i + 1;
        }
        TreeNode largeRoot = solution.buildBST(largeBST);
        long startTime = System.currentTimeMillis();
        solution.kthSmallestIterative(largeRoot, 5000);
        long endTime = System.currentTimeMillis();
        System.out.println("\nPerformance test (10k nodes, k=5000): " + (endTime - startTime) + "ms");
    }
}
