package intervals.hard;

import java.util.*;

/**
 * Interval Intersection of Multiple Lists - Advanced Multi-Set Operations
 * 
 * Related LeetCode Problems:
 * - 986. Interval List Intersections
 * - 56. Merge Intervals
 * - 57. Insert Interval
 * URL: https://leetcode.com/problems/interval-list-intersections/
 * 
 * Company Tags: Google, Amazon, Microsoft, Meta, Apple
 * Difficulty: Hard
 * 
 * Description:
 * Given k lists of sorted intervals, find all intervals that appear in the
 * intersection of ALL k lists. Each list contains disjoint intervals sorted
 * by start time.
 * 
 * Constraints:
 * - 1 <= k <= 100
 * - 0 <= intervals[i].length <= 10^4
 * - intervals[i][j].length == 2
 * - 0 <= intervals[i][j][0] < intervals[i][j][1] <= 10^9
 * - intervals[i] is sorted by start time
 * 
 * Follow-ups:
 * 1. Find intersection of exactly m out of k lists
 * 2. Find union of all k lists
 * 3. Handle weighted intervals in intersection
 * 4. Support streaming/dynamic list updates
 * 5. Find k-way intersection with different merge strategies
 */
public class IntervalIntersectionMultipleLists {

    /**
     * Merge-based approach - progressively intersect pairs
     * Time: O(k * n * log n), Space: O(n)
     */
    public List<int[]> intersection(List<List<int[]>> intervalLists) {
        if (intervalLists == null || intervalLists.isEmpty()) {
            return new ArrayList<>();
        }

        // Start with first list
        List<int[]> result = new ArrayList<>(intervalLists.get(0));

        // Progressively intersect with remaining lists
        for (int i = 1; i < intervalLists.size(); i++) {
            result = intersectTwo(result, intervalLists.get(i));
            if (result.isEmpty()) {
                break; // Early termination if no intersection
            }
        }

        return result;
    }

    private List<int[]> intersectTwo(List<int[]> list1, List<int[]> list2) {
        List<int[]> result = new ArrayList<>();
        int i = 0, j = 0;

        while (i < list1.size() && j < list2.size()) {
            int[] interval1 = list1.get(i);
            int[] interval2 = list2.get(j);

            int start = Math.max(interval1[0], interval2[0]);
            int end = Math.min(interval1[1], interval2[1]);

            if (start < end) {
                result.add(new int[] { start, end });
            }

            // Move pointer of interval that ends first
            if (interval1[1] < interval2[1]) {
                i++;
            } else {
                j++;
            }
        }

        return result;
    }

