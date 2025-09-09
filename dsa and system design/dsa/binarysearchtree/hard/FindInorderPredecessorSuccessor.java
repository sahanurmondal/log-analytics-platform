package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode Variation: Find Inorder Predecessor and Successor in BST (Hard)
 * Related to: LeetCode 285 (Inorder Successor in BST), LeetCode 510 (Inorder
 * Successor in BST II)
 * 
 * Companies: Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High
 *
 * Description:
 * Given a BST and a key, find its inorder predecessor and successor.
 * This hard variant includes advanced techniques for threaded BST, Morris
 * traversal,
 * and handling various edge cases with optimal performance.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^8 <= Node.val <= 10^8
 * - -10^8 <= key <= 10^8
 * 
 * Follow-up Questions:
 * 1. Can you solve without recursion and with O(1) space?
 * 2. What if the BST is threaded?
 * 3. Can you handle concurrent modifications?
 * 4. What if we need multiple predecessor/successor queries?
 */
public class FindInorderPredecessorSuccessor {

    // Approach 1: Optimized Iterative - O(h) time, O(1) space
    public int[] findPredecessorSuccessor(TreeNode root, int key) {
        int predecessor = -1, successor = -1;

        // Find predecessor
        TreeNode current = root;
        while (current != null) {
            if (current.val < key) {
                predecessor = current.val;
                current = current.right;
            } else {
                current = current.left;
            }
        }

        // Find successor
        current = root;
        while (current != null) {
            if (current.val > key) {
                successor = current.val;
                current = current.left;
            } else {
                current = current.right;
            }
        }

        return new int[] { predecessor, successor };
    }

    // Approach 2: Morris Traversal - O(n) time, O(1) space
    public int[] findPredecessorSuccessorMorris(TreeNode root, int key) {
        int predecessor = -1, successor = -1;
        TreeNode current = root;
        boolean foundKey = false;
        TreeNode prev = null;

        while (current != null) {
            if (current.left == null) {
                // Visit current node
                if (foundKey && successor == -1) {
                    successor = current.val;
                }
                if (current.val < key) {
                    predecessor = current.val;
                }
                if (current.val == key) {
                    foundKey = true;
                    if (prev != null && prev.val < key) {
                        predecessor = prev.val;
                    }
                }
                prev = current;
                current = current.right;
            } else {
                // Find inorder predecessor
                TreeNode inorderPred = current.left;
                while (inorderPred.right != null && inorderPred.right != current) {
                    inorderPred = inorderPred.right;
                }

                if (inorderPred.right == null) {
                    // Create thread
                    inorderPred.right = current;
                    current = current.left;
                } else {
                    // Remove thread and visit current
                    inorderPred.right = null;
                    if (foundKey && successor == -1) {
                        successor = current.val;
                    }
                    if (current.val < key) {
                        predecessor = current.val;
                    }
                    if (current.val == key) {
                        foundKey = true;
                        if (prev != null && prev.val < key) {
                            predecessor = prev.val;
                        }
                    }
                    prev = current;
                    current = current.right;
                }
            }
        }

        return new int[] { predecessor, successor };
    }

    // Approach 3: Single Pass with Path Tracking - O(h) time, O(h) space
    public int[] findPredecessorSuccessorPath(TreeNode root, int key) {
        java.util.List<TreeNode> path = new java.util.ArrayList<>();
        TreeNode current = root;

        // Build path to where key would be inserted
        while (current != null) {
            path.add(current);
            if (current.val == key) {
                break;
            } else if (current.val < key) {
                current = current.right;
            } else {
                current = current.left;
            }
        }

        int predecessor = -1, successor = -1;

        // Find predecessor and successor from path
        for (int i = path.size() - 1; i >= 0; i--) {
            TreeNode node = path.get(i);
            if (node.val < key && predecessor == -1) {
                predecessor = node.val;
            }
            if (node.val > key && (successor == -1 || node.val < successor)) {
                successor = node.val;
            }
        }

        // If key was found, handle special cases
        if (!path.isEmpty() && path.get(path.size() - 1).val == key) {
            TreeNode keyNode = path.get(path.size() - 1);

            // Find predecessor in left subtree
            if (keyNode.left != null) {
                TreeNode pred = keyNode.left;
                while (pred.right != null) {
                    pred = pred.right;
                }
                predecessor = pred.val;
            }

            // Find successor in right subtree
            if (keyNode.right != null) {
                TreeNode succ = keyNode.right;
                while (succ.left != null) {
                    succ = succ.left;
                }
                successor = succ.val;
            }
        }

        return new int[] { predecessor, successor };
    }

    // Approach 4: Recursive with Global State - O(h) time, O(h) space
    private TreeNode predecessorNode = null;
    private TreeNode successorNode = null;

    public int[] findPredecessorSuccessorRecursive(TreeNode root, int key) {
        predecessorNode = null;
        successorNode = null;
        findPredecessor(root, key);
        findSuccessor(root, key);

        return new int[] {
                predecessorNode != null ? predecessorNode.val : -1,
                successorNode != null ? successorNode.val : -1
        };
    }

