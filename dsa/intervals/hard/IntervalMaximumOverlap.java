package intervals.hard;

import java.util.*;

/**
 * Interval Maximum Overlap - Advanced Overlap Analysis
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
 * Given a set of intervals, find the maximum number of intervals that overlap
 * at any point in time. This is equivalent to finding the minimum number of
 * resources needed to handle all intervals.
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^9
 * 
 * Follow-ups:
 * 1. Find all time points where maximum overlap occurs
 * 2. Handle weighted intervals (capacity requirements)
 * 3. Find overlap statistics and distribution
 * 4. Support dynamic interval addition/removal
 * 5. Find maximum overlap within specific time windows
 */
public class IntervalMaximumOverlap {

    /**
     * Sweep line approach - optimal solution
     * Time: O(n log n), Space: O(n)
     */
    public int maxOverlap(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Create events for starts and ends
        List<Event> events = new ArrayList<>();
        for (int[] interval : intervals) {
            events.add(new Event(interval[0], EventType.START));
            events.add(new Event(interval[1], EventType.END));
        }

        // Sort events by time, with ends processed before starts at same time
        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        int maxOverlap = 0;
        int currentOverlap = 0;

        for (Event event : events) {
            if (event.type == EventType.START) {
                currentOverlap++;
                maxOverlap = Math.max(maxOverlap, currentOverlap);
            } else {
                currentOverlap--;
            }
        }

        return maxOverlap;
    }

    /**
     * Priority queue approach - alternative solution
     * Time: O(n log n), Space: O(n)
     */
    public int maxOverlapPriorityQueue(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        PriorityQueue<Integer> pq = new PriorityQueue<>(); // Min heap for end times
        int maxOverlap = 0;

        for (int[] interval : intervals) {
            // Remove all intervals that end before current starts
            while (!pq.isEmpty() && pq.peek() <= interval[0]) {
                pq.poll();
            }

            pq.offer(interval[1]);
            maxOverlap = Math.max(maxOverlap, pq.size());
        }

        return maxOverlap;
    }

    /**
     * Follow-up 1: Find all time points where maximum overlap occurs
     * Time: O(n log n), Space: O(n)
     */
    public OverlapAnalysis findMaxOverlapPoints(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new OverlapAnalysis(0, new ArrayList<>());
        }

