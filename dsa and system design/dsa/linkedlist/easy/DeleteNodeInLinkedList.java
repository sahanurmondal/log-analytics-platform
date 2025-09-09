package linkedlist.easy;

/**
 * LeetCode 237: Delete Node in a Linked List
 * https://leetcode.com/problems/delete-node-in-a-linked-list/
 * 
 * Companies: Amazon, Meta, Google, Microsoft, Apple, Adobe, Bloomberg
 * Frequency: High (Asked in 600+ interviews)
 *
 * Description:
 * Write a function to delete a node in a singly-linked list. You will not be
 * given
 * access to the head of the list, instead you will be directly given access to
 * the
 * node to be deleted.
 *
 * It is guaranteed that the node to be deleted is not a tail node in the list.
 *
 * Constraints:
 * - The number of the nodes in the given list is in the range [2, 1000].
 * - -1000 <= Node.val <= 1000
 * - The value of each node in the list is unique.
 * - The node to be deleted is in the list and is not a tail node.
 * 
 * Follow-up Questions:
 * 1. What if the node to delete is the tail node?
 * 2. Can you delete multiple nodes efficiently?
 * 3. How to handle deletion with constraints (e.g., maintain sorted order)?
 * 4. What about deleting nodes by value instead of reference?
 * 5. Can you implement undo functionality for deletions?
 */
public class DeleteNodeInLinkedList {

    // Definition for singly-linked list
    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            ListNode current = this;
            java.util.Set<ListNode> visited = new java.util.HashSet<>();

            while (current != null && !visited.contains(current)) {
                visited.add(current);
                sb.append(current.val);
                if (current.next != null && !visited.contains(current.next)) {
                    sb.append(" -> ");
                }
                current = current.next;
            }

            if (current != null) {
                sb.append(" -> [CYCLE]");
            }

