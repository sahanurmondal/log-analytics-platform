package trees.easy;

import java.util.*;

/**
 * LeetCode 572: Subtree of Another Tree
 * https://leetcode.com/problems/subtree-of-another-tree/
 * 
 * Companies: Amazon, Google, Microsoft, Facebook, Apple, Bloomberg
 * Frequency: High (Asked in 1000+ interviews)
 *
 * Description:
 * Given the roots of two binary trees root and subRoot, return true if there is
 * a subtree
 * of root with the same structure and node values as subRoot and false
 * otherwise.
 * 
 * A subtree of a binary tree tree is a tree that consists of a node in tree and
 * all of this node's descendants.
 * The tree tree could also be considered as a subtree of itself.
 * 
 * Constraints:
 * - The number of nodes in the root tree is in the range [1, 2000].
 * - The number of nodes in the subRoot tree is in the range [1, 1000].
 * - -10^4 <= root.val <= 10^4
 * - -10^4 <= subRoot.val <= 10^4
 * 
 * Follow-up Questions:
 * 1. How would you find all occurrences of the subtree?
 * 2. Can you find the largest common subtree between two trees?
 * 3. What about finding subtrees with similar structure but different values?
 * 4. How to handle subtree matching with wildcards or patterns?
 * 5. Can you find the deepest subtree match?
 * 6. What about finding subtrees that are rotations of each other?
 */
public class SubtreeOfAnotherTree {

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

    // Approach 1: Recursive traversal with tree comparison - O(mn) time, O(m+n)
    // space
    public static boolean isSubtree(TreeNode root, TreeNode subRoot) {
        if (subRoot == null) {
            return true;
        }

        if (root == null) {
            return false;
        }

        // Check if trees are identical starting from current node
        if (isSameTree(root, subRoot)) {
            return true;
        }

        // Recursively check left and right subtrees
        return isSubtree(root.left, subRoot) || isSubtree(root.right, subRoot);
    }

    private static boolean isSameTree(TreeNode p, TreeNode q) {
        if (p == null && q == null) {
            return true;
        }

        if (p == null || q == null) {
            return false;
        }

        return p.val == q.val &&
                isSameTree(p.left, q.left) &&
                isSameTree(p.right, q.right);
    }

    // Approach 2: String serialization approach - O(m+n) time, O(m+n) space
    public static boolean isSubtreeSerialization(TreeNode root, TreeNode subRoot) {
        String rootStr = serialize(root);
        String subRootStr = serialize(subRoot);

        return rootStr.contains("," + subRootStr + ",");
    }

    private static String serialize(TreeNode node) {
        if (node == null) {
            return "null";
        }

        return "#" + node.val + "," + serialize(node.left) + "," + serialize(node.right);
    }

    // Approach 3: Hash-based approach - O(m+n) time, O(m+n) space
    public static boolean isSubtreeHashing(TreeNode root, TreeNode subRoot) {
        Map<String, Integer> treeHashes = new HashMap<>();
        String subRootHash = computeHash(subRoot, treeHashes);

        return findHashInTree(root, subRootHash, treeHashes);
    }

    private static String computeHash(TreeNode node, Map<String, Integer> hashes) {
        if (node == null) {
            return "null";
        }

        String leftHash = computeHash(node.left, hashes);
        String rightHash = computeHash(node.right, hashes);
        String currentHash = leftHash + "," + node.val + "," + rightHash;

        hashes.put(currentHash, hashes.getOrDefault(currentHash, 0) + 1);
        return currentHash;
    }

    private static boolean findHashInTree(TreeNode node, String targetHash, Map<String, Integer> hashes) {
        if (node == null) {
            return false;
        }

        String currentHash = computeHash(node, new HashMap<>());
        if (currentHash.equals(targetHash)) {
            return true;
        }

        return findHashInTree(node.left, targetHash, hashes) ||
                findHashInTree(node.right, targetHash, hashes);
    }

