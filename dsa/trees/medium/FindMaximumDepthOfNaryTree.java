package trees.medium;

import java.util.*;

/**
 * LeetCode 559: Maximum Depth of N-ary Tree
 * https://leetcode.com/problems/maximum-depth-of-n-ary-tree/
 * 
 * Companies: Google, Amazon
 * Frequency: Medium
 *
 * Description: Given a n-ary tree, find its maximum depth.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 10^4]
 * - The depth of the n-ary tree is less than or equal to 1000
 * 
 * Follow-up Questions:
 * 1. Can you solve iteratively?
 * 2. Can you find the widest level?
 * 3. Can you calculate branching factor statistics?
 */
public class FindMaximumDepthOfNaryTree {

    // Approach 1: Recursive DFS
    public int maxDepth(NaryNode root) {
        if (root == null)
            return 0;

        int maxChildDepth = 0;
        if (root.children != null) {
            for (NaryNode child : root.children) {
                maxChildDepth = Math.max(maxChildDepth, maxDepth(child));
            }
        }

        return 1 + maxChildDepth;
    }

    // Follow-up 1: Iterative BFS approach
    public int maxDepthIterative(NaryNode root) {
        if (root == null)
            return 0;

        Queue<NaryNode> queue = new LinkedList<>();
        queue.offer(root);
        int depth = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            depth++;

            for (int i = 0; i < size; i++) {
                NaryNode node = queue.poll();

                if (node.children != null) {
                    for (NaryNode child : node.children) {
                        queue.offer(child);
                    }
                }
            }
        }

