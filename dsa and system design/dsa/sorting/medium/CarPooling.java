package sorting.medium;

/**
 * LeetCode 1094: Car Pooling
 * https://leetcode.com/problems/car-pooling/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a list of trips, return true if you can take all trips with the given
 * capacity.
 *
 * Constraints:
 * - 1 <= trips.length <= 1000
 * - 1 <= capacity <= 10^6
 *
 * Follow-ups:
 * 1. Can you return the minimum capacity required?
 * 2. Can you handle overlapping trips efficiently?
 * 3. Can you handle trips with variable capacity?
 */
public class CarPooling {
    public boolean carPooling(int[][] trips, int capacity) {
        int[] diff = new int[1001];
        for (int[] t : trips) {
            diff[t[1]] += t[0];
            diff[t[2]] -= t[0];
        }
        int curr = 0;
        for (int i = 0; i < diff.length; i++) {
            curr += diff[i];
            if (curr > capacity)
                return false;
        }
        return true;
    }

    // Follow-up 1: Minimum capacity required
    public int minCapacity(int[][] trips) {
        int[] diff = new int[1001];
        for (int[] t : trips) {
            diff[t[1]] += t[0];
            diff[t[2]] -= t[0];
        }
        int curr = 0, maxCap = 0;
        for (int i = 0; i < diff.length; i++) {
            curr += diff[i];
            maxCap = Math.max(maxCap, curr);
        }
        return maxCap;
    }

    // Follow-up 2: Overlapping trips (already handled above)
    // Follow-up 3: Trips with variable capacity (not implemented)

    public static void main(String[] args) {
        CarPooling solution = new CarPooling();
        int[][] trips = { { 2, 1, 5 }, { 3, 3, 7 } };
        System.out.println(solution.carPooling(trips, 4)); // false
        System.out.println(solution.minCapacity(trips)); // 5
    }
}
