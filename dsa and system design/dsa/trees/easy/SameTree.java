package trees.easy;

import java.util.*;

/**
 * LeetCode 100: Same Tree
 * https://leetcode.com/problems/same-tree/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple, Bloomberg, Adobe
 * Frequency: Very High (Asked in 1500+ interviews)
 *
 * Description:
 * Given the roots of two binary trees p and q, write a function to check if
 * they are the same or not.
 * Two binary trees are considered the same if they are structurally identical,
 * and the nodes have the same value.
 * 
 * Constraints:
 * - The number of nodes in both trees is in the range [0, 100].
 * - -10^4 <= Node.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. How would you check if two trees are mirror images of each other?
 * 2. Can you determine if one tree is a subtree of another?
 * 3. How to check if trees are isomorphic (same structure but different
 * values)?
 * 4. What about checking tree similarity with tolerance for differences?
 * 5. How to find the largest common subtree between two trees?
 * 6. Can you serialize and compare trees for equality?
 */
public class SameTree {

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

    // Approach 1: Recursive DFS - O(min(m,n)) time, O(min(m,n)) space
    public static boolean isSameTree(TreeNode p, TreeNode q) {
        // Base cases
        if (p == null && q == null) {
            return true;
        }

        if (p == null || q == null) {
            return false;
        }

        // Check current nodes and recursively check subtrees
        return p.val == q.val &&
                isSameTree(p.left, q.left) &&
                isSameTree(p.right, q.right);
    }

    // Approach 2: Iterative with Stack - O(min(m,n)) time, O(min(m,n)) space
    public static boolean isSameTreeIterative(TreeNode p, TreeNode q) {
        Stack<TreeNode> stackP = new Stack<>();
        Stack<TreeNode> stackQ = new Stack<>();

        stackP.push(p);
        stackQ.push(q);

        while (!stackP.isEmpty() && !stackQ.isEmpty()) {
            TreeNode nodeP = stackP.pop();
            TreeNode nodeQ = stackQ.pop();

            // Both null
            if (nodeP == null && nodeQ == null) {
                continue;
            }

            // One null, one not null
            if (nodeP == null || nodeQ == null) {
                return false;
            }

            // Different values
            if (nodeP.val != nodeQ.val) {
                return false;
            }

            // Add children to stacks
            stackP.push(nodeP.left);
            stackP.push(nodeP.right);
            stackQ.push(nodeQ.left);
            stackQ.push(nodeQ.right);
        }

        return stackP.isEmpty() && stackQ.isEmpty();
    }

    // Approach 3: Level-order traversal (BFS) - O(min(m,n)) time, O(min(m,n)) space
    public static boolean isSameTreeBFS(TreeNode p, TreeNode q) {
        Queue<TreeNode> queueP = new LinkedList<>();
        Queue<TreeNode> queueQ = new LinkedList<>();

        queueP.offer(p);
        queueQ.offer(q);

        while (!queueP.isEmpty() && !queueQ.isEmpty()) {
            TreeNode nodeP = queueP.poll();
            TreeNode nodeQ = queueQ.poll();

            // Both null
            if (nodeP == null && nodeQ == null) {
                continue;
            }

            // One null, one not null
            if (nodeP == null || nodeQ == null) {
                return false;
            }

            // Different values
            if (nodeP.val != nodeQ.val) {
                return false;
            }

            // Add children to queues
            queueP.offer(nodeP.left);
            queueP.offer(nodeP.right);
            queueQ.offer(nodeQ.left);
            queueQ.offer(nodeQ.right);
        }

        return queueP.isEmpty() && queueQ.isEmpty();
    }

    // Approach 4: Serialization comparison - O(m+n) time, O(m+n) space
    public static boolean isSameTreeSerialization(TreeNode p, TreeNode q) {
        return serialize(p).equals(serialize(q));
    }

    private static String serialize(TreeNode root) {
        if (root == null) {
            return "null";
        }

        return root.val + "," + serialize(root.left) + "," + serialize(root.right);
    }

    // Approach 5: Inorder + Preorder comparison - O(m+n) time, O(m+n) space
    public static boolean isSameTreeTraversals(TreeNode p, TreeNode q) {
        List<Integer> inorderP = new ArrayList<>();
        List<Integer> inorderQ = new ArrayList<>();
        List<Integer> preorderP = new ArrayList<>();
        List<Integer> preorderQ = new ArrayList<>();

        inorderWithNulls(p, inorderP);
        inorderWithNulls(q, inorderQ);
        preorderWithNulls(p, preorderP);
        preorderWithNulls(q, preorderQ);

        return inorderP.equals(inorderQ) && preorderP.equals(preorderQ);
    }

