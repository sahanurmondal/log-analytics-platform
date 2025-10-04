package intervals.medium;

import java.util.*;

/**
 * LeetCode 436: Find Right Interval
 * https://leetcode.com/problems/find-right-interval/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, Apple
 * Frequency: Medium (Asked in 80+ interviews)
 *
 * Description: You are given an array of intervals, where intervals[i] =
 * [starti, endi] and each starti is unique.
 * The right interval for an interval i is an interval j such that startj >=
 * endi and startj is minimized.
 * Return an array of right interval indices for each interval i. If no right
 * interval exists for interval i, then put -1 at index i.
 *
 * Constraints:
 * - 1 <= intervals.length <= 2 * 10^4
 * - intervals[i].length == 2
 * - -10^6 <= starti <= endi <= 10^6
 * - The start point of each interval is unique.
 * 
 * Follow-up Questions:
 * 1. What if we need to find all right intervals instead of just the closest
 * one?
 * 2. Can you solve it if intervals are given in a stream?
 * 3. What if we need to find left intervals as well?
 */
public class FindRightInterval {

    // Approach 1: Binary Search with Sorted Starts - O(n log n) time, O(n) space
    public int[] findRightInterval(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        int n = intervals.length;
        int[][] starts = new int[n][2]; // [start, originalIndex]

        // Create array of starts with their original indices
        for (int i = 0; i < n; i++) {
            starts[i][0] = intervals[i][0];
            starts[i][1] = i;
        }

        // Sort by start time
        Arrays.sort(starts, (a, b) -> Integer.compare(a[0], b[0]));

        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            int end = intervals[i][1];
            int rightIndex = binarySearchRightInterval(starts, end);
            result[i] = rightIndex;
        }

