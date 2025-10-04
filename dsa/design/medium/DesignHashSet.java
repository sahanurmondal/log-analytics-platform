package design.medium;

/**
 * LeetCode 705: Design HashSet
 * https://leetcode.com/problems/design-hashset/
 * 
 * Problem:
 * Design a HashSet without using any built-in hash table libraries.
 * Implement MyHashSet class:
 * - void add(key) Inserts the value key into the HashSet.
 * - bool contains(key) Returns whether the value key exists in the HashSet or
 * not.
 * - void remove(key) Removes the value key in the HashSet. If key does not
 * exist in the HashSet, do nothing.
 * 
 * Example:
 * Input
 * ["MyHashSet", "add", "add", "contains", "contains", "add", "contains",
 * "remove", "contains"]
 * [[], [1], [2], [1], [3], [2], [2], [2], [2]]
 * Output
 * [null, null, null, true, false, null, true, null, false]
 * 
 * Explanation:
 * MyHashSet myHashSet = new MyHashSet();
 * myHashSet.add(1); // set = [1]
 * myHashSet.add(2); // set = [1, 2]
 * myHashSet.contains(1); // return True
 * myHashSet.contains(3); // return False, (not found)
 * myHashSet.add(2); // set = [1, 2]
 * myHashSet.contains(2); // return True
 * myHashSet.remove(2); // set = [1]
 * myHashSet.contains(2); // return False, (already removed)
 * 
 * Constraints:
 * 0 <= key <= 10^6
 * At most 10^4 calls will be made to add, remove, and contains.
 * 
 * Company Tags: Amazon, Google, Microsoft, Meta, Apple
 * Frequency: High
 */
public class DesignHashSet {

    // Approach 1: Boolean array (simple but memory intensive)
    private static final int SIZE = 1000001; // Max key value + 1
    private boolean[] set;

    /** Initialize your data structure here. */
    public DesignHashSet() {
        set = new boolean[SIZE];
    }

    public void add(int key) {
        set[key] = true;
    }

    public void remove(int key) {
        set[key] = false;
    }

    /** Returns true if this set contains the specified element */
    public boolean contains(int key) {
        return set[key];
    }

    // Approach 2: Hash table with chaining (more memory efficient)
    public static class MyHashSetChaining {

        // Node class for linked list
        class Node {
            int key;
            Node next;

            Node(int key) {
                this.key = key;
                this.next = null;
            }
        }

        private static final int SIZE = 1000; // Number of buckets
        private Node[] buckets;

        /** Initialize your data structure here. */
        public MyHashSetChaining() {
            buckets = new Node[SIZE];
        }

        private int hash(int key) {
            return key % SIZE;
        }

        public void add(int key) {
            int index = hash(key);

            if (buckets[index] == null) {
                buckets[index] = new Node(key);
                return;
            }

            Node curr = buckets[index];
            while (curr != null) {
                if (curr.key == key) {
                    return; // Key already exists
                }
                if (curr.next == null)
                    break;
                curr = curr.next;
            }

            curr.next = new Node(key); // Add new key
        }

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

        /** Returns true if this set contains the specified element */
        public boolean contains(int key) {
            int index = hash(key);
            Node curr = buckets[index];

            while (curr != null) {
                if (curr.key == key) {
                    return true;
                }
                curr = curr.next;
            }

            return false;
        }
    }

    // Approach 3: Open addressing with linear probing
    public static class MyHashSetOpenAddressing {

        private static final int SIZE = 2000;
        private static final int DELETED = -1;
        private static final int EMPTY = -2;

        private int[] keys;

        /** Initialize your data structure here. */
        public MyHashSetOpenAddressing() {
            keys = new int[SIZE];
            for (int i = 0; i < SIZE; i++) {
                keys[i] = EMPTY;
            }
        }

        private int hash(int key) {
            return key % SIZE;
        }

        public void add(int key) {
            int index = hash(key);

            while (keys[index] != EMPTY && keys[index] != DELETED && keys[index] != key) {
                index = (index + 1) % SIZE;
            }

            keys[index] = key;
        }

        public void remove(int key) {
            int index = hash(key);

            while (keys[index] != EMPTY) {
                if (keys[index] == key) {
                    keys[index] = DELETED;
                    return;
                }
                index = (index + 1) % SIZE;
            }
        }

