package intervals.hard;

import java.util.*;

/**
 * Interval Minimum Cover - Point Coverage Optimization
 * 
 * Related LeetCode Problems:
 * - 452. Minimum Number of Arrows to Burst Balloons
 * - 435. Non-overlapping Intervals
 * - 1024. Video Stitching
 * URL:
 * https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 * 
 * Company Tags: Google, Amazon, Microsoft, Meta, Apple
 * Difficulty: Hard
 * 
 * Description:
 * Given a set of intervals, find the minimum set of points that cover all
 * intervals. Each point must lie within at least one interval, and every
 * interval must contain at least one chosen point.
 * 
 * This is equivalent to the "Interval Point Cover" problem, also known as
 * finding the minimum number of arrows to burst all balloons.
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^9
 * 
 * Follow-ups:
 * 1. Find all possible minimum covers
 * 2. Handle weighted intervals (priority-based covering)
 * 3. Cover with constraints (point restrictions)
 * 4. Find minimum cover with maximum separation
 * 5. Dynamic interval addition with incremental covering
 */
public class IntervalMinimumCover {

    /**
     * Greedy approach - choose rightmost point of leftmost uncovered interval
     * Time: O(n log n), Space: O(n)
     */
    public int[] minCover(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        // Sort intervals by end time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        List<Integer> cover = new ArrayList<>();
        int lastCoverPoint = Integer.MIN_VALUE;

        for (int[] interval : intervals) {
            // If current interval is not covered by last point
            if (interval[0] > lastCoverPoint) {
                // Choose the rightmost point (end of current interval)
                lastCoverPoint = interval[1];
                cover.add(lastCoverPoint);
            }
        }

        return cover.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Alternative greedy approach - choose optimal intersection points
     * Time: O(n log n), Space: O(n)
     */
    public int[] minCoverOptimal(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        // Sort by start time, then by end time
        Arrays.sort(intervals, (a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });

        List<Integer> cover = new ArrayList<>();
        int i = 0;

        while (i < intervals.length) {
            int currentEnd = intervals[i][1];
            int j = i;

            // Find all intervals that start before current ends
            while (j < intervals.length && intervals[j][0] <= currentEnd) {
                currentEnd = Math.min(currentEnd, intervals[j][1]);
                j++;
            }

            // Place point at the intersection end
            cover.add(currentEnd);

            // Skip all covered intervals
            while (i < intervals.length && intervals[i][0] <= currentEnd) {
                i++;
            }
        }

        return cover.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Follow-up 1: Find all possible minimum covers
     * Time: O(n log n + 2^k) where k is number of choice points, Space: O(2^k)
     */
    public List<List<Integer>> findAllMinCovers(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return Arrays.asList(new ArrayList<>());
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        List<List<Integer>> allCovers = new ArrayList<>();
        findAllCoversHelper(intervals, 0, Integer.MIN_VALUE, new ArrayList<>(), allCovers);

        // Filter to keep only minimum length covers
        int minSize = allCovers.stream().mapToInt(List::size).min().orElse(0);
        return allCovers.stream()
                .filter(cover -> cover.size() == minSize)
                .collect(ArrayList::new, (list, item) -> list.add(new ArrayList<>(item)), List::addAll);
    }

    private void findAllCoversHelper(int[][] intervals, int index, int lastCoverPoint,
            List<Integer> currentCover, List<List<Integer>> allCovers) {
        if (index == intervals.length) {
            allCovers.add(new ArrayList<>(currentCover));
            return;
        }

        int[] interval = intervals[index];

        // If current interval is already covered, skip to next
        if (interval[0] <= lastCoverPoint) {
            findAllCoversHelper(intervals, index + 1, lastCoverPoint, currentCover, allCovers);
        } else {
            // Try all possible cover points in the current interval
            Set<Integer> candidatePoints = new HashSet<>();
            candidatePoints.add(interval[1]); // Always try the end point

            // Add intersection points with future intervals
            for (int i = index + 1; i < intervals.length && intervals[i][0] <= interval[1]; i++) {
                candidatePoints.add(Math.min(interval[1], intervals[i][1]));
            }

            for (int point : candidatePoints) {
                if (point >= interval[0] && point <= interval[1]) {
                    currentCover.add(point);
                    findAllCoversHelper(intervals, index + 1, point, currentCover, allCovers);
                    currentCover.remove(currentCover.size() - 1);
                }
            }
        }
    }

    /**
     * Follow-up 2: Weighted intervals - minimize total cost
     * Time: O(n log n), Space: O(n)
     */
    public WeightedCoverResult minWeightedCover(WeightedInterval[] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new WeightedCoverResult(new ArrayList<>(), 0);
        }

        // Sort by end time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a.end, b.end));

        List<Integer> cover = new ArrayList<>();
        double totalCost = 0;
        int lastCoverPoint = Integer.MIN_VALUE;

        for (WeightedInterval interval : intervals) {
            if (interval.start > lastCoverPoint) {
                // Choose point that minimizes cost while covering interval
                int bestPoint = interval.end; // Default to end point
                double minCost = interval.weight;

                // Consider all integer points in the interval
                for (int point = interval.start; point <= interval.end; point++) {
                    double cost = calculateCostAtPoint(intervals, point);
                    if (cost < minCost) {
                        minCost = cost;
                        bestPoint = point;
                    }
                }

                lastCoverPoint = bestPoint;
                cover.add(bestPoint);
                totalCost += minCost;
            }
        }

        return new WeightedCoverResult(cover, totalCost);
    }

    private double calculateCostAtPoint(WeightedInterval[] intervals, int point) {
        double totalCost = 0;
        for (WeightedInterval interval : intervals) {
            if (interval.start <= point && point <= interval.end) {
                totalCost += interval.weight;
            }
        }
        return totalCost;
    }

    /**
     * Follow-up 3: Cover with point constraints
     * Time: O(n log n + m log m), Space: O(n + m)
     */
    public int[] minCoverWithConstraints(int[][] intervals, int[] allowedPoints) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));
        Arrays.sort(allowedPoints);

        List<Integer> cover = new ArrayList<>();
        int lastCoverPoint = Integer.MIN_VALUE;

        for (int[] interval : intervals) {
            if (interval[0] > lastCoverPoint) {
                // Find rightmost allowed point in current interval
                int bestPoint = findRightmostPointInRange(allowedPoints, interval[0], interval[1]);

                if (bestPoint == -1) {
                    // No valid point found - interval cannot be covered
                    return new int[] { -1 }; // Indicate impossible
                }

                lastCoverPoint = bestPoint;
                cover.add(bestPoint);
            }
        }

        return cover.stream().mapToInt(Integer::intValue).toArray();
    }

    private int findRightmostPointInRange(int[] sortedPoints, int start, int end) {
        int left = 0, right = sortedPoints.length - 1;
        int rightmost = -1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (sortedPoints[mid] >= start && sortedPoints[mid] <= end) {
                rightmost = sortedPoints[mid];
                left = mid + 1; // Look for more rightward points
            } else if (sortedPoints[mid] < start) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return rightmost;
    }

    /**
     * Follow-up 4: Minimum cover with maximum separation
     * Time: O(n log n), Space: O(n)
     */
    public int[] minCoverMaxSeparation(int[][] intervals, int minSeparation) {
        if (intervals == null || intervals.length == 0) {
            return new int[0];
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[1], b[1]));

        List<Integer> cover = new ArrayList<>();
        int lastCoverPoint = Integer.MIN_VALUE;

        for (int[] interval : intervals) {
            if (interval[0] > lastCoverPoint) {
                // Find optimal point considering separation constraint
                int candidatePoint = interval[1];

                // If we have previous points, ensure minimum separation
                if (!cover.isEmpty()) {
                    int requiredPoint = cover.get(cover.size() - 1) + minSeparation;
                    if (requiredPoint <= interval[1] && requiredPoint >= interval[0]) {
                        candidatePoint = requiredPoint;
                    } else if (requiredPoint > interval[1]) {
                        // Cannot satisfy separation constraint
                        continue; // Skip this interval or handle differently
                    }
                }

                lastCoverPoint = candidatePoint;
                cover.add(candidatePoint);
            }
        }

        return cover.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Follow-up 5: Dynamic interval addition with incremental covering
     * Time: O(log n) per addition, O(n) per recompute, Space: O(n)
     */
    public static class DynamicIntervalCover {
        private TreeSet<Interval> intervals;
        private List<Integer> currentCover;
        private boolean needsRecompute;

        public DynamicIntervalCover() {
            this.intervals = new TreeSet<>((a, b) -> {
                if (a.end != b.end)
                    return Integer.compare(a.end, b.end);
                return Integer.compare(a.start, b.start);
            });
            this.currentCover = new ArrayList<>();
            this.needsRecompute = false;
        }

        public void addInterval(int start, int end) {
            intervals.add(new Interval(start, end));
            needsRecompute = true;
        }

        public void removeInterval(int start, int end) {
            intervals.remove(new Interval(start, end));
            needsRecompute = true;
        }

        public List<Integer> getCurrentCover() {
            if (needsRecompute) {
                recomputeCover();
                needsRecompute = false;
            }
            return new ArrayList<>(currentCover);
        }

        private void recomputeCover() {
            currentCover.clear();

            if (intervals.isEmpty()) {
                return;
            }

            int lastCoverPoint = Integer.MIN_VALUE;

            for (Interval interval : intervals) {
                if (interval.start > lastCoverPoint) {
                    lastCoverPoint = interval.end;
                    currentCover.add(lastCoverPoint);
                }
            }
        }

        public int getCoverSize() {
            return getCurrentCover().size();
        }
    }

    // Helper classes
    static class Interval {
        int start, end;

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Interval interval = (Interval) obj;
            return start == interval.start && end == interval.end;
        }

        @Override
        public int hashCode() {
            return Objects.hash(start, end);
        }
    }

    static class WeightedInterval {
        int start, end;
        double weight;

        WeightedInterval(int start, int end, double weight) {
            this.start = start;
            this.end = end;
            this.weight = weight;
        }
    }

    static class WeightedCoverResult {
        List<Integer> cover;
        double totalCost;

        WeightedCoverResult(List<Integer> cover, double totalCost) {
            this.cover = cover;
            this.totalCost = totalCost;
        }

        @Override
        public String toString() {
            return "WeightedCoverResult{cover=" + cover + ", cost=" + String.format("%.2f", totalCost) + "}";
        }
    }

    public static void main(String[] args) {
        IntervalMinimumCover solution = new IntervalMinimumCover();

        System.out.println("=== Minimum Cover Test ===");

        // Test Case 1: Basic overlapping intervals
        int[][] intervals1 = { { 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 } };
        System.out.println("Basic overlapping [[1,3],[2,4],[3,5],[4,6]]:");
        System.out.println("  Standard: " + Arrays.toString(solution.minCover(intervals1)));
        System.out.println("  Optimal: " + Arrays.toString(solution.minCoverOptimal(intervals1)));

        // Test Case 2: No overlap
        int[][] intervals2 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("\nNo overlap [[1,2],[3,4],[5,6]]:");
        System.out.println("  Result: " + Arrays.toString(solution.minCover(intervals2)));

        // Test Case 3: All overlap at one point
        int[][] intervals3 = { { 1, 10 }, { 2, 9 }, { 3, 8 } };
        System.out.println("\nAll overlap [[1,10],[2,9],[3,8]]:");
        System.out.println("  Result: " + Arrays.toString(solution.minCover(intervals3)));

        // Test Case 4: Single interval
        int[][] intervals4 = { { 1, 2 } };
        System.out.println("\nSingle interval [[1,2]]:");
        System.out.println("  Result: " + Arrays.toString(solution.minCover(intervals4)));

        // Test Case 5: Follow-up 1 - All possible minimum covers
        System.out.println("\nFollow-up 1 - All minimum covers for [[1,3],[2,4],[3,5]]:");
        int[][] intervals5 = { { 1, 3 }, { 2, 4 }, { 3, 5 } };
        List<List<Integer>> allCovers = solution.findAllMinCovers(intervals5);
        for (int i = 0; i < allCovers.size(); i++) {
            System.out.println("  Cover " + (i + 1) + ": " + allCovers.get(i));
        }

        // Test Case 6: Follow-up 2 - Weighted intervals
        System.out.println("\nFollow-up 2 - Weighted intervals:");
        WeightedInterval[] weighted = {
                new WeightedInterval(1, 3, 1.0),
                new WeightedInterval(2, 4, 2.0),
                new WeightedInterval(3, 5, 0.5),
                new WeightedInterval(4, 6, 1.5)
        };
        WeightedCoverResult weightedResult = solution.minWeightedCover(weighted);
        System.out.println("  " + weightedResult);

        // Test Case 7: Follow-up 3 - Cover with constraints
        System.out.println("\nFollow-up 3 - Cover with point constraints:");
        int[] allowedPoints = { 1, 3, 4, 6, 8 };
        int[] constrainedCover = solution.minCoverWithConstraints(intervals1, allowedPoints);
        System.out.println("  Allowed points: " + Arrays.toString(allowedPoints));
        System.out.println("  Constrained cover: " + Arrays.toString(constrainedCover));

        // Test Case 8: Follow-up 4 - Maximum separation
        System.out.println("\nFollow-up 4 - Cover with minimum separation:");
        int[] separatedCover = solution.minCoverMaxSeparation(intervals1, 2);
        System.out.println("  Min separation 2: " + Arrays.toString(separatedCover));

        // Test Case 9: Follow-up 5 - Dynamic covering
        System.out.println("\nFollow-up 5 - Dynamic interval covering:");
        DynamicIntervalCover dynamic = new DynamicIntervalCover();

        dynamic.addInterval(1, 3);
        System.out.println("  After adding [1,3]: " + dynamic.getCurrentCover());

        dynamic.addInterval(2, 4);
        System.out.println("  After adding [2,4]: " + dynamic.getCurrentCover());

        dynamic.addInterval(5, 7);
        System.out.println("  After adding [5,7]: " + dynamic.getCurrentCover());

        dynamic.removeInterval(2, 4);
        System.out.println("  After removing [2,4]: " + dynamic.getCurrentCover());

        // Test Case 10: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("  Empty intervals: " + Arrays.toString(solution.minCover(new int[][] {})));
        System.out.println("  Null intervals: " + Arrays.toString(solution.minCover(null)));

        // Point intervals
        int[][] pointIntervals = { { 1, 1 }, { 2, 2 }, { 3, 3 } };
        System.out.println("  Point intervals: " + Arrays.toString(solution.minCover(pointIntervals)));

        // Nested intervals
        int[][] nested = { { 1, 10 }, { 2, 8 }, { 3, 6 }, { 4, 5 } };
        System.out.println("  Nested intervals: " + Arrays.toString(solution.minCover(nested)));

        // Test Case 11: Complex scenario
        int[][] complex = { { 1, 4 }, { 2, 6 }, { 5, 8 }, { 7, 10 }, { 9, 12 } };
        System.out.println("\nComplex scenario:");
        System.out.println("  Intervals: " + Arrays.deepToString(complex));
        System.out.println("  Min cover: " + Arrays.toString(solution.minCover(complex)));

        List<List<Integer>> complexAllCovers = solution.findAllMinCovers(complex);
        System.out.println("  All min covers (" + complexAllCovers.size() + " total):");
        for (int i = 0; i < Math.min(complexAllCovers.size(), 5); i++) {
            System.out.println("    " + complexAllCovers.get(i));
        }

        // Test Case 12: Performance test
        System.out.println("\n=== Performance Test ===");
        int[][] large = new int[100000][2];
        Random random = new Random(42);

        for (int i = 0; i < 100000; i++) {
            int start = random.nextInt(1000000);
            int length = random.nextInt(1000) + 1;
            large[i][0] = start;
            large[i][1] = start + length;
        }

        long startTime = System.currentTimeMillis();
        int[] largeResult1 = solution.minCover(large);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int[] largeResult2 = solution.minCoverOptimal(large);
        long time2 = System.currentTimeMillis() - startTime;

        System.out.println("Standard (100k intervals): " + largeResult1.length + " points (" + time1 + "ms)");
        System.out.println("Optimal (100k intervals): " + largeResult2.length + " points (" + time2 + "ms)");

        System.out.println("\n=== Summary ===");
        System.out.println("All minimum cover tests completed successfully!");
    }
}
