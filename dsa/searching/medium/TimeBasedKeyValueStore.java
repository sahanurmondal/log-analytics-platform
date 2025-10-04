package searching.medium;

import java.util.*;

/**
 * LeetCode 981: Time Based Key-Value Store
 * https://leetcode.com/problems/time-based-key-value-store/
 *
 * Description:
 * Design a time-based key-value data structure that can store multiple values
 * for the same key at different time stamps and retrieve the key's value at a
 * certain timestamp.
 *
 * Constraints:
 * - 1 <= key.length, value.length <= 100
 * - 1 <= timestamp <= 10^7
 * - All the timestamps are strictly increasing
 * - At most 2 * 10^5 calls will be made to set and get
 *
 * Follow-ups:
 * 1. Can you optimize for large number of queries?
 * 2. Can you support range queries?
 * 3. Can you support deletion?
 */
public class TimeBasedKeyValueStore {
    private Map<String, TreeMap<Integer, String>> map = new HashMap<>();

    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        if (!map.containsKey(key)) return "";
        TreeMap<Integer, String> tree = map.get(key);
        Map.Entry<Integer, String> entry = tree.floorEntry(timestamp);
        return entry == null ? "" : entry.getValue();
    }

    // Follow-up 1: Optimize for large queries (already O(log n) per query)
    // Follow-up 2: Range queries
    public List<String> getRange(String key, int start, int end) {
        List<String> res = new ArrayList<>();
        if (!map.containsKey(key)) return res;
        TreeMap<Integer, String> tree = map.get(key);
        for (String v : tree.subMap(start, true, end, true).values()) res.add(v);
        return res;
    }

    // Follow-up 3: Support deletion
    public void delete(String key, int timestamp) {
        if (map.containsKey(key)) map.get(key).remove(timestamp);
    }

    public static void main(String[] args) {
        TimeBasedKeyValueStore store = new TimeBasedKeyValueStore();
        store.set("foo", "bar", 1);
        System.out.println("Get foo@1: " + store.get("foo", 1)); // bar
        System.out.println("Get foo@3: " + store.get("foo", 3)); // bar
        store.set("foo", "bar2", 4);
        System.out.println("Get foo@4: " + store.get("foo", 4)); // bar2
        System.out.println("Get foo@5: " + store.get("foo", 5)); // bar2
        System.out.println("Get foo@0: " + store.get("foo", 0)); // ""
        System.out.println("Range foo@1-4: " + store.getRange("foo", 1, 4)); // [bar, bar2]
        store.delete("foo", 4);
        System.out.println("After delete foo@4: " + store.get("foo", 5)); // bar
    }
}
