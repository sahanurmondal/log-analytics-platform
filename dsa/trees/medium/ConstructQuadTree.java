package trees.medium;

import java.util.*;

/**
 * LeetCode 427: Construct Quad Tree
 * https://leetcode.com/problems/construct-quad-tree/
 * 
 * Companies: Google, Uber
 * Frequency: Medium
 *
 * Description: Given a n * n matrix grid, construct a Quad Tree from the
 * matrix.
 *
 * Constraints:
 * - n == grid.length == grid[i].length
 * - n is a power of 2
 * - 1 <= n <= 64
 * - grid[i][j] is 0 or 1
 * 
 * Follow-up Questions:
 * 1. Can you handle non-square matrices?
 * 2. Can you compress the tree further?
 * 3. Can you convert back to matrix?
 */
public class ConstructQuadTree {

    public static class Node {
        public boolean val;
        public boolean isLeaf;
        public Node topLeft;
        public Node topRight;
        public Node bottomLeft;
        public Node bottomRight;

        public Node() {
            this.val = false;
            this.isLeaf = false;
            this.topLeft = null;
            this.topRight = null;
            this.bottomLeft = null;
            this.bottomRight = null;
        }

        public Node(boolean val, boolean isLeaf) {
            this.val = val;
            this.isLeaf = isLeaf;
            this.topLeft = null;
            this.topRight = null;
            this.bottomLeft = null;
            this.bottomRight = null;
        }

        public Node(boolean val, boolean isLeaf, Node topLeft, Node topRight, Node bottomLeft, Node bottomRight) {
            this.val = val;
            this.isLeaf = isLeaf;
            this.topLeft = topLeft;
            this.topRight = topRight;
            this.bottomLeft = bottomLeft;
            this.bottomRight = bottomRight;
        }
    }

    // Approach 1: Recursive construction
    public Node construct(int[][] grid) {
        return constructHelper(grid, 0, 0, grid.length);
    }

    private Node constructHelper(int[][] grid, int row, int col, int size) {
        if (isUniform(grid, row, col, size)) {
            return new Node(grid[row][col] == 1, true);
        }

        int halfSize = size / 2;
        Node topLeft = constructHelper(grid, row, col, halfSize);
        Node topRight = constructHelper(grid, row, col + halfSize, halfSize);
        Node bottomLeft = constructHelper(grid, row + halfSize, col, halfSize);
        Node bottomRight = constructHelper(grid, row + halfSize, col + halfSize, halfSize);

        return new Node(false, false, topLeft, topRight, bottomLeft, bottomRight);
    }

    private boolean isUniform(int[][] grid, int row, int col, int size) {
        int val = grid[row][col];
        for (int i = row; i < row + size; i++) {
            for (int j = col; j < col + size; j++) {
                if (grid[i][j] != val)
                    return false;
            }
        }
        return true;
    }

    // Follow-up 1: Handle non-square matrices (pad to nearest power of 2)
    public Node constructNonSquare(int[][] grid) {
        int maxDim = Math.max(grid.length, grid[0].length);
        int size = 1;
        while (size < maxDim)
            size *= 2;

        int[][] paddedGrid = new int[size][size];
        for (int i = 0; i < grid.length; i++) {
            System.arraycopy(grid[i], 0, paddedGrid[i], 0, grid[i].length);
        }

        return construct(paddedGrid);
    }

    // Follow-up 2: Compress tree by merging identical children
    public Node constructCompressed(int[][] grid) {
        Node root = construct(grid);
        return compressTree(root);
    }

    private Node compressTree(Node node) {
        if (node == null || node.isLeaf)
            return node;

        Node tl = compressTree(node.topLeft);
        Node tr = compressTree(node.topRight);
        Node bl = compressTree(node.bottomLeft);
        Node br = compressTree(node.bottomRight);

        // If all children are leaves with the same value, merge them
        if (tl.isLeaf && tr.isLeaf && bl.isLeaf && br.isLeaf &&
                tl.val == tr.val && tr.val == bl.val && bl.val == br.val) {
            return new Node(tl.val, true);
        }

        return new Node(false, false, tl, tr, bl, br);
    }