    private static void inorderWithNulls(TreeNode node, List<Integer> result) {
        if (node == null) {
            result.add(null);
            return;
        }

        inorderWithNulls(node.left, result);
        result.add(node.val);
        inorderWithNulls(node.right, result);
    }

    private static void preorderWithNulls(TreeNode node, List<Integer> result) {
        if (node == null) {
            result.add(null);
            return;
        }

        result.add(node.val);
        preorderWithNulls(node.left, result);
        preorderWithNulls(node.right, result);
    }

    // Follow-up 1: Mirror trees
    public static class MirrorTrees {

        public static boolean isSymmetric(TreeNode root) {
            if (root == null) {
                return true;
            }
            return isMirror(root.left, root.right);
        }

        private static boolean isMirror(TreeNode left, TreeNode right) {
            if (left == null && right == null) {
                return true;
            }

            if (left == null || right == null) {
                return false;
            }

            return left.val == right.val &&
                    isMirror(left.left, right.right) &&
                    isMirror(left.right, right.left);
        }

        public static boolean isMirrorIterative(TreeNode left, TreeNode right) {
            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(left);
            queue.offer(right);

            while (!queue.isEmpty()) {
                TreeNode node1 = queue.poll();
                TreeNode node2 = queue.poll();

                if (node1 == null && node2 == null) {
                    continue;
                }

                if (node1 == null || node2 == null || node1.val != node2.val) {
                    return false;
                }

                queue.offer(node1.left);
                queue.offer(node2.right);
                queue.offer(node1.right);
                queue.offer(node2.left);
            }

            return true;
        }

        public static TreeNode createMirror(TreeNode root) {
            if (root == null) {
                return null;
            }

            TreeNode mirror = new TreeNode(root.val);
            mirror.left = createMirror(root.right);
            mirror.right = createMirror(root.left);

            return mirror;
        }

        public static boolean areTreesMirrors(TreeNode tree1, TreeNode tree2) {
            return isMirror(tree1, tree2);
        }
    }

    // Follow-up 2: Subtree relationship
    public static class SubtreeCheck {

        public static boolean isSubtree(TreeNode root, TreeNode subRoot) {
            if (subRoot == null) {
                return true;
            }

            if (root == null) {
                return false;
            }

            return isSameTree(root, subRoot) ||
                    isSubtree(root.left, subRoot) ||
                    isSubtree(root.right, subRoot);
        }

        public static boolean isSubtreeEfficient(TreeNode root, TreeNode subRoot) {
            String rootStr = serialize(root);
            String subRootStr = serialize(subRoot);

            return rootStr.contains(subRootStr);
        }

        public static List<TreeNode> findAllSubtreeOccurrences(TreeNode root, TreeNode subRoot) {
            List<TreeNode> occurrences = new ArrayList<>();
            if (subRoot == null) {
                return occurrences;
            }

            findSubtreeOccurrences(root, subRoot, occurrences);
            return occurrences;
        }

        private static void findSubtreeOccurrences(TreeNode root, TreeNode subRoot,
                List<TreeNode> occurrences) {
            if (root == null) {
                return;
            }

            if (isSameTree(root, subRoot)) {
                occurrences.add(root);
            }

            findSubtreeOccurrences(root.left, subRoot, occurrences);
            findSubtreeOccurrences(root.right, subRoot, occurrences);
        }

        public static int countSubtreeOccurrences(TreeNode root, TreeNode subRoot) {
            if (subRoot == null) {
                return 0;
            }

            if (root == null) {
                return 0;
            }

            int count = 0;
            if (isSameTree(root, subRoot)) {
                count = 1;
            }

            return count + countSubtreeOccurrences(root.left, subRoot) +
                    countSubtreeOccurrences(root.right, subRoot);
        }
    }

    // Follow-up 3: Isomorphic trees (same structure, different values)
    public static class IsomorphicTrees {

