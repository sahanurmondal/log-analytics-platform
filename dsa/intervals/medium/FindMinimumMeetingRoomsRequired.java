package intervals.medium;

import java.util.*;

/**
 * LeetCode 253: Meeting Rooms II
 * https://leetcode.com/problems/meeting-rooms-ii/
 * 
 * Companies: Google, Facebook, Amazon, Microsoft, Apple
 * Frequency: High (Asked in 250+ interviews)
 *
 * Description: Given an array of meeting time intervals intervals where
 * intervals[i] = [starti, endi],
 * return the minimum number of conference rooms required.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^4
 * - 0 <= starti < endi <= 10^6
 * - intervals[i].length == 2
 * 
 * Follow-up Questions:
 * 1. Can you assign room numbers to each meeting?
 * 2. What if we need to find the busiest time period?
 * 3. Can you handle real-time meeting scheduling?
 */
public class FindMinimumMeetingRoomsRequired {

    // Approach 1: Min-heap for end times - O(n log n) time, O(n) space
    public int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return 0;

        // Sort meetings by start time
        Arrays.sort(intervals, Comparator.comparingInt(a -> a[0]));

        // Min-heap to track end times of ongoing meetings
        PriorityQueue<Integer> heap = new PriorityQueue<>();

        for (int[] interval : intervals) {
            // If a meeting has ended before current starts, free up the room
            if (!heap.isEmpty() && heap.peek() <= interval[0]) {
                heap.poll();
            }
            // Assign current meeting to a room
            heap.offer(interval[1]);
        }

