package twopointers.medium;

/**
 * LeetCode 986: Interval List Intersections
 * https://leetcode.com/problems/interval-list-intersections/
 *
 * Companies: Google, Facebook
 * Frequency: Medium
 *
 * Description:
 * Given two lists of intervals, return their intersections.
 *
 * Constraints:
 * - 0 <= A.length, B.length <= 1000
 * - 0 <= start < end <= 10^9
 *
 * Follow-ups:
 * 1. Can you merge overlapping intervals?
 * 2. Can you handle intervals with open/closed boundaries?
 * 3. Can you optimize for large lists?
 */
public class IntervalListIntersections {
    public int[][] intervalIntersection(int[][] A, int[][] B) {
        java.util.List<int[]> res = new java.util.ArrayList<>();
        int i = 0, j = 0;
        while (i < A.length && j < B.length) {
            int lo = Math.max(A[i][0], B[j][0]);
            int hi = Math.min(A[i][1], B[j][1]);
            if (lo <= hi)
                res.add(new int[] { lo, hi });
            if (A[i][1] < B[j][1])
                i++;
            else
                j++;
        }
        return res.toArray(new int[res.size()][]);
    }

    // Follow-up 1: Merge overlapping intervals
    public int[][] mergeIntervals(int[][] intervals) {
        java.util.Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        java.util.List<int[]> res = new java.util.ArrayList<>();
        for (int[] interval : intervals) {
            if (res.isEmpty() || res.get(res.size() - 1)[1] < interval[0])
                res.add(interval);
            else
                res.get(res.size() - 1)[1] = Math.max(res.get(res.size() - 1)[1], interval[1]);
        }
        return res.toArray(new int[res.size()][]);
    }

    // Follow-up 2: Handle open/closed boundaries (not implemented)
    // Follow-up 3: Optimize for large lists (already O(n))

    public static void main(String[] args) {
        IntervalListIntersections solution = new IntervalListIntersections();
        // Basic case
        int[][] A = { { 0, 2 }, { 5, 10 }, { 13, 23 }, { 24, 25 } };
        int[][] B = { { 1, 5 }, { 8, 12 }, { 15, 24 }, { 25, 26 } };
        System.out.println("Intersections: " + java.util.Arrays.deepToString(solution.intervalIntersection(A, B)));
        // Edge: No intersection
        int[][] C = { { 1, 3 }, { 5, 9 } };
        int[][] D = { { 10, 12 } };
        System.out.println("No intersection: " + java.util.Arrays.deepToString(solution.intervalIntersection(C, D)));
        // Edge: One list empty
        System.out.println(
                "One empty: " + java.util.Arrays.deepToString(solution.intervalIntersection(new int[0][0], D)));
        // Follow-up: Merge intervals
        int[][] E = { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } };
        System.out.println("Merged: " + java.util.Arrays.deepToString(solution.mergeIntervals(E)));
    }
}