        public static boolean isIsomorphic(TreeNode p, TreeNode q) {
            if (p == null && q == null) {
                return true;
            }

            if (p == null || q == null) {
                return false;
            }

            // Two possibilities for isomorphic trees:
            // 1. No flip: left maps to left, right maps to right
            // 2. Flip: left maps to right, right maps to left
            return (isIsomorphic(p.left, q.left) && isIsomorphic(p.right, q.right)) ||
                    (isIsomorphic(p.left, q.right) && isIsomorphic(p.right, q.left));
        }

        public static boolean hasSameStructure(TreeNode p, TreeNode q) {
            if (p == null && q == null) {
                return true;
            }

            if (p == null || q == null) {
                return false;
            }

            return hasSameStructure(p.left, q.left) && hasSameStructure(p.right, q.right);
        }

        public static String getStructureSignature(TreeNode root) {
            if (root == null) {
                return "null";
            }

            String leftSig = getStructureSignature(root.left);
            String rightSig = getStructureSignature(root.right);

            // Normalize the signature (smaller string first)
            if (leftSig.compareTo(rightSig) > 0) {
                String temp = leftSig;
                leftSig = rightSig;
                rightSig = temp;
            }

            return "(" + leftSig + "," + rightSig + ")";
        }

        public static boolean isIsomorphicBySignature(TreeNode p, TreeNode q) {
            return getStructureSignature(p).equals(getStructureSignature(q));
        }
    }

    // Follow-up 4: Tree similarity with tolerance
    public static class TreeSimilarity {

        public static boolean isSimilar(TreeNode p, TreeNode q, int maxDifferences) {
            int[] differences = { 0 };
            return isSimilarHelper(p, q, differences, maxDifferences);
        }

        private static boolean isSimilarHelper(TreeNode p, TreeNode q, int[] differences, int maxDifferences) {
            if (differences[0] > maxDifferences) {
                return false;
            }

            if (p == null && q == null) {
                return true;
            }

            if (p == null || q == null) {
                differences[0]++;
                return differences[0] <= maxDifferences;
            }

            if (p.val != q.val) {
                differences[0]++;
            }

            return isSimilarHelper(p.left, q.left, differences, maxDifferences) &&
                    isSimilarHelper(p.right, q.right, differences, maxDifferences);
        }

        public static int countDifferences(TreeNode p, TreeNode q) {
            if (p == null && q == null) {
                return 0;
            }

            if (p == null || q == null) {
                return 1 + countNodes(p) + countNodes(q);
            }

            int currentDiff = (p.val != q.val) ? 1 : 0;
            return currentDiff + countDifferences(p.left, q.left) + countDifferences(p.right, q.right);
        }

        private static int countNodes(TreeNode root) {
            if (root == null) {
                return 0;
            }
            return 1 + countNodes(root.left) + countNodes(root.right);
        }

        public static double calculateSimilarityScore(TreeNode p, TreeNode q) {
            int totalNodes = Math.max(countNodes(p), countNodes(q));
            if (totalNodes == 0) {
                return 1.0;
            }

            int differences = countDifferences(p, q);
            return 1.0 - (double) differences / totalNodes;
        }

        public static boolean isSimilarWithThreshold(TreeNode p, TreeNode q, double threshold) {
            return calculateSimilarityScore(p, q) >= threshold;
        }
    }

    // Follow-up 5: Largest common subtree
    public static class LargestCommonSubtree {

        public static TreeNode findLargestCommonSubtree(TreeNode p, TreeNode q) {
            TreeNode[] largest = { null };
            int[] maxSize = { 0 };

            findLCS(p, q, largest, maxSize);
            return largest[0];
        }

        private static int findLCS(TreeNode p, TreeNode q, TreeNode[] largest, int[] maxSize) {
            if (p == null || q == null) {
                return 0;
            }

            int leftSize = findLCS(p.left, q.left, largest, maxSize);
            int rightSize = findLCS(p.right, q.right, largest, maxSize);

            if (isSameTree(p, q)) {
                int currentSize = 1 + leftSize + rightSize;
                if (currentSize > maxSize[0]) {
                    maxSize[0] = currentSize;
                    largest[0] = p; // or create a copy if needed
                }
                return currentSize;
            }

            return 0;
        }

        public static List<TreeNode> findAllCommonSubtrees(TreeNode p, TreeNode q) {
            List<TreeNode> commonSubtrees = new ArrayList<>();
            findAllCommonSubtreesHelper(p, q, commonSubtrees);
            return commonSubtrees;
        }

