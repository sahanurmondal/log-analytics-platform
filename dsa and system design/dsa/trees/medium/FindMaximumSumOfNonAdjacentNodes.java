package trees.medium;

import java.util.*;

/**
 * LeetCode 337: House Robber III (Medium Version)
 * https://leetcode.com/problems/house-robber-iii/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given the root of a binary tree, return the maximum amount of
 * money you can rob without alerting police (no two directly-connected houses).
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - 0 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you track which nodes were robbed?
 * 2. Can you handle different constraint distances?
 * 3. Can you optimize for very large trees?
 */
public class FindMaximumSumOfNonAdjacentNodes {

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

    // Approach 1: DFS with memoization
    public int rob(TreeNode root) {
        int[] result = robHelper(root);
        return Math.max(result[0], result[1]);
    }

    private int[] robHelper(TreeNode node) {
        if (node == null)
            return new int[] { 0, 0 }; // {rob, notRob}

        int[] left = robHelper(node.left);
        int[] right = robHelper(node.right);

        // If we rob current node, we can't rob children
        int rob = node.val + left[1] + right[1];
        // If we don't rob current node, we can choose best from children
        int notRob = Math.max(left[0], left[1]) + Math.max(right[0], right[1]);

        return new int[] { rob, notRob };
    }

    // Follow-up 1: Track which nodes were robbed
    public List<Integer> robWithPath(TreeNode root) {
        Map<TreeNode, Boolean> robbedNodes = new HashMap<>();
        int maxMoney = robWithPathHelper(root, robbedNodes);

        List<Integer> robbedValues = new ArrayList<>();
        collectRobbedNodes(root, robbedNodes, robbedValues);
        return robbedValues;
    }

    private int robWithPathHelper(TreeNode node, Map<TreeNode, Boolean> robbedNodes) {
        if (node == null)
            return 0;

        int[] result = robHelper(node);
        boolean shouldRob = result[0] > result[1];
        robbedNodes.put(node, shouldRob);

        robWithPathHelper(node.left, robbedNodes);
        robWithPathHelper(node.right, robbedNodes);

        return Math.max(result[0], result[1]);
    }

    private void collectRobbedNodes(TreeNode node, Map<TreeNode, Boolean> robbedNodes, List<Integer> result) {
        if (node == null)
            return;

        if (robbedNodes.getOrDefault(node, false)) {
            result.add(node.val);
        }

        collectRobbedNodes(node.left, robbedNodes, result);
        collectRobbedNodes(node.right, robbedNodes, result);
    }

    // Follow-up 2: Handle different constraint distances
    public int robWithDistance(TreeNode root, int distance) {
        Map<TreeNode, Integer> memo = new HashMap<>();
        return robDistanceHelper(root, distance, memo);
    }

    private int robDistanceHelper(TreeNode node, int distance, Map<TreeNode, Integer> memo) {
        if (node == null)
            return 0;
        if (memo.containsKey(node))
            return memo.get(node);

        // Option 1: Don't rob current node
        int notRob = robDistanceHelper(node.left, distance, memo) +
                robDistanceHelper(node.right, distance, memo);

        // Option 2: Rob current node (can't rob within distance)
        int rob = node.val;
        List<TreeNode> validNodes = getNodesAtDistance(node, distance + 1);
        for (TreeNode validNode : validNodes) {
            rob += robDistanceHelper(validNode, distance, memo);
        }

        int result = Math.max(rob, notRob);
        memo.put(node, result);
        return result;
    }

    private List<TreeNode> getNodesAtDistance(TreeNode root, int distance) {
        List<TreeNode> result = new ArrayList<>();
        if (root == null || distance <= 0)
            return result;

        if (distance == 1) {
            if (root.left != null)
                result.add(root.left);
            if (root.right != null)
                result.add(root.right);
            return result;
        }

        result.addAll(getNodesAtDistance(root.left, distance - 1));
        result.addAll(getNodesAtDistance(root.right, distance - 1));
        return result;
    }

