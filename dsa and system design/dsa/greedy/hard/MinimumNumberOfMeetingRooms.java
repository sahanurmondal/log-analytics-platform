package greedy.hard;

import java.util.*;

/**
 * Meeting Rooms II - Minimum Number of Meeting Rooms
 * 
 * LeetCode Problem: 253. Meeting Rooms II
 * URL: https://leetcode.com/problems/meeting-rooms-ii/
 * 
 * Company Tags: Google, Microsoft, Amazon, Meta, Apple, Bloomberg
 * Difficulty: Hard (Medium on LeetCode but complex optimizations)
 * 
 * Description:
 * Given an array of meeting time intervals consisting of start and end times
 * [[s1,e1],[s2,e2],...], find the minimum number of conference rooms required.
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i].length == 2
 * - 0 <= intervals[i][0] < intervals[i][1] <= 10^6
 * 
 * Follow-ups:
 * 1. Can you solve with different approaches?
 * 2. Can you handle room assignments?
 * 3. Can you optimize for memory usage?
 * 4. Can you handle meeting priorities?
 * 5. Can you solve for overlapping interval variations?
 */
public class MinimumNumberOfMeetingRooms {

    /**
     * Priority Queue approach - track end times of ongoing meetings
     * Time: O(n log n), Space: O(n)
     */
    public int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort meetings by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Min heap to track end times of ongoing meetings
        PriorityQueue<Integer> heap = new PriorityQueue<>();

        for (int[] interval : intervals) {
            // If the earliest ending meeting has ended, we can reuse the room
            if (!heap.isEmpty() && heap.peek() <= interval[0]) {
                heap.poll();
            }

            // Add current meeting's end time
            heap.offer(interval[1]);
        }