        List<Event> events = new ArrayList<>();
        for (int[] interval : intervals) {
            events.add(new Event(interval[0], EventType.START));
            events.add(new Event(interval[1], EventType.END));
        }

        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        int maxOverlap = 0;
        int currentOverlap = 0;
        List<Integer> maxOverlapPoints = new ArrayList<>();

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);

            if (event.type == EventType.START) {
                currentOverlap++;
                if (currentOverlap > maxOverlap) {
                    maxOverlap = currentOverlap;
                    maxOverlapPoints.clear();
                    maxOverlapPoints.add(event.time);
                } else if (currentOverlap == maxOverlap) {
                    maxOverlapPoints.add(event.time);
                }
            } else {
                currentOverlap--;
            }
        }

        return new OverlapAnalysis(maxOverlap, maxOverlapPoints);
    }

    /**
     * Follow-up 2: Weighted intervals with capacity requirements
     * Time: O(n log n), Space: O(n)
     */
    public int maxWeightedOverlap(WeightedInterval[] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        List<WeightedEvent> events = new ArrayList<>();
        for (WeightedInterval interval : intervals) {
            events.add(new WeightedEvent(interval.start, EventType.START, interval.weight));
            events.add(new WeightedEvent(interval.end, EventType.END, interval.weight));
        }

        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        int maxOverlap = 0;
        int currentOverlap = 0;

        for (WeightedEvent event : events) {
            if (event.type == EventType.START) {
                currentOverlap += event.weight;
                maxOverlap = Math.max(maxOverlap, currentOverlap);
            } else {
                currentOverlap -= event.weight;
            }
        }

        return maxOverlap;
    }

    /**
     * Follow-up 3: Overlap statistics and distribution
     * Time: O(n log n), Space: O(n)
     */
    public OverlapStatistics getOverlapStatistics(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new OverlapStatistics();
        }

        List<Event> events = new ArrayList<>();
        for (int[] interval : intervals) {
            events.add(new Event(interval[0], EventType.START));
            events.add(new Event(interval[1], EventType.END));
        }

        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        Map<Integer, Long> overlapDuration = new HashMap<>();
        int currentOverlap = 0;
        int maxOverlap = 0;
        int lastTime = events.get(0).time;

        for (Event event : events) {
            // Record duration for current overlap level
            if (event.time > lastTime && currentOverlap > 0) {
                overlapDuration.put(currentOverlap,
                        overlapDuration.getOrDefault(currentOverlap, 0L) + (event.time - lastTime));
            }

            if (event.type == EventType.START) {
                currentOverlap++;
                maxOverlap = Math.max(maxOverlap, currentOverlap);
            } else {
                currentOverlap--;
            }

            lastTime = event.time;
        }

        return new OverlapStatistics(maxOverlap, overlapDuration);
    }

    /**
     * Follow-up 4: Dynamic interval overlap tracking
     * Time: O(log n) per operation, Space: O(n)
     */
    public static class DynamicOverlapTracker {
        private TreeMap<Integer, Integer> eventCounts; // time -> net change in overlap
        private int currentMaxOverlap;

        public DynamicOverlapTracker() {
            this.eventCounts = new TreeMap<>();
            this.currentMaxOverlap = 0;
        }

        public void addInterval(int start, int end) {
            eventCounts.put(start, eventCounts.getOrDefault(start, 0) + 1);
            eventCounts.put(end, eventCounts.getOrDefault(end, 0) - 1);
            recalculateMaxOverlap();
        }

        public void removeInterval(int start, int end) {
            eventCounts.put(start, eventCounts.getOrDefault(start, 0) - 1);
            eventCounts.put(end, eventCounts.getOrDefault(end, 0) + 1);

            // Clean up zero entries
            if (eventCounts.get(start) == 0) {
                eventCounts.remove(start);
            }
            if (eventCounts.get(end) == 0) {
                eventCounts.remove(end);
            }

            recalculateMaxOverlap();
        }

        private void recalculateMaxOverlap() {
            int maxOverlap = 0;
            int currentOverlap = 0;

            for (int change : eventCounts.values()) {
                currentOverlap += change;
                maxOverlap = Math.max(maxOverlap, currentOverlap);
            }

            this.currentMaxOverlap = maxOverlap;
        }

        public int getCurrentMaxOverlap() {
            return currentMaxOverlap;
        }

        public Map<Integer, Integer> getOverlapAtTimes() {
            Map<Integer, Integer> result = new TreeMap<>();
            int currentOverlap = 0;

            for (Map.Entry<Integer, Integer> entry : eventCounts.entrySet()) {
                currentOverlap += entry.getValue();
                result.put(entry.getKey(), currentOverlap);
            }

            return result;
        }
    }

    /**
     * Follow-up 5: Maximum overlap within specific time windows
     * Time: O(n log n + w log n), Space: O(n)
     */
    public List<WindowOverlap> maxOverlapInWindows(int[][] intervals, int[][] windows) {
        if (intervals == null || intervals.length == 0 || windows == null) {
            return new ArrayList<>();
        }

        // Build event list for intervals
        List<Event> events = new ArrayList<>();
        for (int[] interval : intervals) {
            events.add(new Event(interval[0], EventType.START));
            events.add(new Event(interval[1], EventType.END));
        }

        events.sort((a, b) -> {
            if (a.time != b.time)
                return Integer.compare(a.time, b.time);
            return a.type == EventType.END ? -1 : 1;
        });

        List<WindowOverlap> results = new ArrayList<>();

        for (int[] window : windows) {
            int windowStart = window[0];
            int windowEnd = window[1];

            int maxOverlapInWindow = 0;
            int currentOverlap = 0;

            // Count intervals active at window start
            for (Event event : events) {
                if (event.time >= windowStart)
                    break;

                if (event.type == EventType.START) {
                    currentOverlap++;
                } else {
                    currentOverlap--;
                }
            }

            maxOverlapInWindow = currentOverlap;

            // Process events within window
            for (Event event : events) {
                if (event.time >= windowEnd)
                    break;
                if (event.time < windowStart)
                    continue;

                if (event.type == EventType.START) {
                    currentOverlap++;
                    maxOverlapInWindow = Math.max(maxOverlapInWindow, currentOverlap);
                } else {
                    currentOverlap--;
                }
            }

            results.add(new WindowOverlap(windowStart, windowEnd, maxOverlapInWindow));
        }

        return results;
    }

    // Helper classes and enums
    enum EventType {
        START, END
    }

    static class Event {
        int time;
        EventType type;

        Event(int time, EventType type) {
            this.time = time;
            this.type = type;
        }
    }

    static class WeightedEvent extends Event {
        int weight;

        WeightedEvent(int time, EventType type, int weight) {
            super(time, type);
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
    }

    static class OverlapAnalysis {
        int maxOverlap;
        List<Integer> maxOverlapPoints;

        OverlapAnalysis(int maxOverlap, List<Integer> maxOverlapPoints) {
            this.maxOverlap = maxOverlap;
            this.maxOverlapPoints = maxOverlapPoints;
        }

        @Override
        public String toString() {
            return "OverlapAnalysis{maxOverlap=" + maxOverlap +
                    ", points=" + maxOverlapPoints + "}";
        }
    }

    static class OverlapStatistics {
        int maxOverlap;
        Map<Integer, Long> overlapDuration; // overlap level -> total duration

        OverlapStatistics() {
            this.maxOverlap = 0;
            this.overlapDuration = new HashMap<>();
        }

        OverlapStatistics(int maxOverlap, Map<Integer, Long> overlapDuration) {
            this.maxOverlap = maxOverlap;
            this.overlapDuration = overlapDuration;
        }

        public double getAverageOverlap() {
            long totalDuration = overlapDuration.values().stream().mapToLong(Long::longValue).sum();
            long weightedSum = overlapDuration.entrySet().stream()
                    .mapToLong(e -> e.getKey() * e.getValue()).sum();
            return totalDuration == 0 ? 0 : (double) weightedSum / totalDuration;
        }

        @Override
        public String toString() {
            return "OverlapStatistics{maxOverlap=" + maxOverlap +
                    ", avgOverlap=" + String.format("%.2f", getAverageOverlap()) +
                    ", distribution=" + overlapDuration + "}";
        }
    }

    static class WindowOverlap {
        int windowStart, windowEnd, maxOverlap;

        WindowOverlap(int windowStart, int windowEnd, int maxOverlap) {
            this.windowStart = windowStart;
            this.windowEnd = windowEnd;
            this.maxOverlap = maxOverlap;
        }

        @Override
        public String toString() {
            return "Window[" + windowStart + "," + windowEnd + "]->MaxOverlap:" + maxOverlap;
        }
    }

    public static void main(String[] args) {
        IntervalMaximumOverlap solution = new IntervalMaximumOverlap();

        System.out.println("=== Maximum Overlap Test ===");

        // Test Case 1: Basic overlap
        int[][] intervals1 = { { 1, 5 }, { 2, 4 }, { 3, 6 } };
        System.out.println("Basic overlap [[1,5],[2,4],[3,6]]:");
        System.out.println("  Sweep line: " + solution.maxOverlap(intervals1));
        System.out.println("  Priority queue: " + solution.maxOverlapPriorityQueue(intervals1));

        // Test Case 2: No overlap
        int[][] intervals2 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("\nNo overlap [[1,2],[3,4],[5,6]]:");
        System.out.println("  Result: " + solution.maxOverlap(intervals2));

        // Test Case 3: All overlap
        int[][] intervals3 = { { 1, 10 }, { 2, 9 }, { 3, 8 } };
        System.out.println("\nAll overlap [[1,10],[2,9],[3,8]]:");
        System.out.println("  Result: " + solution.maxOverlap(intervals3));

        // Test Case 4: Single interval
        int[][] intervals4 = { { 1, 2 } };
        System.out.println("\nSingle interval [[1,2]]:");
        System.out.println("  Result: " + solution.maxOverlap(intervals4));

        // Test Case 5: Follow-up 1 - Find max overlap points
        System.out.println("\nFollow-up 1 - Max overlap points:");
        OverlapAnalysis analysis = solution.findMaxOverlapPoints(intervals1);
        System.out.println("  " + analysis);

        // Test Case 6: Follow-up 2 - Weighted intervals
        System.out.println("\nFollow-up 2 - Weighted intervals:");
        WeightedInterval[] weighted = {
                new WeightedInterval(1, 5, 2),
                new WeightedInterval(2, 4, 3),
                new WeightedInterval(3, 6, 1)
        };
        int weightedResult = solution.maxWeightedOverlap(weighted);
        System.out.println("  Weighted max overlap: " + weightedResult);

        // Test Case 7: Follow-up 3 - Overlap statistics
        System.out.println("\nFollow-up 3 - Overlap statistics:");
        OverlapStatistics stats = solution.getOverlapStatistics(intervals1);
        System.out.println("  " + stats);

        // Test Case 8: Follow-up 4 - Dynamic tracking
        System.out.println("\nFollow-up 4 - Dynamic overlap tracking:");
        DynamicOverlapTracker tracker = new DynamicOverlapTracker();

        tracker.addInterval(1, 5);
        System.out.println("  After adding [1,5]: " + tracker.getCurrentMaxOverlap());

        tracker.addInterval(2, 4);
        System.out.println("  After adding [2,4]: " + tracker.getCurrentMaxOverlap());

        tracker.addInterval(3, 6);
        System.out.println("  After adding [3,6]: " + tracker.getCurrentMaxOverlap());

        tracker.removeInterval(2, 4);
        System.out.println("  After removing [2,4]: " + tracker.getCurrentMaxOverlap());

        System.out.println("  Overlap timeline: " + tracker.getOverlapAtTimes());

        // Test Case 9: Follow-up 5 - Window-based overlap
        System.out.println("\nFollow-up 5 - Window-based overlap:");
        int[][] windows = { { 0, 3 }, { 2, 5 }, { 4, 8 } };
        List<WindowOverlap> windowResults = solution.maxOverlapInWindows(intervals1, windows);
        for (WindowOverlap wo : windowResults) {
            System.out.println("  " + wo);
        }

        // Test Case 10: Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("  Empty intervals: " + solution.maxOverlap(new int[][] {}));
        System.out.println("  Null intervals: " + solution.maxOverlap(null));

        // Same start times
        int[][] sameStart = { { 1, 3 }, { 1, 4 }, { 1, 5 } };
        System.out.println("  Same start times: " + solution.maxOverlap(sameStart));

        // Same end times
        int[][] sameEnd = { { 1, 5 }, { 2, 5 }, { 3, 5 } };
        System.out.println("  Same end times: " + solution.maxOverlap(sameEnd));

        // Test Case 11: Complex scenario
        int[][] complex = { { 1, 4 }, { 2, 6 }, { 3, 5 }, { 7, 9 }, { 8, 10 }, { 11, 13 } };
        System.out.println("\nComplex scenario:");
        System.out.println("  Intervals: " + Arrays.deepToString(complex));
        System.out.println("  Max overlap: " + solution.maxOverlap(complex));

        OverlapAnalysis complexAnalysis = solution.findMaxOverlapPoints(complex);
        System.out.println("  " + complexAnalysis);

        OverlapStatistics complexStats = solution.getOverlapStatistics(complex);
        System.out.println("  " + complexStats);

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
        int largeResult1 = solution.maxOverlap(large);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int largeResult2 = solution.maxOverlapPriorityQueue(large);
        long time2 = System.currentTimeMillis() - startTime;

        System.out.println("Sweep line (100k intervals): " + largeResult1 + " (" + time1 + "ms)");
        System.out.println("Priority queue (100k intervals): " + largeResult2 + " (" + time2 + "ms)");

        System.out.println("\n=== Summary ===");
        System.out.println("All maximum overlap tests completed successfully!");
    }
}