    // Approach 4: Iterative approach with stack - O(mn) time, O(m+n) space
    public static boolean isSubtreeIterative(TreeNode root, TreeNode subRoot) {
        if (subRoot == null) {
            return true;
        }

        if (root == null) {
            return false;
        }

        Stack<TreeNode> stack = new Stack<>();
        stack.push(root);

        while (!stack.isEmpty()) {
            TreeNode current = stack.pop();

            if (isSameTree(current, subRoot)) {
                return true;
            }

            if (current.right != null) {
                stack.push(current.right);
            }

            if (current.left != null) {
                stack.push(current.left);
            }
        }

        return false;
    }

    // Approach 5: BFS with level-order traversal - O(mn) time, O(m) space
    public static boolean isSubtreeBFS(TreeNode root, TreeNode subRoot) {
        if (subRoot == null) {
            return true;
        }

        if (root == null) {
            return false;
        }

        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            TreeNode current = queue.poll();

            if (isSameTree(current, subRoot)) {
                return true;
            }

            if (current.left != null) {
                queue.offer(current.left);
            }

            if (current.right != null) {
                queue.offer(current.right);
            }
        }

        return false;
    }

    // Follow-up 1: Find all subtree occurrences
    public static class AllSubtreeOccurrences {

        public static List<TreeNode> findAllOccurrences(TreeNode root, TreeNode subRoot) {
            List<TreeNode> occurrences = new ArrayList<>();
            if (subRoot == null) {
                return occurrences;
            }

            findAllOccurrencesHelper(root, subRoot, occurrences);
            return occurrences;
        }

        private static void findAllOccurrencesHelper(TreeNode root, TreeNode subRoot,
                List<TreeNode> occurrences) {
            if (root == null) {
                return;
            }

            if (isSameTree(root, subRoot)) {
                occurrences.add(root);
            }

            findAllOccurrencesHelper(root.left, subRoot, occurrences);
            findAllOccurrencesHelper(root.right, subRoot, occurrences);
        }

        public static int countOccurrences(TreeNode root, TreeNode subRoot) {
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

            return count + countOccurrences(root.left, subRoot) +
                    countOccurrences(root.right, subRoot);
        }

        public static List<String> findAllOccurrencePaths(TreeNode root, TreeNode subRoot) {
            List<String> paths = new ArrayList<>();
            if (subRoot == null) {
                return paths;
            }

            findOccurrencePathsHelper(root, subRoot, "", paths);
            return paths;
        }

        private static void findOccurrencePathsHelper(TreeNode root, TreeNode subRoot,
                String path, List<String> paths) {
            if (root == null) {
                return;
            }

            String currentPath = path.isEmpty() ? String.valueOf(root.val) : path + "->" + root.val;

            if (isSameTree(root, subRoot)) {
                paths.add(currentPath);
            }

            findOccurrencePathsHelper(root.left, subRoot, currentPath, paths);
            findOccurrencePathsHelper(root.right, subRoot, currentPath, paths);
        }
    }

    // Follow-up 2: Largest common subtree
    public static class LargestCommonSubtree {

        public static TreeNode findLargestCommonSubtree(TreeNode root1, TreeNode root2) {
            TreeNode[] largest = { null };
            int[] maxSize = { 0 };

            findLargestCommonSubtreeHelper(root1, root2, largest, maxSize);
            return largest[0];
        }

        private static int findLargestCommonSubtreeHelper(TreeNode p, TreeNode q,
                TreeNode[] largest, int[] maxSize) {
            if (p == null || q == null) {
                return 0;
            }

            int leftSize = findLargestCommonSubtreeHelper(p.left, q.left, largest, maxSize);
            int rightSize = findLargestCommonSubtreeHelper(p.right, q.right, largest, maxSize);

            if (isSameTree(p, q)) {
                int currentSize = 1 + leftSize + rightSize;
                if (currentSize > maxSize[0]) {
                    maxSize[0] = currentSize;
                    largest[0] = p;
                }
                return currentSize;
            }

            return 0;
        }

        public static int getLargestCommonSubtreeSize(TreeNode root1, TreeNode root2) {
            TreeNode lcs = findLargestCommonSubtree(root1, root2);
            return countNodes(lcs);
        }

        private static int countNodes(TreeNode root) {
            if (root == null) {
                return 0;
            }
            return 1 + countNodes(root.left) + countNodes(root.right);
        }

        public static List<TreeNode> findAllCommonSubtrees(TreeNode root1, TreeNode root2) {
            List<TreeNode> commonSubtrees = new ArrayList<>();
            findAllCommonSubtreesHelper(root1, root2, commonSubtrees);
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

            // Check all possible combinations
            findAllCommonSubtreesHelper(p.left, q.left, commonSubtrees);
            findAllCommonSubtreesHelper(p.left, q.right, commonSubtrees);
            findAllCommonSubtreesHelper(p.right, q.left, commonSubtrees);
            findAllCommonSubtreesHelper(p.right, q.right, commonSubtrees);
        }
    }

    // Follow-up 3: Similar structure with different values
    public static class StructuralSimilarity {

        public static boolean hasSimilarStructure(TreeNode root, TreeNode pattern) {
            if (pattern == null) {
                return true;
            }

            if (root == null) {
                return false;
            }

            if (isSameStructure(root, pattern)) {
                return true;
            }

            return hasSimilarStructure(root.left, pattern) ||
                    hasSimilarStructure(root.right, pattern);
        }

        private static boolean isSameStructure(TreeNode p, TreeNode q) {
            if (p == null && q == null) {
                return true;
            }

            if (p == null || q == null) {
                return false;
            }

            return isSameStructure(p.left, q.left) && isSameStructure(p.right, q.right);
        }

        public static String getStructureSignature(TreeNode root) {
            if (root == null) {
                return "null";
            }

            return "(" + getStructureSignature(root.left) + "," +
                    getStructureSignature(root.right) + ")";
        }

        public static List<TreeNode> findSimilarStructures(TreeNode root, TreeNode pattern) {
            List<TreeNode> similar = new ArrayList<>();
            if (pattern == null) {
                return similar;
            }

            String patternSig = getStructureSignature(pattern);
            findSimilarStructuresHelper(root, patternSig, similar);
            return similar;
        }

        private static void findSimilarStructuresHelper(TreeNode root, String patternSig,
                List<TreeNode> similar) {
            if (root == null) {
                return;
            }

            if (getStructureSignature(root).equals(patternSig)) {
                similar.add(root);
            }

            findSimilarStructuresHelper(root.left, patternSig, similar);
            findSimilarStructuresHelper(root.right, patternSig, similar);
        }

        public static boolean isIsomorphicSubtree(TreeNode root, TreeNode pattern) {
            if (pattern == null) {
                return true;
            }

            if (root == null) {
                return false;
            }

            if (isIsomorphic(root, pattern)) {
                return true;
            }

            return isIsomorphicSubtree(root.left, pattern) ||
                    isIsomorphicSubtree(root.right, pattern);
        }

        private static boolean isIsomorphic(TreeNode p, TreeNode q) {
            if (p == null && q == null) {
                return true;
            }

            if (p == null || q == null) {
                return false;
            }

            // Two possibilities: no flip or flip
            return (isIsomorphic(p.left, q.left) && isIsomorphic(p.right, q.right)) ||
                    (isIsomorphic(p.left, q.right) && isIsomorphic(p.right, q.left));
        }
    }

    // Follow-up 4: Pattern matching with wildcards
    public static class PatternMatching {

        public static class PatternNode {
            Integer val; // null represents wildcard
            PatternNode left;
            PatternNode right;

            PatternNode(Integer val) {
                this.val = val;
            }
        }

        public static boolean matchesPattern(TreeNode root, PatternNode pattern) {
            if (pattern == null && root == null) {
                return true;
            }

            if (pattern == null || root == null) {
                return false;
            }

            // Wildcard matches any value
            if (pattern.val == null || pattern.val.equals(root.val)) {
                return matchesPattern(root.left, pattern.left) &&
                        matchesPattern(root.right, pattern.right);
            }

            return false;
        }

        public static boolean hasPatternSubtree(TreeNode root, PatternNode pattern) {
            if (pattern == null) {
                return true;
            }

            if (root == null) {
                return false;
            }

            if (matchesPattern(root, pattern)) {
                return true;
            }

            return hasPatternSubtree(root.left, pattern) ||
                    hasPatternSubtree(root.right, pattern);
        }

        public static List<TreeNode> findPatternMatches(TreeNode root, PatternNode pattern) {
            List<TreeNode> matches = new ArrayList<>();
            if (pattern == null) {
                return matches;
            }

            findPatternMatchesHelper(root, pattern, matches);
            return matches;
        }

        private static void findPatternMatchesHelper(TreeNode root, PatternNode pattern,
                List<TreeNode> matches) {
            if (root == null) {
                return;
            }

            if (matchesPattern(root, pattern)) {
                matches.add(root);
            }

            findPatternMatchesHelper(root.left, pattern, matches);
            findPatternMatchesHelper(root.right, pattern, matches);
        }

        public static PatternNode createWildcardPattern(TreeNode template) {
            if (template == null) {
                return null;
            }

            PatternNode pattern = new PatternNode(null); // Wildcard
            pattern.left = createWildcardPattern(template.left);
            pattern.right = createWildcardPattern(template.right);

            return pattern;
        }
    }

    // Follow-up 5: Deepest subtree match
    public static class DeepestMatch {

        public static TreeNode findDeepestSubtreeMatch(TreeNode root, TreeNode subRoot) {
            TreeNode[] deepest = { null };
            int[] maxDepth = { -1 };

            findDeepestMatchHelper(root, subRoot, 0, deepest, maxDepth);
            return deepest[0];
        }

        private static void findDeepestMatchHelper(TreeNode root, TreeNode subRoot, int depth,
                TreeNode[] deepest, int[] maxDepth) {
            if (root == null) {
                return;
            }

            if (isSameTree(root, subRoot) && depth > maxDepth[0]) {
                deepest[0] = root;
                maxDepth[0] = depth;
            }

            findDeepestMatchHelper(root.left, subRoot, depth + 1, deepest, maxDepth);
            findDeepestMatchHelper(root.right, subRoot, depth + 1, deepest, maxDepth);
        }

        public static int getDepthOfMatch(TreeNode root, TreeNode subRoot) {
            int[] depth = { -1 };
            findDepthOfMatch(root, subRoot, 0, depth);
            return depth[0];
        }

        private static void findDepthOfMatch(TreeNode root, TreeNode subRoot, int currentDepth,
                int[] depth) {
            if (root == null || depth[0] != -1) {
                return;
            }

            if (isSameTree(root, subRoot)) {
                depth[0] = currentDepth;
                return;
            }

            findDepthOfMatch(root.left, subRoot, currentDepth + 1, depth);
            findDepthOfMatch(root.right, subRoot, currentDepth + 1, depth);
        }

        public static List<Integer> getAllMatchDepths(TreeNode root, TreeNode subRoot) {
            List<Integer> depths = new ArrayList<>();
            findAllMatchDepths(root, subRoot, 0, depths);
            return depths;
        }

        private static void findAllMatchDepths(TreeNode root, TreeNode subRoot, int currentDepth,
                List<Integer> depths) {
            if (root == null) {
                return;
            }

            if (isSameTree(root, subRoot)) {
                depths.add(currentDepth);
            }

            findAllMatchDepths(root.left, subRoot, currentDepth + 1, depths);
            findAllMatchDepths(root.right, subRoot, currentDepth + 1, depths);
        }
    }

    // Follow-up 6: Rotational subtrees
    public static class RotationalSubtrees {

        public static boolean hasRotationalSubtree(TreeNode root, TreeNode subRoot) {
            if (subRoot == null) {
                return true;
            }

            if (root == null) {
                return false;
            }

            // Check all possible rotations
            if (isRotation(root, subRoot)) {
                return true;
            }

            return hasRotationalSubtree(root.left, subRoot) ||
                    hasRotationalSubtree(root.right, subRoot);
        }

        private static boolean isRotation(TreeNode p, TreeNode q) {
            if (p == null && q == null) {
                return true;
            }

            if (p == null || q == null) {
                return false;
            }

            if (p.val != q.val) {
                return false;
            }

            // Check all possible rotations
            return (isRotation(p.left, q.left) && isRotation(p.right, q.right)) || // No rotation
                    (isRotation(p.left, q.right) && isRotation(p.right, q.left)); // 180° rotation
        }

        public static TreeNode rotateTree(TreeNode root) {
            if (root == null) {
                return null;
            }

            TreeNode rotated = new TreeNode(root.val);
            rotated.left = rotateTree(root.right);
            rotated.right = rotateTree(root.left);

            return rotated;
        }

        public static List<TreeNode> generateAllRotations(TreeNode root) {
            List<TreeNode> rotations = new ArrayList<>();
            if (root == null) {
                return rotations;
            }

            // Original
            rotations.add(copyTree(root));

            // 180° rotation
            rotations.add(rotateTree(root));

            return rotations;
        }

        private static TreeNode copyTree(TreeNode root) {
            if (root == null) {
                return null;
            }

            TreeNode copy = new TreeNode(root.val);
            copy.left = copyTree(root.left);
            copy.right = copyTree(root.right);

            return copy;
        }

        public static boolean isAnyRotationSubtree(TreeNode root, TreeNode subRoot) {
            List<TreeNode> rotations = generateAllRotations(subRoot);

            for (TreeNode rotation : rotations) {
                if (isSubtree(root, rotation)) {
                    return true;
                }
            }

            return false;
        }
    }

    // Utility methods for testing
    public static TreeNode createSampleTree() {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(4);
        root.right = new TreeNode(5);
        root.left.left = new TreeNode(1);
        root.left.right = new TreeNode(2);
        return root;
    }

    public static TreeNode createSubTree() {
        TreeNode sub = new TreeNode(4);
        sub.left = new TreeNode(1);
        sub.right = new TreeNode(2);
        return sub;
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

    public static void printTree(TreeNode root, String prefix) {
        if (root == null) {
            return;
        }

        System.out.println(prefix + root.val);
        if (root.left != null || root.right != null) {
            printTree(root.left, prefix + "  L: ");
            printTree(root.right, prefix + "  R: ");
        }
    }

    // Performance comparison utility
    public static void compareApproaches(TreeNode root, TreeNode subRoot) {
        System.out.println("=== Performance Comparison ===");

        long start, end;
        int iterations = 10000;

        // Recursive approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSubtree(root, subRoot);
        }
        end = System.nanoTime();
        System.out.println("Recursive: " + (end - start) / 1_000_000 + " ms");

        // Serialization approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSubtreeSerialization(root, subRoot);
        }
        end = System.nanoTime();
        System.out.println("Serialization: " + (end - start) / 1_000_000 + " ms");

        // Iterative approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSubtreeIterative(root, subRoot);
        }
        end = System.nanoTime();
        System.out.println("Iterative: " + (end - start) / 1_000_000 + " ms");

        // BFS approach
        start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            isSubtreeBFS(root, subRoot);
        }
        end = System.nanoTime();
        System.out.println("BFS: " + (end - start) / 1_000_000 + " ms");
    }

    public static void main(String[] args) {
        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic Functionality ===");

        TreeNode root = createSampleTree();
        TreeNode subRoot = createSubTree();

        boolean result1 = isSubtree(root, subRoot);
        boolean result2 = isSubtreeSerialization(root, subRoot);
        boolean result3 = isSubtreeIterative(root, subRoot);
        boolean result4 = isSubtreeBFS(root, subRoot);

        System.out.println("Sample tree:");
        printTree(root, "");
        System.out.println("\nSubtree:");
        printTree(subRoot, "");

        System.out.println("\nResults:");
        System.out.println("Recursive: " + result1);
        System.out.println("Serialization: " + result2);
        System.out.println("Iterative: " + result3);
        System.out.println("BFS: " + result4);

        // Test Case 2: All subtree occurrences
        System.out.println("\n=== Test Case 2: All Subtree Occurrences ===");

        TreeNode complexTree = createComplexTree();
        TreeNode smallSubtree = new TreeNode(4);

        List<TreeNode> allOccurrences = AllSubtreeOccurrences.findAllOccurrences(complexTree, smallSubtree);
        int occurrenceCount = AllSubtreeOccurrences.countOccurrences(complexTree, smallSubtree);
        List<String> occurrencePaths = AllSubtreeOccurrences.findAllOccurrencePaths(complexTree, smallSubtree);

        System.out.println("Complex tree:");
        printTree(complexTree, "");
        System.out.println("\nSearching for single node with value 4:");
        System.out.println("Found " + allOccurrences.size() + " occurrences");
        System.out.println("Count: " + occurrenceCount);
        System.out.println("Paths: " + occurrencePaths);

        // Test Case 3: Largest common subtree
        System.out.println("\n=== Test Case 3: Largest Common Subtree ===");

        TreeNode tree1 = createSampleTree();
        TreeNode tree2 = createComplexTree();

        int lcsSize = LargestCommonSubtree.getLargestCommonSubtreeSize(tree1, tree2);
        List<TreeNode> allCommon = LargestCommonSubtree.findAllCommonSubtrees(tree1, tree2);

        System.out.println("Tree1:");
        printTree(tree1, "");
        System.out.println("\nTree2:");
        printTree(tree2, "");

        System.out.println("\nLargest common subtree size: " + lcsSize);
        System.out.println("All common subtrees: " + allCommon.size());

        // Test Case 4: Structural similarity
        System.out.println("\n=== Test Case 4: Structural Similarity ===");

        TreeNode structPattern = new TreeNode(100);
        structPattern.left = new TreeNode(200);
        structPattern.right = new TreeNode(300);

        boolean hasSimilar = StructuralSimilarity.hasSimilarStructure(tree1, structPattern);
        String sig1 = StructuralSimilarity.getStructureSignature(tree1);
        String sig2 = StructuralSimilarity.getStructureSignature(structPattern);
        List<TreeNode> similarStructures = StructuralSimilarity.findSimilarStructures(complexTree, structPattern);

        System.out.println("Has similar structure: " + hasSimilar);
        System.out.println("Structure signature 1: " + sig1);
        System.out.println("Structure signature 2: " + sig2);
        System.out.println("Found similar structures: " + similarStructures.size());

        // Test Case 5: Pattern matching
        System.out.println("\n=== Test Case 5: Pattern Matching ===");

        PatternMatching.PatternNode pattern = new PatternMatching.PatternNode(null); // Wildcard
        pattern.left = new PatternMatching.PatternNode(1);
        pattern.right = new PatternMatching.PatternNode(2);

        boolean hasPattern = PatternMatching.hasPatternSubtree(root, pattern);
        List<TreeNode> patternMatches = PatternMatching.findPatternMatches(root, pattern);

        System.out.println("Pattern matches with wildcard root:");
        System.out.println("Has pattern: " + hasPattern);
        System.out.println("Pattern matches: " + patternMatches.size());

        // Test Case 6: Deepest match
        System.out.println("\n=== Test Case 6: Deepest Match ===");

        TreeNode deepTree = createComplexTree();
        TreeNode leafNode = new TreeNode(8);

        TreeNode deepestMatch = DeepestMatch.findDeepestSubtreeMatch(deepTree, leafNode);
        int matchDepth = DeepestMatch.getDepthOfMatch(deepTree, leafNode);
        List<Integer> allDepths = DeepestMatch.getAllMatchDepths(deepTree, leafNode);

        System.out.println("Searching for leaf node with value 8:");
        System.out.println("Has deepest match: " + (deepestMatch != null));
        System.out.println("Match depth: " + matchDepth);
        System.out.println("All match depths: " + allDepths);

        // Test Case 7: Rotational subtrees
        System.out.println("\n=== Test Case 7: Rotational Subtrees ===");

        TreeNode originalPattern = new TreeNode(1);
        originalPattern.left = new TreeNode(2);
        originalPattern.right = new TreeNode(3);

        TreeNode rotatedTree = RotationalSubtrees.rotateTree(originalPattern);
        boolean hasRotational = RotationalSubtrees.hasRotationalSubtree(complexTree, originalPattern);
        boolean isAnyRotation = RotationalSubtrees.isAnyRotationSubtree(complexTree, originalPattern);

        System.out.println("Original pattern:");
        printTree(originalPattern, "");
        System.out.println("\nRotated pattern:");
        printTree(rotatedTree, "");

        System.out.println("\nHas rotational subtree: " + hasRotational);
        System.out.println("Is any rotation subtree: " + isAnyRotation);

        // Test Case 8: Performance comparison
        System.out.println("\n=== Test Case 8: Performance Comparison ===");

        compareApproaches(complexTree, subRoot);

        // Test Case 9: Edge cases
        System.out.println("\n=== Test Case 9: Edge Cases ===");

        // Null subtree
        boolean nullSubtree = isSubtree(root, null);

        // Null main tree
        boolean nullMainTree = isSubtree(null, subRoot);

        // Same tree
        boolean sameTree = isSubtree(root, root);

        // Single node trees
        TreeNode single1 = new TreeNode(1);
        TreeNode single2 = new TreeNode(1);
        boolean singleNodes = isSubtree(single1, single2);

        System.out.println("Null subtree: " + nullSubtree);
        System.out.println("Null main tree: " + nullMainTree);
        System.out.println("Same tree: " + sameTree);
        System.out.println("Single nodes: " + singleNodes);

        // Test Case 10: Stress testing
        System.out.println("\n=== Test Case 10: Stress Testing ===");

        // Create large tree
        TreeNode largeTree = createLargeTree(1000);
        TreeNode smallPattern = new TreeNode(500);
        smallPattern.left = new TreeNode(600);
        smallPattern.right = new TreeNode(700);

        long start = System.nanoTime();
        boolean largeResult = isSubtree(largeTree, smallPattern);
        long end = System.nanoTime();

        System.out.println("Large tree test: " + largeResult + " (Time: " + (end - start) / 1_000_000 + " ms)");

        // Test with many occurrences
        TreeNode manyOccurrences = createTreeWithManyOccurrences(100, 5);
        TreeNode commonPattern = new TreeNode(5);

        start = System.nanoTime();
        int manyCount = AllSubtreeOccurrences.countOccurrences(manyOccurrences, commonPattern);
        end = System.nanoTime();

        System.out.println(
                "Many occurrences test: " + manyCount + " occurrences (Time: " + (end - start) / 1_000_000 + " ms)");

        System.out.println("\nSubtree of Another Tree testing completed successfully!");
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

    private static TreeNode createTreeWithManyOccurrences(int totalNodes, int targetValue) {
        if (totalNodes <= 0) {
            return null;
        }

        // Create tree with many nodes having the target value
        TreeNode root = new TreeNode(totalNodes % 3 == 0 ? targetValue : totalNodes);

        int leftNodes = totalNodes / 2;
        int rightNodes = totalNodes - 1 - leftNodes;

        root.left = createTreeWithManyOccurrences(leftNodes, targetValue);
        root.right = createTreeWithManyOccurrences(rightNodes, targetValue);

        return root;
    }
}
