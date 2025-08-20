package trees.medium;

import java.util.*;

/**
 * LeetCode 437: Path Sum III
 * https://leetcode.com/problems/path-sum-iii/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: High
 *
 * Description: Given the root of a binary tree and an integer targetSum, return
 * the number of paths where the sum of the values along the path equals
 * targetSum.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 1000]
 * - -10^9 <= Node.val <= 10^9
 * - -1000 <= targetSum <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find the actual paths?
 * 2. Can you handle multiple target sums?
 * 3. Can you optimize for very large trees?
 */
public class FindPathSumIII {

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

    // Approach 1: Brute force - check every path
    public int pathSum(TreeNode root, int targetSum) {
        if (root == null)
            return 0;

        return pathSumFromNode(root, targetSum) +
                pathSum(root.left, targetSum) +
                pathSum(root.right, targetSum);
    }

    private int pathSumFromNode(TreeNode node, long targetSum) {
        if (node == null)
            return 0;

        int count = 0;
        if (node.val == targetSum)
            count++;

        count += pathSumFromNode(node.left, targetSum - node.val);
        count += pathSumFromNode(node.right, targetSum - node.val);

        return count;
    }

    // Approach 2: Optimized with prefix sum and HashMap
    public int pathSumOptimized(TreeNode root, int targetSum) {
        Map<Long, Integer> prefixSumCount = new HashMap<>();
        prefixSumCount.put(0L, 1); // Base case: empty path
        return pathSumHelper(root, 0L, targetSum, prefixSumCount);
    }

    private int pathSumHelper(TreeNode node, long currentSum, int targetSum, Map<Long, Integer> prefixSumCount) {
        if (node == null)
            return 0;

        currentSum += node.val;

        // Check if there's a prefix sum that makes currentSum - prefixSum = targetSum
        int pathCount = prefixSumCount.getOrDefault(currentSum - targetSum, 0);

        // Add current sum to prefix map
        prefixSumCount.put(currentSum, prefixSumCount.getOrDefault(currentSum, 0) + 1);

        // Recursively check left and right subtrees
        pathCount += pathSumHelper(node.left, currentSum, targetSum, prefixSumCount);
        pathCount += pathSumHelper(node.right, currentSum, targetSum, prefixSumCount);

        // Remove current sum from prefix map (backtrack)
        prefixSumCount.put(currentSum, prefixSumCount.get(currentSum) - 1);

        return pathCount;
    }

    // Follow-up 1: Find the actual paths
    public List<List<Integer>> findAllPaths(TreeNode root, int targetSum) {
        List<List<Integer>> allPaths = new ArrayList<>();
        List<Integer> currentPath = new ArrayList<>();
        findAllPathsHelper(root, targetSum, currentPath, allPaths);
        return allPaths;
    }

    private void findAllPathsHelper(TreeNode node, int targetSum, List<Integer> currentPath,
            List<List<Integer>> allPaths) {
        if (node == null)
            return;

        currentPath.add(node.val);

        // Check all possible paths ending at current node
        long sum = 0;
        for (int i = currentPath.size() - 1; i >= 0; i--) {
            sum += currentPath.get(i);
            if (sum == targetSum) {
                allPaths.add(new ArrayList<>(currentPath.subList(i, currentPath.size())));
            }
        }

        findAllPathsHelper(node.left, targetSum, currentPath, allPaths);
        findAllPathsHelper(node.right, targetSum, currentPath, allPaths);

        currentPath.remove(currentPath.size() - 1);
    }

    // Follow-up 2: Handle multiple target sums
    public Map<Integer, Integer> pathSumMultipleTargets(TreeNode root, int[] targetSums) {
        Map<Integer, Integer> results = new HashMap<>();

        for (int target : targetSums) {
            results.put(target, pathSumOptimized(root, target));
        }

        return results;
    }

    // Follow-up 3: Optimize for very large trees using iterative approach
    public int pathSumIterative(TreeNode root, int targetSum) {
        if (root == null)
            return 0;

        int totalPaths = 0;
        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<List<Long>> pathSumStack = new Stack<>();

        nodeStack.push(root);
        pathSumStack.push(new ArrayList<>());

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            List<Long> pathSums = pathSumStack.pop();

            // Update all path sums with current node value
            List<Long> newPathSums = new ArrayList<>();
            newPathSums.add((long) node.val);

            for (long sum : pathSums) {
                long newSum = sum + node.val;
                newPathSums.add(newSum);
                if (newSum == targetSum)
                    totalPaths++;
            }

            if (node.val == targetSum)
                totalPaths++;

            if (node.left != null) {
                nodeStack.push(node.left);
                pathSumStack.push(new ArrayList<>(newPathSums));
            }

            if (node.right != null) {
                nodeStack.push(node.right);
                pathSumStack.push(new ArrayList<>(newPathSums));
            }
        }

