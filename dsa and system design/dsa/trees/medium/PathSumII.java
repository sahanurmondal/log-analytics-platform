package trees.medium;

import java.util.*;

/**
 * LeetCode 113: Path Sum II
 * https://leetcode.com/problems/path-sum-ii/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given the root of a binary tree and an integer targetSum, return
 * all root-to-leaf paths where the sum of the node values equals targetSum.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 5000]
 * - -1000 <= Node.val <= 1000
 * - -1000 <= targetSum <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find paths with maximum sum?
 * 2. Can you count paths instead of returning them?
 * 3. Can you find paths of specific length?
 */
public class PathSumII {

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

    // Approach 1: DFS with backtracking
    public List<List<Integer>> pathSum(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        pathSumHelper(root, targetSum, currentPath, result);
        return result;
    }

    private void pathSumHelper(TreeNode node, int remainingSum, List<Integer> currentPath, List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        // Check if it's a leaf and sum matches
        if (node.left == null && node.right == null && remainingSum == node.val) {
            result.add(new ArrayList<>(currentPath));
        } else {
            pathSumHelper(node.left, remainingSum - node.val, currentPath, result);
            pathSumHelper(node.right, remainingSum - node.val, currentPath, result);
        }

        currentPath.remove(currentPath.size() - 1); // backtrack
    }

    // Follow-up 1: Find paths with maximum sum
    public List<List<Integer>> pathsWithMaxSum(TreeNode root) {
        List<List<Integer>> allPaths = getAllRootToLeafPaths(root);
        if (allPaths.isEmpty())
            return allPaths;

        int maxSum = Integer.MIN_VALUE;
        for (List<Integer> path : allPaths) {
            int sum = path.stream().mapToInt(Integer::intValue).sum();
            maxSum = Math.max(maxSum, sum);
        }

        List<List<Integer>> maxPaths = new ArrayList<>();
        for (List<Integer> path : allPaths) {
            int sum = path.stream().mapToInt(Integer::intValue).sum();
            if (sum == maxSum) {
                maxPaths.add(path);
            }
        }

        return maxPaths;
    }

    private List<List<Integer>> getAllRootToLeafPaths(TreeNode root) {
        List<List<Integer>> allPaths = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        getAllPathsHelper(root, currentPath, allPaths);
        return allPaths;
    }

    private void getAllPathsHelper(TreeNode node, List<Integer> currentPath, List<List<Integer>> allPaths) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (node.left == null && node.right == null) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            getAllPathsHelper(node.left, currentPath, allPaths);
            getAllPathsHelper(node.right, currentPath, allPaths);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Follow-up 2: Count paths instead of returning them
    public int countPathsWithSum(TreeNode root, int targetSum) {
        return countPathsHelper(root, targetSum);
    }

    private int countPathsHelper(TreeNode node, int remainingSum) {
        if (node == null)
            return 0;

        int count = 0;

        if (node.left == null && node.right == null && remainingSum == node.val) {
            count = 1;
        } else {
            count += countPathsHelper(node.left, remainingSum - node.val);
            count += countPathsHelper(node.right, remainingSum - node.val);
        }

        return count;
    }

