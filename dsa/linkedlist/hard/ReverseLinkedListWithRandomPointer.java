package linkedlist.hard;

import java.util.*;

/**
 * LeetCode Custom: Reverse Linked List with Random Pointer
 * Similar to LeetCode 138: Copy List with Random Pointer
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 120+ interviews)
 *
 * Description: Reverse a linked list where each node has a random pointer.
 *
 * Constraints:
 * - 0 <= n <= 1000
 * - -10000 <= Node.val <= 10000
 * - Node.random is null or pointing to another node in the linked list
 * 
 * Follow-up Questions:
 * 1. Can you do it in O(1) extra space?
 * 2. How to maintain random pointer relationships?
 * 3. What if there are cycles through random pointers?
 */
public class ReverseLinkedListWithRandomPointer {

    static class RandomListNode {
        int val;
        RandomListNode next;
        RandomListNode random;

        public RandomListNode(int val) {
            this.val = val;
            this.next = null;
            this.random = null;
        }
    }

    // Approach 1: HashMap to track positions - O(n) time, O(n) space
    public RandomListNode reverseWithRandom(RandomListNode head) {
        if (head == null)
            return null;

        // Step 1: Create position mappings
        Map<RandomListNode, Integer> nodeToPos = new HashMap<>();
        Map<Integer, RandomListNode> posToNode = new HashMap<>();

        // Map original nodes to positions
        RandomListNode curr = head;
        int pos = 0;
        while (curr != null) {
            nodeToPos.put(curr, pos);
            curr = curr.next;
            pos++;
        }

        // Step 2: Reverse the list structure
        RandomListNode prev = null;
        curr = head;
        int currentPos = 0;

        while (curr != null) {
            RandomListNode next = curr.next;
            curr.next = prev;

            // Store reversed node at new position
            posToNode.put(pos - 1 - currentPos, curr);

            prev = curr;
            curr = next;
            currentPos++;
        }

        // Step 3: Fix random pointers
        curr = prev; // New head
        currentPos = 0;

        while (curr != null) {
            if (curr.random != null) {
                int originalRandomPos = nodeToPos.get(curr.random);
                int newRandomPos = pos - 1 - originalRandomPos;
                curr.random = posToNode.get(newRandomPos);
            }
            curr = curr.next;
            currentPos++;
        }

        return prev;
    }

    // Approach 2: Three-pass with node copying - O(n) time, O(1) space
    public RandomListNode reverseWithRandomConstantSpace(RandomListNode head) {
        if (head == null)
            return null;

        // Step 1: Create copied nodes interleaved with original
        RandomListNode curr = head;
        while (curr != null) {
            RandomListNode copy = new RandomListNode(curr.val);
            copy.next = curr.next;
            curr.next = copy;
            curr = copy.next;
        }

        // Step 2: Set random pointers for copied nodes
        curr = head;
        while (curr != null) {
            if (curr.random != null) {
                curr.next.random = curr.random.next;
            }
            curr = curr.next.next;
        }

        // Step 3: Separate and reverse the copied list
        RandomListNode originalHead = head;
        RandomListNode copyHead = head.next;
        RandomListNode copyCurr = copyHead;

        // Separate the lists
        curr = originalHead;
        while (curr != null) {
            curr.next = curr.next.next;
            if (copyCurr.next != null) {
                copyCurr.next = copyCurr.next.next;
            }
            curr = curr.next;
            copyCurr = copyCurr.next;
        }

        // Reverse the copied list
        return reverseList(copyHead);
    }

    // Approach 3: Recursive with memoization - O(n) time, O(n) space
    public RandomListNode reverseWithRandomRecursive(RandomListNode head) {
        Map<RandomListNode, RandomListNode> memo = new HashMap<>();
        return reverseRecursiveHelper(head, memo);
    }

    private RandomListNode reverseRecursiveHelper(RandomListNode head,
            Map<RandomListNode, RandomListNode> memo) {
        if (head == null)
            return null;

        if (memo.containsKey(head)) {
            return memo.get(head);
        }

        // Create new node
        RandomListNode newNode = new RandomListNode(head.val);
        memo.put(head, newNode);

        // Recursively reverse rest
        newNode.next = reverseRecursiveHelper(head.next, memo);

        // Handle random pointer
        if (head.random != null) {
            newNode.random = reverseRecursiveHelper(head.random, memo);
        }

        return newNode;
    }

    // Follow-up 1: Reverse with cycle detection through random pointers
    public RandomListNode reverseWithCycleDetection(RandomListNode head) {
        if (head == null)
            return null;

        // Detect cycles through random pointers
        Set<RandomListNode> visited = new HashSet<>();
        if (hasCycleThroughRandom(head, visited)) {
            throw new IllegalArgumentException("Cycle detected through random pointers");
        }

        return reverseWithRandom(head);
    }

    private boolean hasCycleThroughRandom(RandomListNode node, Set<RandomListNode> visited) {
        if (node == null)
            return false;

        if (visited.contains(node))
            return true;

        visited.add(node);

        boolean hasCycle = false;
        if (node.next != null) {
            hasCycle |= hasCycleThroughRandom(node.next, new HashSet<>(visited));
        }
        if (node.random != null) {
            hasCycle |= hasCycleThroughRandom(node.random, new HashSet<>(visited));
        }

        return hasCycle;
    }

