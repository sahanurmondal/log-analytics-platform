package binarysearch.medium;

import java.util.*;

/**
 * LeetCode 981: Time Based Key-Value Store
 * https://leetcode.com/problems/time-based-key-value-store/
 *
 * Description:
 * Design a time-based key-value data structure that can store multiple values
 * for the same key at different time stamps
 * and retrieve the key's value at a certain timestamp.
 * 
 * Implement the TimeMap class:
 * - TimeMap() Initializes the object of the data structure.
 * - void set(String key, String value, int timestamp) Stores the key with the
 * value at the given time timestamp.
 * - String get(String key, int timestamp) Returns a value such that set was
 * called previously,
 * with timestamp_prev <= timestamp. If there are multiple such values, it
 * returns the value associated with the largest timestamp_prev.
 * If there are no values, it returns "".
 *
 * Companies: Google, Microsoft, Amazon, Facebook, Apple, Bloomberg, Uber,
 * Airbnb, DoorDash
 * Difficulty: Medium
 * Asked: 2023-2024 (Very High Frequency)
 *
 * Constraints:
 * - 1 <= key.length, value.length <= 100
 * - key and value consist of lowercase English letters and digits.
 * - 1 <= timestamp <= 10^7
 * - All the timestamps timestamp of set are strictly increasing.
 *
 * Follow-ups:
 * - What if timestamps are not strictly increasing?
 * - How to handle concurrent access?
 * - How to optimize for memory usage?
 */
public class TimeBasedKeyValueStore {

    // Binary search approach with HashMap and TreeMap
    class TimeMap {
        private Map<String, TreeMap<Integer, String>> keyTimeMap;

        public TimeMap() {
            keyTimeMap = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            keyTimeMap.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
        }

        public String get(String key, int timestamp) {
            if (!keyTimeMap.containsKey(key)) {
                return "";
            }

            TreeMap<Integer, String> timeMap = keyTimeMap.get(key);
            Integer floorKey = timeMap.floorKey(timestamp);

            return floorKey != null ? timeMap.get(floorKey) : "";
        }
    }

    // Binary search approach with HashMap and ArrayList (more efficient)
    class TimeMapOptimized {
        private Map<String, List<Pair>> keyTimeMap;

        class Pair {
            int timestamp;
            String value;

            Pair(int timestamp, String value) {
                this.timestamp = timestamp;
                this.value = value;
            }
        }

        public TimeMapOptimized() {
            keyTimeMap = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            keyTimeMap.computeIfAbsent(key, k -> new ArrayList<>()).add(new Pair(timestamp, value));
        }

        public String get(String key, int timestamp) {
            if (!keyTimeMap.containsKey(key)) {
                return "";
            }

            List<Pair> pairs = keyTimeMap.get(key);
            return binarySearch(pairs, timestamp);
        }

        private String binarySearch(List<Pair> pairs, int timestamp) {
            int left = 0, right = pairs.size() - 1;
            String result = "";

            while (left <= right) {
                int mid = left + (right - left) / 2;

                if (pairs.get(mid).timestamp <= timestamp) {
                    result = pairs.get(mid).value;
                    left = mid + 1; // Look for larger timestamp
                } else {
                    right = mid - 1;
                }
            }

            return result;
        }
    }

    // Alternative binary search implementation
    class TimeMapAlt {
        private Map<String, List<Integer>> keyTimestamps;
        private Map<String, List<String>> keyValues;

        public TimeMapAlt() {
            keyTimestamps = new HashMap<>();
            keyValues = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            keyTimestamps.computeIfAbsent(key, k -> new ArrayList<>()).add(timestamp);
            keyValues.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
        }

        public String get(String key, int timestamp) {
            if (!keyTimestamps.containsKey(key)) {
                return "";
            }

            List<Integer> timestamps = keyTimestamps.get(key);
            List<String> values = keyValues.get(key);

            int index = findFloorIndex(timestamps, timestamp);
            return index >= 0 ? values.get(index) : "";
        }