        return result;
    }

    // Approach 2: TreeMap for efficient range queries - O(n log n) time, O(n) space
    public int[] findRightIntervalTreeMap(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        TreeMap<Integer, Integer> startToIndex = new TreeMap<>();

        for (int i = 0; i < intervals.length; i++) {
            startToIndex.put(intervals[i][0], i);
        }

        int[] result = new int[intervals.length];

        for (int i = 0; i < intervals.length; i++) {
            int end = intervals[i][1];
            Map.Entry<Integer, Integer> rightEntry = startToIndex.ceilingEntry(end);
            result[i] = rightEntry == null ? -1 : rightEntry.getValue();
        }

        return result;
    }

    // Approach 3: Two Pointers (when intervals are sorted) - O(n log n) time, O(n)
    // space
    public int[] findRightIntervalTwoPointers(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        int n = intervals.length;
        int[][] indexedIntervals = new int[n][3]; // [start, end, originalIndex]

        for (int i = 0; i < n; i++) {
            indexedIntervals[i] = new int[] { intervals[i][0], intervals[i][1], i };
        }

        // Sort by start time
        Arrays.sort(indexedIntervals, (a, b) -> Integer.compare(a[0], b[0]));

        int[] result = new int[n];
        Arrays.fill(result, -1);

        for (int i = 0; i < n; i++) {
            int end = indexedIntervals[i][1];
            int originalIndex = indexedIntervals[i][2];

            // Find the first interval that starts >= end
            for (int j = i + 1; j < n; j++) {
                if (indexedIntervals[j][0] >= end) {
                    result[originalIndex] = indexedIntervals[j][2];
                    break;
                }
            }
        }

        return result;
    }

    // Helper method for binary search
    private int binarySearchRightInterval(int[][] starts, int target) {
        int left = 0, right = starts.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (starts[mid][0] >= target) {
                result = starts[mid][1]; // Store the original index
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    // Follow-up 1: Find all right intervals for each interval
    public List<List<Integer>> findAllRightIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new ArrayList<>();
        }

        TreeMap<Integer, List<Integer>> startToIndices = new TreeMap<>();

        for (int i = 0; i < intervals.length; i++) {
            startToIndices.computeIfAbsent(intervals[i][0], k -> new ArrayList<>()).add(i);
        }

        List<List<Integer>> result = new ArrayList<>();

        for (int i = 0; i < intervals.length; i++) {
            int end = intervals[i][1];
            List<Integer> rightIntervals = new ArrayList<>();

            for (Map.Entry<Integer, List<Integer>> entry : startToIndices.tailMap(end).entrySet()) {
                rightIntervals.addAll(entry.getValue());
            }

            result.add(rightIntervals);
        }

        return result;
    }

    // Follow-up 2: Find left intervals
    public int[] findLeftInterval(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        int n = intervals.length;
        int[][] ends = new int[n][2]; // [end, originalIndex]

        for (int i = 0; i < n; i++) {
            ends[i][0] = intervals[i][1];
            ends[i][1] = i;
        }

        Arrays.sort(ends, (a, b) -> Integer.compare(b[0], a[0])); // Sort by end time descending

        int[] result = new int[n];

        for (int i = 0; i < n; i++) {
            int start = intervals[i][0];
            int leftIndex = binarySearchLeftInterval(ends, start);
            result[i] = leftIndex;
        }

        return result;
    }

    // Helper method for finding left interval
    private int binarySearchLeftInterval(int[][] ends, int target) {
        int left = 0, right = ends.length - 1;
        int result = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (ends[mid][0] <= target) {
                result = ends[mid][1];
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return result;
    }

    // Follow-up 3: Stream processing
    public static class IntervalProcessor {
        private TreeMap<Integer, Integer> startToIndex;
        private int nextIndex;

        public IntervalProcessor() {
            this.startToIndex = new TreeMap<>();
            this.nextIndex = 0;
        }

        public void addInterval(int start, int end) {
            startToIndex.put(start, nextIndex++);
        }

        public int findRightInterval(int end) {
            Map.Entry<Integer, Integer> rightEntry = startToIndex.ceilingEntry(end);
            return rightEntry == null ? -1 : rightEntry.getValue();
        }
    }

    // Helper method: Validate result
    private boolean isValidResult(int[][] intervals, int[] result) {
        for (int i = 0; i < intervals.length; i++) {
            if (result[i] != -1) {
                int rightIndex = result[i];
                if (intervals[rightIndex][0] < intervals[i][1]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindRightInterval solver = new FindRightInterval();

        // Test case 1: Basic case
        int[][] intervals1 = { { 1, 2 }, { 2, 3 }, { 0, 1 }, { 3, 4 } };
        System.out.println("Test 1 - Expected: [-1,3,-1,-1]");
        System.out.println("Approach 1: " + Arrays.toString(solver.findRightInterval(intervals1)));
        System.out.println("Approach 2: " + Arrays.toString(solver.findRightIntervalTreeMap(intervals1)));
        System.out.println("Approach 3: " + Arrays.toString(solver.findRightIntervalTwoPointers(intervals1)));

        // Test case 2: All have right intervals
        int[][] intervals2 = { { 3, 4 }, { 2, 3 }, { 1, 2 } };
        System.out.println("\nTest 2 - Expected: [-1,0,1]");
        System.out.println("Approach 1: " + Arrays.toString(solver.findRightInterval(intervals2)));
        System.out.println("Approach 2: " + Arrays.toString(solver.findRightIntervalTreeMap(intervals2)));

        // Test case 3: No right intervals
        int[][] intervals3 = { { 1, 4 }, { 2, 3 }, { 3, 4 } };
        System.out.println("\nTest 3 - Expected: [-1,-1,-1]");
        System.out.println("Approach 1: " + Arrays.toString(solver.findRightInterval(intervals3)));
        System.out.println("Approach 2: " + Arrays.toString(solver.findRightIntervalTreeMap(intervals3)));

        // Test case 4: Single interval
        int[][] intervals4 = { { 1, 2 } };
        System.out.println("\nTest 4 - Expected: [-1]");
        System.out.println("Approach 1: " + Arrays.toString(solver.findRightInterval(intervals4)));

        // Test follow-ups
        System.out.println("\nFollow-up tests:");

        // All right intervals
        List<List<Integer>> allRightIntervals = solver.findAllRightIntervals(intervals1);
        System.out.println("All right intervals for test 1:");
        for (int i = 0; i < allRightIntervals.size(); i++) {
            System.out.println("Interval " + i + ": " + allRightIntervals.get(i));
        }

        // Left intervals
        int[] leftIntervals = solver.findLeftInterval(intervals1);
        System.out.println("Left intervals: " + Arrays.toString(leftIntervals));

        // Stream processing
        IntervalProcessor processor = new IntervalProcessor();
        processor.addInterval(1, 2);
        processor.addInterval(2, 3);
        processor.addInterval(0, 1);
        processor.addInterval(3, 4);

        System.out.println("Stream processing - Right interval for end=2: " + processor.findRightInterval(2));
        System.out.println("Stream processing - Right interval for end=1: " + processor.findRightInterval(1));

        // Validation
        int[] result1 = solver.findRightInterval(intervals1);
        System.out.println("Result validation: " + solver.isValidResult(intervals1, result1));
    }
}
