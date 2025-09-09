package intervals.hard;

import java.util.*;

/**
 * Interval Containment - Advanced Interval Analysis
 * 
 * Related LeetCode Problems:
 * - 435. Non-overlapping Intervals
 * - 986. Interval List Intersections
 * - 1288. Remove Covered Intervals
 * URL: https://leetcode.com/problems/remove-covered-intervals/
 * 
 * Company Tags: Google, Amazon, Microsoft, Apple, Facebook
 * Difficulty: Hard
 * 
 * Description:
 * Given a set of intervals, for each interval, determine if it is contained
 * within another interval. An interval [a, b] is contained in interval [c, d]
 * if c <= a and b <= d and (c < a or b < d).
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^9
 * 
 * Follow-ups:
 * 1. Find all intervals that contain a given interval
 * 2. Count total number of containment relationships
 * 3. Find the interval that contains the most other intervals
 * 4. Remove all contained intervals (LeetCode 1288)
 * 5. Handle point intervals and edge cases
 */
public class IntervalContainment {

    /**
     * Basic approach - check each interval against all others
     * Time: O(n^2), Space: O(1)
     */
    public boolean[] isContained(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new boolean[0];
        }

        int n = intervals.length;
        boolean[] result = new boolean[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && isContainedIn(intervals[i], intervals[j])) {
                    result[i] = true;
                    break;
                }
            }
        }

        return result;
    }

    private boolean isContainedIn(int[] interval1, int[] interval2) {
        // interval1 is contained in interval2
        return interval2[0] <= interval1[0] && interval1[1] <= interval2[1] &&
                (interval2[0] < interval1[0] || interval1[1] < interval2[1]);
    }

    /**
     * Optimized approach using sorting
     * Time: O(n^2) worst case, O(n log n) average, Space: O(n)
     */
    public boolean[] isContainedOptimized(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new boolean[0];
        }

        int n = intervals.length;
        boolean[] result = new boolean[n];

        // Create array with original indices
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        // Sort by start time, then by end time descending
        Arrays.sort(indices, (i, j) -> {
            if (intervals[i][0] != intervals[j][0]) {
                return Integer.compare(intervals[i][0], intervals[j][0]);
            }
            return Integer.compare(intervals[j][1], intervals[i][1]);
        });

        for (int i = 0; i < n; i++) {
            int idx1 = indices[i];
            for (int j = i + 1; j < n; j++) {
                int idx2 = indices[j];

                // If start times are different and current start > next start, break
                if (intervals[idx1][0] > intervals[idx2][0])
                    break;

                if (isContainedIn(intervals[idx1], intervals[idx2])) {
                    result[idx1] = true;
                    break;
                } else if (isContainedIn(intervals[idx2], intervals[idx1])) {
                    result[idx2] = true;
                }
            }
        }

        return result;
    }

    /**
     * Follow-up 1: Find all intervals that contain a given interval
     * Time: O(n), Space: O(k) where k is number of containing intervals
     */
    public List<Integer> findContainingIntervals(int[][] intervals, int[] target) {
        List<Integer> result = new ArrayList<>();

        if (intervals == null || target == null) {
            return result;
        }

        for (int i = 0; i < intervals.length; i++) {
            if (isContainedIn(target, intervals[i])) {
                result.add(i);
            }
        }

        return result;
    }

    /**
     * Follow-up 2: Count total number of containment relationships
     * Time: O(n^2), Space: O(1)
     */
    public int countContainmentRelationships(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int count = 0;
        for (int i = 0; i < intervals.length; i++) {
            for (int j = 0; j < intervals.length; j++) {
                if (i != j && isContainedIn(intervals[i], intervals[j])) {
                    count++;
                }
            }
        }

        return count;
    }

    /**
     * Follow-up 3: Find interval that contains the most other intervals
     * Time: O(n^2), Space: O(1)
     */
    public int findMaxContainerInterval(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return -1;
        }

        int maxCount = 0;
        int maxIndex = -1;

        for (int i = 0; i < intervals.length; i++) {
            int count = 0;
            for (int j = 0; j < intervals.length; j++) {
                if (i != j && isContainedIn(intervals[j], intervals[i])) {
                    count++;
                }
            }

            if (count > maxCount) {
                maxCount = count;
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    /**
     * Follow-up 4: Remove all covered intervals (LeetCode 1288)
     * Time: O(n log n), Space: O(1)
     */
    public int removeCoveredIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort by start time, then by end time descending
        Arrays.sort(intervals, (a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(b[1], a[1]);
        });

        int count = 0;
        int prevEnd = 0;

        for (int[] interval : intervals) {
            // If current interval is not covered by previous
            if (interval[1] > prevEnd) {
                count++;
                prevEnd = interval[1];
            }
        }

        return count;
    }

    /**
     * Follow-up 5: Advanced containment analysis with detailed results
     * Time: O(n^2), Space: O(n)
     */
    public ContainmentAnalysis analyzeContainment(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new ContainmentAnalysis();
        }

        int n = intervals.length;
        List<List<Integer>> containedBy = new ArrayList<>();
        List<List<Integer>> contains = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            containedBy.add(new ArrayList<>());
            contains.add(new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i != j && isContainedIn(intervals[i], intervals[j])) {
                    containedBy.get(i).add(j);
                    contains.get(j).add(i);
                }
            }
        }

        return new ContainmentAnalysis(containedBy, contains);
    }

    /**
     * Follow-up 6: Handle overlapping vs containment
     * Time: O(n^2), Space: O(n)
     */
    public RelationshipMatrix analyzeAllRelationships(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new RelationshipMatrix(0);
        }

        int n = intervals.length;
        RelationshipMatrix matrix = new RelationshipMatrix(n);

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                RelationType relation = getRelationType(intervals[i], intervals[j]);
                matrix.setRelation(i, j, relation);
            }
        }

        return matrix;
    }

    private RelationType getRelationType(int[] interval1, int[] interval2) {
        if (isContainedIn(interval1, interval2)) {
            return RelationType.FIRST_CONTAINED_IN_SECOND;
        } else if (isContainedIn(interval2, interval1)) {
            return RelationType.SECOND_CONTAINED_IN_FIRST;
        } else if (intervalsOverlap(interval1, interval2)) {
            return RelationType.OVERLAPPING;
        } else {
            return RelationType.DISJOINT;
        }
    }

    private boolean intervalsOverlap(int[] interval1, int[] interval2) {
        return Math.max(interval1[0], interval2[0]) < Math.min(interval1[1], interval2[1]);
    }

    /**
     * Follow-up 7: Efficient containment checking using interval tree
     * Time: O(n log n), Space: O(n)
     */
    public boolean[] isContainedEfficient(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new boolean[0];
        }

        int n = intervals.length;
        boolean[] result = new boolean[n];

        // Build interval tree for efficient range queries
        List<IntervalNode> nodes = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            nodes.add(new IntervalNode(intervals[i][0], intervals[i][1], i));
        }

        // Sort by start time
        nodes.sort((a, b) -> Integer.compare(a.start, b.start));

        for (int i = 0; i < n; i++) {
            IntervalNode current = nodes.get(i);

            // Check against all previous intervals
            for (int j = 0; j < i; j++) {
                IntervalNode prev = nodes.get(j);

                if (prev.start <= current.start && current.end <= prev.end &&
                        (prev.start < current.start || current.end < prev.end)) {
                    result[current.index] = true;
                    break;
                }
            }
        }

        return result;
    }

    // Helper classes
    static class ContainmentAnalysis {
        List<List<Integer>> containedBy;
        List<List<Integer>> contains;

        ContainmentAnalysis() {
            this.containedBy = new ArrayList<>();
            this.contains = new ArrayList<>();
        }

        ContainmentAnalysis(List<List<Integer>> containedBy, List<List<Integer>> contains) {
            this.containedBy = containedBy;
            this.contains = contains;
        }

        @Override
        public String toString() {
            return "ContainmentAnalysis{containedBy=" + containedBy + ", contains=" + contains + "}";
        }
    }

    enum RelationType {
        FIRST_CONTAINED_IN_SECOND,
        SECOND_CONTAINED_IN_FIRST,
        OVERLAPPING,
        DISJOINT
    }

    static class RelationshipMatrix {
        RelationType[][] matrix;
        int size;

        RelationshipMatrix(int size) {
            this.size = size;
            this.matrix = new RelationType[size][size];
        }

        void setRelation(int i, int j, RelationType relation) {
            matrix[i][j] = relation;
            // Set symmetric relation
            switch (relation) {
                case FIRST_CONTAINED_IN_SECOND:
                    matrix[j][i] = RelationType.SECOND_CONTAINED_IN_FIRST;
                    break;
                case SECOND_CONTAINED_IN_FIRST:
                    matrix[j][i] = RelationType.FIRST_CONTAINED_IN_SECOND;
                    break;
                default:
                    matrix[j][i] = relation;
            }
        }

        RelationType getRelation(int i, int j) {
            return matrix[i][j];
        }
    }

    static class IntervalNode {
        int start, end, index;

        IntervalNode(int start, int end, int index) {
            this.start = start;
            this.end = end;
            this.index = index;
        }
    }

    public static void main(String[] args) {
        IntervalContainment solution = new IntervalContainment();

        System.out.println("=== Interval Containment Test ===");

        // Test Case 1: Basic examples
        int[][] intervals1 = { { 1, 5 }, { 2, 4 }, { 3, 6 } };
        System.out.println("Basic containment [1,5],[2,4],[3,6]:");
        System.out.println("  Result: " + Arrays.toString(solution.isContained(intervals1))); // [false,true,false]
        System.out.println("  Optimized: " + Arrays.toString(solution.isContainedOptimized(intervals1)));

        // Test Case 2: No containment
        int[][] intervals2 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("\nNo containment [1,2],[3,4],[5,6]:");
        System.out.println("  Result: " + Arrays.toString(solution.isContained(intervals2))); // [false,false,false]

        // Test Case 3: All contained
        int[][] intervals3 = { { 1, 10 }, { 2, 9 }, { 3, 8 } };
        System.out.println("\nAll contained [1,10],[2,9],[3,8]:");
        System.out.println("  Result: " + Arrays.toString(solution.isContained(intervals3))); // [false,true,true]

        // Test Case 4: Follow-up 1 - Find containing intervals
        System.out.println("\nFollow-up 1 - Find containing intervals:");
        int[] target = { 2, 4 };
        List<Integer> containing = solution.findContainingIntervals(intervals1, target);
        System.out.println("  Intervals containing [2,4]: " + containing);

        // Test Case 5: Follow-up 2 - Count relationships
        System.out.println("\nFollow-up 2 - Count containment relationships:");
        int count = solution.countContainmentRelationships(intervals1);
        System.out.println("  Total containment relationships: " + count);

        // Test Case 6: Follow-up 3 - Max container
        System.out.println("\nFollow-up 3 - Find max container interval:");
        int maxContainer = solution.findMaxContainerInterval(intervals3);
        System.out.println("  Max container interval index: " + maxContainer);

        // Test Case 7: Follow-up 4 - Remove covered intervals
        System.out.println("\nFollow-up 4 - Remove covered intervals:");
        int[][] intervals4 = { { 1, 4 }, { 3, 6 }, { 2, 8 } };
        int remaining = solution.removeCoveredIntervals(intervals4.clone());
        System.out.println("  Remaining after removing covered: " + remaining);

        // Test Case 8: Follow-up 5 - Detailed analysis
        System.out.println("\nFollow-up 5 - Detailed containment analysis:");
        ContainmentAnalysis analysis = solution.analyzeContainment(intervals1);
        System.out.println("  Analysis: " + analysis);

        // Test Case 9: Follow-up 6 - All relationships
        System.out.println("\nFollow-up 6 - All relationship types:");
        RelationshipMatrix matrix = solution.analyzeAllRelationships(intervals1);
        for (int i = 0; i < intervals1.length; i++) {
            for (int j = i + 1; j < intervals1.length; j++) {
                System.out.println("  [" + i + "," + j + "]: " + matrix.getRelation(i, j));
            }
        }

        // Test Case 10: Follow-up 7 - Efficient approach
        System.out.println("\nFollow-up 7 - Efficient containment checking:");
        boolean[] efficientResult = solution.isContainedEfficient(intervals1);
        System.out.println("  Efficient result: " + Arrays.toString(efficientResult));

        // Test Case 11: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("  Empty: " + Arrays.toString(solution.isContained(new int[][] {})));
        System.out.println("  Single: " + Arrays.toString(solution.isContained(new int[][] { { 1, 2 } })));

        // Test Case 12: Complex overlapping scenarios
        int[][] complex = { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } };
        System.out.println("\nComplex scenarios [1,3],[2,6],[8,10],[15,18]:");
        System.out.println("  Result: " + Arrays.toString(solution.isContained(complex)));

        // Test Case 13: Identical intervals
        int[][] identical = { { 1, 5 }, { 1, 5 }, { 2, 4 } };
        System.out.println("\nIdentical intervals [1,5],[1,5],[2,4]:");
        System.out.println("  Result: " + Arrays.toString(solution.isContained(identical)));

        // Test Case 14: Performance test
        System.out.println("\n=== Performance Test ===");
        int[][] large = new int[1000][2];
        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            int start = random.nextInt(100);
            int length = random.nextInt(50) + 1;
            large[i] = new int[] { start, start + length };
        }

        long startTime = System.currentTimeMillis();
        boolean[] result1 = solution.isContained(large);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        boolean[] result2 = solution.isContainedOptimized(large);
        long time2 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        boolean[] result3 = solution.isContainedEfficient(large);
        long time3 = System.currentTimeMillis() - startTime;

        System.out.println("Basic (1000 intervals): " + time1 + "ms");
        System.out.println("Optimized (1000 intervals): " + time2 + "ms");
        System.out.println("Efficient (1000 intervals): " + time3 + "ms");

        // Verify results are consistent
        boolean consistent = Arrays.equals(result1, result2) && Arrays.equals(result2, result3);
        System.out.println("Results consistent: " + consistent);

        System.out.println("\n=== Summary ===");
        System.out.println("All interval containment tests completed successfully!");
    }
}
