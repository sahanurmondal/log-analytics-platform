package intervals.hard;

import java.util.*;

/**
 * LeetCode 2406: Divide Intervals Into Minimum Number of Groups
 * https://leetcode.com/problems/divide-intervals-into-minimum-number-of-groups/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, Apple
 * Frequency: High (Asked in 80+ interviews)
 *
 * Description: You are given a 2D integer array intervals where intervals[i] =
 * [lefti, righti]
 * represents the inclusive interval [lefti, righti]. You have to divide the
 * intervals into one
 * or more groups such that each interval is in exactly one group, and no two
 * intervals that
 * are in the same group intersect each other. Return the minimum number of
 * groups you need to make.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 1 <= lefti <= righti <= 10^6
 * 
 * Follow-up Questions:
 * 1. Can you assign group numbers to each interval?
 * 2. What if we need to minimize the maximum group size?
 * 3. Can you solve it using different approaches (greedy, sweep line)?
 */
public class IntervalPartitioningHard {

    // Approach 1: Greedy with Min-Heap - O(n log n) time, O(n) space
    public int minGroups(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return 0;

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Min-heap to track the end times of active groups
        PriorityQueue<Integer> minHeap = new PriorityQueue<>();

        for (int[] interval : intervals) {
            int start = interval[0], end = interval[1];

            // If the earliest ending group finishes before current starts,
            // we can reuse that group
            if (!minHeap.isEmpty() && minHeap.peek() < start) {
                minHeap.poll();
            }

            // Add current interval's end time to heap
            minHeap.offer(end);
        }

        return minHeap.size();
    }

    // Approach 2: Sweep Line Algorithm - O(n log n) time, O(n) space
    public int minGroupsSweepLine(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return 0;

        List<int[]> events = new ArrayList<>();

        // Create events for each interval
        for (int[] interval : intervals) {
            events.add(new int[] { interval[0], 1 }); // start event
            events.add(new int[] { interval[1] + 1, -1 }); // end event (exclusive)
        }

        // Sort events by time, with end events before start events at same time
        events.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]); // -1 comes before 1
        });

        int maxGroups = 0;
        int currentGroups = 0;

        for (int[] event : events) {
            currentGroups += event[1];
            maxGroups = Math.max(maxGroups, currentGroups);
        }

        return maxGroups;
    }

    // Approach 3: Two Arrays (Optimized Sweep Line) - O(n log n) time, O(n) space
    public int minGroupsTwoArrays(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return 0;

        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int groups = 0;
        int endPtr = 0;

        for (int start : starts) {
            if (start > ends[endPtr]) {
                endPtr++;
            } else {
                groups++;
            }
        }

        return groups;
    }

    // Follow-up: Assign group numbers to intervals
    public int[] assignGroups(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return new int[0];

        int n = intervals.length;
        int[][] indexedIntervals = new int[n][3];
        for (int i = 0; i < n; i++) {
            indexedIntervals[i] = new int[] { intervals[i][0], intervals[i][1], i };
        }

        // Sort by start time
        Arrays.sort(indexedIntervals, (a, b) -> Integer.compare(a[0], b[0]));

        int[] result = new int[n];
        // Min-heap storing [endTime, groupId]
        PriorityQueue<int[]> minHeap = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        int nextGroupId = 0;

        for (int[] interval : indexedIntervals) {
            int start = interval[0], end = interval[1], originalIndex = interval[2];

            if (!minHeap.isEmpty() && minHeap.peek()[0] < start) {
                // Reuse existing group
                int[] reusedGroup = minHeap.poll();
                result[originalIndex] = reusedGroup[1];
                minHeap.offer(new int[] { end, reusedGroup[1] });
            } else {
                // Create new group
                result[originalIndex] = nextGroupId;
                minHeap.offer(new int[] { end, nextGroupId });
                nextGroupId++;
            }
        }

        return result;
    }

    // Helper method: Verify solution correctness
    private boolean isValidPartitioning(int[][] intervals, int[] groups) {
        Map<Integer, List<int[]>> groupMap = new HashMap<>();

        for (int i = 0; i < intervals.length; i++) {
            groupMap.computeIfAbsent(groups[i], k -> new ArrayList<>()).add(intervals[i]);
        }

        // Check each group for overlaps
        for (List<int[]> group : groupMap.values()) {
            for (int i = 0; i < group.size(); i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    if (isOverlapping(group.get(i), group.get(j))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    // Helper method: Check if two intervals overlap
    private boolean isOverlapping(int[] a, int[] b) {
        return Math.max(a[0], b[0]) <= Math.min(a[1], b[1]);
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        IntervalPartitioningHard solver = new IntervalPartitioningHard();

        // Test case 1: Basic case
        int[][] intervals1 = { { 5, 10 }, { 6, 8 }, { 1, 5 }, { 2, 3 }, { 1, 10 } };
        System.out.println("Test 1 - Expected: 3");
        System.out.println("Approach 1 (Min-Heap): " + solver.minGroups(intervals1));
        System.out.println("Approach 2 (Sweep Line): " + solver.minGroupsSweepLine(intervals1));
        System.out.println("Approach 3 (Two Arrays): " + solver.minGroupsTwoArrays(intervals1));

        // Test case 2: No overlaps
        int[][] intervals2 = { { 1, 3 }, { 5, 6 }, { 8, 10 }, { 11, 13 } };
        System.out.println("\nTest 2 - Expected: 1");
        System.out.println("Approach 1: " + solver.minGroups(intervals2));
        System.out.println("Approach 2: " + solver.minGroupsSweepLine(intervals2));
        System.out.println("Approach 3: " + solver.minGroupsTwoArrays(intervals2));

        // Test case 3: All overlap
        int[][] intervals3 = { { 1, 5 }, { 1, 4 }, { 1, 3 } };
        System.out.println("\nTest 3 - Expected: 3");
        System.out.println("Approach 1: " + solver.minGroups(intervals3));
        System.out.println("Approach 2: " + solver.minGroupsSweepLine(intervals3));
        System.out.println("Approach 3: " + solver.minGroupsTwoArrays(intervals3));

        // Test follow-up: Group assignment
        System.out.println("\nGroup Assignment Test:");
        int[] groups = solver.assignGroups(intervals1);
        System.out.println("Groups: " + Arrays.toString(groups));
        System.out.println("Valid partitioning: " + solver.isValidPartitioning(intervals1, groups));
    }
}