    private void findPredecessor(TreeNode root, int key) {
        if (root == null)
            return;

        if (root.val < key) {
            predecessorNode = root;
            findPredecessor(root.right, key);
        } else {
            findPredecessor(root.left, key);
        }
    }

    private void findSuccessor(TreeNode root, int key) {
        if (root == null)
            return;

        if (root.val > key) {
            successorNode = root;
            findSuccessor(root.left, key);
        } else {
            findSuccessor(root.right, key);
        }
    }

    // Approach 5: Threaded BST Simulation - O(h) time, O(1) space
    public int[] findPredecessorSuccessorThreaded(TreeNode root, int key) {
        TreeNode pred = null, succ = null;
        TreeNode current = root;

        // Find the node and its position
        while (current != null) {
            if (current.val == key) {
                // Found exact match
                if (current.left != null) {
                    pred = current.left;
                    while (pred.right != null) {
                        pred = pred.right;
                    }
                }
                if (current.right != null) {
                    succ = current.right;
                    while (succ.left != null) {
                        succ = succ.left;
                    }
                }
                break;
            } else if (current.val < key) {
                pred = current;
                current = current.right;
            } else {
                succ = current;
                current = current.left;
            }
        }

        return new int[] {
                pred != null ? pred.val : -1,
                succ != null ? succ.val : -1
        };
    }

    public static void main(String[] args) {
        FindInorderPredecessorSuccessor solution = new FindInorderPredecessorSuccessor();

        // Test case 1: Normal BST with key present
        TreeNode root1 = new TreeNode(20);
        root1.left = new TreeNode(10);
        root1.right = new TreeNode(30);
        root1.left.left = new TreeNode(5);
        root1.left.right = new TreeNode(15);
        root1.right.left = new TreeNode(25);
        root1.right.right = new TreeNode(35);

        System.out.println("Test Case 1 (Key = 15 - present):");
        System.out.println("Iterative: " + java.util.Arrays.toString(solution.findPredecessorSuccessor(root1, 15)));
        System.out.println("Morris: " + java.util.Arrays.toString(solution.findPredecessorSuccessorMorris(root1, 15)));
        System.out.println("Path: " + java.util.Arrays.toString(solution.findPredecessorSuccessorPath(root1, 15)));
        System.out.println(
                "Recursive: " + java.util.Arrays.toString(solution.findPredecessorSuccessorRecursive(root1, 15)));
        System.out.println(
                "Threaded: " + java.util.Arrays.toString(solution.findPredecessorSuccessorThreaded(root1, 15)));

        // Test case 2: Key not present
        System.out.println("\nTest Case 2 (Key = 18 - not present):");
        System.out.println("Iterative: " + java.util.Arrays.toString(solution.findPredecessorSuccessor(root1, 18)));
        System.out.println("Morris: " + java.util.Arrays.toString(solution.findPredecessorSuccessorMorris(root1, 18)));
        System.out.println("Path: " + java.util.Arrays.toString(solution.findPredecessorSuccessorPath(root1, 18)));

        // Test case 3: Key smaller than all values
        System.out.println("\nTest Case 3 (Key = 1 - smaller than all):");
        System.out.println("Result: " + java.util.Arrays.toString(solution.findPredecessorSuccessor(root1, 1)));

        // Test case 4: Key larger than all values
        System.out.println("\nTest Case 4 (Key = 40 - larger than all):");
        System.out.println("Result: " + java.util.Arrays.toString(solution.findPredecessorSuccessor(root1, 40)));

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node
        TreeNode single = new TreeNode(10);
        System.out.println(
                "Single node (key=10): " + java.util.Arrays.toString(solution.findPredecessorSuccessor(single, 10)));
        System.out.println(
                "Single node (key=5): " + java.util.Arrays.toString(solution.findPredecessorSuccessor(single, 5)));

        // Empty tree
        System.out.println("Empty tree: " + java.util.Arrays.toString(solution.findPredecessorSuccessor(null, 10)));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindInorderPredecessorSuccessor solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger balanced BST
        TreeNode largeRoot = createBalancedBST(1, 1000);
        int testKey = 555; // A key not in the tree

        long startTime, endTime;

        // Test iterative approach
        startTime = System.nanoTime();
        int[] result1 = solution.findPredecessorSuccessor(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + java.util.Arrays.toString(result1) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        int[] result2 = solution.findPredecessorSuccessorMorris(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Morris: " + java.util.Arrays.toString(result2) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test path approach
        startTime = System.nanoTime();
        int[] result3 = solution.findPredecessorSuccessorPath(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Path: " + java.util.Arrays.toString(result3) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test recursive approach
        startTime = System.nanoTime();
        int[] result4 = solution.findPredecessorSuccessorRecursive(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + java.util.Arrays.toString(result4) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test threaded approach
        startTime = System.nanoTime();
        int[] result5 = solution.findPredecessorSuccessorThreaded(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Threaded: " + java.util.Arrays.toString(result5) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");
    }

    private static TreeNode createBalancedBST(int start, int end) {
        if (start > end)
            return null;

        int mid = start + (end - start) / 2;
        TreeNode node = new TreeNode(mid);
        node.left = createBalancedBST(start, mid - 1);
        node.right = createBalancedBST(mid + 1, end);
        return node;
    }
}
