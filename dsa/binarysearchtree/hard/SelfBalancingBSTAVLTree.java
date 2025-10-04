package binarysearchtree.hard;

/**
 * Self-Balancing Binary Search Tree (AVL Tree Implementation)
 * 
 * Problem: Implement a Binary Search Tree that automatically rebalances itself
 * after every insertion and deletion to maintain O(log n) height.
 * 
 * AVL Tree Properties:
 * 1. It is a BST (left < root < right)
 * 2. For every node, the height difference between left and right subtrees is at most 1
 * 3. Balance Factor = height(left) - height(right) ∈ {-1, 0, 1}
 * 
 * Operations:
 * - Insert: O(log n) with automatic rebalancing
 * - Delete: O(log n) with automatic rebalancing
 * - Search: O(log n)
 * - Traversal: O(n)
 * 
 * Rotations Used:
 * 1. Left Rotation (LL)
 * 2. Right Rotation (RR)
 * 3. Left-Right Rotation (LR)
 * 4. Right-Left Rotation (RL)
 * 
 * Difficulty: Hard
 * Topics: Binary Search Tree, AVL Tree, Self-Balancing Trees, Tree Rotations
 * Companies: Google, Amazon, Microsoft, Facebook, Apple
 * 
 * Time Complexity:
 * - Insert: O(log n)
 * - Delete: O(log n)
 * - Search: O(log n)
 * - Min/Max: O(log n)
 * 
 * Space Complexity: O(n) for storing n nodes, O(log n) for recursion stack
 */
public class SelfBalancingBSTAVLTree {
    
    /**
     * AVL Tree Node with height information
     */
    static class AVLNode {
        int val;
        int height;
        AVLNode left;
        AVLNode right;
        
        AVLNode(int val) {
            this.val = val;
            this.height = 1; // New node is initially at height 1
        }
    }
    
    private AVLNode root;
    
    // ===================== HELPER FUNCTIONS =====================
    
    /**
     * Get height of a node
     * Time: O(1)
     */
    private int height(AVLNode node) {
        return node == null ? 0 : node.height;
    }
    
    /**
     * Update height of a node based on children's heights
     * Time: O(1)
     */
    private void updateHeight(AVLNode node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }
    
    /**
     * Get balance factor of a node
     * Balance Factor = height(left) - height(right)
     * Time: O(1)
     */
    private int getBalance(AVLNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }
    
    // ===================== ROTATION OPERATIONS =====================
    
    /**
     * Right Rotation (RR Rotation)
     * 
     *       y                               x
     *      / \     Right Rotation          / \
     *     x   T3   - - - - - - - >        T1  y
     *    / \                                 / \
     *   T1  T2                              T2  T3
     * 
     * Time: O(1)
     */
    private AVLNode rotateRight(AVLNode y) {
        AVLNode x = y.left;
        AVLNode T2 = x.right;
        
        // Perform rotation
        x.right = y;
        y.left = T2;
        
        // Update heights
        updateHeight(y);
        updateHeight(x);
        
        return x; // New root
    }
    
    /**
     * Left Rotation (LL Rotation)
     * 
     *     x                               y
     *    / \      Left Rotation          / \
     *   T1  y     - - - - - - - >       x   T3
     *      / \                          / \
     *     T2  T3                       T1  T2
     * 
     * Time: O(1)
     */
    private AVLNode rotateLeft(AVLNode x) {
        AVLNode y = x.right;
        AVLNode T2 = y.left;
        
        // Perform rotation
        y.left = x;
        x.right = T2;
        
        // Update heights
        updateHeight(x);
        updateHeight(y);
        
        return y; // New root
    }
    
    // ===================== INSERT OPERATION =====================
    
    /**
     * Insert a value into AVL Tree with automatic rebalancing
     * Time: O(log n)
     * Space: O(log n) for recursion
     */
    public void insert(int val) {
        root = insertNode(root, val);
    }
    
    private AVLNode insertNode(AVLNode node, int val) {
        // 1. Perform standard BST insertion
        if (node == null) {
            return new AVLNode(val);
        }
        
        if (val < node.val) {
            node.left = insertNode(node.left, val);
        } else if (val > node.val) {
            node.right = insertNode(node.right, val);
        } else {
            // Duplicate values not allowed
            return node;
        }
        
        // 2. Update height of current node
        updateHeight(node);
        
        // 3. Get balance factor to check if node became unbalanced
        int balance = getBalance(node);
        
        // 4. If unbalanced, there are 4 cases:
        
        // Case 1: Left-Left (LL) - Right Rotation
        if (balance > 1 && val < node.left.val) {
            return rotateRight(node);
        }
        
        // Case 2: Right-Right (RR) - Left Rotation
        if (balance < -1 && val > node.right.val) {
            return rotateLeft(node);
        }
        
        // Case 3: Left-Right (LR) - Left Rotation then Right Rotation
        if (balance > 1 && val > node.left.val) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        
        // Case 4: Right-Left (RL) - Right Rotation then Left Rotation
        if (balance < -1 && val < node.right.val) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        // Node is balanced
        return node;
    }
    
