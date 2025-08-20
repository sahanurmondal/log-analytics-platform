package design.hard;

import java.util.*;

/**
 * LeetCode 715: Range Module
 * https://leetcode.com/problems/range-module/
 *
 * Description: A Range Module is a module that tracks ranges of numbers.
 * Design a data structure to track the ranges represented as half-open
 * intervals.
 * 
 * Constraints:
 * - 1 <= left < right <= 10^9
 * - At most 10^4 calls will be made to addRange, queryRange, and removeRange
 *
 * Follow-up:
 * - Can you solve it efficiently?
 * 
 * Time Complexity: O(n) for all operations in worst case
 * Space Complexity: O(n)
 * 
 * Company Tags: Google, Facebook
 */
public class DesignRangeModule {

    private TreeMap<Integer, Integer> ranges;

    public DesignRangeModule() {
        ranges = new TreeMap<>();
    }

    public void addRange(int left, int right) {
        // Find overlapping ranges
        Integer start = ranges.floorKey(right);
        Integer end = ranges.ceilingKey(left);

        if (start != null && ranges.get(start) >= left) {
            left = Math.min(left, start);
            right = Math.max(right, ranges.get(start));
        }

        // Remove all overlapping ranges
        Map<Integer, Integer> subMap = ranges.subMap(left, true, right, false);
        for (Integer key : new ArrayList<>(subMap.keySet())) {
            right = Math.max(right, ranges.get(key));
            ranges.remove(key);
        }

        ranges.put(left, right);
    }

    public boolean queryRange(int left, int right) {
        Integer start = ranges.floorKey(left);
        return start != null && ranges.get(start) >= right;
    }

    public void removeRange(int left, int right) {
        Integer start = ranges.floorKey(right);
        Integer end = ranges.ceilingKey(left);

        Map<Integer, Integer> subMap = ranges.subMap(left, true, right, false);
        List<int[]> toAdd = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : subMap.entrySet()) {
            int rangeStart = entry.getKey();
            int rangeEnd = entry.getValue();

            if (rangeStart < left) {
                toAdd.add(new int[] { rangeStart, left });
            }
            if (rangeEnd > right) {
                toAdd.add(new int[] { right, rangeEnd });
            }
        }

        // Remove overlapping ranges
        for (Integer key : new ArrayList<>(subMap.keySet())) {
            ranges.remove(key);
        }

        // Add back non-overlapping parts
        for (int[] range : toAdd) {
            ranges.put(range[0], range[1]);
        }
    }

    public static void main(String[] args) {
        DesignRangeModule rangeModule = new DesignRangeModule();
        rangeModule.addRange(10, 20);
        rangeModule.removeRange(14, 16);
        System.out.println(rangeModule.queryRange(10, 14)); // Expected: true
        System.out.println(rangeModule.queryRange(13, 15)); // Expected: false
        System.out.println(rangeModule.queryRange(16, 17)); // Expected: true
    }
}
