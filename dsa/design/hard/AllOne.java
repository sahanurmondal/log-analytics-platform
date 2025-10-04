package design.hard;

import java.util.*;

/**
 * LeetCode 432: All O(1) Data Structure
 * https://leetcode.com/problems/all-oone-data-structure/
 *
 * Description: Design a data structure to store the strings' count with the
 * ability to return the strings with minimum and maximum counts.
 * 
 * Constraints:
 * - 1 <= key.length <= 10
 * - key consists of lowercase English letters
 * - It is guaranteed that for each call to dec, key is existing in the data
 * structure
 * - At most 5 * 10^4 calls will be made to inc, dec, getMaxKey, and getMinKey
 *
 * Follow-up:
 * - Can you perform all four operations in O(1) time complexity?
 * 
 * Time Complexity: O(1) for all operations
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Amazon
 */
public class AllOne {

    class Node {
        int count;
        Set<String> keys;
        Node prev, next;

        Node(int count) {
            this.count = count;
            this.keys = new HashSet<>();
        }
    }

    private Map<String, Node> keyToNode;
    private Node head, tail;

    public AllOne() {
        keyToNode = new HashMap<>();
        head = new Node(0);
        tail = new Node(0);
        head.next = tail;
        tail.prev = head;
    }

    public void inc(String key) {
        if (keyToNode.containsKey(key)) {
            Node node = keyToNode.get(key);
            int count = node.count;
            node.keys.remove(key);

            Node nextNode = node.next;
            if (nextNode == tail || nextNode.count != count + 1) {
                nextNode = new Node(count + 1);
                nextNode.next = node.next;
                nextNode.prev = node;
                node.next.prev = nextNode;
                node.next = nextNode;
            }

            nextNode.keys.add(key);
            keyToNode.put(key, nextNode);

            if (node.keys.isEmpty()) {
                removeNode(node);
            }
        } else {
            Node firstNode = head.next;
            if (firstNode == tail || firstNode.count != 1) {
                firstNode = new Node(1);
                firstNode.next = head.next;
                firstNode.prev = head;
                head.next.prev = firstNode;
                head.next = firstNode;
            }

            firstNode.keys.add(key);
            keyToNode.put(key, firstNode);
        }
    }

    public void dec(String key) {
        Node node = keyToNode.get(key);
        int count = node.count;
        node.keys.remove(key);

        if (count == 1) {
            keyToNode.remove(key);
        } else {
            Node prevNode = node.prev;
            if (prevNode == head || prevNode.count != count - 1) {
                prevNode = new Node(count - 1);
                prevNode.next = node;
                prevNode.prev = node.prev;
                node.prev.next = prevNode;
                node.prev = prevNode;
            }

            prevNode.keys.add(key);
            keyToNode.put(key, prevNode);
        }

        if (node.keys.isEmpty()) {
            removeNode(node);
        }
    }

    public String getMaxKey() {
        return tail.prev == head ? "" : tail.prev.keys.iterator().next();
    }

    public String getMinKey() {
        return head.next == tail ? "" : head.next.keys.iterator().next();
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    public static void main(String[] args) {
        AllOne allOne = new AllOne();
        allOne.inc("hello");
        allOne.inc("hello");
        System.out.println(allOne.getMaxKey()); // Expected: "hello"
        System.out.println(allOne.getMinKey()); // Expected: "hello"
        allOne.inc("leet");
        System.out.println(allOne.getMaxKey()); // Expected: "hello"
        System.out.println(allOne.getMinKey()); // Expected: "leet"
    }
}
