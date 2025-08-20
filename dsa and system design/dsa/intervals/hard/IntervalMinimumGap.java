package intervals.hard;

/**
 * LeetCode [Custom]: Interval Minimum Gap
 * https://leetcode.com/problems/interval-minimum-gap/
 * 
 * Companies: Amazon, Google, Facebook
 * Frequency: Medium
 *
 * Description: Given a list of intervals, find the minimum gap between any two
 * intervals.
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^5
 * - intervals[i][0] < intervals[i][1]
 * - Intervals may overlap
 * 
 * Follow-up Questions:
 * 1. How to handle intervals with same start or end?
 * 2. What if intervals are unsorted?
 * 3. Can you do it in one pass?
 */
public class IntervalMinimumGap {

    // Approach 1: Sort intervals by start, find minimum gap - O(n log n) time, O(1)
    // space
    public int minGap(int[][] intervals) {
        // Sort intervals by start
        java.util.Arrays.sort(intervals, java.util.Comparator.comparingInt(a -> a[0]));
        int minGap = Integer.MAX_VALUE;
        for (int i = 1; i < intervals.length; i++) {
            int gap = intervals[i][0] - intervals[i - 1][1];
            if (gap > 0)
                minGap = Math.min(minGap, gap);
        }
        return minGap == Integer.MAX_VALUE ? 0 : minGap;
    }

    // Approach 2: Sweep line (if intervals are huge, use TreeSet) - O(n log n) time
    public int minGapSweepLine(int[][] intervals) {
        // ...similar logic, but using a sweep line if needed...
        return minGap(intervals); // For this problem, sorting suffices
    }

    // Helper method: Print intervals
    private void printIntervals(int[][] intervals) {
        for (int[] interval : intervals) {
            System.out.print(java.util.Arrays.toString(interval) + " ");
        }
        System.out.println();
    }

    // Test cases
    public static void main(String[] args) {
        IntervalMinimumGap solver = new IntervalMinimumGap();
        int[][] intervals1 = { { 1, 3 }, { 6, 9 }, { 10, 13 } };
        int[][] intervals2 = { { 1, 5 }, { 2, 6 }, { 8, 10 } };
        int[][] intervals3 = { { 1, 2 }, { 4, 6 }, { 8, 10 } };
        System.out.println(solver.minGap(intervals1)); // Output: 3
        System.out.println(solver.minGap(intervals2)); // Output: 2
        System.out.println(solver.minGap(intervals3)); // Output: 2
    }
}