        return depth;
    }

    // Follow-up 1: Iterative DFS with stack
    public int maxDepthIterativeDFS(NaryNode root) {
        if (root == null)
            return 0;

        Stack<NaryNode> nodeStack = new Stack<>();
        Stack<Integer> depthStack = new Stack<>();

        nodeStack.push(root);
        depthStack.push(1);
        int maxDepth = 0;

        while (!nodeStack.isEmpty()) {
            NaryNode node = nodeStack.pop();
            int depth = depthStack.pop();

            maxDepth = Math.max(maxDepth, depth);

            if (node.children != null) {
                for (NaryNode child : node.children) {
                    nodeStack.push(child);
                    depthStack.push(depth + 1);
                }
            }
        }

        return maxDepth;
    }

    // Follow-up 2: Find the widest level
    public int findWidestLevel(NaryNode root) {
        if (root == null)
            return 0;

        Queue<NaryNode> queue = new LinkedList<>();
        queue.offer(root);
        int maxWidth = 0;
        int widestLevel = 0;
        int currentLevel = 0;

        while (!queue.isEmpty()) {
            int size = queue.size();
            currentLevel++;

            if (size > maxWidth) {
                maxWidth = size;
                widestLevel = currentLevel;
            }

            for (int i = 0; i < size; i++) {
                NaryNode node = queue.poll();

                if (node.children != null) {
                    for (NaryNode child : node.children) {
                        queue.offer(child);
                    }
                }
            }
        }

        return widestLevel;
    }

    // Follow-up 3: Calculate branching factor statistics
    public Map<String, Double> calculateBranchingStats(NaryNode root) {
        Map<String, Double> stats = new HashMap<>();
        List<Integer> branchingFactors = new ArrayList<>();

        collectBranchingFactors(root, branchingFactors);

        if (branchingFactors.isEmpty()) {
            stats.put("average", 0.0);
            stats.put("max", 0.0);
            stats.put("min", 0.0);
            return stats;
        }

        double sum = branchingFactors.stream().mapToInt(Integer::intValue).sum();
        double average = sum / branchingFactors.size();
        int max = Collections.max(branchingFactors);
        int min = Collections.min(branchingFactors);

        stats.put("average", average);
        stats.put("max", (double) max);
        stats.put("min", (double) min);

        return stats;
    }

    private void collectBranchingFactors(NaryNode node, List<Integer> factors) {
        if (node == null)
            return;

        int childrenCount = node.children != null ? node.children.size() : 0;
        factors.add(childrenCount);

        if (node.children != null) {
            for (NaryNode child : node.children) {
                collectBranchingFactors(child, factors);
            }
        }
    }

    // Additional: Find minimum depth
    public int minDepth(NaryNode root) {
        if (root == null)
            return 0;

        if (root.children == null || root.children.isEmpty())
            return 1;

        int minDepth = Integer.MAX_VALUE;
        for (NaryNode child : root.children) {
            minDepth = Math.min(minDepth, minDepth(child));
        }

        return minDepth + 1;
    }

    // Additional: Count nodes at each level
    public Map<Integer, Integer> countNodesAtEachLevel(NaryNode root) {
        Map<Integer, Integer> levelCounts = new HashMap<>();
        countNodesHelper(root, 1, levelCounts);
        return levelCounts;
    }

    private void countNodesHelper(NaryNode node, int level, Map<Integer, Integer> levelCounts) {
        if (node == null)
            return;

        levelCounts.put(level, levelCounts.getOrDefault(level, 0) + 1);

        if (node.children != null) {
            for (NaryNode child : node.children) {
                countNodesHelper(child, level + 1, levelCounts);
            }
        }
    }

    public static void main(String[] args) {
        FindMaximumDepthOfNaryTree solution = new FindMaximumDepthOfNaryTree();

        // Test case 1: Basic case
        NaryNode root1 = new NaryNode(1,
                Arrays.asList(new NaryNode(3, Arrays.asList(new NaryNode(5), new NaryNode(6))),
                        new NaryNode(2), new NaryNode(4)));

        System.out.println("Test 1 - Basic N-ary tree:");
        System.out.println("Max depth (recursive): " + solution.maxDepth(root1)); // 3
        System.out.println("Max depth (iterative BFS): " + solution.maxDepthIterative(root1));
        System.out.println("Max depth (iterative DFS): " + solution.maxDepthIterativeDFS(root1));
        System.out.println("Min depth: " + solution.minDepth(root1));
        System.out.println("Widest level: " + solution.findWidestLevel(root1));

        Map<String, Double> stats = solution.calculateBranchingStats(root1);
        System.out.printf("Branching stats - Avg: %.2f, Max: %.0f, Min: %.0f%n",
                stats.get("average"), stats.get("max"), stats.get("min"));

        // Test case 2: Level analysis
        System.out.println("\nTest 2 - Level analysis:");
        Map<Integer, Integer> levelCounts = solution.countNodesAtEachLevel(root1);
        for (Map.Entry<Integer, Integer> entry : levelCounts.entrySet()) {
            System.out.println("Level " + entry.getKey() + ": " + entry.getValue() + " nodes");
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        NaryNode root2 = new NaryNode(1, Collections.emptyList());
        System.out.println("Single node: " + solution.maxDepth(root2)); // 1
        System.out.println("Empty tree: " + solution.maxDepth(null)); // 0

        // Stress test
        System.out.println("\nStress test:");
        NaryNode largeTree = buildLargeNaryTree(1000, 5);
        long start = System.nanoTime();
        int result = solution.maxDepth(largeTree);
        long end = System.nanoTime();
        System.out.println("Large tree result: " + result + " in " + (end - start) / 1_000_000 + " ms");
    }

    private static NaryNode buildLargeNaryTree(int totalNodes, int maxChildren) {
        if (totalNodes <= 0)
            return null;

        NaryNode root = new NaryNode(1);
        Queue<NaryNode> queue = new LinkedList<>();
        queue.offer(root);
        int count = 1;
        Random rand = new Random(42);

        while (!queue.isEmpty() && count < totalNodes) {
            NaryNode current = queue.poll();
            int numChildren = Math.min(rand.nextInt(maxChildren) + 1, totalNodes - count);

            if (numChildren > 0) {
                current.children = new ArrayList<>();
                for (int i = 0; i < numChildren && count < totalNodes; i++) {
                    NaryNode child = new NaryNode(count + 1);
                    current.children.add(child);
                    queue.offer(child);
                    count++;
                }
            }
        }

        return root;
    }

    // Definition for a N-ary tree node.
    static class NaryNode {
        public int val;
        public List<NaryNode> children;

        public NaryNode() {
        }

        public NaryNode(int val) {
            this.val = val;
        }

        public NaryNode(int val, List<NaryNode> children) {
            this.val = val;
            this.children = children;
        }
    }
}
