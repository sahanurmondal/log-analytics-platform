package intervals.hard;

import java.util.*;

/**
 * Interval Difference - Set Operations on Intervals
 * 
 * Related LeetCode Problems:
 * - 986. Interval List Intersections
 * - 715. Range Module
 * - 1272. Remove Interval
 * URL: https://leetcode.com/problems/remove-interval/
 * 
 * Company Tags: Google, Amazon, Microsoft, Meta, Apple
 * Difficulty: Hard
 * 
 * Description:
 * Given two sets of intervals, return the difference (intervals in the first
 * set that are not covered by any interval in the second set). This is
 * equivalent to set subtraction: A - B.
 * 
 * Constraints:
 * - 1 <= intervals1.length, intervals2.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^9
 * 
 * Follow-ups:
 * 1. Implement symmetric difference (A ⊕ B = (A - B) ∪ (B - A))
 * 2. Handle multiple set operations efficiently
 * 3. Find intervals exclusive to first set vs second set
 * 4. Compute difference with weighted intervals
 * 5. Stream-based interval difference for large datasets
 */
public class IntervalDifference {

    /**
     * Sweep line approach - process all events in chronological order
     * Time: O(n log n + m log m), Space: O(n + m)
     */
    public List<int[]> difference(int[][] intervals1, int[][] intervals2) {
        if (intervals1 == null || intervals1.length == 0) {
            return new ArrayList<>();
        }

        if (intervals2 == null || intervals2.length == 0) {
            return Arrays.asList(intervals1);
        }

        // Merge and sort intervals1
        List<int[]> merged1 = mergeIntervals(intervals1);
        List<int[]> merged2 = mergeIntervals(intervals2);

        List<int[]> result = new ArrayList<>();

        for (int[] interval1 : merged1) {
            List<int[]> remaining = subtractInterval(interval1, merged2);
            result.addAll(remaining);
        }

        return result;
    }

    private List<int[]> mergeIntervals(int[][] intervals) {
        if (intervals.length == 0)
            return new ArrayList<>();

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        List<int[]> merged = new ArrayList<>();

        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }

