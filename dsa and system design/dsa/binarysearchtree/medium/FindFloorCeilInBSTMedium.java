package binarysearchtree.medium;

import binarysearchtree.TreeNode;

/**
 * Variation: Find Floor and Ceil in BST (Medium)
 * Related to finding predecessor and successor
 * 
 * Companies: Amazon, Microsoft, Google, Meta
 * Frequency: Medium-High
 *
 * Description:
 * Given a BST and a key, find the floor and ceil of the key in the BST.
 * Floor: The largest value smaller than or equal to key
 * Ceil: The smallest value greater than or equal to key
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4].
 * - -10^4 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. What if key is not in the BST?
 * 2. Can you solve it in one traversal?
 * 3. What if you need to handle duplicates?
 */
public class FindFloorCeilInBSTMedium {

    // Approach 1: Iterative Single Pass - O(h) time, O(1) space
    public int[] findFloorCeil(TreeNode root, int key) {
        int floor = -1;
        int ceil = -1;

        while (root != null) {
            if (root.val == key) {
                // Found exact match
                return new int[] { key, key };
            } else if (root.val < key) {
                // Current can be floor, go right for better floor
                floor = root.val;
                root = root.right;
            } else {
                // Current can be ceil, go left for better ceil
                ceil = root.val;
                root = root.left;
            }
        }

        return new int[] { floor, ceil };
    }

    // Approach 2: Recursive Approach - O(h) time, O(h) space
    public int[] findFloorCeilRecursive(TreeNode root, int key) {
        int[] result = new int[] { -1, -1 };
        findFloorHelper(root, key, result);
        findCeilHelper(root, key, result);
        return result;
    }

    private void findFloorHelper(TreeNode root, int key, int[] result) {
        if (root == null)
            return;

        if (root.val <= key) {
            result[0] = root.val;
            findFloorHelper(root.right, key, result);
        } else {
            findFloorHelper(root.left, key, result);
        }
    }

    private void findCeilHelper(TreeNode root, int key, int[] result) {
        if (root == null)
            return;

        if (root.val >= key) {
            result[1] = root.val;
            findCeilHelper(root.left, key, result);
        } else {
            findCeilHelper(root.right, key, result);
        }
    }

    // Approach 3: Separate Floor and Ceil methods
    public int findFloor(TreeNode root, int key) {
        int floor = -1;

        while (root != null) {
            if (root.val <= key) {
                floor = root.val;
                root = root.right;
            } else {
                root = root.left;
            }
        }

        return floor;
    }

    public int findCeil(TreeNode root, int key) {
        int ceil = -1;

        while (root != null) {
            if (root.val >= key) {
                ceil = root.val;
                root = root.left;
            } else {
                root = root.right;
            }
        }

        return ceil;
    }

    // Approach 4: Using Integer wrapper for better null handling
    public Integer[] findFloorCeilNullSafe(TreeNode root, int key) {
        Integer floor = null;
        Integer ceil = null;

        while (root != null) {
            if (root.val == key) {
                return new Integer[] { key, key };
            } else if (root.val < key) {
                floor = root.val;
                root = root.right;
            } else {
                ceil = root.val;
                root = root.left;
            }
        }

        return new Integer[] { floor, ceil };
    }

    // Helper: Build BST for testing
    public TreeNode buildBST(int[] nums) {
        if (nums.length == 0)
            return null;

        TreeNode root = new TreeNode(nums[0]);
        for (int i = 1; i < nums.length; i++) {
            insertIntoBST(root, nums[i]);
        }

        return root;
    }

    private TreeNode insertIntoBST(TreeNode root, int val) {
        if (root == null) {
            return new TreeNode(val);
        }

        if (val < root.val) {
            root.left = insertIntoBST(root.left, val);
        } else {
            root.right = insertIntoBST(root.right, val);
        }

        return root;
    }

