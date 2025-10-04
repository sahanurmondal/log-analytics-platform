package sorting.hard;

/**
 * LeetCode 218: The Skyline Problem
 * https://leetcode.com/problems/the-skyline-problem/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given a list of buildings, return the skyline formed by these buildings.
 *
 * Constraints:
 * - 1 <= buildings.length <= 10^4
 * - 0 <= left < right <= 2*10^9
 * - 1 <= height <= 10^9
 *
 * Follow-ups:
 * 1. Can you return the area under the skyline?
 * 2. Can you optimize for large input?
 * 3. Can you handle overlapping buildings?
 */
public class SkylineProblem {
    public java.util.List<java.util.List<Integer>> getSkyline(int[][] buildings) {
        java.util.List<int[]> events = new java.util.ArrayList<>();
        for (int[] b : buildings) {
            events.add(new int[] { b[0], -b[2] });
            events.add(new int[] { b[1], b[2] });
        }
        events.sort((a, b) -> a[0] != b[0] ? a[0] - b[0] : a[1] - b[1]);
        java.util.List<java.util.List<Integer>> res = new java.util.ArrayList<>();
        java.util.TreeMap<Integer, Integer> heights = new java.util.TreeMap<>();
        heights.put(0, 1);
        int prev = 0;
        for (int[] e : events) {
            if (e[1] < 0)
                heights.put(-e[1], heights.getOrDefault(-e[1], 0) + 1);
            else {
                heights.put(e[1], heights.get(e[1]) - 1);
                if (heights.get(e[1]) == 0)
                    heights.remove(e[1]);
            }
            int curr = heights.lastKey();
            if (curr != prev) {
                res.add(java.util.Arrays.asList(e[0], curr));
                prev = curr;
            }
        }
        return res;
    }

    // Follow-up 1: Area under the skyline
    public int areaUnderSkyline(int[][] buildings) {
        java.util.List<java.util.List<Integer>> skyline = getSkyline(buildings);
        int area = 0;
        for (int i = 1; i < skyline.size(); i++) {
            int width = skyline.get(i).get(0) - skyline.get(i - 1).get(0);
            int height = skyline.get(i - 1).get(1);
            area += width * height;
        }
        return area;
    }

    // Follow-up 2: Optimize for large input (already handled above)
    // Follow-up 3: Handle overlapping buildings (already handled above)

    public static void main(String[] args) {
        SkylineProblem solution = new SkylineProblem();
        int[][] buildings = { { 2, 9, 10 }, { 3, 7, 15 }, { 5, 12, 12 }, { 15, 20, 10 }, { 19, 24, 8 } };
        System.out.println(solution.getSkyline(buildings)); // skyline points
        System.out.println(solution.areaUnderSkyline(buildings)); // area
    }
}
