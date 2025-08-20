package queues.hard;

/**
 * LeetCode 815: Bus Routes
 * https://leetcode.com/problems/bus-routes/
 *
 * Description:
 * You are given an array routes representing bus routes where routes[i] is a
 * bus route that the ith bus repeats forever.
 *
 * Constraints:
 * - 1 <= routes.length <= 500
 * - 1 <= routes[i].length <= 10^5
 * - All the values of routes[i] are unique
 * - sum(routes[i].length) <= 10^5
 * - 0 <= routes[i][j] < 10^6
 * - 0 <= source, target < 10^6
 *
 * Follow-up:
 * - Can you solve it using BFS on bus routes?
 * - Can you optimize for very large route networks?
 */
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * LeetCode 815: Bus Routes
 * https://leetcode.com/problems/bus-routes/
 *
 * Description:
 * You are given an array routes representing bus routes where routes[i] is a
 * bus route that the ith bus repeats forever.
 *
 * Constraints:
 * - 1 <= routes.length <= 500
 * - 1 <= routes[i].length <= 10^5
 * - All the values of routes[i] are unique
 * - sum(routes[i].length) <= 10^5
 * - 0 <= routes[i][j] < 10^6
 * - 0 <= source, target < 10^6
 *
 * Follow-up:
 * - Can you solve it using BFS on bus routes?
 * - Can you optimize for very large route networks?
 */
public class BusRoutes {
    public int numBusesToDestination(int[][] routes, int source, int target) {
        if (source == target)
            return 0;

        // Map each stop to routes that contain it
        Map<Integer, Set<Integer>> stopToRoutes = new HashMap<>();
        for (int i = 0; i < routes.length; i++) {
            for (int stop : routes[i]) {
                stopToRoutes.computeIfAbsent(stop, k -> new HashSet<>()).add(i);
            }
        }

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visitedRoutes = new HashSet<>();

        // Start BFS from all routes containing source
        if (stopToRoutes.containsKey(source)) {
            for (int route : stopToRoutes.get(source)) {
                queue.offer(route);
                visitedRoutes.add(route);
            }
        }

        int buses = 1;

        while (!queue.isEmpty()) {
            int size = queue.size();

            for (int i = 0; i < size; i++) {
                int currentRoute = queue.poll();

                // Check all stops in current route
                for (int stop : routes[currentRoute]) {
                    if (stop == target)
                        return buses;

                    // Add all unvisited routes that contain this stop
                    if (stopToRoutes.containsKey(stop)) {
                        for (int nextRoute : stopToRoutes.get(stop)) {
                            if (!visitedRoutes.contains(nextRoute)) {
                                visitedRoutes.add(nextRoute);
                                queue.offer(nextRoute);
                            }
                        }
                    }
                }
            }
            buses++;
        }

        return -1;
    }

    public static void main(String[] args) {
        BusRoutes solution = new BusRoutes();
        System.out.println(solution.numBusesToDestination(new int[][] { { 1, 2, 7 }, { 3, 6, 7 } }, 1, 6)); // 2
        System.out.println(solution.numBusesToDestination(
                new int[][] { { 7, 12 }, { 4, 5, 15 }, { 6 }, { 15, 19 }, { 9, 12, 13 } }, 15, 12)); // -1
        // Edge Case: Same source and target
        System.out.println(solution.numBusesToDestination(new int[][] { { 1, 2, 7 }, { 3, 6, 7 } }, 1, 1)); // 0
        // Edge Case: Direct route
        System.out.println(solution.numBusesToDestination(new int[][] { { 1, 2, 7 }, { 3, 6, 7 } }, 1, 2)); // 1
        // Edge Case: No route
        System.out.println(solution.numBusesToDestination(new int[][] { { 1, 2 }, { 3, 4 } }, 1, 4)); // -1
    }
}
