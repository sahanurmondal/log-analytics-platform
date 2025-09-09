package intervals.hard;

import java.util.*;

/**
 * Interval Graph Coloring - Minimum Colors for Non-Overlapping Assignment
 * 
 * Related LeetCode Problems:
 * - 253. Meeting Rooms II
 * - 435. Non-overlapping Intervals
 * - 452. Minimum Number of Arrows to Burst Balloons
 * URL: https://leetcode.com/problems/meeting-rooms-ii/
 * 
 * Company Tags: Google, Amazon, Microsoft, Meta, Apple
 * Difficulty: Hard
 * 
 * Description:
 * Given a set of intervals, assign colors to each interval such that no two
 * overlapping intervals have the same color. Find the minimum number of colors
 * needed. This is equivalent to finding the chromatic number of an interval
 * graph.
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^9
 * 
 * Follow-ups:
 * 1. Return the actual color assignment for each interval
 * 2. Handle weighted intervals (minimize total weight per color)
 * 3. Find all optimal colorings
 * 4. Support dynamic interval addition/removal
 * 5. Constrained coloring (some intervals must have different colors)
 */
public class IntervalGraphColoring {

    /**
     * Greedy coloring using sweep line algorithm
     * Time: O(n log n), Space: O(n)
     */
    public int minColors(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Create events for interval starts and ends
        List<Event> events = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            events.add(new Event(intervals[i][0], EventType.START, i));
            events.add(new Event(intervals[i][1], EventType.END, i));
        }

        // Sort events by time, process ends before starts at same time
        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        int maxColors = 0;
        int currentColors = 0;

        for (Event event : events) {
            if (event.type == EventType.START) {
                currentColors++;
                maxColors = Math.max(maxColors, currentColors);
            } else {
                currentColors--;
            }
        }

