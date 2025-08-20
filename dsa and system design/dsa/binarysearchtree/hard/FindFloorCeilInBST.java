package binarysearchtree.hard;

import binarysearchtree.TreeNode;

/**
 * LeetCode Variation: Find Floor and Ceil in BST (Hard)
 * Related to: LeetCode 270 (Closest BST Value), LeetCode 272 (Closest BST
 * Values II)
 * 
 * Companies: Amazon, Microsoft, Google, Meta, Apple
 * Frequency: High
 *
 * Description:
 * Given a BST and a key, find the floor and ceil of the key in the BST.
 * Floor: largest element <= key, Ceil: smallest element >= key
 * This hard variant includes advanced techniques and optimizations.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4].
 * - -10^8 <= Node.val <= 10^8
 * - -10^8 <= key <= 10^8
 * 
 * Follow-up Questions:
 * 1. Can you solve without recursion?
 * 2. What if the BST is threaded?
 * 3. Can you handle concurrent modifications?
 * 4. What if we need multiple floor/ceil queries?
 */
public class FindFloorCeilInBST {

    // Approach 1: Iterative Single Pass - O(h) time, O(1) space
    public int[] findFloorCeil(TreeNode root, int key) {
        int floor = -1, ceil = -1;

        while (root != null) {
            if (root.val == key) {
                return new int[] { key, key };
            } else if (root.val < key) {
                floor = root.val;
                root = root.right;
            } else {
                ceil = root.val;
                root = root.left;
            }
        }

        return new int[] { floor, ceil };
    }

    // Approach 2: Recursive with Bounds - O(h) time, O(h) space
    public int[] findFloorCeilRecursive(TreeNode root, int key) {
        int[] result = { -1, -1 };
        findFloorCeilHelper(root, key, result);
        return result;
    }

    private void findFloorCeilHelper(TreeNode root, int key, int[] result) {
        if (root == null)
            return;

        if (root.val == key) {
            result[0] = result[1] = key;
            return;
        } else if (root.val < key) {
            result[0] = root.val;
            findFloorCeilHelper(root.right, key, result);
        } else {
            result[1] = root.val;
            findFloorCeilHelper(root.left, key, result);
        }
    }

    // Approach 3: Morris Traversal with Early Termination - O(n) time, O(1) space
    public int[] findFloorCeilMorris(TreeNode root, int key) {
        int floor = -1, ceil = -1;
        TreeNode current = root;
        boolean foundKey = false;

        while (current != null && !foundKey) {
            if (current.left == null) {
                // Visit current node
                if (current.val <= key) {
                    floor = current.val;
                }
                if (current.val >= key && ceil == -1) {
                    ceil = current.val;
                }
                if (current.val == key) {
                    foundKey = true;
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
                    if (current.val <= key) {
                        floor = current.val;
                    }
                    if (current.val >= key && ceil == -1) {
                        ceil = current.val;
                    }
                    if (current.val == key) {
                        foundKey = true;
                    }
                    current = current.right;
                }
            }
        }

        return new int[] { floor, ceil };
    }

    // Approach 4: Path-based with Stack - O(h) time, O(h) space
    public int[] findFloorCeilStack(TreeNode root, int key) {
        if (root == null)
            return new int[] { -1, -1 };

        java.util.Stack<TreeNode> pathStack = new java.util.Stack<>();
        java.util.Stack<Boolean> directionStack = new java.util.Stack<>(); // true for right, false for left

        TreeNode current = root;
        int floor = -1, ceil = -1;

        // Find the path to where key would be inserted
        while (current != null) {
            pathStack.push(current);

            if (current.val == key) {
                return new int[] { key, key };
            } else if (current.val < key) {
                floor = current.val;
                directionStack.push(true);
                current = current.right;
            } else {
                ceil = current.val;
                directionStack.push(false);
                current = current.left;
            }
        }

        return new int[] { floor, ceil };
    }

    // Approach 5: Threaded BST Simulation - O(h) time, O(1) space
    public int[] findFloorCeilThreaded(TreeNode root, int key) {
        TreeNode floorNode = null, ceilNode = null;
        TreeNode current = root;

        while (current != null) {
            if (current.val == key) {
                return new int[] { key, key };
            }

            if (current.val < key) {
                floorNode = current;
                current = current.right;
            } else {
                ceilNode = current;

                // For ceil, we might need to go deeper to find a closer value
                if (current.left != null) {
                    current = current.left;
                } else {
                    break;
                }
            }
        }

        return new int[] {
                floorNode != null ? floorNode.val : -1,
                ceilNode != null ? ceilNode.val : -1
        };
    }