    public static void main(String[] args) {
        FindFloorCeilInBSTMedium solution = new FindFloorCeilInBSTMedium();

        // Test Case 1: Normal BST
        TreeNode root1 = new TreeNode(8);
        root1.left = new TreeNode(4);
        root1.right = new TreeNode(12);
        root1.left.left = new TreeNode(2);
        root1.left.right = new TreeNode(6);
        root1.right.left = new TreeNode(10);
        root1.right.right = new TreeNode(14);

        System.out.println("=== Test Case 1: Normal BST [2,4,6,8,10,12,14] ===");

        int[] testKeys = { 5, 8, 1, 15, 6, 9, 13 };
        for (int key : testKeys) {
            int[] result = solution.findFloorCeil(root1, key);
            System.out.println("Key " + key + ": Floor=" + result[0] + ", Ceil=" + result[1]);
        }
        System.out.println();

        // Test Case 2: Single node
        TreeNode root2 = new TreeNode(5);
        System.out.println("=== Test Case 2: Single Node [5] ===");
        System.out.println("Key 3: " + java.util.Arrays.toString(solution.findFloorCeil(root2, 3))); // [-1, 5]
        System.out.println("Key 5: " + java.util.Arrays.toString(solution.findFloorCeil(root2, 5))); // [5, 5]
        System.out.println("Key 7: " + java.util.Arrays.toString(solution.findFloorCeil(root2, 7))); // [5, -1]
        System.out.println();

        // Test Case 3: Left skewed tree
        TreeNode root3 = new TreeNode(20);
        root3.left = new TreeNode(15);
        root3.left.left = new TreeNode(10);
        root3.left.left.left = new TreeNode(5);

        System.out.println("=== Test Case 3: Left Skewed Tree [5,10,15,20] ===");
        System.out.println("Key 12: " + java.util.Arrays.toString(solution.findFloorCeil(root3, 12))); // [10, 15]
        System.out.println("Key 25: " + java.util.Arrays.toString(solution.findFloorCeil(root3, 25))); // [20, -1]
        System.out.println();

        // Test Case 4: Approach comparison
        System.out.println("=== Test Case 4: Approach Comparison ===");
        TreeNode root4 = solution.buildBST(new int[] { 50, 30, 70, 20, 40, 60, 80, 10, 25, 35, 45 });
        int testKey = 37;

        int[] result1 = solution.findFloorCeil(root4, testKey);
        int[] result2 = solution.findFloorCeilRecursive(root4, testKey);
        Integer[] result3 = solution.findFloorCeilNullSafe(root4, testKey);

        System.out.println("Key " + testKey + ":");
        System.out.println("Iterative: " + java.util.Arrays.toString(result1));
        System.out.println("Recursive: " + java.util.Arrays.toString(result2));
        System.out.println("Null Safe: " + java.util.Arrays.toString(result3));
        System.out.println("Separate Methods: Floor=" + solution.findFloor(root4, testKey) +
                ", Ceil=" + solution.findCeil(root4, testKey));
        System.out.println();

        // Performance comparison
        System.out.println("=== Performance Comparison ===");
        int[] nums = new int[1000];
        for (int i = 0; i < 1000; i++) {
            nums[i] = i * 2; // Even numbers 0, 2, 4, ...
        }
        TreeNode largeBST = solution.buildBST(nums);
        int perfTestKey = 501; // Odd number between even numbers

        long startTime, endTime;

        startTime = System.nanoTime();
        int[] perfResult1 = solution.findFloorCeil(largeBST, perfTestKey);
        endTime = System.nanoTime();
        System.out.println("Iterative: " + java.util.Arrays.toString(perfResult1) +
                " (Time: " + (endTime - startTime) + " ns)");

        startTime = System.nanoTime();
        int[] perfResult2 = solution.findFloorCeilRecursive(largeBST, perfTestKey);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + java.util.Arrays.toString(perfResult2) +
                " (Time: " + (endTime - startTime) + " ns)");
    }
}
