package trees.medium;

import java.util.*;

/**
 * LeetCode 145: Binary Tree Postorder Traversal
 * https://leetcode.com/problems/binary-tree-postorder-traversal/
 * 
 * Companies: Microsoft, Amazon, Google, Facebook, Apple, Bloomberg
 * Frequency: High (Asked in 900+ interviews)
 *
 * Description:
 * Given the root of a binary tree, return the postorder traversal of its nodes'
 * values.
 * 
 * Postorder traversal visits nodes in the order: Left -> Right -> Root
 * 
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 100].
 * - -100 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. Can you solve it iteratively without recursion?
 * 2. How would you implement iterative traversal using only one stack?
 * 3. What about Morris traversal for O(1) space complexity?
 * 4. Can you implement reverse postorder traversal?
 * 5. How to handle n-ary trees for postorder traversal?
 * 6. What about threaded binary tree traversal?
 */
public class BinaryTreePostorderTraversal {

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

    // Approach 1: Recursive DFS - O(n) time, O(h) space
    public static List<Integer> postorderTraversal(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        postorderHelper(root, result);
        return result;
    }

    private static void postorderHelper(TreeNode node, List<Integer> result) {
        if (node == null) {
            return;
        }

        // Left -> Right -> Root
        postorderHelper(node.left, result);
        postorderHelper(node.right, result);
        result.add(node.val);
    }

    // Approach 2: Iterative with two stacks - O(n) time, O(n) space
    public static List<Integer> postorderTraversalIterativeTwoStacks(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack1 = new Stack<>();
        Stack<TreeNode> stack2 = new Stack<>();

        stack1.push(root);

        // First stack to process nodes in reverse postorder
        while (!stack1.isEmpty()) {
            TreeNode node = stack1.pop();
            stack2.push(node);

            // Push left first, then right (opposite of preorder)
            if (node.left != null) {
                stack1.push(node.left);
            }
            if (node.right != null) {
                stack1.push(node.right);
            }
        }

        // Second stack gives us the correct postorder
        while (!stack2.isEmpty()) {
            result.add(stack2.pop().val);
        }

        return result;
    }

    // Approach 3: Iterative with one stack and visited set - O(n) time, O(n) space
    public static List<Integer> postorderTraversalIterativeOneStack(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        Set<TreeNode> visited = new HashSet<>();
        TreeNode current = root;

        while (current != null || !stack.isEmpty()) {
            if (current != null) {
                stack.push(current);
                current = current.left;
            } else {
                TreeNode peekNode = stack.peek();

                // If right child exists and hasn't been processed yet
                if (peekNode.right != null && !visited.contains(peekNode.right)) {
                    current = peekNode.right;
                } else {
                    // Process current node
                    result.add(peekNode.val);
                    visited.add(peekNode);
                    stack.pop();
                }
            }
        }

        return result;
    }

    // Approach 4: Iterative with one stack using modified preorder - O(n) time,
    // O(n) space
    public static List<Integer> postorderTraversalModifiedPreorder(TreeNode root) {
        LinkedList<Integer> result = new LinkedList<>();
        if (root == null) {
            return result;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode node = stack.pop();
            result.addFirst(node.val); // Add to front to reverse order

            // Push left first, then right (for reverse preorder)
            if (node.left != null) {
                stack.push(node.left);
            }
            if (node.right != null) {
                stack.push(node.right);
            }
        }

        return result;
    }

    // Approach 5: Morris Traversal - O(n) time, O(1) space
    public static List<Integer> postorderTraversalMorris(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        TreeNode current = root;
        TreeNode prev;

        while (current != null) {
            if (current.right == null) {
                result.add(0, current.val); // Add to beginning
                current = current.left;
            } else {
                // Find inorder predecessor
                prev = current.right;
                while (prev.left != null && prev.left != current) {
                    prev = prev.left;
                }

                if (prev.left == null) {
                    // Make current the left child of its inorder predecessor
                    prev.left = current;
                    result.add(0, current.val); // Add to beginning
                    current = current.right;
                } else {
                    // Revert the changes made
                    prev.left = null;
                    current = current.left;
                }
            }
        }

        return result;
    }

