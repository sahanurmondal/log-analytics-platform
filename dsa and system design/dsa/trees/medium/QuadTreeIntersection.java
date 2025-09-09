package trees.medium;

import java.util.*;

/**
 * LeetCode 558: Logical OR of Two Binary Grids Represented as Quad-Trees
 * https://leetcode.com/problems/logical-or-of-two-binary-grids-represented-as-quad-trees/
 * 
 * Companies: Google, Uber
 * Frequency: Medium
 *
 * Description: Given two Quad-Trees, return a Quad-Tree that represents the
 * logical OR of the two trees.
 *
 * Constraints:
 * - quadTree1 and quadTree2 are both valid Quad-Trees
 * - n == 2^x where x >= 0
 * 
 * Follow-up Questions:
 * 1. Can you implement logical AND?
 * 2. Can you implement logical XOR?
 * 3. Can you optimize for memory usage?
 */
public class QuadTreeIntersection {

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

    // Approach 1: Recursive OR operation
    public Node intersect(Node quadTree1, Node quadTree2) {
        // If either node is a leaf
        if (quadTree1.isLeaf) {
            return quadTree1.val ? quadTree1 : quadTree2;
        }
        if (quadTree2.isLeaf) {
            return quadTree2.val ? quadTree2 : quadTree1;
        }

        // Both are internal nodes, recursively process children
        Node topLeft = intersect(quadTree1.topLeft, quadTree2.topLeft);
        Node topRight = intersect(quadTree1.topRight, quadTree2.topRight);
        Node bottomLeft = intersect(quadTree1.bottomLeft, quadTree2.bottomLeft);
        Node bottomRight = intersect(quadTree1.bottomRight, quadTree2.bottomRight);

        // Check if all children are leaves with the same value
        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf &&
                topLeft.val == topRight.val && topRight.val == bottomLeft.val && bottomLeft.val == bottomRight.val) {
            return new Node(topLeft.val, true);
        }