        private static void findAllCommonSubtreesHelper(TreeNode p, TreeNode q,
                List<TreeNode> commonSubtrees) {
            if (p == null || q == null) {
                return;
            }

            if (isSameTree(p, q)) {
                commonSubtrees.add(p);
                return; // Don't check children if entire subtree matches
            }

            findAllCommonSubtreesHelper(p.left, q.left, commonSubtrees);
            findAllCommonSubtreesHelper(p.left, q.right, commonSubtrees);
            findAllCommonSubtreesHelper(p.right, q.left, commonSubtrees);
            findAllCommonSubtreesHelper(p.right, q.right, commonSubtrees);
        }

        public static int getLargestCommonSubtreeSize(TreeNode p, TreeNode q) {
            TreeNode lcs = findLargestCommonSubtree(p, q);
            return countNodes(lcs);
        }

        private static int countNodes(TreeNode root) {
            if (root == null) {
                return 0;
            }
            return 1 + countNodes(root.left) + countNodes(root.right);
        }
    }

    // Follow-up 6: Serialization for comparison
    public static class TreeSerialization {

        public static String serializePreorder(TreeNode root) {
            StringBuilder sb = new StringBuilder();
            serializePreorderHelper(root, sb);
            return sb.toString();
        }

        private static void serializePreorderHelper(TreeNode node, StringBuilder sb) {
            if (node == null) {
                sb.append("null,");
                return;
            }

            sb.append(node.val).append(",");
            serializePreorderHelper(node.left, sb);
            serializePreorderHelper(node.right, sb);
        }

        public static String serializeLevelOrder(TreeNode root) {
            if (root == null) {
                return "null";
            }

            StringBuilder sb = new StringBuilder();
            Queue<TreeNode> queue = new LinkedList<>();
            queue.offer(root);

            while (!queue.isEmpty()) {
                TreeNode node = queue.poll();

                if (node == null) {
                    sb.append("null,");
                } else {
                    sb.append(node.val).append(",");
                    queue.offer(node.left);
                    queue.offer(node.right);
                }
            }

            return sb.toString();
        }

        public static TreeNode deserializePreorder(String data) {
            if (data == null || data.isEmpty()) {
                return null;
            }

            String[] values = data.split(",");
            int[] index = { 0 };
            return deserializePreorderHelper(values, index);
        }

        private static TreeNode deserializePreorderHelper(String[] values, int[] index) {
            if (index[0] >= values.length || values[index[0]].equals("null")) {
                index[0]++;
                return null;
            }

            TreeNode node = new TreeNode(Integer.parseInt(values[index[0]++]));
            node.left = deserializePreorderHelper(values, index);
            node.right = deserializePreorderHelper(values, index);

            return node;
        }

        public static boolean compareSerializations(TreeNode p, TreeNode q) {
            String serialP = serializePreorder(p);
            String serialQ = serializePreorder(q);
            return serialP.equals(serialQ);
        }

        public static String getCanonicalForm(TreeNode root) {
            if (root == null) {
                return "null";
            }

            String left = getCanonicalForm(root.left);
            String right = getCanonicalForm(root.right);

            // Sort children to get canonical form
            if (left.compareTo(right) > 0) {
                String temp = left;
                left = right;
                right = temp;
            }

            return "(" + root.val + "," + left + "," + right + ")";
        }

        public static boolean isCanonicallyEqual(TreeNode p, TreeNode q) {
            return getCanonicalForm(p).equals(getCanonicalForm(q));
        }
    }

