package design.easy;

/**
 * LeetCode 706: Design HashMap
 * https://leetcode.com/problems/design-hashmap/
 *
 * Description: Design a HashMap without using any built-in hash table
 * libraries.
 * 
 * Constraints:
 * - 0 <= key, value <= 10^6
 * - At most 10^4 calls will be made to put, get, and remove
 *
 * Follow-up:
 * - Can you solve the problem without using the built-in HashMap library?
 * 
 * Time Complexity: O(1) average, O(n) worst case
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook, Amazon
 */
public class MyHashMap {

    private static final int BUCKET_SIZE = 1000;
    private java.util.List<Entry>[] buckets;

    class Entry {
        int key;
        int value;

        Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        buckets = new java.util.List[BUCKET_SIZE];
        for (int i = 0; i < BUCKET_SIZE; i++) {
            buckets[i] = new java.util.ArrayList<>();
        }
    }

    private int hash(int key) {
        return key % BUCKET_SIZE;
    }

    public void put(int key, int value) {
        int index = hash(key);
        for (Entry entry : buckets[index]) {
            if (entry.key == key) {
                entry.value = value;
                return;
            }
        }
        buckets[index].add(new Entry(key, value));
    }

    public int get(int key) {
        int index = hash(key);
        for (Entry entry : buckets[index]) {
            if (entry.key == key) {
                return entry.value;
            }
        }
        return -1;
    }

    public void remove(int key) {
        int index = hash(key);
        buckets[index].removeIf(entry -> entry.key == key);
    }

    public static void main(String[] args) {
        MyHashMap hashMap = new MyHashMap();
        hashMap.put(1, 1);
        hashMap.put(2, 2);
        System.out.println(hashMap.get(1)); // Expected: 1
        System.out.println(hashMap.get(3)); // Expected: -1
        hashMap.put(2, 1);
        System.out.println(hashMap.get(2)); // Expected: 1
        hashMap.remove(2);
        System.out.println(hashMap.get(2)); // Expected: -1
    }
}
