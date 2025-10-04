package linkedlist.easy;

import java.util.*;

/**
 * LeetCode 876: Middle of the Linked List
 * https://leetcode.com/problems/middle-of-the-linked-list/
 * 
 * Companies: Amazon, Meta, Google, Microsoft, Apple, Adobe, Bloomberg
 * Frequency: Very High (Asked in 900+ interviews)
 *
 * Description:
 * Given the head of a singly linked list, return the middle node of the linked
 * list.
 * If there are two middle nodes, return the second middle node.
 *
 * Constraints:
 * - The number of nodes in the list is in the range [1, 100].
 * - 1 <= Node.val <= 100
 * 
 * Follow-up Questions:
 * 1. What if we want the first middle node instead of second?
 * 2. Can you find the middle without counting the length?
 * 3. How to find k-th node from the middle?
 * 4. What about finding middle in circular linked list?
 * 5. Can you implement iteratively and recursively?
 */
public class MiddleOfLinkedList {

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

    // Approach 1: Two-pass (count then find) - O(n) time, O(1) space
    public ListNode middleNode(ListNode head) {
        if (head == null)
            return null;

        // First pass: count nodes
        int length = 0;
        ListNode current = head;
        while (current != null) {
            length++;
            current = current.next;
        }

        // Second pass: find middle
        int middleIndex = length / 2;
        current = head;
        for (int i = 0; i < middleIndex; i++) {
            current = current.next;
        }

        return current;
    }

