package linkedlist.medium;

import java.util.HashMap;
import linkedlist.RandomListNode;

/**
 * LeetCode 138: Copy List with Random Pointer
 * https://leetcode.com/problems/copy-list-with-random-pointer/
 *
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High
 *
 * Description:
 * Copy a linked list with random pointer.
 *
 * Constraints:
 * - 0 <= n <= 1000
 * - -10000 <= Node.val <= 10000
 *
 * Follow-ups:
 * 1. Can you do it in O(1) space?
 * 2. Can you handle cycles through random pointers?
 * 3. Can you create a reversed copy?
 */
public class CopyListWithRandomPointer {
    // Approach 1: HashMap - O(n) time, O(n) space
    public RandomListNode copyRandomList(RandomListNode head) {
        if (head == null)
            return null;
        HashMap<RandomListNode, RandomListNode> map = new HashMap<>();
        RandomListNode curr = head;
        while (curr != null) {
            map.put(curr, new RandomListNode(curr.val));
            curr = curr.next;
        }
        curr = head;
        while (curr != null) {
            map.get(curr).next = map.get(curr.next);
            map.get(curr).random = map.get(curr.random);
            curr = curr.next;
        }
        return map.get(head);
    }

    // Approach 2: O(1) space (interleaving nodes)
    public RandomListNode copyRandomListO1Space(RandomListNode head) {
        if (head == null)
            return null;
        RandomListNode curr = head;
        while (curr != null) {
            RandomListNode copy = new RandomListNode(curr.val);
            copy.next = curr.next;
            curr.next = copy;
            curr = copy.next;
        }
        curr = head;
        while (curr != null) {
            if (curr.random != null)
                curr.next.random = curr.random.next;
            curr = curr.next.next;
        }
        curr = head;
        RandomListNode copyHead = head.next;
        while (curr != null) {
            RandomListNode copy = curr.next;
            curr.next = copy.next;
            if (copy.next != null)
                copy.next = copy.next.next;
            curr = curr.next;
        }
        return copyHead;
    }

    // Follow-up 1: Cycle detection
    public boolean hasCycle(RandomListNode head) {
        java.util.Set<RandomListNode> visited = new java.util.HashSet<>();
        while (head != null) {
            if (visited.contains(head))
                return true;
            visited.add(head);
            head = head.next;
        }
        return false;
    }

    // Follow-up 2: Create reversed copy
    public RandomListNode createReversedCopy(RandomListNode head) {
        if (head == null)
            return null;
        java.util.Map<RandomListNode, RandomListNode> map = new HashMap<>();
        RandomListNode curr = head;
        while (curr != null) {
            map.put(curr, new RandomListNode(curr.val));
            curr = curr.next;
        }
        curr = head;
        RandomListNode newTail = null;
        while (curr != null) {
            RandomListNode copy = map.get(curr);
            if (curr.next != null)
                map.get(curr.next).next = copy;
            else
                newTail = copy;
            curr = curr.next;
        }
        curr = head;
        while (curr != null) {
            RandomListNode copy = map.get(curr);
            if (curr.random != null)
                copy.random = map.get(curr.random);
            curr = curr.next;
        }
        return newTail;
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
