package trees.medium;

import java.util.*;

/**
 * Advanced Variation: Sum of Right Leaves
 * 
 * Description: Given the root of a binary tree, return the sum of all right
 * leaves.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 1000]
 * - -1000 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you find the product of right leaves?
 * 2. Can you find right leaves at even levels only?
 * 3. Can you handle trees with duplicate values?
 */
public class FindSumOfRightLeaves {

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

    // Approach 1: Recursive DFS
    public int sumOfRightLeaves(TreeNode root) {
        return sumOfRightLeavesHelper(root, false);
    }

    private int sumOfRightLeavesHelper(TreeNode node, boolean isRight) {
        if (node == null)
            return 0;

        // If it's a right leaf
        if (isRight && node.left == null && node.right == null) {
            return node.val;
        }

        return sumOfRightLeavesHelper(node.left, false) +
                sumOfRightLeavesHelper(node.right, true);
    }

    // Follow-up 1: Product of right leaves
    public long productOfRightLeaves(TreeNode root) {
        List<Integer> rightLeaves = new ArrayList<>();
        collectRightLeaves(root, false, rightLeaves);

        if (rightLeaves.isEmpty())
            return 0;

        long product = 1;
        for (int leaf : rightLeaves) {
            product *= leaf;
        }
        return product;
    }

    private void collectRightLeaves(TreeNode node, boolean isRight, List<Integer> rightLeaves) {
        if (node == null)
            return;

        if (isRight && node.left == null && node.right == null) {
            rightLeaves.add(node.val);
            return;
        }

        collectRightLeaves(node.left, false, rightLeaves);
        collectRightLeaves(node.right, true, rightLeaves);
    }

    // Follow-up 2: Right leaves at even levels only
    public int sumOfRightLeavesAtEvenLevels(TreeNode root) {
        return sumRightLeavesEvenLevels(root, false, 1);
    }

    private int sumRightLeavesEvenLevels(TreeNode node, boolean isRight, int level) {
        if (node == null)
            return 0;

        if (isRight && node.left == null && node.right == null && level % 2 == 0) {
            return node.val;
        }

        return sumRightLeavesEvenLevels(node.left, false, level + 1) +
                sumRightLeavesEvenLevels(node.right, true, level + 1);
    }

    // Follow-up 3: Handle trees with duplicate values (collect unique right leaves)
    public int sumOfUniqueRightLeaves(TreeNode root) {
        Set<Integer> uniqueRightLeaves = new HashSet<>();
        collectUniqueRightLeaves(root, false, uniqueRightLeaves);

        return uniqueRightLeaves.stream().mapToInt(Integer::intValue).sum();
    }

    private void collectUniqueRightLeaves(TreeNode node, boolean isRight, Set<Integer> uniqueLeaves) {
        if (node == null)
            return;

        if (isRight && node.left == null && node.right == null) {
            uniqueLeaves.add(node.val);
            return;
        }

        collectUniqueRightLeaves(node.left, false, uniqueLeaves);
        collectUniqueRightLeaves(node.right, true, uniqueLeaves);
    }

    // Additional: Iterative approach
    public int sumOfRightLeavesIterative(TreeNode root) {
        if (root == null)
            return 0;

        Stack<TreeNode> nodeStack = new Stack<>();
        Stack<Boolean> isRightStack = new Stack<>();

        nodeStack.push(root);
        isRightStack.push(false);
        int sum = 0;

        while (!nodeStack.isEmpty()) {
            TreeNode node = nodeStack.pop();
            boolean isRight = isRightStack.pop();

            if (isRight && node.left == null && node.right == null) {
                sum += node.val;
            }

            if (node.left != null) {
                nodeStack.push(node.left);
                isRightStack.push(false);
            }

            if (node.right != null) {
                nodeStack.push(node.right);
                isRightStack.push(true);
            }
        }

        return sum;
    }

    // Additional: Get all right leaves with their levels
    public Map<Integer, List<Integer>> getRightLeavesByLevel(TreeNode root) {
        Map<Integer, List<Integer>> result = new HashMap<>();
        getRightLeavesByLevelHelper(root, false, 1, result);
        return result;
    }

    private void getRightLeavesByLevelHelper(TreeNode node, boolean isRight, int level,
            Map<Integer, List<Integer>> result) {
        if (node == null)
            return;

        if (isRight && node.left == null && node.right == null) {
            result.computeIfAbsent(level, k -> new ArrayList<>()).add(node.val);
            return;
        }

        getRightLeavesByLevelHelper(node.left, false, level + 1, result);
        getRightLeavesByLevelHelper(node.right, true, level + 1, result);
    }

    // Additional: Count right leaves
    public int countRightLeaves(TreeNode root) {
        return countRightLeavesHelper(root, false);
    }

    private int countRightLeavesHelper(TreeNode node, boolean isRight) {
        if (node == null)
            return 0;

        if (isRight && node.left == null && node.right == null) {
            return 1;
        }

        return countRightLeavesHelper(node.left, false) +
                countRightLeavesHelper(node.right, true);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindSumOfRightLeaves solution = new FindSumOfRightLeaves();

        // Test case 1: Basic case
        TreeNode root1 = new TreeNode(3);
        root1.left = new TreeNode(9);
        root1.right = new TreeNode(20);
        root1.right.left = new TreeNode(15);
        root1.right.right = new TreeNode(7);

        System.out.println("Test 1 - Basic tree:");
        System.out.println("Sum of right leaves: " + solution.sumOfRightLeaves(root1));
        System.out.println("Sum of right leaves (iterative): " + solution.sumOfRightLeavesIterative(root1));
        System.out.println("Count of right leaves: " + solution.countRightLeaves(root1));
        System.out.println("Product of right leaves: " + solution.productOfRightLeaves(root1));

        // Test case 2: Right leaves at even levels
        System.out.println("\nTest 2 - Right leaves at even levels:");
        System.out.println("Sum at even levels: " + solution.sumOfRightLeavesAtEvenLevels(root1));

        // Test case 3: Right leaves by level
        System.out.println("\nTest 3 - Right leaves by level:");
        Map<Integer, List<Integer>> rightLeavesByLevel = solution.getRightLeavesByLevel(root1);
        for (Map.Entry<Integer, List<Integer>> entry : rightLeavesByLevel.entrySet()) {
            System.out.println("Level " + entry.getKey() + ": " + entry.getValue());
        }

        // Test case 4: Tree with duplicates
        TreeNode root2 = new TreeNode(1);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(3);
        root2.left.right = new TreeNode(7);
        root2.right.right = new TreeNode(7);

        System.out.println("\nTest 4 - Tree with duplicates:");
        System.out.println("Sum of all right leaves: " + solution.sumOfRightLeaves(root2));
        System.out.println("Sum of unique right leaves: " + solution.sumOfUniqueRightLeaves(root2));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(5);
        System.out.println("Single node: " + solution.sumOfRightLeaves(singleNode));

        TreeNode onlyLeftChildren = new TreeNode(1);
        onlyLeftChildren.left = new TreeNode(2);
        onlyLeftChildren.left.left = new TreeNode(3);
        System.out.println("Only left children: " + solution.sumOfRightLeaves(onlyLeftChildren));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeTree(1000);

        long start = System.nanoTime();
        int result = solution.sumOfRightLeaves(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
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
