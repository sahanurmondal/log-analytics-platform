package intervals.hard;

import java.util.*;

/**
 * LeetCode 986: Interval List Intersections
 * https://leetcode.com/problems/interval-list-intersections/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 50+ interviews)
 *
 * Description: Given a list of intervals, count the number of overlapping
 * interval pairs.
 * Two intervals [a,b] and [c,d] overlap if max(a,c) < min(b,d).
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - intervals[i][0] < intervals[i][1]
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^6
 * 
 * Follow-up Questions:
 * 1. How to efficiently count overlaps for large input with coordinate
 * compression?
 * 2. What if we need to return the actual overlapping pairs?
 * 3. Can you solve it using sweep line algorithm?
 */
public class IntervalOverlapCount {

    // Approach 1: Sort by start + Active intervals tracking - O(n log n) time, O(n)
    // space
    public int countOverlaps(int[][] intervals) {
        if (intervals == null || intervals.length <= 1)
            return 0;

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int count = 0;
        // Use TreeMap to maintain active intervals sorted by end time
        TreeMap<Integer, Integer> activeEnds = new TreeMap<>();

        for (int[] interval : intervals) {
            int start = interval[0], end = interval[1];

            // Remove intervals that end before current starts (no overlap)
            Iterator<Map.Entry<Integer, Integer>> it = activeEnds.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> entry = it.next();
                if (entry.getKey() <= start) {
                    it.remove();
                } else {
                    break; // TreeMap is sorted, so we can break
                }
            }

            // Count overlaps with remaining active intervals
            count += activeEnds.size();

            // Add current interval to active set
            activeEnds.put(end, activeEnds.getOrDefault(end, 0) + 1);
        }

        return count;
    }

    // Approach 2: Brute force for verification - O(n^2) time, O(1) space
    public int countOverlapsBrute(int[][] intervals) {
        if (intervals == null || intervals.length <= 1)
            return 0;

        int count = 0;
        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (isOverlapping(intervals[i], intervals[j])) {
                    count++;
                }
            }
        }
        return count;
    }

    // Approach 3: Sweep line with events - O(n log n) time, O(n) space
    public int countOverlapsSweepLine(int[][] intervals) {
        if (intervals == null || intervals.length <= 1)
            return 0;

        List<int[]> events = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            events.add(new int[] { intervals[i][0], 1, i }); // start event
            events.add(new int[] { intervals[i][1], -1, i }); // end event
        }

        // Sort events: first by time, then end events before start events
        events.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]); // -1 comes before 1
        });

        int count = 0;
        int activeCount = 0;

        for (int[] event : events) {
            if (event[1] == 1) { // start event
                count += activeCount; // current interval overlaps with all active ones
                activeCount++;
            } else { // end event
                activeCount--;
            }
        }

        return count;
    }

    // Helper method: Check if two intervals overlap
    private boolean isOverlapping(int[] a, int[] b) {
        return Math.max(a[0], b[0]) < Math.min(a[1], b[1]);
    }

    // Helper method: Print intervals for debugging
    private void printIntervals(int[][] intervals) {
        System.out.print("Intervals: ");
        for (int[] interval : intervals) {
            System.out.print("[" + interval[0] + "," + interval[1] + "] ");
        }
        System.out.println();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        IntervalOverlapCount solver = new IntervalOverlapCount();

        // Test case 1: Basic overlapping intervals
        int[][] intervals1 = { { 1, 3 }, { 2, 4 }, { 5, 7 } };
        System.out.println("Test 1 - Expected: 1");
        System.out.println("Approach 1: " + solver.countOverlaps(intervals1));
        System.out.println("Approach 2: " + solver.countOverlapsBrute(intervals1));
        System.out.println("Approach 3: " + solver.countOverlapsSweepLine(intervals1));

        // Test case 2: All intervals overlap
        int[][] intervals2 = { { 1, 5 }, { 2, 6 }, { 3, 7 } };
        System.out.println("\nTest 2 - Expected: 3");
        System.out.println("Approach 1: " + solver.countOverlaps(intervals2));
        System.out.println("Approach 2: " + solver.countOverlapsBrute(intervals2));
        System.out.println("Approach 3: " + solver.countOverlapsSweepLine(intervals2));

        // Test case 3: No overlaps
        int[][] intervals3 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("\nTest 3 - Expected: 0");
        System.out.println("Approach 1: " + solver.countOverlaps(intervals3));
        System.out.println("Approach 2: " + solver.countOverlapsBrute(intervals3));
        System.out.println("Approach 3: " + solver.countOverlapsSweepLine(intervals3));

        // Test case 4: Adjacent intervals (no overlap)
        int[][] intervals4 = { { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println("\nTest 4 - Expected: 0");
        System.out.println("Approach 1: " + solver.countOverlaps(intervals4));
        System.out.println("Approach 2: " + solver.countOverlapsBrute(intervals4));
        System.out.println("Approach 3: " + solver.countOverlapsSweepLine(intervals4));

        // Test case 5: Complex case
        int[][] intervals5 = { { 1, 4 }, { 2, 3 }, { 5, 8 }, { 6, 7 } };
        System.out.println("\nTest 5 - Expected: 2");
        System.out.println("Approach 1: " + solver.countOverlaps(intervals5));
        System.out.println("Approach 2: " + solver.countOverlapsBrute(intervals5));
        System.out.println("Approach 3: " + solver.countOverlapsSweepLine(intervals5));
    }
}