    // Utility methods for testing
    public static TreeNode createSampleTree1() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        return root;
    }

    public static TreeNode createSampleTree2() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(2);
        root.right = new TreeNode(3);
        return root;
    }

    public static TreeNode createDifferentTree() {
        TreeNode root = new TreeNode(1);
        root.left = null;
        root.right = new TreeNode(2);
        return root;
    }

    public static TreeNode createMirrorTree() {
        TreeNode root = new TreeNode(1);
        root.left = new TreeNode(3);
        root.right = new TreeNode(2);
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

    // Performance comparison utility
    public static void compareApproaches(TreeNode p, TreeNode q) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 100000;

        // Recursive approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSameTree(p, q);
        }
        end = System.nanoTime();
        System.out.println("Recursive: " + (end - start) / 1_000_000 + " ms");

        // Iterative approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSameTreeIterative(p, q);
        }
        end = System.nanoTime();
        System.out.println("Iterative: " + (end - start) / 1_000_000 + " ms");

        // BFS approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSameTreeBFS(p, q);
        }
        end = System.nanoTime();
        System.out.println("BFS: " + (end - start) / 1_000_000 + " ms");

        // Serialization approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSameTreeSerialization(p, q);
        }
        end = System.nanoTime();
        System.out.println("Serialization: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        TreeNode tree1 = createSampleTree1();
        TreeNode tree2 = createSampleTree2();
        TreeNode tree3 = createDifferentTree();

        boolean same1 = isSameTree(tree1, tree2);
        boolean same2 = isSameTree(tree1, tree3);
        boolean same3 = isSameTreeIterative(tree1, tree2);
        boolean same4 = isSameTreeBFS(tree1, tree2);
        boolean same5 = isSameTreeSerialization(tree1, tree2);

        System.out.println("Tree1 == Tree2: " + same1);
        System.out.println("Tree1 == Tree3: " + same2);
        System.out.println("Iterative result: " + same3);
        System.out.println("BFS result: " + same4);
        System.out.println("Serialization result: " + same5);

        // Test Case 2: Mirror trees
        System.out.println("\n=== Test Case 2: Mirror Trees ===");

        TreeNode symmetric = new TreeNode(1);
        symmetric.left = new TreeNode(2);
        symmetric.right = new TreeNode(2);
        symmetric.left.left = new TreeNode(3);
        symmetric.left.right = new TreeNode(4);
        symmetric.right.left = new TreeNode(4);
        symmetric.right.right = new TreeNode(3);

        boolean isSymmetric = MirrorTrees.isSymmetric(symmetric);
        TreeNode mirrorTree = createMirrorTree();
        boolean areMirrors = MirrorTrees.areTreesMirrors(tree1, mirrorTree);

        System.out.println("Tree is symmetric: " + isSymmetric);
        System.out.println("Trees are mirrors: " + areMirrors);

        // Test Case 3: Subtree relationships
        System.out.println("\n=== Test Case 3: Subtree Relationships ===");

        TreeNode bigTree = createComplexTree();
        TreeNode smallTree = new TreeNode(2);
        smallTree.left = new TreeNode(4);
        smallTree.right = new TreeNode(5);

        boolean isSubtree = SubtreeCheck.isSubtree(bigTree, smallTree);
        int occurrences = SubtreeCheck.countSubtreeOccurrences(bigTree, smallTree);
        List<TreeNode> allOccurrences = SubtreeCheck.findAllSubtreeOccurrences(bigTree, smallTree);

        System.out.println("Is subtree: " + isSubtree);
        System.out.println("Subtree occurrences: " + occurrences);
        System.out.println("Found occurrences: " + allOccurrences.size());

        // Test Case 4: Isomorphic trees
        System.out.println("\n=== Test Case 4: Isomorphic Trees ===");

        TreeNode iso1 = new TreeNode(1);
        iso1.left = new TreeNode(2);
        iso1.right = new TreeNode(3);

        TreeNode iso2 = new TreeNode(1);
        iso2.left = new TreeNode(3);
        iso2.right = new TreeNode(2);

        boolean isIsomorphic = IsomorphicTrees.isIsomorphic(iso1, iso2);
        boolean sameStructure = IsomorphicTrees.hasSameStructure(iso1, tree1);
        String sig1 = IsomorphicTrees.getStructureSignature(iso1);
        String sig2 = IsomorphicTrees.getStructureSignature(iso2);

        System.out.println("Trees are isomorphic: " + isIsomorphic);
        System.out.println("Same structure: " + sameStructure);
        System.out.println("Structure signature 1: " + sig1);
        System.out.println("Structure signature 2: " + sig2);

        // Test Case 5: Tree similarity
        System.out.println("\n=== Test Case 5: Tree Similarity ===");

        TreeNode similar1 = new TreeNode(1);
        similar1.left = new TreeNode(2);
        similar1.right = new TreeNode(3);

        TreeNode similar2 = new TreeNode(1);
        similar2.left = new TreeNode(5); // Different value
        similar2.right = new TreeNode(3);

        boolean isSimilar = TreeSimilarity.isSimilar(similar1, similar2, 1);
        int differences = TreeSimilarity.countDifferences(similar1, similar2);
        double similarity = TreeSimilarity.calculateSimilarityScore(similar1, similar2);
        boolean thresholdSimilar = TreeSimilarity.isSimilarWithThreshold(similar1, similar2, 0.8);

        System.out.println("Similar with 1 difference: " + isSimilar);
        System.out.println("Total differences: " + differences);
        System.out.println("Similarity score: " + String.format("%.2f", similarity));
        System.out.println("Above threshold (0.8): " + thresholdSimilar);

        // Test Case 6: Largest common subtree
        System.out.println("\n=== Test Case 6: Largest Common Subtree ===");

        TreeNode lcs = LargestCommonSubtree.findLargestCommonSubtree(bigTree, tree1);
        int lcsSize = LargestCommonSubtree.getLargestCommonSubtreeSize(bigTree, tree1);
        List<TreeNode> allCommon = LargestCommonSubtree.findAllCommonSubtrees(bigTree, tree1);

        System.out.println("Has largest common subtree: " + (lcs != null));
        System.out.println("LCS size: " + lcsSize);
        System.out.println("All common subtrees: " + allCommon.size());

        // Test Case 7: Serialization
        System.out.println("\n=== Test Case 7: Serialization ===");

        String preorderSerial = TreeSerialization.serializePreorder(tree1);
        String levelSerial = TreeSerialization.serializeLevelOrder(tree1);
        TreeNode deserialized = TreeSerialization.deserializePreorder(preorderSerial);
        boolean serialEqual = TreeSerialization.compareSerializations(tree1, deserialized);
        String canonical1 = TreeSerialization.getCanonicalForm(tree1);
        String canonical2 = TreeSerialization.getCanonicalForm(mirrorTree);
        boolean canonicalEqual = TreeSerialization.isCanonicallyEqual(tree1, mirrorTree);

        System.out.println("Preorder serialization: " + preorderSerial);
        System.out.println("Level order serialization: " + levelSerial);
        System.out.println("Serialization equality: " + serialEqual);
        System.out.println("Canonical form 1: " + canonical1);
        System.out.println("Canonical form 2: " + canonical2);
        System.out.println("Canonically equal: " + canonicalEqual);

        // Test Case 8: Performance comparison
        System.out.println("\n=== Test Case 8: Performance Comparison ===");

        compareApproaches(bigTree, tree1);

        // Test Case 9: Edge cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        // Null trees
        boolean nullEqual = isSameTree(null, null);
        boolean oneNull = isSameTree(tree1, null);

        // Single node trees
        TreeNode single1 = new TreeNode(1);
        TreeNode single2 = new TreeNode(1);
        TreeNode single3 = new TreeNode(2);
        boolean singleEqual = isSameTree(single1, single2);
        boolean singleDifferent = isSameTree(single1, single3);

        System.out.println("Null trees equal: " + nullEqual);
        System.out.println("One tree null: " + oneNull);
        System.out.println("Single nodes equal: " + singleEqual);
        System.out.println("Single nodes different: " + singleDifferent);

        // Test Case 10: Stress testing
        System.out.println("\n=== Test Case 10: Stress Testing ===");

        // Create large identical trees
        TreeNode largeTree1 = createLargeTree(1000, 1);
        TreeNode largeTree2 = createLargeTree(1000, 1);
        TreeNode largeTree3 = createLargeTree(1000, 2);

        long start = System.nanoTime();
        boolean largeEqual = isSameTree(largeTree1, largeTree2);
        long end = System.nanoTime();
        System.out.println("Large identical trees: " + largeEqual + " (Time: " + (end - start) / 1_000_000 + " ms)");

        start = System.nanoTime();
        boolean largeDifferent = isSameTree(largeTree1, largeTree3);
        end = System.nanoTime();
        System.out
                .println("Large different trees: " + largeDifferent + " (Time: " + (end - start) / 1_000_000 + " ms)");

        System.out.println("\nSame Tree testing completed successfully!");
    }

    // Helper method for stress testing
    private static TreeNode createLargeTree(int maxNodes, int baseValue) {
        if (maxNodes <= 0) {
            return null;
        }

        TreeNode root = new TreeNode(baseValue);

        // Create a somewhat balanced tree
        int leftNodes = maxNodes / 2;
        int rightNodes = maxNodes - 1 - leftNodes;

        root.left = createLargeTree(leftNodes, baseValue + 1);
        root.right = createLargeTree(rightNodes, baseValue + leftNodes + 1);

        return root;
    }
}
