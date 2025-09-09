package linkedlist.medium;

import java.util.*;

/**
 * LeetCode 143: Reorder List
 * https://leetcode.com/problems/reorder-list/
 * 
 * Companies: Meta, Amazon, Microsoft, Google, Apple, Bloomberg, Uber
 * Frequency: Very High (Asked in 800+ interviews)
 *
 * Description:
 * You are given the head of a singly linked-list. The list can be represented
 * as:
 * L0 → L1 → … → Ln - 1 → Ln
 * 
 * Reorder it to:
 * L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …
 * 
 * You may not modify the values in the list's nodes. Only nodes themselves may
 * be changed.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [1, 5 * 10^4].
 * - 1 <= Node.val <= 1000
 * 
 * Follow-up Questions:
 * 1. Can you solve it in O(1) space without using extra data structures?
 * 2. How would you handle reordering with different patterns?
 * 3. What if we want to reorder in groups of k?
 * 4. Can you implement an undo operation?
 * 5. How to handle circular linked lists?
 */
public class ReorderList {

    // Definition for singly-linked list
    public static class ListNode {
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

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            ListNode current = this;
            Set<ListNode> visited = new HashSet<>();
            int count = 0;

            while (current != null && !visited.contains(current) && count < 20) {
                visited.add(current);
                sb.append(current.val);
                if (current.next != null && !visited.contains(current.next)) {
                    sb.append(" -> ");
                }
                current = current.next;
                count++;
            }

            if (current != null && count >= 20) {
                sb.append(" -> ...");
            } else if (current != null) {
                sb.append(" -> [CYCLE]");
            }

