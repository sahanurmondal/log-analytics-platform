package greedy.medium;

import java.util.*;

/**
 * LeetCode 452: Minimum Number of Arrows to Burst Balloons
 * https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 * 
 * Companies: Microsoft, Amazon, Google, Meta, Apple
 * Frequency: High (Asked in 200+ interviews)
 *
 * Description:
 * There are some spherical balloons taped onto a flat wall that represents the
 * XY-plane.
 * The balloons are represented as a 2D integer array points where points[i] =
 * [xstart, xend]
 * denotes a balloon whose horizontal diameter stretches from xstart to xend.
 * You do not know
 * the exact y-coordinates of the balloons.
 * 
 * Arrows can be shot up directly vertically (in the positive y-direction) from
 * different
 * points along the x-axis. A balloon with xstart and xend is burst by an arrow
 * shot at x
 * if xstart <= x <= xend. There is no limit to the number of arrows that can be
 * shot.
 * A shot arrow keeps traveling up infinitely, bursting any balloons in its
 * path.
 * 
 * Given the array points, return the minimum number of arrows needed to burst
 * all balloons.
 *
 * Constraints:
 * - 1 <= points.length <= 10^5
 * - points[i].length == 2
 * - -2^31 <= xstart < xend <= 2^31 - 1
 * 
 * Follow-up Questions:
 * 1. Can you return the actual arrow positions?
 * 2. What if balloons have different priorities?
 * 3. Can you solve it with 2D balloons?
 */
public class MinimumNumberOfArrows {

    // Approach 1: Greedy by End Points - O(n log n) time, O(1) space
    public int findMinArrowShots(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }

