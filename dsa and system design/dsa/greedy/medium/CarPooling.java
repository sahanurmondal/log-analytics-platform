package greedy.medium;

import java.util.*;

/**
 * LeetCode 1094: Car Pooling
 * URL: https://leetcode.com/problems/car-pooling/
 * Difficulty: Medium
 * Companies: Amazon, Google, Uber
 * Frequency: High
 * 
 * Description:
 * There is a car with capacity empty seats. The vehicle only drives east (i.e.,
 * it cannot turn around and drive west).
 * You are given the integer capacity and an array trips where trips[i] =
 * [numPassengers, from, to] indicates that
 * the ith trip has numPassengers passengers and the locations to pick them up
 * and drop them off are from and to respectively.
 * The locations are given as the number of kilometers due east from the car's
 * initial location.
 * Return true if it is possible to pick up and drop off all passengers for all
 * the given trips, or false otherwise.
 * 
 * Constraints:
 * - 1 <= trips.length <= 1000
 * - trips[i].length == 3
 * - 1 <= numPassengers <= 100
 * - 0 <= from < to <= 1000
 * - 1 <= capacity <= 10^5
 * 
 * Follow-up Questions:
 * 1. What if the locations can be very large (up to 10^9)?
 * 2. How would you optimize for real-time trip additions?
 * 3. Can you solve it with a priority queue approach?
 * 4. What if trips can overlap at the same location?
 */
public class CarPooling {
    // Difference array approach - O(n)
    public boolean carPooling(int[][] trips, int capacity) {
        int[] diff = new int[1001];

        for (int[] trip : trips) {
            int passengers = trip[0];
            int from = trip[1];
            int to = trip[2];

            diff[from] += passengers;
            diff[to] -= passengers;
        }

        int currentPassengers = 0;
        for (int i = 0; i < 1001; i++) {
            currentPassengers += diff[i];
            if (currentPassengers > capacity) {
                return false;
            }
        }

        return true;
    }

    // Sweep line with sorting - O(n log n)
    public boolean carPoolingSweepLine(int[][] trips, int capacity) {
        List<int[]> events = new ArrayList<>();

        for (int[] trip : trips) {
            events.add(new int[] { trip[1], trip[0] }); // start, +passengers
            events.add(new int[] { trip[2], -trip[0] }); // end, -passengers
        }

        events.sort((a, b) -> a[0] == b[0] ? a[1] - b[1] : a[0] - b[0]);

        int currentPassengers = 0;
        for (int[] event : events) {
            currentPassengers += event[1];
            if (currentPassengers > capacity) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args) {
        CarPooling solution = new CarPooling();
        System.out.println(solution.carPooling(new int[][] { { 2, 1, 5 }, { 3, 3, 7 } }, 4)); // false
        System.out.println(solution.carPooling(new int[][] { { 2, 1, 5 }, { 3, 3, 7 } }, 5)); // true
        System.out.println(solution.carPooling(new int[][] { { 2, 1, 5 }, { 3, 5, 7 } }, 3)); // true
        System.out.println(solution.carPooling(new int[][] { { 3, 2, 7 }, { 3, 7, 9 }, { 8, 3, 9 } }, 11)); // true
        System.out.println(solution.carPooling(new int[][] { { 9, 0, 1 }, { 3, 3, 7 } }, 4)); // false
    }
}