            return sb.toString();
        }
    }

    // Approach 1: Using ArrayList - O(n) time, O(n) space
    public void reorderList(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        // Store all nodes in a list
        List<ListNode> nodes = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            nodes.add(current);
            current = current.next;
        }

        // Reorder using two pointers
        int left = 0, right = nodes.size() - 1;

        while (left < right) {
            nodes.get(left).next = nodes.get(right);
            left++;

            if (left >= right)
                break;

            nodes.get(right).next = nodes.get(left);
            right--;
        }

        nodes.get(left).next = null;
    }

    // Approach 2: Optimal - Find middle, reverse second half, merge - O(n) time,
    // O(1) space
    public void reorderListOptimal(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        // Step 1: Find the middle
        ListNode slow = head;
        ListNode fast = head;
        ListNode prev = null;

        while (fast != null && fast.next != null) {
            prev = slow;
            slow = slow.next;
            fast = fast.next.next;
        }

        // Split the list
        prev.next = null;

        // Step 2: Reverse the second half
        ListNode secondHalf = reverseList(slow);

        // Step 3: Merge the two halves
        mergeAlternating(head, secondHalf);
    }

    // Approach 3: Using Stack - O(n) time, O(n) space
    public void reorderListStack(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        // Push all nodes to stack
        Stack<ListNode> stack = new Stack<>();
        ListNode current = head;
        int size = 0;

        while (current != null) {
            stack.push(current);
            current = current.next;
            size++;
        }

        // Reorder using stack
        current = head;
        for (int i = 0; i < size / 2; i++) {
            ListNode last = stack.pop();
            ListNode next = current.next;

            current.next = last;
            last.next = next;
            current = next;
        }

        current.next = null;
    }

    // Approach 4: Using Deque - O(n) time, O(n) space
    public void reorderListDeque(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        // Add all nodes to deque
        Deque<ListNode> deque = new ArrayDeque<>();
        ListNode current = head;

        while (current != null) {
            deque.add(current);
            current = current.next;
        }

        // Remove head from deque
        deque.removeFirst();

        current = head;
        boolean takeFromEnd = true;

        while (!deque.isEmpty()) {
            ListNode next;
            if (takeFromEnd) {
                next = deque.removeLast();
            } else {
                next = deque.removeFirst();
            }

            current.next = next;
            current = next;
            takeFromEnd = !takeFromEnd;
        }

        current.next = null;
    }

    // Approach 5: Recursive approach - O(n) time, O(n) space
    public void reorderListRecursive(ListNode head) {
        if (head == null || head.next == null) {
            return;
        }

        int length = getLength(head);
        reorderHelper(head, length);
    }

    private ListNode reorderHelper(ListNode head, int length) {
        if (length == 1) {
            ListNode next = head.next;
            head.next = null;
            return next;
        }

        if (length == 2) {
            ListNode next = head.next.next;
            head.next.next = null;
            return next;
        }

        ListNode tail = reorderHelper(head.next, length - 2);
        ListNode afterTail = tail.next;
        ListNode originalNext = head.next;

        head.next = tail;
        tail.next = originalNext;

        return afterTail;
    }

    // Follow-up 1: Reorder with different patterns
    public void reorderListPattern(ListNode head, String pattern) {
        if (head == null || head.next == null || pattern == null) {
            return;
        }

        List<ListNode> nodes = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            nodes.add(current);
            current = current.next;
        }

        switch (pattern.toLowerCase()) {
            case "reverse":
                reorderReverse(nodes);
                break;
            case "alternating":
                reorderAlternating(nodes);
                break;
            case "groups":
                reorderGroups(nodes, 2);
                break;
            case "spiral":
                reorderSpiral(nodes);
                break;
            default:
                reorderAlternating(nodes); // Default pattern
        }

        // Reconnect the list
        for (int i = 0; i < nodes.size() - 1; i++) {
            nodes.get(i).next = nodes.get(i + 1);
        }
        nodes.get(nodes.size() - 1).next = null;
    }

    // Follow-up 3: Reorder in groups of k
    public void reorderListGroups(ListNode head, int k) {
        if (head == null || head.next == null || k <= 1) {
            return;
        }

        List<ListNode> nodes = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            nodes.add(current);
            current = current.next;
        }

        List<ListNode> reordered = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i += k) {
            List<ListNode> group = new ArrayList<>();
            for (int j = i; j < Math.min(i + k, nodes.size()); j++) {
                group.add(nodes.get(j));
            }

            // Reverse the group
            Collections.reverse(group);
            reordered.addAll(group);
        }

        // Reconnect
        for (int i = 0; i < reordered.size() - 1; i++) {
            reordered.get(i).next = reordered.get(i + 1);
        }
        reordered.get(reordered.size() - 1).next = null;
    }

    // Follow-up 4: Reorder with undo capability
    public static class ReorderableList {
        private ListNode head;
        private Stack<List<ListNode>> history;

        public ReorderableList(ListNode head) {
            this.head = head;
            this.history = new Stack<>();
            saveState();
        }

        private void saveState() {
            List<ListNode> state = new ArrayList<>();
            ListNode current = head;

            while (current != null) {
                state.add(current);
                current = current.next;
            }

            history.push(new ArrayList<>(state));
        }

        public void reorder() {
            saveState();
            reorderAlternating(head);
        }

        public void undo() {
            if (history.size() <= 1) {
                return; // Cannot undo initial state
            }

            history.pop(); // Remove current state
            List<ListNode> previousState = history.peek();

            // Restore previous state
            for (int i = 0; i < previousState.size() - 1; i++) {
                previousState.get(i).next = previousState.get(i + 1);
            }
            previousState.get(previousState.size() - 1).next = null;

            head = previousState.get(0);
        }

        public ListNode getHead() {
            return head;
        }

        private void reorderAlternating(ListNode head) {
            List<ListNode> nodes = new ArrayList<>();
            ListNode current = head;

            while (current != null) {
                nodes.add(current);
                current = current.next;
            }

            List<ListNode> reordered = new ArrayList<>();
            int left = 0, right = nodes.size() - 1;
            boolean fromLeft = true;

            while (left <= right) {
                if (fromLeft) {
                    reordered.add(nodes.get(left++));
                } else {
                    reordered.add(nodes.get(right--));
                }
                fromLeft = !fromLeft;
            }

            // Reconnect
            for (int i = 0; i < reordered.size() - 1; i++) {
                reordered.get(i).next = reordered.get(i + 1);
            }
            reordered.get(reordered.size() - 1).next = null;

            this.head = reordered.get(0);
        }
    }

    // Follow-up 5: Handle circular linked lists
    public void reorderCircularList(ListNode head) {
        if (head == null || head.next == null || head.next == head) {
            return;
        }

        // First, break the cycle and find the tail
        ListNode slow = head;
        ListNode fast = head;
        ListNode tail = null;

        do {
            tail = slow;
            slow = slow.next;
            fast = fast.next.next;
        } while (fast != head && fast.next != head);

        // Break the cycle
        while (tail.next != head) {
            tail = tail.next;
        }
        tail.next = null;

        // Reorder the linear list
        reorderListOptimal(head);

        // Restore the cycle
        ListNode current = head;
        while (current.next != null) {
            current = current.next;
        }
        current.next = head;
    }

    // Advanced: Multi-pattern reordering
    public void reorderMultiPattern(ListNode head, int[] pattern) {
        if (head == null || head.next == null || pattern == null) {
            return;
        }

        List<ListNode> nodes = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            nodes.add(current);
            current = current.next;
        }

        List<ListNode> reordered = new ArrayList<>();

        for (int index : pattern) {
            if (index >= 0 && index < nodes.size()) {
                reordered.add(nodes.get(index));
            }
        }

        // Reconnect
        for (int i = 0; i < reordered.size() - 1; i++) {
            reordered.get(i).next = reordered.get(i + 1);
        }
        if (!reordered.isEmpty()) {
            reordered.get(reordered.size() - 1).next = null;
        }
    }

    // Advanced: Weighted reordering
    public void reorderListWeighted(ListNode head, int[] weights) {
        if (head == null || head.next == null || weights == null) {
            return;
        }

        List<NodeWeight> nodeWeights = new ArrayList<>();
        ListNode current = head;
        int index = 0;

        while (current != null && index < weights.length) {
            nodeWeights.add(new NodeWeight(current, weights[index]));
            current = current.next;
            index++;
        }

        // Sort by weight
        nodeWeights.sort((a, b) -> Integer.compare(b.weight, a.weight));

        // Reconnect based on sorted weights
        for (int i = 0; i < nodeWeights.size() - 1; i++) {
            nodeWeights.get(i).node.next = nodeWeights.get(i + 1).node;
        }
        nodeWeights.get(nodeWeights.size() - 1).node.next = null;
    }

    private static class NodeWeight {
        ListNode node;
        int weight;

        NodeWeight(ListNode node, int weight) {
            this.node = node;
            this.weight = weight;
        }
    }

    // Advanced: Conditional reordering
    public void reorderListConditional(ListNode head, java.util.function.Predicate<Integer> condition) {
        if (head == null || head.next == null) {
            return;
        }

        List<ListNode> matching = new ArrayList<>();
        List<ListNode> nonMatching = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            if (condition.test(current.val)) {
                matching.add(current);
            } else {
                nonMatching.add(current);
            }
            current = current.next;
        }

        // Merge: alternating between matching and non-matching
        List<ListNode> reordered = new ArrayList<>();
        int i = 0, j = 0;
        boolean takeMatching = true;

        while (i < matching.size() || j < nonMatching.size()) {
            if (takeMatching && i < matching.size()) {
                reordered.add(matching.get(i++));
            } else if (!takeMatching && j < nonMatching.size()) {
                reordered.add(nonMatching.get(j++));
            } else if (i < matching.size()) {
                reordered.add(matching.get(i++));
            } else {
                reordered.add(nonMatching.get(j++));
            }
            takeMatching = !takeMatching;
        }

        // Reconnect
        for (int k = 0; k < reordered.size() - 1; k++) {
            reordered.get(k).next = reordered.get(k + 1);
        }
        reordered.get(reordered.size() - 1).next = null;
    }

    // Helper methods
    private ListNode reverseList(ListNode head) {
        ListNode prev = null;
        ListNode current = head;

        while (current != null) {
            ListNode next = current.next;
            current.next = prev;
            prev = current;
            current = next;
        }

        return prev;
    }

    private void mergeAlternating(ListNode first, ListNode second) {
        while (second != null) {
            ListNode nextFirst = first.next;
            ListNode nextSecond = second.next;

            first.next = second;
            second.next = nextFirst;

            first = nextFirst;
            second = nextSecond;
        }
    }

    private int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }

    // Pattern helper methods
    private void reorderReverse(List<ListNode> nodes) {
        Collections.reverse(nodes);
    }

    private void reorderAlternating(List<ListNode> nodes) {
        List<ListNode> temp = new ArrayList<>(nodes);
        nodes.clear();

        int left = 0, right = temp.size() - 1;
        boolean fromLeft = true;

        while (left <= right) {
            if (fromLeft) {
                nodes.add(temp.get(left++));
            } else {
                nodes.add(temp.get(right--));
            }
            fromLeft = !fromLeft;
        }
    }

    private void reorderGroups(List<ListNode> nodes, int groupSize) {
        List<ListNode> temp = new ArrayList<>(nodes);
        nodes.clear();

        for (int i = 0; i < temp.size(); i += groupSize) {
            List<ListNode> group = new ArrayList<>();
            for (int j = i; j < Math.min(i + groupSize, temp.size()); j++) {
                group.add(temp.get(j));
            }
            Collections.reverse(group);
            nodes.addAll(group);
        }
    }

    private void reorderSpiral(List<ListNode> nodes) {
        List<ListNode> temp = new ArrayList<>(nodes);
        nodes.clear();

        int left = 0, right = temp.size() - 1;
        int top = 0, bottom = 0;

        while (left <= right) {
            // Add from left
            if (left <= right) {
                nodes.add(temp.get(left++));
            }

            // Add from right
            if (left <= right) {
                nodes.add(temp.get(right--));
            }
        }
    }

    // Helper: Create linked list from array
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

    // Helper: Convert list to array
    public static int[] listToArray(ListNode head) {
        List<Integer> values = new ArrayList<>();

        while (head != null) {
            values.add(head.val);
            head = head.next;
        }

        return values.stream().mapToInt(Integer::intValue).toArray();
    }

    // Helper: Copy list
    public static ListNode copyList(ListNode head) {
        if (head == null)
            return null;

        ListNode dummy = new ListNode(0);
        ListNode current = dummy;

        while (head != null) {
            current.next = new ListNode(head.val);
            current = current.next;
            head = head.next;
        }

        return dummy.next;
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(ListNode head) {
        Map<String, Long> results = new HashMap<>();

        // Test ArrayList approach
        ListNode copy1 = copyList(head);
        long start = System.nanoTime();
        reorderList(copy1);
        results.put("ArrayList", System.nanoTime() - start);

        // Test optimal approach
        ListNode copy2 = copyList(head);
        start = System.nanoTime();
        reorderListOptimal(copy2);
        results.put("Optimal", System.nanoTime() - start);

        // Test stack approach
        ListNode copy3 = copyList(head);
        start = System.nanoTime();
        reorderListStack(copy3);
        results.put("Stack", System.nanoTime() - start);

        // Test deque approach
        ListNode copy4 = copyList(head);
        start = System.nanoTime();
        reorderListDeque(copy4);
        results.put("Deque", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        ReorderList solution = new ReorderList();

        // Test Case 1: Even length list
        System.out.println("=== Test Case 1: Even Length List ===");
        ListNode evenList = createList(new int[] { 1, 2, 3, 4 });
        System.out.println("Original: " + evenList);

        ListNode copy1 = copyList(evenList);
        solution.reorderList(copy1);
        System.out.println("ArrayList approach: " + copy1);

        ListNode copy2 = copyList(evenList);
        solution.reorderListOptimal(copy2);
        System.out.println("Optimal approach: " + copy2);

        ListNode copy3 = copyList(evenList);
        solution.reorderListStack(copy3);
        System.out.println("Stack approach: " + copy3);

        ListNode copy4 = copyList(evenList);
        solution.reorderListDeque(copy4);
        System.out.println("Deque approach: " + copy4);

        // Test Case 2: Odd length list
        System.out.println("\n=== Test Case 2: Odd Length List ===");
        ListNode oddList = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Original: " + oddList);

        ListNode oddCopy = copyList(oddList);
        solution.reorderListOptimal(oddCopy);
        System.out.println("Reordered: " + oddCopy);

        // Test Case 3: Different patterns
        System.out.println("\n=== Test Case 3: Different Patterns ===");
        ListNode patternList = createList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.println("Original: " + patternList);

        ListNode reverseCopy = copyList(patternList);
        solution.reorderListPattern(reverseCopy, "reverse");
        System.out.println("Reverse pattern: " + reverseCopy);

        ListNode spiralCopy = copyList(patternList);
        solution.reorderListPattern(spiralCopy, "spiral");
        System.out.println("Spiral pattern: " + spiralCopy);

        // Test Case 4: Group reordering
        System.out.println("\n=== Test Case 4: Group Reordering ===");
        ListNode groupList = createList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });
        System.out.println("Original: " + groupList);

        ListNode groupCopy = copyList(groupList);
        solution.reorderListGroups(groupCopy, 3);
        System.out.println("Groups of 3: " + groupCopy);

        // Test Case 5: Undo functionality
        System.out.println("\n=== Test Case 5: Undo Functionality ===");
        ListNode undoList = createList(new int[] { 1, 2, 3, 4, 5 });
        ReorderableList reorderable = new ReorderableList(undoList);

        System.out.println("Initial: " + reorderable.getHead());
        reorderable.reorder();
        System.out.println("After reorder: " + reorderable.getHead());
        reorderable.undo();
        System.out.println("After undo: " + reorderable.getHead());

        // Test Case 6: Multi-pattern reordering
        System.out.println("\n=== Test Case 6: Multi-pattern Reordering ===");
        ListNode multiList = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Original: " + multiList);

        ListNode multiCopy = copyList(multiList);
        int[] pattern = { 4, 0, 3, 1, 2 }; // Custom order
        solution.reorderMultiPattern(multiCopy, pattern);
        System.out.println("Custom pattern: " + multiCopy);

        // Test Case 7: Weighted reordering
        System.out.println("\n=== Test Case 7: Weighted Reordering ===");
        ListNode weightedList = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("Original: " + weightedList);

        ListNode weightedCopy = copyList(weightedList);
        int[] weights = { 1, 5, 2, 4, 3 }; // Higher weight = earlier position
        solution.reorderListWeighted(weightedCopy, weights);
        System.out.println("Weighted reorder: " + weightedCopy);

        // Test Case 8: Conditional reordering
        System.out.println("\n=== Test Case 8: Conditional Reordering ===");
        ListNode conditionalList = createList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.println("Original: " + conditionalList);

        ListNode conditionalCopy = copyList(conditionalList);
        solution.reorderListConditional(conditionalCopy, x -> x % 2 == 0); // Even numbers first
        System.out.println("Even/Odd alternating: " + conditionalCopy);

        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");

        // Create larger list for performance testing
        int[] largeArray = new int[1000];
        for (int i = 0; i < 1000; i++) {
            largeArray[i] = i + 1;
        }
        ListNode largeList = createList(largeArray);

        Map<String, Long> performance = solution.comparePerformance(largeList);
        performance.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .forEach(entry -> System.out.println(entry.getKey() + ": " +
                        entry.getValue() / 1000.0 + " microseconds"));

        // Edge cases
        System.out.println("\n=== Edge Cases ===");

        // Single node
        ListNode singleNode = createList(new int[] { 42 });
        System.out.println("Single node before: " + singleNode);
        solution.reorderListOptimal(singleNode);
        System.out.println("Single node after: " + singleNode);

        // Two nodes
        ListNode twoNodes = createList(new int[] { 1, 2 });
        System.out.println("Two nodes before: " + twoNodes);
        solution.reorderListOptimal(twoNodes);
        System.out.println("Two nodes after: " + twoNodes);

        // Empty list
        ListNode emptyList = null;
        solution.reorderListOptimal(emptyList);
        System.out.println("Empty list after reorder: " + (emptyList == null ? "null" : emptyList.toString()));

        // Large list verification
        System.out.println("\n=== Large List Verification ===");
        ListNode verifyList = createList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        System.out.println("Original: " + verifyList);

        // Test all approaches give same result
        ListNode v1 = copyList(verifyList);
        ListNode v2 = copyList(verifyList);
        ListNode v3 = copyList(verifyList);
        ListNode v4 = copyList(verifyList);

        solution.reorderList(v1);
        solution.reorderListOptimal(v2);
        solution.reorderListStack(v3);
        solution.reorderListDeque(v4);

        int[] result1 = listToArray(v1);
        int[] result2 = listToArray(v2);
        int[] result3 = listToArray(v3);
        int[] result4 = listToArray(v4);

        boolean allSame = Arrays.equals(result1, result2) &&
                Arrays.equals(result2, result3) &&
                Arrays.equals(result3, result4);

        System.out.println("All approaches consistent: " + allSame);
        System.out.println("Final result: " + Arrays.toString(result1));

        System.out.println("\nReorder List testing completed successfully!");
    }
}
