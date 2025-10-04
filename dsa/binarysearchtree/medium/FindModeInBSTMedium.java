package binarysearchtree.medium;

import binarysearchtree.TreeNode;
import java.util.*;

/**
 * LeetCode 501: Find Mode in Binary Search Tree (Medium Variant)
 * https://leetcode.com/problems/find-mode-in-binary-search-tree/
 * 
 * Companies: Amazon, Google, Microsoft, Meta
 * Frequency: Medium
 *
 * Description:
 * Given a BST, return all the mode(s) (the most frequently occurred element) in
 * the BST. A mode is a value that appears most frequently.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * - The tree may contain duplicates
 * 
 * Follow-up Questions:
 * 1. Can you solve it without using extra space for storing values?
 * 2. What if the tree doesn't contain duplicates?
 * 3. Can you solve it in one pass?
 */
public class FindModeInBSTMedium {

    // Approach 1: HashMap approach - O(n) time, O(n) space
    public int[] findMode(TreeNode root) {
        if (root == null)
            return new int[0];

        Map<Integer, Integer> freqMap = new HashMap<>();
        traverseAndCount(root, freqMap);

        int maxFreq = 0;
        for (int freq : freqMap.values()) {
            maxFreq = Math.max(maxFreq, freq);
        }

        List<Integer> modes = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : freqMap.entrySet()) {
            if (entry.getValue() == maxFreq) {
                modes.add(entry.getKey());
            }
        }

