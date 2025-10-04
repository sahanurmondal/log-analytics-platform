package design.medium;

import java.util.*;

/**
 * LeetCode 1845: Seat Reservation Manager
 * https://leetcode.com/problems/seat-reservation-manager/
 *
 * Description: Design a system that manages the reservation state of n seats
 * that are numbered from 1 to n.
 * 
 * Constraints:
 * - 1 <= n <= 10^5
 * - 1 <= seatNumber <= n
 * - For each call to reserve, it is guaranteed that there will be at least one
 * unreserved seat
 * - For each call to unreserve, it is guaranteed that seatNumber will be
 * reserved
 * - At most 10^5 calls in total will be made to reserve and unreserve
 *
 * Follow-up:
 * - Can you solve it efficiently?
 * 
 * Time Complexity: O(log n) for reserve/unreserve
 * Space Complexity: O(n)
 * 
 * Company Tags: Google
 */
public class DesignSeatManager {

    private PriorityQueue<Integer> availableSeats;

    public DesignSeatManager(int n) {
        availableSeats = new PriorityQueue<>();
        for (int i = 1; i <= n; i++) {
            availableSeats.offer(i);
        }
    }

    public int reserve() {
        return availableSeats.poll();
    }

    public void unreserve(int seatNumber) {
        availableSeats.offer(seatNumber);
    }

    public static void main(String[] args) {
        DesignSeatManager seatManager = new DesignSeatManager(5);
        System.out.println(seatManager.reserve()); // Expected: 1
        System.out.println(seatManager.reserve()); // Expected: 2
        seatManager.unreserve(2);
        System.out.println(seatManager.reserve()); // Expected: 2
        System.out.println(seatManager.reserve()); // Expected: 3
        System.out.println(seatManager.reserve()); // Expected: 4
        System.out.println(seatManager.reserve()); // Expected: 5
        seatManager.unreserve(5);
    }
}