    // Follow-up 3: Find paths of specific length
    public List<List<Integer>> pathsOfLength(TreeNode root, int targetLength) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        pathsOfLengthHelper(root, targetLength, currentPath, result);
        return result;
    }

    private void pathsOfLengthHelper(TreeNode node, int remainingLength, List<Integer> currentPath,
            List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (node.left == null && node.right == null && remainingLength == 1) {
            result.add(new ArrayList<>(currentPath));
        } else if (remainingLength > 1) {
            pathsOfLengthHelper(node.left, remainingLength - 1, currentPath, result);
            pathsOfLengthHelper(node.right, remainingLength - 1, currentPath, result);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Additional: Find paths with sum in range
    public List<List<Integer>> pathsWithSumInRange(TreeNode root, int minSum, int maxSum) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        pathsInRangeHelper(root, minSum, maxSum, currentPath, result);
        return result;
    }

    private void pathsInRangeHelper(TreeNode node, int minSum, int maxSum, List<Integer> currentPath,
            List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (node.left == null && node.right == null) {
            int sum = currentPath.stream().mapToInt(Integer::intValue).sum();
            if (sum >= minSum && sum <= maxSum) {
                result.add(new ArrayList<>(currentPath));
            }
        } else {
            pathsInRangeHelper(node.left, minSum, maxSum, currentPath, result);
            pathsInRangeHelper(node.right, minSum, maxSum, currentPath, result);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    // Additional: Find path with specific pattern
    public List<List<Integer>> pathsWithPattern(TreeNode root, List<Integer> pattern) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        pathsWithPatternHelper(root, pattern, currentPath, result);
        return result;
    }

    private void pathsWithPatternHelper(TreeNode node, List<Integer> pattern, List<Integer> currentPath,
            List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        if (node.left == null && node.right == null) {
            if (matchesPattern(currentPath, pattern)) {
                result.add(new ArrayList<>(currentPath));
            }
        } else {
            pathsWithPatternHelper(node.left, pattern, currentPath, result);
            pathsWithPatternHelper(node.right, pattern, currentPath, result);
        }

        currentPath.remove(currentPath.size() - 1);
    }

    private boolean matchesPattern(List<Integer> path, List<Integer> pattern) {
        if (path.size() != pattern.size())
            return false;

        for (int i = 0; i < path.size(); i++) {
            if (!path.get(i).equals(pattern.get(i))) {
                return false;
            }
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        PathSumII solution = new PathSumII();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(5);
        root1.left = new TreeNode(4);
        root1.right = new TreeNode(8);
        root1.left.left = new TreeNode(11);
        root1.right.left = new TreeNode(13);
        root1.right.right = new TreeNode(4);
        root1.left.left.left = new TreeNode(7);
        root1.left.left.right = new TreeNode(2);
        root1.right.right.left = new TreeNode(5);
        root1.right.right.right = new TreeNode(1);

        int targetSum = 22;
        System.out.println("Test 1 - Path sum " + targetSum + ":");
        List<List<Integer>> paths = solution.pathSum(root1, targetSum);
        for (int i = 0; i < paths.size(); i++) {
            System.out.println("Path " + (i + 1) + ": " + paths.get(i));
        }

        // Test case 2: Count paths
        System.out.println("\nTest 2 - Count paths with sum " + targetSum + ": " +
                solution.countPathsWithSum(root1, targetSum));

        // Test case 3: Paths with maximum sum
        System.out.println("\nTest 3 - Paths with maximum sum:");
        List<List<Integer>> maxSumPaths = solution.pathsWithMaxSum(root1);
        for (int i = 0; i < maxSumPaths.size(); i++) {
            System.out.println("Max path " + (i + 1) + ": " + maxSumPaths.get(i));
        }

        // Test case 4: Paths of specific length
        System.out.println("\nTest 4 - Paths of length 4:");
        List<List<Integer>> lengthPaths = solution.pathsOfLength(root1, 4);
        for (List<Integer> path : lengthPaths) {
            System.out.println("Length 4 path: " + path);
        }

        // Test case 5: Paths with sum in range
        System.out.println("\nTest 5 - Paths with sum in range [20, 25]:");
        List<List<Integer>> rangePaths = solution.pathsWithSumInRange(root1, 20, 25);
        for (List<Integer> path : rangePaths) {
            System.out.println("Range path: " + path + " (sum: " +
                    path.stream().mapToInt(Integer::intValue).sum() + ")");
        }

        // Test case 6: Paths with pattern
        List<Integer> pattern = Arrays.asList(5, 4, 11, 7);
        System.out.println("\nTest 6 - Paths matching pattern " + pattern + ":");
        List<List<Integer>> patternPaths = solution.pathsWithPattern(root1, pattern);
        for (List<Integer> path : patternPaths) {
            System.out.println("Pattern match: " + path);
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.pathSum(null, 5));

        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node (sum 1): " + solution.pathSum(singleNode, 1));
        System.out.println("Single node (sum 2): " + solution.pathSum(singleNode, 2));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        List<List<Integer>> result = solution.pathSum(largeTree, 100);
        long end = System.nanoTime();
        System.out.println("Large tree paths found: " + result.size() + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode(rand.nextInt(20) + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(20) + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
