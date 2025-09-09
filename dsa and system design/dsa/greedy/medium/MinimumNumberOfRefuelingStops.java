package greedy.medium;

/**
 * LeetCode 871: Minimum Number of Refueling Stops
 * https://leetcode.com/problems/minimum-number-of-refueling-stops/
 *
 * Description:
 * Given target, startFuel, and stations, return the minimum number of refueling
 * stops to reach the target.
 *
 * Constraints:
 * - 1 <= target, startFuel <= 10^9
 * - 1 <= stations.length <= 500
 * - stations[i].length == 2
 */
import java.util.PriorityQueue;

/**
 * LeetCode 871: Minimum Number of Refueling Stops
 * https://leetcode.com/problems/minimum-number-of-refueling-stops/
 *
 * Description:
 * Given target, startFuel, and stations, return the minimum number of refueling
 * stops to reach the target.
 *
 * Constraints:
 * - 1 <= target, startFuel <= 10^9
 * - 1 <= stations.length <= 500
 * - stations[i].length == 2
 *
 * Follow-up:
 * - Can you solve it using greedy approach with max heap?
 * - Can you optimize for dynamic programming solution?
 */
public class MinimumNumberOfRefuelingStops {
    public int minRefuelStops(int target, int startFuel, int[][] stations) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>((a, b) -> b - a);
        int fuel = startFuel;
        int stops = 0;
        int i = 0;

        while (fuel < target) {
            // Add all reachable stations to the heap
            while (i < stations.length && stations[i][0] <= fuel) {
                maxHeap.offer(stations[i][1]);
                i++;
            }

            // If no stations are reachable, impossible to reach target
            if (maxHeap.isEmpty()) {
                return -1;
            }

            // Refuel at the station with maximum fuel
            fuel += maxHeap.poll();
            stops++;
        }

        return stops;
    }

    public static void main(String[] args) {
        MinimumNumberOfRefuelingStops solution = new MinimumNumberOfRefuelingStops();
        System.out.println(
                solution.minRefuelStops(100, 10, new int[][] { { 10, 60 }, { 20, 30 }, { 30, 30 }, { 60, 40 } })); // 2
        System.out.println(solution.minRefuelStops(100, 1, new int[][] { { 10, 100 } })); // -1
        // Edge Case: No stations
        System.out.println(solution.minRefuelStops(100, 100, new int[][] {})); // 0
        // Edge Case: Large input
        int[][] stations = new int[500][2];
        for (int i = 0; i < 500; i++) {
            stations[i][0] = i * 2;
            stations[i][1] = 2;
        }
        System.out.println(solution.minRefuelStops(1000, 10, stations)); // Should be large
    }
}
