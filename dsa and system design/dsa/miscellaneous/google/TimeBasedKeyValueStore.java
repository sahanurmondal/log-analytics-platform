package miscellaneous.google;

import java.util.*;

/**
 * LeetCode 981: Time Based Key-Value Store
 * https://leetcode.com/problems/time-based-key-value-store/
 *
 * Description:
 * Design a time-based key-value data structure that can store multiple values
 * for the same key
 * at different time stamps and retrieve the key's value at a certain timestamp.
 * 
 * Company: Google
 * Difficulty: Medium
 * Asked: Multiple times in 2022-2023
 * 
 * Constraints:
 * - 1 <= key.length, value.length <= 100
 * - key and value consist of lowercase English letters and digits
 * - 1 <= timestamp <= 10^7
 * - All the timestamps of set are strictly increasing
 * - At most 2 * 10^5 calls will be made to set and get
 */
public class TimeBasedKeyValueStore {

    private Map<String, TreeMap<Integer, String>> map;

    public TimeBasedKeyValueStore() {
        map = new HashMap<>();
    }

    public void set(String key, String value, int timestamp) {
        map.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
    }

    public String get(String key, int timestamp) {
        if (!map.containsKey(key))
            return "";

        TreeMap<Integer, String> timeMap = map.get(key);
        Integer floorKey = timeMap.floorKey(timestamp);
        return floorKey == null ? "" : timeMap.get(floorKey);
    }

    public static void main(String[] args) {
        TimeBasedKeyValueStore timeMap = new TimeBasedKeyValueStore();

        timeMap.set("foo", "bar", 1);
        System.out.println(timeMap.get("foo", 1)); // "bar"
        System.out.println(timeMap.get("foo", 3)); // "bar"

        timeMap.set("foo", "bar2", 4);
        System.out.println(timeMap.get("foo", 4)); // "bar2"
        System.out.println(timeMap.get("foo", 5)); // "bar2"
    }
}
