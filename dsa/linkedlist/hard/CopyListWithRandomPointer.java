package linkedlist.hard;

import linkedlist.RandomListNode;

/**
 * LeetCode 138: Copy List with Random Pointer (Hard)
 * https://leetcode.com/problems/copy-list-with-random-pointer/
 *
 * Description:
 * Copy a linked list with next and random pointers.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [0, 1000].
 */
public class CopyListWithRandomPointer {
    public RandomListNode copyRandomList(RandomListNode head) {
        if (head == null) {
            return null;
        }

        java.util.Map<RandomListNode, RandomListNode> map = new java.util.HashMap<>();

        // First pass: create all nodes
        RandomListNode current = head;
        while (current != null) {
            map.put(current, new RandomListNode(current.val));
            current = current.next;
        }

        // Second pass: assign next and random pointers
        current = head;
        while (current != null) {
            RandomListNode copy = map.get(current);
            copy.next = map.get(current.next);
            copy.random = map.get(current.random);
            current = current.next;
        }

        return map.get(head);
    }

    public static void main(String[] args) {
        CopyListWithRandomPointer solution = new CopyListWithRandomPointer();
        RandomListNode head = new RandomListNode(1);
        head.next = new RandomListNode(2);
        head.next.next = new RandomListNode(3);
        head.random = head.next.next;
        head.next.random = head;
        head.next.next.random = head.next;
        RandomListNode copy = solution.copyRandomList(head);
        // Edge Case: Empty list
        System.out.println(solution.copyRandomList(null)); // null
        // Edge Case: Single node
        RandomListNode single = new RandomListNode(1);
        single.random = single;
        System.out.println(solution.copyRandomList(single)); // 1
    }
}

// ...existing RandomListNode class...
