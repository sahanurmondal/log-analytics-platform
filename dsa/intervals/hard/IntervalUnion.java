package intervals.hard;

import java.util.*;

/**
 * LeetCode 56: Merge Intervals
 * https://leetcode.com/problems/merge-intervals/
 * 
 * Companies: Amazon, Google, Facebook, Microsoft, Apple
 * Frequency: High (Asked in 200+ interviews)
 *
 * Description: Given an array of intervals where intervals[i] = [starti, endi],
 * merge all overlapping intervals, and return an array of the non-overlapping
 * intervals that cover all the intervals in the input.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^4
 * - intervals[i].length == 2
 * - 0 <= starti <= endi <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you do it in-place with O(1) extra space?
 * 2. What if intervals come in a stream?
 * 3. How to handle different merge criteria (touching vs overlapping)?
 */
public class IntervalUnion {

    // Approach 1: Sort and Merge - O(n log n) time, O(n) space
    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }

        // Sort intervals by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            int[] next = intervals[i];

            // Check if current and next intervals overlap
            if (current[1] >= next[0]) {
                // Merge intervals
                current[1] = Math.max(current[1], next[1]);
            } else {
                // No overlap, add current to result and move to next
                merged.add(current);
                current = next;
            }
        }

        // Add the last interval
        merged.add(current);

        return merged.toArray(new int[merged.size()][]);
    }

    // Approach 2: In-place merge (optimized space) - O(n log n) time, O(1) space
    public int[][] mergeInPlace(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        int writeIndex = 0;

        for (int i = 1; i < intervals.length; i++) {
            // Check if current interval overlaps with the last merged interval
            if (intervals[writeIndex][1] >= intervals[i][0]) {
                // Merge intervals
                intervals[writeIndex][1] = Math.max(intervals[writeIndex][1], intervals[i][1]);
            } else {
                // No overlap, move to next position
                writeIndex++;
                intervals[writeIndex] = intervals[i];
            }
        }

        // Return subarray with merged intervals
        return Arrays.copyOf(intervals, writeIndex + 1);
    }

    // Approach 3: Using PriorityQueue (for stream processing) - O(n log n) time,
    // O(n) space
    public int[][] mergeWithPriorityQueue(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }

        // Min-heap sorted by start time
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        for (int[] interval : intervals) {
            pq.offer(interval);
        }

        List<int[]> merged = new ArrayList<>();
        int[] current = pq.poll();

        while (!pq.isEmpty()) {
            int[] next = pq.poll();

            if (current[1] >= next[0]) {
                current[1] = Math.max(current[1], next[1]);
            } else {
                merged.add(current);
                current = next;
            }
        }

        merged.add(current);
        return merged.toArray(new int[merged.size()][]);
    }

    // Follow-up 1: Merge touching intervals (adjacent intervals)
    public int[][] mergeTouchingIntervals(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        int[] current = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            int[] next = intervals[i];

            // Merge if overlapping OR touching (current[1] == next[0])
            if (current[1] >= next[0]) {
                current[1] = Math.max(current[1], next[1]);
            } else {
                merged.add(current);
                current = next;
            }
        }

        merged.add(current);
        return merged.toArray(new int[merged.size()][]);
    }

    // Follow-up 2: Insert new interval and merge
    public int[][] insertAndMerge(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;

        // Add all intervals that end before newInterval starts
        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Merge overlapping intervals with newInterval
        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);

        // Add remaining intervals
        while (i < intervals.length) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }

    // Follow-up 3: Calculate total covered length after merge
    public int getTotalCoveredLength(int[][] intervals) {
        int[][] merged = merge(intervals);
        int totalLength = 0;

        for (int[] interval : merged) {
            totalLength += interval[1] - interval[0];
        }

        return totalLength;
    }

    // Helper method: Check if two intervals overlap
    private boolean isOverlapping(int[] a, int[] b) {
        return Math.max(a[0], b[0]) <= Math.min(a[1], b[1]);
    }

    // Helper method: Print intervals
    private void printIntervals(int[][] intervals, String label) {
        System.out.print(label + ": ");
        for (int[] interval : intervals) {
            System.out.print("[" + interval[0] + "," + interval[1] + "] ");
        }
        System.out.println();
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        IntervalUnion solver = new IntervalUnion();

        // Test case 1: Basic merge
        int[][] intervals1 = { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } };
        System.out.println("Test 1 - Basic merge:");
        solver.printIntervals(intervals1, "Input");
        int[][] result1 = solver.merge(intervals1);
        solver.printIntervals(result1, "Output");
        System.out.println("Expected: [1,6] [8,10] [15,18]\n");

        // Test case 2: All intervals merge into one
        int[][] intervals2 = { { 1, 4 }, { 4, 5 } };
        System.out.println("Test 2 - Adjacent intervals:");
        solver.printIntervals(intervals2, "Input");
        int[][] result2 = solver.merge(intervals2);
        solver.printIntervals(result2, "Output");
        System.out.println("Expected: [1,5]\n");

        // Test case 3: No merge needed
        int[][] intervals3 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("Test 3 - No overlaps:");
        solver.printIntervals(intervals3, "Input");
        int[][] result3 = solver.merge(intervals3);
        solver.printIntervals(result3, "Output");
        System.out.println("Expected: [1,2] [3,4] [5,6]\n");

        // Test case 4: Complex overlapping
        int[][] intervals4 = { { 1, 4 }, { 0, 4 } };
        System.out.println("Test 4 - Complex overlapping:");
        solver.printIntervals(intervals4, "Input");
        int[][] result4 = solver.merge(intervals4);
        solver.printIntervals(result4, "Output");
        System.out.println("Expected: [0,4]\n");

        // Test different approaches
        System.out.println("Comparing approaches for intervals1:");
        int[][] result1a = solver.merge(intervals1.clone());
        int[][] result1b = solver.mergeInPlace(intervals1.clone());
        int[][] result1c = solver.mergeWithPriorityQueue(intervals1.clone());
        System.out.println("All approaches same result: " +
                (Arrays.deepEquals(result1a, result1b) && Arrays.deepEquals(result1b, result1c)));

        // Test follow-ups
        System.out.println("\nFollow-up tests:");

        // Insert and merge
        int[][] intervals5 = { { 1, 3 }, { 6, 9 } };
        int[] newInterval = { 2, 5 };
        int[][] insertResult = solver.insertAndMerge(intervals5, newInterval);
        System.out.print("Insert [2,5] into [[1,3],[6,9]]: ");
        solver.printIntervals(insertResult, "Result");

        // Total covered length
        int totalLength = solver.getTotalCoveredLength(intervals1);
        System.out.println("Total covered length: " + totalLength);

        // Merge touching intervals
        int[][] touching = { { 1, 2 }, { 2, 3 }, { 4, 5 } };
        int[][] touchingResult = solver.mergeTouchingIntervals(touching);
        System.out.print("Merge touching [1,2],[2,3],[4,5]: ");
        solver.printIntervals(touchingResult, "Result");
    }
}
