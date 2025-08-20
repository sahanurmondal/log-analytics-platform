package trees.hard;

import java.util.*;

/**
 * LeetCode 437: Path Sum III
 * https://leetcode.com/problems/path-sum-iii/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given the root of a binary tree and an integer targetSum, return
 * the number of paths where the sum equals targetSum.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 1000]
 * - -10^9 <= Node.val <= 10^9
 * - -1000 <= targetSum <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find all actual paths with the target sum?
 * 2. Can you count paths with sum in a range?
 * 3. Can you optimize for very large trees?
 */
public class CountPathsWithSum {

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

    // Approach 1: Prefix sum with HashMap (O(n) time)
    public int pathSum(TreeNode root, int targetSum) {
        Map<Long, Integer> prefixSumMap = new HashMap<>();
        prefixSumMap.put(0L, 1);
        return pathSumHelper(root, 0L, targetSum, prefixSumMap);
    }

    private int pathSumHelper(TreeNode node, long currentSum, int targetSum, Map<Long, Integer> prefixSumMap) {
        if (node == null)
            return 0;

        currentSum += node.val;
        int count = prefixSumMap.getOrDefault(currentSum - targetSum, 0);

        prefixSumMap.put(currentSum, prefixSumMap.getOrDefault(currentSum, 0) + 1);

        count += pathSumHelper(node.left, currentSum, targetSum, prefixSumMap);
        count += pathSumHelper(node.right, currentSum, targetSum, prefixSumMap);

        prefixSumMap.put(currentSum, prefixSumMap.get(currentSum) - 1);

        return count;
    }

    // Follow-up 1: Find all actual paths with target sum
    public List<List<Integer>> findAllPaths(TreeNode root, int targetSum) {
        List<List<Integer>> result = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        findAllPathsHelper(root, targetSum, currentPath, result);
        return result;
    }

    private void findAllPathsHelper(TreeNode node, int targetSum, List<Integer> currentPath,
            List<List<Integer>> result) {
        if (node == null)
            return;

        currentPath.add(node.val);

        // Check all possible paths ending at current node
        int sum = 0;
        for (int i = currentPath.size() - 1; i >= 0; i--) {
            sum += currentPath.get(i);
            if (sum == targetSum) {
                result.add(new ArrayList<>(currentPath.subList(i, currentPath.size())));
            }
        }

        findAllPathsHelper(node.left, targetSum, currentPath, result);
        findAllPathsHelper(node.right, targetSum, currentPath, result);

        currentPath.remove(currentPath.size() - 1);
    }

    // Follow-up 2: Count paths with sum in range [low, high]
    public int pathSumInRange(TreeNode root, int low, int high) {
        Map<Long, Integer> prefixSumMap = new HashMap<>();
        prefixSumMap.put(0L, 1);
        return pathSumRangeHelper(root, 0L, low, high, prefixSumMap);
    }

    private int pathSumRangeHelper(TreeNode node, long currentSum, int low, int high, Map<Long, Integer> prefixSumMap) {
        if (node == null)
            return 0;

        currentSum += node.val;
        int count = 0;

        // Count paths with sum in range
        for (long sum : prefixSumMap.keySet()) {
            long pathSum = currentSum - sum;
            if (pathSum >= low && pathSum <= high) {
                count += prefixSumMap.get(sum);
            }
        }

        prefixSumMap.put(currentSum, prefixSumMap.getOrDefault(currentSum, 0) + 1);

        count += pathSumRangeHelper(node.left, currentSum, low, high, prefixSumMap);
        count += pathSumRangeHelper(node.right, currentSum, low, high, prefixSumMap);

        prefixSumMap.put(currentSum, prefixSumMap.get(currentSum) - 1);

        return count;
    }

    // Follow-up 3: Memory optimized for large trees
    public int pathSumOptimized(TreeNode root, int targetSum) {
        return pathSumFromNode(root, targetSum) +
                (root != null ? pathSumOptimized(root.left, targetSum) + pathSumOptimized(root.right, targetSum) : 0);
    }

    private int pathSumFromNode(TreeNode node, long targetSum) {
        if (node == null)
            return 0;
        return (node.val == targetSum ? 1 : 0) +
                pathSumFromNode(node.left, targetSum - node.val) +
                pathSumFromNode(node.right, targetSum - node.val);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        CountPathsWithSum solution = new CountPathsWithSum();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(10);
        root1.left = new TreeNode(5);
        root1.right = new TreeNode(-3);
        root1.left.left = new TreeNode(3);
        root1.left.right = new TreeNode(2);
        root1.right.right = new TreeNode(11);
        root1.left.left.left = new TreeNode(3);
        root1.left.left.right = new TreeNode(-2);
        root1.left.right.right = new TreeNode(1);

        System.out.println("Test 1 - Target sum 8, Expected: 3");
        System.out.println("Result: " + solution.pathSum(root1, 8));

        // Test case 2: Find all paths
        System.out.println("\nTest 2 - All paths with sum 8:");
        List<List<Integer>> allPaths = solution.findAllPaths(root1, 8);
        for (List<Integer> path : allPaths) {
            System.out.println(path);
        }

        // Test case 3: Paths in range
        System.out.println("\nTest 3 - Paths with sum in range [7, 9]:");
        System.out.println("Result: " + solution.pathSumInRange(root1, 7, 9));

        // Test case 4: Optimized version
        System.out.println("\nTest 4 - Optimized version:");
        System.out.println("Result: " + solution.pathSumOptimized(root1, 8));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null root: " + solution.pathSum(null, 5));

        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node (match): " + solution.pathSum(singleNode, 5));
        System.out.println("Single node (no match): " + solution.pathSum(singleNode, 10));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);
        long start = System.nanoTime();
        int result = solution.pathSum(largeTree, 50);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " (Time: " + (end - start) / 1_000_000 + " ms)");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;
        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();
            if (count < nodes) {
                current.left = new TreeNode((count % 20) - 10);
                queue.offer(current.left);
                count++;
            }
            if (count < nodes) {
                current.right = new TreeNode((count % 20) - 10);
                queue.offer(current.right);
                count++;
            }
        }
        return root;
    }
}
