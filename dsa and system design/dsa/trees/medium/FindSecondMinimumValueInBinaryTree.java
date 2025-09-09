package trees.medium;

import java.util.*;

/**
 * LeetCode 671: Second Minimum Node In a Binary Tree
 * https://leetcode.com/problems/second-minimum-node-in-a-binary-tree/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a non-empty special binary tree where each node has either
 * 0 or 2 children and the value of the root is the minimum among all nodes,
 * find the second minimum value in the tree.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 25]
 * - 1 <= Node.val <= 2^31 - 1
 * - For each node, its value is the minimum of its children's values
 * 
 * Follow-up Questions:
 * 1. Can you find the k-th minimum value?
 * 2. Can you handle trees without the special property?
 * 3. Can you find all unique values in sorted order?
 */
public class FindSecondMinimumValueInBinaryTree {

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

    // Approach 1: DFS with pruning (leveraging special tree property)
    public int findSecondMinimumValue(TreeNode root) {
        if (root == null)
            return -1;
        return findSecondMin(root, root.val);
    }

    private int findSecondMin(TreeNode node, int minVal) {
        if (node == null)
            return -1;

        // If current node value is greater than minimum, it's a candidate
        if (node.val > minVal)
            return node.val;

        // Otherwise, search in children
        int leftMin = findSecondMin(node.left, minVal);
        int rightMin = findSecondMin(node.right, minVal);

        if (leftMin == -1)
            return rightMin;
        if (rightMin == -1)
            return leftMin;

        return Math.min(leftMin, rightMin);
    }

    // Follow-up 1: Find k-th minimum value
    public int findKthMinimumValue(TreeNode root, int k) {
        Set<Integer> uniqueValues = new TreeSet<>();
        collectAllValues(root, uniqueValues);

        if (uniqueValues.size() < k)
            return -1;

        int index = 0;
        for (int value : uniqueValues) {
            if (++index == k)
                return value;
        }

        return -1;
    }

    private void collectAllValues(TreeNode node, Set<Integer> values) {
        if (node == null)
            return;

        values.add(node.val);
        collectAllValues(node.left, values);
        collectAllValues(node.right, values);
    }

    // Follow-up 2: Handle trees without special property
    public int findSecondMinimumGeneral(TreeNode root) {
        Set<Integer> uniqueValues = new TreeSet<>();
        collectAllValues(root, uniqueValues);

        if (uniqueValues.size() < 2)
            return -1;

        Iterator<Integer> iterator = uniqueValues.iterator();
        iterator.next(); // Skip first minimum
        return iterator.next(); // Return second minimum
    }

    // Follow-up 3: Find all unique values in sorted order
    public List<Integer> findAllUniqueValuesSorted(TreeNode root) {
        Set<Integer> uniqueValues = new TreeSet<>();
        collectAllValues(root, uniqueValues);
        return new ArrayList<>(uniqueValues);
    }