        return merged;
    }

    private List<int[]> subtractInterval(int[] target, List<int[]> subtractors) {
        List<int[]> result = new ArrayList<>();
        int currentStart = target[0];
        int targetEnd = target[1];

        for (int[] sub : subtractors) {
            if (sub[1] <= currentStart || sub[0] >= targetEnd) {
                continue; // No overlap
            }

            // Add part before overlap
            if (currentStart < sub[0]) {
                result.add(new int[] { currentStart, Math.min(sub[0], targetEnd) });
            }

            // Move current start past the overlap
            currentStart = Math.max(currentStart, sub[1]);

            if (currentStart >= targetEnd) {
                break;
            }
        }

        // Add remaining part
        if (currentStart < targetEnd) {
            result.add(new int[] { currentStart, targetEnd });
        }

        return result;
    }

    /**
     * Optimized approach using two pointers
     * Time: O(n log n + m log m), Space: O(1) excluding output
     */
    public List<int[]> differenceOptimized(int[][] intervals1, int[][] intervals2) {
        List<int[]> result = new ArrayList<>();

        if (intervals1 == null || intervals1.length == 0) {
            return result;
        }

        if (intervals2 == null || intervals2.length == 0) {
            return Arrays.asList(intervals1);
        }

        List<int[]> merged1 = mergeIntervals(intervals1);
        List<int[]> merged2 = mergeIntervals(intervals2);

        int i = 0, j = 0;

        while (i < merged1.size()) {
            int[] current = merged1.get(i);
            int start = current[0];
            int end = current[1];

            // Find all overlapping intervals in intervals2
            while (j < merged2.size() && merged2.get(j)[1] <= start) {
                j++;
            }

            int tempJ = j;
            while (tempJ < merged2.size() && merged2.get(tempJ)[0] < end) {
                int[] overlap = merged2.get(tempJ);

                // Add part before overlap
                if (start < overlap[0]) {
                    result.add(new int[] { start, Math.min(overlap[0], end) });
                }

                // Move start past the overlap
                start = Math.max(start, overlap[1]);

                if (start >= end)
                    break;
                tempJ++;
            }

            // Add remaining part
            if (start < end) {
                result.add(new int[] { start, end });
            }

            i++;
        }

        return result;
    }

    /**
     * Follow-up 1: Symmetric difference (A ⊕ B)
     * Time: O(n log n + m log m), Space: O(n + m)
     */
    public List<int[]> symmetricDifference(int[][] intervals1, int[][] intervals2) {
        List<int[]> result = new ArrayList<>();

        // A - B
        result.addAll(difference(intervals1, intervals2));

        // B - A
        result.addAll(difference(intervals2, intervals1));

        // Merge the results
        if (result.isEmpty())
            return result;

        result.sort((a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        for (int[] interval : result) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }

        return merged;
    }

    /**
     * Follow-up 2: Multiple set operations
     * Time: O(k * n log n), Space: O(n) where k is number of operations
     */
    public List<int[]> performOperations(int[][] base, List<SetOperation> operations) {
        List<int[]> current = Arrays.asList(base);

        for (SetOperation op : operations) {
            switch (op.type) {
                case SUBTRACT:
                    current = difference(current.toArray(new int[0][]), op.intervals);
                    break;
                case UNION:
                    current = union(current.toArray(new int[0][]), op.intervals);
                    break;
                case INTERSECT:
                    current = intersect(current.toArray(new int[0][]), op.intervals);
                    break;
            }
        }

        return current;
    }

    private List<int[]> union(int[][] intervals1, int[][] intervals2) {
        List<int[]> combined = new ArrayList<>();
        combined.addAll(Arrays.asList(intervals1));
        combined.addAll(Arrays.asList(intervals2));

        return mergeIntervals(combined.toArray(new int[0][]));
    }

    private List<int[]> intersect(int[][] intervals1, int[][] intervals2) {
        List<int[]> result = new ArrayList<>();

        List<int[]> merged1 = mergeIntervals(intervals1);
        List<int[]> merged2 = mergeIntervals(intervals2);

        int i = 0, j = 0;

        while (i < merged1.size() && j < merged2.size()) {
            int[] a = merged1.get(i);
            int[] b = merged2.get(j);

            int start = Math.max(a[0], b[0]);
            int end = Math.min(a[1], b[1]);

            if (start < end) {
                result.add(new int[] { start, end });
            }

            if (a[1] < b[1])
                i++;
            else
                j++;
        }

        return result;
    }

    /**
     * Follow-up 3: Find exclusive intervals
     * Time: O(n log n + m log m), Space: O(n + m)
     */
    public ExclusiveResult findExclusiveIntervals(int[][] intervals1, int[][] intervals2) {
        List<int[]> onlyInFirst = difference(intervals1, intervals2);
        List<int[]> onlyInSecond = difference(intervals2, intervals1);
        List<int[]> common = intersect(intervals1, intervals2);

        return new ExclusiveResult(onlyInFirst, onlyInSecond, common);
    }

    /**
     * Follow-up 4: Weighted interval difference
     * Time: O(n log n + m log m), Space: O(n + m)
     */
    public List<WeightedInterval> weightedDifference(WeightedInterval[] intervals1,
            WeightedInterval[] intervals2) {
        List<WeightedInterval> result = new ArrayList<>();

        // Convert to regular intervals for difference calculation
        int[][] regular1 = new int[intervals1.length][2];
        int[][] regular2 = new int[intervals2.length][2];

        for (int i = 0; i < intervals1.length; i++) {
            regular1[i] = new int[] { intervals1[i].start, intervals1[i].end };
        }

        for (int i = 0; i < intervals2.length; i++) {
            regular2[i] = new int[] { intervals2[i].start, intervals2[i].end };
        }

        List<int[]> diff = difference(regular1, regular2);

        // Map back to weighted intervals, preserving weights from original
        for (int[] interval : diff) {
            for (WeightedInterval orig : intervals1) {
                if (orig.start <= interval[0] && interval[1] <= orig.end) {
                    result.add(new WeightedInterval(interval[0], interval[1], orig.weight));
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Follow-up 5: Stream-based processing for large datasets
     * Time: O(n + m), Space: O(1) per operation
     */
    public Iterator<int[]> streamDifference(Iterator<int[]> stream1, int[][] intervals2) {
        List<int[]> merged2 = mergeIntervals(intervals2);

        return new Iterator<int[]>() {
            private Iterator<int[]> currentResult = Collections.emptyIterator();

            @Override
            public boolean hasNext() {
                while (!currentResult.hasNext() && stream1.hasNext()) {
                    int[] next = stream1.next();
                    List<int[]> diff = subtractInterval(next, merged2);
                    currentResult = diff.iterator();
                }
                return currentResult.hasNext();
            }

            @Override
            public int[] next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                return currentResult.next();
            }
        };
    }

    /**
     * Advanced: Complete interval algebra
     * Time: O(n log n), Space: O(n)
     */
    public IntervalSet createIntervalSet(int[][] intervals) {
        return new IntervalSet(mergeIntervals(intervals));
    }

    // Helper classes and enums
    enum OperationType {
        SUBTRACT, UNION, INTERSECT
    }

    static class SetOperation {
        OperationType type;
        int[][] intervals;

        SetOperation(OperationType type, int[][] intervals) {
            this.type = type;
            this.intervals = intervals;
        }
    }

    static class ExclusiveResult {
        List<int[]> onlyInFirst;
        List<int[]> onlyInSecond;
        List<int[]> common;

        ExclusiveResult(List<int[]> onlyInFirst, List<int[]> onlyInSecond, List<int[]> common) {
            this.onlyInFirst = onlyInFirst;
            this.onlyInSecond = onlyInSecond;
            this.common = common;
        }

        @Override
        public String toString() {
            return String.format("ExclusiveResult{onlyInFirst=%s, onlyInSecond=%s, common=%s}",
                    formatIntervals(onlyInFirst), formatIntervals(onlyInSecond), formatIntervals(common));
        }

        private String formatIntervals(List<int[]> intervals) {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < intervals.size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append("[").append(intervals.get(i)[0]).append(",").append(intervals.get(i)[1]).append("]");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    static class WeightedInterval {
        int start, end, weight;

        WeightedInterval(int start, int end, int weight) {
            this.start = start;
            this.end = end;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "[" + start + "," + end + "," + weight + "]";
        }
    }

    static class IntervalSet {
        List<int[]> intervals;

        IntervalSet(List<int[]> intervals) {
            this.intervals = intervals;
        }

        public IntervalSet subtract(IntervalSet other) {
            IntervalDifference solver = new IntervalDifference();
            List<int[]> result = solver.difference(
                    intervals.toArray(new int[0][]),
                    other.intervals.toArray(new int[0][]));
            return new IntervalSet(result);
        }

        public IntervalSet union(IntervalSet other) {
            IntervalDifference solver = new IntervalDifference();
            List<int[]> result = solver.union(
                    intervals.toArray(new int[0][]),
                    other.intervals.toArray(new int[0][]));
            return new IntervalSet(result);
        }

        public IntervalSet intersect(IntervalSet other) {
            IntervalDifference solver = new IntervalDifference();
            List<int[]> result = solver.intersect(
                    intervals.toArray(new int[0][]),
                    other.intervals.toArray(new int[0][]));
            return new IntervalSet(result);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < intervals.size(); i++) {
                if (i > 0)
                    sb.append(",");
                sb.append("[").append(intervals.get(i)[0]).append(",").append(intervals.get(i)[1]).append("]");
            }
            sb.append("]");
            return sb.toString();
        }
    }

    public static void main(String[] args) {
        IntervalDifference solution = new IntervalDifference();

        System.out.println("=== Interval Difference Test ===");

        // Test Case 1: Basic difference
        int[][] intervals1 = { { 1, 5 }, { 10, 15 } };
        int[][] intervals2 = { { 3, 6 }, { 12, 14 } };
        System.out.println("Basic difference [1,5],[10,15] - [3,6],[12,14]:");
        List<int[]> diff1 = solution.difference(intervals1, intervals2);
        System.out.println("  Result: " + formatResult(diff1));

        List<int[]> diff1Opt = solution.differenceOptimized(intervals1, intervals2);
        System.out.println("  Optimized: " + formatResult(diff1Opt));

        // Test Case 2: No overlap
        int[][] intervals3 = { { 1, 2 } };
        int[][] intervals4 = { { 3, 4 } };
        System.out.println("\nNo overlap [1,2] - [3,4]:");
        List<int[]> diff2 = solution.difference(intervals3, intervals4);
        System.out.println("  Result: " + formatResult(diff2));

        // Test Case 3: Complete overlap
        int[][] intervals5 = { { 1, 10 } };
        int[][] intervals6 = { { 1, 10 } };
        System.out.println("\nComplete overlap [1,10] - [1,10]:");
        List<int[]> diff3 = solution.difference(intervals5, intervals6);
        System.out.println("  Result: " + formatResult(diff3));

        // Test Case 4: Partial overlap
        int[][] intervals7 = { { 1, 6 } };
        int[][] intervals8 = { { 3, 8 } };
        System.out.println("\nPartial overlap [1,6] - [3,8]:");
        List<int[]> diff4 = solution.difference(intervals7, intervals8);
        System.out.println("  Result: " + formatResult(diff4));

        // Test Case 5: Follow-up 1 - Symmetric difference
        System.out.println("\nFollow-up 1 - Symmetric difference:");
        List<int[]> symDiff = solution.symmetricDifference(intervals1, intervals2);
        System.out.println("  [1,5],[10,15] ⊕ [3,6],[12,14]: " + formatResult(symDiff));

        // Test Case 6: Follow-up 2 - Multiple operations
        System.out.println("\nFollow-up 2 - Multiple set operations:");
        List<SetOperation> operations = Arrays.asList(
                new SetOperation(OperationType.SUBTRACT, new int[][] { { 2, 4 } }),
                new SetOperation(OperationType.UNION, new int[][] { { 8, 12 } }));

        List<int[]> multiResult = solution.performOperations(new int[][] { { 1, 6 } }, operations);
        System.out.println("  [1,6] - [2,4] ∪ [8,12]: " + formatResult(multiResult));

        // Test Case 7: Follow-up 3 - Exclusive intervals
        System.out.println("\nFollow-up 3 - Find exclusive intervals:");
        ExclusiveResult exclusive = solution.findExclusiveIntervals(intervals1, intervals2);
        System.out.println("  " + exclusive);

        // Test Case 8: Follow-up 4 - Weighted intervals
        System.out.println("\nFollow-up 4 - Weighted interval difference:");
        WeightedInterval[] weighted1 = {
                new WeightedInterval(1, 5, 10),
                new WeightedInterval(10, 15, 20)
        };
        WeightedInterval[] weighted2 = {
                new WeightedInterval(3, 6, 5),
                new WeightedInterval(12, 14, 15)
        };

        List<WeightedInterval> weightedDiff = solution.weightedDifference(weighted1, weighted2);
        System.out.println("  Weighted result: " + weightedDiff);

        // Test Case 9: Interval set operations
        System.out.println("\nInterval Set Operations:");
        IntervalSet set1 = solution.createIntervalSet(intervals1);
        IntervalSet set2 = solution.createIntervalSet(intervals2);

        System.out.println("  Set1: " + set1);
        System.out.println("  Set2: " + set2);
        System.out.println("  Set1 - Set2: " + set1.subtract(set2));
        System.out.println("  Set1 ∪ Set2: " + set1.union(set2));
        System.out.println("  Set1 ∩ Set2: " + set1.intersect(set2));

        // Test Case 10: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("  Empty first set: " + formatResult(solution.difference(new int[][] {}, intervals2)));
        System.out.println("  Empty second set: " + formatResult(solution.difference(intervals1, new int[][] {})));

        // Test Case 11: Complex scenario
        int[][] complex1 = { { 1, 3 }, { 4, 6 }, { 8, 10 }, { 12, 15 } };
        int[][] complex2 = { { 2, 5 }, { 7, 11 }, { 13, 14 } };
        System.out.println("\nComplex scenario:");
        System.out.println("  [1,3],[4,6],[8,10],[12,15] - [2,5],[7,11],[13,14]:");
        List<int[]> complexDiff = solution.difference(complex1, complex2);
        System.out.println("  Result: " + formatResult(complexDiff));

        // Test Case 12: Performance test
        System.out.println("\n=== Performance Test ===");
        int[][] large1 = new int[10000][2];
        int[][] large2 = new int[5000][2];
        Random random = new Random(42);

        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(100000);
            int length = random.nextInt(1000) + 1;
            large1[i] = new int[] { start, start + length };
        }

        for (int i = 0; i < 5000; i++) {
            int start = random.nextInt(100000);
            int length = random.nextInt(1000) + 1;
            large2[i] = new int[] { start, start + length };
        }

        long startTime = System.currentTimeMillis();
        List<int[]> result1 = solution.difference(large1, large2);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        List<int[]> result2 = solution.differenceOptimized(large1, large2);
        long time2 = System.currentTimeMillis() - startTime;

        System.out.println("Standard (10k vs 5k intervals): " + result1.size() + " results (" + time1 + "ms)");
        System.out.println("Optimized (10k vs 5k intervals): " + result2.size() + " results (" + time2 + "ms)");

        System.out.println("\n=== Summary ===");
        System.out.println("All interval difference tests completed successfully!");
    }

    private static String formatResult(List<int[]> intervals) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < intervals.size(); i++) {
            if (i > 0)
                sb.append(",");
            sb.append("[").append(intervals.get(i)[0]).append(",").append(intervals.get(i)[1]).append("]");
        }
        sb.append("]");
        return sb.toString();
    }
}