    // Follow-up 2: Reverse segments with random pointers
    public RandomListNode reverseSegmentWithRandom(RandomListNode head, int start, int end) {
        if (head == null || start == end)
            return head;

        // Find segment boundaries
        RandomListNode dummy = new RandomListNode(0);
        dummy.next = head;
        RandomListNode prev = dummy;

        for (int i = 0; i < start - 1; i++) {
            prev = prev.next;
        }

        RandomListNode segmentStart = prev.next;
        RandomListNode segmentEnd = segmentStart;

        for (int i = 0; i < end - start; i++) {
            segmentEnd = segmentEnd.next;
        }

        RandomListNode nextSegment = segmentEnd.next;
        segmentEnd.next = null;

        // Reverse the segment
        RandomListNode reversedSegment = reverseWithRandom(segmentStart);

        // Reconnect
        prev.next = reversedSegment;
        segmentStart.next = nextSegment;

        return dummy.next;
    }

    // Follow-up 3: Preserve original list while creating reversed copy
    public RandomListNode createReversedCopy(RandomListNode head) {
        if (head == null)
            return null;

        Map<RandomListNode, RandomListNode> originalToCopy = new HashMap<>();

        // First pass: create all nodes
        RandomListNode curr = head;
        while (curr != null) {
            originalToCopy.put(curr, new RandomListNode(curr.val));
            curr = curr.next;
        }

        // Second pass: set up next pointers in reverse order
        curr = head;
        RandomListNode newTail = null;

        while (curr != null) {
            RandomListNode copyNode = originalToCopy.get(curr);
            if (curr.next != null) {
                originalToCopy.get(curr.next).next = copyNode;
            } else {
                newTail = copyNode;
            }
            curr = curr.next;
        }

        // Third pass: set up random pointers
        curr = head;
        while (curr != null) {
            RandomListNode copyNode = originalToCopy.get(curr);
            if (curr.random != null) {
                copyNode.random = originalToCopy.get(curr.random);
            }
            curr = curr.next;
        }

        return newTail;
    }

    // Helper methods
    private RandomListNode reverseList(RandomListNode head) {
        RandomListNode prev = null;
        RandomListNode curr = head;

        while (curr != null) {
            RandomListNode next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }

        return prev;
    }

    private void printListWithRandom(RandomListNode head) {
        RandomListNode curr = head;
        Map<RandomListNode, Integer> nodeToIndex = new HashMap<>();

        // Map nodes to indices
        int index = 0;
        while (curr != null) {
            nodeToIndex.put(curr, index);
            curr = curr.next;
            index++;
        }

        // Print with random pointers
        curr = head;
        index = 0;
        while (curr != null) {
            String randomStr = curr.random == null ? "null" : nodeToIndex.get(curr.random).toString();
            System.out.print("[" + curr.val + "," + randomStr + "] ");
            curr = curr.next;
            index++;
        }
        System.out.println();
    }

    public static void main(String[] args) {
        ReverseLinkedListWithRandomPointer solution = new ReverseLinkedListWithRandomPointer();

        // Test case 1: Basic list with random pointers
        RandomListNode head1 = new RandomListNode(1);
        RandomListNode node2 = new RandomListNode(2);
        RandomListNode node3 = new RandomListNode(3);
        RandomListNode node4 = new RandomListNode(4);

        head1.next = node2;
        node2.next = node3;
        node3.next = node4;

        head1.random = node3;
        node2.random = head1;
        node4.random = node2;

        System.out.print("Original: ");
        solution.printListWithRandom(head1);

        RandomListNode result1 = solution.reverseWithRandom(head1);
        System.out.print("Reversed: ");
        solution.printListWithRandom(result1);

        // Test case 2: Constant space approach
        RandomListNode head2 = new RandomListNode(7);
        RandomListNode node13 = new RandomListNode(13);
        RandomListNode node11 = new RandomListNode(11);
        RandomListNode node10 = new RandomListNode(10);
        RandomListNode node1 = new RandomListNode(1);

        head2.next = node13;
        node13.next = node11;
        node11.next = node10;
        node10.next = node1;

        node13.random = head2;
        node11.random = node1;
        node10.random = node11;
        node1.random = head2;

        System.out.print("\nOriginal (test 2): ");
        solution.printListWithRandom(head2);

        RandomListNode result2 = solution.reverseWithRandomConstantSpace(head2);
        System.out.print("Reversed (constant space): ");
        solution.printListWithRandom(result2);

        // Test case 3: Follow-up - Create reversed copy
        RandomListNode head3 = new RandomListNode(3);
        RandomListNode node3_2 = new RandomListNode(3);
        RandomListNode node3_3 = new RandomListNode(3);

        head3.next = node3_2;
        node3_2.next = node3_3;
        head3.random = node3_3;

        System.out.print("\nOriginal (preserved): ");
        solution.printListWithRandom(head3);

        RandomListNode copy = solution.createReversedCopy(head3);
        System.out.print("Reversed copy: ");
        solution.printListWithRandom(copy);
        System.out.print("Original (unchanged): ");
        solution.printListWithRandom(head3);
    }
}
