package linkedlist.hard;

import linkedlist.ListNode;

/**
 * Remove Cycle from Linked List (Floyd's Cycle Detection + Removal)
 * Related to LeetCode 142: Linked List Cycle II
 * https://leetcode.com/problems/linked-list-cycle-ii/
 * 
 * Company Tags: Amazon, Microsoft, Facebook, Google, Apple
 * Difficulty: Hard
 * Frequency: Very High
 * 
 * Description:
 * Given a linked list, detect if it has a cycle and remove the cycle if
 * present.
 * Return the head of the modified list.
 * 
 * Constraints:
 * - The number of nodes in the list is in the range [0, 10^4].
 * - -10^5 <= Node.val <= 10^5
 * - pos is -1 or a valid index in the linked-list.
 * 
 * Follow-ups:
 * 1. Can you solve it without using extra space?
 * 2. Can you detect the start of the cycle?
 * 3. What if the cycle involves the head node?
 * 4. How would you handle multiple cycles in a modified data structure?
 */
public class RemoveCycleFromLinkedList {

    /**
     * Approach 1: Floyd's Cycle Detection + Removal
     * Time: O(n), Space: O(1)
     */
    public ListNode removeCycle(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        // Step 1: Detect cycle using Floyd's algorithm
        ListNode slow = head;
        ListNode fast = head;

        // Find if cycle exists
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                break; // Cycle detected
            }
        }

        // No cycle found
        if (fast == null || fast.next == null) {
            return head;
        }

        // Step 2: Find the start of the cycle
        ListNode start = head;
        while (start != slow) {
            start = start.next;
            slow = slow.next;
        }

        // Step 3: Remove the cycle
        // Find the node just before the cycle start
        while (slow.next != start) {
            slow = slow.next;
        }
        slow.next = null; // Break the cycle

        return head;
    }

    /**
     * Approach 2: Using HashSet for cycle detection and removal
     * Time: O(n), Space: O(n)
     */
    public ListNode removeCycleWithSet(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        java.util.Set<ListNode> visited = new java.util.HashSet<>();
        ListNode curr = head;
        ListNode prev = null;

        while (curr != null) {
            if (visited.contains(curr)) {
                // Cycle detected, remove it
                prev.next = null;
                break;
            }

            visited.add(curr);
            prev = curr;
            curr = curr.next;
        }

        return head;
    }

    /**
     * Follow-up 1: Alternative cycle removal method
     * Time: O(n), Space: O(1)
     */
    public ListNode removeCycleAlternative(ListNode head) {
        if (head == null || head.next == null) {
            return head;
        }

        ListNode slow = head;
        ListNode fast = head;

        // Detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                break;
            }
        }

        if (fast == null || fast.next == null) {
            return head; // No cycle
        }

        // Special case: cycle starts at head
        if (slow == head) {
            while (fast.next != head) {
                fast = fast.next;
            }
            fast.next = null;
            return head;
        }

        // Find cycle start
        ListNode ptr1 = head;
        ListNode ptr2 = slow;

        while (ptr1.next != ptr2.next) {
            ptr1 = ptr1.next;
            ptr2 = ptr2.next;
        }

        ptr2.next = null; // Remove cycle
        return head;
    }

    /**
     * Follow-up 2: Return both modified list and cycle start node
     */
    static class Result {
        ListNode head;
        ListNode cycleStart;

        Result(ListNode head, ListNode cycleStart) {
            this.head = head;
            this.cycleStart = cycleStart;
        }
    }

    public Result removeCycleWithInfo(ListNode head) {
        if (head == null || head.next == null) {
            return new Result(head, null);
        }

        ListNode slow = head;
        ListNode fast = head;

        // Detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                break;
            }
        }

        if (fast == null || fast.next == null) {
            return new Result(head, null); // No cycle
        }

        // Find cycle start
        ListNode cycleStart = head;
        while (cycleStart != slow) {
            cycleStart = cycleStart.next;
            slow = slow.next;
        }

        // Remove cycle
        while (slow.next != cycleStart) {
            slow = slow.next;
        }
        slow.next = null;

        return new Result(head, cycleStart);
    }

    /**
     * Follow-up 3: Count nodes in cycle before removal
     */
    public int countCycleNodes(ListNode head) {
        if (head == null || head.next == null) {
            return 0;
        }

        ListNode slow = head;
        ListNode fast = head;

        // Detect cycle
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                break;
            }
        }

        if (fast == null || fast.next == null) {
            return 0; // No cycle
        }

        // Count nodes in cycle
        int count = 1;
        ListNode temp = slow;
        while (temp.next != slow) {
            temp = temp.next;
            count++;
        }

        return count;
    }

    // Helper method to create a cycle for testing
    private ListNode createCycleAt(ListNode head, int pos) {
        if (pos == -1)
            return head;

        ListNode cycleStart = head;
        for (int i = 0; i < pos; i++) {
            cycleStart = cycleStart.next;
        }

        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
        }

        tail.next = cycleStart;
        return head;
    }

    // Helper method to check if list has cycle
    private boolean hasCycle(ListNode head) {
        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;

            if (slow == fast) {
                return true;
            }
        }

        return false;
    }

    public static void main(String[] args) {
        RemoveCycleFromLinkedList solution = new RemoveCycleFromLinkedList();

        System.out.println("=== Remove Cycle from Linked List ===");

        // Test Case 1: List with cycle at position 1
        // 3 -> 2 -> 0 -> -4 -> (back to 2)
        ListNode head1 = new ListNode(3);
        head1.next = new ListNode(2);
        head1.next.next = new ListNode(0);
        head1.next.next.next = new ListNode(-4);
        head1 = solution.createCycleAt(head1, 1);

        System.out.println("Test 1 - Has cycle before removal: " + solution.hasCycle(head1));
        ListNode result1 = solution.removeCycle(head1);
        System.out.println("Test 1 - Has cycle after removal: " + solution.hasCycle(result1));

        // Test Case 2: List with cycle at head
        // 1 -> 2 -> (back to 1)
        ListNode head2 = new ListNode(1);
        head2.next = new ListNode(2);
        head2 = solution.createCycleAt(head2, 0);

        System.out.println("Test 2 - Has cycle before removal: " + solution.hasCycle(head2));
        ListNode result2 = solution.removeCycle(head2);
        System.out.println("Test 2 - Has cycle after removal: " + solution.hasCycle(result2));

        // Test Case 3: Single node with self cycle
        ListNode head3 = new ListNode(1);
        head3.next = head3;

        System.out.println("Test 3 - Has cycle before removal: " + solution.hasCycle(head3));
        ListNode result3 = solution.removeCycle(head3);
        System.out.println("Test 3 - Has cycle after removal: " + solution.hasCycle(result3));

        // Test Case 4: No cycle
        ListNode head4 = new ListNode(1);
        head4.next = new ListNode(2);
        head4.next.next = new ListNode(3);

        System.out.println("Test 4 - Has cycle before removal: " + solution.hasCycle(head4));
        ListNode result4 = solution.removeCycle(head4);
        System.out.println("Test 4 - Has cycle after removal: " + solution.hasCycle(result4));

        // Test Case 5: Empty list
        ListNode result5 = solution.removeCycle(null);
        System.out.println("Test 5 - Empty list result: " + (result5 == null ? "null" : "not null"));

        // Test Follow-up: Count cycle nodes
        ListNode cycleTest = new ListNode(1);
        cycleTest.next = new ListNode(2);
        cycleTest.next.next = new ListNode(3);
        cycleTest.next.next.next = new ListNode(4);
        cycleTest = solution.createCycleAt(cycleTest, 1);

        int cycleCount = solution.countCycleNodes(cycleTest);
        System.out.println("Cycle node count: " + cycleCount);
    }
}
