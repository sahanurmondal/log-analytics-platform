package greedy.medium;

/**
 * Variation: Minimum Platforms
 *
 * Description:
 * Given arrival and departure times of trains, find the minimum number of
 * platforms required at the railway station.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= arrival[i], departure[i] <= 10^5
 */
import java.util.Arrays;

/**
 * LeetCode 253: Meeting Rooms II (similar problem)
 * https://leetcode.com/problems/meeting-rooms-ii/
 *
 * Description:
 * Given arrival and departure times of trains, find the minimum number of
 * platforms required at the railway station.
 *
 * Constraints:
 * - 1 <= n <= 10^5
 * - 0 <= arrival[i], departure[i] <= 10^5
 *
 * Follow-up:
 * - Can you solve it using sorting and two pointers?
 * - Can you optimize for real-time train scheduling?
 */
public class MinimumPlatforms {
    public int findPlatform(int[] arrival, int[] departure) {
        Arrays.sort(arrival);
        Arrays.sort(departure);

        int platforms = 0, maxPlatforms = 0;
        int i = 0, j = 0;

        while (i < arrival.length && j < departure.length) {
            if (arrival[i] <= departure[j]) {
                platforms++;
                i++;
            } else {
                platforms--;
                j++;
            }
            maxPlatforms = Math.max(maxPlatforms, platforms);
        }

        return maxPlatforms;
    }

    public static void main(String[] args) {
        MinimumPlatforms solution = new MinimumPlatforms();
        System.out.println(solution.findPlatform(new int[] { 900, 940, 950, 1100, 1500, 1800 },
                new int[] { 910, 1200, 1120, 1130, 1900, 2000 })); // 3
        // Edge Case: All trains at same time
        System.out.println(solution.findPlatform(new int[] { 1000, 1000, 1000 }, new int[] { 1010, 1010, 1010 })); // 3
        // Edge Case: Single train
        System.out.println(solution.findPlatform(new int[] { 900 }, new int[] { 910 })); // 1
        // Edge Case: Large input
        int[] arr = new int[100000], dep = new int[100000];
        for (int i = 0; i < 100000; i++) {
            arr[i] = i;
            dep[i] = i + 10;
        }
        System.out.println(solution.findPlatform(arr, dep)); // 10
    }
}