    // Approach 2: Slow-Fast pointers (Floyd's Tortoise and Hare) - O(n) time, O(1)
    // space
    public ListNode middleNodeFast(ListNode head) {
        if (head == null)
            return null;

        ListNode slow = head;
        ListNode fast = head;

        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    // Approach 3: Using ArrayList - O(n) time, O(n) space
    public ListNode middleNodeArray(ListNode head) {
        if (head == null)
            return null;

        List<ListNode> nodes = new ArrayList<>();

        while (head != null) {
            nodes.add(head);
            head = head.next;
        }

        return nodes.get(nodes.size() / 2);
    }

    // Approach 4: Recursive - O(n) time, O(n) space
    public ListNode middleNodeRecursive(ListNode head) {
        int length = getLength(head);
        return getMiddleHelper(head, length / 2);
    }

    private ListNode getMiddleHelper(ListNode head, int stepsToMiddle) {
        if (stepsToMiddle == 0) {
            return head;
        }
        return getMiddleHelper(head.next, stepsToMiddle - 1);
    }

    // Follow-up 1: Get first middle node (for even length lists)
    public ListNode firstMiddleNode(ListNode head) {
        if (head == null)
            return null;

        ListNode slow = head;
        ListNode fast = head;

        // Stop when fast.next.next is null (instead of fast.next)
        while (fast.next != null && fast.next.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }

        return slow;
    }

    // Follow-up 1: Get both middle nodes for even length
    public ListNode[] bothMiddleNodes(ListNode head) {
        if (head == null)
            return new ListNode[] { null, null };

        int length = getLength(head);

        if (length % 2 == 1) {
            // Odd length - only one middle
            ListNode middle = middleNodeFast(head);
            return new ListNode[] { middle, middle };
        } else {
            // Even length - two middles
            ListNode first = firstMiddleNode(head);
            ListNode second = first.next;
            return new ListNode[] { first, second };
        }
    }

    // Follow-up 3: Find k-th node from middle
    public ListNode kthFromMiddle(ListNode head, int k) {
        ListNode middle = middleNodeFast(head);

        if (k == 0)
            return middle;

        if (k > 0) {
            // k nodes after middle
            for (int i = 0; i < k && middle != null; i++) {
                middle = middle.next;
            }
            return middle;
        } else {
            // |k| nodes before middle
            int length = getLength(head);
            int middleIndex = length / 2;
            int targetIndex = middleIndex + k;

            if (targetIndex < 0)
                return null;

            ListNode current = head;
            for (int i = 0; i < targetIndex; i++) {
                current = current.next;
            }
            return current;
        }
    }

    // Follow-up 4: Middle of circular linked list
    public ListNode middleNodeCircular(ListNode head) {
        if (head == null)
            return null;

        // First detect if there's a cycle and find cycle length
        ListNode slow = head;
        ListNode fast = head;

        // Detect cycle
        do {
            if (fast == null || fast.next == null) {
                // No cycle - use regular middle finding
                return middleNodeFast(head);
            }
            slow = slow.next;
            fast = fast.next.next;
        } while (slow != fast);

        // Count cycle length
        int cycleLength = 1;
        ListNode temp = slow.next;
        while (temp != slow) {
            cycleLength++;
            temp = temp.next;
        }

        // Find middle of cycle
        int middleSteps = cycleLength / 2;
        for (int i = 0; i < middleSteps; i++) {
            slow = slow.next;
        }

        return slow;
    }

    // Advanced: Find all nodes at distance d from middle
    public List<ListNode> nodesAtDistanceFromMiddle(ListNode head, int distance) {
        List<ListNode> result = new ArrayList<>();
        ListNode middle = middleNodeFast(head);

        // Add middle node if distance is 0
        if (distance == 0) {
            result.add(middle);
            return result;
        }

        // Find nodes at given distance
        int length = getLength(head);
        int middleIndex = length / 2;

        // Check left side
        int leftIndex = middleIndex - distance;
        if (leftIndex >= 0) {
            ListNode leftNode = getNodeAtIndex(head, leftIndex);
            if (leftNode != null) {
                result.add(leftNode);
            }
        }

        // Check right side
        int rightIndex = middleIndex + distance;
        if (rightIndex < length) {
            ListNode rightNode = getNodeAtIndex(head, rightIndex);
            if (rightNode != null) {
                result.add(rightNode);
            }
        }

        return result;
    }

    // Advanced: Middle of linked list with weights
    public ListNode weightedMiddle(ListNode head, int[] weights) {
        if (head == null || weights == null)
            return null;

        // Calculate total weight
        int totalWeight = 0;
        for (int weight : weights) {
            totalWeight += weight;
        }

        int targetWeight = totalWeight / 2;
        int currentWeight = 0;
        ListNode current = head;
        int index = 0;

        while (current != null && index < weights.length) {
            currentWeight += weights[index];
            if (currentWeight >= targetWeight) {
                return current;
            }
            current = current.next;
            index++;
        }

        return current;
    }

    // Advanced: Find median node(s) of sorted linked list
    public ListNode[] medianNodes(ListNode head) {
        if (head == null)
            return new ListNode[] { null };

        int length = getLength(head);

        if (length % 2 == 1) {
            // Odd length - single median
            return new ListNode[] { middleNodeFast(head) };
        } else {
            // Even length - two medians
            ListNode[] middles = bothMiddleNodes(head);
            return middles;
        }
    }

    // Advanced: Middle node with custom comparison
    public ListNode middleNodeCustom(ListNode head, Comparator<ListNode> comparator) {
        if (head == null)
            return null;

        List<ListNode> nodes = new ArrayList<>();
        ListNode current = head;

        while (current != null) {
            nodes.add(current);
            current = current.next;
        }

        // Sort using custom comparator
        nodes.sort(comparator);

        // Return middle of sorted list
        return nodes.get(nodes.size() / 2);
    }

    // Advanced: K middle nodes (for very large lists)
    public List<ListNode> kMiddleNodes(ListNode head, int k) {
        if (head == null || k <= 0)
            return new ArrayList<>();

        int length = getLength(head);
        List<ListNode> result = new ArrayList<>();

        // Calculate positions for k middle nodes
        for (int i = 0; i < k; i++) {
            int position = (length * (i + 1)) / (k + 1);
            ListNode node = getNodeAtIndex(head, position);
            if (node != null) {
                result.add(node);
            }
        }

        return result;
    }

    // Helper methods
    private int getLength(ListNode head) {
        int length = 0;
        while (head != null) {
            length++;
            head = head.next;
        }
        return length;
    }

    private ListNode getNodeAtIndex(ListNode head, int index) {
        int currentIndex = 0;
        while (head != null && currentIndex < index) {
            head = head.next;
            currentIndex++;
        }
        return head;
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

    // Helper: Create circular linked list
    public static ListNode createCircularList(int[] values) {
        if (values == null || values.length == 0) {
            return null;
        }

        ListNode head = createList(values);

        // Find tail and connect to head
        ListNode tail = head;
        while (tail.next != null) {
            tail = tail.next;
        }
        tail.next = head;

        return head;
    }

    // Helper: Get all node values
    public static List<Integer> getValues(ListNode head) {
        List<Integer> values = new ArrayList<>();
        while (head != null) {
            values.add(head.val);
            head = head.next;
        }
        return values;
    }

    // Performance comparison
    public Map<String, Long> comparePerformance(ListNode head) {
        Map<String, Long> results = new HashMap<>();

        // Test two-pass approach
        long start = System.nanoTime();
        middleNode(head);
        results.put("TwoPass", System.nanoTime() - start);

        // Test fast-slow pointers
        start = System.nanoTime();
        middleNodeFast(head);
        results.put("FastSlow", System.nanoTime() - start);

        // Test array approach
        start = System.nanoTime();
        middleNodeArray(head);
        results.put("Array", System.nanoTime() - start);

        // Test recursive approach
        start = System.nanoTime();
        middleNodeRecursive(head);
        results.put("Recursive", System.nanoTime() - start);

        return results;
    }

    public static void main(String[] args) {
        MiddleOfLinkedList solution = new MiddleOfLinkedList();

        // Test Case 1: Odd length list
        System.out.println("=== Test Case 1: Odd Length List ===");
        ListNode oddList = createList(new int[] { 1, 2, 3, 4, 5 });
        System.out.println("List: " + oddList);

        ListNode middle1 = solution.middleNode(oddList);
        ListNode middle2 = solution.middleNodeFast(oddList);
        ListNode middle3 = solution.middleNodeArray(oddList);
        ListNode middle4 = solution.middleNodeRecursive(oddList);

        System.out.println("Two-pass middle: " + middle1.val);
        System.out.println("Fast-slow middle: " + middle2.val);
        System.out.println("Array middle: " + middle3.val);
        System.out.println("Recursive middle: " + middle4.val);

        // Test Case 2: Even length list
        System.out.println("\n=== Test Case 2: Even Length List ===");
        ListNode evenList = createList(new int[] { 1, 2, 3, 4, 5, 6 });
        System.out.println("List: " + evenList);

        ListNode evenMiddle = solution.middleNodeFast(evenList);
        System.out.println("Second middle: " + evenMiddle.val);

        // Follow-up 1: First middle for even length
        System.out.println("\n=== Follow-up 1: First Middle ===");
        ListNode firstMiddle = solution.firstMiddleNode(evenList);
        System.out.println("First middle: " + firstMiddle.val);

        ListNode[] bothMiddles = solution.bothMiddleNodes(evenList);
        System.out.println("Both middles: " + bothMiddles[0].val + ", " + bothMiddles[1].val);

        // Test with odd length
        ListNode[] oddMiddles = solution.bothMiddleNodes(oddList);
        System.out.println("Odd list middles: " + oddMiddles[0].val + ", " + oddMiddles[1].val);

        // Follow-up 3: K-th from middle
        System.out.println("\n=== Follow-up 3: K-th from Middle ===");
        ListNode testList = createList(new int[] { 1, 2, 3, 4, 5, 6, 7 });
        System.out.println("List: " + testList);

        for (int k = -3; k <= 3; k++) {
            ListNode kthNode = solution.kthFromMiddle(testList, k);
            String value = (kthNode != null) ? String.valueOf(kthNode.val) : "null";
            System.out.println("k=" + k + " from middle: " + value);
        }

        // Follow-up 4: Circular linked list (commented out to avoid infinite loops in
        // toString)
        System.out.println("\n=== Follow-up 4: Circular Linked List ===");
        // Note: Creating a simple test without circular reference for demo
        ListNode circularTest = createList(new int[] { 1, 2, 3, 4, 5, 6 });
        ListNode circularMiddle = solution.middleNodeCircular(circularTest);
        System.out.println("Circular middle (non-circular test): " + circularMiddle.val);

        // Advanced: Nodes at distance from middle
        System.out.println("\n=== Advanced: Nodes at Distance from Middle ===");
        ListNode distanceList = createList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
        System.out.println("List: " + distanceList);

        for (int d = 0; d <= 3; d++) {
            List<ListNode> nodesAtDistance = solution.nodesAtDistanceFromMiddle(distanceList, d);
            System.out.print("Distance " + d + ": ");
            for (ListNode node : nodesAtDistance) {
                System.out.print(node.val + " ");
            }
            System.out.println();
        }

        // Advanced: Weighted middle
        System.out.println("\n=== Advanced: Weighted Middle ===");
        ListNode weightedList = createList(new int[] { 1, 2, 3, 4, 5 });
        int[] weights = { 1, 2, 3, 4, 5 }; // Higher weights later

        ListNode weightedMiddleNode = solution.weightedMiddle(weightedList, weights);
        System.out.println("Weighted middle: " + weightedMiddleNode.val);

        // Advanced: Median nodes
        System.out.println("\n=== Advanced: Median Nodes ===");

        ListNode[] medianOdd = solution.medianNodes(oddList);
        System.out.print("Odd list medians: ");
        for (ListNode node : medianOdd) {
            System.out.print(node.val + " ");
        }
        System.out.println();

        ListNode[] medianEven = solution.medianNodes(evenList);
        System.out.print("Even list medians: ");
        for (ListNode node : medianEven) {
            System.out.print(node.val + " ");
        }
        System.out.println();

        // Advanced: Custom comparison
        System.out.println("\n=== Advanced: Custom Comparison ===");
        ListNode customList = createList(new int[] { 5, 1, 8, 3, 9, 2 });
        System.out.println("Original: " + customList);

        // Find middle when sorted by value
        ListNode customMiddle = solution.middleNodeCustom(customList,
                Comparator.comparing(node -> node.val));
        System.out.println("Middle when sorted by value: " + customMiddle.val);

        // Advanced: K middle nodes
        System.out.println("\n=== Advanced: K Middle Nodes ===");
        ListNode kMiddleList = createList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 });
        System.out.println("List: " + kMiddleList);

        List<ListNode> kMiddles = solution.kMiddleNodes(kMiddleList, 3);
        System.out.print("3 middle nodes: ");
        for (ListNode node : kMiddles) {
            System.out.print(node.val + " ");
        }
        System.out.println();

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
        ListNode singleMiddle = solution.middleNodeFast(singleNode);
        System.out.println("Single node middle: " + singleMiddle.val);

        // Two nodes
        ListNode twoNodes = createList(new int[] { 1, 2 });
        ListNode twoMiddle = solution.middleNodeFast(twoNodes);
        System.out.println("Two nodes middle: " + twoMiddle.val);

        // Empty list
        ListNode emptyMiddle = solution.middleNodeFast(null);
        System.out.println("Empty list middle: " + (emptyMiddle == null ? "null" : emptyMiddle.val));

        // Large odd list
        ListNode largeOdd = createList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });
        ListNode largeOddMiddle = solution.middleNodeFast(largeOdd);
        System.out.println("Large odd list middle: " + largeOddMiddle.val);

        // Verify all approaches give same result
        System.out.println("\n=== Verification ===");
        ListNode verifyList = createList(new int[] { 1, 2, 3, 4, 5, 6, 7, 8 });

        ListNode v1 = solution.middleNode(verifyList);
        ListNode v2 = solution.middleNodeFast(verifyList);
        ListNode v3 = solution.middleNodeArray(verifyList);
        ListNode v4 = solution.middleNodeRecursive(verifyList);

        boolean allSame = v1.val == v2.val && v2.val == v3.val && v3.val == v4.val;
        System.out.println("All approaches consistent: " + allSame);
        System.out.println("Middle value: " + v1.val);

        System.out.println("\nMiddle of Linked List testing completed successfully!");
    }
}
