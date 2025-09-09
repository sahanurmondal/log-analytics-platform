package design.easy;

/**
 * LeetCode 705: Design HashSet
 * https://leetcode.com/problems/design-hashset/
 *
 * Description: Design a HashSet without using any built-in hash table
 * libraries.
 * 
 * Constraints:
 * - 0 <= key <= 10^6
 * - At most 10^4 calls will be made to add, remove, and contains
 *
 * Follow-up:
 * - Can you solve the problem without using the built-in HashSet library?
 * 
 * Time Complexity: O(1) average, O(n) worst case
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class MyHashSet {

    private static final int BUCKET_SIZE = 1000;
    private java.util.List<Integer>[] buckets;

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        buckets = new java.util.List[BUCKET_SIZE];
        for (int i = 0; i < BUCKET_SIZE; i++) {
            buckets[i] = new java.util.ArrayList<>();
        }
    }

    private int hash(int key) {
        return key % BUCKET_SIZE;
    }

    public void add(int key) {
        int index = hash(key);
        if (!buckets[index].contains(key)) {
            buckets[index].add(key);
        }
    }

    public void remove(int key) {
        int index = hash(key);
        buckets[index].remove(Integer.valueOf(key));
    }

    public boolean contains(int key) {
        int index = hash(key);
        return buckets[index].contains(key);
    }

    // Alternative implementation - Array-based
    static class MyHashSetArray {
        private boolean[] set;

        public MyHashSetArray() {
            set = new boolean[1000001]; // 0 <= key <= 10^6
        }

        public void add(int key) {
            set[key] = true;
        }

        public void remove(int key) {
            set[key] = false;
        }

        public boolean contains(int key) {
            return set[key];
        }
    }

    public static void main(String[] args) {
        MyHashSet hashSet = new MyHashSet();
        hashSet.add(1);
        hashSet.add(2);
        System.out.println(hashSet.contains(1)); // Expected: true
        System.out.println(hashSet.contains(3)); // Expected: false
        hashSet.add(2);
        System.out.println(hashSet.contains(2)); // Expected: true
        hashSet.remove(2);
        System.out.println(hashSet.contains(2)); // Expected: false
    }
}
