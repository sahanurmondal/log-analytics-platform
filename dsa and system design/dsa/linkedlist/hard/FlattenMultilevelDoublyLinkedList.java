package linkedlist.hard;

/**
 * LeetCode 430: Flatten a Multilevel Doubly Linked List
 * https://leetcode.com/problems/flatten-a-multilevel-doubly-linked-list/
 * 
 * Company Tags: Amazon, Microsoft, Google, Facebook
 * Difficulty: Medium/Hard
 * Frequency: High
 * 
 * Description:
 * You are given a doubly linked list which in addition to the next and previous
 * pointers,
 * it could have a child pointer, which may or may not point to a separate
 * doubly linked list.
 * These child lists may have one or more children of their own, and so on.
 * 
 * Flatten the list so that all the nodes appear in a single-level, doubly
 * linked list.
 * You are given the head of the first level of the list.
 * 
 * Constraints:
 * - The number of Nodes will not exceed 1000.
 * - 1 <= Node.val <= 10^5
 * 
 * Follow-ups:
 * 1. Can you solve it iteratively?
 * 2. Can you solve it recursively?
 * 3. What if we need to unflatten the list back to its original structure?
 * 4. How would you handle memory constraints for very deep nested structures?
 */
public class FlattenMultilevelDoublyLinkedList {

    static class Node {
        public int val;
        public Node prev;
        public Node next;
        public Node child;

        public Node() {
        }

        public Node(int val) {
            this.val = val;
        }

        public Node(int val, Node prev, Node next, Node child) {
            this.val = val;
            this.prev = prev;
            this.next = next;
            this.child = child;
        }
    }

    /**
     * Approach 1: Iterative using Stack
     * Time: O(n), Space: O(d) where d is max depth
     */
    public Node flatten(Node head) {
        if (head == null)
            return head;

        java.util.Stack<Node> stack = new java.util.Stack<>();
        Node curr = head;

        while (curr != null) {
            if (curr.child != null) {
                // If there's a next node, save it for later
                if (curr.next != null) {
                    stack.push(curr.next);
                }

                // Connect child to current
                curr.next = curr.child;
                curr.child.prev = curr;
                curr.child = null;
            }

            // If we reach the end and have saved nodes
            if (curr.next == null && !stack.isEmpty()) {
                Node nextNode = stack.pop();
                curr.next = nextNode;
                nextNode.prev = curr;
            }

            curr = curr.next;
        }

        return head;
    }

    /**
     * Approach 2: Recursive DFS
     * Time: O(n), Space: O(d) where d is max depth
     */
    public Node flattenRecursive(Node head) {
        if (head == null)
            return head;
        flattenDFS(head);
        return head;
    }

    private Node flattenDFS(Node head) {
        Node curr = head;
        Node last = null;

        while (curr != null) {
            Node child = curr.child;
            Node next = curr.next;

            if (child != null) {
                curr.next = child;
                child.prev = curr;

                Node childTail = flattenDFS(child);

                if (next != null) {
                    childTail.next = next;
                    next.prev = childTail;
                }

                curr.child = null;
                last = childTail;
            } else {
                last = curr;
            }

            curr = next;
        }

        return last;
    }

    /**
     * Follow-up 1: Iterative without stack using prev pointer manipulation
     * Time: O(n), Space: O(1)
     */
    public Node flattenOptimal(Node head) {
        if (head == null)
            return head;

        Node curr = head;
        while (curr != null) {
            if (curr.child == null) {
                curr = curr.next;
                continue;
            }

            Node child = curr.child;

            // Find the tail of child list
            while (child.next != null) {
                child = child.next;
            }

            // Connect child tail to curr.next
            child.next = curr.next;
            if (curr.next != null) {
                curr.next.prev = child;
            }

            // Connect curr to child head
            curr.next = curr.child;
            curr.child.prev = curr;
            curr.child = null;

            curr = curr.next;
        }

        return head;
    }

    /**
     * Follow-up 3: Unflatten back to original structure
     * This requires storing the original structure information
     */
    static class NodeWithOriginal {
        int val;
        NodeWithOriginal prev, next, child;
        boolean hadChild;

        NodeWithOriginal(int val) {
            this.val = val;
        }
    }

    // Helper method to print the flattened list
    private void printList(Node head) {
        Node curr = head;
        while (curr != null) {
            System.out.print(curr.val);
            if (curr.next != null)
                System.out.print(" <-> ");
            curr = curr.next;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        FlattenMultilevelDoublyLinkedList solution = new FlattenMultilevelDoublyLinkedList();

        // Test Case 1: Basic multilevel list
        // 1---2---3---7---8---11---12--NULL
        // | |
        // 4---5---6--NULL 9---10--NULL
        Node head1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node7 = new Node(7);
        Node node8 = new Node(8);
        Node node11 = new Node(11);
        Node node12 = new Node(12);

        Node node4 = new Node(4);
        Node node5 = new Node(5);
        Node node6 = new Node(6);

        Node node9 = new Node(9);
        Node node10 = new Node(10);

        // Main level connections
        head1.next = node2;
        node2.prev = head1;
        node2.next = node3;
        node3.prev = node2;
        node3.next = node7;
        node7.prev = node3;
        node7.next = node8;
        node8.prev = node7;
        node8.next = node11;
        node11.prev = node8;
        node11.next = node12;
        node12.prev = node11;

        // Child connections
        node3.child = node4;
        node4.next = node5;
        node5.prev = node4;
        node5.next = node6;
        node6.prev = node5;

        node8.child = node9;
        node9.next = node10;
        node10.prev = node9;

        System.out.println("=== Flatten Multilevel Doubly Linked List ===");
        System.out.println("Original multilevel structure created");

        Node result1 = solution.flatten(head1);
        System.out.print("Flattened (Iterative): ");
        solution.printList(result1);

        // Test Case 2: Single node
        Node single = new Node(42);
        Node result2 = solution.flatten(single);
        System.out.print("Single node: ");
        solution.printList(result2);

        // Test Case 3: No child nodes
        Node simple1 = new Node(1);
        Node simple2 = new Node(2);
        Node simple3 = new Node(3);
        simple1.next = simple2;
        simple2.prev = simple1;
        simple2.next = simple3;
        simple3.prev = simple2;

        Node result3 = solution.flatten(simple1);
        System.out.print("No children: ");
        solution.printList(result3);

        // Test Case 4: Deep nesting
        Node deep1 = new Node(1);
        Node deep2 = new Node(2);
        Node deep3 = new Node(3);
        Node deep4 = new Node(4);

        deep1.next = deep2;
        deep2.prev = deep1;
        deep1.child = deep3;
        deep3.child = deep4;

        Node result4 = solution.flattenRecursive(deep1);
        System.out.print("Deep nesting (Recursive): ");
        solution.printList(result4);

        // Test Case 5: Empty list
        Node result5 = solution.flatten(null);
        System.out.println("Empty list: " + (result5 == null ? "null" : "not null"));
    }
}