    // Approach 6: Iterative with explicit state tracking - O(n) time, O(n) space
    public static List<Integer> postorderTraversalExplicitState(TreeNode root) {
        List<Integer> result = new ArrayList<>();
        if (root == null) {
            return result;
        }

        Stack<NodeState> stack = new Stack<>();
        stack.push(new NodeState(root, TraversalState.INITIAL));

        while (!stack.isEmpty()) {
            NodeState current = stack.pop();
            TreeNode node = current.node;
            TraversalState state = current.state;

            switch (state) {
                case INITIAL:
                    // Schedule processing in postorder: root, right, left
                    stack.push(new NodeState(node, TraversalState.FINAL));

                    if (node.right != null) {
                        stack.push(new NodeState(node.right, TraversalState.INITIAL));
                    }

                    if (node.left != null) {
                        stack.push(new NodeState(node.left, TraversalState.INITIAL));
                    }
                    break;

                case FINAL:
                    result.add(node.val);
                    break;
            }
        }

        return result;
    }

    private static class NodeState {
        TreeNode node;
        TraversalState state;

        NodeState(TreeNode node, TraversalState state) {
            this.node = node;
            this.state = state;
        }
    }

    private enum TraversalState {
        INITIAL, FINAL
    }

    // Follow-up 1: Reverse postorder traversal
    public static class ReversePostorder {

        public static List<Integer> reversePostorderTraversal(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            reversePostorderHelper(root, result);
            return result;
        }

        private static void reversePostorderHelper(TreeNode node, List<Integer> result) {
            if (node == null) {
                return;
            }

            // Root -> Right -> Left (reverse of postorder)
            result.add(node.val);
            reversePostorderHelper(node.right, result);
            reversePostorderHelper(node.left, result);
        }

        public static List<Integer> reversePostorderIterative(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            if (root == null) {
                return result;
            }

            Stack<TreeNode> stack = new Stack<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                TreeNode node = stack.pop();
                result.add(node.val);

                // Push left first, then right
                if (node.left != null) {
                    stack.push(node.left);
                }
                if (node.right != null) {
                    stack.push(node.right);
                }
            }

            return result;
        }

