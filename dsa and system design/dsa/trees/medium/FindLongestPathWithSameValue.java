package trees.medium;

import java.util.*;

/**
 * LeetCode 687: Longest Univalue Path
 * https://leetcode.com/problems/longest-univalue-path/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary tree, return the length of the
 * longest path, where each node in the path has the same value.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - -1000 <= Node.val <= 1000
 * - The depth of the tree will not exceed 1000
 * 
 * Follow-up Questions:
 * 1. Can you find all paths of maximum length?
 * 2. Can you find paths with specific values?
 * 3. Can you handle weighted paths?
 */
public class FindLongestPathWithSameValue {

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

    private int maxLength = 0;

    // Approach 1: DFS with path tracking
    public int longestUnivaluePath(TreeNode root) {
        maxLength = 0;
        univaluePathHelper(root);
        return maxLength;
    }

    private int univaluePathHelper(TreeNode node) {
        if (node == null)
            return 0;

        int leftPath = univaluePathHelper(node.left);
        int rightPath = univaluePathHelper(node.right);

        int leftArrow = 0, rightArrow = 0;

        if (node.left != null && node.left.val == node.val) {
            leftArrow = leftPath + 1;
        }
        if (node.right != null && node.right.val == node.val) {
            rightArrow = rightPath + 1;
        }

        maxLength = Math.max(maxLength, leftArrow + rightArrow);
        return Math.max(leftArrow, rightArrow);
    }

    // Follow-up 1: Find all paths of maximum length
    public List<List<TreeNode>> findAllMaxPaths(TreeNode root) {
        List<List<TreeNode>> allPaths = new ArrayList<>();
        maxLength = 0;

        // First pass to find max length
        univaluePathHelper(root);

        // Second pass to collect all max paths
        findAllMaxPathsHelper(root, allPaths);
        return allPaths;
    }

    private int findAllMaxPathsHelper(TreeNode node, List<List<TreeNode>> allPaths) {
        if (node == null)
            return 0;

        int leftPath = findAllMaxPathsHelper(node.left, allPaths);
        int rightPath = findAllMaxPathsHelper(node.right, allPaths);

        int leftArrow = 0, rightArrow = 0;

        if (node.left != null && node.left.val == node.val) {
            leftArrow = leftPath + 1;
        }
        if (node.right != null && node.right.val == node.val) {
            rightArrow = rightPath + 1;
        }

        if (leftArrow + rightArrow == maxLength) {
            List<TreeNode> path = new ArrayList<>();
            buildPath(node, leftArrow, rightArrow, path);
            allPaths.add(path);
        }

        return Math.max(leftArrow, rightArrow);
    }

    private void buildPath(TreeNode node, int leftLen, int rightLen, List<TreeNode> path) {
        // Build left path
        TreeNode current = node;
        for (int i = leftLen; i > 0; i--) {
            current = current.left;
            path.add(0, current);
        }

        // Add center node
        path.add(node);

        // Build right path
        current = node;
        for (int i = 0; i < rightLen; i++) {
            current = current.right;
            path.add(current);
        }
    }

    // Follow-up 2: Find paths with specific value
    public List<List<TreeNode>> findPathsWithValue(TreeNode root, int targetValue) {
        List<List<TreeNode>> result = new ArrayList<>();
        findPathsWithValueHelper(root, targetValue, result);
        return result;
    }

    private int findPathsWithValueHelper(TreeNode node, int targetValue, List<List<TreeNode>> result) {
        if (node == null)
            return 0;

        int leftPath = findPathsWithValueHelper(node.left, targetValue, result);
        int rightPath = findPathsWithValueHelper(node.right, targetValue, result);

        if (node.val != targetValue)
            return 0;

        int leftArrow = 0, rightArrow = 0;

        if (node.left != null && node.left.val == targetValue) {
            leftArrow = leftPath + 1;
        }
        if (node.right != null && node.right.val == targetValue) {
            rightArrow = rightPath + 1;
        }

        if (leftArrow + rightArrow > 0) {
            List<TreeNode> path = new ArrayList<>();
            buildPath(node, leftArrow, rightArrow, path);
            result.add(path);
        }

        return Math.max(leftArrow, rightArrow);
    }

    // Follow-up 3: Handle weighted paths
    public int longestWeightedUnivaluePath(TreeNode root, Map<TreeNode, Integer> weights) {
        return longestWeightedHelper(root, weights)[0];
    }