        return heap.size();
    }

    // Approach 2: Sweep line algorithm - O(n log n) time, O(n) space
    public int minMeetingRoomsSweep(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return 0;

        int n = intervals.length;
        int[] starts = new int[n], ends = new int[n];

        for (int i = 0; i < n; i++) {
            starts[i] = intervals[i][0];
            ends[i] = intervals[i][1];
        }

        Arrays.sort(starts);
        Arrays.sort(ends);

        int rooms = 0, endPtr = 0;
        for (int start : starts) {
            if (start < ends[endPtr]) {
                rooms++;
            } else {
                endPtr++;
            }
        }
        return rooms;
    }

    // Approach 3: Event-based sweep line - O(n log n) time, O(n) space
    public int minMeetingRoomsEvents(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return 0;

        List<int[]> events = new ArrayList<>();

        for (int[] meeting : intervals) {
            events.add(new int[] { meeting[0], 1 }); // start event
            events.add(new int[] { meeting[1], -1 }); // end event
        }

        // Sort events: first by time, then end events before start events
        events.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]); // -1 comes before 1
        });

        int maxRooms = 0, currentRooms = 0;
        for (int[] event : events) {
            currentRooms += event[1];
            maxRooms = Math.max(maxRooms, currentRooms);
        }

        return maxRooms;
    }

    // Follow-up 1: Assign room numbers to meetings
    public int[] assignRoomNumbers(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return new int[0];

        int n = intervals.length;
        int[][] indexedMeetings = new int[n][3]; // [start, end, originalIndex]

        for (int i = 0; i < n; i++) {
            indexedMeetings[i] = new int[] { intervals[i][0], intervals[i][1], i };
        }

        Arrays.sort(indexedMeetings, (a, b) -> Integer.compare(a[0], b[0]));

        int[] roomAssignments = new int[n];
        // Min-heap storing [endTime, roomNumber]
        PriorityQueue<int[]> availableRooms = new PriorityQueue<>((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });

        int nextRoomNumber = 0;

        for (int[] meeting : indexedMeetings) {
            int start = meeting[0], end = meeting[1], originalIndex = meeting[2];

            if (!availableRooms.isEmpty() && availableRooms.peek()[0] <= start) {
                int[] availableRoom = availableRooms.poll();
                int roomNumber = availableRoom[1];
                roomAssignments[originalIndex] = roomNumber;
                availableRooms.offer(new int[] { end, roomNumber });
            } else {
                roomAssignments[originalIndex] = nextRoomNumber;
                availableRooms.offer(new int[] { end, nextRoomNumber });
                nextRoomNumber++;
            }
        }

        return roomAssignments;
    }

    // Follow-up 2: Find busiest time period
    public int[] findBusiestPeriod(int[][] intervals) {
        if (intervals == null || intervals.length == 0)
            return new int[0];

        List<int[]> events = new ArrayList<>();
        for (int[] meeting : intervals) {
            events.add(new int[] { meeting[0], 1 });
            events.add(new int[] { meeting[1], -1 });
        }

        events.sort((a, b) -> {
            if (a[0] != b[0])
                return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });

        int maxRooms = 0, currentRooms = 0;
        int busiestStart = 0, busiestEnd = 0;

        for (int i = 0; i < events.size(); i++) {
            currentRooms += events.get(i)[1];
            if (currentRooms > maxRooms) {
                maxRooms = currentRooms;
                busiestStart = events.get(i)[0];
                // Find end of this busy period
                int j = i + 1;
                while (j < events.size() && currentRooms == maxRooms) {
                    j++;
                    if (j < events.size())
                        currentRooms += events.get(j)[1];
                }
                busiestEnd = j < events.size() ? events.get(j - 1)[0] : events.get(events.size() - 1)[0];
            }
        }

        return new int[] { busiestStart, busiestEnd, maxRooms };
    }

    // Helper method: Validate room assignments
    private boolean isValidRoomAssignment(int[][] intervals, int[] rooms) {
        Map<Integer, List<int[]>> roomSchedule = new HashMap<>();

        for (int i = 0; i < intervals.length; i++) {
            roomSchedule.computeIfAbsent(rooms[i], k -> new ArrayList<>()).add(intervals[i]);
        }

        for (List<int[]> schedule : roomSchedule.values()) {
            schedule.sort((a, b) -> Integer.compare(a[0], b[0]));
            for (int i = 0; i < schedule.size() - 1; i++) {
                if (schedule.get(i)[1] > schedule.get(i + 1)[0]) {
                    return false;
                }
            }
        }
        return true;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        FindMinimumMeetingRoomsRequired solution = new FindMinimumMeetingRoomsRequired();

        // Test case 1: Basic case
        int[][] intervals1 = { { 0, 30 }, { 5, 10 }, { 15, 20 } };
        System.out.println("Test 1 - Expected: 2");
        System.out.println("Approach 1: " + solution.minMeetingRooms(intervals1));
        System.out.println("Approach 2: " + solution.minMeetingRoomsSweep(intervals1));
        System.out.println("Approach 3: " + solution.minMeetingRoomsEvents(intervals1));

        // Test case 2: No overlap
        int[][] intervals2 = { { 7, 10 }, { 2, 4 } };
        System.out.println("\nTest 2 - Expected: 1");
        System.out.println("Approach 1: " + solution.minMeetingRooms(intervals2));

        // Test case 3: All overlap
        int[][] intervals3 = { { 1, 10 }, { 2, 9 }, { 3, 8 } };
        System.out.println("\nTest 3 - Expected: 3");
        System.out.println("Approach 1: " + solution.minMeetingRooms(intervals3));

        // Test case 4: Adjacent meetings
        int[][] intervals4 = { { 1, 2 }, { 2, 3 }, { 3, 4 } };
        System.out.println("\nTest 4 - Expected: 1");
        System.out.println("Approach 1: " + solution.minMeetingRooms(intervals4));

        // Test follow-ups
        System.out.println("\nFollow-up tests:");

        // Room assignments
        int[] roomAssignments = solution.assignRoomNumbers(intervals1);
        System.out.println("Room assignments: " + Arrays.toString(roomAssignments));
        System.out.println("Valid assignment: " + solution.isValidRoomAssignment(intervals1, roomAssignments));

        // Busiest period
        int[] busiestPeriod = solution.findBusiestPeriod(intervals1);
        System.out.println("Busiest period: [" + busiestPeriod[0] + ", " + busiestPeriod[1] + "] with "
                + busiestPeriod[2] + " rooms");

        // Large input test
        int[][] large = new int[10000][2];
        for (int i = 0; i < 10000; i++) {
            large[i][0] = i;
            large[i][1] = i + 10;
        }
        System.out.println("\nLarge input (10000 meetings): " + solution.minMeetingRooms(large));
    }
}