            return sb.toString();
        }
    }

    // Approach 1: Copy next node's value and delete next - O(1) time, O(1) space
    public void deleteNode(ListNode node) {
        // Copy the value from the next node
        node.val = node.next.val;

        // Delete the next node
        node.next = node.next.next;
    }

    // Approach 2: With validation - O(1) time, O(1) space
    public boolean deleteNodeSafe(ListNode node) {
        // Validate input
        if (node == null || node.next == null) {
            return false; // Cannot delete tail node or null node
        }

        // Copy the value from the next node
        node.val = node.next.val;

        // Delete the next node
        node.next = node.next.next;

        return true;
    }

    // Approach 3: Generic delete with backup - O(1) time, O(1) space
    public DeletedNodeInfo deleteNodeWithBackup(ListNode node) {
        if (node == null || node.next == null) {
            return null; // Cannot delete
        }

        // Store original information for potential undo
        DeletedNodeInfo backup = new DeletedNodeInfo();
        backup.originalValue = node.val;
        backup.deletedValue = node.next.val;
        backup.nextNode = node.next.next;
        backup.deletedNode = node.next;

        // Perform deletion
        node.val = node.next.val;
        node.next = node.next.next;

        return backup;
    }

    // Helper class for backup information
    public static class DeletedNodeInfo {
        int originalValue;
        int deletedValue;
        ListNode nextNode;
        ListNode deletedNode;
    }

    // Follow-up 1: Handle tail node deletion (requires head reference)
    public ListNode deleteNodeIncludingTail(ListNode head, ListNode nodeToDelete) {
        if (head == null || nodeToDelete == null) {
            return head;
        }

        // If deleting head
        if (head == nodeToDelete) {
            return head.next;
        }

        // If deleting tail, need to find previous node
        if (nodeToDelete.next == null) {
            ListNode current = head;
            while (current != null && current.next != nodeToDelete) {
                current = current.next;
            }
            if (current != null) {
                current.next = null;
            }
            return head;
        }

        // Regular deletion (copy next node's value)
        nodeToDelete.val = nodeToDelete.next.val;
        nodeToDelete.next = nodeToDelete.next.next;

        return head;
    }

    // Follow-up 2: Delete multiple nodes efficiently
    public void deleteMultipleNodes(ListNode[] nodesToDelete) {
        if (nodesToDelete == null)
            return;

        for (ListNode node : nodesToDelete) {
            if (node != null && node.next != null) {
                deleteNode(node);
            }
        }
    }

    // Follow-up 2: Delete nodes by indices
    public ListNode deleteNodesByIndices(ListNode head, int[] indices) {
        if (head == null || indices == null || indices.length == 0) {
            return head;
        }

        // Sort indices in descending order to avoid index shifts
        java.util.Arrays.sort(indices);

        // Convert to set for O(1) lookup
        java.util.Set<Integer> indexSet = new java.util.HashSet<>();
        for (int index : indices) {
            indexSet.add(index);
        }

        // Handle head deletion
        if (indexSet.contains(0)) {
            head = head.next;
            indexSet.remove(0);
            // Shift all indices
            java.util.Set<Integer> shiftedSet = new java.util.HashSet<>();
            for (int idx : indexSet) {
                shiftedSet.add(idx - 1);
            }
            indexSet = shiftedSet;
        }

        ListNode current = head;
        int currentIndex = 0;

        while (current != null && current.next != null) {
            if (indexSet.contains(currentIndex + 1)) {
                current.next = current.next.next;
                indexSet.remove(currentIndex + 1);
            } else {
                current = current.next;
            }
            currentIndex++;
        }

        return head;
    }

    // Follow-up 3: Delete while maintaining sorted order
    public void deleteSortedNode(ListNode node) {
        if (node == null || node.next == null) {
            return;
        }

        // Standard deletion maintains sorted order automatically
        deleteNode(node);
    }

    // Follow-up 3: Delete node and maintain balanced structure
    public void deleteNodeBalanced(ListNode node, java.util.Map<Integer, Integer> frequency) {
        if (node == null || node.next == null) {
            return;
        }

        // Update frequency map
        frequency.put(node.val, frequency.getOrDefault(node.val, 0) - 1);
        frequency.put(node.next.val, frequency.getOrDefault(node.next.val, 0) + 1);

        // Perform deletion
        deleteNode(node);
    }

    // Follow-up 4: Delete nodes by value
    public ListNode deleteNodesByValue(ListNode head, int value) {
        // Handle head nodes with target value
        while (head != null && head.val == value) {
            head = head.next;
        }

        if (head == null)
            return null;

        ListNode current = head;
        while (current.next != null) {
            if (current.next.val == value) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }

        return head;
    }

    // Follow-up 4: Delete all occurrences of multiple values
    public ListNode deleteNodesByValues(ListNode head, java.util.Set<Integer> values) {
        // Handle head nodes
        while (head != null && values.contains(head.val)) {
            head = head.next;
        }

        if (head == null)
            return null;

        ListNode current = head;
        while (current.next != null) {
            if (values.contains(current.next.val)) {
                current.next = current.next.next;
            } else {
                current = current.next;
            }
        }

        return head;
    }

    // Follow-up 5: Undo functionality
    public static class UndoableLinkedList {
        private java.util.Stack<DeletedNodeInfo> deletionHistory;

        public UndoableLinkedList() {
            this.deletionHistory = new java.util.Stack<>();
        }

        public boolean deleteNode(ListNode node) {
            if (node == null || node.next == null) {
                return false;
            }

            // Store deletion info
            DeletedNodeInfo info = new DeletedNodeInfo();
            info.originalValue = node.val;
            info.deletedValue = node.next.val;
            info.nextNode = node.next.next;
            info.deletedNode = node.next;

            deletionHistory.push(info);

            // Perform deletion
            node.val = node.next.val;
            node.next = node.next.next;

            return true;
        }

        public boolean undoLastDeletion(ListNode modifiedNode) {
            if (deletionHistory.isEmpty()) {
                return false;
            }

            DeletedNodeInfo info = deletionHistory.pop();

            // Restore original state
            modifiedNode.val = info.originalValue;
            modifiedNode.next = info.deletedNode;
            info.deletedNode.val = info.deletedValue;
            info.deletedNode.next = info.nextNode;

            return true;
        }

        public int getDeletionHistorySize() {
            return deletionHistory.size();
        }

        public void clearHistory() {
            deletionHistory.clear();
        }
    }

    // Advanced: Delete node with circular reference detection
    public boolean deleteNodeCircular(ListNode node) {
        if (node == null || node.next == null) {
            return false;
        }

        // Check for self-reference (node pointing to itself)
        if (node.next == node) {
            return false; // Cannot delete in single-node cycle
        }

        // Standard deletion
        node.val = node.next.val;
        node.next = node.next.next;

        return true;
    }

    // Advanced: Delete with callback notification
    public interface DeletionCallback {
        void onNodeDeleted(int deletedValue, int replacementValue);
    }

    public void deleteNodeWithCallback(ListNode node, DeletionCallback callback) {
        if (node == null || node.next == null) {
            return;
        }

        int deletedValue = node.next.val;
        int replacementValue = node.next.val;

        deleteNode(node);

        if (callback != null) {
            callback.onNodeDeleted(deletedValue, replacementValue);
        }
    }

    // Advanced: Conditional deletion
    public boolean deleteNodeIf(ListNode node, java.util.function.Predicate<Integer> condition) {
        if (node == null || node.next == null) {
            return false;
        }

        if (condition.test(node.val)) {
            deleteNode(node);
            return true;
        }

        return false;
    }

    // Helper methods
    public static ListNode createList(int[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        for (int value : values) {
            current.next = new ListNode(value);
            current = current.next;
        }

        return dummy.next;
    }

    public static ListNode findNode(ListNode head, int value) {
        while (head != null) {
            if (head.val == value) {
                return head;
            }
            head = head.next;
        }
        return null;
    }

    public static ListNode getNodeByIndex(ListNode head, int index) {
        int currentIndex = 0;
        while (head != null && currentIndex < index) {
            head = head.next;
            currentIndex++;
        }
        return head;
    }

    public static int getListLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }

    public static boolean areListsEqual(ListNode l1, ListNode l2) {
        while (l1 != null && l2 != null) {
            if (l1.val != l2.val) {
                return false;
            }
            l1 = l1.next;
            l2 = l2.next;
        }
        return l1 == null && l2 == null;
    }

    public static void main(String[] args) {
        DeleteNodeInLinkedList solution = new DeleteNodeInLinkedList();

        // Test Case 1: Basic deletion
        System.out.println("=== Test Case 1: Basic Deletion ===");

        ListNode head1 = createList(new int[] { 4, 5, 1, 9 });
        System.out.println("Original list: " + head1);

        ListNode nodeToDelete = findNode(head1, 5);
        solution.deleteNode(nodeToDelete);
        System.out.println("After deleting 5: " + head1);

        nodeToDelete = findNode(head1, 1);
        solution.deleteNode(nodeToDelete);
        System.out.println("After deleting 1: " + head1);

        // Test Case 2: Safe deletion with validation
        System.out.println("\n=== Test Case 2: Safe Deletion ===");

        ListNode head2 = createList(new int[] { 1, 2, 3, 4 });
        System.out.println("Original list: " + head2);

        ListNode node2 = findNode(head2, 2);
        boolean success1 = solution.deleteNodeSafe(node2);
        System.out.println("Delete node 2: " + success1 + " -> " + head2);

        ListNode tailNode = getNodeByIndex(head2, getListLength(head2) - 1);
        boolean success2 = solution.deleteNodeSafe(tailNode);
        System.out.println("Delete tail node: " + success2 + " -> " + head2);

        // Test Case 3: Deletion with backup
        System.out.println("\n=== Test Case 3: Deletion with Backup ===");

        ListNode head3 = createList(new int[] { 10, 20, 30, 40 });
        System.out.println("Original list: " + head3);

        ListNode node3 = findNode(head3, 20);
        DeletedNodeInfo backup = solution.deleteNodeWithBackup(node3);
        System.out.println("After deleting 20: " + head3);

        if (backup != null) {
            System.out.println("Backup info - Original: " + backup.originalValue +
                    ", Deleted: " + backup.deletedValue);
        }

        // Follow-up 1: Handle tail node deletion
        System.out.println("\n=== Follow-up 1: Handle Tail Node ===");

        ListNode head4 = createList(new int[] { 1, 2, 3, 4 });
        System.out.println("Original list: " + head4);

        ListNode tailNode4 = getNodeByIndex(head4, 3); // Last node
        head4 = solution.deleteNodeIncludingTail(head4, tailNode4);
        System.out.println("After deleting tail: " + head4);

        ListNode headNode = head4;
        head4 = solution.deleteNodeIncludingTail(head4, headNode);
        System.out.println("After deleting head: " + head4);

        // Follow-up 2: Delete multiple nodes
        System.out.println("\n=== Follow-up 2: Delete Multiple Nodes ===");

        ListNode head5 = createList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.println("Original list: " + head5);

        ListNode[] toDelete = {
                findNode(head5, 2),
                findNode(head5, 4)
        };

        solution.deleteMultipleNodes(toDelete);
        System.out.println("After deleting nodes 2 and 4: " + head5);

        // Delete by indices
        ListNode head6 = createList(new int[] { 10, 20, 30, 40, 50 });
        System.out.println("Original list: " + head6);

        head6 = solution.deleteNodesByIndices(head6, new int[] { 1, 3 });
        System.out.println("After deleting indices 1 and 3: " + head6);

        // Follow-up 4: Delete by values
        System.out.println("\n=== Follow-up 4: Delete by Values ===");

        ListNode head7 = createList(new int[] { 1, 2, 3, 2, 4, 2, 5 });
        System.out.println("Original list: " + head7);

        head7 = solution.deleteNodesByValue(head7, 2);
        System.out.println("After deleting all 2s: " + head7);

        ListNode head8 = createList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.println("Original list: " + head8);

        java.util.Set<Integer> valuesToDelete = new java.util.HashSet<>();
        valuesToDelete.add(2);
        valuesToDelete.add(4);
        valuesToDelete.add(6);

        head8 = solution.deleteNodesByValues(head8, valuesToDelete);
        System.out.println("After deleting 2, 4, 6: " + head8);

        // Follow-up 5: Undo functionality
        System.out.println("\n=== Follow-up 5: Undo Functionality ===");

        UndoableLinkedList undoList = new UndoableLinkedList();
        ListNode head9 = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Original list: " + head9);

        ListNode node9_2 = findNode(head9, 2);
        undoList.deleteNode(node9_2);
        System.out.println("After deleting 2: " + head9);

        ListNode node9_4 = findNode(head9, 4);
        undoList.deleteNode(node9_4);
        System.out.println("After deleting 4: " + head9);

        System.out.println("Deletion history size: " + undoList.getDeletionHistorySize());

        // Undo last deletion
        undoList.undoLastDeletion(node9_4);
        System.out.println("After undo: " + head9);

        undoList.undoLastDeletion(node9_2);
        System.out.println("After second undo: " + head9);

        // Advanced: Conditional deletion
        System.out.println("\n=== Advanced: Conditional Deletion ===");

        ListNode head10 = createList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.println("Original list: " + head10);

        ListNode node10 = findNode(head10, 4);
        boolean deleted = solution.deleteNodeIf(node10, val -> val % 2 == 0);
        System.out.println("Delete if even (4): " + deleted + " -> " + head10);

        ListNode node10_odd = findNode(head10, 3);
        boolean deletedOdd = solution.deleteNodeIf(node10_odd, val -> val % 2 == 0);
        System.out.println("Delete if even (3): " + deletedOdd + " -> " + head10);

        // Advanced: Deletion with callback
        System.out.println("\n=== Advanced: Deletion with Callback ===");

        ListNode head11 = createList(new int[] { 10, 20, 30, 40 });
        System.out.println("Original list: " + head11);

        DeletionCallback callback = (deleted1, replacement) -> System.out
                .println("Callback: Deleted " + deleted1 + ", Replaced with " + replacement);

        ListNode node11 = findNode(head11, 20);
        solution.deleteNodeWithCallback(node11, callback);
        System.out.println("After deletion with callback: " + head11);

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Two-node list
        ListNode twoNode = createList(new int[] { 1, 2 });
        System.out.println("Two-node list: " + twoNode);
        solution.deleteNode(twoNode);
        System.out.println("After deleting first: " + twoNode);

        // Three-node list, delete middle
        ListNode threeNode = createList(new int[] { 1, 2, 3 });
        System.out.println("Three-node list: " + threeNode);
        ListNode middle = findNode(threeNode, 2);
        solution.deleteNode(middle);
        System.out.println("After deleting middle: " + threeNode);

        // List with duplicate values
        ListNode duplicates = createList(new int[] { 1, 2, 2, 3 });
        System.out.println("List with duplicates: " + duplicates);
        ListNode firstTwo = findNode(duplicates, 2);
        solution.deleteNode(firstTwo);
        System.out.println("After deleting first 2: " + duplicates);

        // Helper methods testing
        System.out.println("\n=== Helper Methods ===");

        ListNode testList = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Test list: " + testList);
        System.out.println("Length: " + getListLength(testList));
        System.out.println("Node at index 2: " + getNodeByIndex(testList, 2).val);
        System.out.println("Find node with value 3: " +
                (findNode(testList, 3) != null ? "Found" : "Not found"));

        ListNode testList2 = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Lists equal: " + areListsEqual(testList, testList2));

        System.out.println("\nDelete Node in Linked List testing completed successfully!");
    }
}
