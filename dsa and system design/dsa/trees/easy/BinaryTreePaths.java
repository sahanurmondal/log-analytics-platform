package trees.easy;

import java.util.*;

/**
 * LeetCode 257: Binary Tree Paths
 * https://leetcode.com/problems/binary-tree-paths/
 * 
 * Companies: Google, Amazon, Microsoft, Facebook, Apple, Bloomberg
 * Frequency: High (Asked in 800+ interviews)
 *
 * Description:
 * Given the root of a binary tree, return all root-to-leaf paths in any order.
 * A leaf is a node with no children.
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 100].
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. What if we need to find paths with a specific sum?
 * 2. Can you find the longest path in the tree?
 * 3. How would you find all paths between any two nodes?
 * 4. Can you find paths with specific patterns or conditions?
 * 5. What about finding the shortest path to a target node?
 * 6. How to handle weighted trees with path costs?
 */
public class BinaryTreePaths {

    // Definition for a binary tree node
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

    // Approach 1: DFS with String concatenation - O(n) time, O(h) space
    public static List<String> binaryTreePaths(TreeNode root) {
        List<String> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        dfs(root, "", result);
        return result;
    }

    private static void dfs(TreeNode node, String path, List<String> result) {
        if (node == null) {
            return;
        }

        // Add current node to path
        String currentPath = path.isEmpty() ? String.valueOf(node.val) : path + "->" + node.val;

        // If it's a leaf node, add to result
        if (node.left == null && node.right == null) {
            result.add(currentPath);
            return;
        }

        // Continue DFS on children
        dfs(node.left, currentPath, result);
        dfs(node.right, currentPath, result);
    }

    // Approach 2: DFS with StringBuilder (more efficient) - O(n) time, O(h) space
    public static List<String> binaryTreePathsStringBuilder(TreeNode root) {
        List<String> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        dfsStringBuilder(root, new StringBuilder(), result);
        return result;
    }

    private static void dfsStringBuilder(TreeNode node, StringBuilder path, List<String> result) {
        if (node == null) {
            return;
        }

        int length = path.length();

        // Add current node to path
        if (length > 0) {
            path.append("->");
        }
        path.append(node.val);

        // If it's a leaf node, add to result
        if (node.left == null && node.right == null) {
            result.add(path.toString());
        } else {
            // Continue DFS on children
            dfsStringBuilder(node.left, path, result);
            dfsStringBuilder(node.right, path, result);
        }

        // Backtrack
        path.setLength(length);
    }

    // Approach 3: DFS with List (for numerical paths) - O(n) time, O(h) space
    public static List<List<Integer>> binaryTreePathsAsLists(TreeNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        List<Integer> path = new ArrayList<>();
        dfsWithList(root, path, result);
        return result;
    }

    private static void dfsWithList(TreeNode node, List<Integer> path, List<List<Integer>> result) {
        if (node == null) {
            return;
        }

        // Add current node to path
        path.add(node.val);

        // If it's a leaf node, add to result
        if (node.left == null && node.right == null) {
            result.add(new ArrayList<>(path));
        } else {
            // Continue DFS on children
            dfsWithList(node.left, path, result);
            dfsWithList(node.right, path, result);
        }

        // Backtrack
        path.remove(path.size() - 1);
    }

    // Approach 4: Iterative BFS - O(n) time, O(n) space
    public static List<String> binaryTreePathsIterative(TreeNode root) {
        List<String> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Queue<TreeNode> nodeQueue = new LinkedList<>();
        Queue<String> pathQueue = new LinkedList<>();

        nodeQueue.offer(root);
        pathQueue.offer(String.valueOf(root.val));

        while (!nodeQueue.isEmpty()) {
            TreeNode node = nodeQueue.poll();
            String path = pathQueue.poll();

            // If it's a leaf node, add to result
            if (node.left == null && node.right == null) {
                result.add(path);
                continue;
            }

            // Add children to queues
            if (node.left != null) {
                nodeQueue.offer(node.left);
                pathQueue.offer(path + "->" + node.left.val);
            }

            if (node.right != null) {
                nodeQueue.offer(node.right);
                pathQueue.offer(path + "->" + node.right.val);
            }
        }

        return result;
    }

