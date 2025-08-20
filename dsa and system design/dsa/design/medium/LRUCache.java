package design.medium;

import java.util.*;

/**
 * LeetCode 146: LRU Cache
 * https://leetcode.com/problems/lru-cache/
 *
 * Description: Design a data structure that follows the constraints of a Least
 * Recently Used (LRU) cache.
 * 
 * Constraints:
 * - 1 <= capacity <= 3000
 * - 0 <= key <= 10^4
 * - 0 <= value <= 10^5
 * - At most 2 * 10^5 calls will be made to get and put
 *
 * Follow-up:
 * - Can you do both operations in O(1) time complexity?
 * 
 * Time Complexity: O(1) for both get and put
 * Space Complexity: O(capacity)
 * 
 * Company Tags: Google, Facebook, Amazon, Microsoft, Apple
 */
public class LRUCache {

    class DLinkedNode {
        int key;
        int value;
        DLinkedNode prev;
        DLinkedNode next;

        DLinkedNode() {
        }

        DLinkedNode(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private Map<Integer, DLinkedNode> cache;
    private int capacity;
    private DLinkedNode head, tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();

        // Create dummy head and tail nodes
        head = new DLinkedNode();
        tail = new DLinkedNode();
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        DLinkedNode node = cache.get(key);
        if (node == null) {
            return -1;
        }

        // Move to head (mark as recently used)
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        DLinkedNode node = cache.get(key);

        if (node == null) {
            DLinkedNode newNode = new DLinkedNode(key, value);

            if (cache.size() >= capacity) {
                // Remove least recently used node
                DLinkedNode last = removeTail();
                cache.remove(last.key);
            }

            cache.put(key, newNode);
            addToHead(newNode);
        } else {
            // Update existing node
            node.value = value;
            moveToHead(node);
        }
    }

    private void addToHead(DLinkedNode node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(DLinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(DLinkedNode node) {
        removeNode(node);
        addToHead(node);
    }

    private DLinkedNode removeTail() {
        DLinkedNode last = tail.prev;
        removeNode(last);
        return last;
    }

    public static void main(String[] args) {
        LRUCache lru = new LRUCache(2);
        lru.put(1, 1);
        lru.put(2, 2);
        System.out.println(lru.get(1)); // Expected: 1
        lru.put(3, 3);
        System.out.println(lru.get(2)); // Expected: -1
        lru.put(4, 4);
        System.out.println(lru.get(1)); // Expected: -1
        System.out.println(lru.get(3)); // Expected: 3
        System.out.println(lru.get(4)); // Expected: 4
    }
}