        return heap.size();
    }

    /**
     * Sweep Line Algorithm - count active meetings at any time
     * Time: O(n log n), Space: O(n)
     */
    public int minMeetingRoomsSweepLine(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        List<int[]> events = new ArrayList<>();

        // Create events for meeting starts and ends
        for (int[] interval : intervals) {
            events.add(new int[] { interval[0], 1 }); // Meeting starts (+1 room)
            events.add(new int[] { interval[1], -1 }); // Meeting ends (-1 room)
        }

        // Sort events by time, process ends before starts at same time
        events.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]); // -1 before 1
        });

        int maxRooms = 0;
        int currentRooms = 0;

        for (int[] event : events) {
            currentRooms += event[1];
            maxRooms = Math.max(maxRooms, currentRooms);
        }

        return maxRooms;
    }

    /**
     * Two Pointers approach - separate start and end arrays
     * Time: O(n log n), Space: O(n)
     */
    public int minMeetingRoomsTwoPointers(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int n = intervals.length;
        int[] starts = new int[n];
        int[] ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0;
        int endPointer = 0;

        for (int i = 0; i < n; i++) {
            // If meeting starts after or when another meeting ends
            if (starts[i] >= ends[endPointer]) {
                endPointer++;
            } else {
                // Need a new room
                rooms++;
            }
        }

        return rooms;
    }

    /**
     * Follow-up 1: TreeMap approach for large sparse time ranges
     * Time: O(n log k) where k is unique time points, Space: O(k)
     */
    public int minMeetingRoomsTreeMap(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        TreeMap<Integer, Integer> timeline = new TreeMap<>();

        for (int[] interval : intervals) {
            timeline.put(interval[0], timeline.getOrDefault(interval[0], 0) + 1);
            timeline.put(interval[1], timeline.getOrDefault(interval[1], 0) - 1);
        }

        int maxRooms = 0;
        int currentRooms = 0;

        for (int change : timeline.values()) {
            currentRooms += change;
            maxRooms = Math.max(maxRooms, currentRooms);
        }

        return maxRooms;
    }

    /**
     * Follow-up 2: Room assignment - return which room each meeting uses
     * Time: O(n log n), Space: O(n)
     */
    public List<Integer> assignMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new ArrayList<>();
        }

        int n = intervals.length;
        List<Integer> roomAssignments = new ArrayList<>(Collections.nCopies(n, -1));

        // Create list of meetings with original indices
        List<int[]> meetings = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            meetings.add(new int[] { intervals[i][0], intervals[i][1], i });
        }

        // Sort by start time
        meetings.sort((a, b) -> Integer.compare(a[0], b[0]));

        // Min heap: {end_time, room_number}
        PriorityQueue<int[]> rooms = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));
        int nextRoomNumber = 0;

        for (int[] meeting : meetings) {
            int start = meeting[0];
            int end = meeting[1];
            int originalIndex = meeting[2];

            // Check if we can reuse a room
            if (!rooms.isEmpty() && rooms.peek()[0] <= start) {
                int[] availableRoom = rooms.poll();
                roomAssignments.set(originalIndex, availableRoom[1]);
                rooms.offer(new int[] { end, availableRoom[1] });
            } else {
                // Need a new room
                roomAssignments.set(originalIndex, nextRoomNumber);
                rooms.offer(new int[] { end, nextRoomNumber });
                nextRoomNumber++;
            }
        }

        return roomAssignments;
    }

    /**
     * Follow-up 3: Memory optimized for streaming data
     * Time: O(n log n), Space: O(k) where k is max concurrent meetings
     */
    public int minMeetingRoomsStreaming(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));

        // Only keep track of the minimum end time
        PriorityQueue<Integer> endTimes = new PriorityQueue<>();
        int maxRooms = 0;

        for (int[] interval : intervals) {
            // Remove all meetings that have ended
            while (!endTimes.isEmpty() && endTimes.peek() <= interval[0]) {
                endTimes.poll();
            }

            endTimes.offer(interval[1]);
            maxRooms = Math.max(maxRooms, endTimes.size());
        }

        return maxRooms;
    }

    /**
     * Follow-up 4: Weighted meetings with priorities
     * Time: O(n log n), Space: O(n)
     */
    public int minMeetingRoomsWithPriority(int[][] intervals, int[] priorities) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        int n = intervals.length;
        List<int[]> meetings = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            meetings.add(new int[] { intervals[i][0], intervals[i][1], priorities[i], i });
        }

        // Sort by start time, then by priority (higher first)
        meetings.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(b[2], a[2]); // Higher priority first
        });

        // Priority queue: {end_time, room_priority}
        PriorityQueue<int[]> rooms = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]); // End time first
            return Integer.compare(b[1], a[1]); // Higher room priority
        });

        for (int[] meeting : meetings) {
            int start = meeting[0];
            int end = meeting[1];
            int priority = meeting[2];

            // Try to reuse room with compatible priority
            if (!rooms.isEmpty() && rooms.peek()[0] <= start) {
                rooms.poll();
            }

            rooms.offer(new int[] { end, priority });
        }

        return rooms.size();
    }

    /**
     * Follow-up 5: Find maximum number of overlapping meetings at any time
     * Time: O(n log n), Space: O(n)
     */
    public List<int[]> findMaxOverlapTime(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return new ArrayList<>();
        }

        List<int[]> events = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            events.add(new int[] { intervals[i][0], 1, i }); // Start event
            events.add(new int[] { intervals[i][1], -1, i }); // End event
        }

        events.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]); // Process ends before starts
        });

        int maxOverlap = 0;
        int currentOverlap = 0;
        List<Integer> maxOverlapTime = new ArrayList<>();
        Set<Integer> activeMeetings = new HashSet<>();
        List<int[]> result = new ArrayList<>();

        for (int[] event : events) {
            if (event[1] == 1) { // Start event
                activeMeetings.add(event[2]);
                currentOverlap++;
            } else { // End event
                activeMeetings.remove(event[2]);
                currentOverlap--;
            }

            if (currentOverlap > maxOverlap) {
                maxOverlap = currentOverlap;
                maxOverlapTime.clear();
                maxOverlapTime.add(event[0]);

                result.clear();
                for (int meetingId : activeMeetings) {
                    result.add(intervals[meetingId]);
                }
            } else if (currentOverlap == maxOverlap && currentOverlap > 0) {
                maxOverlapTime.add(event[0]);
            }
        }

        return result;
    }

    public static void main(String[] args) {
        MinimumNumberOfMeetingRooms solution = new MinimumNumberOfMeetingRooms();

        System.out.println("=== Minimum Number of Meeting Rooms Test ===");

        // Test Case 1: Basic examples
        int[][] intervals1 = { { 0, 30 }, { 5, 10 }, { 15, 20 } };
        System.out.println("Priority Queue [0,30],[5,10],[15,20]: " +
                solution.minMeetingRooms(intervals1)); // 2
        System.out.println("Sweep Line: " +
                solution.minMeetingRoomsSweepLine(intervals1)); // 2
        System.out.println("Two Pointers: " +
                solution.minMeetingRoomsTwoPointers(intervals1)); // 2

        int[][] intervals2 = { { 7, 10 }, { 2, 4 } };
        System.out.println("No overlap [7,10],[2,4]: " +
                solution.minMeetingRooms(intervals2)); // 1

        // Test Case 2: All overlap
        int[][] intervals3 = { { 1, 10 }, { 2, 9 }, { 3, 8 } };
        System.out.println("All overlap [1,10],[2,9],[3,8]: " +
                solution.minMeetingRooms(intervals3)); // 3

        // Test Case 3: No overlap
        int[][] intervals4 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println("Sequential [1,2],[3,4],[5,6]: " +
                solution.minMeetingRooms(intervals4)); // 1

        // Test Case 4: Room assignments
        List<Integer> assignments = solution.assignMeetingRooms(intervals1);
        System.out.println("Room assignments for [0,30],[5,10],[15,20]: " + assignments);

        // Test Case 5: TreeMap approach
        System.out.println("TreeMap approach: " +
                solution.minMeetingRoomsTreeMap(intervals1)); // 2

        // Test Case 6: Edge cases
        System.out.println("Empty input: " + solution.minMeetingRooms(new int[][] {})); // 0
        System.out.println("Single meeting: " + solution.minMeetingRooms(new int[][] { { 1, 2 } })); // 1

        // Test Case 7: Meetings with priorities
        int[] priorities = { 1, 3, 2 };
        System.out.println("With priorities [1,3,2]: " +
                solution.minMeetingRoomsWithPriority(intervals1, priorities));

        // Test Case 8: Find maximum overlap
        List<int[]> maxOverlap = solution.findMaxOverlapTime(intervals1);
        System.out.println("Maximum overlapping meetings:");
        for (int[] meeting : maxOverlap) {
            System.out.println("  [" + meeting[0] + ", " + meeting[1] + "]");
        }

        // Test Case 9: Adjacent meetings (edge case)
        int[][] adjacent = { { 1, 5 }, { 5, 10 }, { 10, 15 } };
        System.out.println("Adjacent meetings [1,5],[5,10],[10,15]: " +
                solution.minMeetingRooms(adjacent)); // 1

        // Test Case 10: Same start times
        int[][] sameStart = { { 1, 5 }, { 1, 3 }, { 1, 7 } };
        System.out.println("Same start times [1,5],[1,3],[1,7]: " +
                solution.minMeetingRooms(sameStart)); // 3

        // Performance test
        System.out.println("\n=== Performance Test ===");
        int[][] largeInput = new int[10000][2];
        Random random = new Random(42);

        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(1000);
            int duration = random.nextInt(50) + 1;
            largeInput[i] = new int[] { start, start + duration };
        }

        long startTime = System.currentTimeMillis();
        int result1 = solution.minMeetingRooms(largeInput);
        long time1 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int result2 = solution.minMeetingRoomsSweepLine(largeInput);
        long time2 = System.currentTimeMillis() - startTime;

        startTime = System.currentTimeMillis();
        int result3 = solution.minMeetingRoomsTwoPointers(largeInput);
        long time3 = System.currentTimeMillis() - startTime;

        System.out.println("Priority Queue: " + result1 + " (" + time1 + "ms)");
        System.out.println("Sweep Line: " + result2 + " (" + time2 + "ms)");
        System.out.println("Two Pointers: " + result3 + " (" + time3 + "ms)");

        // Test Case 11: Streaming approach
        startTime = System.currentTimeMillis();
        int streamingResult = solution.minMeetingRoomsStreaming(largeInput);
        long streamingTime = System.currentTimeMillis() - startTime;
        System.out.println("Streaming: " + streamingResult + " (" + streamingTime + "ms)");
    }
}