    // Approach 5: Morris Traversal inspired approach - O(n) time, O(1) extra space
    public static List<String> binaryTreePathsMorris(TreeNode root) {
        List<String> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        // Use modified preorder traversal with parent tracking
        morrisTraversal(root, result);
        return result;
    }

    private static void morrisTraversal(TreeNode root, List<String> result) {
        Stack<TreeNode> stack = new Stack<>();
        Stack<String> pathStack = new Stack<>();

        TreeNode current = root;
        String currentPath = "";

        while (current != null || !stack.isEmpty()) {
            if (current != null) {
                // Build path
                currentPath = currentPath.isEmpty() ? String.valueOf(current.val) : currentPath + "->" + current.val;

                // Check if leaf
                if (current.left == null && current.right == null) {
                    result.add(currentPath);
                }

                // Push to stack for backtracking
                if (current.right != null) {
                    stack.push(current.right);
                    pathStack.push(currentPath);
                }

                current = current.left;
            } else {
                current = stack.pop();
                currentPath = pathStack.pop();
            }
        }
    }

    // Follow-up 1: Paths with specific sum
    public static class PathSum {

        public static List<List<Integer>> pathSum(TreeNode root, int targetSum) {
            List<List<Integer>> result = new ArrayList<>();
            if (root == null) {
                return result;
            }

            List<Integer> path = new ArrayList<>();
            pathSumHelper(root, targetSum, path, result);
            return result;
        }

        private static void pathSumHelper(TreeNode node, int remainingSum,
                List<Integer> path, List<List<Integer>> result) {
            if (node == null) {
                return;
            }

            path.add(node.val);
            remainingSum -= node.val;

            // If it's a leaf and sum matches
            if (node.left == null && node.right == null && remainingSum == 0) {
                result.add(new ArrayList<>(path));
            } else {
                pathSumHelper(node.left, remainingSum, path, result);
                pathSumHelper(node.right, remainingSum, path, result);
            }

            path.remove(path.size() - 1);
        }

        public static boolean hasPathSum(TreeNode root, int targetSum) {
            if (root == null) {
                return false;
            }

            if (root.left == null && root.right == null) {
                return root.val == targetSum;
            }

            return hasPathSum(root.left, targetSum - root.val) ||
                    hasPathSum(root.right, targetSum - root.val);
        }

        public static int pathSumCount(TreeNode root, int targetSum) {
            if (root == null) {
                return 0;
            }

            return pathSumFrom(root, targetSum) +
                    pathSumCount(root.left, targetSum) +
                    pathSumCount(root.right, targetSum);
        }

        private static int pathSumFrom(TreeNode node, int targetSum) {
            if (node == null) {
                return 0;
            }

            int count = 0;
            if (node.val == targetSum) {
                count++;
            }

            count += pathSumFrom(node.left, targetSum - node.val);
            count += pathSumFrom(node.right, targetSum - node.val);

            return count;
        }
    }

    // Follow-up 2: Longest path in tree
    public static class LongestPath {

        public static List<Integer> longestPath(TreeNode root) {
            if (root == null) {
                return new ArrayList<>();
            }

            List<List<Integer>> allPaths = binaryTreePathsAsLists(root);

            List<Integer> longest = new ArrayList<>();
            for (List<Integer> path : allPaths) {
                if (path.size() > longest.size()) {
                    longest = path;
                }
            }

            return longest;
        }

        public static int longestPathLength(TreeNode root) {
            if (root == null) {
                return 0;
            }

            return 1 + Math.max(longestPathLength(root.left), longestPathLength(root.right));
        }

