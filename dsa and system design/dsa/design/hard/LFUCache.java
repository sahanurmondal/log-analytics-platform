package design.hard;

import java.util.*;

/**
 * LeetCode 460: LFU Cache
 * https://leetcode.com/problems/lfu-cache/
 *
 * Description: Design and implement a data structure for a Least Frequently
 * Used (LFU) cache.
 * 
 * Constraints:
 * - 1 <= capacity <= 10^4
 * - 0 <= key <= 10^5
 * - 0 <= value <= 10^9
 * - At most 2 * 10^5 calls will be made to get and put
 *
 * Follow-up:
 * - Can you do both operations in O(1) time complexity?
 * 
 * Time Complexity: O(1) for both get and put
 * Space Complexity: O(capacity)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft
 */
public class LFUCache {

    class Node {
        int key, value, freq;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }

    class DoublyLinkedList {
        Node head, tail;

        DoublyLinkedList() {
            head = new Node(0, 0);
            tail = new Node(0, 0);
            head.next = tail;
            tail.prev = head;
        }

        void addToHead(Node node) {
            node.next = head.next;
            node.prev = head;
            head.next.prev = node;
            head.next = node;
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        Node removeTail() {
            Node last = tail.prev;
            remove(last);
            return last;
        }

        boolean isEmpty() {
            return head.next == tail;
        }
    }

    private int capacity;
    private int minFreq;
    private Map<Integer, Node> keyToNode;
    private Map<Integer, DoublyLinkedList> freqToList;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 1;
        this.keyToNode = new HashMap<>();
        this.freqToList = new HashMap<>();
    }

    public int get(int key) {
        Node node = keyToNode.get(key);
        if (node == null) {
            return -1;
        }

        updateFreq(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0)
            return;

        Node node = keyToNode.get(key);
        if (node != null) {
            node.value = value;
            updateFreq(node);
            return;
        }

        if (keyToNode.size() >= capacity) {
            DoublyLinkedList minFreqList = freqToList.get(minFreq);
            Node toRemove = minFreqList.removeTail();
            keyToNode.remove(toRemove.key);
        }

        Node newNode = new Node(key, value);
        keyToNode.put(key, newNode);
        freqToList.computeIfAbsent(1, k -> new DoublyLinkedList()).addToHead(newNode);
        minFreq = 1;
    }

    private void updateFreq(Node node) {
        int oldFreq = node.freq;
        int newFreq = oldFreq + 1;

        // Remove from old frequency list
        freqToList.get(oldFreq).remove(node);

        // Update minFreq if necessary
        if (oldFreq == minFreq && freqToList.get(oldFreq).isEmpty()) {
            minFreq++;
        }

        // Add to new frequency list
        node.freq = newFreq;
        freqToList.computeIfAbsent(newFreq, k -> new DoublyLinkedList()).addToHead(node);
    }

    public static void main(String[] args) {
        LFUCache lfu = new LFUCache(2);
        lfu.put(1, 1);
        lfu.put(2, 2);
        System.out.println(lfu.get(1)); // Expected: 1
        lfu.put(3, 3);
        System.out.println(lfu.get(2)); // Expected: -1
        System.out.println(lfu.get(3)); // Expected: 3
        lfu.put(4, 4);
        System.out.println(lfu.get(1)); // Expected: -1
        System.out.println(lfu.get(3)); // Expected: 3
        System.out.println(lfu.get(4)); // Expected: 4
    }
}
