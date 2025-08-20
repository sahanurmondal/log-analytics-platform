package greedy.medium;

import java.util.*;

/**
 * LeetCode 452: Minimum Number of Arrows to Burst Balloons
 * URL:
 * https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 * Difficulty: Medium
 * Companies: Microsoft, Amazon, Google
 * Frequency: High
 */
public class MinimumNumberOfArrowsToBurstBalloons {
    // Greedy approach - sort by end points
    public int findMinArrowShots(int[][] points) {
        if (points.length == 0)
            return 0;

        // Sort by end points
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));

        int arrows = 1;
        int arrowPosition = points[0][1];

        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > arrowPosition) {
                arrows++;
                arrowPosition = points[i][1];
            }
        }

        return arrows;
    }

    // Alternative approach - merge intervals
    public int findMinArrowShotsAlt(int[][] points) {
        if (points.length == 0)
            return 0;

        Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        for (int[] point : points) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < point[0]) {
                merged.add(point);
            } else {
                int[] last = merged.get(merged.size() - 1);
                last[1] = Math.min(last[1], point[1]);
            }
        }

        return merged.size();
    }

    public static void main(String[] args) {
        MinimumNumberOfArrowsToBurstBalloons solution = new MinimumNumberOfArrowsToBurstBalloons();

        System.out.println(solution.findMinArrowShots(new int[][] { { 10, 16 }, { 2, 8 }, { 1, 6 }, { 7, 12 } })); // 2
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 } })); // 4
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 4, 5 } })); // 2
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 2 } })); // 1
        System.out.println(solution.findMinArrowShots(new int[][] { { 2, 3 }, { 2, 3 } })); // 1
    }
}
