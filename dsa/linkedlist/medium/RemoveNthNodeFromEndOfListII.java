package linkedlist.medium;

/**
 * Variation: Remove Nth Node From End of List II
 *
 * Description:
 * Remove the nth node from the end of a doubly linked list.
 *
 * Constraints:
 * - 1 <= n <= length of list
 */
public class RemoveNthNodeFromEndOfListII {
    public DListNode removeNthFromEnd(DListNode head, int n) {
        if (head == null)
            return null;

        // First pass: count total nodes
        int length = 0;
        DListNode curr = head;
        while (curr != null) {
            length++;
            curr = curr.next;
        }

        // Calculate position from start
        int posFromStart = length - n;

        // If removing head
        if (posFromStart == 0) {
            DListNode newHead = head.next;
            if (newHead != null) {
                newHead.prev = null;
            }
            return newHead;
        }

        // Second pass: find node to remove
        curr = head;
        for (int i = 0; i < posFromStart; i++) {
            curr = curr.next;
        }

        // Remove the node
        if (curr.prev != null) {
            curr.prev.next = curr.next;
        }
        if (curr.next != null) {
            curr.next.prev = curr.prev;
        }

        return head;
    }

    public static void main(String[] args) {
        RemoveNthNodeFromEndOfListII solution = new RemoveNthNodeFromEndOfListII();

        // Test case 1: Remove 2nd from end
        DListNode head1 = new DListNode(1, new DListNode(2, new DListNode(3, new DListNode(4, new DListNode(5)))));
        System.out.println(solution.removeNthFromEnd(head1, 2)); // 1<->2<->3<->5

        // Test case 2: Remove head (5th from end)
        DListNode head2 = new DListNode(1, new DListNode(2, new DListNode(3, new DListNode(4, new DListNode(5)))));
        System.out.println(solution.removeNthFromEnd(head2, 5)); // 2<->3<->4<->5

        // Test case 3: Single node
        DListNode head3 = new DListNode(1);
        System.out.println(solution.removeNthFromEnd(head3, 1)); // null
    }
}

class DListNode {
    int val;
    DListNode prev, next;

    DListNode() {
    }

    DListNode(int val) {
        this.val = val;
    }

    DListNode(int val, DListNode next) {
        this.val = val;
        this.next = next;
        if (next != null) {
            next.prev = this;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        DListNode curr = this;
        while (curr != null) {
            sb.append(curr.val);
            if (curr.next != null) {
                sb.append("<->");
            }
            curr = curr.next;
        }
        return sb.toString();
    }
}
