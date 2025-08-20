package design;

import java.util.*;

/**
 * LeetCode 146: LRU Cache
 *
 * Description:
 * Design a data structure that follows the constraints of a Least Recently Used
 * (LRU) cache.
 *
 * Input: int capacity, get(int key), put(int key, int value)
 * Output: int (for get)
 *
 * Constraints:
 * - 1 <= capacity <= 3000
 * - 0 <= key, value <= 10^4
 *
 * Solution Approaches:
 * 1. Doubly Linked List + HashMap (O(1) time for get/put, O(capacity) space)
 * Steps:
 * a. Use a doubly linked list to track usage order.
 * b. Use a hashmap for O(1) access.
 * Time: O(1) for get/put.
 * Space: O(capacity).
 * - Example: put(1,1), put(2,2), get(1), put(3,3), get(2) → -1
 * 2. LinkedHashMap (Java built-in) (O(1) time for get/put, O(capacity) space)
 * Steps:
 * a. Extend LinkedHashMap and override removeEldestEntry.
 * Time: O(1)
 * Space: O(capacity).
 * 
 * Time Complexity: O(1) for both get and put operations
 * Space Complexity: O(capacity)
 * 
 * Company Tags: Amazon, Google, Microsoft, Facebook, Apple
 */
public class LRUCache {

    // Node class for doubly linked list
    private static class Node {
        int key;
        int value;
        Node prev;
        Node next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head; // dummy head (most recently used)
    private final Node tail; // dummy tail (least recently used)
    private int size;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.size = 0;

        // Create dummy head and tail nodes
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }

        // Move to head (mark as most recently used)
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);

        if (node != null) {
            // Key exists, update value and move to head
            node.value = value;
            moveToHead(node);
        } else {
            // New key
            Node newNode = new Node(key, value);

            if (size >= capacity) {
                // Remove least recently used item
                Node tail = removeTail();
                cache.remove(tail.key);
                size--;
            }

            // Add new node to head
            addToHead(newNode);
            cache.put(key, newNode);
            size++;
        }
    }

    // Helper methods for doubly linked list operations

    private void addToHead(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private Node removeTail() {
        Node lastNode = tail.prev;
        removeNode(lastNode);
        return lastNode;
    }

    // Additional utility methods for debugging and testing

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public void clear() {
        cache.clear();
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    // Get all keys in order from most to least recently used
    public List<Integer> getKeysInOrder() {
        List<Integer> keys = new ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            keys.add(current.key);
            current = current.next;
        }
        return keys;
    }

    @Override
    public String toString() {
        List<Integer> keys = getKeysInOrder();
        return String.format("LRUCache[capacity=%d, size=%d, keys=%s]",
                capacity, size, keys);
    }

    public static void main(String[] args) {
        System.out.println("=== Basic LRU Cache Tests ===");

        // Test basic functionality
        LRUCache lru = new LRUCache(2);
        System.out.println("Initial cache: " + lru);

        System.out.println("\nTesting basic operations:");
        lru.put(1, 1);
        lru.put(2, 2);
        System.out.println("After put(1,1), put(2,2): " + lru);
        System.out.println("get(1): " + lru.get(1)); // returns 1
        System.out.println("Cache after get(1): " + lru);

        lru.put(3, 3); // evicts key 2
        System.out.println("After put(3,3): " + lru);
        System.out.println("get(2): " + lru.get(2)); // returns -1 (not found)
        System.out.println("get(3): " + lru.get(3)); // returns 3
        System.out.println("get(1): " + lru.get(1)); // returns 1

        lru.put(4, 4); // evicts key 3
        System.out.println("After put(4,4): " + lru);
        System.out.println("get(1): " + lru.get(1)); // returns 1
        System.out.println("get(3): " + lru.get(3)); // returns -1 (not found)
        System.out.println("get(4): " + lru.get(4)); // returns 4

        System.out.println("\n=== Edge Case Tests ===");

        // Edge Case 1: Capacity 1
        System.out.println("Testing capacity 1:");
        LRUCache lru1 = new LRUCache(1);
        lru1.put(1, 1);
        lru1.put(2, 2); // evicts 1
        System.out.println("get(1): " + lru1.get(1)); // -1
        System.out.println("get(2): " + lru1.get(2)); // 2

        // Edge Case 2: Overwrite value
        System.out.println("\nTesting value overwrite:");
        LRUCache lru2 = new LRUCache(2);
        lru2.put(1, 1);
        lru2.put(1, 10); // overwrite
        System.out.println("get(1): " + lru2.get(1)); // 10
        System.out.println("Cache: " + lru2);

        // Edge Case 3: Get non-existent key
        System.out.println("\nTesting non-existent key:");
        LRUCache lru3 = new LRUCache(2);
        System.out.println("get(999): " + lru3.get(999)); // -1

        // Edge Case 4: All gets
        System.out.println("\nTesting all gets (empty cache):");
        LRUCache lru4 = new LRUCache(2);
        for (int i = 1; i <= 5; i++) {
            System.out.println("get(" + i + "): " + lru4.get(i)); // all -1
        }

        // Edge Case 5: All puts
        System.out.println("\nTesting all puts:");
        LRUCache lru5 = new LRUCache(3);
        for (int i = 1; i <= 5; i++) {
            lru5.put(i, i * 10);
            System.out.println("After put(" + i + "," + (i * 10) + "): " + lru5);
        }

        // Edge Case 6: Repeated operations
        System.out.println("\nTesting repeated operations:");
        LRUCache lru6 = new LRUCache(2);
        lru6.put(1, 1);
        lru6.put(1, 1); // same key-value
        lru6.put(1, 2); // same key, different value
        System.out.println("get(1): " + lru6.get(1)); // 2
        System.out.println("Cache: " + lru6);

        // Edge Case 7: Zero and negative values (within constraints)
        System.out.println("\nTesting zero values:");
        LRUCache lru7 = new LRUCache(2);
        lru7.put(0, 0);
        lru7.put(1, 0);
        System.out.println("get(0): " + lru7.get(0)); // 0
        System.out.println("get(1): " + lru7.get(1)); // 0
        System.out.println("Cache: " + lru7);

        System.out.println("\n=== Complex Usage Pattern Test ===");
        LRUCache complexCache = new LRUCache(3);

        // Complex sequence
        complexCache.put(1, 1);
        complexCache.put(2, 2);
        complexCache.put(3, 3);
        System.out.println("After initial puts: " + complexCache);

        complexCache.get(1); // move 1 to front
        System.out.println("After get(1): " + complexCache);

        complexCache.put(4, 4); // evict 2 (least recently used)
        System.out.println("After put(4,4): " + complexCache);

        complexCache.get(2); // should be -1
        System.out.println("get(2): " + complexCache.get(2));

        complexCache.get(3); // move 3 to front
        complexCache.get(4); // move 4 to front
        complexCache.put(5, 5); // evict 1
        System.out.println("Final cache: " + complexCache);

        System.out.println("\n=== Performance Test ===");
        LRUCache perfCache = new LRUCache(1000);
        long startTime = System.currentTimeMillis();

        // Performance test: 10k operations
        for (int i = 0; i < 5000; i++) {
            perfCache.put(i, i * 2);
        }

        for (int i = 0; i < 5000; i++) {
            perfCache.get(i % 1000);
        }

        long endTime = System.currentTimeMillis();
        System.out.println("10k operations completed in " + (endTime - startTime) + "ms");
        System.out.println("Final cache size: " + perfCache.size());
        System.out.println("Is full: " + perfCache.isFull());
    }
}