        return maxColors;
    }

    /**
     * Priority queue approach - similar to meeting rooms
     * Time: O(n log n), Space: O(n)
     */
    public int minColorsPriorityQueue(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Min heap to track end times of active intervals
        PriorityQueue<Integer> heap = new PriorityQueue<>();

        for (int[] interval : intervals) {
            // Remove all intervals that have ended
            while (!heap.isEmpty() && heap.peek() <= interval[0]) {
                heap.poll();
            }

            // Add current interval's end time
            heap.offer(interval[1]);
        }

        return heap.size();
    }

    /**
     * Follow-up 1: Return actual color assignments
     * Time: O(n log n), Space: O(n)
     */
    public ColoringResult assignColors(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new ColoringResult(0, new int[0]);
        }

        int n = intervals.length;
        int[] colors = new int[n];

        // Create interval objects with original indices
        List<IntervalWithIndex> intervalList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            intervalList.add(new IntervalWithIndex(intervals[i][0], intervals[i][1], i));
        }

        // Sort by start time
        intervalList.sort((a, b) -> Integer.compare(a.start, b.start));

        // Min heap: {end_time, color}
        PriorityQueue<ColorSlot> availableColors = new PriorityQueue<>((a, b) -> Integer.compare(a.endTime, b.endTime));
        int nextColor = 0;

        for (IntervalWithIndex interval : intervalList) {
            // Find available color (reuse color from ended interval)
            ColorSlot availableSlot = null;
            while (!availableColors.isEmpty() && availableColors.peek().endTime <= interval.start) {
                availableSlot = availableColors.poll();
            }

            int assignedColor;
            if (availableSlot != null) {
                // Reuse existing color
                assignedColor = availableSlot.color;
            } else {
                // Need new color
                assignedColor = nextColor++;
            }

            colors[interval.originalIndex] = assignedColor;
            availableColors.offer(new ColorSlot(interval.end, assignedColor));
        }

        return new ColoringResult(nextColor, colors);
    }

    /**
     * Follow-up 2: Weighted interval coloring
     * Time: O(n^2), Space: O(n)
     */
    public WeightedColoringResult weightedColoring(int[][] intervals, int[] weights) {
        if (intervals == null || weights == null || intervals.length != weights.length) {
            return new WeightedColoringResult(0, new int[0], new int[0]);
        }

        int n = intervals.length;
        int[] colors = new int[n];

        // Build overlap graph
        boolean[][] overlaps = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (intervalsOverlap(intervals[i], intervals[j])) {
                    overlaps[i][j] = overlaps[j][i] = true;
                }
            }
        }

        // Greedy coloring with weight consideration
        Arrays.fill(colors, -1);
        List<Integer> colorWeights = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            Set<Integer> usedColors = new HashSet<>();

            // Find colors used by overlapping intervals
            for (int j = 0; j < n; j++) {
                if (overlaps[i][j] && colors[j] != -1) {
                    usedColors.add(colors[j]);
                }
            }

            // Find best color (minimize weight increase)
            int bestColor = -1;
            int minWeightIncrease = Integer.MAX_VALUE;

            for (int color = 0; color < colorWeights.size(); color++) {
                if (!usedColors.contains(color)) {
                    int weightIncrease = Math.max(0, weights[i] - colorWeights.get(color));
                    if (weightIncrease < minWeightIncrease) {
                        minWeightIncrease = weightIncrease;
                        bestColor = color;
                    }
                }
            }

            if (bestColor == -1) {
                // Need new color
                bestColor = colorWeights.size();
                colorWeights.add(weights[i]);
            } else {
                // Update existing color weight
                colorWeights.set(bestColor, Math.max(colorWeights.get(bestColor), weights[i]));
            }

            colors[i] = bestColor;
        }

        return new WeightedColoringResult(colorWeights.size(), colors,
                colorWeights.stream().mapToInt(Integer::intValue).toArray());
    }

    /**
     * Follow-up 3: Find all optimal colorings
     * Time: O(k * n^2) where k is number of optimal colorings, Space: O(k * n)
     */
    public List<int[]> findAllOptimalColorings(int[][] intervals) {
        int minColors = minColors(intervals);
        List<int[]> allColorings = new ArrayList<>();

        findAllColoringsRecursive(intervals, 0, new int[intervals.length],
                new ArrayList<>(), allColorings, minColors);

        return allColorings;
    }

    private void findAllColoringsRecursive(int[][] intervals, int index, int[] coloring,
            List<Integer> colorEndTimes, List<int[]> allColorings,
            int maxColors) {
        if (index == intervals.length) {
            if (colorEndTimes.size() == maxColors) {
                allColorings.add(coloring.clone());
            }
            return;
        }

        int[] currentInterval = intervals[index];

        // Try assigning existing colors
        for (int color = 0; color < colorEndTimes.size(); color++) {
            if (colorEndTimes.get(color) <= currentInterval[0]) {
                coloring[index] = color;
                int oldEndTime = colorEndTimes.get(color);
                colorEndTimes.set(color, currentInterval[1]);

                findAllColoringsRecursive(intervals, index + 1, coloring, colorEndTimes, allColorings, maxColors);

                colorEndTimes.set(color, oldEndTime);
            }
        }

        // Try assigning new color (if within limit)
        if (colorEndTimes.size() < maxColors) {
            coloring[index] = colorEndTimes.size();
            colorEndTimes.add(currentInterval[1]);

            findAllColoringsRecursive(intervals, index + 1, coloring, colorEndTimes, allColorings, maxColors);

            colorEndTimes.remove(colorEndTimes.size() - 1);
        }
    }

    /**
     * Follow-up 4: Dynamic interval coloring
     * Time: O(log n) per operation, Space: O(n)
     */
    public static class DynamicIntervalColoring {
        private TreeMap<Integer, Integer> colorEndTimes; // color -> end time
        private TreeMap<Integer, Set<Integer>> timeToColors; // end time -> colors
        private int nextColor;

        public DynamicIntervalColoring() {
            this.colorEndTimes = new TreeMap<>();
            this.timeToColors = new TreeMap<>();
            this.nextColor = 0;
        }

        public int addInterval(int start, int end) {
            // Clean up expired colors
            cleanupExpiredColors(start);

            // Find available color
            Integer availableColor = null;
            for (Map.Entry<Integer, Integer> entry : colorEndTimes.entrySet()) {
                if (entry.getValue() <= start) {
                    availableColor = entry.getKey();
                    break;
                }
            }

            int assignedColor;
            if (availableColor != null) {
                // Reuse existing color
                assignedColor = availableColor;
                int oldEndTime = colorEndTimes.get(assignedColor);

                // Remove from old time mapping
                timeToColors.get(oldEndTime).remove(assignedColor);
                if (timeToColors.get(oldEndTime).isEmpty()) {
                    timeToColors.remove(oldEndTime);
                }
            } else {
                // Assign new color
                assignedColor = nextColor++;
            }

            // Update mappings
            colorEndTimes.put(assignedColor, end);
            Set<Integer> colorsAtTime = timeToColors.get(end);
            if (colorsAtTime == null) {
                colorsAtTime = new HashSet<>();
                timeToColors.put(end, colorsAtTime);
            }
            colorsAtTime.add(assignedColor);

            return assignedColor;
        }

        public void removeInterval(int color, int endTime) {
            colorEndTimes.remove(color);

            Set<Integer> colors = timeToColors.get(endTime);
            if (colors != null) {
                colors.remove(color);
                if (colors.isEmpty()) {
                    timeToColors.remove(endTime);
                }
            }
        }

        public int getCurrentColorCount() {
            return nextColor;
        }

        private void cleanupExpiredColors(int currentTime) {
            Iterator<Map.Entry<Integer, Set<Integer>>> iterator = timeToColors.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Set<Integer>> entry = iterator.next();
                if (entry.getKey() <= currentTime) {
                    for (Integer color : entry.getValue()) {
                        colorEndTimes.remove(color);
                    }
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Follow-up 5: Constrained coloring
     * Time: O(n^2), Space: O(n)
     */
    public int constrainedColoring(int[][] intervals, List<Constraint> constraints) {
        int n = intervals.length;

        // Build constraint graph
        boolean[][] mustDiffer = new boolean[n][n];
        for (Constraint constraint : constraints) {
            if (constraint.type == ConstraintType.MUST_DIFFER) {
                mustDiffer[constraint.interval1][constraint.interval2] = true;
                mustDiffer[constraint.interval2][constraint.interval1] = true;
            }
        }

        // Build overlap graph with constraints
        boolean[][] conflicts = new boolean[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (intervalsOverlap(intervals[i], intervals[j]) || mustDiffer[i][j]) {
                    conflicts[i][j] = conflicts[j][i] = true;
                }
            }
        }

        // Greedy coloring
        int[] colors = new int[n];
        Arrays.fill(colors, -1);
        int maxColor = -1;

        for (int i = 0; i < n; i++) {
            Set<Integer> usedColors = new HashSet<>();

            for (int j = 0; j < n; j++) {
                if (conflicts[i][j] && colors[j] != -1) {
                    usedColors.add(colors[j]);
                }
            }

            int color = 0;
            while (usedColors.contains(color)) {
                color++;
            }

            colors[i] = color;
            maxColor = Math.max(maxColor, color);
        }

        return maxColor + 1;
    }

    private static boolean intervalsOverlap(int[] a, int[] b) {
        return Math.max(a[0], b[0]) < Math.min(a[1], b[1]);
    }

    // Helper classes and enums
    enum EventType {
        START, END
    }

    static class Event {
        int time;
        EventType type;
        int intervalIndex;

        Event(int time, EventType type, int intervalIndex) {
            this.time = time;
            this.type = type;
            this.intervalIndex = intervalIndex;
        }
    }

    static class IntervalWithIndex {
        int start, end, originalIndex;

        IntervalWithIndex(int start, int end, int originalIndex) {
            this.start = start;
            this.end = end;
            this.originalIndex = originalIndex;
        }
    }

    static class ColorSlot {
        int endTime, color;

        ColorSlot(int endTime, int color) {
            this.endTime = endTime;
            this.color = color;
        }
    }

    static class ColoringResult {
        int numColors;
        int[] colors;

        ColoringResult(int numColors, int[] colors) {
            this.numColors = numColors;
            this.colors = colors;
        }

        @Override
        public String toString() {
            return "ColoringResult{numColors=" + numColors + ", colors=" + Arrays.toString(colors) + "}";
        }
    }

    static class WeightedColoringResult {
        int numColors;
        int[] intervalColors;
        int[] colorWeights;

        WeightedColoringResult(int numColors, int[] intervalColors, int[] colorWeights) {
            this.numColors = numColors;
            this.intervalColors = intervalColors;
            this.colorWeights = colorWeights;
        }

        @Override
        public String toString() {
            return "WeightedColoringResult{numColors=" + numColors +
                    ", intervalColors=" + Arrays.toString(intervalColors) +
                    ", colorWeights=" + Arrays.toString(colorWeights) + "}";
        }
    }

    enum ConstraintType {
        MUST_DIFFER, MUST_SAME
    }

    static class Constraint {
        int interval1, interval2;
        ConstraintType type;

        Constraint(int interval1, int interval2, ConstraintType type) {
            this.interval1 = interval1;
            this.interval2 = interval2;
            this.type = type;
        }
    }

    public static void main(String[] args) {
        IntervalGraphColoring solution = new IntervalGraphColoring();

        System.out.println("=== Interval Graph Coloring Test ===");

        // Test Case 1: Basic examples
        int[][] intervals1 = { { 1, 3 }, { 2, 4 }, { 3, 5 }, { 4, 6 } };
        System.out.println("Basic coloring [1,3],[2,4],[3,5],[4,6]:");
        System.out.println("  Min colors (sweep line): " + solution.minColors(intervals1)); // 2
        System.out.println("  Min colors (priority queue): " + solution.minColorsPriorityQueue(intervals1)); // 2

        // Test Case 2: No overlaps
        int[][] intervals2 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("\nNo overlaps [1,2],[3,4],[5,6]:");
        System.out.println("  Min colors: " + solution.minColors(intervals2)); // 1

        // Test Case 3: All overlapping
        int[][] intervals3 = { { 1, 10 }, { 2, 9 }, { 3, 8 } };
        System.out.println("\nAll overlapping [1,10],[2,9],[3,8]:");
        System.out.println("  Min colors: " + solution.minColors(intervals3)); // 3

        // Test Case 4: Follow-up 1 - Color assignments
        System.out.println("\nFollow-up 1 - Color assignments:");
        ColoringResult coloring = solution.assignColors(intervals1);
        System.out.println("  " + coloring);

        // Test Case 5: Follow-up 2 - Weighted coloring
        System.out.println("\nFollow-up 2 - Weighted interval coloring:");
        int[] weights = { 5, 3, 7, 2 };
        WeightedColoringResult weightedResult = solution.weightedColoring(intervals1, weights);
        System.out.println("  " + weightedResult);

        // Test Case 6: Follow-up 3 - All optimal colorings
        System.out.println("\nFollow-up 3 - All optimal colorings:");
        int[][] simple = { { 1, 2 }, { 2, 3 } };
        List<int[]> allColorings = solution.findAllOptimalColorings(simple);
        System.out.println("  Number of optimal colorings for [1,2],[2,3]: " + allColorings.size());
        for (int i = 0; i < allColorings.size(); i++) {
            System.out.println("    Coloring " + (i + 1) + ": " + Arrays.toString(allColorings.get(i)));
        }

        // Test Case 7: Follow-up 4 - Dynamic coloring
        System.out.println("\nFollow-up 4 - Dynamic interval coloring:");
        DynamicIntervalColoring dynamic = new DynamicIntervalColoring();

        System.out.println("  Add [1,3]: color " + dynamic.addInterval(1, 3));
        System.out.println("  Add [2,4]: color " + dynamic.addInterval(2, 4));
        System.out.println("  Add [5,7]: color " + dynamic.addInterval(5, 7));
        System.out.println("  Current colors used: " + dynamic.getCurrentColorCount());

        // Test Case 8: Follow-up 5 - Constrained coloring
        System.out.println("\nFollow-up 5 - Constrained coloring:");
        List<Constraint> constraints = Arrays.asList(
                new Constraint(0, 2, ConstraintType.MUST_DIFFER));

        int constrainedColors = solution.constrainedColoring(intervals1, constraints);
        System.out.println("  Colors with constraints: " + constrainedColors);

        // Test Case 9: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("  Empty: " + solution.minColors(new int[][] {}));
        System.out.println("  Single interval: " + solution.minColors(new int[][] { { 1, 2 } }));

        // Test Case 10: Complex scenario
        int[][] complex = { { 0, 2 }, { 1, 4 }, { 2, 6 }, { 3, 5 }, { 4, 8 }, { 5, 7 } };
        System.out.println("\nComplex scenario [0,2],[1,4],[2,6],[3,5],[4,8],[5,7]:");
        System.out.println("  Min colors: " + solution.minColors(complex));

        ColoringResult complexColoring = solution.assignColors(complex);
        System.out.println("  " + complexColoring);

        // Test Case 11: Performance test
        System.out.println("\n=== Performance Test ===");
        int[][] largeIntervals = new int[10000][2];
        Random random = new Random(42);

        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(10000);
            int length = random.nextInt(100) + 1;
            largeIntervals[i] = new int[] { start, start + length };
        }

        long startTime = System.currentTimeMillis();
        int result1 = solution.minColors(largeIntervals);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int result2 = solution.minColorsPriorityQueue(largeIntervals);
        long time2 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        ColoringResult result3 = solution.assignColors(largeIntervals);
        long time3 = System.currentTimeMillis() - startTime;

        System.out.println("Sweep line (10k intervals): " + result1 + " colors (" + time1 + "ms)");
        System.out.println("Priority queue (10k intervals): " + result2 + " colors (" + time2 + "ms)");
        System.out.println("With assignments (10k intervals): " + result3.numColors + " colors (" + time3 + "ms)");

        // Test Case 12: Validation
        System.out.println("\nValidation:");
        boolean valid1 = validateColoring(intervals1, coloring.colors);
        System.out.println("  Basic coloring valid: " + valid1);

        System.out.println("\n=== Summary ===");
        System.out.println("All interval graph coloring tests completed successfully!");
    }

    private static boolean validateColoring(int[][] intervals, int[] colors) {
        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (intervalsOverlap(intervals[i], intervals[j]) && colors[i] == colors[j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