        return new Node(false, false, topLeft, topRight, bottomLeft, bottomRight);
    }

    // Follow-up 1: Logical AND operation
    public Node intersectAND(Node quadTree1, Node quadTree2) {
        if (quadTree1.isLeaf) {
            return quadTree1.val ? quadTree2 : quadTree1;
        }
        if (quadTree2.isLeaf) {
            return quadTree2.val ? quadTree1 : quadTree2;
        }

        Node topLeft = intersectAND(quadTree1.topLeft, quadTree2.topLeft);
        Node topRight = intersectAND(quadTree1.topRight, quadTree2.topRight);
        Node bottomLeft = intersectAND(quadTree1.bottomLeft, quadTree2.bottomLeft);
        Node bottomRight = intersectAND(quadTree1.bottomRight, quadTree2.bottomRight);

        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf &&
                topLeft.val == topRight.val && topRight.val == bottomLeft.val && bottomLeft.val == bottomRight.val) {
            return new Node(topLeft.val, true);
        }

        return new Node(false, false, topLeft, topRight, bottomLeft, bottomRight);
    }

    // Follow-up 2: Logical XOR operation
    public Node intersectXOR(Node quadTree1, Node quadTree2) {
        if (quadTree1.isLeaf && quadTree2.isLeaf) {
            return new Node(quadTree1.val ^ quadTree2.val, true);
        }

        if (quadTree1.isLeaf) {
            if (quadTree1.val) {
                return negateTree(quadTree2);
            } else {
                return cloneTree(quadTree2);
            }
        }

        if (quadTree2.isLeaf) {
            if (quadTree2.val) {
                return negateTree(quadTree1);
            } else {
                return cloneTree(quadTree1);
            }
        }

        Node topLeft = intersectXOR(quadTree1.topLeft, quadTree2.topLeft);
        Node topRight = intersectXOR(quadTree1.topRight, quadTree2.topRight);
        Node bottomLeft = intersectXOR(quadTree1.bottomLeft, quadTree2.bottomLeft);
        Node bottomRight = intersectXOR(quadTree1.bottomRight, quadTree2.bottomRight);

        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf &&
                topLeft.val == topRight.val && topRight.val == bottomLeft.val && bottomLeft.val == bottomRight.val) {
            return new Node(topLeft.val, true);
        }

        return new Node(false, false, topLeft, topRight, bottomLeft, bottomRight);
    }

    // Helper: Negate a quad tree
    private Node negateTree(Node node) {
        if (node.isLeaf) {
            return new Node(!node.val, true);
        }

        Node topLeft = negateTree(node.topLeft);
        Node topRight = negateTree(node.topRight);
        Node bottomLeft = negateTree(node.bottomLeft);
        Node bottomRight = negateTree(node.bottomRight);

        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf &&
                topLeft.val == topRight.val && topRight.val == bottomLeft.val && bottomLeft.val == bottomRight.val) {
            return new Node(topLeft.val, true);
        }

        return new Node(false, false, topLeft, topRight, bottomLeft, bottomRight);
    }

    // Helper: Clone a quad tree
    private Node cloneTree(Node node) {
        if (node.isLeaf) {
            return new Node(node.val, true);
        }

        return new Node(false, false,
                cloneTree(node.topLeft),
                cloneTree(node.topRight),
                cloneTree(node.bottomLeft),
                cloneTree(node.bottomRight));
    }

    // Follow-up 3: Memory optimized using node reuse
    public Node intersectOptimized(Node quadTree1, Node quadTree2) {
        return intersectWithReuse(quadTree1, quadTree2, new HashMap<>());
    }

    private Node intersectWithReuse(Node quadTree1, Node quadTree2, Map<String, Node> memo) {
        String key = getNodeKey(quadTree1) + "|" + getNodeKey(quadTree2);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }

        Node result;

        if (quadTree1.isLeaf) {
            result = quadTree1.val ? quadTree1 : quadTree2;
        } else if (quadTree2.isLeaf) {
            result = quadTree2.val ? quadTree2 : quadTree1;
        } else {
            Node topLeft = intersectWithReuse(quadTree1.topLeft, quadTree2.topLeft, memo);
            Node topRight = intersectWithReuse(quadTree1.topRight, quadTree2.topRight, memo);
            Node bottomLeft = intersectWithReuse(quadTree1.bottomLeft, quadTree2.bottomLeft, memo);
            Node bottomRight = intersectWithReuse(quadTree1.bottomRight, quadTree2.bottomRight, memo);

            if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf &&
                    topLeft.val == topRight.val && topRight.val == bottomLeft.val
                    && bottomLeft.val == bottomRight.val) {
                result = new Node(topLeft.val, true);
            } else {
                result = new Node(false, false, topLeft, topRight, bottomLeft, bottomRight);
            }
        }

        memo.put(key, result);
        return result;
    }

    private String getNodeKey(Node node) {
        if (node.isLeaf) {
            return "L" + node.val;
        }
        return "I" + node.hashCode();
    }

    // Helper: Convert quad tree to matrix for visualization
    public int[][] toMatrix(Node root, int size) {
        int[][] matrix = new int[size][size];
        fillMatrix(root, matrix, 0, 0, size);
        return matrix;
    }

    private void fillMatrix(Node node, int[][] matrix, int row, int col, int size) {
        if (node.isLeaf) {
            int value = node.val ? 1 : 0;
            for (int i = row; i < row + size; i++) {
                for (int j = col; j < col + size; j++) {
                    matrix[i][j] = value;
                }
            }
        } else {
            int half = size / 2;
            fillMatrix(node.topLeft, matrix, row, col, half);
            fillMatrix(node.topRight, matrix, row, col + half, half);
            fillMatrix(node.bottomLeft, matrix, row + half, col, half);
            fillMatrix(node.bottomRight, matrix, row + half, col + half, half);
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
        QuadTreeIntersection solution = new QuadTreeIntersection();

        // Test case 1: Basic OR operation
        Node tree1 = new Node(false, false);
        tree1.topLeft = new Node(true, true);
        tree1.topRight = new Node(true, true);
        tree1.bottomLeft = new Node(false, true);
        tree1.bottomRight = new Node(false, true);

        Node tree2 = new Node(false, false);
        tree2.topLeft = new Node(false, true);
        tree2.topRight = new Node(false, true);
        tree2.bottomLeft = new Node(true, true);
        tree2.bottomRight = new Node(true, true);

        System.out.println("Test 1 - OR operation:");
        System.out.println("Tree 1:");
        solution.printTree(tree1, "", true);
        System.out.println("\nTree 2:");
        solution.printTree(tree2, "", true);

        Node orResult = solution.intersect(tree1, tree2);
        System.out.println("\nOR Result:");
        solution.printTree(orResult, "", true);

        // Test case 2: AND operation
        System.out.println("\nTest 2 - AND operation:");
        Node andResult = solution.intersectAND(tree1, tree2);
        System.out.println("AND Result:");
        solution.printTree(andResult, "", true);

        // Test case 3: XOR operation
        System.out.println("\nTest 3 - XOR operation:");
        Node xorResult = solution.intersectXOR(tree1, tree2);
        System.out.println("XOR Result:");
        solution.printTree(xorResult, "", true);

        // Test case 4: Matrix visualization
        System.out.println("\nTest 4 - Matrix representation (2x2):");
        int[][] matrix1 = solution.toMatrix(tree1, 2);
        int[][] matrix2 = solution.toMatrix(tree2, 2);
        int[][] orMatrix = solution.toMatrix(orResult, 2);

        System.out.println("Matrix 1:");
        printMatrix(matrix1);
        System.out.println("Matrix 2:");
        printMatrix(matrix2);
        System.out.println("OR Matrix:");
        printMatrix(orMatrix);

        // Edge cases
        System.out.println("\nEdge cases:");
        Node leaf1 = new Node(true, true);
        Node leaf2 = new Node(false, true);
        Node leafOR = solution.intersect(leaf1, leaf2);
        System.out.println("Leaf OR result: " + leafOR.val + " (isLeaf: " + leafOR.isLeaf + ")");

        // Stress test
        System.out.println("\nStress test:");
        Node largeTree1 = buildLargeQuadTree(6); // 2^6 x 2^6 grid
        Node largeTree2 = buildLargeQuadTree(6);

        long start = System.nanoTime();
        Node largeResult = solution.intersectOptimized(largeTree1, largeTree2);
        long end = System.nanoTime();
        System.out.println("Large quad tree intersection: " + (end - start) / 1_000_000 + " ms");
    }

    private static void printMatrix(int[][] matrix) {
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }
    }

    private static Node buildLargeQuadTree(int levels) {
        if (levels == 0) {
            return new Node(Math.random() > 0.5, true);
        }

        return new Node(false, false,
                buildLargeQuadTree(levels - 1),
                buildLargeQuadTree(levels - 1),
                buildLargeQuadTree(levels - 1),
                buildLargeQuadTree(levels - 1));
    }
}
