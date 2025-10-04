package trees.medium;

import java.util.*;

/**
 * LeetCode 116: Populating Next Right Pointers in Each Node
 * https://leetcode.com/problems/populating-next-right-pointers-in-each-node/
 * 
 * Companies: Amazon, Google, Microsoft
 * Frequency: High
 *
 * Description: You are given a perfect binary tree. Populate each next pointer
 * to point to its next right node.
 *
 * Constraints:
 * - The number of nodes in the tree is in the range [0, 2^12 - 1]
 * - -1000 <= Node.val <= 1000
 * - Tree is a perfect binary tree
 * 
 * Follow-up Questions:
 * 1. Can you solve with O(1) extra space?
 * 2. Can you handle non-perfect binary trees?
 * 3. Can you populate left pointers as well?
 */
public class PopulatingNextRightPointersInEachNode {

    public static class Node {
        public int val;
        public Node left;
        public Node right;
        public Node next;

        public Node() {
        }

        public Node(int val) {
            this.val = val;
        }

        public Node(int val, Node left, Node right, Node next) {
            this.val = val;
            this.left = left;
            this.right = right;
            this.next = next;
        }
    }

    // Approach 1: BFS using queue (O(n) space)
    public Node connect(Node root) {
        if (root == null)
            return null;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            Node prev = null;

            for (int i = 0; i < size; i++) {
                Node current = queue.poll();

                if (prev != null) {
                    prev.next = current;
                }
                prev = current;

                if (current.left != null)
                    queue.offer(current.left);
                if (current.right != null)
                    queue.offer(current.right);
            }
        }

