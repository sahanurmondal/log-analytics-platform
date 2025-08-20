package design.medium;

import java.util.*;

/**
 * LeetCode 1146: Snapshot Array
 * https://leetcode.com/problems/snapshot-array/
 *
 * Description: Implement a SnapshotArray that supports the following interface:
 * - SnapshotArray(int length): initializes an array-like data structure with
 * the given length
 * - void set(index, val): sets the element at the given index to be equal to
 * val
 * - int snap(): takes a snapshot of the array and returns the snapshot_id
 * - int get(index, snap_id): returns the value at the given index, at the time
 * when the snapshot with the given snap_id was taken
 * 
 * Constraints:
 * - 1 <= length <= 5 * 10^4
 * - 0 <= index < length
 * - 0 <= val <= 10^9
 * - 0 <= snap_id < (the total number of times we call snap())
 * - At most 5 * 10^4 calls will be made to set, snap, and get
 *
 * Follow-up:
 * - Can you optimize space usage?
 * 
 * Time Complexity: O(1) for set, O(1) for snap, O(log S) for get where S is
 * snapshots
 * Space Complexity: O(N * S) in worst case
 * 
 * Company Tags: Google, Facebook
 */
public class DesignSnapshotArray {

    private List<TreeMap<Integer, Integer>> snapshots;
    private int currentSnap;

    public DesignSnapshotArray(int length) {
        snapshots = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            snapshots.add(new TreeMap<>());
            snapshots.get(i).put(0, 0); // Initialize with snap_id 0, value 0
        }
        currentSnap = 0;
    }

    public void set(int index, int val) {
        snapshots.get(index).put(currentSnap, val);
    }

    public int snap() {
        return currentSnap++;
    }

    public int get(int index, int snap_id) {
        TreeMap<Integer, Integer> history = snapshots.get(index);
        // Find the largest snap_id <= given snap_id
        Map.Entry<Integer, Integer> entry = history.floorEntry(snap_id);
        return entry == null ? 0 : entry.getValue();
    }

    // Alternative implementation - More space efficient
    static class SnapshotArrayOptimized {
        private Map<Integer, TreeMap<Integer, Integer>> data;
        private int currentSnap;

        public SnapshotArrayOptimized(int length) {
            data = new HashMap<>();
            currentSnap = 0;
        }

        public void set(int index, int val) {
            data.computeIfAbsent(index, k -> new TreeMap<>()).put(currentSnap, val);
        }

        public int snap() {
            return currentSnap++;
        }

        public int get(int index, int snap_id) {
            if (!data.containsKey(index)) {
                return 0;
            }

            TreeMap<Integer, Integer> history = data.get(index);
            Map.Entry<Integer, Integer> entry = history.floorEntry(snap_id);
            return entry == null ? 0 : entry.getValue();
        }
    }

    public static void main(String[] args) {
        DesignSnapshotArray snapshotArr = new DesignSnapshotArray(3);
        snapshotArr.set(0, 5);
        int snap1 = snapshotArr.snap(); // snap_id = 0
        snapshotArr.set(0, 6);
        System.out.println(snapshotArr.get(0, snap1)); // Expected: 5

        // Test with multiple indices
        snapshotArr.set(1, 10);
        snapshotArr.set(2, 20);
        int snap2 = snapshotArr.snap(); // snap_id = 1

        snapshotArr.set(1, 15);
        System.out.println(snapshotArr.get(1, snap2)); // Expected: 10
        System.out.println(snapshotArr.get(1, snap2 + 1)); // Expected: 15 (current value)

        // Test optimized version
        SnapshotArrayOptimized optimized = new SnapshotArrayOptimized(2);
        optimized.set(0, 4);
        optimized.set(1, 16);
        int snapOpt = optimized.snap();
        optimized.set(1, 22);
        System.out.println(optimized.get(1, snapOpt)); // Expected: 16
    }
}