        return totalPaths;
    }

    // Additional: Find longest path with target sum
    public int longestPathWithSum(TreeNode root, int targetSum) {
        return longestPathHelper(root, targetSum, new ArrayList<>());
    }

    private int longestPathHelper(TreeNode node, int targetSum, List<Integer> currentPath) {
        if (node == null)
            return 0;

        currentPath.add(node.val);
        int maxLength = 0;

        long sum = 0;
        for (int i = currentPath.size() - 1; i >= 0; i--) {
            sum += currentPath.get(i);
            if (sum == targetSum) {
                maxLength = Math.max(maxLength, currentPath.size() - i);
            }
        }

        maxLength = Math.max(maxLength, longestPathHelper(node.left, targetSum, currentPath));
        maxLength = Math.max(maxLength, longestPathHelper(node.right, targetSum, currentPath));

        currentPath.remove(currentPath.size() - 1);
        return maxLength;
    }

    // Additional: Count paths with sum in range
    public int pathSumInRange(TreeNode root, int minSum, int maxSum) {
        return pathSumRangeHelper(root, minSum, maxSum, new ArrayList<>());
    }

    private int pathSumRangeHelper(TreeNode node, int minSum, int maxSum, List<Integer> currentPath) {
        if (node == null)
            return 0;

        currentPath.add(node.val);
        int count = 0;

        long sum = 0;
        for (int i = currentPath.size() - 1; i >= 0; i--) {
            sum += currentPath.get(i);
            if (sum >= minSum && sum <= maxSum) {
                count++;
            }
        }

        count += pathSumRangeHelper(node.left, minSum, maxSum, currentPath);
        count += pathSumRangeHelper(node.right, minSum, maxSum, currentPath);

        currentPath.remove(currentPath.size() - 1);
        return count;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindPathSumIII solution = new FindPathSumIII();

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

        int targetSum = 8;
        System.out.println("Test 1 - Basic case (target: " + targetSum + "):");
        System.out.println("Path count (brute force): " + solution.pathSum(root1, targetSum));
        System.out.println("Path count (optimized): " + solution.pathSumOptimized(root1, targetSum));
        System.out.println("Path count (iterative): " + solution.pathSumIterative(root1, targetSum));

        // Test case 2: Find actual paths
        System.out.println("\nTest 2 - Actual paths:");
        List<List<Integer>> paths = solution.findAllPaths(root1, targetSum);
        for (int i = 0; i < paths.size(); i++) {
            System.out.println("Path " + (i + 1) + ": " + paths.get(i));
        }

        // Test case 3: Multiple target sums
        int[] targets = { 8, 11, 5 };
        System.out.println("\nTest 3 - Multiple targets:");
        Map<Integer, Integer> multiResults = solution.pathSumMultipleTargets(root1, targets);
        for (Map.Entry<Integer, Integer> entry : multiResults.entrySet()) {
            System.out.println("Target " + entry.getKey() + ": " + entry.getValue() + " paths");
        }

        // Test case 4: Longest path with sum
        System.out.println("\nTest 4 - Longest path with sum " + targetSum + ": " +
                solution.longestPathWithSum(root1, targetSum));

        // Test case 5: Paths with sum in range
        System.out.println("\nTest 5 - Paths with sum in range [5, 10]: " +
                solution.pathSumInRange(root1, 5, 10));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Empty tree: " + solution.pathSum(null, 5));

        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node (target 5): " + solution.pathSum(singleNode, 5));
        System.out.println("Single node (target 3): " + solution.pathSum(singleNode, 3));

        TreeNode negativeValues = new TreeNode(-1);
        negativeValues.left = new TreeNode(-2);
        negativeValues.right = new TreeNode(-3);
        System.out.println("Negative values (target -3): " + solution.pathSum(negativeValues, -3));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(500);

        long start = System.nanoTime();
        int result = solution.pathSumOptimized(largeTree, 100);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(50);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode(rand.nextInt(200) - 100);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(200) - 100);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
