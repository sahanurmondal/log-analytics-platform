package stacks.medium;

import java.util.*;

/**
 * LeetCode 636: Exclusive Time of Functions
 * https://leetcode.com/problems/exclusive-time-of-functions/
 * 
 * Companies: Google, Amazon, Facebook
 * Frequency: Medium
 *
 * Description: Given n functions and logs, return the exclusive time of each function.
 *
 * Constraints:
 * - 1 <= n <= 100
 * - 1 <= logs.length <= 500
 * - 0 <= function_id < n
 * - 0 <= timestamp <= 10^9
 * - No two start events will happen at the same timestamp
 * - No two end events will happen at the same timestamp
 * - Each function has an "end" log for each "start" log
 *
 * Follow-up:
 * - Can you handle recursive function calls?
 * - Can you extend to multi-threaded execution?
 */
public class ExclusiveTimeOfFunctions {

    // Approach 1: Stack simulation
    public int[] exclusiveTime(int n, List<String> logs) {
        int[] res = new int[n];
        Stack<Integer> stack = new Stack<>();
        int prevTime = 0;
        for (String log : logs) {
            String[] parts = log.split(":");
            int id = Integer.parseInt(parts[0]);
            String type = parts[1];
            int time = Integer.parseInt(parts[2]);
            if (type.equals("start")) {
                if (!stack.isEmpty()) res[stack.peek()] += time - prevTime;
                stack.push(id);
                prevTime = time;
            } else {
                res[stack.pop()] += time - prevTime + 1;
                prevTime = time + 1;
            }
        }
        return res;
    }

    // Comprehensive test cases
    public static void main(String[] args) {
        ExclusiveTimeOfFunctions solution = new ExclusiveTimeOfFunctions();

        // Test case 1: Basic case
        int n1 = 2;
        List<String> logs1 = Arrays.asList("0:start:0","1:start:2","1:end:5","0:end:6");
        System.out.println("Test 1 - logs: " + logs1 + " Expected: [3,4]");
        System.out.println("Result: " + Arrays.toString(solution.exclusiveTime(n1, logs1)));

        // Edge cases
        System.out.println("\nEdge cases:");
        System.out.println("Single function: " + Arrays.toString(solution.exclusiveTime(1, Arrays.asList("0:start:0","0:end:1"))));
    }
}