        public static int diameter(TreeNode root) {
            int[] maxDiameter = { 0 };
            diameterHelper(root, maxDiameter);
            return maxDiameter[0];
        }

        private static int diameterHelper(TreeNode node, int[] maxDiameter) {
            if (node == null) {
                return 0;
            }

            int leftHeight = diameterHelper(node.left, maxDiameter);
            int rightHeight = diameterHelper(node.right, maxDiameter);

            // Update diameter (path through current node)
            maxDiameter[0] = Math.max(maxDiameter[0], leftHeight + rightHeight);

            return 1 + Math.max(leftHeight, rightHeight);
        }
    }

    // Follow-up 3: All paths between two nodes
    public static class PathBetweenNodes {

        public static List<Integer> pathBetween(TreeNode root, int start, int end) {
            List<Integer> pathToStart = pathToNode(root, start);
            List<Integer> pathToEnd = pathToNode(root, end);

            if (pathToStart.isEmpty() || pathToEnd.isEmpty()) {
                return new ArrayList<>();
            }

            // Find LCA
            int lcaIndex = findLCAIndex(pathToStart, pathToEnd);

            // Build path: start -> LCA -> end
            List<Integer> result = new ArrayList<>();

            // Add path from start to LCA (reverse)
            for (int i = pathToStart.size() - 1; i > lcaIndex; i--) {
                result.add(pathToStart.get(i));
            }

            // Add LCA
            result.add(pathToStart.get(lcaIndex));

            // Add path from LCA to end
            for (int i = lcaIndex + 1; i < pathToEnd.size(); i++) {
                result.add(pathToEnd.get(i));
            }

            return result;
        }

        private static List<Integer> pathToNode(TreeNode root, int target) {
            List<Integer> path = new ArrayList<>();
            if (findPath(root, target, path)) {
                return path;
            }
            return new ArrayList<>();
        }

        private static boolean findPath(TreeNode node, int target, List<Integer> path) {
            if (node == null) {
                return false;
            }

            path.add(node.val);

            if (node.val == target) {
                return true;
            }

            if (findPath(node.left, target, path) || findPath(node.right, target, path)) {
                return true;
            }

            path.remove(path.size() - 1);
            return false;
        }

        private static int findLCAIndex(List<Integer> path1, List<Integer> path2) {
            int i = 0;
            while (i < path1.size() && i < path2.size() &&
                    path1.get(i).equals(path2.get(i))) {
                i++;
            }
            return i - 1;
        }

        public static List<List<Integer>> allPathsBetween(TreeNode root, int start, int end) {
            List<List<Integer>> result = new ArrayList<>();
            List<Integer> currentPath = new ArrayList<>();

            findAllPathsBetween(root, start, end, currentPath, result, false);
            return result;
        }

        private static void findAllPathsBetween(TreeNode node, int start, int end,
                List<Integer> currentPath, List<List<Integer>> result,
                boolean foundStart) {
            if (node == null) {
                return;
            }

            currentPath.add(node.val);

            if (node.val == start) {
                foundStart = true;
            }

            if (foundStart && node.val == end) {
                result.add(new ArrayList<>(currentPath));
            } else {
                findAllPathsBetween(node.left, start, end, currentPath, result, foundStart);
                findAllPathsBetween(node.right, start, end, currentPath, result, foundStart);
            }

            currentPath.remove(currentPath.size() - 1);
        }
    }

    // Follow-up 4: Paths with specific patterns
    public static class PatternPaths {

        public static List<List<Integer>> pathsWithPattern(TreeNode root, List<Integer> pattern) {
            List<List<Integer>> result = new ArrayList<>();
            if (root == null || pattern.isEmpty()) {
                return result;
            }

            List<Integer> currentPath = new ArrayList<>();
            findPatternPaths(root, pattern, 0, currentPath, result);
            return result;
        }

