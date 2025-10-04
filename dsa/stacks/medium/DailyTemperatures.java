package stacks.medium;

import java.util.*;

/**
 * LeetCode 739: Daily Temperatures
 * https://leetcode.com/problems/daily-temperatures/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: High
 *
 * Description: Given a list of daily temperatures, return a list such that for each day, tells you how many days you would have to wait until a warmer temperature.
 *
 * Constraints:
 * - 1 <= temperatures.length <= 10^5
 * - 30 <= temperatures[i] <= 100
 * 
 * Follow-up Questions:
 * 1. Can you find the max/min wait?
 * 2. Can you optimize for large arrays?
 * 3. Can you handle decreasing temperatures?
 */
public class DailyTemperatures {

    // Approach 1: Monotonic stack
    public int[] dailyTemperatures(int[] temperatures) {
        int n = temperatures.length;
        int[] res = new int[n];
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < n; i++) {
            while (!stack.isEmpty() && temperatures[i] > temperatures[stack.peek()]) {
                int idx = stack.pop();
                res[idx] = i - idx;
            }
            stack.push(i);
        }
        return res;
    }

    // Follow-up 1: Max wait
    public int maxWait(int[] temperatures) {
        int[] waits = dailyTemperatures(temperatures);
        int max = 0;
        for (int w : waits) max = Math.max(max, w);
        return max;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        DailyTemperatures solution = new DailyTemperatures();

        // Test case 1: Basic case
        int[] temps1 = {73,74,75,71,69,72,76,73};
        System.out.println("Test 1 - temps: " + Arrays.toString(temps1) + " Expected: [1,1,4,2,1,1,0,0]");
        System.out.println("Result: " + Arrays.toString(solution.dailyTemperatures(temps1)));

        // Test case 2: Max wait
        System.out.println("\nTest 2 - Max wait:");
        System.out.println(solution.maxWait(temps1));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("All decreasing: " + Arrays.toString(solution.dailyTemperatures(new int[]{100,99,98,97})));
        System.out.println("Single temp: " + Arrays.toString(solution.dailyTemperatures(new int[]{70})));
    }
}