        public static List<Integer> getPostorderFromReverse(TreeNode root) {
            List<Integer> reverse = reversePostorderTraversal(root);
            Collections.reverse(reverse);
            return reverse;
        }
    }

    // Follow-up 2: N-ary tree postorder traversal
    public static class NaryTreeTraversal {

        public static class NaryNode {
            int val;
            List<NaryNode> children;

            NaryNode() {
            }

            NaryNode(int val) {
                this.val = val;
                this.children = new ArrayList<>();
            }

            NaryNode(int val, List<NaryNode> children) {
                this.val = val;
                this.children = children;
            }
        }

        public static List<Integer> postorderNary(NaryNode root) {
            List<Integer> result = new ArrayList<>();
            postorderNaryHelper(root, result);
            return result;
        }

        private static void postorderNaryHelper(NaryNode node, List<Integer> result) {
            if (node == null) {
                return;
            }

            // Process all children first
            for (NaryNode child : node.children) {
                postorderNaryHelper(child, result);
            }

            // Then process current node
            result.add(node.val);
        }

        public static List<Integer> postorderNaryIterative(NaryNode root) {
            LinkedList<Integer> result = new LinkedList<>();
            if (root == null) {
                return result;
            }

            Stack<NaryNode> stack = new Stack<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                NaryNode node = stack.pop();
                result.addFirst(node.val);

                // Add children to stack (they will be processed in reverse order)
                for (NaryNode child : node.children) {
                    stack.push(child);
                }
            }

            return result;
        }

        public static NaryNode createSampleNaryTree() {
            NaryNode root = new NaryNode(1);
            root.children.add(new NaryNode(3));
            root.children.add(new NaryNode(2));
            root.children.add(new NaryNode(4));

            root.children.get(0).children.add(new NaryNode(5));
            root.children.get(0).children.add(new NaryNode(6));

            return root;
        }
    }

    // Follow-up 3: Threaded binary tree traversal
    public static class ThreadedBinaryTree {

        public static class ThreadedNode {
            int val;
            ThreadedNode left;
            ThreadedNode right;
            boolean rightThreaded; // True if right pointer is a thread

            ThreadedNode(int val) {
                this.val = val;
                this.rightThreaded = false;
            }
        }

        public static List<Integer> postorderThreaded(ThreadedNode root) {
            List<Integer> result = new ArrayList<>();
            if (root == null) {
                return result;
            }

            // Convert to threaded tree first
            ThreadedNode threadedRoot = createThreadedTree(root);

            // Perform postorder traversal
            postorderThreadedHelper(threadedRoot, result);

            return result;
        }

        private static ThreadedNode createThreadedTree(ThreadedNode root) {
            // This is a simplified version - in practice, you'd need proper threading
            return root;
        }

        private static void postorderThreadedHelper(ThreadedNode node, List<Integer> result) {
            if (node == null) {
                return;
            }

            // Left subtree
            postorderThreadedHelper(node.left, result);

            // Right subtree (if not threaded)
            if (!node.rightThreaded) {
                postorderThreadedHelper(node.right, result);
            }

            // Current node
            result.add(node.val);
        }

        public static ThreadedNode findInorderSuccessor(ThreadedNode node) {
            if (node.rightThreaded) {
                return node.right;
            }

            // Find leftmost node in right subtree
            ThreadedNode current = node.right;
            while (current != null && current.left != null) {
                current = current.left;
            }

            return current;
        }
    }

    // Follow-up 4: Postorder with additional operations
    public static class PostorderWithOperations {

        public static List<Integer> postorderWithSum(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            postorderWithSumHelper(root, result);
            return result;
        }

        private static int postorderWithSumHelper(TreeNode node, List<Integer> result) {
            if (node == null) {
                return 0;
            }

            int leftSum = postorderWithSumHelper(node.left, result);
            int rightSum = postorderWithSumHelper(node.right, result);

            int totalSum = leftSum + rightSum + node.val;
            result.add(totalSum);

            return totalSum;
        }

        public static List<Integer> postorderWithDepth(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            postorderWithDepthHelper(root, 0, result);
            return result;
        }

        private static void postorderWithDepthHelper(TreeNode node, int depth, List<Integer> result) {
            if (node == null) {
                return;
            }

            postorderWithDepthHelper(node.left, depth + 1, result);
            postorderWithDepthHelper(node.right, depth + 1, result);

            // Store depth information (could be combined with value)
            result.add(depth);
        }

        public static Map<Integer, List<Integer>> postorderByLevel(TreeNode root) {
            Map<Integer, List<Integer>> levelMap = new HashMap<>();
            postorderByLevelHelper(root, 0, levelMap);
            return levelMap;
        }

        private static void postorderByLevelHelper(TreeNode node, int level,
                Map<Integer, List<Integer>> levelMap) {
            if (node == null) {
                return;
            }

            postorderByLevelHelper(node.left, level + 1, levelMap);
            postorderByLevelHelper(node.right, level + 1, levelMap);

            @SuppressWarnings("unused")
            List<Integer> levelList = levelMap.computeIfAbsent(level, levelKey -> new ArrayList<>());
            levelList.add(node.val);
        }

        public static List<String> postorderWithPath(TreeNode root) {
            List<String> result = new ArrayList<>();
            postorderWithPathHelper(root, "", result);
            return result;
        }

        private static void postorderWithPathHelper(TreeNode node, String path, List<String> result) {
            if (node == null) {
                return;
            }

            String currentPath = path.isEmpty() ? String.valueOf(node.val) : path + "->" + node.val;

            postorderWithPathHelper(node.left, currentPath, result);
            postorderWithPathHelper(node.right, currentPath, result);

            result.add(currentPath);
        }
    }

    // Follow-up 5: Postorder with constraints and filters
    public static class ConstrainedPostorder {

        public static List<Integer> postorderEvenOnly(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            postorderEvenOnlyHelper(root, result);
            return result;
        }

        private static void postorderEvenOnlyHelper(TreeNode node, List<Integer> result) {
            if (node == null) {
                return;
            }

            postorderEvenOnlyHelper(node.left, result);
            postorderEvenOnlyHelper(node.right, result);

            if (node.val % 2 == 0) {
                result.add(node.val);
            }
        }

        public static List<Integer> postorderInRange(TreeNode root, int minVal, int maxVal) {
            List<Integer> result = new ArrayList<>();
            postorderInRangeHelper(root, minVal, maxVal, result);
            return result;
        }

        private static void postorderInRangeHelper(TreeNode node, int minVal, int maxVal,
                List<Integer> result) {
            if (node == null) {
                return;
            }

            postorderInRangeHelper(node.left, minVal, maxVal, result);
            postorderInRangeHelper(node.right, minVal, maxVal, result);

            if (node.val >= minVal && node.val <= maxVal) {
                result.add(node.val);
            }
        }

        public static List<Integer> postorderLeafNodesOnly(TreeNode root) {
            List<Integer> result = new ArrayList<>();
            postorderLeafNodesOnlyHelper(root, result);
            return result;
        }

        private static void postorderLeafNodesOnlyHelper(TreeNode node, List<Integer> result) {
            if (node == null) {
                return;
            }

            postorderLeafNodesOnlyHelper(node.left, result);
            postorderLeafNodesOnlyHelper(node.right, result);

            // Only add leaf nodes
            if (node.left == null && node.right == null) {
                result.add(node.val);
            }
        }

        public static List<Integer> postorderWithPredicate(TreeNode root,
                java.util.function.Predicate<Integer> predicate) {
            List<Integer> result = new ArrayList<>();
            postorderWithPredicateHelper(root, predicate, result);
            return result;
        }

        private static void postorderWithPredicateHelper(TreeNode node,
                java.util.function.Predicate<Integer> predicate,
                List<Integer> result) {
            if (node == null) {
                return;
            }

            postorderWithPredicateHelper(node.left, predicate, result);
            postorderWithPredicateHelper(node.right, predicate, result);

            if (predicate.test(node.val)) {
                result.add(node.val);
            }
        }
    }

    // Utility methods for testing
    public static TreeNode createSampleTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        root.left.left = new TreeNode(4);
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
        root.left.left.left = new TreeNode(8);
        root.left.left.right = new TreeNode(9);
        return root;
    }

    public static TreeNode createSkewedTree() {
        TreeNode root = new TreeNode(1);
        root.right = new TreeNode(2);
        root.right.right = new TreeNode(3);
        root.right.right.right = new TreeNode(4);
        return root;
    }

    public static void printTraversal(List<Integer> traversal, String name) {
        System.out.println(name + ": " + traversal);
    }

    // Performance comparison utility
    public static void compareApproaches(TreeNode root) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 10000;

        // Recursive approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            postorderTraversal(root);
        }
        end = System.nanoTime();
        System.out.println("Recursive: " + (end - start) / 1_000_000 + " ms");

        // Two stacks iterative
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            postorderTraversalIterativeTwoStacks(root);
        }
        end = System.nanoTime();
        System.out.println("Two stacks: " + (end - start) / 1_000_000 + " ms");

        // One stack iterative
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            postorderTraversalIterativeOneStack(root);
        }
        end = System.nanoTime();
        System.out.println("One stack: " + (end - start) / 1_000_000 + " ms");

        // Modified preorder
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            postorderTraversalModifiedPreorder(root);
        }
        end = System.nanoTime();
        System.out.println("Modified preorder: " + (end - start) / 1_000_000 + " ms");

        // Morris traversal
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            postorderTraversalMorris(root);
        }
        end = System.nanoTime();
        System.out.println("Morris: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        TreeNode root = createSampleTree();

        List<Integer> recursive = postorderTraversal(root);
        List<Integer> twoStacks = postorderTraversalIterativeTwoStacks(root);
        List<Integer> oneStack = postorderTraversalIterativeOneStack(root);
        List<Integer> modifiedPreorder = postorderTraversalModifiedPreorder(root);
        List<Integer> morris = postorderTraversalMorris(root);
        List<Integer> explicitState = postorderTraversalExplicitState(root);

        System.out.println("Sample tree structure:");
        System.out.println("    1");
        System.out.println("   / \\");
        System.out.println("  2   3");
        System.out.println(" / \\");
        System.out.println("4   5");

        printTraversal(recursive, "Recursive");
        printTraversal(twoStacks, "Two stacks");
        printTraversal(oneStack, "One stack");
        printTraversal(modifiedPreorder, "Modified preorder");
        printTraversal(morris, "Morris");
        printTraversal(explicitState, "Explicit state");

        // Verify all approaches give same result
        boolean allEqual = recursive.equals(twoStacks) &&
                twoStacks.equals(oneStack) &&
                oneStack.equals(modifiedPreorder) &&
                modifiedPreorder.equals(explicitState);
        System.out.println("All approaches consistent: " + allEqual);

        // Test Case 2: Reverse postorder
        System.out.println("\n=== Test Case 2: Reverse Postorder ===");

        List<Integer> reversePostorder = ReversePostorder.reversePostorderTraversal(root);
        List<Integer> reverseIterative = ReversePostorder.reversePostorderIterative(root);
        List<Integer> fromReverse = ReversePostorder.getPostorderFromReverse(root);

        printTraversal(reversePostorder, "Reverse postorder");
        printTraversal(reverseIterative, "Reverse iterative");
        printTraversal(fromReverse, "Postorder from reverse");

        System.out.println("Reverse consistency: " + reversePostorder.equals(reverseIterative));
        System.out.println("Original from reverse: " + fromReverse.equals(recursive));

        // Test Case 3: N-ary tree
        System.out.println("\n=== Test Case 3: N-ary Tree ===");

        NaryTreeTraversal.NaryNode naryRoot = NaryTreeTraversal.createSampleNaryTree();
        List<Integer> naryRecursive = NaryTreeTraversal.postorderNary(naryRoot);
        List<Integer> naryIterative = NaryTreeTraversal.postorderNaryIterative(naryRoot);

        System.out.println("N-ary tree structure:");
        System.out.println("    1");
        System.out.println(" /  |  \\");
        System.out.println("3   2   4");
        System.out.println("|\\");
        System.out.println("5 6");

        printTraversal(naryRecursive, "N-ary recursive");
        printTraversal(naryIterative, "N-ary iterative");
        System.out.println("N-ary consistency: " + naryRecursive.equals(naryIterative));

        // Test Case 4: Postorder with operations
        System.out.println("\n=== Test Case 4: Postorder with Operations ===");

        TreeNode complexTree = createComplexTree();

        List<Integer> withSum = PostorderWithOperations.postorderWithSum(complexTree);
        List<Integer> withDepth = PostorderWithOperations.postorderWithDepth(complexTree);
        Map<Integer, List<Integer>> byLevel = PostorderWithOperations.postorderByLevel(complexTree);
        List<String> withPath = PostorderWithOperations.postorderWithPath(complexTree);

        printTraversal(withSum, "With subtree sum");
        printTraversal(withDepth, "With depth");
        System.out.println("By level: " + byLevel);
        System.out.println("With path: " + withPath);

        // Test Case 5: Constrained postorder
        System.out.println("\n=== Test Case 5: Constrained Postorder ===");

        List<Integer> evenOnly = ConstrainedPostorder.postorderEvenOnly(complexTree);
        List<Integer> inRange = ConstrainedPostorder.postorderInRange(complexTree, 3, 7);
        List<Integer> leafOnly = ConstrainedPostorder.postorderLeafNodesOnly(complexTree);
        List<Integer> greaterThan5 = ConstrainedPostorder.postorderWithPredicate(complexTree, x -> x > 5);

        printTraversal(evenOnly, "Even values only");
        printTraversal(inRange, "Values in range [3,7]");
        printTraversal(leafOnly, "Leaf nodes only");
        printTraversal(greaterThan5, "Values > 5");

        // Test Case 6: Edge cases
        System.out.println("\n=== Test Case 6: Edge Cases ===");

        // Empty tree
        List<Integer> emptyTree = postorderTraversal(null);

        // Single node
        TreeNode single = new TreeNode(42);
        List<Integer> singleNode = postorderTraversal(single);

        // Skewed tree
        TreeNode skewed = createSkewedTree();
        List<Integer> skewedResult = postorderTraversal(skewed);

        printTraversal(emptyTree, "Empty tree");
        printTraversal(singleNode, "Single node");
        printTraversal(skewedResult, "Skewed tree");

        // Test Case 7: Large tree performance
        System.out.println("\n=== Test Case 7: Large Tree Performance ===");

        TreeNode largeTree = createLargeTree(1000);

        long start = System.nanoTime();
        List<Integer> largeResult = postorderTraversal(largeTree);
        long end = System.nanoTime();

        System.out.println("Large tree (1000 nodes): " + largeResult.size() + " nodes");
        System.out.println("Time taken: " + (end - start) / 1_000_000 + " ms");

        // Test Case 8: Performance comparison
        System.out.println("\n=== Test Case 8: Performance Comparison ===");

        compareApproaches(complexTree);

        // Test Case 9: Memory usage comparison
        System.out.println("\n=== Test Case 9: Memory Usage Analysis ===");

        Runtime runtime = Runtime.getRuntime();

        // Test recursive approach
        long memBefore = runtime.totalMemory() - runtime.freeMemory();
        for (int i = 0; i < 1000; i++) {
            postorderTraversal(complexTree);
        }
        long memAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Recursive memory delta: " + (memAfter - memBefore) / 1024 + " KB");

        // Test Morris approach
        System.gc(); // Attempt garbage collection
        memBefore = runtime.totalMemory() - runtime.freeMemory();
        for (int i = 0; i < 1000; i++) {
            postorderTraversalMorris(complexTree);
        }
        memAfter = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("Morris memory delta: " + (memAfter - memBefore) / 1024 + " KB");

        // Test Case 10: Stress testing
        System.out.println("\n=== Test Case 10: Stress Testing ===");

        // Test with very deep tree
        TreeNode deepTree = createDeepTree(500);

        start = System.nanoTime();
        List<Integer> deepResult = postorderTraversalIterativeTwoStacks(deepTree); // Use iterative to avoid stack
                                                                                   // overflow
        end = System.nanoTime();

        System.out.println("Deep tree (500 levels): " + deepResult.size() + " nodes");
        System.out.println("Time taken: " + (end - start) / 1_000_000 + " ms");

        // Test with wide tree
        TreeNode wideTree = createWideTree(10, 3); // 10 levels, 3 children per node

        start = System.nanoTime();
        List<Integer> wideResult = postorderTraversal(wideTree);
        end = System.nanoTime();

        System.out.println("Wide tree: " + wideResult.size() + " nodes");
        System.out.println("Time taken: " + (end - start) / 1_000_000 + " ms");

        System.out.println("\nBinary Tree Postorder Traversal testing completed successfully!");
    }

    // Helper methods for stress testing
    private static TreeNode createLargeTree(int maxNodes) {
        if (maxNodes <= 0) {
            return null;
        }

        TreeNode root = new TreeNode(maxNodes);

        int leftNodes = maxNodes / 2;
        int rightNodes = maxNodes - 1 - leftNodes;

        root.left = createLargeTree(leftNodes);
        root.right = createLargeTree(rightNodes);

        return root;
    }

    private static TreeNode createDeepTree(int depth) {
        if (depth <= 0) {
            return null;
        }

        TreeNode root = new TreeNode(depth);
        root.left = createDeepTree(depth - 1);

        return root;
    }

    private static TreeNode createWideTree(int levels, int childrenPerLevel) {
        if (levels <= 0) {
            return null;
        }

        TreeNode root = new TreeNode(levels);

        if (levels > 1) {
            root.left = createWideTree(levels - 1, childrenPerLevel);
            root.right = createWideTree(levels - 1, childrenPerLevel);
        }

        return root;
    }
}