        private static void findPatternPaths(TreeNode node, List<Integer> pattern, int patternIndex,
                List<Integer> currentPath, List<List<Integer>> result) {
            if (node == null) {
                return;
            }

            currentPath.add(node.val);

            if (node.val == pattern.get(patternIndex)) {
                patternIndex++;
                if (patternIndex == pattern.size()) {
                    result.add(new ArrayList<>(currentPath));
                    patternIndex--; // Allow overlapping patterns
                }
            } else {
                patternIndex = node.val == pattern.get(0) ? 1 : 0;
            }

            findPatternPaths(node.left, pattern, patternIndex, currentPath, result);
            findPatternPaths(node.right, pattern, patternIndex, currentPath, result);

            currentPath.remove(currentPath.size() - 1);
        }

        public static List<List<Integer>> increasingPaths(TreeNode root) {
            List<List<Integer>> result = new ArrayList<>();
            if (root == null) {
                return result;
            }

            List<Integer> currentPath = new ArrayList<>();
            findIncreasingPaths(root, Integer.MIN_VALUE, currentPath, result);
            return result;
        }

        private static void findIncreasingPaths(TreeNode node, int lastValue,
                List<Integer> currentPath, List<List<Integer>> result) {
            if (node == null) {
                return;
            }

            if (node.val > lastValue) {
                currentPath.add(node.val);

                if (node.left == null && node.right == null) {
                    if (currentPath.size() > 1) {
                        result.add(new ArrayList<>(currentPath));
                    }
                } else {
                    findIncreasingPaths(node.left, node.val, currentPath, result);
                    findIncreasingPaths(node.right, node.val, currentPath, result);
                }

                currentPath.remove(currentPath.size() - 1);
            }
        }

        public static List<List<Integer>> evenSumPaths(TreeNode root) {
            List<List<Integer>> result = new ArrayList<>();
            if (root == null) {
                return result;
            }

            List<Integer> currentPath = new ArrayList<>();
            findEvenSumPaths(root, 0, currentPath, result);
            return result;
        }

        private static void findEvenSumPaths(TreeNode node, int currentSum,
                List<Integer> currentPath, List<List<Integer>> result) {
            if (node == null) {
                return;
            }

            currentPath.add(node.val);
            currentSum += node.val;

            if (node.left == null && node.right == null && currentSum % 2 == 0) {
                result.add(new ArrayList<>(currentPath));
            } else {
                findEvenSumPaths(node.left, currentSum, currentPath, result);
                findEvenSumPaths(node.right, currentSum, currentPath, result);
            }

            currentPath.remove(currentPath.size() - 1);
        }
    }

    // Follow-up 5: Shortest path to target
    public static class ShortestPath {

        public static List<Integer> shortestPathToTarget(TreeNode root, int target) {
            if (root == null) {
                return new ArrayList<>();
            }

            Queue<TreeNode> nodeQueue = new LinkedList<>();
            Queue<List<Integer>> pathQueue = new LinkedList<>();

            nodeQueue.offer(root);
            pathQueue.offer(Arrays.asList(root.val));

            while (!nodeQueue.isEmpty()) {
                TreeNode node = nodeQueue.poll();
                List<Integer> path = pathQueue.poll();

                if (node.val == target) {
                    return path;
                }

                if (node.left != null) {
                    nodeQueue.offer(node.left);
                    List<Integer> leftPath = new ArrayList<>(path);
                    leftPath.add(node.left.val);
                    pathQueue.offer(leftPath);
                }

                if (node.right != null) {
                    nodeQueue.offer(node.right);
                    List<Integer> rightPath = new ArrayList<>(path);
                    rightPath.add(node.right.val);
                    pathQueue.offer(rightPath);
                }
            }

            return new ArrayList<>();
        }