    // Additional: Find second minimum using priority queue
    public int findSecondMinimumPQ(TreeNode root) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Collections.reverseOrder());
        findSecondMinPQ(root, maxHeap);

        return maxHeap.size() >= 2 ? maxHeap.peek() : -1;
    }

    private void findSecondMinPQ(TreeNode node, PriorityQueue<Integer> heap) {
        if (node == null)
            return;

        if (heap.size() < 2) {
            heap.offer(node.val);
        } else if (node.val < heap.peek()) {
            // Remove duplicates
            if (!heap.contains(node.val)) {
                heap.poll();
                heap.offer(node.val);
            }
        }

        findSecondMinPQ(node.left, heap);
        findSecondMinPQ(node.right, heap);
    }

    // Additional: Find minimum and second minimum in one pass
    public int[] findMinAndSecondMin(TreeNode root) {
        long min = Long.MAX_VALUE;
        long secondMin = Long.MAX_VALUE;

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();

            if (node.val < min) {
                secondMin = min;
                min = node.val;
            } else if (node.val < secondMin && node.val != min) {
                secondMin = node.val;
            }

            if (node.left != null)
                queue.offer(node.left);
            if (node.right != null)
                queue.offer(node.right);
        }

        return new int[] { (int) min, secondMin == Long.MAX_VALUE ? -1 : (int) secondMin };
    }

    // Additional: Count occurrences of each value
    public Map<Integer, Integer> countValueOccurrences(TreeNode root) {
        Map<Integer, Integer> count = new HashMap<>();
        countValues(root, count);
        return count;
    }

    private void countValues(TreeNode node, Map<Integer, Integer> count) {
        if (node == null)
            return;

        count.put(node.val, count.getOrDefault(node.val, 0) + 1);
        countValues(node.left, count);
        countValues(node.right, count);
    }

    // Additional: Validate special tree property
    public boolean isValidSpecialTree(TreeNode root) {
        if (root == null)
            return true;
        return validateSpecialProperty(root);
    }

    private boolean validateSpecialProperty(TreeNode node) {
        if (node == null)
            return true;

        // Leaf node is valid
        if (node.left == null && node.right == null)
            return true;

        // Internal node must have exactly 2 children
        if (node.left == null || node.right == null)
            return false;

        // Node value must be minimum of its children
        int minChildValue = Math.min(node.left.val, node.right.val);
        if (node.val != minChildValue)
            return false;

        return validateSpecialProperty(node.left) && validateSpecialProperty(node.right);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindSecondMinimumValueInBinaryTree solution = new FindSecondMinimumValueInBinaryTree();

        // Test case 1: Basic special tree
        TreeNode root1 = new TreeNode(2);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);

        System.out.println("Test 1 - Basic special tree:");
        System.out.println("Second minimum: " + solution.findSecondMinimumValue(root1));
        System.out.println("Is valid special tree: " + solution.isValidSpecialTree(root1));
        System.out.println("All unique values: " + solution.findAllUniqueValuesSorted(root1));

        // Test case 2: More complex special tree
        TreeNode root2 = new TreeNode(2);
        root2.left = new TreeNode(2);
        root2.right = new TreeNode(5);
        root2.right.left = new TreeNode(5);
        root2.right.right = new TreeNode(7);

        System.out.println("\nTest 2 - Complex special tree:");
        System.out.println("Second minimum: " + solution.findSecondMinimumValue(root2));
        System.out.println("3rd minimum: " + solution.findKthMinimumValue(root2, 3));

        int[] minSecondMin = solution.findMinAndSecondMin(root2);
        System.out.println("Min: " + minSecondMin[0] + ", Second min: " + minSecondMin[1]);

        // Test case 3: Value occurrences
        System.out.println("\nTest 3 - Value occurrences:");
        Map<Integer, Integer> occurrences = solution.countValueOccurrences(root2);
        for (Map.Entry<Integer, Integer> entry : occurrences.entrySet()) {
            System.out.println("Value " + entry.getKey() + ": " + entry.getValue() + " times");
        }

        // Test case 4: General tree (not special)
        TreeNode general = new TreeNode(5);
        general.left = new TreeNode(3);
        general.right = new TreeNode(7);
        general.left.left = new TreeNode(1);
        general.left.right = new TreeNode(4);

        System.out.println("\nTest 4 - General tree:");
        System.out.println("Second minimum (general): " + solution.findSecondMinimumGeneral(general));
        System.out.println("Is valid special tree: " + solution.isValidSpecialTree(general));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + solution.findSecondMinimumValue(singleNode));

        TreeNode allSame = new TreeNode(2);
        allSame.left = new TreeNode(2);
        allSame.right = new TreeNode(2);
        System.out.println("All same values: " + solution.findSecondMinimumValue(allSame));

        TreeNode twoValues = new TreeNode(1);
        twoValues.left = new TreeNode(1);
        twoValues.right = new TreeNode(2);
        twoValues.left.left = new TreeNode(1);
        twoValues.left.right = new TreeNode(1);
        System.out.println("Two distinct values: " + solution.findSecondMinimumValue(twoValues));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeSpecialTree(100);

        long start = System.nanoTime();
        int result = solution.findSecondMinimumValue(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeSpecialTree(int nodes) {
        if (nodes <= 0)
            return null;

        TreeNode root = new TreeNode(1);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        int value = 1;

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                value++;
                current.left = new TreeNode(Math.min(current.val, value));
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                value++;
                current.right = new TreeNode(Math.min(current.val, value));
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
