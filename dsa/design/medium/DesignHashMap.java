package design.medium;

/**
 * LeetCode 706: Design HashMap
 * https://leetcode.com/problems/design-hashmap/
 * 
 * Problem:
 * Design a HashMap without using any built-in hash table libraries.
 * Implement the MyHashMap class:
 * - MyHashMap() initializes the object with an empty map.
 * - void put(int key, int value) inserts a (key, value) pair into the HashMap.
 * If the key already exists, update the corresponding value.
 * - int get(int key) returns the value to which the specified key is mapped, or
 * -1 if this map contains no mapping for the key.
 * - void remove(int key) removes the key and its corresponding value if the map
 * contains the mapping for the key.
 * 
 * Example:
 * Input
 * ["MyHashMap", "put", "put", "get", "get", "put", "get", "remove", "get"]
 * [[], [1, 1], [2, 2], [1], [3], [2, 1], [2], [2], [2]]
 * Output
 * [null, null, null, 1, -1, null, 1, null, -1]
 * 
 * Explanation:
 * MyHashMap myHashMap = new MyHashMap();
 * myHashMap.put(1, 1); // The map is now [[1,1]]
 * myHashMap.put(2, 2); // The map is now [[1,1], [2,2]]
 * myHashMap.get(1); // return 1, The map is now [[1,1], [2,2]]
 * myHashMap.get(3); // return -1 (i.e., not found), The map is now [[1,1],
 * [2,2]]
 * myHashMap.put(2, 1); // The map is now [[1,1], [2,1]] (i.e., update the
 * existing value)
 * myHashMap.get(2); // return 1, The map is now [[1,1], [2,1]]
 * myHashMap.remove(2); // remove the mapping for 2, The map is now [[1,1]]
 * myHashMap.get(2); // return -1 (i.e., not found), The map is now [[1,1]]
 * 
 * Constraints:
 * 0 <= key, value <= 10^6
 * At most 10^4 calls will be made to put, get, and remove.
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Frequency: High
 */
public class DesignHashMap {

    // Approach 1: Array-based implementation (simple but memory intensive)
    private static final int SIZE = 1000001; // Max key value + 1
    private int[] map;

    /** Initialize your data structure here. */
    public DesignHashMap() {
        map = new int[SIZE];
        for (int i = 0; i < SIZE; i++) {
            map[i] = -1; // -1 indicates no mapping
        }
    }

    /** value will always be non-negative. */
    public void put(int key, int value) {
        map[key] = value;
    }

    /**
     * Returns the value to which the specified key is mapped, or -1 if this map
     * contains no mapping for the key
     */
    public int get(int key) {
        return map[key];
    }

    /**
     * Removes the mapping of the specified value key if this map contains a mapping
     * for the key
     */
    public void remove(int key) {
        map[key] = -1;
    }

    // Approach 2: Hash table with chaining (more memory efficient)
    public static class MyHashMapChaining {

        // Node class for linked list
        class Node {
            int key, value;
            Node next;

            Node(int key, int value) {
                this.key = key;
                this.value = value;
                this.next = null;
            }
        }

        private static final int SIZE = 1000; // Number of buckets
        private Node[] buckets;

        /** Initialize your data structure here. */
        public MyHashMapChaining() {
            buckets = new Node[SIZE];
        }

        private int hash(int key) {
            return key % SIZE;
        }

        /** value will always be non-negative. */
        public void put(int key, int value) {
            int index = hash(key);

            if (buckets[index] == null) {
                buckets[index] = new Node(key, value);
                return;
            }

            Node curr = buckets[index];
            while (curr != null) {
                if (curr.key == key) {
                    curr.value = value; // Update existing key
                    return;
                }
                if (curr.next == null)
                    break;
                curr = curr.next;
            }

            curr.next = new Node(key, value); // Add new key
        }

        /**
         * Returns the value to which the specified key is mapped, or -1 if this map
         * contains no mapping for the key
         */
        public int get(int key) {
            int index = hash(key);
            Node curr = buckets[index];

            while (curr != null) {
                if (curr.key == key) {
                    return curr.value;
                }
                curr = curr.next;
            }

            return -1;
        }

        /**
         * Removes the mapping of the specified value key if this map contains a mapping
         * for the key
         */
        public void remove(int key) {
            int index = hash(key);

            if (buckets[index] == null)
                return;

            if (buckets[index].key == key) {
                buckets[index] = buckets[index].next;
                return;
            }

            Node curr = buckets[index];
            while (curr.next != null) {
                if (curr.next.key == key) {
                    curr.next = curr.next.next;
                    return;
                }
                curr = curr.next;
            }
        }
    }

