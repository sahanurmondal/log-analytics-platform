package intervals.easy;

import java.util.*;

/**
 * LeetCode 252: Meeting Rooms
 * https://leetcode.com/problems/meeting-rooms/
 * 
 * Companies: Amazon, Google, Meta, Microsoft, Apple, Uber
 * Frequency: High (Asked in 200+ interviews)
 * 
 * Description:
 * Given an array of meeting time intervals where intervals[i] = [starti, endi],
 * determine if a person could attend all meetings.
 *
 * Constraints:
 * - 0 <= intervals.length <= 10^4
 * - intervals[i].length == 2
 * - 0 <= starti < endi <= 10^6
 * 
 * Follow-up Questions:
 * 1. What if we want to find the minimum number of meeting rooms needed?
 * 2. Can you return which meetings conflict?
 * 3. What if meetings have priorities and we can cancel lower priority ones?
 * 4. How would you handle meetings across different time zones?
 */
public class MeetingRooms {

    // Approach 1: Sort by start time - O(n log n) time, O(1) space
    public boolean canAttendMeetings(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return true;
        }

        // Sort by start time
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        // Check for overlaps
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] < intervals[i - 1][1]) {
                return false; // Overlap found
            }
        }

        return true;
    }

    // Approach 2: Brute Force - O(n²) time, O(1) space
    public boolean canAttendMeetingsBruteForce(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return true;
        }

        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (intervalsOverlap(intervals[i], intervals[j])) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean intervalsOverlap(int[] interval1, int[] interval2) {
        return interval1[0] < interval2[1] && interval2[0] < interval1[1];
    }

    // Approach 3: Using TreeMap for range queries - O(n log n) time, O(n) space
    public boolean canAttendMeetingsTreeMap(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return true;
        }

        TreeMap<Integer, Integer> timeline = new TreeMap<>();

        for (int[] interval : intervals) {
            Integer floorKey = timeline.floorKey(interval[1] - 1);
            if (floorKey != null && timeline.get(floorKey) > interval[0]) {
                return false; // Overlap found
            }
            timeline.put(interval[0], interval[1]);
        }

        return true;
    }

    // Follow-up 1: Minimum number of meeting rooms needed (Meeting Rooms II)
    public int minMeetingRooms(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }

        // Create events: start = +1, end = -1
        List<int[]> events = new ArrayList<>();
        for (int[] interval : intervals) {
            events.add(new int[] { interval[0], 1 }); // Meeting starts
            events.add(new int[] { interval[1], -1 }); // Meeting ends
        }

        // Sort events by time, then by type (end before start for same time)
        events.sort((a, b) -> {
            if (a[0] == b[0])
                return a[1] - b[1]; // End events first
            return a[0] - b[0];
        });

        int maxRooms = 0;
        int currentRooms = 0;

        for (int[] event : events) {
            currentRooms += event[1];
            maxRooms = Math.max(maxRooms, currentRooms);
        }

        return maxRooms;
    }

    // Follow-up 2: Find conflicting meetings
    public List<List<int[]>> findConflictingMeetings(int[][] intervals) {
        List<List<int[]>> conflicts = new ArrayList<>();

        for (int i = 0; i < intervals.length; i++) {
            for (int j = i + 1; j < intervals.length; j++) {
                if (intervalsOverlap(intervals[i], intervals[j])) {
                    List<int[]> conflict = Arrays.asList(intervals[i], intervals[j]);
                    conflicts.add(conflict);
                }
            }
        }

        return conflicts;
    }

    // Follow-up 3: Meeting scheduling with priorities
    public boolean canAttendWithPriorities(int[][] intervals, int[] priorities) {
        if (intervals == null || intervals.length <= 1) {
            return true;
        }

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            indices.add(i);
        }

        // Sort by priority (higher priority first)
        indices.sort((i, j) -> priorities[j] - priorities[i]);

        List<int[]> scheduled = new ArrayList<>();

        for (int idx : indices) {
            int[] meeting = intervals[idx];
            boolean canSchedule = true;

            for (int[] scheduledMeeting : scheduled) {
                if (intervalsOverlap(meeting, scheduledMeeting)) {
                    canSchedule = false;
                    break;
                }
            }

            if (canSchedule) {
                scheduled.add(meeting);
            }
        }

        return scheduled.size() == intervals.length;
    }

    // Follow-up 4: Time zone aware meetings
    public boolean canAttendMeetingsWithTimeZones(int[][] intervals, String[] timeZones, String targetTimeZone) {
        // Convert all meetings to target timezone
        int[][] convertedIntervals = new int[intervals.length][2];

        for (int i = 0; i < intervals.length; i++) {
            int[] converted = convertTimeZone(intervals[i], timeZones[i], targetTimeZone);
            convertedIntervals[i] = converted;
        }

        return canAttendMeetings(convertedIntervals);
    }

    private int[] convertTimeZone(int[] interval, String fromTz, String toTz) {
        // Simplified timezone conversion (in real scenario, use proper timezone
        // libraries)
        Map<String, Integer> tzOffsets = Map.of(
                "UTC", 0, "EST", -5, "PST", -8, "CST", -6, "MST", -7);

        int fromOffset = tzOffsets.getOrDefault(fromTz, 0);
        int toOffset = tzOffsets.getOrDefault(toTz, 0);
        int offsetDiff = (toOffset - fromOffset) * 60; // Convert to minutes

        return new int[] { interval[0] + offsetDiff, interval[1] + offsetDiff };
    }

    // Helper: Get meeting schedule statistics
    public Map<String, Object> getMeetingStatistics(int[][] intervals) {
        Map<String, Object> stats = new HashMap<>();

        if (intervals == null || intervals.length == 0) {
            stats.put("totalMeetings", 0);
            return stats;
        }

        stats.put("totalMeetings", intervals.length);

        int totalDuration = 0;
        int maxDuration = 0;
        int minDuration = Integer.MAX_VALUE;
        int earliestStart = Integer.MAX_VALUE;
        int latestEnd = 0;

        for (int[] interval : intervals) {
            int duration = interval[1] - interval[0];
            totalDuration += duration;
            maxDuration = Math.max(maxDuration, duration);
            minDuration = Math.min(minDuration, duration);
            earliestStart = Math.min(earliestStart, interval[0]);
            latestEnd = Math.max(latestEnd, interval[1]);
        }

        stats.put("totalDuration", totalDuration);
        stats.put("averageDuration", (double) totalDuration / intervals.length);
        stats.put("maxDuration", maxDuration);
        stats.put("minDuration", minDuration);
        stats.put("earliestStart", earliestStart);
        stats.put("latestEnd", latestEnd);
        stats.put("timeSpan", latestEnd - earliestStart);
        stats.put("canAttendAll", canAttendMeetings(intervals.clone()));
        stats.put("minRoomsNeeded", minMeetingRooms(intervals.clone()));

        return stats;
    }

    // Helper: Generate optimal schedule
    public List<int[]> generateOptimalSchedule(int[][] intervals, int[] priorities) {
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < intervals.length; i++) {
            indices.add(i);
        }

        // Sort by priority, then by duration (shorter first for same priority)
        indices.sort((i, j) -> {
            if (priorities[i] != priorities[j]) {
                return priorities[j] - priorities[i]; // Higher priority first
            }
            int durI = intervals[i][1] - intervals[i][0];
            int durJ = intervals[j][1] - intervals[j][0];
            return durI - durJ; // Shorter duration first
        });

        List<int[]> schedule = new ArrayList<>();

        for (int idx : indices) {
            int[] meeting = intervals[idx];
            boolean canAdd = true;

            for (int[] scheduledMeeting : schedule) {
                if (intervalsOverlap(meeting, scheduledMeeting)) {
                    canAdd = false;
                    break;
                }
            }

            if (canAdd) {
                schedule.add(meeting);
            }
        }

        // Sort final schedule by start time
        schedule.sort((a, b) -> a[0] - b[0]);
        return schedule;
    }

    public static void main(String[] args) {
        MeetingRooms solution = new MeetingRooms();

        // Test Case 1: No conflicts
        int[][] meetings1 = { { 0, 30 }, { 5, 10 }, { 15, 20 } };
        boolean result1 = solution.canAttendMeetings(meetings1);
        System.out.println("Test 1 - Can attend all: " + result1); // Expected: false

        // Verify with brute force
        boolean bruteResult1 = solution.canAttendMeetingsBruteForce(meetings1.clone());
        System.out.println("Brute force result: " + bruteResult1);
        System.out.println("Results match: " + (result1 == bruteResult1));

        // Test Case 2: No conflicts
        int[][] meetings2 = { { 7, 10 }, { 2, 4 } };
        boolean result2 = solution.canAttendMeetings(meetings2);
        System.out.println("Test 2 - Can attend all: " + result2); // Expected: true

        // Test Case 3: Edge case - empty array
        int[][] meetings3 = {};
        boolean result3 = solution.canAttendMeetings(meetings3);
        System.out.println("Test 3 - Empty array: " + result3); // Expected: true

        // Test Case 4: Single meeting
        int[][] meetings4 = { { 1, 5 } };
        boolean result4 = solution.canAttendMeetings(meetings4);
        System.out.println("Test 4 - Single meeting: " + result4); // Expected: true

        // Test Case 5: Adjacent meetings (no overlap)
        int[][] meetings5 = { { 1, 5 }, { 5, 10 }, { 10, 15 } };
        boolean result5 = solution.canAttendMeetings(meetings5);
        System.out.println("Test 5 - Adjacent meetings: " + result5); // Expected: true

        // Follow-up 1: Minimum meeting rooms
        System.out.println("\nFollow-up 1 - Minimum meeting rooms:");
        int minRooms1 = solution.minMeetingRooms(meetings1);
        System.out.println("Min rooms for test 1: " + minRooms1); // Expected: 2

        int[][] meetingsComplex = { { 0, 10 }, { 5, 15 }, { 10, 20 }, { 15, 25 } };
        int minRoomsComplex = solution.minMeetingRooms(meetingsComplex);
        System.out.println("Min rooms for complex case: " + minRoomsComplex); // Expected: 2

        // Follow-up 2: Find conflicts
        System.out.println("\nFollow-up 2 - Conflicting meetings:");
        List<List<int[]>> conflicts = solution.findConflictingMeetings(meetings1);
        System.out.println("Number of conflicts: " + conflicts.size());
        for (int i = 0; i < conflicts.size(); i++) {
            List<int[]> conflict = conflicts.get(i);
            System.out.printf("Conflict %d: [%d,%d] overlaps with [%d,%d]\n",
                    i + 1,
                    conflict.get(0)[0], conflict.get(0)[1],
                    conflict.get(1)[0], conflict.get(1)[1]);
        }

        // Follow-up 3: Priority-based scheduling
        System.out.println("\nFollow-up 3 - Priority-based scheduling:");
        int[] priorities = { 3, 1, 2 }; // Higher number = higher priority
        boolean canAttendWithPriority = solution.canAttendWithPriorities(meetings1, priorities);
        System.out.println("Can attend with priorities: " + canAttendWithPriority);

        List<int[]> optimalSchedule = solution.generateOptimalSchedule(meetings1, priorities);
        System.out.println("Optimal schedule:");
        for (int i = 0; i < optimalSchedule.size(); i++) {
            int[] meeting = optimalSchedule.get(i);
            System.out.printf("Meeting %d: [%d,%d]\n", i + 1, meeting[0], meeting[1]);
        }

        // Follow-up 4: Time zone conversion
        System.out.println("\nFollow-up 4 - Time zone conversion:");
        int[][] tzMeetings = { { 0, 60 }, { 30, 90 } }; // In minutes
        String[] timeZones = { "EST", "PST" };
        boolean canAttendTz = solution.canAttendMeetingsWithTimeZones(tzMeetings, timeZones, "UTC");
        System.out.println("Can attend with timezone conversion: " + canAttendTz);

        // Statistics
        System.out.println("\nMeeting Statistics:");
        Map<String, Object> stats = solution.getMeetingStatistics(meetings1);
        stats.forEach((key, value) -> System.out.println(key + ": " + value));

        // Performance comparison
        System.out.println("\nPerformance comparison (large dataset):");
        int[][] largeMeetings = new int[1000][2];
        Random random = new Random(42);
        for (int i = 0; i < 1000; i++) {
            int start = random.nextInt(1000);
            int duration = random.nextInt(100) + 1;
            largeMeetings[i] = new int[] { start, start + duration };
        }

        long start = System.nanoTime();
        boolean sortResult = solution.canAttendMeetings(largeMeetings.clone());
        long sortTime = System.nanoTime() - start;

        start = System.nanoTime();
        boolean bruteResult = solution.canAttendMeetingsBruteForce(largeMeetings.clone());
        long bruteTime = System.nanoTime() - start;

        start = System.nanoTime();
        boolean treeMapResult = solution.canAttendMeetingsTreeMap(largeMeetings.clone());
        long treeMapTime = System.nanoTime() - start;

        System.out.println("Sort approach: " + sortResult + " (" + sortTime / 1_000_000.0 + " ms)");
        System.out.println("Brute force: " + bruteResult + " (" + bruteTime / 1_000_000.0 + " ms)");
        System.out.println("TreeMap approach: " + treeMapResult + " (" + treeMapTime / 1_000_000.0 + " ms)");
        System.out.println("All results consistent: " +
                (sortResult == bruteResult && bruteResult == treeMapResult));
    }
}
