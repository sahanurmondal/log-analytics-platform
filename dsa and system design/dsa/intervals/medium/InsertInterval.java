package intervals.medium;

import java.util.*;

/**
 * LeetCode 57: Insert Interval
 * https://leetcode.com/problems/insert-interval/
 * 
 * Companies: Meta, Amazon, Google, Microsoft, Apple, LinkedIn
 * Frequency: Very High (Asked in 400+ interviews)
 *
 * Description:
 * You are given an array of non-overlapping intervals where intervals[i] =
 * [starti, endi]
 * represent the start and the end of the ith interval and intervals is sorted
 * in ascending
 * order by starti. You are also given an interval newInterval = [start, end]
 * that represents
 * the start and end of another interval.
 * 
 * Insert newInterval into intervals such that intervals is still sorted in
 * ascending order
 * by starti and intervals still does not have any overlapping intervals (merge
 * overlapping
 * intervals if necessary).
 * 
 * Return intervals after the insertion.
 *
 * Constraints:
 * - 0 <= intervals.length <= 10^4
 * - intervals[i].length == 2
 * - 0 <= starti <= endi <= 10^5
 * - intervals is sorted by starti in ascending order
 * - newInterval.length == 2
 * - 0 <= start <= end <= 10^5
 * 
 * Follow-up Questions:
 * 1. What if multiple intervals need to be inserted?
 * 2. Can you handle the case where intervals are not initially sorted?
 * 3. What if we need to track which intervals were merged?
 * 4. How would you handle interval insertion with priorities?
 */
public class InsertInterval {

    // Approach 1: Linear Scan with Three Phases - O(n) time, O(n) space
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        int i = 0;
        int n = intervals.length;

        // Phase 1: Add all intervals that end before newInterval starts
        while (i < n && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Phase 2: Merge all overlapping intervals with newInterval
        while (i < n && intervals[i][0] <= newInterval[1]) {
            newInterval[0] = Math.min(newInterval[0], intervals[i][0]);
            newInterval[1] = Math.max(newInterval[1], intervals[i][1]);
            i++;
        }
        result.add(newInterval);

        // Phase 3: Add all remaining intervals
        while (i < n) {
            result.add(intervals[i]);
            i++;
        }

        return result.toArray(new int[result.size()][]);
    }

    // Approach 2: Binary Search + Merge - O(n) time, O(n) space
    public int[][] insertBinarySearch(int[][] intervals, int[] newInterval) {
        if (intervals.length == 0) {
            return new int[][] { newInterval };
        }

        // Find insertion position for start
        int startPos = findInsertPosition(intervals, newInterval[0], true);
        int endPos = findInsertPosition(intervals, newInterval[1], false);

        List<int[]> result = new ArrayList<>();

        // Add intervals before merge region
        for (int i = 0; i < startPos; i++) {
            result.add(intervals[i]);
        }

        // Merge overlapping intervals
        int mergeStart = newInterval[0];
        int mergeEnd = newInterval[1];

        for (int i = startPos; i <= endPos && i < intervals.length; i++) {
            if (intervals[i][0] <= newInterval[1] && intervals[i][1] >= newInterval[0]) {
                mergeStart = Math.min(mergeStart, intervals[i][0]);
                mergeEnd = Math.max(mergeEnd, intervals[i][1]);
            }
        }

        result.add(new int[] { mergeStart, mergeEnd });

        // Add intervals after merge region
        int nextIndex = endPos + 1;
        while (nextIndex < intervals.length && intervals[nextIndex][0] <= newInterval[1]) {
            nextIndex++;
        }

        for (int i = nextIndex; i < intervals.length; i++) {
            result.add(intervals[i]);
        }

        return result.toArray(new int[result.size()][]);
    }

    private int findInsertPosition(int[][] intervals, int target, boolean findStart) {
        int left = 0, right = intervals.length - 1;
        int result = intervals.length;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            int compareValue = findStart ? intervals[mid][1] : intervals[mid][0];

            if (compareValue < target) {
                left = mid + 1;
            } else {
                result = mid;
                right = mid - 1;
            }
        }

