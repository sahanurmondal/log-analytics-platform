package intervals.hard;

import java.util.*;

/**
 * LeetCode 352: Data Stream as Disjoint Intervals
 * https://leetcode.com/problems/data-stream-as-disjoint-intervals/
 * 
 * Companies: Google, Amazon, Meta, Microsoft, Apple
 * Frequency: Medium (Asked in 100+ interviews)
 *
 * Description:
 * Given a data stream of integers, implement a data structure that:
 * 1. void addNum(int value) - Adds the integer value to the stream
 * 2. int[][] getIntervals() - Returns a summary of the integers in the stream
 * currently as a list of disjoint intervals [starti, endi]
 *
 * Constraints:
 * - 0 <= value <= 10^4
 * - At most 3 * 10^4 calls will be made to addNum and getIntervals
 * - It is guaranteed that there will be no integer values with the same value
 * added multiple times
 * 
 * Follow-up Questions:
 * 1. What if we need to support removal of numbers?
 * 2. Can you handle range updates efficiently?
 * 3. What if we need to query if a specific number exists?
 * 4. How would you handle very large ranges efficiently?
 */
public class DataStreamAsDisjointIntervals {

    // Approach 1: TreeMap with Interval Merging - O(log n) addNum, O(n)
    // getIntervals
    static class SummaryRanges {
        protected TreeMap<Integer, Integer> intervals; // start -> end

        public SummaryRanges() {
            intervals = new TreeMap<>();
        }

        public void addNum(int value) {
            // Check if value is already covered
            Integer floorKey = intervals.floorKey(value);
            if (floorKey != null && intervals.get(floorKey) >= value) {
                return; // Already covered
            }

            // Check if value extends an existing interval or creates a new one
            Integer lowerKey = intervals.lowerKey(value);
            Integer higherKey = intervals.higherKey(value);

            boolean mergeWithLower = lowerKey != null && intervals.get(lowerKey) == value - 1;
            boolean mergeWithHigher = higherKey != null && higherKey == value + 1;

            if (mergeWithLower && mergeWithHigher) {
                // Merge three intervals: lower, [value, value], higher
                int newEnd = intervals.get(higherKey);
                intervals.put(lowerKey, newEnd);
                intervals.remove(higherKey);
            } else if (mergeWithLower) {
                // Extend lower interval
                intervals.put(lowerKey, intervals.get(lowerKey) + 1);
            } else if (mergeWithHigher) {
                // Extend higher interval by moving it
                int higherEnd = intervals.get(higherKey);
                intervals.remove(higherKey);
                intervals.put(value, higherEnd);
            } else {
                // Create new interval
                intervals.put(value, value);
            }
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

        // Helper method to get intervals as list
        public List<int[]> getIntervalsAsList() {
            List<int[]> result = new ArrayList<>();
            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                result.add(new int[] { entry.getKey(), entry.getValue() });
            }
            return result;
        }
    }

    // Approach 2: TreeSet with Custom Interval Class - O(log n) addNum, O(n)
    // getIntervals
    static class SummaryRangesTreeSet {
        private TreeSet<Interval> intervals;

        class Interval implements Comparable<Interval> {
            int start, end;

            Interval(int start, int end) {
                this.start = start;
                this.end = end;
            }

            @Override
            public int compareTo(Interval other) {
                return Integer.compare(this.start, other.start);
            }

            @Override
            public boolean equals(Object obj) {
                if (this == obj)
                    return true;
                if (!(obj instanceof Interval))
                    return false;
                Interval other = (Interval) obj;
                return start == other.start && end == other.end;
            }

            @Override
            public int hashCode() {
                return Objects.hash(start, end);
            }
        }

        public SummaryRangesTreeSet() {
            intervals = new TreeSet<>();
        }

        public void addNum(int value) {
            Interval newInterval = new Interval(value, value);

            // Find intervals that might merge
            Interval lower = intervals.floor(newInterval);
            Interval higher = intervals.ceiling(newInterval);

            // Check if already covered
            if (lower != null && lower.end >= value) {
                return;
            }

            // Check merging possibilities
            boolean mergeWithLower = lower != null && lower.end == value - 1;
            boolean mergeWithHigher = higher != null && higher.start == value + 1;

            if (mergeWithLower && mergeWithHigher) {
                // Merge all three
                lower.end = higher.end;
                intervals.remove(higher);
            } else if (mergeWithLower) {
                // Extend lower
                lower.end = value;
            } else if (mergeWithHigher) {
                // Extend higher
                higher.start = value;
            } else {
                // Add new interval
                intervals.add(newInterval);
            }
        }