    private int[] longestWeightedHelper(TreeNode node, Map<TreeNode, Integer> weights) {
        if (node == null)
            return new int[] { 0, 0 }; // {maxPath, maxFromNode}

        int[] left = longestWeightedHelper(node.left, weights);
        int[] right = longestWeightedHelper(node.right, weights);

        int nodeWeight = weights.getOrDefault(node, 1);
        int leftArrow = 0, rightArrow = 0;

        if (node.left != null && node.left.val == node.val) {
            leftArrow = left[1] + nodeWeight;
        }
        if (node.right != null && node.right.val == node.val) {
            rightArrow = right[1] + nodeWeight;
        }

        int maxPath = Math.max(Math.max(left[0], right[0]), leftArrow + rightArrow);
        int maxFromNode = Math.max(leftArrow, rightArrow);

        return new int[] { maxPath, maxFromNode };
    }

    // Helper: Count paths of specific length
    public int countPathsOfLength(TreeNode root, int targetLength) {
        return countPathsHelper(root, targetLength)[0];
    }

    private int[] countPathsHelper(TreeNode node, int targetLength) {
        if (node == null)
            return new int[] { 0, 0 }; // {count, maxLength}

        int[] left = countPathsHelper(node.left, targetLength);
        int[] right = countPathsHelper(node.right, targetLength);

        int leftArrow = 0, rightArrow = 0;

        if (node.left != null && node.left.val == node.val) {
            leftArrow = left[1] + 1;
        }
        if (node.right != null && node.right.val == node.val) {
            rightArrow = right[1] + 1;
        }

        int pathLength = leftArrow + rightArrow;
        int count = left[0] + right[0];

        if (pathLength == targetLength)
            count++;

        return new int[] { count, Math.max(leftArrow, rightArrow) };
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindLongestPathWithSameValue solution = new FindLongestPathWithSameValue();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(4);
        root1.right = new TreeNode(5);
        root1.left.left = new TreeNode(1);
        root1.left.right = new TreeNode(1);
        root1.right.right = new TreeNode(5);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Longest univalue path: " + solution.longestUnivaluePath(root1));

        // Test case 2: All same values
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(1);
        root2.left.left = new TreeNode(1);
        root2.left.right = new TreeNode(1);
        root2.right.left = new TreeNode(1);
        root2.right.right = new TreeNode(1);

        System.out.println("\nTest 2 - All same values:");
        System.out.println("Longest univalue path: " + solution.longestUnivaluePath(root2));

        // Test case 3: Find all max paths
        System.out.println("\nTest 3 - All max paths:");
        List<List<TreeNode>> maxPaths = solution.findAllMaxPaths(root1);
        System.out.println("Found " + maxPaths.size() + " max paths");
        for (int i = 0; i < maxPaths.size(); i++) {
            System.out.print("Path " + (i + 1) + ": ");
            for (TreeNode node : maxPaths.get(i)) {
                System.out.print(node.val + " ");
            }
            System.out.println();
        }

        // Test case 4: Paths with specific value
        System.out.println("\nTest 4 - Paths with value 5:");
        List<List<TreeNode>> pathsWithValue = solution.findPathsWithValue(root1, 5);
        System.out.println("Found " + pathsWithValue.size() + " paths with value 5");

        // Test case 5: Weighted paths
        Map<TreeNode, Integer> weights = new HashMap<>();
        weights.put(root1, 2);
        weights.put(root1.left, 1);
        weights.put(root1.right, 3);

        System.out.println("\nTest 5 - Weighted paths:");
        System.out.println("Weighted path length: " + solution.longestWeightedUnivaluePath(root1, weights));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.longestUnivaluePath(null));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.longestUnivaluePath(singleNode));

        TreeNode zigzag = new TreeNode(1);
        zigzag.left = new TreeNode(2);
        zigzag.left.left = new TreeNode(1);
        System.out.println("Zigzag pattern: " + solution.longestUnivaluePath(zigzag));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildUniformTree(1000, 5);

        long start = System.nanoTime();
        int result = solution.longestUnivaluePath(largeTree);
        long end = System.nanoTime();
        System.out.println("Large uniform tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildUniformTree(int nodes, int value) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(value);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode(value);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(value);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