        return modes.stream().mapToInt(i -> i).toArray();
    }

    private void traverseAndCount(TreeNode root, Map<Integer, Integer> freqMap) {
        if (root == null)
            return;

        freqMap.put(root.val, freqMap.getOrDefault(root.val, 0) + 1);
        traverseAndCount(root.left, freqMap);
        traverseAndCount(root.right, freqMap);
    }

    // Approach 2: Inorder traversal with O(1) space - O(n) time, O(h) space
    private TreeNode prev = null;
    private int currentCount = 0;
    private int maxCount = 0;
    private List<Integer> modes = new ArrayList<>();

    public int[] findModeInorder(TreeNode root) {
        if (root == null)
            return new int[0];

        prev = null;
        currentCount = 0;
        maxCount = 0;
        modes = new ArrayList<>();

        inorderTraversal(root);

        return modes.stream().mapToInt(i -> i).toArray();
    }

    private void inorderTraversal(TreeNode root) {
        if (root == null)
            return;

        inorderTraversal(root.left);

        // Process current node
        if (prev == null || root.val != prev.val) {
            currentCount = 1;
        } else {
            currentCount++;
        }

        if (currentCount > maxCount) {
            maxCount = currentCount;
            modes.clear();
            modes.add(root.val);
        } else if (currentCount == maxCount) {
            modes.add(root.val);
        }

        prev = root;

        inorderTraversal(root.right);
    }

    // Approach 3: Two-pass approach for optimal space
    public int[] findModeTwoPass(TreeNode root) {
        if (root == null)
            return new int[0];

        // First pass: find max frequency
        maxCount = 0;
        currentCount = 0;
        prev = null;
        findMaxFrequency(root);

        // Second pass: collect modes
        modes = new ArrayList<>();
        currentCount = 0;
        prev = null;
        collectModes(root);

        return modes.stream().mapToInt(i -> i).toArray();
    }

    private void findMaxFrequency(TreeNode root) {
        if (root == null)
            return;

        findMaxFrequency(root.left);

        if (prev == null || root.val != prev.val) {
            currentCount = 1;
        } else {
            currentCount++;
        }

        maxCount = Math.max(maxCount, currentCount);
        prev = root;

        findMaxFrequency(root.right);
    }

    private void collectModes(TreeNode root) {
        if (root == null)
            return;

        collectModes(root.left);

        if (prev == null || root.val != prev.val) {
            currentCount = 1;
        } else {
            currentCount++;
        }

        if (currentCount == maxCount) {
            modes.add(root.val);
        }

        prev = root;

        collectModes(root.right);
    }

    // Helper: Build BST for testing (allowing duplicates)
    public TreeNode buildBSTWithDuplicates(int[] nums) {
        if (nums.length == 0)
            return null;

        TreeNode root = new TreeNode(nums[0]);
        for (int i = 1; i < nums.length; i++) {
            insertIntoBSTWithDuplicates(root, nums[i]);
        }

        return root;
    }

    private TreeNode insertIntoBSTWithDuplicates(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val <= root.val) {
            root.left = insertIntoBSTWithDuplicates(root.left, val);
        } else {
            root.right = insertIntoBSTWithDuplicates(root.right, val);
        }

        return root;
    }

    // Helper: Print inorder traversal
    public void printInorder(TreeNode root) {
        if (root == null)
            return;

        printInorder(root.left);
        System.out.print(root.val + " ");
        printInorder(root.right);
    }

    public static void main(String[] args) {
        FindModeInBSTMedium solution = new FindModeInBSTMedium();

        // Test Case 1: LeetCode Example
        TreeNode root1 = new TreeNode(1);
        root1.right = new TreeNode(2);
        root1.right.left = new TreeNode(2);

        System.out.println("=== Test Case 1: LeetCode Example ===");
        System.out.print("Tree: ");
        solution.printInorder(root1);
        System.out.println();
        System.out.println("Mode (HashMap): " + Arrays.toString(solution.findMode(root1))); // Expected: [2]
        System.out.println("Mode (Inorder): " + Arrays.toString(solution.findModeInorder(root1))); // Expected: [2]
        System.out.println("Mode (Two-Pass): " + Arrays.toString(solution.findModeTwoPass(root1))); // Expected: [2]
        System.out.println();

        // Test Case 2: Multiple modes
        TreeNode root2 = solution.buildBSTWithDuplicates(new int[] { 4, 2, 6, 2, 3, 6, 7 });
        System.out.println("=== Test Case 2: Multiple Modes ===");
        System.out.print("Tree: ");
        solution.printInorder(root2);
        System.out.println();
        System.out.println("Mode (HashMap): " + Arrays.toString(solution.findMode(root2))); // Expected: [2, 6]
        System.out.println("Mode (Inorder): " + Arrays.toString(solution.findModeInorder(root2))); // Expected: [2, 6]
        System.out.println();

        // Test Case 3: All unique values
        TreeNode root3 = new TreeNode(5);
        root3.left = new TreeNode(3);
        root3.right = new TreeNode(7);
        root3.left.left = new TreeNode(1);
        root3.left.right = new TreeNode(4);

        System.out.println("=== Test Case 3: All Unique Values ===");
        System.out.print("Tree: ");
        solution.printInorder(root3);
        System.out.println();
        System.out.println("Mode (HashMap): " + Arrays.toString(solution.findMode(root3))); // Expected: [1, 3, 4, 5, 7]
        System.out.println();

        // Test Case 4: Single value repeated
        TreeNode root4 = solution.buildBSTWithDuplicates(new int[] { 5, 5, 5, 5 });
        System.out.println("=== Test Case 4: Single Value Repeated ===");
        System.out.print("Tree: ");
        solution.printInorder(root4);
        System.out.println();
        System.out.println("Mode (HashMap): " + Arrays.toString(solution.findMode(root4))); // Expected: [5]
        System.out.println("Mode (Inorder): " + Arrays.toString(solution.findModeInorder(root4))); // Expected: [5]
        System.out.println();

        // Test Case 5: Single node
        TreeNode root5 = new TreeNode(42);
        System.out.println("=== Test Case 5: Single Node ===");
        System.out.println("Mode: " + Arrays.toString(solution.findMode(root5))); // Expected: [42]
        System.out.println();

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        TreeNode largeBST = solution.buildBSTWithDuplicates(new int[] { 8, 3, 10, 1, 6, 14, 4, 7, 13, 3, 6, 1 });

        long startTime, endTime;

        startTime = System.nanoTime();
        int[] hashMapResult = solution.findMode(largeBST);
        endTime = System.nanoTime();
        System.out.println("HashMap: " + Arrays.toString(hashMapResult) + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int[] inorderResult = solution.findModeInorder(largeBST);
        endTime = System.nanoTime();
        System.out.println("Inorder: " + Arrays.toString(inorderResult) + " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int[] twoPassResult = solution.findModeTwoPass(largeBST);
        endTime = System.nanoTime();
        System.out.println("Two-Pass: " + Arrays.toString(twoPassResult) + " (Time: " + (endTime - startTime) + " ns)");
    }
}
