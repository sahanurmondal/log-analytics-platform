package intervals;

import java.util.*;

/**
 * LeetCode 56: Merge Intervals
 * URL: https://leetcode.com/problems/merge-intervals/
 * Difficulty: Medium
 * Companies: Amazon, Microsoft, Google, Facebook, Apple
 * Frequency: High (Asked in 200+ interviews)
 *
 * Description:
 * Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals,
 * and return an array of the non-overlapping intervals that cover all the intervals in the input.
 *
 * Example:
 * Input: intervals = [[1,3],[2,6],[8,10],[15,18]]
 * Output: [[1,6],[8,10],[15,18]]
 * Explanation: Since intervals [1,3] and [2,6] overlap, merge them into [1,6].
 *
 * Constraints:
 * - 1 <= intervals.length <= 10^4
 * - intervals[i].length == 2
 * - 0 <= starti <= endi <= 10^4
 * 
 * Follow-up Questions:
 * 1. Can you solve in-place to save space?
 * 2. What if intervals come in streaming fashion?
 * 3. How would you handle different data types for intervals?
 * 4. Can you optimize for mostly non-overlapping intervals?
 */
public class MergeIntervals {
    
    // Approach 1: Sort and Merge - O(n log n) time, O(n) space
    public int[][] merge(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        // Sort by start time
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        
        List<int[]> result = new ArrayList<>();
        int[] current = intervals[0];
        
        for (int i = 1; i < intervals.length; i++) {
            if (current[1] >= intervals[i][0]) {
                // Overlapping intervals, merge them
                current[1] = Math.max(current[1], intervals[i][1]);
            } else {
                // Non-overlapping interval, add current to result
                result.add(current);
                current = intervals[i];
            }
        }
        
        // Add the last interval
        result.add(current);
        
        return result.toArray(new int[result.size()][]);
    }
    