        /** Returns true if this set contains the specified element */
        public boolean contains(int key) {
            int index = hash(key);

            while (keys[index] != EMPTY) {
                if (keys[index] == key) {
                    return true;
                }
                index = (index + 1) % SIZE;
            }

            return false;
        }
    }

    // Approach 4: 2D bucketing approach (space-efficient for sparse data)
    public static class MyHashSetBucketing {

        private static final int BUCKET_SIZE = 1000;
        private static final int BUCKET_COUNT = 1001;

        private boolean[][] buckets;

        /** Initialize your data structure here. */
        public MyHashSetBucketing() {
            buckets = new boolean[BUCKET_COUNT][];
        }

        private int getBucket(int key) {
            return key / BUCKET_SIZE;
        }

        private int getPos(int key) {
            return key % BUCKET_SIZE;
        }

        public void add(int key) {
            int bucket = getBucket(key);
            int pos = getPos(key);

            if (buckets[bucket] == null) {
                buckets[bucket] = new boolean[BUCKET_SIZE];
            }

            buckets[bucket][pos] = true;
        }

        public void remove(int key) {
            int bucket = getBucket(key);
            int pos = getPos(key);

            if (buckets[bucket] != null) {
                buckets[bucket][pos] = false;
            }
        }

        /** Returns true if this set contains the specified element */
        public boolean contains(int key) {
            int bucket = getBucket(key);
            int pos = getPos(key);

            return buckets[bucket] != null && buckets[bucket][pos];
        }
    }

    /**
     * Follow-up: Dynamic resizing hash set
     */
    public static class MyHashSetDynamic {

        class Node {
            int key;
            Node next;

            Node(int key) {
                this.key = key;
            }
        }

        private Node[] buckets;
        private int size;
        private int capacity;
        private static final double LOAD_FACTOR = 0.75;

        public MyHashSetDynamic() {
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
                    add(head.key);
                    head = head.next;
                }
            }
        }

        public void add(int key) {
            if (contains(key))
                return;

            if (size >= capacity * LOAD_FACTOR) {
                resize();
            }

            int index = hash(key);

            if (buckets[index] == null) {
                buckets[index] = new Node(key);
            } else {
                Node newNode = new Node(key);
                newNode.next = buckets[index];
                buckets[index] = newNode;
            }

            size++;
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

        public boolean contains(int key) {
            int index = hash(key);
            Node curr = buckets[index];

            while (curr != null) {
                if (curr.key == key) {
                    return true;
                }
                curr = curr.next;
            }

            return false;
        }
    }

    public static void main(String[] args) {
        // Test basic implementation
        DesignHashSet myHashSet = new DesignHashSet();

        myHashSet.add(1);
        myHashSet.add(2);
        System.out.println("contains(1): " + myHashSet.contains(1)); // true
        System.out.println("contains(3): " + myHashSet.contains(3)); // false
        myHashSet.add(2);
        System.out.println("contains(2): " + myHashSet.contains(2)); // true
        myHashSet.remove(2);
        System.out.println("contains(2): " + myHashSet.contains(2)); // false

        // Test chaining implementation
        System.out.println("\nTesting chaining implementation:");
        MyHashSetChaining chainingSet = new MyHashSetChaining();
        chainingSet.add(1);
        chainingSet.add(2);
        System.out.println("contains(1): " + chainingSet.contains(1)); // true
        System.out.println("contains(3): " + chainingSet.contains(3)); // false
        chainingSet.add(2);
        System.out.println("contains(2): " + chainingSet.contains(2)); // true
        chainingSet.remove(2);
        System.out.println("contains(2): " + chainingSet.contains(2)); // false

        // Test bucketing implementation
        System.out.println("\nTesting bucketing implementation:");
        MyHashSetBucketing bucketingSet = new MyHashSetBucketing();
        bucketingSet.add(1);
        bucketingSet.add(2);
        System.out.println("contains(1): " + bucketingSet.contains(1)); // true
        System.out.println("contains(3): " + bucketingSet.contains(3)); // false
        bucketingSet.add(2);
        System.out.println("contains(2): " + bucketingSet.contains(2)); // true
        bucketingSet.remove(2);
        System.out.println("contains(2): " + bucketingSet.contains(2)); // false

        System.out.println("\nAll test cases completed successfully!");
    }
}