        public int[][] getIntervals() {
            int[][] result = new int[intervals.size()][2];
            int i = 0;
            for (Interval interval : intervals) {
                result[i][0] = interval.start;
                result[i][1] = interval.end;
                i++;
            }
            return result;
        }
    }

    // Follow-up 1: Support removal
    static class SummaryRangesWithRemoval {
        private TreeMap<Integer, Integer> intervals;

        public SummaryRangesWithRemoval() {
            intervals = new TreeMap<>();
        }

        public void addNum(int value) {
            // Same logic as SummaryRanges.addNum()
            Integer floorKey = intervals.floorKey(value);
            if (floorKey != null && intervals.get(floorKey) >= value) {
                return;
            }

            Integer lowerKey = intervals.lowerKey(value);
            Integer higherKey = intervals.higherKey(value);

            boolean mergeWithLower = lowerKey != null && intervals.get(lowerKey) == value - 1;
            boolean mergeWithHigher = higherKey != null && higherKey == value + 1;

            if (mergeWithLower && mergeWithHigher) {
                int newEnd = intervals.get(higherKey);
                intervals.put(lowerKey, newEnd);
                intervals.remove(higherKey);
            } else if (mergeWithLower) {
                intervals.put(lowerKey, intervals.get(lowerKey) + 1);
            } else if (mergeWithHigher) {
                int higherEnd = intervals.get(higherKey);
                intervals.remove(higherKey);
                intervals.put(value, higherEnd);
            } else {
                intervals.put(value, value);
            }
        }

        public void removeNum(int value) {
            Integer floorKey = intervals.floorKey(value);
            if (floorKey == null || intervals.get(floorKey) < value) {
                return; // Value not present
            }

            int intervalEnd = intervals.get(floorKey);
            intervals.remove(floorKey);

            // Split interval if necessary
            if (floorKey < value) {
                intervals.put(floorKey, value - 1); // Left part
            }
            if (value < intervalEnd) {
                intervals.put(value + 1, intervalEnd); // Right part
            }
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

    // Follow-up 2: Range updates
    static class SummaryRangesWithRangeUpdate {
        private TreeMap<Integer, Integer> intervals;

        public SummaryRangesWithRangeUpdate() {
            intervals = new TreeMap<>();
        }

        public void addRange(int start, int end) {
            if (start > end)
                return;

            // Find all intervals that overlap with [start, end]
            List<Integer> toRemove = new ArrayList<>();
            int newStart = start;
            int newEnd = end;

            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                int intervalStart = entry.getKey();
                int intervalEnd = entry.getValue();

                if (intervalEnd < start - 1)
                    continue; // No overlap
                if (intervalStart > end + 1)
                    break; // No more overlaps

                // Overlap or adjacent
                newStart = Math.min(newStart, intervalStart);
                newEnd = Math.max(newEnd, intervalEnd);
                toRemove.add(intervalStart);
            }

            // Remove overlapping intervals
            for (int key : toRemove) {
                intervals.remove(key);
            }

            // Add merged interval
            intervals.put(newStart, newEnd);
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

    // Follow-up 3: Query existence
    static class SummaryRangesWithQuery extends SummaryRanges {
        public boolean contains(int value) {
            Integer floorKey = intervals.floorKey(value);
            return floorKey != null && intervals.get(floorKey) >= value;
        }

        public int countNumbers() {
            int count = 0;
            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                count += entry.getValue() - entry.getKey() + 1;
            }
            return count;
        }

        public List<Integer> getMissingInRange(int start, int end) {
            List<Integer> missing = new ArrayList<>();

            for (int i = start; i <= end; i++) {
                if (!contains(i)) {
                    missing.add(i);
                }
            }

            return missing;
        }
    }

    // Follow-up 4: Compressed representation for large ranges
    static class CompressedSummaryRanges {
        private TreeMap<Integer, Integer> intervals;
        private Set<Integer> singlePoints; // For sparse data
        private static final int COMPRESSION_THRESHOLD = 10;

        public CompressedSummaryRanges() {
            intervals = new TreeMap<>();
            singlePoints = new TreeSet<>();
        }

        public void addNum(int value) {
            if (singlePoints.contains(value))
                return;

            // Check if it forms a dense region
            int denseCount = 0;
            for (int i = value - COMPRESSION_THRESHOLD; i <= value + COMPRESSION_THRESHOLD; i++) {
                if (singlePoints.contains(i))
                    denseCount++;
            }

            if (denseCount >= COMPRESSION_THRESHOLD / 2) {
                // Convert to interval representation
                compressRegion(value - COMPRESSION_THRESHOLD, value + COMPRESSION_THRESHOLD);
            } else {
                singlePoints.add(value);
            }
        }

        private void compressRegion(int start, int end) {
            List<Integer> toRemove = new ArrayList<>();
            for (int point : singlePoints) {
                if (point >= start && point <= end) {
                    toRemove.add(point);
                }
            }

            for (int point : toRemove) {
                singlePoints.remove(point);
                // Add to interval structure (similar to regular addNum)
                addToInterval(point);
            }
        }

        private void addToInterval(int value) {
            Integer floorKey = intervals.floorKey(value);
            if (floorKey != null && intervals.get(floorKey) >= value) {
                return;
            }

            Integer lowerKey = intervals.lowerKey(value);
            Integer higherKey = intervals.higherKey(value);

            boolean mergeWithLower = lowerKey != null && intervals.get(lowerKey) == value - 1;
            boolean mergeWithHigher = higherKey != null && higherKey == value + 1;

            if (mergeWithLower && mergeWithHigher) {
                int newEnd = intervals.get(higherKey);
                intervals.put(lowerKey, newEnd);
                intervals.remove(higherKey);
            } else if (mergeWithLower) {
                intervals.put(lowerKey, intervals.get(lowerKey) + 1);
            } else if (mergeWithHigher) {
                int higherEnd = intervals.get(higherKey);
                intervals.remove(higherKey);
                intervals.put(value, higherEnd);
            } else {
                intervals.put(value, value);
            }
        }

        public int[][] getIntervals() {
            List<int[]> result = new ArrayList<>();

            // Add intervals
            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                result.add(new int[] { entry.getKey(), entry.getValue() });
            }

            // Add single points as [point, point] intervals
            for (int point : singlePoints) {
                result.add(new int[] { point, point });
            }

            // Sort by start time
            result.sort((a, b) -> a[0] - b[0]);

            return result.toArray(new int[result.size()][]);
        }

        public Map<String, Object> getCompressionStats() {
            Map<String, Object> stats = new HashMap<>();
            stats.put("intervalCount", intervals.size());
            stats.put("singlePointCount", singlePoints.size());
            stats.put("totalRanges", intervals.size() + singlePoints.size());

            int totalNumbers = singlePoints.size();
            for (Map.Entry<Integer, Integer> entry : intervals.entrySet()) {
                totalNumbers += entry.getValue() - entry.getKey() + 1;
            }
            stats.put("totalNumbers", totalNumbers);

            return stats;
        }
    }