    public static void main(String[] args) {
        FindFloorCeilInBST solution = new FindFloorCeilInBST();

        // Test case 1: Normal BST with key present
        TreeNode root1 = new TreeNode(20);
        root1.left = new TreeNode(10);
        root1.right = new TreeNode(30);
        root1.left.left = new TreeNode(5);
        root1.left.right = new TreeNode(15);
        root1.right.left = new TreeNode(25);
        root1.right.right = new TreeNode(35);

        System.out.println("Test Case 1 (Key = 15 - present):");
        System.out.println("Iterative: " + java.util.Arrays.toString(solution.findFloorCeil(root1, 15)));
        System.out.println("Recursive: " + java.util.Arrays.toString(solution.findFloorCeilRecursive(root1, 15)));
        System.out.println("Morris: " + java.util.Arrays.toString(solution.findFloorCeilMorris(root1, 15)));
        System.out.println("Stack: " + java.util.Arrays.toString(solution.findFloorCeilStack(root1, 15)));
        System.out.println("Threaded: " + java.util.Arrays.toString(solution.findFloorCeilThreaded(root1, 15)));

        // Test case 2: Key not present (between existing values)
        System.out.println("\nTest Case 2 (Key = 18 - not present):");
        System.out.println("Iterative: " + java.util.Arrays.toString(solution.findFloorCeil(root1, 18)));
        System.out.println("Recursive: " + java.util.Arrays.toString(solution.findFloorCeilRecursive(root1, 18)));
        System.out.println("Morris: " + java.util.Arrays.toString(solution.findFloorCeilMorris(root1, 18)));

        // Test case 3: Key smaller than all values
        System.out.println("\nTest Case 3 (Key = 1 - smaller than all):");
        System.out.println("Result: " + java.util.Arrays.toString(solution.findFloorCeil(root1, 1)));

        // Test case 4: Key larger than all values
        System.out.println("\nTest Case 4 (Key = 40 - larger than all):");
        System.out.println("Result: " + java.util.Arrays.toString(solution.findFloorCeil(root1, 40)));

        // Edge cases
        System.out.println("\nEdge Cases:");

        // Single node
        TreeNode single = new TreeNode(10);
        System.out.println("Single node (key=10): " + java.util.Arrays.toString(solution.findFloorCeil(single, 10)));
        System.out.println("Single node (key=5): " + java.util.Arrays.toString(solution.findFloorCeil(single, 5)));
        System.out.println("Single node (key=15): " + java.util.Arrays.toString(solution.findFloorCeil(single, 15)));

        // Empty tree
        System.out.println("Empty tree: " + java.util.Arrays.toString(solution.findFloorCeil(null, 10)));

        // Performance test
        performanceTest(solution);
    }

    private static void performanceTest(FindFloorCeilInBST solution) {
        System.out.println("\n--- Performance Comparison ---");

        // Create a larger balanced BST
        TreeNode largeRoot = createBalancedBST(1, 1000);
        int testKey = 555; // A key not in the tree

        long startTime, endTime;

        // Test iterative approach
        startTime = System.nanoTime();
        int[] result1 = solution.findFloorCeil(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + java.util.Arrays.toString(result1) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test recursive approach
        startTime = System.nanoTime();
        int[] result2 = solution.findFloorCeilRecursive(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + java.util.Arrays.toString(result2) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test Morris approach
        startTime = System.nanoTime();
        int[] result3 = solution.findFloorCeilMorris(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Morris: " + java.util.Arrays.toString(result3) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test stack approach
        startTime = System.nanoTime();
        int[] result4 = solution.findFloorCeilStack(largeRoot, testKey);
        endTime = System.nanoTime();
        System.out.println("Stack: " + java.util.Arrays.toString(result4) + " (Time: " +
                (endTime - startTime) / 1_000_000.0 + " ms)");

        // Test threaded approach
        startTime = System.nanoTime();
        int[] result5 = solution.findFloorCeilThreaded(largeRoot, testKey);
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