        return result;
    }

    // Approach 3: TreeMap based (good for multiple insertions) - O(log n) per
    // insertion
    static class IntervalManager {
        private TreeMap<Integer, Integer> intervals; // start -> end

        public IntervalManager(int[][] initialIntervals) {
            intervals = new TreeMap<>();
            for (int[] interval : initialIntervals) {
                addInterval(interval);
            }
        }

        public void addInterval(int[] newInterval) {
            if (newInterval == null || newInterval.length != 2)
                return;

            int start = newInterval[0];
            int end = newInterval[1];

            // Find overlapping intervals
            Integer floorKey = intervals.floorKey(end);
            Integer ceilingKey = intervals.ceilingKey(start);

            List<Integer> toRemove = new ArrayList<>();

            // Check all potentially overlapping intervals
            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                int intervalStart = entry.getKey();
                int intervalEnd = entry.getValue();

                if (intervalEnd < start)
                    continue; // No overlap
                if (intervalStart > end)
                    break; // No more overlaps

                // Overlap found - merge
                start = Math.min(start, intervalStart);
                end = Math.max(end, intervalEnd);
                toRemove.add(intervalStart);
            }

            // Remove overlapping intervals
            for (int key : toRemove) {
                intervals.remove(key);
            }

            // Add merged interval
            intervals.put(start, end);
        }

        public int[][] getIntervals() {
            int[][] result = new int[intervals.size()][2];
            int i = 0;
            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                result[i][0] = entry.getKey();
                result[i][1] = entry.getValue();
                i++;
            }
            return result;
        }
    }

    // Follow-up 1: Insert multiple intervals
    public int[][] insertMultiple(int[][] intervals, int[][] newIntervals) {
        List<int[]> result = new ArrayList<>();

        // Add all intervals to a list
        for (int[] interval : intervals) {
            result.add(interval);
        }
        for (int[] interval : newIntervals) {
            result.add(interval);
        }

        // Sort by start time
        result.sort((a, b) -> a[0] - b[0]);

        // Merge overlapping intervals
        return mergeIntervals(result.toArray(new int[result.size()][]));
    }

    private int[][] mergeIntervals(int[][] intervals) {
        if (intervals.length <= 1)
            return intervals;

        List<int[]> merged = new ArrayList<>();
        merged.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] current = intervals[i];
            int[] last = merged.get(merged.size() - 1);

            if (current[0] <= last[1]) {
                // Overlapping - merge
                last[1] = Math.max(last[1], current[1]);
            } else {
                // Non-overlapping - add new interval
                merged.add(current);
            }
        }

        return merged.toArray(new int[merged.size()][]);
    }

    // Follow-up 2: Handle unsorted intervals
    public int[][] insertUnsorted(int[][] intervals, int[] newInterval) {
        // First sort the intervals
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        // Then use regular insert
        return insert(intervals, newInterval);
    }

    // Follow-up 3: Track merged intervals
    public InsertResult insertWithTracking(int[][] intervals, int[] newInterval) {
        List<int[]> result = new ArrayList<>();
        List<int[]> merged = new ArrayList<>();
        int i = 0;

        // Add intervals before overlap
        while (i < intervals.length && intervals[i][1] < newInterval[0]) {
            result.add(intervals[i]);
            i++;
        }

        // Collect and merge overlapping intervals
        int mergeStart = newInterval[0];
        int mergeEnd = newInterval[1];

        while (i < intervals.length && intervals[i][0] <= newInterval[1]) {
            merged.add(intervals[i]);
            mergeStart = Math.min(mergeStart, intervals[i][0]);
            mergeEnd = Math.max(mergeEnd, intervals[i][1]);
            i++;
        }

        int[] finalInterval = new int[] { mergeStart, mergeEnd };
        result.add(finalInterval);

        // Add remaining intervals
        while (i < intervals.length) {
            result.add(intervals[i]);
            i++;
        }

        return new InsertResult(
                result.toArray(new int[result.size()][]),
                merged.toArray(new int[merged.size()][]),
                finalInterval);
    }

    static class InsertResult {
        public final int[][] intervals;
        public final int[][] mergedIntervals;
        public final int[] resultInterval;

        public InsertResult(int[][] intervals, int[][] mergedIntervals, int[] resultInterval) {
            this.intervals = intervals;
            this.mergedIntervals = mergedIntervals;
            this.resultInterval = resultInterval;
        }
    }

    // Follow-up 4: Priority-based insertion
    public int[][] insertWithPriority(int[][] intervals, int[] priorities,
            int[] newInterval, int newPriority) {
        List<IntervalWithPriority> intervalList = new ArrayList<>();

        // Add existing intervals with priorities
        for (int i = 0; i < intervals.length; i++) {
            intervalList.add(new IntervalWithPriority(intervals[i], priorities[i]));
        }

        // Add new interval
        intervalList.add(new IntervalWithPriority(newInterval, newPriority));

        // Sort by start time, then by priority (higher priority first)
        intervalList.sort((a, b) -> {
            if (a.interval[0] != b.interval[0]) {
                return a.interval[0] - b.interval[0];
            }
            return b.priority - a.priority; // Higher priority first
        });

        // Merge intervals considering priorities
        List<int[]> result = new ArrayList<>();

        for (IntervalWithPriority current : intervalList) {
            if (result.isEmpty()) {
                result.add(current.interval.clone());
                continue;
            }

            int[] last = result.get(result.size() - 1);

            if (current.interval[0] <= last[1]) {
                // Overlapping - merge with higher priority interval winning conflicts
                last[1] = Math.max(last[1], current.interval[1]);
            } else {
                result.add(current.interval.clone());
            }
        }

        return result.toArray(new int[result.size()][]);
    }

    static class IntervalWithPriority {
        int[] interval;
        int priority;

        IntervalWithPriority(int[] interval, int priority) {
            this.interval = interval;
            this.priority = priority;
        }
    }

    // Helper: Validate intervals are non-overlapping and sorted
    public boolean isValidIntervalSet(int[][] intervals) {
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i - 1][1] || intervals[i][0] < intervals[i - 1][0]) {
                return false;
            }
        }
        return true;
    }

    // Helper: Count total coverage
    public int getTotalCoverage(int[][] intervals) {
        int total = 0;
        for (int[] interval : intervals) {
            total += interval[1] - interval[0] + 1;
        }
        return total;
    }

    // Helper: Find gaps in intervals
    public List<int[]> findGaps(int[][] intervals) {
        List<int[]> gaps = new ArrayList<>();

        for (int i = 1; i < intervals.length; i++) {
            int gapStart = intervals[i - 1][1] + 1;
            int gapEnd = intervals[i][0] - 1;

            if (gapStart <= gapEnd) {
                gaps.add(new int[] { gapStart, gapEnd });
            }
        }

        return gaps;
    }

    public static void main(String[] args) {
        InsertInterval solution = new InsertInterval();

        // Test Case 1: Insert in middle with merge
        int[][] intervals1 = { { 1, 3 }, { 6, 9 } };
        int[] newInterval1 = { 2, 5 };

        int[][] result1 = solution.insert(intervals1, newInterval1);
        System.out.println("Test 1 - Insert [2,5] into [[1,3],[6,9]]:");
        System.out.println("Result: " + Arrays.deepToString(result1));
        // Expected: [[1,5],[6,9]]

        // Test Case 2: Insert with multiple merges
        int[][] intervals2 = { { 1, 2 }, { 3, 5 }, { 6, 7 }, { 8, 10 }, { 12, 16 } };
        int[] newInterval2 = { 4, 8 };

        int[][] result2 = solution.insert(intervals2, newInterval2);
        System.out.println("\nTest 2 - Insert [4,8] with multiple merges:");
        System.out.println("Result: " + Arrays.deepToString(result2));
        // Expected: [[1,2],[3,10],[12,16]]

        // Test Case 3: Insert at beginning
        int[][] intervals3 = { { 3, 5 }, { 12, 15 } };
        int[] newInterval3 = { 1, 2 };

        int[][] result3 = solution.insert(intervals3, newInterval3);
        System.out.println("\nTest 3 - Insert at beginning:");
        System.out.println("Result: " + Arrays.deepToString(result3));
        // Expected: [[1,2],[3,5],[12,15]]

        // Test Case 4: Insert at end
        int[][] intervals4 = { { 1, 5 } };
        int[] newInterval4 = { 6, 8 };

        int[][] result4 = solution.insert(intervals4, newInterval4);
        System.out.println("\nTest 4 - Insert at end:");
        System.out.println("Result: " + Arrays.deepToString(result4));
        // Expected: [[1,5],[6,8]]

        // Test Case 5: Empty intervals
        int[][] intervals5 = {};
        int[] newInterval5 = { 5, 7 };

        int[][] result5 = solution.insert(intervals5, newInterval5);
        System.out.println("\nTest 5 - Empty intervals:");
        System.out.println("Result: " + Arrays.deepToString(result5));
        // Expected: [[5,7]]

        // Compare with binary search approach
        int[][] result1BS = solution.insertBinarySearch(intervals1.clone(), newInterval1);
        System.out.println("\nBinary search approach matches: " +
                Arrays.deepEquals(result1, result1BS));

        // Follow-up 1: Multiple intervals
        System.out.println("\n=== Follow-up 1: Multiple intervals ===");
        int[][] multipleNew = { { 2, 5 }, { 8, 11 } };
        int[][] multiResult = solution.insertMultiple(intervals1, multipleNew);
        System.out.println("Insert multiple intervals: " + Arrays.deepToString(multiResult));

        // Follow-up 2: Unsorted intervals
        System.out.println("\n=== Follow-up 2: Unsorted intervals ===");
        int[][] unsorted = { { 6, 9 }, { 1, 3 } };
        int[][] unsortedResult = solution.insertUnsorted(unsorted, newInterval1);
        System.out.println("Insert into unsorted: " + Arrays.deepToString(unsortedResult));

        // Follow-up 3: Track merged intervals
        System.out.println("\n=== Follow-up 3: Track merged intervals ===");
        InsertResult tracked = solution.insertWithTracking(intervals2, newInterval2);
        System.out.println("Final intervals: " + Arrays.deepToString(tracked.intervals));
        System.out.println("Merged intervals: " + Arrays.deepToString(tracked.mergedIntervals));
        System.out.println("Result interval: " + Arrays.toString(tracked.resultInterval));

        // Follow-up 4: Priority-based insertion
        System.out.println("\n=== Follow-up 4: Priority-based insertion ===");
        int[] priorities = { 1, 3, 2, 1, 2 }; // Higher number = higher priority
        int[][] priorityResult = solution.insertWithPriority(intervals2, priorities,
                newInterval2, 5);
        System.out.println("Priority-based result: " + Arrays.deepToString(priorityResult));

        // TreeMap approach testing
        System.out.println("\n=== TreeMap Approach ===");
        IntervalManager manager = new IntervalManager(intervals1);
        System.out.println("Initial: " + Arrays.deepToString(manager.getIntervals()));

        manager.addInterval(newInterval1);
        System.out.println("After insert: " + Arrays.deepToString(manager.getIntervals()));

        manager.addInterval(new int[] { 10, 12 });
        System.out.println("After another insert: " + Arrays.deepToString(manager.getIntervals()));

        // Helper methods testing
        System.out.println("\n=== Helper Methods ===");
        System.out.println("Is valid interval set: " + solution.isValidIntervalSet(result1));
        System.out.println("Total coverage: " + solution.getTotalCoverage(result1));

        List<int[]> gaps = solution.findGaps(result1);
        System.out.println("Gaps found: " + gaps.size());
        for (int[] gap : gaps) {
            System.out.println("Gap: " + Arrays.toString(gap));
        }

        // Performance comparison
        System.out.println("\n=== Performance Testing ===");

        // Generate large test case
        int[][] largeIntervals = new int[1000][2];
        for (int i = 0; i < 1000; i++) {
            largeIntervals[i] = new int[] { i * 3, i * 3 + 1 };
        }
        int[] largeNewInterval = { 500, 1500 };

        long start = System.nanoTime();
        int[][] linearResult = solution.insert(largeIntervals.clone(), largeNewInterval);
        long linearTime = System.nanoTime() - start;

        start = System.nanoTime();
        int[][] bsResult = solution.insertBinarySearch(largeIntervals.clone(), largeNewInterval);
        long bsTime = System.nanoTime() - start;

        System.out.println("Linear approach: " + linearTime / 1_000_000.0 + " ms");
        System.out.println("Binary search approach: " + bsTime / 1_000_000.0 + " ms");
        System.out.println("Results match: " + Arrays.deepEquals(linearResult, bsResult));
        System.out.println("Result length: " + linearResult.length);

        // Edge case testing
        System.out.println("\n=== Edge Cases ===");

        // Complete overlap
        int[][] edgeCase1 = { { 1, 5 } };
        int[] newEdge1 = { 2, 3 };
        int[][] edgeResult1 = solution.insert(edgeCase1, newEdge1);
        System.out.println("Complete containment: " + Arrays.deepToString(edgeResult1));

        // New interval contains existing
        int[][] edgeCase2 = { { 2, 3 } };
        int[] newEdge2 = { 1, 5 };
        int[][] edgeResult2 = solution.insert(edgeCase2, newEdge2);
        System.out.println("New contains existing: " + Arrays.deepToString(edgeResult2));

        // Adjacent intervals
        int[][] edgeCase3 = { { 1, 2 }, { 4, 5 } };
        int[] newEdge3 = { 3, 3 };
        int[][] edgeResult3 = solution.insert(edgeCase3, newEdge3);
        System.out.println("Fill gap: " + Arrays.deepToString(edgeResult3));
    }
}