    public static void main(String[] args) {
        System.out.println("Testing DataStreamAsDisjointIntervals implementations:\n");

        // Test Case 1: Basic functionality
        System.out.println("=== Test Case 1: Basic TreeMap Implementation ===");
        SummaryRanges summaryRanges = new SummaryRanges();

        summaryRanges.addNum(1);
        System.out.println("After adding 1: " + Arrays.deepToString(summaryRanges.getIntervals()));
        // Expected: [[1,1]]

        summaryRanges.addNum(3);
        System.out.println("After adding 3: " + Arrays.deepToString(summaryRanges.getIntervals()));
        // Expected: [[1,1],[3,3]]

        summaryRanges.addNum(7);
        System.out.println("After adding 7: " + Arrays.deepToString(summaryRanges.getIntervals()));
        // Expected: [[1,1],[3,3],[7,7]]

        summaryRanges.addNum(2);
        System.out.println("After adding 2: " + Arrays.deepToString(summaryRanges.getIntervals()));
        // Expected: [[1,3],[7,7]]

        summaryRanges.addNum(6);
        System.out.println("After adding 6: " + Arrays.deepToString(summaryRanges.getIntervals()));
        // Expected: [[1,3],[6,7]]

        // Test Case 2: TreeSet implementation comparison
        System.out.println("\n=== Test Case 2: TreeSet Implementation Comparison ===");
        SummaryRangesTreeSet treeSetImpl = new SummaryRangesTreeSet();

        int[] testNums = { 1, 3, 7, 2, 6, 4, 5 };
        for (int num : testNums) {
            summaryRanges.addNum(num);
            treeSetImpl.addNum(num);
        }

        int[][] treeMapResult = summaryRanges.getIntervals();
        int[][] treeSetResult = treeSetImpl.getIntervals();

        System.out.println("TreeMap result: " + Arrays.deepToString(treeMapResult));
        System.out.println("TreeSet result: " + Arrays.deepToString(treeSetResult));
        System.out.println("Results match: " + Arrays.deepEquals(treeMapResult, treeSetResult));

        // Test Case 3: Follow-up 1 - Removal
        System.out.println("\n=== Follow-up 1: Removal Support ===");
        SummaryRangesWithRemoval withRemoval = new SummaryRangesWithRemoval();

        // Add numbers to form [1,7]
        for (int i = 1; i <= 7; i++) {
            withRemoval.addNum(i);
        }
        System.out.println("After adding 1-7: " + Arrays.deepToString(withRemoval.getIntervals()));

        withRemoval.removeNum(4);
        System.out.println("After removing 4: " + Arrays.deepToString(withRemoval.getIntervals()));
        // Expected: [[1,3],[5,7]]

        withRemoval.removeNum(1);
        System.out.println("After removing 1: " + Arrays.deepToString(withRemoval.getIntervals()));
        // Expected: [[2,3],[5,7]]

        withRemoval.removeNum(7);
        System.out.println("After removing 7: " + Arrays.deepToString(withRemoval.getIntervals()));
        // Expected: [[2,3],[5,6]]

        // Test Case 4: Follow-up 2 - Range updates
        System.out.println("\n=== Follow-up 2: Range Updates ===");
        SummaryRangesWithRangeUpdate rangeUpdate = new SummaryRangesWithRangeUpdate();

        rangeUpdate.addRange(1, 3);
        System.out.println("After adding range [1,3]: " + Arrays.deepToString(rangeUpdate.getIntervals()));

        rangeUpdate.addRange(6, 8);
        System.out.println("After adding range [6,8]: " + Arrays.deepToString(rangeUpdate.getIntervals()));

        rangeUpdate.addRange(3, 6);
        System.out.println("After adding range [3,6]: " + Arrays.deepToString(rangeUpdate.getIntervals()));
        // Expected: [[1,8]]

        // Test Case 5: Follow-up 3 - Query functionality
        System.out.println("\n=== Follow-up 3: Query Functionality ===");
        SummaryRangesWithQuery withQuery = new SummaryRangesWithQuery();

        for (int i : new int[] { 1, 3, 5, 7, 9 }) {
            withQuery.addNum(i);
        }

        System.out.println("Intervals: " + Arrays.deepToString(withQuery.getIntervals()));
        System.out.println("Contains 3: " + withQuery.contains(3)); // true
        System.out.println("Contains 4: " + withQuery.contains(4)); // false
        System.out.println("Contains 6: " + withQuery.contains(6)); // false
        System.out.println("Total numbers: " + withQuery.countNumbers()); // 5

        List<Integer> missing = withQuery.getMissingInRange(1, 10);
        System.out.println("Missing in range [1,10]: " + missing);

        // Test Case 6: Follow-up 4 - Compressed representation
        System.out.println("\n=== Follow-up 4: Compressed Representation ===");
        CompressedSummaryRanges compressed = new CompressedSummaryRanges();

        // Add sparse numbers
        for (int i = 0; i < 100; i += 10) {
            compressed.addNum(i);
        }

        System.out.println("Sparse intervals: " + Arrays.deepToString(compressed.getIntervals()));
        System.out.println("Compression stats: " + compressed.getCompressionStats());

        // Add dense region
        for (int i = 50; i <= 60; i++) {
            compressed.addNum(i);
        }

        System.out.println("After adding dense region [50,60]:");
        System.out.println("Intervals: " + Arrays.deepToString(compressed.getIntervals()));
        System.out.println("Compression stats: " + compressed.getCompressionStats());

        // Performance testing
        System.out.println("\n=== Performance Comparison ===");

        long start = System.nanoTime();
        SummaryRanges perfTest1 = new SummaryRanges();
        for (int i = 0; i < 1000; i++) {
            perfTest1.addNum(i * 2); // Sparse addition
        }
        int[][] result1 = perfTest1.getIntervals();
        long time1 = System.nanoTime() - start;

        start = System.nanoTime();
        SummaryRangesTreeSet perfTest2 = new SummaryRangesTreeSet();
        for (int i = 0; i < 1000; i++) {
            perfTest2.addNum(i * 2);
        }
        int[][] result2 = perfTest2.getIntervals();
        long time2 = System.nanoTime() - start;

        System.out.println("TreeMap approach: " + time1 / 1_000_000.0 + " ms");
        System.out.println("TreeSet approach: " + time2 / 1_000_000.0 + " ms");
        System.out.println("Results consistent: " + Arrays.deepEquals(result1, result2));
        System.out.println("Number of intervals: " + result1.length);

        // Edge cases
        System.out.println("\n=== Edge Cases ===");
        SummaryRanges edgeTest = new SummaryRanges();

        // Test duplicate addition
        edgeTest.addNum(5);
        edgeTest.addNum(5);
        System.out.println("After adding 5 twice: " + Arrays.deepToString(edgeTest.getIntervals()));

        // Test reverse order
        SummaryRanges reverseTest = new SummaryRanges();
        for (int i = 10; i >= 1; i--) {
            reverseTest.addNum(i);
        }
        System.out.println("After adding 10 to 1: " + Arrays.deepToString(reverseTest.getIntervals()));
        // Expected: [[1,10]]
    }
}
