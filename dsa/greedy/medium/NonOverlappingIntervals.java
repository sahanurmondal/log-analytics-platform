package greedy.medium;

import java.util.*;

/**
 * LeetCode 435: Non-overlapping Intervals
 * https://leetcode.com/problems/non-overlapping-intervals/
 * 
 * Companies: Google, Meta, Amazon, Microsoft, Apple
 * Frequency: High (Asked in 150+ interviews)
 *
 * Description:
 * Given an array of intervals intervals where intervals[i] = [starti, endi],
 * return the minimum number of intervals you need to remove to make the rest of
 * the intervals non-overlapping.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - -5 * 10^4 <= starti < endi <= 5 * 10^4
 * 
 * Follow-up Questions:
 * 1. Can you return which intervals to remove?
 * 2. What if we want to maximize the number of remaining intervals?
 * 3. Can you solve it using different greedy strategies?
 */
public class NonOverlappingIntervals {

    // Approach 1: Greedy by End Time - O(n log n) time, O(1) space
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        // Sort intervals by end time (greedy choice)
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int removals = 0;
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            // If current interval starts before last selected interval ends
            if (intervals[i][0] < lastEnd) {
                // Remove current interval (overlapping)
                removals++;
            } else {
                // Update last end time
                lastEnd = intervals[i][1];
            }
        }

        return removals;
    }

    // Approach 2: Greedy by Start Time - O(n log n) time, O(1) space
    public int eraseOverlapIntervalsByStart(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int removals = 0;
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < lastEnd) {
                // Remove the interval with later end time
                removals++;
                lastEnd = Math.min(lastEnd, intervals[i][1]);
            } else {
                lastEnd = intervals[i][1];
            }
        }

        return removals;
    }

    // Approach 3: Activity Selection Problem (maximize remaining intervals)
    public int eraseOverlapIntervalsActivitySelection(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return 0;
        }

        // Sort by end time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int count = 1; // Count of non-overlapping intervals
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= lastEnd) {
                count++;
                lastEnd = intervals[i][1];
            }
        }

        return intervals.length - count; // Remove the rest
    }

    // Follow-up 1: Return which intervals to remove
    public List<int[]> getIntervalsToRemove(int[][] intervals) {
        List<int[]> toRemove = new ArrayList<>();

        if (intervals == null || intervals.length <= 1) {
            return toRemove;
        }

        // Create indexed intervals
        int n = intervals.length;
        int[][] indexedIntervals = new int[n][3];
        for (int i = 0; i < n; i++) {
            indexedIntervals[i] = new int[] { intervals[i][0], intervals[i][1], i };
        }

        Arrays.sort(indexedIntervals, (a, b) -> Integer.compare(a[1], b[1]));

        int lastEnd = indexedIntervals[0][1];

        for (int i = 1; i < indexedIntervals.length; i++) {
            if (indexedIntervals[i][0] < lastEnd) {
                int originalIndex = indexedIntervals[i][2];
                toRemove.add(intervals[originalIndex]);
            } else {
                lastEnd = indexedIntervals[i][1];
            }
        }

        return toRemove;
    }

    // Follow-up 2: Return maximum number of non-overlapping intervals
    public int maxNonOverlappingIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        int count = 1;
        int lastEnd = intervals[0][1];

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= lastEnd) {
                count++;
                lastEnd = intervals[i][1];
            }
        }

        return count;
    }

    // Follow-up 3: Return the optimal set of non-overlapping intervals
    public int[][] getOptimalIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0][];
        }

        // Create indexed intervals
        int n = intervals.length;
        int[][] indexedIntervals = new int[n][3];
        for (int i = 0; i < n; i++) {
            indexedIntervals[i] = new int[] { intervals[i][0], intervals[i][1], i };
        }

        Arrays.sort(indexedIntervals, (a, b) -> Integer.compare(a[1], b[1]));

        List<int[]> result = new ArrayList<>();
        result.add(new int[] { indexedIntervals[0][0], indexedIntervals[0][1] });
        int lastEnd = indexedIntervals[0][1];

        for (int i = 1; i < indexedIntervals.length; i++) {
            if (indexedIntervals[i][0] >= lastEnd) {
                result.add(new int[] { indexedIntervals[i][0], indexedIntervals[i][1] });
                lastEnd = indexedIntervals[i][1];
            }
        }

        return result.toArray(new int[0][]);
    }

    // Helper method: Validate if intervals are non-overlapping
    private boolean areNonOverlapping(int[][] intervals) {
        if (intervals.length <= 1)
            return true;

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i - 1][1]) {
                return false;
            }
        }

        return true;
    }

    // Helper method: Print intervals
    private void printIntervals(int[][] intervals, String label) {
        System.out.println(label + ":");
        for (int[] interval : intervals) {
            System.out.print("[" + interval[0] + "," + interval[1] + "] ");
        }
        System.out.println();
    }

    // Helper: Get interval overlap count
    public int getOverlapCount(int[][] intervals) {
        int overlaps = 0;

        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (intervals[i][1] > intervals[j][0] && intervals[j][1] > intervals[i][0]) {
                    overlaps++;
                }
            }
        }

        return overlaps;
    }

    public static void main(String[] args) {
        NonOverlappingIntervals solution = new NonOverlappingIntervals();

        // Test Case 1: Standard overlapping intervals
        int[][] intervals1 = { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 1, 3 } };
        int result1 = solution.eraseOverlapIntervals(intervals1);
        System.out.println("Test 1 - Minimum removals: " + result1); // Expected: 1

        // Show removed intervals
        List<int[]> toRemove = solution.getIntervalsToRemove(intervals1);
        System.out.print("Intervals to remove from test 1: ");
        for (int[] interval : toRemove) {
            System.out.print("[" + interval[0] + "," + interval[1] + "] ");
        }
        System.out.println();

        // Maximum non-overlapping intervals
        int maxNonOverlapping = solution.maxNonOverlappingIntervals(intervals1);
        System.out.println("Maximum non-overlapping intervals: " + maxNonOverlapping);

        // Optimal interval set
        int[][] optimal = solution.getOptimalIntervals(intervals1);
        solution.printIntervals(optimal, "Optimal non-overlapping set");
        System.out.println("Is optimal set non-overlapping: " + solution.areNonOverlapping(optimal));

        // Verify total count
        System.out.println("Original count: " + intervals1.length);
        System.out.println("Removals: " + solution.eraseOverlapIntervals(intervals1.clone()));
        System.out.println("Remaining: " + optimal.length);
        System.out.println("Verification: " +
                (intervals1.length - solution.eraseOverlapIntervals(intervals1.clone()) == optimal.length));

        // Test Case 2: No overlapping intervals
        int[][] intervals2 = { { 1, 2 }, { 2, 3 } };
        int result2 = solution.eraseOverlapIntervals(intervals2);
        System.out.println("\nTest 2 - Minimum removals: " + result2); // Expected: 0

        // Test Case 3: All overlapping intervals
        int[][] intervals3 = { { 1, 2 }, { 1, 2 }, { 1, 2 } };
        int result3 = solution.eraseOverlapIntervals(intervals3);
        System.out.println("Test 3 - Minimum removals: " + result3); // Expected: 2

        // Test Case 4: Complex overlapping pattern
        int[][] intervals4 = { { 1, 100 }, { 11, 22 }, { 1, 11 }, { 2, 12 } };
        int result4 = solution.eraseOverlapIntervals(intervals4);
        System.out.println("Test 4 - Minimum removals: " + result4); // Expected: 2

        // Compare different approaches
        System.out.println("\nComparing approaches for test 1:");
        System.out.println("By end time: " + solution.eraseOverlapIntervals(intervals1.clone()));
        System.out.println("By start time: " + solution.eraseOverlapIntervalsByStart(intervals1.clone()));
        System.out
                .println("Activity selection: " + solution.eraseOverlapIntervalsActivitySelection(intervals1.clone()));

        // Overlap analysis
        System.out.println("\nOverlap analysis:");
        System.out.println("Original overlap count: " + solution.getOverlapCount(intervals1));
        System.out.println("Optimal set overlap count: " + solution.getOverlapCount(optimal));
    }
}
