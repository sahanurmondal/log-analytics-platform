package intervals.medium;

import java.util.*;

/**
 * LeetCode 759: Employee Free Time
 * https://leetcode.com/problems/employee-free-time/
 * 
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 15+ interviews)
 *
 * Description: Given a schedule of working intervals for employees, return the
 * free time
 * intervals common to all employees. Each employee has a list of
 * non-overlapping intervals,
 * and we need to find the intervals when all employees are free.
 *
 * Constraints:
 * - 1 <= schedule.length, schedule[i].length <= 50
 * - 0 <= interval.start < interval.end <= 10^8
 * - Each employee's schedule is sorted by start time
 * 
 * Follow-up Questions:
 * 1. What if we need to find time when at least k employees are free?
 * 2. How to handle overlapping intervals within an employee's schedule?
 * 3. What if we need the longest free time interval?
 * 4. How to optimize for very large number of employees?
 */
public class EmployeeFreeTime {

    static class Interval {
        int start;
        int end;

        Interval() {
            start = 0;
            end = 0;
        }

        Interval(int s, int e) {
            start = s;
            end = e;
        }

        @Override
        public String toString() {
            return "[" + start + "," + end + "]";
        }
    }

    // Approach 1: Merge All Intervals - O(n log n) time, O(n) space
    public List<Interval> employeeFreeTime(List<List<Interval>> schedule) {
        List<Interval> allIntervals = new ArrayList<>();

        // Collect all intervals from all employees
        for (List<Interval> employee : schedule) {
            allIntervals.addAll(employee);
        }

        // Sort by start time
        Collections.sort(allIntervals, (a, b) -> Integer.compare(a.start, b.start));

        // Merge overlapping intervals
        List<Interval> merged = mergeIntervals(allIntervals);

        // Find gaps between merged intervals
        List<Interval> freeTime = new ArrayList<>();
        for (int i = 0; i < merged.size() - 1; i++) {
            if (merged.get(i).end < merged.get(i + 1).start) {
                freeTime.add(new Interval(merged.get(i).end, merged.get(i + 1).start));
            }
        }

        return freeTime;
    }

    // Approach 2: Priority Queue (Heap) - O(n log k) time, O(k) space where k is
    // number of employees
    public List<Interval> employeeFreeTimePQ(List<List<Interval>> schedule) {
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(a[0], b[0]));

        // Add first interval from each employee: [start, end, employee_idx,
        // interval_idx]
        for (int i = 0; i < schedule.size(); i++) {
            if (!schedule.get(i).isEmpty()) {
                Interval interval = schedule.get(i).get(0);
                pq.offer(new int[] { interval.start, interval.end, i, 0 });
            }
        }

        List<Interval> freeTime = new ArrayList<>();
        int prevEnd = pq.peek()[0]; // Start from the earliest start time

        while (!pq.isEmpty()) {
            int[] curr = pq.poll();
            int start = curr[0], end = curr[1], empIdx = curr[2], intIdx = curr[3];

            // If there's a gap between prevEnd and current start
            if (prevEnd < start) {
                freeTime.add(new Interval(prevEnd, start));
            }

            prevEnd = Math.max(prevEnd, end);

            // Add next interval from the same employee if exists
            if (intIdx + 1 < schedule.get(empIdx).size()) {
                Interval nextInterval = schedule.get(empIdx).get(intIdx + 1);
                pq.offer(new int[] { nextInterval.start, nextInterval.end, empIdx, intIdx + 1 });
            }
        }