    // ===================== DELETE OPERATION =====================
    
    /**
     * Delete a value from AVL Tree with automatic rebalancing
     * Time: O(log n)
     * Space: O(log n) for recursion
     */
    public void delete(int val) {
        root = deleteNode(root, val);
    }
    
    private AVLNode deleteNode(AVLNode node, int val) {
        // 1. Perform standard BST deletion
        if (node == null) {
            return null;
        }
        
        if (val < node.val) {
            node.left = deleteNode(node.left, val);
        } else if (val > node.val) {
            node.right = deleteNode(node.right, val);
        } else {
            // Node to be deleted found
            
            // Case 1: Node with only one child or no child
            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                // Case 2: Node with two children
                // Get inorder successor (smallest in right subtree)
                AVLNode successor = findMin(node.right);
                node.val = successor.val;
                node.right = deleteNode(node.right, successor.val);
            }
        }
        
        // If tree had only one node
        if (node == null) {
            return null;
        }
        
        // 2. Update height of current node
        updateHeight(node);
        
        // 3. Get balance factor
        int balance = getBalance(node);
        
        // 4. If unbalanced, there are 4 cases:
        
        // Case 1: Left-Left (LL)
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }
        
        // Case 2: Left-Right (LR)
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        
        // Case 3: Right-Right (RR)
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }
        
        // Case 4: Right-Left (RL)
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        return node;
    }
    
    // ===================== SEARCH OPERATIONS =====================
    
    /**
     * Search for a value in AVL Tree
     * Time: O(log n)
     */
    public boolean search(int val) {
        return searchNode(root, val);
    }
    
    private boolean searchNode(AVLNode node, int val) {
        if (node == null) {
            return false;
        }
        
        if (val == node.val) {
            return true;
        } else if (val < node.val) {
            return searchNode(node.left, val);
        } else {
            return searchNode(node.right, val);
        }
    }
    
    /**
     * Find minimum value in the tree
     * Time: O(log n)
     */
    public Integer findMin() {
        if (root == null) return null;
        return findMin(root).val;
    }
    
    private AVLNode findMin(AVLNode node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    /**
     * Find maximum value in the tree
     * Time: O(log n)
     */
    public Integer findMax() {
        if (root == null) return null;
        AVLNode node = root;
        while (node.right != null) {
            node = node.right;
        }
        return node.val;
    }
    
    // ===================== TRAVERSAL OPERATIONS =====================
    
    /**
     * Inorder traversal (returns sorted order)
     * Time: O(n)
     */
    public void inorder() {
        System.out.print("Inorder: ");
        inorderTraversal(root);
        System.out.println();
    }
    
    private void inorderTraversal(AVLNode node) {
        if (node != null) {
            inorderTraversal(node.left);
            System.out.print(node.val + " ");
            inorderTraversal(node.right);
        }
    }
    
    /**
     * Preorder traversal
     * Time: O(n)
     */
    public void preorder() {
        System.out.print("Preorder: ");
        preorderTraversal(root);
        System.out.println();
    }
    
    private void preorderTraversal(AVLNode node) {
        if (node != null) {
            System.out.print(node.val + " ");
            preorderTraversal(node.left);
            preorderTraversal(node.right);
        }
    }
    
    // ===================== UTILITY FUNCTIONS =====================
    
    /**
     * Check if the tree is balanced (AVL property)
     * Time: O(n)
     */
    public boolean isBalanced() {
        return checkBalance(root) != -1;
    }
    
    private int checkBalance(AVLNode node) {
        if (node == null) return 0;
        
        int leftHeight = checkBalance(node.left);
        if (leftHeight == -1) return -1;
        
        int rightHeight = checkBalance(node.right);
        if (rightHeight == -1) return -1;
        
        if (Math.abs(leftHeight - rightHeight) > 1) {
            return -1;
        }
        
        return Math.max(leftHeight, rightHeight) + 1;
    }
    
    /**
     * Get height of the tree
     * Time: O(1)
     */
    public int getHeight() {
        return height(root);
    }
    
    /**
     * Print tree structure with heights and balance factors
     */
    public void printTree() {
        System.out.println("\n=== AVL Tree Structure ===");
        printTree(root, "", true);
        System.out.println("=========================\n");
    }
    
    private void printTree(AVLNode node, String prefix, boolean isTail) {
        if (node == null) return;
        
        System.out.println(prefix + (isTail ? "└── " : "├── ") + 
                          node.val + " (h:" + node.height + ", bf:" + getBalance(node) + ")");
        
        if (node.left != null || node.right != null) {
            if (node.right != null) {
                printTree(node.right, prefix + (isTail ? "    " : "│   "), false);
            }
            if (node.left != null) {
                printTree(node.left, prefix + (isTail ? "    " : "│   "), true);
            }
        }
    }
    
    // ===================== MAIN - DEMONSTRATION =====================
    
    public static void main(String[] args) {
        SelfBalancingBSTAVLTree avl = new SelfBalancingBSTAVLTree();
        
        System.out.println("=== AVL Tree Implementation Demo ===\n");
        
        // Test Case 1: Sequential Insertions (would create skewed tree in regular BST)
        System.out.println("Test 1: Sequential Insertions (10, 20, 30, 40, 50, 25)");
        int[] values = {10, 20, 30, 40, 50, 25};
        
        for (int val : values) {
            System.out.println("\nInserting: " + val);
            avl.insert(val);
            avl.printTree();
        }
        
        System.out.println("Tree Height: " + avl.getHeight());
        System.out.println("Is Balanced: " + avl.isBalanced());
        avl.inorder();
        avl.preorder();
        
        // Test Case 2: Search Operations
        System.out.println("\n\nTest 2: Search Operations");
        System.out.println("Search 25: " + avl.search(25));
        System.out.println("Search 100: " + avl.search(100));
        System.out.println("Min value: " + avl.findMin());
        System.out.println("Max value: " + avl.findMax());
        
        // Test Case 3: Deletion with Rebalancing
        System.out.println("\n\nTest 3: Deletion with Rebalancing");
        
        System.out.println("\nDeleting: 10");
        avl.delete(10);
        avl.printTree();
        avl.inorder();
        
        System.out.println("\nDeleting: 30");
        avl.delete(30);
        avl.printTree();
        avl.inorder();
        
        System.out.println("\nTree Height after deletions: " + avl.getHeight());
        System.out.println("Is Balanced: " + avl.isBalanced());
        
        // Test Case 4: Complex Scenario
        System.out.println("\n\nTest 4: Complex Scenario - Building Larger Tree");
        SelfBalancingBSTAVLTree avl2 = new SelfBalancingBSTAVLTree();
        
        int[] largeSet = {50, 25, 75, 10, 30, 60, 80, 5, 15, 27, 55, 1};
        System.out.println("Inserting: " + java.util.Arrays.toString(largeSet));
        
        for (int val : largeSet) {
            avl2.insert(val);
        }
        
        avl2.printTree();
        System.out.println("Tree Height: " + avl2.getHeight());
        System.out.println("Is Balanced: " + avl2.isBalanced());
        avl2.inorder();
        
        // Test Case 5: All Rotation Types
        System.out.println("\n\nTest 5: Demonstrating All Rotation Types");
        
        // LL Rotation
        System.out.println("\n--- Left-Left (LL) Rotation ---");
        SelfBalancingBSTAVLTree llTree = new SelfBalancingBSTAVLTree();
        llTree.insert(30);
        llTree.insert(20);
        llTree.insert(10); // Triggers LL rotation
        llTree.printTree();
        
        // RR Rotation
        System.out.println("\n--- Right-Right (RR) Rotation ---");
        SelfBalancingBSTAVLTree rrTree = new SelfBalancingBSTAVLTree();
        rrTree.insert(10);
        rrTree.insert(20);
        rrTree.insert(30); // Triggers RR rotation
        rrTree.printTree();
        
        // LR Rotation
        System.out.println("\n--- Left-Right (LR) Rotation ---");
        SelfBalancingBSTAVLTree lrTree = new SelfBalancingBSTAVLTree();
        lrTree.insert(30);
        lrTree.insert(10);
        lrTree.insert(20); // Triggers LR rotation
        lrTree.printTree();
        
        // RL Rotation
        System.out.println("\n--- Right-Left (RL) Rotation ---");
        SelfBalancingBSTAVLTree rlTree = new SelfBalancingBSTAVLTree();
        rlTree.insert(10);
        rlTree.insert(30);
        rlTree.insert(20); // Triggers RL rotation
        rlTree.printTree();
        
        System.out.println("\n=== All Tests Completed Successfully ===");
    }
}
