package miscellaneous.recent;

/**
 * Recent Problem: Reverse Linked List II
 * 
 * Description:
 * Given the head of a linked list and two integers left and right,
 * reverse the nodes from position left to position right.
 * 
 * Companies: Facebook, Amazon, Microsoft
 * Difficulty: Medium
 * Asked: 2023-2024
 */
public class ReverseLinkedListII {

    class ListNode {
        int val;
        ListNode next;

        ListNode() {
        }

        ListNode(int val) {
            this.val = val;
        }

        ListNode(int val, ListNode next) {
            this.val = val;
            this.next = next;
        }
    }

    public ListNode reverseBetween(ListNode head, int left, int right) {
        if (head == null || left == right) {
            return head;
        }

        ListNode dummy = new ListNode(0);
        dummy.next = head;
        ListNode prev = dummy;

        // Move to the node before left position
        for (int i = 0; i < left - 1; i++) {
            prev = prev.next;
        }

        ListNode current = prev.next;

        // Reverse nodes from left to right
        for (int i = 0; i < right - left; i++) {
            ListNode nextNode = current.next;
            current.next = nextNode.next;
            nextNode.next = prev.next;
            prev.next = nextNode;
        }

        return dummy.next;
    }

    public static void main(String[] args) {
        ReverseLinkedListII solution = new ReverseLinkedListII();

        // Create linked list: 1->2->3->4->5
        ListNode head = solution.new ListNode(1);
        head.next = solution.new ListNode(2);
        head.next.next = solution.new ListNode(3);
        head.next.next.next = solution.new ListNode(4);
        head.next.next.next.next = solution.new ListNode(5);

        ListNode result = solution.reverseBetween(head, 2, 4);

        // Print result: should be 1->4->3->2->5
        while (result != null) {
            System.out.print(result.val + " ");
            result = result.next;
        }
    }
}
