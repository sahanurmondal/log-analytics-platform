package arrays.medium;

import java.util.*;

/**
 * LeetCode 56: Merge Intervals
 * https://leetcode.com/problems/merge-intervals/
 */
public class MergeIntervals {
    public int[][] merge(int[][] intervals) {
        if (intervals.length <= 1)
            return intervals;

        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        List<int[]> result = new ArrayList<>();
        result.add(intervals[0]);

        for (int i = 1; i < intervals.length; i++) {
            int[] current = intervals[i];
            int[] last = result.get(result.size() - 1);

            if (current[0] <= last[1]) {
                last[1] = Math.max(last[1], current[1]);
            } else {
                result.add(current);
            }
        }

        return result.toArray(new int[result.size()][]);
    }

    // Alternative solution - In-place merge (modifies input)
    public int[][] mergeInPlace(int[][] intervals) {
        if (intervals.length <= 1)
            return intervals;

        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);

        int writeIndex = 0;

        for (int i = 1; i < intervals.length; i++) {
            if (intervals[writeIndex][1] >= intervals[i][0]) {
                // Merge intervals
                intervals[writeIndex][1] = Math.max(intervals[writeIndex][1], intervals[i][1]);
            } else {
                // Move to next position
                writeIndex++;
                intervals[writeIndex] = intervals[i];
            }
        }

        return Arrays.copyOf(intervals, writeIndex + 1);
    }

    // Follow-up solution - Without sorting (using TreeMap)
    public int[][] mergeWithoutSort(int[][] intervals) {
        TreeMap<Integer, Integer> map = new TreeMap<>();

        // Build the map
        for (int[] interval : intervals) {
            map.put(interval[0], Math.max(map.getOrDefault(interval[0], 0), interval[1]));
        }

        List<int[]> result = new ArrayList<>();

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            int start = entry.getKey();
            int end = entry.getValue();

            if (!result.isEmpty() && result.get(result.size() - 1)[1] >= start) {
                result.get(result.size() - 1)[1] = Math.max(result.get(result.size() - 1)[1], end);
            } else {
                result.add(new int[] { start, end });
            }
        }

        return result.toArray(new int[result.size()][]);
    }

    public static void main(String[] args) {
        MergeIntervals solution = new MergeIntervals();

        // Test Case 1: Normal case
        int[][] intervals1 = { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 15, 18 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals1))); // Expected: [[1,6],[8,10],[15,18]]

        // Test Case 2: Edge case - overlapping all
        int[][] intervals2 = { { 1, 4 }, { 4, 5 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals2))); // Expected: [[1,5]]

        // Test Case 3: Corner case - single interval
        int[][] intervals3 = { { 1, 4 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals3))); // Expected: [[1,4]]

        // Test Case 4: No overlapping
        int[][] intervals4 = { { 1, 2 }, { 3, 4 }, { 5, 6 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals4))); // Expected: [[1,2],[3,4],[5,6]]

        // Test Case 5: All merge into one
        int[][] intervals5 = { { 1, 3 }, { 2, 6 }, { 8, 10 }, { 9, 12 }, { 10, 15 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals5))); // Expected: [[1,6],[8,15]]

        // Test Case 6: Special case - same start
        int[][] intervals6 = { { 1, 4 }, { 1, 5 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals6))); // Expected: [[1,5]]

        // Test Case 7: Nested intervals
        int[][] intervals7 = { { 1, 10 }, { 2, 3 }, { 4, 5 }, { 6, 7 }, { 8, 9 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals7))); // Expected: [[1,10]]

        // Test Case 8: Adjacent intervals
        int[][] intervals8 = { { 1, 4 }, { 4, 7 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals8))); // Expected: [[1,7]]

        // Test Case 9: Reverse order input
        int[][] intervals9 = { { 15, 18 }, { 8, 10 }, { 2, 6 }, { 1, 3 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals9))); // Expected: [[1,6],[8,10],[15,18]]

        // Test Case 10: Point intervals
        int[][] intervals10 = { { 1, 1 }, { 2, 2 }, { 3, 3 } };
        System.out.println(Arrays.deepToString(solution.merge(intervals10))); // Expected: [[1,1],[2,2],[3,3]]
    }
}