    // Approach 2: In-place merge - O(n log n) time, O(1) extra space
    public int[][] mergeInPlace(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        
        int writeIndex = 0;
        
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[writeIndex][1] >= intervals[i][0]) {
                // Merge overlapping intervals
                intervals[writeIndex][1] = Math.max(intervals[writeIndex][1], intervals[i][1]);
            } else {
                // Move to next position
                writeIndex++;
                intervals[writeIndex] = intervals[i];
            }
        }
        
        // Return the merged portion
        return Arrays.copyOf(intervals, writeIndex + 1);
    }
    
    // Approach 3: Using TreeMap for streaming - O(n log n) time, O(n) space
    public int[][] mergeWithTreeMap(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        TreeMap<Integer, Integer> map = new TreeMap<>();
        
        // Process each interval
        for (int[] interval : intervals) {
            int start = interval[0];
            int end = interval[1];
            
            // Find all overlapping intervals
            Integer floorKey = map.floorKey(end);
            if (floorKey != null && map.get(floorKey) >= start) {
                // There's overlap, need to merge
                start = Math.min(start, floorKey);
                end = Math.max(end, map.get(floorKey));
                
                // Remove all overlapping intervals
                Iterator<Map.Entry<Integer, Integer>> it = map.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<Integer, Integer> entry = it.next();
                    if (entry.getKey() >= start && entry.getValue() <= end) {
                        it.remove();
                    }
                }
            }
            
            map.put(start, end);
        }
        
        // Convert back to array
        int[][] result = new int[map.size()][2];
        int i = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            result[i][0] = entry.getKey();
            result[i][1] = entry.getValue();
            i++;
        }
        
        return result;
    }
    
    // Approach 4: Optimized for sparse intervals - O(n log n) time, O(n) space
    public int[][] mergeOptimized(int[][] intervals) {
        if (intervals == null || intervals.length <= 1) {
            return intervals;
        }
        
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        
        List<int[]> merged = new ArrayList<>();
        
        for (int[] interval : intervals) {
            if (merged.isEmpty() || merged.get(merged.size() - 1)[1] < interval[0]) {
                merged.add(interval);
            } else {
                merged.get(merged.size() - 1)[1] = Math.max(merged.get(merged.size() - 1)[1], interval[1]);
            }
        }
        
        return merged.toArray(new int[merged.size()][]);
    }

    public static void main(String[] args) {
        MergeIntervals solution = new MergeIntervals();
        
        // Test Case 1: Basic overlapping intervals
        System.out.println("=== Test Case 1: Basic Overlapping ===");
        int[][] intervals1 = {{1, 3}, {2, 6}, {8, 10}, {15, 18}};
        printResult(solution.merge(intervals1)); // [[1,6],[8,10],[15,18]]
        
        // Test Case 2: All overlapping
        System.out.println("\n=== Test Case 2: All Overlapping ===");
        int[][] intervals2 = {{1, 4}, {4, 5}};
        printResult(solution.merge(intervals2)); // [[1,5]]
        
        // Test Case 3: No overlapping
        System.out.println("\n=== Test Case 3: No Overlapping ===");
        int[][] intervals3 = {{1, 2}, {3, 4}, {5, 6}};
        printResult(solution.merge(intervals3)); // [[1,2],[3,4],[5,6]]
        
        // Test Case 4: Single interval
        System.out.println("\n=== Test Case 4: Single Interval ===");
        int[][] intervals4 = {{1, 4}};
        printResult(solution.merge(intervals4)); // [[1,4]]
        
        // Test Case 5: Identical intervals
        System.out.println("\n=== Test Case 5: Identical Intervals ===");
        int[][] intervals5 = {{1, 3}, {1, 3}};
        printResult(solution.merge(intervals5)); // [[1,3]]
        
        // Test Case 6: Nested intervals
        System.out.println("\n=== Test Case 6: Nested Intervals ===");
        int[][] intervals6 = {{1, 10}, {2, 3}, {4, 5}, {6, 7}, {8, 9}};
        printResult(solution.merge(intervals6)); // [[1,10]]
        
        // Test Case 7: Compare approaches
        System.out.println("\n=== Test Case 7: Approach Comparison ===");
        compareApproaches(solution, intervals1);
        
        // Test Case 8: Edge case - touching intervals
        System.out.println("\n=== Test Case 8: Touching Intervals ===");
        int[][] intervals8 = {{1, 4}, {5, 6}};
        printResult(solution.merge(intervals8)); // [[1,4],[5,6]]
        
        // Test Case 9: Performance test
        System.out.println("\n=== Test Case 9: Performance Test ===");
        performanceTest(solution);
        
        // Test Case 10: Large range intervals
        System.out.println("\n=== Test Case 10: Large Range ===");
        int[][] intervals10 = {{0, 10000}, {1, 2}, {3, 4}};
        printResult(solution.merge(intervals10)); // [[0,10000]]
        
        // Test Case 11: Reverse order input
        System.out.println("\n=== Test Case 11: Reverse Order ===");
        int[][] intervals11 = {{4, 5}, {2, 3}, {1, 1}};
        printResult(solution.merge(intervals11)); // [[1,1],[2,3],[4,5]]
        
        // Test Case 12: Validation test
        System.out.println("\n=== Test Case 12: Validation Test ===");
        validateAllApproaches(solution);
        
        // Test Case 13: Complex overlapping
        System.out.println("\n=== Test Case 13: Complex Overlapping ===");
        int[][] intervals13 = {{2, 3}, {4, 5}, {6, 7}, {8, 9}, {1, 10}};
        printResult(solution.merge(intervals13)); // [[1,10]]
        
        // Test Case 14: Stress test
        System.out.println("\n=== Test Case 14: Stress Test ===");
        stressTest(solution);
        
        // Test Case 15: Empty and edge cases
        System.out.println("\n=== Test Case 15: Edge Cases ===");
        printResult(solution.merge(new int[][]{})); // []
        printResult(solution.merge(new int[][]{{0, 0}})); // [[0,0]]
    }
    
    private static void printResult(int[][] result) {
        System.out.print("[");
        for (int i = 0; i < result.length; i++) {
            System.out.print("[" + result[i][0] + "," + result[i][1] + "]");
            if (i < result.length - 1) System.out.print(",");
        }
        System.out.println("]");
    }
    
    private static void compareApproaches(MergeIntervals solution, int[][] intervals) {
        int[][] result1 = solution.merge(intervals.clone());
        int[][] result2 = solution.mergeInPlace(intervals.clone());
        int[][] result3 = solution.mergeOptimized(intervals.clone());
        
        System.out.print("Standard: ");
        printResult(result1);
        System.out.print("In-place: ");
        printResult(result2);
        System.out.print("Optimized: ");
        printResult(result3);
        
        boolean consistent = Arrays.deepEquals(result1, result2) && Arrays.deepEquals(result2, result3);
        System.out.println("All consistent: " + consistent);
    }
    
    private static void performanceTest(MergeIntervals solution) {
        int[][] largeIntervals = new int[1000][2];
        Random rand = new Random(42);
        
        for (int i = 0; i < 1000; i++) {
            int start = rand.nextInt(1000);
            int end = start + rand.nextInt(100) + 1;
            largeIntervals[i] = new int[]{start, end};
        }
        
        long start = System.currentTimeMillis();
        int[][] result = solution.merge(largeIntervals);
        long end = System.currentTimeMillis();
        
        System.out.println("Performance test: " + result.length + 
                          " intervals merged in " + (end - start) + "ms");
    }
    
    private static void validateAllApproaches(MergeIntervals solution) {
        int[][] testIntervals = {{1, 3}, {2, 6}, {8, 10}, {9, 12}, {15, 18}};
        
        int[][] result1 = solution.merge(testIntervals.clone());
        int[][] result2 = solution.mergeInPlace(testIntervals.clone());
        int[][] result3 = solution.mergeOptimized(testIntervals.clone());
        
        boolean allConsistent = Arrays.deepEquals(result1, result2) && Arrays.deepEquals(result2, result3);
        System.out.println("Validation: " + result1.length + " intervals, All consistent: " + allConsistent);
    }
    
    private static void stressTest(MergeIntervals solution) {
        // Create highly overlapping intervals
        int[][] stressIntervals = new int[100][2];
        for (int i = 0; i < 100; i++) {
            stressIntervals[i] = new int[]{i / 10, i / 10 + 20};
        }
        
        int[][] result = solution.merge(stressIntervals);
        System.out.println("Stress test completed: " + result.length + " merged intervals");
    }
}