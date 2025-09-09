package miscellaneous.amazon;

import java.util.*;

/**
 * LeetCode 253: Meeting Rooms II
 * https://leetcode.com/problems/meeting-rooms-ii/
 *
 * Description:
 * Given an array of meeting time intervals where intervals[i] = [starti, endi],
 * return the minimum number of conference rooms required.
 * 
 * Company: Amazon
 * Difficulty: Medium
 * Asked: Very frequently in 2023-2024
 * 
 * Constraints:
 * - 1 <= intervals.length <= 10^4
 * - 0 <= starti < endi <= 10^6
 */
public class MeetingRoomsII {

    public int minMeetingRooms(int[][] intervals) {
        if (intervals.length == 0)
            return 0;

        PriorityQueue<Integer> heap = new PriorityQueue<>();
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        heap.offer(intervals[0][1]);

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] >= heap.peek()) {
                heap.poll();
            }
            heap.offer(intervals[i][1]);
        }

        return heap.size();
    }

    public static void main(String[] args) {
        MeetingRoomsII solution = new MeetingRoomsII();

        int[][] intervals1 = { { 0, 30 }, { 5, 10 }, { 15, 20 } };
        System.out.println(solution.minMeetingRooms(intervals1)); // 2

        int[][] intervals2 = { { 7, 10 }, { 2, 4 } };
        System.out.println(solution.minMeetingRooms(intervals2)); // 1
    }
}