        return root;
    }

    // Follow-up 1: O(1) space solution using next pointers
    public Node connectOptimal(Node root) {
        if (root == null)
            return null;

        Node leftmost = root;

        while (leftmost.left != null) {
            Node head = leftmost;

            while (head != null) {
                // Connect left child to right child
                head.left.next = head.right;

                // Connect right child to next node's left child
                if (head.next != null) {
                    head.right.next = head.next.left;
                }

                head = head.next;
            }

            leftmost = leftmost.left;
        }

        return root;
    }

    // Follow-up 2: Handle non-perfect binary trees
    public Node connectII(Node root) {
        if (root == null)
            return null;

        Node level_start = root;

        while (level_start != null) {
            Node curr = level_start;
            Node prev = null;
            Node next_level_start = null;

            while (curr != null) {
                if (curr.left != null) {
                    if (prev != null) {
                        prev.next = curr.left;
                    } else {
                        next_level_start = curr.left;
                    }
                    prev = curr.left;
                }

                if (curr.right != null) {
                    if (prev != null) {
                        prev.next = curr.right;
                    } else {
                        next_level_start = curr.right;
                    }
                    prev = curr.right;
                }

                curr = curr.next;
            }

            level_start = next_level_start;
        }

        return root;
    }

    // Follow-up 3: Populate left pointers as well (bidirectional)
    public static class BiNode {
        public int val;
        public BiNode left;
        public BiNode right;
        public BiNode next;
        public BiNode prev;

        public BiNode(int val) {
            this.val = val;
        }
    }

    public BiNode connectBidirectional(BiNode root) {
        if (root == null)
            return null;

        Queue<BiNode> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            BiNode prevNode = null;

            for (int i = 0; i < size; i++) {
                BiNode current = queue.poll();

                if (prevNode != null) {
                    prevNode.next = current;
                    current.prev = prevNode;
                }
                prevNode = current;

                if (current.left != null)
                    queue.offer(current.left);
                if (current.right != null)
                    queue.offer(current.right);
            }
        }

        return root;
    }

    // Additional: Get nodes at each level
    public List<List<Integer>> getLevels(Node root) {
        List<List<Integer>> levels = new ArrayList<>();
        if (root == null)
            return levels;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            List<Integer> level = new ArrayList<>();

            for (int i = 0; i < size; i++) {
                Node node = queue.poll();
                level.add(node.val);

                if (node.left != null)
                    queue.offer(node.left);
                if (node.right != null)
                    queue.offer(node.right);
            }

            levels.add(level);
        }

        return levels;
    }

    // Additional: Verify next pointers are correctly set
    public boolean verifyNextPointers(Node root) {
        if (root == null)
            return true;

        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            int size = queue.size();
            Node prev = null;

            for (int i = 0; i < size; i++) {
                Node current = queue.poll();

                if (prev != null && prev.next != current) {
                    return false;
                }

                if (i == size - 1 && current.next != null) {
                    return false; // Last node in level should point to null
                }

                prev = current;

                if (current.left != null)
                    queue.offer(current.left);
                if (current.right != null)
                    queue.offer(current.right);
            }
        }

        return true;
    }

    // Helper: Print next pointers for each level
    public void printNextPointers(Node root) {
        if (root == null)
            return;

        Node levelStart = root;
        int level = 1;

        while (levelStart != null) {
            System.out.print("Level " + level + ": ");
            Node current = levelStart;
            Node nextLevelStart = null;

            while (current != null) {
                System.out.print(current.val);
                if (current.next != null) {
                    System.out.print(" -> ");
                }

                if (nextLevelStart == null) {
                    nextLevelStart = current.left != null ? current.left : current.right;
                }

                current = current.next;
            }
            System.out.println(" -> null");

            levelStart = nextLevelStart;
            level++;
        }
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        PopulatingNextRightPointersInEachNode solution = new PopulatingNextRightPointersInEachNode();

        // Test case 1: Perfect binary tree
        Node root1 = new Node(1);
        root1.left = new Node(2);
        root1.right = new Node(3);
        root1.left.left = new Node(4);
        root1.left.right = new Node(5);
        root1.right.left = new Node(6);
        root1.right.right = new Node(7);

        System.out.println("Test 1 - Perfect binary tree (BFS approach):");
        solution.connect(root1);
        solution.printNextPointers(root1);
        System.out.println("Verification: " + solution.verifyNextPointers(root1));

        // Test case 2: Perfect binary tree with optimal approach
        Node root2 = new Node(1);
        root2.left = new Node(2);
        root2.right = new Node(3);
        root2.left.left = new Node(4);
        root2.left.right = new Node(5);
        root2.right.left = new Node(6);
        root2.right.right = new Node(7);

        System.out.println("\nTest 2 - Perfect binary tree (O(1) space):");
        solution.connectOptimal(root2);
        solution.printNextPointers(root2);

        // Test case 3: Non-perfect binary tree
        Node root3 = new Node(1);
        root3.left = new Node(2);
        root3.right = new Node(3);
        root3.left.left = new Node(4);
        root3.left.right = new Node(5);
        root3.right.right = new Node(7);

        System.out.println("\nTest 3 - Non-perfect binary tree:");
        solution.connectII(root3);
        solution.printNextPointers(root3);

        // Test case 4: Levels extraction
        System.out.println("\nTest 4 - Levels:");
        List<List<Integer>> levels = solution.getLevels(root1);
        for (int i = 0; i < levels.size(); i++) {
            System.out.println("Level " + (i + 1) + ": " + levels.get(i));
        }

        // Edge cases
        System.out.println("\nEdge cases:");
        Node singleNode = new Node(1);
        solution.connect(singleNode);
        System.out.println("Single node verification: " + solution.verifyNextPointers(singleNode));

        System.out.println("Null tree: " + (solution.connect(null) == null));

        // Stress test
        System.out.println("\nStress test:");
        Node largeTree = buildPerfectTree(10); // 2^10 - 1 = 1023 nodes

        long start = System.nanoTime();
        solution.connectOptimal(largeTree);
        long end = System.nanoTime();
        System.out.println("Large perfect tree connected in: " + (end - start) / 1_000_000 + " ms");
        System.out.println("Verification: " + solution.verifyNextPointers(largeTree));
    }

    private static Node buildPerfectTree(int levels) {
        if (levels <= 0)
            return null;

        Node root = new Node(1);
        Queue<Node> queue = new LinkedList<>();
        queue.offer(root);
        int currentLevel = 1;
        int nodeValue = 2;

        while (currentLevel < levels && !queue.isEmpty()) {
            int levelSize = queue.size();

            for (int i = 0; i < levelSize; i++) {
                Node current = queue.poll();

                current.left = new Node(nodeValue++);
                current.right = new Node(nodeValue++);

                queue.offer(current.left);
                queue.offer(current.right);
            }

            currentLevel++;
        }

        return root;
    }
}