        private int findFloorIndex(List<Integer> timestamps, int target) {
            int left = 0, right = timestamps.size() - 1;
            int result = -1;

            while (left <= right) {
                int mid = left + (right - left) / 2;

                if (timestamps.get(mid) <= target) {
                    result = mid;
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return result;
        }
    }

    // Using Map with custom comparator
    class TimeMapComparator {
        private Map<String, Map<Integer, String>> store;

        public TimeMapComparator() {
            store = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            store.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
        }

        public String get(String key, int timestamp) {
            Map<Integer, String> timeMap = store.get(key);
            if (timeMap == null) {
                return "";
            }

            // Find the largest timestamp <= given timestamp
            Integer bestTime = null;
            for (Integer time : timeMap.keySet()) {
                if (time <= timestamp) {
                    if (bestTime == null || time > bestTime) {
                        bestTime = time;
                    }
                }
            }

            return bestTime != null ? timeMap.get(bestTime) : "";
        }
    }

    // Thread-safe version
    class TimeMapThreadSafe {
        private final Map<String, TreeMap<Integer, String>> keyTimeMap;
        private final Object lock = new Object();

        public TimeMapThreadSafe() {
            keyTimeMap = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            synchronized (lock) {
                keyTimeMap.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
            }
        }

        public String get(String key, int timestamp) {
            synchronized (lock) {
                TreeMap<Integer, String> timeMap = keyTimeMap.get(key);
                if (timeMap == null) {
                    return "";
                }

                Integer floorKey = timeMap.floorKey(timestamp);
                return floorKey != null ? timeMap.get(floorKey) : "";
            }
        }
    }

    // Memory-optimized version with compression
    class TimeMapCompressed {
        private Map<String, List<CompressedEntry>> keyTimeMap;

        class CompressedEntry {
            int timestamp;
            String value;

            CompressedEntry(int timestamp, String value) {
                this.timestamp = timestamp;
                this.value = value.intern(); // String interning for memory efficiency
            }
        }

        public TimeMapCompressed() {
            keyTimeMap = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            keyTimeMap.computeIfAbsent(key, k -> new ArrayList<>())
                    .add(new CompressedEntry(timestamp, value));
        }

        public String get(String key, int timestamp) {
            List<CompressedEntry> entries = keyTimeMap.get(key);
            if (entries == null) {
                return "";
            }

            int left = 0, right = entries.size() - 1;
            String result = "";

            while (left <= right) {
                int mid = left + (right - left) / 2;

                if (entries.get(mid).timestamp <= timestamp) {
                    result = entries.get(mid).value;
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return result;
        }
    }

    // Version with range queries
    class TimeMapRange {
        private Map<String, TreeMap<Integer, String>> keyTimeMap;

        public TimeMapRange() {
            keyTimeMap = new HashMap<>();
        }

        public void set(String key, String value, int timestamp) {
            keyTimeMap.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
        }

        public String get(String key, int timestamp) {
            TreeMap<Integer, String> timeMap = keyTimeMap.get(key);
            if (timeMap == null) {
                return "";
            }

            Integer floorKey = timeMap.floorKey(timestamp);
            return floorKey != null ? timeMap.get(floorKey) : "";
        }

        // Get all values in timestamp range [start, end]
        public List<String> getRange(String key, int start, int end) {
            TreeMap<Integer, String> timeMap = keyTimeMap.get(key);
            if (timeMap == null) {
                return new ArrayList<>();
            }

            List<String> result = new ArrayList<>();
            for (Map.Entry<Integer, String> entry : timeMap.subMap(start, true, end, true).entrySet()) {
                result.add(entry.getValue());
            }

            return result;
        }

        // Get all timestamps for a key
        public List<Integer> getTimestamps(String key) {
            TreeMap<Integer, String> timeMap = keyTimeMap.get(key);
            if (timeMap == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(timeMap.keySet());
        }
    }

    // Version with statistics
    class TimeMapStats {
        private Map<String, TreeMap<Integer, String>> keyTimeMap;
        private Map<String, Integer> keyCount;
        private int totalOperations;

        public TimeMapStats() {
            keyTimeMap = new HashMap<>();
            keyCount = new HashMap<>();
            totalOperations = 0;
        }

        public void set(String key, String value, int timestamp) {
            keyTimeMap.computeIfAbsent(key, k -> new TreeMap<>()).put(timestamp, value);
            keyCount.put(key, keyCount.getOrDefault(key, 0) + 1);
            totalOperations++;
        }

        public String get(String key, int timestamp) {
            totalOperations++;
            TreeMap<Integer, String> timeMap = keyTimeMap.get(key);
            if (timeMap == null) {
                return "";
            }

            Integer floorKey = timeMap.floorKey(timestamp);
            return floorKey != null ? timeMap.get(floorKey) : "";
        }

        public int getKeyCount(String key) {
            return keyCount.getOrDefault(key, 0);
        }

        public int getTotalOperations() {
            return totalOperations;
        }

        public Set<String> getAllKeys() {
            return new HashSet<>(keyTimeMap.keySet());
        }
    }

    public static void main(String[] args) {
        // Test basic TimeMap
        TimeBasedKeyValueStore solution = new TimeBasedKeyValueStore();
        TimeMap timeMap = solution.new TimeMap();

        // Test Case 1: Basic operations
        timeMap.set("foo", "bar", 1);
        System.out.println(timeMap.get("foo", 1)); // Expected: "bar"
        System.out.println(timeMap.get("foo", 3)); // Expected: "bar"

        timeMap.set("foo", "bar2", 4);
        System.out.println(timeMap.get("foo", 4)); // Expected: "bar2"
        System.out.println(timeMap.get("foo", 5)); // Expected: "bar2"

        // Test Case 2: Non-existent key
        System.out.println(timeMap.get("nonexistent", 1)); // Expected: ""

        // Test Case 3: Timestamp before any set
        timeMap.set("love", "high", 10);
        timeMap.set("love", "low", 20);
        System.out.println(timeMap.get("love", 5)); // Expected: ""
        System.out.println(timeMap.get("love", 10)); // Expected: "high"
        System.out.println(timeMap.get("love", 15)); // Expected: "high"
        System.out.println(timeMap.get("love", 20)); // Expected: "low"
        System.out.println(timeMap.get("love", 25)); // Expected: "low"

        // Test optimized version
        System.out.println("\nTesting Optimized Version:");
        TimeMapOptimized optimized = solution.new TimeMapOptimized();
        optimized.set("test", "value1", 1);
        optimized.set("test", "value2", 3);
        optimized.set("test", "value3", 5);

        System.out.println(optimized.get("test", 0)); // Expected: ""
        System.out.println(optimized.get("test", 1)); // Expected: "value1"
        System.out.println(optimized.get("test", 2)); // Expected: "value1"
        System.out.println(optimized.get("test", 3)); // Expected: "value2"
        System.out.println(optimized.get("test", 4)); // Expected: "value2"
        System.out.println(optimized.get("test", 5)); // Expected: "value3"
        System.out.println(optimized.get("test", 6)); // Expected: "value3"

        // Test alternative implementation
        System.out.println("\nTesting Alternative Version:");
        TimeMapAlt alt = solution.new TimeMapAlt();
        alt.set("key1", "val1", 10);
        alt.set("key1", "val2", 20);
        alt.set("key1", "val3", 30);

        System.out.println(alt.get("key1", 5)); // Expected: ""
        System.out.println(alt.get("key1", 10)); // Expected: "val1"
        System.out.println(alt.get("key1", 25)); // Expected: "val2"
        System.out.println(alt.get("key1", 35)); // Expected: "val3"

        // Test range queries
        System.out.println("\nTesting Range Queries:");
        TimeMapRange range = solution.new TimeMapRange();
        range.set("data", "a", 1);
        range.set("data", "b", 3);
        range.set("data", "c", 5);
        range.set("data", "d", 7);

        List<String> rangeResult = range.getRange("data", 2, 6);
        System.out.println("Range [2,6]: " + rangeResult); // Expected: [b, c]

        List<Integer> timestamps = range.getTimestamps("data");
        System.out.println("All timestamps: " + timestamps); // Expected: [1, 3, 5, 7]

        // Test statistics version
        System.out.println("\nTesting Statistics Version:");
        TimeMapStats stats = solution.new TimeMapStats();
        stats.set("metric", "100", 1);
        stats.set("metric", "200", 5);
        stats.set("counter", "1", 2);

        System.out.println("Key count for 'metric': " + stats.getKeyCount("metric")); // Expected: 2
        System.out.println("Total operations: " + stats.getTotalOperations()); // Expected: 3
        System.out.println("All keys: " + stats.getAllKeys()); // Expected: [metric, counter]

        stats.get("metric", 3);
        System.out.println("Total operations after get: " + stats.getTotalOperations()); // Expected: 4

        // Performance test
        System.out.println("\nPerformance Testing:");
        TimeMapOptimized perfTest = solution.new TimeMapOptimized();

        long startTime = System.currentTimeMillis();

        // Set 10000 values
        for (int i = 1; i <= 10000; i++) {
            perfTest.set("perf", "value" + i, i);
        }

        long setTime = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();

        // Get 1000 random values
        for (int i = 0; i < 1000; i++) {
            int timestamp = (int) (Math.random() * 10000) + 1;
            perfTest.get("perf", timestamp);
        }

        long getTime = System.currentTimeMillis() - startTime;

        System.out.println("Set 10000 values time: " + setTime + "ms");
        System.out.println("Get 1000 values time: " + getTime + "ms");

        // Edge cases
        System.out.println("\nEdge Cases:");
        TimeMapOptimized edge = solution.new TimeMapOptimized();

        // Single value
        edge.set("single", "only", 1);
        System.out.println("Single value test: " + edge.get("single", 1)); // Expected: "only"
        System.out.println("Single value before: " + edge.get("single", 0)); // Expected: ""
        System.out.println("Single value after: " + edge.get("single", 2)); // Expected: "only"

        // Same timestamp (should overwrite)
        edge.set("same", "first", 10);
        edge.set("same", "second", 10); // Same timestamp
        System.out.println("Same timestamp: " + edge.get("same", 10)); // Expected: "second" (or depends on
                                                                       // implementation)

        // Large timestamp values
        edge.set("large", "big", Integer.MAX_VALUE);
        System.out.println("Large timestamp: " + edge.get("large", Integer.MAX_VALUE)); // Expected: "big"

        // Multiple keys with same values
        edge.set("key1", "samevalue", 1);
        edge.set("key2", "samevalue", 1);
        System.out.println("Same value different keys: " + edge.get("key1", 1) + ", " + edge.get("key2", 1)); // Expected:
                                                                                                              // "samevalue,
                                                                                                              // samevalue"

        // Test memory usage comparison
        System.out.println("\nMemory Usage Test:");
        TimeMap regular = solution.new TimeMap();
        TimeMapCompressed compressed = solution.new TimeMapCompressed();

        // Add same data to both
        String[] commonValues = { "value1", "value2", "value1", "value3", "value1" }; // Repeated values
        for (int i = 0; i < commonValues.length; i++) {
            regular.set("test", commonValues[i], i + 1);
            compressed.set("test", commonValues[i], i + 1);
        }

        // Verify both work the same
        for (int i = 1; i <= commonValues.length; i++) {
            String regularResult = regular.get("test", i);
            String compressedResult = compressed.get("test", i);
            System.out.println("Timestamp " + i + ": regular=" + regularResult + ", compressed=" + compressedResult);
        }
    }
}