    // Approach 3: Open addressing with linear probing
    public static class MyHashMapOpenAddressing {

        private static final int SIZE = 2000;
        private static final int DELETED = -2;
        private static final int EMPTY = -1;

        private int[] keys;
        private int[] values;

        /** Initialize your data structure here. */
        public MyHashMapOpenAddressing() {
            keys = new int[SIZE];
            values = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                values[i] = EMPTY;
            }
        }

        private int hash(int key) {
            return key % SIZE;
        }

        /** value will always be non-negative. */
        public void put(int key, int value) {
            int index = hash(key);

            while (values[index] != EMPTY && values[index] != DELETED && keys[index] != key) {
                index = (index + 1) % SIZE;
            }

            keys[index] = key;
            values[index] = value;
        }

        /**
         * Returns the value to which the specified key is mapped, or -1 if this map
         * contains no mapping for the key
         */
        public int get(int key) {
            int index = hash(key);

            while (values[index] != EMPTY) {
                if (values[index] != DELETED && keys[index] == key) {
                    return values[index];
                }
                index = (index + 1) % SIZE;
            }

            return -1;
        }

        /**
         * Removes the mapping of the specified value key if this map contains a mapping
         * for the key
         */
        public void remove(int key) {
            int index = hash(key);

            while (values[index] != EMPTY) {
                if (values[index] != DELETED && keys[index] == key) {
                    values[index] = DELETED;
                    return;
                }
                index = (index + 1) % SIZE;
            }
        }
    }

    /**
     * Follow-up: Dynamic resizing hash map
     */
    public static class MyHashMapDynamic {

        class Node {
            int key, value;
            Node next;

            Node(int key, int value) {
                this.key = key;
                this.value = value;
            }
        }

        private Node[] buckets;
        private int size;
        private int capacity;
        private static final double LOAD_FACTOR = 0.75;

        public MyHashMapDynamic() {
            capacity = 16;
            buckets = new Node[capacity];
            size = 0;
        }

        private int hash(int key) {
            return key % capacity;
        }

        private void resize() {
            Node[] oldBuckets = buckets;
            capacity *= 2;
            buckets = new Node[capacity];
            size = 0;

            for (Node head : oldBuckets) {
                while (head != null) {
                    put(head.key, head.value);
                    head = head.next;
                }
            }
        }

        public void put(int key, int value) {
            if (size >= capacity * LOAD_FACTOR) {
                resize();
            }

            int index = hash(key);

            if (buckets[index] == null) {
                buckets[index] = new Node(key, value);
                size++;
                return;
            }

            Node curr = buckets[index];
            while (curr != null) {
                if (curr.key == key) {
                    curr.value = value;
                    return;
                }
                if (curr.next == null)
                    break;
                curr = curr.next;
            }

            curr.next = new Node(key, value);
            size++;
        }

        public int get(int key) {
            int index = hash(key);
            Node curr = buckets[index];

            while (curr != null) {
                if (curr.key == key) {
                    return curr.value;
                }
                curr = curr.next;
            }

            return -1;
        }

        public void remove(int key) {
            int index = hash(key);

            if (buckets[index] == null)
                return;

            if (buckets[index].key == key) {
                buckets[index] = buckets[index].next;
                size--;
                return;
            }

            Node curr = buckets[index];
            while (curr.next != null) {
                if (curr.next.key == key) {
                    curr.next = curr.next.next;
                    size--;
                    return;
                }
                curr = curr.next;
            }
        }
    }

    public static void main(String[] args) {
        // Test basic implementation
        DesignHashMap myHashMap = new DesignHashMap();

        myHashMap.put(1, 1);
        myHashMap.put(2, 2);
        System.out.println("get(1): " + myHashMap.get(1)); // 1
        System.out.println("get(3): " + myHashMap.get(3)); // -1
        myHashMap.put(2, 1);
        System.out.println("get(2): " + myHashMap.get(2)); // 1
        myHashMap.remove(2);
        System.out.println("get(2): " + myHashMap.get(2)); // -1

        // Test chaining implementation
        System.out.println("\nTesting chaining implementation:");
        MyHashMapChaining chainingMap = new MyHashMapChaining();
        chainingMap.put(1, 1);
        chainingMap.put(2, 2);
        System.out.println("get(1): " + chainingMap.get(1)); // 1
        System.out.println("get(3): " + chainingMap.get(3)); // -1
        chainingMap.put(2, 1);
        System.out.println("get(2): " + chainingMap.get(2)); // 1
        chainingMap.remove(2);
        System.out.println("get(2): " + chainingMap.get(2)); // -1

        System.out.println("\nAll test cases completed successfully!");
    }
}