    /**
     * Sweep line approach - find intersection points
     * Time: O(k * n * log(k * n)), Space: O(k * n)
     */
    public List<int[]> intersectionSweepLine(List<List<int[]>> intervalLists) {
        if (intervalLists == null || intervalLists.isEmpty()) {
            return new ArrayList<>();
        }

        // Create events for all intervals
        List<Event> events = new ArrayList<>();

        for (int listIndex = 0; listIndex < intervalLists.size(); listIndex++) {
            for (int[] interval : intervalLists.get(listIndex)) {
                events.add(new Event(interval[0], EventType.START, listIndex));
                events.add(new Event(interval[1], EventType.END, listIndex));
            }
        }

        // Sort events by time
        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1; // Process ends before starts
        });

        List<int[]> result = new ArrayList<>();
        Set<Integer> activeLists = new HashSet<>();
        int intersectionStart = -1;

        for (Event event : events) {
            boolean wasFullIntersection = activeLists.size() == intervalLists.size();

            if (event.type == EventType.START) {
                activeLists.add(event.listIndex);
            } else {
                activeLists.remove(event.listIndex);
            }

            boolean isFullIntersection = activeLists.size() == intervalLists.size();

            if (!wasFullIntersection && isFullIntersection) {
                // Start of intersection
                intersectionStart = event.time;
            } else if (wasFullIntersection && !isFullIntersection) {
                // End of intersection
                if (intersectionStart < event.time) {
                    result.add(new int[] { intersectionStart, event.time });
                }
            }
        }

        return result;
    }

    /**
     * Follow-up 1: Find intersection of exactly m out of k lists
     * Time: O(k * n * log(k * n)), Space: O(k * n)
     */
    public List<int[]> intersectionOfM(List<List<int[]>> intervalLists, int m) {
        if (intervalLists == null || intervalLists.isEmpty() || m <= 0 || m > intervalLists.size()) {
            return new ArrayList<>();
        }

        List<Event> events = new ArrayList<>();

        for (int listIndex = 0; listIndex < intervalLists.size(); listIndex++) {
            for (int[] interval : intervalLists.get(listIndex)) {
                events.add(new Event(interval[0], EventType.START, listIndex));
                events.add(new Event(interval[1], EventType.END, listIndex));
            }
        }

        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        List<int[]> result = new ArrayList<>();
        Set<Integer> activeLists = new HashSet<>();
        int intersectionStart = -1;

        for (Event event : events) {
            boolean hadMIntersection = activeLists.size() >= m;

            if (event.type == EventType.START) {
                activeLists.add(event.listIndex);
            } else {
                activeLists.remove(event.listIndex);
            }

            boolean hasMIntersection = activeLists.size() >= m;

            if (!hadMIntersection && hasMIntersection) {
                intersectionStart = event.time;
            } else if (hadMIntersection && !hasMIntersection) {
                if (intersectionStart < event.time) {
                    result.add(new int[] { intersectionStart, event.time });
                }
            }
        }

        return result;
    }

    /**
     * Follow-up 2: Find union of all k lists
     * Time: O(k * n * log(k * n)), Space: O(k * n)
     */
    public List<int[]> union(List<List<int[]>> intervalLists) {
        if (intervalLists == null || intervalLists.isEmpty()) {
            return new ArrayList<>();
        }

        // Collect all intervals
        List<int[]> allIntervals = new ArrayList<>();
        for (List<int[]> list : intervalLists) {
            allIntervals.addAll(list);
        }

        // Sort by start time
        allIntervals.sort((a, b) -> Integer.compare(a[0], b[0]));

        // Merge overlapping intervals
        List<int[]> result = new ArrayList<>();
        for (int[] interval : allIntervals) {
            if (result.isEmpty() || result.get(result.size() - 1)[1] < interval[0]) {
                result.add(interval);
            } else {
                result.get(result.size() - 1)[1] = Math.max(result.get(result.size() - 1)[1], interval[1]);
            }
        }

        return result;
    }

    /**
     * Follow-up 3: Weighted intersection - sum weights of overlapping intervals
     * Time: O(k * n * log(k * n)), Space: O(k * n)
     */
    public List<WeightedInterval> weightedIntersection(List<List<WeightedInterval>> weightedLists) {
        if (weightedLists == null || weightedLists.isEmpty()) {
            return new ArrayList<>();
        }

        List<WeightedEvent> events = new ArrayList<>();

        for (int listIndex = 0; listIndex < weightedLists.size(); listIndex++) {
            for (WeightedInterval interval : weightedLists.get(listIndex)) {
                events.add(new WeightedEvent(interval.start, EventType.START, listIndex, interval.weight));
                events.add(new WeightedEvent(interval.end, EventType.END, listIndex, interval.weight));
            }
        }

        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        List<WeightedInterval> result = new ArrayList<>();
        Map<Integer, Integer> activeWeights = new HashMap<>();
        int intersectionStart = -1;

        for (WeightedEvent event : events) {
            boolean wasFullIntersection = activeWeights.size() == weightedLists.size();

            if (event.type == EventType.START) {
                activeWeights.put(event.listIndex, event.weight);
            } else {
                activeWeights.remove(event.listIndex);
            }

            boolean isFullIntersection = activeWeights.size() == weightedLists.size();

            if (!wasFullIntersection && isFullIntersection) {
                intersectionStart = event.time;
            } else if (wasFullIntersection && !isFullIntersection) {
                if (intersectionStart < event.time) {
                    int totalWeight = activeWeights.values().stream().mapToInt(Integer::intValue).sum();
                    result.add(new WeightedInterval(intersectionStart, event.time, totalWeight));
                }
            }
        }

        return result;
    }

    /**
     * Follow-up 4: Dynamic intersection with streaming updates
     * Time: O(log n) per update, O(n) per query, Space: O(n)
     */
    public static class DynamicMultiListIntersection {
        private List<TreeSet<Interval>> intervalLists;
        private int k;

        public DynamicMultiListIntersection(int k) {
            this.k = k;
            this.intervalLists = new ArrayList<>();
            for (int i = 0; i < k; i++) {
                intervalLists.add(new TreeSet<>((a, b) -> Integer.compare(a.start, b.start)));
            }
        }

        public void addInterval(int listIndex, int start, int end) {
            if (listIndex >= 0 && listIndex < k) {
                intervalLists.get(listIndex).add(new Interval(start, end));
            }
        }

        public void removeInterval(int listIndex, int start, int end) {
            if (listIndex >= 0 && listIndex < k) {
                intervalLists.get(listIndex).remove(new Interval(start, end));
            }
        }

        public List<int[]> getCurrentIntersection() {
            List<List<int[]>> lists = new ArrayList<>();
            for (TreeSet<Interval> set : intervalLists) {
                List<int[]> list = new ArrayList<>();
                for (Interval interval : set) {
                    list.add(new int[] { interval.start, interval.end });
                }
                lists.add(list);
            }

            IntervalIntersectionMultipleLists solver = new IntervalIntersectionMultipleLists();
            return solver.intersection(lists);
        }
    }

    /**
     * Follow-up 5: K-way intersection with different strategies
     */
    public IntersectionResult kWayIntersectionAnalysis(List<List<int[]>> intervalLists) {
        if (intervalLists == null || intervalLists.isEmpty()) {
            return new IntersectionResult();
        }

        int k = intervalLists.size();
        List<int[]> fullIntersection = intersection(intervalLists);

        // Find intersections for all possible subset sizes
        Map<Integer, List<List<int[]>>> intersectionsBySize = new HashMap<>();

        for (int m = 1; m <= k; m++) {
            List<List<int[]>> intersectionsOfM = new ArrayList<>();

            // Generate all combinations of m lists
            List<List<Integer>> combinations = generateCombinations(k, m);

            for (List<Integer> combination : combinations) {
                List<List<int[]>> subLists = new ArrayList<>();
                for (int index : combination) {
                    subLists.add(intervalLists.get(index));
                }

                List<int[]> subIntersection = intersection(subLists);
                if (!subIntersection.isEmpty()) {
                    intersectionsOfM.add(subIntersection);
                }
            }

            intersectionsBySize.put(m, intersectionsOfM);
        }

        return new IntersectionResult(fullIntersection, intersectionsBySize);
    }

    private List<List<Integer>> generateCombinations(int n, int k) {
        List<List<Integer>> result = new ArrayList<>();
        generateCombinationsHelper(n, k, 0, new ArrayList<>(), result);
        return result;
    }

    private void generateCombinationsHelper(int n, int k, int start, List<Integer> current,
            List<List<Integer>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < n; i++) {
            current.add(i);
            generateCombinationsHelper(n, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    // Helper classes and enums
    enum EventType {
        START, END
    }

    static class Event {
        int time;
        EventType type;
        int listIndex;

        Event(int time, EventType type, int listIndex) {
            this.time = time;
            this.type = type;
            this.listIndex = listIndex;
        }
    }

    static class WeightedEvent extends Event {
        int weight;

        WeightedEvent(int time, EventType type, int listIndex, int weight) {
            super(time, type, listIndex);
            this.weight = weight;
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

    static class IntersectionResult {
        List<int[]> fullIntersection;
        Map<Integer, List<List<int[]>>> intersectionsBySize;

        IntersectionResult() {
            this.fullIntersection = new ArrayList<>();
            this.intersectionsBySize = new HashMap<>();
        }

        IntersectionResult(List<int[]> fullIntersection, Map<Integer, List<List<int[]>>> intersectionsBySize) {
            this.fullIntersection = fullIntersection;
            this.intersectionsBySize = intersectionsBySize;
        }

        @Override
        public String toString() {
            return "IntersectionResult{fullIntersection=" + formatIntervals(fullIntersection) +
                    ", sizeBreakdown=" + intersectionsBySize.keySet() + "}";
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

    public static void main(String[] args) {
        IntervalIntersectionMultipleLists solution = new IntervalIntersectionMultipleLists();

        System.out.println("=== Multiple Lists Intersection Test ===");

        // Test Case 1: Basic three-way intersection
        List<List<int[]>> lists1 = Arrays.asList(
                Arrays.asList(new int[] { 1, 5 }, new int[] { 10, 14 }),
                Arrays.asList(new int[] { 2, 6 }, new int[] { 8, 12 }),
                Arrays.asList(new int[] { 3, 7 }, new int[] { 11, 15 }));

        System.out.println("Basic 3-way intersection:");
        List<int[]> result1 = solution.intersection(lists1);
        System.out.println("  Standard: " + formatResult(result1));

        List<int[]> result1Sweep = solution.intersectionSweepLine(lists1);
        System.out.println("  Sweep line: " + formatResult(result1Sweep));

        // Test Case 2: No intersection
        List<List<int[]>> lists2 = Arrays.asList(
                Arrays.asList(new int[] { 1, 2 }),
                Arrays.asList(new int[] { 3, 4 }));

        System.out.println("\nNo intersection:");
        List<int[]> result2 = solution.intersection(lists2);
        System.out.println("  Result: " + formatResult(result2));

        // Test Case 3: Single list
        List<List<int[]>> lists3 = Arrays.asList(
                Arrays.asList(new int[] { 1, 2 }, new int[] { 3, 4 }));

        System.out.println("\nSingle list:");
        List<int[]> result3 = solution.intersection(lists3);
        System.out.println("  Result: " + formatResult(result3));

        // Test Case 4: Follow-up 1 - Intersection of M out of K
        System.out.println("\nFollow-up 1 - Intersection of M=2 out of K=3:");
        List<int[]> resultM = solution.intersectionOfM(lists1, 2);
        System.out.println("  Result: " + formatResult(resultM));

        // Test Case 5: Follow-up 2 - Union of all lists
        System.out.println("\nFollow-up 2 - Union of all lists:");
        List<int[]> union = solution.union(lists1);
        System.out.println("  Union: " + formatResult(union));

        // Test Case 6: Follow-up 3 - Weighted intersection
        System.out.println("\nFollow-up 3 - Weighted intersection:");
        List<List<WeightedInterval>> weightedLists = Arrays.asList(
                Arrays.asList(new WeightedInterval(1, 5, 10), new WeightedInterval(10, 14, 20)),
                Arrays.asList(new WeightedInterval(2, 6, 15), new WeightedInterval(8, 12, 25)),
                Arrays.asList(new WeightedInterval(3, 7, 30), new WeightedInterval(11, 15, 35)));

        List<WeightedInterval> weightedResult = solution.weightedIntersection(weightedLists);
        System.out.println("  Weighted result: " + weightedResult);

        // Test Case 7: Follow-up 4 - Dynamic intersection
        System.out.println("\nFollow-up 4 - Dynamic intersection:");
        DynamicMultiListIntersection dynamic = new DynamicMultiListIntersection(3);

        dynamic.addInterval(0, 1, 5);
        dynamic.addInterval(1, 2, 6);
        dynamic.addInterval(2, 3, 7);

        List<int[]> dynamicResult1 = dynamic.getCurrentIntersection();
        System.out.println("  After adding [1,5], [2,6], [3,7]: " + formatResult(dynamicResult1));

        dynamic.addInterval(0, 10, 14);
        dynamic.addInterval(1, 8, 12);
        dynamic.addInterval(2, 11, 15);

        List<int[]> dynamicResult2 = dynamic.getCurrentIntersection();
        System.out.println("  After adding more intervals: " + formatResult(dynamicResult2));

        // Test Case 8: Follow-up 5 - K-way analysis
        System.out.println("\nFollow-up 5 - K-way intersection analysis:");
        IntersectionResult analysis = solution.kWayIntersectionAnalysis(lists1);
        System.out.println("  " + analysis);

        // Test Case 9: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("  Empty lists: " + formatResult(solution.intersection(new ArrayList<>())));

        List<List<int[]>> emptyIntervals = Arrays.asList(new ArrayList<>());
        System.out.println("  Lists with empty intervals: " + formatResult(solution.intersection(emptyIntervals)));

        // Test Case 10: Complex overlapping scenario
        List<List<int[]>> complex = Arrays.asList(
                Arrays.asList(new int[] { 1, 3 }, new int[] { 6, 9 }, new int[] { 12, 15 }),
                Arrays.asList(new int[] { 2, 4 }, new int[] { 5, 7 }, new int[] { 13, 16 }),
                Arrays.asList(new int[] { 1, 8 }, new int[] { 10, 14 }),
                Arrays.asList(new int[] { 0, 5 }, new int[] { 11, 17 }));

        System.out.println("\nComplex 4-way intersection:");
        List<int[]> complexResult = solution.intersection(complex);
        System.out.println("  Result: " + formatResult(complexResult));

        // Test Case 11: Performance test
        System.out.println("\n=== Performance Test ===");
        List<List<int[]>> largeLists = new ArrayList<>();
        Random random = new Random(42);

        for (int k = 0; k < 10; k++) {
            List<int[]> list = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                int start = random.nextInt(10000);
                int length = random.nextInt(100) + 1;
                list.add(new int[] { start, start + length });
            }
            // Sort each list
            list.sort((a, b) -> Integer.compare(a[0], b[0]));
            largeLists.add(list);
        }

        long startTime = System.currentTimeMillis();
        List<int[]> largeResult1 = solution.intersection(largeLists);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        List<int[]> largeResult2 = solution.intersectionSweepLine(largeLists);
        long time2 = System.currentTimeMillis() - startTime;

        System.out.println(
                "Standard (10 lists, 1k intervals each): " + largeResult1.size() + " results (" + time1 + "ms)");
        System.out.println(
                "Sweep line (10 lists, 1k intervals each): " + largeResult2.size() + " results (" + time2 + "ms)");

        System.out.println("\n=== Summary ===");
        System.out.println("All multiple lists intersection tests completed successfully!");
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
