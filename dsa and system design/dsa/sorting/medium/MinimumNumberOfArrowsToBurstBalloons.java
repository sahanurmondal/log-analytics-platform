package sorting.medium;

/**
 * LeetCode 452: Minimum Number of Arrows to Burst Balloons
 * https://leetcode.com/problems/minimum-number-of-arrows-to-burst-balloons/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a list of balloons represented by intervals, return the minimum number
 * of arrows needed to burst all balloons.
 *
 * Constraints:
 * - 1 <= points.length <= 10^5
 * - -2^31 <= x_start < x_end <= 2^31 - 1
 *
 * Follow-ups:
 * 1. Can you return the actual arrow positions?
 * 2. Can you optimize for large input?
 * 3. Can you handle overlapping intervals?
 */
public class MinimumNumberOfArrowsToBurstBalloons {
    public int findMinArrowShots(int[][] points) {
        if (points.length == 0)
            return 0;
        java.util.Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
        int arrows = 1, end = points[0][1];
        for (int[] p : points) {
            if (p[0] > end) {
                arrows++;
                end = p[1];
            }
        }
        return arrows;
    }

    // Follow-up 1: Return actual arrow positions
    public java.util.List<Integer> arrowPositions(int[][] points) {
        java.util.List<Integer> res = new java.util.ArrayList<>();
        if (points.length == 0)
            return res;
        java.util.Arrays.sort(points, (a, b) -> Integer.compare(a[1], b[1]));
        int end = points[0][1];
        res.add(end);
        for (int[] p : points) {
            if (p[0] > end) {
                end = p[1];
                res.add(end);
            }
        }
        return res;
    }

    // Follow-up 2: Optimize for large input (already handled above)
    // Follow-up 3: Overlapping intervals (already handled above)

    public static void main(String[] args) {
        MinimumNumberOfArrowsToBurstBalloons solution = new MinimumNumberOfArrowsToBurstBalloons();

        System.out.println(solution.findMinArrowShots(new int[][] { { 10, 16 }, { 2, 8 }, { 1, 6 }, { 7, 12 } })); // 2
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 2 }, { 3, 4 }, { 5, 6 }, { 7, 8 } })); // 4
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 2 }, { 2, 3 }, { 3, 4 }, { 4, 5 } })); // 2

        // Edge Case: Single balloon
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 5 } })); // 1

        // Edge Case: All balloons overlap
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 10 }, { 2, 8 }, { 3, 6 }, { 4, 5 } })); // 1

        // Edge Case: Large coordinates
        System.out.println(solution.findMinArrowShots(new int[][] { { -2147483648, 2147483647 } })); // 1

        // Edge Case: Adjacent balloons
        System.out.println(solution.findMinArrowShots(new int[][] { { 1, 2 }, { 2, 3 } })); // 1
    }
}
