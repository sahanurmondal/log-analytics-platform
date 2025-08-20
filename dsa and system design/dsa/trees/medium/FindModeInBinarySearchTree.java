package trees.medium;

import java.util.*;

/**
 * LeetCode 501: Find Mode in Binary Search Tree
 * https://leetcode.com/problems/find-mode-in-binary-search-tree/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given the root of a binary search tree (BST) with duplicates,
 * return all the mode(s) (i.e., the most frequently occurred element) in it.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [1, 10^4]
 * - -10^5 <= Node.val <= 10^5
 * 
 * Follow-up Questions:
 * 1. Can you do it without using extra space (no HashMap)?
 * 2. Can you find the k most frequent elements?
 * 3. Can you handle the case where there are no duplicates?
 */
public class FindModeInBinarySearchTree {

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

    // Approach 1: Using HashMap
    public int[] findMode(TreeNode root) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        inorderTraversal(root, frequencyMap);

        int maxFreq = Collections.max(frequencyMap.values());
        List<Integer> modes = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() == maxFreq) {
                modes.add(entry.getKey());
            }
        }

        return modes.stream().mapToInt(i -> i).toArray();
    }

    private void inorderTraversal(TreeNode node, Map<Integer, Integer> frequencyMap) {
        if (node == null)
            return;

        inorderTraversal(node.left, frequencyMap);
        frequencyMap.put(node.val, frequencyMap.getOrDefault(node.val, 0) + 1);
        inorderTraversal(node.right, frequencyMap);
    }

    // Follow-up 1: Without extra space (O(1) space)
    private List<Integer> modes = new ArrayList<>();
    private int currentVal = 0;
    private int currentCount = 0;
    private int maxCount = 0;

    public int[] findModeOptimal(TreeNode root) {
        modes.clear();
        currentCount = 0;
        maxCount = 0;

        // First pass to find max frequency
        inorderForMaxFreq(root);

        // Reset for second pass
        modes.clear();
        currentCount = 0;
        maxCount = 0;

        // Second pass to collect all modes
        inorderForModes(root);

        return modes.stream().mapToInt(i -> i).toArray();
    }

    private void inorderForMaxFreq(TreeNode node) {
        if (node == null)
            return;

        inorderForMaxFreq(node.left);

        if (currentCount == 0 || node.val != currentVal) {
            currentVal = node.val;
            currentCount = 1;
        } else {
            currentCount++;
        }

        maxCount = Math.max(maxCount, currentCount);

        inorderForMaxFreq(node.right);
    }

    private void inorderForModes(TreeNode node) {
        if (node == null)
            return;

        inorderForModes(node.left);

        if (currentCount == 0 || node.val != currentVal) {
            currentVal = node.val;
            currentCount = 1;
        } else {
            currentCount++;
        }

        if (currentCount == maxCount) {
            modes.add(currentVal);
        }

        inorderForModes(node.right);
    }

    // Follow-up 2: Find k most frequent elements
    public int[] findKMostFrequent(TreeNode root, int k) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        inorderTraversal(root, frequencyMap);

        PriorityQueue<Map.Entry<Integer, Integer>> minHeap = new PriorityQueue<>(
                (a, b) -> a.getValue() - b.getValue());

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            minHeap.offer(entry);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        int[] result = new int[minHeap.size()];
        int index = 0;
        while (!minHeap.isEmpty()) {
            result[index++] = minHeap.poll().getKey();
        }

        return result;
    }

    // Follow-up 3: Handle case with no duplicates
    public int[] findModeNoDuplicates(TreeNode root) {
        List<Integer> allValues = new ArrayList<>();
        collectAllValues(root, allValues);
        return allValues.stream().mapToInt(i -> i).toArray();
    }

    private void collectAllValues(TreeNode node, List<Integer> values) {
        if (node == null)
            return;

        collectAllValues(node.left, values);
        values.add(node.val);
        collectAllValues(node.right, values);
    }

    // Additional: Get frequency map
    public Map<Integer, Integer> getFrequencyMap(TreeNode root) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        inorderTraversal(root, frequencyMap);
        return frequencyMap;
    }

    // Additional: Count unique values
    public int countUniqueValues(TreeNode root) {
        Set<Integer> uniqueValues = new HashSet<>();
        collectUniqueValues(root, uniqueValues);
        return uniqueValues.size();
    }

    private void collectUniqueValues(TreeNode node, Set<Integer> values) {
        if (node == null)
            return;

        values.add(node.val);
        collectUniqueValues(node.left, values);
        collectUniqueValues(node.right, values);
    }

    // Additional: Find least frequent elements
    public int[] findLeastFrequent(TreeNode root) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        inorderTraversal(root, frequencyMap);

        int minFreq = Collections.min(frequencyMap.values());
        List<Integer> leastFrequent = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() == minFreq) {
                leastFrequent.add(entry.getKey());
            }
        }

        return leastFrequent.stream().mapToInt(i -> i).toArray();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindModeInBinarySearchTree solution = new FindModeInBinarySearchTree();

        // Test case 1: Basic case with duplicates
        TreeNode root1 = new TreeNode(1);
        root1.right = new TreeNode(2);
        root1.right.left = new TreeNode(2);

        System.out.println("Test 1 - Basic BST with duplicates:");
        System.out.println("Modes (HashMap): " + Arrays.toString(solution.findMode(root1)));
        System.out.println("Modes (Optimal): " + Arrays.toString(solution.findModeOptimal(root1)));

        // Test case 2: Multiple modes
        TreeNode root2 = new TreeNode(2);
        root2.left = new TreeNode(1);
        root2.right = new TreeNode(3);
        root2.left.left = new TreeNode(1);
        root2.right.right = new TreeNode(3);

        System.out.println("\nTest 2 - Multiple modes:");
        System.out.println("Modes: " + Arrays.toString(solution.findMode(root2)));
        System.out.println("Frequency map: " + solution.getFrequencyMap(root2));

        // Test case 3: K most frequent
        System.out.println("\nTest 3 - 2 most frequent:");
        System.out.println("Result: " + Arrays.toString(solution.findKMostFrequent(root2, 2)));

        // Test case 4: All same values
        TreeNode root3 = new TreeNode(5);
        root3.left = new TreeNode(5);
        root3.right = new TreeNode(5);
        root3.left.left = new TreeNode(5);

        System.out.println("\nTest 4 - All same values:");
        System.out.println("Modes: " + Arrays.toString(solution.findMode(root3)));
        System.out.println("Unique values count: " + solution.countUniqueValues(root3));

        // Test case 5: No duplicates
        TreeNode root4 = new TreeNode(4);
        root4.left = new TreeNode(2);
        root4.right = new TreeNode(6);
        root4.left.left = new TreeNode(1);
        root4.left.right = new TreeNode(3);

        System.out.println("\nTest 5 - No duplicates:");
        System.out.println("All values: " + Arrays.toString(solution.findModeNoDuplicates(root4)));
        System.out.println("Least frequent: " + Arrays.toString(solution.findLeastFrequent(root4)));

        // Edge cases
        System.out.println("\nEdge cases:");
        TreeNode singleNode = new TreeNode(1);
        System.out.println("Single node: " + Arrays.toString(solution.findMode(singleNode)));

        TreeNode linear = new TreeNode(1);
        linear.right = new TreeNode(1);
        linear.right.right = new TreeNode(1);
        System.out.println("Linear tree: " + Arrays.toString(solution.findMode(linear)));

        // Stress test
        System.out.println("\nStress test:");
        TreeNode largeTree = buildLargeBSTWithDuplicates(1000);

        long start = System.nanoTime();
        int[] result = solution.findModeOptimal(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree modes count: " + result.length + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static TreeNode buildLargeBSTWithDuplicates(int nodes) {
        TreeNode root = new TreeNode(500);
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < nodes) {
            TreeNode current = queue.poll();

            if (count < nodes) {
                // Introduce some duplicates
                int leftVal = rand.nextBoolean() ? current.val - rand.nextInt(10) - 1 : current.val;
                current.left = new TreeNode(leftVal);
                queue.offer(current.left);
                count++;
            }

            if (count < nodes) {
                int rightVal = rand.nextBoolean() ? current.val + rand.nextInt(10) + 1 : current.val;
                current.right = new TreeNode(rightVal);
                queue.offer(current.right);
                count++;
            }
        }

        return root;
    }
}