    // Follow-up 3: Convert quad tree back to matrix
    public int[][] treeToMatrix(Node root, int size) {
        int[][] matrix = new int[size][size];
        fillMatrix(root, matrix, 0, 0, size);
        return matrix;
    }

    private void fillMatrix(Node node, int[][] matrix, int row, int col, int size) {
        if (node.isLeaf) {
            int val = node.val ? 1 : 0;
            for (int i = row; i < row + size; i++) {
                for (int j = col; j < col + size; j++) {
                    matrix[i][j] = val;
                }
            }
        } else {
            int halfSize = size / 2;
            fillMatrix(node.topLeft, matrix, row, col, halfSize);
            fillMatrix(node.topRight, matrix, row, col + halfSize, halfSize);
            fillMatrix(node.bottomLeft, matrix, row + halfSize, col, halfSize);
            fillMatrix(node.bottomRight, matrix, row + halfSize, col + halfSize, halfSize);
        }
    }

    // Helper: Print tree structure
    public void printTree(Node node, String prefix, boolean isLast) {
        if (node == null)
            return;

        System.out.println(prefix + (isLast ? "└── " : "├── ") +
                (node.isLeaf ? "Leaf(" + node.val + ")" : "Internal"));

        if (!node.isLeaf) {
            printTree(node.topLeft, prefix + (isLast ? "    " : "│   "), false);
            printTree(node.topRight, prefix + (isLast ? "    " : "│   "), false);
            printTree(node.bottomLeft, prefix + (isLast ? "    " : "│   "), false);
            printTree(node.bottomRight, prefix + (isLast ? "    " : "│   "), true);
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ConstructQuadTree solution = new ConstructQuadTree();

        // Test case 1: Basic case
        int[][] grid1 = {
                { 0, 1 },
                { 1, 0 }
        };
        System.out.println("Test 1 - Basic 2x2 grid:");
        Node tree1 = solution.construct(grid1);
        solution.printTree(tree1, "", true);

        // Test case 2: Uniform grid
        int[][] grid2 = {
                { 1, 1 },
                { 1, 1 }
        };
        System.out.println("\nTest 2 - Uniform grid:");
        Node tree2 = solution.construct(grid2);
        solution.printTree(tree2, "", true);

        // Test case 3: Larger grid
        int[][] grid3 = {
                { 1, 1, 0, 0 },
                { 1, 1, 0, 0 },
                { 1, 1, 1, 1 },
                { 1, 1, 1, 1 }
        };
        System.out.println("\nTest 3 - 4x4 grid:");
        Node tree3 = solution.construct(grid3);
        solution.printTree(tree3, "", true);

        // Test case 4: Convert back to matrix
        System.out.println("\nTest 4 - Convert back to matrix:");
        int[][] reconstructed = solution.treeToMatrix(tree3, 4);
        for (int[] row : reconstructed) {
            System.out.println(Arrays.toString(row));
        }

        // Test case 5: Compressed tree
        System.out.println("\nTest 5 - Compressed tree:");
        Node compressed = solution.constructCompressed(grid3);
        solution.printTree(compressed, "", true);

        // Edge cases
        System.out.println("\nEdge cases:");
        int[][] single = { { 1 } };
        Node singleTree = solution.construct(single);
        System.out.println("Single cell tree is leaf: " + singleTree.isLeaf);

        int[][] allZeros = { { 0, 0 }, { 0, 0 } };
        Node zeroTree = solution.construct(allZeros);
        System.out.println("All zeros tree is leaf: " + zeroTree.isLeaf + ", value: " + zeroTree.val);

        // Stress test
        System.out.println("\nStress test:");
        int size = 64;
        int[][] largeGrid = new int[size][size];
        Random rand = new Random(42);
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                largeGrid[i][j] = rand.nextInt(2);
            }
        }

        long start = System.nanoTime();
        Node largeTree = solution.construct(largeGrid);
        long end = System.nanoTime();
        System.out.println("64x64 grid processed in: " + (end - start) / 1_000_000 + " ms");
    }
}