        return freeTime;
    }

    // Approach 3: Sweep Line Algorithm - O(n log n) time, O(n) space
    public List<Interval> employeeFreeTimeSweep(List<List<Interval>> schedule) {
        List<int[]> events = new ArrayList<>();

        // Create start and end events
        for (List<Interval> employee : schedule) {
            for (Interval interval : employee) {
                events.add(new int[] { interval.start, 1 }); // start event
                events.add(new int[] { interval.end, -1 }); // end event
            }
        }

        // Sort events by time, end events before start events for same time
        Collections.sort(events, (a, b) -> a[0] == b[0] ? Integer.compare(a[1], b[1]) : Integer.compare(a[0], b[0]));

        List<Interval> freeTime = new ArrayList<>();
        int activeEmployees = 0;
        int freeStart = -1;

        for (int[] event : events) {
            int time = event[0];
            int type = event[1];

            if (activeEmployees == 0 && type == 1) {
                // First employee starts working, end of free time
                if (freeStart != -1) {
                    freeTime.add(new Interval(freeStart, time));
                }
            }

            activeEmployees += type;

            if (activeEmployees == 0 && type == -1) {
                // Last employee stops working, start of free time
                freeStart = time;
            }
        }

        return freeTime;
    }

    // Follow-up 1: Free time when at least k employees are free
    public List<Interval> employeeFreeTimeK(List<List<Interval>> schedule, int k) {
        List<int[]> events = new ArrayList<>();

        for (List<Interval> employee : schedule) {
            for (Interval interval : employee) {
                events.add(new int[] { interval.start, 1 });
                events.add(new int[] { interval.end, -1 });
            }
        }

        Collections.sort(events, (a, b) -> a[0] == b[0] ? Integer.compare(a[1], b[1]) : Integer.compare(a[0], b[0]));

        List<Interval> freeTime = new ArrayList<>();
        int activeEmployees = 0;
        int freeStart = -1;
        int totalEmployees = schedule.size();

        for (int[] event : events) {
            int time = event[0];
            int type = event[1];

            if (totalEmployees - activeEmployees >= k && type == 1) {
                if (freeStart != -1) {
                    freeTime.add(new Interval(freeStart, time));
                }
            }

            activeEmployees += type;

            if (totalEmployees - activeEmployees >= k && type == -1) {
                freeStart = time;
            }
        }

        return freeTime;
    }

    // Follow-up 3: Find longest free time interval
    public Interval longestFreeTime(List<List<Interval>> schedule) {
        List<Interval> freeTime = employeeFreeTime(schedule);

        if (freeTime.isEmpty())
            return null;

        Interval longest = freeTime.get(0);
        for (Interval interval : freeTime) {
            if (interval.end - interval.start > longest.end - longest.start) {
                longest = interval;
            }
        }

        return longest;
    }

    private List<Interval> mergeIntervals(List<Interval> intervals) {
        if (intervals.isEmpty())
            return new ArrayList<>();

        List<Interval> merged = new ArrayList<>();
        merged.add(intervals.get(0));

        for (int i = 1; i < intervals.size(); i++) {
            Interval current = intervals.get(i);
            Interval last = merged.get(merged.size() - 1);

            if (current.start <= last.end) {
                last.end = Math.max(last.end, current.end);
            } else {
                merged.add(current);
            }
        }

        return merged;
    }

    public static void main(String[] args) {
        EmployeeFreeTime solution = new EmployeeFreeTime();

        // Test 1: Basic case with two employees
        System.out.println("Test 1: Two employees with gaps - Expected: [[3,4]]");
        List<List<Interval>> schedule1 = Arrays.asList(
                Arrays.asList(new Interval(1, 3), new Interval(6, 7)),
                Arrays.asList(new Interval(2, 4), new Interval(6, 8)));
        System.out.println(solution.employeeFreeTime(schedule1));

        // Test 2: Three employees with multiple gaps
        System.out.println("Test 2: Three employees - Expected: [[5,6], [7,9]]");
        List<List<Interval>> schedule2 = Arrays.asList(
                Arrays.asList(new Interval(1, 3), new Interval(9, 12)),
                Arrays.asList(new Interval(2, 4), new Interval(6, 7)),
                Arrays.asList(new Interval(1, 5), new Interval(7, 9)));
        System.out.println(solution.employeeFreeTime(schedule2));

        // Test 3: No free time
        System.out.println("Test 3: No free time - Expected: []");
        List<List<Interval>> schedule3 = Arrays.asList(
                Arrays.asList(new Interval(1, 5)),
                Arrays.asList(new Interval(2, 6)),
                Arrays.asList(new Interval(3, 4)));
        System.out.println(solution.employeeFreeTime(schedule3));

        // Test 4: Single employee
        System.out.println("Test 4: Single employee - Expected: [[3,4]]");
        List<List<Interval>> schedule4 = Arrays.asList(
                Arrays.asList(new Interval(1, 3), new Interval(4, 6)));
        System.out.println(solution.employeeFreeTime(schedule4));

        // Test 5: All employees free at same time
        System.out.println("Test 5: Common free time - Expected: [[4,5]]");
        List<List<Interval>> schedule5 = Arrays.asList(
                Arrays.asList(new Interval(1, 2), new Interval(5, 6)),
                Arrays.asList(new Interval(1, 3), new Interval(4, 10)),
                Arrays.asList(new Interval(2, 4), new Interval(7, 8)));
        System.out.println(solution.employeeFreeTime(schedule5));

        // Test 6: Large time ranges
        System.out.println("Test 6: Large ranges - Expected: [[50,100]]");
        List<List<Interval>> schedule6 = Arrays.asList(
                Arrays.asList(new Interval(1, 50), new Interval(100, 200)),
                Arrays.asList(new Interval(10, 30), new Interval(150, 250)));
        System.out.println(solution.employeeFreeTime(schedule6));

        // Test 7: Priority Queue approach
        System.out.println("Test 7: PQ approach - Expected: [[3,4]]");
        System.out.println(solution.employeeFreeTimePQ(schedule1));

        // Test 8: Sweep line approach
        System.out.println("Test 8: Sweep line - Expected: [[3,4]]");
        System.out.println(solution.employeeFreeTimeSweep(schedule1));

        // Test 9: Adjacent intervals
        System.out.println("Test 9: Adjacent intervals - Expected: []");
        List<List<Interval>> schedule9 = Arrays.asList(
                Arrays.asList(new Interval(1, 3), new Interval(4, 6)),
                Arrays.asList(new Interval(2, 5), new Interval(6, 9)));
        System.out.println(solution.employeeFreeTime(schedule9));

        // Test 10: Follow-up K employees free
        System.out.println("Test 10: At least 1 employee free - Expected: varies");
        System.out.println(solution.employeeFreeTimeK(schedule2, 1));

        // Test 11: Longest free time
        System.out.println("Test 11: Longest free time - Expected: [7,9]");
        Interval longest = solution.longestFreeTime(schedule2);
        System.out.println(longest != null ? longest.toString() : "null");

        // Test 12: Empty schedules
        System.out.println("Test 12: Empty schedule - Expected: []");
        List<List<Interval>> schedule12 = Arrays.asList(
                Arrays.asList());
        System.out.println(solution.employeeFreeTime(schedule12));

        // Test 13: Overlapping at boundaries
        System.out.println("Test 13: Boundary overlaps - Expected: []");
        List<List<Interval>> schedule13 = Arrays.asList(
                Arrays.asList(new Interval(1, 4)),
                Arrays.asList(new Interval(4, 7)),
                Arrays.asList(new Interval(7, 10)));
        System.out.println(solution.employeeFreeTime(schedule13));

        // Test 14: Very small intervals
        System.out.println("Test 14: Small intervals - Expected: [[2,3]]");
        List<List<Interval>> schedule14 = Arrays.asList(
                Arrays.asList(new Interval(0, 2), new Interval(3, 4)),
                Arrays.asList(new Interval(1, 2), new Interval(3, 5)));
        System.out.println(solution.employeeFreeTime(schedule14));

        // Test 15: Complex pattern
        System.out.println("Test 15: Complex overlapping - Expected: [[8,10]]");
        List<List<Interval>> schedule15 = Arrays.asList(
                Arrays.asList(new Interval(1, 3), new Interval(4, 6), new Interval(10, 12)),
                Arrays.asList(new Interval(2, 5), new Interval(7, 8), new Interval(11, 13)),
                Arrays.asList(new Interval(1, 4), new Interval(6, 7), new Interval(9, 11)));
        System.out.println(solution.employeeFreeTime(schedule15));
    }
}