        public static int shortestDistance(TreeNode root, int target) {
            if (root == null) {
                return -1;
            }

            if (root.val == target) {
                return 0;
            }

            Queue<TreeNode> queue = new LinkedList<>();
            Queue<Integer> distances = new LinkedList<>();

            queue.offer(root);
            distances.offer(0);

            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();
                int distance = distances.poll();

                if (node.val == target) {
                    return distance;
                }

                if (node.left != null) {
                    queue.offer(node.left);
                    distances.offer(distance + 1);
                }

                if (node.right != null) {
                    queue.offer(node.right);
                    distances.offer(distance + 1);
                }
            }

            return -1;
        }

        public static List<List<Integer>> allShortestPaths(TreeNode root, int target) {
            List<List<Integer>> result = new ArrayList<>();
            if (root == null) {
                return result;
            }

            int shortestDist = shortestDistance(root, target);
            if (shortestDist == -1) {
                return result;
            }

            List<Integer> currentPath = new ArrayList<>();
            findAllShortestPaths(root, target, shortestDist, 0, currentPath, result);

            return result;
        }

        private static void findAllShortestPaths(TreeNode node, int target, int targetDist,
                int currentDist, List<Integer> currentPath,
                List<List<Integer>> result) {
            if (node == null || currentDist > targetDist) {
                return;
            }

            currentPath.add(node.val);

            if (node.val == target && currentDist == targetDist) {
                result.add(new ArrayList<>(currentPath));
            } else {
                findAllShortestPaths(node.left, target, targetDist, currentDist + 1, currentPath, result);
                findAllShortestPaths(node.right, target, targetDist, currentDist + 1, currentPath, result);
            }

            currentPath.remove(currentPath.size() - 1);
        }
    }

    // Follow-up 6: Weighted trees with path costs
    public static class WeightedTree {

        public static class WeightedTreeNode {
            int val;
            int weight;
            WeightedTreeNode left;
            WeightedTreeNode right;

            WeightedTreeNode(int val, int weight) {
                this.val = val;
                this.weight = weight;
            }
        }

        public static class PathWithCost {
            List<Integer> path;
            int cost;

            PathWithCost(List<Integer> path, int cost) {
                this.path = new ArrayList<>(path);
                this.cost = cost;
            }
        }

        public static List<PathWithCost> allPathsWithCosts(WeightedTreeNode root) {
            List<PathWithCost> result = new ArrayList<>();
            if (root == null) {
                return result;
            }

            List<Integer> currentPath = new ArrayList<>();
            findPathsWithCosts(root, 0, currentPath, result);
            return result;
        }

        private static void findPathsWithCosts(WeightedTreeNode node, int currentCost,
                List<Integer> currentPath, List<PathWithCost> result) {
            if (node == null) {
                return;
            }

            currentPath.add(node.val);
            currentCost += node.weight;

            if (node.left == null && node.right == null) {
                result.add(new PathWithCost(currentPath, currentCost));
            } else {
                findPathsWithCosts(node.left, currentCost, currentPath, result);
                findPathsWithCosts(node.right, currentCost, currentPath, result);
            }

            currentPath.remove(currentPath.size() - 1);
        }

        public static PathWithCost cheapestPath(WeightedTreeNode root) {
            List<PathWithCost> allPaths = allPathsWithCosts(root);
            if (allPaths.isEmpty()) {
                return null;
            }

            PathWithCost cheapest = allPaths.get(0);
            for (PathWithCost path : allPaths) {
                if (path.cost < cheapest.cost) {
                    cheapest = path;
                }
            }

            return cheapest;
        }

        public static PathWithCost mostExpensivePath(WeightedTreeNode root) {
            List<PathWithCost> allPaths = allPathsWithCosts(root);
            if (allPaths.isEmpty()) {
                return null;
            }

            PathWithCost mostExpensive = allPaths.get(0);
            for (PathWithCost path : allPaths) {
                if (path.cost > mostExpensive.cost) {
                    mostExpensive = path;
                }
            }

            return mostExpensive;
        }
    }

    // Utility methods for testing
    public static TreeNode createSampleTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.right = new TreeNode(5);
        return root;
    }

    public static TreeNode createComplexTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
        root.left.right = new TreeNode(5);
        root.right.left = new TreeNode(6);
        root.right.right = new TreeNode(7);
        return root;
    }

    public static void printPaths(List<String> paths) {
        System.out.println("Paths:");
        for (String path : paths) {
            System.out.println("  " + path);
        }
    }

    public static void printPathsAsLists(List<List<Integer>> paths) {
        System.out.println("Paths:");
        for (List<Integer> path : paths) {
            System.out.println("  " + path);
        }
    }

    // Performance comparison utility
    public static void compareApproaches(TreeNode root) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 10000;

        // DFS with String concatenation
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            binaryTreePaths(root);
        }
        end = System.nanoTime();
        System.out.println("DFS String concatenation: " + (end - start) / 1_000_000 + " ms");

        // DFS with StringBuilder
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            binaryTreePathsStringBuilder(root);
        }
        end = System.nanoTime();
        System.out.println("DFS StringBuilder: " + (end - start) / 1_000_000 + " ms");

        // Iterative BFS
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            binaryTreePathsIterative(root);
        }
        end = System.nanoTime();
        System.out.println("Iterative BFS: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        TreeNode root1 = createSampleTree();
        List<String> paths1 = binaryTreePaths(root1);
        List<String> paths2 = binaryTreePathsStringBuilder(root1);
        List<String> paths3 = binaryTreePathsIterative(root1);

        System.out.println("Sample tree paths:");
        printPaths(paths1);

        System.out.println("\nVerifying consistency:");
        System.out.println("String concat == StringBuilder: " + paths1.equals(paths2));
        System.out.println("StringBuilder == Iterative: " + paths2.equals(paths3));

        // Test Case 2: Complex tree
        System.out.println("\n=== Test Case 2: Complex Tree ===");

        TreeNode root2 = createComplexTree();
        List<List<Integer>> numericPaths = binaryTreePathsAsLists(root2);

        System.out.println("Complex tree paths:");
        printPathsAsLists(numericPaths);

        // Test Case 3: Path sum
        System.out.println("\n=== Test Case 3: Path Sum ===");

        TreeNode sumTree = new TreeNode(5);
        sumTree.left = new TreeNode(4);
        sumTree.right = new TreeNode(8);
        sumTree.left.left = new TreeNode(11);
        sumTree.left.left.left = new TreeNode(7);
        sumTree.left.left.right = new TreeNode(2);
        sumTree.right.left = new TreeNode(13);
        sumTree.right.right = new TreeNode(4);
        sumTree.right.right.right = new TreeNode(1);

        int targetSum = 22;
        List<List<Integer>> pathSumResult = PathSum.pathSum(sumTree, targetSum);
        boolean hasPath = PathSum.hasPathSum(sumTree, targetSum);
        int pathCount = PathSum.pathSumCount(sumTree, targetSum);

        System.out.println("Target sum: " + targetSum);
        System.out.println("Has path with sum: " + hasPath);
        System.out.println("Number of paths with sum: " + pathCount);
        System.out.println("Paths with target sum:");
        printPathsAsLists(pathSumResult);

        // Test Case 4: Longest path
        System.out.println("\n=== Test Case 4: Longest Path ===");

        List<Integer> longestPath = LongestPath.longestPath(root2);
        int longestLength = LongestPath.longestPathLength(root2);
        int diameter = LongestPath.diameter(root2);

        System.out.println("Longest path: " + longestPath);
        System.out.println("Longest path length: " + longestLength);
        System.out.println("Tree diameter: " + diameter);

        // Test Case 5: Path between nodes
        System.out.println("\n=== Test Case 5: Path Between Nodes ===");

        List<Integer> pathBetween = PathBetweenNodes.pathBetween(root2, 4, 7);
        List<List<Integer>> allPathsBetween = PathBetweenNodes.allPathsBetween(root2, 2, 3);

        System.out.println("Path between nodes 4 and 7: " + pathBetween);
        System.out.println("All paths from 2 to 3:");
        printPathsAsLists(allPathsBetween);

        // Test Case 6: Pattern paths
        System.out.println("\n=== Test Case 6: Pattern Paths ===");

        List<Integer> pattern = Arrays.asList(1, 2);
        List<List<Integer>> patternPaths = PatternPaths.pathsWithPattern(root2, pattern);
        List<List<Integer>> increasingPaths = PatternPaths.increasingPaths(root2);
        List<List<Integer>> evenSumPaths = PatternPaths.evenSumPaths(root2);

        System.out.println("Paths with pattern " + pattern + ":");
        printPathsAsLists(patternPaths);
        System.out.println("Increasing paths:");
        printPathsAsLists(increasingPaths);
        System.out.println("Even sum paths:");
        printPathsAsLists(evenSumPaths);

        // Test Case 7: Shortest path
        System.out.println("\n=== Test Case 7: Shortest Path ===");

        int target = 6;
        List<Integer> shortestPath = ShortestPath.shortestPathToTarget(root2, target);
        int shortestDist = ShortestPath.shortestDistance(root2, target);
        List<List<Integer>> allShortestPaths = ShortestPath.allShortestPaths(root2, target);

        System.out.println("Target: " + target);
        System.out.println("Shortest path: " + shortestPath);
        System.out.println("Shortest distance: " + shortestDist);
        System.out.println("All shortest paths:");
        printPathsAsLists(allShortestPaths);

        // Test Case 8: Weighted tree
        System.out.println("\n=== Test Case 8: Weighted Tree ===");

        WeightedTree.WeightedTreeNode weightedRoot = new WeightedTree.WeightedTreeNode(1, 5);
        weightedRoot.left = new WeightedTree.WeightedTreeNode(2, 3);
        weightedRoot.right = new WeightedTree.WeightedTreeNode(3, 2);
        weightedRoot.left.left = new WeightedTree.WeightedTreeNode(4, 1);
        weightedRoot.left.right = new WeightedTree.WeightedTreeNode(5, 4);

        List<WeightedTree.PathWithCost> pathsWithCosts = WeightedTree.allPathsWithCosts(weightedRoot);
        WeightedTree.PathWithCost cheapest = WeightedTree.cheapestPath(weightedRoot);
        WeightedTree.PathWithCost mostExpensive = WeightedTree.mostExpensivePath(weightedRoot);

        System.out.println("All paths with costs:");
        for (WeightedTree.PathWithCost pathCost : pathsWithCosts) {
            System.out.println("  Path: " + pathCost.path + ", Cost: " + pathCost.cost);
        }

        if (cheapest != null) {
            System.out.println("Cheapest path: " + cheapest.path + ", Cost: " + cheapest.cost);
        }

        if (mostExpensive != null) {
            System.out.println("Most expensive path: " + mostExpensive.path + ", Cost: " + mostExpensive.cost);
        }

        // Test Case 9: Performance comparison
        System.out.println("\n=== Test Case 9: Performance Comparison ===");

        compareApproaches(root2);

        // Test Case 10: Edge cases
        System.out.println("\n=== Test Case 10: Edge Cases ===");

        // Single node
        TreeNode singleNode = new TreeNode(1);
        List<String> singlePaths = binaryTreePaths(singleNode);
        System.out.println("Single node paths: " + singlePaths);

        // Empty tree
        List<String> emptyPaths = binaryTreePaths(null);
        System.out.println("Empty tree paths: " + emptyPaths);

        // Linear tree
        TreeNode linearRoot = new TreeNode(1);
        linearRoot.left = new TreeNode(2);
        linearRoot.left.left = new TreeNode(3);
        linearRoot.left.left.left = new TreeNode(4);

        List<String> linearPaths = binaryTreePaths(linearRoot);
        System.out.println("Linear tree paths: " + linearPaths);

        System.out.println("\nBinary Tree Paths testing completed successfully!");
    }
}
