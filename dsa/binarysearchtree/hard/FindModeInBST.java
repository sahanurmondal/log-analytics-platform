package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode 501: Find Mode in Binary Search Tree (Hard)
 * https://leetcode.com/problems/find-mode-in-binary-search-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: High
 *
 * Description:
 * Given a BST, return all the mode(s) (the most frequently occurred element) in
 * the BST. The BST may contain duplicates.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -10^5 <= Node.val <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you solve it without using extra space (O(1) space)?
 * 2. Can you solve it in one pass?
 * 3. What if the BST allows duplicates?
 */
public class FindModeInBST {

    // Approach 1: Inorder Traversal with HashMap - O(n) time, O(n) space
    public int[] findMode(TreeNode root) {
        if (root == null)
            return new int[0];

        java.util.Map<Integer, Integer> frequencyMap = new java.util.HashMap<>();
        inorderTraversal(root, frequencyMap);

        int maxFreq = frequencyMap.values().stream().max(Integer::compareTo).orElse(0);

        return frequencyMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFreq)
                .mapToInt(java.util.Map.Entry::getKey)
                .toArray();
    }

    private void inorderTraversal(TreeNode node, java.util.Map<Integer, Integer> map) {
        if (node == null)
            return;
        inorderTraversal(node.left, map);
        map.put(node.val, map.getOrDefault(node.val, 0) + 1);
        inorderTraversal(node.right, map);
    }

    // Approach 2: Two-Pass Inorder (Space-Optimized) - O(n) time, O(h) space
    private int currentVal;
    private int currentCount;
    private int maxCount;
    private java.util.List<Integer> modes;

    public int[] findModeOptimized(TreeNode root) {
        if (root == null)
            return new int[0];

        // First pass: find max frequency
        maxCount = 0;
        currentCount = 0;
        firstPass(root);

        // Second pass: collect all modes
        modes = new java.util.ArrayList<>();
        currentCount = 0;
        secondPass(root);

        return modes.stream().mapToInt(Integer::intValue).toArray();
    }

    private void firstPass(TreeNode node) {
        if (node == null)
            return;

        firstPass(node.left);

        if (node.val != currentVal) {
            currentVal = node.val;
            currentCount = 0;
        }
        currentCount++;
        maxCount = Math.max(maxCount, currentCount);

        firstPass(node.right);
    }

    private void secondPass(TreeNode node) {
        if (node == null)
            return;

        secondPass(node.left);

        if (node.val != currentVal) {
            currentVal = node.val;
            currentCount = 0;
        }
        currentCount++;
        if (currentCount == maxCount) {
            modes.add(node.val);
        }

        secondPass(node.right);
    }

    // Approach 3: Morris Traversal (Constant Space) - O(n) time, O(1) space
    public int[] findModeMorris(TreeNode root) {
        if (root == null)
            return new int[0];

        java.util.List<Integer> result = new java.util.ArrayList<>();
        TreeNode current = root;
        int currentVal = Integer.MIN_VALUE;
        int currentCount = 0;
        int maxCount = 0;

        // First pass with Morris traversal to find max frequency
        while (current != null) {
            if (current.left == null) {
                // Visit current node
                if (current.val != currentVal) {
                    currentVal = current.val;
                    currentCount = 1;
                } else {
                    currentCount++;
                }
                maxCount = Math.max(maxCount, currentCount);
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
                    if (current.val != currentVal) {
                        currentVal = current.val;
                        currentCount = 1;
                    } else {
                        currentCount++;
                    }
                    maxCount = Math.max(maxCount, currentCount);
                    current = current.right;
                }
            }
        }

        // Second pass to collect modes
        current = root;
        currentVal = Integer.MIN_VALUE;
        currentCount = 0;

        while (current != null) {
            if (current.left == null) {
                // Visit current node
                if (current.val != currentVal) {
                    currentVal = current.val;
                    currentCount = 1;
                } else {
                    currentCount++;
                }
                if (currentCount == maxCount) {
                    result.add(current.val);
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
                    if (current.val != currentVal) {
                        currentVal = current.val;
                        currentCount = 1;
                    } else {
                        currentCount++;
                    }
                    if (currentCount == maxCount) {
                        result.add(current.val);
                    }
                    current = current.right;
                }
            }
        }

        return result.stream().mapToInt(Integer::intValue).toArray();
    }

    // Approach 4: Iterative with Stack - O(n) time, O(h) space
    public int[] findModeIterative(TreeNode root) {
        if (root == null)
            return new int[0];

        java.util.Stack<TreeNode> stack = new java.util.Stack<>();
        java.util.Map<Integer, Integer> freq = new java.util.HashMap<>();
        TreeNode current = root;

        // Inorder traversal with stack
        while (current != null || !stack.isEmpty()) {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            freq.put(current.val, freq.getOrDefault(current.val, 0) + 1);
            current = current.right;
        }

        int maxFreq = freq.values().stream().max(Integer::compareTo).orElse(0);

        return freq.entrySet().stream()
                .filter(entry -> entry.getValue() == maxFreq)
                .mapToInt(java.util.Map.Entry::getKey)
                .sorted()
                .toArray();
    }

    public static void main(String[] args) {
        FindModeInBST solution = new FindModeInBST();

        // Test case 1: BST with duplicates [1,null,2,2]
        TreeNode root1 = new TreeNode(1);
        root1.right = new TreeNode(2);
        root1.right.left = new TreeNode(2);

        System.out.println("Test Case 1 [1,null,2,2]:");
        System.out.println("HashMap: " + java.util.Arrays.toString(solution.findMode(root1)));
        System.out.println("Optimized: " + java.util.Arrays.toString(solution.findModeOptimized(root1)));
        System.out.println("Morris: " + java.util.Arrays.toString(solution.findModeMorris(root1)));
        System.out.println("Iterative: " + java.util.Arrays.toString(solution.findModeIterative(root1)));

        // Test case 2: BST with all same values [2,2,2]
        TreeNode root2 = new TreeNode(2);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(2);

        System.out.println("\nTest Case 2 [2,2,2]:");
        System.out.println("HashMap: " + java.util.Arrays.toString(solution.findMode(root2)));
        System.out.println("Optimized: " + java.util.Arrays.toString(solution.findModeOptimized(root2)));

        // Test case 3: BST with multiple modes [1,1,2,2,3,3]
        TreeNode root3 = new TreeNode(2);
        root3.left = new TreeNode(1);
        root3.right = new TreeNode(3);
        root3.left.left = new TreeNode(1);
        root3.right.right = new TreeNode(3);

        System.out.println("\nTest Case 3 (Multiple modes):");
        System.out.println("HashMap: " + java.util.Arrays.toString(solution.findMode(root3)));
        System.out.println("Morris: " + java.util.Arrays.toString(solution.findModeMorris(root3)));

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node
        TreeNode single = new TreeNode(1);
        System.out.println("Single node: " + java.util.Arrays.toString(solution.findMode(single)));

        // Empty tree
        System.out.println("Empty tree: " + java.util.Arrays.toString(solution.findMode(null)));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindModeInBST solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a BST with some duplicates
        TreeNode largeRoot = createBSTWithDuplicates(1, 500);

        long startTime, endTime;

        // Test HashMap approach
        startTime = System.nanoTime();
        int[] result1 = solution.findMode(largeRoot);
        endTime = System.nanoTime();
        System.out.println("HashMap: " + result1.length + " modes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test optimized approach
        startTime = System.nanoTime();
        int[] result2 = solution.findModeOptimized(largeRoot);
        endTime = System.nanoTime();
        System.out.println("Optimized: " + result2.length + " modes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        int[] result3 = solution.findModeMorris(largeRoot);
        endTime = System.nanoTime();
        System.out.println("Morris: " + result3.length + " modes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test iterative approach
        startTime = System.nanoTime();
        int[] result4 = solution.findModeIterative(largeRoot);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + result4.length + " modes (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }

    private static TreeNode createBSTWithDuplicates(int start, int end) {
        if (start > end)
            return null;

        int mid = start + (end - start) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = createBSTWithDuplicates(start, mid - 1);
        node.right = createBSTWithDuplicates(mid + 1, end);

        // Add some duplicates randomly
        if (mid % 3 == 0 && node.left == null) {
            node.left = new TreeNode(mid);
        }
        if (mid % 5 == 0 && node.right != null) {
            TreeNode duplicate = new TreeNode(mid);
            duplicate.right = node.right;
            node.right = duplicate;
        }

        return node;
    }
}