        // Sort by end points (greedy choice)
        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));

        int arrows = 1;
        int arrowPosition = points[0][1]; // Shoot at the end of first balloon

        for (int i = 1; i < points.length; i++) {
            // If current balloon starts after our arrow position
            if (points[i][0] > arrowPosition) {
                arrows++;
                arrowPosition = points[i][1]; // Shoot at end of current balloon
            }
        }

        return arrows;
    }

    // Approach 2: Interval Merging - O(n log n) time, O(n) space
    public int findMinArrowShotsIntervalMerging(int[][] points) {
        if (points == null || points.length == 0) {
            return 0;
        }

        Arrays.sort(points, (a, b) -> Integer.compare(a[0], b[0]));

        List<int[]> merged = new ArrayList<>();
        for (int[] point : points) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < point[0]) {
                merged.add(point);
            } else {
                int[] last = merged.get(merged.size() - 1);
                last[1] = Math.min(last[1], point[1]); // Keep overlap
            }
        }

        return merged.size();
    }

    // Follow-up 1: Return actual arrow positions
    public List<Integer> getArrowPositions(int[][] points) {
        List<Integer> positions = new ArrayList<>();

        if (points == null || points.length == 0) {
            return positions;
        }

        Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));

        int arrowPosition = points[0][1];
        positions.add(arrowPosition);

        for (int i = 1; i < points.length; i++) {
            if (points[i][0] > arrowPosition) {
                arrowPosition = points[i][1];
                positions.add(arrowPosition);
            }
        }

        return positions;
    }

    // Follow-up 2: Balloons with priorities (higher priority = must burst first)
    public int findMinArrowShotsWithPriority(int[][] points, int[] priorities) {
        if (points == null || points.length == 0) {
            return 0;
        }

        int n = points.length;
        Integer[] indices = new Integer[n];
        for (int i = 0; i < n; i++) {
            indices[i] = i;
        }

        // Sort by priority (descending), then by end point
        Arrays.sort(indices, (i, j) -> {
            if (priorities[i] != priorities[j]) {
                return Integer.compare(priorities[j], priorities[i]);
            }
            return Integer.compare(points[i][1], points[j][1]);
        });

        int arrows = 0;
        boolean[] burst = new boolean[n];

        for (int idx : indices) {
            if (!burst[idx]) {
                arrows++;
                int arrowPos = points[idx][1];

                // Burst all balloons that this arrow can hit
                for (int i = 0; i < n; i++) {
                    if (!burst[i] && points[i][0] <= arrowPos && arrowPos <= points[i][1]) {
                        burst[i] = true;
                    }
                }
            }
        }

        return arrows;
    }

    // Follow-up 3: 2D Balloons (circles with radius)
    public int findMinArrowShots2D(int[][] centers, int[] radii) {
        if (centers == null || centers.length == 0) {
            return 0;
        }

        int n = centers.length;
        List<int[]> intervals = new ArrayList<>();

        // Convert circles to x-intervals
        for (int i = 0; i < n; i++) {
            int left = centers[i][0] - radii[i];
            int right = centers[i][0] + radii[i];
            intervals.add(new int[] { left, right });
        }

        return findMinArrowShots(intervals.toArray(new int[0][]));
    }

    // Helper: Verify arrow positions
    public boolean verifyArrowPositions(int[][] points, List<Integer> arrows) {
        boolean[] burst = new boolean[points.length];

        for (int arrow : arrows) {
            for (int i = 0; i < points.length; i++) {
                if (points[i][0] <= arrow && arrow <= points[i][1]) {
                    burst[i] = true;
                }
            }
        }

        for (boolean isBurst : burst) {
            if (!isBurst) {
                return false;
            }
        }

        return true;
    }

    // Helper: Get overlapping balloons for given arrow position
    public List<Integer> getBalloonsBurstByArrow(int[][] points, int arrowPosition) {
        List<Integer> balloons = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            if (points[i][0] <= arrowPosition && arrowPosition <= points[i][1]) {
                balloons.add(i);
            }
        }

        return balloons;
    }

    public static void main(String[] args) {
        MinimumNumberOfArrows solution = new MinimumNumberOfArrows();

        // Test Case 1: Standard case
        int[][] points1 = { { 10, 16 }, { 2, 8 }, { 1, 6 }, { 7, 12 } };
        int result1 = solution.findMinArrowShots(points1);
        System.out.println("Test 1 - Min arrows: " + result1); // Expected: 2

        List<Integer> arrows1 = solution.getArrowPositions(points1);
        System.out.println("Arrow positions: " + arrows1);
        System.out.println("Verification: " + solution.verifyArrowPositions(points1, arrows1));

        // Test Case 2: No overlapping balloons
        int[][] points2 = { { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 } };
        int result2 = solution.findMinArrowShots(points2);
        System.out.println("Test 2 - Min arrows: " + result2); // Expected: 4

        // Test Case 3: All overlapping balloons
        int[][] points3 = { { 1, 10 }, { 2, 9 }, { 3, 8 }, { 4, 7 } };
        int result3 = solution.findMinArrowShots(points3);
        System.out.println("Test 3 - Min arrows: " + result3); // Expected: 1

        // Test Case 4: Edge case with negative coordinates
        int[][] points4 = { { -2147483646, -2147483645 }, { 2147483646, 2147483647 } };
        int result4 = solution.findMinArrowShots(points4);
        System.out.println("Test 4 - Min arrows: " + result4); // Expected: 2

        // Follow-up tests
        System.out.println("\nFollow-up 2 - With priorities:");
        int[] priorities = { 1, 3, 2, 1 }; // Higher number = higher priority
        int resultPriority = solution.findMinArrowShotsWithPriority(points1, priorities);
        System.out.println("Min arrows with priority: " + resultPriority);

        System.out.println("\nFollow-up 3 - 2D Balloons:");
        int[][] centers = { { 0, 0 }, { 3, 0 }, { 6, 0 } };
        int[] radii = { 2, 2, 2 };
        int result2D = solution.findMinArrowShots2D(centers, radii);
        System.out.println("Min arrows for 2D balloons: " + result2D);

        // Detailed analysis for test 1
        System.out.println("\nDetailed analysis for test 1:");
        for (int arrow : arrows1) {
            List<Integer> burstBalloons = solution.getBalloonsBurstByArrow(points1, arrow);
            System.out.println("Arrow at position " + arrow + " bursts balloons: " + burstBalloons);
        }
    }
}