    // Follow-up 3: Optimized for very large trees using iterative approach
    public int robIterative(TreeNode root) {
        if (root == null)
            return 0;

        Map<TreeNode, Integer> robMap = new HashMap<>();
        Map<TreeNode, Integer> notRobMap = new HashMap<>();

        // Post-order traversal using stack
        Stack<TreeNode> stack = new Stack<>();
        TreeNode current = root;
        TreeNode lastVisited = null;

        while (current != null || !stack.isEmpty()) {
            if (current != null) {
                stack.push(current);
                current = current.left;
            } else {
                TreeNode peekNode = stack.peek();
                if (peekNode.right != null && lastVisited != peekNode.right) {
                    current = peekNode.right;
                } else {
                    processNode(peekNode, robMap, notRobMap);
                    lastVisited = stack.pop();
                }
            }
        }

        return Math.max(robMap.getOrDefault(root, 0), notRobMap.getOrDefault(root, 0));
    }

    private void processNode(TreeNode node, Map<TreeNode, Integer> robMap, Map<TreeNode, Integer> notRobMap) {
        int leftRob = robMap.getOrDefault(node.left, 0);
        int leftNotRob = notRobMap.getOrDefault(node.left, 0);
        int rightRob = robMap.getOrDefault(node.right, 0);
        int rightNotRob = notRobMap.getOrDefault(node.right, 0);

        robMap.put(node, node.val + leftNotRob + rightNotRob);
        notRobMap.put(node, Math.max(leftRob, leftNotRob) + Math.max(rightRob, rightNotRob));
    }

    // Additional: Get maximum sum with at least k nodes robbed
    public int robWithMinNodes(TreeNode root, int k) {
        Map<String, Integer> memo = new HashMap<>();
        int result = robMinNodesHelper(root, k, memo);
        return result == Integer.MIN_VALUE ? -1 : result;
    }

    private int robMinNodesHelper(TreeNode node, int k, Map<String, Integer> memo) {
        if (node == null)
            return k <= 0 ? 0 : Integer.MIN_VALUE;

        String key = node.hashCode() + "," + k;
        if (memo.containsKey(key))
            return memo.get(key);

        // Option 1: Rob current node
        int rob = Integer.MIN_VALUE;
        int leftRob = robMinNodesHelper(node.left, k - 1, memo);
        int rightRob = robMinNodesHelper(node.right, 0, memo);

        if (leftRob != Integer.MIN_VALUE && rightRob != Integer.MIN_VALUE) {
            rob = node.val + leftRob + rightRob;
        }

        // Option 2: Don't rob current node
        int notRob = robMinNodesHelper(node.left, k, memo);
        int rightNotRob = robMinNodesHelper(node.right, 0, memo);

        if (notRob != Integer.MIN_VALUE && rightNotRob != Integer.MIN_VALUE) {
            notRob += rightNotRob;
        } else {
            notRob = Integer.MIN_VALUE;
        }

        int result = Math.max(rob, notRob);
        memo.put(key, result);
        return result;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMaximumSumOfNonAdjacentNodes solution = new FindMaximumSumOfNonAdjacentNodes();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.right = new TreeNode(3);
        root1.right.right = new TreeNode(1);

        System.out.println("Test 1 - Basic rob: " + solution.rob(root1));

        // Test case 2: Track robbed nodes
        System.out.println("\nTest 2 - Robbed nodes: " + solution.robWithPath(root1));

        // Test case 3: Different distances
        System.out.println("\nTest 3 - Rob with distance 2: " + solution.robWithDistance(root1, 2));

        // Test case 4: Iterative approach
        System.out.println("\nTest 4 - Iterative approach: " + solution.robIterative(root1));

        // Test case 5: Minimum nodes constraint
        System.out.println("\nTest 5 - Rob with min 2 nodes: " + solution.robWithMinNodes(root1, 2));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Null root: " + solution.rob(null));

        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node: " + solution.rob(singleNode));

        TreeNode linear = new TreeNode(2);
        linear.left = new TreeNode(1);
        linear.left.left = new TreeNode(4);
        System.out.println("Linear tree: " + solution.rob(linear));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.rob(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");

        start = System.nanoTime();
        int iterResult = solution.robIterative(largeTree);
        end = System.nanoTime();
        System.out.println("Iterative result: " + iterResult + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(100);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                current.left = new TreeNode(rand.nextInt(100) + 1);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                current.right = new TreeNode(rand.nextInt(100) + 1);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
